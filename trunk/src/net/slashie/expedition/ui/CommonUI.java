package net.slashie.expedition.ui;

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
		String itemDescription = item.getItem().getDescription();
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
	
}
