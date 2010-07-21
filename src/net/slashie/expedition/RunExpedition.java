package net.slashie.expedition;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

import net.slashie.expedition.action.ArmExpedition;
import net.slashie.expedition.action.BuildSettlement;
import net.slashie.expedition.action.DropEquipment;
import net.slashie.expedition.action.MeleeAttack;
import net.slashie.expedition.action.NPRainArrows;
import net.slashie.expedition.action.RainArrows;
import net.slashie.expedition.action.RepairShips;
import net.slashie.expedition.action.Use;
import net.slashie.expedition.action.Walk;
import net.slashie.expedition.action.navigation.ResetDeadReckon;
import net.slashie.expedition.data.ExpeditionDAO;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.game.GameFiles;
import net.slashie.expedition.item.ItemFactory;
import net.slashie.expedition.ui.ExpeditionDisplay;
import net.slashie.expedition.ui.ExpeditionGenerator;
import net.slashie.expedition.ui.console.CharExpeditionDisplay;
import net.slashie.expedition.ui.console.CharPlayerGenerator;
import net.slashie.expedition.ui.console.ExpeditionConsoleUI;
import net.slashie.expedition.ui.console.ExpeditionConsoleUISelector;
import net.slashie.expedition.ui.console.effects.CharEffects;
import net.slashie.libjcsi.CharKey;
import net.slashie.libjcsi.ConsoleSystemInterface;
import net.slashie.libjcsi.jcurses.JCursesConsoleInterface;
import net.slashie.libjcsi.wswing.WSwingConsoleInterface;
import net.slashie.serf.SworeException;
import net.slashie.serf.action.Action;
import net.slashie.serf.action.ActionFactory;
import net.slashie.serf.game.SworeGame;
import net.slashie.serf.level.FeatureFactory;
import net.slashie.serf.level.MapCellFactory;
import net.slashie.serf.sound.SFXManager;
import net.slashie.serf.sound.STMusicManagerNew;
import net.slashie.serf.ui.Appearance;
import net.slashie.serf.ui.AppearanceFactory;
import net.slashie.serf.ui.CommandListener;
import net.slashie.serf.ui.EffectFactory;
import net.slashie.serf.ui.UISelector;
import net.slashie.serf.ui.UserAction;
import net.slashie.serf.ui.UserCommand;
import net.slashie.serf.ui.UserInterface;
import net.slashie.serf.ui.consoleUI.CharAppearance;
import net.slashie.serf.ui.consoleUI.ConsoleUISelector;
import net.slashie.serf.ui.consoleUI.ConsoleUserInterface;
import net.slashie.serf.ui.consoleUI.effects.CharEffectFactory;
import net.slashie.utils.FileUtil;
import net.slashie.utils.sound.midi.STMidiPlayer;



public class RunExpedition {
	private final static int JCURSES_CONSOLE = 0, SWING_GFX = 1, SWING_CONSOLE = 2;
	//private static SystemInterface si;
	private static UserInterface ui;
	private static UISelector uiSelector;
	
	private static ExpeditionGame currentGame;
	private static boolean createNew = true;
	private static int mode;
	
	public static String getConfigurationVal(String key){
		return configuration.getProperty(key);
	}

	private static void init(){
		if (createNew){
			System.out.println("Expedition "+ExpeditionGame.getVersion());
			System.out.println("Slash ~ 2009");
			System.out.println("Powered By Serf "+SworeGame.getVersion());
			System.out.println("Reading configuration");
	    	readConfiguration();
            try {
    			
    			switch (mode){
				case SWING_GFX:
					/*System.out.println("Initializing Graphics Appearances");
					initializeGAppearances();
					break;*/
				case JCURSES_CONSOLE:
				case SWING_CONSOLE:
					System.out.println("Initializing Char Appearances");
					initializeCAppearances();
					break;
    			}
				System.out.println("Initializing Action Objects");
				initializeActions();
				initializeSelectors();
				System.out.println("Loading Data");
				initializeItems();
				initializeCells();
				
				/*initializeMonsters();
				initializeNPCs();*/
				initializeFeatures();
				switch (mode){
				case SWING_GFX:
					/*System.out.println("Initializing Swing GFX System Interface");
					SwingSystemInterface si = new SwingSystemInterface();
					System.out.println("Initializing Swing GFX User Interface");
					UserInterface.setSingleton(new GFXUserInterface());
					GFXCuts.initializeSingleton();
					Display.thus = new GFXDisplay(si, UIconfiguration);
					PlayerGenerator.thus = new GFXPlayerGenerator(si);
					//PlayerGenerator.thus.initSpecialPlayers();
					EffectFactory.setSingleton(new GFXEffectFactory());
					((GFXEffectFactory)EffectFactory.getSingleton()).setEffects(new GFXEffects().getEffects());
					ui = UserInterface.getUI();
					initializeUI(si);*/
					break;
				case JCURSES_CONSOLE:
					System.out.println("Initializing JCurses System Interface");
					ConsoleSystemInterface csi = null;
					try{
						csi = new JCursesConsoleInterface();
					}
		            catch (ExceptionInInitializerError eiie){
		            	crash("Fatal Error Initializing JCurses", eiie);
		            	eiie.printStackTrace();
		                System.exit(-1);
		            }
		            System.out.println("Initializing Console User Interface");
					UserInterface.setSingleton(new ExpeditionConsoleUI(csi));
					/*CharCuts.initializeSingleton();*/
					ExpeditionDisplay.thus = new CharExpeditionDisplay(csi);
					ExpeditionGenerator.thus = new CharPlayerGenerator(csi);
					//PlayerGenerator.thus.initSpecialPlayers();
					EffectFactory.setSingleton(new CharEffectFactory());
					((CharEffectFactory)EffectFactory.getSingleton()).setEffects(new CharEffects().getEffects());
					ui = UserInterface.getUI();
					initializeUI(csi);
					break;
				case SWING_CONSOLE:
					System.out.println("Initializing Swing Console System Interface");
					csi = null;
					csi = new WSwingConsoleInterface("Expedition", false);
					System.out.println("Initializing Console User Interface");
					UserInterface.setSingleton(new ExpeditionConsoleUI(csi));
					//CharCuts.initializeSingleton();
					ExpeditionDisplay.thus = new CharExpeditionDisplay(csi);
					ExpeditionGenerator.thus = new CharPlayerGenerator(csi);
					//PlayerGenerator.thus.initSpecialPlayers();
					EffectFactory.setSingleton(new CharEffectFactory());
					((CharEffectFactory)EffectFactory.getSingleton()).setEffects(new CharEffects().getEffects());
					ui = UserInterface.getUI();
					initializeUI(csi);
				}
				
            } catch (SworeException crle){
            	crash("Error initializing", crle);
            }
            STMusicManagerNew.initManager();
        	if (configuration.getProperty("enableSound") != null && configuration.getProperty("enableSound").equals("true")){ // Sound
        		if (configuration.getProperty("enableMusic") == null || !configuration.getProperty("enableMusic").equals("true")){ // Music
    	    		STMusicManagerNew.thus.setEnabled(false);
    		    } else {
    		    	System.out.println("Initializing Midi Sequencer");
    	    		try {
    	    			STMidiPlayer.sequencer = MidiSystem.getSequencer ();
    	    			//STMidiPlayer.setVolume(0.1d);
    	    			STMidiPlayer.sequencer.open();
    	    			
    	    		} catch(MidiUnavailableException mue) {
    	            	SworeGame.addReport("Midi device unavailable");
    	            	System.out.println("Midi Device Unavailable");
    	            	STMusicManagerNew.thus.setEnabled(false);
    	            	return;
    	            }
    	    		System.out.println("Initializing Music Manager");
    				
    		    	
    	    		Enumeration keys = configuration.keys();
    	    	    while (keys.hasMoreElements()){
    	    	    	String key = (String) keys.nextElement();
    	    	    	if (key.startsWith("mus_")){
    	    	    		String music = key.substring(4);
    	    	    		STMusicManagerNew.thus.addMusic(music, configuration.getProperty(key));
    	    	    	}
    	    	    }
    	    	    STMusicManagerNew.thus.setEnabled(true);
    		    }
    	    	if (configuration.getProperty("enableSFX") == null || !configuration.getProperty("enableSFX").equals("true")){
    		    	SFXManager.setEnabled(false);
    		    } else {
    		    	SFXManager.setEnabled(true);
    		    }
        	}
			createNew = false;
    	}
	}
	private static Properties configuration;
	private static Properties UIconfiguration;
	private static String uiFile;
	
	private static void readConfiguration(){
		configuration = new Properties();
	    try {
	    	configuration.load(new FileInputStream("expedition.properties"));
	    } catch (IOException e) {
	    	System.out.println("Error loading configuration file, please confirm existence of expedition.properties");
	    	System.exit(-1);
	    }
	    
	    if (mode == SWING_GFX){
		    UIconfiguration = new Properties();
		    try {
		    	UIconfiguration.load(new FileInputStream(uiFile));
		    } catch (IOException e) {
		    	System.out.println("Error loading configuration file, please confirm existence of "+uiFile);
		    	System.exit(-1);
		    }
	    }

	}
	
	
				
	private static void	title() {
		
		int choice = ExpeditionDisplay.thus.showTitleScreen();
		switch (choice){
		case 0:
			newGame();
			break;
		case 1:
			loadGame();
			break;
		case 2:
			System.out.println("Expedition "+ExpeditionGame.getVersion()+", clean Exit");
			System.out.println("Thank you for playing!");
			System.exit(0);
			break;
		}
		
	}
	
	private static void loadGame(){
		File saveDirectory = new File("savegame");
		File[] saves = saveDirectory.listFiles(new SaveGameFilenameFilter() );
		
		int index = ExpeditionDisplay.thus.showSavedGames(saves);
		if (index == -1)
			title();
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saves[index]));
			currentGame = (ExpeditionGame) ois.readObject();
			ois.close();
		} catch (IOException ioe){
 
			ioe.printStackTrace();
		} catch (ClassNotFoundException cnfe){
			crash("Invalid savefile or wrong version", new SworeException("Invalid savefile or wrong version"));
		}
		currentGame.setInterfaces(ui, uiSelector);
		if (currentGame.getPlayer().getLevel() == null){
			crash("Player wasnt loaded", new Exception("Player wasnt loaded"));
		}
		currentGame.setPlayer(currentGame.getPlayer());
		ui.setPlayer(currentGame.getPlayer());
		uiSelector.setPlayer(currentGame.getPlayer());
		currentGame.resume();
	}
	
	private static void newGame(){
		if (currentGame != null){
			ui.removeCommandListener(currentGame);
		}
		currentGame = new ExpeditionGame();
		currentGame.setCanSave(true);
		currentGame.setInterfaces(ui, uiSelector);
		currentGame.newGame(1);
	}

	private static void initializeUI(Object si){
		Action walkAction = new Walk();
		Action meleeAction = new MeleeAttack();
		
		Action dropEquipment = new DropEquipment();
		Action buildSettlement = new BuildSettlement();
		Action rainArrows = new RainArrows();
		Action armExpedition = new ArmExpedition();
		Action resetReckon = new ResetDeadReckon();
		Action repairShips = new RepairShips();

		UserAction[] userActions = new UserAction[] {
		    new UserAction(dropEquipment, CharKey.d),
		    new UserAction(buildSettlement, CharKey.b),
		    new UserAction(rainArrows, CharKey.f),
		    new UserAction(armExpedition, CharKey.a),
		    new UserAction(resetReckon, CharKey.R),
		    new UserAction(repairShips, CharKey.r),
		};


		

		UserCommand[] userCommands = new UserCommand[]{
			new UserCommand(CommandListener.PROMPTQUIT, CharKey.Q),
			new UserCommand(CommandListener.HELP, CharKey.F1),
			new UserCommand(CommandListener.LOOK, CharKey.l),
			new UserCommand(CommandListener.PROMPTSAVE, CharKey.S),
			new UserCommand(CommandListener.HELP, CharKey.h),
			new UserCommand(CommandListener.SHOWINVEN, CharKey.i),

			//new UserCommand(CommandListener.SHOWSTATS, CharKey.c),
			//new UserCommand(CommandListener.CHARDUMP, CharKey.C),
			//new UserCommand(CommandListener.SHOWMESSAGEHISTORY, CharKey.m),
			//new UserCommand(CommandListener.SHOWMAP, CharKey.O),
			new UserCommand(CommandListener.SWITCHMUSIC, CharKey.T),
		};
		switch (mode){
		case SWING_GFX:
			/*((GFXUserInterface)ui).init((SwingSystemInterface)si, userCommands, UIconfiguration, target);
			uiSelector = new GFXUISelector();
			((GFXUISelector)uiSelector).init((SwingSystemInterface)si, userActions, UIconfiguration, walkAction, target, attack, (GFXUserInterface)ui);*/
			break;
		case JCURSES_CONSOLE: case SWING_CONSOLE:
			((ExpeditionConsoleUI)ui).init((ConsoleSystemInterface)si, userCommands, null);
			uiSelector = new ExpeditionConsoleUISelector();
			((ConsoleUISelector)uiSelector).init((ConsoleSystemInterface)si, userActions, walkAction, null, meleeAction, (ConsoleUserInterface)ui);
			break;
		}
	}
	
	public static void main(String args[]){
		//mode = SWING_GFX;
		mode = SWING_CONSOLE;
		uiFile = "slash-barrett.ui";
		if (args!= null && args.length > 0){
			if (args[0].equalsIgnoreCase("sgfx")){
				mode = SWING_GFX;
				if (args.length > 1)
					uiFile = args[1];
				else
					uiFile = "slash-barrett.ui";
			}
			else if (args[0].equalsIgnoreCase("jc"))
				mode = JCURSES_CONSOLE;
			else if (args[0].equalsIgnoreCase("sc"))
				mode = SWING_CONSOLE;
		}
		
		init();
		System.out.println("Launching game");
		try {
			while (true){
				title();
			}
		} catch (Exception e){
			ExpeditionGame.crash("Unrecoverable Exception [Press Space]",e);
			//si.waitKey(CharKey.SPACE);
		}
	}

	/*private static void initializeGAppearances(){
		Appearance[] definitions = new GFXAppearances().getAppearances();
		for (int i=0; i<definitions.length; i++){
			AppearanceFactory.getAppearanceFactory().addDefinition(definitions[i]);
		}
	}*/
	
	private static void initializeCAppearances(){
		Appearance[] definitions = ExpeditionDAO.getCharAppearances();
		for (int i=0; i<definitions.length; i++){
			AppearanceFactory.getAppearanceFactory().addDefinition(definitions[i]);
		}
	}
	
	private static void initializeActions(){
		ActionFactory af = ActionFactory.getActionFactory();
		Action[] definitions = new Action[]{
			new NPRainArrows()
		};
		for (int i = 0; i < definitions.length; i++)
			af.addDefinition(definitions[i]);
	}
	
	private static void initializeCells(){
		MapCellFactory.getMapCellFactory().init(ExpeditionDAO.getCellDefinitions(AppearanceFactory.getAppearanceFactory()));
	}

	private static void initializeFeatures(){
		FeatureFactory.getFactory().init(ExpeditionDAO.getFeatureDefinitions(AppearanceFactory.getAppearanceFactory()));
	}

	private static void initializeSelectors(){
		
	}

	private static void initializeItems(){
		ItemFactory.init(ExpeditionDAO.getItemDefinitions(AppearanceFactory.getAppearanceFactory()));
	}

	    public static void crash(String message, Throwable exception){
    	System.out.println("Expedition "+ExpeditionGame.getVersion()+": Error");
        System.out.println("");
        System.out.println("Unrecoverable error: "+message);
        exception.printStackTrace();
        if (currentGame != null){
        	System.out.println("Trying to save game");
        	GameFiles.saveGame(currentGame, (net.slashie.expedition.domain.Expedition)currentGame.getPlayer());
        }
        System.exit(-1);
    }
    
}

class SaveGameFilenameFilter implements FilenameFilter {

	public boolean accept(File arg0, String arg1) {
		//if (arg0.getName().endsWith(".sav"))
		if (arg1.endsWith(".sav"))
			return true;
		else
			return false;
	}
	
}