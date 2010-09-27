package net.slashie.expedition.ui;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.ExpeditionItem;
import net.slashie.expedition.domain.ExpeditionUnit;
import net.slashie.expedition.domain.Expedition.MovementMode;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.item.ItemFactory;


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
		ret.setName("Colón");
		ret.setExpeditionary("Colón");
		ret.setAccountedGold(200);
		ExpeditionUnit explorer = (ExpeditionUnit)ItemFactory.createItem("COLOMBUS");
		ret.setLeaderUnit(explorer);
		//ret.addItem(explorer, 1);
		
		ret.setMovementMode(MovementMode.FOOT);
		return ret;
	}

	
}
