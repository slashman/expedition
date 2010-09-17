package net.slashie.expedition.domain;

public class Food extends Good {
	private int unitsFedPerGood;
	public int getUnitsFedPerGood() {
		return unitsFedPerGood;
	}
	public Food(String classifierId, String description, String pluralDescription,
			int weight, int unitsFedPerGood, int baseValue) {
		super(classifierId, description, pluralDescription, weight, GoodType.SUPPLIES, baseValue);
		this.unitsFedPerGood = unitsFedPerGood;
				
	}
}
