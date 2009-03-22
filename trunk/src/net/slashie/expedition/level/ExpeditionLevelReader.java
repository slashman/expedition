package net.slashie.expedition.level;

import java.util.Hashtable;

import net.slashie.expedition.world.ExpeditionLevel;
import net.slashie.serf.level.GridLevelReader;
import net.slashie.util.Pair;
import net.slashie.utils.Position;

public abstract class ExpeditionLevelReader extends GridLevelReader implements ExpeditionLevel{
	
	public ExpeditionLevelReader(String levelNameset, int levelWidth,
			int levelHeight, int gridWidth, int gridHeight,
			Hashtable<String, String> charmap, Pair<String, Position> mainExit) {
		super(levelNameset, levelWidth, levelHeight, gridWidth, gridHeight, charmap,
				mainExit);
	}

	private ExpeditionLevelHelper helper = new ExpeditionLevelHelper(this);
	
	@Override
	public String getMusicKey() {
		return helper.getMusicKey();
	}

	@Override
	public String getSuperLevelId() {
		return helper.getSuperLevelId();
	}
	
	public ExpeditionLevelHelper getHelper(){
		return helper;
	}
}
