package net.slashie.expedition.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.slashie.expedition.domain.Armor;
import net.slashie.expedition.domain.ExpeditionItem;
import net.slashie.expedition.domain.ExpeditionUnit;
import net.slashie.expedition.domain.Food;
import net.slashie.expedition.domain.GoodType;
import net.slashie.expedition.domain.NPC;
import net.slashie.expedition.domain.Store;
import net.slashie.expedition.domain.StoreItemInfo;
import net.slashie.expedition.domain.Vehicle;
import net.slashie.expedition.domain.Weapon;
import net.slashie.expedition.item.ItemFactory;
import net.slashie.expedition.town.Building;
import net.slashie.expedition.town.Farm;
import net.slashie.expedition.town.Building.SpecialCapability;
import net.slashie.expedition.world.Culture;
import net.slashie.expedition.world.ExpeditionCell;
import net.slashie.expedition.world.ExpeditionFeature;
import net.slashie.expedition.world.OverworldExpeditionCell;
import net.slashie.expedition.world.agents.DayShiftAgent;
import net.slashie.libjcsi.ConsoleSystemInterface;
import net.slashie.serf.level.AbstractCell;
import net.slashie.serf.ui.AppearanceFactory;
import net.slashie.serf.ui.consoleUI.CharAppearance;
import net.slashie.util.Pair;
import net.slashie.utils.roll.Roll;

public class ExpeditionDAO {
	final static int FOOD_PACK = 200;
	final static int LIQUID_PACK = 500;
	final static int WOOD_PACK = 50;
	
	public static AbstractCell[] getCellDefinitions (AppearanceFactory appFactory){
		
		Store goodsStore = new Store();
		goodsStore.setOwnerName("Goods Store");
		goodsStore.addItem(10000, new StoreItemInfo("BISCUIT", FOOD_PACK, "half-barrels"));
		goodsStore.addItem(10000, new StoreItemInfo("FRESHWATER", LIQUID_PACK, "barrels"));
		goodsStore.addItem(10000, new StoreItemInfo("SAUERKRAUT", FOOD_PACK, "half-barrels"));
		goodsStore.addItem(500, new StoreItemInfo("RUM", LIQUID_PACK, "barrels"));
		goodsStore.addItem(10000, new StoreItemInfo("WOOD", WOOD_PACK, "packs"));
		
		//Weapons Store
		Store weaponsStore = new Store();
		weaponsStore.setOwnerName("Armory");
		weaponsStore.addItem(5000, new StoreItemInfo("STEEL_SPEAR"));
		weaponsStore.addItem(1000, new StoreItemInfo("STEEL_SWORD"));
		weaponsStore.addItem(2000, new StoreItemInfo("COMPOSITE_BOW"));
		weaponsStore.addItem(1500, new StoreItemInfo("WOODEN_CROSSBOW"));
		weaponsStore.addItem(1000, new StoreItemInfo("HARQUEBUS"));
		weaponsStore.addItem(2000, new StoreItemInfo("STUDDED_VEST"));
		weaponsStore.addItem(1000, new StoreItemInfo("BREASTPLATE"));
		
		//Port
		Store port = new Store();
		port.setOwnerName("Harbor");
		port.addItem(30, new StoreItemInfo("CARRACK"));
		port.addItem(20, new StoreItemInfo("CARAVEL"));
		port.addItem(50, new StoreItemInfo("CAPTAIN"));
		port.addItem(10000, new StoreItemInfo("SAILOR"));
		port.addItem(50000, new StoreItemInfo("COLONIST"));
		port.addItem(35000, new StoreItemInfo("ROGUE"));
	
		//Pub
		Store merchant = new Store();
		merchant.setOwnerName("Trade Company");
		merchant.addItem(10000, new StoreItemInfo("COTTON"));
		merchant.addItem(10000, new StoreItemInfo("SUGAR"));
		merchant.addItem(10000, new StoreItemInfo("CLOTH"));
		merchant.addItem(200, new StoreItemInfo("COW"));
		merchant.addItem(200, new StoreItemInfo("HORSE"));
		merchant.addItem(200, new StoreItemInfo("PIGS"));
		
		//Guild
		Store guild = new Store();
		guild.setOwnerName("Guild");
		guild.addItem(7000, new StoreItemInfo("MARINE"));
		guild.addItem(5000, new StoreItemInfo("SOLDIER"));
		guild.addItem(1500, new StoreItemInfo("ARCHER"));
		guild.addItem(1000, new StoreItemInfo("CARPENTER"));
		guild.addItem(200, new StoreItemInfo("EXPLORER"));

		
		return new AbstractCell[]{
			//Overworld cells
			new OverworldExpeditionCell("GRASS", "Grass", true, 0, false, false, false, false, 25, 20, false),
			new OverworldExpeditionCell("PLAINS", "Grass", true, 0, false, false, false,false, 25, 20, false),
			new OverworldExpeditionCell("WATER", "Deep Water", false, 0, false, false, false,false, 5, 10, false),
			new OverworldExpeditionCell("WATER2", "Shallow Water", true, 0, true, false, false,false, 25, 20, false),
			new OverworldExpeditionCell("MOUNTAIN", "Mountain", true, 1, false, false, false,false, 10, 5, false),
			new OverworldExpeditionCell("SNOWY_MOUNTAIN", "Snow Mountain", true, 2, false, false, false,false, 0, 0, false),
			new OverworldExpeditionCell("FOREST", "Forest", true, 0, false, false, true,true, 20, 20, true),
			new OverworldExpeditionCell("PORT_CITY", "Port City", false, 0, false, false, false,false,0,0, false),
			
			//Inworld Cells
			new ExpeditionCell("GOODS_STORE", "Goods Store", goodsStore),
			new ExpeditionCell("WEAPONS_STORE", "Weapons Store", weaponsStore),
			new ExpeditionCell("PORT", "Harbor", port),
			new ExpeditionCell("MERCHANT", "Trade Company", merchant),
			new ExpeditionCell("GUILD", "Guild", guild),
			
			new ExpeditionCell("SPAIN_GRASS", "Grass"),
			new ExpeditionCell("SPAIN_GRASS_BLOCKED", "Grass", true, false),
			new ExpeditionCell("SPAIN_FLOOR", "Stone Floor"),
			new ExpeditionCell("SPAIN_DOCKS", "Docks"),
			new ExpeditionCell("SPAIN_WATER", "Ocean", false, false, true),
			new ExpeditionCell("SPAIN_WALL", "Stone Wall", true, true),
			new ExpeditionCell("BOOKSHELF", "Bookshelf", true, true),
			new ExpeditionCell("BOOKSHELF_R", "Bookshelf", true, true),
			new ExpeditionCell("BOOKSHELF_L", "Bookshelf", true, true),
			new ExpeditionCell("SPAIN_HOUSE", "Houses", true, true),
			new ExpeditionCell("SPAIN_SHIP", "Ships", false, false, true),
			new ExpeditionCell("SPAIN_COLUMN", "Column", true, true),
			new ExpeditionCell("SPAIN_CASTLE", "Spain Castle", true, true),
					
			new ExpeditionCell("DEPARTURE", "Docks", "DEPARTURE"),
			
			
			new ExpeditionCell("CASTLE_GATE", "Castle Gate", "TRAVEL_CASTLE"),
			
			new ExpeditionCell("CASTLE_FLOOR", "Castle Floor"),
			new ExpeditionCell("CASTLE_PLAZA", "Garden"),
			new ExpeditionCell("CASTLE_TREE", "Tree", true, true),
			new ExpeditionCell("BLUE_CARPET", "Carpet"),
			new ExpeditionCell("RED_CARPET", "Carpet"),
			new ExpeditionCell("CASTLE_WALL", "Castle Wall", true, true),
			new ExpeditionCell("SPAIN_BANNER", "Banner of Spain", true,true),
			new ExpeditionCell("CASTLE_WINDOW", "Castle Window", true, true),
			new ExpeditionCell("THRONE", "Throne of Spain", true, true),
			new ExpeditionCell("KING_FERDINAND", "Ferdinand II, King of Aragon", true, true),
			new ExpeditionCell("QUEEN_ISABELLE", "Isabella, Queen of Castile and León", true, true),
			
			new ExpeditionCell("SPAIN_CREST", "The Spain Coat of Arms", true, true),
			new ExpeditionCell("CASTLE_CURTAIN", "Curtain", true,true),
			
		};
	}
	
	public static CharAppearance[] getCharAppearances(){
		return new CharAppearance[]{
			//Expeditions
			new CharAppearance("EXPEDITION", '@', ConsoleSystemInterface.RED),
			new CharAppearance("SHIP_EXPEDITION", 'v', ConsoleSystemInterface.RED),
			new CharAppearance("SHIP_EXPEDITION_N", '^', ConsoleSystemInterface.RED),
			new CharAppearance("SHIP_EXPEDITION_E", '>', ConsoleSystemInterface.RED),
			new CharAppearance("SHIP_EXPEDITION_S", 'v', ConsoleSystemInterface.RED),
			new CharAppearance("SHIP_EXPEDITION_W", '<', ConsoleSystemInterface.RED),
			new CharAppearance("SHIP_EXPEDITION_NE", '7', ConsoleSystemInterface.RED),
			new CharAppearance("SHIP_EXPEDITION_SE", 'J', ConsoleSystemInterface.RED),
			new CharAppearance("SHIP_EXPEDITION_SW", 'L', ConsoleSystemInterface.RED),
			new CharAppearance("SHIP_EXPEDITION_NW", 'F', ConsoleSystemInterface.RED),
			new CharAppearance("BOAT_EXPEDITION", 'v', ConsoleSystemInterface.RED),
			
			//Non principal Expeditions
			new CharAppearance("HOSTILE_EXPEDITION", '@', ConsoleSystemInterface.BLUE),
			new CharAppearance("BOAT_HOSTILE_EXPEDITION", 'v', ConsoleSystemInterface.RED),
			
			//Overworld Terrain
			new CharAppearance("GRASS", '.', ConsoleSystemInterface.GREEN),
			new CharAppearance("PLAINS", '.', ConsoleSystemInterface.BROWN),
			new CharAppearance("WATER", '~', ConsoleSystemInterface.DARK_BLUE),
			new CharAppearance("WATER2", '~', ConsoleSystemInterface.TEAL),
			new CharAppearance("WATER_SHADOW", '*', ConsoleSystemInterface.TEAL),
			new CharAppearance("MOUNTAIN", '^', ConsoleSystemInterface.GREEN),
			new CharAppearance("SNOWY_MOUNTAIN", '^', ConsoleSystemInterface.CYAN),
			new CharAppearance("FOREST", '&', ConsoleSystemInterface.GREEN),
			new CharAppearance("CHOPPED_FOREST", '&', ConsoleSystemInterface.BROWN),
			new CharAppearance("PORT_CITY", '#', ConsoleSystemInterface.BROWN),
			new CharAppearance("STORM", '*', ConsoleSystemInterface.GRAY),

			
			//Overworld Features
			new CharAppearance("SHIP", 'v', ConsoleSystemInterface.RED),
			new CharAppearance("GOODS_CACHE", '*', ConsoleSystemInterface.RED),
			new CharAppearance("TOWN", '^', ConsoleSystemInterface.RED),

			
			//Inworld terrain
			new CharAppearance("DEPARTURE", '#', ConsoleSystemInterface.BROWN),
			new CharAppearance("CITY_GRASS", '.', ConsoleSystemInterface.BROWN),
			new CharAppearance("CITY_SEA", '~', ConsoleSystemInterface.BROWN),
			
			
			new CharAppearance("SPAIN_GRASS", '.', ConsoleSystemInterface.GREEN),
			new CharAppearance("SPAIN_GRASS_BLOCKED", '.', ConsoleSystemInterface.GREEN),
			new CharAppearance("SPAIN_FLOOR", '.', ConsoleSystemInterface.LIGHT_GRAY),
			new CharAppearance("SPAIN_WATER", '~', ConsoleSystemInterface.DARK_BLUE),
			new CharAppearance("SPAIN_WALL", '#', ConsoleSystemInterface.GRAY),
			new CharAppearance("SPAIN_HOUSE", '#', ConsoleSystemInterface.TEAL),
			new CharAppearance("SPAIN_SHIP", 'v', ConsoleSystemInterface.RED),
			new CharAppearance("SPAIN_DOCKS", '=', ConsoleSystemInterface.BROWN),
			new CharAppearance("SPAIN_COLUMN", 'o', ConsoleSystemInterface.BROWN),
			new CharAppearance("SPAIN_CASTLE", '#', ConsoleSystemInterface.BROWN),
			
			// Castle
			new CharAppearance("CASTLE_FLOOR", '.', ConsoleSystemInterface.BROWN),
			new CharAppearance("CASTLE_PLAZA", '.', ConsoleSystemInterface.GREEN),
			new CharAppearance("CASTLE_TREE", '&', ConsoleSystemInterface.BROWN),
			new CharAppearance("BLUE_CARPET", '=', ConsoleSystemInterface.BLUE),
			new CharAppearance("RED_CARPET", '=', ConsoleSystemInterface.RED),
			new CharAppearance("CASTLE_WALL", '#', ConsoleSystemInterface.PURPLE),
			new CharAppearance("SPAIN_BANNER", '+', ConsoleSystemInterface.PURPLE),
			new CharAppearance("CASTLE_WINDOW", '#', ConsoleSystemInterface.CYAN),
			new CharAppearance("THRONE", 'T', ConsoleSystemInterface.YELLOW),
			new CharAppearance("SPAIN_CREST", 'C', ConsoleSystemInterface.YELLOW),
			new CharAppearance("CASTLE_CURTAIN", '+', ConsoleSystemInterface.RED),
			new CharAppearance("CASTLE_GATE", '>', ConsoleSystemInterface.BROWN),
			
			new CharAppearance("KING_FERDINAND", '@', ConsoleSystemInterface.TEAL),
			new CharAppearance("QUEEN_ISABELLE", '@', ConsoleSystemInterface.LEMON),
			new CharAppearance("DOMINIK", '@', ConsoleSystemInterface.BROWN),
			new CharAppearance("COLOMBUS", '@', ConsoleSystemInterface.GRAY),
			new CharAppearance("BIZCOCHO", 'd', ConsoleSystemInterface.BROWN),
			new CharAppearance("CRISTOFORO", '@', ConsoleSystemInterface.GREEN),
			new CharAppearance("SANTIAGO", '@', ConsoleSystemInterface.YELLOW),

			new CharAppearance("BOOKSHELF_L", '#', ConsoleSystemInterface.BROWN),
			new CharAppearance("BOOKSHELF", '#', ConsoleSystemInterface.BROWN),
			new CharAppearance("BOOKSHELF_R", '#', ConsoleSystemInterface.BROWN),
			
			//Units
			new CharAppearance("SAILOR", 's', ConsoleSystemInterface.BLUE),
			new CharAppearance("ROGUE", 'r', ConsoleSystemInterface.BROWN),
			new CharAppearance("MARINE", 'm', ConsoleSystemInterface.TEAL),
			new CharAppearance("SOLDIER", 'S', ConsoleSystemInterface.GREEN),
			new CharAppearance("GUARD", 'S', ConsoleSystemInterface.GREEN),
			new CharAppearance("ARCHER", 'a', ConsoleSystemInterface.DARK_BLUE),			
			new CharAppearance("CAPTAIN", 'C', ConsoleSystemInterface.CYAN),
			new CharAppearance("EXPLORER", 'e', ConsoleSystemInterface.BLUE),
			new CharAppearance("CARPENTER", 'c', ConsoleSystemInterface.DARK_RED),
			new CharAppearance("COLONIST", 'c', ConsoleSystemInterface.YELLOW),
			
			new CharAppearance("EAGLE_WARRIOR", 'w', ConsoleSystemInterface.GREEN),
			new CharAppearance("JAGUAR_WARRIOR", 'w', ConsoleSystemInterface.YELLOW),
			new CharAppearance("QUETZAL_ARCHER", 'a', ConsoleSystemInterface.LEMON),
			new CharAppearance("NATIVE_WARRIOR", 'w', ConsoleSystemInterface.RED),
			new CharAppearance("NATIVE_ARCHER", 'a', ConsoleSystemInterface.DARK_RED),
			new CharAppearance("NATIVE_COMMONER", 'c', ConsoleSystemInterface.BLUE),
			new CharAppearance("NATIVE_SHAMAN", 'S', ConsoleSystemInterface.CYAN),
			
			
			new CharAppearance("NATIVE_VILLAGE", '^', ConsoleSystemInterface.DARK_RED),
			new CharAppearance("NATIVE_TOWN", '^', ConsoleSystemInterface.RED),
			new CharAppearance("NATIVE_CITY", '^', ConsoleSystemInterface.RED),

			
			//Stores
			new CharAppearance("GOODS_STORE", '1', ConsoleSystemInterface.RED),
			new CharAppearance("WEAPONS_STORE", '2', ConsoleSystemInterface.RED),
			new CharAppearance("PORT", '3', ConsoleSystemInterface.RED),
			new CharAppearance("MERCHANT", '4', ConsoleSystemInterface.RED),
			new CharAppearance("GUILD", '5', ConsoleSystemInterface.RED),
			
			
			//Goods
			new CharAppearance("BISCUIT", '%', ConsoleSystemInterface.BROWN),
			new CharAppearance("FRUIT", '%', ConsoleSystemInterface.YELLOW),
			new CharAppearance("BREAD", '%', ConsoleSystemInterface.YELLOW),
			new CharAppearance("DRIED_MEAT", '%', ConsoleSystemInterface.BROWN),
			new CharAppearance("WHEAT_FOODER", '%', ConsoleSystemInterface.BROWN),
			new CharAppearance("BEANS", '%', ConsoleSystemInterface.BROWN),
			new CharAppearance("MAIZE", '%', ConsoleSystemInterface.GREEN),
			new CharAppearance("WHEAT", '%', ConsoleSystemInterface.YELLOW),
			new CharAppearance("POTATOES", '%', ConsoleSystemInterface.BROWN),
			new CharAppearance("TOMATOES", '%', ConsoleSystemInterface.RED),
			new CharAppearance("FISH", '%', ConsoleSystemInterface.CYAN),

			
			new CharAppearance("RUM", '%', ConsoleSystemInterface.BROWN),
			new CharAppearance("WOOD", '=', ConsoleSystemInterface.BROWN),
			new CharAppearance("FRESHWATER", '%', ConsoleSystemInterface.CYAN),
			new CharAppearance("SAUERKRAUT", '%', ConsoleSystemInterface.GREEN),
			new CharAppearance("DEAD_NATIVE", '@', ConsoleSystemInterface.GRAY),
			
			new CharAppearance("COTTON", '$', ConsoleSystemInterface.GRAY),
			new CharAppearance("SUGAR", '$', ConsoleSystemInterface.GREEN),
			new CharAppearance("CLOTH", '$', ConsoleSystemInterface.GRAY),
			new CharAppearance("COCA", '$', ConsoleSystemInterface.GREEN),
			new CharAppearance("COCOA", '$', ConsoleSystemInterface.BROWN),
			new CharAppearance("CHILI_PEPPER", '$', ConsoleSystemInterface.RED),
			new CharAppearance("PINEAPPLE", '$', ConsoleSystemInterface.YELLOW),
			new CharAppearance("STRAWBERRIES", '$', ConsoleSystemInterface.RED),
			new CharAppearance("TOBACCO", '$', ConsoleSystemInterface.BROWN),
			new CharAppearance("COATS", '$', ConsoleSystemInterface.GRAY),
			new CharAppearance("FURS", '$', ConsoleSystemInterface.GRAY),
			new CharAppearance("GOLD_ARTIFACTS", '$', ConsoleSystemInterface.YELLOW),
			new CharAppearance("NATIVE_ARTIFACTS", '$', ConsoleSystemInterface.BROWN),

			
			//Weapons
			new CharAppearance("STEEL_SPEAR", '/', ConsoleSystemInterface.BROWN),
			new CharAppearance("STEEL_SWORD", '\\', ConsoleSystemInterface.LIGHT_GRAY),
			new CharAppearance("COMPOSITE_BOW", ')', ConsoleSystemInterface.BROWN),
			new CharAppearance("WOODEN_CROSSBOW", '}', ConsoleSystemInterface.BROWN),
			new CharAppearance("HARQUEBUS", '>', ConsoleSystemInterface.LIGHT_GRAY),
			new CharAppearance("STUDDED_VEST", ']', ConsoleSystemInterface.BROWN),
			new CharAppearance("BREASTPLATE", ']', ConsoleSystemInterface.LIGHT_GRAY),
			new CharAppearance("PLUMED_BOW", ')', ConsoleSystemInterface.BROWN),
			new CharAppearance("SIMPLE_BOW", ')', ConsoleSystemInterface.BROWN),
			new CharAppearance("OBSIDIAN_SWORD", '\\', ConsoleSystemInterface.LIGHT_GRAY),
			new CharAppearance("WOODEN_MACE", '\\', ConsoleSystemInterface.BROWN),

			// Livestock
			new CharAppearance("ATTACK_DOG", 'd', ConsoleSystemInterface.BROWN),
			new CharAppearance("COW", 'c', ConsoleSystemInterface.BROWN),
			new CharAppearance("HORSE", 'h', ConsoleSystemInterface.BROWN),
			new CharAppearance("PIGS", 'p', ConsoleSystemInterface.BROWN),
			new CharAppearance("LLAMA", 'l', ConsoleSystemInterface.BROWN),

			
			//Ships
			new CharAppearance("CARRACK", 'V', ConsoleSystemInterface.BROWN),
			new CharAppearance("CARAVEL", 'w', ConsoleSystemInterface.BROWN),
			
			
		};
	};
	
	private final static int UNIT_WEIGHT = 300;
	public static ExpeditionFeature[] getFeatureDefinitions(AppearanceFactory appFactory){
		return new ExpeditionFeature[0];
	}
	
	public static ExpeditionItem[] getItemDefinitions(AppearanceFactory appFactory){
		return new ExpeditionItem[]{
			//People
			new ExpeditionUnit("SAILOR", "Sailor", "Sailors", "Ship Crew.", UNIT_WEIGHT, 200,
					new Roll("1D1"),
					new Roll("1D1"),
					2,
					70,10,
					1,
				new String[]{"STEEL_SPEAR","WOODEN_MACE"},
				new String[]{""}, 60, 50),
			new ExpeditionUnit("ROGUE",  "Rogue",  "Rogues", "Survival Wolf", UNIT_WEIGHT, 250,
					new Roll("1D2"),
					new Roll("1D2"),
					2,
					85,15,
					1,
					new String[]{"STEEL_SWORD","STEEL_SPEAR","WOODEN_MACE"},
					new String[]{""}, 30, 50),
			new ExpeditionUnit("MARINE", "Marine", "Marines", "Trained Sea Soldier",UNIT_WEIGHT, 250,
					new Roll("1D3"),
					new Roll("1D2"),
					3,
					90,5,
					2,
					new String[]{"HARQUEBUS","WOODEN_CROSSBOW","STEEL_SWORD","STEEL_SPEAR","WOODEN_MACE"},
					new String[]{"STUDDED_VEST"}, 70, 100),
			new ExpeditionUnit("SOLDIER", "Soldier", "Soldiers", "Man-at-arms", UNIT_WEIGHT, 200,
					new Roll("1D3"),
					new Roll("1D3"),
					4,
					95,5,
					2,
					new String[]{"STEEL_SWORD", "HARQUEBUS", "STEEL_SPEAR","WOODEN_MACE"},
					new String[]{"BREASTPLATE", "STUDDED_VEST"}, 100, 200),
			new ExpeditionUnit("GUARD", "Burly Guard","Guards", "Heavily Armored Guard", UNIT_WEIGHT, 200,
					new Roll("1D3"),
					new Roll("1D3"),
					4,
					95,5,
					2,
					new String[]{"STEEL_SWORD", "HARQUEBUS", "STEEL_SPEAR","WOODEN_MACE"},
					new String[]{"BREASTPLATE", "STUDDED_VEST"}, 100, 200),
			new ExpeditionUnit("ARCHER", "Archer","Archers", "Skilled with Bow and Crossbow", UNIT_WEIGHT, 200,
					new Roll("1D2"),
					new Roll("1D2"),
					2,
					70,10,
					2,
					new String[]{"WOODEN_CROSSBOW", "COMPOSITE_BOW", "SIMPLE_BOW", "STEEL_SPEAR","WOODEN_MACE"},
					new String[]{"STUDDED_VEST"}, 120, 150),
			new ExpeditionUnit("CAPTAIN", "Captain","Captains", "Leads a Ship", UNIT_WEIGHT, 200,
					new Roll("1D1"),
					new Roll("1D1"),
					3,
					50,5,
					3,
					new String[]{"HARQUEBUS", "STEEL_SWORD", "STEEL_SPEAR","WOODEN_MACE" },
					new String[]{"BREASTPLATE", "STUDDED_VEST"}, 300, 50),
			new ExpeditionUnit("EXPLORER", "Explorer","Explorers", "Extends expedition field of view", UNIT_WEIGHT, 400,
					new Roll("1D2"),
					new Roll("1D1"),
					4,
					70,5,
					3,
					new String[]{"STEEL_SPEAR","WOODEN_MACE"},
					new String[]{}, 500, 50),
			new ExpeditionUnit("CARPENTER", "Carpenter","Carpenters", "Good at repairing ships", UNIT_WEIGHT, 300, 
					new Roll("1D1"),
					new Roll("1D1"),
					3,
					50,5, 
					1, 
					new String[]{"STEEL_SPEAR","WOODEN_MACE"},
					new String[]{""}, 60, 100),
			new ExpeditionUnit("COLONIST",  "Colonist",  "Colonists",  "Settler of new frontiers", UNIT_WEIGHT, 300, 
					new Roll("1D1"),
					new Roll("1D1"),
					1,
					50,5,
					1,
					new String[]{"STEEL_SPEAR","WOODEN_MACE"},
					new String[]{""}, 10, 10),
			//Native Units
			new ExpeditionUnit("NATIVE_WARRIOR", "Warrior","Warriors", "Trained to defend his tribe", UNIT_WEIGHT, 200, 
					new Roll("1D3"),
					new Roll("1D2"),
					3,
					80,15,
					1,
					new String[]{"WOODEN_MACE"},
					new String[]{""}, 200, 100),
			new ExpeditionUnit("NATIVE_ARCHER", "Archer","Archers", "Able with the bow", UNIT_WEIGHT, 200,
					new Roll("1D2"),
					new Roll("1D1"),
					2,
					60,10,
					1,
					new String[]{"SIMPLE_BOW", "WOODEN_MACE"},
					new String[]{""}, 200, 100),
			new ExpeditionUnit("NATIVE_COMMONER", "Native","Natives", "Working member of a tribe", UNIT_WEIGHT, 300, 
					new Roll("1D1"),
					new Roll("1D1"),
					1,
					50,5, 
					1,
					new String[]{"WOODEN_MACE"},
					new String[]{""}, 100, 10),
			new ExpeditionUnit("NATIVE_SHAMAN", "Shaman","Shamans", "Shaman of the tribe", UNIT_WEIGHT, 50, 
					new Roll("1D1"),
					new Roll("1D1"),
					1,
					50,5,
					2,
					new String[]{""},
					new String[]{""}, 100, 5000),
			new ExpeditionUnit("EAGLE_WARRIOR", "Eagle Warrior","Eagle Warriors", "Elite Warrior", UNIT_WEIGHT, 200,
					new Roll("2D1"),
					new Roll("1D1"),
					3,
					95,20,
					2,
					new String[]{"OBSIDIAN_SWORD", "WOODEN_MACE", "STEEL_SPEAR"},
					new String[]{}, 200, 200),
			new ExpeditionUnit("JAGUAR_WARRIOR", "Jaguar Warrior","Jaguar Warriors", "Elite Warrior", UNIT_WEIGHT, 200,
					new Roll("1D3"),
					new Roll("1D2"),
					4,
					95,10,
					2,
					new String[]{"OBSIDIAN_SWORD", "STEEL_SWORD", "WOODEN_MACE", "STEEL_SPEAR"},
					new String[]{}, 200, 300),
			new ExpeditionUnit("QUETZAL_ARCHER", "Quetzal Archer","Quetzal Archers", "Elite Archer", UNIT_WEIGHT, 200,
					new Roll("1D2"),
					new Roll("1D2"),
					3,
					95,5,
					2,
					new String[]{"PLUMED_BOW", "COMPOSITE_BOW", "SIMPLE_BOW","WOODEN_MACE"},
					new String[]{}, 200, 300),
			//Goods
			new Food("BISCUIT", "Biscuit", "Biscuit", "Food Ration", 3, 1, 40,50, FOOD_PACK),
			new Food("BREAD", "Bread", "Bread", "Food Ration", 3, 1, 100, 200, FOOD_PACK),
			new Food("DRIED_MEAT", "Dried Meat", "Dried Meat", "Food Ration", 3, 1, 400,50, FOOD_PACK),
			new Food("SAUERKRAUT", "Sauerkraut","Sauerkraut", "Food Ration", 3, 1, 400,50, FOOD_PACK),
			
			new Food("BEANS", "Beans", "Beans", "Food Ration", 3, 1, 800,100, FOOD_PACK),
			new Food("MAIZE", "Maize", "Maize", "Food Ration", 3, 1, 400,50, FOOD_PACK),
			new Food("WHEAT", "Wheat", "Wheat", "Food Ration", 3, 1, 20,40, FOOD_PACK),
			new Food("POTATOES", "Potatoes", "Potatoes", "Food Ration", 3, 1, 800,200, FOOD_PACK),
			new Food("TOMATOES", "Tomatoes", "Tomatoes", "Food Ration", 3, 1, 800,200, FOOD_PACK),
			new Food("FISH", "Fish", "Fish", "Food Ration", 3, 1, 100, 200, FOOD_PACK),
			new Food("FRUIT", "Fruit", "Fruit", "Food Ration", 3, 1, 100, 100, FOOD_PACK),
			
			new ExpeditionItem("FRESHWATER", "Freshwater", "Freshwater", "Liquid of Life", "FRESHWATER", 2, GoodType.SUPPLIES, 20,5),
			new ExpeditionItem("RUM", "Rum", "Rum", "Liquid of Life", "RUM", 2, GoodType.SUPPLIES, 400,500),
			
			new ExpeditionItem("WOOD", "Wooden log", "Wooden logs", "Wood piece", "WOOD", 10, GoodType.SUPPLIES, 100,5),
			
			// Trade Goods, Old world
			new ExpeditionItem("COTTON", "Cotton", "Cotton", "Trade Good", "COTTON", 200, GoodType.TRADE_GOODS, 200 , 400),
			new ExpeditionItem("SUGAR", "Sugar", "Sugar", "Trade Good", "SUGAR", 800, GoodType.TRADE_GOODS, 400 , 800),
			new ExpeditionItem("CLOTH", "Cloth", "Cloth", "Trade Good", "CLOTH", 50, GoodType.TRADE_GOODS, 600, 700 ),
			
			// Trade Goods, New world
			new ExpeditionItem("COCA", "Coca", "Coca", "Trade Good", "COCA", 500, GoodType.TRADE_GOODS, 200, 500 ),
			new ExpeditionItem("COCOA", "Cocoa", "Cocoa", "Trade Good", "COCOA", 600, GoodType.TRADE_GOODS, 1200, 400 ),
			new ExpeditionItem("CHILI_PEPPER", "Chili", "Chili", "Trade Good", "CHILI_PEPPER", 600, GoodType.TRADE_GOODS, 1800, 400 ),
			new ExpeditionItem("PINEAPPLE", "Pineapple", "Pineapple", "Trade Good", "PINEAPPLE", 800, GoodType.TRADE_GOODS, 1000, 500 ),
			new ExpeditionItem("STRAWBERRIES", "Strawberries", "Strawberries", "Trade Good", "STRAWBERRIES", 800, GoodType.TRADE_GOODS, 1000 , 300),
			new ExpeditionItem("TOBACCO", "Tobacco", "Tobacco", "Trade Good", "TOBACCO", 200, GoodType.TRADE_GOODS, 1500 , 500),
			new ExpeditionItem("COATS", "Coats", "Coats", "Trade Good", "COATS", 60, GoodType.TRADE_GOODS, 2000, 800 ),
			new ExpeditionItem("FURS", "Furs", "Furs", "Trade Good", "FURS", 60, GoodType.TRADE_GOODS, 1000, 500 ),
			new ExpeditionItem("GOLD_ARTIFACTS", "Gold Artifacts", "Gold Artifacts", "Trade Good", "GOLD_ARTIFACTS", 40, GoodType.TRADE_GOODS, 1600, 500 ),
			new ExpeditionItem("NATIVE_ARTIFACTS", "Native Artifacts", "Native Artifacts", "Trade Good", "NATIVE_ARTIFACTS", 20, GoodType.TRADE_GOODS, 800, 200 ),

			
			// Armory
			new Weapon("STEEL_SPEAR", "Steel Spear","Steel Spears", "Basic weapon [All classes]", new Roll("1D2"), new Roll("1D1"), false, 80, false, 8, 5, 20),
			new Weapon("STEEL_SWORD", "Steel Sword", "Steel Swords", "Strong weapon [Marine, Soldier, Captain, Rogue]", new Roll("2D3"), new Roll("1D1"), false, 90, false, 10, 10, 50),
			new Weapon("COMPOSITE_BOW", "Longbow", "Longbows", "Cheap ranged weapon [Archer]", new Roll("1D3"), new Roll("0"), false, 80, true, 5, 6, 20),
			new Weapon("WOODEN_CROSSBOW", "Crossbow", "Crossbows", "Simple ranged weapon [Marine, Archer]", new Roll("2D2"), new Roll("0"), true, 95, true, 10, 25, 50),
			new Weapon("HARQUEBUS", "Harquebus", "Harquebus", "Strong ranged weapon [Marine, Soldier, Captain]", new Roll("3D2"), new Roll("0"), true, 70, true, 10, 30, 50),
			
			new Weapon("PLUMED_BOW", "Plumed Bow", "Plumed Bows", "Spiritual Bows [Quetzal Archer]", new Roll("1D4"), new Roll("0"), true, 95, true, 5, 20, 10),
			new Weapon("SIMPLE_BOW", "Simple Bow", "Simple Bows", "Simple Wooden Bow [Archer, Native Archer, Quetzal Archer]", new Roll("1D2"), new Roll("0"), true, 80, true, 3, 3, 5),
			new Weapon("OBSIDIAN_SWORD", "Obsidian Sword", "Obsidian Swords", "Polished stone sword [Eagle Warrior, Jaguar Warrior]",new Roll("2D2"), new Roll("1D1"), false, 90, false, 15, 50, 100),
			new Weapon("WOODEN_MACE", "Wooden Mace", "Wooden Maces", "Simple wooden mace [All classes]", new Roll("1D1"), new Roll("0"), false, 70, false, 10, 3, 10),
			
			new Armor("BREASTPLATE", "Breastplate","Breastplates", "Metal breast armor [Soldier, Captain]", 20, 4, new Roll("1D4"), "Plate", 50, 200),
			new Armor("STUDDED_VEST", "Studded Vest", "Studded Vests", "[Marine, Soldier, Archer, Captain]", 10, 1, new Roll("1D2"), "Leather", 20, 10),
			
			// Livestock
			new ExpeditionItem("ATTACK_DOG", "Attack Dog", "Attack Dogs", "Men best companion", "ATTACK_DOG",  50, GoodType.LIVESTOCK, 100, 50),
			new ExpeditionItem("COW", "Cow", "Cows", "Cow", "COW",  500, GoodType.LIVESTOCK, 500, 600),
			new ExpeditionItem("HORSE", "Horse", "Horses", "Mounts", "HORSE",  800, GoodType.LIVESTOCK, 1000, 3000),
			new ExpeditionItem("PIGS", "Pig", "Pigs", "Big Pink Pig", "PIGS",  150, GoodType.LIVESTOCK, 300, 200),
			new ExpeditionItem("LLAMA", "Llama", "Llamas", "Horse-like creature", "LLAMA",  300, GoodType.LIVESTOCK, 800, 200),

			
			//Ships
			new Vehicle("CARRACK","Carrack","Carracks", "Big, bulky ship",1,true,false,false,3,100000, 10, false, GoodType.VEHICLE, 16000, 32000),
			new Vehicle("CARAVEL","Caravel","Caravels", "Small, quick ship", 1,true,false,false,4,60000, 15, false, GoodType.VEHICLE, 12000, 24000),
			
			//Special
			new ExpeditionUnit("KING_FERDINAND", "Ferdinand II, King of Aragón","Kings", "King", UNIT_WEIGHT, 300, 
					new Roll("1D1"),
					new Roll("1D1"),
					2,
					50,5, 
					1, new String[]{""},
					new String[]{""},5000, 5000) ,
			new ExpeditionUnit("QUEEN_ISABELLE", "Isabella, Queen of Castile and León","Queens", "Queen", UNIT_WEIGHT, 300, 
					new Roll("1D1"),
					new Roll("1D1"),
					1,
					50,5, 
					1, new String[]{""},
					new String[]{""}, 5000, 5000),
			new ExpeditionUnit("DOMINIK", "Friar Domenico Marcus","Mingoses", "", UNIT_WEIGHT, 300, 
					new Roll("1D1"),
					new Roll("1D1"),
					1,
					50,5, 
					1, new String[]{""},
					new String[]{""}, 5000, 5000),
			new ExpeditionUnit("COLOMBUS", "Colón","Colones", "",  UNIT_WEIGHT, 300, 
					new Roll("1D1"),
					new Roll("1D1"),
					1,
					50,5, 
					1, new String[]{""},
					new String[]{""}, 5000, 5000),
			new ExpeditionUnit("BIZCOCHO", "Bizcocho","Bizcochos","", UNIT_WEIGHT, 300, 
					new Roll("1D1"),
					new Roll("1D1"),
					1,
					50,5, 
					1, new String[]{""},
					new String[]{""}, 5000, 5000),
			new ExpeditionUnit("CRISTOFORO", "Juan Cristóforo, el bardo","Jices", "", UNIT_WEIGHT, 300, 
					new Roll("1D1"),
					new Roll("1D1"),
					1,
					50,5, 
					1, new String[]{""},
					new String[]{""}, 5000, 5000),
			new ExpeditionUnit("SANTIAGO", "Don Santiago","Santiagos", "",  UNIT_WEIGHT, 300, 
					new Roll("1D1"),
					new Roll("1D1"),
					1,
					50,5, 
					1, new String[]{""},
					new String[]{""}, 5000, 5000)
		};
		
	}

	private final static Map<SpecialCapability, Object> NO_CAPABILITIES = new HashMap<SpecialCapability, Object>();
	private final static Map<SpecialCapability, Object> PLAZA_CAPABILITIES = new HashMap<SpecialCapability, Object>();
	static {
		PLAZA_CAPABILITIES.put(SpecialCapability.FORAGED_FOOD_STORAGE, 50);
	}
	private final static Map<SpecialCapability, Object> STORAGE_CAPABILITIES = new HashMap<SpecialCapability, Object>();
	static {
		STORAGE_CAPABILITIES.put(SpecialCapability.FORAGED_FOOD_STORAGE, 300);
	}
	
	private static Building[] buildings = new Building[]{
		new Building("PLAZA", "Plaza", "Center of community life", 40, DayShiftAgent.TICKS_PER_DAY * 120, 0, 14, PLAZA_CAPABILITIES),
		new Building("HOUSE", "House", "Simple wooden house, can hold 10 persons", 40, DayShiftAgent.TICKS_PER_DAY * 90, 10, 7, NO_CAPABILITIES),
		// new Building("CHURCH", "Small Church", "", 60, DayShiftAgent.TICKS_PER_DAY * 60, 0, 10),
		
		new Building("STORAGE", "Storage Tower", "Can hold 300 units of foraged food", 60, DayShiftAgent.TICKS_PER_DAY * 240, 0, 14, STORAGE_CAPABILITIES),
		new Farm(),
		/*new Building(),
		new Building("MILL", "Mill", "Transforms grain into bread", 60, DayShiftAgent.TICKS_PER_DAY * 80, 0, 14),
		
		new Building("LUMBER_CAMP", "Lumber Camp", "Can hold 500 units of wood", 60, DayShiftAgent.TICKS_PER_DAY * 80, 0, 14),
		
		new Building("TRADING_OUTPOST", "Trading Outpost", "", 60, DayShiftAgent.TICKS_PER_DAY * 120, 0, 10),
		new Building("HARBOR", "Harbor", "", 60, DayShiftAgent.TICKS_PER_DAY * 120, 0, 14),
		new Building("BLACKSMITH", "Blacksmith", "", 60, DayShiftAgent.TICKS_PER_DAY * 200, 0, 14),
		new Building("MISSION", "Mission", "", 60, DayShiftAgent.TICKS_PER_DAY * 400, 0, 30),
		new Building("BARRACKS", "Barracks", "", 60, DayShiftAgent.TICKS_PER_DAY * 400, 0, 30),
		new Building("STABLES", "Stables", "", 60, DayShiftAgent.TICKS_PER_DAY * 500, 0, 30),
		new Building("FORT", "Fort", "", 60, DayShiftAgent.TICKS_PER_DAY * 1000, 0, 120),
		new Building("CATHEDRAL", "Cathedral", "", 60, DayShiftAgent.TICKS_PER_DAY * 1000, 0, 120),
		new Building("PALISADE", "Palisade", "", 60, DayShiftAgent.TICKS_PER_DAY * 400, 0, 30),
		new Building("STONE_WALL", "Stone Wall", "", 60, DayShiftAgent.TICKS_PER_DAY * 800, 0, 60),
		new Building("WATCH_TOWER", "Watch tower", "", 60, DayShiftAgent.TICKS_PER_DAY * 500, 0, 20)*/
		
	};
	
	public static ExpeditionUnit[] getUnitDefinitions(){
		return new ExpeditionUnit[]{
				
		};
	}
	
	private final static GoodType GOOD_TYPES[] = new GoodType[] {
		GoodType.SUPPLIES,
		GoodType.TRADE_GOODS,
		GoodType.LIVESTOCK,
		GoodType.ARMORY,
		GoodType.PEOPLE
	};

	private static Map<String, Culture> culturesMap = new HashMap<String, Culture>(); 
	static {
		Culture[] cultures = new Culture[] { 
			new Culture("MOUNT", "Fort Ancient", true, 3, 
					composePairList("NATIVE_WARRIOR,20", "NATIVE_ARCHER,20"),
					gtvm(1.0d,1.0d,1.0d,1.0d),
					new String [] {"SIMPLE_BOW","WOODEN_MACE","BEANS","MAIZE","FISH","CLOTH","STRAWBERRIES","TOBACCO","COATS","FURS","NATIVE_ARTIFACTS"},
					1, 3, 2), 
			new Culture("MISSI", "Missisipians", true, 2, 
					composePairList("NATIVE_WARRIOR,30", "NATIVE_ARCHER,10"), 
					gtvm(1.5d,1.0d,1.0d,0.5d,1),
					new String [] {"SIMPLE_BOW","WOODEN_MACE","DRIED_MEAT","BEANS","FISH","CLOTH","STRAWBERRIES","TOBACCO","FURS","NATIVE_ARTIFACTS"},
					1, 3, 3),
			new Culture("AZTEC", "Aztec", true, 3, 
					composePairList("NATIVE_WARRIOR,20", "NATIVE_ARCHER,20", "JAGUAR_WARRIOR,10","EAGLE_WARRIOR,20"), 
					gtvm(0.5d,0.5d,1.0d,2.0d,1),
					new String [] {"SIMPLE_BOW","OBSIDIAN_SWORD","WOODEN_MACE","DRIED_MEAT","MAIZE","TOMATOES","FISH","CLOTH","COCOA","CHILI_PEPPER","COATS","FURS","GOLD_ARTIFACTS"},
					3, 2, 2),
			new Culture("HUAST", "Huastec", true, 2, 
					composePairList("NATIVE_WARRIOR,20", "NATIVE_ARCHER,10", "JAGUAR_WARRIOR,5","EAGLE_WARRIOR,15"), 
					gtvm(0.5d,1.0d,0.5d,2.0d,1),
					new String [] {"SIMPLE_BOW","OBSIDIAN_SWORD","BEANS","MAIZE","FISH","CLOTH","COCOA","FURS","GOLD_ARTIFACTS","NATIVE_ARTIFACTS"},
					2, 1, 3),
			new Culture("MIXTE", "Mixtec", true, 3, 
					composePairList("NATIVE_WARRIOR,20", "NATIVE_ARCHER,10", "EAGLE_WARRIOR,20", "QUETZAL_ARCHER,10"), 
					gtvm(1.0d,0.5d,1.0d,1.5d,1),
					new String [] {"PLUMED_BOW","SIMPLE_BOW","DRIED_MEAT","BEANS","MAIZE","TOMATOES","FISH","CLOTH","CHILI_PEPPER"},
					2, 1, 2),
			new Culture("MAYA", "Maya", true, 1, 
					composePairList("NATIVE_WARRIOR,20", "NATIVE_ARCHER,10", "EAGLE_WARRIOR,10", "QUETZAL_ARCHER,30"),
					gtvm(2.0d,2.0d,0.1d,1.0d,1),
					new String [] {"PLUMED_BOW","SIMPLE_BOW","WOODEN_MACE","DRIED_MEAT","MAIZE","FISH","CLOTH","COCOA","CHILI_PEPPER","COATS","GOLD_ARTIFACTS","NATIVE_ARTIFACTS"},
					2, 2, 3),
			new Culture("PURHE", "P'urhépecha", true, 3, 
					composePairList("NATIVE_WARRIOR,20", "NATIVE_ARCHER,10", "EAGLE_WARRIOR,20", "JAGUAR_WARRIOR,10"),
					gtvm(0.5d,0.1d,0.5d,3.0d,1),
					new String [] {"SIMPLE_BOW","OBSIDIAN_SWORD","WOODEN_MACE","BEANS","MAIZE","FISH","CLOTH","COCOA","CHILI_PEPPER","FURS","NATIVE_ARTIFACTS"},
					1, 3, 1),
			new Culture("TOTON", "Totonac", true, 2, 
					composePairList("NATIVE_WARRIOR,20", "NATIVE_ARCHER,10", "EAGLE_WARRIOR,10", "QUETZAL_ARCHER,5"),
					gtvm(1.0d,1.5d,0.5d,1.0d,1),
					new String [] {"SIMPLE_BOW","OBSIDIAN_SWORD","DRIED_MEAT","BEANS","MAIZE","FISH","CLOTH","COCOA"},
					2, 1, 1),
			new Culture("ZAPOT", "Zapotec", true, 2, 
					composePairList("NATIVE_WARRIOR,60"),
					gtvm(1.0d,1.0d,1.0d,1.0d,1),
					new String [] {"OBSIDIAN_SWORD","WOODEN_MACE","MAIZE","FISH","CLOTH","CHILI_PEPPER","COATS","NATIVE_ARTIFACTS"},
					2, 2, 2),
			new Culture("CANAR", "Cañaris", true, 3, 
					composePairList("NATIVE_WARRIOR,20", "NATIVE_ARCHER,20"), 
					gtvm(1.0d,2.0d,0.1d,1.0d,1),
					new String [] {"SIMPLE_BOW","WOODEN_MACE","LLAMA","DRIED_MEAT","BEANS","POTATOES","TOMATOES","FISH","CLOTH","COCA","COCOA","PINEAPPLE","FURS"},
					1, 3, 1),
			new Culture("CHACH", "Chachapoya", true, 1, 
					composePairList("NATIVE_WARRIOR,40"),
					gtvm(1.0d,1.5d,1.0d,0.5d,1),
					new String [] {"WOODEN_MACE","LLAMA","BEANS","MAIZE","POTATOES","TOMATOES","CLOTH","COCOA","CHILI_PEPPER","PINEAPPLE","FURS","NATIVE_ARTIFACTS"},
					2, 2, 3),
			new Culture("CHIMU", "Chimú", true, 1, 
					composePairList("NATIVE_WARRIOR,20", "NATIVE_ARCHER,10"),
					gtvm(1.0d,1.0d,1.0d,1.0d,1),
					new String [] {"SIMPLE_BOW","WOODEN_MACE","LLAMA","DRIED_MEAT","POTATOES","TOMATOES","FISH","CLOTH","COCA","PINEAPPLE","NATIVE_ARTIFACTS"},
					1, 2, 3),
			new Culture("INCA", "Inca", true, 3, 
					composePairList("NATIVE_WARRIOR,40", "NATIVE_ARCHER,20"), 
					gtvm(0.1d,2.0d,1.0d,1.0d,1),
					new String [] {"SIMPLE_BOW","OBSIDIAN_SWORD","WOODEN_MACE","LLAMA","BEANS","MAIZE","POTATOES","TOMATOES","FISH","CLOTH","COCA","STRAWBERRIES","COATS","GOLD_ARTIFACTS","NATIVE_ARTIFACTS"},
					3, 1, 3),
			new Culture("MUISC", "Muisca", true, 2, 
					composePairList("NATIVE_WARRIOR,20", "NATIVE_ARCHER,20"),
					gtvm(1.0d,1.0d,1.0d,1.0d,1),
					new String [] {"SIMPLE_BOW","OBSIDIAN_SWORD","WOODEN_MACE","LLAMA","DRIED_MEAT","BEANS","POTATOES","TOMATOES","FISH","CLOTH","COCA","CHILI_PEPPER","PINEAPPLE","FURS","NATIVE_ARTIFACTS"},
					1, 3, 3),
			new Culture("TAIRO", "Tairona", true, 3, 
					composePairList("NATIVE_WARRIOR,40", "NATIVE_ARCHER,20"), 
					gtvm(1.0d,1.0d,1.0d,1.0d,1),
					new String [] {"SIMPLE_BOW","WOODEN_MACE","DRIED_MEAT","MAIZE","TOMATOES","FISH","CLOTH","COCA","PINEAPPLE","FURS","GOLD_ARTIFACTS","NATIVE_ARTIFACTS"},
					1, 3, 3),
			
			new Culture("ARTIC", "Artic Mammal Hunters", false, 1, 
					composePairList(), 
					gtvm(2.0d,1.0d,0.5d,0.5d,1),
					new String [] {"DRIED_MEAT","CLOTH","COATS","FURS"},
					0, 0, 2),
			new Culture("HUNTE", "Hunters-Gatherers", false, 2, 
					composePairList( "NATIVE_ARCHER,10"),
					gtvm(1.0d,0.1d,2.0d,1.0d,1),
					new String [] {"SIMPLE_BOW","DRIED_MEAT","CLOTH","FURS","NATIVE_ARTIFACTS"},
					0, 1, 2),
			new Culture("FISHI", "Fishing people", false, 3, 
					composePairList("NATIVE_WARRIOR,40"),
					gtvm(3.0d,0.1d,1.0d,0.1d,1),
					new String [] {"BEANS","FISH","NATIVE_ARTIFACTS"},
					0, 1, 1),
			new Culture("BISON", "Bison Hunters", false, 3, 
					composePairList("NATIVE_WARRIOR,40", "NATIVE_ARCHER,40"), 
					gtvm(2.0d,0.1d,0.1d,3.0d,1),
					new String [] {"SIMPLE_BOW","WOODEN_MACE","MAIZE","FURS"},
					0, 0, 3),
			new Culture("FARME", "Maiz Farmers", false, 2, 
					composePairList("NATIVE_ARCHER,30"),
					gtvm(0.5d,0.1d,3.0d,1.0d,1),
					new String [] {"SIMPLE_BOW","WOODEN_MACE","BEANS","MAIZE","POTATOES","NATIVE_ARTIFACTS"},
					0, 1, 3)
		};
		for (Culture culture: cultures){
			culturesMap.put(culture.getCode(), culture);
		}
	}

	public static Culture getCulture(String string) {
		return culturesMap.get(string);
	}

	private static List<Pair<GoodType, Double>> gtvm(double... d) {
		List<Pair<GoodType, Double>> ret = new ArrayList<Pair<GoodType,Double>>();
		int i = 0;
		for (Double value: d){
			ret.add(new Pair<GoodType, Double>(GOOD_TYPES[i], value));
			i++;
		}
		return ret;
	}

	private static List<Pair<Double, String>> composePairList(String... pairs) {
		List<Pair<Double,String>> ret = new ArrayList<Pair<Double,String>>();
		for (String pairString: pairs){
			String[] splitPair = pairString.split(",");
			ret.add(new Pair<Double, String>(Double.parseDouble(splitPair[1])/100.0d, splitPair[0]));
		}
		return ret;
	}

	public static Building[] getBuildings() {
		return buildings;
	}
	
	public static NPC[] getNPCs(){
		return new NPC[]{
				new NPC((ExpeditionUnit)ItemFactory.createItem("DOMINIK"), true),
				new NPC((ExpeditionUnit)ItemFactory.createItem("BIZCOCHO"), false, "WOOF-WOOF!", "GRRRR!!!"),
				new NPC((ExpeditionUnit)ItemFactory.createItem("CRISTOFORO"), false, "The Cave is gushing...", "I play my lute and receive the good wishes of the people.", "The song goes: Ho eyo he hum!"),
				new NPC((ExpeditionUnit)ItemFactory.createItem("SANTIAGO"), false, "The World is changing!", "All things have a true nature!"),
				new NPC((ExpeditionUnit)ItemFactory.createItem("SOLDIER"), false, "The moors are gone. Long live the King and Queen!"),
				new NPC((ExpeditionUnit)ItemFactory.createItem("MARINE"), false, "Our fleet is strong"),
				new NPC((ExpeditionUnit)ItemFactory.createItem("ARCHER"), false, "Gunpowder weapons will never replace the keen archer", "May I go with you? You have my bow."),
				new NPC((ExpeditionUnit)ItemFactory.createItem("GUARD"), true),
				new NPC((ExpeditionUnit)ItemFactory.createItem("COLONIST"), false, "Many a grave has been unjustly filled in the name of justice.", "I will not speak of my sin!"),
			};
	}
}
