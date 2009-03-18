package net.slashie.expedition.world;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import net.slashie.expedition.domain.GoodsCache;
import net.slashie.util.Pair;
import net.slashie.utils.Position;

public class ExpeditionMacroLevel extends ExpeditionLevel{
	private Pair<Integer,Integer> handyReusablePair = new Pair<Integer, Integer>(0,0);
	
	private List<GoodsCache> cacheList = new ArrayList<GoodsCache>();
	private Hashtable<String, GoodsCache> cacheHash = new Hashtable<String, GoodsCache>();
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
}
