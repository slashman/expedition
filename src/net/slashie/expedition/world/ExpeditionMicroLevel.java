package net.slashie.expedition.world;

import net.slashie.expedition.level.BufferedExpeditionLevel;
import net.slashie.util.Pair;
import net.slashie.utils.Position;

public class ExpeditionMicroLevel extends BufferedExpeditionLevel{
	private boolean dock;
	private Pair<Integer, Integer> location;
	
	private boolean isZoomIn;
	
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
		handyReusableObject.setA("LAT  "+Math.abs(location.getA()) + (location.getA() > 0?"�N":"�S"));
		//This is the real longitude calculation:
		handyReusableObject.setB("LONG "+Math.abs(location.getB()) + (location.getB() > 0?"�E":"�W"));
		//handyReusableObject.setB("West (Dead):   "+expeditionLevel.get);
		return handyReusableObject;
	}
	
	private Pair<String,String> handyReusableObject2 = new Pair<String, String>("H","H");

	public Pair<String,String> getLocationMeans(){
		handyReusableObject2.setA("Land");
		handyReusableObject2.setB("Land");
		return handyReusableObject2;
	}


	public String getTemperatureDescription() {
		return "Warm";
	}
	
	public Weather getWeather() {
		return Weather.CLEAR;
	}
	public boolean isZoomIn() {
		return isZoomIn;
	}
	public void setZoomIn(boolean isZoomIn) {
		this.isZoomIn = isZoomIn;
	}

	@Override
	public CardinalDirection getWindDirection() {
		return CardinalDirection.NULL;
	}
	
	@Override
	public void elapseTime(int lastActionTimeCost) {
		// TODO Auto-generated method stub
	}
	@Override
	public boolean hasStorm(Position destinationPoint) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
