package net.slashie.expedition.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.slashie.expedition.action.BuildBuildings;
import net.slashie.expedition.action.Hibernate;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.item.ItemFactory;
import net.slashie.expedition.item.StorageType;
import net.slashie.expedition.town.Building;
import net.slashie.expedition.town.Farm;
import net.slashie.expedition.ui.ExpeditionUserInterface;
import net.slashie.expedition.world.ExpeditionMacroLevel;
import net.slashie.expedition.world.OverworldExpeditionCell;
import net.slashie.lang.Percentage;
import net.slashie.serf.action.Actor;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.ui.UserInterface;
import net.slashie.util.Pair;
import net.slashie.utils.Util;

/**
 * Represents a settlement founded by the Expedition.
 * 
 * The base inventory of the GoodsCache represents units and goods
 * placed temporary by the expedition on the town, as well as goods
 * available for the expeditionary to transfer into self.
 * 
 * There's a separate inventory representing units and goods beyond
 * the reach of the Expeditionary
 *  * Settled colonists and units
 *  * Gathered resources
 *  
 * @author Slash
 *
 */
@SuppressWarnings("serial")
public class Town extends GoodsCache{
	private static final String[] TOWN_ACTIONS = new String[] { 
		"Transfer equipment and people",
		"Construct building on settlement",
		"Inhabit settlement",
		"Pass through the settlement",
		"Do nothing"
	};
	
	private String name;
	protected Expedition founderExpedition;
	protected Date foundedIn;
	
	/**
	 * Represents the buildings constructed on the settlement
	 */
	private List<Building> buildings = new ArrayList<Building>();
	
	/**
	 * This separate inventory represents units that can't be transferred directly to the 
	 * Expedition, but are used for the daily production cycle.
	 */
	private Inventory localInventory;
	
	/**
	 * Represents how much can the founding expedition use the local
	 * resources and fetch the town production
	 * 
	 * Influences lodging available for temporary expedition units
	 * and the quantity of goods placed on the fetchable inventory
	 * over the production phase.
	 */
	private Percentage governance;
	
	public Town(ExpeditionGame game) {
		super(game, "TOWN");
		founderExpedition = game.getExpedition();
		foundedIn = game.getGameTime().getTime();
		governance = founderExpedition.getBaseGovernance();
	}
	
	/**
	 * Determines how many expedition members is the settlement
	 * willing to host, based on the governance
	 * @return
	 */
	public int getLodgingCapacity(){
		return governance.transformInt(getPopulationCapacity());
	}
	
	/**
	 * Represents the total population the settlement may host
	 * @return
	 */
	public int getPopulationCapacity(){
		int ret = 0;
		for (Building building: buildings){
			ret += building.getPopulationCapacity();
		}
		return ret;
	}
	
	/**
	 * Base name of the settlement
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Allows changing the name of the settlement
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	public String getLongDescription(){
		return "The "+getTitle()+" of "+getName(); 
	}
	
	protected void townAction(int switchChat, Expedition expedition) {
		switch (switchChat){
		case 0:
			((ExpeditionUserInterface)UserInterface.getUI()).transferFromExpedition(this);
			break;
		case 1:
			// Build
			BuildBuildings buildAction = new BuildBuildings();
			buildAction.setTown(this);
			expedition.setNextAction(buildAction);
			break;
		case 2: 
			// Inhabit
			if (getPopulation() + expedition.getTotalUnits() + 1 <= getLodgingCapacity()){
				Hibernate hibernate = new Hibernate(7, true);
				expedition.setPosition(getPosition().x(), getPosition().y(), getPosition().z());
				expedition.setNextAction(hibernate);
			} else {
				expedition.getLevel().addMessage(getDescription()+" can't host all of your expedition.");
			}
		case 3:
			expedition.setPosition(getPosition());
			break;
		case 4:
			break;
		}
	}

	public void showBlockingMessage(String message) {
		((ExpeditionUserInterface)UserInterface.getUI()).showBlockingMessage(message);
	}

	protected String[] getTownActions() {
		return TOWN_ACTIONS;
	}

	public int getSize(){
		return (getPopulation() / 1000)+1;
	}

	public int getPopulation() {
		return getTotalUnits();
	}

	public boolean isTown() {
		return getSize() > 5 && getSize() < 20;
	}

	public boolean isCity() {
		return getSize() > 20;
	}
	
	public void addLocalItem (ExpeditionItem item, int quantity){
		localInventory.addItem(item, quantity);
	}
	
	public void addLocalItem (String itemID, int quantity){
		localInventory.addItem(itemID, quantity);
	}
	
	public String getTitle(){
		if (isCity())
			return "city";
		if (isTown())
			return "town";
		return "village";
	}
	
	public void addBuilding(Building building) {
		buildings.add(building);
	}
	
	public Expedition getFounderExpedition() {
		return founderExpedition;
	}

	public Date getFoundedIn() {
		return foundedIn;
	}

	public List<Building> getBuildings() {
		return buildings;
	}

	/* Production */
	/**
	 * Gather resources around the settlement, based on
	 *  * Workforce and Specialists
	 *  * Tiles around, and their resources value
	 *  * Storage capacity
	 */
	public void gatherResources(){
		int internalWorkforce = getTotalLocalUnits();
		int externalWorkforce = getTotalUnits();
		int workforce = internalWorkforce + externalWorkforce;
		List<OverworldExpeditionCell> cellsAround = ((ExpeditionMacroLevel)getLevel()).getMapCellsAround(getPosition());
		for (OverworldExpeditionCell cell: cellsAround){
			for (Pair<String, Integer> resource: cell.getDailyResources()){
				int maxStorage = getStoreable(resource.getA());
				if (maxStorage == 0)
					continue;
				int gatherQuantity = workforce * resource.getB();
				if (gatherQuantity > maxStorage)
					gatherQuantity = maxStorage;
				// Feudal contribution
				int feudal = governance.transformInt(gatherQuantity);
				addItem(resource.getA(), feudal);
				addLocalItem(resource.getA(), gatherQuantity-feudal);
				// TODO: Spend non-renewable items from the world, wood for example
			}
		}
	}
	
	/**
	 * If the item is food (other than fish), check the foraged storage capacity
	 * @param itemId
	 * @return
	 */
	private int getStoreable(String itemId){
		ExpeditionItem itemSample = ItemFactory.createItem(itemId);
		StorageType storageType = itemSample.getStorageType();
		int maxStorage = getMaxStorage(storageType);
		int currentLocalStorage = getCurrentLocalStorage(storageType);
		return maxStorage - currentLocalStorage;

	}
	
	private int getMaxStorage(StorageType storageType) {
		int acum = 0;
		for (Building building: buildings){
			acum += building.getStorageCapacity(storageType);
		}
		return acum;
	}
	
	private int getCurrentLocalStorage(StorageType storageType) {
		int acum = 0;
		for (Equipment e: localInventory.getItems()){
			if (((ExpeditionItem)e.getItem()).getStorageType() == storageType){
				acum += e.getQuantity();
			}
		}
		return acum;
	}
	
	//TODO: This should make part of the transformation cycle (using buildings) Should also require manning
	public void checkCrops() {
		for (Building building: buildings){
			if (building instanceof Farm)
				((Farm)building).checkCrop(ExpeditionGame.getCurrentGame().getGameTime(), this);
		}
	}
	
	
	/* Growth*/
	public void tryGrowing(){
		//This is called each 30 days
		if (Util.chance(95)){
			int growth = (int)Math.round(getPopulation() * ((double)Util.rand(1, 5)/100.0d));
			if (growth > 0){
				if (getPopulation() + growth > getPopulationCapacity()){
					growth = getPopulationCapacity() - getPopulation(); 
				}
				if (growth > 0){
					addLocalItem(ItemFactory.createItem("COLONIST"), growth);
				}
			}
		}
	}
	
	


	/* GoodsCache overrides */
	@Override
	public boolean requiresUnitsToContainItems() {
		return false;
	}

	@Override
	public String getTypeDescription() {
		if (isCity())
			return "City";
		if (isTown())
			return "Town";
		return "Village";
	}
	
	@Override
	public boolean destroyOnEmpty() {
		return false;
	}
	
	@Override
	public void consumeFood() {
		//Do nothing, this must be handled differently
	}
	
	@Override
	/**
	 * Determines how many of an item from the expedition can the town carry
	 */	
	public int getCarryable(ExpeditionItem item) {
		if (item instanceof ExpeditionUnit){
			return getLodgingCapacity() - getTotalUnits();
		} else {
			return super.getCarryable(item);
		}
	}
	
	/**
	 * Determines if the town can carry an item from the expedition
	 */
	public boolean canCarry(ExpeditionItem item, int quantity) {
		if (item instanceof ExpeditionUnit){
			int currentUnits = getTotalUnits();
			if (currentUnits + quantity > getLodgingCapacity()){
				return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean isInfiniteCapacity() {
		return false;
	}
	
	@Override
	public String getDescription() {
		return getName();
	}
	
	@Override
	public String getClassifierID() {
		return "Town"+name;
	}
	
	@Override
	public void onStep(Actor a) {
		if (a != ExpeditionGame.getCurrentGame().getExpedition()){
			return;
		}
		if (!ExpeditionGame.getCurrentGame().getExpedition().getMovementMode().isLandMovement()){
			return;
		}
		((ExpeditionUserInterface)UserInterface.getUI()).showCityInfo(this);
		townAction(UserInterface.getUI().switchChat(getLongDescription(),"What do you want to do", getTownActions()), (Expedition)a);
		((ExpeditionUserInterface)UserInterface.getUI()).afterTownAction();
	}
	
	public int getTotalLocalUnits() {
		int totalUnits = 0;
		for (Equipment equipment: localInventory.getItems()){
			if (equipment.getItem() instanceof ExpeditionUnit){
				totalUnits += equipment.getQuantity();
			}
		}
		return totalUnits;
	}
	
}
