package net.slashie.expedition.world;

import net.slashie.serf.level.AbstractCell;
import net.slashie.serf.ui.AppearanceFactory;

public class OverworldExpeditionCell extends AbstractCell{
	private boolean isLand, isMountain, isRiver;
	private double foodConsumptionModifier;
	
	public boolean isRiver() {
		return isRiver;
	}

	public boolean isLand() {
		return isLand;
	}



	public boolean isMountain() {
		return isMountain;
	}

	public double getFoodConsumptionModifier() {
		return foodConsumptionModifier;
	}

	public OverworldExpeditionCell(String pid, String description, boolean isLand, boolean isMountain, boolean isRiver, double d) {
		super(pid, description, description, AppearanceFactory.getAppearanceFactory().getAppearance(pid));
		this.isLand = isLand;
		this.isMountain = isMountain;
		this.isRiver = isRiver;
		this.foodConsumptionModifier = d;
	}



	public String getWeather() {
		return "Sunny";
	}
	
	public String getTemperature(){
		return "Warm";
	}
	
	@Override
	public boolean cloneRequired() {
		return false;
	}

}
