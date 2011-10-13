package net.slashie.expedition.level;

public class GlobeMapModel {
	private static GlobeModel singleton;
	
	public static GlobeModel getSingleton() {
		return singleton;
	}

	public static void setSingleton(GlobeModel singleton) {
		GlobeMapModel.singleton = singleton;
	}
}
