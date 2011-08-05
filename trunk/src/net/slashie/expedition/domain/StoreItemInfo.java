package net.slashie.expedition.domain;

import java.io.Serializable;

import net.slashie.expedition.item.ItemFactory;

public class StoreItemInfo implements Cloneable, Serializable{
	private static final long serialVersionUID = 1L;
	
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
	
	public void setFullId(String fullId) {
		this.fullId = fullId;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public void setPack(int pack) {
		this.pack = pack;
	}
	public void setPackDescription(String packDescription) {
		this.packDescription = packDescription;
	}
	
	public StoreItemInfo(String fullId) {
		super();
		this.fullId = fullId;
		this.price = ItemFactory.getEuropeanPackPrize(fullId);
		this.pack = 1;
		this.packDescription = "units";
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
