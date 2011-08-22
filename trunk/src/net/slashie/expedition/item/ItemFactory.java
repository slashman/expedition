package net.slashie.expedition.item;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import net.slashie.expedition.domain.ExpeditionItem;
import net.slashie.expedition.domain.ExpeditionUnit;
import net.slashie.expedition.domain.Weapon;
import net.slashie.expedition.domain.Weapon.WeaponType;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.serf.ui.Appearance;

public class ItemFactory {
	private static Hashtable<String, ExpeditionItem> definitions = new Hashtable<String, ExpeditionItem>();
	private static Map<WeaponType, List<Weapon>> weaponTypesMap = new Hashtable<WeaponType, List<Weapon>>();
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
		}
	}
	
	public static ExpeditionItem createItem(String itemId){
		ExpeditionItem ret = definitions.get(itemId);
		if (ret == null){
			ExpeditionGame.crash("Item "+itemId+" not found");
			//return null;
		}
		return ret.clone();
	}

	
	public static ExpeditionUnit createUnit(String fullID, String weaponType) {
		
		return null;
	}

	public static int getEuropeanPackPrize(String fullId) {
		ExpeditionItem ret = definitions.get(fullId);
		if (ret == null){
			return 0;
		}
		return (int) Math.round(ret.getEuropeValue() * ret.getValuePack());
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
}
