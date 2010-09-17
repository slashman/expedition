package net.slashie.expedition.domain;


public class Good extends ExpeditionItem {
	private GoodType goodType;
	private int baseValue;
	
	public Good(String classifierId, String description, String pluralDescription, int weight, GoodType goodType, int baseValue) {
		super(classifierId, description, pluralDescription, classifierId, weight);
		this.goodType = goodType;
		this.baseValue = baseValue;
	}

	public GoodType getGoodType() {
		return goodType;
	}

	public int getBaseValue() {
		return baseValue;
	}
}
