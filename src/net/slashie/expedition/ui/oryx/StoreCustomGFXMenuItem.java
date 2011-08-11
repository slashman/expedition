package net.slashie.expedition.ui.oryx;

import java.awt.Color;
import java.awt.Image;

import net.slashie.expedition.domain.Armor;
import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.ExpeditionItem;
import net.slashie.expedition.domain.ExpeditionUnit;
import net.slashie.expedition.domain.Store;
import net.slashie.expedition.domain.StoreItemInfo;
import net.slashie.expedition.domain.Weapon;
import net.slashie.libjcsi.CharKey;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.ui.Appearance;
import net.slashie.serf.ui.oryxUI.GFXAppearance;
import net.slashie.serf.ui.oryxUI.SwingSystemInterface;
import net.slashie.utils.swing.CustomGFXMenuItem;

public class StoreCustomGFXMenuItem implements CustomGFXMenuItem{
	private static final long serialVersionUID = 1L;
	
	private Equipment item;
	private Store store;
	private Expedition expedition;

	public StoreCustomGFXMenuItem(Equipment item, Store store, Expedition expedition) {
		this.item = item;
		this.store = store;
		this.expedition = expedition;
	}
	
	public Equipment getEquipment(){
		return item;
	}
	
	public String getGroupClassifier() {
		return ((ExpeditionItem)item.getItem()).getGroupClassifier();
	}
	
	
	public Image getMenuImage() {
		return null;
	}
	
	public String getMenuDetail() {
		return null;
	}

	public String getMenuDescription() {
		return null;
	}
	
	@Override
	public boolean showTooltip() {
		return true;
	}
	
	@Override
	public void drawTooltip(SwingSystemInterface si, int x, int y, int index) {
		// Get some info
		Image unitImage = ((GFXAppearance)item.getItem().getAppearance()).getImage();
		ExpeditionItem eitem = (ExpeditionItem)item.getItem();
		String itemDescription = item.getItem().getDescription();
		StoreItemInfo itemInfo = store.getBasicInfo((ExpeditionItem)item.getItem(), expedition);
	
		// Draw a cute border
		if (eitem instanceof ExpeditionUnit){
			si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).setColor(ExpeditionOryxUI.ITEM_BOX_HIGHLIGHT_COLOR);
			si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).fillRect(x+1, y+1, 350 - 2, 60 - 2);
			si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).setColor(ExpeditionOryxUI.ITEM_BOX_BORDER_COLOR);
			si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).drawRect(x+1, y+1, 350 - 2, 60 - 2);
			si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).drawRect(x+2, y+2, 350 - 4, 60 - 4);
		} else {
			si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).setColor(ExpeditionOryxUI.ITEM_BOX_HIGHLIGHT_COLOR);
			si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).fillRect(x+1, y+1, ExpeditionOryxUI.STANDARD_ITEM_WIDTH - 2, 60 - 2);
			si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).setColor(ExpeditionOryxUI.ITEM_BOX_BORDER_COLOR);
			si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).drawRect(x+1, y+1, ExpeditionOryxUI.STANDARD_ITEM_WIDTH - 2, 60 - 2);
			si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).drawRect(x+2, y+2, ExpeditionOryxUI.STANDARD_ITEM_WIDTH - 4, 60 - 4);
		}
		
		si.drawImage(ExpeditionOryxUI.UI_WIDGETS_LAYER, x + 12, y + 12, unitImage);
		si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+5, y + 55, (char)(CharKey.a + index + 1) + ". " + itemDescription, Color.WHITE);
		
		// Unit status
		if (eitem instanceof ExpeditionUnit){
			ExpeditionUnit unit = (ExpeditionUnit)eitem;
			si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+48, y + 15, unit.getLongDescription(), Color.WHITE);
			int i = 0;
			for (Appearance weaponAppearance: unit.getAvailableWeaponAppearances()){
				si.drawImage(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+48+i*28, y+17, ((GFXAppearance)weaponAppearance).getImage());
				i++;
			}
			for (Appearance armorAppearance: unit.getAvailableArmorAppearances()){
				si.drawImage(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+48+i*28, y+17, ((GFXAppearance)armorAppearance).getImage());
			}
		} else {
			si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+48, y + 15, "Weight "+eitem.getWeight(), Color.WHITE);
			if (itemInfo.getPack() != 1){
				si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+48, y + 28, itemInfo.getPackDescription()+"x"+itemInfo.getPack(), Color.WHITE);
			}
			si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+48, y + 42, "$"+itemInfo.getPrice(), Color.WHITE);
		}
		
	}
	
	@Override
	public void drawMenuItem(SwingSystemInterface si, int x, int y, int index, boolean highlight) {
		// Get some info
		Image unitImage = ((GFXAppearance)item.getItem().getAppearance()).getImage();
		ExpeditionItem eitem = (ExpeditionItem)item.getItem();
		String itemDescription = item.getItem().getDescription();
		StoreItemInfo itemInfo = store.getBasicInfo((ExpeditionItem)item.getItem(), expedition);
		
		// Draw a cute border
		if (highlight){
			si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).setColor(ExpeditionOryxUI.ITEM_BOX_HIGHLIGHT_COLOR);
			si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).fillRect(x+1, y+1, ExpeditionOryxUI.STANDARD_ITEM_WIDTH - 2, 60 - 2);
			si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).fillRect(x+2, y+2, ExpeditionOryxUI.STANDARD_ITEM_WIDTH - 4, 60 - 4);
		} else {
			si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).setColor(ExpeditionOryxUI.ITEM_BOX_COLOR);
			si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).fillRect(x+1, y+1, ExpeditionOryxUI.STANDARD_ITEM_WIDTH - 2, 60 - 2);
			si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).fillRect(x+2, y+2, ExpeditionOryxUI.STANDARD_ITEM_WIDTH - 4, 60 - 4);
		}
		si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).setColor(ExpeditionOryxUI.ITEM_BOX_BORDER_COLOR);
		si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).drawRect(x+1, y+1, ExpeditionOryxUI.STANDARD_ITEM_WIDTH - 2, 60 - 2);
		si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).drawRect(x+2, y+2, ExpeditionOryxUI.STANDARD_ITEM_WIDTH - 4, 60 - 4);
		
		si.drawImage(ExpeditionOryxUI.UI_WIDGETS_LAYER, x + 12, y + 12, unitImage);
		
		// Unit status
		if (eitem instanceof ExpeditionUnit){
			ExpeditionUnit unit = (ExpeditionUnit)eitem;
			si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+48, y + 15, "ATK: "+unit.getAttack().getString(), Color.WHITE);
			si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+48, y + 28, "DEF: "+unit.getDefense().getString(), Color.WHITE);
			if (unit.isMonthlyWage()){
				si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+48, y + 42, "$"+itemInfo.getPrice()+" monthly", Color.WHITE);
			} else {
				si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+48, y + 42, "$"+itemInfo.getPrice()+", join and split", Color.WHITE);
			}
			si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+5, y + 55, (char)(CharKey.a + index + 1) + ". " + itemDescription, Color.WHITE);
		} else {
			si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+48, y + 15, "Weight "+eitem.getWeight(), Color.WHITE);
			if (itemInfo.getPack() != 1){
				si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+48, y + 28, itemInfo.getPackDescription()+"x"+itemInfo.getPack(), Color.WHITE);
			}
			si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+48, y + 42, "$"+itemInfo.getPrice(), Color.WHITE);
			si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+5, y + 55, (char)(CharKey.a + index + 1) + ". " +itemDescription, Color.WHITE);
		}
	}
	
	
}
