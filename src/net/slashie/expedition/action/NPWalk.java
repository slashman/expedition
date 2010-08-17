package net.slashie.expedition.action;

import java.util.ArrayList;
import java.util.List;

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
import net.slashie.serf.ui.UserInterface;
import net.slashie.utils.Position;
import net.slashie.utils.Util;

public class NPWalk extends Action {

	@Override
	public void execute() {
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
	        if (cell == null){
	        	return;
	        }
	        
	        if (cell.getStore() != null){
	        	return;
	        }
	        
	        if (cell.getStepCommand() != null){
	        	if (cell.getStepCommand().equals("DEPARTURE")){
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
	            return;
	        }
        }
        Actor actor = performer.getLevel().getActorAt(destinationPoint);
        if (actor != null){
        	if (actor instanceof Expedition && !(actor instanceof NonPrincipalExpedition)){
        		Expedition targetExpedition = (Expedition) actor;
        		targetExpedition.setJustAttacked(true);
   				//Attack!
   				expedition.getLevel().addMessage("The "+expedition.getDescription()+" attacks: ");
   				//Calculate how many of the expedition fighters will attack. 
   				//TODO: Enhance, make it dependant on expeditionary commandship
   				int attackProportion = Util.rand(80, 100);
   				
   				//Calculate Damage
   				List<Equipment> units = expedition.getUnits();
   				int damageCaused = 0;
   				for (Equipment unit_: units){
   					ExpeditionUnit unit = (ExpeditionUnit)unit_.getItem();
   					damageCaused += (int)Math.round((double)unit.getAttack() * (double)unit_.getQuantity() * ((double)attackProportion/100.0d));
   				}

   				//Calculate how many of the expedition defenders will act 
   				//TODO: Enhance, make it dependant on expeditionary commandship
   				int defenseProportion = Util.rand(80, 100);
   				
   				//Calculate mitigation
   				List<Equipment> enemyUnits = targetExpedition.getUnits();
   				int damageMitigated = 0;
   				for (Equipment unit_: enemyUnits){
   					ExpeditionUnit unit = (ExpeditionUnit)unit_.getItem();
   					damageMitigated += (int)Math.round((double)unit.getDefense() * (double)unit_.getQuantity() * ((double)defenseProportion/100.0d));
   				}
   				
   				//Calculate deaths on expedition
   				int outcome = damageCaused - damageMitigated;
   				if (outcome < 0)
   					outcome = 0;
   				int deaths = outcome;
   				if (deaths > 0)
   					targetExpedition.killUnits(deaths);
   				else
   					performer.getLevel().addMessage(" No one is killed.");
   				return;
        	}
        } else {
        	expedition.landOn(destinationPoint);
        }
	}

	@Override
	public String getID() {
		return "NPWalk";
	}

}
