package net.slashie.expedition.world;

import net.slashie.serf.level.AbstractLevel;
import net.slashie.util.Pair;

public abstract class ExpeditionLevel extends AbstractLevel{
	private String musicKey;
	private String superLevelId;
	private String weather;
	
	public void setWeather(String weather) {
		this.weather = weather;
	}

	public abstract Pair<Integer, Integer> getLocation();
	
	private Pair<String,String> handyReusableObject = new Pair<String, String>("H","H");
	public Pair<String,String> getLocationDescription(){
		Pair<Integer, Integer> location = getLocation();
		handyReusableObject.setA(Math.abs(location.getA()) + (location.getA() > 0?"N":"S"));
		handyReusableObject.setB(Math.abs(location.getB()) + (location.getB() > 0?"W":"E"));
		
		return handyReusableObject;
	}
	
	public String getMusicKey() {
		return musicKey;
	}
	public void setMusicKey(String musicKey) {
		this.musicKey = musicKey;
	}
	
	public String getSuperLevelId() {
		return superLevelId;
	}
	public void setSuperLevelId(String superLevelId) {
		this.superLevelId = superLevelId;
	}

	public String getWeather() {
		return "Sunny";
	}

	public int getTemperature() {
		return 12;
	}
	
}
