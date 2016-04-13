package net.slashie.expedition.world;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import org.apache.log4j.Logger;
import net.ck.expedition.model.test.ExpeditionProperties;
import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.ExpeditionItem;
import net.slashie.expedition.domain.GoodsCache;
import net.slashie.expedition.domain.NativeTown;
import net.slashie.expedition.domain.Expedition.MovementMode;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.game.ExpeditionMusicManager;
import net.slashie.expedition.level.ExpeditionLevelReader;
import net.slashie.expedition.level.GlobeModel;
import net.slashie.expedition.ui.ExpeditionUserInterface;
import net.slashie.expedition.world.agents.ForageAgent;
import net.slashie.expedition.world.agents.HourShiftAgent;
import net.slashie.expedition.world.agents.RandomEventAgent;
import net.slashie.expedition.world.agents.WeeklyAgent;
import net.slashie.expedition.world.agents.WindAgent;
import net.slashie.serf.action.Actor;
import net.slashie.serf.action.Message;
import net.slashie.serf.level.AbstractCell;
import net.slashie.serf.level.AbstractFeature;
import net.slashie.serf.ui.Appearance;
import net.slashie.serf.ui.AppearanceFactory;
import net.slashie.serf.ui.UserInterface;
import net.slashie.util.Pair;
import net.slashie.utils.Position;
import net.slashie.utils.Util;

@SuppressWarnings("serial")
public class ExpeditionMacroLevel extends ExpeditionLevelReader
{
	private Actor currentWindAgent;
	private Actor currentHourShiftAgent;
	private Actor currentForageAgent;
	private Actor currentWeeklyAgent;
	private Actor currentRandomEventsAgent;
	final Logger logger = Logger.getRootLogger();

	public ExpeditionMacroLevel(String levelNameset, int levelWidth, int levelHeight, int gridWidth, int gridHeight,
			Hashtable<String, String> charmap, Position startPosition, GlobeModel globeModel)
	{
		super(levelNameset, levelWidth, levelHeight, gridWidth, gridHeight, charmap, startPosition, globeModel);
		currentWindAgent = new WindAgent();
		currentHourShiftAgent = new HourShiftAgent();
		currentForageAgent = new ForageAgent();
		currentWeeklyAgent = new WeeklyAgent();
		currentRandomEventsAgent = new RandomEventAgent();
		addActor(currentWindAgent);
		addActor(currentHourShiftAgent);
		addActor(currentForageAgent);
		addActor(currentWeeklyAgent);
		addActor(currentRandomEventsAgent);		
	}

	private Pair<Integer, Integer> handyReusablePair = new Pair<Integer, Integer>(0, 0);

	private Weather weather = Weather.CLEAR;

	public Pair<Integer, Integer> getLocation()
	{
		handyReusablePair.setA(resolveYToLatitude());
		handyReusablePair.setB(resolveXToLongitude());
		return handyReusablePair;
	}

	private int resolveXToLongitude()
	{
		return globeModel.getLongitudeDegrees(getPlayer().getPosition().x);
	}

	private int resolveYToLatitude()
	{
		return globeModel.getLatitudeDegrees(getPlayer().getPosition().y);
	}

	private int currentTemperature = 15;

	public int getTemperature()
	{
		return currentTemperature;
	}

	private int apparentTemperature = 0;

	public String getTemperatureDescription()
	{
		return TemperatureRules.getTemperatureDescription(apparentTemperature);
	}

	public Weather getWeather()
	{
		return weather;
	}

	private Pair<String, String> handyReusableObject = new Pair<String, String>("H", "H");

	public Pair<String, String> getLocationDescription()
	{
		Pair<Integer, Integer> location = getLocation();

		handyReusableObject.setA(Math.abs(location.getA()) + (location.getA() > 0 ? "ºN" : "ºS"));
		// This is the real longitude calculation:
		if (getExpedition().hasMarineChronometer())
		{
			handyReusableObject.setB(Math.abs(location.getB()) + (location.getB() > 0 ? "E" : "W"));
		}
		else
		{
			if (getExpedition().getDeducedReckonWest() > 0)
				handyReusableObject.setB(getExpedition().getDeducedReckonWest() + "nl");
			else
				handyReusableObject.setB((-getExpedition().getDeducedReckonWest()) + "nl");
		}
		return handyReusableObject;
	}

	private Pair<String, String> handyReusableObject2 = new Pair<String, String>("H", "H");

	private int weatherChangeCounter;

	private int tempChangeCounter;

	private boolean isOnITZ;

	public Pair<String, String> getLocationMeans()
	{
		handyReusableObject2.setA("Sextant");
		handyReusableObject2.setB("Ded'Reckon");
		return handyReusableObject2;
	}

	private Pair<String, String> handyReusableObject3 = new Pair<String, String>("H", "H");

	private boolean onMountains = false;

	@Override
	public Pair<String, String> getLocationLabels()
	{
		handyReusableObject3.setA("LAT");
		if (getExpedition().getDeducedReckonWest() > 0)
			handyReusableObject3.setB("West");
		else
			handyReusableObject3.setB("East");
		return handyReusableObject3;
	}

	@Override
	public void updateLevelStatus()
	{
		super.updateLevelStatus();
	}

	public void addEquipment(ExpeditionItem item, int quantity, Position where)
	{
		if (((OverworldExpeditionCell) getMapCell(where)).isLand())
		{
			GoodsCache cache = getOrCreateCache(where);
			cache.addItem(item, quantity);
			if (cache.getItems().size() == 0)
				destroyFeature(cache);
		}
		else
		{
			// Drop things into the big sea
		}
	}

	/**
	 * Gets a cache at a locations or returns a new instance
	 * 
	 * @param where
	 * @param appearance
	 * @return
	 */
	public GoodsCache getOrCreateCache(Position where)
	{
		List<AbstractFeature> features = getFeaturesAt(where);
		GoodsCache cache = null;
		boolean newCache = false;
		if (features == null || features.size() == 0)
		{
			newCache = true;
		}
		else
		{
			for (AbstractFeature feature : features)
			{
				if (feature instanceof GoodsCache)
				{
					cache = (GoodsCache) feature;
					newCache = false;
					break;
				}
				else
				{
					newCache = true;
				}
			}
		}
		if (newCache)
		{
			cache = new GoodsCache((ExpeditionGame) (getExpedition().getGame()),
					getMapCell(where).getAppearance().getID(), "GOODS_CACHE");
			cache.setPosition(new Position(where));
			addFeature(cache);
		}
		return cache;
	}

	public GoodsCache getCache(Position where)
	{
		List<AbstractFeature> features = getFeaturesAt(where);
		if (features == null || features.size() == 0)
		{
			return null;
		}
		else
		{
			for (AbstractFeature feature : features)
			{
				if (feature instanceof GoodsCache)
				{
					return (GoodsCache) feature;
				}
			}
		}
		return null;
	}

	public void addAllEquipment(Expedition expedition, Position where)
	{
		if (((OverworldExpeditionCell) getMapCell(where)).isLand())
		{
			GoodsCache cache = getOrCreateCache(where);
			cache.addAllGoods(expedition);
			if (cache.getItems().size() == 0)
				destroyFeature(cache);
		}
		else
		{
			// Drop things into the big sea
		}
	}

	@Override
	public boolean isZoomIn()
	{
		return false;
	}

	@Override
	public CardinalDirection getWindDirection()
	{
		return currentWind;
	}

	public void setWindDirection(CardinalDirection direction)
	{
		currentWind = direction;
	}

	@Override
	public void elapseTime(int lastActionTimeCost)
	{
		// TODO: Create Agents for each of this phenomena
		stormBreedCounter -= lastActionTimeCost;
		stormChangeCounter += lastActionTimeCost;
		weatherChangeCounter -= lastActionTimeCost;
		tempChangeCounter -= lastActionTimeCost;

		if (weatherChangeCounter < 0)
		{
			weatherChange();
			weatherChangeCounter = Util.rand(200, 400);
		}
		OverworldExpeditionCell currentCell = (OverworldExpeditionCell) getMapCell(getExpedition().getPosition());
		if (currentCell.getHeightMod() > 1)
		{
			tempChangeCounter = -1;
			onMountains = true;
		}
		else if (onMountains)
		{
			tempChangeCounter = -1;
			onMountains = false;
		}
		if (tempChangeCounter < 0)
		{
			if (currentCell.getHeightMod() > 1)
			{
				currentTemperature = 5;
			}
			else
			{
				currentTemperature = (int) Math.round(TemperatureRules.getRulingTemperature(resolveYToLatitude(),
						ExpeditionGame.getCurrentGame().getGameTime().get(Calendar.MONTH) + 1));
				if (currentCell.isSea())
				{
					currentTemperature += 5;
				}
			}
			apparentTemperature = currentTemperature + getWeather().getTemperatureModification();

			tempChangeCounter = Util.rand(200, 400);
		}

		if (stormBreedCounter <= 0)
		{
			// More storms on gale winds and hurricanes
			int chance = 0;
			switch (getWeather())
			{
			case STORM:
				chance = 80;
				stormBreedCounter = 120;
				break;
			case GALE_WIND:
				chance = 80;
				stormBreedCounter = 80;
				break;
			case HURRICANE:
				chance = 100;
				stormBreedCounter = 40;
				break;
			default:
				chance = 0;
				stormBreedCounter = 40;
				break;
			}

			if (getExpedition().getMovementMode() != MovementMode.SHIP)
			{
				chance /= 3.0d;
			}
			if (Util.chance(chance))
			{
				for (int i = 0; i < 3; i++)
				{
					Position pos = new Position(getExpedition().getPosition());
					int signX = Util.chance(50) ? 1 : -1;
					int signY = Util.chance(50) ? 1 : -1;
					pos.y += Util.rand(8, 10) * signY * globeModel.getLatitudeHeight();
					pos.x += Util.rand(8, 10) * signX * globeModel.getLongitudeScale(pos.y);
					Storm storm = new Storm(pos);
					storms.add(storm);
					storm.seed(6, 18);
					storm.grow();
				}
			}
		}

		if (stormChangeCounter >= 40)
		{
			List<Storm> removeList = new ArrayList<Storm>();
			for (Storm storm : storms)
			{
				storm.evolve();
				if (storm.getMass() < 5)
				{
					removeList.add(storm);
				}
				storm.move(getWindDirection());
			}
			for (Storm storm : removeList)
			{
				storms.remove(storm);
			}

			stormChangeCounter = 0;
		}
	}

	private void weatherChange()
	{
		Weather currentWeather = getWeather();
		OverworldExpeditionCell currentCell = (OverworldExpeditionCell) getMapCell(getExpedition().getPosition());

		// Special weather transitions: Fog, Hurricane, Snow and Dust Storm

		// Fog
		if (getTemperature() > 10)
		{
			// Hot fog
			if (currentCell.isWater())
			{
				// In the ocean, fog only rises if there's no wind
				if (getWindDirection() == CardinalDirection.NULL)
				{
					if (Util.chance(currentWeather.getTransitionChance(Weather.FOG)))
					{
						setWeather(Weather.FOG);
						return;
					}
				}
			}
			else
			{
				// In land, fog rises when the terrain is wet and temperature
				// rises
				if (currentCell.isRiver() || currentCell.isMarsh())
				{
					// Note that rain makes terrain wet, thus must have an
					// higher chance
					if (Util.chance(currentWeather.getTransitionChance(Weather.FOG)))
					{
						setWeather(Weather.FOG);
						return;
					}
				}
			}
		}
		else
		{
			// Cold fog
			if (Util.chance(currentWeather.getTransitionChance(Weather.FOG)))
			{
				setWeather(Weather.FOG);
				return;
			}
		}

		// Hurricane
		if (currentCell.isSea())
		{
			if (Util.chance(currentWeather.getTransitionChance(Weather.HURRICANE)))
			{
				setWeather(Weather.HURRICANE);
				return;
			}
		}

		// Snow
		if (getTemperature() < 0)
		{
			if (Util.chance(currentWeather.getTransitionChance(Weather.SNOW)))
			{
				setWeather(Weather.SNOW);
				return;
			}
		}

		// Dust Storm
		if (currentCell.isDesert())
		{
			if (Util.chance(currentWeather.getTransitionChance(Weather.DUST_STORM)))
			{
				setWeather(Weather.DUST_STORM);
				return;
			}
		}

		// Storms at the doldrums
		if (isOnITZ() && currentCell.isSea())
		{
			if (Util.chance(currentWeather.getTransitionChance(Weather.DUST_STORM)))
			{
				setWeather(Weather.STORM);
				return;
			}
		}
		// "Normal" weather chances
		setWeather(currentWeather.nextWeather());

	}

	private List<Storm> storms = new ArrayList<Storm>();

	private int stormBreedCounter;
	private int stormChangeCounter;

	private CardinalDirection currentWind;

	public CardinalDirection getPrevailingWind(int month)
	{
		int itcz = TemperatureRules.getITCZ(month) + Util.rand(-5, 5);
		int latitude = resolveYToLatitude();
		if (latitude >= itcz - 4 && latitude <= itcz + 4)
		{

			return CardinalDirection.NULL;
		}

		if (latitude > 60)
			return CardinalDirection.SOUTH;
		if (latitude > 30)
			return CardinalDirection.NORTHEAST;
		if (latitude > 5)
			return CardinalDirection.SOUTHWEST; //
		if (latitude > -5)
			return CardinalDirection.WEST; //
		if (latitude > -30)
			return CardinalDirection.NORTHWEST; //
		if (latitude > -60)
			return CardinalDirection.SOUTHEAST;
		else
			return CardinalDirection.NORTH;
	}

	@Override
	public boolean hasStorm(Position position)
	{
		for (Storm storm : storms)
		{
			if (storm.hasStormlet(position))
				return true;
		}

		return false;
	}

	AbstractFeature shadowFeature = new AbstractFeature()
	{
		{
			setAppearanceId("WATER_SHADOW");
		}

		@Override
		public String getClassifierID()
		{
			return "WaterShadow";
		}

		@Override
		public String getDescription()
		{
			return "Sea Wind Shadow";
		}

		@Override
		public Appearance getAppearance()
		{
			return super.getAppearance();
		}

		@Override
		public boolean isInvisible()
		{
			return false;
		}

		@Override
		public boolean isVisible()
		{
			return true;
		}

		@Override
		public boolean isOpaque()
		{
			return false;
		}
	};

	AbstractFeature stormletFeature = new StormletFeature();

	@Override
	@Deprecated
	public AbstractFeature getFeatureAt(Position position)
	{
		if (hasStorm(position))
			return stormletFeature;
		return super.getFeatureAt(position);
	}

	@Override
	public List<AbstractFeature> getFeaturesAt(Position tempP)
	{
		// Base list
		List<AbstractFeature> ret = super.getFeaturesAt(tempP);

		// Stormlets
		if (hasStorm(tempP))
		{
			if (ret == null)
				ret = new ArrayList<AbstractFeature>();
			if (!ret.contains(stormletFeature))
			{
				ret.add(stormletFeature);
			}
		}
		else if (ret != null)
		{
			if (ret.contains(stormletFeature))
			{
				ret.remove(stormletFeature);
			}
		}

		// Ship shadow (Consider removing this as a feature, should be just an
		// UI thing)
		OverworldExpeditionCell cell = (OverworldExpeditionCell) getMapCell(tempP);
		if (ret == null && cell != null && getExpedition() != null
				&& getExpedition().getMovementMode() == MovementMode.SHIP)
		{
			// Get cell to the wind shadow
			Position var = new Position(getWindDirection().getVectors());
			var = globeModel.scaleVar(var, getExpedition().getLatitude());
			if (tempP.equals(Position.add(getExpedition().getPosition(), var)))
			{
				if (ret == null)
					ret = new ArrayList<AbstractFeature>();
				ret.add(shadowFeature);
			}
		}
		return ret;
	}

	protected boolean remembers(int x, int y, int z)
	{
		return false;
	}

	class StormletFeature extends AbstractFeature
	{
		private static final long serialVersionUID = 1L;

		public StormletFeature()
		{
			setAppearanceId("STORM");
		}

		@Override
		public String getClassifierID()
		{
			return "Storm";
		}

		@Override
		public String getDescription()
		{
			return "Storm";
		}

		@Override
		public Appearance getAppearance()
		{
			return super.getAppearance();
		}

		@Override
		public boolean isInvisible()
		{
			return false;
		}

		@Override
		public boolean isVisible()
		{
			return true;
		}

		@Override
		public boolean isOpaque()
		{
			return true;
		}
	}

	private long lastPlayedLevelTune = -1;
	private long LEVEL_MUSIC_TUNE_PLAYBACK_GAP = 120 * 1000;

	public void setWeather(Weather weather)
	{
		Weather formerWeather = this.weather;
		this.weather = weather;
		if (formerWeather != weather)
		{
			if (UserInterface.getUI() != null)
			{
				((ExpeditionUserInterface) UserInterface.getUI()).notifyWeatherChange(weather);
				addMessage(weather.getChangeMessage(formerWeather));
			}
			else
			{
				logger.debug(weather.getChangeMessage(formerWeather));
			}

			if (weather.isWindy() && getWindDirection() == CardinalDirection.NULL)
			{
				setWindDirection(getWindDirection().rotate(1));
			}
			if (formerWeather.isStormy() && !weather.isStormy())
			{
				// Remove all storms
				storms.clear();
			}
			if (ExpeditionProperties.isDebug())
			{

			}
			else
			{
				playMusic();
			}
		}

	}

	@Override
	public String getMusicKey()
	{
		if (((OverworldExpeditionCell) getMapCell(getPlayer().getPosition())).isSea())
			return "SEA";
		else
			return "LAND";
	}

	public boolean isOnITZ()
	{
		return isOnITZ;
	}

	public int getApparentTemperature()
	{
		return apparentTemperature;
	}

	public void setIsOnITZ(boolean isOnITZ)
	{
		this.isOnITZ = isOnITZ;
	}

	private Position LOSPosition = new Position(0, 0, 0);

	public boolean blockLOS(int x, int y)
	{
		LOSPosition.x = x;
		LOSPosition.y = y;
		LOSPosition.z = 0;
		if (!isValidCoordinate(x, y))
			return true;
		List<AbstractFeature> feats = getFeaturesAt(LOSPosition);
		AbstractCell cell = getMapCell(x, y, getPlayer().getPosition().z);

		if (feats != null)
			for (AbstractFeature feat : feats)
			{
				if (feat != null && feat.isOpaque())
					return true;
				if (feat != null && feat.overrideOpacity() && cell.isOpaque())
				{
					return false;
				}
				if (feat != null)
					feat.onSeenByPlayer();
			}
		if (cell == null)
			return false;
		else
		{
			AbstractCell playerCell = getMapCell(getPlayer().getPosition());
			if (cell.getHeightMod() == playerCell.getHeightMod())
				return cell.isOpaque();
			else if (cell.getHeightMod() > playerCell.getHeightMod())
				return true;
			else
				return false;

		}
	}

	@Override
	public void playMusic()
	{
		if (weather.getMusicKey() != null)
		{
			ExpeditionMusicManager.playTune(weather.getMusicKey());
		}
		else
		{
			if (System.currentTimeMillis() > lastPlayedLevelTune + LEVEL_MUSIC_TUNE_PLAYBACK_GAP)
			{
				ExpeditionMusicManager.playTune(getMusicKey());
				lastPlayedLevelTune = System.currentTimeMillis();
			}
			else
			{
				ExpeditionMusicManager.stopWeather();
			}
		}
	}

	@Override
	public void enterLevel()
	{
		lastPlayedLevelTune = -1;
	}

	private List<NativeTown> nativeTowns = new ArrayList<NativeTown>();

	public void addNativeTown(NativeTown t)
	{
		nativeTowns.add(t);
		addFeature(t);
	}

	public List<NativeTown> getNativeTowns()
	{
		return nativeTowns;
	}

	
	@Override
	public Appearance filterAppearance(Appearance appearance)
	{
		if (getWeather() == Weather.FOG)
		{
			return AppearanceFactory.getAppearanceFactory().getAppearance(appearance.getID() + "_FOG");
		}
		else
		{
			return super.filterAppearance(appearance);
		}
	}

	public List<Pair<Position, OverworldExpeditionCell>> getMapCellsAndPositionsAround(Position p)
	{
		List<Pair<Position, OverworldExpeditionCell>> ret = new ArrayList<Pair<Position, OverworldExpeditionCell>>();
		List<Position> positions = getPositionsAround(p);
		for (Position position : positions)
		{
			ret.add(new Pair<Position, OverworldExpeditionCell>(position,
					(OverworldExpeditionCell) getMapCell(position)));
		}
		return ret;
	}

	public List<OverworldExpeditionCell> getMapCellsAround(Position p)
	{
		List<OverworldExpeditionCell> ret = new ArrayList<OverworldExpeditionCell>();
		List<Position> positions = getPositionsAround(p);
		for (Position position : positions)
		{
			ret.add((OverworldExpeditionCell) getMapCell(position));
		}
		return ret;
	}

	public Forest getOrCreateForest(Position position)
	{
		Forest f = null;
		List<AbstractFeature> allFeatures = getFeaturesAt(position);
		if (allFeatures != null)
		{
			for (AbstractFeature feature : allFeatures)
			{
				if (feature instanceof Forest)
				{
					f = (Forest) feature;
					break;
				}
			}
		}
		if (f == null)
		{
			// No feature here yet, but since this is a forest, let's add a
			// forest feature;
			f = new Forest(Util.rand(500, 750));
			f.setPosition(new Position(position));
			addFeature(f);
		}
		return f;
	}
}
