package net.slashie.expedition.world;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.ExpeditionItem;
import net.slashie.expedition.domain.GoodsCache;
import net.slashie.expedition.domain.SeaPseudoCache;
import net.slashie.expedition.domain.Expedition.MovementMode;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.game.GameFiles;
import net.slashie.expedition.level.ExpeditionLevelReader;
import net.slashie.expedition.ui.ExpeditionUserInterface;
import net.slashie.serf.action.Actor;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.game.Player;
import net.slashie.serf.level.AbstractCell;
import net.slashie.serf.level.AbstractFeature;
import net.slashie.serf.level.Dispatcher;
import net.slashie.serf.level.FeatureFactory;
import net.slashie.serf.level.MapCellFactory;
import net.slashie.serf.ui.Appearance;
import net.slashie.serf.ui.UserInterface;
import net.slashie.util.Pair;
import net.slashie.utils.Position;
import net.slashie.utils.Util;

public class ExpeditionMacroLevel extends ExpeditionLevelReader{
	public ExpeditionMacroLevel(String levelNameset, int levelWidth,
			int levelHeight, int gridWidth, int gridHeight,
			Hashtable<String, String> charmap, Position startPosition) {
		super(levelNameset, levelWidth, levelHeight, gridWidth, gridHeight, charmap,
				startPosition);
	}

	private Pair<Integer,Integer> handyReusablePair = new Pair<Integer, Integer>(0,0);

	private Weather weather = Weather.CLEAR;
	

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
	
	private int currentTemperature = 15;
	private int getTemperature() {
		return currentTemperature;
	}
	
	public String getTemperatureDescription(){
		return TemperatureRules.getTemperatureDescription(getTemperature());
	}
	
	public Weather getWeather() {
		return weather ;
	}
	
	private Pair<String,String> handyReusableObject = new Pair<String, String>("H","H");
	public Pair<String,String> getLocationDescription(){
		Pair<Integer, Integer> location = getLocation();
		
		handyReusableObject.setA("LAT "+Math.abs(location.getA()) + (location.getA() > 0?"ºN":"ºS"));
		//This is the real longitude calculation:
		//handyReusableObject.setB(Math.abs(location.getB()) + (location.getB() > 0?"E":"W"));
		if (getExpedition().getDeducedReckonWest()>0)
			handyReusableObject.setB("West "+getExpedition().getDeducedReckonWest()+"nl");
		else
			handyReusableObject.setB("East "+(-getExpedition().getDeducedReckonWest())+"nl");
		return handyReusableObject;
	}
	private Pair<String,String> handyReusableObject2 = new Pair<String, String>("H","H");

	private int weatherChangeCounter;

	private int tempChangeCounter;

	public Pair<String,String> getLocationMeans(){
		handyReusableObject2.setA("Sextant");
		handyReusableObject2.setB("Ded'Reckon");
		return handyReusableObject2;
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

	@Override
	public boolean isZoomIn() {
		return false;
	}

	@Override
	public CardinalDirection getWindDirection() {
		if (currentWind == null || isTimeToChangeWind()){
			CardinalDirection prevailingWind = getPrevailingWind();
			currentWind = prevailingWind;
			int rotateSign = Util.chance(50) ? 1 : -1;
			int rotate = 0;
			if (Util.chance(70)){
				rotate = Util.rand(0, 2);
			} else if (Util.chance(30)){
				rotate = Util.rand(1, 4);
			}
			for (int i = 0; i < rotate; i++){
				currentWind = currentWind.rotate(rotateSign);
			}
			isTimeToChangeWind = false;
		}
		return currentWind;
	}
	

	@Override
	public void elapseTime(int lastActionTimeCost) {
		windChangeCounter += lastActionTimeCost;
		stormBreedCounter -= lastActionTimeCost;
		stormChangeCounter += lastActionTimeCost;
		weatherChangeCounter -= lastActionTimeCost;
		tempChangeCounter -= lastActionTimeCost;

		if (windChangeCounter > 500){
			isTimeToChangeWind = true;
			windChangeCounter = 0;
		}
		
		if (weatherChangeCounter < 0){
			weatherChange();
			weatherChangeCounter = Util.rand(200,400);
		}
		
		if (tempChangeCounter < 0){
			currentTemperature = (int)Math.round(TemperatureRules.getRulingTemperature(resolveYToLatitude(), ExpeditionGame.getCurrentGame().getGameTime().get(Calendar.MONTH)+1));
			currentTemperature += Util.rand(-5, 5);
			tempChangeCounter = Util.rand(200,400);
		}
		
		if (stormBreedCounter < 0){
			stormBreedCounter = 50;
			//More storms on gale winds and hurricanes
			int chance = 0;
			int nextStorm = 100;
			switch (getWeather()){
			case STORM:
				chance = 80;
				nextStorm = 50;
				break;
			case GALE_WIND:
				chance = 80;
				nextStorm = 100;
				break;
			case HURRICANE:
				chance = 100;
				nextStorm = 25;
				break;
			default:
				chance = 0;
				nextStorm = 100;
				break;
			}
			
			if (getExpedition().getMovementMode() != MovementMode.SHIP){
				chance /= 3.0d;
			}
			if (Util.chance(chance)){
				Position pos = new Position(getExpedition().getPosition());
				int signX = Util.chance(50) ? 1 : -1;
				int signY = Util.chance(50) ? 1 : -1;
				pos.x += Util.rand(9, 15) * signX;
				pos.y += Util.rand(9, 15) * signY;
				Storm storm = new Storm(pos);
				storms.add(storm);
				storm.seed(6, 18);
				storm.grow();
				stormBreedCounter = nextStorm;
			}
		}
		if (stormChangeCounter > 20){
			List<Storm> removeList = new ArrayList<Storm>();
			for (Storm storm: storms){
				storm.evolve();
				
				if (storm.getMass() < 5){
					removeList.add(storm);
				}
				storm.move(getWindDirection());
			}
			for (Storm storm: removeList){
				storms.remove(storm);
			}

			stormChangeCounter = 0;
		}
	}
	
	private void weatherChange(){
		Weather currentWeather = getWeather();
		OverworldExpeditionCell currentCell = (OverworldExpeditionCell) getMapCell(getExpedition().getPosition());
		
		// Special weather transitions: Fog, Hurricane, Snow and Dust Storm
		
		// Fog
		if (getTemperature() > 10){
			// Hot fog
			if (currentCell.isWater()){
				// In the ocean, fog only rises if there's no wind
				if (getWindDirection() == CardinalDirection.NULL) {
					if (Util.chance(currentWeather.getTransitionChance(Weather.FOG))){
						setWeather(Weather.FOG);
						return;
					}
				}
			} else {
				// In land, fog rises when the terrain is wet and temperature rises
				if (currentCell.isRiver() || currentCell.isMarsh()){
					//Note that rain makes terrain wet, thus must have an higher chance
					if (Util.chance(currentWeather.getTransitionChance(Weather.FOG))){
						setWeather(Weather.FOG);
						return;
					}
				}
			}
		} else {
			// Cold fog
			if (Util.chance(currentWeather.getTransitionChance(Weather.FOG))){
				setWeather(Weather.FOG);
				return;
			}
		}
		
		//Hurricane
		if (currentCell.isSea()){
			if (Util.chance(currentWeather.getTransitionChance(Weather.HURRICANE))){
				setWeather(Weather.HURRICANE);
				return;
			}
		}
		
		// Snow
		if (getTemperature() < 0){
			if (Util.chance(currentWeather.getTransitionChance(Weather.SNOW))){
				setWeather(Weather.SNOW);
				return;
			}
		}

		// Dust Storm
		if (currentCell.isDesert()){
			if (Util.chance(currentWeather.getTransitionChance(Weather.DUST_STORM))){
				setWeather(Weather.DUST_STORM);
				return;
			}
		}
		
		// "Normal" weather chances
		setWeather (currentWeather.nextWeather());
		
	}
	


	private List<Storm> storms = new ArrayList<Storm>();
	
	private int windChangeCounter;
	private int stormBreedCounter;
	private int stormChangeCounter;

	private boolean isTimeToChangeWind;
	
	private boolean isTimeToChangeWind() {
		return isTimeToChangeWind;
	}

	private CardinalDirection currentWind;

	private CardinalDirection getPrevailingWind() {
		int latitude = resolveYToLatitude();
		if (latitude > 60)
			return CardinalDirection.SOUTH;
		if (latitude > 30)
			return CardinalDirection.NORTHEAST;
		if (latitude > 5)
			return CardinalDirection.SOUTHWEST;
		if (latitude > -5)
			return CardinalDirection.WEST;
		if (latitude > -30)
			return CardinalDirection.NORTHWEST;
		if (latitude > -60)
			return CardinalDirection.SOUTHEAST;
		else
			return CardinalDirection.NORTH;
	}
	
	@Override
	public boolean hasStorm(Position position) {
		for (Storm storm: storms){
			if (storm.hasStormlet(position))
				return true;
		}

		return false;
	}

	AbstractFeature stormletFeature = new StormletFeature();
	@Override
	public AbstractFeature getFeatureAt(Position position) {
		if (hasStorm(position))
			return stormletFeature;
		return super.getFeatureAt(position);
	}
	
	@Override
	public List<AbstractFeature> getFeaturesAt(Position p) {
		List<AbstractFeature> ret = super.getFeaturesAt(p);
		if (hasStorm(p)){
			if (ret == null)
				ret = new ArrayList<AbstractFeature>();
			ret.add(stormletFeature);
		}
		return ret;
	}
	
	protected boolean remembers(int x, int y, int z){
		return false;
	}

	
	class StormletFeature extends AbstractFeature {
		public StormletFeature() {
			setAppearanceId("STORM");
		}
		
		@Override
		public String getClassifierID() {
			return "Storm";
		}

		@Override
		public String getDescription() {
			return "Storm";
		}
		
		@Override
		public Appearance getAppearance() {
			return super.getAppearance();
		}
		
		@Override
		public boolean isInvisible() {
			return false;
		}
		
		@Override
		public boolean isVisible() {
			return true;
		}
		
		@Override
		public boolean isOpaque() {
			return true;
		}
	}


	public void setWeather(Weather weather) {
		Weather formerWeather = this.weather;
		this.weather = weather;
		if (formerWeather != weather){
			addMessage(weather.getChangeMessage(formerWeather));
		}
		
	}
}
