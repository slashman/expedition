package net.slashie.expedition.domain;

import java.io.Serializable;

import net.slashie.expedition.item.ItemFactory;

public class StoreItemInfo implements Cloneable, Serializable{
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
	public StoreItemInfo(String fullId) {
		super();
		this.fullId = fullId;
		this.price = ItemFactory.getEuropeanPackPrize(fullId);
		this.pack = 1;
	}
	
	public StoreItemInfo(String fullId, int pack, String packDescription) {
		super();
		this.fullId = fullId;
		this.price = ItemFactory.getEuropeanPackPrize(fullId);
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
