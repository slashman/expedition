package net.slashie.expedition.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import net.slashie.serf.game.Equipment;
import net.slashie.serf.ui.Appearance;


public class Store implements Serializable, Cloneable{
	private Hashtable<String, Integer> prices = new Hashtable<String, Integer>();
	private List<Equipment> inventory = new ArrayList<Equipment>();
	private String text;
	private String ownerName;
	private Appearance ownerAppearance;
	
	public List<Equipment> getInventory() {
		return inventory;
	}
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getOwnerName() {
		return ownerName;
	}
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	public Appearance getOwnerAppearance() {
		return ownerAppearance;
	}
	public void setOwnerAppearance(Appearance ownerAppearance) {
		this.ownerAppearance = ownerAppearance;
	}
	
	public int getPriceFor(ExpeditionItem item){
		return prices.get(item.getFullID());
	}
	
	public void addItem(ExpeditionItem item, int quantity, Integer prize){
		Equipment existingEquipment = null;
		for (Equipment equipment: inventory){
			if (equipment.getItem().getFullID().equals(item.getFullID())){
				existingEquipment = equipment;
				break;
			}
		}
		if (existingEquipment == null){
			inventory.add(new Equipment(item, quantity)) ;
		} else {
			existingEquipment.increaseQuantity(quantity);
		}
		prices.put(item.getFullID(), prize);
	}
	
	@Override
	public Store clone() {
		try {
			Store store = (Store)super.clone();
			store.inventory = new ArrayList<Equipment>();
			for (Equipment eq: getInventory()){
				store.inventory.add(eq.clone());
			}
			store.prices = new Hashtable<String, Integer>();
			for (String key: prices.keySet()){
				store.prices.put(key, prices.get(key));
			}
			return store;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
}
