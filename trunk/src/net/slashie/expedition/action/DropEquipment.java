package net.slashie.expedition.action;

import net.slashie.expedition.domain.GoodsCache;
import net.slashie.expedition.domain.SeaPseudoCache;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.ui.ExpeditionUserInterface;
import net.slashie.expedition.world.ExpeditionMacroLevel;
import net.slashie.expedition.world.OverworldExpeditionCell;
import net.slashie.serf.action.Action;
import net.slashie.serf.action.Actor;
import net.slashie.serf.game.Player;
import net.slashie.serf.ui.UserInterface;

public class DropEquipment extends Action{

	@Override
	public void execute() {
		if (((OverworldExpeditionCell) performer.getLevel().getMapCell(performer.getPosition())).isLand()){
			GoodsCache cache = ((ExpeditionMacroLevel)performer.getLevel()).getOrCreateCache(performer.getPosition());
			((ExpeditionUserInterface)UserInterface.getUI()).transferFromExpedition(cache);
			if (cache.destroyOnEmpty() && cache.getItems().size() == 0)
				performer.getLevel().destroyFeature(cache);
		} else {
			//Drop things into the big sea
			GoodsCache cache = new SeaPseudoCache((ExpeditionGame)((Player)performer).getGame());
			((ExpeditionUserInterface)UserInterface.getUI()).transferFromExpedition(cache);
		}

		
	}

	@Override
	public String getID() {
		return "DropEquipment";
	}
	
	@Override
	public boolean canPerform(Actor a) {
		if (!(a.getLevel() instanceof ExpeditionMacroLevel))
			return false;
		return true;
	}
	
	@Override
	public String getInvalidationMessage() {
		if (!(performer.getLevel() instanceof ExpeditionMacroLevel))
			return "You can't drop your equipment here!";
		return "";
	}
	
	

}
