package net.slashie.expedition.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
import net.slashie.serf.ui.UserInterface;
import net.slashie.util.Pair;
import net.slashie.utils.Position;
import net.slashie.utils.Util;

public class Expedition extends Player implements FoodConsumer{
	public enum Title {
		EXPLORER (1, "Explorador", 0, 0, 0, 0),
		HIDALGO  (2, "Hidalgo",    0, 0, 1, 20000),
		LORD     (3, "Señor",      0, 1, 2, 50000),
		VIZCOUNT (4, "Vizconde",   1, 2, 5, 100000),
		COUNT    (5, "Conde",      2, 5,10, 150000),
		MARCHIS  (6, "Marqués",    5,10,30, 300000),
		DUKE     (7, "Duque",     10,30,60, 500000),
		VICEROY  (8, "Virrey",    30,60,90, 950000)
		;
		private int requiredCities, requiredTowns, requiredVillages;
		private int rank;
		private int prize;
		private String description;
		
		private Title(int rank, String description, int requiredCities, int requiredTowns, int requiredVillages, int prize){
			this.rank = rank;
			this.description = description;
			this.requiredTowns = requiredTowns;
			this.requiredCities = requiredCities;
			this.requiredVillages = requiredVillages;
			this.prize = prize;
		}
		
		public int getRank(){
			return rank;
		}

		public String getDescription() {
			return description;
		}
		
		public boolean attainsRank(Expedition e){
			if (e.getTitle().getTitle().getRank() > getRank())
				return false;
			int cities = 0;
			int towns = 0;
			int villages = 0;
			for (Town town: e.getTowns()){
				if (town.isCity()) {
					cities++;
				} else if (town.isTown()){
					towns++;
				} else {  
					villages++;
				}
			}
			return cities >= requiredCities && towns >= requiredTowns && villages >= requiredVillages;
		}

		public String pickRealm(Expedition exp) {
			if (this == VICEROY)
				return "the New World"; 
			for (Town town: exp.getTowns()){
				if (requiredCities > 0) {
					if (town.isCity())
						return town.getName();
					else 
						continue;
				} else if (requiredTowns > 0){
					if ( town.isTown())
						return town.getName();
					else 
						continue;
				} else { 
					return town.getName();
				}
			}
			return "";
		}

		public int getPrize() {
			return prize;
		}
	}
	
	public static final int DEATH_BY_STARVATION = 1, DEATH_BY_DROWNING = 2;

	private int deducedReckonWest;
	
	public void resetDeducedReckonWest(){
		deducedReckonWest = 0;
	}
	
	public void increaseDeducedReckonWest(int q){
		deducedReckonWest += q;
	}
	
	private FoodConsumerDelegate foodConsumerDelegate;
	
	public int getDeducedReckonWest() {
		return deducedReckonWest;
	}

	public MovementSpeed getMovementSpeed() {
		if (getMovementMode() == MovementMode.SHIP){
			if (hasFullShipCrew()){
				return MovementSpeed.FAST;
			} else {
				return MovementSpeed.SLOW;
			}
		} else {
			return MovementSpeed.NORMAL;
		}
		
	}

	/**
	 * Determines if the expedition has all the crew it needs to sail
	 * quickly.
	 * 
	 * Crew per ship is:
	 *  1 Captain
	 *  10 Sailors
	 * @return
	 */
	private boolean hasFullShipCrew() {
		int ships = getTotalShips();
		int requiredCaptains = ships;
		int requiredSailors = ships * 10;
		
		int captains = getItemCount("CAPTAIN");
		int sailors = getItemCount("SAILOR");
		return captains >= requiredCaptains && sailors >= requiredSailors;
		
	}

	public Expedition(ExpeditionGame game) {
		setGame(game);
		foodConsumerDelegate = new FoodConsumerDelegate(this);
		game.addFoodConsumer(this);
	}
	
	
	private List<Vehicle> currentVehicles = new ArrayList<Vehicle>();
	
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
	
	public enum MovementSpeed {
		SLOW,
		NORMAL,
		FAST;
		
		public String getDescription(){
			switch (this){
			case SLOW:
				return "Slow";
			case NORMAL:
				return "";
			case FAST:
				return "Fast";
			}
			return "None";
		}
		
	}

	/**
	 * Represent the gold credit of the player in Spain
	 */
	private int accountedGold;
	
	private String expeditionary;

	public static class Rank {
		private Title title;
		private String realm;
		
		public void grantTitle(Title title, String realm){
			this.title = title;
			this.realm = realm;
		}
		
		public String getDescription (String name) {
			if (title == null)
				return name;
			else
				return title.getDescription() + " " + name;
		}

		public String getFullDescription (String name) {
			if (title == null)
				return name;
			else
				return title.getDescription() + " " + name + " of " + realm;
		}
		
		public Title getTitle() {
			return title;
		}

		public int getPrize() {
			return title.getPrize();
		}
		
	}
	
	private Rank expeditionaryTitle = new Rank();

	private List<Town> towns = new ArrayList<Town>();

	private int deathCause;
	
	public List<Vehicle> getCurrentVehicles() {
		return currentVehicles;
	}

	public void setCurrentVehicles(List<Vehicle> currentVehicles) {
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

	public Rank getTitle() {
		return expeditionaryTitle;
	}
	
	public String getExpeditionaryTitle() {
		if (expeditionaryTitle != null)
			return expeditionaryTitle.getDescription(getExpeditionary());
		else
			return getExpeditionary();
	}

	public int getCurrentlyCarrying(){
		if (getCarryCapacity() == 0)
			return 101;
		return (int)Math.round(((double)getCurrentWeight()/(double)getCarryCapacity())*100.0d);
	}


	public int getCarryCapacity(){
		if (getMovementMode() != MovementMode.FOOT){
			int carryCapacity = 0;
			List<Vehicle> inventory = getCurrentVehicles();
			for (Vehicle vehicle: inventory){
				carryCapacity += vehicle.getCarryCapacity();
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
				//If the equipment is an unit, and it has equipment, they must carry the weight of their equipment!
				if (equipment.getItem() instanceof ExpeditionUnit){
					ExpeditionUnit unit = (ExpeditionUnit) equipment.getItem();
					if (unit.getWeapon() != null){
						currentlyCarrying += unit.getWeapon().getWeight() * equipment.getQuantity();
					}
				}
			}
			return currentlyCarrying;
		}
	}
	
/*	public List<Equipment> getShips(){
		List<Equipment> ret = new ArrayList<Equipment>();  
		List<Equipment> inventory = getInventory();
		for (Equipment equipment: inventory){
			if (equipment.getItem() instanceof Vehicle){
				if (((Vehicle)equipment.getItem()).isMoveOnWater())
					ret.add(equipment);
			}
		}
		return ret;
	}*/
	
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
			if (equipment.getItem() instanceof Food){
				Food good = (Food)equipment.getItem();
				currentFood += good.getUnitsFedPerGood() * equipment.getQuantity();
			}
		}
		return currentFood;
	}
	
	public void checkDeath(){
		if (getTotalUnits() <= 0){
			UserInterface.getUI().refresh();
			deathCause = DEATH_BY_STARVATION;
			informPlayerEvent (DEATH);
		}
	}
	
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
	public void beforeItemsAddition(AbstractItem item, int quantity) {
		
	}

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
		if (getLocation().isZoomIn()){
			return AppearanceFactory.getAppearanceFactory().getAppearance("EXPLORER");
		} else {
			switch (getMovementMode()){
			case FOOT:
				return super.getAppearance();
			case SHIP:
				return AppearanceFactory.getAppearanceFactory().getAppearance("SHIP_EXPEDITION");
			case HORSE:
				return AppearanceFactory.getAppearanceFactory().getAppearance("HORSE_EXPEDITION");
			}
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

	public String getTemperature() {
		if (getLevel() instanceof ExpeditionMicroLevel){
			return ((ExpeditionLevel)getLevel()).getTemperature();
		} else if (getLevel() instanceof ExpeditionMacroLevel){
			return ((OverworldExpeditionCell)getLevel().getMapCell(getPosition())).getTemperature();
		} else
			return "Warm";
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
		if (what instanceof Vehicle && !((Vehicle)what).isFakeVehicle()){
			for (int i = 0; i < quantity; i++){
				currentVehicles.add((Vehicle)((ExpeditionItem)what).clone());
			}
		} else {
			addItem(what, quantity);
		}
		
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
	
	public int getItemCount(String itemId) {
		int goodCount = 0;
		List<Equipment> inventory = getInventory();
		for (Equipment equipment: inventory){
			if (equipment.getItem().getFullID().equals(itemId)){
				goodCount += equipment.getQuantity();
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
					reduceQuantityOf(equipment.getItem(), quantity);
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

	public int getSumOfValuables() {
		int currentValuable = 0;
		List<Equipment> inventory = getInventory();
		for (Equipment equipment: inventory){
			if (equipment.getItem() instanceof Valuable){
				Valuable good = (Valuable)equipment.getItem();
				currentValuable += good.getGoldValue() * equipment.getQuantity();
			}
		}
		return currentValuable;
	}

	public void cashValuables() {
		int valuables = getSumOfValuables();
		List<Equipment> inventory = getInventory();
		for (int i = 0; i < inventory.size(); i++){
			Equipment equipment = inventory.get(i);
			if (equipment.getItem() instanceof Good){
				Good good = (Good)equipment.getItem();
				if (good.getGoodType() == GoodType.VALUABLE){
					reduceQuantityOf(good, equipment.getQuantity());
				}
			}
		}
		addAccountedGold(valuables);
		
	}

	private void addAccountedGold(int valuables) {
		accountedGold += valuables;
	}
	
	public int getFoodConsumptionMultiplier() {
		/*switch (getMovementSpeed()){
		case FAST:
			return 1;
		case NORMAL:
			return 2;
		case SLOW:
			return 3;
		}*/

		return 1;
	}

	public int getOffshoreFoodDays() {
		MovementMode currentMovementMode = getMovementMode();
		setMovementMode(MovementMode.SHIP);
		int ret = getFoodDays();
		setMovementMode(currentMovementMode);
		return ret;
	}

	public int getTotalShips() {
		return getCurrentVehicles().size();
	}

	public boolean isArmed() {
		return getFlag("ARMED");
	}
	
	public void setArmed(boolean value){
		setFlag("ARMED", value);
	}

	public List<Equipment> getUnarmedUnits() {
		List<Equipment> units = getUnits();
		List<Equipment> ret = new ArrayList<Equipment>();
		for (Equipment unit: units){
			if (((ExpeditionUnit)unit.getItem()).getWeapon() == null){
				ret.add(unit);
			}
		}
		return ret;
	}
	
	public List<Equipment> getUnarmoredUnits() {
		List<Equipment> units = getUnits();
		List<Equipment> ret = new ArrayList<Equipment>();
		for (Equipment unit: units){
			if (((ExpeditionUnit)unit.getItem()).getArmor() == null){
				ret.add(unit);
			}
		}
		return ret;
	}

	public int getMaxFiringRange() {
		int maxRange = -1;
		for (Equipment unit: getUnits()){
			if (((ExpeditionUnit)unit.getItem()).getRange() > maxRange){
				maxRange = ((ExpeditionUnit)unit.getItem()).getRange();
			}
		}
		return maxRange;
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
	}

	public List<Equipment> getGoods() {
		List<Equipment> ret = new ArrayList<Equipment>();  
		List<Equipment> inventory = getInventory();
		for (Equipment equipment: inventory){
			if (equipment.getItem() instanceof Good && !(equipment.getItem() instanceof Weapon || equipment.getItem() instanceof Armor || equipment.getItem() instanceof Valuable)){
				ret.add(equipment);
			}
		}
		return ret;
	}

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
	
	public void killUnits(int quantity) {
		Collection<Pair<ExpeditionUnit, Integer>> values = foodConsumerDelegate.killUnits(quantity);
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
			level.addMessage(killMessage +" dies.");
		else
			level.addMessage(killMessage +" die.");
	}

	public void addTown(Town town) {
		towns.add(town);
	}
	
	public List<Town> getTowns(){
		return towns;
	}

	
	public void checkDrown() {
		if (getTotalShips() <= 0){
			UserInterface.getUI().refresh();
			deathCause = DEATH_BY_DROWNING;
			informPlayerEvent (DEATH);
		}
	}

	public int getDeathCause() {
		return deathCause;
	}

	public int getShipHealth() {
		int sum = 0;
		int sumMax = 0;
		for (Vehicle vehicle: getCurrentVehicles()){
			sum += vehicle.getResistance();
			sumMax += vehicle.getMaxResistance();
		}
		return (int)(Math.round ( ((double)sum/(double)sumMax) *100.0d));
	}

}
