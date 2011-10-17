package net.slashie.expedition.domain;

import net.slashie.expedition.item.StorageType;

@SuppressWarnings("serial")
public class Food extends ExpeditionItem {
	private int unitsFedPerGood;
	public int getUnitsFedPerGood() {
		return unitsFedPerGood;
	}
	public Food(String classifierId, String description, String pluralDescription, String longDescription, 
			int weight, int unitsFedPerGood, int palosStoreValue, int baseTradingValue, StorageType storageType) {
		super(classifierId, description, pluralDescription, longDescription, classifierId, weight, GoodType.SUPPLIES, palosStoreValue, baseTradingValue, storageType);
		this.unitsFedPerGood = unitsFedPerGood;
	}
}
