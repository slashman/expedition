package net.slashie.expedition.domain;

import net.slashie.utils.roll.Roll;

public class Weapon extends ExpeditionItem{
	private static final long serialVersionUID = 1L;
	
	public enum WeaponType {
		MACE,
		SPEAR,
		SWORD,
		BOW,
		CROSSBOW,
		MUSKET;

		public String getAppearanceId() {
			switch (this){
			case BOW:
				return "SIMPLE_BOW";
			case SPEAR:
				return "STEEL_SPEAR";
			case SWORD:
				return "STEEL_SWORD";
			case CROSSBOW:
				return "WOODEN_CROSSBOW";
			case MUSKET:
				return "HARQUEBUS";
			case MACE:
				return "WOODEN_MACE";
			}
			return null;
		}
	}
	
	private int burden;
	private Roll attack;
	private Roll defense;
	private boolean isTool;
	private int hitChance;
	private boolean isRanged;
	private WeaponType weaponType;
	
	public int getBurden() {
		return burden;
	}

	public Roll getAttack() {
		return attack;
	}

	public Roll getDefense() {
		return defense;
	}

	public Weapon(WeaponType weaponType, String classifierId, String description, String pluralDescription, String longDescription,
			Roll attack, Roll defense, boolean isTool, int hitChance, boolean isRanged, int weight, int europeValue, int americaValue) {
		super(classifierId, description, pluralDescription, longDescription, classifierId, weight, GoodType.ARMORY, europeValue, americaValue);
		this.weaponType = weaponType;
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

	public WeaponType getWeaponType() {
		return weaponType;
	}

}
