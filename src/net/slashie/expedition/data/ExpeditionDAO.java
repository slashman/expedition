package net.slashie.expedition.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.slashie.expedition.domain.Armor;
import net.slashie.expedition.domain.Armor.ArmorType;
import net.slashie.expedition.domain.Expedition.MovementSpeed;
import net.slashie.expedition.domain.ExpeditionItem;
import net.slashie.expedition.domain.ExpeditionUnit;
import net.slashie.expedition.domain.Food;
import net.slashie.expedition.domain.GoodType;
import net.slashie.expedition.domain.NPC;
import net.slashie.expedition.domain.Store;
import net.slashie.expedition.domain.StoreItemInfo;
import net.slashie.expedition.domain.StoreShipInfo;
import net.slashie.expedition.domain.Vehicle;
import net.slashie.expedition.domain.Water;
import net.slashie.expedition.domain.Weapon;
import net.slashie.expedition.domain.Weapon.WeaponType;
import net.slashie.expedition.item.ItemFactory;
import net.slashie.expedition.item.Mount;
import net.slashie.expedition.item.StorageType;
import net.slashie.expedition.town.Building;
import net.slashie.expedition.town.Farm;
import net.slashie.expedition.town.House;
import net.slashie.expedition.town.Plaza;
import net.slashie.expedition.town.Storage;
import net.slashie.expedition.world.AnimalNest;
import net.slashie.expedition.world.BotanyCrop;
import net.slashie.expedition.world.Culture;
import net.slashie.expedition.world.ExpeditionFeature;
import net.slashie.expedition.world.Plant;
import net.slashie.expedition.world.StoreFactory;
import net.slashie.libjcsi.ConsoleSystemInterface;
import net.slashie.serf.ui.AppearanceFactory;
import net.slashie.serf.ui.consoleUI.CharAppearance;
import net.slashie.util.Pair;
import net.slashie.utils.roll.Roll;

public class ExpeditionDAO {
	public static void initializeStoresFactory(){

		Store goodsStore = new Store(GoodType.SUPPLIES);
		goodsStore.setOwnerName("Supplies Store");
		goodsStore.addItem(50000, new StoreItemInfo("BISCUIT", 500, 0));
		//goodsStore.addItem(10000, new StoreItemInfo("FRESHWATER", Store.LIQUID_PACK, "barrels")); //TODO: Implement
		goodsStore.addItem(10000, new StoreItemInfo("FRESHWATER", 500, 0));
		// goodsStore.addItem(10000, new StoreItemInfo("SAUERKRAUT", Store.FOOD_PACK, "barrels")); TODO: Implement
		// goodsStore.addItem(500, new StoreItemInfo("RUM", Store.LIQUID_PACK, "barrels")); TODO: Implement
		goodsStore.addItem(10000, new StoreItemInfo("WOOD", 100, 0));
		StoreFactory.getSingleton().addStore("PALOS_GOODS", goodsStore);
		
		//Weapons Store
		Store weaponsStore = new Store(GoodType.ARMORY);
		weaponsStore.setOwnerName("Armory");
		weaponsStore.addItem(500, new StoreItemInfo("STEEL_SPEAR", 50, 0));
		weaponsStore.addItem(100, new StoreItemInfo("STEEL_SWORD", 10, 0));
		weaponsStore.addItem(200, new StoreItemInfo("COMPOSITE_BOW", 20, 0));
		weaponsStore.addItem(150, new StoreItemInfo("WOODEN_CROSSBOW", 15, 0));
		weaponsStore.addItem(100, new StoreItemInfo("HARQUEBUS", 10, 0));
		weaponsStore.addItem(200, new StoreItemInfo("STUDDED_VEST", 20, 0));
		weaponsStore.addItem(100, new StoreItemInfo("BREASTPLATE", 10, 0));
		StoreFactory.getSingleton().addStore("PALOS_ARMORY", weaponsStore);
		
		//Port
		Store port = new Store(GoodType.VEHICLE);
		port.setOwnerName("Docks");
		port.addItem(new StoreShipInfo("CARRACK", "San Pedro", 1500));
		port.addItem(new StoreShipInfo("CARRACK", "San Miguel", -2000));
		port.addItem(new StoreShipInfo("CARAVEL", "La Tunicia", 300));
		port.addItem(new StoreShipInfo("CARAVEL", "Santa Ana", -250));
		port.addItem(new StoreShipInfo("CARAVEL", "Trinidad", 1000));
		port.addItem(10, new StoreItemInfo("CAPTAIN", 1, 0));
		port.addItem(500, new StoreItemInfo("SAILOR", 10, 0));
		port.addItem(1000, new StoreItemInfo("COLONIST", 50, 0));
		port.addItem(300, new StoreItemInfo("ROGUE", 5, 0));
		StoreFactory.getSingleton().addStore("PALOS_DOCKS", port);
	
		//Pub
		Store merchant = new Store(GoodType.TRADE_GOODS);
		merchant.setOwnerName("Trade Company");
		merchant.addItem(150, new StoreItemInfo("COTTON", 5, 0));
		merchant.addItem(150, new StoreItemInfo("SUGAR", 5, 0));
		merchant.addItem(150, new StoreItemInfo("CLOTH", 10, 0));
		merchant.addItem(20, new StoreItemInfo("COW", 1, 0));
		merchant.addItem(30, new StoreItemInfo("HORSE", 2, 0));
		merchant.addItem(50, new StoreItemInfo("PIGS", 3, 0));
		StoreFactory.getSingleton().addStore("PALOS_TRADE", merchant);
		
		//Guild
		Store guild = new Store(GoodType.PEOPLE);
		guild.setOwnerName("Guild");
		guild.addItem(70, new StoreItemInfo("MARINE", 1, 0));
		guild.addItem(50, new StoreItemInfo("SOLDIER", 1, 0));
		guild.addItem(150, new StoreItemInfo("ARCHER", 1, 0));
		guild.addItem(50, new StoreItemInfo("CARPENTER", 1, 0));
		guild.addItem(10, new StoreItemInfo("DOCTOR", 1, 0));
		guild.addItem(10, new StoreItemInfo("EXPLORER", 1, 0));
		guild.addItem(15, new StoreItemInfo("NATURALIST", 1, 0));
		StoreFactory.getSingleton().addStore("PALOS_GUILD", guild);
	}
	
	public static CharAppearance[] getCharAppearances(){
		return new CharAppearance[]{
			//Expeditions
			new CharAppearance("EXPEDITION", '@', ConsoleSystemInterface.GRAY),
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
			new CharAppearance("HORSE_EXPEDITION", 'H', ConsoleSystemInterface.GRAY),
			
			//Non principal Expeditions
			new CharAppearance("HOSTILE_EXPEDITION", '@', ConsoleSystemInterface.RED),
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
			new CharAppearance("DOCTOR", 'D', ConsoleSystemInterface.BLUE),
			new CharAppearance("NATURALIST", 'N', ConsoleSystemInterface.BLUE),
			
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
			
			new CharAppearance("RUIN", '&', ConsoleSystemInterface.PURPLE),

			
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
					2,
				new WeaponType[]{WeaponType.SPEAR, WeaponType.MACE},
				new ArmorType[]{}, 600),
			new ExpeditionUnit("ROGUE",  "Rogue",  "Rogues", "Survival Wolf", UNIT_WEIGHT, 250,
					new Roll("1D2"),
					new Roll("1D2"),
					2,
					85,15,
					1,
					2,
					new WeaponType[]{WeaponType.SWORD, WeaponType.SPEAR, WeaponType.MACE},
					new ArmorType[]{}, 300),
			new ExpeditionUnit("MARINE", "Marine", "Marines", "Trained Sea Soldier",UNIT_WEIGHT, 250,
					new Roll("1D3"),
					new Roll("1D2"),
					3,
					90,5,
					2,
					2,
					new WeaponType[]{WeaponType.MUSKET, WeaponType.CROSSBOW, WeaponType.SWORD, WeaponType.SPEAR, WeaponType.MACE},
					new ArmorType[]{ArmorType.LIGHT}, 700),
			new ExpeditionUnit("SOLDIER", "Soldier", "Soldiers", "Man-at-arms", UNIT_WEIGHT, 200,
					new Roll("1D3"),
					new Roll("1D3"),
					4,
					95,5,
					2,
					3,
					new WeaponType[]{WeaponType.SWORD, WeaponType.MUSKET, WeaponType.SPEAR, WeaponType.MACE},
					new ArmorType[]{ArmorType.METAL_HEAVY, ArmorType.LIGHT}, 1000),
			new ExpeditionUnit("GUARD", "Burly Guard","Guards", "Heavily Armored Guard", UNIT_WEIGHT, 200,
					new Roll("1D3"),
					new Roll("1D3"),
					4,
					95,5,
					2,
					3,
					new WeaponType[]{WeaponType.SWORD, WeaponType.MUSKET, WeaponType.SPEAR, WeaponType.MACE},
					new ArmorType[]{ArmorType.METAL_HEAVY, ArmorType.LIGHT}, 1000),
			new ExpeditionUnit("ARCHER", "Archer","Archers", "Skilled with bow weapons", UNIT_WEIGHT, 200,
					new Roll("1D2"),
					new Roll("1D2"),
					2,
					70,10,
					2,
					2,
					new WeaponType[]{WeaponType.CROSSBOW, WeaponType.BOW, WeaponType.SPEAR, WeaponType.MACE},
					new ArmorType[]{ArmorType.LIGHT}, 800),
			new ExpeditionUnit("CAPTAIN", "Officer","Officers", "Seaman able to lead a Ship", UNIT_WEIGHT, 200,
					new Roll("1D1"),
					new Roll("1D1"),
					3,
					50,5,
					3,
					4,
					new WeaponType[]{WeaponType.MUSKET, WeaponType.SWORD, WeaponType.SPEAR, WeaponType.MACE},
					new ArmorType[]{ArmorType.METAL_HEAVY, ArmorType.LIGHT}, 3000),
			new ExpeditionUnit("EXPLORER", "Explorer","Explorers", "Extends field of view", UNIT_WEIGHT, 400,
					new Roll("1D2"),
					new Roll("1D1"),
					4,
					70,5,
					3,
					3,
					new WeaponType[]{WeaponType.SPEAR, WeaponType.MACE},
					new ArmorType[]{}, 5000),
			new ExpeditionUnit("CARPENTER", "Carpenter","Carpenters", "Good at repairing ships", UNIT_WEIGHT, 300, 
					new Roll("1D1"),
					new Roll("1D1"),
					3,
					50,5, 
					1, 
					2,
					new WeaponType[]{WeaponType.SPEAR, WeaponType.MACE},
					new ArmorType[]{}, 600),
			new ExpeditionUnit("COLONIST",  "Colonist",  "Colonists",  "Settler of new frontiers", UNIT_WEIGHT, 300, 
					new Roll("1D1"),
					new Roll("1D1"),
					1,
					50,5,
					1,
					2,
					new WeaponType[]{WeaponType.SPEAR, WeaponType.MACE},
					new ArmorType[]{}, 100),
			new ExpeditionUnit("CHILD",  "Child",  "Children",  "A young human", UNIT_WEIGHT, 10, 
					new Roll("1D0"),
					new Roll("1D0"),
					0,
					10,0,
					1,
					2,
					new WeaponType[]{},
					new ArmorType[]{}, 100),
			new ExpeditionUnit("DOCTOR",  "Doctor",  "Doctors",  "Heals expedition units", UNIT_WEIGHT, 300, 
					new Roll("1D1"),
					new Roll("1D1"),
					1,
					50,5,
					1,
					2,
					new WeaponType[]{WeaponType.SPEAR, WeaponType.MACE},
					new ArmorType[]{}, 4000),
			new ExpeditionUnit("NATURALIST",  "Naturalist",  "Naturalists",  "Search for exotic plants and animals", UNIT_WEIGHT, 300, 
					new Roll("1D1"),
					new Roll("1D1"),
					1,
					50,5,
					1,
					2,
					new WeaponType[]{WeaponType.SPEAR, WeaponType.MACE},
					new ArmorType[]{}, 4000),
							
			//Native Units
			new ExpeditionUnit("NATIVE_WARRIOR", "Warrior","Warriors", "Defender of his tribe", UNIT_WEIGHT, 200, 
					new Roll("1D3"),
					new Roll("1D2"),
					3,
					80,15,
					1,
					2,
					new WeaponType[]{WeaponType.SPEAR, WeaponType.MACE},
					new ArmorType[]{}, 200),
			new ExpeditionUnit("NATIVE_ARCHER", "Archer","Archers", "Able with the bow", UNIT_WEIGHT, 200,
					new Roll("1D2"),
					new Roll("1D1"),
					2,
					60,10,
					1,
					2,
					new WeaponType[]{WeaponType.BOW, WeaponType.SPEAR, WeaponType.MACE},
					new ArmorType[]{}, 200),
			new ExpeditionUnit("NATIVE_COMMONER", "Native","Natives", "Working member of a tribe", UNIT_WEIGHT, 300, 
					new Roll("1D1"),
					new Roll("1D1"),
					1,
					50,5, 
					1,
					2,
					new WeaponType[]{WeaponType.MACE},
					new ArmorType[]{}, 100),
			new ExpeditionUnit("NATIVE_SHAMAN", "Shaman","Shamans", "Shaman of the tribe", UNIT_WEIGHT, 50, 
					new Roll("1D1"),
					new Roll("1D1"),
					1,
					50,5,
					2,
					3,
					new WeaponType[]{},
					new ArmorType[]{}, 100),
			new ExpeditionUnit("EAGLE_WARRIOR", "Eagle Warrior","Eagle Warriors", "Elite Warrior", UNIT_WEIGHT, 200,
					new Roll("2D1"),
					new Roll("1D2"),
					3,
					95,20,
					2,
					2,
					new WeaponType[]{WeaponType.SWORD, WeaponType.SPEAR, WeaponType.MACE},
					new ArmorType[]{ArmorType.LIGHT}, 200),
			new ExpeditionUnit("JAGUAR_WARRIOR", "Jaguar Warrior","Jaguar Warriors", "Elite Warrior", UNIT_WEIGHT, 200,
					new Roll("1D3"),
					new Roll("1D2"),
					4,
					95,10,
					2,
					3,
					new WeaponType[]{WeaponType.SWORD, WeaponType.SPEAR, WeaponType.MACE},
					new ArmorType[]{ArmorType.LIGHT}, 200),
			new ExpeditionUnit("QUETZAL_ARCHER", "Quetzal Archer","Quetzal Archers", "Elite Archer", UNIT_WEIGHT, 200,
					new Roll("1D2"),
					new Roll("1D2"),
					3,
					95,5,
					2,
					3,
					new WeaponType[]{WeaponType.BOW, WeaponType.SPEAR, WeaponType.MACE},
					new ArmorType[]{ArmorType.LIGHT}, 200),
				
			//Animals units
			new ExpeditionUnit("LLAMA_UNIT", "Llama", "Llamas", "Wild Llamas", UNIT_WEIGHT, 200,
					new Roll("1D2"),
					new Roll("1D2"),
					2,
					70,10,
					2,
					2,
					null,
					null, 800),
			
			//Goods
			
			new Food("BREAD", "Bread", "Bread", "Food Ration", 1, 1, 1, 2, StorageType.WAREHOUSE),
			new Food("DRIED_MEAT", "Dried Meat", "Dried Meat", "Food Ration", 2, 1, 1,1, StorageType.WAREHOUSE),
			new Food("SAUERKRAUT", "Sauerkraut","Sauerkraut", "Food Ration", 2, 1, 2,1, StorageType.WAREHOUSE),
			
			new Food("BISCUIT",
					"Biscuit",
					"Biscuit", 
					"Food Ration", 
					3, 
					1, 
					1, 
					1, 
					StorageType.WAREHOUSE),
			new Water("FRESHWATER", 
					"Freshwater", 
					"Freshwater", 
					"Liquid of Life", 
					3, 
					1, 
					20,
					5, 
					StorageType.WAREHOUSE),
			
			
			new Food("BEANS", "Beans", "Beans", "Food Ration", 2, 1, 4,1, StorageType.GRANARY),
			new Food("MAIZE", "Maize", "Maize", "Food Ration", 3, 1, 2,1, StorageType.GRANARY),
			new Food("WHEAT", "Wheat", "Wheat", "Food Ration", 3, 1, 1,2, StorageType.GRANARY),
			new Food("POTATOES", "Potatoes", "Potatoes", "Food Ration", 5, 1, 4,1, StorageType.GRANARY),
			new Food("TOMATOES", "Tomatoes", "Tomatoes", "Food Ration", 2, 1, 4,1, StorageType.GRANARY),
			new Food("FISH", "Fish", "Fish", "Food Ration", 1, 1, 1, 1, StorageType.WAREHOUSE),
			new Food("FRUIT", "Fruit", "Fruit", "Food Ration", 3, 1, 1, 1, StorageType.WAREHOUSE),
			
			
			
			new ExpeditionItem("RUM", "Rum", "Rum", "Liquid of Life", "RUM", 2, GoodType.SUPPLIES, 400,500, StorageType.WAREHOUSE),
			
			new ExpeditionItem("WOOD", "Wooden log", "Wooden logs", "Wood piece", "WOOD", 10, GoodType.SUPPLIES, 1,1, StorageType.WAREHOUSE),
			
			// Trade Goods, Old world
			new ExpeditionItem("COTTON", "Cotton", "Cotton", "Trade Good", "COTTON", 200, GoodType.TRADE_GOODS, 200 , 400, StorageType.WAREHOUSE),
			new ExpeditionItem("SUGAR", "Sugar", "Sugar", "Trade Good", "SUGAR", 800, GoodType.TRADE_GOODS, 400 , 800, StorageType.WAREHOUSE),
			new ExpeditionItem("CLOTH", "Cloth", "Cloth", "Trade Good", "CLOTH", 50, GoodType.TRADE_GOODS, 600, 700, StorageType.WAREHOUSE),
			
			// Trade Goods, New world
			new ExpeditionItem("COCA", "Coca", "Coca", "Trade Good", "COCA", 500, GoodType.TRADE_GOODS, 200, 500, StorageType.WAREHOUSE ),
			new ExpeditionItem("COCOA", "Cocoa", "Cocoa", "Trade Good", "COCOA", 600, GoodType.TRADE_GOODS, 1200, 400, StorageType.WAREHOUSE ),
			new ExpeditionItem("CHILI_PEPPER", "Chili", "Chili", "Trade Good", "CHILI_PEPPER", 600, GoodType.TRADE_GOODS, 1800, 400, StorageType.WAREHOUSE ),
			new ExpeditionItem("PINEAPPLE", "Pineapple", "Pineapple", "Trade Good", "PINEAPPLE", 800, GoodType.TRADE_GOODS, 1000, 500, StorageType.WAREHOUSE ),
			new ExpeditionItem("STRAWBERRIES", "Strawberries", "Strawberries", "Trade Good", "STRAWBERRIES", 800, GoodType.TRADE_GOODS, 1000 , 300, StorageType.WAREHOUSE),
			new ExpeditionItem("TOBACCO", "Tobacco", "Tobacco", "Trade Good", "TOBACCO", 200, GoodType.TRADE_GOODS, 1500 , 500, StorageType.WAREHOUSE),
			new ExpeditionItem("COATS", "Coats", "Coats", "Trade Good", "COATS", 60, GoodType.TRADE_GOODS, 2000, 800, StorageType.WAREHOUSE),
			new ExpeditionItem("FURS", "Furs", "Furs", "Trade Good", "FURS", 60, GoodType.TRADE_GOODS, 1000, 500, StorageType.WAREHOUSE ),
			new ExpeditionItem("GOLD_ARTIFACTS", "Gold Artifacts", "Gold Artifacts", "Trade Good", "GOLD_ARTIFACTS", 40, GoodType.TRADE_GOODS, 1600, 500, StorageType.WAREHOUSE),
			new ExpeditionItem("NATIVE_ARTIFACTS", "Native Artifacts", "Native Artifacts", "Trade Good", "NATIVE_ARTIFACTS", 20, GoodType.TRADE_GOODS, 800, 200, StorageType.WAREHOUSE),

			
			// Armory
			new Weapon(WeaponType.SPEAR, "STEEL_SPEAR", "Steel Spear","Steel Spears", "Basic weapon [All classes]", new Roll("1D2"), new Roll("1D1"), false, 80, false, 8, 50, 80),
			new Weapon(WeaponType.SWORD, "STEEL_SWORD", "Steel Sword", "Steel Swords", "Strong weapon [Marine, Soldier, Officer, Rogue]", new Roll("2D3"), new Roll("1D1"), false, 90, false, 10, 150, 200),
			new Weapon(WeaponType.BOW, "COMPOSITE_BOW", "Longbow", "Longbows", "Cheap ranged weapon [Archer]", new Roll("1D3"), new Roll("0"), false, 80, true, 5, 60, 90),
			new Weapon(WeaponType.CROSSBOW, "WOODEN_CROSSBOW", "Crossbow", "Crossbows", "Simple ranged weapon [Marine, Archer]", new Roll("2D2"), new Roll("0"), true, 95, true, 10, 180, 250),
			new Weapon(WeaponType.MUSKET, "HARQUEBUS", "Harquebus", "Harquebus", "Strong ranged weapon [Marine, Soldier, Officer]", new Roll("3D2"), new Roll("0"), true, 70, true, 10, 300, 400),
			
			new Weapon(WeaponType.BOW, "PLUMED_BOW", "Plumed Bow", "Plumed Bows", "Spiritual Bows [Quetzal Archer]", new Roll("1D4"), new Roll("0"), true, 95, true, 5, 200, 100),
			new Weapon(WeaponType.BOW, "SIMPLE_BOW", "Simple Bow", "Simple Bows", "Simple Wooden Bow [Archer, Native Archer, Quetzal Archer]", new Roll("1D2"), new Roll("0"), true, 80, true, 3, 30, 50),
			new Weapon(WeaponType.MACE, "OBSIDIAN_SWORD", "Obsidian Macuahuitl", "Obsidian Macuahuitls", "Wooden sword with prismatic blades of stone [All classes]",new Roll("2D2"), new Roll("1D1"), false, 90, false, 15, 300, 200),
			new Weapon(WeaponType.MACE, "WOODEN_MACE", "Wooden Mace", "Wooden Maces", "Simple wooden mace [All classes]", new Roll("1D1"), new Roll("0"), false, 70, false, 10, 30, 30),
			
			new Armor(ArmorType.METAL_HEAVY, "BREASTPLATE", "Breastplate","Breastplates", "Metal breast armor [Soldier, Officer]", 20, 4, new Roll("1D4"), "Plate", 500, 800),
			new Armor(ArmorType.LIGHT, "STUDDED_VEST", "Studded Vest", "Studded Vests", "[Marine, Soldier, Archer, Officer]", 10, 1, new Roll("1D2"), "Leather", 50, 70),
			
			// Livestock
			new ExpeditionItem("ATTACK_DOG", "Attack Dog", "Attack Dogs", "Men best companion", "ATTACK_DOG",  50, GoodType.LIVESTOCK, 200, 150, StorageType.BARN),
			new ExpeditionItem("COW", "Cow", "Cows", "Cow", "COW",  500, GoodType.LIVESTOCK, 5000, 6000, StorageType.BARN),
			new Mount("HORSE", "Horse", "Horses", "Mounts", "HORSE",  800, GoodType.LIVESTOCK, 2000, 3000, 1600, MovementSpeed.FAST),
			new ExpeditionItem("PIGS", "Pig", "Pigs", "Big Pink Pig", "PIGS",  150, GoodType.LIVESTOCK, 500, 400, StorageType.BARN),
			new ExpeditionItem("LLAMA", "Llama", "Llamas", "Horse-like creature", "LLAMA",  300, GoodType.LIVESTOCK, 4000, 2000, StorageType.BARN),

			
			//Ships
			new Vehicle("CARRACK","Carrack","Carracks", "Big, bulky ship",1,true,false,3,100000, 10, false, GoodType.VEHICLE, 160000, 32000),
			new Vehicle("CARAVEL","Caravel","Caravels", "Small, quick ship", 1,true,false,4,60000, 15, false, GoodType.VEHICLE, 120000, 24000),
			
			//Special
			new ExpeditionUnit("KING_FERDINAND", "Ferdinand II, King of Arag�n","Kings", "King", UNIT_WEIGHT, 300, 
					new Roll("1D1"),
					new Roll("1D1"),
					2,
					50,5, 
					1, 
					3, new WeaponType[]{},
					new ArmorType[]{},5000) ,
			new ExpeditionUnit("QUEEN_ISABELLE", "Isabella, Queen of Castile and Le�n","Queens", "Queen", UNIT_WEIGHT, 300, 
					new Roll("1D1"),
					new Roll("1D1"),
					1,
					50,5, 
					1, 
					3, new WeaponType[]{},
					new ArmorType[]{}, 5000),
			new ExpeditionUnit("DOMINIK", "Friar Domenico Marcus","Mingoses", "", UNIT_WEIGHT, 300, 
					new Roll("1D1"),
					new Roll("1D1"),
					1,
					50,5, 
					1, 
					2, new WeaponType[]{},
					new ArmorType[]{}, 5000),
			new ExpeditionUnit("COLOMBUS", "Col�n","Colones", "",  UNIT_WEIGHT, 300, 
					new Roll("1D1"),
					new Roll("1D1"),
					1,
					50,5, 
					1,
					2, new WeaponType[]{},
					new ArmorType[]{}, 5000),
			new ExpeditionUnit("BIZCOCHO", "Bizcocho","Bizcochos","", UNIT_WEIGHT, 300, 
					new Roll("1D1"),
					new Roll("1D1"),
					1,
					50,5, 
					1,
					2, new WeaponType[]{},
					new ArmorType[]{}, 5000),
			new ExpeditionUnit("CRISTOFORO", "Juan Crist�foro, el bardo","Jices", "", UNIT_WEIGHT, 300, 
					new Roll("1D1"),
					new Roll("1D1"),
					1,
					50,5, 
					1,
					2, new WeaponType[]{},
					new ArmorType[]{}, 5000),
			new ExpeditionUnit("SANTIAGO", "Don Santiago","Santiagos", "",  UNIT_WEIGHT, 300, 
					new Roll("1D1"),
					new Roll("1D1"),
					1,
					50,5, 
					1,
					3, new WeaponType[]{},
					new ArmorType[]{}, 5000)
		};
		
	}

	
	private static Building[] buildings = new Building[]{
		//new Building("PLAZA", "Plaza", "Center of community life", 40, DayShiftAgent.TICKS_PER_DAY * 120, 0, 14, PLAZA_CAPABILITIES),
		new Plaza(),
		new House(),
		new Storage (),
		new Farm(),
		/*
		new Building("MILL", "Mill", "Transforms grain into bread", 60, DayShiftAgent.TICKS_PER_DAY * 80, 0, 14),
		
		new Building("LUMBER_CAMP", "Lumber Camp", "Can hold 500 units of wood", 60, DayShiftAgent.TICKS_PER_DAY * 80, 0, 14),
		new Building("TRADING_OUTPOST", "Trading Outpost", "", 60, DayShiftAgent.TICKS_PER_DAY * 120, 0, 10),
		new Building("HARBOR", "Harbor", "", 60, DayShiftAgent.TICKS_PER_DAY * 120, 0, 14),
		new Building("BLACKSMITH", "Blacksmith", "", 60, DayShiftAgent.TICKS_PER_DAY * 200, 0, 14),
		
		new Building("CHURCH", "Small Church", "", 60, DayShiftAgent.TICKS_PER_DAY * 60, 0, 10),
		new Building("MISSION", "Mission", "", 60, DayShiftAgent.TICKS_PER_DAY * 400, 0, 30),
		new Building("CATHEDRAL", "Cathedral", "", 60, DayShiftAgent.TICKS_PER_DAY * 1000, 0, 120),
		
		new Building("BARRACKS", "Barracks", "", 60, DayShiftAgent.TICKS_PER_DAY * 400, 0, 30),
		new Building("STABLES", "Stables", "", 60, DayShiftAgent.TICKS_PER_DAY * 500, 0, 30),
		
		new Building("FORT", "Fort", "", 60, DayShiftAgent.TICKS_PER_DAY * 1000, 0, 120),
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
					gtvm(1.0d,1.0d,1.0d,1.0d,1.0d),
					composePairList("SIMPLE_BOW,5","WOODEN_MACE,10","BEANS,200","MAIZE,100","FISH,50","CLOTH,10","STRAWBERRIES,10","TOBACCO,10","COATS,5","FURS,5","NATIVE_ARTIFACTS,1"),
					1, 3, 2), 
			new Culture("MISSI", "Missisipians", true, 2, 
					composePairList("NATIVE_WARRIOR,30", "NATIVE_ARCHER,10"), 
					gtvm(1.5d,1.0d,1.0d,0.5d,1),
					composePairList("SIMPLE_BOW,5","WOODEN_MACE,10","BEANS,200","DRIED_MEAT,100","FISH,50","CLOTH,10","STRAWBERRIES,10","TOBACCO,10","FURS,5","NATIVE_ARTIFACTS,1"),
					1, 3, 3),
			new Culture("AZTEC", "Aztec", true, 3, 
					composePairList("NATIVE_WARRIOR,20", "NATIVE_ARCHER,20", "JAGUAR_WARRIOR,10","EAGLE_WARRIOR,20"), 
					gtvm(0.5d,0.5d,1.0d,2.0d,1),
					composePairList("SIMPLE_BOW,5","OBSIDIAN_SWORD,5","WOODEN_MACE,10","TOMATOES,200","DRIED_MEAT,100","FISH,50","CLOTH,10","COCOA,10","CHILI_PEPPER,10","FURS,5","GOLD_ARTIFACTS,1"),
					3, 2, 2),
			new Culture("HUAST", "Huastec", true, 2, 
					composePairList("NATIVE_WARRIOR,20", "NATIVE_ARCHER,10", "JAGUAR_WARRIOR,5","EAGLE_WARRIOR,15"), 
					gtvm(0.5d,1.0d,0.5d,2.0d,1),
					composePairList("SIMPLE_BOW,5","OBSIDIAN_SWORD,5","WOODEN_MACE,10","MAIZE,200","FISH,50","CLOTH,10","COCOA,10","FURS,5","GOLD_ARTIFACTS,1","NATIVE_ARTIFACTS,2"),
					2, 1, 3),
			new Culture("MIXTE", "Mixtec", true, 3, 
					composePairList("NATIVE_WARRIOR,20", "NATIVE_ARCHER,10", "EAGLE_WARRIOR,20", "QUETZAL_ARCHER,10"), 
					gtvm(1.0d,0.5d,1.0d,1.5d,1),
					composePairList("PLUMED_BOW,5","SIMPLE_BOW,10","WOODEN_MACE,10","MAIZE,200","FISH,50","CLOTH,10","COCOA,10","FURS,5","CHILI_PEPPER,10"),
					2, 1, 2),
			new Culture("MAYA", "Maya", true, 1, 
					composePairList("NATIVE_WARRIOR,20", "NATIVE_ARCHER,10", "EAGLE_WARRIOR,10", "QUETZAL_ARCHER,30"),
					gtvm(2.0d,2.0d,0.1d,1.0d,1),
					composePairList("PLUMED_BOW,5","SIMPLE_BOW,10","WOODEN_MACE,10","MAIZE,200","FISH,50","CLOTH,10","COCOA,10","FURS,5","GOLD_ARTIFACTS,1","NATIVE_ARTIFACTS,2"),
					2, 2, 3),
			new Culture("PURHE", "P'urh�pecha", true, 3, 
					composePairList("NATIVE_WARRIOR,20", "NATIVE_ARCHER,10", "EAGLE_WARRIOR,20", "JAGUAR_WARRIOR,10"),
					gtvm(0.5d,0.1d,0.5d,3.0d,1),
					composePairList("SIMPLE_BOW,5","OBSIDIAN_SWORD,5","WOODEN_MACE,10","MAIZE,200","FISH,50","CLOTH,10","COCOA,10","FURS,5","NATIVE_ARTIFACTS,2"),
					1, 3, 1),
			new Culture("TOTON", "Totonac", true, 2, 
					composePairList("NATIVE_WARRIOR,20", "NATIVE_ARCHER,10", "EAGLE_WARRIOR,10", "QUETZAL_ARCHER,5"),
					gtvm(1.0d,1.5d,0.5d,1.0d,1),
					composePairList("SIMPLE_BOW,5","OBSIDIAN_SWORD,5","MAIZE,200","FISH,50","CLOTH,10","COCOA,10"),
					2, 1, 1),
			new Culture("ZAPOT", "Zapotec", true, 2, 
					composePairList("NATIVE_WARRIOR,60"),
					gtvm(1.0d,1.0d,1.0d,1.0d,1),
					composePairList("OBSIDIAN_SWORD,5","WOODEN_MACE,10","MAIZE,200","FISH,50","CLOTH,10","CHILI_PEPPER,10","NATIVE_ARTIFACTS,5"),
					2, 2, 2),
			new Culture("CANAR", "Ca�aris", true, 3, 
					composePairList("NATIVE_WARRIOR,20", "NATIVE_ARCHER,20"), 
					gtvm(1.0d,2.0d,0.1d,1.0d,1),
					composePairList("SIMPLE_BOW,5","OBSIDIAN_SWORD,10","DRIED_MEAT,100","BEANS,120","POTATOES,40","FISH,50","CLOTH,5","COCA,3","PINEAPPLE,3","NATIVE_ARTIFACTS,5"),
					1, 3, 1),
			new Culture("CHACH", "Chachapoya", true, 1, 
					composePairList("NATIVE_WARRIOR,40"),
					gtvm(1.0d,1.5d,1.0d,0.5d,1),
					composePairList("SIMPLE_BOW,5","OBSIDIAN_SWORD,10","LLAMA,3","DRIED_MEAT,100","BEANS,120","POTATOES,40","FISH,50","CLOTH,5","COCA,3","PINEAPPLE,3","NATIVE_ARTIFACTS,5"),
					2, 2, 3),
			new Culture("CHIMU", "Chim�", true, 1, 
					composePairList("NATIVE_WARRIOR,20", "NATIVE_ARCHER,10"),
					gtvm(1.0d,1.0d,1.0d,1.0d,1),
					composePairList("SIMPLE_BOW,5","OBSIDIAN_SWORD,10","LLAMA,3","DRIED_MEAT,100","BEANS,120","POTATOES,40","FISH,50","CLOTH,5","COCA,3","PINEAPPLE,3","NATIVE_ARTIFACTS,5"),
					1, 2, 3),
			new Culture("INCA", "Inca", true, 3, 
					composePairList("NATIVE_WARRIOR,40", "NATIVE_ARCHER,20"), 
					gtvm(0.1d,2.0d,1.0d,1.0d,1),
					composePairList("SIMPLE_BOW,5","OBSIDIAN_SWORD,10","LLAMA,3","DRIED_MEAT,100","BEANS,120","POTATOES,40","FISH,50","CLOTH,5","COCA,3","STRAWBERRIES,3","NATIVE_ARTIFACTS,5", "GOLD_ARTIFACTS,5"),
					3, 1, 3),
			new Culture("MUISC", "Muisca", true, 2, 
					composePairList("NATIVE_WARRIOR,20", "NATIVE_ARCHER,20"),
					gtvm(1.0d,1.0d,1.0d,1.0d,1),
					composePairList("SIMPLE_BOW,5","WOODEN_MACE,10","LLAMA,3","DRIED_MEAT,100","BEANS,120","POTATOES,40","FISH,50","CLOTH,5","COCA,3","PINEAPPLE,3","NATIVE_ARTIFACTS,5"),
					1, 3, 3),
			new Culture("TAIRO", "Tairona", true, 3, 
					composePairList("NATIVE_WARRIOR,40", "NATIVE_ARCHER,20"), 
					gtvm(1.0d,1.0d,1.0d,1.0d,1),
					composePairList("SIMPLE_BOW,5","WOODEN_MACE,10","DRIED_MEAT,100","MAIZE,120","TOMATOES,40","FISH,50","CLOTH,5","COCA,3","PINEAPPLE,3","GOLD_ARTIFACTS,2","NATIVE_ARTIFACTS,5"),
					1, 3, 3),
			new Culture("ARTIC", "Artic Mammal Hunters", false, 1, 
					composePairList(), 
					gtvm(2.0d,1.0d,0.5d,0.5d,1),
					composePairList("DRIED_MEAT,200","FURS,5"),
					0, 0, 2),
			new Culture("HUNTE", "Hunters-Gatherers", false, 2, 
					composePairList( "NATIVE_ARCHER,10"),
					gtvm(1.0d,0.1d,2.0d,1.0d,1),
					composePairList("SIMPLE_BOW,5","DRIED_MEAT,200","NATIVE_ARTIFACTS,5"),
					0, 1, 2),
			new Culture("FISHI", "Fishing people", false, 3, 
					composePairList("NATIVE_WARRIOR,40"),
					gtvm(3.0d,0.1d,1.0d,0.1d,1),
					composePairList("FISH,200","NATIVE_ARTIFACTS,5"),
					0, 1, 1),
			new Culture("BISON", "Bison Hunters", false, 3, 
					composePairList("NATIVE_WARRIOR,40", "NATIVE_ARCHER,40"), 
					gtvm(2.0d,0.1d,0.1d,3.0d,1),
					composePairList("SIMPLE_BOW,5","WOODEN_MACE,10","DRIED_MEAT,200","FURS,5"),
					0, 0, 3),
			new Culture("FARME", "Maiz Farmers", false, 2, 
					composePairList("NATIVE_ARCHER,30"),
					gtvm(0.5d,0.1d,3.0d,1.0d,1),
					composePairList("SIMPLE_BOW,5","WOODEN_MACE,10","MAIZE,200","NATIVE_ARTIFACTS,5"),
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
		if (d.length != 5)
			throw new RuntimeException("Invalid values for good type evaluation");
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
			ret.add(new Pair<Double, String>(Double.parseDouble(splitPair[1].trim())/100.0d, splitPair[0].trim()));
		}
		return ret;
	}

	public static Building[] getBuildings() {
		return buildings;
	}
	
	public static NPC[] getNPCs(){
		return new NPC[]{
				new NPC((ExpeditionUnit)ItemFactory.createItem("DOMINIK"), true, true),
				new NPC((ExpeditionUnit)ItemFactory.createItem("BIZCOCHO"), false, true, "WOOF-WOOF!", "GRRRR!!!"),
				new NPC((ExpeditionUnit)ItemFactory.createItem("CRISTOFORO"), false, true, "The Cave is gushing...", "I play my lute and receive the good wishes of the people.", "The song goes: Ho eyo he hum!"),
				new NPC((ExpeditionUnit)ItemFactory.createItem("SANTIAGO"), false, true, "The World is changing!", "All things have a true nature!"),
				new NPC((ExpeditionUnit)ItemFactory.createItem("SOLDIER"), false, false, "The moors are gone. Long live the King and Queen!"),
				new NPC((ExpeditionUnit)ItemFactory.createItem("MARINE"), false, false, "Our fleet is strong"),
				new NPC((ExpeditionUnit)ItemFactory.createItem("ARCHER"), false, false, "Gunpowder weapons will never replace the keen archer", "May I go with you? You have my bow."),
				new NPC((ExpeditionUnit)ItemFactory.createItem("GUARD"), true, false),
				new NPC((ExpeditionUnit)ItemFactory.createItem("COLONIST"), false, false, "Many a grave has been unjustly filled in the name of justice.", "I will not speak of my sin!"),
				new NPC((ExpeditionUnit)ItemFactory.createItem("KING_FERDINAND"), true, true, "I am glad we could fetch you before leaving C�rdova!", "You have my blessing, may you have success in your journey"),
				new NPC((ExpeditionUnit)ItemFactory.createItem("QUEEN_ISABELLE"), true, true, "I hope our Holy mother Mary blesses your trip", "Please spread our faith around the world")
			};
	}
	
	public static Map<String, AnimalNest> animalNestsMap = new HashMap<String, AnimalNest>();
	static{
		AnimalNest[] nests = new AnimalNest[]{
			new AnimalNest("SPACE", "Llamas From Space", 3, 20, "LLAMA_UNIT", true)	
		};
		
		for (AnimalNest nest: nests){
			animalNestsMap.put(nest.getCode(), nest);
		}
	}
	
	public static AnimalNest getAnimalNest(String animalNestCode){
		return animalNestsMap.get(animalNestCode);
	}

	public static Map<String, BotanyCrop> botanyCropsMap = new HashMap<String, BotanyCrop>();
	static{
		BotanyCrop[] crops = new BotanyCrop[]{
				new BotanyCrop("POISON", "Poison Plants", Arrays.asList(new Plant[]{
					new Plant("Aconitum", "Aconitum. The dark green leaves of Aconitum species lack stipules. They are palmate or deeply palmately lobed with 5–7 segments. Each segment again is 3-lobed with coarse sharp teeth. The leaves have a spiral (alternate) arrangement. The lower leaves have long petioles.", ""),
					new Plant("Agave", "Aconitum. Chiefly Mexican, agaves are also native to the southern and western United States and central and tropical South America. They are succulents with a large rosette of thick, fleshy leaves, each ending generally in a sharp point and with a spiny margin; the stout stem is usually short, the leaves apparently springing from the root. Along with plants from the related genus Yucca, various Agave species are popular ornamental plants.", "")
				}), 20)
		};
		
		for (BotanyCrop bc: crops){
			botanyCropsMap.put(bc.getCode(), bc);
		}
	}
	
	public static BotanyCrop getBotanyCrop(String botanyCrop) {
		return botanyCropsMap.get(botanyCrop);
	}
}
