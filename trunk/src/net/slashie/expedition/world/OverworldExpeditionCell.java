package net.slashie.expedition.world;

import net.slashie.serf.level.AbstractCell;
import net.slashie.serf.ui.AppearanceFactory;

public class OverworldExpeditionCell extends AbstractCell{
	private boolean isLand, isMountain, isRiver, isWood;
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

	public OverworldExpeditionCell(String pid, String description, boolean isLand, boolean isMountain, boolean isRiver, double d, boolean isSolid, boolean isWood, boolean isOpaque) {
		super(pid, description, description, AppearanceFactory.getAppearanceFactory().getAppearance(pid), isSolid, isOpaque);
		this.isLand = isLand;
		this.isMountain = isMountain;
		this.isRiver = isRiver;
		this.foodConsumptionModifier = d;
		this.isWood = isWood;
		setWater(!isLand);
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

	public boolean isWood() {
		return isWood;
	}

	public boolean isMarsh() {
		// TODO Implement marshes
		return false;
	}

	public boolean isDesert() {
		// TODO Implement deserts
		return false;
	}

	public boolean isSea() {
		// TODO Split rivers and seas
		return isWater() && !isRiver();
	}

}
