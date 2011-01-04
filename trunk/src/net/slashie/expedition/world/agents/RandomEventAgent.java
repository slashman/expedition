package net.slashie.expedition.world.agents;

import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.serf.action.Action;
import net.slashie.serf.action.ActionSelector;
import net.slashie.serf.action.Actor;
import net.slashie.utils.Util;

public class RandomEventAgent extends Actor{

	public static final int TICKS_PER_DAY = 275;
	
	protected static final Action BEAT = new Action() {

		@Override
		public void execute() {
			ExpeditionGame.getCurrentGame().getExpedition().randomEvents();
			ExpeditionGame.getCurrentGame().getExpedition().updateMorale();
		}

		@Override
		public String getID() {
			return null;
		}
		
		@Override
		public int getCost() {
			return Util.rand((int)Math.round((double)DayShiftAgent.TICKS_PER_DAY/4.0d), DayShiftAgent.TICKS_PER_DAY);
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
		return "DAY_SHIFT";
	}

	@Override
	public String getDescription() {
		return "Day Shift";
	}
	
	@Override
	public ActionSelector getSelector() {
		return SELECTOR;
	}
	
	

}
