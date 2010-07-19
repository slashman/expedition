package net.slashie.expedition.ui;

import java.util.ArrayList;
import java.util.List;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.ExpeditionItem;
import net.slashie.expedition.domain.ExpeditionUnit;
import net.slashie.expedition.domain.Good;
import net.slashie.expedition.domain.GoodType;
import net.slashie.expedition.domain.Vehicle;
import net.slashie.expedition.domain.Expedition.MovementMode;
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
		ret.setExpeditionaryTitle("Commoner");
		ret.setAccountedGold(200);
		ExpeditionItem explorer = ItemFactory.createItem("EXPLORER");
		ret.addItem(explorer, 1);
		ret.setMovementMode(MovementMode.FOOT);
		return ret;
	}

	
}
