package net.slashie.expedition.action;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.world.agents.DayShiftAgent;
import net.slashie.serf.action.Action;

public class Hibernate extends Action{

	@Override
	public void execute() {
		((Expedition)performer).setHibernate(true);
	}
	
	@Override
	public int getCost() {
		return DayShiftAgent.TICKS_PER_DAY * 7;
	}

	@Override
	public String getID() {
		return "Hibernate";
	}
	
	@Override
	public void executeDisplaced() {
		((Expedition)performer).setHibernate(false);
	}
	
	@Override
	public void executionInterrupted() {
		((Expedition)performer).setHibernate(false);
	}

}
