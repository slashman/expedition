package net.slashie.expedition.action;

import java.util.List;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.ExpeditionUnit;
import net.slashie.expedition.domain.NonPrincipalExpedition;
import net.slashie.expedition.world.ExpeditionMacroLevel;
import net.slashie.serf.action.Actor;
import net.slashie.serf.ai.RangedAction;
import net.slashie.serf.game.Equipment;
import net.slashie.utils.Position;
import net.slashie.utils.Util;

public class RainArrows extends RangedAction {
	public RainArrows() {
		set (10, "directionalmissile", "rainArrows","arrows.wav");
	}

	@Override
	public void set(int range, String effectType, String effectID,
			String effectWav) {
		super.set(range, effectType, effectID, effectWav);
	}
	
	@Override
	public boolean canPerform(Actor a) {
		if (!(a.getLevel() instanceof ExpeditionMacroLevel)){
			return false;
		}
		Expedition expedition = (Expedition)a;
		//Pre-check ammo
		if (expedition.getGoodCount("ARROWS") == 0){
			invalidationMessage = "No arrows in stock.";
			return false;
		}
		return true;
		
	}
	
	@Override
	public String getInvalidationMessage() {
		if (!(performer.getLevel() instanceof ExpeditionMacroLevel)){
			return "You can't do that here!";
		}
		Expedition expedition = (Expedition)performer;
		//Pre-check ammo
		if (expedition.getGoodCount("ARROWS") == 0){
			return "No arrows in stock.";
		}
		return "";
	}
	
	
	@Override
	public boolean actOverTarget(Actor target) {
		Expedition expedition = (Expedition)performer;
		NonPrincipalExpedition npe = (NonPrincipalExpedition)target;


		//Select the units with enough range
		int distance = Position.distance(performer.getPosition(), npe.getPosition());
		List<Equipment> units = expedition.getUnitsOverRange(distance);
		if (units.size() == 0){
			target.getLevel().addMessage("Target is outside firing range");
			
			return false;
		}
		

		//Calculate how many of the expedition fighters will attack. Only those with enough range will do it. 
		//TODO: Enhance, make it dependant on expeditionary commandship
		int attackProportion = Util.rand(80, 100);

		//Check if there is enough ammo, and reduce the proportion if not
		int shoots = 0;
		for (Equipment unit: units){
			shoots += (int)Math.round((double)unit.getQuantity() * (double)attackProportion/100.0d);
		}
		
		if (expedition.getGoodCount("ARROWS") < shoots){
			shoots = expedition.getGoodCount("ARROWS");
		}
		
		//Reduce Ammo
		expedition.reduceGood("ARROWS", shoots);
		
		//Attack!
		expedition.getLevel().addMessage("You fire over the "+npe.getDescription()+": ");
		
		//Calculate Damage
		int damageCaused = 0;
		int roundShoots;
		for (Equipment unit_: units){
			ExpeditionUnit unit = (ExpeditionUnit)unit_.getItem();
			roundShoots = (shoots > unit_.getQuantity() ? unit_.getQuantity() : shoots);
			damageCaused += (int)Math.round((double)unit.getAttack() * (double)roundShoots);
			shoots -= roundShoots;
			
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
		int outcome = attackProportion - defenseProportion;
		if (outcome < 0)
			outcome = 0;
		int deaths = outcome;
		if (deaths > 0)
			npe.killUnitsOnMeleeBattle(deaths);
		else
			performer.getLevel().addMessage(" No one is killed.");
		 return true;
			
	}
	
	public boolean preEffectCheck(Actor target){
		Expedition expedition = (Expedition)performer;
		Expedition targetExpedition = (Expedition)target;


		//Select the units with enough range
		int distance = Position.distance(performer.getPosition(), targetExpedition.getPosition());
		List<Equipment> units = expedition.getUnitsOverRange(distance);
		if (units.size() == 0){
			target.getLevel().addMessage("Target is outside firing range");
			return false;
		}
		return true;
	}

	@Override
	public String getPromptPosition() {
		return "Select your target and press space";
	}

}
