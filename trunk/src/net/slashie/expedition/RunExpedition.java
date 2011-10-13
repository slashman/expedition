package net.slashie.expedition;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.sound.midi.MidiUnavailableException;

import net.slashie.expedition.action.ArmExpedition;
import net.slashie.expedition.action.BuildSettlement;
import net.slashie.expedition.action.ChopWoods;
import net.slashie.expedition.action.DropEquipment;
import net.slashie.expedition.action.MeleeAttack;
import net.slashie.expedition.action.MountMounts;
import net.slashie.expedition.action.RepairShips;
import net.slashie.expedition.action.Walk;
import net.slashie.expedition.action.navigation.Anchor;
import net.slashie.expedition.action.navigation.ResetDeadReckon;
import net.slashie.expedition.data.ExpeditionDAO;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.game.ExpeditionMusicManager;
import net.slashie.expedition.game.GameFiles;
import net.slashie.expedition.game.ExpeditionGame.ExpeditionVersion;
import net.slashie.expedition.game.GameFiles.SaveGameFilenameFilter;
import net.slashie.expedition.item.ItemFactory;
import net.slashie.expedition.level.FlatMapModelSeconds;
import net.slashie.expedition.level.GlobeMapModel;
import net.slashie.expedition.town.BuildingFactory;
import net.slashie.expedition.town.NPCFactory;
import net.slashie.expedition.ui.ExpeditionDisplay;
import net.slashie.expedition.ui.console.CharExpeditionDisplay;
import net.slashie.expedition.ui.console.ExpeditionConsoleUI;
import net.slashie.expedition.ui.console.ExpeditionConsoleUISelector;
import net.slashie.expedition.ui.console.effects.CharEffects;
import net.slashie.expedition.ui.oryx.ExpeditionGFXUISelector;
import net.slashie.expedition.ui.oryx.ExpeditionOryxUI;
import net.slashie.expedition.ui.oryx.OryxExpeditionDisplay;
import net.slashie.expedition.ui.oryx.effects.GFXEffects;
import net.slashie.expedition.world.StoreFactory;
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
import net.slashie.serf.ui.consoleUI.ConsoleUISelector;
import net.slashie.serf.ui.consoleUI.ConsoleUserInterface;
import net.slashie.serf.ui.consoleUI.effects.CharEffectFactory;
import net.slashie.serf.ui.oryxUI.Assets;
import net.slashie.serf.ui.oryxUI.GFXAppearances;
import net.slashie.serf.ui.oryxUI.GFXUISelector;
import net.slashie.serf.ui.oryxUI.GFXUserInterface;
import net.slashie.serf.ui.oryxUI.SwingSystemInterface;
import net.slashie.serf.ui.oryxUI.UIAssetsLoader;
import net.slashie.serf.ui.oryxUI.effects.GFXEffectFactory;
import net.slashie.utils.PropertyFilters;
import net.slashie.utils.sound.midi.STMidiPlayer;

import org.apache.commons.httpclient.HttpException;

public class RunExpedition {
	enum DisplayMode {
		JCURSES_CONSOLE,
		SWING_GFX,
		SWING_CONSOLE
	}
	
	private static final int LAYERS = 6;
	private static UserInterface ui;
	private static UISelector uiSelector;
	
	private static ExpeditionGame currentGame;
	private static boolean createNew = true;
	private static DisplayMode mode;
	private static Assets assets;
	
	public static String getConfigurationVal(String key){
		return configuration.getProperty(key);
	}

	private static void init(){
		if (createNew){
			System.out.println("Expedition "+ExpeditionGame.getVersion());
			System.out.println("Slashware Interactive ~ 2009-2011");
			System.out.println("Powered By Serf "+SworeGame.getVersion());
			System.out.println("Reading configuration");
	    	readConfiguration();
            try {
    			
    			switch (mode){
				case SWING_GFX:
					System.out.println("Initializing Graphics Appearances");
					initializeGAppearances(graphicsPackDir, "appearances.xml");
					break;
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
				initializeStores();
				initializeItems();
				initializeCells();
				initializeBuildings();
				initializeNPCS();
				initializeFeatures();
				switch (mode){
				case SWING_GFX:
					System.out.println("Initializing Swing GFX System Interface");
					SwingSystemInterface si = new SwingSystemInterface(LAYERS, false, 
							PropertyFilters.inte(UIconfiguration.getProperty("WINDOW_WIDTH")), 
							PropertyFilters.inte(UIconfiguration.getProperty("WINDOW_HEIGHT")), 
							PropertyFilters.inte(UIconfiguration.getProperty("FRAMES_PER_SECOND")));
					
					System.out.println("Initializing Oryx GFX User Interface");
					assets = loadUIAssets(graphicsPackDir);
					UserInterface.setSingleton(new ExpeditionOryxUI());
					
					EffectFactory.setSingleton(new GFXEffectFactory());
					((GFXEffectFactory)EffectFactory.getSingleton()).setEffects(new GFXEffects().getEffects());
					ui = UserInterface.getUI();
					initializeUI(si);
					ExpeditionDisplay.thus = new OryxExpeditionDisplay(si, assets, UIconfiguration);
					break;
				case JCURSES_CONSOLE:
					System.out.println("Initializing JCurses System Interface");
					ConsoleSystemInterface csi = null;
					try{
						csi = new JCursesConsoleInterface();
						csi.setAutoRefresh(false);
					}
		            catch (ExceptionInInitializerError eiie){
		            	crash("Fatal Error Initializing JCurses", eiie);
		            	eiie.printStackTrace();
		                System.exit(-1);
		            }
		            System.out.println("Initializing Console User Interface");
					UserInterface.setSingleton(new ExpeditionConsoleUI(csi));
					ExpeditionDisplay.thus = new CharExpeditionDisplay(csi);
					
					EffectFactory.setSingleton(new CharEffectFactory());
					((CharEffectFactory)EffectFactory.getSingleton()).setEffects(new CharEffects().getEffects());
					ui = UserInterface.getUI();
					initializeUI(csi);
					break;
				case SWING_CONSOLE:
					System.out.println("Initializing Swing Console System Interface");
					csi = null;
					csi = new WSwingConsoleInterface("Expedition", configuration);
					System.out.println("Initializing Console User Interface");
					UserInterface.setSingleton(new ExpeditionConsoleUI(csi));
					//CharCuts.initializeSingleton();
					ExpeditionDisplay.thus = new CharExpeditionDisplay(csi);
					//PlayerGenerator.thus.initSpecialPlayers();
					EffectFactory.setSingleton(new CharEffectFactory());
					((CharEffectFactory)EffectFactory.getSingleton()).setEffects(new CharEffects().getEffects());
					ui = UserInterface.getUI();
					initializeUI(csi);
				}
				
            } catch (SworeException crle){
            	crash("Error initializing", crle);
            }
            GlobeMapModel.setSingleton(new FlatMapModelSeconds());
            STMusicManagerNew.initManager();
            ExpeditionMusicManager.init();
        	if (configuration.getProperty("enableSound") != null && configuration.getProperty("enableSound").equals("true")){ // Sound
        		if (configuration.getProperty("enableMusic") == null || !configuration.getProperty("enableMusic").equals("true")){ // Music
        			ExpeditionMusicManager.setEnabled(false);
    		    } else {
    		    	System.out.println("Initializing Midi Sequencer");
    	    		try {
    	    			STMidiPlayer.initializeSequencer();
    	    		} catch(MidiUnavailableException mue) {
    	            	SworeGame.addReport("Midi device unavailable");
    	            	System.out.println("Midi Device Unavailable");
    	            	ExpeditionMusicManager.setEnabled(false);
    	            	return;
    	            }
    	    		System.out.println("Initializing Music Manager");
    				
    		    	
    	    		Enumeration keys = configuration.keys();
    	    	    while (keys.hasMoreElements()){
    	    	    	String key = (String) keys.nextElement();
    	    	    	if (key.startsWith("mus_")){
    	    	    		String music = key.substring(4);
    	    	    		ExpeditionMusicManager.addTune(music, configuration.getProperty(key));
    	    	    	}
    	    	    }
    	    	    ExpeditionMusicManager.setEnabled(true);
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
	private static Assets loadUIAssets(String graphicsPackDir) {
		return UIAssetsLoader.getAssets(graphicsPackDir, "uiAssets.xml");
	}

	private static void initializeStores() {
		StoreFactory.initializeSingleton(new StoreFactory());
	}
	
	private static Properties configuration;
	private static Properties UIconfiguration;
	private static Properties keyConfig;
	private static Properties keyBindings;
	
	private static String preselectedUIFile;
	private static String graphicsPackDir;
	
	private static void readConfiguration(){
		configuration = new Properties();
	    try {
	    	configuration.load(new FileInputStream("expedition.properties"));
	    } catch (IOException e) {
	    	System.out.println("Error loading configuration file, please confirm existence of expedition.properties");
	    	System.exit(-1);
	    }
	    
	    String uiFile = preselectedUIFile;
	    
	    if (uiFile == null){
	    	uiFile = configuration.getProperty("graphicsPack");
	    }
	    
	    keyConfig = new Properties();
	    try {
	    	keyConfig.load(new FileInputStream("keys.properties"));
	    	
	    } catch (IOException e) {
	    	System.out.println("Error loading configuration file, please confirm existence of keys.properties");
	    	System.exit(-1);
	    }
	    
	    if (mode == DisplayMode.SWING_GFX){
		    UIconfiguration = new Properties();
		    try {
		    	System.out.println("Loading "+uiFile+" graphics pack.");
		    	graphicsPackDir = new File(uiFile).getParent();
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
			selectScenario();
			break;
		case 1:
			UserInterface.getUI().showSystemMessage("This mode isn't yet available.");
			break;
		case 2:
			loadGame();
			break;
		case 3:
			if (UserInterface.getUI().promptChat("Quit: Are you sure")){
				System.out.println("Expedition "+ExpeditionGame.getVersion()+", clean Exit");
				System.out.println("Thank you for playing!");
				System.exit(0);
				break;
			}
		}
		
	}
	
	private static void selectScenario() {
		int choice = ExpeditionDisplay.thus.selectScenario();
		if (choice == 0)
			newGame();
		else
			return;
		
	}

	private static void loadGame(){
		File saveDirectory = new File("savegame");
		File[] saves = saveDirectory.listFiles(new SaveGameFilenameFilter() );
		
		int index = ExpeditionDisplay.thus.showSavedGames(saves);
		if (index == -1){
			return;
		}
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saves[index]));
			currentGame = (ExpeditionGame) ois.readObject();
			ois.close();
		} catch (InvalidClassException ice){
			UserInterface.getUI().showImportantMessage("This save game is not compatible with version "+ExpeditionGame.getVersion());
			return;
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
		Action armExpedition = new ArmExpedition();
		Action resetReckon = new ResetDeadReckon();
		Action repairShips = new RepairShips();
		Action chopWoods = new ChopWoods();
		Action mountMounts = new MountMounts();
		Action anchor = new Anchor();

		keyBindings = new Properties();
		keyBindings.put("DONOTHING1_KEY", readKeyString(keyConfig, "doNothing"));
		keyBindings.put("DONOTHING2_KEY", readKeyString(keyConfig, "doNothing2"));
		keyBindings.put("UP1_KEY", readKeyString(keyConfig, "up"));
		keyBindings.put("UP2_KEY", readKeyString(keyConfig, "up2"));
		keyBindings.put("LEFT1_KEY", readKeyString(keyConfig, "left"));
		keyBindings.put("LEFT2_KEY", readKeyString(keyConfig, "left2"));
		keyBindings.put("RIGHT1_KEY", readKeyString(keyConfig, "right"));
		keyBindings.put("RIGHT2_KEY", readKeyString(keyConfig, "right2"));
		keyBindings.put("DOWN1_KEY", readKeyString(keyConfig, "down"));
		keyBindings.put("DOWN2_KEY", readKeyString(keyConfig, "down2"));
		keyBindings.put("UPRIGHT1_KEY", readKeyString(keyConfig, "upRight"));
		keyBindings.put("UPRIGHT2_KEY", readKeyString(keyConfig, "upRight2"));
		keyBindings.put("UPLEFT1_KEY", readKeyString(keyConfig, "upLeft"));
		keyBindings.put("UPLEFT2_KEY", readKeyString(keyConfig, "upLeft2"));
		keyBindings.put("DOWNLEFT1_KEY", readKeyString(keyConfig, "downLeft"));
		keyBindings.put("DOWNLEFT2_KEY", readKeyString(keyConfig, "downLeft2"));
		keyBindings.put("DOWNRIGHT1_KEY", readKeyString(keyConfig, "downRight"));
		keyBindings.put("DOWNRIGHT2_KEY", readKeyString(keyConfig, "downRight2"));
		keyBindings.put("SELF1_KEY", readKeyString(keyConfig, "self"));
		keyBindings.put("SELF2_KEY", readKeyString(keyConfig, "self2"));
		
		keyBindings.put("DROP_EQUIPMENT_KEY", readKeyString(keyConfig, "drop"));
		keyBindings.put("BUILD_SETTLEMENT_KEY", readKeyString(keyConfig, "build"));
		keyBindings.put("ARM_EXPEDITION_KEY", readKeyString(keyConfig, "arm"));
		keyBindings.put("RESET_RECKON_KEY", readKeyString(keyConfig, "reset"));
		keyBindings.put("REPAIR_SHIPS_KEY", readKeyString(keyConfig, "repair"));
		keyBindings.put("CHOP_WOODS_KEY", readKeyString(keyConfig, "chopWoods"));
		keyBindings.put("MOUNT_KEY", readKeyString(keyConfig, "mount"));
		keyBindings.put("ANCHOR_KEY", readKeyString(keyConfig, "anchor"));
		
		keyBindings.put("QUIT_KEY", readKeyString(keyConfig, "PROMPTQUIT"));
		keyBindings.put("HELP1_KEY", readKeyString(keyConfig, "HELP1"));
		keyBindings.put("HELP2_KEY", readKeyString(keyConfig, "HELP2"));
		keyBindings.put("LOOK_KEY", readKeyString(keyConfig, "LOOK"));
		keyBindings.put("PROMPT_SAVE_KEY", readKeyString(keyConfig, "PROMPTSAVE"));
		keyBindings.put("SHOW_INVENTORY_KEY", readKeyString(keyConfig, "SHOWINVEN"));
		keyBindings.put("SWITCH_MUSIC_KEY", readKeyString(keyConfig, "SWITCHMUSIC"));
		keyBindings.put("SWITCH_SFX_KEY", readKeyString(keyConfig, "SWITCHSFX"));
		keyBindings.put("MESSAGE_LOG_KEY", readKeyString(keyConfig, "MESSAGE_LOG"));
		
		UserAction[] userActions = new UserAction[] {
			    new UserAction(dropEquipment, i(keyBindings.getProperty("DROP_EQUIPMENT_KEY"))),
			    new UserAction(buildSettlement, i(keyBindings.getProperty("BUILD_SETTLEMENT_KEY"))),
			    new UserAction(armExpedition, i(keyBindings.getProperty("ARM_EXPEDITION_KEY"))),
			    new UserAction(resetReckon, i(keyBindings.getProperty("RESET_RECKON_KEY"))),
			    new UserAction(repairShips, i(keyBindings.getProperty("REPAIR_SHIPS_KEY"))),
			    new UserAction(chopWoods, i(keyBindings.getProperty("CHOP_WOODS_KEY"))),
			    new UserAction(mountMounts, i(keyBindings.getProperty("MOUNT_KEY"))),
			    new UserAction(anchor, i(keyBindings.getProperty("ANCHOR_KEY"))),
			};
		
		UserCommand[] userCommands = new UserCommand[]{
			new UserCommand(CommandListener.Command.PROMPTQUIT, i(keyBindings.getProperty("QUIT_KEY"))),
			new UserCommand(CommandListener.Command.HELP, i(keyBindings.getProperty("HELP1_KEY"))),
			new UserCommand(CommandListener.Command.LOOK, i(keyBindings.getProperty("LOOK_KEY"))),
			new UserCommand(CommandListener.Command.PROMPTSAVE, i(keyBindings.getProperty("PROMPT_SAVE_KEY"))),
			new UserCommand(CommandListener.Command.HELP, i(keyBindings.getProperty("HELP2_KEY"))),
			new UserCommand(CommandListener.Command.SHOWINVEN, i(keyBindings.getProperty("SHOW_INVENTORY_KEY"))),
			new UserCommand(CommandListener.Command.SWITCHMUSIC, i(keyBindings.getProperty("SWITCH_MUSIC_KEY"))),
			new UserCommand(CommandListener.Command.SWITCHSFX, i(keyBindings.getProperty("SWITCH_SFX_KEY"))),
			new UserCommand(CommandListener.Command.SHOWMESSAGEHISTORY, i(keyBindings.getProperty("MESSAGE_LOG_KEY"))),
		};
		
		switch (mode){
		case SWING_GFX:
			SwingSystemInterface ssi = (SwingSystemInterface)si;
			((ExpeditionOryxUI)ui).init(ssi, "Expedition: The New World v"+ExpeditionGame.getVersion()+", Santiago Zapata 2009-2011", userCommands, UIconfiguration, assets, null);
			UserInterface.getUI().showImportantMessage("Thank you for trying out this version of Expedition: The New World.\n\nThis game is in active development, if you like the game please visit http://slashware.net to learn about ways to help us complete it!");
			if (((ExpeditionOryxUI)ui).promptChat(" Do you want to enable full screen mode?",140,388,520,200)){
				si = new SwingSystemInterface(LAYERS, true, 
						PropertyFilters.inte(UIconfiguration.getProperty("WINDOW_WIDTH")), 
						PropertyFilters.inte(UIconfiguration.getProperty("WINDOW_HEIGHT")), 
						PropertyFilters.inte(UIconfiguration.getProperty("FRAMES_PER_SECOND")));
				ssi = (SwingSystemInterface)si;
				System.out.println("Initializing Oryx GFX User Interface");
				UserInterface.setSingleton(new ExpeditionOryxUI());
				ExpeditionDisplay.thus = new OryxExpeditionDisplay(ssi, assets, UIconfiguration);
				ui = UserInterface.getUI();
				((ExpeditionOryxUI)ui).init(ssi, "Expedition: The New World v"+ExpeditionGame.getVersion()+", Santiago Zapata 2009-2011", userCommands, UIconfiguration, assets, null);
			}
			if (((ExpeditionOryxUI)ui).promptChat("Do you want to check for new versions?",140,388,520,200)){
				try {
					ExpeditionVersion latestVersion = ExpeditionGame.checkNewVersion();
					if (latestVersion == null){
						UserInterface.getUI().showImportantMessage("Error connecting to expeditionworld.net. Please browse to ensure you have the latest version :)");
					} else if (latestVersion.equals(ExpeditionGame.getExpeditionVersion())){
						UserInterface.getUI().showImportantMessage("You are up to date :)");
					} else {
						UserInterface.getUI().showImportantMessage("A newer version, "+latestVersion.getCode()+" from "+latestVersion.getFormattedDate()+" is available at the website! Please download it from http://expeditionworld.net");
					}
				} catch (HttpException e) {
					UserInterface.getUI().showImportantMessage("Error connecting to expeditionworld.net. Please browse to ensure you have the latest version :)");
				} catch (IOException e) {
					UserInterface.getUI().showImportantMessage("Error connecting to expeditionworld.net. Please browse to ensure you have the latest version :)");
				}
			}
			uiSelector = new ExpeditionGFXUISelector();
			((GFXUISelector)uiSelector).init((SwingSystemInterface)si, userActions, UIconfiguration, walkAction, null, meleeAction, (GFXUserInterface)ui, keyBindings, assets);
			break;
		case JCURSES_CONSOLE: case SWING_CONSOLE:
			((ExpeditionConsoleUI)ui).init((ConsoleSystemInterface)si, userCommands, null);
			uiSelector = new ExpeditionConsoleUISelector();
			((ConsoleUISelector)uiSelector).init((ConsoleSystemInterface)si, userActions, walkAction, null, meleeAction, (ConsoleUserInterface)ui, keyBindings);
			break;
		}
	}
	
	
	private static int i(String val){
		return Integer.parseInt(val);
	}
	
	public static void main(String args[]){
		if (args!= null && args.length > 0){
			if (args[0].equalsIgnoreCase("gfx")){
				mode = DisplayMode.SWING_GFX;
				if (args.length > 1)
					preselectedUIFile = args[1];
			}
			else if (args[0].equalsIgnoreCase("jc"))
				mode = DisplayMode.JCURSES_CONSOLE;
			else if (args[0].equalsIgnoreCase("sc"))
				mode = DisplayMode.SWING_CONSOLE;
		} else {
			mode = DisplayMode.SWING_GFX;
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

	private static String readKeyString(Properties config, String keyName) {
		return readKey(config, keyName)+"";
	}

	
	private static int readKey(Properties config, String keyName) {
		String fieldName = config.getProperty(keyName).trim();
		if (fieldName == null)
			throw new RuntimeException("Invalid key.cfg file, property not found: "+keyName);
		try {
			Field field = CharKey.class.getField(fieldName);
			return field.getInt(CharKey.class);
		} catch (SecurityException e) {
			e.printStackTrace();
			throw new RuntimeException("Error reading field : "+fieldName);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			throw new RuntimeException("Error reading field : "+fieldName);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new RuntimeException("Error reading field : "+fieldName);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException("Error reading field : "+fieldName);
		}
	}
	private static void initializeGAppearances(String graphicsPackPath, String graphicsPackXMLFile){
		List<Appearance> definitions = GFXAppearances.getGFXAppearances(graphicsPackPath, graphicsPackXMLFile);
		for (Appearance definition: definitions){
			AppearanceFactory.getAppearanceFactory().addDefinition(definition);
		}
	}
	
	private static void initializeCAppearances(){
		Appearance[] definitions = ExpeditionDAO.getCharAppearances();
		for (int i=0; i<definitions.length; i++){
			AppearanceFactory.getAppearanceFactory().addDefinition(definitions[i]);
		}
	}
	
	private static void initializeActions(){
		ActionFactory af = ActionFactory.getActionFactory();
		Action[] definitions = new Action[]{
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
	
	private static void initializeBuildings(){
		BuildingFactory.setBuildings(ExpeditionDAO.getBuildings());
	}
	
	private static void initializeNPCS(){
		NPCFactory.setNPCs(ExpeditionDAO.getNPCs());
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