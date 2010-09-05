package net.slashie.expedition.ui;

import net.slashie.expedition.domain.AssaultOutcome;
import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.ExpeditionItem;
import net.slashie.expedition.domain.ExpeditionUnit;
import net.slashie.expedition.domain.Good;
import net.slashie.expedition.domain.GoodsCache;
import net.slashie.expedition.domain.Store;
import net.slashie.expedition.domain.StoreItemInfo;
import net.slashie.serf.game.Equipment;

public class CommonUI {
	public static String getMenuCacheDescription(Equipment item, Expedition expedition, GoodsCache cache){
		String itemDescription = ((ExpeditionItem)item.getItem()).getFullDescription();
		int inventory = item.getQuantity();
		int stock = 0;
		if (expedition != null){
			if (item.getItem() instanceof Good){ 
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

	public static String getBattleResultsString(String battleName,
			AssaultOutcome attackerRangedAttackOutcome,
			AssaultOutcome defenderRangedAttackOutcome,
			AssaultOutcome[] mountedAttackOutcome,
			AssaultOutcome[] meleeAttackOutcome) {
		String message = battleName+" XXX ";
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
		return message;
	}
	
}
