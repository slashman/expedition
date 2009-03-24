package net.slashie.expedition.domain;


public class Good extends ExpeditionItem {
	private GoodType goodType;
	
	public Good(String classifierId, String description, String pluralDescription,
			int weight, GoodType goodType) {
		super(classifierId, description, pluralDescription, classifierId, weight);
		this.goodType = goodType;
	}

	public GoodType getGoodType() {
		return goodType;
	}
}
