package net.slashie.expedition.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.world.ExpeditionLevel;
import net.slashie.expedition.world.ExpeditionMacroLevel;
import net.slashie.expedition.world.ExpeditionMicroLevel;
import net.slashie.expedition.world.FoodConsumer;
import net.slashie.expedition.world.FoodConsumerDelegate;
import net.slashie.expedition.world.OverworldExpeditionCell;
import net.slashie.serf.action.Actor;
import net.slashie.serf.baseDomain.AbstractItem;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.game.Player;
import net.slashie.serf.level.AbstractFeature;
import net.slashie.serf.ui.Appearance;
import net.slashie.serf.ui.AppearanceFactory;
import net.slashie.util.Pair;
import net.slashie.utils.Position;
import net.slashie.utils.Util;

public class Expedition extends Player implements FoodConsumer{
	private FoodConsumerDelegate foodConsumerDelegate; 
	public Expedition(ExpeditionGame game) {
		setGame(game);
		foodConsumerDelegate = new FoodConsumerDelegate(this);
		game.addFoodConsumer(this);
	}
	
	
	private List<Equipment> currentVehicles;
	public enum MovementMode {
		FOOT,
		HORSE,
		SHIP;
		
		public String getDescription(){
			switch (this){
			case FOOT:
				return "On Foot";
			case HORSE:
				return "Horseback";
			case SHIP:
				return "Ship";
			}
			return "None";
		}
	}
	
	private MovementMode movementMode = MovementMode.FOOT;
	

	/**
	 * Represent the gold credit of the player in Spain
	 */
	private int accountedGold;
	
	private String expeditionary;
	private String expeditionaryTitle;
	
	public List<Equipment> getCurrentVehicles() {
		return currentVehicles;
	}

	public void setCurrentVehicles(List<Equipment> currentVehicles) {
		this.currentVehicles = currentVehicles;
	}

	public int getLastActionTimeCost() {
		return getCost();
	}

	public MovementMode getMovementMode() {
		return movementMode;
	}

	public void setMovementMode(MovementMode movementMode) {
		this.movementMode = movementMode;
	}

	public int getAccountedGold() {
		return accountedGold;
	}

	public void setAccountedGold(int accountedGold) {
		this.accountedGold = accountedGold;
	}
/*
	public Expedition getOffshoreExpedition() {
		return offshoreExpedition;
	}

	public void setOffshoreExpedition(Expedition offshoreExpedition) {
		this.offshoreExpedition = offshoreExpedition;
	}*/

	public String getExpeditionary() {
		return expeditionary;
	}

	public void setExpeditionary(String expeditionary) {
		this.expeditionary = expeditionary;
	}

	public String getExpeditionaryTitle() {
		return expeditionaryTitle;
	}

	public void setExpeditionaryTitle(String expeditionaryTitle) {
		this.expeditionaryTitle = expeditionaryTitle;
	}


	
	public int getCurrentlyCarrying(){
		if (getCarryCapacity() == 0)
			return 101;
		return (int)Math.round(((double)getCurrentWeight()/(double)getCarryCapacity())*100.0d);
	}


	private int getCarryCapacity(){
		if (getMovementMode() != MovementMode.FOOT){
			int carryCapacity = 0;
			List<Equipment> inventory = getCurrentVehicles();
			for (Equipment equipment: inventory){
				carryCapacity += ((Vehicle)equipment.getItem()).getCarryCapacity() * equipment.getQuantity();
			}
			return carryCapacity;
		} else {
			// On foot, carry capacity is determined by the expedition itself
			int carryCapacity = 0;
			List<Equipment> inventory = getInventory();
			for (Equipment equipment: inventory){
				if (equipment.getItem() instanceof Vehicle){
					carryCapacity += ((Vehicle)equipment.getItem()).getCarryCapacity() * equipment.getQuantity();
				}
			}
			return carryCapacity;
		}
	}
	
	private int getCurrentWeight(){
		if (getMovementMode() != MovementMode.FOOT){
			// All expedition aboard vehicles. Vehicles must carry the weight of all the units along with the goods
			int currentlyCarrying = 0;
			List<Equipment> inventory = getInventory();
			for (Equipment equipment: inventory){
				currentlyCarrying += ((ExpeditionItem)equipment.getItem()).getWeight() * equipment.getQuantity();
			}
			return currentlyCarrying;
		} else {
			// On foot, vehicles (including units) take care of carrying themselves around
			int currentlyCarrying = 0;
			List<Equipment> inventory = getInventory();
			for (Equipment equipment: inventory){
				if (!(equipment.getItem() instanceof Vehicle)){
					currentlyCarrying += ((ExpeditionItem)equipment.getItem()).getWeight() * equipment.getQuantity();
				}
			}
			return currentlyCarrying;
		}
	}
	
	public List<Equipment> getShips(){
		List<Equipment> ret = new ArrayList<Equipment>();  
		List<Equipment> inventory = getInventory();
		for (Equipment equipment: inventory){
			if (equipment.getItem() instanceof Vehicle){
				if (((Vehicle)equipment.getItem()).isMoveOnWater())
					ret.add(equipment);
			}
		}
		return ret;
	}
	
	public int getCarryable(ExpeditionItem item){
		return (int)Math.floor((getCarryCapacity()-getCurrentWeight())/item.getWeight());
	}
	
	
	
	public int getPower(){
		int power = 0;
		List<Equipment> inventory = getInventory();
		for (Equipment equipment: inventory){
			if (equipment.getItem() instanceof ExpeditionUnit){
				power += ((ExpeditionUnit)equipment.getItem()).getPower() * equipment.getQuantity();
			}
		}
		return power;
	}
	
	public List<Equipment> getUnits(){
		List<Equipment> ret = new ArrayList<Equipment>();  
		List<Equipment> inventory = getInventory();
		for (Equipment equipment: inventory){
			if (equipment.getItem() instanceof ExpeditionUnit){
				ret.add(equipment);
			}
		}
		return ret;
	}
	
	public int getTotalUnits(){
		int acum = 0;
		List<Equipment> units = getUnits();
		for (Equipment unit: units){
			acum += unit.getQuantity();
		}
		return acum;
	}
	
	public int getFoodDays(){
		if (getDailyFoodConsumption() == 0){
			return 0;
		}
		return (int)Math.round((double)getCurrentFood()/(double)getDailyFoodConsumption());
	}
	
	private int getCurrentFood(){
		int currentFood = 0;
		List<Equipment> inventory = getInventory();
		for (Equipment equipment: inventory){
			if (equipment.getItem() instanceof Good){
				Good good = (Good)equipment.getItem();
				if (good.getGoodType() == GoodType.FOOD){
					currentFood += good.getUnitsFedPerGood() * equipment.getQuantity();
				}
			}
		}
		return currentFood;
	}
	

	/*private int getDailyFoodConsumption(){
		int dailyFoodConsumption = 0;
		List<Equipment> inventory = getInventory();
		for (Equipment equipment: inventory){
			if (equipment.getItem() instanceof ExpeditionUnit){
				ExpeditionUnit unit = (ExpeditionUnit)equipment.getItem();
				dailyFoodConsumption += unit.getDailyFoodConsumption() * equipment.getQuantity();
			}
		}
		return dailyFoodConsumption;
	}*/
	
	//private int expeditionStarveResistance = 5;
	
	/*public void elapseDay(){
		int remainder = reduceFood(getDailyFoodConsumption());
		if (remainder > 0){
			//Reduce expedition resistance
			expeditionStarveResistance --;
			if (expeditionStarveResistance <= 0){
				killUnits((double)Util.rand(5, 40)/100.0d);
			}
		} else {
			if (expeditionStarveResistance < 5)
				expeditionStarveResistance++;
		}
	}*/
	
	/*public void killUnits(double proportion){
		List<Equipment> inventory = getInventory();
		for (Equipment equipment: inventory){
			if (equipment.getItem() instanceof ExpeditionUnit){
				int killUnits = (int)Math.ceil(equipment.getQuantity() * proportion);
				equipment.reduceQuantity(killUnits);
			}
		}
		checkDeath();
	}*/
	
	public void checkDeath(){
		if (getTotalUnits() <= 0){
			informPlayerEvent (DEATH);
		}
	}
	
	/*public int reduceFood(int quantity){
		int foodToSpend = quantity;
		List<Equipment> inventory = getInventory();
		for (Equipment equipment: inventory){
			if (equipment.getItem() instanceof Good){
				Good good = (Good)equipment.getItem();
				if (good.getGoodType() == GoodType.FOOD){
					int unitsToSpend = (int)Math.ceil((double)foodToSpend / (double)good.getUnitsFedPerGood());
					if (unitsToSpend > equipment.getQuantity()){
						unitsToSpend = equipment.getQuantity();
					}
					foodToSpend -= unitsToSpend * good.getUnitsFedPerGood();
					equipment.reduceQuantity(unitsToSpend);
					if (foodToSpend <= 0){
						return 0;
					}
				}
			}
		}
		return foodToSpend;
	}*/
	
	public void recalcCapacity(){
		//Should I refresh the stats on demand instead of always calculating them??
	}
	
	//private Pair<Integer, Integer> spainLocation = new Pair<Integer, Integer>(37,-6);
	public ExpeditionLevel getLocation(){
		/*if (getLevel().getID().equals("SPAIN")){
			return spainLocation;
		}
		return null;*/
		return (ExpeditionLevel) getLevel();
	}
	

	
	@Override
	public void beforeItemAddition(AbstractItem item) {}

	@Override
	public boolean canCarry(AbstractItem item, int quantity) {
		if (!(item instanceof ExpeditionItem))
			return false;
		if (getMovementMode() == MovementMode.FOOT && item instanceof Vehicle)
			return true;
		ExpeditionItem expItem = (ExpeditionItem) item;
		return getCurrentWeight() + (expItem.getWeight() * quantity) <= getCarryCapacity();
	}

	@Override
	public int getDarkSightRange() {
		return 9;
	}

	@Override
	public List<AbstractItem> getEquippedItems() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Position getFreeSquareAround(Position p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSaveFilename() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getSightRange() {
		return 9;
	}

	@Override
	public String getStatusString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onActorStep(Actor actor) {
		
	}

	@Override
	public void onFeatureStep(AbstractFeature destinationFeature) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onItemsStep(List<AbstractItem> items) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onItemStep(AbstractItem item) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNullDestination() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSolidDestination() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void counterFinished(String counterId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean extendedInfoAvailable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getClassifierID() {
		return "MAIN_EXPEDITION";
	}

	@Override
	public boolean isInvisible() {
		return false;
	}
	
	public void reduceAccountedGold(int gold){
		accountedGold -= gold;
	}
	
	public Appearance getAppearance(){
		switch (getMovementMode()){
		case FOOT:
			return super.getAppearance();
		case SHIP:
			return AppearanceFactory.getAppearanceFactory().getAppearance("SHIP_EXPEDITION");
		case HORSE:
			return AppearanceFactory.getAppearanceFactory().getAppearance("HORSE_EXPEDITION");
		}
		return null;
	}

	public String getWeather() {
		if (getLevel() instanceof ExpeditionMicroLevel){
			return ((ExpeditionLevel)getLevel()).getWeather();
		} else if (getLevel() instanceof ExpeditionMacroLevel){
			return ((OverworldExpeditionCell)getLevel().getMapCell(getPosition())).getWeather();
		} else
			return null;
	}

	public int getTemperature() {
		if (getLevel() instanceof ExpeditionMicroLevel){
			return ((ExpeditionLevel)getLevel()).getTemperature();
		} else if (getLevel() instanceof ExpeditionMacroLevel){
			return ((OverworldExpeditionCell)getLevel().getMapCell(getPosition())).getTemperature();
		} else
			return 6;
	}

	public void removeAllGoods() {
		removeAllItems();
	}

	public int getOffshoreCurrentlyCarrying() {
		MovementMode currentMovementMode = getMovementMode();
		setMovementMode(MovementMode.SHIP);
		int retValue = getCurrentlyCarrying();
		setMovementMode(currentMovementMode);
		return retValue;
	}

	public int getOffshoreCarryable(ExpeditionItem item) {
		MovementMode currentMovementMode = getMovementMode();
		setMovementMode(MovementMode.SHIP);
		int retValue = getCarryable(item);
		setMovementMode(currentMovementMode);
		return retValue;
	}


	public void addItemOffshore(ExpeditionItem what, int quantity){
		MovementMode currentMovementMode = getMovementMode();
		setMovementMode(MovementMode.SHIP);
		addItem(what, quantity);
		setMovementMode(currentMovementMode);
	}
	
	public boolean canCarryOffshore (ExpeditionItem what, int quantity){
		MovementMode currentMovementMode = getMovementMode();
		setMovementMode(MovementMode.SHIP);
		boolean ret = canCarry(what, quantity);
		setMovementMode(currentMovementMode);
		return ret;
	}

	public int getWeightToBoardShip() {
		int currentlyCarrying = 0;
		List<Equipment> inventory = getInventory();
		for (Equipment equipment: inventory){
			currentlyCarrying += ((ExpeditionItem)equipment.getItem()).getWeight() * equipment.getQuantity();
		}
		return currentlyCarrying;
	}

	public int getGoodCount(String string) {
		int goodCount = 0;
		List<Equipment> inventory = getInventory();
		for (Equipment equipment: inventory){
			if (equipment.getItem() instanceof Good){
				Good good = (Good)equipment.getItem();
				if (good.getFullID().equals(string)){
					goodCount += equipment.getQuantity();
				}
			}
		}
		return goodCount;
	}

	public void reduceGood(String goodId, int quantity){
		List<Equipment> inventory = getInventory();
		for (Equipment equipment: inventory){
			if (equipment.getItem() instanceof Good){
				Good good = (Good)equipment.getItem();
				if (good.getFullID().equals(goodId)){
					equipment.reduceQuantity(quantity);
					return;
				}
			}
		}
	}

	public void consumeFood() {
		foodConsumerDelegate.consumeFood();
	}
	public int getDailyFoodConsumption() {
		return foodConsumerDelegate.getDailyFoodConsumption();
	}

	public void killUnitsOnMeleeBattle(int deaths) {
		int toKill = deaths;
		List<Equipment> inventory = getInventory();
		String killMessage = "";
		Hashtable<String, Pair<ExpeditionUnit, Integer>> acumHash = new Hashtable<String, Pair<ExpeditionUnit,Integer>>();
		while (toKill > 0){
			for (Equipment equipment: inventory){
				in: if (equipment.getItem() instanceof ExpeditionUnit){
					int killUnits = Util.rand(1, toKill);
					if (killUnits > equipment.getQuantity()){
						killUnits = equipment.getQuantity();
					}
					if (killUnits == 0)
						break in;
					String itemId = equipment.getItem().getFullID();
					Pair<ExpeditionUnit, Integer> currentlyKilled = acumHash.get(itemId);
					if (currentlyKilled == null){
						currentlyKilled = new Pair<ExpeditionUnit, Integer>((ExpeditionUnit)equipment.getItem(), killUnits);
						acumHash.put(itemId, currentlyKilled);
					} else {
						currentlyKilled.setB(currentlyKilled.getB()+killUnits);
					}
					toKill -= killUnits;
					equipment.reduceQuantity(killUnits);
				}
			}
			if (getTotalUnits() == 0)
				break;
		}
		Collection<Pair<ExpeditionUnit, Integer>> values = acumHash.values();
		int i = 0;
		for (Pair<ExpeditionUnit, Integer> killInfo: values){
			if (killInfo.getB() == 0){
				i++;
				continue;
			}
			if (killInfo.getB() == 1)
				killMessage += killInfo.getB()+" "+killInfo.getA().getDescription();
			else
				killMessage += killInfo.getB()+" "+killInfo.getA().getPluralDescription();
			if (i == values.size()-2)
				killMessage += " and ";
			else if (i == values.size()-1)
				;
			else if (values.size()>1)
				killMessage += ", ";
			i++;
		}
		
		level.addMessage(killMessage +" die in the attack.");
		checkDeath();
	}

	public List<Equipment> getUnitsOverRange(int distance) {
		List<Equipment> ret = new ArrayList<Equipment>();  
		List<Equipment> inventory = getInventory();
		for (Equipment equipment: inventory){
			if (equipment.getItem() instanceof ExpeditionUnit && ((ExpeditionUnit)equipment.getItem()).getRange() >= distance){
				ret.add(equipment);
			}
		}
		return ret;
	}
}
