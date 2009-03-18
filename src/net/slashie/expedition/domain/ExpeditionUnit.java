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
	private String special;
	
	/**
	 * Returns the perceived power for the unit
	 * @return
	 */
	public int getPower(){
		return getAttack()*3+getDefense()*2+getSpeed()+getRange();
	}
	
	public int getDailyFoodConsumption() {
		return dailyFoodConsumption;
	}

	public void setDailyFoodConsumption(int dailyFoodConsumption) {
		this.dailyFoodConsumption = dailyFoodConsumption;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getRange() {
		return range;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public int getAttack() {
		return attack;
	}

	public void setAttack(int attack) {
		this.attack = attack;
	}

	public int getDefense() {
		return defense;
	}

	public void setDefense(int defense) {
		this.defense = defense;
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
			String special) {
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
	}
	
}
