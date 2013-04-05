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
	private boolean isExotic;
	
	public AnimalNest(String code, String name, int aggresiveness, int radius, String members, boolean isExotic) {
		super();
		this.code = code;
		this.name = name;
		this.aggresiveness = aggresiveness;
		this.radius = radius;
		this.members = members;
		this.isExotic = isExotic;
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

	public boolean isExotic() {
		return isExotic;
	}

	public void setExotic(boolean isExotic) {
		this.isExotic = isExotic;
	}
}
