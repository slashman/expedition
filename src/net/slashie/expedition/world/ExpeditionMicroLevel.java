package net.slashie.expedition.world;

import net.slashie.expedition.level.BufferedExpeditionLevel;
import net.slashie.util.Pair;

public class ExpeditionMicroLevel extends BufferedExpeditionLevel{
	private boolean dock;
	private Pair<Integer, Integer> location;
	
	
	
	public void setDock(boolean dock) {
		this.dock = dock;
	}
	public boolean isDock() {
		return dock;
	}
	public void setLocation(Pair<Integer, Integer> location) {
		this.location = location;
	}
	public Pair<Integer, Integer> getLocation() {
		return location;
	}
	
	/*private Pair<String,String> handyReusableObject = new Pair<String, String>("H","H");
	public Pair<String,String> getLocationDescription(){
		
		Pair<Integer, Integer> location = getLocation();
		handyReusableObject.setA(Math.abs(location.getA()) + (location.getA() > 0?"N":"S"));
		handyReusableObject.setB(Math.abs(location.getB()) + (location.getB() > 0?"W":"E"));
		
		return handyReusableObject;
	}*/
	
	private Pair<String,String> handyReusableObject = new Pair<String, String>("H","H");
	public Pair<String,String> getLocationDescription(){
		Pair<Integer, Integer> location = getLocation();
		handyReusableObject.setA("(Land) "+Math.abs(location.getA()) + (location.getA() > 0?"N":"S"));
		//This is the real longitude calculation:
		handyReusableObject.setB("(Land) "+Math.abs(location.getB()) + (location.getB() > 0?"E":"W"));
		//handyReusableObject.setB("West (Dead):   "+expeditionLevel.get);
		return handyReusableObject;
	}

	public int getTemperature() {
		// TODO Auto-generated method stub
		return 12;
	}
	
	public String getWeather() {
		// TODO Auto-generated method stub
		return "Calm";
	}
	
}
