package net.slashie.expedition.world;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.NonPrincipalExpedition;
import net.slashie.serf.action.Actor;
import net.slashie.serf.level.AbstractFeature;

/**
 * Represents a settlement which is not owned
 * by the player expedition
 * @author Slash
 *
 */
@SuppressWarnings("serial")
public class Settlement extends AbstractFeature{
	private String name;
	public Settlement(String name, String appearanceId) {
		this.name = name;
		setAppearanceId(appearanceId);
	}
	
	@Override
	public String getClassifierID() {
		return "SETTLEMENT";
	}

	@Override
	public String getDescription() {
		return name;
	}
	
	public void onStep(Actor a) {
		if (a instanceof Expedition && !(a instanceof NonPrincipalExpedition)){
			// Show a menu with options
		}
	}
	
	@Override
	public boolean isSolid() {
		return false;
	}

}
