package net.slashie.expedition.level;

import java.util.Hashtable;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.ExpeditionFactory;
import net.slashie.expedition.domain.NPC;
import net.slashie.expedition.town.NPCFactory;
import net.slashie.expedition.world.ExpeditionLevel;
import net.slashie.serf.action.AwareActor;
import net.slashie.serf.level.AbstractCell;
import net.slashie.serf.level.GridLevelReader;
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
	
	/**
	 * Bold move, this acts as a proxy where x and y are translated into lat and long
	 * @param x Expedition references entities with longitude instead of x
	 * @param y Expedition references entities with latitude instead of y
	 */
	@Override
	public AbstractCell getMapCell(int x, int y, int z) {
		int gridX = GlobeMapModel.transformLongIntoX(x);
		int gridY = GlobeMapModel.transformLatIntoY(y);
		return super.getMapCell(gridX, gridY, z);
	}
	
	protected void darken(int x, int y, int z) {
		int gridX = GlobeMapModel.transformLongIntoX(x);
		int gridY = GlobeMapModel.transformLatIntoY(y);
		super.darken(gridX, gridY, z);
	}
	
	public boolean isVisible(int x, int y, int z) {
		int gridX = GlobeMapModel.transformLongIntoX(x);
		int gridY = GlobeMapModel.transformLatIntoY(y);
		return super.isVisible(gridX, gridY, z);
	}
	
	protected void markLit(int x, int y, int z) {
		int gridX = GlobeMapModel.transformLongIntoX(x);
		int gridY = GlobeMapModel.transformLatIntoY(y);
		super.markLit(gridX, gridY, z);
	}

	@Override
	protected void markRemembered(int x, int y, int z) {
		int gridX = GlobeMapModel.transformLongIntoX(x);
		int gridY = GlobeMapModel.transformLatIntoY(y);
		super.markRemembered(gridX, gridY, z);
	}

	@Override
	protected void markVisible(int x, int y, int z) {
		int gridX = GlobeMapModel.transformLongIntoX(x);
		int gridY = GlobeMapModel.transformLatIntoY(y);
		super.markVisible(gridX, gridY, z);	
	}

	@Override
	protected boolean remembers(int x, int y, int z) {
		int gridX = GlobeMapModel.transformLongIntoX(x);
		int gridY = GlobeMapModel.transformLatIntoY(y);
		return super.remembers(gridX, gridY, z);	
	}
	
	@Override
	protected boolean isLit(Position p) {
		p.x = GlobeMapModel.transformLongIntoX(p.x());
		p.y = GlobeMapModel.transformLatIntoY(p.y());
		return super.isLit(p);	
	}
	
	public boolean isValidCoordinate(int longMinutes, int latMinutes){
		return 	! (longMinutes <= -180*60 ||
					latMinutes <= -90 * 60  ||
					longMinutes >= 180 * 60 ||
					latMinutes >= 90*60);
	}
	
	/**
	 * Bold move, this acts as a proxy where x and y are translated into lat and long
	 * @param x Expedition references entities with longitude instead of x
	 * @param y Expedition references entities with latitude instead of y
	 */
	@Override
	public AbstractCell[][] getVisibleCellsAround(AwareActor watcher, 
			int longMinutes,
			int latMinutes, 
			int z, int xspan, int yspan) {
		int magnification = GlobeMapModel.getLongitudeScale(latMinutes);
		int xstart = longMinutes - xspan * magnification;
		int ystart = latMinutes - yspan * magnification;
		int xend = longMinutes + xspan * magnification;
		int yend = latMinutes + yspan * magnification;
		AbstractCell [][] ret = new AbstractCell [2 * xspan + 1][2 * yspan + 1];
		int px = 0;
		for (int ilong = xstart; ilong <=xend; ilong+=magnification){
			int py = 0;
			//int ix = transformLongIntoX(ilong);
			for (int ilat =  ystart ; ilat <= yend; ilat+=magnification){
				//int iy = transformLatIntoY(ilat);
				if (isVisible(ilong, ilat, z)){
					ret[px][(2 * yspan) - py] = getMapCell(ilong, ilat, z);
					watcher.seeMapCell(ret[px][py]);
				}
				py++;
			}
			px++;
		}
		return ret;
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
