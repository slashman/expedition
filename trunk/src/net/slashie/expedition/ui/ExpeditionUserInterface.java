package net.slashie.expedition.ui;

import java.util.List;

import net.slashie.expedition.domain.AssaultOutcome;
import net.slashie.expedition.domain.GoodType;
import net.slashie.expedition.domain.GoodsCache;
import net.slashie.expedition.domain.LandingParty;
import net.slashie.expedition.domain.Store;
import net.slashie.expedition.domain.Town;
import net.slashie.expedition.town.Building;
import net.slashie.expedition.world.Weather;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.ui.Appearance;

public interface ExpeditionUserInterface {
	public void launchStore(Store store);
	public boolean depart();
	
	public void transferFromCache(String prompt, GoodType preselectedGoodType, GoodsCache from);
	public void transferFromExpedition(GoodsCache toCache);
	public void transferFromExpedition(GoodsCache ship, int destinationMinUnits);
	
	

	public void showBlockingMessage(String message);
	public void showBlockingMessage(String message, boolean keepMessage);
	
	public static final String[] months = new String[]{
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
	public List<Equipment> selectItemsFromExpedition(String prompt, String verb, Appearance destinationAppearance);
	public boolean promptUnitList(List<Equipment> unitList, String title, String prompt);
	public List<Building> createBuildingPlan();
	public void showCityInfo(Town town);
	public void afterTownAction();
	public LandingParty selectLandingParty();
	public void notifyWeatherChange(Weather weather);
}
