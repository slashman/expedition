package net.slashie.expedition.world.agents;

import net.slashie.expedition.domain.Town;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.world.SettlementLevel;
import net.slashie.serf.action.Action;
import net.slashie.serf.action.ActionSelector;
import net.slashie.serf.action.Actor;

@SuppressWarnings("serial")
public class WeeklyAgent extends Actor{
	protected static final Action BEAT = new Action() {
		@Override
		public void execute() {
			// Check crops at colonies
			for (Town town: ExpeditionGame.getCurrentGame().getExpedition().getTowns()){
				town.checkCrops();
			}
			
			// Restock Palos (and other cities, in the future)
			for (SettlementLevel town: ExpeditionGame.getCurrentGame().getSettlements()){
				town.restockStores();
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
