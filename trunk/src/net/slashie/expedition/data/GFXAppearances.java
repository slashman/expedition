package net.slashie.expedition.data;

import java.awt.image.BufferedImage;
import java.util.Hashtable;

import net.slashie.libjcsi.ConsoleSystemInterface;
import net.slashie.serf.game.SworeGame;
import net.slashie.serf.ui.consoleUI.CharAppearance;
import net.slashie.serf.ui.oryxUI.GFXAppearance;
import net.slashie.utils.ImageUtils;

public class GFXAppearances {
	private static Hashtable images = new Hashtable();

	public static GFXAppearance createAppearance(String ID, int xpos, int ypos){
		xpos--;
		ypos--;
		String filename = "res/expedition.gif";
		String darkfilename = "res/expedition_d.gif";
		BufferedImage bigImage = (BufferedImage) images.get(filename);
		BufferedImage bigDarkImage = (BufferedImage) images.get(darkfilename);
		if (bigImage == null){
			try {
				bigImage = ImageUtils.createImage(filename);
			} catch (Exception e){
				SworeGame.crash("Error loading image "+filename, e);
			}
			images.put(filename, bigImage);
			try {
				bigDarkImage = ImageUtils.createImage(darkfilename);
			} catch (Exception e){
				SworeGame.crash("Error loading image "+darkfilename, e);
			}
			images.put(darkfilename, bigDarkImage);
		}
		try {
			BufferedImage img = ImageUtils.crearImagen(bigImage,  xpos*24, ypos*24, 24, 24);
			BufferedImage imgd = ImageUtils.crearImagen(bigDarkImage,  xpos*24, ypos*24, 24, 24);
			GFXAppearance ret = new GFXAppearance(ID, img, imgd, 0,0);
			return ret;
		} catch (Exception e){
			SworeGame.crash("Error loading image "+filename, e);
		}
		return null;
	}
	
	public static GFXAppearance[] getGFXAppearances(){
		
		return new GFXAppearance[]{
				//Expeditions
				createAppearance("EXPEDITION", 9,12),
				createAppearance("SHIP_EXPEDITION", 1 ,5),
				
				//Non principal Expeditions
				createAppearance("HOSTILE_EXPEDITION", 1 ,11),
				
				//Overworld Terrain
				createAppearance("GRASS", 3 ,3),
				createAppearance("PLAINS", 4 ,3),
				createAppearance("WATER", 1 ,1),
				createAppearance("WATER2", 3 ,1),
				createAppearance("MOUNTAIN", 4 ,2),
				createAppearance("FOREST", 1 ,4),
				createAppearance("PORT_CITY", 11 ,6),
				
				//Overworld Features
				createAppearance("SHIP", 1 ,5),
				createAppearance("GOODS_CACHE", 2 ,6),
				createAppearance("TOWN", 11 ,6),

				//Inworld terrain
				createAppearance("SPAIN_WALL", 7 ,3),
				createAppearance("DEPARTURE", 5 ,4),
				createAppearance("CITY_GRASS", 4 ,3),
				createAppearance("CITY_SEA", 1 ,2),
				
				createAppearance("SPAIN_GRASS", 4 ,3),
				createAppearance("SPAIN_FLOOR", 7 ,4),
				createAppearance("SPAIN_WATER", 1 ,2),
				createAppearance("SPAIN_HOUSE", 11 ,6),
				createAppearance("SPAIN_SHIP", 1 ,5),
				createAppearance("SPAIN_DOCKS", 5 ,4),
				createAppearance("SPAIN_COLUMN", 8 ,4),
				createAppearance("SPAIN_CASTLE", 8 ,3),
				
				createAppearance("CASTLE_FLOOR", 9 ,3),
				createAppearance("BLUE_CARPET", 11 ,3),
				createAppearance("RED_CARPET", 10 ,3),
				createAppearance("CASTLE_WALL", 8 ,3),
				
				createAppearance("CASTLE_WINDOW", 10 ,4),
				createAppearance("SPAIN_BANNER", 2 ,13),
				createAppearance("THRONE", 3 ,13),
				createAppearance("SPAIN_CREST", 1 ,13),
				createAppearance("CASTLE_CURTAIN", 4 ,13),
				createAppearance("CASTLE_GATE", 11 ,4),
				createAppearance("KING_FERDINAND", 11 ,10),
				createAppearance("QUEEN_ISABELLE", 12 ,10),

				createAppearance("SAILOR", 1 ,10),
				createAppearance("ROGUE", 2 ,10),
				createAppearance("MARINE", 3 ,10),
				createAppearance("SOLDIER", 4 ,10),
				createAppearance("ARCHER", 5 ,10),
				createAppearance("CAPTAIN", 6 ,10),
				createAppearance("EXPLORER", 7 ,10),
				createAppearance("CARPENTER", 9 ,10),
				createAppearance("COLONIST", 10 ,10),
				
				createAppearance("NATIVE_WARRIOR", 1 ,11),
				createAppearance("NATIVE_BRAVE", 2 ,11),
				createAppearance("NATIVE_ARCHER", 3 ,11),
				
				createAppearance("GOODS_STORE", 1 ,6),
				createAppearance("WEAPONS_STORE", 2 ,6),
				createAppearance("PORT", 3 ,6),
				createAppearance("PUB", 4 ,6),
				createAppearance("GUILD", 5 ,6),
				
				createAppearance("FOOD", 4 ,7),
				createAppearance("RUM", 6 ,8),
				createAppearance("WOOD", 7 ,8),
				createAppearance("FRESHWATER", 8 ,7),
				createAppearance("FOOD_SAUERKRAUT", 9 ,7),
				createAppearance("GOLD_NUGGET", 7 ,7),
				createAppearance("GOLD_BRACELET", 7 ,7),
				createAppearance("DEAD_NATIVE", 4 ,12),
				createAppearance("NATIVE_ARTIFACT", 4 ,8),
				createAppearance("NATIVE_FOOD", 6 ,7),
				
				
				createAppearance("SPEARS", 1 ,9),
				createAppearance("SWORDS", 2 ,9),
				createAppearance("BOWS", 3 ,9),
				createAppearance("ARROWS", 4 ,9),
				createAppearance("GUNS", 5 ,9),
				createAppearance("STUDDED_LEATHER", 6 ,9),
				createAppearance("PLATE", 7 ,9),
				createAppearance("CARRACK", 5 ,5),
				createAppearance("CARAVEL", 1 ,5),

				
			};
	}

}
