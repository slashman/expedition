package net.slashie.expedition.ui.oryx;

import java.awt.Color;
import java.awt.Image;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import net.slashie.serf.ui.UserInterface;
import net.slashie.serf.ui.oryxUI.SwingSystemInterface;
import net.slashie.utils.PropertyFilters;
import net.slashie.utils.swing.CleanButton;

public class ExpeditionCleanButton extends CleanButton{
	private static final long serialVersionUID = 1L;
	private static transient SwingSystemInterface si; 
	
	private static Map<Integer, Image> buttonImages = new HashMap<Integer, Image>();
	private static Map<Integer, Image> hoverButtonImages = new HashMap<Integer, Image>();
	
	public static void init(SwingSystemInterface si, Properties uiProperties){
		ExpeditionCleanButton.si = si;
		try {
			buttonImages.put(2, PropertyFilters.getImage(uiProperties.getProperty("IMG_UI"), uiProperties.getProperty("BTN_SIZE2_BOUNDS")));
			buttonImages.put(3, PropertyFilters.getImage(uiProperties.getProperty("IMG_UI"), uiProperties.getProperty("BTN_SIZE3_BOUNDS")));
			buttonImages.put(4, PropertyFilters.getImage(uiProperties.getProperty("IMG_UI"), uiProperties.getProperty("BTN_SIZE4_BOUNDS")));
			buttonImages.put(8, PropertyFilters.getImage(uiProperties.getProperty("IMG_UI"), uiProperties.getProperty("BTN_SIZE8_BOUNDS")));
			hoverButtonImages.put(2, PropertyFilters.getImage(uiProperties.getProperty("IMG_UI"), uiProperties.getProperty("BTN_SIZE2_HOVER_BOUNDS")));
			hoverButtonImages.put(3, PropertyFilters.getImage(uiProperties.getProperty("IMG_UI"), uiProperties.getProperty("BTN_SIZE3_HOVER_BOUNDS")));
			hoverButtonImages.put(4, PropertyFilters.getImage(uiProperties.getProperty("IMG_UI"), uiProperties.getProperty("BTN_SIZE4_HOVER_BOUNDS")));
			hoverButtonImages.put(8, PropertyFilters.getImage(uiProperties.getProperty("IMG_UI"), uiProperties.getProperty("BTN_SIZE8_HOVER_BOUNDS")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ExpeditionCleanButton(int blockSize, String text) {
		super(buttonImages.get(blockSize), ((ExpeditionOryxUI)UserInterface.getUI()).HAND_CURSOR, text);
		setFont(si.getFont());
		setForeground(Color.WHITE);
		setHover(hoverButtonImages.get(blockSize));
	}

}
