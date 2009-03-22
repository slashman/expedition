package net.slashie.expedition.level;

import net.slashie.expedition.world.ExpeditionLevel;
import net.slashie.serf.level.BufferedLevel;

public abstract class BufferedExpeditionLevel extends BufferedLevel implements ExpeditionLevel{
	private ExpeditionLevelHelper helper = new ExpeditionLevelHelper(this);
	
	public String getMusicKey() {
		return helper.getMusicKey();
	}

	public String getSuperLevelId() {
		return helper.getSuperLevelId();
	}
	
	public ExpeditionLevelHelper getHelper(){
		return helper;
	}

}
