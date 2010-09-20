package net.slashie.expedition.domain;

import net.slashie.utils.roll.Roll;

public class Armor extends ExpeditionItem {
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
			int weight, int burden, Roll defense, String shortDescription, int europeValue, int americaValue) {
		super(classifierId, description, pluralDescription, classifierId, weight, GoodType.ARMORY, europeValue, americaValue);
		this.burden = burden;
		this.defense = defense;
		this.shortDescription = shortDescription;
				
	}

	public String getShortDescription() {
		return shortDescription;
	}
}
