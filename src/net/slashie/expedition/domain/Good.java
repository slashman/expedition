package net.slashie.expedition.domain;


public class Good extends ExpeditionItem {
	private GoodType goodType;
	private int unitsFedPerGood;
	
	public int getUnitsFedPerGood() {
		return unitsFedPerGood;
	}

	public void setUnitsFedPerGood(int unitsFedPerGood) {
		this.unitsFedPerGood = unitsFedPerGood;
	}

	
	
	public Good(String classifierId, String description, 
			int weight, GoodType goodType, int unitsFedPerGood) {
		super(classifierId, description, description, classifierId, weight);
		this.goodType = goodType;
		this.unitsFedPerGood = unitsFedPerGood;
	}

	public GoodType getGoodType() {
		return goodType;
	}
}
