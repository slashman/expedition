package net.slashie.expedition.domain;

import net.slashie.utils.roll.Roll;

public class Weapon extends Good{
	private int burden;
	private Roll attack;
	private Roll defense;
	private boolean isTool;
	private int hitChance;
	private boolean isRanged;
	
	public int getBurden() {
		return burden;
	}

	public Roll getAttack() {
		return attack;
	}

	public Roll getDefense() {
		return defense;
	}

	public Weapon(String classifierId, String description, String pluralDescription,
			Roll attack, Roll defense, boolean isTool, int hitChance, boolean isRanged, int weight) {
		super(classifierId, description, pluralDescription, weight, GoodType.WEAPON);
		this.burden = burden;
		this.isTool = isTool;
		this.attack = attack;
		this.defense = defense;
		this.hitChance = hitChance;
		this.isRanged = isRanged;
				
	}


	
	public boolean isTool() {
		return isTool;
	}

	public int getHitChance() {
		return hitChance;
	}

	public boolean isRanged() {
		return isRanged;
	}

}
