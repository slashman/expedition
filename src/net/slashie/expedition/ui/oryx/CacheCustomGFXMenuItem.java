package net.slashie.expedition.ui.oryx;

import java.awt.Color;
import java.awt.Image;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.ExpeditionItem;
import net.slashie.expedition.domain.GoodsCache;
import net.slashie.expedition.domain.ItemContainer;
import net.slashie.libjcsi.CharKey;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.ui.oryxUI.GFXAppearance;
import net.slashie.serf.ui.oryxUI.SwingSystemInterface;
import net.slashie.utils.swing.CustomGFXMenuItem;

public class CacheCustomGFXMenuItem implements CustomGFXMenuItem{
	private static final long serialVersionUID = 1L;
	
	private Equipment item;

	private ItemContainer from;
	private ItemContainer to;

	public CacheCustomGFXMenuItem(Equipment item, ItemContainer from, ItemContainer to) {
		this.item = item;
		this.to = to;
		this.from = from;
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
	public void drawTooltip(SwingSystemInterface si, int x, int y) {

		
	}
	
	@Override
	public void drawMenuItem(SwingSystemInterface si, int x, int y, int index, boolean highlight) {
		// Get some info
		Image unitImage = ((GFXAppearance)item.getItem().getAppearance()).getImage();
		String itemDescription = item.getItem().getDescription();
		int inventory = item.getQuantity();
		String current = to.getItemCountBasic(item.getItem().getFullID())+"";

		// Draw a cute border
		if (highlight){
			si.getGraphics2D().setColor(new Color(82,79,34));
			si.getGraphics2D().fillRect(x+1, y+1, 200 - 2, 60 - 2);
			si.getGraphics2D().fillRect(x+2, y+2, 200 - 4, 60 - 4);
		} 
		si.getGraphics2D().setColor(OryxExpeditionDisplay.COLOR_BOLD);
		si.getGraphics2D().drawRect(x+1, y+1, 200 - 2, 60 - 2);
		si.getGraphics2D().drawRect(x+2, y+2, 200 - 4, 60 - 4);
		
		si.drawImage(x + 12, y + 12, unitImage);
		
		si.printAtPixel(x+48, y + 15, from.getDescription()+": "+inventory, Color.WHITE);
		si.printAtPixel(x+48, y + 28, to.getDescription()+": "+current, Color.WHITE);
		si.printAtPixel(x+5, y + 55, (char)(CharKey.a + index + 1) + ". " +itemDescription, Color.WHITE);
	}
	
	
}
