package net.slashie.expedition.action;

import java.util.ArrayList;
import java.util.List;

import net.slashie.expedition.domain.BattleManager;
import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.ExpeditionUnit;
import net.slashie.expedition.domain.GoodsCache;
import net.slashie.expedition.domain.NonPrincipalExpedition;
import net.slashie.expedition.domain.ShipCache;
import net.slashie.expedition.domain.Expedition.MovementMode;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.ui.ExpeditionUserInterface;
import net.slashie.expedition.world.ExpeditionCell;
import net.slashie.expedition.world.OverworldExpeditionCell;
import net.slashie.serf.action.Action;
import net.slashie.serf.action.Actor;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.game.Player;
import net.slashie.serf.level.AbstractCell;
import net.slashie.serf.level.AbstractFeature;
import net.slashie.serf.ui.ActionCancelException;
import net.slashie.serf.ui.UserInterface;
import net.slashie.utils.Position;
import net.slashie.utils.Util;

public class Bump extends Action {

	@Override
	public void execute() {
		Expedition expedition = (Expedition) performer;
		if (targetDirection == Action.SELF){
			return;
		}
        Position var = directionToVariation(targetDirection);
        Position destinationPoint = Position.add(performer.getPosition(), var);
        
        AbstractCell absCell = performer.getLevel().getMapCell(destinationPoint);
        if (absCell instanceof ExpeditionCell){
	        ExpeditionCell cell = (ExpeditionCell)absCell;
	        if (cell == null){
	        	return;
	        }
	        
	        if (cell.getStore() != null){
	        	return;
	        }
        } else {
	        OverworldExpeditionCell cell = (OverworldExpeditionCell)absCell;
	        if (cell == null){
	        	return;
	        }
	        if (cell.isSolid())
	        	return;
	        
	        if (!cell.isLand()&& !(expedition.getMovementMode() == MovementMode.SHIP)){
	            return;
	        }
        }
        Actor actor = performer.getLevel().getActorAt(destinationPoint);
        if (actor != null){
        	if (actor instanceof Expedition && !(actor instanceof NonPrincipalExpedition)){
        		Expedition targetExpedition = (Expedition) actor;
        		targetExpedition.setJustAttacked(true);
   				//Attack!
   				expedition.getLevel().addMessage("The "+expedition.getDescription()+" attack");
   				BattleManager.battle("The "+expedition.getDescription()+" attack: ", expedition, targetExpedition);
   				return;
        	}
        } else {
        	try {
				expedition.landOn(destinationPoint);
			} catch (ActionCancelException e) {
				
			}
        }
	}

	@Override
	public String getID() {
		return "Bump";
	}

}
