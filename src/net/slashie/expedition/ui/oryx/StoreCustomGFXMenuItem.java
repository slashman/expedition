package net.slashie.expedition.ui.oryx;

import java.awt.Color;
import java.awt.Image;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.ExpeditionItem;
import net.slashie.expedition.domain.ExpeditionUnit;
import net.slashie.expedition.domain.Store;
import net.slashie.expedition.domain.StoreItemInfo;
import net.slashie.expedition.domain.Vehicle;
import net.slashie.libjcsi.CharKey;
import net.slashie.serf.ui.Appearance;
import net.slashie.serf.ui.oryxUI.GFXAppearance;
import net.slashie.serf.ui.oryxUI.SwingSystemInterface;

public class StoreCustomGFXMenuItem extends CacheCustomGFXMenuItem{
	private static final long serialVersionUID = 1L;
	
	private ExpeditionItem item;
	private Store store;
	private Expedition expedition;

	public StoreCustomGFXMenuItem(ExpeditionItem item, Store store, Expedition expedition) {
		super(item, store, expedition);
		this.item = item;
		this.store = store;
		this.expedition = expedition;
	}
	
	public ExpeditionItem getEquipment(){
		return item;
	}
	
	public String getGroupClassifier() {
		return item.getGroupClassifier();
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
		Image unitImage = ((GFXAppearance)item.getDialogAppearance()).getImage();
		String itemDescription = item.getDescription();
		StoreItemInfo itemInfo = store.getBasicInfo(item, expedition);
	
		// Draw a cute border
		if (item instanceof ExpeditionUnit){
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
		if (item instanceof ExpeditionUnit){
			ExpeditionUnit unit = (ExpeditionUnit)item;
			si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+48, y + 15, unit.getLongDescription(), Color.WHITE);
			int i = 0;
			for (Appearance weaponAppearance: unit.getAvailableWeaponAppearances()){
				si.drawImage(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+48+i*28, y+17, ((GFXAppearance)weaponAppearance).getImage());
				i++;
			}
			for (Appearance armorAppearance: unit.getAvailableArmorAppearances()){
				si.drawImage(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+48+i*28, y+17, ((GFXAppearance)armorAppearance).getImage());
				i++;
			}
		} else if (item instanceof Vehicle){
			Vehicle vehicle = (Vehicle)item;
			si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+48, y + 15, vehicle.getName(), Color.WHITE);
			si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+48, y + 28, "Int: "+vehicle.getResistance()+"/"+vehicle.getMaxResistance(), Color.WHITE);
			si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+48, y + 42, "Cap: "+vehicle.getCarryCapacity(), Color.WHITE);
			si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+5, y + 55, (char)(CharKey.a + index + 1) + ". " + itemDescription, Color.WHITE);
		} else {
			si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+48, y + 15, "Weight "+item.getWeight(), Color.WHITE);
			if (itemInfo != null){
				si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+48, y + 42, "$"+itemInfo.getPrice(), Color.WHITE);
			} else {
				si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+48, y + 42, "Not interested", Color.WHITE);
			}
			si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+5, y + 55, (char)(CharKey.a + index + 1) + ". " +itemDescription, Color.WHITE);
		}
		
	}
	
	@Override
	public void drawMenuItem(SwingSystemInterface si, int x, int y, int index, boolean highlight) {
		// Get some info
		Image unitImage = ((GFXAppearance)item.getDialogAppearance()).getImage();
		String itemDescription = item.getDescription();
		StoreItemInfo itemInfo = store.getBasicInfo(item, expedition);
		
		// Draw a cute border
		if (highlight){
			si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).setColor(ExpeditionOryxUI.ITEM_BOX_HIGHLIGHT_COLOR);
			si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).fillRect(x+1, y+1, ExpeditionOryxUI.STANDARD_ITEM_WIDTH - 2, 60 - 2);
			si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).fillRect(x+2, y+2, ExpeditionOryxUI.STANDARD_ITEM_WIDTH - 4, 60 - 4);
		} else {
			if (itemInfo == null){
				si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).setColor(Color.DARK_GRAY);
			} else {
				si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).setColor(ExpeditionOryxUI.ITEM_BOX_COLOR);
			}
			
			si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).fillRect(x+1, y+1, ExpeditionOryxUI.STANDARD_ITEM_WIDTH - 2, 60 - 2);
			si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).fillRect(x+2, y+2, ExpeditionOryxUI.STANDARD_ITEM_WIDTH - 4, 60 - 4);
		}
		si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).setColor(ExpeditionOryxUI.ITEM_BOX_BORDER_COLOR);
		si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).drawRect(x+1, y+1, ExpeditionOryxUI.STANDARD_ITEM_WIDTH - 2, 60 - 2);
		si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).drawRect(x+2, y+2, ExpeditionOryxUI.STANDARD_ITEM_WIDTH - 4, 60 - 4);
		
		si.drawImage(ExpeditionOryxUI.UI_WIDGETS_LAYER, x + 12, y + 12, unitImage);
		
		// Unit status
		if (item instanceof ExpeditionUnit){
			ExpeditionUnit unit = (ExpeditionUnit)item;
			si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+48, y + 15, "ATK: "+unit.getAttack().getString(), Color.WHITE);
			si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+48, y + 28, "DEF: "+unit.getDefense().getString(), Color.WHITE);
			if (itemInfo != null){
				switch (unit.getContractType()){
				case JOIN_AND_SPLIT:
					si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+48, y + 42, "$"+itemInfo.getPrice()+", for the spoils", Color.WHITE);
					break;
				case LIFETIME:
					si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+48, y + 42, "$"+itemInfo.getPrice()+", lifetime", Color.WHITE);
					break;
				case MONTHLY:
					si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+48, y + 42, "$"+itemInfo.getPrice()+" monthly", Color.WHITE);
					break;
				}
			} else {
				si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+48, y + 42, "Not interested", Color.WHITE);
			}
			si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+5, y + 55, (char)(CharKey.a + index + 1) + ". " + itemDescription, Color.WHITE);
		} else if (item instanceof Vehicle){
			Vehicle vehicle = (Vehicle)item;
			si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+48, y + 15, vehicle.getName(), Color.WHITE);
			if (itemInfo != null){
				si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+48, y + 42, "$"+itemInfo.getPrice(), Color.WHITE);
			} else {
				si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+48, y + 42, "Not interested", Color.WHITE);
			}
			si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+5, y + 55, (char)(CharKey.a + index + 1) + ". " + itemDescription, Color.WHITE);
		} else {
			si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+48, y + 15, "Weight "+item.getWeight(), Color.WHITE);
			if (itemInfo != null){
				si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+48, y + 42, "$"+itemInfo.getPrice(), Color.WHITE);
			} else {
				si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+48, y + 42, "Not interested", Color.WHITE);
			}
			si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+5, y + 55, (char)(CharKey.a + index + 1) + ". " +itemDescription, Color.WHITE);
		}
	}
}
