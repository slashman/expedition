package net.slashie.expedition.world;

import java.util.Hashtable;
import java.util.List;

import net.slashie.expedition.level.ExpeditionLevelReader;
import net.slashie.serf.level.AbstractFeature;
import net.slashie.serf.level.Dispatcher;
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
		
		handyReusableObject.setA("(Sext) "+Math.abs(location.getA()) + (location.getA() > 0?"N":"S"));
		//This is the real longitude calculation:
		//handyReusableObject.setB(Math.abs(location.getB()) + (location.getB() > 0?"E":"W"));
		handyReusableObject.setB("(DRek) ---");
		return handyReusableObject;
	}
	
	@Override
	public void updateLevelStatus() {
		super.updateLevelStatus();
	}


}
