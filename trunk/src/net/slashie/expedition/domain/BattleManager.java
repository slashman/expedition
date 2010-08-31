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
		
		//Calculate how many of the expedition fighters will attack. 
		/*
		int attackProportion = Util.rand(80, 100);
		int defenseProportion = Util.rand(80, 100);
		
		//Calculate Damage
		int damageCaused = 0;
		for (Equipment unit_: attackingUnits){
			ExpeditionUnit unit = (ExpeditionUnit)unit_.getItem();
			damageCaused += (int)Math.round((double)unit.getAttack() * (double)unit_.getQuantity() * ((double)attackProportion/100.0d));
		}

		
		//Calculate mitigation
		int damageMitigated = 0;
		for (Equipment unit_: defendingUnits){
			ExpeditionUnit unit = (ExpeditionUnit)unit_.getItem();
			damageMitigated += (int)Math.round((double)unit.getDefense() * (double)unit_.getQuantity() * ((double)defenseProportion/100.0d));
		}
		
		//Calculate deaths 
		int outcome = damageCaused - damageMitigated;
		if (outcome < 0)
			outcome = 0;
		int deaths = outcome;
		if (deaths > 0)
			((FoodConsumer)actor).killUnits(deaths);
		else
			performer.getLevel().addMessage(" No one is killed.");
			*/
	}

	private static AssaultOutcome[] meleeAttack(List<Equipment> attackingUnits, UnitContainer attackingExpedition,
			List<Equipment> defendingUnits, UnitContainer defendingExpedition) {
		AssaultOutcome[] ret = new AssaultOutcome[]{ new AssaultOutcome(), new AssaultOutcome() };
		for (Equipment equipment: attackingUnits){
			for (int i = 0; i < equipment.getQuantity(); i++){
				Equipment randomTarget = pickRandomTargetFairly(defendingUnits);
				singleAttack(equipment, randomTarget, defendingExpedition, ret[0]);
				singleAttack(randomTarget, equipment, attackingExpedition, ret[1]);
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
				int damage = attackingUnit.getAttack().roll();
				damageUnits(1, defendingEquipment, defendingExpedition, damage, outcome);
			}
		}
	}

	private static void damageUnits(
			int quantity, 
			Equipment target,
			UnitContainer targetExpedition,
			int damage, AssaultOutcome outcome) {
		ExpeditionUnit targetUnit = (ExpeditionUnit) target.getItem(); 
		for (int i = 0; i < quantity; i++){
			int defense = targetUnit.getDefense().roll();
			int realDamage = damage - defense;
			if (realDamage <= 0){
				// Shrug off
			} else if (realDamage <= targetUnit.getResistance()) {
				// Wound
				if (targetUnit.isWounded()){
					// Kill unit :(
					targetExpedition.reduceUnits(targetUnit, 1);
					outcome.addDeath(targetUnit);
				} else {
					// Add a wounded unit
					targetExpedition.reduceUnits(targetUnit, 1);
					ExpeditionUnit woundedUnit = (ExpeditionUnit)targetUnit.clone();
					woundedUnit.setWounded(true);
					targetExpedition.addUnits(woundedUnit, 1);
					outcome.addWound(targetUnit);
				}
			} else {
				// Overkill  :(
				targetExpedition.reduceUnits(targetUnit, 1);
				outcome.addDeath(targetUnit);
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
