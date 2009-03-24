package net.slashie.expedition.domain;

public class Weapon extends Good{
	private int burden;
	private int attack;
	private int defense;
	
	public int getBurden() {
		return burden;
	}

	public int getAttack() {
		return attack;
	}

	public int getDefense() {
		return defense;
	}

	public Weapon(String classifierId, String description, String pluralDescription,
			int weight, int burden, int attack, int defense) {
		super(classifierId, description, pluralDescription, weight, GoodType.WEAPON);
		this.burden = burden;
		this.attack = attack;
		this.defense = defense;
				
	}

}
