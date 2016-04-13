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
import net.slashie.expedition.action.RepairShips;
import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.Expedition.MovementMode;
import net.slashie.expedition.domain.ExpeditionItem;
import net.slashie.expedition.domain.Vehicle;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.item.ItemFactory;
import net.slashie.serf.action.Action;

public class TestRepairShips
{
	static private Expedition ret;
	static private ExpeditionItem wood;
	static private ExpeditionItem sailor;
	static private ExpeditionItem carpenter;
	static private Action a;
	final static Logger logger = Logger.getRootLogger();

	static private List<Vehicle> startingShips = new ArrayList<Vehicle>();
	private static ExpeditionItem captain;
	private static ExpeditionItem explorer;
	private static ExpeditionItem food;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		logger.debug("Setting Up Test Case");
		RunExpedition.setMode(DisplayMode.SWING_GFX);
		RunExpedition.readConfiguration();
		RunExpedition.initializeGraphics();
		RunExpedition.initializeData();

		ExpeditionGame game = new ExpeditionGame();
		ret = new Expedition(game);
		a = new RepairShips();

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
		carpenter = ItemFactory.createItem("CARPENTER");

		food = ItemFactory.createItem("BISCUIT");
		wood = ItemFactory.createItem("WOOD");

		ret.addItemOffshore(sailor, 30);
		ret.addItemOffshore(captain, 3);
		ret.addItemOffshore(explorer, 1);
		ret.addItemOffshore(food, 100);

		ret.setMovementMode(MovementMode.SHIP);
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
		// logger.debug("Start: tearDown Arguments: " + null);
		ret.reduceItemOffshore(wood, ret.getItemCount("WOOD"));
		ret.reduceItemOffshore(sailor, ret.getItemCount("SAILOR"));
		ret.reduceItemOffshore(captain, ret.getItemCount("CAPTAIN"));
		ret.reduceItemOffshore(explorer, ret.getItemCount("EXPLORER"));
		ret.reduceItemOffshore(carpenter, ret.getItemCount("CARPENTER"));
		ret.reduceItemOffshore(food, ret.getItemCount("BISCUIT"));
		ret.getCurrentVehicles().get(0).setResistance(100);
	}

	@Test
	public void testRepairNoDamagedShip()
	{
		logger.debug("-----------------------------------------------------------");
		logger.debug("test 1: repair ship without having a damaged ship");
		ret.addItemOffshore(wood, 100);
		ret.addItemOffshore(food, 100);
		ret.addItemOffshore(sailor, 30);
		ret.addItemOffshore(captain, 3);
		ret.addItemOffshore(explorer, 1);
		a.setPerformer(ret);
		a.execute();
		assertEquals(100, ret.getItemCount("WOOD"));
		logger.debug("pass: no wood used up, ships in perfect condition");
	}

	@Test
	public void testRepairDamagedShipNoWood()
	{
		logger.debug("-----------------------------------------------------------");
		logger.debug("test 2: try to repair ship, but there is no wood");
		ret.addItemOffshore(food, 100);
		ret.addItemOffshore(sailor, 30);
		ret.addItemOffshore(captain, 3);
		ret.addItemOffshore(explorer, 1);
		ret.getCurrentVehicles().get(0).setResistance(2);
		a = new RepairShips();
		a.setPerformer(ret);
		a.execute();
		assertEquals(0, ret.getItemCount("WOOD"));
		assertEquals(2, ret.getCurrentVehicles().get(0).getResistance());
		logger.debug("pass: no wood available");
	}

	@Test
	public void testRepairDamagedShipWithWood()
	{
		logger.debug("-----------------------------------------------------------");
		logger.debug("test 3: try to repair ship, there is wood");
		logger.debug("test 3: should be 30 sailors, 30 wood, 3 resistance");
		ret.addItemOffshore(wood, 100);
		ret.addItemOffshore(food, 100);
		ret.addItemOffshore(sailor, 30);
		ret.addItemOffshore(captain, 3);
		ret.addItemOffshore(explorer, 1);
		ret.getCurrentVehicles().get(0).setResistance(2);
		a = new RepairShips();
		a.setPerformer(ret);
		a.execute();
		assertEquals(70, ret.getItemCount("WOOD"));
		assertEquals(5, ret.getCurrentVehicles().get(0).getResistance());
		logger.debug(ret.getItemCount("WOOD"));
		logger.debug(ret.getCurrentVehicles().get(0).getResistance());
		logger.debug("pass: 30 wood spent, ship repaired");
	}

	@Test
	public void testRepairDamagedShipWithWoodCarpentersOnly()
	{
		logger.debug("-----------------------------------------------------------");
		logger.debug("test 4: try to repair ship, there is wood, only carpenters");
		logger.debug("should be 3 carpenters take 30 wood, repairs 3 resistance");
		ret.addItemOffshore(wood, 100);
		ret.addItemOffshore(food, 100);
		ret.addItemOffshore(captain, 3);
		ret.addItemOffshore(explorer, 1);
		ret.addItemOffshore(carpenter, 3);
		ret.getCurrentVehicles().get(0).setResistance(2);
		a = new RepairShips();
		a.setPerformer(ret);
		a.execute();
		assertEquals(5, ret.getCurrentVehicles().get(0).getResistance());
		assertEquals(97, ret.getItemCount("WOOD"));
		logger.debug(ret.getItemCount("WOOD"));
		logger.debug(ret.getCurrentVehicles().get(0).getResistance());
		logger.debug("pass: 3 wood spent, ship repaired");
	}

}
