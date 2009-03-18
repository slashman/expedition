package net.slashie.expedition.ui.console.effects;

import java.awt.Color;
import java.util.Vector;

import net.slashie.libjcsi.ConsoleSystemInterface;
import net.slashie.serf.ui.consoleUI.effects.CharAnimatedMissileEffect;
import net.slashie.serf.ui.consoleUI.effects.CharDirectionalMissileEffect;
import net.slashie.serf.ui.consoleUI.effects.CharEffect;

public class CharEffects {
	private CharEffect [] effects = new CharEffect[]{
		new CharDirectionalMissileEffect("rainArrows", "\\|/--/|\\", ConsoleSystemInterface.WHITE, 50),

	};

	public CharEffect[] getEffects() {
		return effects;
	}

}
