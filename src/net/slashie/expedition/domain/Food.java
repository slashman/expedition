package net.slashie.expedition.domain;

@SuppressWarnings("serial")
public class Food extends ExpeditionItem {
	private int unitsFedPerGood;
	public int getUnitsFedPerGood() {
		return unitsFedPerGood;
	}
	public Food(String classifierId, String description, String pluralDescription, String longDescription, 
			int weight, int unitsFedPerGood, int palosStoreValue, int baseTradingValue) {
		super(classifierId, description, pluralDescription, longDescription, classifierId, weight, GoodType.SUPPLIES, palosStoreValue, baseTradingValue);
		this.unitsFedPerGood = unitsFedPerGood;
	}
}
