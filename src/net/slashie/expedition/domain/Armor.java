package net.slashie.expedition.domain;

public class Armor extends Good {
	private int burden;
	private int defense;
	private String shortDescription;
	
	public int getBurden() {
		return burden;
	}

	public int getDefense() {
		return defense;
	}
	
	public Armor(String classifierId, String description, String pluralDescription,
			int weight, int burden, int defense, String shortDescription) {
		super(classifierId, description, pluralDescription, weight, GoodType.WEAPON);
		this.burden = burden;
		this.defense = defense;
		this.shortDescription = shortDescription;
				
	}

	public String getShortDescription() {
		return shortDescription;
	}
}
