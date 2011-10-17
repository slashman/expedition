package net.slashie.expedition.town;

import java.io.Serializable;
import java.util.Map;

import net.slashie.expedition.item.StorageType;

@SuppressWarnings("serial")
public class Building implements Cloneable, Serializable{
	private String id;
	private String description;
	private String longDescription;
	private int woodCost;
	private int buildTimeCost;
	private int populationCapacity;
	private int minBuildDays;
	private Map<StorageType, Integer> storageCapacity;

	public Building(String id, String description, String longDescription, int woodCost,
			int buildTimeCost, int populationCapacity, int minBuildDays, Map<StorageType, Integer> storageCapacity) {
		super();
		this.id = id;
		this.description = description;
		this.longDescription = longDescription;
		this.woodCost = woodCost;
		this.buildTimeCost = buildTimeCost;
		this.populationCapacity = populationCapacity;
		this.minBuildDays = minBuildDays;
		this.storageCapacity = storageCapacity;
	}
	
	public String getDescription() {
		return description;
	}
	public int getWoodCost() {
		return woodCost;
	}
	public int getBuildTimeCost() {
		return buildTimeCost;
	}
	public int getPopulationCapacity() {
		return populationCapacity;
	}
	
	@Override
	protected Building clone(){
		try {
			Building ret = (Building) super.clone();
			return ret;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String getId() {
		return id;
	}
	public int getMinBuildDays() {
		return minBuildDays;
	}
	public String getLongDescription() {
		return longDescription;
	}

	public int getStorageCapacity(StorageType storageType){
		return storageCapacity.get(storageType);
	}
	
	public boolean isPluralizableDescription() {
		return true;
	}
	
}
