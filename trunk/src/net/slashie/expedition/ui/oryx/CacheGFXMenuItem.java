package net.slashie.expedition.ui.oryx;

import java.awt.Image;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.GoodsCache;
import net.slashie.expedition.ui.CommonUI;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.ui.oryxUI.GFXAppearance;
import net.slashie.utils.swing.GFXMenuItem;

public class CacheGFXMenuItem implements GFXMenuItem {
	private Equipment item;
	private GoodsCache cache;
	private Expedition expedition;
	
	public CacheGFXMenuItem(Equipment item, GoodsCache cache) {
		this.item = item;
		this.cache = cache;
	}
	
	public CacheGFXMenuItem(Equipment item, Expedition expedition) {
		this.item = item;
		this.expedition = expedition;
	}
	
	public Equipment getEquipment(){
		return item;
	}

	public String getMenuDescription() {
		return CommonUI.getMenuCacheDescription(item, expedition,cache);
	}
	
	@Override
	public Image getMenuImage() {
		return getItemAppearance().getImage();
	}

	
	@Override
	public String getMenuDetail() {
		return "";
	}

	private GFXAppearance getItemAppearance(){
		return (GFXAppearance)item.getItem().getAppearance();
	}
}
