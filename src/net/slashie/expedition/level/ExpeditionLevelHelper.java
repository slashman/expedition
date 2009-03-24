package net.slashie.expedition.level;

import java.io.Serializable;

import net.slashie.expedition.world.ExpeditionLevel;
import net.slashie.util.Pair;

public class ExpeditionLevelHelper implements Serializable{
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


	
}
