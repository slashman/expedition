package net.slashie.expedition.world;

import net.slashie.expedition.game.ExpeditionMusicManager;
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
	
	private Pair<String,String> handyReusableObject = new Pair<String, String>("","");
	public Pair<String,String> getLocationDescription(){
		/*Pair<Integer, Integer> location = getLocation();
		handyReusableObject.setA("LAT  "+Math.abs(location.getA()) + (location.getA() > 0?"ºN":"ºS"));
		handyReusableObject.setB("LONG "+Math.abs(location.getB()) + (location.getB() > 0?"ºE":"ºW"));*/
		return handyReusableObject;
	}
	
	private Pair<String,String> handyReusableObject2 = new Pair<String, String>("","");

	public Pair<String,String> getLocationMeans(){
		/*handyReusableObject2.setA("Land");
		handyReusableObject2.setB("Land");*/
		return handyReusableObject2;
	}
	
	private Pair<String,String> handyReusableObject3 = new Pair<String, String>("","");
	
	@Override
	public Pair<String, String> getLocationLabels() {
		/*handyReusableObject3.setA("Land");
		handyReusableObject3.setB("Land");*/
		return handyReusableObject3;
	}

	private int temperature;

	public String getTemperatureDescription() {
		return TemperatureRules.getTemperatureDescription(temperature);
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
	public int getTemperature() {
		return temperature;
	}
	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}
	
	@Override
	public void playMusic() {
		if (getWeather().getMusicKey() != null){
			ExpeditionMusicManager.playTune(getWeather().getMusicKey());
		} else {
			ExpeditionMusicManager.stopWeather();
			ExpeditionMusicManager.playTune(getMusicKey());
		}
	}
	
	@Override
	public void enterLevel() {
	}
}
