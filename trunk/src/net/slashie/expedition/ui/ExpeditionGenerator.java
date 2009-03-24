package net.slashie.expedition.ui;

import java.util.ArrayList;
import java.util.List;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.ExpeditionItem;
import net.slashie.expedition.domain.ExpeditionUnit;
import net.slashie.expedition.domain.Good;
import net.slashie.expedition.domain.GoodType;
import net.slashie.expedition.domain.Vehicle;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.item.ItemFactory;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.game.Player;
import net.slashie.serf.game.SworeGame;
import net.slashie.serf.ui.AppearanceFactory;

/**
 * Represents an object delegated with the task of generating an expedition
 * object under user request.
 * 
 * @author Slash
 *
 */
public abstract class ExpeditionGenerator {
	public static ExpeditionGenerator thus;
	/**
	 * Creates an expedition
	 * @return
	 */
	public abstract Expedition createExpedition(ExpeditionGame game);
	
	protected Expedition getExpeditionObject(ExpeditionGame game){
		Expedition ret = new Expedition(game);
		ret.setGame(game);
		ret.setAppearanceId("EXPEDITION");
		ret.setName("Colombus");
		ret.setExpeditionary("Colombus");
		ret.setExpeditionaryTitle("Explorer");
		ret.setAccountedGold(5000);
		List<Equipment> startingShips = new ArrayList<Equipment>();
		startingShips.add(new Equipment(ItemFactory.createItem("CARRACK"), 3));
		ret.setCurrentVehicles(startingShips);
		
		ExpeditionItem food = ItemFactory.createItem("FOOD");
		ExpeditionItem sailor = ItemFactory.createItem("SAILOR");
		ExpeditionItem captain = ItemFactory.createItem("CAPTAIN");
		ExpeditionItem explorer = ItemFactory.createItem("EXPLORER");
		
		ret.addItemOffshore(sailor, 30);
		ret.addItemOffshore(captain, 3);
		ret.addItemOffshore(explorer, 1);
		ret.addItemOffshore(ItemFactory.createItem("SPEARS"), 34);
		
		for (int i = 0; i < 5000; i++){
			ret.addItemOffshore(food, 100);
			if (ret.getOffshoreCurrentlyCarrying()>20)
				break;
		}
		
		
		return ret;
	}

	
}
