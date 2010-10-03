package net.slashie.expedition.world.agents;

import java.util.Calendar;

import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.world.CardinalDirection;
import net.slashie.expedition.world.ExpeditionMacroLevel;
import net.slashie.serf.action.Action;
import net.slashie.serf.action.ActionSelector;
import net.slashie.serf.action.Actor;
import net.slashie.utils.Util;

public class WindAgent extends Actor{

	protected static final int WIND_BEAT = 500;
	
	protected static final Action BEAT = new Action() {

		@Override
		public void execute() {
			ExpeditionMacroLevel level = (ExpeditionMacroLevel) performer.getLevel();
			CardinalDirection prevailingWind = level.getPrevailingWind(ExpeditionGame.getCurrentGame().getGameTime().get(Calendar.MONTH));
			CardinalDirection currentWind = prevailingWind;
			int rotateSign = Util.chance(50) ? 1 : -1;
			int rotate = 0;
			boolean isOnITZ = false;
			if (currentWind == CardinalDirection.NULL){
				// In the doldrums
				isOnITZ = true;
				if (Util.chance(50)){
					rotate = Util.rand(0, 2);
				}  
			} else if (!level.getWeather().isWindy() && Util.chance(15)) {
				rotate = 0;
				currentWind = CardinalDirection.NULL;
			} else if (Util.chance(70)){
				rotate = Util.rand(0, 2);
			} else if (Util.chance(30)){
				rotate = Util.rand(1, 4);
			}
			for (int i = 0; i < rotate; i++){
				currentWind = currentWind.rotate(rotateSign);
			}
			level.setWindDirection(currentWind);
			level.setIsOnITZ(isOnITZ);
		}

		@Override
		public String getID() {
			return null;
		}
		
		@Override
		public int getCost() {
			return WIND_BEAT;
		}
		
	};
	private static final ActionSelector WIND_SELECTOR = new ActionSelector(){

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
		return "WIND";
	}

	@Override
	public String getDescription() {
		return "Wind";
	}
	
	@Override
	public ActionSelector getSelector() {
		return WIND_SELECTOR;
	}
	
	

}
