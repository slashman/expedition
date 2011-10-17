package net.slashie.expedition.domain;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import net.slashie.expedition.item.ItemFactory;
import net.slashie.serf.baseDomain.AbstractItem;
import net.slashie.serf.game.Equipment;

public class Inventory {
	private List<Equipment> items = new ArrayList<Equipment>();
	private Map<String, Equipment> itemsHash = new Hashtable<String, Equipment>();
	
	
	public int getItemCountBasic(String string) {
		int goodCount = 0;
		List<Equipment> inventory = getItems();
		for (Equipment equipment: inventory){
			if (((ExpeditionItem)equipment.getItem()).getBaseID().equals(string)){
				goodCount += equipment.getQuantity();
			}
		}
		return goodCount;
	}
	
	public List<Equipment> getItemsWithBaseID(String baseId) {
		List<Equipment> ret = new ArrayList<Equipment>();
		List<Equipment> inventory = getItems();
		for (Equipment equipment: inventory){
			if (((ExpeditionItem)equipment.getItem()).getBaseID().equals(baseId)){
				ret.add(equipment);
			}
		}
		return ret;
	}
	
	public List<Equipment> getItems() {
		return items;
	}
	
	public void addItem(ExpeditionItem item, int quantity) {
		String toAddID = item.getFullID();
		Equipment equipmentx = itemsHash.get(toAddID);
		if (equipmentx == null){
			equipmentx = new Equipment(item, quantity);
			itemsHash.put(toAddID, equipmentx);
			items.add(equipmentx);
		} else {
			equipmentx.increaseQuantity(quantity);
		}
	}
	
	public void addItem(String toAddID, int quantity) {
		Equipment equipmentx = itemsHash.get(toAddID);
		if (equipmentx == null){
			equipmentx = new Equipment(ItemFactory.createItem(toAddID), quantity);
			itemsHash.put(toAddID, equipmentx);
			items.add(equipmentx);
		} else {
			equipmentx.increaseQuantity(quantity);
		}
	}
	
	public void reduceQuantityOf(AbstractItem item, int quantity) {
		for (int i = 0; i < items.size(); i++){
			Equipment equipment = (Equipment) items.get(i);
			if (equipment.getItem().equals(item)){
				equipment.reduceQuantity(quantity);
				if (equipment.getQuantity() < 0){
					// This should never happen
					System.out.println("WARNING: Invalid scenario has just happened. Please contact us with this information:");
					equipment.setQuantity(0);
					try {
						throw new RuntimeException();
					} catch (RuntimeException e){
						e.printStackTrace();
					}
				}
				if (equipment.isEmpty()){
					items.remove(equipment);
					itemsHash.remove(equipment.getItem().getFullID());
				}
				return;
			}
		}
	}
	
	public void reduceQuantityOf(String itemId, int quantity) {
		for (int i = 0; i < items.size(); i++){
			Equipment equipment = (Equipment) items.get(i);
			if (equipment.getItem().getFullID().equals(itemId)){
				equipment.reduceQuantity(quantity);
				if (equipment.getQuantity() < 0){
					// This should never happen
					System.out.println("WARNING: Invalid scenario has just happened. Please contact us with this information:");
					equipment.setQuantity(0);
					try {
						throw new RuntimeException();
					} catch (RuntimeException e){
						e.printStackTrace();
					}
				}
				if (equipment.isEmpty()){
					items.remove(equipment);
					itemsHash.remove(equipment.getItem().getFullID());
				}
				return;
			}
		}
	}
	
	public void addAllGoods(Expedition expedition){
		for (Equipment equipment: expedition.getInventory()){
			if (equipment.getQuantity() > 0) {
				Equipment current = itemsHash.get(equipment.getItem().getFullID());
				if (current == null){
					Equipment new_ = equipment.clone();
					itemsHash.put(equipment.getItem().getFullID(), new_);
					items.add(new_);
				} else {
					current.increaseQuantity(equipment.getQuantity());
				}
			}
		}
	}

	public void removeAllItems() {
		items.clear();
		itemsHash.clear();
	}
}
