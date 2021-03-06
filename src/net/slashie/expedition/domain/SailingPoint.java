package net.slashie.expedition.domain;

import net.slashie.expedition.domain.Expedition.MovementSpeed;

public enum SailingPoint {
	BEATING 	("Beating",		0,	44,		MovementSpeed.VERY_SLOW),
	CLOSE_REACH ("Close Reach",	45,	79,		MovementSpeed.SLOW),
	BEAM_REACH 	("Beam Reach",	80,	109,	MovementSpeed.SLOW),
	BROAD_REACH ("Broad Reach", 110,159,	MovementSpeed.NORMAL),
	RUNNING 	("Running", 	160,180,	MovementSpeed.FAST),
	
	BARE_POLES ("Bare Poles",MovementSpeed.SLOW),
	RUNNING_STORM ("Surfing",MovementSpeed.NORMAL),
	
	NONE ("None"),
	STALLED ("Becalmed");
	
	private String name;
	private Integer from, to;
	private MovementSpeed speed;
	private SailingPoint(String name, int from, int to, MovementSpeed speed) {
		this.name = name;
		this.from = from;
		this.to = to;
		this.speed = speed;
	}
	
	private SailingPoint(String name, MovementSpeed speed) {
		this.name = name;
		this.speed = speed;
	}
	
	private SailingPoint(String name){
		this.name = name;
		this.speed = MovementSpeed.NONE;
	}

	public static SailingPoint resolvePoint(int angularDifference) {
		for (SailingPoint sailingPoint: values()){
			if (sailingPoint.from == null)
				continue;
			if (angularDifference >= sailingPoint.from && angularDifference <= sailingPoint.to)
				return sailingPoint;
		}
		return SailingPoint.NONE;
	}

	public String getDescription() {
		return name;
	}

	public MovementSpeed getSpeed() {
		return speed;
	}
}
