package net.slashie.expedition.action;

import java.util.List;

import net.slashie.expedition.action.navigation.TurnShip;
import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.SailingPoint;
import net.slashie.expedition.domain.ShipCache;
import net.slashie.expedition.domain.Expedition.MovementMode;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.level.ExpeditionLevelReader;
import net.slashie.expedition.world.CardinalDirection;
import net.slashie.expedition.world.ExpeditionCell;
import net.slashie.expedition.world.ExpeditionLevel;
import net.slashie.expedition.world.ExpeditionMicroLevel;
import net.slashie.expedition.world.OverworldExpeditionCell;
import net.slashie.serf.action.Action;
import net.slashie.serf.action.Actor;
import net.slashie.serf.level.AbstractCell;
import net.slashie.serf.level.AbstractFeature;
import net.slashie.serf.ui.ActionCancelException;
import net.slashie.utils.Position;
import net.slashie.utils.Util;

public class Walk extends Action{
	private boolean actionCancelled = false;
	
	/**
	 * Inverted Up and Down since Y now means Lat which grows inversely 
	 * @param code
	 * @return
	 */
	public static Position directionToVariation(int code){
		switch (code){
			case UP:
			return VARDN;
			case DOWN:
			return VARUP;
			case LEFT:
			return VARLF;
			case RIGHT:
			return VARRG;
			case UPRIGHT:
			return VARDR;
			case UPLEFT:
			return VARDL;
			case DOWNRIGHT:
			return VARUR;
			case DOWNLEFT:
			return VARUL;
			case SELF:
			return VARSL;
			default:
			return null;
		}
	}
	
	@Override
	public boolean canPerform(Actor a) {
		Expedition expedition = (Expedition) a;
		int scale = 1;
		if (expedition.getLevel() instanceof ExpeditionLevelReader){
			scale = ExpeditionLevelReader.getLongitudeScale(expedition.getPosition().y());;
		}
		
		//if (expedition.getLevel() instanceof ExpeditionMicroLevel && ((ExpeditionMicroLevel)expedition.getLevel()).isDock()){
		if (expedition.getLevel() instanceof ExpeditionMicroLevel ){
			
			/*if (expedition.getOffshoreCurrentlyCarrying() > 100){
				invalidationMessage = "You are stranded! drop some items!";
				return false;
			}*/
		} else {
			if (expedition.getCurrentlyCarrying() > 100){
				invalidationMessage = "You are stranded! drop some items!";
				return false;
			}
		}
		
        Position var = null;

        if (expedition.getLevel() instanceof ExpeditionLevelReader){
			var = directionToVariation(targetDirection);
		} else {
			var = Action.directionToVariation(targetDirection);
		}
        
        if (expedition.getMovementMode() == MovementMode.SHIP){
			
			//Don't walk, sail instead!
			if (var.x() == 0){
				var = expedition.getHeading().getVectors();
			} else {
				return true;
			}
		}
        
        var = Position.mul(var, scale);
        
        Position destinationPoint = Position.add(a.getPosition(), var);
        
    	Actor actor = expedition.getLevel().getActorAt(destinationPoint);
    	/*if (actor != null){
    		invalidationMessage = "You can't walk there";
    		return false;
    	}*/
    	
        AbstractCell absCell = a.getLevel().getMapCell(destinationPoint);
        if (absCell instanceof ExpeditionCell){
	        ExpeditionCell cell = (ExpeditionCell)absCell;
	        if (cell.isSolid() || cell.isWater()){
	        	invalidationMessage = "You can't go there";
	        	return false;
	        }
        } else {
	        OverworldExpeditionCell cell = (OverworldExpeditionCell)absCell;
	        
	        if (cell == null){
	        	invalidationMessage = "You can't go there";
	        	return false;
	        }
	        
	        if (!cell.isLand()){
	        	if (expedition.getMovementMode() != MovementMode.SHIP){
		        	//Trying to walk into the water
		        	List<AbstractFeature> features =expedition.getLevel().getFeaturesAt(destinationPoint);
		        	if (features != null){
			        	for (AbstractFeature feature: features){
			        		if (feature instanceof ShipCache){
			        			break;
			        		}
				            invalidationMessage = "You can't go there";
					        return false;
			        	}
		        	} else {
		        		invalidationMessage = "You can't go there";
		        		return false;
		        	}
	        	}
	        }
	        
	        if (cell.isSolid()){
	        	invalidationMessage = "You can't go there";
	        	return false;
	        }
        }
		return true;
	}
	
	@Override
	public void execute() {
		actionCancelled = false;
		Expedition expedition = (Expedition) performer;
		Position var = null;
		if (expedition.getLevel() instanceof ExpeditionLevelReader){
			var = directionToVariation(targetDirection);
		} else {
			var = Action.directionToVariation(targetDirection);
		}
                
		if (expedition.getMovementMode() == MovementMode.SHIP){
			boolean stalled = false;
			//Don't walk, sail instead!
			if (var.x() != 0){
				TurnShip turnShip = new TurnShip(var.x());
				turnShip.setPerformer(performer);
				turnShip.execute();
			}
			if (expedition.getSailingPoint() == SailingPoint.BEATING){
				if (Util.chance(60)) {
					expedition.getLevel().addMessage("You are on irons!");
					stalled = true;
				}
			}
			if (expedition.getLocation().getWindDirection()== CardinalDirection.NULL){
				expedition.getLevel().addMessage("No wind propels your ship!");
				stalled = true;
			}
			if (stalled){
				var = new Position(0,0);
			} else {
				var = expedition.getHeading().getVectors();
			}
		} else {
			if (targetDirection == Action.SELF){
				try {
					expedition.landOn(expedition.getPosition());
				} catch (ActionCancelException e) {
					
				}
				return;
			}
		}
		
		int scale = 1;
		if (expedition.getLevel() instanceof ExpeditionLevelReader){
			scale = ExpeditionLevelReader.getLongitudeScale(expedition.getPosition().y());
		}
        var = Position.mul(var, scale);

		
		Position destinationPoint = Position.add(performer.getPosition(), var);

		
		AbstractCell cell = performer.getLevel().getMapCell(destinationPoint);

		if (cell == null){
			expedition.getLevel().addMessage("You can't go there");
			actionCancelled = true;
        	return;
        }

		if (((ExpeditionLevel)expedition.getLevel()).isZoomIn()){
			Actor actor = expedition.getLevel().getActorAt(destinationPoint);
			if (actor != null){
				actor.onPlayerBump();
				actionCancelled = true;
	        	return;
			}
		}
		
		try {
			expedition.landOn(destinationPoint);
		} catch (ActionCancelException e) {
        	actionCancelled = true;
		}
		        
        //UserInterface.getUI().addMessage(new Message("Your expedition travels "+directionDescriptions[targetDirection], performer.getPosition()));
	}
	
	@Override
	public String getID() {
		return "WALK";
	}
	
	private Expedition getExpedition(){
		return (Expedition) performer;
	}
	
	@Override
	public int getCost() {
		if (actionCancelled){
			actionCancelled = false;
			return 0;
		}
		return getExpedition().getMovementSpeed().getMovementCost();
	}
	

	@Override
	public String getSFX() {
		if (ExpeditionGame.getCurrentGame().getExpedition().getMovementMode() == MovementMode.FOOT)
			return "wav/shaktool_yowzer_footstep_1.wav";
		else
			return "wav/shaktool_yowzer_footstep_2.wav";
	}
	

}
