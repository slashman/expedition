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
		String filename = "res/expedition.png";
		String darkfilename = "res/expedition_d.png";
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
	
	public static GFXAppearance sumAppearances(String ID, int xpos1, int ypos1, int xpos2, int ypos2){
		xpos1--;
		ypos1--;
		xpos2--;
		ypos2--;
		String filename = "res/expedition.png";
		String darkfilename = "res/expedition_d.png";
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
			BufferedImage img1 = ImageUtils.crearImagen(bigImage,  xpos1*24, ypos1*24, 24, 24);
			BufferedImage imgd1 = ImageUtils.crearImagen(bigDarkImage,  xpos1*24, ypos1*24, 24, 24);
			BufferedImage img2 = ImageUtils.crearImagen(bigImage,  xpos2*24, ypos2*24, 24, 24);
			BufferedImage imgd2 = ImageUtils.crearImagen(bigDarkImage,  xpos2*24, ypos2*24, 24, 24);
			BufferedImage imgSum = ImageUtils.overlay(img1, img2, 0, 0);
			BufferedImage imgSumD = ImageUtils.overlay(imgd1, imgd2, 0, 0);
			GFXAppearance ret = new GFXAppearance(ID, imgSum, imgSumD, 0,0);
			return ret;
		} catch (Exception e){
			SworeGame.crash("Error loading image "+filename, e);
		}
		return null;
		
	}

	
	public static GFXAppearance[] getGFXAppearances(){
		
		return new GFXAppearance[]{
				//Expeditions
				createAppearance("EXPEDITION", 1,16),
				createAppearance("SHIP_EXPEDITION_N", 1 ,15),
				createAppearance("SHIP_EXPEDITION_E", 2 ,15),
				createAppearance("SHIP_EXPEDITION_S", 3 ,15),
				createAppearance("SHIP_EXPEDITION_W", 4 ,15),
				createAppearance("SHIP_EXPEDITION_NE", 5 ,15),
				createAppearance("SHIP_EXPEDITION_SE", 6 ,15),
				createAppearance("SHIP_EXPEDITION_SW", 7 ,15),
				createAppearance("SHIP_EXPEDITION_NW", 8 ,15),
				createAppearance("BOAT_EXPEDITION", 3, 16),
				createAppearance("HORSE_EXPEDITION", 2,16),

				
				
				//Non principal Expeditions
				createAppearance("HOSTILE_EXPEDITION", 6,16),
				createAppearance("BOAT_HOSTILE_EXPEDITION", 8,16),
				
				//Overworld Terrain
				createAppearance("GRASS", 3 ,3),
				createAppearance("PLAINS", 4 ,3),
				createAppearance("WATER", 1 ,1),
				createAppearance("WATER2", 3 ,1),
				createAppearance("WATER_SHADOW", 4 ,20),
				createAppearance("MOUNTAIN", 5 ,17),
				createAppearance("SNOWY_MOUNTAIN", 6 ,17),
				createAppearance("FOREST", 2 ,17),
				createAppearance("CHOPPED_FOREST", 4 ,17),
				createAppearance("PORT_CITY", 11 ,6),
				createAppearance("STORM", 1, 17),
				
				//Overworld Features
				createAppearance("SHIP", 1 ,5),
				createAppearance("GOODS_CACHE", 9 ,6),
				createAppearance("TOWN", 11 ,6),
				createAppearance("NATIVE_VILLAGE", 10 ,6),
				createAppearance("NATIVE_TOWN", 10 ,6),
				createAppearance("NATIVE_CITY", 11 ,6),

				//Inworld terrain
				createAppearance("SPAIN_WALL", 7 ,3),
				createAppearance("DEPARTURE", 5 ,4),
				createAppearance("CITY_GRASS", 4 ,3),
				createAppearance("CITY_SEA", 1 ,2),
				
				createAppearance("SPAIN_GRASS", 4 ,3),
				createAppearance("SPAIN_GRASS_BLOCKED", 4 ,3),
				createAppearance("SPAIN_FLOOR", 7 ,4),
				createAppearance("SPAIN_WATER", 1 ,2),
				createAppearance("SPAIN_HOUSE", 11 ,6),
				createAppearance("SPAIN_SHIP", 1 ,5),
				createAppearance("SPAIN_DOCKS", 5 ,4),
				createAppearance("SPAIN_COLUMN", 8 ,4),
				createAppearance("SPAIN_CASTLE", 8 ,3),
				
				createAppearance("CASTLE_FLOOR", 9 ,3),
				createAppearance("CASTLE_PLAZA", 4 ,3),
				createAppearance("CASTLE_TREE", 2 ,4),
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
				createAppearance("DOMINIK", 1 ,19),
				createAppearance("COLOMBUS", 2 ,19),
				createAppearance("BIZCOCHO", 4 ,19),
				createAppearance("CRISTOFORO", 5, 19),
				createAppearance("SANTIAGO", 6, 19),

				createAppearance("SAILOR", 1 ,10),
				createAppearance("ROGUE", 2 ,10),
				createAppearance("MARINE", 3 ,10),
				createAppearance("SOLDIER", 6 ,10),
				createAppearance("GUARD", 7 ,19),
				createAppearance("ARCHER", 5 ,10),
				createAppearance("CAPTAIN", 7 ,10),
				createAppearance("EXPLORER", 8 ,10),
				createAppearance("CARPENTER", 9 ,10),
				createAppearance("COLONIST", 10 ,10),
				
				sumAppearances("SAILOR_MOUNTED", 1 ,10 , 1, 25),
				sumAppearances("ROGUE_MOUNTED", 2 ,10, 1, 25),
				sumAppearances("MARINE_MOUNTED", 3 ,10, 1, 25),
				sumAppearances("SOLDIER_MOUNTED", 6 ,10, 1, 25),
				sumAppearances("GUARD_MOUNTED", 7 ,19, 1, 25),
				sumAppearances("ARCHER_MOUNTED", 5 ,10, 1, 25),
				sumAppearances("CAPTAIN_MOUNTED", 7 ,10, 1, 25),
				sumAppearances("EXPLORER_MOUNTED", 8 ,10, 1, 25),
				sumAppearances("CARPENTER_MOUNTED", 9 ,10, 1, 25),
				sumAppearances("COLONIST_MOUNTED", 10 ,10, 1, 25),
				sumAppearances("COLOMBUS_MOUNTED", 2 ,19, 1, 25),
				sumAppearances("NATIVE_WARRIOR_MOUNTED", 1 ,11, 1, 25),
				sumAppearances("NATIVE_ARCHER_MOUNTED", 3 ,11, 1, 25),
				sumAppearances("NATIVE_COMMONER_MOUNTED", 3 ,19, 1, 25),
				sumAppearances("NATIVE_SHAMAN_MOUNTED", 3 ,11, 1, 25),
				sumAppearances("QUETZAL_ARCHER_MOUNTED", 8 ,19, 1, 25),
				sumAppearances("EAGLE_WARRIOR_MOUNTED", 9 ,19, 1, 25),
				sumAppearances("JAGUAR_WARRIOR_MOUNTED", 10 ,19, 1, 25),
				
				createAppearance("NATIVE_WARRIOR", 1 ,11),
				createAppearance("NATIVE_ARCHER", 3 ,11),
				createAppearance("NATIVE_COMMONER", 3 ,19),
				createAppearance("NATIVE_SHAMAN", 3 ,11),
				createAppearance("QUETZAL_ARCHER", 8 ,19),
				createAppearance("EAGLE_WARRIOR", 9 ,19),
				createAppearance("JAGUAR_WARRIOR", 10 ,19),

				createAppearance("BOOKSHELF_L", 1, 20),
				createAppearance("BOOKSHELF", 2, 20),
				createAppearance("BOOKSHELF_R", 3, 20),
				
				createAppearance("GOODS_STORE", 1 ,6),
				createAppearance("WEAPONS_STORE", 2 ,6),
				createAppearance("PORT", 3 ,6),
				createAppearance("MERCHANT", 4 ,6),
				createAppearance("GUILD", 5 ,6),
				
				createAppearance("BISCUIT", 6 ,8),
				createAppearance("FRUIT", 7 ,7),
				createAppearance("BREAD", 7 ,7),
				createAppearance("DRIED_MEAT", 4 ,7),

				createAppearance("RUM", 5 ,8),
				createAppearance("WOOD", 4 ,24),
				createAppearance("FRESHWATER", 8 ,7),
				createAppearance("SAUERKRAUT", 9 ,7),
				createAppearance("DEAD_NATIVE", 4 ,12),
				createAppearance("NATIVE_ARTIFACTS", 4 ,8),
				
				createAppearance("BEANS", 1 ,24),
				createAppearance("MAIZE", 6 ,7),
				createAppearance("WHEAT", 5 ,24),
				createAppearance("POTATOES", 2 ,24),
				createAppearance("TOMATOES", 3 ,24),
				createAppearance("FISH", 1 ,22),

				
				createAppearance("COTTON", 4 ,8),
				createAppearance("SUGAR", 4 ,8),
				createAppearance("CLOTH", 4 ,8),
				createAppearance("COCA", 4 ,8),
				createAppearance("COCOA", 4 ,8),
				createAppearance("CHILI_PEPPER", 4 ,8),
				createAppearance("PINEAPPLE", 4 ,8),
				createAppearance("STRAWBERRIES", 4 ,8),
				createAppearance("TOBACCO", 4 ,8),
				createAppearance("COATS", 4 ,8),
				createAppearance("FURS", 4 ,8),
				createAppearance("GOLD_ARTIFACT", 4 ,8),
				createAppearance("NATIVE_ARTIFACTS", 4 ,8),

				
				createAppearance("STEEL_SPEAR", 1 ,9),
				createAppearance("STEEL_SWORD", 2 ,9),
				createAppearance("WOODEN_CROSSBOW", 4 ,9),
				createAppearance("COMPOSITE_BOW", 3 ,17),
				createAppearance("HARQUEBUS", 6 ,9),
				createAppearance("STUDDED_VEST", 7 ,9),
				createAppearance("BREASTPLATE", 8 ,9),
				
				createAppearance("PLUMED_BOW", 2 ,23),
				createAppearance("SIMPLE_BOW", 1 ,23),
				createAppearance("OBSIDIAN_SWORD", 4 ,23),
				createAppearance("WOODEN_MACE", 3 ,23),

				
				createAppearance("CARRACK", 5 ,5),
				createAppearance("CARAVEL", 1 ,5),

				createAppearance("ATTACK_DOG", 1 ,21),
				createAppearance("COW", 2 ,21),
				createAppearance("HORSE", 3 ,21),
				createAppearance("PIGS", 4 ,21),
				createAppearance("LLAMA", 5 ,21),
			};
	}

}
