package net.slashie.expedition.item;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import net.slashie.expedition.domain.Armor;
import net.slashie.expedition.domain.Armor.ArmorType;
import net.slashie.expedition.domain.ExpeditionItem;
import net.slashie.expedition.domain.ExpeditionUnit;
import net.slashie.expedition.domain.Vehicle;
import net.slashie.expedition.domain.Weapon;
import net.slashie.expedition.domain.Weapon.WeaponType;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.serf.ui.Appearance;

public class ItemFactory {
	static final Logger logger = Logger.getRootLogger();
	private static Hashtable<String, ExpeditionItem> definitions = new Hashtable<String, ExpeditionItem>();
	private static Map<WeaponType, List<Weapon>> weaponTypesMap = new Hashtable<WeaponType, List<Weapon>>();
	private static Map<ArmorType, List<Armor>> armorTypesMap = new Hashtable<ArmorType, List<Armor>>();
	public static void init(ExpeditionItem[] definitions_){
		for (int i = 0; i < definitions_.length; i++){
			definitions.put(definitions_[i].getFullID(), definitions_[i]);
			if (definitions_[i] instanceof Weapon){
				List<Weapon> list = weaponTypesMap.get(((Weapon)definitions_[i]).getWeaponType());
				if (list == null){
					list = new ArrayList<Weapon>();
					weaponTypesMap.put(((Weapon)definitions_[i]).getWeaponType(), list);
				}
				list.add((Weapon)definitions_[i]);
			}
			if (definitions_[i] instanceof Armor){
				List<Armor> list = armorTypesMap.get(((Armor)definitions_[i]).getArmorType());
				if (list == null){
					list = new ArrayList<Armor>();
					armorTypesMap.put(((Armor)definitions_[i]).getArmorType(), list);
				}
				list.add((Armor)definitions_[i]);
			}
		}
	}
	
	public static ExpeditionItem createItem(String itemId)
	{
		//logger.debug("Start: createItem Arguments: " + itemId);
		ExpeditionItem ret = definitions.get(itemId);
		if (ret == null){
			ExpeditionGame.crash("Item "+itemId+" not found");
			//return null;
		}
		return ret.clone();
	}

	/**
	 * 
	 * @param fullID "EXPLORER"
	 * @param weaponType currently unused
	 * @return returns the unit, cast to a unit, not an item
	 */
	public static ExpeditionUnit createUnit(String fullID, String weaponType) 
	{
		ExpeditionUnit unit = (ExpeditionUnit) definitions.get(fullID);
		return unit;
	}

	public static int getPalosStorePrice(String fullId) {
		ExpeditionItem ret = definitions.get(fullId);
		if (ret == null){
			return 0;
		}
		return ret.getPalosStoreValue();
	}
	
	public static String getItemNameById(String itemId) {
		ExpeditionItem ret = definitions.get(itemId);
		if (ret == null){
			ExpeditionGame.crash("Item "+itemId+" not found");
			//return null;
		}
		return ret.getDescription();
	}
	
	public static Appearance getItemAppearanceById(String itemId) {
		ExpeditionItem ret = definitions.get(itemId);
		if (ret == null){
			ExpeditionGame.crash("Item "+itemId+" not found");
			//return null;
		}
		return ret.getAppearance();
	}

	public static List<Weapon> getItemsByWeaponType(WeaponType weaponType) {
		return weaponTypesMap.get(weaponType);
	}
	
	public static List<Armor> getItemsByArmorType(ArmorType weaponType) {
		return armorTypesMap.get(weaponType);
	}

	
	public static ExpeditionItem createShip(String type, String name) {
		Vehicle ship = (Vehicle) createItem(type);
		ship.setName(name);
		return ship;
	}
}
