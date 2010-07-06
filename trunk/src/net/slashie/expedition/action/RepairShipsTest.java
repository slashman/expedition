package net.slashie.expedition.action;

import java.util.ArrayList;
import java.util.List;

import net.slashie.expedition.data.ExpeditionDAO;
import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.ExpeditionItem;
import net.slashie.expedition.domain.Vehicle;
import net.slashie.expedition.domain.Expedition.MovementMode;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.item.ItemFactory;
import net.slashie.serf.action.Action;
import net.slashie.serf.ui.AppearanceFactory;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class RepairShipsTest extends TestCase{
	private Expedition ret;
	
	@Override
	protected void setUp() {
		ItemFactory.init(ExpeditionDAO.getItemDefinitions(AppearanceFactory.getAppearanceFactory()));

		ExpeditionGame game = new ExpeditionGame();
		ret = new Expedition(game);
		ret.setGame(game);
		ret.setAppearanceId("EXPEDITION");
		ret.setName("Colombus");
		ret.setExpeditionary("Colombus");
		ret.setExpeditionaryTitle("Explorer");
		ret.setAccountedGold(700000);
		List<Vehicle> startingShips = new ArrayList<Vehicle>();
		startingShips.add((Vehicle)ItemFactory.createItem("CARRACK"));
		startingShips.add((Vehicle)ItemFactory.createItem("CARRACK"));
		startingShips.add((Vehicle)ItemFactory.createItem("CARRACK"));
		ret.setCurrentVehicles(startingShips);
		
		ExpeditionItem food = ItemFactory.createItem("FOOD");
		ExpeditionItem sailor = ItemFactory.createItem("SAILOR");
		ExpeditionItem captain = ItemFactory.createItem("CAPTAIN");
		ExpeditionItem explorer = ItemFactory.createItem("EXPLORER");
		
		ret.addItemOffshore(sailor, 30);
		ret.addItemOffshore(captain, 3);
		ret.addItemOffshore(explorer, 1);
		ret.addItemOffshore(food, 100);
		
		ret.setMovementMode(MovementMode.SHIP);
	}
	
	public void testExecute(){
		Action a = new RepairShips();
		a.setPerformer(ret);
		a.execute();
		assertEquals(1, ret.getItemCount("WOOD"));
		assertEquals(2, ret.getItemCount("WOOD"));
	}
	
	public static Test suite(){
		return new TestSuite(RepairShipsTest.class);
	}
}
