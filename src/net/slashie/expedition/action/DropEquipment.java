package net.slashie.expedition.action;

import net.slashie.expedition.domain.GoodsCache;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.ui.ExpeditionUserInterface;
import net.slashie.expedition.world.ExpeditionMacroLevel;
import net.slashie.expedition.world.OverworldExpeditionCell;
import net.slashie.serf.action.Action;
import net.slashie.serf.action.Actor;
import net.slashie.serf.game.Player;
import net.slashie.serf.ui.UserInterface;
import net.slashie.utils.Position;

public class BuildCache extends Action{

	@Override
	public void execute() {
		
		GoodsCache cache = new GoodsCache((ExpeditionGame)((Player)performer).getGame());
		cache.setPosition(new Position(performer.getPosition()));
		((ExpeditionUserInterface)UserInterface.getUI()).transferFromExpedition(cache);
		performer.getLevel().addFeature(cache);
	}

	@Override
	public String getID() {
		return "BuildCache";
	}
	
	@Override
	public boolean canPerform(Actor a) {
		if (!(a.getLevel() instanceof ExpeditionMacroLevel))
			return false;
		if (!((OverworldExpeditionCell) a.getLevel().getMapCell(a.getPosition())).isLand())
			return false;
		return true;
	}
	
	@Override
	public String getInvalidationMessage() {
		if (!(performer.getLevel() instanceof ExpeditionMacroLevel))
			return "You can't build a caché here!";
		if (!((OverworldExpeditionCell) performer.getLevel().getMapCell(performer.getPosition())).isLand())
			return "You can't build a caché here!";
		return "";
	}
	
	

}
