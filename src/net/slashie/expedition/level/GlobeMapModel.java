package net.slashie.expedition.level;

import net.slashie.utils.Position;

public class GlobeMapModel {
	// Note: This is calibrated for the 8colorsAmericaAndAtlantic map
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
	public static int getLongitudeScale(int latitudeMinutes) {
		double latitudeDegrees = latitudeMinutes / 60.0d;
		return (int) Math.floor(3.0d / Math.cos(latitudeDegrees * (Math.PI / 180.0d)));
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

}
