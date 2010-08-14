package net.slashie.expedition.world;

public class Culture {
	private String code;
	private String name;
	private boolean isCivilization;
	public Culture(String code, String name, boolean isCivilization) {
		super();
		this.code = code;
		this.name = name;
		this.isCivilization = isCivilization;
	}
	public String getCode() {
		return code;
	}
	public String getName() {
		return name;
	}
	public boolean isCivilization() {
		return isCivilization;
	}
	
	
}
