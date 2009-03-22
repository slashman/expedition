package net.slashie.expedition.world;

import java.util.Hashtable;
import net.slashie.expedition.level.ExpeditionLevelReader;
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
	
	public Pair<String, String> getLocationDescription() {
		return getHelper().getLocationDescription();
	}
	

}
