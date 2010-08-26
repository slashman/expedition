package net.slashie.expedition.world;

import net.slashie.util.Util;
import net.slashie.utils.Position;

public enum CardinalDirection {
	NORTH("N", 0, 0, -1),
	NORTHWEST("NW", 45, -1, -1),
	WEST("W", 90, -1, 0),
	SOUTHWEST("SW", 135, -1, 1),
	SOUTH("S", 180, 0,  1),
	SOUTHEAST("SE", 225, 1, 1),
	EAST("E", 270, 1, 0),
	NORTHEAST("NE",315, 1, -1),
	NULL( "-", null, 0, 0);
	
	private String abbreviation;
	private Integer referenceAngle;
	private Position vectors;
	
	private CardinalDirection(String abbreviation, Integer referenceAngle, Integer xVector, Integer yVector) {
		this.abbreviation = abbreviation;
		this.referenceAngle = referenceAngle;
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
}
