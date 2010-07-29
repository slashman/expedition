package net.slashie.expedition.ui.oryx.effects;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;


import net.slashie.libjcsi.ConsoleSystemInterface;
import net.slashie.serf.game.SworeGame;
import net.slashie.serf.ui.consoleUI.effects.CharDirectionalMissileEffect;
import net.slashie.serf.ui.consoleUI.effects.CharEffect;
import net.slashie.serf.ui.oryxUI.effects.GFXDirectionalMissileEffect;
import net.slashie.serf.ui.oryxUI.effects.GFXEffect;
import net.slashie.utils.ImageUtils;

public class GFXEffects {
	private BufferedImage IMG_EFFECTS;
	
	 {
		try {
			IMG_EFFECTS = ImageUtils.createImage("res/crl_effects.gif");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private GFXEffect [] effects = new GFXEffect[]{
		new GFXDirectionalMissileEffect("rainArrows", load8(0,14), 10),
	};

	public GFXEffect[] getEffects() {
		return effects;
	}
	
	private Image[] load8(int xpos, int ypos){
		return load(8, xpos, ypos);
	}
	
	private BufferedImage[] load(int frames, int xpos, int ypos) {
		BufferedImage[] ret = new BufferedImage[frames];
		for (int x = 0; x < frames; x++){
			try {
				ret[x] = ImageUtils.crearImagen(IMG_EFFECTS, (xpos+x)*32, ypos * 32, 32,32);
			} catch (Exception e){
				SworeGame.crash("Error loading effect", e);
			}
		}
		return ret;
	}
}
