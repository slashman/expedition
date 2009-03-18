package net.slashie.expedition.domain;

public class Vehicle extends ExpeditionItem{
	
	
	private boolean moveOnWater;
	private boolean moveOnAir;
	private int speedModifier;
	private int carryCapacity;
	
	public boolean isMoveOnWater() {
		return moveOnWater;
	}
	public void setMoveOnWater(boolean moveOnWater) {
		this.moveOnWater = moveOnWater;
	}
	public boolean isMoveOnAir() {
		return moveOnAir;
	}
	public void setMoveOnAir(boolean moveOnAir) {
		this.moveOnAir = moveOnAir;
	}
	public int getSpeedModifier() {
		return speedModifier;
	}
	public void setSpeedModifier(int speedModifier) {
		this.speedModifier = speedModifier;
	}
	public int getCarryCapacity() {
		return carryCapacity;
	}
	public void setCarryCapacity(int carryCapacity) {
		this.carryCapacity = carryCapacity;
	}
	public Vehicle(String classifierId, String description, String pluralDescription,
			int weight, boolean moveOnWater,
			boolean moveOnAir, int speedModifier, int carryCapacity) {
		super(classifierId, description,pluralDescription, classifierId, weight);
		this.moveOnWater = moveOnWater;
		this.moveOnAir = moveOnAir;
		this.speedModifier = speedModifier;
		this.carryCapacity = carryCapacity;
	}
}
