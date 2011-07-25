package net.slashie.expedition.level;

import net.slashie.utils.Position;

public class GlobeMapModel {
	public static int transformLongIntoX(int longMinutes){
		return (int)Math.round((double)longMinutes * 0.3324d) + 3377; 
	}
	
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

}
