package net.slashie.expedition.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.item.ItemFactory;
import net.slashie.expedition.item.Mount;
import net.slashie.expedition.town.Building;
import net.slashie.expedition.town.BuildingFactory;
import net.slashie.expedition.ui.ExpeditionUserInterface;
import net.slashie.expedition.world.CardinalDirection;
import net.slashie.expedition.world.ExpeditionCell;
import net.slashie.expedition.world.ExpeditionLevel;
import net.slashie.expedition.world.ExpeditionMacroLevel;
import net.slashie.expedition.world.FoodConsumer;
import net.slashie.expedition.world.FoodConsumerDelegate;
import net.slashie.expedition.world.OverworldExpeditionCell;
import net.slashie.expedition.world.TemperatureRules;
import net.slashie.serf.action.Actor;
import net.slashie.serf.baseDomain.AbstractItem;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.game.Player;
import net.slashie.serf.level.AbstractCell;
import net.slashie.serf.level.AbstractFeature;
import net.slashie.serf.ui.ActionCancelException;
import net.slashie.serf.ui.Appearance;
import net.slashie.serf.ui.AppearanceFactory;
import net.slashie.serf.ui.UserInterface;
import net.slashie.util.Pair;
import net.slashie.utils.Position;
import net.slashie.utils.Util;

public class Expedition extends Player implements FoodConsumer, UnitContainer{
	private ExpeditionUnit leaderUnit;
	
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
	
	public static final int DEATH_BY_STARVATION = 1, DEATH_BY_DROWNING = 2, DEATH_BY_SLAYING = 3;

	private static final String[] MORALE_DESCRIPTIONS = new String[] {
		"Rebellious",
		"Cracking appart",
		"Hopeless",
		"Downcast",
		"Restless",
		"Calm",
		"Steadfast",
		"Content",
		"Happy",
		"Joyous",
		"Victorious"
	};

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
			int reduction = 0;
			int boost = 0;
			if (!hasFullShipCrew()){
				reduction+=2;
			} 
			if (getShipHealth() < 70){
				reduction++;
			}
			if (getLocation().getWeather().isWindy()){
				boost++;
			}  
			MovementSpeed ret = getSailingPoint().getSpeed();
			for (int i = 0; i < reduction; i++){
				ret = ret.reduced();
			}
			for (int i = 0; i < boost; i++){
				ret = ret.boost();
			}
			boost = getMoraleSpeedModifier();
			if (boost <0){
				for (int i = 0; i < boost; i++){
					ret = ret.reduced();
				}
			} else {
				for (int i = 0; i < boost; i++){
					ret = ret.boost();
				}
			}
			return ret;
		} else if (getMovementMode() == MovementMode.HORSE){
			return MovementSpeed.FAST;
		} else {
			MovementSpeed ret = MovementSpeed.NORMAL;
			int boost = getMoraleSpeedModifier();
			if (boost <0){
				for (int i = 0; i < boost; i++){
					ret = ret.reduced();
				}
			} else {
				for (int i = 0; i < boost; i++){
					ret = ret.boost();
				}
			}
			
			if (getFoodDays() == 0){
				return MovementSpeed.SLOW;
			} else {
				return MovementSpeed.NORMAL;
			}
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
		int requiredSailors = ships * 25;
		
		int captains = getItemCountBasic("CAPTAIN");
		int sailors = getItemCountBasic("SAILOR");
		return captains >= requiredCaptains && sailors >= requiredSailors;
		
	}

	public Expedition(ExpeditionGame game) {
		HANDLE_FEATURES = false;
		setGame(game);
		foodConsumerDelegate = new FoodConsumerDelegate(this);
		game.addFoodConsumer(this);
		expeditionMorale = 5;
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
		VERY_SLOW ("Very Slow!", 80),
		SLOW ("Slow", 40),
		NORMAL ("", 20),
		FAST ("Fast", 10),
		VERY_FAST("Very Fast!", 5), 
		NONE ("No movement", 160);
		
		
		private MovementSpeed(String description, int movementCost) {
			this.description = description;
			this.movementCost = movementCost;
		}
		
		public MovementSpeed boost() {
			switch (this){
			case NONE: 
				return VERY_SLOW;
			case VERY_SLOW: 
				return SLOW;
			case SLOW:
				return NORMAL;
			case NORMAL:
				return FAST;
			case FAST: case VERY_FAST:
				return VERY_FAST;
			}
			return null;
		}

		public MovementSpeed reduced() {
			switch (this){
			case VERY_SLOW: case NONE:
				return NONE;
			case SLOW: 
				return VERY_SLOW;
			case NORMAL:
				return SLOW;
			case FAST:
				return NORMAL;
			case VERY_FAST:
				return FAST;
			}
			return null;
		}

		private String description;
		private int movementCost;
		
		public String getDescription() {
			return description;
		}
		public int getMovementCost() {
			return movementCost;
		}

		
		
	}

	/**
	 * Represent the gold credit of the player in Spain
	 */
	private int accountedGold;
	
	private String expeditionary;

	public static class Rank implements Serializable {
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

	private boolean justAttacked = false;
	
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
			// On foot, carry capacity is determined by the expedition itself, and its horses
			int carryCapacity = 0;
			List<Equipment> inventory = getInventory();
			for (Equipment equipment: inventory){
				if (equipment.getItem() instanceof Vehicle){
					carryCapacity += ((Vehicle)equipment.getItem()).getCarryCapacity() * equipment.getQuantity();
				}
				if (equipment.getItem() instanceof Mount){
					carryCapacity += ((Mount)equipment.getItem()).getCarryCapacity() * equipment.getQuantity();
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
	
	public int getTotalUnits(){
		int acum = 0;
		List<Equipment> units = getGoods(GoodType.PEOPLE);
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
	
	/**
	 * Ignores extreme food consumption conditions (like hibernating)
	 * @return
	 */
	public int getProjectedFoodDays(){
		return (int)Math.round((double)getCurrentFood()/(double)foodConsumerDelegate.getDailyFoodConsumption());
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
			if (justAttacked )
				deathCause = DEATH_BY_SLAYING;
			else
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
		int bonus = 0;
		int malus = 0;
		if (getItemCount("EXPLORER")>0)
			bonus++;
		if (getLocation() instanceof ExpeditionMacroLevel){
			bonus += ((OverworldExpeditionCell)getLocation().getMapCell(getPosition())).getHeightMod(); 
		}
		switch(getLocation().getWeather()){
		case CLEAR:
		case WINDY:
			malus = 0;
			break;
		case CLOUDY:
			malus = 1;
			break;
		case RAIN:
		case SNOW:
			malus = 2;
			break;
		case STORM:
		case GALE_WIND:
			malus = 3;
			break;
		case HURRICANE:
			malus = 4;
			break;
		case FOG:
		case DUST_STORM:
			malus = 6;
			break;
		}
		
		int ret = 8 + bonus - malus;
		if (ret >= 1 && ret <= 9)
			return ret;
		else if (ret < 1)
			return 1;
		else
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
			return getLeaderUnit().getAppearance();
		} else {
			switch (getMovementMode()){
			case FOOT:
				if (getCurrentCell() instanceof OverworldExpeditionCell && ((OverworldExpeditionCell) getCurrentCell()).isRiver()){
					return AppearanceFactory.getAppearanceFactory().getAppearance("BOAT_"+super.getAppearance().getID());
				} else {
					return super.getAppearance();
				}
			case SHIP:
				return AppearanceFactory.getAppearanceFactory().getAppearance("SHIP_EXPEDITION_"+getHeading().getAbbreviation());
			case HORSE:
				return AppearanceFactory.getAppearanceFactory().getAppearance("HORSE_EXPEDITION");
			}
		}
		return null;
	}


	private AbstractCell getCurrentCell() {
		return getLocation().getMapCell(getPosition());
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
	
	public int getItemCountBasic(String itemId) {
		int goodCount = 0;
		List<Equipment> inventory = getInventory();
		for (Equipment equipment: inventory){
			if (((ExpeditionItem)equipment.getItem()).getBaseID().equals(itemId)){
				goodCount += equipment.getQuantity();
			}
		}
		return goodCount;
	}
	

	public void reduceGood(String goodId, int quantity){
		List<Equipment> inventory = getInventory();
		for (Equipment equipment: inventory){
			if (equipment.getItem().getFullID().equals(goodId)){
				reduceQuantityOf(equipment.getItem(), quantity);
				return;
			}
		}
	}
	
	public void reduceAllItems(List<Equipment> items){
		for (Equipment equipment: items){
			reduceQuantityOf(equipment.getItem().getFullID(), equipment.getQuantity());
		}
	}

	public void consumeFood() {
		foodConsumerDelegate.consumeFood();
	}
	
	public int getDailyFoodConsumption() {
		if (isHibernate()){
			return 0;
		} else {
			return foodConsumerDelegate.getDailyFoodConsumption();
		}
	}

	

	/*public List<Equipment> getUnitsOverRange(int distance) {
		List<Equipment> ret = new ArrayList<Equipment>();  
		List<Equipment> inventory = getInventory();
		for (Equipment equipment: inventory){
			if (equipment.getItem() instanceof ExpeditionUnit && ((ExpeditionUnit)equipment.getItem()).getRange() >= distance){
				ret.add(equipment);
			}
		}
		return ret;
	}
	 */
	public int getSumOfValuables() {
		int currentValuable = 0;
		List<Equipment> inventory = getInventory();
		for (Equipment equipment: inventory){
			if (((ExpeditionItem)equipment.getItem()).getGoodType()== GoodType.TRADE_GOODS){
				ExpeditionItem good = (ExpeditionItem)equipment.getItem();
				currentValuable += Math.round(good.getEuropeValue() * (double)equipment.getQuantity());
			}
		}
		return currentValuable;
	}

	public void cashValuables() {
		int valuables = getSumOfValuables();
		List<Equipment> inventory = getInventory();
		for (int i = 0; i < inventory.size(); i++){
			Equipment equipment = inventory.get(i);
			if (((ExpeditionItem)equipment.getItem()).getGoodType()== GoodType.TRADE_GOODS){
				reduceQuantityOf(equipment.getItem(), equipment.getQuantity());
			}
		}
		addAccountedGold(valuables);
		
	}

	private void addAccountedGold(int valuables) {
		accountedGold += valuables;
	}
	
	public double getFoodConsumptionMultiplier() {
		/*switch (getMovementSpeed()){
		case FAST:
			return 1;
		case NORMAL:
			return 2;
		case SLOW:
			return 3;
		}*/
		if (getLevel() instanceof ExpeditionMacroLevel)
			return TemperatureRules.getTemperatureFoodModifier(getLocation().getTemperature());
		else
			return 1;

	}

	public int getOffshoreFoodDays() {
		MovementMode currentMovementMode = getMovementMode();
		setMovementMode(MovementMode.SHIP);
		int ret = getProjectedFoodDays();
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
		List<Equipment> units = getGoods(GoodType.PEOPLE);
		List<Equipment> ret = new ArrayList<Equipment>();
		for (Equipment unit: units){
			if (((ExpeditionUnit)unit.getItem()).getWeapon() == null){
				ret.add(unit);
			}
		}
		return ret;
	}
	
	public List<Equipment> getUnarmoredUnits() {
		List<Equipment> units = getGoods(GoodType.PEOPLE);
		List<Equipment> ret = new ArrayList<Equipment>();
		for (Equipment unit: units){
			if (((ExpeditionUnit)unit.getItem()).getArmor() == null){
				ret.add(unit);
			}
		}
		return ret;
	}

/*	public int getMaxFiringRange() {
		int maxRange = -1;
		for (Equipment unit: getUnits()){
			if (((ExpeditionUnit)unit.getItem()).getRange() > maxRange){
				maxRange = ((ExpeditionUnit)unit.getItem()).getRange();
			}
		}
		return maxRange;
	}*/

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
			getLevel().addMessage(killMessage +" dies.");
		else
			getLevel().addMessage(killMessage +" die.");
	}

	public void addTown(Town town) {
		towns.add(town);
	}
	
	public List<Town> getTowns(){
		return towns;
	}

	
	public void checkDrown() {
		if (getTotalShips() <= 0){
			OverworldExpeditionCell cell = (OverworldExpeditionCell) getLevel().getMapCell(getPosition());
			if (cell.isRiver()){
				setMovementMode(MovementMode.FOOT);
			} else {
				UserInterface.getUI().refresh();
				deathCause = DEATH_BY_DROWNING;
				informPlayerEvent (DEATH);
			}
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

	public void wearOutShips(int chance, boolean willingly) {
		if (getMovementMode() == MovementMode.SHIP){
			//Randomly damage ships
			List<Vehicle> vehicles = getCurrentVehicles();
			List<Vehicle> vehiclesToRemove = new ArrayList<Vehicle>();
			for (Vehicle vehicle: vehicles){
				vehicle.wearOut(getLevel(),chance, willingly);
				if (vehicle.isDestroyed()){
					vehiclesToRemove.add(vehicle);
					if (!willingly)
						modifyPerceivedLuck(-3);
				}
			}
			for (Vehicle vehicle: vehiclesToRemove){
				OverworldExpeditionCell cell = (OverworldExpeditionCell) getLevel().getMapCell(getPosition());
				if (cell.isRiver()){
					getLevel().addMessage("The "+vehicle.getDescription()+" breaks into the shallow water.");
				} else {
					getLevel().addMessage("You have lost a "+vehicle.getDescription()+" to the sea!");
				}
				vehicles.remove(vehicle);
			}
			
			checkDrown();
		}		
	}

	
	private CardinalDirection heading = CardinalDirection.WEST;
	
	public SailingPoint getSailingPoint(){
		if (getMovementMode() != MovementMode.SHIP)
			return SailingPoint.NONE;
		CardinalDirection windDirection = getLocation().getWindDirection();
		if (windDirection == CardinalDirection.NULL){
			return SailingPoint.STALLED;
		}
		int headingAngle = getHeading().getReferenceAngle();
		int angularDifference = (int)Math.abs(180-(headingAngle > windDirection.getReferenceAngle() ? headingAngle - windDirection.getReferenceAngle() : windDirection.getReferenceAngle() - headingAngle));
		if (getLocation().hasStorm(getPosition())){
			if (angularDifference == 0)
				return SailingPoint.RUNNING_STORM;
			else
				return SailingPoint.BARE_POLES;
		} else {
			return SailingPoint.resolvePoint(angularDifference);
		}
	}

	public CardinalDirection getHeading() {
		return heading;
	}

	public void setHeading(CardinalDirection heading) {
		this.heading = heading;
	}

	@Override
	public void updateStatus() {
		super.updateStatus();
		justAttacked = false;
	}

	public void setJustAttacked(boolean justAttacked) {
		this.justAttacked = justAttacked;
	}
	
	List<AbstractFeature> reusableFeatureList = new ArrayList<AbstractFeature>();
	@Override
	public void landOn(Position destinationPoint) throws ActionCancelException {
		boolean storm = getLocation().hasStorm(destinationPoint) && getMovementMode() == MovementMode.SHIP; 
        if (storm){
			getLevel().addMessage("You are caught on a Storm!");
			wearOutShips(20, true);
			increaseDeducedReckonWest(Util.rand(-5, 5));
			if (Util.chance(30)){
				//Random movement caused by the storm
				destinationPoint.x += Util.rand(-1, 1);
				destinationPoint.y += Util.rand(-1, 1);
			}
        }
        
        List<AbstractFeature> features = getLevel().getFeaturesAt(destinationPoint);
        if (features != null) {
        	//Clone the collection to prevent coModification issues
        	reusableFeatureList.clear();
        	reusableFeatureList.addAll(features); 
        	boolean solidFeature = false;
			for (AbstractFeature feature: reusableFeatureList){
	        	feature.onStep(this);
	        	if (feature.isSolid())
	        		solidFeature  = true;
        	}
        	if (solidFeature){
        		return;
        	}
        }
        
        AbstractCell absCell = getLevel().getMapCell(destinationPoint);
        if (absCell instanceof ExpeditionCell){
	        ExpeditionCell cell = (ExpeditionCell)absCell;
	        if (cell.getStore() != null){
	        	((ExpeditionUserInterface)UserInterface.getUI()).launchStore(cell.getStore());
	        	throw new ActionCancelException();
	        }
	        
	        if (cell.getStepCommand() != null){
	        	if (cell.getStepCommand().equals("DEPARTURE")){
	        		if (getTotalShips() == 0) {
        				getLevel().addMessage("You have no ships to board.");
        				throw new ActionCancelException();
	        		} else if (getItemCountBasic("SAILOR") < 15){
	        			getLevel().addMessage("You need at least 15 sailors to depart.");
        				throw new ActionCancelException();
	        		} else {
		        		if (((ExpeditionUserInterface)UserInterface.getUI()).depart()){
		        			String superLevelId = getLocation().getSuperLevelId();
		        			if (superLevelId == null){
		        				getLevel().addMessage("Nowhere to go.");
		        			} else {
		        				setMovementMode(MovementMode.SHIP);
		        				informPlayerEvent(Player.EVT_GOTO_LEVEL, superLevelId);
		        				//expedition.setCurrentVehicles(expedition.getShips());
		        				destinationPoint = new Position(getPosition());
		        				destinationPoint.x--;
		        		        super.landOn(destinationPoint);
				        		throw new ActionCancelException();

		        			}
		        		} 
		        		throw new ActionCancelException();
		        		
	        		}
	        	} else if (cell.getStepCommand().equals("TRAVEL_CASTLE")){
	        		((ExpeditionUserInterface)UserInterface.getUI()).showBlockingMessage("A chariot takes you to the Alcazar of Segovia");

	        		
	        	}
	        	
	        }
        } else {
	        OverworldExpeditionCell cell = (OverworldExpeditionCell)absCell;
	        /*if (!cell.isLand()&& !(getMovementMode() == MovementMode.SHIP)){
	        	// Walking into the sea, just check for features (ships) to board
	        	AbstractFeature feature = getLevel().getFeatureAt(destinationPoint);
	            if (feature != null && feature.isSolid()){
	            	feature.onStep(this);
	            }
	            throw new ActionCancelException();
	        }*/
	        
	        switch(getMovementMode()){
	        case SHIP:
	        	if (cell.isRiver()){
	        		wearOutShips(30, true);
	        	}
	        	if (cell.isLand() && !cell.isRiver()){
	        		if (UserInterface.getUI().promptChat("Do you want to land?")){
	        			GoodsCache ship = new ShipCache((ExpeditionGame)getGame(), getCurrentVehicles());
	        			ship.addAllGoods(this);
	        			removeAllGoods();
	        			setMovementMode(MovementMode.FOOT);
	        			
        				setCurrentVehicles(new ArrayList<Vehicle>());
	        			((ExpeditionUserInterface)UserInterface.getUI()).transferFromCache(ship);
	        			ship.setPosition(new Position(getPosition()));
	        			getLevel().addFeature(ship);
	        			if (getUnmountedUnits().size() == 0){
	    					setMovementMode(MovementMode.HORSE);
	    				}
	        			if (daysOnSea > 20){
	        				message("Land at last!");
	        			}
	        			resetDaysAtSea();
	        		} else {
	        			throw new ActionCancelException();
	        		}
	        	}
	        }
	        /*
	         * Dead Reckon Calculation
	         *  @ latitude 0 (apply for all latitudes to simplify the model)
				cells	592
				longitude degrees	30
				degrees / cell	0,050675676
				mt / degree 111321  (http://books.google.com.co/books?id=wu7zHtd2LO4C&hl=en)
				mt / cell	5641,266892
				nautical leagues / mt	0,000179
				nautical leagues / cell	1,009786774

	         */
	        
	        if (Util.chance(95)) {
	        	//Simulate the lack of precision
	        	increaseDeducedReckonWest(getPosition().x()-destinationPoint.x());
	        	//increaseDeducedReckonWest(-var.x());
	        }
        }
        

        super.landOn(destinationPoint);
	}
	
	private void resetDaysAtSea() {
		daysOnSea = 0;
	}

	@Override
	public void doNothing() {
		try {
			landOn(getPosition());
		} catch (ActionCancelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.doNothing();
	}
	
	@Override
	public void addUnits(ExpeditionUnit unit, int quantity) {
		addItem(unit, quantity);
	}
	
	public void addAllItems(List<Equipment> items){
		for (Equipment equipment: items){
			if (canCarry(equipment.getItem(), equipment.getQuantity())){
				addItem(equipment.getItem(), equipment.getQuantity());
			} else {
				GoodsCache cache = ((ExpeditionMacroLevel)getLevel()).getOrCreateCache(getPosition());
				cache.addItem(equipment.getItem(), equipment.getQuantity());				
			}
			
		}
	}
	
	@Override
	public void reduceUnits(ExpeditionUnit unit, int quantity) {
		reduceQuantityOf(unit, quantity);
		checkDeath();
	}

	
	public List<Equipment> getTools(boolean clone) {
		List<Equipment> ret = new ArrayList<Equipment>();  
		List<Equipment> inventory = getInventory();
		for (Equipment equipment: inventory){
			if (equipment.getItem() instanceof Weapon || equipment.getItem() instanceof Armor){
				if (clone){
					ret.add(new Equipment(equipment.getItem(), equipment.getQuantity()));
				} else {
					ret.add(equipment);
				}
				
			}
		}
		return ret;
	}

	public List<Equipment> getGoods(GoodType goodType, boolean clone) {
		List<Equipment> ret = new ArrayList<Equipment>();  
		List<Equipment> inventory = getInventory();
		for (Equipment equipment: inventory){
			if (((ExpeditionItem)equipment.getItem()).getGoodType() == goodType){
				if (clone){
					ret.add(new Equipment(equipment.getItem(), equipment.getQuantity()));
				} else {
					ret.add(equipment);
				}
			}
		}
		return ret;
	}
	
	public List<Equipment> getGoods(GoodType goodType) {
		return getGoods(goodType, false);
		
	}

	public ExpeditionUnit getLeaderUnit() {
		return leaderUnit;
	}

	public void setLeaderUnit(ExpeditionUnit leaderUnit) {
		this.leaderUnit = leaderUnit;
	}

	
	/**
	 * Determines how much building power has this expedition
	 * @return
	 */
	public int getBuildingCapacity() {
		int power = 0;
		List<Equipment> inventory = getInventory();
		for (Equipment equipment: inventory){
			if (equipment.getItem() instanceof ExpeditionUnit){
				int multiplier = ((ExpeditionUnit)equipment.getItem()).getBaseID().equals("CARPENTER") ? 2 : 1;
				power += equipment.getQuantity() * multiplier;
			}
		}
		return power;
	}


	/**
	 * Get the buildings the expeditions knows how to build
	 * @return
	 */
	public List<Building> getKnownBuildings() {
		// TODO Make this dependant on the advancement level of the expedition
		return BuildingFactory.getBuildingsList();
	}

	private boolean hibernate;

	private int winBalance = 0;

	private int daysOnSea;

	private int expeditionMorale;

	public boolean isHibernate() {
		return hibernate;
	}

	public void setHibernate(boolean hibernate) {
		this.hibernate = hibernate;
	}

	public enum Tip {
		ON_THE_WAY_TO_NEW_WORLD,
		YOU_ARE_ON_IRONS,
		TRAPPED_ON_A_STORM,
		LANDING_ON_NEW_WORLD,
		EXPANDING_SETTLEMENT
	}
	
	public void giveTip(Tip tip){
		if (!getFlag("TIP_GIVEN_"+tip)){
			setFlag("TIP_GIVEN_"+tip, true);
			switch (tip){
			case YOU_ARE_ON_IRONS:
				
				break;
			}
		}
	}

	public boolean forageFood() {
		if (!(getLocation() instanceof ExpeditionMacroLevel)){
			return false;
		}
		ExpeditionMacroLevel level = (ExpeditionMacroLevel)  getLocation();
		
		OverworldExpeditionCell cell = (OverworldExpeditionCell) getLevel().getMapCell(getPosition());
		if (Util.chance(cell.getForageChance())){
			int quantity = cell.getForageQuantity() ;
			String food = "";
			if (cell.isRiver()){
				if (isForaging()){
					food = "FISH";
					int multiplier = (int)Math.ceil(getTotalUnits()/10.0d);
					quantity *= multiplier;
					level.addMessage("You catch "+quantity+" fish.");
					modifyPerceivedLuck(1);
				} else {
					return false;
				}
			} else if (cell.isSea()) {
				food = "FISH";
				int multiplier = (int)Math.ceil(getItemCount("SAILOR")/25.0d);
				quantity *= multiplier;
				level.addMessage("You catch "+quantity+" fish.");
				modifyPerceivedLuck(1);
			} else {
				if (isForaging()){
					food = "FRUIT";
					int multiplier = (int)Math.ceil(getTotalUnits()/10.0d);
					quantity *= multiplier;
					level.addMessage("You forage "+quantity+" fruits.");
					modifyPerceivedLuck(1);
				} else {
					return false;
				}
				
			}
			ExpeditionItem foodSample = ItemFactory.createItem(food);
			addItem(foodSample, quantity);
			return true;
		} else {
			return false;
		}		
	}

	public boolean isForaging() {
		return getLocation() instanceof ExpeditionMacroLevel && getFoodDays() <= 5;
	}
	
	public boolean isMounted() {
		return getFlag("MOUNTED");
	}
	
	public void setMounted(boolean value){
		setFlag("MOUNTED", value);
	}

	public List<Equipment> getMounts() {
		List<Equipment> ret = new ArrayList<Equipment>();
		List<Equipment> inventory = getInventory();
		for (Equipment equipment: inventory){
			if (equipment.getItem() instanceof Mount){
				ret.add(equipment);
			}
		}
		return ret;
	}

	public List<Equipment> getUnmountedUnits() {
		List<Equipment> units = getGoods(GoodType.PEOPLE);
		List<Equipment> ret = new ArrayList<Equipment>();
		for (Equipment unit: units){
			if (((ExpeditionUnit)unit.getItem()).getMount() == null){
				ret.add(unit);
			}
		}
		return ret;
	}
	
	public int getMorale(){
		return expeditionMorale;
	}
	public void updateMorale(){
		if (getTotalUnits() == 0){
			expeditionMorale = 5;
			return;
		}
		expeditionMorale = 5;
		// Maluses
		// Lack of food
		if (getFoodDays() < 15)
			expeditionMorale --;
		if (getFoodDays() < 5)
			expeditionMorale --;
		// Military Losses
		if (winBalance  < -2)
			expeditionMorale --;
		if (winBalance < -5)
			expeditionMorale --;
		// Long time no see land
		if (daysOnSea > 20){
			if (Util.chance(20)) message("We need to touch land");
			expeditionMorale --;
		}
		if (daysOnSea > 40){
			if (Util.chance(20)) message("We really need to touch land!");
			expeditionMorale --;
		}
		// On a storm
		if (getMovementMode() == MovementMode.SHIP && getLocation().hasStorm(getPosition())){
			if (Util.chance(20)) message("This storm will be our doom!");
			expeditionMorale -= 2;
		}
		// Ships Cracking appart
		if (getMovementMode() == MovementMode.SHIP && getShipHealth() < 50 ){
			if (Util.chance(20)) message("Our ships are cracking apart.");
			expeditionMorale --;
		}
		// Becalmed
		if (getLocation().getWindDirection() == CardinalDirection.NULL){
			expeditionMorale --;
		}
		// Unlucky
		if (getPerceivedLuck() < -5){
			if (Util.chance(20)) message("We are in a row of back luck.");
			expeditionMorale --;
		}
		
		// Temporary mod
		if (hasCounter("MORALE_DOWN")){
			expeditionMorale --;
		}

		
		//Bonuses
		// Military Wins
		if (winBalance > 2)
			expeditionMorale ++;
		if (winBalance > 5)
			expeditionMorale ++;
		// Leaders
		if (getItemCountBasic("CAPTAIN") >= 1)
			expeditionMorale ++;
		if (getItemCountBasic("SOLDIER") >= 1 || getItemCountBasic("MARINE") >= 1){
			expeditionMorale ++;
		}
		// Loot
		if (getSumOfValuables() > 5000){
			if (Util.chance(20)) message("We are rich!");
			expeditionMorale ++;
		}
		// Ship in good health
		if (getMovementMode() == MovementMode.SHIP && getShipHealth() > 95 ){
			if (Util.chance(20)) message("Our ships sail steady!");
			expeditionMorale ++;
		}
		// Rum for the sailors
		if (getMovementMode() == MovementMode.SHIP && getItemCountBasic("RUM") > getItemCountBasic("SAILOR") * 5){
			if (Util.chance(20)) message("Rum for the sailors!");
			expeditionMorale ++;
		}
		// Lucky
		if (getPerceivedLuck() > 10){
			if (Util.chance(10)) message("We feel lucky");
			expeditionMorale ++;
		}
		
		// Temporary Mod
		if (hasCounter("MORALE_UP")){
			expeditionMorale ++;
		}
		
		if (expeditionMorale > 10){
			expeditionMorale = 10;
			return;
		}
		if (expeditionMorale < 0) {
			expeditionMorale = 0;
			return;
		}
	}
	
	public String getMoraleDescription(){
		return MORALE_DESCRIPTIONS [getMorale()];
		
	}

	public void increaseWinBalance() {
		winBalance++;
		if (winBalance == 10)
			winBalance = 10;
	}

	public void decreaseWinBalance() {
		winBalance--;
		if (winBalance == -10)
			winBalance = -10;
	}

	public void increaseDaysAtSea() {
		daysOnSea ++;
	}
	
	public int getMoraleAttackModifier(){
		int morale = getMorale();
		if (morale == 10)
			return 3;
		if (morale >= 8)
			return 2;
		if (morale >= 6)
			return 1;
		if (morale >= 4)
			return 0;
		if (morale >= 2)
			return -1;
		return -2;
	}

	public int getMoraleSpeedModifier(){
		int morale = getMorale();
		if (morale >= 8)
			return 2;
		if (morale >= 6)
			return 1;
		if (morale >= 4)
			return 0;
		if (morale >= 2)
			return -1;
		return -2;
	}

	public void randomEvents() {
		// Jonah
		if (getPerceivedLuck() < -10){
			if (getTotalUnits() > 1) {
				if (Util.chance(20)){
					int choice = UserInterface.getUI().switchChat("A man overboard is bringing us doom", "There is a Jonah amidst us, he must drown in the seas!XXX What will you do?", "Defend the man.", "Throw the man overboard.");
					if (choice == 0){
						getLevel().addMessage("We are doomed!");
						setCounter("MORALE_DOWN", 100);
					} else {
						List<Equipment> units = getGoods(GoodType.PEOPLE);
						Equipment randomEquipment = (Equipment) Util.randomElementOf(units);
						reduceQuantityOf(randomEquipment.getItem());
						getLevel().addMessage("You cast a "+randomEquipment.getItem().getDescription()+" into the sea. May he rests in peace");
						modifyPerceivedLuck(10);
						setCounter("MORALE_UP", 100);
					}
				}
			}
		}
		// Unease
		if (getMorale() < 4){
			if (Util.chance(5)){
				int choice = UserInterface.getUI().switchChat("Unease in your expedition", "Enough of this mindless journey! Let's head back home!XXX What will you do?", "Preach about the importance of this mission.", "Give away 100 gold.");
				if (choice == 0){
					int shot = Util.rand(0, 100);
					if (shot < 30){
						getLevel().addMessage("Your expedition feels motivated");
						setCounter("MORALE_UP", 100);
					} else if (shot < 60) {
						getLevel().addMessage("Your empty words do not make a difference!");
						setCounter("MORALE_DOWN", 100);
					} else {
						getLevel().addMessage("Your words make no effect on your men");
					}
				} else {
					if (getAccountedGold() >= 100){
						reduceAccountedGold(100);
						if (Util.chance(90)){
							getLevel().addMessage("We hope to survive enough to spend this gold.");
							setCounter("MORALE_UP", 100);
						} else {
							getLevel().addMessage("Gold is of no use to us.");
						}
					} else {
						setAccountedGold(0);
						if (Util.chance(10)){
							getLevel().addMessage("We hope to live enough to see that gold");
							setCounter("MORALE_UP", 100);
						} else {
							
							getLevel().addMessage("We do not believe in your empty promises!");
							setCounter("MORALE_DOWN", 100);
						}
					}
				}
				updateMorale();
			}
		}
		
	}
	
	private int perceivedLuck;

	public int getPerceivedLuck() {
		return perceivedLuck;
	}

	public void modifyPerceivedLuck(int perceivedLuckMod) {
		this.perceivedLuck += perceivedLuckMod;
		if (perceivedLuck > 15)
			perceivedLuck = 15;
		if (perceivedLuck < -15)
			perceivedLuck = -15;
	}

	public void dayShift() {
		wearOutShips(5, false);
		
		if (getMovementMode() == MovementMode.SHIP){
			increaseDaysAtSea();
			if (getLocation().getWindDirection() == CardinalDirection.NULL){
				modifyPerceivedLuck(-1);
			}
			if (getLocation().hasStorm(getPosition())){
				modifyPerceivedLuck(-1);
			}
		}
		updateMorale();
		randomEvents();
		if (Util.chance(50))
			modifyPerceivedLuck(1);

	}
	
}
