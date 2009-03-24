package net.slashie.expedition.domain;

public class Valuable extends Good{
	private int goldValue;
	public int getGoldValue() {
		return goldValue;
	}
	public Valuable(String classifierId, String description, String pluralDescription,
			int weight, int goldValue) {
		super(classifierId, description, pluralDescription, weight, GoodType.VALUABLE);
		this.goldValue = goldValue;
	}
}
