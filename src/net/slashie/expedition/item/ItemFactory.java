package net.slashie.expedition.item;

import java.util.Hashtable;

import net.slashie.expedition.domain.ExpeditionItem;
import net.slashie.expedition.game.ExpeditionGame;

public class ItemFactory {
	private static Hashtable<String, ExpeditionItem> definitions = new Hashtable<String, ExpeditionItem>();
	public static void init(ExpeditionItem[] definitions_){
		for (int i = 0; i < definitions_.length; i++)
			definitions.put(definitions_[i].getFullID(), definitions_[i]);
	}
	
	public static ExpeditionItem createItem(String itemId){
		ExpeditionItem ret = definitions.get(itemId);
		if (ret == null){
			ExpeditionGame.crash("Item "+itemId+" not found");
			return null;
		}
		return ret;
	}
}
