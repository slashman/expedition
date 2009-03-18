package net.slashie.expedition.world;

import net.slashie.serf.level.AbstractCell;
import net.slashie.serf.ui.AppearanceFactory;

public class OverworldExpeditionCell extends AbstractCell{
	private boolean isLand, isMountain;
	private int speedModifier;
	
	

	public boolean isLand() {
		return isLand;
	}



	public boolean isMountain() {
		return isMountain;
	}



	public int getSpeedModifier() {
		return speedModifier;
	}



	public OverworldExpeditionCell(String pid, String description, boolean isLand, boolean isMountain, int speedModifier) {
		super(pid, description, description, AppearanceFactory.getAppearanceFactory().getAppearance(pid));
		this.isLand = isLand;
		this.isMountain = isMountain;
		this.speedModifier = speedModifier;
	}



	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public String getWeather() {
		return "Sunny";
	}
	
	public int getTemperature(){
		return 18;
	}

}
