package net.slashie.expedition.domain;

import net.slashie.expedition.game.ExpeditionGame;

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

}
