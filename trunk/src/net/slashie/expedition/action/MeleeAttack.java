package net.slashie.expedition.action;

import java.util.ArrayList;
import java.util.List;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.ExpeditionUnit;
import net.slashie.expedition.domain.NativeTown;
import net.slashie.expedition.domain.NonPrincipalExpedition;
import net.slashie.expedition.domain.Expedition.MovementMode;
import net.slashie.expedition.ui.ExpeditionUserInterface;
import net.slashie.expedition.world.ExpeditionMacroLevel;
import net.slashie.expedition.world.FoodConsumer;
import net.slashie.serf.action.Action;
import net.slashie.serf.action.Actor;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.ui.UserInterface;
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
    		List<Equipment> attackingUnitsFullGroup = expedition.getUnits();;
    		List<Equipment> defendingUnitsFullGroup = null;
    		List<Equipment> attackingUnits = new ArrayList<Equipment>();
    		List<Equipment> defendingUnits = new ArrayList<Equipment>();
    		if (actor instanceof NativeTown){
    			NativeTown town = (NativeTown) actor;
    			if (town.getTotalUnits() == 0){
    				((ExpeditionUserInterface)UserInterface.getUI()).transferFromCache(town);
    				return;
    			} else {
					performer.getLevel().addMessage("You attack the "+town.getDescription());
					defendingUnitsFullGroup = town.getUnits();
					town.resetTurnsBeforeNextExpedition();
					actor.getLevel().getDispatcher().removeActor(town);
					actor.getLevel().getDispatcher().addActor(town, true);
    			}
    		} else if (actor instanceof NonPrincipalExpedition){
    			NonPrincipalExpedition npe = (NonPrincipalExpedition)actor;
   				//Attack!
   				expedition.getLevel().addMessage("You attack the "+npe.getDescription()+": ");
   				defendingUnitsFullGroup = npe.getUnits();
    		}
    		
   		
    		// Trim attacking and defending teams to 50
    		int acum = 0;
    		for (Equipment eq: attackingUnitsFullGroup){
    			if (acum > 50)
    				break;
    			if (acum + eq.getQuantity() > 50){
    				attackingUnits.add(new Equipment(eq.getItem(), 50-acum));
    				break;
    			} else {    				
    				attackingUnits.add(eq);
    				acum += eq.getQuantity();
    			}
    		}
    		
    		acum = 0;
    		for (Equipment eq: defendingUnitsFullGroup){
    			if (acum > 50)
    				break;
    			if (acum + eq.getQuantity() > 50){
    				defendingUnits.add(new Equipment(eq.getItem(), 50-acum));
    				break;
    			} else {    				
    				defendingUnits.add(eq);
    				acum += eq.getQuantity();
    			}
    		}
    		
			//Calculate how many of the expedition fighters will attack. 
			//TODO: Enhance, make it dependant on expeditionary commandship
			int attackProportion = Util.rand(80, 100);
			int defenseProportion = Util.rand(80, 100);
			
			//Calculate Damage
			int damageCaused = 0;
			for (Equipment unit_: attackingUnits){
				ExpeditionUnit unit = (ExpeditionUnit)unit_.getItem();
				damageCaused += (int)Math.round((double)unit.getAttack() * (double)unit_.getQuantity() * ((double)attackProportion/100.0d));
			}

			
			//Calculate mitigation
			int damageMitigated = 0;
			for (Equipment unit_: defendingUnits){
				ExpeditionUnit unit = (ExpeditionUnit)unit_.getItem();
				damageMitigated += (int)Math.round((double)unit.getDefense() * (double)unit_.getQuantity() * ((double)defenseProportion/100.0d));
			}
			
			//Calculate deaths 
			int outcome = damageCaused - damageMitigated;
			if (outcome < 0)
				outcome = 0;
			int deaths = outcome;
			if (deaths > 0)
				((FoodConsumer)actor).killUnits(deaths);
			else
				performer.getLevel().addMessage(" No one is killed.");
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
