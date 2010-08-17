package net.slashie.expedition.ai;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.ExpeditionFactory;
import net.slashie.expedition.domain.NativeTown;
import net.slashie.serf.action.Action;
import net.slashie.serf.action.ActionSelector;
import net.slashie.serf.action.Actor;
import net.slashie.serf.action.AwareActor;
import net.slashie.serf.action.PassAction;
import net.slashie.utils.Position;

public class NativeActionSelector implements ActionSelector {
	private static final Action PASS_ACTION = new PassAction(200);
	
	private class NativeTownAwareActor extends AwareActor {
		private NativeTown town;
		@Override
		public int getSightRange() {
			return town.getSightRange();
		}

		@Override
		public String getClassifierID() {
			return null;
		}

		@Override
		public String getDescription() {
			return null;
		}

		public void setTown(NativeTown town) {
			this.town = town;
		}
		
	};

	private NativeTownAwareActor awareActorDelegate = new NativeTownAwareActor();
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
		
		awareActorDelegate.setTown(town);
		awareActorDelegate.setLevel(town.getLevel());
		awareActorDelegate.setPosition(town.getPosition());
		awareActorDelegate.setWasSeen(town.wasSeen());
		if (town.isHostile()){
			if (awareActorDelegate.isActorInLOS(town.getLevel().getPlayer())){
				
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
		return PASS_ACTION;
	}
	
	
	
}
