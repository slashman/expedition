package net.slashie.expedition.level;

import net.slashie.expedition.world.ExpeditionLevel;
import net.slashie.serf.level.BufferedLevel;
import net.slashie.utils.Position;

public abstract class BufferedExpeditionLevel extends BufferedLevel implements ExpeditionLevel{
	private ExpeditionLevelHelper helper = new ExpeditionLevelHelper();
	
	public String getMusicKey() {
		return helper.getMusicKey();
	}

	public String getSuperLevelId() {
		return helper.getSuperLevelId();
	}
	
	public ExpeditionLevelHelper getHelper(){
		return helper;
	}

	public boolean isSpawnPointUsed(Position spawnPoint) {
		return helper.isSpawnPointUsed(spawnPoint);
	}

	public void setSpawnPointUsed(Position spawnPoint) {
		helper.setSpawnPointUsed(spawnPoint);
	}
	
	

}
