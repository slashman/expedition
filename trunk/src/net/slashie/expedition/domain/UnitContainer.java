package net.slashie.expedition.domain;

import net.slashie.expedition.domain.Expedition.DeathCause;

public interface UnitContainer {
	void reduceUnits(ExpeditionUnit unit, int quantity, DeathCause deathCause);
	void addUnits(ExpeditionUnit unit, int quantity);
}
