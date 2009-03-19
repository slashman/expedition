package net.slashie.expedition.world;

import net.slashie.expedition.level.BufferedExpeditionLevel;
import net.slashie.util.Pair;

public class ExpeditionMicroLevel extends BufferedExpeditionLevel{
	private Pair<Integer, Integer> location;
	public void setLocation(Pair<Integer, Integer> location) {
		this.location = location;
	}
	@Override
	public Pair<Integer, Integer> getLocation() {
		return location;
	}
	
	private Pair<String,String> handyReusableObject = new Pair<String, String>("H","H");
	public Pair<String,String> getLocationDescription(){
		Pair<Integer, Integer> location = getLocation();
		handyReusableObject.setA(Math.abs(location.getA()) + (location.getA() > 0?"N":"S"));
		handyReusableObject.setB(Math.abs(location.getB()) + (location.getB() > 0?"W":"E"));
		
		return handyReusableObject;
	}

	@Override
	public int getTemperature() {
		// TODO Auto-generated method stub
		return -5;
	}
	
	@Override
	public String getWeather() {
		// TODO Auto-generated method stub
		return "Sunny";
	}
	
}
