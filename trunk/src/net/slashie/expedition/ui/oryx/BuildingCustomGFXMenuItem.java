package net.slashie.expedition.ui.oryx;

import java.awt.Color;
import java.awt.Image;

import net.slashie.expedition.town.Building;
import net.slashie.libjcsi.CharKey;
import net.slashie.serf.ui.AppearanceFactory;
import net.slashie.serf.ui.oryxUI.GFXAppearance;
import net.slashie.serf.ui.oryxUI.SwingSystemInterface;
import net.slashie.utils.swing.CustomGFXMenuItem;

public class BuildingCustomGFXMenuItem implements CustomGFXMenuItem{
	private static final long serialVersionUID = 1L;
	
	private Building building;
	private int quantity;
	
	public BuildingCustomGFXMenuItem(Building building) {
		this.building = building;
	}
	
	public Building getBuilding(){
		return building;
	}
	
	public Image getMenuImage() {
		//TODO: Building Images
		return null;
	}
	
	public String getMenuDetail() {
		return "  "+building.getLongDescription();
	}

	public String getMenuDescription() {
		return "["+quantity + "] " + building.getDescription() + " ("+(building.getWoodCost() * quantity)+" wood, "+(building.getBuildTimeCost()*quantity)+" workpower)";
	}
	
	public String getGroupClassifier() {
		return "DITTO";
	}

	public void add() {
		quantity++;
	}
	
	public void remove() {
		if (quantity > 0)
			quantity--;
	}

	public int getQuantity() {
		return quantity;
	}
	
	@Override
	public void drawMenuItem(SwingSystemInterface si, int x, int y, int index, boolean highlight) {
		// Draw a cute border
		si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).setColor(ExpeditionOryxUI.ITEM_BOX_BORDER_COLOR);
		si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).drawRect(x+1, y+1, ExpeditionOryxUI.STANDARD_ITEM_WIDTH - 2, ExpeditionOryxUI.STANDARD_ITEM_HEIGHT - 2);
		si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).drawRect(x+2, y+2, ExpeditionOryxUI.STANDARD_ITEM_WIDTH - 4, ExpeditionOryxUI.STANDARD_ITEM_HEIGHT - 4);
		
		si.drawImage(ExpeditionOryxUI.UI_WIDGETS_LAYER, x + 12, y + 12, ((GFXAppearance)AppearanceFactory.getAppearanceFactory().getAppearance("SPAIN_HOUSE")).getImage());// TODO: Draw building
		si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+5, y + 55, (char)(CharKey.a+index+1)+". " +quantity + " " + building.getDescription(), Color.WHITE);
		
		si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+48, y + 15, "Wood: " + (building.getWoodCost() * quantity), Color.WHITE);
		si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+48, y + 28, "Work: " + (building.getBuildTimeCost()*quantity), Color.WHITE);
	}
	
	@Override
	public void drawTooltip(SwingSystemInterface si, int x, int y, int index) {
		// Draw a cute border
		si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).setColor(ExpeditionOryxUI.ITEM_BOX_HIGHLIGHT_COLOR);
		si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).fillRect(x+1, y+1, ExpeditionOryxUI.STANDARD_ITEM_WIDTH - 2, ExpeditionOryxUI.STANDARD_ITEM_HEIGHT - 2);
		si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).setColor(ExpeditionOryxUI.ITEM_BOX_BORDER_COLOR);
		si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).drawRect(x+1, y+1, ExpeditionOryxUI.STANDARD_ITEM_WIDTH - 2, ExpeditionOryxUI.STANDARD_ITEM_HEIGHT - 2);
		si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).drawRect(x+2, y+2, ExpeditionOryxUI.STANDARD_ITEM_WIDTH - 4, ExpeditionOryxUI.STANDARD_ITEM_HEIGHT - 4);
		
		si.drawImage(ExpeditionOryxUI.UI_WIDGETS_LAYER, x + 12, y + 12, ((GFXAppearance)AppearanceFactory.getAppearanceFactory().getAppearance("SPAIN_HOUSE")).getImage());// TODO: Draw building
		si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+5, y + 55, (char)(CharKey.a+index+1)+". " +quantity + " " + building.getDescription(), Color.WHITE);
		
		si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+48, y + 15, "Wood: " + (building.getWoodCost() * quantity), Color.WHITE);
		si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+48, y + 28, "Work: " + (building.getBuildTimeCost()*quantity), Color.WHITE);
		
	}
	
	@Override
	public boolean showTooltip() {
		return true;
	}
}
