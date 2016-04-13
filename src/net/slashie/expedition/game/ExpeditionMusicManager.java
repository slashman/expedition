package net.slashie.expedition.game;

import net.slashie.expedition.world.ExpeditionMacroLevel;
import net.slashie.expedition.world.Weather;
import net.slashie.serf.level.AbstractLevel;
import net.slashie.serf.sound.STMusicManagerNew;
import net.slashie.serf.ui.UserInterface;
import net.slashie.serf.ui.UserInterface.SoundCycle;

public class ExpeditionMusicManager
{
	private static STMusicManagerNew weatherMusicManager;

	public static void init()
	{
		weatherMusicManager = new STMusicManagerNew();
	}

	private static boolean playingWeather;

	public static void playTune(String tune)
	{
		if (tune.startsWith("WEATHER"))
		{
			STMusicManagerNew.thus.stopMusic();
			weatherMusicManager.playKey(tune);
			playingWeather = true;
		}
		else
		{
			if (!playingWeather)
			{
				stopWeather();
				if (STMusicManagerNew.thus != null)
				{
					STMusicManagerNew.thus.playKey(tune);
				}
			}
			else
			{
				AbstractLevel level = ExpeditionGame.getCurrentGame().getExpedition().getLevel();
				if (level instanceof ExpeditionMacroLevel)
				{
					Weather weather = ((ExpeditionMacroLevel) level).getWeather();
					if (weather.getMusicKey() != null)
					{
						// Ignore the tune since weather is better
					}
					else
					{
						stopWeather();
						STMusicManagerNew.thus.playKey(tune);

					}
				}
			}
		}
	}

	public static void stopWeather()
	{
		if (weatherMusicManager != null)
		{
			weatherMusicManager.stopMusic();
		}
		playingWeather = false;
	}

	public static void addTune(String tuneKey, String file)
	{
		STMusicManagerNew.thus.addMusic(tuneKey, file);
		weatherMusicManager.addMusic(tuneKey, file);
	}

	public static void setEnabled(boolean enabled)
	{
		STMusicManagerNew.thus.setEnabled(enabled);
		weatherMusicManager.setEnabled(enabled);
	}

	public static void setSoundCycle(SoundCycle cycle)
	{
		UserInterface.getUI().setSoundCycle(cycle);
		STMusicManagerNew.thus.setVolume(cycle.getGain());
		weatherMusicManager.setVolume(cycle.getGain());
	}
}
