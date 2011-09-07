package net.slashie.expedition.domain;

@SuppressWarnings("serial")
public class StoreShipInfo extends StoreItemInfo {
	
	public StoreShipInfo(String fullId, String name, int difference) {
		super(fullId, difference);
		this.type = fullId;
		this.name = name;
	}
	
	private String type;
	private String name;
	public String getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}


}
