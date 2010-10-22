package net.slashie.expedition.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.ui.ExpeditionUserInterface;
import net.slashie.expedition.world.FoodConsumer;
import net.slashie.expedition.world.FoodConsumerDelegate;
import net.slashie.serf.action.Actor;
import net.slashie.serf.baseDomain.AbstractItem;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.level.AbstractFeature;
import net.slashie.serf.ui.AppearanceFactory;
import net.slashie.serf.ui.UserInterface;
import net.slashie.util.Pair;
import net.slashie.utils.Util;

public class GoodsCache extends AbstractFeature implements FoodConsumer, UnitContainer{
	private FoodConsumerDelegate foodConsumerDelegate; 
	
	public GoodsCache(ExpeditionGame game) {
		setAppearanceId("GOODS_CACHE");
		foodConsumerDelegate = new FoodConsumerDelegate(this);
		game.addFoodConsumer(this);
	}
	
	public GoodsCache() {
	}

	public int getItemCount(String string) {
		int goodCount = 0;
		List<Equipment> inventory = getInventory();
		for (Equipment equipment: inventory){
			if (equipment.getItem().getFullID().equals(string)){
				goodCount += equipment.getQuantity();
			}
		}
		return goodCount;
	}
	
	public int getItemCountBasic(String string) {
		int goodCount = 0;
		List<Equipment> inventory = getInventory();
		for (Equipment equipment: inventory){
			if (((ExpeditionItem)equipment.getItem()).getBaseID().equals(string)){
				goodCount += equipment.getQuantity();
			}
		}
		return goodCount;
	}
	
	public List<Equipment> getItems() {
		return items;
	}

	private List<Equipment> items = new ArrayList<Equipment>();
	private Map<String, Equipment> itemsHash = new Hashtable<String, Equipment>(); 
	
	public void addAllGoods(Expedition expedition){
		for (Equipment equipment: expedition.getInventory()){
			if (equipment.getQuantity() > 0) {
				Equipment new_ = equipment.clone();
				itemsHash.put(equipment.getItem().getFullID(), new_);
				items.add(new_);
			}
		}
	}
	
	public void addAllGoods(GoodsCache cache){
		for (Equipment equipment: cache.getInventory()){
			addItem(equipment.getItem(), equipment.getQuantity());
		}
	}
	
	public int getCarryable(ExpeditionItem item){
		return -1;
	}
	
	private int getCurrentWeight(){
		int currentlyCarrying = 0;
		List<Equipment> inventory = getItems();
		for (Equipment equipment: inventory){
			if (!(equipment.getItem() instanceof Vehicle)){
				currentlyCarrying += ((ExpeditionItem)equipment.getItem()).getWeight() * equipment.getQuantity();
			}
		}
		return currentlyCarrying;
	}

	@Override
	public AbstractFeature featureDestroyed(Actor actor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void counterFinished(String counterId) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean extendedInfoAvailable() {
		return false;
	}

	@Override
	public String getClassifierID() {
		return "Mound";
	}

	@Override
	public boolean isInvisible() {
		return false;
	}
	
	@Override
	public void onStep(Actor a) {
		if (a instanceof Expedition && !(a instanceof NonPrincipalExpedition)){
			switch (UserInterface.getUI().switchChat("Goods Cache","What do you want to do?", "Fetch Equipment", "Caché Equipment")){
			case 0:
				((ExpeditionUserInterface)UserInterface.getUI()).transferFromCache(this);
    			break;
			case 1:
				((ExpeditionUserInterface)UserInterface.getUI()).transferFromExpedition(this);
				break;
			}
		}
	}

	public boolean canCarry(ExpeditionItem item, int quantity) {
		return true;
	}

	public void addItem(AbstractItem item, int quantity) {
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
	
	@Override
	public String getDescription() {
		return "Equipment Caché";
	}

	@Override
	public boolean isSolid() {
		return true;
	}

	public int getTotalUnits() {
		int totalUnits = 0;
		List<Equipment> inventory = getItems();
		for (Equipment equipment: inventory){
			if (equipment.getItem() instanceof ExpeditionUnit){
				totalUnits += equipment.getQuantity();
			}
		}
		return totalUnits;
	}
	
	public void checkDeath() {
		// TODO Auto-generated method stub
		if (getItems().size() == 0){
			die();
		}
	}
	
	public void consumeFood() {
		foodConsumerDelegate.consumeFood();
	}
	
	public int getDailyFoodConsumption() {
		return foodConsumerDelegate.getDailyFoodConsumption();
	}
	
	public List<Equipment> getInventory() {
		return getItems();
	}
	/*
	public List<Equipment> getUnits() {
		List<Equipment> ret = new ArrayList<Equipment>();
		List<Equipment> inventory = getItems();
		for (Equipment equipment: inventory){
			if (equipment.getItem() instanceof ExpeditionUnit){
				ret.add(equipment);
			}
		}
		return ret;
	}
	
	public List<Equipment> getTools() {
		List<Equipment> ret = new ArrayList<Equipment>();  
		List<Equipment> inventory = getInventory();
		for (Equipment equipment: inventory){
			if (equipment.getItem() instanceof Weapon || equipment.getItem() instanceof Armor){
				ret.add(equipment);
			}
		}
		return ret;
	}*/
/*
	public List<Equipment> getGoods() {
		List<Equipment> ret = new ArrayList<Equipment>();  
		List<Equipment> inventory = getInventory();
		for (Equipment equipment: inventory){
			if (equipment.getItem() instanceof Good && !(equipment.getItem() instanceof Weapon || equipment.getItem() instanceof Armor || equipment.getItem() instanceof Trade)){
				ret.add(equipment);
			}
		}
		return ret;
	}*/
/*
	public List<Equipment> getValuables() {
		List<Equipment> ret = new ArrayList<Equipment>();  
		List<Equipment> inventory = getInventory();
		for (Equipment equipment: inventory){
			if (equipment.getItem() instanceof Valuable){
				ret.add(equipment);
			}
		}
		return ret;
	}
	*/
	
	
	
	public double getFoodConsumptionMultiplier() {
		return 1;
	}
	
	public void reduceQuantityOf(AbstractItem item, int quantity) {
		for (int i = 0; i < items.size(); i++){
			Equipment equipment = (Equipment) items.get(i);
			if (equipment.getItem().equals(item)){
				equipment.reduceQuantity(quantity);
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
				if (equipment.isEmpty()){
					items.remove(equipment);
					itemsHash.remove(equipment.getItem().getFullID());
				}
				return;
			}
		}
	}
	
	public boolean isInfiniteCapacity(){
		return true;
	}
	
	public void killUnits(int quantity) {
		Collection<Pair<ExpeditionUnit, Integer>> values = foodConsumerDelegate.killUnits(quantity);
		if (wasSeen()){
			String killMessage = "";
			int i = 0;
			for (Pair<ExpeditionUnit, Integer> killInfo: values){
				if (killInfo.getB() == 0){
					i++;
					continue;
				}
				if (killInfo.getB() == 1){
					killMessage += "A "+killInfo.getA().getDescription();
				} else
					killMessage += killInfo.getB()+" "+killInfo.getA().getPluralDescription();
				if (i == values.size()-2)
					killMessage += " and ";
				else if (i == values.size()-1)
					;
				else if (values.size()>1)
					killMessage += ", ";
				i++;
			}
			if (quantity == 1)
				getLevel().addMessage(killMessage +" dies.");
			else
				getLevel().addMessage(killMessage +" die.");
		}
	}

	public boolean destroyOnEmpty() {
		return true;
	}
	
	@Override
	public void addUnits(ExpeditionUnit unit, int quantity) {
		addItem(unit, quantity);
	}
	
	@Override
	public void reduceUnits(ExpeditionUnit unit, int quantity) {
		reduceQuantityOf(unit, quantity);
	}
	
	public int getGoodTypeCount(GoodType goodType) {
		int acum = 0;
		for (Equipment e: getInventory()){
			ExpeditionItem g = (ExpeditionItem) e.getItem();
			if (g.getGoodType() == goodType)
				acum += e.getQuantity();
		}
		return acum;
	}
	
	public List<Equipment> getGoods(GoodType goodType) {
		List<Equipment> ret = new ArrayList<Equipment>();
		for (Equipment e: getInventory()){
			ExpeditionItem g = (ExpeditionItem) e.getItem();
			if (g.getGoodType() == goodType)
				ret.add(new Equipment(e.getItem(), e.getQuantity()));
		}
		return ret;
	}

	public void addAllItems(List<Equipment> items) {
		for (Equipment equipment: items){
			addItem(equipment.getItem(), equipment.getQuantity());
		}
	}
	
	public void reduceAllItems(List<Equipment> items){
		for (Equipment equipment: items){
			reduceQuantityOf(equipment.getItem().getFullID(), equipment.getQuantity());
		}
	}
}
