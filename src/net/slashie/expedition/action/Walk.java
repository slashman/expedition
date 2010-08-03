package net.slashie.expedition.action;

import java.util.ArrayList;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.GoodsCache;
import net.slashie.expedition.domain.ShipCache;
import net.slashie.expedition.domain.Vehicle;
import net.slashie.expedition.domain.Expedition.MovementMode;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.ui.ExpeditionUserInterface;
import net.slashie.expedition.world.ExpeditionCell;
import net.slashie.expedition.world.ExpeditionMicroLevel;
import net.slashie.expedition.world.OverworldExpeditionCell;
import net.slashie.serf.action.Action;
import net.slashie.serf.action.Actor;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.game.Player;
import net.slashie.serf.level.AbstractCell;
import net.slashie.serf.level.AbstractFeature;
import net.slashie.serf.ui.UserInterface;
import net.slashie.utils.Position;
import net.slashie.utils.Util;

public class Walk extends Action{
	private boolean actionCancelled = false;
	
	@Override
	public boolean canPerform(Actor a) {
		Expedition expedition = (Expedition) a;
		
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
		
        Position var = directionToVariation(targetDirection);
        Position destinationPoint = Position.add(a.getPosition(), var);
        
    	Actor actor = expedition.getLevel().getActorAt(destinationPoint);
    	if (actor != null){
    		invalidationMessage = "You can't walk there";
    		return false;
    	}
    	
        AbstractCell absCell = a.getLevel().getMapCell(destinationPoint);
        if (absCell instanceof ExpeditionCell){
	        ExpeditionCell cell = (ExpeditionCell)absCell;
	        if (cell == null){
	        	invalidationMessage = "You can't walk there";
	        	return false;
	        }
	        
	        if (cell.isSolid() || cell.isWater()){
	        	invalidationMessage = "You can't walk there";
	        	return false;
	        }
        } else {
	        OverworldExpeditionCell cell = (OverworldExpeditionCell)absCell;
	        
	        if (cell == null){
	        	invalidationMessage = "You can't walk there";
	        	return false;
	        }
	        
	        if (!cell.isLand()&& !(expedition.getMovementMode() == MovementMode.SHIP)){
	        	AbstractFeature feature =expedition.getLevel().getFeatureAt(destinationPoint);
	            if (feature == null || !feature.isSolid()){
	            	invalidationMessage = "You can't walk there";
		        	return false;
	            }
	        }
	        
	        if (cell.isSolid()){
	        	invalidationMessage = "You can't walk there";
	        	return false;
	        }
        }
		return true;
	}

	
	@Override
	public void execute() {
		actionCancelled = false;
		Expedition expedition = (Expedition) performer;
		if (targetDirection == Action.SELF){
			expedition.getLevel().addMessage("You stand alert.");
			return;
		}
		
        Position var = directionToVariation(targetDirection);
        Position destinationPoint = Position.add(performer.getPosition(), var);
        
        AbstractCell absCell = performer.getLevel().getMapCell(destinationPoint);
        if (absCell instanceof ExpeditionCell){
	        ExpeditionCell cell = (ExpeditionCell)absCell;
	        if (cell.getStore() != null){
	        	((ExpeditionUserInterface)UserInterface.getUI()).launchStore(cell.getStore());
	        	actionCancelled = true;
	        	return;
	        }
	        
	        if (cell.getStepCommand() != null){
	        	if (cell.getStepCommand().equals("DEPARTURE")){
	        		if (expedition.getTotalShips() == 0) {
        				expedition.getLevel().addMessage("You have no ships to board.");
        				actionCancelled = true;
		        		return;
	        		} else {
		        		if (((ExpeditionUserInterface)UserInterface.getUI()).depart()){
		        			String superLevelId = expedition.getLocation().getSuperLevelId();
		        			if (superLevelId == null){
		        				expedition.getLevel().addMessage("Nowhere to go.");
		        			} else {
		        				expedition.setMovementMode(MovementMode.SHIP);
		        				expedition.informPlayerEvent(Player.EVT_GOTO_LEVEL, superLevelId);
		        				//expedition.setCurrentVehicles(expedition.getShips());
		        			}
		        		} 
		        		actionCancelled = true;
		        		return;
		        		
	        		}
	        	} else if (cell.getStepCommand().equals("TRAVEL_CASTLE")){
	        		((ExpeditionUserInterface)UserInterface.getUI()).showBlockingMessage("The Queen has arranged a charriot to take you to the Alcazar of Segovia");

	        		
	        	}
	        	
	        }
        } else {
	        OverworldExpeditionCell cell = (OverworldExpeditionCell)absCell;
	        if (!cell.isLand()&& !(expedition.getMovementMode() == MovementMode.SHIP)){
	        	AbstractFeature feature =expedition.getLevel().getFeatureAt(destinationPoint);
	            if (feature != null && feature.isSolid()){
	            	feature.onStep(expedition);
	            }
	            actionCancelled = true;
	            return;
	        }
	        
	        switch(expedition.getMovementMode()){
	        case SHIP:
	        	if (cell.isRiver()){
	        		expedition.wearOutShips(30);
	        	}
	        	if (cell.isLand() && !cell.isRiver()){
	        		if (UserInterface.getUI().promptChat("Do you want to land?  (Y/n)")){
	        			GoodsCache ship = new ShipCache((ExpeditionGame)((Player)performer).getGame(), expedition.getCurrentVehicles());
	        			ship.addAllGoods(expedition);
	        			expedition.removeAllGoods();
	        			expedition.setMovementMode(MovementMode.FOOT);
        				expedition.setCurrentVehicles(new ArrayList<Vehicle>());
	        			((ExpeditionUserInterface)UserInterface.getUI()).transferFromCache(ship);
	        			ship.setPosition(new Position(expedition.getPosition()));
	        			expedition.getLevel().addFeature(ship);
	        		} else {
	        			actionCancelled = true;
	        			return;
	        		}
	        	}
	        }
	        /*
	         * Dead Reckon Calculation
	         *  @ latitude 0 (apply for all latitudes to simplify the model)
				cells	592
				longitude degrees	30
				degrees / cell	0,050675676
				mt / degree 111321  (http://books.google.com.co/books?id=wu7zHtd2LO4C&hl=en)
				mt / cell	5641,266892
				nautical leagues / mt	0,000179
				nautical leagues / cell	1,009786774

	         */
	        
	        if (Util.chance(95)) {
	        	//Simulate the lack of precision
	        	expedition.increaseDeducedReckonWest(-var.x());
	        }
        }
        AbstractFeature feature =expedition.getLevel().getFeatureAt(destinationPoint);
        if (feature != null && feature.isSolid()){
        	feature.onStep(expedition);
        	return;
        }
        

        expedition.landOn(destinationPoint);
        
        //UserInterface.getUI().addMessage(new Message("Your expedition travels "+directionDescriptions[targetDirection], performer.getPosition()));
	}
	
	private String[] directionDescriptions = new String[]{
		"north",
		"south",
		"west",
		"east",
		"northeast",
		"northwest",
		"southeast",
		"southwest",
		"nowhere"
	};
			


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
		switch (getExpedition().getMovementMode()){
		case FOOT: case HORSE:
			switch (getExpedition().getMovementSpeed()){
			case FAST:
				return 20;
			case NORMAL:
				return 30;
			case SLOW:
				return 50;
			}
		case SHIP:
			switch (getExpedition().getMovementSpeed()){
			case FAST:
				return 10;
			case NORMAL:
				return 20;
			case SLOW:
				return 30;
			}
		}
		return 30;
	}
	
	

}
