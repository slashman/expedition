package net.slashie.expedition.locations;

import net.slashie.serf.level.Unleasher;
import net.slashie.serf.levelGeneration.FileStaticPattern;

public class World extends FileStaticPattern {
	public World () {
		charMap.put(".", "GRASS");
		charMap.put("E", "GRASS EXPEDITION NATIVE_EXPEDITION1");
		charMap.put("F", "GRASS EXPEDITION NATIVE_EXPEDITION2");
		charMap.put("-", "WATER");
		charMap.put("m", "MOUNTAIN");
		charMap.put("f", "FOREST");
		//charMap.put("S", "PORT_CITY EXIT_GLOBE_COORDINATES SPAIN");
		charMap.put("S", "GRASS");
		
		charMap.put("$", "SNOWY_MOUNTAIN"); // Very high mountains
		charMap.put("^", "MOUNTAIN"); // Very high mountains
		charMap.put(".", "GRASS"); // Grassland
		charMap.put(" ", "WATER"); // Deep Sea
		charMap.put(",", "PLAINS"); // Plain
		charMap.put("-", "WATER"); // Deep Sea
		charMap.put("&", "FOREST"); // Forest
		charMap.put("#", "WATER2"); // River
		
		
		unleashers = new Unleasher[]{};
	}

	@Override
	public String getDescription() {
		return "The World";
	}
	
	@Override
	public String getFilename() {
		return "bigMap";
	}
}
