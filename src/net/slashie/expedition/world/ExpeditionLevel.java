package net.slashie.expedition.world;

import net.slashie.serf.level.AbstractCell;
import net.slashie.util.Pair;
import net.slashie.utils.Position;

public interface ExpeditionLevel{
	public Pair<Integer, Integer> getLocation();
	public String getMusicKey();
	public String getSuperLevelId();
	public String getWeather();
	public int getTemperature();
	public String getDescription();
	public Pair<String, String> getLocationDescription();
	public AbstractCell getMapCell(Position position);
	public boolean isSpawnPointUsed(Position spawnPoint);
	public void setSpawnPointUsed(Position spawnPoint);
	public boolean isZoomIn();
}
