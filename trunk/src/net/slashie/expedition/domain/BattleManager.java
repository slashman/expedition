package net.slashie.expedition.domain;

import java.util.ArrayList;
import java.util.List;

import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.ui.ExpeditionUserInterface;
import net.slashie.serf.action.Actor;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.ui.UserInterface;
import net.slashie.util.Pair;
import net.slashie.utils.Util;

public class BattleManager {

	public static void battle(String battleName, Expedition attacker, Actor defender) {
		List<Equipment> attackingUnitsFullGroup = attacker.getGoods(GoodType.PEOPLE);
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
				defendingUnitsFullGroup = town.getGoods(GoodType.PEOPLE);
				town.resetTurnsBeforeNextExpedition();
				defender.getLevel().getDispatcher().removeActor(town);
				defender.getLevel().getDispatcher().addActor(town, true);
			}
		} else if (defender instanceof Expedition){
			Expedition npe = (Expedition)defender;
			defendingUnitsFullGroup = npe.getGoods(GoodType.PEOPLE);
		} else {
			//Invalid Defender
			return;
		}
		
		// Interrupt whatever action the defender was up to
		defender.setInterrupted();
		
		// Trim attacking and defending teams to 60, 20 ranged (if possible), 20 mounted (if possible) and the remaining.
		List<Equipment> attackingUnits = selectSquad(attackingUnitsFullGroup);
		List<Equipment> defendingUnits = selectSquad(defendingUnitsFullGroup);
		List<Equipment> originalAttackingUnits = cloneEquipmentList(attackingUnits);
		List<Equipment> originalDefendingUnits = cloneEquipmentList(defendingUnits);
		
		((ExpeditionUserInterface)UserInterface.getUI()).showBattleScene(battleName, attackingUnits, defendingUnits);

		// Ranged phase: Ranged units from both teams attack
		AssaultOutcome attackerRangedAttackOutcome =  rangedAttack(attackingUnits, defendingUnits, (UnitContainer)defender);
		AssaultOutcome defenderRangedAttackOutcome = rangedAttack(defendingUnits, attackingUnits, attacker);
		
		// Mounted phase: Mounted units from attacker attack (Defender will attack back)
		AssaultOutcome[] attackerMountedAttackOutcome = mountedAttack(attackingUnits, attacker, defendingUnits, (UnitContainer)defender);
		
		// Melee phase: Attacker charges (Defender will attack back)
		AssaultOutcome[] attackerMeleeAttackOutcome = meleeAttack(attackingUnits, attacker, defendingUnits, (UnitContainer)defender);
		
		// Score outcomes to see who won
		int attackerScore = 0;
		int defenderScore = 0;
		attackerScore += eval(attackerRangedAttackOutcome);
		defenderScore += eval(defenderRangedAttackOutcome);
		attackerScore += eval(attackerMountedAttackOutcome[0]);
		defenderScore += eval(attackerMountedAttackOutcome[0]);
		attackerScore += eval(attackerMeleeAttackOutcome[0]);
		defenderScore += eval(attackerMeleeAttackOutcome[0]);
		/*System.out.println("Attacker Score "+attackerScore);
		System.out.println("Defender Score "+defenderScore);*/
		if (attackerScore > defenderScore){
			if (defender instanceof NativeTown){
				((NativeTown)defender).increaseScaredLevel();
			} else if (defender instanceof NonPrincipalExpedition){
				
			}
		} else {
			if (defender instanceof NativeTown){
				((NativeTown)defender).reduceScaredLevel();
			} else if (defender instanceof NonPrincipalExpedition){
				
			}
		}
		((ExpeditionUserInterface)UserInterface.getUI()).showBattleResults(originalAttackingUnits, originalDefendingUnits, battleName, attackerRangedAttackOutcome, defenderRangedAttackOutcome, attackerMountedAttackOutcome, attackerMeleeAttackOutcome, attackerScore, defenderScore);
		
		
	}

	private static int eval(AssaultOutcome assaultOutcome) {
		int score = 0;
		for (Pair<ExpeditionUnit, Integer> wound: assaultOutcome.getWounds()){
			score += wound.getB();
		}
		for (Pair<ExpeditionUnit, Integer> death: assaultOutcome.getDeaths()){
			score += death.getB() * 3;
		}
		return score;
	}

	private static List<Equipment> cloneEquipmentList(
			List<Equipment> originalList) {
		List<Equipment> clonedList = new ArrayList<Equipment>();
		for (Equipment e: originalList){
			clonedList.add(new Equipment(e.getItem(), e.getQuantity()));
		}
		return clonedList;
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
					if (randomTarget == null){
						//Noone left to attack
						return ret;
					}
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
		
		// Ranged
		for (Equipment eq: fullGroup){
			if (remaining == 0)
				break;
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
		// Mounted
		for (Equipment eq: fullGroup){
			if (remaining == 0)
				break;
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
		// On foot 
		for (Equipment eq: fullGroup){
			if (remaining == 0)
				break;
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
		
		// Not wounded
		for (Equipment eq: fullGroup){
			if (remaining == 0)
				break;
			ExpeditionUnit unit = (ExpeditionUnit) eq.getItem();
			if (unit.isWounded())
				continue;
			if (usedUnitsFullIDs.contains(unit.getFullID()))
				continue;
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
		
		// Remaining
		for (Equipment eq: fullGroup){
			if (remaining == 0)
				break;
			ExpeditionUnit unit = (ExpeditionUnit) eq.getItem();
			if (usedUnitsFullIDs.contains(unit.getFullID()))
				continue;
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
		return squad;
	}
}
