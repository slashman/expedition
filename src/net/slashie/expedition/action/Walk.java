package net.slashie.expedition.action;

import java.util.ArrayList;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.GoodsCache;
import net.slashie.expedition.domain.ShipCache;
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

public class Walk extends Action{

	@Override
	public void execute() {
		Expedition expedition = (Expedition) performer;
		if (targetDirection == Action.SELF){
			expedition.getLevel().addMessage("You stand alert.");
			return;
		}
		
		if (expedition.getLevel() instanceof ExpeditionMicroLevel && ((ExpeditionMicroLevel)expedition.getLevel()).isDock()){
			if (expedition.getOffshoreCurrentlyCarrying() > 100){
				expedition.getLevel().addMessage("You are stranded! drop some items!");
				return;
			}
		} else {
			if (expedition.getCurrentlyCarrying() > 100){
				expedition.getLevel().addMessage("You are stranded! drop some items!");
				return;
			}
		}
		
		
		
        Position var = directionToVariation(targetDirection);
        Position destinationPoint = Position.add(performer.getPosition(), var);
        
    	Actor actor = expedition.getLevel().getActorAt(destinationPoint);
    	if (actor != null){
    		return;
    	}
    	
        AbstractCell absCell = performer.getLevel().getMapCell(destinationPoint);
        if (absCell instanceof ExpeditionCell){
	        ExpeditionCell cell = (ExpeditionCell)absCell;
	        
	        if (cell == null){
	        	return;
	        }
	        
	        if (cell.isSolid() || cell.isWater())
	        	return;
	        
	        if (cell.getStore() != null){
	        	((ExpeditionUserInterface)UserInterface.getUI()).launchStore(cell.getStore());
	        	return;
	        }
	        
	        if (cell.getStepCommand() != null){
	        	if (cell.getStepCommand().equals("DEPARTURE")){
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
	        		return;
	        	}
	        	
	        }
        } else {
        	
        	
	        OverworldExpeditionCell cell = (OverworldExpeditionCell)absCell;
	        if (cell == null){
	        	return;
	        }
	        
	        if (cell.isSolid())
	        	return;
	        
	        if (!cell.isLand()&& !(expedition.getMovementMode() == MovementMode.SHIP)){
	        	AbstractFeature feature =expedition.getLevel().getFeatureAt(destinationPoint);
	            if (feature != null && feature.isSolid()){
	            	feature.onStep(expedition);
	            }
	            return;
	        }
	        
	        switch(expedition.getMovementMode()){
	        case SHIP:
	        	if (cell.isLand()){
	        		if (UserInterface.getUI().promptChat("Do you want to land?  (Y/n)")){
	        			GoodsCache ship = new ShipCache((ExpeditionGame)((Player)performer).getGame(), expedition.getCurrentVehicles());
	        			ship.addAllGoods(expedition);
	        			expedition.removeAllGoods();
	        			expedition.setMovementMode(MovementMode.FOOT);
        				expedition.setCurrentVehicles(new ArrayList<Equipment>());
	        			((ExpeditionUserInterface)UserInterface.getUI()).transferFromCache(ship);
	        			ship.setPosition(new Position(expedition.getPosition()));
	        			expedition.getLevel().addFeature(ship);
	        		} else {
	        			return;
	        		}
	        	}
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
