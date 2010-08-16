package net.slashie.expedition.action;

import java.util.List;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.ExpeditionUnit;
import net.slashie.expedition.domain.NonPrincipalExpedition;
import net.slashie.serf.action.Actor;
import net.slashie.serf.ai.RangedAction;
import net.slashie.serf.game.Equipment;
import net.slashie.utils.Position;
import net.slashie.utils.Util;

public class NPRainArrows extends RangedAction {
	public NPRainArrows() {
		set (10, "directionalmissile", "rainArrows","arrows.wav");
	}

	@Override
	public void set(int range, String effectType, String effectID,
			String effectWav) {
		super.set(range, effectType, effectID, effectWav);
	}
	
	@Override
	public boolean canPerform(Actor a) {
		Expedition expedition = (Expedition)a;
		//Pre-check ammo
		if (expedition.getGoodCount("ARROWS") == 0){
			invalidationMessage = "No arrows in stock.";
			return false;
		}
		return true;
		
	}
	
	@Override
	public String getID() {
		return "NP_RAINARROWS";
	}
	
	public boolean preEffectCheck(Actor target){
		Expedition expedition = (Expedition)performer;
		if (!(target instanceof Expedition))
			return true;
		Expedition targetExpedition = (Expedition)target;


		//Select the units with enough range
		int distance = Position.distance(performer.getPosition(), targetExpedition.getPosition());
		List<Equipment> units = expedition.getUnitsOverRange(distance);
		if (units.size() == 0){
			invalidationMessage = "No units within firing range";
			return false;
		}
		return true;
	}
	@Override
	public boolean actOverTarget(Actor target) {
		Expedition expedition = (Expedition)performer;
		
		if (!(target instanceof Expedition))
			return false;
		Expedition targetExpedition = (Expedition)target;
		
		//Select the units with enough range
		int distance = Position.distance(performer.getPosition(), targetExpedition.getPosition());
		List<Equipment> units = expedition.getUnitsOverRange(distance);
		if (units.size() == 0){
			invalidationMessage = "No units within firing range";
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
		expedition.getLevel().addMessage("The "+expedition.getDescription()+" fires arrows at yours!: ");
		
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
		List<Equipment> enemyUnits = targetExpedition.getUnits();
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
			targetExpedition.killUnits(deaths);
		else
			performer.getLevel().addMessage(" No one is killed.");
		 return true;
			
	}
	

}
