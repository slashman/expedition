package net.slashie.expedition.domain;

import net.slashie.serf.action.Message;
import net.slashie.serf.level.AbstractLevel;
import net.slashie.serf.ui.UserInterface;
import net.slashie.utils.Util;

public class Vehicle extends ExpeditionItem{
	private boolean moveOnWater;
	private boolean moveOnAir;
	private int speedModifier;
	private int carryCapacity;
	private int resistance;
	private boolean fakeVehicle;
	private int maxResistance;
	
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
			boolean moveOnAir, int speedModifier, int carryCapacity, int resistance, boolean fakeVehicle) {
		super(classifierId, description,pluralDescription, classifierId, weight);
		this.moveOnWater = moveOnWater;
		this.moveOnAir = moveOnAir;
		this.speedModifier = speedModifier;
		this.carryCapacity = carryCapacity;
		this.resistance = resistance;
		this.maxResistance = resistance;
		this.fakeVehicle = fakeVehicle;
	}
	
	public int getResistance() {
		return resistance;
	}
	public void setResistance(int resistance) {
		this.resistance = resistance;
	}
	public void dailyWearOut(AbstractLevel l, int chance) {
		if (Util.chance(chance)){
			l.addMessage("A "+getDescription()+" suffers damage!");
			resistance --;
		}
	}
	
	public boolean isDestroyed(){
		return resistance <= 0;
	}

	public boolean isFakeVehicle() {
		return fakeVehicle;
	}
	public int getMaxResistance() {
		return maxResistance;
	}
	public void recoverResistance(int recovery) {
		resistance += recovery;
		if (resistance > maxResistance)
			resistance = maxResistance;
	}
	
	
}
