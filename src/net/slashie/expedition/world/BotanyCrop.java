package net.slashie.expedition.world;

import java.util.List;

public class BotanyCrop {
	private String code;
	private String name;
	private List<Plant> plants;
	private int radius;
	
	
	public BotanyCrop(String code, String name, List<Plant> plants, int radius) {
		super();
		this.code = code;
		this.name = name;
		this.plants = plants;
		this.radius = radius;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Plant> getPlants() {
		return plants;
	}
	public void setPlants(List<Plant> plants) {
		this.plants = plants;
	}
	public void addPlant(Plant plant){
		plants.add(plant);
	}
	public int getRadius() {
		return radius;
	}
	public void setRadius(int radius) {
		this.radius = radius;
	}
}
