package net.slashie.expedition.domain;

import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.serf.ui.Appearance;
import net.slashie.serf.ui.AppearanceFactory;

public class SeaPseudoCache extends GoodsCache{
	public SeaPseudoCache(ExpeditionGame game) {
		super();
	}
	
	public String getDescription() {
		return "Sea";
	}
	
	@Override
	public boolean isInfiniteCapacity() {
		return false;
	}

	@Override
	public Appearance getAppearance() {
		return AppearanceFactory.getAppearanceFactory().getAppearance("WATER");
	}
	
	@Override
	public int getFoodDays() {
		return -1;
	}
	
	@Override
	public boolean requiresUnitsToContainItems() {
		return false;
	}
}
