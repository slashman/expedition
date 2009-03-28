package net.slashie.expedition.level;

import java.io.Serializable;
import java.util.Hashtable;

import net.slashie.expedition.world.ExpeditionLevel;
import net.slashie.util.Pair;
import net.slashie.utils.Position;

public class ExpeditionLevelHelper implements Serializable{
	private Hashtable<String, Boolean> spawnPointsRegistry = new Hashtable<String, Boolean>();
	
	public ExpeditionLevelHelper() {
		super();
	}

	private String musicKey;
	private String superLevelId;
	
	public String getMusicKey() {
		return musicKey;
	}
	public void setMusicKey(String musicKey) {
		this.musicKey = musicKey;
	}
	public String getSuperLevelId() {
		return superLevelId;
	}
	public void setSuperLevelId(String superLevelId) {
		this.superLevelId = superLevelId;
	}

	public void setSpawnPointUsed(Position p){
		spawnPointsRegistry.put(p.toString(), true);
	}
	
	public boolean isSpawnPointUsed(Position spawnPoint){
		Boolean val = spawnPointsRegistry.get(spawnPoint.toString());
		return (val != null && val);
	}
	
	
	

	
}
