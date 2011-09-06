package net.slashie.expedition.level;

import net.slashie.utils.Position;

public abstract class GlobeModel {
	public abstract int transformLongIntoX(int longSeconds);

	public abstract int transformLatIntoY(int latSeconds);
	
	public abstract int getLongitudeScale(int latitudeSeconds);
	
	public abstract int getLatitudeHeight();
	
	public abstract int transformYIntoLat(int y);

	public abstract int transformXIntoLong(int x);
	
	/**
	 * Transform a longitude minutes value into a distance in nautical miles
	 * @param q
	 * @return
	 */
	
	public abstract double transformLongIntoNauticalMiles(int latitudeSecondsPosition, int longitudeSeconds);
	

	/**
	 * Normalizes longitude to its nearest reference
	 * @param gridX
	 * @return
	 */
	public int normalizeLong(int latitudeMinutes, int longitudeMinutes) {
		int scale = getLongitudeScale(latitudeMinutes);
		return (int)Math.floor((double)longitudeMinutes/(double)scale) * scale;
	}
	
	public int normalizeLat(int latitudeMinutes) {
		int scale = getLatitudeHeight();
		return (int)Math.floor((double)latitudeMinutes/(double)scale) * scale;
	}
	
	public void transformIntoLatLong(Position p) {
		p.x = transformXIntoLong(p.x);
		p.y = transformYIntoLat(p.y);
	}

	/**
	 * For correct diagonal scaling, should calculate the longitude scale of the new latitude
	 * @param var
	 * @param latitudeMinutes
	 * @return
	 */
	public Position scaleVar(Position var, int latitudeMinutes) {
		int newLatMinutes = latitudeMinutes + var.y * getLatitudeHeight();
		return new Position(var.x * getLongitudeScale(newLatMinutes), var.y * getLatitudeHeight());
	}

	public abstract boolean isValidCoordinate(int longitude, int latitutde);
	
}
