package net.slashie.expedition.data;

import net.slashie.expedition.domain.Armor;
import net.slashie.expedition.domain.ExpeditionItem;
import net.slashie.expedition.domain.ExpeditionUnit;
import net.slashie.expedition.domain.Food;
import net.slashie.expedition.domain.Good;
import net.slashie.expedition.domain.GoodType;
import net.slashie.expedition.domain.Store;
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
		goodsStore.addItem(ItemFactory.createItem("FOOD"), 15300, 5);
		goodsStore.addItem(ItemFactory.createItem("RUM"), 160, 20);
		goodsStore.addItem(ItemFactory.createItem("WOOD"), 550, 20);
		goodsStore.addItem(ItemFactory.createItem("FRESHWATER"), 165, 2);
		goodsStore.addItem(ItemFactory.createItem("FOOD_SAUERKRAUT"), 3200, 10);
		
		//Weapons Store
		Store weaponsStore = new Store();
		weaponsStore.setOwnerName("Weapons Store");
		weaponsStore.addItem(ItemFactory.createItem("SPEARS"), 550, 5);
		weaponsStore.addItem(ItemFactory.createItem("SWORDS"), 130, 20);
		weaponsStore.addItem(ItemFactory.createItem("BOWS"), 200, 30);
		weaponsStore.addItem(ItemFactory.createItem("XBOWS"), 80, 50);
		weaponsStore.addItem(ItemFactory.createItem("GUNS"), 12, 60);
		weaponsStore.addItem(ItemFactory.createItem("ARROWS"), 1500, 2);
		weaponsStore.addItem(ItemFactory.createItem("STUDDED_LEATHER"), 160, 30);
		weaponsStore.addItem(ItemFactory.createItem("PLATE"), 30, 50);
		
		//Port
		Store port = new Store();
		port.setOwnerName("Port");
		port.addItem(ItemFactory.createItem("CARRACK"), 3, 12000);
		port.addItem(ItemFactory.createItem("CARAVEL"), 4, 14000);
		port.addItem(ItemFactory.createItem("CAPTAIN"), 6, 200);
		
		//Pub
		Store pub = new Store();
		pub.setOwnerName("Pub");
		pub.addItem(ItemFactory.createItem("SAILOR"), 30, 130);
		pub.addItem(ItemFactory.createItem("ROGUE"), 350, 12);
		
		//Guild
		Store guild = new Store();
		guild.setOwnerName("Guild");
		guild.addItem(ItemFactory.createItem("MARINE"), 66, 30);
		guild.addItem(ItemFactory.createItem("SOLDIER"), 130, 20);
		guild.addItem(ItemFactory.createItem("ARCHER"), 85, 50);
		
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
			//Units
			new ExpeditionUnit("SAILOR", "Sailor", "Sailors", 1,50,1,1,1,1,1,1,1,1,"",
				new String[]{"SPEARS"},
				new String[]{""}),
			new ExpeditionUnit("ROGUE",  "Rogue",  "Rogues",  1,1,50,1,1,1,1,1,1,1,"",
					new String[]{"BOWS", "SPEARS"},
					new String[]{""}),
			new ExpeditionUnit("MARINE", "Marine", "Marines", 1,1,30,1,1,1,1,1,1,1,"",
					new String[]{"XBOWS","BOWS","SWORDS","SPEARS"},
					new String[]{"STUDDED_LEATHER"}),
			new ExpeditionUnit("SOLDIER", "Soldier","Soldiers",1,1,30,1,1,1,1,1,1,1,"",
					new String[]{"GUNS", "SWORDS", "SPEARS"},
					new String[]{"STUDDED_LEATHER", "PLATE"}),
			new ExpeditionUnit("ARCHER", "Archer","Archers",1,1,1,8,1,1,1,1,1,1,"",
					new String[]{"XBOWS", "BOWS", "SPEARS"},
					new String[]{"STUDDED_LEATHER"}),
			new ExpeditionUnit("CAPTAIN", "Captain","Captains", 1,1,20,1,1,1,1,1,1,1,"",
					new String[]{"GUNS", "SWORDS", "SPEARS" },
					new String[]{"STUDDED_LEATHER", "PLATE"}),
			new ExpeditionUnit("EXPLORER", "Explorer","Explorers",1,1,60,1,1,1,1,1,1,1,"",
					new String[]{"SPEARS"},
					new String[]{"STUDDED_LEATHER"}),
		
			//Native Units
			new ExpeditionUnit("NATIVE_WARRIOR", "Warrior","Warriors", 1,1,1,1,1,1,1,1,1,0,"",
					new String[]{""},
					new String[]{""}),
			new ExpeditionUnit("NATIVE_ARCHER", "Archer","Archers",1,1,1,5,1,1,1,1,1,0,"",
					new String[]{""},
					new String[]{""}),
			
			//Goods
			new Food("FOOD", "Food", "Food", 1, 10),
			new Food("RUM", "Rum", "Rum", 1, 2),
			new Good("WOOD", "Wood", "Wood", 1, GoodType.TOOL),
			new Food("FRESHWATER", "Freshwater", "Freshwater", 1,1),
			new Food("FOOD_SAUERKRAUT", "Sauerkraut","Sauerkraut", 1, 8),
			
			//New Worlds Goods
			new Valuable("GOLD_NUGGET", "Gold Nugget", "Gold Nuggets", 1, 10),
			new Valuable("GOLD_BRACELET", "Gold Bracelet","Gold Bracelets",  1, 20),
			new Valuable("NATIVE_ARTIFACT", "Pottery", "Pottery", 5, 5),
			new Food("NATIVE_FOOD", "Stash of Maíz", "Stashes of Maíz", 1, 8),
			
			//Weapons
			new Weapon("SPEARS", "Spear","Spears", 1, 1, 2, 1),
			new Weapon("SWORDS", "Sword", "Swords", 1, 1, 3, 0),
			new Weapon("BOWS", "Bow", "Bows", 1, 1, 2, 0),
			new Weapon("XBOWS", "Crossbow", "Crossbows", 1, 2, 3, 0),
			new Weapon("GUNS", "Harquebus", "Harquebuses", 1, 3, 5, 0),
			new Armor("STUDDED_LEATHER", "Studded Leather", "Studded Leather", 2, 1, 1),
			new Armor("PLATE", "Plate","Plate", 4, 4, 3),
			
			new Good("ARROWS", "Arrow", "Arrows", 1, GoodType.WEAPON),
			
			//Ships
			new Vehicle("CARRACK","Carrack","Carracks",1,true,false,3,2500),
			new Vehicle("CARAVEL","Caravel","Caravels", 1,true,false,4,2000),
		};
		
	}
	
	public static ExpeditionUnit[] getUnitDefinitions(){
		return new ExpeditionUnit[]{
				
		};
	}
}
