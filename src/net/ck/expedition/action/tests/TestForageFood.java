package net.ck.expedition.action.tests;
import static org.junit.Assert.assertEquals;
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
import net.slashie.expedition.domain.Vehicle;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.item.ItemFactory;
import net.slashie.expedition.level.FlatMapModelSeconds;
import net.slashie.expedition.level.GlobeMapModel;
import net.slashie.expedition.locations.World;
import net.slashie.expedition.world.ExpeditionMacroLevel;
import net.slashie.expedition.world.OverworldExpeditionCell;
import net.slashie.expedition.world.Weather;
import net.slashie.serf.action.Action;
import net.slashie.serf.levelGeneration.StaticPattern;
import net.slashie.utils.Position;

public class TestForageFood
{
	static private Expedition ret;
	static private ExpeditionItem sailor;
	static private Action a;
	final static Logger logger = Logger.getRootLogger();

	static private List<Vehicle> startingShips = new ArrayList<Vehicle>();
	private static ExpeditionItem captain;
	private static ExpeditionItem explorer;
	private static ExpeditionItem food;
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
		a = new ForageFood();

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

		food = ItemFactory.createItem("BISCUIT");

		ret.addItemOffshore(sailor, 30);
		ret.addItemOffshore(captain, 3);
		ret.addItemOffshore(explorer, 1);
		ret.addItemOffshore(food, 100);

		ret.setMovementMode(MovementMode.SHIP);

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
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testClear()
	{
		ExpeditionMacroLevel level = (ExpeditionMacroLevel) ret.getLocation();
		level.setPlayer(ret);
		ret.setPosition(new Position(0, 0, 0));
		level.setWeather(Weather.CLEAR);
		OverworldExpeditionCell cell = (OverworldExpeditionCell) ret.getLevel().getMapCell(new Position(0, 0, 0));
		logger.debug("current cell " + cell.getShortDescription());
		a.setPerformer(ret);
		a.execute(100);
	}

	@Test
	public void testFog()
	{
		ExpeditionMacroLevel level = (ExpeditionMacroLevel) ret.getLocation();
		level.setPlayer(ret);
		ret.setPosition(new Position(0, 0, 0));
		level.setWeather(Weather.FOG);
		OverworldExpeditionCell cell = (OverworldExpeditionCell) ret.getLevel().getMapCell(new Position(0, 0, 0));
		logger.debug("current cell " + cell.getShortDescription());
		a.setPerformer(ret);
		a.execute(100);
	}

	@Test
	public void testCloudy()
	{
		ExpeditionMacroLevel level = (ExpeditionMacroLevel) ret.getLocation();
		level.setPlayer(ret);
		ret.setPosition(new Position(0, 0, 0));
		level.setWeather(Weather.CLOUDY);
		OverworldExpeditionCell cell = (OverworldExpeditionCell) ret.getLevel().getMapCell(new Position(0, 0, 0));
		logger.debug("current cell " + cell.getShortDescription());
		a.setPerformer(ret);
		a.execute(100);
	}

	@Test
	public void testDustStorm()
	{
		ExpeditionMacroLevel level = (ExpeditionMacroLevel) ret.getLocation();
		level.setPlayer(ret);
		ret.setPosition(new Position(0, 0, 0));
		level.setWeather(Weather.DUST_STORM);
		OverworldExpeditionCell cell = (OverworldExpeditionCell) ret.getLevel().getMapCell(new Position(0, 0, 0));
		logger.debug("current cell " + cell.getShortDescription());
		a.setPerformer(ret);
		a.execute(100);
	}

	@Test
	public void testGaleWind()
	{
		ExpeditionMacroLevel level = (ExpeditionMacroLevel) ret.getLocation();
		level.setPlayer(ret);
		ret.setPosition(new Position(0, 0, 0));
		level.setWeather(Weather.GALE_WIND);
		OverworldExpeditionCell cell = (OverworldExpeditionCell) ret.getLevel().getMapCell(new Position(0, 0, 0));
		logger.debug("current cell " + cell.getShortDescription());
		a.setPerformer(ret);
		a.execute(100);
	}

	@Test
	public void testHurricane()
	{
		ExpeditionMacroLevel level = (ExpeditionMacroLevel) ret.getLocation();
		level.setPlayer(ret);
		ret.setPosition(new Position(0, 0, 0));
		level.setWeather(Weather.HURRICANE);
		OverworldExpeditionCell cell = (OverworldExpeditionCell) ret.getLevel().getMapCell(new Position(0, 0, 0));
		logger.debug("current cell " + cell.getShortDescription());
		a.setPerformer(ret);
		a.execute(100);
	}

	@Test
	public void testRain()
	{
		ExpeditionMacroLevel level = (ExpeditionMacroLevel) ret.getLocation();
		level.setPlayer(ret);
		ret.setPosition(new Position(0, 0, 0));
		level.setWeather(Weather.RAIN);
		OverworldExpeditionCell cell = (OverworldExpeditionCell) ret.getLevel().getMapCell(new Position(0, 0, 0));
		logger.debug("current cell " + cell.getShortDescription());
		a.setPerformer(ret);
		a.execute(100);
	}

	@Test
	public void testSnow()
	{
		ExpeditionMacroLevel level = (ExpeditionMacroLevel) ret.getLocation();
		level.setPlayer(ret);
		ret.setPosition(new Position(0, 0, 0));
		level.setWeather(Weather.SNOW);
		OverworldExpeditionCell cell = (OverworldExpeditionCell) ret.getLevel().getMapCell(new Position(0, 0, 0));
		logger.debug("current cell " + cell.getShortDescription());
		a.setPerformer(ret);
		a.execute(100);
	}

	@Test
	public void testStorm()
	{
		// TODO: select certain fields from the map, play with all terrain types
		// and all weather conditions
		ExpeditionMacroLevel level = (ExpeditionMacroLevel) ret.getLocation();
		level.setPlayer(ret);
		ret.setPosition(new Position(0, 0, 0));
		level.setWeather(Weather.STORM);
		OverworldExpeditionCell cell = (OverworldExpeditionCell) ret.getLevel().getMapCell(new Position(0, 0, 0));
		logger.debug("current cell " + cell.getShortDescription());
		a.setPerformer(ret);
		a.execute(100);
	}

	@Test
	public void windy()
	{
		ExpeditionMacroLevel level = (ExpeditionMacroLevel) ret.getLocation();
		level.setPlayer(ret);
		ret.setPosition(new Position(0, 0, 0));
		level.setWeather(Weather.WINDY);
		OverworldExpeditionCell cell = (OverworldExpeditionCell) ret.getLevel().getMapCell(new Position(0, 0, 0));
		logger.debug(level.getWeather().toString() + " " + "current cell " + cell.getShortDescription());
		a.setPerformer(ret);
		a.execute(100);
		int multiplier = (int) Math.ceil(ret.getItemCount("SAILOR") / 12.0d);
		logger.debug("forage quantity for deep sea: " + cell.getForageQuantity());
		assertEquals(cell.getForageQuantity() * multiplier, 30);
	}

	@Test
	public void testWindyForest()
	{

		level.setPlayer(ret);
		ret.setPosition(new Position(-261900, 42300, 0));
		level.setWeather(Weather.WINDY);
		OverworldExpeditionCell cell = (OverworldExpeditionCell) ret.getLevel().getMapCell(ret.getPosition());
		int multiplier = (int) Math.ceil(ret.getTotalUnits() / 10.0d);
		logger.debug("test for land, current cell " + cell.getShortDescription());
		logger.debug("total units: " + ret.getTotalUnits());
		logger.debug("forage quantity for forest: " + cell.getForageQuantity());
		logger.debug("multiplier: " + multiplier);
		a.setPerformer(ret);
		a.execute(100);
		assertEquals(multiplier * cell.getForageQuantity(), 80);
	}

	@Test
	public void testWindyGrass()
	{

		level.setPlayer(ret);
		ret.setPosition(new Position(-269820, 39600, 0));
		level.setWeather(Weather.WINDY);
		OverworldExpeditionCell cell = (OverworldExpeditionCell) ret.getLevel().getMapCell(ret.getPosition());
		int multiplier = (int) Math.ceil(ret.getTotalUnits() / 10.0d);
		logger.debug("test for land, current cell " + cell.getShortDescription());
		logger.debug("total units: " + ret.getTotalUnits());
		logger.debug("forage quantity for grass: " + cell.getForageQuantity());
		logger.debug("multiplier: " + multiplier);
		a.setPerformer(ret);
		a.execute(100);
		assertEquals(multiplier * cell.getForageQuantity(), 80);
	}
}
