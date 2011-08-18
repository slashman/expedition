package net.slashie.expedition.ui;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.slashie.expedition.domain.AssaultOutcome;
import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.ExpeditionItem;
import net.slashie.expedition.domain.ExpeditionUnit;
import net.slashie.expedition.domain.GoodType;
import net.slashie.expedition.domain.GoodsCache;
import net.slashie.expedition.domain.LandingParty;
import net.slashie.expedition.domain.NativeTown;
import net.slashie.expedition.domain.Store;
import net.slashie.expedition.domain.StoreItemInfo;
import net.slashie.expedition.domain.Town;
import net.slashie.expedition.domain.LandingParty.LandingSpec;
import net.slashie.expedition.town.Building;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.text.EnglishGrammar;
import net.slashie.util.Pair;

public class CommonUI {
	public static String getMenuCacheDescription(Equipment item, Expedition expedition, GoodsCache cache){
		String itemDescription = ((ExpeditionItem)item.getItem()).getFullDescription();
		itemDescription += " <";
		int inventory = item.getQuantity();
		int stock = 0;
		if (expedition != null){
			int current = expedition.getItemCountBasic(item.getItem().getFullID());
			if (current > 0){
				itemDescription += " On Expedition: "+current;
			}
			if (((ExpeditionItem)item.getItem()).getGoodType() != GoodType.PEOPLE){ 
				stock = expedition.getCarryable((ExpeditionItem)item.getItem());
				if (stock < inventory)
					itemDescription += " Max: "+stock;
			}
			itemDescription += " Available: "+inventory+" >";
			return itemDescription;
		} else {
			int current = cache.getItemCountBasic(item.getItem().getFullID());
			stock = cache.getCarryable((ExpeditionItem)item.getItem());
			itemDescription += " On Expedition: "+inventory;
			if (current > 0){
				itemDescription += " On "+cache.getDescription()+": "+current;
			}
			if (stock != -1){
				if (stock < inventory)
					itemDescription += " Max: "+stock;
			}
			itemDescription += " >";
			return itemDescription;
		}
	}
	
	public static String getMenuStoreDescription(Equipment item, Expedition offShore, Store store){
		String itemDescription = item.getItem().getDescription();
		int inventory = item.getQuantity();
		int stock = offShore.getOffshoreCarryable((ExpeditionItem)item.getItem());
		int current = offShore.getItemCountBasic(item.getItem().getFullID());
		StoreItemInfo itemInfo = store.getBasicInfo((ExpeditionItem)item.getItem(), offShore);
		if (item.getItem() instanceof ExpeditionUnit){
			if (stock < 0)
				stock = 0;
			if (itemInfo.getPack() > 1){
				if (stock < inventory)
					return itemInfo.getPack()+" "+ itemDescription + ", "+itemInfo.getPrice()+"$ [Current "+current+", Max "+stock+", Available "+inventory+"]";
				else
					return itemInfo.getPack()+" "+ itemDescription + ", "+itemInfo.getPrice()+"$ [Current "+current+", Available "+inventory+"]";
			} else {
				if (stock < inventory)
					return itemDescription + ", "+itemInfo.getPrice()+"$ [Current "+current+", Max "+stock+", Available "+inventory+"]";
				else
					return itemDescription + ", "+itemInfo.getPrice()+"$ [Current "+current+", Available "+inventory+"]";
			}
		} else {
			if (stock < 0)
				stock = 0;
			if (itemInfo.getPack() > 1){
				if (stock < inventory)
					return itemInfo.getPack()+" "+ itemDescription + " for "+itemInfo.getPrice()+"$ [Current "+current+", Max "+stock+", Available "+inventory+"]";
				else
					return itemInfo.getPack()+" "+ itemDescription + " for "+itemInfo.getPrice()+"$ [Current "+current+", Available "+inventory+"]";
			} else {
				if (stock < inventory)
					return itemDescription + " for "+itemInfo.getPrice()+"$ [Current "+current+", Max "+stock+", Available "+inventory+"]";
				else
					return itemDescription + " for "+itemInfo.getPrice()+"$ [Current "+current+", Available "+inventory+"]";
			}
		}
	}

	public static List<String> getBattleResultsString(
			List<Equipment> originalAttackingUnits, List<Equipment> originalDefendingUnits, String battleName,
			AssaultOutcome attackerRangedAttackOutcome,
			AssaultOutcome defenderRangedAttackOutcome,
			AssaultOutcome[] mountedAttackOutcome,
			AssaultOutcome[] meleeAttackOutcome, int attackerScore, int defenderScore) {
		List<String> ret = new ArrayList<String>();
		
		String message = battleName+" XXX ";
		message += ExpeditionUnit.getUnitsStringFromEquipment(originalAttackingUnits).getA()+" XXX ";
		message += "    ... engage with ... XXX ";
		message += ExpeditionUnit.getUnitsStringFromEquipment(originalDefendingUnits).getA()+" XXX ";
		ret.add(message);
		message = "";
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
		if (!message.equals(""))
			ret.add(message);
		message = "";

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
		if (!message.equals(""))
			ret.add(message);
		message = "";
		
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
		if (!message.equals(""))
			ret.add(message);
		message = "";
		
		if (mountedAttackOutcome[1].hasDeaths()){
			message += mountedAttackOutcome[1].getDeathsString()+" XXX ";
		}
		if (mountedAttackOutcome[1].hasWounds()){
			message += mountedAttackOutcome[1].getWoundsString()+" XXX ";
		}
		if (!message.equals(""))
			ret.add(message);
		message = "";
		
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
		if (!message.equals(""))
			ret.add(message);
		message = "";
		
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
		if (!message.equals(""))
			ret.add(message);
		message = "";
		
		if (nothingHappened) {
			message += "No losses for both sides XXX ";
		}
		if (attackerScore > defenderScore){
			message += "The attacking party wins the battle XXX ";
		} else {
			message += "The defending party wins the battle XXX ";
		}
		if (!message.equals(""))
			ret.add(message);
		message = "";
		return ret;
	}

	public static String getTownDescription(Town town) {
		String townInfo = town.getName()+" XXX ";
		if (town instanceof NativeTown){
			NativeTown nativeTown = (NativeTown) town;
			townInfo += "Disposition: ";
			if (nativeTown.isUnfriendly()){
				townInfo += "Hostile ";
			} else {
				townInfo += "Friendly ";
			}
			if (nativeTown.getScaredLevel()>0){
				townInfo += "Afraid ";
			}
			townInfo += " XXX ";
		}
		
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
			if (pair.getA().isPluralizableDescription())
				townInfo += pair.getB()+" "+EnglishGrammar.plural(pair.getA().getDescription(),pair.getB());
			else
				townInfo += pair.getB()+" "+pair.getB()+" "+pair.getA().getDescription();
			if (i < buildingsMap.values().size())
				townInfo += ", ";
			i++;
		}
		return townInfo;
	}

	public static String getIntroText() {
		return "The Portuguese get closer every year to finding an African route to the Indies. " +
		"They have mapped half the coast of that unknown land of jungle and desert, " +
		"and they have established outposts to strengthen their presence. They have perfected " +
		"their ships and nautical techniques. It is now only a matter of time before they reach their goal. \n\n" +
		"My son, I have seen you sitting at the docks of the port of Palos, looking west into the vastness of " +
		"the Ocean. When you were a child, you once asked me what was beyond the horizon, where the ships " +
		"can no longer be seen. Now history has given you a chance to find out the answer for yourself.";
	}

	
	private static List<LandingParty> STANDARD_LANDING_PARTIES;
	static
	{
		STANDARD_LANDING_PARTIES = new ArrayList<LandingParty>();
		
		LandingParty singleExplorerParty = new LandingParty();
		singleExplorerParty.setExplorers(LandingSpec.ONE);
		singleExplorerParty.setHorses(LandingSpec.ONE);
		singleExplorerParty.setName("Single Explorer");
		singleExplorerParty.setMounted(true);
		STANDARD_LANDING_PARTIES.add(singleExplorerParty);
		
		LandingParty explorationParty = new LandingParty();
		explorationParty.setExplorers(LandingSpec.HALF);
		explorationParty.setHorses(LandingSpec.ALL);
		explorationParty.setDoctors(LandingSpec.ALL);
		explorationParty.setName("Exploration Party");
		explorationParty.setMounted(true);
		STANDARD_LANDING_PARTIES.add(explorationParty);

		LandingParty lumberParty = new LandingParty();
		lumberParty.setExplorers(LandingSpec.ONE);
		lumberParty.setCarpenters(LandingSpec.ALL);
		lumberParty.setHorses(LandingSpec.ALL);
		lumberParty.setName("Lumberjacking Group");
		lumberParty.setMounted(false);
		STANDARD_LANDING_PARTIES.add(lumberParty);
		
		LandingParty battleParty = new LandingParty();
		battleParty.setExplorers(LandingSpec.ONE);
		battleParty.setHorses(LandingSpec.ALL);
		battleParty.setSoldiers(LandingSpec.HALF);
		battleParty.setDoctors(LandingSpec.ALL);
		battleParty.setName("Assault Expedition");
		battleParty.setMounted(true);
		STANDARD_LANDING_PARTIES.add(battleParty);
		
		LandingParty allButCrewParty = new LandingParty();
		allButCrewParty.setExplorers(LandingSpec.ALL);
		allButCrewParty.setHorses(LandingSpec.ALL);
		allButCrewParty.setSoldiers(LandingSpec.ALL);
		allButCrewParty.setDoctors(LandingSpec.ALL);
		allButCrewParty.setCarpenters(LandingSpec.ALL);
		allButCrewParty.setName("All but ships crew");
		allButCrewParty.setMounted(false);
		STANDARD_LANDING_PARTIES.add(allButCrewParty);
		
		LandingParty allParty = new LandingParty();
		allParty.setExplorers(LandingSpec.ALL);
		allParty.setHorses(LandingSpec.ALL);
		allParty.setSoldiers(LandingSpec.ALL);
		allParty.setDoctors(LandingSpec.ALL);
		allParty.setCarpenters(LandingSpec.ALL);
		allParty.setCrew(LandingSpec.ALL);
		allParty.setName("Everybody");
		allParty.setMounted(false);
		STANDARD_LANDING_PARTIES.add(allParty);
	}
	public static List<LandingParty> getLandingParties() {
		return STANDARD_LANDING_PARTIES;
	}
	
}
