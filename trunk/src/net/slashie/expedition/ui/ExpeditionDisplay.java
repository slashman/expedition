package net.slashie.expedition.ui;

import java.io.File;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.game.ExpeditionGame;

public abstract class ExpeditionDisplay  {
	public static ExpeditionDisplay thus;
	/**
	 * Shows the title screen
	 * @return A numeric option chosen by the user which is interpreted by the ExpeditionGame
	 */
	public abstract int showTitleScreen();
	
	/**
	 * Shows the saved games and allow the user to pick one
	 * @param saves An array of files representing save games
	 * @return The index of the chosen save game or -1 to cancel
	 */
	public abstract int showSavedGames(File[] saves);
	
	public abstract void showIntro(Expedition expedition);
	
	public abstract void showHelp();
	
	public abstract Expedition createExpedition(ExpeditionGame game);

}
