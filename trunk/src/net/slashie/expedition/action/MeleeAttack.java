package net.slashie.expedition.action;

import java.util.List;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.ExpeditionUnit;
import net.slashie.expedition.domain.NonPrincipalExpedition;
import net.slashie.expedition.domain.Expedition.MovementMode;
import net.slashie.expedition.world.ExpeditionMacroLevel;
import net.slashie.serf.action.Action;
import net.slashie.serf.action.Actor;
import net.slashie.serf.game.Equipment;
import net.slashie.utils.Position;
import net.slashie.utils.Util;

public class MeleeAttack extends Action {
	@Override
	public void execute() {
		Expedition expedition = (Expedition) performer;
		if (targetDirection == Action.SELF){
			expedition.getLevel().addMessage("You stand alert.");
			return;
		}
        Position var = directionToVariation(targetDirection);
        Position destinationPoint = Position.add(performer.getPosition(), var);
        
    	Actor actor = expedition.getLevel().getActorAt(destinationPoint);
    	if (actor != null){
    		if (actor instanceof NonPrincipalExpedition){
    			NonPrincipalExpedition npe = (NonPrincipalExpedition)actor;
   				//Attack!
   				expedition.getLevel().addMessage("You attack the "+npe.getDescription()+": ");
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
   				List<Equipment> enemyUnits = npe.getUnits();
   				int damageMitigated = 0;
   				for (Equipment unit_: enemyUnits){
   					ExpeditionUnit unit = (ExpeditionUnit)unit_.getItem();
   					damageMitigated += (int)Math.round((double)unit.getDefense() * (double)unit_.getQuantity() * ((double)defenseProportion/100.0d));
   				}
   				
   				//Calculate deaths on npe
   				//int outcome = attackProportion - defenseProportion;
   				int outcome = damageCaused - damageMitigated;
   				if (outcome < 0)
   					outcome = 0;
   				int deaths = outcome;
   				if (deaths > 0)
   					npe.killUnits(deaths);
   				else
   					performer.getLevel().addMessage(" No one is killed.");
   				
    		}
    	}
	}
	
	@Override
	public String getID() {
		return "MeleeAttack";
	}
	
	@Override
	public boolean canPerform(Actor a) {
		return a.getLevel() instanceof ExpeditionMacroLevel && ((Expedition)a).getMovementMode() != MovementMode.SHIP;
	}
	
	@Override
	public String getInvalidationMessage() {
		return "You can't attack!";
	}
}
