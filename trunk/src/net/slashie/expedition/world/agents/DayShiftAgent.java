package net.slashie.expedition.world.agents;

import java.util.Calendar;
import java.util.List;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.world.FoodConsumer;
import net.slashie.serf.action.Action;
import net.slashie.serf.action.ActionSelector;
import net.slashie.serf.action.Actor;

public class DayShiftAgent extends Actor{

	public static final int TICKS_PER_DAY = 275;
	
	protected static final Action BEAT = new Action() {

		@Override
		public void execute() {
			Calendar currentTime = ExpeditionGame.getCurrentGame().getGameTime();
			int month = currentTime.get(Calendar.MONTH);
			currentTime.add(Calendar.DATE, 1);
			if (currentTime.get(Calendar.MONTH) > month){
				ExpeditionGame.getCurrentGame().monthChange();
			}
			//Everybody eat
			List<FoodConsumer> foodConsumers = ExpeditionGame.getCurrentGame().getFoodConsumers();
			for (int i = 0; i < foodConsumers.size(); i++){
				foodConsumers.get(i).consumeFood();
			}
			
			Expedition expedition = ExpeditionGame.getCurrentGame().getExpedition();
			expedition.dayShift();
		}

		@Override
		public String getID() {
			return null;
		}
		
		@Override
		public int getCost() {
			return TICKS_PER_DAY;
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
