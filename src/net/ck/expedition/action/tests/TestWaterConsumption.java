package net.ck.expedition.action.tests;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import net.slashie.expedition.DisplayMode;
import net.slashie.expedition.RunExpedition;
import net.slashie.expedition.action.ForageFood;
import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.Expedition.MovementMode;
import net.slashie.expedition.domain.ExpeditionItem;
import net.slashie.expedition.domain.Food;
import net.slashie.expedition.domain.Vehicle;
import net.slashie.expedition.domain.Water;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.item.ItemFactory;
import net.slashie.expedition.level.FlatMapModelSeconds;
import net.slashie.expedition.level.GlobeMapModel;
import net.slashie.expedition.locations.World;
import net.slashie.expedition.world.ExpeditionMacroLevel;
import net.slashie.serf.action.Action;
import net.slashie.serf.levelGeneration.StaticPattern;
import net.slashie.utils.Position;

public class TestWaterConsumption
{
	final static Logger logger = Logger.getRootLogger();
	static private Expedition ret;
	static private ExpeditionItem wood;
	static private ExpeditionItem sailor;
	static private ExpeditionItem carpenter;
	static private Action a;

	private static List<Vehicle> startingShips = new ArrayList<Vehicle>();
	private static ExpeditionItem captain;
	private static ExpeditionItem explorer;
	private static Food food;
	private static Water water;
	private static ExpeditionMacroLevel level;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		logger.debug("Setting Up Test Case");
		RunExpedition.setMode(DisplayMode.SWING_GFX);
		RunExpedition.readConfiguration();
		RunExpedition.initializeGraphics();
		RunExpedition.initializeData();

		ExpeditionGame game = new ExpeditionGame();
		game.setGameTime(3, 8, 1492, 13, 0);
		ret = new Expedition(game);

		ret.setGame(game);
		ret.setAppearanceId("EXPEDITION");
		ret.setName("Colombus");
		ret.setExpeditionary("Colombus");
		ret.getTitle().grantTitle(Expedition.Title.EXPLORER, "of Spain");
		ret.setAccountedGold(700000);

		startingShips.add((Vehicle) ItemFactory.createItem("CARRACK"));
		startingShips.add((Vehicle) ItemFactory.createItem("CARRACK"));
		startingShips.add((Vehicle) ItemFactory.createItem("CARRACK"));
		ret.setCurrentVehicles(startingShips);

		sailor = ItemFactory.createItem("SAILOR");
		captain = ItemFactory.createItem("CAPTAIN");
		explorer = ItemFactory.createItem("EXPLORER");

		food = (Food) ItemFactory.createItem("BISCUIT");
		water = (Water) ItemFactory.createItem("FRESHWATER");

		// ret.addItemOffshore(sailor, 30);
		// ret.addItemOffshore(captain, 3);

		ret.setMovementMode(MovementMode.FOOT);

		StaticPattern pattern = new World();
		GlobeMapModel.setSingleton(new FlatMapModelSeconds());
		level = new ExpeditionMacroLevel("scenarios/theNewWorld/world", 3374, 2939, 50, 50, pattern.getCharMap(),
				new Position(-427, 2235), GlobeMapModel.getSingleton());
		ret.setLevel(level);				
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
	}

	@Before
	public void setUp() throws Exception
	{
		ret.addItemOffshore(explorer, 1);
		ret.setThirstResistance(ret.getMaxThirstResistance());
		ret.setHungerResistance(ret.getMaxHungerResistance());
	}

	@After
	public void tearDown() throws Exception
	{
		ret.reduceItemOffshore(food, 100);
		ret.reduceItemOffshore(water, 100);
		ret.reduceItemOffshore(explorer, 1);
	}

	@Test
	public void testWithFoodAndWater()
	{
		logger.debug("test: 1 explorer, 100 food, 100 water BEGIN");
		ret.addItemOffshore(food, 100);
		ret.addItemOffshore(water, 100);
		logger.debug("food: " + ret.getCurrentFood());
		logger.debug("daily food consumption: " + ret.getDailyFoodConsumption());
		logger.debug("water: " + ret.getCurrentWater());
		logger.debug("daily water consumption: " + ret.getDailyWaterConsumption());
		ret.consumeFood();
		ret.consumeWater();
		assertEquals(97, ret.getCurrentFood());
		assertEquals(97, ret.getCurrentWater());
		logger.debug("food: " + ret.getCurrentFood());
		logger.debug("water: " + ret.getCurrentWater());
		logger.debug("test: 1 explorer, 100 food, 100 water END");
		logger.debug("==========================================");
	}
	
	@Test
	public void testWithProjectedFoodAndProjectedWater()
	{
		logger.debug("test: 1 explorer, 100 food, 100 water Projected vs normal vs offshore BEGIN");
		ret.addItemOffshore(food, 100);
		ret.addItemOffshore(water, 100);
		ret.setMovementMode(MovementMode.SHIP);
		
		/*
		logger.debug("food: " + ret.getCurrentFood());
		logger.debug("daily food consumption: " + ret.getDailyFoodConsumption());
		logger.debug("food days: " + ret.getFoodDays());
		logger.debug("projected food days: " + ret.getProjectedFoodDays());
		logger.debug("offshore food days: " + ret.getOffshoreFoodDays());
		logger.debug("water: " + ret.getCurrentWater());
		logger.debug("daily water consumption: " + ret.getDailyWaterConsumption());
		logger.debug("water days: " + ret.getWaterDays());
		logger.debug("projected water days: " + ret.getProjectedWaterDays());
		logger.debug("offshore water days: " + ret.getOffshoreWaterDays());
		*/
		assertEquals(33, ret.getFoodDays());
		assertEquals(33, ret.getProjectedFoodDays());
		assertEquals(33, ret.getOffshoreFoodDays());
		
		assertEquals(33, ret.getWaterDays());
		assertEquals(33, ret.getProjectedWaterDays());
		assertEquals(33, ret.getOffShoreWaterDays());
		
		logger.debug("test: 1 explorer, 100 food, 100 water Projected vs normal vs offshore END");
		logger.debug("==========================================");
	}
	
	
	@Test
	public void testFoodAndWaterConsumptionTemperatureZones()
	{
		logger.debug("test: 1 explorer, 100 food, 100 water temperature zones BEGIN");
		ret.addItemOffshore(food, 100);
		ret.addItemOffshore(water, 100);
		ret.setMovementMode(MovementMode.SHIP);

		logger.debug("temperature: " + level.getTemperature());
		
		logger.debug("test: 1 explorer, 100 food, 100 water temperature zones END");
		logger.debug("==========================================");
	}
	
	
	

	@Test
	public void testWithOutFood()
	{
		int i = 8;
		logger.debug("test: 1 explorer, 0 food, 100 water, " + i + " times BEGIN");
		ret.addItemOffshore(water, 100);
		logger.debug("hunger resistance: " + ret.getHungerResistance());
		logger.debug("thirst resistance: " + ret.getThirstResistance());
		logger.debug("food: " + ret.getCurrentFood());

		logger.debug("water: " + ret.getCurrentWater());
		for (int j = 0; j < i; j++)
		{
			ret.consumeFood();
			ret.consumeWater();
		}
		logger.debug("food: " + ret.getCurrentFood());
		logger.debug("water: " + ret.getCurrentWater());
		logger.debug("hunger resistance: " + ret.getHungerResistance());
		logger.debug("thirst resistance: " + ret.getThirstResistance());
		assertEquals(5, ret.getThirstResistance());
		assertEquals(0, ret.getHungerResistance());
		// only 6*3 water used, since explorer is dead by the 7th call
		assertEquals(82, ret.getCurrentWater());
		logger.debug("test: 1 explorer, 0 food, 100 water, " + i + " times END");
		logger.debug("==========================================");
	}

	@Test
	public void testWithOutWater()
	{
		int i = 8;
		logger.debug("test: 1 explorer, 100 food, 0 water, " + i + " times BEGIN");
		ret.addItemOffshore(food, 100);
		logger.debug("hunger resistance: " + ret.getHungerResistance());
		logger.debug("thirst resistance: " + ret.getThirstResistance());
		logger.debug("food: " + ret.getCurrentFood());
		logger.debug("water: " + ret.getCurrentWater());
		for (int j = 0; j < i; j++)
		{
			ret.consumeFood();
			ret.consumeWater();
		}
		logger.debug("hunger resistance: " + ret.getHungerResistance());
		logger.debug("thirst resistance: " + ret.getThirstResistance());
		assertEquals(0, ret.getThirstResistance());
		assertEquals(7, ret.getHungerResistance());
		// only 5*3 food used, since explorer is dead by the 6th call
		assertEquals(85, ret.getCurrentFood());
		logger.debug("test: 1 explorer, 100 food, 0 water, " + i + " times  END");
		logger.debug("==========================================");
	}

}
