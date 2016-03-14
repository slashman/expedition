package net.slashie.expedition.action;

import java.util.Calendar;
import java.util.List;
import net.ck.expedition.utils.swing.MessengerService;
import net.slashie.expedition.action.navigation.TurnShip;
import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.Ruin;
import net.slashie.expedition.domain.SailingPoint;
import net.slashie.expedition.domain.ShipCache;
import net.slashie.expedition.domain.Expedition.MovementMode;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.level.ExpeditionLevelReader;
import net.slashie.expedition.level.GlobeMapModel;
import net.slashie.expedition.ui.ExpeditionDiscovery;
import net.slashie.expedition.ui.ExpeditionDiscovery.Discovery;
import net.slashie.expedition.ui.ExpeditionDisplay;
import net.slashie.expedition.ui.ExpeditionUserInterface;
import net.slashie.expedition.ui.oryx.OryxExpeditionDisplay;
import net.slashie.expedition.world.CardinalDirection;
import net.slashie.expedition.world.ExpeditionCell;
import net.slashie.expedition.world.ExpeditionLevel;
import net.slashie.expedition.world.ExpeditionMacroLevel;
import net.slashie.expedition.world.ExpeditionMicroLevel;
import net.slashie.expedition.world.OverworldExpeditionCell;
import net.slashie.serf.action.Action;
import net.slashie.serf.action.Actor;
import net.slashie.serf.level.AbstractCell;
import net.slashie.serf.level.AbstractFeature;
import net.slashie.serf.ui.ActionCancelException;
import net.slashie.serf.ui.UserInterface;
import net.slashie.utils.Position;
import net.slashie.utils.Util;

@SuppressWarnings("serial")
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
		
		if (expedition.getLevel() instanceof ExpeditionMicroLevel ){
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
        
        boolean sailingForward = false;
        if (expedition.getMovementMode() == MovementMode.SHIP){
			//Don't walk, sail instead!
			if (var.x() == 0 && !expedition.isAnchored()){
				sailingForward = true;
				var = expedition.getHeading().getVectors();
			} else {
				return true;
			}
		}
        
        if (expedition.getLevel() instanceof ExpeditionLevelReader){
			var = GlobeMapModel.getSingleton().scaleVar(var, expedition.getPosition().y());
		}
        
        Position destinationPoint = Position.add(a.getPosition(), var);
        
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
		        	if (features != null && features.size() > 0){
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
	        } else {
	        	// Cell is land, check if the player bumps land with a forward command
	        	if (!cell.isRiver() && sailingForward){
	        		invalidationMessage = "We shouldn't sail straight ashore!";
	        		return false;
	        	}
	        }
	        
	        if (cell.isSolid()){
	        	invalidationMessage = "You can't go there";
	        	return false;
	        }
	        
	        Actor actor = a.getLevel().getActorAt(destinationPoint);
	        if (actor != null){
	        	if (actor instanceof Ruin){
	        		Ruin r = (Ruin)actor;
	        		invalidationMessage = "You can't go there";
	        		if (!r.isDiscovered()){
	        			String discoveryText = "You discovered " + r.getDescription();
	        			invalidationMessage = discoveryText;
	        			r.setDiscovered();
	        			Calendar gameTime = ExpeditionGame.getCurrentGame().getGameTime();
	        			String time = ExpeditionUserInterface.months[gameTime.get(Calendar.MONTH)] +" "+ gameTime.get(Calendar.DATE)+", "+MessengerService.getTimeDescriptionFromHour(gameTime.get(Calendar.HOUR_OF_DAY));
	        			expedition.addDiscoveryLog(new ExpeditionDiscovery(discoveryText, Discovery.Ruin, time, 20));
	        		}
		        	return false;
	        	}
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
			if (expedition.isAnchored()){
				if (var.x() == 0){
					if (UserInterface.getUI().promptChat("We must weigh anchors to sail forward, should we do it?")){
						expedition.setAnchored(false);
					}
				}

			} 
			if (expedition.isAnchored()){
				stalled = true;
			}else if (expedition.getSailingPoint() == SailingPoint.BEATING){
				if (Util.chance(60)) {
					expedition.getLevel().addMessage("You are on irons!");
					stalled = true;
				}
			} else if (expedition.getLocation().getWindDirection()== CardinalDirection.NULL){
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
		
		
		if (expedition.getLevel() instanceof ExpeditionLevelReader){
			var = GlobeMapModel.getSingleton().scaleVar(var, expedition.getPosition().y());
		}
	
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
		if (ExpeditionGame.getCurrentGame().getExpedition().getMovementMode().isLandMovement())
			return "wav/shaktool_yowzer_footstep_1.wav";
		else
			return "wav/shaktool_yowzer_footstep_2.wav";
	}
	

}
