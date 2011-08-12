package net.slashie.expedition.ai;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.NativeTown;
import net.slashie.serf.action.Action;
import net.slashie.serf.action.ActionSelector;
import net.slashie.serf.action.Actor;
import net.slashie.serf.action.PassAction;
import net.slashie.utils.Position;
import net.slashie.utils.Util;

public class NativeActionSelector implements ActionSelector {
	private static final long serialVersionUID = 1L;
	
	private static final Action PASS_ACTION = new PassAction(200);
	public ActionSelector derive() {
		return null;
	}

	public String getID() {
		return null;
	}

	public Action selectAction(Actor who) {
		NativeTown town = (NativeTown)  who;
		if (Position.flatDistance(who.getPosition(), who.getLevel().getPlayer().getPosition()) > 50){
			//Since the player is away, we don't exist. Until the player sees us again.
			who.die();
			town.setDisabled(true);
			return PASS_ACTION;
		}
		
		if (town.isUnfriendly()){
			if (town.wasSeen() && Position.distance(town.getPosition(), town.getLevel().getPlayer().getPosition()) <= town.getSightRange()){
 				int potentialPower = town.getPotentialPower(); 
				if (potentialPower > 0){
					int maxPotentialPower = 3;
					if (town.getCulture().isCivilization())
						maxPotentialPower = 5;
					if (potentialPower > maxPotentialPower)
						potentialPower = maxPotentialPower;
					Expedition expedition = town.deployTroops(potentialPower);
					expedition.setPosition(town.getPosition());
					town.getLevel().addActor(expedition);
					town.getLevel().addMessage("The "+town.getDescription()+" sends out an expedition!");
				}
			}
		}
		if (Util.chance(20))
			town.reduceScaredLevel();
		return PASS_ACTION;
	}
	
	
	
}
