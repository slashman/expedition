package net.slashie.expedition.level;

import java.util.Hashtable;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.ExpeditionFactory;
import net.slashie.expedition.world.ExpeditionLevel;
import net.slashie.serf.level.Dispatcher;
import net.slashie.serf.level.GridLevelReader;
import net.slashie.util.Pair;
import net.slashie.utils.Position;

public abstract class ExpeditionLevelReader extends GridLevelReader implements ExpeditionLevel{
	
	public ExpeditionLevelReader(String levelNameset, 
			int levelWidth,
			int levelHeight, int gridWidth, int gridHeight, 
			Hashtable<String, String> charmap, Position startPosition) {
		super(levelNameset, levelWidth, levelHeight, gridWidth, gridHeight, charmap,
				startPosition);
	}

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
	
	@Override
	public void handleSpecialRenderCommand(Position where, String[] cmds,
			int xoff, int yoff) {
		if (cmds[1].equals("EXPEDITION")){
			Expedition expedition = ExpeditionFactory.getExpedition(cmds[2]);
			expedition.setPosition(where.x+xoff,where.y+yoff,where.z);
			addActor(expedition);
		}
	}
}
