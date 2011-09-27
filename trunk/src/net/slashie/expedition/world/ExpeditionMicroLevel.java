package net.slashie.expedition.world;

import java.util.Calendar;

import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.game.ExpeditionMusicManager;
import net.slashie.expedition.level.BufferedExpeditionLevel;
import net.slashie.expedition.ui.ExpeditionUserInterface;
import net.slashie.serf.action.Message;
import net.slashie.util.Pair;
import net.slashie.utils.Position;

@SuppressWarnings("serial")
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
	
	private Pair<String,String> handyReusableObject = new Pair<String, String>("","");
	public Pair<String,String> getLocationDescription(){
		return handyReusableObject;
	}
	
	private Pair<String,String> handyReusableObject2 = new Pair<String, String>("","");

	public Pair<String,String> getLocationMeans(){
		return handyReusableObject2;
	}
	
	private Pair<String,String> handyReusableObject3 = new Pair<String, String>("","");
	
	@Override
	public Pair<String, String> getLocationLabels() {
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
	
	public void addMessage(String what, Position where){
		addMessage(new Message(what, where, formatTime(ExpeditionGame.getCurrentGame().getGameTime())));
	}

	private String formatTime(Calendar gameTime) {
		return ExpeditionUserInterface.months[gameTime.get(Calendar.MONTH)] +" "+ gameTime.get(Calendar.DATE)+", "+getTimeDescriptionFromHour(gameTime.get(Calendar.HOUR_OF_DAY));
	}
	
	public static String getTimeDescriptionFromHour(int i) {
		if (i > 22){
			return "Midnight";
		} else if (i > 18){
			return "Night";
		} else if (i > 14){
			return "Afternoon";
		} else if (i > 10){
			return "Noon";
		} else if (i > 6){
			return "Morning";
		} else if (i > 4){
			return "Dawn";
		} else {
			return "Midnight";
		}
	}
}
