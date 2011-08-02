package net.slashie.expedition.level;

import java.util.Hashtable;
import java.util.List;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.ExpeditionFactory;
import net.slashie.expedition.domain.NPC;
import net.slashie.expedition.town.NPCFactory;
import net.slashie.expedition.world.ExpeditionLevel;
import net.slashie.serf.action.Actor;
import net.slashie.serf.action.AwareActor;
import net.slashie.serf.level.AbstractCell;
import net.slashie.serf.level.AbstractFeature;
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
		x = GlobeMapModel.normalizeLong(y, x);
		int gridY = GlobeMapModel.transformLatIntoY(y);
		int gridX = GlobeMapModel.transformLongIntoX(x);
		// The map may be incomplete
		if (gridY < 0 || gridY > super.getHeight())
			return null;
		return super.getMapCell(gridX, gridY, z);
	}
	
	protected void darken(int x, int y, int z) {
		int gridX = GlobeMapModel.transformLongIntoX(x);
		int gridY = GlobeMapModel.transformLatIntoY(y);
		if (gridY < 0 || gridY > super.getHeight())
			return;
		super.darken(gridX, gridY, z);
	}
	
	public boolean isVisible(int x, int y, int z) {
		int gridX = GlobeMapModel.transformLongIntoX(x);
		int gridY = GlobeMapModel.transformLatIntoY(y);
		if (gridY < 0 || gridY > super.getHeight())
			return false;
		return super.isVisible(gridX, gridY, z);
	}
	
	private Position tempSeen = new Position(0,0);

	public void setSeen(int x, int y) {
		tempSeen.x = x; tempSeen.y = y; tempSeen.z = getPlayer().getPosition().z;
		markVisible(x, y, getPlayer().getPosition().z);
		markRemembered(x, y, getPlayer().getPosition().z);
		Actor m = getActorAt(tempSeen);
		if (m != null){
			m.setWasSeen(true);
		}
	}
	
	protected void markLit(int x, int y, int z) {
		int gridX = GlobeMapModel.transformLongIntoX(x);
		int gridY = GlobeMapModel.transformLatIntoY(y);
		if (gridY < 0 || gridY > super.getHeight())
			return;
		super.markLit(gridX, gridY, z);
	}

	@Override
	protected void markRemembered(int x, int y, int z) {
		int gridX = GlobeMapModel.transformLongIntoX(x);
		int gridY = GlobeMapModel.transformLatIntoY(y);
		if (gridY < 0 || gridY > super.getHeight())
			return;
		super.markRemembered(gridX, gridY, z);
	}

	@Override
	protected void markVisible(int x, int y, int z) {
		int gridX = GlobeMapModel.transformLongIntoX(x);
		int gridY = GlobeMapModel.transformLatIntoY(y);
		if (gridY < 0 || gridY > super.getHeight())
			return;
		super.markVisible(gridX, gridY, z);	
	}

	@Override
	protected boolean remembers(int x, int y, int z) {
		int gridX = GlobeMapModel.transformLongIntoX(x);
		int gridY = GlobeMapModel.transformLatIntoY(y);
		if (gridY < 0 || gridY > super.getHeight())
			return false;
		return super.remembers(gridX, gridY, z);	
	}
	
	@Override
	protected boolean isLit(Position p) {
		p.x = GlobeMapModel.transformLongIntoX(p.x());
		p.y = GlobeMapModel.transformLatIntoY(p.y());
		if (p.y < 0 || p.y > super.getHeight())
			return false;
		return super.isLit(p);	
	}
	
	public boolean isValidCoordinate(int longMinutes, int latMinutes){
		return 	! (longMinutes <= -180*60 ||
					latMinutes <= -90 * 60  ||
					longMinutes >= 180 * 60 ||
					latMinutes >= 90*60);
	}
	
	/**
	 * This acts as a proxy where x and y are translated into lat and long
	 * 
	 * @param x Expedition references entities with longitude instead of x
	 * @param y Expedition references entities with latitude instead of y
	 */
	@Override
	public AbstractCell[][] getVisibleCellsAround(AwareActor watcher, 
			int longMinutes,
			int latMinutes, 
			int z, int xspan, int yspan) {
		int longitudeScale = GlobeMapModel.getLongitudeScale(latMinutes);
		int latitudeScale= GlobeMapModel.getLatitudeHeight();
		
		int xstart = longMinutes - xspan * longitudeScale;
		int xend = longMinutes + xspan * longitudeScale;
		
		int ystart = latMinutes - yspan * latitudeScale;
		int yend = latMinutes + yspan * latitudeScale;

		AbstractCell [][] ret = new AbstractCell [2 * xspan + 1][2 * yspan + 1];
		int px = 0;
		int visible = 0;
		for (int ilong = xstart; ilong <=xend; ilong+=longitudeScale){
			int py = 0;
			for (int ilat =  ystart ; ilat <= yend; ilat+=latitudeScale){
				int iilong = ilong;
				int iilat = ilat;
				
				/* TODO: Pole Mirror */
				/*if (ilat < 0){
					iilat = ilat * -1;
					iilong = (ilong + 180*60) % (360*60); 
				}*/
				
				if (isVisible(iilong, iilat, z)){
					ret[px][(2 * yspan) - py] = getMapCell(iilong, iilat, z);
					watcher.seeMapCell(ret[px][py]);
					visible++;
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
		} else if (cmds[1].equals("EXIT_GLOBE_COORDINATES")){
			addExit(new Position(GlobeMapModel.transformXIntoLong(where.x+xoff),GlobeMapModel.transformYIntoLat(where.y+yoff),where.z), cmds[2]);
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
	
	
	/**
	 * Returns the actor *around* x.
	 * An actor may ocuppy more than one cells wide, based on the magnification level
	 * determined by his latitude. 
	 */
	private Position recyclablePosition = new Position(0,0);
	public Actor getActorAt(Position x){
		int magnificationLevel = GlobeMapModel.getLongitudeScale(x.y());
		int start = x.x() - (int)Math.round(magnificationLevel/2.0d);
		recyclablePosition.y = x.y();
		for (int xrow = start; xrow < start + magnificationLevel; xrow ++){
			recyclablePosition.x = xrow;
			Actor a = super.getActorAt(recyclablePosition);
			if (a != null)
				return a;
		}
		return null;
	}
	
	@Override
	public String getExitOn(Position pos) {
		int magnificationLevel = GlobeMapModel.getLongitudeScale(pos.y());
		int start = pos.x() - (int)Math.round(magnificationLevel/2.0d);
		recyclablePosition.y = pos.y();
		for (int xrow = start; xrow < start + magnificationLevel; xrow ++){
			recyclablePosition.x = xrow;
			String exit = super.getExitOn(recyclablePosition);
			if (exit != null)
				return exit;
		}
		return null;
	}
	
	@Override
	public List<AbstractFeature> getFeaturesAt(Position pos){
		int magnificationLevel = GlobeMapModel.getLongitudeScale(pos.y());
		int start = pos.x() - (int)Math.round(magnificationLevel/2.0d);
		recyclablePosition.y = pos.y();
		for (int xrow = start; xrow < start + magnificationLevel; xrow ++){
			recyclablePosition.x = xrow;
			List<AbstractFeature> ret = super.getFeaturesAt(recyclablePosition);
			if (ret != null)
				return ret;
		}
		return null;
	}

}
