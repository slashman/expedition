package net.slashie.expedition.ui.oryx;

import java.awt.Color;
import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

import net.slashie.serf.ui.oryxUI.Assets;
import net.slashie.serf.ui.oryxUI.SwingSystemInterface;
import net.slashie.utils.swing.CleanButton;

public class ExpeditionCleanButton extends CleanButton{
	private static final long serialVersionUID = 1L;
	private static transient SwingSystemInterface si; 
	
	private static Map<Integer, Image> buttonImages = new HashMap<Integer, Image>();
	private static Map<Integer, Image> hoverButtonImages = new HashMap<Integer, Image>();
	
	public static void init(SwingSystemInterface si, Assets assets){
		ExpeditionCleanButton.si = si;
		buttonImages.put(2, assets.getImageAsset("BTN_SIZE2"));
		buttonImages.put(3, assets.getImageAsset("BTN_SIZE3"));
		buttonImages.put(4, assets.getImageAsset("BTN_SIZE4"));
		buttonImages.put(8, assets.getImageAsset("BTN_SIZE8"));
		hoverButtonImages.put(2, assets.getImageAsset("BTN_SIZE2_HOVER"));
		hoverButtonImages.put(3, assets.getImageAsset("BTN_SIZE3_HOVER"));
		hoverButtonImages.put(4, assets.getImageAsset("BTN_SIZE4_HOVER"));
		hoverButtonImages.put(8, assets.getImageAsset("BTN_SIZE8_HOVER"));
		
	}

	public ExpeditionCleanButton(int blockSize, String text) {
		super(buttonImages.get(blockSize), ExpeditionOryxUI.HAND_CURSOR, text);
		setFont(si.getFont(ExpeditionOryxUI.UI_WIDGETS_LAYER));
		setForeground(Color.WHITE);
		setHover(hoverButtonImages.get(blockSize));
	}

}
