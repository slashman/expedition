package net.slashie.expedition.action;

import java.util.ArrayList;
import java.util.List;

import net.slashie.expedition.domain.BattleManager;
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
import net.slashie.serf.text.EnglishGrammar;
import net.slashie.serf.ui.UserInterface;
import net.slashie.utils.Position;
import net.slashie.utils.Util;

public class MeleeAttack extends Action {
	@Override
	public void execute() {
		Expedition expedition = (Expedition) performer;
		if (targetDirection == Action.SELF){
			return;
		}
        Position var = directionToVariation(targetDirection);
        Position destinationPoint = Position.add(performer.getPosition(), var);
        
    	Actor actor = expedition.getLevel().getActorAt(destinationPoint);
    	
    	String battleName = "";
    	if (actor != null){
    		if (actor instanceof NativeTown){
    			NativeTown town = (NativeTown) actor;
    			if (town.getTotalUnits() > 0){
    				battleName = "You attack the "+town.getDescription();
    				
    			} else {
    				battleName = "You raid the "+town.getDescription();
    			}
    			actor.getLevel().addMessage(battleName);
    		} else if (actor instanceof NonPrincipalExpedition){
    			NonPrincipalExpedition npe = (NonPrincipalExpedition)actor;
        		battleName = "You attack "+EnglishGrammar.a(npe.getDescription())+" "+npe.getDescription();
    			actor.getLevel().addMessage(battleName);
    		}
    		BattleManager.battle(battleName, expedition, actor);
    	}
	}

	@Override
	public String getID() {
		return "MeleeAttack";
	}
	
	@Override
	public boolean canPerform(Actor a) {
		return a.getLevel() instanceof ExpeditionMacroLevel && ((Expedition)a).getMovementMode() != MovementMode.SHIP && targetDirection != Action.SELF;
	}
	
	@Override
	public String getInvalidationMessage() {
		return "You can't attack!";
	}
}
