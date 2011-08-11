package net.slashie.expedition.game;

import net.slashie.serf.sound.STMusicManagerNew;

public class ExpeditionMusicManager {
	private static STMusicManagerNew weatherMusicManager;
	
	public static void init(){
		weatherMusicManager = new STMusicManagerNew();
	}

	public static void playTune(String tune){
		if (tune.startsWith("WEATHER")){
			STMusicManagerNew.thus.stopMusic();
			weatherMusicManager.playKey(tune);
		} else {
			weatherMusicManager.stopMusic();
			STMusicManagerNew.thus.playKey(tune);
		}
	}

	public static void stopWeather() {
		weatherMusicManager.stopMusic();
	}

	public static void addTune(String tuneKey, String file) {
		STMusicManagerNew.thus.addMusic(tuneKey, file);
		weatherMusicManager.addMusic(tuneKey, file);
	}

	public static void setEnabled(boolean enabled) {
		STMusicManagerNew.thus.setEnabled(enabled);
		weatherMusicManager.setEnabled(enabled);
	}
}
