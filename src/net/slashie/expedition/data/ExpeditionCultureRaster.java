package net.slashie.expedition.data;

import java.io.BufferedWriter;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import net.slashie.util.FileUtil;
import net.slashie.utils.Position;
import net.slashie.utils.RasterWriter;

public class ExpeditionCultureRaster {
	public static Hashtable<String, String> culturesCharmap = new Hashtable<String, String>();

	static {
		culturesCharmap.put("255,0,55", "MOUNT");
		culturesCharmap.put("255,0,102", "MISSI");
		culturesCharmap.put("255,0,170", "AZTEC");
		culturesCharmap.put("255,0,242", "HUAST");
		culturesCharmap.put("191,0,255", "MIXTE");
		culturesCharmap.put("132,0,255", "MAYA");
		culturesCharmap.put("60,0,255", "PURHE");
		culturesCharmap.put("0,25,255", "TOTON");
		culturesCharmap.put("0,98,255", "ZAPOT");
		culturesCharmap.put("0,161,255", "CANAR");
		culturesCharmap.put("0,247,255", "CHACH");
		culturesCharmap.put("0,255,196", "CHIMU");
		culturesCharmap.put("0,255,144", "INCA");
		culturesCharmap.put("0,255,85", "MUISC");
		culturesCharmap.put("17,255,0", "TAIRO");
		culturesCharmap.put("120,219,87", "ARTIC");
		culturesCharmap.put("146,219,87", "HUNTE");
		culturesCharmap.put("171,219,87", "FISHI");
		culturesCharmap.put("204,219,87", "BISON");
		culturesCharmap.put("219,210,87", "FARME");
	}
	
	public static void main(String[] args) throws Exception{
		System.out.println("Expedition Culture Raster");
		System.out.println("Reading image "+args[0]+" into data file "+args[1]);
		Map<Position, String> culturesMap = new CultureWriter(culturesCharmap).read(args[0]);
		BufferedWriter w = FileUtil.getWriter(args[1]);
		Set<Entry<Position, String>> entrySet = culturesMap.entrySet();
		for (Entry<Position,String> entry: entrySet){
			w.write(entry.getKey().x+","+entry.getKey().y+","+entry.getValue());
			w.newLine();
		}
		w.close();
		System.out.println("Raster Finished");
	}
	
}
