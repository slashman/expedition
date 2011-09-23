package net.slashie.expedition.domain;

public enum GoodType {
	ARMORY ("armory", "Weapons"),
	LIVESTOCK ("livestock", "Livestock"),
	PEOPLE ("people", "People"),
	SUPPLIES ("supplies", "Supplies"),
	TRADE_GOODS ("trade goods", "Trade Goods"), 
	VEHICLE ("vehicles", "Vehicles");
	

	GoodType(String description, String name){
		this.description = description;
	}
	private String description;
	
	static GoodType[] GOOD_TYPES_LIST = new GoodType []{
		PEOPLE,
		SUPPLIES,
		TRADE_GOODS,
		ARMORY,
		LIVESTOCK,
		VEHICLE
	};
	
	
	
	public String getDescription() {
		return description;
	}
	public static GoodType[] getGoodTypes() {
		return GOOD_TYPES_LIST;
	}
}
