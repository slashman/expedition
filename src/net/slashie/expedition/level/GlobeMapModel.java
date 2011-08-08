package net.slashie.expedition.level;

import java.util.HashMap;
import java.util.Map;

import net.slashie.utils.Position;

public class GlobeMapModel {
	private final static int LONGITUDE_EQUATOR_NAUTICAL_MILES = 60; 
	
	// Note: This is calibrated for the 8colorsAmericaAndAtlantic map
	private static Map<Integer, Integer> _transformLongIntoX = new HashMap<Integer, Integer>(); 
	public static int transformLongIntoX(int longMinutes){
		return (int)Math.round((double)longMinutes * 0.3324d) + 3377; 
	}

	// Note: This is calibrated for the 8colorsAmericaAndAtlantic map
	public static int transformLatIntoY(int latMinutes){
		return (int)Math.round((double)latMinutes * -0.3338d) + 1580; 
	}
	
	/**
	 * Steps left and right are 3 minutes at equator, and more minutes as aproaching the poles
	 */
	private static Map<Integer, Integer> _getLongitudeScaleCache = new HashMap<Integer, Integer>(); 
	public static int getLongitudeScale(int latitudeMinutes) {
		double latitudeDegrees = latitudeMinutes / 60.0d;
		Integer ret = _getLongitudeScaleCache.get(latitudeMinutes); 
		if (ret == null){
			//ret = (int) Math.floor(3.0d / Math.cos(latitudeDegrees * (Math.PI / 180.0d)));
			ret = (int) Math.round(3.0d / Math.cos(latitudeDegrees * (Math.PI / 180.0d)));
			_getLongitudeScaleCache.put(latitudeMinutes, ret);
		}
		return ret;
	}
	
	public static void transformIntoLatLong(Position p) {
		p.x = transformXIntoLong(p.x);
		p.y = transformYIntoLat(p.y);
	}

	public static int transformYIntoLat(int y) {
		return (int)Math.floor((double)(y-1580)/ -0.3338d);
	}

	public static int transformXIntoLong(int x) {
		return (int)Math.floor((double)(x - 3377)/ 0.3324d);
	}

	/**
	 * Normalizes longitude to its nearest reference
	 * @param gridX
	 * @return
	 */
	public static int normalizeLong(int latitudeMinutes, int longitudeMinutes) {
		int scale = getLongitudeScale(latitudeMinutes);
		return (int)Math.floor((double)longitudeMinutes/(double)scale) * scale;
	}

	public static int getLatitudeHeight() {
		return 3;
	}

	public static Position scaleVar(Position var, int latitudeMinutes) {
		return new Position(var.x * getLongitudeScale(latitudeMinutes), var.y * getLatitudeHeight());
	}

	
	/**
	 * Transform a longitude minutes value into a distance in nautical miles
	 * @param q
	 * @return
	 */
	
	public static double transformLongIntoNauticalMiles(int latitudeMinutesPosition, int longitudeMinutesValue) {
		double latitudeDegrees = latitudeMinutesPosition / 60.0d;
		double nauticalMiles = Math.cos(latitudeDegrees * (Math.PI / 180.0d));
		nauticalMiles *= LONGITUDE_EQUATOR_NAUTICAL_MILES;
		nauticalMiles /= 60.0d;
		nauticalMiles *= longitudeMinutesValue;
		return nauticalMiles;
	}

}
