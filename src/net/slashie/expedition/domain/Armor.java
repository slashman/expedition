package net.slashie.expedition.domain;

import net.slashie.utils.roll.Roll;

public class Armor extends Good {
	private int burden;
	private Roll defense;
	private String shortDescription;
	
	public int getBurden() {
		return burden;
	}

	public Roll getDefense() {
		return defense;
	}
	
	public Armor(String classifierId, String description, String pluralDescription,
			int weight, int burden, Roll defense, String shortDescription, int baseValue) {
		super(classifierId, description, pluralDescription, weight, GoodType.WEAPON, baseValue);
		this.burden = burden;
		this.defense = defense;
		this.shortDescription = shortDescription;
				
	}

	public String getShortDescription() {
		return shortDescription;
	}
}
