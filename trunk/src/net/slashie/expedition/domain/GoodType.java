package net.slashie.expedition.domain;

public enum GoodType {
	ARMORY ("armory"),
	LIVESTOCK ("livestock"),
	PEOPLE ("people"),
	SUPPLIES ("supplies"),
	TRADE_GOODS ("trade goods"), 
	VEHICLE ("vehicles");
	

	GoodType(String description){
		this.description = description;
	}
	private String description;
	static String[] CHOICES_LIST = new String []{
			"Weapons", "Livestock", "People", "Supplies", "Trade Goods"
	};
	static GoodType[] GOOD_TYPES_LIST = new GoodType []{
		PEOPLE,
		SUPPLIES,
		TRADE_GOODS,
		ARMORY,
		LIVESTOCK,
		VEHICLE
	};
	
	public static String[] getChoicesList() {
		return CHOICES_LIST;
	}
	public static GoodType fromChoice(int goodTypeChoice) {
		switch (goodTypeChoice){
		case 0:
			return ARMORY;
		case 1:
			return LIVESTOCK;
		case 2:
			return PEOPLE;
		case 3:
			return SUPPLIES;
		case 4:
			return TRADE_GOODS;
		}
		return null;
	}
	public String getDescription() {
		return description;
	}
	public static GoodType[] getGoodTypes() {
		return GOOD_TYPES_LIST;
	}
}
