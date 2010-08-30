package net.slashie.expedition.domain;

public interface UnitContainer {
	void reduceUnits(ExpeditionUnit unit, int quantity);
	void addUnits(ExpeditionUnit unit, int quantity);
}
