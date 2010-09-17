package net.slashie.expedition.domain;

public enum GoodType {
	SUPPLIES ("supplies"),
	ARTIFACT ("artifacts"),
	TOOL ("tools"),
	WEAPON ("weapons");

	GoodType(String description){
		this.description = description;
	}
	private String description;
	static String[] CHOICES_LIST = new String []{
			"Supplies", "Artifacts", "Tools", "Weapons"
	};
	public static String[] getChoicesList() {
		return CHOICES_LIST;
	}
	public static GoodType fromChoice(int goodTypeChoice) {
		switch (goodTypeChoice){
		case 0:
			return SUPPLIES;
		case 1:
			return ARTIFACT;
		case 2:
			return TOOL;
		case 3:
			return WEAPON;
		}
		return null;
	}
	public String getDescription() {
		return description;
	}
}
