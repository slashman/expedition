package net.slashie.expedition.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import net.slashie.expedition.item.ItemFactory;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.ui.Appearance;

public class Store implements Serializable, Cloneable{
	private static final long serialVersionUID = 1L;
	
	public final static int FOOD_PACK = 200;
	public final static int LIQUID_PACK = 500;
	public final static int WOOD_PACK = 50;
	
	private Hashtable<String, StoreItemInfo> prices = new Hashtable<String, StoreItemInfo>();
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

	public StoreItemInfo getBasicInfo(ExpeditionItem item, Expedition expedition){
		return prices.get(item.getFullID());
	}
		
	public StoreItemInfo getBuyInfo(ExpeditionItem item, Expedition expedition){
		if (item instanceof Food){
			StoreItemInfo ret = prices.get(item.getFullID()).clone();
			double unitPrice = (double)ret.getPrice() / (double)ret.getPack();
			ret.setPack(expedition.getDailyFoodConsumption());
			ret.setPackDescription("days");
			ret.setPrice((int)Math.round(unitPrice*ret.getPack()));
			return ret;
		} else {
			return prices.get(item.getFullID());
		}
	}
	
	public void addItem(int quantity, StoreItemInfo info){
		ExpeditionItem item = ItemFactory.createItem(info.getFullId());
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
		prices.put(item.getFullID(), info);
	}
	
	
	
	@Override
	public Store clone() {
		try {
			Store store = (Store)super.clone();
			store.inventory = new ArrayList<Equipment>();
			for (Equipment eq: getInventory()){
				store.inventory.add(eq.clone());
			}
			store.prices = new Hashtable<String, StoreItemInfo>();
			for (String key: prices.keySet()){
				store.prices.put(key, prices.get(key).clone());
			}
			return store;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
}
