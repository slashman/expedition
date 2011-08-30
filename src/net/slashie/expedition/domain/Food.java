package net.slashie.expedition.domain;

public class Food extends ExpeditionItem {
	private int unitsFedPerGood;
	public int getUnitsFedPerGood() {
		return unitsFedPerGood;
	}
	public Food(String classifierId, String description, String pluralDescription, String longDescription, 
			int weight, int unitsFedPerGood, int europeValue, int americaValue) {
		super(classifierId, description, pluralDescription, longDescription, classifierId, weight, GoodType.SUPPLIES, europeValue, americaValue);
		this.unitsFedPerGood = unitsFedPerGood;
		this.setValuePack(200); // This is the multiplier to calculate the prices
				
	}
}
