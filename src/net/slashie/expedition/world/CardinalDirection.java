package net.slashie.expedition.world;

import net.slashie.util.Util;
import net.slashie.utils.Position;

public enum CardinalDirection {
	NORTH("N", 0, 0, 1, "north"),
	NORTHWEST("NW", 45, -1, 1, "northwest"),
	WEST("W", 90, -1, 0, "west"),
	SOUTHWEST("SW", 135, -1, -1, "southwest"),
	SOUTH("S", 180, 0,  -1, "south"),
	SOUTHEAST("SE", 225, 1, -1, "southeast"),
	EAST("E", 270, 1, 0, "east"),
	NORTHEAST("NE",315, 1, 1,"northeast"),
	NULL( "-", null, 0, 0, "-");
	
	private String abbreviation;
	private Integer referenceAngle;
	private Position vectors;
	private String description;
	
	private CardinalDirection(String abbreviation, Integer referenceAngle, Integer xVector, Integer yVector, String description) {
		this.abbreviation = abbreviation;
		this.referenceAngle = referenceAngle;
		this.description = description; 
		if (xVector != null)
			this.vectors = new Position(xVector, yVector);
	}

	public String getAbbreviation(){
		return abbreviation;
	}

	public int getReferenceAngle() {
		return referenceAngle;
	}

	public CardinalDirection rotate(int direction) {
		int referenceAngle = 0;
		if (this.referenceAngle == null){
			referenceAngle = 45 * Util.rand(0, 7);
		} else {
			referenceAngle = getReferenceAngle();
		}
		
		int angle =  referenceAngle + 45 * direction;
		if (angle < 0)
			angle = 360 + angle;
		return getCardinalDirection(angle);
	}

	public static CardinalDirection getCardinalDirection(int angle) {
		for (CardinalDirection cardinalDirection: values()){
			if (cardinalDirection.referenceAngle != null && cardinalDirection.referenceAngle == angle % 360)
				return cardinalDirection;
		}
		return null;
	}

	public Position getVectors() {
		return vectors;
	}

	
	public String getDescription() {
		return description;
	}

	
	public static CardinalDirection getGeneralDirection(Position from,
			Position to) {
		int angle = (int) Math.round( Math.atan2(to.y-from.y, to.x-from.x) / (Math.PI / 180.0d));
		/*
		 *   270
		 *180    0
		 *    90
		 */
		if (angle > 338)
			return CardinalDirection.EAST;
		else if (angle > 293)
			return CardinalDirection.NORTHEAST;
		else if (angle > 248)
			return CardinalDirection.NORTH;
		else if (angle > 203)
			return CardinalDirection.NORTHWEST;
		else if (angle > 158)
			return CardinalDirection.WEST;
		else if (angle > 113)
			return CardinalDirection.SOUTHWEST;
		else if (angle > 68)
			return CardinalDirection.SOUTH;
		else if (angle > 23)
			return CardinalDirection.SOUTHEAST;
		else 
			return CardinalDirection.EAST;
	}

	public static CardinalDirection getRandomDirection() {
		return (CardinalDirection) Util.randomElementOf(values());
	}
	
}
