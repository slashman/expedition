package net.slashie.expedition.world;

public class SettlementType {
	private String name;
	private int populationCost;
	private int woodCost;
	private int stoneCost;
	private int goldCost;
	public String getName() {
		return name;
	}
	public int getPopulationCost() {
		return populationCost;
	}
	public int getWoodCost() {
		return woodCost;
	}
	public int getStoneCost() {
		return stoneCost;
	}
	public int getGoldCost() {
		return goldCost;
	}
	public SettlementType(String name, int populationCost, int woodCost,
			int stoneCost, int goldCost) {
		super();
		this.name = name;
		this.populationCost = populationCost;
		this.woodCost = woodCost;
		this.stoneCost = stoneCost;
		this.goldCost = goldCost;
	}
}
