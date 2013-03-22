package net.slashie.expedition.world;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.ExpeditionFactory;
import net.slashie.utils.Position;

@SuppressWarnings("serial")
public class AnimalNest {
	private String code;
	private String name;
	private String members;
	private int aggresiveness;
	private int radius;
	
	public AnimalNest(String code, String name, int aggresiveness, int radius, String members) {
		super();
		this.code = code;
		this.name = name;
		this.aggresiveness = aggresiveness;
		this.radius = radius;
		this.members = members;
	}
	
	public Expedition deployAnimalGroup(){
		return ExpeditionFactory.deployAnimalGroup(this);
	}
	
	public String getMembers() {
		return members;
	}

	public void setMembers(String members) {
		this.members = members;
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

	public int getAggresiveness() {
		return aggresiveness;
	}

	public void setAggresiveness(int aggresiveness) {
		this.aggresiveness = aggresiveness;
	}
	
	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}
}
