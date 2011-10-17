package net.slashie.expedition.domain;

import net.slashie.expedition.item.StorageType;
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
	
	private Roll attack;
	private Roll defense;
	private boolean isTool;
	private int hitChance;
	private boolean isRanged;
	private WeaponType weaponType;
	
	public Roll getAttack() {
		return attack;
	}

	public Roll getDefense() {
		return defense;
	}

	public Weapon(WeaponType weaponType, String classifierId, String description, String pluralDescription, String longDescription,
			Roll attack, Roll defense, boolean isTool, int hitChance, boolean isRanged, int weight, int palosStoreValue, int baseTradingValue) {
		super(classifierId, description, pluralDescription, longDescription, classifierId, weight, GoodType.ARMORY, palosStoreValue, baseTradingValue, StorageType.WAREHOUSE);
		this.weaponType = weaponType;
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
