package net.slashie.expedition.domain;

public class Armor extends Good {
	private int burden;
	private int defense;
	
	public int getBurden() {
		return burden;
	}

	public int getDefense() {
		return defense;
	}
	
	public Armor(String classifierId, String description, String pluralDescription,
			int weight, int burden, int defense) {
		super(classifierId, description, pluralDescription, weight, GoodType.WEAPON);
		this.burden = burden;
		this.defense = defense;
				
	}
}
