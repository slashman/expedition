package net.slashie.expedition.domain;

import java.io.Serializable;

import net.slashie.expedition.item.ItemFactory;

public class StoreItemInfo implements Cloneable, Serializable{
	private static final long serialVersionUID = 1L;
	
	private String fullId;
	private int price;
	
	public String getFullId() {
		return fullId;
	}
	public int getPrice() {
		return price;
	}
	
	public void setFullId(String fullId) {
		this.fullId = fullId;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	
	public StoreItemInfo(String fullId) {
		super();
		this.fullId = fullId;
		this.price = ItemFactory.getEuropeanPackPrize(fullId);
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
