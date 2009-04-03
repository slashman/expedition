package net.slashie.expedition.data;

import net.slashie.expedition.domain.Armor;
import net.slashie.expedition.domain.ExpeditionItem;
import net.slashie.expedition.domain.ExpeditionUnit;
import net.slashie.expedition.domain.Food;
import net.slashie.expedition.domain.Good;
import net.slashie.expedition.domain.GoodType;
import net.slashie.expedition.domain.Store;
import net.slashie.expedition.domain.StoreItemInfo;
import net.slashie.expedition.domain.Valuable;
import net.slashie.expedition.domain.Vehicle;
import net.slashie.expedition.domain.Weapon;
import net.slashie.expedition.item.ItemFactory;
import net.slashie.expedition.world.ExpeditionCell;
import net.slashie.expedition.world.ExpeditionFeature;
import net.slashie.expedition.world.OverworldExpeditionCell;
import net.slashie.libjcsi.ConsoleSystemInterface;
import net.slashie.serf.level.AbstractCell;
import net.slashie.serf.ui.AppearanceFactory;
import net.slashie.serf.ui.consoleUI.CharAppearance;

public class ExpeditionDAO {
	
	public static AbstractCell[] getCellDefinitions (AppearanceFactory appFactory){
		Store goodsStore = new Store();
		goodsStore.setOwnerName("Goods Store");
		goodsStore.addItem(15300, new StoreItemInfo("FOOD", 1, 10, "packs"));
		goodsStore.addItem(160, new StoreItemInfo("RUM", 20, 5, "barrels"));
		goodsStore.addItem(550, new StoreItemInfo("WOOD", 550));
		goodsStore.addItem(165, new StoreItemInfo("FRESHWATER", 165, 2, "barrels"));
		goodsStore.addItem(3200, new StoreItemInfo("FOOD_SAUERKRAUT", 3, 10, "barrels"));
		
		//Weapons Store
		Store weaponsStore = new Store();
		weaponsStore.setOwnerName("Weapons Store");
		weaponsStore.addItem(550, new StoreItemInfo("SPEARS", 5));
		weaponsStore.addItem(130, new StoreItemInfo("SWORDS", 10));
		weaponsStore.addItem(200, new StoreItemInfo("BOWS", 6));
		weaponsStore.addItem(180, new StoreItemInfo("XBOWS", 25));
		weaponsStore.addItem(100, new StoreItemInfo("GUNS", 30));
		weaponsStore.addItem(3200, new StoreItemInfo("ARROWS", 2));
		weaponsStore.addItem(160, new StoreItemInfo("STUDDED_LEATHER",20));
		weaponsStore.addItem(80, new StoreItemInfo("PLATE", 50));
		
		//Port
		Store port = new Store();
		port.setOwnerName("Port");
		port.addItem(10, new StoreItemInfo("CARRACK", 4000));
		port.addItem(15, new StoreItemInfo("CARAVEL", 3000));
		
		port.addItem(12, new StoreItemInfo("CAPTAIN", 300));
		
		//Pub
		Store pub = new Store();
		pub.setOwnerName("Pub");
		pub.addItem(100, new StoreItemInfo("SAILOR", 60));
		pub.addItem(350, new StoreItemInfo("ROGUE", 30));
		
		//Guild
		Store guild = new Store();
		guild.setOwnerName("Guild");
		guild.addItem(70, new StoreItemInfo("MARINE", 70));
		guild.addItem(130, new StoreItemInfo("SOLDIER", 100));
		guild.addItem(85, new StoreItemInfo("ARCHER", 120));
		
		return new AbstractCell[]{
			//Overworld cells
			new OverworldExpeditionCell("GRASS", "Grass", true, false, 3),
			new OverworldExpeditionCell("PLAINS", "Grass", true, false, 3),
			new OverworldExpeditionCell("WATER", "Ocean", false, false, 5),
			new OverworldExpeditionCell("WATER2", "Water", false, false, 5),
			new OverworldExpeditionCell("MOUNTAIN", "Mountain", true, true, 1),
			new OverworldExpeditionCell("FOREST", "Forest", true, false, 2),
			new OverworldExpeditionCell("PORT_CITY", "Port City", false, false, 1),
			
			//Inworld Cells
			new ExpeditionCell("GOODS_STORE", "Goods Store", goodsStore),
			new ExpeditionCell("WEAPONS_STORE", "Weapons Store", weaponsStore),
			new ExpeditionCell("PORT", "Port", port),
			new ExpeditionCell("PUB", "Pub", pub),
			new ExpeditionCell("GUILD", "Guild", guild),
			
			new ExpeditionCell("SPAIN_GRASS", "Grass"),
			new ExpeditionCell("SPAIN_FLOOR", "Stone Floor"),
			new ExpeditionCell("SPAIN_DOCKS", "Docks"),
			new ExpeditionCell("SPAIN_WATER", "Ocean", false, false, true),
			new ExpeditionCell("SPAIN_WALL", "Stone Wall", true, false),
			new ExpeditionCell("SPAIN_HOUSE", "Houses", true, true),
			new ExpeditionCell("SPAIN_SHIP", "Ships", false, false, true),
					
			new ExpeditionCell("DEPARTURE", "Docks", "DEPARTURE")
		};
	}
	
	public static CharAppearance[] getCharAppearances(){
		return new CharAppearance[]{
			//Expeditions
			new CharAppearance("EXPEDITION", '@', ConsoleSystemInterface.YELLOW),
			new CharAppearance("SHIP_EXPEDITION", 'v', ConsoleSystemInterface.RED),
			
			//Non principal Expeditions
			new CharAppearance("HOSTILE_EXPEDITION", '@', ConsoleSystemInterface.RED),
			
			//Overworld Terrain
			new CharAppearance("GRASS", '.', ConsoleSystemInterface.GREEN),
			new CharAppearance("PLAINS", '.', ConsoleSystemInterface.BROWN),
			new CharAppearance("WATER", '~', ConsoleSystemInterface.DARK_BLUE),
			new CharAppearance("WATER2", '~', ConsoleSystemInterface.BLUE),
			new CharAppearance("MOUNTAIN", '^', ConsoleSystemInterface.GREEN),
			new CharAppearance("FOREST", '&', ConsoleSystemInterface.GREEN),
			new CharAppearance("PORT_CITY", '#', ConsoleSystemInterface.BROWN),
			

			
			//Overworld Features
			new CharAppearance("SHIP", 'v', ConsoleSystemInterface.RED),
			new CharAppearance("GOODS_CACHE", '*', ConsoleSystemInterface.RED),
			new CharAppearance("TOWN", '^', ConsoleSystemInterface.RED),

			
			//Inworld terrain
			new CharAppearance("SPAIN_WALL", '#', ConsoleSystemInterface.BROWN),
			new CharAppearance("DEPARTURE", '#', ConsoleSystemInterface.BROWN),
			new CharAppearance("CITY_GRASS", '.', ConsoleSystemInterface.BROWN),
			new CharAppearance("CITY_SEA", '~', ConsoleSystemInterface.BROWN),
			
			
			new CharAppearance("SPAIN_GRASS", '.', ConsoleSystemInterface.GREEN),
			new CharAppearance("SPAIN_FLOOR", '.', ConsoleSystemInterface.LIGHT_GRAY),
			new CharAppearance("SPAIN_WATER", '~', ConsoleSystemInterface.DARK_BLUE),
			new CharAppearance("SPAIN_WALL", '#', ConsoleSystemInterface.GRAY),
			new CharAppearance("SPAIN_HOUSE", '#', ConsoleSystemInterface.TEAL),
			new CharAppearance("SPAIN_SHIP", 'v', ConsoleSystemInterface.RED),
			new CharAppearance("SPAIN_DOCKS", '=', ConsoleSystemInterface.BROWN),
		
			
			//Units
			new CharAppearance("SAILOR", '@', ConsoleSystemInterface.BLUE),
			new CharAppearance("ROGUE", '@', ConsoleSystemInterface.BROWN),
			new CharAppearance("MARINE", '@', ConsoleSystemInterface.TEAL),
			new CharAppearance("SOLDIER", '@', ConsoleSystemInterface.GREEN),
			new CharAppearance("ARCHER", '@', ConsoleSystemInterface.DARK_BLUE),			
			new CharAppearance("CAPTAIN", '@', ConsoleSystemInterface.CYAN),
			new CharAppearance("EXPLORER", '@', ConsoleSystemInterface.RED),

			//native Units
			new CharAppearance("NATIVE_WARRIOR", '@', ConsoleSystemInterface.RED),
			new CharAppearance("NATIVE_ARCHER", '@', ConsoleSystemInterface.DARK_RED),
			
			//Stores
			new CharAppearance("GOODS_STORE", '1', ConsoleSystemInterface.RED),
			new CharAppearance("WEAPONS_STORE", '2', ConsoleSystemInterface.RED),
			new CharAppearance("PORT", '3', ConsoleSystemInterface.RED),
			new CharAppearance("PUB", '4', ConsoleSystemInterface.RED),
			new CharAppearance("GUILD", '5', ConsoleSystemInterface.RED),
			
			
			//Goods
			new CharAppearance("FOOD", '%', ConsoleSystemInterface.BROWN),
			new CharAppearance("RUM", '%', ConsoleSystemInterface.BROWN),
			new CharAppearance("WOOD", '=', ConsoleSystemInterface.BROWN),
			new CharAppearance("FRESHWATER", '%', ConsoleSystemInterface.CYAN),
			new CharAppearance("FOOD_SAUERKRAUT", '%', ConsoleSystemInterface.GREEN),
			
			new CharAppearance("GOLD_NUGGET", '*', ConsoleSystemInterface.YELLOW),
			new CharAppearance("GOLD_BRACELET", '}', ConsoleSystemInterface.YELLOW),
			new CharAppearance("DEAD_NATIVE", '@', ConsoleSystemInterface.GRAY),
			new CharAppearance("NATIVE_ARTIFACT", 'p', ConsoleSystemInterface.BROWN),
			new CharAppearance("NATIVE_FOOD", '%', ConsoleSystemInterface.GREEN),

			
			//Weapons
			new CharAppearance("SPEARS", '/', ConsoleSystemInterface.BROWN),
			new CharAppearance("SWORDS", '\\', ConsoleSystemInterface.LIGHT_GRAY),
			new CharAppearance("BOWS", ')', ConsoleSystemInterface.BROWN),
			new CharAppearance("ARROWS", '/', ConsoleSystemInterface.GRAY),
			new CharAppearance("XBOWS", '}', ConsoleSystemInterface.BROWN),
			new CharAppearance("GUNS", '>', ConsoleSystemInterface.LIGHT_GRAY),
			new CharAppearance("STUDDED_LEATHER", ']', ConsoleSystemInterface.BROWN),
			new CharAppearance("PLATE", ']', ConsoleSystemInterface.LIGHT_GRAY),
			
			//Ships
			new CharAppearance("CARRACK", 'V', ConsoleSystemInterface.BROWN),
			new CharAppearance("CARAVEL", 'w', ConsoleSystemInterface.BROWN),
			
			
		};
	};
	
	public static ExpeditionFeature[] getFeatureDefinitions(AppearanceFactory appFactory){
		return new ExpeditionFeature[0];
	}
	
	public static ExpeditionItem[] getItemDefinitions(AppearanceFactory appFactory){
		return new ExpeditionItem[]{
				//weight, carryCapacity, attack, defense, dailyFoodConsumption
			//Units
			new ExpeditionUnit("SAILOR", "Sailor", "Sailors", 200, 20, 1, 1, 1, 
				new String[]{"SPEARS"},
				new String[]{""}),
			new ExpeditionUnit("ROGUE",  "Rogue",  "Rogues",  250, 25, 2, 2, 1,
					new String[]{"BOWS", "SPEARS"},
					new String[]{""}),
			new ExpeditionUnit("MARINE", "Marine", "Marines", 300, 25, 3, 2, 2,
					new String[]{"XBOWS","BOWS","SWORDS","SPEARS"},
					new String[]{"STUDDED_LEATHER"}),
			new ExpeditionUnit("SOLDIER", "Soldier","Soldiers", 300, 20, 3, 3, 2,
					new String[]{"GUNS", "SWORDS", "SPEARS"},
					new String[]{"STUDDED_LEATHER", "PLATE"}),
			new ExpeditionUnit("ARCHER", "Archer","Archers", 250, 20, 2, 2, 2,
					new String[]{"XBOWS", "BOWS", "SPEARS"},
					new String[]{"STUDDED_LEATHER"}),
			new ExpeditionUnit("CAPTAIN", "Captain","Captains", 300, 20, 1, 1, 3,
					new String[]{"GUNS", "SWORDS", "SPEARS" },
					new String[]{"STUDDED_LEATHER", "PLATE"}),
			new ExpeditionUnit("EXPLORER", "Explorer","Explorers", 300, 40, 2, 1, 3,
					new String[]{"SPEARS"},
					new String[]{"STUDDED_LEATHER"}),
		
			//Native Units
			new ExpeditionUnit("NATIVE_WARRIOR", "Warrior","Warriors", 200, 20, 2, 1, 0,
					new String[]{""},
					new String[]{""}),
			new ExpeditionUnit("NATIVE_ARCHER", "Archer","Archers", 200, 20, 2, 1, 0,
					new String[]{""},
					new String[]{""}),
			
			//Goods
			new Food("FOOD", "Food", "Food", 1, 1),
			new Food("RUM", "Rum", "Rum", 5, 2),
			new Good("WOOD", "Wood", "Wood", 10, GoodType.TOOL),
			new Food("FRESHWATER", "Freshwater", "Freshwater", 5,1),
			new Food("FOOD_SAUERKRAUT", "Sauerkraut","Sauerkraut", 3, 1),
			
			//New Worlds Goods
			new Valuable("GOLD_NUGGET", "Gold Nugget", "Gold Nuggets", 50, 15),
			new Valuable("GOLD_BRACELET", "Gold Bracelet","Gold Bracelets",  50, 7),
			new Valuable("NATIVE_ARTIFACT", "Pottery", "Pottery", 70, 6),
			new Food("NATIVE_FOOD", "Stash of Maíz", "Stashes of Maíz", 5, 1),

			//Weapons
			new Weapon("SPEARS", "Spear","Spears", 1, 2, 1, 30),
			new Weapon("SWORDS", "Sword", "Swords", 3, 1, 1, 50),
			new Weapon("BOWS", "Bow", "Bows", 2, 0, 5, 30),
			new Weapon("XBOWS", "Crossbow", "Crossbows", 4, 0, 4, 70),
			new Weapon("GUNS", "Harquebus", "Harquebuses", 6, 0, 2, 60),
			new Armor("PLATE", "Plate","Plate", 40, 4, 3, "PL"),
			new Armor("STUDDED_LEATHER", "Studded Leather", "Studded Leather", 20, 1, 1, "LE"),
			
			new Good("ARROWS", "Arrow", "Arrows", 5, GoodType.WEAPON),
			
			//Ships
			new Vehicle("CARRACK","Carrack","Carracks",1,true,false,3,25000),
			new Vehicle("CARAVEL","Caravel","Caravels", 1,true,false,4,20000),
		};
		
	}
	
	public static ExpeditionUnit[] getUnitDefinitions(){
		return new ExpeditionUnit[]{
				
		};
	}
}
