package net.slashie.expedition.level;

import java.util.HashMap;
import java.util.Map;

import net.slashie.utils.Position;

public class GlobeMapModelSeconds extends GlobeModel{
	private final static int LONGITUDE_MINUTES_EQUATOR_NAUTICAL_MILES = 1; // 1 second = 1 nautical mile at equator 
	
	// Note: This is calibrated for the 8colorsAmericaAndAtlantic map
	@Override
	public int transformLongIntoX(int longSeconds){
		return (int)Math.round((double)longSeconds * 0.00554d) + 3377; //8colorsAmericaAndAtlantic map, seconds
	}

	// Note: This is calibrated for the 8colorsAmericaAndAtlantic map
	@Override
	public int transformLatIntoY(int latSeconds){
		return (int)Math.round((double)latSeconds * -0.0055638d) + 1580;
	}
	
	/**
	 * Steps left and right are 180 seconds at equator, and more seconds as aproaching the poles
	 */
	private static Map<Integer, Integer> _getLongitudeScaleCache = new HashMap<Integer, Integer>(); 
	@Override
	public int getLongitudeScale(int latitudeSeconds) {
		Integer ret = _getLongitudeScaleCache.get(latitudeSeconds); 
		if (ret == null){
			ret = (int) Math.round(180.0d / Math.cos(((double)latitudeSeconds / 60.0d / 60.0d) * (Math.PI / 180.0d)));
			_getLongitudeScaleCache.put(latitudeSeconds, ret);
		}
		return ret;
	}

	@Override
	public int transformYIntoLat(int y) {
		return (int)Math.floor((double)(y - 1580) / -0.0055638d);
	}

	@Override
	public int transformXIntoLong(int x) {
		return (int)Math.floor((double)(x - 3377) / 0.00554d);
	}

	@Override
	public int getLatitudeHeight() {
		return 180;
	}

	/**
	 * Transform a longitude minutes value into a distance in nautical miles
	 * @param q
	 * @return
	 */
	@Override
	public double transformLongIntoNauticalMiles(int latitudeSecondsPosition, int longitudeSeconds) {
		double nauticalMiles = Math.cos(((double)latitudeSecondsPosition / 60.0d / 60.0d) * (Math.PI / 180.0d));
		nauticalMiles *= LONGITUDE_MINUTES_EQUATOR_NAUTICAL_MILES;
		nauticalMiles /= 60.0d;
		nauticalMiles *= longitudeSeconds;
		return nauticalMiles;
	}
	
	@Override
	public boolean isValidCoordinate(int longSeconds, int latSeconds) {
		return ! (longSeconds <= -180*60*60 ||
				latSeconds <= -90 * 60 * 60 ||
				longSeconds >= 180 * 60  * 60||
				latSeconds >= 90*60 * 60);
	}
	
	@Override
	public int getLongitudeDegrees(int longitude) {
		return (int)Math.round(longitude / 60.0d / 60.0d);
	}

	@Override
	public int getLatitudeDegrees(int latitude) {
		return (int)Math.round(latitude / 60.0d / 60.0d);
	}
	
	@Override
	public int getMilesDistance(Position position, Position position2) {
		// TODO Use a real long distance calculator, for now use a ver approximate flat distance :P
		int longDistance = position.x - position2.x;
		double longitudeMiles = transformLongIntoNauticalMiles(position.y, longDistance);
		int latDistance = position.y - position2.y;
		latDistance *= LONGITUDE_MINUTES_EQUATOR_NAUTICAL_MILES;
		latDistance /= 60.0d;
		return (int) Math.sqrt(Math.pow(longitudeMiles,2) + Math.pow(latDistance,2));
	}
}