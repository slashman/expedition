package net.slashie.expedition.ui.oryx;

import java.awt.Image;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.ExpeditionItem;
import net.slashie.expedition.domain.ExpeditionUnit;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.ui.oryxUI.GFXAppearance;
import net.slashie.utils.swing.GFXMenuItem;

public class InventoryGFXMenuItem implements GFXMenuItem {
	private Equipment item;

	public InventoryGFXMenuItem(Equipment item) {
		this.item = item;
	}
	
	public Equipment getEquipment(){
		return item;
	}
	
	public Image getMenuImage() {
		return ((GFXAppearance)item.getItem().getAppearance()).getImage();
	}
	
	public String getMenuDetail() {
		int quantity = item.getQuantity();
		ExpeditionItem eitem = (ExpeditionItem)item.getItem();
		if (eitem instanceof ExpeditionUnit){
			ExpeditionUnit unit = (ExpeditionUnit) eitem;
			return "ATK"+ unit.getAttack().getMax()+" DEF"+ unit.getDefense().getMax() +" {Weight: "+(eitem.getWeight() * quantity)+")";
		} else {
			return "Weight: "+(eitem.getWeight() * quantity);
		}
	}

	public String getMenuDescription() {
		
		int quantity = item.getQuantity();
		ExpeditionItem eitem = (ExpeditionItem)item.getItem();
		String itemDescription = item.getItem().getDescription();
		if (eitem instanceof ExpeditionUnit){
			itemDescription = eitem.getFullDescription();
			return quantity + " " + itemDescription;
		} else {
			return quantity + " " + itemDescription;
		}
	}
}
