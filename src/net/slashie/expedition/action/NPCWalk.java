package net.slashie.expedition.action;

import net.slashie.expedition.world.ExpeditionCell;
import net.slashie.serf.action.Action;
import net.slashie.serf.action.Actor;
import net.slashie.serf.level.AbstractCell;
import net.slashie.utils.Position;

public class NPCWalk extends Action{

	@Override
	public boolean canPerform(Actor a) {
        Position var = directionToVariation(targetDirection);
        Position destinationPoint = Position.add(a.getPosition(), var);
    	Actor actor = a.getLevel().getActorAt(destinationPoint);
    	if (actor != null)
    		return false;
    	
        AbstractCell absCell = a.getLevel().getMapCell(destinationPoint);
        if (absCell instanceof ExpeditionCell){
	        ExpeditionCell cell = (ExpeditionCell)absCell;
	        if (cell.isSolid() || cell.isWater()){
	        	invalidationMessage = "You can't go there";
	        	return false;
	        }
        }
		return true;
	}

	
	@Override
	public void execute() {
		Position var = directionToVariation(targetDirection);
        
        Position destinationPoint = Position.add(performer.getPosition(), var);

		AbstractCell cell = performer.getLevel().getMapCell(destinationPoint);

		if (cell == null){
			return;
        }
		
		performer.setPosition(destinationPoint);
		
	}
	

	@Override
	public String getID() {
		return "NPC_WALK";
	}
	
	@Override
	public int getCost() {
		return 50;
	}
	
	

}
