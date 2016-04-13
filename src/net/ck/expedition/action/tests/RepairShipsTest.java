package net.ck.expedition.action.tests;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
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

public class RepairShipsTest extends TestCase
{
	private Expedition ret;
	private ExpeditionItem wood;
	private Action a;
	final static Logger logger = Logger.getRootLogger();

	List<Vehicle> startingShips = new ArrayList<Vehicle>();

	@Deprecated
	protected void setUp()
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

		ExpeditionItem food = ItemFactory.createItem("BISCUIT");
		ExpeditionItem sailor = ItemFactory.createItem("SAILOR");
		ExpeditionItem captain = ItemFactory.createItem("CAPTAIN");
		ExpeditionItem explorer = ItemFactory.createItem("EXPLORER");
		wood = ItemFactory.createItem("WOOD");

		ret.addItemOffshore(sailor, 30);
		ret.addItemOffshore(captain, 3);
		ret.addItemOffshore(explorer, 1);
		ret.addItemOffshore(food, 100);
		ret.addItemOffshore(wood, 100);

		ret.setMovementMode(MovementMode.SHIP);
	}
	@Deprecated
	public void testRepairNoDamagedShip()
	{
		logger.debug("test 1: repair ship without having a damaged ship");

		a.setPerformer(ret);
		a.execute();
		assertEquals(100, ret.getItemCount("WOOD"));
		logger.debug("pass: no wood used up, ships in perfect condition");
	}
	@Deprecated
	public void testRepairDamagedShipNoWood()
	{
		logger.debug("test 2: try to repair ship, but there is no wood");
		ret.reduceItemOffshore(wood, 100);
		ret.getCurrentVehicles().get(0).setResistance(2);
		// logger.debug(ret.getCurrentVehicles().get(0).getResistance());
		a = new RepairShips();
		a.setPerformer(ret);
		a.execute();
		assertEquals(0, ret.getItemCount("WOOD"));
		logger.debug("pass: no wood available");
	}
	@Deprecated
	public void testRepairDamagedShipWithWood()
	{
		logger.debug("test 3: try to repair ship, there is wood");
		ret.addItemOffshore(wood, 100);
		a = new RepairShips();
		a.setPerformer(ret);
		a.execute();
		assertEquals(70, ret.getItemCount("WOOD"));
		// logger.debug(ret.getItemCount("WOOD"));
		logger.debug("pass: 30 wood spent, ship repaired");
		logger.debug(ret.getCurrentVehicles().get(0).getResistance());
	}
	@Deprecated
	public void tearDown()
	{
		ret = null;
		wood = null;
		a = null;
	}
	@Deprecated
	public static Test suite()
	{
		return new TestSuite(RepairShipsTest.class);
	}
}
