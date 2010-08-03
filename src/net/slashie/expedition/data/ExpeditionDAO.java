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
		goodsStore.addItem(80000, new StoreItemInfo("FOOD", 1, 10, "packs"));
		goodsStore.addItem(500, new StoreItemInfo("RUM", 20, 5, "barrels"));
		goodsStore.addItem(10000, new StoreItemInfo("WOOD", 5));
		goodsStore.addItem(10000, new StoreItemInfo("FRESHWATER", 165, 2, "barrels"));
		goodsStore.addItem(20000, new StoreItemInfo("FOOD_SAUERKRAUT", 3, 10, "barrels"));
		goodsStore.addItem(1000, new StoreItemInfo("CARPENTER", 60));

		
		//Weapons Store
		Store weaponsStore = new Store();
		weaponsStore.setOwnerName("Armory");
		weaponsStore.addItem(5000, new StoreItemInfo("SPEARS", 5));
		weaponsStore.addItem(1000, new StoreItemInfo("SWORDS", 10));
		weaponsStore.addItem(2000, new StoreItemInfo("BOWS", 6));
		weaponsStore.addItem(1500, new StoreItemInfo("XBOWS", 25));
		weaponsStore.addItem(1000, new StoreItemInfo("GUNS", 30));
		weaponsStore.addItem(30000, new StoreItemInfo("ARROWS", 2));
		weaponsStore.addItem(2000, new StoreItemInfo("STUDDED_LEATHER",20));
		weaponsStore.addItem(1000, new StoreItemInfo("PLATE", 50));
		
		//Port
		Store port = new Store();
		port.setOwnerName("Harbor");
		port.addItem(30, new StoreItemInfo("CARRACK", 8000));
		port.addItem(20, new StoreItemInfo("CARAVEL", 6000));
		
		port.addItem(50, new StoreItemInfo("CAPTAIN", 300));
		
		//Pub
		Store pub = new Store();
		pub.setOwnerName("Pub");
		pub.addItem(10000, new StoreItemInfo("SAILOR", 60));
		pub.addItem(35000, new StoreItemInfo("ROGUE", 30));
		pub.addItem(50000, new StoreItemInfo("COLONIST", 10));

		
		//Guild
		Store guild = new Store();
		guild.setOwnerName("Guild");
		guild.addItem(7000, new StoreItemInfo("MARINE", 70));
		guild.addItem(5000, new StoreItemInfo("SOLDIER", 100));
		guild.addItem(1500, new StoreItemInfo("ARCHER", 120));
		
		return new AbstractCell[]{
			//Overworld cells
			new OverworldExpeditionCell("GRASS", "Grass", true, false, false, 1.2d),
			new OverworldExpeditionCell("PLAINS", "Grass", true, false, false, 1.0d),
			new OverworldExpeditionCell("WATER", "Ocean", false, false, false, 1.0d),
			new OverworldExpeditionCell("WATER2", "Water", true, false, true, 1.0d),
			new OverworldExpeditionCell("MOUNTAIN", "Mountain", true, true, false, 1.5d),
			new OverworldExpeditionCell("FOREST", "Forest", true, false, false, 1.2d),
			new OverworldExpeditionCell("PORT_CITY", "Port City", false, false, false, 1.0d),
			
			//Inworld Cells
			new ExpeditionCell("GOODS_STORE", "Goods Store", goodsStore),
			new ExpeditionCell("WEAPONS_STORE", "Weapons Store", weaponsStore),
			new ExpeditionCell("PORT", "Harbor", port),
			new ExpeditionCell("PUB", "Pub", pub),
			new ExpeditionCell("GUILD", "Guild", guild),
			
			new ExpeditionCell("SPAIN_GRASS", "Grass"),
			new ExpeditionCell("SPAIN_FLOOR", "Stone Floor"),
			new ExpeditionCell("SPAIN_DOCKS", "Docks"),
			new ExpeditionCell("SPAIN_WATER", "Ocean", false, false, true),
			new ExpeditionCell("SPAIN_WALL", "Stone Wall", true, true),
			new ExpeditionCell("SPAIN_HOUSE", "Houses", true, true),
			new ExpeditionCell("SPAIN_SHIP", "Ships", false, false, true),
			new ExpeditionCell("SPAIN_COLUMN", "Column", true, true),
			new ExpeditionCell("SPAIN_CASTLE", "Spain Castle", true, true),
					
			new ExpeditionCell("DEPARTURE", "Docks", "DEPARTURE"),
			
			
			new ExpeditionCell("CASTLE_GATE", "Castle Gate", "TRAVEL_CASTLE"),
			
			new ExpeditionCell("CASTLE_FLOOR", "Castle Floor"),
			new ExpeditionCell("BLUE_CARPET", "Carpet"),
			new ExpeditionCell("RED_CARPET", "Carpet"),
			new ExpeditionCell("CASTLE_WALL", "Castle Wall", true, true),
			new ExpeditionCell("SPAIN_BANNER", "Banner of Spain"),
			new ExpeditionCell("CASTLE_WINDOW", "Castle Window", true, true),
			new ExpeditionCell("THRONE", "Throne of Spain", true, true),
			new ExpeditionCell("KING_FERDINAND", "Ferdinand II, King of Aragon", true, true),
			new ExpeditionCell("QUEEN_ISABELLE", "Isabella, Queen of Castile and León", true, true),
			
			new ExpeditionCell("SPAIN_CREST", "The Spain Coat of Arms", true, true),
			new ExpeditionCell("CASTLE_CURTAIN", "Curtain"),
			
		};
	}
	
	public static CharAppearance[] getCharAppearances(){
		return new CharAppearance[]{
			//Expeditions
			new CharAppearance("EXPEDITION", '@', ConsoleSystemInterface.RED),
			new CharAppearance("SHIP_EXPEDITION", 'v', ConsoleSystemInterface.RED),
			
			//Non principal Expeditions
			new CharAppearance("HOSTILE_EXPEDITION", '@', ConsoleSystemInterface.BLUE),
			
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
			new CharAppearance("SPAIN_COLUMN", 'o', ConsoleSystemInterface.BROWN),
			new CharAppearance("SPAIN_CASTLE", '#', ConsoleSystemInterface.BROWN),
			
			// Castle
			new CharAppearance("CASTLE_FLOOR", '.', ConsoleSystemInterface.BROWN),
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
			
			//Units
			new CharAppearance("SAILOR", '@', ConsoleSystemInterface.BLUE),
			new CharAppearance("ROGUE", '@', ConsoleSystemInterface.BROWN),
			new CharAppearance("MARINE", '@', ConsoleSystemInterface.TEAL),
			new CharAppearance("SOLDIER", '@', ConsoleSystemInterface.GREEN),
			new CharAppearance("ARCHER", '@', ConsoleSystemInterface.DARK_BLUE),			
			new CharAppearance("CAPTAIN", '@', ConsoleSystemInterface.CYAN),
			new CharAppearance("EXPLORER", '@', ConsoleSystemInterface.RED),
			new CharAppearance("CARPENTER", '@', ConsoleSystemInterface.DARK_RED),
			new CharAppearance("COLONIST", '@', ConsoleSystemInterface.YELLOW),
		
			//native Units
			new CharAppearance("NATIVE_WARRIOR", '@', ConsoleSystemInterface.RED),
			new CharAppearance("NATIVE_BRAVE", '@', ConsoleSystemInterface.PURPLE),
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
			new ExpeditionUnit("SAILOR", "Sailor", "Sailors", 200, 200, 1, 1, 1, 
				new String[]{"SPEARS"},
				new String[]{""}),
			new ExpeditionUnit("ROGUE",  "Rogue",  "Rogues",  250, 250, 2, 2, 1,
					new String[]{"BOWS", "SPEARS"},
					new String[]{""}),
			new ExpeditionUnit("MARINE", "Marine", "Marines", 300, 250, 3, 2, 2,
					new String[]{"XBOWS","BOWS","SWORDS","SPEARS"},
					new String[]{"STUDDED_LEATHER"}),
			new ExpeditionUnit("SOLDIER", "Soldier","Soldiers", 300, 200, 3, 3, 2,
					new String[]{"GUNS", "SWORDS", "SPEARS"},
					new String[]{"STUDDED_LEATHER", "PLATE"}),
			new ExpeditionUnit("ARCHER", "Archer","Archers", 250, 200, 2, 2, 2,
					new String[]{"XBOWS", "BOWS", "SPEARS"},
					new String[]{"STUDDED_LEATHER"}),
			new ExpeditionUnit("CAPTAIN", "Captain","Captains", 300, 200, 1, 1, 3,
					new String[]{"GUNS", "SWORDS", "SPEARS" },
					new String[]{"STUDDED_LEATHER", "PLATE"}),
			new ExpeditionUnit("EXPLORER", "Explorer","Explorers", 300, 400, 2, 1, 3,
					new String[]{"SPEARS"},
					new String[]{"STUDDED_LEATHER"}),
			new ExpeditionUnit("CARPENTER", "Carpenter","Carpenters", 250, 300, 1, 1, 1, 
					new String[]{"SPEARS"},
					new String[]{""}),
			new ExpeditionUnit("COLONIST",  "Colonist",  "Colonists",  200, 300, 1, 1, 1,
					new String[]{"SPEARS"},
					new String[]{""}),
			//Native Units
			new ExpeditionUnit("NATIVE_WARRIOR", "Warrior","Warriors", 200, 200, 2, 1, 0,
					new String[]{""},
					new String[]{""}),
			new ExpeditionUnit("NATIVE_BRAVE", "Brave","Braves", 200, 200, 4, 2, 0,
					new String[]{""},
					new String[]{""}),
			new ExpeditionUnit("NATIVE_ARCHER", "Archer","Archers", 200, 200, 2, 1, 0,
					new String[]{""},
					new String[]{""}),
			
			//Goods
			new Food("FOOD", "Food", "Food", 1, 1),
			new Food("RUM", "Rum", "Rum", 5, 2),
			new Good("WOOD", "Wood", "Wood", 10, GoodType.TOOL),
			new Food("FRESHWATER", "Freshwater", "Freshwater", 5,1),
			new Food("FOOD_SAUERKRAUT", "Sauerkraut","Sauerkraut", 3, 1),
			
			//New Worlds Goods
			new Valuable("GOLD_NUGGET", "Gold Nugget", "Gold Nuggets", 50, 45),
			new Valuable("GOLD_BRACELET", "Gold Bracelet","Gold Bracelets",  50, 25),
			new Valuable("NATIVE_ARTIFACT", "Pottery", "Pottery", 70, 20),
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
			new Vehicle("CARRACK","Carrack","Carracks",1,true,false,3,25000, 10, false),
			new Vehicle("CARAVEL","Caravel","Caravels", 1,true,false,4,20000, 15, false),
			
			//Special
			new ExpeditionUnit("KING_FERDINAND", "Ferdinand II, King of Aragón","Kings", 250, 300, 1, 1, 1, new String[]{""},
					new String[]{""}) ,
			new ExpeditionUnit("QUEEN_ISABELLE", "Isabella, Queen of Castile and León","Queens", 250, 300, 1, 1, 1, new String[]{""},
					new String[]{""})
		};
		
	}
	
	public static ExpeditionUnit[] getUnitDefinitions(){
		return new ExpeditionUnit[]{
				
		};
	}
}
