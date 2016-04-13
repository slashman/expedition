package net.slashie.expedition.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.log4j.Logger;
import net.ck.expedition.utils.swing.MessengerService;
import net.slashie.expedition.action.Hibernate;
import net.slashie.expedition.domain.Armor.ArmorType;
import net.slashie.expedition.domain.Weapon.WeaponType;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.game.ExpeditionMusicManager;
import net.slashie.expedition.item.ItemFactory;
import net.slashie.expedition.item.Mount;
import net.slashie.expedition.level.ExpeditionLevelReader;
import net.slashie.expedition.level.GlobeMapModel;
import net.slashie.expedition.town.Building;
import net.slashie.expedition.town.BuildingFactory;
import net.slashie.expedition.town.BuildingTeam;
import net.slashie.expedition.ui.ExpeditionDiscovery;
import net.slashie.expedition.ui.ExpeditionDiscovery.Discovery;
import net.slashie.expedition.ui.ExpeditionUserInterface;
import net.slashie.expedition.world.AnimalNest;
import net.slashie.expedition.world.BotanyCrop;
import net.slashie.expedition.world.CardinalDirection;
import net.slashie.expedition.world.ExpeditionCell;
import net.slashie.expedition.world.ExpeditionLevel;
import net.slashie.expedition.world.ExpeditionMacroLevel;
import net.slashie.expedition.world.ExpeditionMicroLevel;
import net.slashie.expedition.world.FoodConsumer;
import net.slashie.expedition.world.FoodConsumerDelegate;
import net.slashie.expedition.world.OverworldExpeditionCell;
import net.slashie.expedition.world.Plant;
import net.slashie.expedition.world.SettlementLevel;
import net.slashie.expedition.world.TemperatureRules;
import net.slashie.expedition.world.Weather;
import net.slashie.expedition.worldGen.WorldGenerator;
import net.slashie.lang.Percentage;
import net.slashie.serf.action.Actor;
import net.slashie.serf.baseDomain.AbstractItem;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.game.Player;
import net.slashie.serf.level.AbstractCell;
import net.slashie.serf.level.AbstractFeature;
import net.slashie.serf.text.EnglishGrammar;
import net.slashie.serf.ui.ActionCancelException;
import net.slashie.serf.ui.Appearance;
import net.slashie.serf.ui.AppearanceFactory;
import net.slashie.serf.ui.UserInterface;
import net.slashie.util.Pair;
import net.slashie.utils.Circle;
import net.slashie.utils.Position;
import net.slashie.utils.Util;

@SuppressWarnings("serial")
public class Expedition extends Player implements FoodConsumer, UnitContainer, ItemContainer, BuildingTeam
{
	public enum DeathCause
	{
		DEATH_BY_DROWNING, DEATH_BY_SLAYING, DEATH_BY_STARVATION
	}
	public enum MovementMode
	{
		FOOT, HORSE, SHIP;

		public String getDescription()
		{
			switch (this)
			{
			case FOOT:
				return "On Foot";
			case HORSE:
				return "Horseback";
			case SHIP:
				return "Ship";
			}
			return "None";
		}

		public boolean isLandMovement()
		{
			return this == FOOT || this == HORSE;
		}
	}
	public enum MovementSpeed
	{
		FAST("Fast", 10), NONE("No movement", 160), NORMAL("", 20), SLOW("Slow", 40), VERY_FAST("Very Fast!",
				5), VERY_SLOW("Very Slow!", 80);

		private String description;

		private int movementCost;

		private MovementSpeed(String description, int movementCost)
		{
			this.description = description;
			this.movementCost = movementCost;
		}

		public MovementSpeed boost()
		{
			switch (this)
			{
			case NONE:
				return VERY_SLOW;
			case VERY_SLOW:
				return SLOW;
			case SLOW:
				return NORMAL;
			case NORMAL:
				return FAST;
			case FAST:
			case VERY_FAST:
				return VERY_FAST;
			}
			return null;
		}
		public String getDescription()
		{
			return description;
		}

		public int getMovementCost()
		{
			return movementCost;
		}

		public MovementSpeed reduced()
		{
			switch (this)
			{
			case VERY_SLOW:
			case NONE:
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

	}
	public static class Rank implements Serializable
	{
		private String realm;
		private Title title;

		public String getDescription(String name)
		{
			if (title == null)
				return name;
			else
				return title.getDescription() + " " + name;
		}

		public String getFullDescription(String name)
		{
			if (title == null)
				return name;
			else
				return title.getDescription() + " " + name + " of " + realm;
		}

		public int getPrize()
		{
			return title.getPrize();
		}

		public Title getTitle()
		{
			return title;
		}

		public void grantTitle(Title title, String realm)
		{
			this.title = title;
			this.realm = realm;
		}

	}
	public enum Tip
	{
		EXPANDING_SETTLEMENT, LANDING_ON_NEW_WORLD, ON_THE_WAY_TO_NEW_WORLD, TRAPPED_ON_A_STORM, YOU_ARE_ON_IRONS
	}

	public enum Title
	{
		COUNT(5, "Conde", 2, 5, 10, 1500000), DUKE(7, "Duque", 10, 30, 60, 5000000), EXPLORER(1, "Explorador", 0, 0, 0, 0), HIDALGO(2, "Hidalgo", 0, 0, 1, 200000), LORD(3, "Señor", 0, 1, 2,
						500000), MARCHIS(6,
						"Marqués", 5, 10, 30,
						3000000), VICEROY(8, "Virrey", 30, 60, 90, 9500000), VIZCOUNT(4, "Vizconde", 1, 2, 5, 1000000);
		private String description;
		private int prize;
		private int rank;
		private int requiredCities, requiredTowns, requiredVillages;

		private Title(int rank, String description, int requiredCities, int requiredTowns, int requiredVillages,
				int prize)
		{
			this.rank = rank;
			this.description = description;
			this.requiredTowns = requiredTowns;
			this.requiredCities = requiredCities;
			this.requiredVillages = requiredVillages;
			this.prize = prize;
		}

		public boolean attainsRank(Expedition e)
		{
			if (e.getTitle().getTitle().getRank() > getRank())
				return false;
			int cities = 0;
			int towns = 0;
			int villages = 0;
			for (Town town : e.getTowns())
			{
				if (town.isCity())
				{
					cities++;
				}
				else if (town.isTown())
				{
					towns++;
				}
				else
				{
					villages++;
				}
			}
			return cities >= requiredCities && towns >= requiredTowns && villages >= requiredVillages;
		}

		public String getDescription()
		{
			return description;
		}

		public int getPrize()
		{
			return prize;
		}

		public int getRank()
		{
			return rank;
		}

		public String pickRealm(Expedition exp)
		{
			if (this == VICEROY)
				return "the New World";
			for (Town town : exp.getTowns())
			{
				if (requiredCities > 0)
				{
					if (town.isCity())
						return town.getName();
					else
						continue;
				}
				else if (requiredTowns > 0)
				{
					if (town.isTown())
						return town.getName();
					else
						continue;
				}
				else
				{
					return town.getName();
				}
			}
			return "";
		}
	}

	private static final String[] MORALE_DESCRIPTIONS = new String[]
	{ "Depressed", "Cracking Apart", "Hopeless", "Downcast", "Restless", "Calm", "Steadfast", "Content", "Happy",
			"Joyous", "Victorious" };

	/**
	 * Represent the gold credit of the player in Spain
	 */
	private int accountedGold;

	/**
	 * Determines if the expedition is anchored.
	 */
	private boolean anchored;

	private List<Vehicle> currentVehicles = new ArrayList<Vehicle>();

	private int daysOnSea;

	private DeathCause deathCause;

	private double deducedReckonWest;

	private List<ExpeditionDiscovery> discoveryLog;

	private String expeditionary;

	private Rank expeditionaryTitle = new Rank();

	private int expeditionMorale;

	private int fame;

	private FoodConsumerDelegate foodConsumerDelegate;

	private CardinalDirection heading = CardinalDirection.WEST;

	private boolean hibernate;

	private boolean isOnOpenSea = false;

	private ExpeditionUnit leaderUnit;

	final Logger logger = Logger.getRootLogger();

	private MovementMode movementMode = MovementMode.FOOT;

	private int perceivedLuck;

	List<AbstractFeature> reusableFeatureList = new ArrayList<AbstractFeature>();

	private List<Town> towns = new ArrayList<Town>();

	private int winBalance = 0;

	private int thirstResistance = 0;
	private int hungerResistance = 0;
	
	private int maxThirstResistance;
	private int maxHungerResistance;
	
	public Expedition(ExpeditionGame game)
	{
		HANDLE_FEATURES = false;
		setGame(game);
		setExpeditionMorale(5);
		setThirstResistance(5);
		setMaxThirstResistance(5);
		setHungerResistance(7);
		setMaxHungerResistance(7);		
		foodConsumerDelegate = new FoodConsumerDelegate(this);
		game.addFoodConsumer(this);

		discoveryLog = new ArrayList<ExpeditionDiscovery>();
		// discoveryLog.add(new ExpeditionDiscovery("You discovered an ancient
		// ruin of Tairona culture", Discovery.Ruin, "August 23", 23));
		setFame(0);
	}

	public String toString()
	{
		return getExpeditionMorale() + " " + getThirstResistance() + " " + getHungerResistance();
		
	}
	
	
	public int getExpeditionMorale()
	{
		return expeditionMorale;
	}

	public void setExpeditionMorale(int expeditionMorale)
	{
		this.expeditionMorale = expeditionMorale;
	}

	public void addAccountedGold(int valuables)
	{
		accountedGold += valuables;
	}

	public void addAllItems(List<Equipment> items)
	{
		// Order, put livestock last to ensure they can be added to the
		// expedition
		Collections.sort(items, new Comparator<Equipment>()
		{
			@Override
			public int compare(Equipment o1, Equipment o2)
			{
				boolean firstLiveStock = ((ExpeditionItem) o1.getItem()).getGoodType() == GoodType.LIVESTOCK;
				boolean secondLiveStock = ((ExpeditionItem) o2.getItem()).getGoodType() == GoodType.LIVESTOCK;
				if (firstLiveStock && secondLiveStock)
				{
					return 0;
				}
				else if (!firstLiveStock && !secondLiveStock)
				{
					return 0;
				}
				else
				{
					if (firstLiveStock)
					{
						return 1;
					}
					else
					{
						return -1;
					}
				}
			}
		});
		for (Equipment equipment : items)
		{
			if (canCarry(equipment.getItem(), equipment.getQuantity()))
			{
				addItem(equipment.getItem(), equipment.getQuantity());
			}
			else
			{
				GoodsCache cache = ((ExpeditionMacroLevel) getLevel()).getOrCreateCache(getPosition());
				cache.addItem((ExpeditionItem) equipment.getItem(), equipment.getQuantity());
			}

		}
	}

	/**
	 * Adds all these items without validating if the expedition can carry them.
	 * 
	 * The expedition may become stranded afterwards.
	 * 
	 * @param items
	 */
	public void addAllItemsForced(List<Equipment> items)
	{
		for (Equipment equipment : items)
		{
			addItemForced(equipment.getItem(), equipment.getQuantity());
		}
	}

	public void addDiscoveryLog(ExpeditionDiscovery discoveryLog)
	{
		this.discoveryLog.add(discoveryLog);
	}

	@Override
	public void addItem(ExpeditionItem item, int quantity)
	{
		super.addItem(item, quantity);
		validateMounted();
	}

	/**
	 * Add the item without validating if the expedition can carry it.
	 * 
	 * The expedition may become stranded afterwards
	 * 
	 * @param toAdd
	 * @param quantity
	 */
	public void addItemForced(AbstractItem toAdd, int quantity)
	{
		String toAddID = toAdd.getFullID();
		Equipment equipmentx = inventory.get(toAddID);
		if (equipmentx == null)
			inventory.put(toAddID, new Equipment(toAdd, quantity));
		else
			equipmentx.increaseQuantity(quantity);
	}

	public void addItemOffshore(ExpeditionItem what, int quantity)
	{
		MovementMode currentMovementMode = getMovementMode();
		setMovementMode(MovementMode.SHIP);
		if (what instanceof Vehicle && !((Vehicle) what).isFakeVehicle())
		{
			for (int i = 0; i < quantity; i++)
			{
				currentVehicles.add((Vehicle) ((ExpeditionItem) what).clone());
			}
		}
		else
		{
			addItem(what, quantity);
		}

		setMovementMode(currentMovementMode);
	}

	public void addTown(Town town)
	{
		towns.add(town);
	}

	@Override
	public void addUnits(ExpeditionUnit unit, int quantity)
	{
		addItem(unit, quantity);
	}

	@Override
	public void afterActing()
	{
		super.afterActing();
		// Randomly sight land
		if (getMovementMode() == MovementMode.SHIP)
		{
			boolean sawLand = false;
			if (daysOnSea > 15 && onOpenSea())
			{
				int sightRangeInMiles = getSightRangeInCells();
				int additionalScale = GlobeMapModel.getSingleton().getLatitudeHeight();
				sawLand = sightLand(sightRangeInMiles + 1 * additionalScale)
						|| sightLand(sightRangeInMiles + 2 * additionalScale);
				if (!sawLand && Util.chance(50) && sightRangeInMiles > 4 * additionalScale)
				{
					sawLand = nearLandSignals(18) || nearLandSignals(19) || nearLandSignals(20);
				}
				// Wrong Land sight
				if (!sawLand && getMorale() < 4 && Util.chance(5))
				{
					CardinalDirection d = CardinalDirection.getRandomDirection();
					if (d == CardinalDirection.NULL)
						d = CardinalDirection.WEST;
					switch (Util.rand(0, 1))
					{
					case 0:
						message("There seems to be land to the " + d.getDescription());
						break;
					case 1:
						message("You see a cloud block to the " + d.getDescription());
						break;
					}
				}
			}

		}
		else
		{
			if (Util.chance(100))
			{
				// Try to deploy an animal expedition only if the chance
				List<Pair<Position, AnimalNest>> nests = WorldGenerator.animalNests;
				Pair<Position, AnimalNest> nearestNest = null;
				double minDistance = -1;

				for (Pair<Position, AnimalNest> n : nests)
				{
					double distance = Position.distance(getPosition(), n.getA());
					double nestRadius = GlobeMapModel.getSingleton().getLongitudeScale(n.getA().y())
							* n.getB().getRadius();
					if (distance <= nestRadius)
					{
						if (minDistance == -1)
						{
							minDistance = distance;
							nearestNest = n;
						}
						else if (distance < minDistance)
						{
							minDistance = distance;
							nearestNest = n;
						}
					}
				}

				if (nearestNest != null)
				{
					int r = nearestNest.getB().getRadius();

					int longScale = GlobeMapModel.getSingleton().getLongitudeScale(getLatitude());
					int latScale = GlobeMapModel.getSingleton().getLatitudeHeight();

					int xx = Util.rand(getPosition().x() - r * longScale, getPosition().x() + r * longScale);
					int yy = Util.rand(getPosition().y() - r * latScale, getPosition().y() + r * latScale);

					Position p = new Position(xx, yy);

					double sight = getSightRangeInDots();
					boolean isFar = (Position.distance(p, getPosition()) > sight);

					OverworldExpeditionCell cell = (OverworldExpeditionCell) getLevel().getMapCell(p);
					if (isFar && cell != null && !cell.isRiver() && cell.isLand()
							&& getLevel().getFeaturesAt(p) == null)
					{
						Expedition expedition = nearestNest.getB().deployAnimalGroup();
						expedition.setPosition(p);
						// expedition.setPosition(new
						// Position(getPosition().x()+2*GlobeMapModel.getSingleton().getLongitudeScale(getLatitude()),
						// getPosition().y()));
						getLevel().addActor(expedition);
						getLevel().addMessage("A group of " + nearestNest.getB().getName() + " appears!");
					}
				}
			}

			// According to the naturalist number then pickup exotic plants
			int naturalist = getNaturalistNumber() * 10;
			if (Util.chance(naturalist))
			{
				List<Pair<Position, BotanyCrop>> crops = WorldGenerator.botanyCrops;
				Pair<Position, BotanyCrop> nearest = null;
				double minDistance = -1;

				for (Pair<Position, BotanyCrop> b : crops)
				{
					double distance = Position.distance(getPosition(), b.getA());
					double nestRadius = GlobeMapModel.getSingleton().getLongitudeScale(b.getA().y())
							* b.getB().getRadius();
					if (distance <= nestRadius)
					{
						if (minDistance == -1)
						{
							minDistance = distance;
							nearest = b;
						}
						else if (distance < minDistance)
						{
							minDistance = distance;
							nearest = b;
						}
					}
				}

				if (nearest != null)
				{
					List<Plant> plants = nearest.getB().getPlants();
					int ind = Util.rand(0, plants.size() - 1);

					Plant plant = plants.get(ind);

					String discoveryText = "You discovered a " + plant.getName();
					if (!hasDiscovered(Discovery.Plant, discoveryText))
					{
						((ExpeditionUserInterface) UserInterface.getUI())
								.showImageBlockingMessage(plant.getDescription(), plant.getName());
						getLevel().addMessage(discoveryText);
					}

					Calendar gameTime = ExpeditionGame.getCurrentGame().getGameTime();
					String time = ExpeditionUserInterface.months[gameTime.get(Calendar.MONTH)] + " "
							+ gameTime.get(Calendar.DATE) + ", "
							+ MessengerService.getTimeDescriptionFromHour(gameTime.get(Calendar.HOUR_OF_DAY));
					addDiscoveryLog(new ExpeditionDiscovery(discoveryText, Discovery.Plant, time, 5));
				}
			}
		}

	}

	/*
	 * public List<Equipment> getShips(){ List<Equipment> ret = new
	 * ArrayList<Equipment>(); List<Equipment> inventory = getInventory(); for
	 * (Equipment equipment: inventory){ if (equipment.getItem() instanceof
	 * Vehicle){ if (((Vehicle)equipment.getItem()).isMoveOnWater())
	 * ret.add(equipment); } } return ret; }
	 */

	public void arm()
	{
		List<Equipment> units = getUnarmedUnits();
		Collections.sort(units, new Comparator<Equipment>()
		{
			public int compare(Equipment arg0, Equipment arg1)
			{
				return ((ExpeditionUnit) arg1.getItem()).getAttack().getMax()
						- ((ExpeditionUnit) arg0.getItem()).getAttack().getMax();
			}
		});
		for (Equipment unit : units)
		{
			WeaponType[] preferredWeapons = ((ExpeditionUnit) unit.getItem()).getWeaponTypes();
			for (WeaponType preferredType : preferredWeapons)
			{
				List<Weapon> Weapon = ItemFactory.getItemsByWeaponType(preferredType);
				for (Weapon weaponId : Weapon)
				{
					int available = getItemCount(weaponId.getFullID());
					if (available == 0)
						continue;
					int unitsToArm = available > unit.getQuantity() ? unit.getQuantity() : available;
					reduceGood(weaponId.getFullID(), unitsToArm);
					// Split equipment in armed and disarmed
					if (unitsToArm > 0)
					{
						reduceQuantityOf(unit.getItem(), unitsToArm);
						// ExpeditionUnit newUnit =
						// (ExpeditionUnit)ItemFactory.createItem(unit.getItem().getFullID());
						ExpeditionUnit newUnit = (ExpeditionUnit) ((ExpeditionUnit) unit.getItem()).clone();
						newUnit.setArm((Weapon) ItemFactory.createItem(weaponId.getFullID()));
						addItem(newUnit, unitsToArm);
					}
				}
			}
		}

		units = getUnarmoredUnits();
		Collections.sort(units, new Comparator<Equipment>()
		{
			public int compare(Equipment arg0, Equipment arg1)
			{
				return ((ExpeditionUnit) arg1.getItem()).getDefense().getMax()
						- ((ExpeditionUnit) arg0.getItem()).getDefense().getMax();
			}
		});
		for (Equipment unit : units)
		{
			ArmorType[] preferredArmors = ((ExpeditionUnit) unit.getItem()).getArmorTypes();
			for (ArmorType armorType : preferredArmors)
			{
				List<Armor> armors = ItemFactory.getItemsByArmorType(armorType);
				for (Armor armorId : armors)
				{
					int available = getItemCount(armorId.getFullID());
					int unitsToArm = available > unit.getQuantity() ? unit.getQuantity() : available;
					reduceGood(armorId.getFullID(), unitsToArm);
					// Split equipment in armored and unarmored
					if (unitsToArm > 0)
					{
						reduceQuantityOf(unit.getItem(), unitsToArm);
						// ExpeditionUnit newUnit =
						// (ExpeditionUnit)ItemFactory.createItem(unit.getItem().getFullID());
						ExpeditionUnit newUnit = (ExpeditionUnit) ((ExpeditionUnit) unit.getItem()).clone();
						newUnit.setArmor((Armor) ItemFactory.createItem(armorId.getFullID()));
						addItem(newUnit, unitsToArm);
					}
				}
			}
		}
		setArmed(true);
	}

	@Override
	public void beforeActing()
	{
		super.beforeActing();
		isOnOpenSea = true;
	}

	@Override
	public void beforeItemsAddition(AbstractItem item, int quantity)
	{

	}

	private void boostMorale(int i)
	{
		setCounter("MORALE_UP", i);
	}

	@Override
	public boolean canCarry(AbstractItem item, int quantity)
	{
		if (!(item instanceof ExpeditionItem))
			return false;
		if (getMovementMode().isLandMovement() && item instanceof Vehicle)
			return true;
		if (item instanceof Mount)
		{
			if (getMovementMode().isLandMovement() && getTotalUnits() > 0)
				return true;
		}
		ExpeditionItem expItem = (ExpeditionItem) item;
		return getCurrentWeight() + (expItem.getWeight() * quantity) <= getCarryCapacity();
	}

	@Override
	public boolean canCarry(ExpeditionItem item, int quantity)
	{
		return canCarry((AbstractItem) item, quantity);
	}

	public boolean canCarryOffshore(ExpeditionItem what, int quantity)
	{
		MovementMode currentMovementMode = getMovementMode();
		setMovementMode(MovementMode.SHIP);
		boolean ret = canCarry(what, quantity);
		setMovementMode(currentMovementMode);
		return ret;
	}

	/**
	 * Determines if the expedition can disembark. The expedition must be
	 * adjacent to land in order to disembark.
	 * 
	 * @return
	 */
	public boolean canDisembark()
	{
		return getLandCellAround() != null;
	}

	public void cashValuables()
	{
		int valuables = getSumOfValuables();
		List<Equipment> inventory = getInventory();
		for (int i = 0; i < inventory.size(); i++)
		{
			Equipment equipment = inventory.get(i);
			if (((ExpeditionItem) equipment.getItem()).getGoodType() == GoodType.TRADE_GOODS)
			{
				reduceQuantityOf(equipment.getItem(), equipment.getQuantity());
			}
		}
		addAccountedGold(valuables);

	}

	public void checkDeath()
	{
		if (getTotalUnits() <= 0)
		{
			if (getLevel() instanceof ExpeditionMicroLevel)
				return;
			if (UserInterface.getUI() != null)
			{
			UserInterface.getUI().refresh();
			}
			if (deathCause == null)
				deathCause = DeathCause.DEATH_BY_SLAYING;
			if (UserInterface.getUI() != null)
			{
			UserInterface.getUI().onPlayerDeath();
			}
			informPlayerEvent(DEATH);
		}
	}

	public void checkDrown()
	{
		if (getTotalShips() <= 0)
		{
			OverworldExpeditionCell cell = (OverworldExpeditionCell) getLevel().getMapCell(getPosition());
			if (cell.isRiver())
			{
				setMovementMode(MovementMode.FOOT);
			}
			else
			{
				UserInterface.getUI().refresh();
				deathCause = DeathCause.DEATH_BY_DROWNING;
				informPlayerEvent(DEATH);
			}
		}
	}

	public void consumeFood()
	{
		foodConsumerDelegate.consumeFood();
	}

	/*
	@Override
	public void consumeWater()
	{
		// Consume water only if is not near to a sweet water spot
		List<Pair<Position, OverworldExpeditionCell>> around = ((ExpeditionMacroLevel) getLevel())
				.getMapCellsAndPositionsAround(getPosition());
		for (Pair<Position, OverworldExpeditionCell> a : around)
		{
			if (a.getB().isRiver())
			{
				foodConsumerDelegate.removeThirst();
				return;
			}
		}

		if (foodConsumerDelegate.consumeWater())
		{
			if (Util.chance(40))
			{
				getLevel().addMessage("Your crowd is thirsty!");
				if (foodConsumerDelegate.getThirstResistance() >= 5)
					expeditionMorale--;
			}
		}
	}
*/
	public void consumeWater()
	{
		foodConsumerDelegate.consumeWater();	
		//getLevel().addMessage("test");
	}
	
	@Override
	public void counterFinished(String counterId)
	{
		// TODO Auto-generated method stub

	}

	public void dayShift()
	{
		wearOutShips(5, false);

		if (getMovementMode() == MovementMode.SHIP)
		{
			increaseDaysAtSea();
			if (getLocation().getWindDirection() == CardinalDirection.NULL)
			{
				modifyPerceivedLuck(-1);
			}
			if (getLocation().hasStorm(getPosition()))
			{
				modifyPerceivedLuck(-1);
			}
		}
		else
		{
			if (getDaysOnSea() > 0)
			{
				increaseDaysAtSea();
			}
		}
		if (Util.chance(50))
			modifyPerceivedLuck(1);

		if (!hasFullShipCrew())
		{
			if (Util.chance(50))
				getLevel().addMessage("We are struggling to sail our ships");
		}
		// Heal units
		heal();

	}

	private void decreaseMorale(int i)
	{
		setCounter("MORALE_DOWN", i);
	}

	public void decreaseWinBalance()
	{
		winBalance--;
		if (winBalance == -10)
			winBalance = -10;
	}

	@Override
	public void doNothing()
	{
		try
		{
			landOn(getPosition());
		}
		catch (ActionCancelException e)
		{

		}
		super.doNothing();
	}

	@Override
	public boolean extendedInfoAvailable()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean forageFood()
	{

		if (!(getLocation() instanceof ExpeditionMacroLevel))
		{
			return false;
		}
		ExpeditionMacroLevel level = (ExpeditionMacroLevel) getLocation();

		OverworldExpeditionCell cell = (OverworldExpeditionCell) getLevel().getMapCell(getPosition());

		if (Util.chance(cell.getForageChance()))
		{
			int quantity = cell.getForageQuantity();
			String food = "";
			if (cell.isRiver())
			{
				if (isForaging())
				{
					food = "FISH";
					int multiplier = (int) Math.ceil(getTotalUnits() / 20.0d);

					quantity *= multiplier;
					if (quantity > 0)
					{
						MessengerService.showImportantMessage(getEventDatePreffix() + "We have caught " + quantity + " fish!!");
						modifyPerceivedLuck(1);
					}
				}
				else
				{
					return false;
				}
			}
			else if (cell.isSea())
			{
				food = "FISH";
				if (!level.getWeather().isStormy())
				{
					if (level.getWeather().equals(Weather.CLEAR))
					{
						food = "FISH";
						int multiplier = (int) Math.ceil(getItemCount("SAILOR") / 30.0d);
						quantity *= multiplier;
						if (quantity > 0)
						{
							MessengerService.showImportantMessage(getEventDatePreffix() + level.getWeather().toString() + ": We have caught " + quantity + " fish!!");
							modifyPerceivedLuck(1);
						}
					}

					else if (level.getWeather().equals(Weather.CLOUDY))
					{
						food = "FISH";
						int multiplier = (int) Math.ceil(getItemCount("SAILOR") / 23.0d);
						quantity *= multiplier;
						if (quantity > 0)
						{
							MessengerService.showImportantMessage(getEventDatePreffix() + level.getWeather().toString() + ": We have caught " + quantity + " fish!!");
							modifyPerceivedLuck(1);
						}
					}

					else if (level.getWeather().equals(Weather.RAIN))
					{
						food = "FISH";
						int multiplier = (int) Math.ceil(getItemCount("SAILOR") / 15.0d);
						quantity *= multiplier;
						if (quantity > 0)
						{
							MessengerService.showImportantMessage(getEventDatePreffix() + level.getWeather().toString() + ": We have caught " + quantity + " fish!!");
							modifyPerceivedLuck(1);
						}
					}
					else
					{
						food = "FISH";
						int multiplier = (int) Math.ceil(getItemCount("SAILOR") / 12.0d);
						quantity *= multiplier;
						if (quantity > 0)
						{
							MessengerService.showImportantMessage(getEventDatePreffix() + level.getWeather().toString() + ": We have caught " + quantity + " fish!!");
							modifyPerceivedLuck(1);
						}
					}

				}
				else
				{
					MessengerService.showImportantMessage(getEventDatePreffix() + "Stormy Weather: No fish!!");
					return false;
				}
			}
			else
			{
				if (isForaging())
				{
					food = "FRUIT";
					int multiplier = (int) Math.ceil(getTotalUnits() / 10.0d);
					quantity *= multiplier;
					if (quantity > 0)
					{
						MessengerService.showImportantMessage(getEventDatePreffix() + "We have foraged " + quantity + " fruit!!!");
						modifyPerceivedLuck(1);
					}
				}
				else
				{
					return false;
				}

			}
			try
			{
				ExpeditionItem foodSample = ItemFactory.createItem(food);
				addItem(foodSample, quantity);
			}
			catch (Exception e)
			{
				logger.debug("Exception caught:");
				logger.debug(level.getWeather().toString());
				e.printStackTrace();
			}
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean forageFood(int i)
	{

		if (!(getLocation() instanceof ExpeditionMacroLevel))
		{
			return false;
		}
		ExpeditionMacroLevel level = (ExpeditionMacroLevel) getLocation();

		OverworldExpeditionCell cell = (OverworldExpeditionCell) getLevel().getMapCell(getPosition());

		if (Util.chance(i))
		{
			int quantity = cell.getForageQuantity();
			String food = "empty";
			if (cell.isRiver())
			{
				if (isForaging())
				{
					food = "FISH";
					int multiplier = (int) Math.ceil(getTotalUnits() / 20.0d);

					quantity *= multiplier;
					if (quantity > 0)
					{

						MessengerService.showImportantMessage(getEventDatePreffix() + "We have caught " + quantity + " fish!!");						
						modifyPerceivedLuck(1);
					}
				}
				else
				{
					return false;
				}
			}
			else if (cell.isSea())
			{
				if (!level.getWeather().isStormy())
				{
					if (level.getWeather().equals(Weather.CLEAR))
					{
						food = "FISH";
						int multiplier = (int) Math.ceil(getItemCount("SAILOR") / 30.0d);
						quantity *= multiplier;
						if (quantity > 0)
						{
							MessengerService.showImportantMessage(getEventDatePreffix() + "Clear Weather: We have caught " + quantity + " fish!!");
							modifyPerceivedLuck(1);
						}
					}

					else if (level.getWeather().equals(Weather.CLOUDY))
					{
						food = "FISH";
						int multiplier = (int) Math.ceil(getItemCount("SAILOR") / 23.0d);
						quantity *= multiplier;
						if (quantity > 0)
						{
							MessengerService.showImportantMessage(getEventDatePreffix() + "Cloudy Weather: We have caught " + quantity + " fish!!");
							modifyPerceivedLuck(1);
						}
					}

					else if (level.getWeather().equals(Weather.RAIN))
					{
						food = "FISH";
						int multiplier = (int) Math.ceil(getItemCount("SAILOR") / 15.0d);
						quantity *= multiplier;
						if (quantity > 0)
						{
							MessengerService.showImportantMessage(getEventDatePreffix() + "Rain Weather: We have caught " + quantity + " fish!!");
							modifyPerceivedLuck(1);
						}
					}
					else
					{
						food = "FISH";
						int multiplier = (int) Math.ceil(getItemCount("SAILOR") / 12.0d);
						quantity *= multiplier;
						if (quantity > 0)
						{
							
							MessengerService.showImportantMessage(getEventDatePreffix() + level.getWeather().toString() + ": We have caught " + quantity + " fish!!");
								modifyPerceivedLuck(1);
						}
					}
				}
				else
				{
					MessengerService.showImportantMessage(getEventDatePreffix() + level.getWeather().toString() + ": We have cought fish: " + quantity);
					return false;
				}
			}
			else
			{
				if (isForaging())
				{
					food = "FRUIT";
					int multiplier = (int) Math.ceil(getTotalUnits() / 10.0d);
					quantity *= multiplier;
					if (quantity > 0)
					{
						MessengerService.showImportantMessage(getEventDatePreffix() + "We have foraged " + quantity + " fruit!!!");
						modifyPerceivedLuck(1);
					}
				}
				else
				{
					return false;
				}

			}
			try
			{
				ExpeditionItem foodSample = ItemFactory.createItem(food);
				addItem(foodSample, quantity);
			}
			catch (Exception e)
			{
				logger.debug("Exception caught: " + level.getWeather().toString());
				e.printStackTrace();
			}
			return true;
		}
		else
		{
			return false;
		}
	}

	public int getAccountedGold()
	{
		return accountedGold;
	}

	public Appearance getAppearance()
	{
		if (getLocation().isZoomIn())
		{
			return getLeaderUnit().getAppearance();
		}
		else
		{
			switch (getMovementMode())
			{
			case FOOT:
				if (getCurrentCell() instanceof OverworldExpeditionCell
						&& ((OverworldExpeditionCell) getCurrentCell()).isRiver())
				{
					return AppearanceFactory.getAppearanceFactory()
							.getAppearance("BOAT_" + super.getAppearance().getID());
				}
				else
				{
					return super.getAppearance();
				}
			case SHIP:
				return AppearanceFactory.getAppearanceFactory()
						.getAppearance("SHIP_EXPEDITION_" + getHeading().getAbbreviation());
			case HORSE:
				return AppearanceFactory.getAppearanceFactory().getAppearance("HORSE_EXPEDITION");
			}
		}
		return null;
	}

	/**
	 * Represents how much governance the Expeditionary has over newly-founded
	 * colonies.
	 * 
	 * It's associated with the morale
	 * 
	 * @return
	 */
	public Percentage getBaseGovernance()
	{
		return new Percentage(getMorale() * 10);
	}

	/**
	 * Determines how much building power has this expedition
	 * 
	 * @return
	 */
	public int getBuildingCapacity()
	{
		int power = 0;
		List<Equipment> inventory = getInventory();
		for (Equipment equipment : inventory)
		{
			if (equipment.getItem() instanceof ExpeditionUnit)
			{
				int multiplier = ((ExpeditionUnit) equipment.getItem()).getBaseID().equals("CARPENTER") ? 2 : 1;
				power += equipment.getQuantity() * multiplier;
			}
		}
		return power;
	}

	public int getCarryable(ExpeditionItem item)
	{
		if (getMovementMode() != MovementMode.SHIP && item instanceof ExpeditionUnit) // All
																						// units
																						// welcome
																						// into
																						// a
																						// land
																						// expedition
			return -1;
		if (getMovementMode() != MovementMode.SHIP && item.getFullID().equals("HORSE"))
		{
			// All horses are welcome, as long as there's a man to ride them
			if (getTotalUnits() > 0)
				return -1;
			else
				return 0;
		}
		return (int) Math.floor((getCarryCapacity() - getCurrentWeight()) / item.getWeight());
	}

	public int getCarryCapacity()
	{
		if (getMovementMode() == MovementMode.SHIP)
		{
			int carryCapacity = 0;
			List<Vehicle> inventory = getCurrentVehicles();
			for (Vehicle vehicle : inventory)
			{
				carryCapacity += vehicle.getCarryCapacity();
			}
			return carryCapacity;
		}
		else
		{
			// On foot, carry capacity is determined by the expedition itself,
			// and its horses
			int carryCapacity = 0;
			List<Equipment> inventory = getInventory();
			for (Equipment equipment : inventory)
			{
				if (equipment.getItem() instanceof Vehicle)
				{
					carryCapacity += ((Vehicle) equipment.getItem()).getCarryCapacity() * equipment.getQuantity();
				}
				if (equipment.getItem() instanceof Mount)
				{
					carryCapacity += ((Mount) equipment.getItem()).getCarryCapacity() * equipment.getQuantity();
				}

			}
			return carryCapacity;
		}
	}

	@Override
	public String getClassifierID()
	{
		return "MAIN_EXPEDITION";
	}

	private AbstractCell getCurrentCell()
	{
		return getLocation().getMapCell(getPosition());
	}

	public int getCurrentFood()
	{
		int currentFood = 0;
		List<Equipment> inventory = getInventory();
		for (Equipment equipment : inventory)
		{
			if (equipment.getItem() instanceof Food)
			{
				Food good = (Food) equipment.getItem();
				currentFood += good.getUnitsFedPerGood() * equipment.getQuantity();
			}
		}
		return currentFood;
	}

	public int getCurrentlyCarrying()
	{
		if (getCarryCapacity() == 0)
			return 0;
		return (int) Math.round(((double) getCurrentWeight() / (double) getCarryCapacity()) * 100.0d);
	}

	public List<Vehicle> getCurrentVehicles()
	{
		return currentVehicles;
	}

	public int getCurrentWater()
	{
		int currentWater = 0;
		List<Equipment> inventory = getInventory();
		for (Equipment equipment : inventory)
		{
			/*if (equipment.getItem().getFullID().equals("FRESHWATER"))
			{
				Water water = (Water) equipment.getItem();
				currentWater += water.getUnitsFedPerGood() * equipment.getQuantity();
			}*/
			if (equipment.getItem() instanceof Water)
			{
				Water good = (Water) equipment.getItem();
				currentWater += good.getUnitsFedPerGood() * equipment.getQuantity();
			}
		}
		return currentWater;
	}

	private int getCurrentWeight()
	{
		if (getMovementMode() == MovementMode.SHIP)
		{
			// All expedition aboard vehicles. Vehicles must carry the weight of
			// all the units along with the goods
			int currentlyCarrying = 0;
			List<Equipment> inventory = getInventory();
			for (Equipment equipment : inventory)
			{
				currentlyCarrying += ((ExpeditionItem) equipment.getItem()).getWeight() * equipment.getQuantity();
			}
			return currentlyCarrying;
		}
		else
		{
			// On foot, vehicles (including units) and mounts take care of
			// carrying themselves around
			int currentlyCarrying = 0;
			List<Equipment> inventory = getInventory();
			for (Equipment equipment : inventory)
			{
				if (!(equipment.getItem() instanceof Vehicle || equipment.getItem() instanceof Mount))
				{
					currentlyCarrying += ((ExpeditionItem) equipment.getItem()).getWeight() * equipment.getQuantity();
				}
				// If the equipment is an unit, and it has equipment, they must
				// carry the weight of their equipment!
				if (equipment.getItem() instanceof ExpeditionUnit)
				{
					ExpeditionUnit unit = (ExpeditionUnit) equipment.getItem();
					if (unit.getWeapon() != null)
					{
						currentlyCarrying += unit.getWeapon().getWeight() * equipment.getQuantity();
					}
				}
			}
			return currentlyCarrying;
		}
	}

	/**
	 * returns daily Food consumption, takes hibernate into account
	 */
	@Override
	public int getDailyFoodConsumption()
	{
		if (isHibernate())
		{
			return 0;
		}
		else
		{
			return foodConsumerDelegate.getDailyFoodConsumption();
		}
	}

	
	/**
	 * returns daily water consumption, takes hibernate into account
	 */
	@Override
	public int getDailyWaterConsumption()
	{
		if (isHibernate())
		{
			return 0;
		}
		else
		{
			return foodConsumerDelegate.getDailyWaterConsumption();
		}
	}

	@Override
	public int getDarkSightRange()
	{
		return getSightRangeInCells();
	}

	public int getDaysOnSea()
	{
		return daysOnSea;
	}

	public DeathCause getDeathCause()
	{
		return deathCause;
	}

	public int getDeducedReckonWest()
	{
		return (int) Math.round(deducedReckonWest);
	}

	@Override
	public String getDescription()
	{
		if (movementMode == MovementMode.SHIP)
		{
			return "Ships";
		}
		else
		{
			return "Expedition";
		}
	}

	public Appearance getDialogAppearance()
	{
		Appearance mapAppearance = getAppearance();
		String mapAppearanceId = mapAppearance.getID();
		return AppearanceFactory.getAppearanceFactory().getAppearance("DIALOG_" + mapAppearanceId);
	}

	public List<ExpeditionDiscovery> getDiscoveryLog()
	{
		return discoveryLog;
	}

	/**
	 * Looks of a town around the expedition
	 * 
	 * @return
	 */
	public Town getDockingTown()
	{
		if (!(getLevel() instanceof ExpeditionLevelReader))
			return null;
		List<AbstractFeature> features = ((ExpeditionLevelReader) getLevel()).getFeaturesAround(this, getPosition().x,
				getPosition().y, getPosition().z, 1, 1);
		for (AbstractFeature feature : features)
		{
			if (feature instanceof Town && !(feature instanceof NativeTown))
				return (Town) feature;
		}
		return null;
	}

	@Override
	public List<AbstractItem> getEquippedItems()
	{
		// TODO Auto-generated method stub
		return null;
	}

	private String getEventDatePreffix()
	{
		Calendar gameTime = ((ExpeditionGame) getGame()).getGameTime();
		return gameTime.get(Calendar.YEAR) + ", " + ExpeditionUserInterface.months[gameTime.get(Calendar.MONTH)] + " "
				+ gameTime.get(Calendar.DATE) + " XXX ";
	}

	public String getExpeditionary()
	{
		return expeditionary;
	}

	public String getExpeditionaryTitle()
	{
		if (expeditionaryTitle != null)
			return expeditionaryTitle.getDescription(getExpeditionary());
		else
			return getExpeditionary();
	}

	public int getFame()
	{
		return fame;
	}

	public double getFoodConsumptionMultiplier()
	{
		/*
		 * switch (getMovementSpeed()){ case FAST: return 1; case NORMAL: return
		 * 2; case SLOW: return 3; }
		 */
		if (getLevel() instanceof ExpeditionMacroLevel)
			return TemperatureRules.getTemperatureFoodModifier(getLocation().getTemperature());
		else
			return 1;

	}



	@Override
	public Position getFreeSquareAround(Position p)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public List<Equipment> getGoods(GoodType goodType)
	{

		if (goodType == GoodType.VEHICLE)
		{
			List<Vehicle> vehicles = getCurrentVehicles();
			List<Equipment> vehiclesInventory = new ArrayList<Equipment>();
			for (Vehicle vehicle : vehicles)
			{
				vehiclesInventory.add(new Equipment(vehicle, 1));
			}
			return vehiclesInventory;
		}
		else
			return getGoods(goodType, false);

	}

	public List<Equipment> getGoods(GoodType goodType, boolean clone)
	{
		List<Equipment> ret = new ArrayList<Equipment>();
		List<Equipment> inventory = getInventory();
		for (Equipment equipment : inventory)
		{
			if (((ExpeditionItem) equipment.getItem()).getGoodType() == goodType)
			{
				if (clone)
				{
					ret.add(new Equipment(equipment.getItem(), equipment.getQuantity()));
				}
				else
				{
					ret.add(equipment);
				}
			}
		}
		return ret;
	}

	public CardinalDirection getHeading()
	{
		return heading;
	}

	public int getItemCount(String itemId)
	{
		int goodCount = 0;
		List<Equipment> inventory = getInventory();
		for (Equipment equipment : inventory)
		{
			if (equipment.getItem().getFullID().equals(itemId))
			{
				goodCount += equipment.getQuantity();
			}
		}
		return goodCount;
	}

	public int getItemCountBasic(String itemId)
	{
		int goodCount = 0;
		List<Equipment> inventory = getInventory();
		for (Equipment equipment : inventory)
		{
			if (((ExpeditionItem) equipment.getItem()).getBaseID().equals(itemId))
			{
				goodCount += equipment.getQuantity();
			}
		}
		return goodCount;
	}

	@Override
	public List<Equipment> getItems()
	{
		return getInventory();
	}

	/**
	 * Get the buildings the expeditions knows how to build
	 * 
	 * @return
	 */
	public List<Building> getKnownBuildings()
	{
		// TODO Make this dependant on the advancement level of the expedition
		return BuildingFactory.getBuildingsList();
	}

	public Position getLandCellAround()
	{
		List<Position> cells = getLandCellsAround();
		if (cells.size() == 0)
			return null;
		else
		{
			for (Position cell : cells)
			{
				if (cell.x == getLongitude() || cell.y == getLatitude())
					return cell;
			}
			return cells.get(0);
		}
	}

	public List<Position> getLandCellsAround()
	{
		List<Position> ret = new ArrayList<Position>();
		int longitudeScale = GlobeMapModel.getSingleton().getLongitudeScale(getLatitude());
		int latitudeScale = GlobeMapModel.getSingleton().getLatitudeHeight();
		AbstractCell[][] around = getEnvironmentAround(1, 1).getCellsAround();
		for (int x = 0; x < around[0].length; x++)
		{
			for (int y = 0; y < around.length; y++)
			{
				if (((OverworldExpeditionCell) around[x][y]).isLand())
				{
					ret.add(new Position(getLongitude() + (x - 1) * longitudeScale,
							getLatitude() + (y - 1) * -latitudeScale));
				}
			}
		}
		return ret;
	}

	public int getLastActionTimeCost()
	{
		return getCost();
	}

	public int getLatitude()
	{
		return getPosition().y();
	}

	public ExpeditionUnit getLeaderUnit()
	{
		return leaderUnit;
	}

	public ExpeditionLevel getLocation()
	{
		return (ExpeditionLevel) getLevel();
	}

	public int getLongitude()
	{
		return getPosition().x();
	}

	/**
	 * 0 to 10, 5 is normal
	 * 
	 * @return
	 */
	public int getMorale()
	{
		return expeditionMorale;
	}

	public int getMoraleAttackModifier()
	{
		int morale = getMorale();
		if (morale == 10)
			return 2;
		if (morale >= 6)
			return 1;
		if (morale >= 4)
			return 0;
		if (morale >= 2)
			return -1;
		return -2;
	}

	public String getMoraleDescription()
	{
		int morale = getMorale();
		return MORALE_DESCRIPTIONS[morale] + " (" + morale + ")";

	}

	public int getMoraleSpeedModifier()
	{
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

	public List<Equipment> getMounts()
	{
		List<Equipment> ret = new ArrayList<Equipment>();
		List<Equipment> inventory = getInventory();
		for (Equipment equipment : inventory)
		{
			if (equipment.getItem() instanceof Mount)
			{
				ret.add(equipment);
			}
		}
		return ret;
	}

	public MovementMode getMovementMode()
	{
		return movementMode;
	}

	public MovementSpeed getMovementSpeed()
	{
		if (getMovementMode() == MovementMode.SHIP)
		{
			int reduction = 0;
			int boost = 0;
			if (!hasFullShipCrew())
			{
				reduction += 2;
			}
			if (getShipHealth() < 70)
			{
				reduction++;
			}
			if (getLocation().getWeather().isWindy())
			{
				boost++;
			}
			MovementSpeed ret = getSailingPoint().getSpeed();
			for (int i = 0; i < reduction; i++)
			{
				ret = ret.reduced();
			}
			for (int i = 0; i < boost; i++)
			{
				ret = ret.boost();
			}
			boost = getMoraleSpeedModifier();
			if (boost < 0)
			{
				for (int i = 0; i < boost; i++)
				{
					ret = ret.reduced();
				}
			}
			else
			{
				for (int i = 0; i < boost; i++)
				{
					ret = ret.boost();
				}
			}
			return ret;
		}
		else if (getMovementMode() == MovementMode.HORSE)
		{
			return MovementSpeed.FAST;
		}
		else
		{
			MovementSpeed ret = MovementSpeed.NORMAL;
			int boost = getMoraleSpeedModifier();
			if (boost < 0)
			{
				for (int i = 0; i < boost; i++)
				{
					ret = ret.reduced();
				}
			}
			else
			{
				for (int i = 0; i < boost; i++)
				{
					ret = ret.boost();
				}
			}

			if (getFoodDays() == 0)
			{
				return MovementSpeed.SLOW;
			}
			else
			{
				return MovementSpeed.NORMAL;
			}
		}
	}

	public int getNaturalistNumber()
	{
		int acum = 0;
		List<Equipment> units = getGoods(GoodType.PEOPLE);
		for (Equipment unit : units)
		{
			if (unit.getItem().getFullID().equals("NATURALIST"))
				acum += unit.getQuantity();
		}
		return acum;
	}

	public int getOffshoreCarryable(ExpeditionItem item)
	{
		MovementMode currentMovementMode = getMovementMode();
		setMovementMode(MovementMode.SHIP);
		int retValue = getCarryable(item);
		setMovementMode(currentMovementMode);
		return retValue;
	}

	public int getOffshoreCarryCapacity()
	{
		MovementMode currentMovementMode = getMovementMode();
		setMovementMode(MovementMode.SHIP);
		int retValue = getCarryCapacity();
		setMovementMode(currentMovementMode);
		return retValue;
	}

	public int getOffshoreCurrentlyCarrying()
	{
		MovementMode currentMovementMode = getMovementMode();
		setMovementMode(MovementMode.SHIP);
		int retValue = getCurrentlyCarrying();
		setMovementMode(currentMovementMode);
		return retValue;
	}

	public int getOffshoreFoodDays()
	{
		MovementMode currentMovementMode = getMovementMode();
		setMovementMode(MovementMode.SHIP);
		int ret = getProjectedFoodDays();
		setMovementMode(currentMovementMode);
		return ret;
	}

	public int getPerceivedLuck()
	{
		return perceivedLuck;
	}

	public int getPower()
	{
		int power = 0;
		List<Equipment> inventory = getInventory();
		for (Equipment equipment : inventory)
		{
			if (equipment.getItem() instanceof ExpeditionUnit)
			{
				power += ((ExpeditionUnit) equipment.getItem()).getPower() * equipment.getQuantity();
			}
		}
		return power;
	}

	/**
	 * Ignores extreme food consumption conditions (like hibernating)
	 * 
	 * @return
	 */
	public int getProjectedFoodDays()
	{
		int fooddays = (int) Math.round((double) getCurrentFood() / (double) foodConsumerDelegate.getDailyFoodConsumption());		
		return fooddays;
	}

	
	public int getFoodDays()
	{
		if (getDailyFoodConsumption() == 0)
		{
			return 0;
		}
		return (int) Math.round((double) getCurrentFood() / (double) getDailyFoodConsumption());
	}
	
	
	/**
	 * Ignores extreme food consumption conditions (like hibernating)
	 * 
	 * @returns the water days, 
	 */
	public int getProjectedWaterDays()
	{
		int waterdays = (int) Math.round((double) getCurrentWater() / (double) foodConsumerDelegate.getDailyWaterConsumption());
		return waterdays;
	}
	
	
	
	private ExpeditionUnit getRandomUnitFair()
	{
		int totalUnits = getTotalUnits();
		int randomUnit = Util.rand(1, totalUnits);
		int acum = 0;
		List<Equipment> units = getGoods(GoodType.PEOPLE);
		for (Equipment unit : units)
		{
			if (unit.getQuantity() + acum >= randomUnit)
				return (ExpeditionUnit) unit.getItem();
			else
				acum += unit.getQuantity();
		}
		// Shouldn't happen
		return null;
	}

	public SailingPoint getSailingPoint()
	{
		if (getMovementMode() != MovementMode.SHIP)
			return SailingPoint.NONE;
		CardinalDirection windDirection = getLocation().getWindDirection();
		if (windDirection == CardinalDirection.NULL)
		{
			return SailingPoint.STALLED;
		}
		int headingAngle = getHeading().getReferenceAngle();
		int angularDifference = (int) Math.abs(180 - (headingAngle > windDirection.getReferenceAngle()
				? headingAngle - windDirection.getReferenceAngle() : windDirection.getReferenceAngle() - headingAngle));
		if (getLocation().hasStorm(getPosition()))
		{
			if (angularDifference == 0)
				return SailingPoint.RUNNING_STORM;
			else
				return SailingPoint.BARE_POLES;
		}
		else
		{
			return SailingPoint.resolvePoint(angularDifference);
		}
	}

	@Override
	public String getSaveFilename()
	{
		// TODO Auto-generated method stub
		return null;
	}

	private int getSeaAccidentChance(Weather weather)
	{
		switch (weather)
		{
		case CLEAR:
		case FOG:
		case CLOUDY:
			return 2;
		case RAIN:
		case SNOW:
			return 4;
		case STORM:
		case WINDY:
			return 6;
		case GALE_WIND:
			return 20;
		case HURRICANE:
			return 40;
		case DUST_STORM:
			return 0;
		}
		return 0;
	}

	public int getShipHealth()
	{
		int sum = 0;
		int sumMax = 0;
		for (Vehicle vehicle : getCurrentVehicles())
		{
			sum += vehicle.getResistance();
			sumMax += vehicle.getMaxResistance();
		}
		return (int) (Math.round(((double) sum / (double) sumMax) * 100.0d));
	}

	@Override
	public int getSightRangeInCells()
	{
		int bonus = 0;
		int malus = 0;
		if (getItemCount("EXPLORER") > 0)
			bonus++;
		if (getLocation() instanceof ExpeditionMacroLevel)
		{
			bonus += ((OverworldExpeditionCell) getLocation().getMapCell(getPosition())).getHeightMod();
		}
		switch (getLocation().getWeather())
		{
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

		int BASE_SIGHT = 9;
		// int scale = GlobeMapModel.getLongitudeScale(getPosition().y());
		int scale = 1; // Map shouldn't require this to be scaled
		int base = BASE_SIGHT * scale;
		int mod = (bonus - malus) * scale;

		int ret = base + mod;
		if (ret >= 1 && ret <= base)
			return ret;
		else if (ret < 1)
			return 1;
		else
			return base;
	}

	@Override
	public int getSightRangeInDots()
	{
		if (getLevel() instanceof ExpeditionMacroLevel)
		{
			return getSightRangeInCells() * GlobeMapModel.getSingleton().getLatitudeHeight(); // This
																								// is
																								// an
																								// approximation
		}
		else
		{
			return getSightRangeInCells();
		}
	}

	@Override
	public String getStatusString()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public int getSumOfValuables()
	{
		int currentValuable = 0;
		List<Equipment> inventory = getInventory();
		for (Equipment equipment : inventory)
		{
			if (((ExpeditionItem) equipment.getItem()).getGoodType() == GoodType.TRADE_GOODS)
			{
				ExpeditionItem good = (ExpeditionItem) equipment.getItem();
				currentValuable += good.getPalosStoreValue() * equipment.getQuantity();
			}
		}
		return currentValuable;
	}

	public Rank getTitle()
	{
		return expeditionaryTitle;
	}

	public List<Equipment> getTools(boolean clone)
	{
		List<Equipment> ret = new ArrayList<Equipment>();
		List<Equipment> inventory = getInventory();
		for (Equipment equipment : inventory)
		{
			if (equipment.getItem() instanceof Weapon || equipment.getItem() instanceof Armor)
			{
				if (clone)
				{
					ret.add(new Equipment(equipment.getItem(), equipment.getQuantity()));
				}
				else
				{
					ret.add(equipment);
				}

			}
		}
		return ret;
	}

	public int getTotalShips()
	{
		return getCurrentVehicles().size();
	}

	public int getTotalUnits()
	{
		int acum = 0;
		List<Equipment> units = getGoods(GoodType.PEOPLE);
		for (Equipment unit : units)
		{
			acum += unit.getQuantity();
		}
		return acum;
	}

	public List<Town> getTowns()
	{
		return towns;
	}

	@Override
	public String getTypeDescription()
	{
		return "Expedition";
	}

	public List<Equipment> getUnarmedUnits()
	{
		List<Equipment> units = getGoods(GoodType.PEOPLE);
		List<Equipment> ret = new ArrayList<Equipment>();
		for (Equipment unit : units)
		{
			if (((ExpeditionUnit) unit.getItem()).getWeapon() == null)
			{
				ret.add(unit);
			}
		}
		return ret;
	}

	public List<Equipment> getUnarmoredUnits()
	{
		List<Equipment> units = getGoods(GoodType.PEOPLE);
		List<Equipment> ret = new ArrayList<Equipment>();
		for (Equipment unit : units)
		{
			if (((ExpeditionUnit) unit.getItem()).getArmor() == null)
			{
				ret.add(unit);
			}
		}
		return ret;
	}

	public List<Equipment> getUnmountedUnits()
	{
		List<Equipment> units = getGoods(GoodType.PEOPLE);
		List<Equipment> ret = new ArrayList<Equipment>();
		for (Equipment unit : units)
		{
			if (((ExpeditionUnit) unit.getItem()).getMount() == null)
			{
				ret.add(unit);
			}
		}
		return ret;
	}

	public int getUnwoundedUnitCountBasic(String basicId)
	{
		int goodCount = 0;
		List<Equipment> inventory = getGoods(GoodType.PEOPLE);
		for (Equipment equipment : inventory)
		{
			ExpeditionUnit unit = (ExpeditionUnit) equipment.getItem();
			if (unit.getBaseID().equals(basicId) && !unit.isWounded())
			{
				goodCount += equipment.getQuantity();
			}
		}
		return goodCount;
	}

	public int getVehicleCount(String itemId)
	{
		int goodCount = 0;
		List<Vehicle> inventory = getCurrentVehicles();
		for (Vehicle equipment : inventory)
		{
			if (equipment.getFullID().equals(itemId))
			{
				goodCount++;
			}
		}
		return goodCount;
	}

	public double getWaterConsumptionMultiplier()
	{
		if (getLevel() instanceof ExpeditionMacroLevel)
		{		
			return TemperatureRules.getTemperatureWaterModifier(getLocation().getTemperature());
		}		
		else
		{			
			return 1;
		}
	}

	public int getWaterDays()
	{
		if (getDailyWaterConsumption() == 0)
		{
			return 0;
		}
		return (int) Math.round((double) getCurrentWater() / (double) getDailyWaterConsumption());
	}

	public int getWeightToBoardShip()
	{
		int currentlyCarrying = 0;
		List<Equipment> inventory = getInventory();
		for (Equipment equipment : inventory)
		{
			currentlyCarrying += ((ExpeditionItem) equipment.getItem()).getWeight() * equipment.getQuantity();
		}
		return currentlyCarrying;
	}

	public List<Equipment> getWoundedUnits()
	{
		List<Equipment> units = getGoods(GoodType.PEOPLE);
		List<Equipment> ret = new ArrayList<Equipment>();
		for (Equipment unit : units)
		{
			if (((ExpeditionUnit) unit.getItem()).isWounded())
			{
				ret.add(unit);
			}
		}
		return ret;
	}

	public void giveTip(Tip tip)
	{
		if (!getFlag("TIP_GIVEN_" + tip))
		{
			setFlag("TIP_GIVEN_" + tip, true);
			switch (tip)
			{
			case YOU_ARE_ON_IRONS:
				break;
			case EXPANDING_SETTLEMENT:
				break;
			case LANDING_ON_NEW_WORLD:
				break;
			case ON_THE_WAY_TO_NEW_WORLD:
				break;
			case TRAPPED_ON_A_STORM:
				break;
			default:
				break;
			}
		}
	}

	public boolean hasDiscovered(Discovery discovery, String discoveryText)
	{
		for (ExpeditionDiscovery d : discoveryLog)
		{
			if (d.getDiscoveryType() == discovery && d.getDiscoveryText().equals(discoveryText))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines if the expedition has all the crew it needs to sail quickly.
	 * 
	 * Crew per ship is: 1 Captain 25 Sailors
	 * 
	 * @return
	 */
	private boolean hasFullShipCrew()
	{
		int ships = getTotalShips();
		int requiredCaptains = ships;
		int requiredSailors = ships * 25;

		int captains = getUnwoundedUnitCountBasic("CAPTAIN");
		int sailors = getUnwoundedUnitCountBasic("SAILOR");
		return captains >= requiredCaptains && sailors >= requiredSailors;

	}

	public boolean hasMarineChronometer()
	{
		return false;
	}

	/**
	 * Heals wounded units at the expedition, this should be called daily
	 */
	public void heal()
	{
		int chance = 4; // At sea, an unit recovers in one month (1/28=4%)
		if (getMovementMode().isLandMovement())
			chance = 5; // At sea, an unit recovers in three weeks (1/21=5%)

		List<Equipment> woundedUnits = getWoundedUnits();

		// Doctors Bonus
		/* For each 20 wounded units, each unwounded doctor adds a 5% chance */
		int doctors = getUnwoundedUnitCountBasic("DOCTOR");
		if (doctors > 0)
		{
			int woundedUnitsCount = 0;
			for (Equipment woundedEquipment : woundedUnits)
			{
				woundedUnitsCount += woundedEquipment.getQuantity();
			}
			int requiredDoctors = (int) Math.ceil(woundedUnitsCount / 20);
			if (requiredDoctors > doctors)
			{
				chance += 2;
			}
			else
			{
				chance += 5;
			}
		}

		for (Equipment woundedEquipment : woundedUnits)
		{
			if (Util.chance(chance))
			{
				// Some units will heal
				ExpeditionUnit unit = (ExpeditionUnit) woundedEquipment.getItem();
				int units = Util.rand(1, woundedEquipment.getQuantity());
				message(units + " " + EnglishGrammar.plural(unit.getDescription(), units) + " have recovered");
				reduceUnits((ExpeditionUnit) woundedEquipment.getItem(), units);
				ExpeditionUnit unwoundedUnit = (ExpeditionUnit) unit.clone();
				unwoundedUnit.setWounded(false);
				addUnits(unwoundedUnit, units);
			}
		}
	}

	public void increaseDaysAtSea()
	{
		daysOnSea++;
	}

	/**
	 * Quantity in degree minutes
	 * 
	 * @param q
	 */
	public void increaseDeducedReckonWestByLongitude(int longitude)
	{
		deducedReckonWest += GlobeMapModel.getSingleton().transformLongIntoNauticalMiles(getPosition().y, longitude);
	}

	/**
	 * Quantity in miles
	 * 
	 * @param q
	 */
	public void increaseDeducedReckonWestByMiles(int miles)
	{
		deducedReckonWest += miles;
	}

	public void increaseWinBalance()
	{
		winBalance++;
		if (winBalance == 10)
			winBalance = 10;
	}

	public boolean isAnchored()
	{
		return anchored;
	}

	public boolean isArmed()
	{
		return getFlag("ARMED");
	}

	public boolean isForaging()
	{
		return getLocation() instanceof ExpeditionMacroLevel ;//&& getFoodDays() <= 5;
	}

	public boolean isHibernate()
	{
		return hibernate;
	}

	@Override
	public boolean isInvisible()
	{
		return false;
	}

	public boolean isMounted()
	{
		return getFlag("MOUNTED");
	}

	private boolean isOnSea()
	{
		return getLocation() instanceof ExpeditionMacroLevel
				&& ((OverworldExpeditionCell) getLocation().getMapCell(getPosition())).isSea();
	}

	@Override
	public boolean isPeopleContainer()
	{
		return true;
	}

	public void killUnits(int quantity, String cause)
	{
		Collection<Pair<ExpeditionUnit, Integer>> values = foodConsumerDelegate.killUnits(quantity);
		String killMessage = "";
		int i = 0;
		for (Pair<ExpeditionUnit, Integer> killInfo : values)
		{
			if (killInfo.getB() == 0)
			{
				i++;
				continue;
			}
			if (killInfo.getB() == 1)
			{
				killMessage += EnglishGrammar.a(killInfo.getA().getDescription()) + " "
						+ killInfo.getA().getDescription();
			}
			else
				killMessage += killInfo.getB() + " " + killInfo.getA().getPluralDescription();
			if (i == values.size() - 2)
				killMessage += " and ";
			else if (i == values.size() - 1)
				;
			else if (values.size() > 1)
				killMessage += ", ";
			i++;
		}
		if (quantity == 1)
			//getLevel().addMessage(killMessage + " " + cause + " to death.");
			MessengerService.addMessage(killMessage + " " + cause + " to death.", getPosition());
		else
			MessengerService.addMessage(killMessage + " " + cause + " to death.", getPosition());
	}

	@Override
	public void landOn(Position destinationPoint) throws ActionCancelException
	{
		boolean storm = getLocation().hasStorm(destinationPoint) && getMovementMode() == MovementMode.SHIP;
		if (storm)
		{
			getLevel().addMessage("You are caught on a Storm!");
			if (isAnchored())
			{
				getLevel().addMessage("We must weigh anchors or dock immediately!");
				wearOutShips(40, true);
			}
			else
			{
				wearOutShips(20, true);
				increaseDeducedReckonWestByMiles(Util.rand(-5, 5));
				if (Util.chance(30))
				{
					int xScale = GlobeMapModel.getSingleton().getLongitudeScale(getPosition().y);
					int yScale = GlobeMapModel.getSingleton().getLatitudeHeight();
					// Random movement caused by the storm
					destinationPoint.x += Util.rand(-1, 1) * xScale;
					destinationPoint.y += Util.rand(-1, 1) * yScale;
				}
			}

			if (Util.chance(5))
			{
				if (getTotalUnits() == 1)
				{
					// The last one aboard falls into the sea, no one can try to
					// save him
					ExpeditionUnit randomUnit = getRandomUnitFair();
					UserInterface.getUI()
							.showImportantMessage(getEventDatePreffix() + "The last " + randomUnit.getDescription()
									+ " of your expedition falls overboard... the sea takes him away...");
					deathCause = DeathCause.DEATH_BY_DROWNING;
					informPlayerEvent(DEATH);
					return;
				}
				else
				{
					ExpeditionUnit randomUnit = getRandomUnitFair();
					int choice = UserInterface.getUI().switchChat("Man overboard!",
							"A " + randomUnit.getDescription()
									+ " has fallen overboard in the storm! XXX What will you do?",
							"Let's try to save him!", "We must leave the man behind.");
					if (choice == 0)
					{
						if (Util.chance(50))
						{
							message("You pull the " + randomUnit.getDescription() + " from the seas!");
							boostMorale(100);
						}
						else
						{
							reduceQuantityOf(randomUnit);
							message("The sea takes the " + randomUnit.getDescription() + " away!");
						}
						wearOutShips(50, false);
					}
					else
					{
						message("The sea takes the " + randomUnit.getDescription() + " away!");
						reduceQuantityOf(randomUnit);
						decreaseMorale(100);
					}
				}
			}
			seaAccident(10);
		}

		List<AbstractFeature> features = getLevel().getFeaturesAt(destinationPoint);
		if (features != null)
		{
			// Normalize destination point
			destinationPoint.x = GlobeMapModel.getSingleton().normalizeLong(destinationPoint.y, destinationPoint.x);

			// Clone the collection to prevent coModification issues
			reusableFeatureList.clear();
			reusableFeatureList.addAll(features);
			boolean solidFeature = false;
			for (AbstractFeature feature : reusableFeatureList)
			{
				feature.onStep(this);
				if (feature.isSolid())
					solidFeature = true;
			}
			if (solidFeature)
			{
				return;
			}
		}

		if (getLevel() instanceof SettlementLevel)
		{
			Store store = ((SettlementLevel) getLevel()).getStoreAt(destinationPoint);
			if (store != null)
			{
				((ExpeditionUserInterface) UserInterface.getUI()).launchStore(store);
				throw new ActionCancelException();
			}
		}

		AbstractCell absCell = getLevel().getMapCell(destinationPoint);
		if (absCell instanceof ExpeditionCell)
		{
			ExpeditionCell cell = (ExpeditionCell) absCell;
			if (cell.getStepCommand() != null)
			{
				if (cell.getStepCommand().equals("DEPARTURE"))
				{
					if (getTotalShips() == 0)
					{
						getLevel().addMessage("You have no ships to board.");
						throw new ActionCancelException();
					}
					else if (getItemCountBasic("SAILOR") < 15)
					{
						getLevel().addMessage("You need at least 15 sailors to depart.");
						throw new ActionCancelException();
					}
					else
					{
						if (((ExpeditionUserInterface) UserInterface.getUI()).depart())
						{
							String superLevelId = getLocation().getSuperLevelId();
							if (superLevelId == null)
							{
								getLevel().addMessage("Nowhere to go.");
							}
							else
							{
								if (!getFlag("SAILING_EXPLAINED"))
								{
									setFlag("SAILING_EXPLAINED", true);
									if (UserInterface.getUI()
											.promptChat("Do you want me to explain you the basics of sailing?"))
									{
										m("Your ships are currently anchored in front of the port. You can rotate them around to set your bearing, then you must weigh anchors to sail ahead.");
										m("For sailing ships, speed is greatly influenced by the direction and strength of the wind, the morale of your crew and the integrity of both hull and tacking (greater than 75%)");
										m("You must also have enough hands on board (25 sailors and an officer per ship), in order to sail at decent speeds.");
										m("If you are sailing against the wind you will commonly find yourself \"On Irons\", that means that your crew was unable to outmaneuver the wind and so the ship didn't advance.");
										m("Likewise, you may find your expedition on a becalmed sea; unless all of your ships are oar powered, you won't be able to move around..");
										m("You ships will always move forward when unanchored. Beware sailing your ships into land! You can anchor if you are near beach, in order to avoid crashing.");
										m("As your expedition is not equiped with a marine chronometer, there's no way to know your exact longitudinal position. That's where the Dead' Reckon counter comes");
										m("The Dead' Reckon is your aproximate calculus of longitudinal distance from a reference point, based on the distance travelled every day. You can reset the reference point anytime.");
										ml("God bless your voyage! Remember you can check the game manual for detailed instructions any time!");
									}

								}

								setMovementMode(MovementMode.SHIP);
								informPlayerEvent(Player.EVT_GOTO_LEVEL, superLevelId);
								destinationPoint = new Position(getPosition());
								destinationPoint.x -= GlobeMapModel.getSingleton().getLongitudeScale(getPosition().y);
								setAnchored(true);
								super.landOn(destinationPoint);
								throw new ActionCancelException();

							}
						}
						throw new ActionCancelException();

					}
				}
				else if (cell.getStepCommand().equals("TRAVEL_CASTLE"))
				{
					((ExpeditionUserInterface) UserInterface.getUI())
							.showBlockingMessage("A charriot takes you to the Alcazar of C�rdova");
				}
			}
		}
		else
		{
			OverworldExpeditionCell cell = (OverworldExpeditionCell) absCell;

			switch (getMovementMode())
			{
			case SHIP:
				if (cell.isRiver())
				{
					wearOutShips(30, true);
				}
				if (cell.isLand() && !cell.isRiver())
				{
					getLevel().addMessage("You are too close to land!");
					wearOutShips(60, true);
					throw new ActionCancelException();
				}
				break;
			default:
				if (cell.isLand() && !cell.isRiver())
				{
					touchLand();
				}
			}
			if (Util.chance(95))
			{
				// Simulate the lack of precision
				increaseDeducedReckonWestByLongitude(getPosition().x() - destinationPoint.x());
			}
		}
		super.landOn(destinationPoint);
	}

	private void m(String string)
	{
		((ExpeditionUserInterface) UserInterface.getUI()).showBlockingMessage(string, true);
	}

	private void ml(String string)
	{
		((ExpeditionUserInterface) UserInterface.getUI()).showBlockingMessage(string, false);
	}

	public void modifyPerceivedLuck(int perceivedLuckMod)
	{
		this.perceivedLuck += perceivedLuckMod;
		if (perceivedLuck > 15)
			perceivedLuck = 15;
		if (perceivedLuck < -15)
			perceivedLuck = -15;
	}

	public void mount()
	{
		List<Equipment> mounts = getMounts();
		for (Equipment mount : mounts)
		{
			// For each kind of mount, try to mount all unmounted units
			List<Equipment> units = getUnmountedUnits();
			Collections.sort(units, new Comparator<Equipment>()
			{
				public int compare(Equipment arg0, Equipment arg1)
				{
					return ((ExpeditionUnit) arg1.getItem()).getAttack().getMax()
							- ((ExpeditionUnit) arg0.getItem()).getAttack().getMax();
				}
			});
			for (Equipment unit : units)
			{
				int available = mount.getQuantity();
				int unitsToMount = available > unit.getQuantity() ? unit.getQuantity() : available;
				reduceQuantityOf(mount.getItem(), unitsToMount);
				// Split equipment in mounted and unmounted
				if (unitsToMount > 0)
				{
					reduceQuantityOf(unit.getItem(), unitsToMount);
					ExpeditionUnit newUnit = (ExpeditionUnit) ((ExpeditionUnit) unit.getItem()).clone();
					newUnit.setMount((Mount) mount.getItem());
					addItem(newUnit, unitsToMount);
				}
			}
		}
		setMounted(true);
		validateMounted();
	}

	private boolean nearLandSignals(int range)
	{
		// See if there's land nearby
		if (!(getLevel() instanceof ExpeditionMacroLevel))
			return false;
		Circle c = new Circle(getPosition(), range);
		List<Position> points = c.getPoints();
		for (Position point : points)
		{
			if (Util.chance(5))
			{
				OverworldExpeditionCell cell = (OverworldExpeditionCell) getLocation().getMapCell(point);
				if (cell != null && !cell.isWater())
				{
					if (!getFlag("APROACHING_LAND_TUTORIAL"))
					{
						setFlag("APROACHING_LAND_TUTORIAL", true);
						FriarTutorial.activate(FriarTutorial.APROACHING_LAND);
					}
					switch (Util.rand(0, 3))
					{
					case 0:
						message("You see some tufts of grass floating on the sea!");
						return true;
					case 1:
						CardinalDirection d = CardinalDirection.getGeneralDirection(getPosition(), point);
						message("You see birds heading " + d.getDescription());
						return true;
					case 2:
						d = CardinalDirection.getGeneralDirection(getPosition(), point);
						message("You see a cloud block to the " + d.getDescription());
						return true;
					case 3:
						d = CardinalDirection.getGeneralDirection(getPosition(), point);
						message("There seems to be land to the " + d.getDescription());
						return true;
					}
				}
			}
		}
		return false;

	}

	@Override
	public void onActorStep(Actor actor)
	{

	}

	@Override
	public void onFeatureStep(AbstractFeature destinationFeature)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onItemsStep(List<AbstractItem> items)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onItemStep(AbstractItem item)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onNullDestination()
	{
		// TODO Auto-generated method stub

	}

	private boolean onOpenSea()
	{
		return isOnOpenSea;
	}

	@Override
	public void onSolidDestination()
	{
		// TODO Auto-generated method stub

	}

	public void randomEvents()
	{
		// Jonah
		if (isOnSea() && getPerceivedLuck() < -10)
		{
			if (getTotalUnits() > 1)
			{
				if (Util.chance(20))
				{
					int choice = UserInterface.getUI().switchChat("A man overboard is bringing us doom",
							"There is a Jonah amidst us, he must drown in the seas!XXX What will you do?",
							"Defend the man.", "Throw the man overboard.");
					if (choice == 0)
					{
						getLevel().addMessage("We are doomed!");
						decreaseMorale(100);
					}
					else
					{
						ExpeditionUnit randomUnit = getRandomUnitFair();
						getLevel().addMessage(
								"You cast a " + randomUnit.getDescription() + " into the sea. May he rests in peace");
						modifyPerceivedLuck(10);
						boostMorale(100);
					}
				}
			}
		}
		// Unease
		if (getMorale() < 4)
		{
			if (Util.chance(5))
			{
				int choice = UserInterface.getUI().switchChat("Unease in your expedition",
						"Enough of this mindless journey! Let's head back home! XXX What will you do?",
						"Preach about the importance of this mission.", "Give away 100 gold.");
				if (choice == 0)
				{
					int shot = Util.rand(0, 100);
					if (shot < 30)
					{
						getLevel().addMessage("Your expedition feels motivated");
						boostMorale(100);
					}
					else if (shot < 60)
					{
						getLevel().addMessage("Your empty words do not make a difference!");
						decreaseMorale(100);
					}
					else
					{
						getLevel().addMessage("Your words make no effect on your men");
					}
				}
				else
				{
					if (getAccountedGold() >= 100)
					{
						reduceAccountedGold(100);
						if (Util.chance(90))
						{
							getLevel().addMessage("We hope to survive enough to spend this gold.");
							boostMorale(100);
						}
						else
						{
							getLevel().addMessage("Gold is of no use to us.");
						}
					}
					else
					{
						setAccountedGold(0);
						if (Util.chance(10))
						{
							getLevel().addMessage("We hope to live enough to see that gold");
							boostMorale(100);
						}
						else
						{

							getLevel().addMessage("We do not believe in your empty promises!");
							decreaseMorale(100);
						}
					}
				}
				updateMorale();
			}
		}

		// Accident
		if (isOnSea())
		{
			seaAccident(getSeaAccidentChance(getLocation().getWeather()));
		}

		// Murder
		if (hasCounter("KILLER_ALIVE") || getMorale() < 3)
		{
			if (Util.chance(hasCounter("KILLER_ALIVE") ? 10 : 5) && getTotalUnits() > 2)
			{
				ExpeditionUnit randomUnit = getRandomUnitFair();
				reduceQuantityOf(randomUnit);
				int choice = UserInterface.getUI().switchChat("Treacherous Murder!",
						"A " + randomUnit.getDescription() + " has been murdered! XXX What will you do?",
						"Investigate the event. (1 day)", "Ignore the event.");
				if (choice == 0)
				{
					// Investigate the murder
					setNextAction(new Hibernate(1, false));
					if (Util.chance(50))
					{
						boostMorale(100);
						ExpeditionUnit murderer = getRandomUnitFair();
						choice = UserInterface.getUI().switchChat("Treacherous Murder!",
								"You found a " + murderer.getDescription()
										+ " to be the culprit! XXX What will you do?",
								"Punish the " + murderer.getDescription() + ".",
								"Execute the " + murderer.getDescription() + ".");
						if (choice == 0)
						{
							UserInterface.getUI().showImportantMessage("You flog the " + murderer.getDescription()
									+ ", hoping this doesn't happen again.");
							if (Util.chance(33))
							{
								setCounter("KILLER_ALIVE", 100);
							}
							else
							{
								removeCounter("KILLER_ALIVE");
							}
						}
						else
						{
							reduceQuantityOf(murderer);
							UserInterface.getUI().showImportantMessage(
									"You execute the " + murderer.getDescription() + ". May this serve as an example");
							removeCounter("KILLER_ALIVE");
						}
					}
					else
					{
						UserInterface.getUI().showImportantMessage(
								"You found no culprit. You hope the murderer won't show up again...");
						if (Util.chance(66))
						{
							setCounter("KILLER_ALIVE", 50);
						}
						else
						{
							removeCounter("KILLER_ALIVE");
						}
					}
				}
				else
				{
					// Ignore the event
					UserInterface.getUI().showImportantMessage("You hope the murderer won't show up again...");
					decreaseMorale(100);
					setCounter("KILLER_ALIVE", 200);
				}

			}
		}

	}

	public void recalcCapacity()
	{
		// Should I refresh the stats on demand instead of always calculating
		// them??
	}

	public void reduceAccountedGold(int gold)
	{
		accountedGold -= gold;
	}

	public void reduceAllItems(List<Equipment> items)
	{
		for (Equipment equipment : items)
		{
			reduceQuantityOf(equipment.getItem().getFullID(), equipment.getQuantity());
		}
	}

	public void reduceGood(String goodId, int quantity)
	{
		List<Equipment> inventory = getInventory();
		for (Equipment equipment : inventory)
		{
			if (equipment.getItem().getFullID().equals(goodId))
			{
				reduceQuantityOf(equipment.getItem(), quantity);
				return;
			}
		}
	}

	public void reduceItemOffshore(ExpeditionItem item, int quantity)
	{
		MovementMode currentMovementMode = getMovementMode();
		setMovementMode(MovementMode.SHIP);
		reduceQuantityOf(item, quantity);
		setMovementMode(currentMovementMode);
	}

	@Override
	public void reduceQuantityOf(AbstractItem what, int quantity)
	{
		super.reduceQuantityOf(what, quantity);
		if (what instanceof ExpeditionUnit)
			validateMounted();
	}

	public void reduceUnits(ExpeditionUnit unit, int quantity)
	{
		reduceQuantityOf(unit, quantity);
	}

	@Override
	public void reduceUnits(ExpeditionUnit unit, int quantity, DeathCause cause)
	{
		reduceUnits(unit, quantity);
		if (cause != null)
			deathCause = cause;
	}

	public void removeAllGoods()
	{
		removeAllItems();
	}

	public void removeVehicle(ExpeditionItem eitem)
	{
		currentVehicles.remove(eitem);
	}

	public void reportDiscoveries()
	{
		if (discoveryLog.size() == 0)
			return;

		for (ExpeditionDiscovery d : discoveryLog)
		{
			if (d.isReported())
				continue;

			setFame(getFame() + d.getFame());
			getLevel().addMessage(
					"You reported " + d.getDiscoveryText() + " to Spain and gain " + d.getFame() + " points of fame");
			d.setReported(true);
		}
	}

	@Override
	public boolean requiresUnitsToContainItems()
	{
		return true;
	}

	public void resetDaysAtSea()
	{
		daysOnSea = 0;
	}

	public void resetDeducedReckonWest()
	{
		deducedReckonWest = 0;
	}

	private void seaAccident(int chance)
	{
		if (Util.chance(chance))
		{
			ExpeditionUnit randomUnit = getRandomUnitFair();
			UserInterface.getUI().showImportantMessage(getEventDatePreffix() + "Accident! A "
					+ randomUnit.getDescription() + " is injured while performing his duty on board!");
			reduceUnits(randomUnit, 1, DeathCause.DEATH_BY_DROWNING);
			ExpeditionUnit woundedUnit = (ExpeditionUnit) randomUnit.clone();
			woundedUnit.setWounded(true);
			addUnits(woundedUnit, 1);
		}
	}

	@Override
	public void see()
	{
		if (getLevel() instanceof ExpeditionLevelReader)
		{
			fov.setScale(GlobeMapModel.getSingleton().getLongitudeScale(getPosition().y()),
					GlobeMapModel.getSingleton().getLatitudeHeight());
		}
		else
		{
			fov.setScale(1, 1);
		}
		super.see();
	}

	@Override
	public void seeMapCell(AbstractCell cell)
	{
		super.seeMapCell(cell);
		if (isOnOpenSea)
		{
			if (cell instanceof OverworldExpeditionCell)
			{
				if (!((OverworldExpeditionCell) cell).isWater())
				{
					isOnOpenSea = false;
				}
			}
		}
	}

	public void setAccountedGold(int accountedGold)
	{
		this.accountedGold = accountedGold;
	}

	public void setAnchored(boolean anchored)
	{
		this.anchored = anchored;
	}

	public void setArmed(boolean value)
	{
		setFlag("ARMED", value);
	}

	public void setCurrentVehicles(List<Vehicle> currentVehicles)
	{
		this.currentVehicles = currentVehicles;
	}

	public void setExpeditionary(String expeditionary)
	{
		this.expeditionary = expeditionary;
	}

	public void setFame(int fame)
	{
		this.fame = fame;
	}

	public void setHeading(CardinalDirection heading)
	{
		this.heading = heading;
	}

	public void setHibernate(boolean hibernate)
	{
		this.hibernate = hibernate;
	}

	public void setLeaderUnit(ExpeditionUnit leaderUnit)
	{
		this.leaderUnit = leaderUnit;
	}

	public void setMounted(boolean value)
	{
		setFlag("MOUNTED", value);
	}

	public void setMovementMode(MovementMode movementMode)
	{
		this.movementMode = movementMode;
		if (movementMode.isLandMovement())
		{
			setAnchored(false);
		}
	}

	private boolean sightLand(int range)
	{
		if (!(getLevel() instanceof ExpeditionMacroLevel))
			return false;
		Circle c = new Circle(getPosition(), range);
		List<Position> points = c.getPoints();
		for (Position point : points)
		{
			if (Util.chance(15))
			{
				OverworldExpeditionCell cell = (OverworldExpeditionCell) getLocation().getMapCell(point);
				if (cell != null && !cell.isWater())
				{
					CardinalDirection d = CardinalDirection.getGeneralDirection(getPosition(), point);
					message("You see land to the " + d.getDescription() + "!");
					return true;
				}
			}
		}
		return false;

	}

	public void touchLand()
	{
		if (getDaysOnSea() > 0)
		{
			ExpeditionMusicManager.playTune("LAND");
		}
		if (getDaysOnSea() > 20)
		{
			message("Land at last!");
		}
		int longitudeDegrees = GlobeMapModel.getSingleton().getLongitudeDegrees(getPosition().x);
		if (longitudeDegrees < -30)
		{
			if (!getFlag("FIRST_LAND_TUTORIAL"))
			{
				setFlag("FIRST_LAND_TUTORIAL", true);
				FriarTutorial.activate(FriarTutorial.FIRST_LAND);
			}
		}
		resetDaysAtSea();
	}

	public void touchPort()
	{
		if (getDaysOnSea() > 20)
		{
			message("Land at last!");
		}
		if (getDeducedReckonWest() != 0)
		{
			if (UserInterface.getUI().promptChat("Do you want to reset your deduced reckon?"))
			{
				resetDeducedReckonWest();
			}
		}
		resetDaysAtSea();
	}

	public void updateMorale()
	{
		if (getTotalUnits() == 0)
		{
			expeditionMorale = 5;
			return;
		}
		expeditionMorale = 5;
		// Maluses
		// Lack of food
		if (getFoodDays() < 15)
			expeditionMorale--;
		if (getFoodDays() < 5)
			expeditionMorale--;
		// Military Losses
		if (winBalance < -2)
			expeditionMorale--;
		if (winBalance < -5)
			expeditionMorale--;
		// Long time no see land
		if (daysOnSea > 20)
		{
			if (Util.chance(20))
				message("Our men want to touch land");
			expeditionMorale--;
		}
		if (daysOnSea > 40)
		{
			if (Util.chance(20))
				message("Our men are desperate to touch land!");
			expeditionMorale--;
		}
		// On a storm
		if (getMovementMode() == MovementMode.SHIP && getLocation().hasStorm(getPosition()))
		{
			if (Util.chance(20))
				message("This storm will be our doom!");
			expeditionMorale -= 2;
		}
		// Ships Cracking appart
		if (getMovementMode() == MovementMode.SHIP && getShipHealth() < 50)
		{
			if (Util.chance(20))
				message("Our ships are cracking apart.");
			expeditionMorale--;
		}
		// Becalmed
		if (getLocation().getWindDirection() == CardinalDirection.NULL)
		{
			expeditionMorale--;
		}
		// Unlucky
		if (getPerceivedLuck() < -5)
		{
			if (Util.chance(20))
				message("We are in a row of back luck.");
			expeditionMorale--;
		}

		// Temporary mod
		if (hasCounter("MORALE_DOWN"))
		{
			expeditionMorale--;
		}

		// Bonuses
		// Military Wins
		if (winBalance > 2)
			expeditionMorale++;
		if (winBalance > 5)
			expeditionMorale++;
		// Leaders
		if (getItemCountBasic("CAPTAIN") >= 1)
			expeditionMorale++;
		if (getItemCountBasic("SOLDIER") >= 1 || getItemCountBasic("MARINE") >= 1)
		{
			expeditionMorale++;
		}
		// Loot
		if (getSumOfValuables() > 50000)
		{
			if (Util.chance(20))
				message("We are rich!");
			expeditionMorale++;
		}
		// Ship in good health
		if (getMovementMode() == MovementMode.SHIP && getShipHealth() > 95)
		{
			if (Util.chance(20))
				message("Our ships are in good shape.");
			expeditionMorale++;
		}
		// Rum for the sailors
		if (getMovementMode() == MovementMode.SHIP && getItemCountBasic("RUM") > getItemCountBasic("SAILOR") * 5)
		{
			if (Util.chance(20))
				message("Rum for the sailors!");
			expeditionMorale++;
		}
		// Lucky
		if (getPerceivedLuck() > 10)
		{
			if (Util.chance(10))
				message("We feel lucky");
			expeditionMorale++;
		}

		// Temporary Mod
		if (hasCounter("MORALE_UP"))
		{
			expeditionMorale++;
		}

		if (expeditionMorale > 10)
		{
			expeditionMorale = 10;
			return;
		}
		if (expeditionMorale < 0)
		{
			expeditionMorale = 0;
			return;
		}
	}

	public void validateMounted()
	{
		if (movementMode.isLandMovement())
		{
			if (getUnmountedUnits().size() == 0 && getTotalUnits() > 0)
			{
				setMovementMode(MovementMode.HORSE);
			}
			else
			{
				setMovementMode(MovementMode.FOOT);
			}
		}
	}

	public void wearOutShips(int chance, boolean willingly)
	{
		if (getMovementMode() == MovementMode.SHIP)
		{
			// Randomly damage ships
			List<Vehicle> vehicles = getCurrentVehicles();
			List<Vehicle> vehiclesToRemove = new ArrayList<Vehicle>();
			for (Vehicle vehicle : vehicles)
			{
				vehicle.wearOut(getLevel(), chance, willingly);
				if (vehicle.isDestroyed())
				{
					vehiclesToRemove.add(vehicle);
					if (!willingly)
						modifyPerceivedLuck(-3);
				}
			}
			for (Vehicle vehicle : vehiclesToRemove)
			{
				OverworldExpeditionCell cell = (OverworldExpeditionCell) getLevel().getMapCell(getPosition());
				if (cell.isRiver())
				{
					MessengerService.showImportantMessage( getEventDatePreffix() + "We lost " + vehicle.getName() + ".");
				}
				else
				{
					MessengerService.showImportantMessage(getEventDatePreffix() + "We lost " + vehicle.getName() + " to the sea.");
				}
				vehicles.remove(vehicle);
			}

			checkDrown();
		}
	}

	public int getThirstResistance()
	{
		return thirstResistance;
	}

	public void setThirstResistance(int thirstResistance)
	{		
		this.thirstResistance = thirstResistance;
	}

	public int getHungerResistance()
	{
		return hungerResistance;
	}

	public void setHungerResistance(int hungerResistance)
	{	
		this.hungerResistance = hungerResistance;
	}

	public int getMaxHungerResistance()
	{
		return maxHungerResistance;
	}

	public void setMaxHungerResistance(int maxHungerResistance)
	{
		this.maxHungerResistance = maxHungerResistance;
	}

	public int getMaxThirstResistance()
	{
		return maxThirstResistance;
	}

	public void setMaxThirstResistance(int maxThirstResistance)
	{
		this.maxThirstResistance = maxThirstResistance;
	}

	public int getOffShoreWaterDays()
	{
		MovementMode currentMovementMode = getMovementMode();
		setMovementMode(MovementMode.SHIP);
		int ret = getProjectedWaterDays();
		setMovementMode(currentMovementMode);
		return ret;
	}
}
