package net.slashie.expedition.world;

import net.slashie.util.Pair;

public interface ExpeditionLevel{
	public Pair<Integer, Integer> getLocation();
	public String getMusicKey();
	public String getSuperLevelId();
	public String getWeather();
	public int getTemperature();
}
