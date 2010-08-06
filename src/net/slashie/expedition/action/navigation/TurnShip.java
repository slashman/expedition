package net.slashie.expedition.action.navigation;

import net.slashie.expedition.domain.Expedition;
import net.slashie.serf.action.Action;

public class TurnShip extends Action{
	int direction;
	
	public TurnShip(int direction) {
		this.direction = direction;
	}

	@Override
	public void execute() {
		Expedition e = ((Expedition)performer);
		e.setHeading(e.getHeading().rotate(-direction));
	}

	@Override
	public String getID() {
		return "TurnShip";
	}

}