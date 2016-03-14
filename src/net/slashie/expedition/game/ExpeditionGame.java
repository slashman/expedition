package net.slashie.expedition.game;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import net.ck.expedition.model.test.ExpeditionProperties;
import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.Expedition.MovementMode;
import net.slashie.expedition.domain.FriarTutorial;
import net.slashie.expedition.domain.Town;
import net.slashie.expedition.level.GlobeMapModel;
import net.slashie.expedition.ui.ExpeditionDisplay;
import net.slashie.expedition.ui.ExpeditionUserInterface;
import net.slashie.expedition.world.ExpeditionLevel;
import net.slashie.expedition.world.ExpeditionMicroLevel;
import net.slashie.expedition.world.FoodConsumer;
import net.slashie.expedition.world.GlobeFOV;
import net.slashie.expedition.world.LevelMaster;
import net.slashie.expedition.world.SettlementLevel;
import net.slashie.expedition.worldGen.WorldGenerator;
import net.slashie.serf.action.Actor;
import net.slashie.serf.fov.FOV;
import net.slashie.serf.game.Player;
import net.slashie.serf.game.SworeGame;
import net.slashie.serf.level.AbstractLevel;
import net.slashie.serf.level.LevelMetaData;
import net.slashie.serf.ui.UserInterface;

@SuppressWarnings("serial")
public class ExpeditionGame extends SworeGame
{
	
	final static Logger logger = Logger.getRootLogger();
	public static class ExpeditionVersion
	{
		public ExpeditionVersion(String code, int year, int month, int date)
		{
			this.code = code;
			Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			c.set(year, month, date);
			this.date = c.getTime();
		}

		@Override
		public boolean equals(Object arg0)
		{
			return ((ExpeditionVersion) arg0).getCode().equals(code);
		}

		String code;
		Date date;

		public String getCode()
		{
			return code;
		}

		DateFormat sdf = DateFormat.getDateInstance(DateFormat.MEDIUM);

		public String getFormattedDate()
		{
			return sdf.format(date);
		}

	}

	private static final ExpeditionVersion THIS_VERSION = new ExpeditionVersion("0.5", 2012, 4, 21);

	private static ExpeditionGame currentGame;
	private int lastExpeditionId = 1;
	private Calendar currentTime;

	@Override
	public void afterPlayerAction()
	{
		if (getExpedition().getLocation() instanceof ExpeditionMicroLevel)
		{

		}
		else
		{
			getExpedition().getLocation().elapseTime(getExpedition().getLastActionTimeCost());
		}

	}

	public void monthChange()
	{
		List<Town> towns = ((Expedition) getPlayer()).getTowns();
		for (Town town : towns)
		{
			town.tryGrowing();
		}
	}

	private List<FoodConsumer> foodConsumers = new ArrayList<FoodConsumer>();
	private List<SettlementLevel> settlementLevels = new ArrayList<SettlementLevel>();

	public void addFoodConsumer(FoodConsumer foodConsumer)
	{
		foodConsumers.add(foodConsumer);
	}

	@Override
	public void beforeGameStart()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void beforePlayerAction()
	{
		getExpedition().checkDeath();
	}

	@Override
	public AbstractLevel createLevel(LevelMetaData levelMetadata)
	{
		return LevelMaster.createLevel(levelMetadata.getLevelID(), getExpedition());
	}

	@Override
	public Player generatePlayer(int gameType, SworeGame game)
	{
		return ExpeditionDisplay.thus.createExpedition((ExpeditionGame) game);
	}

	@Override
	public String getDeathMessage()
	{
		switch (getCurrentGame().getExpedition().getDeathCause())
		{
		case DEATH_BY_STARVATION:
			return "Your expedition has perished by hunger..";
		case DEATH_BY_DROWNING:
			return "Your expedition has drowned in the seas..";
		case DEATH_BY_SLAYING:
			return "Your expedition has been slain..";
		}
		return "Your expedition has perished..";
	}

	@Override
	public String getFirstMessage(Actor player)
	{
		return "Welcome!";
	}

	@Override
	public void onGameOver()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onGameResume()
	{
		currentGame = this;
		WorldGenerator.addAnimalNests();
		WorldGenerator.addBotanyCrops();
		ExpeditionLevel expeditionLevel = (ExpeditionLevel) getExpedition().getLevel();
		expeditionLevel.enterLevel();
		((ExpeditionUserInterface) UserInterface.getUI()).notifyWeatherChange(expeditionLevel.getWeather());
		((ExpeditionUserInterface) UserInterface.getUI()).reactivate();
		expeditionLevel.playMusic();
	}

	@Override
	public void onGameStart(int gameType)
	{
		logger.debug("on game start");
		currentGame = this;
		WorldGenerator.addAnimalNests();
		WorldGenerator.addBotanyCrops();

		if (ExpeditionProperties.isDebug())
		{

		}
		else
		{
			ExpeditionDisplay.thus.showIntro(getExpedition());
		}
		loadMetadata();
		loadLevel("SPAIN_CASTLE");
		((ExpeditionUserInterface) UserInterface.getUI()).reactivate();

		if (ExpeditionProperties.isDebug())
		{
			
		}
		else
		{
			ExpeditionDisplay.thus.showIntro(getExpedition());
		}
		FriarTutorial.activate(0);
		setGameTime(3, 8, 1492, 13, 0);
	}

	public static void setCurrentGame(ExpeditionGame currentGame)
	{
		ExpeditionGame.currentGame = currentGame;
	}

	private void loadMetadata()
	{
		LevelMetaData md = null;
		md = new LevelMetaData("PALOS");
		addMetaData("PALOS", md);
		md = new LevelMetaData("SPAIN_CASTLE");
		addMetaData("SPAIN_CASTLE", md);
		md = new LevelMetaData("WORLD");
		addMetaData("WORLD", md);

	}

	public void setGameTime(int day, int month, int year, int hours, int minutes)
	{
		currentTime = Calendar.getInstance();
		currentTime.set(Calendar.YEAR, year);
		currentTime.set(Calendar.MONTH, month - 1);
		currentTime.set(Calendar.DATE, day);
		currentTime.set(Calendar.HOUR_OF_DAY, hours);
		currentTime.set(Calendar.MINUTE, minutes);
	}

	public Calendar getGameTime()
	{
		return currentTime;
	}

	@Override
	public void onGameWon()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onLevelLoad(AbstractLevel level)
	{
		ExpeditionLevel expeditionLevel = (ExpeditionLevel) level;
		expeditionLevel.playMusic();
		if (level instanceof ExpeditionMicroLevel)
			getExpedition().setMovementMode(MovementMode.FOOT);
		if (level.getID().equals("PALOS"))
		{
			int sumOfValuables = getExpedition().getSumOfValuables();
			if (sumOfValuables > 0)
			{
				String prompt = "Do you want to cash your valuables for " + sumOfValuables + " maraved�es?";
				if (UserInterface.getUI().promptChat(prompt))
				{
					getExpedition().cashValuables();
				}
			}
			getExpedition().touchPort();
			getExpedition().reportDiscoveries();
		}
		if (level.getID().equals("WORLD"))
		{
			/*
			 * level.getPlayer().setPosition(-329, 2158, 0); // Gibraltar
			 * level.getPlayer().setPosition(-4330, 732, 0); // Cabo dela vela
			 * level.getPlayer().setPosition(-2063, -1821, 0); // En medio del
			 * atl�ntico
			 */
			level.getPlayer().setPosition(-4362, 889, 0); // Near the Tairona
			// level.getPlayer().setPosition(-329, 3000, 0); // North
			int lat = 889 * 60;
			int longi = -4362 * 60;
			lat = GlobeMapModel.getSingleton().normalizeLat(lat);
			longi = GlobeMapModel.getSingleton().normalizeLong(lat, longi);
			level.getPlayer().setPosition(longi, lat, 0); // Near the Tairona
			/**/
		}
		((ExpeditionUserInterface) UserInterface.getUI()).notifyWeatherChange(expeditionLevel.getWeather());

	}

	public static ExpeditionVersion getExpeditionVersion()
	{
		return THIS_VERSION;
	}

	public static String getVersion()
	{
		return THIS_VERSION.code;
	}

	public Expedition getExpedition()
	{
		return (Expedition) getPlayer();
	}

	public static ExpeditionGame getCurrentGame()
	{
		return currentGame;
	}

	public int getLastExpeditionId()
	{
		lastExpeditionId++;
		return lastExpeditionId;
	}

	public void commandSelected(Command commandCode)
	{
		super.commandSelected(commandCode);
		switch (commandCode)
		{
		case HELP:
			ExpeditionDisplay.thus.showHelp();
			break;
		}
	}

	public List<FoodConsumer> getFoodConsumers()
	{
		return foodConsumers;
	}

	@Override
	protected FOV getNewFOV()
	{
		return new GlobeFOV(getExpedition());
	}

	public List<SettlementLevel> getSettlements()
	{
		return settlementLevels;
	}

	public void registerSettlement(SettlementLevel level)
	{
		settlementLevels.add(level);
	}

	public static ExpeditionVersion checkNewVersion() throws HttpException, IOException
	{
		String url = "http://expeditionworld.net/latestVersion";
		HttpClient client = new HttpClient();
		GetMethod latestVersion = new GetMethod(url);
		client.executeMethod(latestVersion);
		String str = latestVersion.getResponseBodyAsString();
		String[] info = str.split(",");
		latestVersion.releaseConnection();
		try
		{
			return new ExpeditionVersion(info[0], Integer.parseInt(info[1]), Integer.parseInt(info[2]),
					Integer.parseInt(info[3]));
		}
		catch (NumberFormatException e)
		{
			System.out.println("Invalid content for latest version URL: " + str);
			return null;
		}
	}
}
