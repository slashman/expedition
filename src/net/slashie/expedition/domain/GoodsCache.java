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

public class GoodsCache extends AbstractFeature implements FoodConsumer{
	private FoodConsumerDelegate foodConsumerDelegate; 
	
	public GoodsCache(ExpeditionGame game) {
		setAppearanceId("GOODS_CACHE");
		foodConsumerDelegate = new FoodConsumerDelegate(this);
		game.addFoodConsumer(this);
	}
	
	public GoodsCache() {
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
			switch (UserInterface.getUI().switchChat("What do you want to do?", "Fetch Equipment", "Caché Equipment")){
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
		} else
			equipmentx.increaseQuantity(quantity);

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
	
	public double getFoodConsumptionMultiplier() {
		return 1;
	}
	
	public void reduceQuantityOf(AbstractItem item, int quantity) {
		for (int i = 0; i < items.size(); i++){
			Equipment equipment = (Equipment) items.get(i);
			if (equipment.getItem().equals(item)){
				equipment.reduceQuantity(quantity);
				if (equipment.isEmpty())
					items.remove(equipment);
				return;
			}
		}
	}
	
	public boolean isInfiniteCapacity(){
		return true;
	}
	
	public void killUnits(int deaths) {
		foodConsumerDelegate.killUnits(deaths);
	}

	public boolean destroyOnEmpty() {
		return true;
	}
	
}
