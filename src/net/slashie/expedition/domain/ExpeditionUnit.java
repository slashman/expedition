package net.slashie.expedition.domain;

import java.util.List;

public class ExpeditionUnit extends Vehicle{
	
	private String name;
	private int range;
	private int attack;
	private int defense;
	private int speed;
	private int movement;
	private int resistance;
	private int dailyFoodConsumption;
	private String[] weaponTypes;
	private String[] armorTypes;
	private String special;
	private Weapon weapon;
	
	public Weapon getWeapon() {
		return weapon;
	}

	public String[] getWeaponTypes() {
		return weaponTypes;
	}

	public String[] getArmorTypes() {
		return armorTypes;
	}

	/**
	 * Returns the perceived power for the unit
	 * @return
	 */
	public int getPower(){
		return getAttack()*3+getDefense()*2+getSpeed()+getRange();
	}
	
	public int getDailyFoodConsumption() {
		/*if (weapon != null)
			return dailyFoodConsumption + weapon.getBurden();
		else*/
			return dailyFoodConsumption;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getRange() {
		if (getWeapon() != null){
			return range + getWeapon().getRange();
		} else {
			return 1;
		}
	}

	public void setRange(int range) {
		this.range = range;
	}

	public int getAttack() {
		if (weapon == null)
			return attack;
		else 
			return attack + weapon.getAttack();
	}

	public int getDefense() {
		if (weapon == null)
			return defense;
		else
			return defense + weapon.getDefense();
		
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getMovement() {
		return movement;
	}

	public void setMovement(int movement) {
		this.movement = movement;
	}

	public int getResistance() {
		return resistance;
	}

	public void setResistance(int resistance) {
		this.resistance = resistance;
	}

	public String getSpecial() {
		return special;
	}

	public void setSpecial(String special) {
		this.special = special;
	}

	public ExpeditionUnit(String classifierId, String description, String pluralDescription,
			int weight, int speedModifier, int carryCapacity,
			int range, int attack, int defense, int speed,
			int movement, int resistance, int dailyFoodConsumption,
			String special, String[] weaponTypes, String[] armorTypes) {
		super(classifierId, description, pluralDescription, weight, false,
				false, speedModifier, carryCapacity);
		this.name = description;
		this.range = range;
		this.attack = attack;
		this.defense = defense;
		this.speed = speed;
		this.movement = movement;
		this.resistance = resistance;
		this.dailyFoodConsumption = dailyFoodConsumption;
		this.special = special;
		this.weaponTypes = weaponTypes;
		this.armorTypes = armorTypes;
	}

	public void setArm(Weapon createItem) {
		weapon = createItem;
	}
	
	@Override
	public String getMenuDescription() {
		if (weapon != null){
			return weapon.getDescription()+ " " + getDescription();
		} else {
			return getDescription();
		}
	}
	
	@Override
	public String getFullID() {
		if (weapon != null){
			return super.getFullID()+","+weapon.getFullID();
		} else {
			return super.getFullID();
		}
	}
	
	@Override
	public int getWeight() {
		if (weapon != null){
			return super.getWeight()+weapon.getWeight();
		} else {
			return super.getWeight();
		}
	}

}
