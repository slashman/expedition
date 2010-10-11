package net.slashie.expedition.ui;

import java.util.List;

import net.slashie.expedition.action.BuildBuildings;
import net.slashie.expedition.domain.AssaultOutcome;
import net.slashie.expedition.domain.GoodsCache;
import net.slashie.expedition.domain.Store;
import net.slashie.expedition.domain.Town;
import net.slashie.expedition.town.Building;
import net.slashie.serf.game.Equipment;

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
			List<Equipment> originalAttackingUnits, List<Equipment> originalDefendingUnits, String battleTitle,
			AssaultOutcome attackerRangedAttackOutcome,
			AssaultOutcome defenderRangedAttackOutcome,
			AssaultOutcome[] mountedAttackOutcome,
			AssaultOutcome[] meleeAttackOutcome, int attackerScore, int defenderScore);
	
	public void showBattleScene(
			String battleTitle, 
			List<Equipment> attackingUnits,
			List<Equipment> defendingUnits);
	public List<Equipment> selectItemsFromExpedition(String prompt, String verb);
	public boolean promptUnitList(List<Equipment> unitList, String title, String prompt);
	public List<Building> createBuildingPlan();
	public void showCityInfo(Town town);
	public void afterTownAction();
}
