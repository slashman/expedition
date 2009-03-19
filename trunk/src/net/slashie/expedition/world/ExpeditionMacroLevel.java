package net.slashie.expedition.world;

import java.util.Hashtable;
import net.slashie.expedition.level.ExpeditionLevelReader;
import net.slashie.util.Pair;
import net.slashie.utils.Position;

public class ExpeditionMacroLevel extends ExpeditionLevelReader{
	public ExpeditionMacroLevel(String levelNameset, int levelWidth,
			int levelHeight, int gridWidth, int gridHeight,
			Hashtable<String, String> charmap, Pair<String, Position> mainExit) {
		super(levelNameset, levelWidth, levelHeight, gridWidth, gridHeight, charmap,
				mainExit);
	}

	private Pair<Integer,Integer> handyReusablePair = new Pair<Integer, Integer>(0,0);
	
	/*private List<GoodsCache> cacheList = new ArrayList<GoodsCache>();
	private Hashtable<String, GoodsCache> cacheHash = new Hashtable<String, GoodsCache>();*/
	@Override
	public Pair<Integer, Integer> getLocation() {
		handyReusablePair.setA(resolveXToLongitude());
		handyReusablePair.setB(resolveYToLatitude());
		return handyReusablePair;
	}
	
	private int resolveXToLongitude(){
		return (int)Math.round((getPlayer().getPosition().x() - 3340)/19.6d); 
	}
	
	private int resolveYToLatitude(){
		return (int)Math.round((getPlayer().getPosition().y() - 1572)/19.47d); 
	}
	
	/*public void addCache(Position where, GoodsCache cache){
		addFeature(cache);
		/*cacheList.add(cache);
		cacheHash.put(where.toString(), cache);
	}*/
	/*
	public GoodsCache getCacheAt(Position where){
		return cacheHash.get(where.toString());
	}*/
	
	@Override
	public int getTemperature() {
		// TODO Auto-generated method stub
		return 12;
	}
	
	@Override
	public String getWeather() {
		// TODO Auto-generated method stub
		return "Calm";
	}
	
	@Override
	public Pair<String, String> getLocationDescription() {
		// TODO Auto-generated method stub
		return null;
	}
	

}
