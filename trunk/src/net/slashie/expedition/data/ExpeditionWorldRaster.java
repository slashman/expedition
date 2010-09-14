package net.slashie.expedition.data;

import java.util.Hashtable;

import net.slashie.utils.RasterWriter;

public class ExpeditionWorldRaster {
	public static Hashtable<String, String> worldCharmap = new Hashtable<String, String>();

	static {
		worldCharmap.put("255,0,208", "$"); // Very high mountains
		worldCharmap.put("132,130,77", "^"); // Very high mountains
		worldCharmap.put("187,254,145", "."); // Grassland
		worldCharmap.put("33,130,188", " "); // Deep Sea
		worldCharmap.put("189,188,106", ","); // Plain
		worldCharmap.put("149,190,235", " "); // Medium Sea
		worldCharmap.put("162,254,252", "-"); // Shallow Sea
		worldCharmap.put("132,190,130", "&"); // Forest
		worldCharmap.put("162,254,252", " "); // Medium Sea
		worldCharmap.put("41,190,250", "#"); // River
		worldCharmap.put("255,0,0", "S"); //
	}

	
	public static void main(String[] args){
		System.out.println("Expedition World Raster");
		System.out.println("Reading image "+args[0]+" into characters map "+args[1]);
		new RasterWriter(worldCharmap).gridRaster(args[0], args[1], 50, 50);
		System.out.println("Raster Finished");
	}
	
}
