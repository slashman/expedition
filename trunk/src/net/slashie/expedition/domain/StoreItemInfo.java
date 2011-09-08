package net.slashie.expedition.domain;

import java.io.Serializable;

import net.slashie.expedition.item.ItemFactory;

@SuppressWarnings("serial")
public class StoreItemInfo implements Cloneable, Serializable{
	private String fullId;
	private int price;
	private int weeklyRestock;
	
	public String getFullId() {
		return fullId;
	}
	
	public int getPrice() {
		return price;
	}
	
	public int getWeeklyRestock() {
		return weeklyRestock;
	}
	
	public StoreItemInfo(String fullId, int weeklyRestock, int difference) {
		super();
		this.fullId = fullId;
		this.price = ItemFactory.getEuropeanPackPrize(fullId) + difference;
		this.weeklyRestock = weeklyRestock;
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
