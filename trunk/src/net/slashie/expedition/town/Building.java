package net.slashie.expedition.town;

import java.io.Serializable;

public class Building implements Cloneable, Serializable{
	private String id;
	private String description;
	private String longDescription;
	private int woodCost;
	private int buildTimeCost;
	private int populationCapacity;
	private int minBuildDays;


	public Building(String id, String description, String longDescription, int woodCost,
			int buildTimeCost, int populationCapacity, int minBuildDays) {
		super();
		this.id = id;
		this.description = description;
		this.longDescription = longDescription;
		this.woodCost = woodCost;
		this.buildTimeCost = buildTimeCost;
		this.populationCapacity = populationCapacity;
		this.minBuildDays = minBuildDays;
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
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
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


	
}
