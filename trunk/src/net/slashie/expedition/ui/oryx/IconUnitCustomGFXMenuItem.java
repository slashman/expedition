package net.slashie.expedition.ui.oryx;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;

import net.slashie.expedition.domain.ExpeditionItem;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.ui.oryxUI.GFXAppearance;
import net.slashie.serf.ui.oryxUI.SwingSystemInterface;
import net.slashie.utils.ImageUtils;
import net.slashie.utils.swing.CustomGFXMenuItem;

public class IconUnitCustomGFXMenuItem implements CustomGFXMenuItem{
	private static final long serialVersionUID = 1L;
	
	private Equipment item;
	private boolean flip;
	private boolean down;

	public IconUnitCustomGFXMenuItem(Equipment item, boolean flip, boolean down) {
		this.item = item;
		this.flip = flip;
		this.down = down;
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
		ExpeditionItem eitem = (ExpeditionItem)item.getItem();
		String itemDescription = eitem.getFullDescription();
		
		int textWidth = (int) si.getTextWidth(ExpeditionOryxUI.UI_WIDGETS_LAYER, itemDescription);
		
		// Check if the tooltip will go outa the screen
		if (x + 30 + textWidth > si.getScreenWidth()){
			int diff = x + 30 + textWidth - si.getScreenWidth();
			x -= diff;
		}

		si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).setColor(ExpeditionOryxUI.ITEM_BOX_HIGHLIGHT_COLOR);
		si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).fillRect(x+26, y, textWidth + 10 + 2, 20 - 2);
		si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).setColor(ExpeditionOryxUI.ITEM_BOX_BORDER_COLOR);
		si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).drawRect(x+26+1, y+1, textWidth + 10 + 2 - 2, 20 - 2);
		si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).drawRect(x+26+2, y+2, textWidth + 10 + 2 - 4, 20 - 4);
		
		if (down){
			si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+ 25 + 5, y + 15, itemDescription, Color.WHITE);
		} else {
			si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+ 25 + 5, y + 15, itemDescription, Color.WHITE);
		}
		
	}
	
	@Override
	public void drawMenuItem(SwingSystemInterface si, int x, int y, int index, boolean highlight) {
		Image unitImage = ((GFXAppearance)((ExpeditionItem)item.getItem()).getDialogAppearance()).getImage();
		if (flip){
			Image img = ImageUtils.vFlip((BufferedImage)unitImage);
			si.drawImage(ExpeditionOryxUI.UI_WIDGETS_LAYER, x, y, img);
		} else {
			si.drawImage(ExpeditionOryxUI.UI_WIDGETS_LAYER, x, y, unitImage);

		}
		if (down){
			si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x, y + 24 + 12, "x"+item.getQuantity(), Color.WHITE);
		} else {
			si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+ 13, y + 28, "x"+item.getQuantity(), Color.WHITE);
		}
	}
	
	
}
