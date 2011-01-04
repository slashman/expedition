package net.slashie.expedition.action;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.world.agents.DayShiftAgent;
import net.slashie.serf.action.Action;

public class Hibernate extends Action{
	
	private int days;
	private boolean stopFoodConsumption;
	
	public Hibernate(int days, boolean stopFoodConsumption){
		this.days = days;
		this.stopFoodConsumption = stopFoodConsumption;
	}
	
	@Override
	public void execute() {
		if (stopFoodConsumption)
			((Expedition)performer).setHibernate(true);
	}
	
	@Override
	public int getCost() {
		return DayShiftAgent.TICKS_PER_DAY * days;
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
