package net.slashie.expedition.domain;

import java.util.ArrayList;

import net.slashie.expedition.action.NPWalk;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.item.ItemFactory;
import net.slashie.serf.ai.RangedActionSpec;
import net.slashie.serf.ai.SimpleAI;
import net.slashie.serf.ui.AppearanceFactory;
import net.slashie.util.Util;

public class ExpeditionFactory {
	public static Expedition getExpedition(String classifierId){
		ExpeditionGame game = ExpeditionGame.getCurrentGame();
		NonPrincipalExpedition ret = new NonPrincipalExpedition(game, "hostileExpedition"+game.getLastExpeditionId());
		ret.setGame(game);
		ret.setAppearanceId("HOSTILE_EXPEDITION");
		ret.setName("natives");
		ret.setExpeditionary("-");
		ret.setExpeditionaryTitle("-");
		
		SimpleAI ai = new SimpleAI(game.getPlayer(), new NPWalk()) ;
		ArrayList<RangedActionSpec> rangedActions = new ArrayList<RangedActionSpec>();
		rangedActions.add(new RangedActionSpec("NP_RAINARROWS", 3,80,"directionalmissile","rainArrows"));
		ai.setRangedActions(rangedActions);
		ai.setWaitPlayerRange(20);
		ret.setSelector(ai);

		ret.addItem(ItemFactory.createItem("NATIVE_WARRIOR"), Util.rand(10, 70));
		ret.addItem(ItemFactory.createItem("ARROWS"), Util.rand(30, 60));
		ret.addItem(ItemFactory.createItem("NATIVE_ARCHER"), Util.rand(15, 45));

		ret.calculateInitialPower();
		return ret;
	}
}
