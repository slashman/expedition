package net.slashie.expedition.domain;

public class Food extends Good {
	private int unitsFedPerGood;
	public int getUnitsFedPerGood() {
		return unitsFedPerGood;
	}
	public Food(String classifierId, String description, String pluralDescription,
			int weight, int unitsFedPerGood) {
		super(classifierId, description, pluralDescription, weight, GoodType.FOOD);
		this.unitsFedPerGood = unitsFedPerGood;
				
	}
}
