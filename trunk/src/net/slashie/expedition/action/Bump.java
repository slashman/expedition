package net.slashie.expedition.action;

import net.slashie.expedition.domain.BattleManager;
import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.NonPrincipalExpedition;
import net.slashie.expedition.domain.Expedition.MovementMode;
import net.slashie.expedition.level.ExpeditionLevelReader;
import net.slashie.expedition.level.GlobeMapModel;
import net.slashie.expedition.world.ExpeditionCell;
import net.slashie.expedition.world.OverworldExpeditionCell;
import net.slashie.serf.action.Action;
import net.slashie.serf.action.Actor;
import net.slashie.serf.level.AbstractCell;
import net.slashie.serf.text.EnglishGrammar;
import net.slashie.serf.ui.ActionCancelException;
import net.slashie.utils.Position;

@SuppressWarnings("serial")
public class Bump extends Action {

	@Override
	public void execute() {
		Expedition expedition = (Expedition) performer;
		if (targetDirection == Action.SELF){
			return;
		}
		Position var = directionToVariation(targetDirection);
		
		if (expedition.getLevel() instanceof ExpeditionLevelReader){
			var = GlobeMapModel.getSingleton().scaleVar(var, expedition.getPosition().y());
		}
        
        Position destinationPoint = Position.add(performer.getPosition(), var);
		
		
        AbstractCell absCell = performer.getLevel().getMapCell(destinationPoint);
        if (absCell instanceof ExpeditionCell){
	        ExpeditionCell cell = (ExpeditionCell)absCell;
	        if (cell == null){
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
   				//Attack!
        		if (targetExpedition.getTotalUnits() > 0){
	        		String message = EnglishGrammar.a(expedition.getDescription())+" "+expedition.getDescription()+" attacks you";
	   				expedition.getLevel().addMessage(message);
	   				BattleManager.battle(message, expedition, targetExpedition);
        		}
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

	@Override
	public int getCost() {
		if (performer instanceof NonPrincipalExpedition)
			return ((NonPrincipalExpedition)performer).getMovementSpeed().getMovementCost();
		else
			return 40;
	}
}
