package net.slashie.expedition.ui.oryx;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextArea;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.ExpeditionFactory;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.game.ExpeditionMusicManager;
import net.slashie.expedition.game.GameFiles;
import net.slashie.expedition.game.GameFiles.LicenseInfo;
import net.slashie.expedition.ui.CommonUI;
import net.slashie.expedition.ui.ExpeditionDisplay;
import net.slashie.libjcsi.CharKey;
import net.slashie.serf.game.SworeGame;
import net.slashie.serf.sound.STMusicManagerNew;
import net.slashie.serf.ui.UserInterface;
import net.slashie.serf.ui.oryxUI.AddornedBorderTextArea;
import net.slashie.serf.ui.oryxUI.GFXUISelector;
import net.slashie.serf.ui.oryxUI.GFXUserInterface;
import net.slashie.serf.ui.oryxUI.SwingSystemInterface;
import net.slashie.utils.ImageUtils;
import net.slashie.utils.PropertyFilters;
import net.slashie.utils.swing.BorderedMenuBox;
import net.slashie.utils.swing.CallbackActionListener;
import net.slashie.utils.swing.CallbackKeyListener;
import net.slashie.utils.swing.CleanButton;
import net.slashie.utils.swing.GFXMenuItem;
import net.slashie.utils.swing.SimpleGFXMenuItem;

public class OryxExpeditionDisplay extends ExpeditionDisplay{
	private SwingSystemInterface si;
	
	private String IMG_TITLE;
	private String IMG_BLANK;
	public static Font FNT_TEXT;
	public static Font FNT_TITLE;
	public static Font FNT_DIALOGUEIN;
	public static Font FNT_MONO;
	private static BufferedImage IMG_PICKER;
	private static BufferedImage IMG_BORDERS;
	private static Properties uiProperties;
	public static Color COLOR_BOLD;
	private Cursor HAND_CURSOR;
	
	private void initProperties(Properties p){
		uiProperties = p;
		IMG_TITLE = p.getProperty("IMG_TITLE");
		IMG_BLANK = p.getProperty("IMG_BLANK");
		COLOR_BOLD = PropertyFilters.getColor(p.getProperty("COLOR_BOLD"));
		HAND_CURSOR = GFXUserInterface.createCursor(uiProperties.getProperty("IMG_CURSORS"), 6, 2, 10, 4);
		try {
			IMG_PICKER = PropertyFilters.getImage(p.getProperty("IMG_PICKER"), p.getProperty("IMG_PICKER_BOUNDS"));
			IMG_BORDERS = PropertyFilters.getImage(p.getProperty("IMG_BORDERS"), p.getProperty("IMG_BORDERS_BOUNDS"));
			FNT_TITLE = PropertyFilters.getFont(p.getProperty("FNT_TITLE"), p.getProperty("FNT_TITLE_SIZE"));
			FNT_TEXT = PropertyFilters.getFont(p.getProperty("FNT_TEXT"), p.getProperty("FNT_TEXT_SIZE"));
			FNT_DIALOGUEIN  = FNT_TEXT;
			FNT_MONO = PropertyFilters.getFont(p.getProperty("FNT_MONO"), p.getProperty("FNT_MONO_SIZE"));
		} catch (FontFormatException ffe){
			SworeGame.crash("Error loading the font", ffe);
		} catch (IOException ioe){
			SworeGame.crash("Error loading the font", ioe);
		} catch (Exception e){
			SworeGame.crash("Error loading images", e);
		}
	}
	
	private AddornedBorderTextArea addornedTextArea;

	public OryxExpeditionDisplay(SwingSystemInterface si, Properties p){
		initProperties(p);
		this.si = si;
		try {
			//BufferedImage BORDERS = ImageUtils.createImage(IMG_BORDERS);
			int tileSize = PropertyFilters.inte(p.getProperty("TILE_SIZE"));
			
			BufferedImage BORDER1 = ImageUtils.crearImagen(IMG_BORDERS, tileSize,0,tileSize,tileSize);
			BufferedImage BORDER2 = ImageUtils.crearImagen(IMG_BORDERS, 0,0,tileSize,tileSize);
			BufferedImage BORDER3 = ImageUtils.crearImagen(IMG_BORDERS, tileSize*3,0,tileSize,tileSize);
			BufferedImage BORDER4 = ImageUtils.crearImagen(IMG_BORDERS, tileSize*2,0, tileSize,tileSize);
			
			addornedTextArea = new AddornedBorderTextArea(
					BORDER1,
					BORDER2,
					BORDER3,
					BORDER4,
					new Color(52,42,20),
					new Color(164,138,68),
					new Color(232,253,77),
					tileSize,
					6,9,12);
			addornedTextArea.setVisible(false);
			addornedTextArea.setEnabled(false);
			addornedTextArea.setForeground(Color.WHITE);
			addornedTextArea.setBackground(Color.BLACK);
			addornedTextArea.setFont(FNT_DIALOGUEIN);
			addornedTextArea.setOpaque(false);
		}
		 catch (Exception e){
			 SworeGame.crash("Error loading UI data", e);
		 }
		 si.add(addornedTextArea);
	}
	
	public int showTitleScreen(){
		ExpeditionOryxUI oui = ((ExpeditionOryxUI)UserInterface.getUI()); 
		oui.messageBox.setVisible(false);
		oui.persistantMessageBox.setVisible(false);
		ExpeditionMusicManager.playTune("TITLE");
		
		si.setFont(0, FNT_TEXT);
		si.setCursor(GFXUserInterface.createCursor(uiProperties.getProperty("IMG_CURSORS"), 6, 3, 4, 4));

		si.drawImage(0, IMG_TITLE);
		si.printAtPixel(0, 30, 550, "Version "+ExpeditionGame.getVersion()+", ", Color.WHITE);
		si.printAtPixel(0, 30, 568, "A production of Slashware Interactive 2009-2011", Color.WHITE);
		
   	
    	// Read the license info 
    	LicenseInfo licenseInfo = GameFiles.getLicenseInfo();
		
		if (licenseInfo.licensee == null || licenseInfo.licensee.equals("unregistered")){
			si.printAtPixel(0, 30, 586, "Unregistered Version", Color.WHITE);
		} else {
			si.printAtPixel(0, 30, 586, "Registered for "+licenseInfo.licenseLevel+" "+licenseInfo.licensee+"!", Color.YELLOW);
		}

		ExpeditionCleanButton historyButton = new ExpeditionCleanButton(8, "Historic Scenario");
		historyButton.setLocation(558, 30);
		ExpeditionCleanButton expeditionButton = new ExpeditionCleanButton(8, "New Expedition");
		expeditionButton.setLocation(558, 82);
		ExpeditionCleanButton resumeButton = new ExpeditionCleanButton(8, "Continue Journey");
		resumeButton.setLocation(558, 134);
		ExpeditionCleanButton exitButton = new ExpeditionCleanButton(8, "Exit");
		exitButton.setLocation(558, 186);
		
		expeditionButton.setVisible(false); // This isn't yet implemented
		
		File saveDirectory = new File("savegame");
		final File[] saves = saveDirectory.listFiles(new GameFiles.SaveGameFilenameFilter() );
		
		if (saves.length == 0){
			resumeButton.setVisible(false);
		}
		
		si.add(historyButton);
		si.add(expeditionButton);
		si.add(resumeButton);
		si.add(exitButton);
		
		si.commitLayer(0);
		//si.commitLayer(1);
		
		BlockingQueue<Integer> titleSelectionHandler = new LinkedBlockingQueue<Integer>();

		historyButton.addActionListener(new CallbackActionListener<Integer>(titleSelectionHandler){
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					handler.put(0);
				} catch (InterruptedException e1) {
				}
			}
		});
		
		expeditionButton.addActionListener(new CallbackActionListener<Integer>(titleSelectionHandler){
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					handler.put(1);
				} catch (InterruptedException e1) {
				}
			}
		});
		
		resumeButton.addActionListener(new CallbackActionListener<Integer>(titleSelectionHandler){
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					handler.put(2);
				} catch (InterruptedException e1) {
				}
			}
		});
		
		exitButton.addActionListener(new CallbackActionListener<Integer>(titleSelectionHandler){
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					handler.put(3);
				} catch (InterruptedException e1) {

				}
			}
		});

		CallbackKeyListener<Integer> cbkl = new CallbackKeyListener<Integer>(titleSelectionHandler){
			@Override
			public void keyPressed(KeyEvent e) {
				try {
					int code = SwingSystemInterface.charCode(e);
					if (code == CharKey.a || code == CharKey.A)
						handler.put(0);
					if (saves.length != 0 && (code == CharKey.c || code == CharKey.C))
						handler.put(2);
					if (code == CharKey.d || code == CharKey.D || code == CharKey.ESC){
						handler.put(3);
					}
				} catch (InterruptedException e1) {
				}
			}
		};
		
		si.addKeyListener(cbkl);

		
		Integer choice = null;
		while (choice == null) {
			try {
				choice = titleSelectionHandler.take();
				if (choice.intValue() == 1){
					UserInterface.getUI().showSystemMessage("This mode isn't yet available.");
					choice = null;
				}
			} catch (InterruptedException e1) {}
		}
		
		
		
		si.remove(historyButton);
		si.remove(expeditionButton);
		si.remove(resumeButton);
		si.remove(exitButton);
		si.removeKeyListener(cbkl);
		si.recoverFocus();
		return choice;
		
	}
	
	public int selectScenario(){
		si.drawImage(0, uiProperties.getProperty("IMG_BLANK2"));
		si.print(0, 24, 100, "Please select an scenario", COLOR_BOLD);
		
		Image image = null;
		try {
			image = ImageUtils.createImage(uiProperties.getProperty("BTN_THE_NEW_WORLD"));
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		CleanButton theNewWorldButton = new CleanButton(image, HAND_CURSOR);
		theNewWorldButton.setBounds(new Rectangle(560, 15, 230, 264));
		ExpeditionCleanButton useButton = new ExpeditionCleanButton(2, "Use");
		useButton.setLocation(622,291);
		
		ExpeditionCleanButton backButton = new ExpeditionCleanButton(2, "< Back");
		backButton.setLocation(622,400);
		
		si.add(theNewWorldButton);
		si.add(useButton);
		si.add(backButton);
		si.commitLayer(0);
		BlockingQueue<Integer> titleSelectionHandler = new LinkedBlockingQueue<Integer>();

		theNewWorldButton.addActionListener(new CallbackActionListener<Integer>(titleSelectionHandler){
			@Override
			public void actionPerformed(ActionEvent e) {
				((JButton)e.getSource()).removeActionListener(this);
				int xstart = 3;
				int ystart = 5;
				si.print(0, xstart, ystart + 1, "The New World", COLOR_BOLD);
				si.print(0, xstart+3, ystart + 2, "Official Scenario by Slashware Interactive", Color.WHITE);
				si.print(0, xstart, ystart + 3, "Date", COLOR_BOLD);
				si.print(0, xstart+20, ystart + 3, "July 5, 1492", Color.WHITE);
				si.print(0, xstart, ystart + 4, "Location", COLOR_BOLD);
				si.print(0, xstart+20, ystart + 4, "Palos de la Frontera, Spain", Color.WHITE);
				si.print(0, xstart, ystart + 5, "Expeditionary", COLOR_BOLD);
				si.print(0, xstart+20, ystart + 5, "Christopher Colombus", Color.WHITE);
				si.print(0, xstart+3, ystart + 6, "Navigation", COLOR_BOLD);
				si.print(0, xstart+3, ystart + 7, "Cartography", COLOR_BOLD);
				si.print(0, xstart+3, ystart + 8, "Negotiation", COLOR_BOLD);
				si.print(0, xstart+3, ystart + 9, "Land Combat", COLOR_BOLD);
				si.print(0, xstart+3, ystart + 10, "Sea Combat", COLOR_BOLD);
				
				si.print(0, xstart+20, ystart + 6, "Expert", Color.WHITE);
				si.print(0, xstart+20, ystart + 7, "Good", Color.WHITE);
				si.print(0, xstart+20, ystart + 8, "Normal", Color.WHITE);
				si.print(0, xstart+20, ystart + 9, "Unexperienced", Color.WHITE);
				si.print(0, xstart+20, ystart + 10, "Unexperienced", Color.WHITE);
				si.commitLayer(0);
			}
		});
		
		useButton.addActionListener(new CallbackActionListener<Integer>(titleSelectionHandler){
			public void actionPerformed(ActionEvent ev2) {
				int xstart = 3;
				int ystart = 5;
				si.drawImage(0, uiProperties.getProperty("IMG_BLANK2"));
				si.print(0, xstart, ystart + 1, "The New World", COLOR_BOLD);
				si.print(0, xstart+3, ystart + 2, "Official Scenario by Slashware Interactive", Color.WHITE);
				si.print(0, xstart, ystart + 3, "Date", COLOR_BOLD);
				si.print(0, xstart+20, ystart + 3, "July 5, 1492", Color.WHITE);
				si.print(0, xstart, ystart + 4, "Location", COLOR_BOLD);
				si.print(0, xstart+20, ystart + 4, "Palos de la Frontera, Spain", Color.WHITE);
				si.print(0, xstart, ystart + 5, "Expeditionary", COLOR_BOLD);
				si.print(0, xstart+20, ystart + 5, "Christopher Colombus", Color.WHITE);
				si.print(0, xstart+3, ystart + 6, "Navigation", COLOR_BOLD);
				si.print(0, xstart+3, ystart + 7, "Cartography", COLOR_BOLD);
				si.print(0, xstart+3, ystart + 8, "Negotiation", COLOR_BOLD);
				si.print(0, xstart+3, ystart + 9, "Land Combat", COLOR_BOLD);
				si.print(0, xstart+3, ystart + 10, "Sea Combat", COLOR_BOLD);
				
				si.print(0, xstart+20, ystart + 6, "Expert", Color.WHITE);
				si.print(0, xstart+20, ystart + 7, "Good", Color.WHITE);
				si.print(0, xstart+20, ystart + 8, "Normal", Color.WHITE);
				si.print(0, xstart+20, ystart + 9, "Unexperienced", Color.WHITE);
				si.print(0, xstart+20, ystart + 10, "Unexperienced", Color.WHITE);
				si.print(0, xstart+20, ystart + 10, "Unexperienced", Color.WHITE);
				try {
					handler.put(0);
				} catch (InterruptedException e) {}
			};
		});
		
		backButton.addActionListener(new CallbackActionListener<Integer>(titleSelectionHandler){
			public void actionPerformed(ActionEvent ev2) {
				try {
					handler.put(1);
				} catch (InterruptedException e) {}
			};
		});
		
		Integer choice = null;
		while (choice == null){
			try {
				choice = titleSelectionHandler.take();
			} catch (InterruptedException e1) {}
		}
		
		si.remove(theNewWorldButton);
		si.remove(useButton);
		si.remove(backButton);
		si.recoverFocus();
		return choice;
		
	}
	
	public void showIntro(Expedition e){
		si.drawImage(0, uiProperties.getProperty("IMG_THE_NEW_WORLD_INTRO"));
		si.commitLayer(0);
		String message = CommonUI.getIntroText();
		message = message.replaceAll("XXX", "\n");
		((ExpeditionOryxUI)UserInterface.getUI()).showTextBox(message, 16, 216, 776, 376);
		
	}

	public void showHelp(){
		si.saveLayer(0);
		si.saveLayer(2);
		si.cleanLayer(2);
		((GFXUISelector)UserInterface.getUI().getPlayer().getSelector()).deactivate();
		((ExpeditionOryxUI)UserInterface.getUI()).messageBox.setVisible(false);
		((ExpeditionOryxUI)UserInterface.getUI()).persistantMessageBox.setVisible(false);
		
		si.cls(0);
		si.print(0, 3, 1, "== Keyboard Reference ==", Color.CYAN);
		si.print(0, 3, 2, "== Movement ==", Color.CYAN);
		si.print(0, 3, 3, " On Foot ", Color.CYAN);
		si.print(0, 3, 4, "Move Around using the numpad ", Color.WHITE);
		si.print(0, 3, 5, "or the directional keys", Color.WHITE);
		
		si.print(0, 40, 3, " Sailing", Color.CYAN);
		si.print(0, 40, 4, "Rotate your ships using Left/Right", Color.WHITE);
		si.print(0, 40, 5, "Advance with any other direction", Color.WHITE);
		
		si.print(0, 3, 6, " ", Color.WHITE);
		si.print(0, 3, 7, "== Commands ==", Color.CYAN);
		
		si.print(0, 3, 8, "  A: Drop anchors / Weigh anchors", Color.WHITE);
		si.print(0, 3, 9, "  a: Arm / Disarm expedition", Color.WHITE);
		si.print(0, 3,10, "  b: Build a Settlement", Color.WHITE);
		si.print(0, 3,11, "  d: Transfer equipment / Make Landfall", Color.WHITE);
		si.print(0, 3,12, "  i: Show inventory", Color.WHITE);
		si.print(0, 3,13, "  l: Look around", Color.WHITE);
		si.print(0, 3,14, "  m: Ride/Unmount your mounts", Color.WHITE);
		si.print(0, 3,15, "  r: Repair damaged ships", Color.WHITE);
		si.print(0, 3,16, "  R: Reset dead' reckon counter", Color.WHITE);
		si.print(0, 3,17, "  w: Chop wood from forests", Color.WHITE);
		si.print(0, 3,18, "  F: Change SFX volume", Color.WHITE);
		si.print(0, 3,19, "  M: Change Music Volume", Color.WHITE);
		si.print(0, 3,20, "  S: Save Game", Color.WHITE);
		si.print(0, 3,21, "  Q: Quit", Color.WHITE);
		si.print(0, 3,22, "  ", Color.WHITE);
		si.print(0, 3,23, "  Press Space or Click to continue", Color.CYAN);
		si.commitLayer(2);
		si.commitLayer(0);

		si.waitKeysOrClick(CharKey.SPACE, CharKey.ENTER);
		   
		si.loadLayer(0);
		si.loadLayer(2);
	}
	
	public void init(SwingSystemInterface syst){
		si = syst;
	}
	
	public int showSavedGames(File[] saveFiles){
		si.drawImage(0, IMG_BLANK);
		
		if (saveFiles == null || saveFiles.length == 0){
			
			si.print(0, 3,6, "No expeditions available",Color.WHITE);
			si.print(0, 4,8, "[Esc to Cancel]",Color.WHITE);
			si.commitLayer(0);
			si.waitKey(CharKey.ESC);
			return -1;
		}
			
		si.print(0, 3,6, "Pick an Expedition",Color.WHITE);
		List<GFXMenuItem> items = new ArrayList<GFXMenuItem>();
		for (int i = 0; i < saveFiles.length; i++){
			String saveFileName = saveFiles[i].getName();
			SimpleGFXMenuItem saveFileItem = new SimpleGFXMenuItem(saveFileName.substring(0,saveFileName.indexOf(".sav")), i);
			items.add(saveFileItem);
		}
		
		BorderedMenuBox menuBox = ((ExpeditionOryxUI)UserInterface.getUI()).createBorderedMenuBox();
		menuBox.setLegend("Pick an Expedition");
		menuBox.setMenuItems(items);
		menuBox.setItemsPerPage(10);
		menuBox.setBounds(20,20,400,400);
		SimpleGFXMenuItem selected = (SimpleGFXMenuItem) menuBox.getSelection();
		
		if (selected == null)
			return -1;
		else
			return selected.getValue();
	}
	
	public void showTextBox(String text, int consoleX, int consoleY, int consoleW, int consoleH){
		addornedTextArea.setBounds(consoleX, consoleY, consoleW, consoleH);
		addornedTextArea.setText(text);
		addornedTextArea.setVisible(true);
		si.waitKeys(CharKey.SPACE, CharKey.ENTER);
		addornedTextArea.setVisible(false);
	}
	
	public void showTextBox(String title, String text, int consoleX, int consoleY, int consoleW, int consoleH){
		showTextBox (title+" "+text, consoleX, consoleY, consoleW, consoleH);
	}
	
	public void showTextBoxNoWait(String text, int consoleX, int consoleY, int consoleW, int consoleH){
		addornedTextArea.setBounds(consoleX, consoleY, consoleW, consoleH);
		addornedTextArea.setText(text);
		addornedTextArea.setVisible(true);
	}
	
	public void clearTextBox(){
		addornedTextArea.setVisible(false);	
	}
	
	public boolean showTextBoxPrompt(String text, int consoleX, int consoleY, int consoleW, int consoleH){
		addornedTextArea.setBounds(consoleX, consoleY, consoleW, consoleH);
		addornedTextArea.setText(text);
		addornedTextArea.setVisible(true);
		CharKey x = new CharKey(CharKey.NONE);
		while (x.code != CharKey.Y && x.code != CharKey.y && x.code != CharKey.N && x.code != CharKey.n)
			x = si.inkey();
		boolean ret = (x.code == CharKey.Y || x.code == CharKey.y);
		addornedTextArea.setVisible(false);
		return ret;
	}
	
	public void showTextBox(String text, int consoleX, int consoleY, int consoleW, int consoleH, Font f){
		addornedTextArea.setBounds(consoleX, consoleY, consoleW, consoleH);
		addornedTextArea.setText(text);
		addornedTextArea.setFont(f);
		addornedTextArea.setVisible(true);
		si.waitKeys(CharKey.SPACE, CharKey.ENTER);
		addornedTextArea.setVisible(false);
	}

	private int readAlphaToNumber(int numbers){
		while (true){
			CharKey key = si.inkey();
			if (key.code >= CharKey.A && key.code <= CharKey.A + numbers -1){
				return key.code - CharKey.A;
			}
			if (key.code >= CharKey.a && key.code <= CharKey.a + numbers -1){
				return key.code - CharKey.a;
			}
		}
	}
	
	//private Color TRANSPARENT_BLUE = new Color(100,100,100,200);
	
	public void showScreen(Object pScreen){
		si.saveLayer(0);
		String screenText = (String) pScreen;
		showTextBox(screenText, 430, 70,340,375);
		//si.waitKey(CharKey.SPACE);
		si.loadLayer(0);
	}

	public static JTextArea createTempArea(int xpos, int ypos, int w, int h){
		JTextArea ret = new JTextArea();
		ret.setOpaque(false);
		ret.setForeground(Color.WHITE);
		ret.setVisible(true);
		ret.setEditable(false);
		ret.setFocusable(false);
		ret.setBounds(xpos, ypos, w, h);
		ret.setLineWrap(true);
		ret.setWrapStyleWord(true);
		ret.setFont(FNT_TEXT);
		return ret;
	}
	
	@Override
	public Expedition createExpedition(ExpeditionGame game) {
		Image image = null;
		try {
			image = ImageUtils.createImage(uiProperties.getProperty("BTN_THE_NEW_WORLD"));
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		si.drawImage(0, 560, 15, image);

		ExpeditionCleanButton okButton = new ExpeditionCleanButton(4, "Ok");
		okButton.setLocation(350, 495);
		
		final BlockingQueue<Integer> inputQueue = si.getInputQueue();
		
		String defaultName = "West Indies";
		
		okButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					inputQueue.put(CharKey.ENTER);
				} catch (InterruptedException e1) {}
			}
		});
		
		si.add(okButton);
		si.printAtPixel(0, 128, 428, "Enter a name for the Expedition", Color.WHITE);
		si.commitLayer(0);
		
		String name = "";
		while (name.trim().equals("")){
			name = si.input(2, 222, 463, Color.WHITE, 20 , defaultName);
			if (name.trim().equals("")){
				UserInterface.getUI().showSystemMessage("Please enter a name for the Expedition.");
				si.commitLayer(0);
			}
		}
		si.remove(okButton);
		return ExpeditionFactory.createPlayerExpedition(name, "Colombus", game);
	}
	
}