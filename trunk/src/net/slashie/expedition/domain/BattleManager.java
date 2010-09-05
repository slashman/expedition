package net.slashie.expedition.domain;

import java.util.ArrayList;
import java.util.List;

import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.item.ItemFactory;
import net.slashie.expedition.ui.ExpeditionDisplay;
import net.slashie.expedition.ui.ExpeditionUserInterface;
import net.slashie.serf.action.Actor;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.ui.UserInterface;
import net.slashie.util.Pair;
import net.slashie.utils.Util;

public class BattleManager {

	public static void battle(String battleName, Expedition attacker, Actor defender) {
		List<Equipment> attackingUnitsFullGroup = attacker.getUnits();
		List<Equipment> defendingUnitsFullGroup = null;
		
		if (defender instanceof NativeTown){
			//Select a defending group from the town inhabitants
			NativeTown town = (NativeTown) defender;
			if (town.getTotalUnits() == 0){
				if (attacker == ExpeditionGame.getCurrentGame().getPlayer()){
					((ExpeditionUserInterface)UserInterface.getUI()).transferFromCache(town);
				}
				return;
			} else {
				defendingUnitsFullGroup = town.getUnits();
				town.resetTurnsBeforeNextExpedition();
				defender.getLevel().getDispatcher().removeActor(town);
				defender.getLevel().getDispatcher().addActor(town, true);
			}
		} else if (defender instanceof Expedition){
			Expedition npe = (Expedition)defender;
			defendingUnitsFullGroup = npe.getUnits();
		} else {
			//Invalid Defender
			return;
		}
		
		
		// Trim attacking and defending teams to 60, 20 ranged (if possible), 20 mounted (if possible) and the remaining.
		List<Equipment> attackingUnits = selectSquad(attackingUnitsFullGroup);
		List<Equipment> defendingUnits = selectSquad(defendingUnitsFullGroup);
		
		((ExpeditionUserInterface)UserInterface.getUI()).showBattleScene(battleName, attackingUnits, defendingUnits);

		// Ranged phase: Ranged units from both teams attack
		AssaultOutcome attackerRangedAttackOutcome =  rangedAttack(attackingUnits, defendingUnits, (UnitContainer)defender);
		AssaultOutcome defenderRangedAttackOutcome = rangedAttack(defendingUnits, attackingUnits, attacker);
		
		// Mounted phase: Mounted units from attacker attack (Defender will attack back)
		AssaultOutcome[] attackerMountedAttackOutcome = mountedAttack(attackingUnits, attacker, defendingUnits, (UnitContainer)defender);
		
		// Melee phase: Attacker charges (Defender will attack back)
		AssaultOutcome[] attackerMeleeAttackOutcome = meleeAttack(attackingUnits, attacker, defendingUnits, (UnitContainer)defender);

		((ExpeditionUserInterface)UserInterface.getUI()).showBattleResults(battleName, attackerRangedAttackOutcome, defenderRangedAttackOutcome, attackerMountedAttackOutcome, attackerMeleeAttackOutcome);
		
		
	}

	private static void removeDeadAndWounded(AssaultOutcome assaultOutcome, List<Equipment> unitGroup) {
		List<Pair<ExpeditionUnit, Integer>> deaths = assaultOutcome.getDeaths();
		for (Pair<ExpeditionUnit, Integer> death: deaths){
			for (Equipment eq: unitGroup){
				if (eq.getItem().getFullID().equals(death.getA().getFullID())){
					eq.reduceQuantity(death.getB());
					break;
				}
			}
		}
		
		List<Pair<ExpeditionUnit, Integer>> wounded = assaultOutcome.getDeaths();
		for (Pair<ExpeditionUnit, Integer> wound: wounded){
			for (Equipment eq: unitGroup){
				if (eq.getItem().getFullID().equals(wound.getA().getFullID())){
					eq.reduceQuantity(wound.getB());
					break;
				}
			}
		}
		
		for (int i = 0; i < unitGroup.size(); i++){
			if (unitGroup.get(i).getQuantity() == 0){
				unitGroup.remove(i);
				i--;
			}
		}
	}

	private static AssaultOutcome[] meleeAttack(List<Equipment> attackingUnits, 
			UnitContainer attackingExpedition,
			List<Equipment> defendingUnits, UnitContainer defendingExpedition) {
		AssaultOutcome[] ret = new AssaultOutcome[]{ new AssaultOutcome(), new AssaultOutcome() };
		for (Equipment equipment: attackingUnits){
			for (int i = 0; i < equipment.getQuantity(); i++){
				Equipment randomTarget = pickRandomTargetFairly(defendingUnits);
				if (randomTarget == null){
					//Noone left to attack
					return ret;
				}
				singleAttack(equipment, randomTarget, defendingExpedition, ret[0]);
				singleAttack(randomTarget, equipment, attackingExpedition, ret[1]);
				if (randomTarget.getQuantity() == 0){
					defendingUnits.remove(randomTarget);
				}
			}
		}
		return ret;
	}
	
	private static AssaultOutcome[] mountedAttack(List<Equipment> attackingUnits, UnitContainer attackingExpedition,
			List<Equipment> defendingUnits, UnitContainer defendingExpedition) {
		AssaultOutcome[] ret = new AssaultOutcome[]{ new AssaultOutcome(), new AssaultOutcome() };
		for (Equipment equipment: attackingUnits){
			ExpeditionUnit unit = (ExpeditionUnit)equipment.getItem(); 
			if ( !unit.isRangedAttack() && unit.isMounted()){
				for (int i = 0; i < equipment.getQuantity(); i++){
					Equipment randomTarget = pickRandomTargetFairly(defendingUnits);
					singleAttack(equipment, randomTarget, defendingExpedition, ret[0]);
					singleAttack(randomTarget, equipment, attackingExpedition, ret[1]);
					if (randomTarget.getQuantity() == 0){
						defendingUnits.remove(randomTarget);
					}
				}
			}
		}
		return ret;
	}

	private static AssaultOutcome rangedAttack(List<Equipment> attackingUnits, List<Equipment> defendingUnits, UnitContainer defendingExpedition) {
		AssaultOutcome ret = new AssaultOutcome();
		for (Equipment equipment: attackingUnits){
			ExpeditionUnit unit = (ExpeditionUnit)equipment.getItem(); 
			if ( unit.isRangedAttack()){
				for (int i = 0; i < equipment.getQuantity(); i++){
					Equipment randomTarget = pickRandomTargetFairly(defendingUnits);
					singleAttack(equipment, randomTarget, defendingExpedition, ret);
					if (randomTarget.getQuantity() == 0){
						defendingUnits.remove(randomTarget);
					}
				}
			}
		}
		return ret;
	}
	
	private static void singleAttack(Equipment attackerEquipment, Equipment defendingEquipment, UnitContainer defendingExpedition, AssaultOutcome outcome) {
		ExpeditionUnit attackingUnit = (ExpeditionUnit)attackerEquipment.getItem();
		ExpeditionUnit defendingUnit = (ExpeditionUnit)defendingEquipment.getItem();
		if (Util.chance(attackingUnit.getHitChance())){
			//Pick a random target from the enemies
			if (!Util.chance(defendingUnit.getEvadeChance())){
				
				ExpeditionUnit targetUnit = (ExpeditionUnit) defendingEquipment.getItem(); 
				int damage = attackingUnit.getAttack().roll();
				int defense = targetUnit.getDefense().roll();
				int realDamage = damage - defense;
				if (realDamage <= 0){
					// Shrug off
				} else if (realDamage <= targetUnit.getResistance()) {
					// Wound
					if (targetUnit.isWounded()){
						// Kill unit :(
						defendingExpedition.reduceUnits(targetUnit, 1);
						defendingEquipment.reduceQuantity(1);
						outcome.addDeath(targetUnit);
					} else {
						// Add a wounded unit
						defendingExpedition.reduceUnits(targetUnit, 1);
						ExpeditionUnit woundedUnit = (ExpeditionUnit)targetUnit.clone();
						woundedUnit.setWounded(true);
						defendingExpedition.addUnits(woundedUnit, 1);
						defendingEquipment.reduceQuantity(1);
						outcome.addWound(targetUnit);
					}
				} else {
					// Overkill  :(
					defendingExpedition.reduceUnits(targetUnit, 1);
					defendingEquipment.reduceQuantity(1);
					outcome.addDeath(targetUnit);
				}
			}
		}
	}

	private static Equipment pickRandomTargetFairly
		(List<Equipment> targetUnits) {
		int count = 0;
		for (Equipment eq: targetUnits){
			count += eq.getQuantity();
		}
		int rand = Util.rand(0, count-1);
		count = 0;
		for (Equipment eq: targetUnits){
			count += eq.getQuantity();
			if (rand < count)
				return eq;
		}
		return null;
	}
	
	// Trim attacking and defending teams to 60, 20 ranged (if possible), 20 mounted (if possible) and the remaining.

	private static List<Equipment> selectSquad(
			List<Equipment> fullGroup) {
		int remaining = 60;
		int remainingRanged = 20;
		int remainingMounted = 20;
		List<String> usedUnitsFullIDs = new ArrayList<String>();
		List<Equipment> squad = new ArrayList<Equipment>();
		for (Equipment eq: fullGroup){
			ExpeditionUnit unit = (ExpeditionUnit) eq.getItem();
			if (unit.isWounded())
				continue;
			if (usedUnitsFullIDs.contains(unit.getFullID()))
				continue;
			if (unit.isRangedAttack()){
				int quantity = eq.getQuantity();
				if (quantity > remainingRanged){
					quantity = remainingRanged;
				}
				Equipment clone = eq.clone();
				clone.setQuantity(quantity);
				squad.add(clone);
				remainingRanged -= quantity;
				remaining -= quantity;
				usedUnitsFullIDs.add(eq.getItem().getFullID());
			}
		}
		for (Equipment eq: fullGroup){
			ExpeditionUnit unit = (ExpeditionUnit) eq.getItem();
			if (unit.isWounded())
				continue;
			if (usedUnitsFullIDs.contains(unit.getFullID()))
				continue;
			if (unit.isMounted()){
				int quantity = eq.getQuantity();
				if (quantity > remainingMounted){
					quantity = remainingMounted;
				}
				Equipment clone = eq.clone();
				clone.setQuantity(quantity);
				squad.add(clone);
				remainingMounted -= quantity;
				remaining -= quantity;
				usedUnitsFullIDs.add(eq.getItem().getFullID());

			}
		}
		for (Equipment eq: fullGroup){
			ExpeditionUnit unit = (ExpeditionUnit) eq.getItem();
			if (unit.isWounded())
				continue;
			if (usedUnitsFullIDs.contains(unit.getFullID()))
				continue;
			if (!unit.isMounted() && !unit.isRangedAttack()){
				int quantity = eq.getQuantity();
				if (quantity > remaining){
					quantity = remaining;
				}
				Equipment clone = eq.clone();
				clone.setQuantity(quantity);
				squad.add(clone);
				remaining -= quantity;
				usedUnitsFullIDs.add(eq.getItem().getFullID());

			}
		}
		for (Equipment eq: fullGroup){
			ExpeditionUnit unit = (ExpeditionUnit) eq.getItem();
			if (usedUnitsFullIDs.contains(unit.getFullID()))
				continue;
			if (!unit.isMounted() && !unit.isRangedAttack()){
				int quantity = eq.getQuantity();
				if (quantity > remaining){
					quantity = remaining;
				}
				Equipment clone = eq.clone();
				clone.setQuantity(quantity);
				squad.add(clone);
				remaining -= quantity;
				usedUnitsFullIDs.add(eq.getItem().getFullID());
			}
		}
		return squad;
	}
}
