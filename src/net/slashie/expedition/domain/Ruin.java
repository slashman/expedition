package net.slashie.expedition.domain;

import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.world.Culture;
import net.slashie.serf.action.Actor;
import net.slashie.serf.ui.Appearance;
import net.slashie.serf.ui.AppearanceFactory;

@SuppressWarnings("serial")
public class Ruin extends Actor{
	private Culture culture;
	private boolean discovered;
	
	public Ruin(ExpeditionGame game, Culture culture) {
		this.culture = culture;
		discovered = false;
		setAppearanceId("LLAMA");
	}
	
	public Appearance getAppearance(){
		return AppearanceFactory.getAppearanceFactory().getAppearance(getAppearanceId());
	}
	
	@Override
	public String getDescription() {
		return "An ancient Ruin of " + culture.getName() + " culture! ";
	}

	@Override
	public String getClassifierID() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public boolean isDiscovered() {
		return discovered;
	}

	public void setDiscovered() {
		this.discovered = true;
	}
}
