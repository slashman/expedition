package net.slashie.expedition.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import net.slashie.expedition.world.Culture;
import net.slashie.expedition.world.ExpeditionCell;
import net.slashie.expedition.world.ExpeditionFeature;
import net.slashie.expedition.world.OverworldExpeditionCell;
import net.slashie.libjcsi.ConsoleSystemInterface;
import net.slashie.serf.level.AbstractCell;
import net.slashie.serf.ui.AppearanceFactory;
import net.slashie.serf.ui.consoleUI.CharAppearance;
import net.slashie.util.Pair;
import net.slashie.utils.roll.Roll;

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
			new OverworldExpeditionCell("GRASS", "Grass", true, false, false, 1.2d, false, false, false),
			new OverworldExpeditionCell("PLAINS", "Grass", true, false, false, 1.0d, false, false,false),
			new OverworldExpeditionCell("WATER", "Ocean", false, false, false, 1.0d, false, false,false),
			new OverworldExpeditionCell("WATER2", "Water", true, false, true, 1.0d, false, false,false),
			new OverworldExpeditionCell("MOUNTAIN", "Mountain", true, true, false, 1.5d, false, false,true),
			new OverworldExpeditionCell("FOREST", "Forest", true, false, false, 1.2d, false, true,true),
			new OverworldExpeditionCell("PORT_CITY", "Port City", false, false, false, 1.0d, false, false,false),
			
			//Inworld Cells
			new ExpeditionCell("GOODS_STORE", "Goods Store", goodsStore),
			new ExpeditionCell("WEAPONS_STORE", "Weapons Store", weaponsStore),
			new ExpeditionCell("PORT", "Harbor", port),
			new ExpeditionCell("PUB", "Pub", pub),
			new ExpeditionCell("GUILD", "Guild", guild),
			
			new ExpeditionCell("SPAIN_GRASS", "Grass"),
			new ExpeditionCell("SPAIN_GRASS_BLOCKED", "Grass", true, false),
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
			new CharAppearance("WATER2", '~', ConsoleSystemInterface.BLUE),
			new CharAppearance("MOUNTAIN", '^', ConsoleSystemInterface.GREEN),
			new CharAppearance("FOREST", '&', ConsoleSystemInterface.GREEN),
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
			new CharAppearance("SAILOR", 's', ConsoleSystemInterface.BLUE),
			new CharAppearance("ROGUE", 'r', ConsoleSystemInterface.BROWN),
			new CharAppearance("MARINE", 'm', ConsoleSystemInterface.TEAL),
			new CharAppearance("SOLDIER", 'S', ConsoleSystemInterface.GREEN),
			new CharAppearance("ARCHER", 'a', ConsoleSystemInterface.DARK_BLUE),			
			new CharAppearance("CAPTAIN", 'C', ConsoleSystemInterface.CYAN),
			new CharAppearance("EXPLORER", 'e', ConsoleSystemInterface.RED),
			new CharAppearance("CARPENTER", 'c', ConsoleSystemInterface.DARK_RED),
			new CharAppearance("COLONIST", 'c', ConsoleSystemInterface.YELLOW),
		
			//native Units
			new CharAppearance("NATIVE_WARRIOR", 'w', ConsoleSystemInterface.RED),
			new CharAppearance("NATIVE_BRAVE", 'W', ConsoleSystemInterface.PURPLE),
			new CharAppearance("NATIVE_ARCHER", 'a', ConsoleSystemInterface.DARK_RED),
			new CharAppearance("NATIVE_COMMONER", 'c', ConsoleSystemInterface.BLUE),
			new CharAppearance("NATIVE_LEADER", 'S', ConsoleSystemInterface.CYAN),
			new CharAppearance("NATIVE_VILLAGE", '^', ConsoleSystemInterface.DARK_RED),
			new CharAppearance("NATIVE_TOWN", '^', ConsoleSystemInterface.RED),
			new CharAppearance("NATIVE_CITY", '^', ConsoleSystemInterface.RED),

			
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
			new ExpeditionUnit("SAILOR", "Sailor", "Sailors", 200, 200,
					new Roll("1D1"),
					new Roll("1D1"),
					2,
					70,10,
					1,
				new String[]{"SPEARS"},
				new String[]{""}),
			new ExpeditionUnit("ROGUE",  "Rogue",  "Rogues",  250, 250,
					new Roll("1D2"),
					new Roll("1D2"),
					2,
					85,15,
					1,
					new String[]{"BOWS", "SPEARS"},
					new String[]{""}),
			new ExpeditionUnit("MARINE", "Marine", "Marines", 300, 250,
					new Roll("1D3"),
					new Roll("1D2"),
					3,
					90,5,
					2,
					new String[]{"XBOWS","BOWS","SWORDS","SPEARS"},
					new String[]{"STUDDED_LEATHER"}),
			new ExpeditionUnit("SOLDIER", "Soldier","Soldiers", 300, 200,
					new Roll("1D3"),
					new Roll("1D3"),
					4,
					95,5,
					2,
					new String[]{"GUNS", "SWORDS", "SPEARS"},
					new String[]{"STUDDED_LEATHER", "PLATE"}),
			new ExpeditionUnit("ARCHER", "Archer","Archers", 250, 200,
					new Roll("1D2"),
					new Roll("1D2"),
					2,
					70,10,
					2,
					new String[]{"XBOWS", "BOWS", "SPEARS"},
					new String[]{"STUDDED_LEATHER"}),
			new ExpeditionUnit("CAPTAIN", "Captain","Captains", 300, 200,
					new Roll("1D1"),
					new Roll("1D1"),
					3,
					50,5,
					3,
					new String[]{"GUNS", "SWORDS", "SPEARS" },
					new String[]{"STUDDED_LEATHER", "PLATE"}),
			new ExpeditionUnit("EXPLORER", "Explorer","Explorers", 300, 400,
					new Roll("1D2"),
					new Roll("1D1"),
					4,
					70,5,
					3,
					new String[]{"SPEARS"},
					new String[]{"STUDDED_LEATHER"}),
			new ExpeditionUnit("CARPENTER", "Carpenter","Carpenters", 250, 300, 
					new Roll("1D1"),
					new Roll("1D1"),
					3,
					50,5, 
					1, 
					new String[]{"SPEARS"},
					new String[]{""}),
			new ExpeditionUnit("COLONIST",  "Colonist",  "Colonists",  200, 300, 
					new Roll("1D1"),
					new Roll("1D1"),
					1,
					50,5,
					1,
					new String[]{"SPEARS"},
					new String[]{""}),
			//Native Units
			new ExpeditionUnit("NATIVE_WARRIOR", "Warrior","Warriors", 200, 200, 
					new Roll("1D3"),
					new Roll("1D2"),
					3,
					80,15,
					0,
					new String[]{"SPEARS"},
					new String[]{""}),
			new ExpeditionUnit("NATIVE_BRAVE", "Brave","Braves", 200, 200,
					new Roll("1D4"),
					new Roll("1D2"),
					4,
					90,10,
					0,
					new String[]{"SPEARS"},
					new String[]{""}),
			new ExpeditionUnit("NATIVE_ARCHER", "Archer","Archers", 200, 200,
					new Roll("1D2"),
					new Roll("1D1"),
					2,
					60,10,
					0,
					new String[]{"BOWS", "SPEARS"},
					new String[]{""}),
			new ExpeditionUnit("NATIVE_COMMONER", "Native","Natives", 200, 300, 
					new Roll("1D1"),
					new Roll("1D1"),
					1,
					50,5, 
					0,
					new String[]{""},
					new String[]{""}),
			new ExpeditionUnit("NATIVE_LEADER", "Shaman","Shamans", 200, 50, 
					new Roll("1D1"),
					new Roll("1D1"),
					1,
					50,5,
					0,
					new String[]{""},
					new String[]{""}),
			
			//Goods
			new Food("FOOD", "Food", "Food", 1, 1),
			new Food("RUM", "Rum", "Rum", 5, 2),
			new Good("WOOD", "Wooden log", "Wooden logs", 10, GoodType.TOOL),
			new Food("FRESHWATER", "Freshwater", "Freshwater", 5,1),
			new Food("FOOD_SAUERKRAUT", "Sauerkraut","Sauerkraut", 3, 1),
			
			//New Worlds Goods
			new Valuable("GOLD_NUGGET", "Gold Nugget", "Gold Nuggets", 50, 45),
			new Valuable("GOLD_BRACELET", "Gold Bracelet","Gold Bracelets",  50, 25),
			new Valuable("NATIVE_ARTIFACT", "Pottery", "Pottery", 70, 20),
			new Food("NATIVE_FOOD", "Stash of Maíz", "Stashes of Maíz", 5, 1),

			//Weapons
			new Weapon("SPEARS", "Spear","Spears", new Roll("1D1"), new Roll("1D1"), false, 80, false, 30),
			new Weapon("SWORDS", "Sword", "Swords", new Roll("1D3"), new Roll("1D1"), false, 90, false, 50),
			new Weapon("BOWS", "Bow", "Bows", new Roll("1D2"), new Roll("0"), false, 80, true, 30),
			new Weapon("XBOWS", "Crossbow", "Crossbows", new Roll("2D2"), new Roll("0"), true, 95, true, 70),
			new Weapon("GUNS", "Harquebus", "Harquebuses", new Roll("3D2"), new Roll("0"), true, 70, true, 60),
			new Armor("PLATE", "Plate Mail","Plate Mails", 40, 4, new Roll("1D3"), "Plate"),
			new Armor("STUDDED_LEATHER", "Studded Vest", "Studded Vests", 20, 1, new Roll("1"), "Leather"),
			
			new Good("ARROWS", "Arrow", "Arrows", 5, GoodType.WEAPON),
			
			//Ships
			new Vehicle("CARRACK","Carrack","Carracks",1,true,false,false,3,25000, 10, false),
			new Vehicle("CARAVEL","Caravel","Caravels", 1,true,false,false,4,20000, 15, false),
			
			//Special
			new ExpeditionUnit("KING_FERDINAND", "Ferdinand II, King of Aragón","Kings", 250, 300, 
					new Roll("1D1"),
					new Roll("1D1"),
					2,
					50,5, 
					1, new String[]{""},
					new String[]{""}) ,
			new ExpeditionUnit("QUEEN_ISABELLE", "Isabella, Queen of Castile and León","Queens", 250, 300, 
					new Roll("1D1"),
					new Roll("1D1"),
					1,
					50,5, 
					1, new String[]{""},
					new String[]{""})
		};
		
	}
	
	public static ExpeditionUnit[] getUnitDefinitions(){
		return new ExpeditionUnit[]{
				
		};
	}

	private static Map<String, Culture> culturesMap = new HashMap<String, Culture>(); 
	static {
		Culture[] cultures = new Culture[] { 
			new Culture("MOUNT", "Fort Ancient", true, 3, composeList("NATIVE_WARRIOR,50"), 1, 3, 2), 
			new Culture("MISSI", "Missisipians", true, 2, composeList("NATIVE_WARRIOR,30", "NATIVE_BRAVE, 10"), 1, 3, 3),
			new Culture("AZTEC", "Aztec", true, 3, composeList("NATIVE_WARRIOR,40", "NATIVE_BRAVE,20", "NATIVE_ARCHER,10"), 3, 2, 2),
			new Culture("HUAST", "Huastec", true, 2, composeList("NATIVE_WARRIOR,30", "NATIVE_BRAVE,10", "NATIVE_ARCHER,20"), 2, 1, 3),
			new Culture("MIXTE", "Mixtec", true, 3, composeList("NATIVE_WARRIOR,10", "NATIVE_BRAVE,10", "NATIVE_ARCHER,10"), 2, 1, 2),
			new Culture("MAYA", "Maya", true, 1, composeList("NATIVE_WARRIOR,10", "NATIVE_BRAVE,10", "NATIVE_ARCHER,30"), 2, 2, 3),
			new Culture("PURHE", "P'urhépecha", true, 3, composeList("NATIVE_WARRIOR,10", "NATIVE_BRAVE,10", "NATIVE_ARCHER,10"), 1, 3, 1),
			new Culture("TOTON", "Totonac", true, 2, composeList("NATIVE_WARRIOR,10", "NATIVE_BRAVE,10", "NATIVE_ARCHER,10"), 2, 1, 1),
			new Culture("ZAPOT", "Zapotec", true, 2, composeList("NATIVE_WARRIOR,10", "NATIVE_BRAVE,10", "NATIVE_ARCHER,10"), 2, 2, 2),
			new Culture("CANAR", "Cañaris", true, 3, composeList("NATIVE_WARRIOR,10", "NATIVE_BRAVE,10", "NATIVE_ARCHER,10"), 1, 3, 1),
			new Culture("CHACH", "Chachapoya", true, 1, composeList("NATIVE_WARRIOR,10", "NATIVE_BRAVE,10", "NATIVE_ARCHER,10"), 2, 2, 3),
			new Culture("CHIMU", "Chimú", true, 1, composeList("NATIVE_WARRIOR,10", "NATIVE_BRAVE,10", "NATIVE_ARCHER,10"), 1, 2, 3),
			new Culture("INCA", "Inca", true, 3, composeList("NATIVE_WARRIOR,20", "NATIVE_BRAVE,10", "NATIVE_ARCHER,10"), 3, 1, 3),
			new Culture("MUISC", "Muisca", true, 2, composeList("NATIVE_WARRIOR,10", "NATIVE_BRAVE,10", "NATIVE_ARCHER,10"), 1, 3, 3),
			new Culture("TAIRO", "Tairona", true, 3, composeList("NATIVE_WARRIOR,10", "NATIVE_BRAVE,10", "NATIVE_ARCHER,10"), 1, 3, 3),
			new Culture("ARTIC", "Artic Mammal Hunters", false, 1, composeList("NATIVE_WARRIOR,10"), 0, 0, 2),
			new Culture("HUNTE", "Hunters-Gatherers", false, 2, composeList("NATIVE_BRAVE,10", "NATIVE_ARCHER,10"), 0, 1, 2),
			new Culture("FISHI", "Fishing people", false, 3, composeList("NATIVE_WARRIOR,20"), 0, 1, 1),
			new Culture("BISON", "Bison Hunters", false, 3, composeList("NATIVE_WARRIOR,10","NATIVE_ARCHER,10"), 0, 0, 3),
			new Culture("FARME", "Maiz Farmers", false, 2, composeList("NATIVE_WARRIOR,10", "NATIVE_BRAVE,10"), 0, 1, 3)
		};
		for (Culture culture: cultures){
			culturesMap.put(culture.getCode(), culture);
		}
	}

	public static Culture getCulture(String string) {
		return culturesMap.get(string);
	}

	private static List<Pair<Double, String>> composeList(String... pairs) {
		List<Pair<Double,String>> ret = new ArrayList<Pair<Double,String>>();
		for (String pairString: pairs){
			String[] splitPair = pairString.split(",");
			ret.add(new Pair<Double, String>(Double.parseDouble(splitPair[1])/100.0d, splitPair[0]));
		}
		return ret;
	}
}
