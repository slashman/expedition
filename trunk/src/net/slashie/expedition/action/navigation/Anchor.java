package net.slashie.expedition.action.navigation;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.world.ExpeditionMacroLevel;
import net.slashie.expedition.world.OverworldExpeditionCell;
import net.slashie.serf.action.Action;
import net.slashie.serf.action.Actor;
import net.slashie.serf.level.AbstractCell;

public class Anchor extends Action{
	private static final long serialVersionUID = 1L;

	@Override
	public void execute() {
		Expedition e = (Expedition)performer;
		if (e.isAnchored()){
			e.getLevel().addMessage("Weigh Anchors!");
			e.setAnchored(false);
		} else {
			e.getLevel().addMessage("Drop Anchors!");
			e.setAnchored(true);
		}
	}

	@Override
	public String getID() {
		return "ResetDeadReckon";
	}
	
	/**
	 * Can only anchor if there's land at least 2 squares nearby 
	 */
	@Override
	public boolean canPerform(Actor a) {
		/*if (true)
			return true;*/
		Expedition e = (Expedition) a;
		if (!(a.getLevel() instanceof ExpeditionMacroLevel)){
			invalidationMessage = "You can't do that here";
			return false;
		}
		if (e.getMovementMode().isLandMovement()){
			invalidationMessage = "You can't do that here";
			return false;
		}
		if (e.isAnchored()){
			return true;
		}
		
		AbstractCell[][] around = e.getEnvironmentAround(2, 2).getCellsAround();
		for (int x = 0; x < around.length; x++){
			for (int y = 0; y < around[0].length; y++){
				if (around[x][y] != null && ((OverworldExpeditionCell)around[x][y]).isLand()){
					return true;
				}
			}
		}
		invalidationMessage = "The sea is too deep around!";
		return false;
	}

}
