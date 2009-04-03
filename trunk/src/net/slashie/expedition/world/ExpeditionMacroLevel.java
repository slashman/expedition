package net.slashie.expedition.world;

import java.util.Hashtable;
import java.util.List;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.ExpeditionItem;
import net.slashie.expedition.domain.GoodsCache;
import net.slashie.expedition.domain.SeaPseudoCache;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.level.ExpeditionLevelReader;
import net.slashie.expedition.ui.ExpeditionUserInterface;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.game.Player;
import net.slashie.serf.level.AbstractFeature;
import net.slashie.serf.level.Dispatcher;
import net.slashie.serf.ui.UserInterface;
import net.slashie.util.Pair;
import net.slashie.utils.Position;

public class ExpeditionMacroLevel extends ExpeditionLevelReader{
	public ExpeditionMacroLevel(String levelNameset, int levelWidth,
			int levelHeight, int gridWidth, int gridHeight,
			Hashtable<String, String> charmap, Position startPosition) {
		super(levelNameset, levelWidth, levelHeight, gridWidth, gridHeight, charmap,
				startPosition);
	}

	private Pair<Integer,Integer> handyReusablePair = new Pair<Integer, Integer>(0,0);
	

	public Pair<Integer, Integer> getLocation() {
		handyReusablePair.setA(resolveYToLatitude());
		handyReusablePair.setB(resolveXToLongitude());
		return handyReusablePair;
	}
	
	private int resolveXToLongitude(){
		return (int)Math.round((getPlayer().getPosition().x() - 3340)/19.6d); 
	}
	
	private int resolveYToLatitude(){
		return (int)Math.round((getPlayer().getPosition().y() - 1572)/-19.47d); 
	}
	
	public int getTemperature() {
		// TODO Auto-generated method stub
		return 12;
	}
	
	public String getWeather() {
		// TODO Auto-generated method stub
		return "Calm";
	}
	
	private Pair<String,String> handyReusableObject = new Pair<String, String>("H","H");
	public Pair<String,String> getLocationDescription(){
		Pair<Integer, Integer> location = getLocation();
		
		handyReusableObject.setA("LAT  (Sext)  "+Math.abs(location.getA()) + (location.getA() > 0?"ºN":"ºS"));
		//This is the real longitude calculation:
		//handyReusableObject.setB(Math.abs(location.getB()) + (location.getB() > 0?"E":"W"));
		if (getExpedition().getDeducedReckonWest()>0)
			handyReusableObject.setB("West (DReck) "+getExpedition().getDeducedReckonWest()+"nl");
		else
			handyReusableObject.setB("East (DReck) "+(-getExpedition().getDeducedReckonWest())+"nl");
		return handyReusableObject;
	}
	
	@Override
	public void updateLevelStatus() {
		super.updateLevelStatus();
	}
	
	
	public void addEquipment(ExpeditionItem item, int quantity, Position where){
		if (((OverworldExpeditionCell) getMapCell(where)).isLand()){
			AbstractFeature feature = getFeatureAt(where);
			GoodsCache cache = null;
			boolean newCache = false;
			if (feature != null && feature instanceof GoodsCache){
				cache = (GoodsCache) feature;
			} else {
				cache = new GoodsCache(ExpeditionGame.getCurrentGame());
				cache.setPosition(new Position(where));
				newCache = true;
			}
			cache.addItem(item, quantity);
			if (newCache && cache.getItems().size() > 0)
				addFeature(cache);
		} else {
			//Drop things into the big sea
		}
	}

	public void addAllEquipment(Expedition expedition, Position where) {
		if (((OverworldExpeditionCell) getMapCell(where)).isLand()){
			AbstractFeature feature = getFeatureAt(where);
			GoodsCache cache = null;
			boolean newCache = false;
			if (feature != null && feature instanceof GoodsCache){
				cache = (GoodsCache) feature;
			} else {
				cache = new GoodsCache(ExpeditionGame.getCurrentGame());
				cache.setPosition(new Position(where));
				newCache = true;
			}
			cache.addAllGoods(expedition);
			if (newCache && cache.getItems().size() > 0)
				addFeature(cache);
		} else {
			//Drop things into the big sea
		}
	}


}
