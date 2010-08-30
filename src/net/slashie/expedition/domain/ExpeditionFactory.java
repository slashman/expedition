package net.slashie.expedition.domain;

import java.util.ArrayList;

import net.slashie.expedition.action.Bump;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.item.ItemFactory;
import net.slashie.serf.ai.RangedActionSpec;
import net.slashie.serf.ai.SimpleAI;
import net.slashie.serf.ui.AppearanceFactory;
import net.slashie.util.Util;

public class ExpeditionFactory {
	public static Expedition getExpedition(String classifierId, int expeditionPower){
		ExpeditionGame game = ExpeditionGame.getCurrentGame();
		NonPrincipalExpedition ret = new NonPrincipalExpedition(game, "hostileExpedition"+game.getLastExpeditionId());
		ret.setGame(game);
		ret.setAppearanceId("HOSTILE_EXPEDITION");
		ret.setName("natives");
		ret.setExpeditionary("-");
		
		SimpleAI ai = new SimpleAI(game.getPlayer(), new Bump()) ;
		ret.setSelector(ai);
		
		//int expeditionPower = Util.rand(1, 4);

		ret.addItem(ItemFactory.createItem("NATIVE_WARRIOR"), Util.rand(10, expeditionPower*10));
		ret.addItem(ItemFactory.createItem("NATIVE_BRAVE"), Util.rand(0, expeditionPower*5));
		ret.addItem(ItemFactory.createItem("ARROWS"), Util.rand(0, expeditionPower*30));
		ret.addItem(ItemFactory.createItem("NATIVE_ARCHER"), Util.rand(0, expeditionPower*10));
		ret.addItem(ItemFactory.createItem("GOLD_NUGGET"), Util.rand(0, expeditionPower*5));
		ret.addItem(ItemFactory.createItem("GOLD_BRACELET"), Util.rand(0, expeditionPower*7));
		ret.addItem(ItemFactory.createItem("NATIVE_ARTIFACT"), Util.rand(0, expeditionPower*10));
		ret.addItem(ItemFactory.createItem("NATIVE_FOOD"), Util.rand(expeditionPower*100, expeditionPower*700));
		
		ret.calculateInitialPower();
		return ret;
	}
}
