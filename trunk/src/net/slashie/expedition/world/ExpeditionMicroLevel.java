package net.slashie.expedition.world;

import net.slashie.util.Pair;

public class ExpeditionMicroLevel extends ExpeditionLevel{
	private Pair<Integer, Integer> location;
	public void setLocation(Pair<Integer, Integer> location) {
		this.location = location;
	}
	@Override
	public Pair<Integer, Integer> getLocation() {
		return location;
	}
}
