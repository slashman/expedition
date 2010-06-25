package net.slashie.expedition.domain;

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
	private Armor armor;
	
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

	

	public void setRange(int range) {
		this.range = range;
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
			int weight, int carryCapacity,
			int attack, int defense, 
			int dailyFoodConsumption,
			String[] weaponTypes, String[] armorTypes) {
		super(classifierId, description, pluralDescription, weight, false,
				false, 1, carryCapacity, 1, true);
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
		updateCompositeVariables();
	}

	public void setArm(Weapon createItem) {
		weapon = createItem;
		updateCompositeVariables();
	}
	
	private void updateCompositeVariables(){
		fullId = super.getFullID();
		unitWeight = super.getWeight();
		menuDescription = "";
		totalRange = 1;
		totalAttack = attack;
		totalDefense = defense;
		if (armor != null){
			menuDescription += "+"+armor.getDefense()+" ";
		}

		if (weapon != null){
			fullId += ","+weapon.getFullID();
			menuDescription += weapon.getDescription()+" ";
			unitWeight += weapon.getWeight();
			totalRange = range + weapon.getRange();
			totalAttack += weapon.getAttack();
			totalDefense += weapon.getDefense();
		}
		
		menuDescription += getDescription();
		if (armor != null){
			fullId += ";"+armor.getFullID();
			//menuDescription += "("+armor.getShortDescription()+")";
			unitWeight += armor.getWeight();
			totalDefense += armor.getDefense();
		}
		
		
		
	}
	
	private String menuDescription;
	@Override
	public String getMenuDescription() {
		return menuDescription;
	}
	
	private String fullId;
	@Override
	public String getFullID() {
		return fullId;
	}
	
	private int unitWeight;
	@Override
	public int getWeight() {
		return unitWeight;
	}
	
	private int totalRange;
	public int getRange() {
		return totalRange;
	}
	
	private int totalAttack;
	public int getAttack() {
		return totalAttack;
	}

	private int totalDefense;
	public int getDefense() {
		return totalDefense;
	}
	

	public Armor getArmor() {
		return armor;
	}

}
