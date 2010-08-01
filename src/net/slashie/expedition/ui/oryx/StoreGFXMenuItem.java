package net.slashie.expedition.ui.oryx;

import java.awt.Image;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.Store;
import net.slashie.expedition.ui.CommonUI;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.ui.oryxUI.GFXAppearance;
import net.slashie.utils.swing.GFXMenuItem;

public class StoreGFXMenuItem implements GFXMenuItem {
	private Equipment item;
	private Store store;
	private Expedition offShore;

	public StoreGFXMenuItem(Equipment item, Store store, Expedition offShore) {
		super();
		this.item = item;
		this.store = store;
		this.offShore = offShore;
	}
	
	public Equipment getEquipment(){
		return item;
	}

	public Image getMenuImage() {
		return ((GFXAppearance)item.getItem().getAppearance()).getImage();
	}
	
	public String getMenuDetail(){
		return "";
	}
	
	public String getMenuDescription() {
		return CommonUI.getMenuStoreDescription(item, offShore, store);
	}
}