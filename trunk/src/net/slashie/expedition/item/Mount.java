package net.slashie.expedition.item;

import net.slashie.expedition.domain.ExpeditionItem;
import net.slashie.expedition.domain.GoodType;
import net.slashie.expedition.domain.Expedition.MovementSpeed;

@SuppressWarnings("serial")
public class Mount extends ExpeditionItem{
	private int carryCapacity;
	private MovementSpeed speed;
	public Mount(String classifierId, String description,
			String pluralDescription, String longDescription,
			String appearanceId, int weight, GoodType goodType,
			int palosStoreValue, int baseTradingValue, int carryCapacity, MovementSpeed speed) {
		super(classifierId, description, pluralDescription, longDescription,
				appearanceId, weight, goodType, palosStoreValue, baseTradingValue);
		this.carryCapacity = carryCapacity;
		this.speed = speed;
	}
	public int getCarryCapacity() {
		return carryCapacity;
	}
	public MovementSpeed getSpeed() {
		return speed;
	}

}
