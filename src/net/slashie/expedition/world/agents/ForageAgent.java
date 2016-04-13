package net.slashie.expedition.world.agents;

import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.serf.action.Action;
import net.slashie.serf.action.ActionSelector;
import net.slashie.serf.action.Actor;

public class ForageAgent extends Actor{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static final Action BEAT = new Action() {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void execute() {
			ExpeditionGame.getCurrentGame().getExpedition().forageFood();
		}

		@Override
		public String getID() {
			return null;
		}
		
		@Override
		public int getCost() {
			return (int)Math.round((double)DayShiftAgent.TICKS_PER_DAY / 1.5d);
		}
		
	};
	private static final ActionSelector SELECTOR = new ActionSelector(){

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

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
		return "FORAGE_AGENT";
	}

	@Override
	public String getDescription() {
		return "Forage Agent";
	}
	
	@Override
	public ActionSelector getSelector() {
		return SELECTOR;
	}
	
	

}
