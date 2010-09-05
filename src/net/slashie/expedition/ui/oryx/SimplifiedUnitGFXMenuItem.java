package net.slashie.expedition.ui.oryx;

import net.slashie.expedition.domain.ExpeditionUnit;
import net.slashie.serf.game.Equipment;

public class SimplifiedUnitGFXMenuItem extends UnitGFXMenuItem {

	public SimplifiedUnitGFXMenuItem(Equipment e) {
		super(e);
	}
	
	public String getMenuDescription() {
		return ((ExpeditionUnit)e.getItem()).getDescription();
	}

}
