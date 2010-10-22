package net.slashie.expedition.world.agents;

import net.slashie.expedition.domain.Town;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.serf.action.Action;
import net.slashie.serf.action.ActionSelector;
import net.slashie.serf.action.Actor;

public class WeeklyAgent extends Actor{

	protected static final Action BEAT = new Action() {

		@Override
		public void execute() {
			// Forage food at colonies
			for (Town town: ExpeditionGame.getCurrentGame().getExpedition().getTowns()){
				town.forageFood();
				town.checkCrops();
			}
		}

		@Override
		public String getID() {
			return null;
		}
		
		@Override
		public int getCost() {
			return DayShiftAgent.TICKS_PER_DAY * 7;
		}
		
	};
	private static final ActionSelector SELECTOR = new ActionSelector(){

		public ActionSelector derive() {
			return null;
		}

		public String getID() {
			return null;
		}

		public Action selectAction(Actor who) {
			return BEAT;
		}
		
	};

	@Override
	public String getClassifierID() {
		return "WEEKLY_AGENT";
	}

	@Override
	public String getDescription() {
		return "Weekly Agent";
	}
	
	@Override
	public ActionSelector getSelector() {
		return SELECTOR;
	}
	
	

}
