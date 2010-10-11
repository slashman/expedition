package net.slashie.expedition.ui;

import java.text.DateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.slashie.expedition.domain.AssaultOutcome;
import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.ExpeditionItem;
import net.slashie.expedition.domain.ExpeditionUnit;
import net.slashie.expedition.domain.GoodType;
import net.slashie.expedition.domain.GoodsCache;
import net.slashie.expedition.domain.Store;
import net.slashie.expedition.domain.StoreItemInfo;
import net.slashie.expedition.domain.Town;
import net.slashie.expedition.town.Building;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.text.EnglishGrammar;
import net.slashie.util.Pair;

public class CommonUI {
	public static String getMenuCacheDescription(Equipment item, Expedition expedition, GoodsCache cache){
		String itemDescription = ((ExpeditionItem)item.getItem()).getFullDescription();
		int inventory = item.getQuantity();
		int stock = 0;
		if (expedition != null){
			if (((ExpeditionItem)item.getItem()).getGoodType() != GoodType.PEOPLE){ 
				stock = expedition.getCarryable((ExpeditionItem)item.getItem());
				if (stock < inventory)
					return itemDescription + " (Max "+stock+") {Available "+inventory+"}";
				else
					return itemDescription + " {Available "+inventory+"}";
			} else {
				return itemDescription + " {Available "+inventory+"}";
			}
		} else {
			stock = cache.getCarryable((ExpeditionItem)item.getItem());
			if (stock == -1)
				return itemDescription + " {On Expedition "+inventory+"}";
			else {
				if (stock < inventory)
					return itemDescription + " (Max "+stock+") {On Expedition "+inventory+"}";
				else
					return itemDescription + " {On Expedition "+inventory+"}";
			}
		}
	}
	
	public static String getMenuStoreDescription(Equipment item, Expedition offShore, Store store){
		String itemDescription = item.getItem().getDescription();
		int inventory = item.getQuantity();
		int stock = offShore.getOffshoreCarryable((ExpeditionItem)item.getItem());
		StoreItemInfo itemInfo = store.getPriceFor((ExpeditionItem)item.getItem());
		if (item.getItem() instanceof ExpeditionUnit){
			if (stock < 0)
				stock = 0;
			if (itemInfo.getPack() > 1){
				if (stock < inventory)
					return itemInfo.getPack()+" "+ itemDescription + ", "+itemInfo.getPrice()+"$ (max "+stock+") {"+inventory+" Available}";
				else
					return itemInfo.getPack()+" "+ itemDescription + ", "+itemInfo.getPrice()+"$ {"+inventory+" Available}";
			} else {
				if (stock < inventory)
					return itemDescription + ", "+itemInfo.getPrice()+"$ (max "+stock+") {"+inventory+" Available}";
				else
					return itemDescription + ", "+itemInfo.getPrice()+"$ {"+inventory+" Available}";
			}
		} else {
			if (stock < 0)
				stock = 0;
			if (itemInfo.getPack() > 1){
				if (stock < inventory)
					return itemInfo.getPack()+" "+ itemDescription + " for "+itemInfo.getPrice()+"$ (max "+stock+") {Stock:"+inventory+"}";
				else
					return itemInfo.getPack()+" "+ itemDescription + " for "+itemInfo.getPrice()+"$ {Stock:"+inventory+"}";
			} else {
				if (stock < inventory)
					return itemDescription + " for "+itemInfo.getPrice()+"$ (max "+stock+") {Stock:"+inventory+"}";
				else
					return itemDescription + " for "+itemInfo.getPrice()+"$ {Stock:"+inventory+"}";
			}
		}
	}

	public static String getBattleResultsString(
			List<Equipment> originalAttackingUnits, List<Equipment> originalDefendingUnits, String battleName,
			AssaultOutcome attackerRangedAttackOutcome,
			AssaultOutcome defenderRangedAttackOutcome,
			AssaultOutcome[] mountedAttackOutcome,
			AssaultOutcome[] meleeAttackOutcome, int attackerScore, int defenderScore) {
		String message = battleName+" XXX ";
		
		message += ExpeditionUnit.getUnitsStringFromEquipment(originalAttackingUnits).getA()+" XXX ";
		message += "    ... engage with ... XXX ";
		message += ExpeditionUnit.getUnitsStringFromEquipment(originalDefendingUnits).getA()+" XXX ";
		message += "XXX ";
		boolean nothingHappened = true;
		// Ranged Phase
		if (attackerRangedAttackOutcome.hasEvents()){
			message += "    >> Ranged Attack << XXX ";
			nothingHappened = false;
		}
		
		if (attackerRangedAttackOutcome.hasDeaths()){
			message += attackerRangedAttackOutcome.getDeathsString()+" XXX ";
		}
		if (attackerRangedAttackOutcome.hasWounds()){
			message += attackerRangedAttackOutcome.getWoundsString()+" XXX ";
		}

		if (defenderRangedAttackOutcome.hasEvents()){
			message += "    >> Ranged Retaliation << XXX ";
			nothingHappened = false;
		}
		
		if (defenderRangedAttackOutcome.hasDeaths()){
			message += defenderRangedAttackOutcome.getDeathsString()+" XXX ";
		}
		if (defenderRangedAttackOutcome.hasWounds()){
			message += defenderRangedAttackOutcome.getWoundsString()+" XXX ";
		}
		
		
		// Charge Phase
		if (mountedAttackOutcome[0].hasEvents()){
			message += "    >> Mounted charge outcome << XXX ";
			nothingHappened = false;
		}
		
		if (mountedAttackOutcome[0].hasDeaths()){
			message += mountedAttackOutcome[0].getDeathsString()+" XXX ";
		}
		if (mountedAttackOutcome[0].hasWounds()){
			message += mountedAttackOutcome[0].getWoundsString()+" XXX ";
		}
		
		if (mountedAttackOutcome[1].hasEvents()){
			message += "    >> Mounted charge losses << XXX " ;
			nothingHappened = false;
		}
		
		if (mountedAttackOutcome[1].hasDeaths()){
			message += mountedAttackOutcome[1].getDeathsString()+" XXX ";
		}
		if (mountedAttackOutcome[1].hasWounds()){
			message += mountedAttackOutcome[1].getWoundsString()+" XXX ";
		}
		
		// Melee Phase
		if (meleeAttackOutcome[0].hasEvents()){
			message += "    >> Melee outcome << XXX ";
			nothingHappened = false;
		}
		
		if (meleeAttackOutcome[0].hasDeaths()){
			message += meleeAttackOutcome[0].getDeathsString()+" XXX ";
		}
		
		if (meleeAttackOutcome[0].hasWounds()){
			message += meleeAttackOutcome[0].getWoundsString()+" XXX ";
		}
		
		if (meleeAttackOutcome[1].hasEvents()){
			message += "    >> Melee losses << XXX ";
			nothingHappened = false;
		}
		
		if (meleeAttackOutcome[1].hasDeaths()){
			message += meleeAttackOutcome[1].getDeathsString()+" XXX ";
		}
		if (meleeAttackOutcome[1].hasWounds()){
			message += meleeAttackOutcome[1].getWoundsString()+" XXX ";
		}
		
		if (nothingHappened) {
			message += "No losses for both sides XXX ";
		}
		if (attackerScore > defenderScore){
			message += "The attacking party wins the battle XXX ";
		} else {
			message += "The defending party wins the battle XXX ";
		}
		return message;
	}

	public static String getTownDescription(Town town) {
		String townInfo = town.getName()+" XXX ";
		if (town.getFoundedIn() != null){
			townInfo += "Founded on "+ DateFormat.getDateInstance(DateFormat.MEDIUM).format(town.getFoundedIn())+" by "+town.getFounderExpedition().getExpeditionaryTitle()+" XXX ";
		}
		if (town.getPopulationCapacity() > 0){
			townInfo += "Population: "+town.getPopulation()+"/"+town.getPopulationCapacity()+" XXX ";
		} else {
			townInfo += "Population: "+town.getPopulation()+" XXX ";
		}
		
		Map<String, Pair<Building, Integer>> buildingsMap = new HashMap<String, Pair<Building,Integer>>();
		for (Building building: town.getBuildings()){
			Pair<Building, Integer> pair = buildingsMap.get(building.getId());
			if (pair == null){
				pair = new Pair<Building, Integer>(building, 1);
				buildingsMap.put(building.getId(), pair);
			} else {
				pair.setB(pair.getB()+1);
			}
		}
		int i = 1;
		for (Pair<Building, Integer> pair: buildingsMap.values()){
			townInfo += pair.getB()+" "+EnglishGrammar.plural(pair.getA().getDescription(),pair.getB());
			if (i < buildingsMap.values().size())
				townInfo += ", ";
			i++;
		}
		return townInfo;
	}
	
}
