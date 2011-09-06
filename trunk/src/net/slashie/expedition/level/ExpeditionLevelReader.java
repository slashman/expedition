package net.slashie.expedition.level;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.ExpeditionFactory;
import net.slashie.expedition.domain.NPC;
import net.slashie.expedition.town.NPCFactory;
import net.slashie.expedition.world.ExpeditionLevel;
import net.slashie.serf.action.Actor;
import net.slashie.serf.action.AwareActor;
import net.slashie.serf.action.EnvironmentInfo;
import net.slashie.serf.baseDomain.AbstractItem;
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
	
	protected void darken(int x, int y, int z) {
		int gridX = GlobeMapModel.getSingleton().transformLongIntoX(x);
		int gridY = GlobeMapModel.getSingleton().transformLatIntoY(y);
		if (gridY < 0 || gridY > super.getHeight())
			return;
		super.darken(gridX, gridY, z);
	}
	
	public boolean isVisible(int x, int y, int z) {
		int gridX = GlobeMapModel.getSingleton().transformLongIntoX(x);
		int gridY = GlobeMapModel.getSingleton().transformLatIntoY(y);
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
		List<AbstractFeature> f = getFeaturesAt(tempSeen);
		if (f != null && f.size() > 0){
			for (AbstractFeature feature: f){
				feature.setWasSeen(true);
			}
		}
	}
	
	protected void markLit(int x, int y, int z) {
		int gridX = GlobeMapModel.getSingleton().transformLongIntoX(x);
		int gridY = GlobeMapModel.getSingleton().transformLatIntoY(y);
		if (gridY < 0 || gridY > super.getHeight())
			return;
		super.markLit(gridX, gridY, z);
	}

	@Override
	protected void markRemembered(int x, int y, int z) {
		int gridX = GlobeMapModel.getSingleton().transformLongIntoX(x);
		int gridY = GlobeMapModel.getSingleton().transformLatIntoY(y);
		if (gridY < 0 || gridY > super.getHeight())
			return;
		super.markRemembered(gridX, gridY, z);
	}

	@Override
	protected void markVisible(int x, int y, int z) {
		int gridX = GlobeMapModel.getSingleton().transformLongIntoX(x);
		int gridY = GlobeMapModel.getSingleton().transformLatIntoY(y);
		if (gridY < 0 || gridY > super.getHeight())
			return;
		super.markVisible(gridX, gridY, z);	
	}

	@Override
	protected boolean remembers(int x, int y, int z) {
		int gridX = GlobeMapModel.getSingleton().transformLongIntoX(x);
		int gridY = GlobeMapModel.getSingleton().transformLatIntoY(y);
		if (gridY < 0 || gridY > super.getHeight())
			return false;
		return super.remembers(gridX, gridY, z);	
	}
	
	@Override
	protected boolean isLit(Position p) {
		p.x = GlobeMapModel.getSingleton().transformLongIntoX(p.x());
		p.y = GlobeMapModel.getSingleton().transformLatIntoY(p.y());
		if (p.y < 0 || p.y > super.getHeight())
			return false;
		return super.isLit(p);	
	}
	
	public boolean isValidCoordinate(int longMinutes, int latMinutes){
		return GlobeMapModel.getSingleton().isValidCoordinate(longMinutes, latMinutes);
	}
	
	/**
	 * This acts as a proxy where x and y are translated into lat and long
	 * 
	 * @param x Expedition references entities with longitude instead of x
	 * @param y Expedition references entities with latitude instead of y
	 */
	@Override
	public EnvironmentInfo getEnvironmentAroundActor(AwareActor watcher, int longMinutes, int latMinutes, int z, int xspan, int yspan) {
		EnvironmentInfo ret = new EnvironmentInfo();

		int latitudeScale= GlobeMapModel.getSingleton().getLatitudeHeight();
		int ystart = latMinutes - yspan * latitudeScale;
		int yend = latMinutes + yspan * latitudeScale;

		AbstractCell [][] cellsAround = new AbstractCell [2 * xspan + 1][2 * yspan + 1];
		int py = 0;
		
		int visible = 0;
		Position runner = new Position(0,0);
		for (int ilat =  ystart ; ilat <= yend; ilat+=latitudeScale){
			int px = 0;
			runner.y = ilat;
			int longitudeScale = GlobeMapModel.getSingleton().getLongitudeScale(ilat);
			int xstart = longMinutes - xspan * longitudeScale;
			int xend = longMinutes + xspan * longitudeScale;

			for (int ilong = xstart; ilong <=xend; ilong+=longitudeScale){
				runner.x = ilong;
				int iilong = ilong;
				int iilat = ilat;
				
				/* TODO: Pole Mirror */
				/*if (ilat < 0){
					iilat = ilat * -1;
					iilong = (ilong + 180*60) % (360*60); 
				}*/
				
				if (isVisible(iilong, iilat, z)){
					cellsAround[px][(2 * yspan) - py] = getMapCell(iilong, iilat, z);
					watcher.seeMapCell(cellsAround[px][py]);
					visible++;
					
					List<AbstractFeature> feats = getFeaturesAt(runner);
					if (feats != null){
						ret.addFeature(px-xspan, yspan-py, feats);
					}
					
					List<AbstractItem> items = getItemsAt(runner);
					AbstractItem item = null;
					if (items != null){
						item = items.get(0);
					}
					if (item != null){
						if (item.isVisible()){
							ret.addItem(px-xspan, yspan-py, item);
						}
					}
					
					Actor actor = getActorAt(runner);
					if (actor != getPlayer() && actor != null && !actor.isInvisible()){
						ret.addActor(px-xspan, yspan-py, actor);
					}
					
				}
				px++;
			}
			py++;
		}
		ret.setCellsAround(cellsAround);
		return ret;
	}
	
	public List<AbstractFeature> getFeaturesAround(AwareActor watcher, 
			int longMinutes,
			int latMinutes, 
			int z, int xspan, int yspan) {
		int longitudeScale = GlobeMapModel.getSingleton().getLongitudeScale(latMinutes);
		int latitudeScale= GlobeMapModel.getSingleton().getLatitudeHeight();
		
		int xstart = longMinutes - xspan * longitudeScale;
		int xend = longMinutes + xspan * longitudeScale;
		
		int ystart = latMinutes - yspan * latitudeScale;
		int yend = latMinutes + yspan * latitudeScale;

		List<AbstractFeature>  ret = new ArrayList<AbstractFeature>();
		int px = 0;
		Position pos = new Position(0,0);
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
				pos.x = iilong;
				pos.y = iilat;
				List<AbstractFeature> featuresAt = getFeaturesAt(pos); 
				if (featuresAt != null){
					ret.addAll(featuresAt);
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
			addActor(npc);
			npc.setPosition(where.x+xoff,where.y+yoff,where.z);
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
		int magnificationLevel = GlobeMapModel.getSingleton().getLongitudeScale(x.y());
		//int start = x.x() - (int)Math.round(magnificationLevel/2.0d);
		int start = GlobeMapModel.getSingleton().normalizeLong(x.y(), x.x());
		recyclablePosition.y = x.y();
		for (int xrow = start; xrow < start + magnificationLevel; xrow ++){
			recyclablePosition.x = xrow;
			Actor a = super.getActorAt(recyclablePosition);
			if (a != null)
				return a;
		}
		return null;
	}
	
	/**
	 * Bold move, this acts as a proxy where x and y are translated into lat and long
	 * @param x Expedition references entities with longitude instead of x
	 * @param y Expedition references entities with latitude instead of y
	 */
	@Override
	public AbstractCell getMapCell(int x, int y, int z) {
		x = GlobeMapModel.getSingleton().normalizeLong(y, x);
		y = GlobeMapModel.getSingleton().normalizeLat(y);
		int gridY = GlobeMapModel.getSingleton().transformLatIntoY(y);
		int gridX = GlobeMapModel.getSingleton().transformLongIntoX(x);
		// The map may be incomplete
		if (gridY < 0 || gridY > super.getHeight())
			return null;
		return super.getMapCell(gridX, gridY, z);
	}
	
	
	/**
	 * Normalize lat and long, rounding them to the nearest reference points
	 */
	@Override
	public String getExitOn(Position pos) {
		int x = GlobeMapModel.getSingleton().normalizeLong(pos.y, pos.x);
		int y = GlobeMapModel.getSingleton().normalizeLat(pos.y);
		recyclablePosition.y = y;
		recyclablePosition.x = x;
		return super.getExitOn(recyclablePosition);

		/*int x = GlobeMapModel.normalizeLong(pos.y, pos.x);
		int y = GlobeMapModel.normalizeLat(pos.y);
		recyclablePosition.y = GlobeMapModel.transformLatIntoY(y);
		recyclablePosition.x = GlobeMapModel.transformLongIntoX(x);
		return super.getExitOn(recyclablePosition);*/
	}
	
	public Position getExitFor(String levelID){
		Position exitPosition = super.getExitFor(levelID);
		Position normalized = new Position(exitPosition);
		
		normalized.x = GlobeMapModel.getSingleton().normalizeLong(normalized.y, normalized.x);
		normalized.y = GlobeMapModel.getSingleton().normalizeLat(normalized.y);
		return normalized;
	}

	/**
	 * Normalize lat and long, rounding them to the nearest reference points
	 */
	@Override
	public List<AbstractFeature> getFeaturesAt(Position pos){
		int x = GlobeMapModel.getSingleton().normalizeLong(pos.y, pos.x);
		int y = GlobeMapModel.getSingleton().normalizeLat(pos.y);
		recyclablePosition.y = y;
		recyclablePosition.x = x;
		return super.getFeaturesAt(recyclablePosition);
	}
	
	/**
	 * Normalize lat and long, rounding them to the nearest reference points
	 */
	@Override
	public void addFeature(AbstractFeature feature){
		int x = GlobeMapModel.getSingleton().normalizeLong(feature.getPosition().y, feature.getPosition().x);
		int y = GlobeMapModel.getSingleton().normalizeLat(feature.getPosition().y);
		feature.setPosition(x, y, 0);
		super.addFeature(feature, false);
	}

}
