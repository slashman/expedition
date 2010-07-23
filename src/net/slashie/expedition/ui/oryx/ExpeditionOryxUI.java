package net.slashie.expedition.ui.oryx;

import net.slashie.serf.action.Actor;
import net.slashie.serf.ui.oryxUI.GFXUserInterface;

public class ExpeditionOryxUI extends GFXUserInterface{

	@Override
	public void showDetailedInfo(Actor a) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getQuitPrompt() {
		return "Quit?";
	}

	@Override
	public String getQuitMessage() {
		return "Quit?";
	}

	@Override
	public boolean promptChat(String message) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void showInventory() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int switchChat(String prompt, String... options) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String inputBox(String prompt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processHelp() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMusicOn() {
		// TODO Auto-generated method stub
		
	}
	
}
