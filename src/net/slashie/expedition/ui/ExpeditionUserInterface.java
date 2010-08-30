package net.slashie.expedition.ui;

import net.slashie.expedition.domain.AssaultOutcome;
import net.slashie.expedition.domain.GoodsCache;
import net.slashie.expedition.domain.Store;

public interface ExpeditionUserInterface {
	public void launchStore(Store store);
	public boolean depart();
	public void transferFromCache(GoodsCache ship);
	
	public void transferFromExpedition(GoodsCache ship);
	public void transferFromExpedition(GoodsCache ship, int minUnits);

	public void showBlockingMessage(String message);
	
	static final String[] months = new String[]{
			"Janvary",
			"Febrvary",
			"March",
			"April",
			"May",
			"Jvne",
			"Jvly",
			"Avgvst",
			"September",
			"October",
			"November",
			"December"
		};
	

	public void showBattleResults(
			AssaultOutcome attackerRangedAttackOutcome,
			AssaultOutcome defenderRangedAttackOutcome,
			AssaultOutcome[] mountedAttackOutcome,
			AssaultOutcome[] meleeAttackOutcome);
}
