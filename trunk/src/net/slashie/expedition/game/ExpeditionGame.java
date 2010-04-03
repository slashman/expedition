package net.slashie.expedition.game;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.Expedition.MovementMode;
import net.slashie.expedition.ui.ExpeditionDisplay;
import net.slashie.expedition.ui.ExpeditionGenerator;
import net.slashie.expedition.world.ExpeditionLevel;
import net.slashie.expedition.world.ExpeditionMicroLevel;
import net.slashie.expedition.world.FoodConsumer;
import net.slashie.expedition.world.LevelMaster;
import net.slashie.serf.action.Actor;
import net.slashie.serf.game.Player;
import net.slashie.serf.game.SworeGame;
import net.slashie.serf.level.AbstractLevel;
import net.slashie.serf.level.LevelMetaData;
import net.slashie.serf.sound.STMusicManagerNew;
import net.slashie.serf.ui.CommandListener;
import net.slashie.serf.ui.UserInterface;

public class ExpeditionGame extends SworeGame {
	private static ExpeditionGame currentGame;
	private int lastExpeditionId = 1;
	private Calendar currentTime;
	private int dayShiftCount = 12;
	@Override
	public void afterPlayerAction() {
		if (getExpedition().getLocation() instanceof ExpeditionMicroLevel){
			
		} else {
			dayShiftCount += getExpedition().getLastActionTimeCost();
			if (dayShiftCount >= 200){
				dayShiftCount = 0;
				currentTime.add(Calendar.DATE, 1);
				for (int i = 0; i < foodConsumers.size(); i++){
					foodConsumers.get(i).consumeFood();
				}
			}
		}
		
	}
	
	private List<FoodConsumer> foodConsumers = new ArrayList<FoodConsumer>();
	public void addFoodConsumer(FoodConsumer foodConsumer){
		foodConsumers.add(foodConsumer);
	}

	@Override
	public void beforeGameStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforePlayerAction() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AbstractLevel createLevel(LevelMetaData levelMetadata) {
		return LevelMaster.createLevel(levelMetadata.getLevelID(), getExpedition());
	}

	@Override
	public Player generatePlayer(int gameType, SworeGame game) {
		return ExpeditionGenerator.thus.createExpedition((ExpeditionGame)game);
	}

	@Override
	public String getDeathMessage() {
		return "Your expedition has perished..";
	}

	@Override
	public String getFirstMessage(Actor player) {
		return "Welcome to Spain, "+getExpedition().getExpeditionary();
	}

	@Override
	public void onGameOver() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGameResume() {
		currentGame = this;
		ExpeditionLevel expeditionLevel = (ExpeditionLevel)getExpedition().getLevel();
		if (expeditionLevel.getMusicKey() != null)
			STMusicManagerNew.thus.playKey(expeditionLevel.getMusicKey());
	}

	@Override
	public void onGameStart(int gameType) {
		currentGame = this;
		ExpeditionDisplay.thus.showIntro(getExpedition());
		loadMetadata();
		loadLevel("SPAIN");
		setGameTime(20,7,1492);
	}
	
	private void loadMetadata() {
		LevelMetaData md = null;
		md = new LevelMetaData("SPAIN");
		addMetaData("SPAIN", md);
		md = new LevelMetaData("WORLD");
		addMetaData("WORLD", md);
		
	}

	private void setGameTime(int day, int month, int year) {
		currentTime = Calendar.getInstance();
		currentTime.set(Calendar.YEAR, year);
		currentTime.set(Calendar.MONTH, month-1);
		currentTime.set(Calendar.DATE, day);
	}
	
	public Calendar getGameTime(){
		return currentTime;
	}

	@Override
	public void onGameWon() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLevelLoad(AbstractLevel level) {
		ExpeditionLevel expeditionLevel = (ExpeditionLevel)level;
		if (expeditionLevel.getMusicKey() != null)
			STMusicManagerNew.thus.playKey(expeditionLevel.getMusicKey());
		if (level instanceof ExpeditionMicroLevel)
			getExpedition().setMovementMode(MovementMode.FOOT);
		if (level.getID().equals("SPAIN")){
			int sumOfValuables = getExpedition().getSumOfValuables(); 
			if (sumOfValuables > 0){
				String prompt = "Do you want to cash your valuables for "+sumOfValuables+" maravedíes?";
				if (UserInterface.getUI().promptChat(prompt)){
					getExpedition().cashValuables();
				}
			}
		}

		
	}
	
	public static String getVersion(){
		return "v0.1.6 rev97";
	}
	
	public Expedition getExpedition(){
		return (Expedition) getPlayer();
	}

	public static ExpeditionGame getCurrentGame() {
		return currentGame;
	}

	public int getLastExpeditionId() {
		lastExpeditionId++;
		return lastExpeditionId;
	}

	public void commandSelected (int commandCode){
		super.commandSelected(commandCode);
		switch (commandCode){
		case CommandListener.HELP:
			ExpeditionDisplay.thus.showHelp();
			break;
		}
	}

}
