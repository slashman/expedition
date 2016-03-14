package net.ck.expedition.model.test;
import static org.junit.Assert.*;
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
import net.slashie.expedition.domain.ExpeditionUnit;
import net.slashie.expedition.domain.Food;
import net.slashie.expedition.domain.Town;
import net.slashie.expedition.domain.Vehicle;
import net.slashie.expedition.domain.Water;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.item.ItemFactory;
import net.slashie.expedition.level.FlatMapModelSeconds;
import net.slashie.expedition.level.GlobeMapModel;
import net.slashie.expedition.locations.World;
import net.slashie.expedition.town.Farm;
import net.slashie.expedition.town.House;
import net.slashie.expedition.town.Plaza;
import net.slashie.expedition.town.Storage;
import net.slashie.expedition.world.ExpeditionMacroLevel;
import net.slashie.serf.action.Action;
import net.slashie.serf.levelGeneration.StaticPattern;
import net.slashie.utils.Position;
public class TestColonyCreation
{
	final static Logger logger = Logger.getRootLogger();
	static private Expedition ret;
	static private ExpeditionItem wood;
	static private ExpeditionItem sailor;
	static private ExpeditionItem carpenter;
	static private Action a;

	private static List<Vehicle> startingShips = new ArrayList<Vehicle>();
	private static ExpeditionItem captain;
	private static ExpeditionUnit explorer;
	private static Food food;
	private static Water water;
	private static ExpeditionMacroLevel level;
	private static ExpeditionGame game;
	
	private static Plaza plaza;
	private static House house;
	private static Storage storage;
	private static Farm farm;
	

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		logger.debug("Setting Up Test Case");
		RunExpedition.setMode(DisplayMode.SWING_GFX);
		RunExpedition.readConfiguration();
		RunExpedition.initializeGraphics();
		RunExpedition.initializeData();

		game = new ExpeditionGame();
		game.setGameTime(3, 8, 1492, 13, 0);
		ret = new Expedition(game);
		ExpeditionGame.setCurrentGame(game);
		a = new ForageFood();
		ret.setGame(game);
		ret.setAppearanceId("EXPEDITION");
		ret.setName("Colombus");
		logger.debug("base governance for real: " + (ret.getMorale() * 10));
		ret.setExpeditionary("Colombus");
		ret.getTitle().grantTitle(Expedition.Title.EXPLORER, "of Spain");
		ret.setAccountedGold(700000);

		startingShips.add((Vehicle) ItemFactory.createItem("CARRACK"));
		startingShips.add((Vehicle) ItemFactory.createItem("CARRACK"));
		startingShips.add((Vehicle) ItemFactory.createItem("CARRACK"));
		ret.setCurrentVehicles(startingShips);

		sailor = ItemFactory.createItem("SAILOR");
		captain = ItemFactory.createItem("CAPTAIN");
		explorer = ItemFactory.createUnit("EXPLORER", "");

		food = (Food) ItemFactory.createItem("BISCUIT");
		water = (Water) ItemFactory.createItem("FRESHWATER");
		wood = ItemFactory.createItem("WOOD");
		
		house = new House();
		plaza = new Plaza();
		storage = new Storage();
		farm = new Farm();
		
		ret.setMovementMode(MovementMode.FOOT);

		StaticPattern pattern = new World();
		GlobeMapModel.setSingleton(new FlatMapModelSeconds());
		level = new ExpeditionMacroLevel("scenarios/theNewWorld/world", 3374, 2939, 50, 50, pattern.getCharMap(),
				new Position(-427, 2235), GlobeMapModel.getSingleton());
		ret.setLevel(level);
		ret.setPosition(new Position(-269820, 39600, 0));		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
	}

	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testHousingCapacity()
	{
		logger.debug("test for housing capacity: 3 houses, 50% moral BEGIN");
		Town town = new Town(game,ret);
		town.setPosition(new Position(-269820, 39600, 0));
		town.setLevel(level);
		town.setBFC();
				
		town.addBuilding(plaza);
		town.addBuilding(house);
		town.addBuilding(house);
		town.addBuilding(house);
		town.addBuilding(farm);
		town.addBuilding(farm);
		town.addItem("WOOD", 50);
		assertEquals(15, town.getLodgingCapacity());
		
		town.addUnits(explorer, 51);
		logger.debug("total units in town:" + town.getTotalUnits());
		assertEquals(30, town.getTotalUnits());
		
		logger.debug(ExpeditionGame.getCurrentGame().getGameTime().getTime());
		//town.tryGrowing();
		//town.gatherResources();
				
		//assertEquals(10, town.getItemCount("WOOD"));
		logger.debug("test for housing capacity: 3 houses, 50% moral END");
		town.act();
		a.setPerformer(ret);
		game.resume();
		logger.debug(ExpeditionGame.getCurrentGame().getGameTime().getTime());
	}
}

