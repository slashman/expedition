package net.slashie.expedition.domain;

public class Food extends ExpeditionItem {
	private int unitsFedPerGood;
	public int getUnitsFedPerGood() {
		return unitsFedPerGood;
	}
	public Food(String classifierId, String description, String pluralDescription,
			int weight, int unitsFedPerGood, int europeValue, int americaValue, int valuePack) {
		super(classifierId, description, pluralDescription, classifierId, weight, GoodType.SUPPLIES, europeValue, americaValue);
		this.unitsFedPerGood = unitsFedPerGood;
		this.setValuePack(valuePack);
				
	}
}
