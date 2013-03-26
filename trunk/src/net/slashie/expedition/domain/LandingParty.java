package net.slashie.expedition.domain;

public class LandingParty {
	private String name;
	public enum LandingSpec {
		NONE, ONE, HALF, ALL
	}
	private LandingSpec soldiers = LandingSpec.NONE;
	private LandingSpec explorers = LandingSpec.NONE;
	private LandingSpec crew = LandingSpec.NONE;
	private LandingSpec doctors = LandingSpec.NONE;
	private LandingSpec carpenters = LandingSpec.NONE;
	private LandingSpec horses = LandingSpec.NONE;
	private LandingSpec colonists = LandingSpec.NONE;
	private LandingSpec botanist = LandingSpec.NONE;
	
	public LandingSpec getBotanist() {
		return botanist;
	}
	public void setBotanist(LandingSpec botanist) {
		this.botanist = botanist;
	}
	private boolean isMounted;
	
	public boolean isMounted() {
		return isMounted;
	}
	public void setMounted(boolean isMounted) {
		this.isMounted = isMounted;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public LandingSpec getSoldiers() {
		return soldiers;
	}
	public void setSoldiers(LandingSpec soldiers) {
		this.soldiers = soldiers;
	}
	public LandingSpec getExplorers() {
		return explorers;
	}
	public void setExplorers(LandingSpec explorers) {
		this.explorers = explorers;
	}
	public LandingSpec getCrew() {
		return crew;
	}
	public void setCrew(LandingSpec crew) {
		this.crew = crew;
	}
	public LandingSpec getDoctors() {
		return doctors;
	}
	public void setDoctors(LandingSpec doctors) {
		this.doctors = doctors;
	}
	public LandingSpec getHorses() {
		return horses;
	}
	public void setHorses(LandingSpec horses) {
		this.horses = horses;
	}
	public LandingSpec getCarpenters() {
		return carpenters;
	}
	public void setCarpenters(LandingSpec carpenters) {
		this.carpenters = carpenters;
	}
	public LandingSpec getColonists() {
		return colonists;
	}
	public void setColonists(LandingSpec colonists) {
		this.colonists = colonists;
	}
	
}
