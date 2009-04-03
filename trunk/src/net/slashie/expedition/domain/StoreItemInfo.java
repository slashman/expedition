package net.slashie.expedition.domain;

public class StoreItemInfo implements Cloneable{
	private String fullId;
	private int price;
	private int pack;
	private String packDescription;
	public String getPackDescription() {
		return packDescription;
	}
	public String getFullId() {
		return fullId;
	}
	public int getPrice() {
		return price;
	}
	public int getPack() {
		return pack;
	}
	public StoreItemInfo(String fullId, int price) {
		super();
		this.fullId = fullId;
		this.price = price;
		this.pack = 1;
	}
	
	public StoreItemInfo(String fullId, int price, int pack, String packDescription) {
		super();
		this.fullId = fullId;
		this.price = price;
		this.pack = pack;
		this.packDescription = packDescription;
	}

	public StoreItemInfo clone(){
		try {
			return (StoreItemInfo) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

}
