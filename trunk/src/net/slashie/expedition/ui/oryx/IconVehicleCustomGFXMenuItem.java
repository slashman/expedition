package net.slashie.expedition.ui.oryx;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;

import net.slashie.expedition.domain.Vehicle;
import net.slashie.serf.ui.oryxUI.GFXAppearance;
import net.slashie.serf.ui.oryxUI.SwingSystemInterface;
import net.slashie.utils.ImageUtils;
import net.slashie.utils.swing.CustomGFXMenuItem;

public class IconVehicleCustomGFXMenuItem implements CustomGFXMenuItem{
	private static final long serialVersionUID = 1L;
	
	private Vehicle vehicle;
	private boolean flip;

	public IconVehicleCustomGFXMenuItem(Vehicle vehicle, boolean flip) {
		this.vehicle = vehicle;
		this.flip = flip;
	}
	
	public Vehicle getVehicle(){
		return vehicle;
	}
	
	public String getGroupClassifier() {
		return vehicle.getGroupClassifier();
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
		String itemDescription = vehicle.getFullDescription();

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
		
		si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+ 25 + 5, y + 15, itemDescription, Color.WHITE);
		
	}
	
	@Override
	public void drawMenuItem(SwingSystemInterface si, int x, int y, int index, boolean highlight) {
		Image unitImage = ((GFXAppearance)vehicle.getAppearance()).getImage();
		if (flip){
			Image img = ImageUtils.vFlip((BufferedImage)unitImage);
			si.drawImage(ExpeditionOryxUI.UI_WIDGETS_LAYER, x, y, img);
		} else {
			si.drawImage(ExpeditionOryxUI.UI_WIDGETS_LAYER, x, y, unitImage);

		}
		int integrity = vehicle.getIntegrityPercent();
		Color integrityColor = Color.WHITE;
		if (integrity < 75){
			integrityColor = Color.RED;
		}
		si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x, y + 24 + 12, "["+integrity+"%]", integrityColor);
	}
	
	
}
