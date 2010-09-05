package net.slashie.expedition.domain;

import net.slashie.utils.roll.Roll;

public class ExpeditionUnit extends Vehicle{
	
	private String name;
	private int movement; //TODO: Make this affect expedition speed?
	private int resistance;  //TODO: Make this affect battle performance
	private int dailyFoodConsumption;
	private int baseHitChance;
	private Roll baseAttack;
	private Roll baseDefense;
	
	private int evadeChance;
	private int hitChance;
	private Roll compositeAttack;
	private Roll compositeDefense;
	private boolean isRangedAttack;
	
	private String[] weaponTypes;
	private String[] armorTypes;
	private String special;
	private Weapon weapon;
	private Armor armor;
	private Vehicle vehicle;
	
	public void setArmor(Armor armor) {
		this.armor = armor;
		updateCompositeVariables();
	}

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
		return (getAttack().getMax()*3+getDefense().getMax()*2)*(isRangedAttack()?2:1);
	}
	
	public Roll getAttack(){
		return compositeAttack;
	}
	
	public Roll getDefense(){
		return compositeDefense;
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

	public ExpeditionUnit(String classifierId, 
			String description, 
			String pluralDescription,
			int weight, 
			int carryCapacity,
			Roll baseAttack, 
			Roll baseDefense,
			int resistance,
			int baseHitChance,
			int evadeChance,
			int dailyFoodConsumption,
			String[] weaponTypes, String[] armorTypes) {
		super(classifierId, description, pluralDescription, weight, false,
				false, false, 1, carryCapacity, 1, true);
		this.name = description;
		this.baseAttack = baseAttack;
		this.baseDefense = baseDefense;
		this.baseHitChance = baseHitChance;
		this.evadeChance = evadeChance;
		this.movement = movement;
		this.resistance = resistance;
		this.dailyFoodConsumption = dailyFoodConsumption;
		this.special = special;
		this.weaponTypes = weaponTypes;
		this.armorTypes = armorTypes;
		updateCompositeVariables();
	}

	public void setArm(Weapon createItem) {
		weapon = createItem;
		updateCompositeVariables();
	}
	
	private void updateCompositeVariables(){
		fullId = super.getFullID();
		unitWeight = super.getWeight();
		fullDescription = "";
		compositeAttack = new Roll(baseAttack);
		compositeDefense = new Roll(baseDefense);
		
		if (isWounded){
			fullDescription += "Wounded ";
		}
		
		if (armor != null){
			//fullDescription += "+"+armor.getDefense().getMax()+" ";
			fullDescription += armor.getShortDescription()+" ";
		}

		if (weapon != null){
			fullId += ","+weapon.getFullID();
			fullDescription += weapon.getDescription()+" ";
			unitWeight += weapon.getWeight();
			if (weapon.isTool()){
				compositeAttack = new Roll(weapon.getAttack());
				hitChance = weapon.getHitChance();
			} else {
				compositeAttack.addModifierRoll(weapon.getAttack());
				hitChance = (int) Math.round( (baseHitChance + weapon.getHitChance())/2.0d);
			}
			if (weapon.isRanged()){
				isRangedAttack = true;
			} else {
				isRangedAttack = false;
			}
		} else {
			hitChance = baseHitChance;
		}
		fullDescriptionBase = fullDescription;
		fullDescription += getDescription();
		if (armor != null){
			fullId += ";"+armor.getFullID();
			//menuDescription += "("+armor.getShortDescription()+")";
			unitWeight += armor.getWeight();
			compositeDefense = new Roll(armor.getDefense());
		}
		if (vehicle != null && vehicle.isHorse()){
			isMounted = true;
		} else {
			isMounted = false;
		}
		if (isWounded){
			fullId += ",WOUNDED";
		}
	}
	
	private String fullDescription;
	
	@Override
	public String getFullDescription() {
		return fullDescription;
	}
	
	private String fullId;
	@Override
	public String getFullID() {
		return fullId;
	}
	
	private int unitWeight;
	private boolean isMounted;
	private boolean isWounded;
	private String fullDescriptionBase;
	@Override
	public int getWeight() {
		return unitWeight;
	}
	
	public Armor getArmor() {
		return armor;
	}

	
	public int getHitChance() {
		return hitChance;
	}

	public int getEvadeChance() {
		return evadeChance;
	}

	public boolean isRangedAttack() {
		return isRangedAttack;
	}

	public boolean isMounted() {
		return isMounted;
	}

	public boolean isWounded() {
		return isWounded;
	}

	public void setWounded(boolean isWounded) {
		this.isWounded = isWounded;
		updateCompositeVariables();
	}
	
	@Override
	public String getPluralDescription() {
		return fullDescriptionBase + super.getPluralDescription();
	}
	
	public String getBasicId(){
		return super.getFullID();
	}
}

	
