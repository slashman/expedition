package net.slashie.expedition.level;

import java.util.Hashtable;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.ExpeditionFactory;
import net.slashie.expedition.domain.ExpeditionUnit;
import net.slashie.expedition.domain.NPC;
import net.slashie.expedition.item.ItemFactory;
import net.slashie.expedition.town.NPCFactory;
import net.slashie.expedition.world.ExpeditionLevel;
import net.slashie.serf.level.Dispatcher;
import net.slashie.serf.level.GridLevelReader;
import net.slashie.util.Pair;
import net.slashie.utils.Position;

public abstract class ExpeditionLevelReader extends GridLevelReader implements ExpeditionLevel{
	private ExpeditionLevelHelper helper;

	public ExpeditionLevelReader(String levelNameset, 
			int levelWidth,
			int levelHeight, int gridWidth, int gridHeight, 
			Hashtable<String, String> charmap, Position startPosition) {
		super(levelNameset, levelWidth, levelHeight, gridWidth, gridHeight, charmap,
				startPosition);
	}

	
	public String getMusicKey() {
		return helper.getMusicKey();
	}

	public String getSuperLevelId() {
		return helper.getSuperLevelId();
	}
	

	@Override
	public void handleSpecialRenderCommand(Position where, String[] cmds,
			int xoff, int yoff) {
		if (cmds[1].equals("EXPEDITION")){
			//Creates an expedition once (Generated expeditions are recorded)
			Position p = new Position(where.x+xoff,where.y+yoff,where.z);
			if (!isSpawnPointUsed(p)){
				Expedition expedition = ExpeditionFactory.getExpedition(cmds[2],2);
				expedition.setPosition(where.x+xoff,where.y+yoff,where.z);
				addActor(expedition);
				setSpawnPointUsed(p);
			}
		} else if (cmds[1].equals("NPC")){
			NPC npc = NPCFactory.createNPC(cmds[2]);
			npc.setPosition(where.x+xoff,where.y+yoff,where.z);
			addActor(npc);
		}
	}
	
	public Expedition getExpedition(){
		return (Expedition) getPlayer();
	}
	
	public boolean isSpawnPointUsed(Position spawnPoint) {
		return getHelper().isSpawnPointUsed(spawnPoint);
	}

	public void setSpawnPointUsed(Position spawnPoint) {
		getHelper().setSpawnPointUsed(spawnPoint);
	}
	
	public ExpeditionLevelHelper getHelper(){
		if (helper == null)
			helper = new ExpeditionLevelHelper();
		return helper;
	}
}
