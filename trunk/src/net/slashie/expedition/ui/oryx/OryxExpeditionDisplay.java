package net.slashie.expedition.ui.oryx;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextArea;

import net.slashie.libjcsi.CharKey;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.ExpeditionFactory;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.game.GameFiles;
import net.slashie.expedition.game.GameFiles.LicenseInfo;
import net.slashie.expedition.ui.ExpeditionDisplay;
import net.slashie.serf.game.SworeGame;
import net.slashie.serf.sound.STMusicManagerNew;
import net.slashie.serf.ui.UserInterface;
import net.slashie.serf.ui.oryxUI.AddornedBorderTextArea;
import net.slashie.serf.ui.oryxUI.SwingSystemInterface;
import net.slashie.utils.ImageUtils;
import net.slashie.utils.PropertyFilters;
import net.slashie.utils.swing.CallbackActionListener;
import net.slashie.utils.swing.CallbackHandler;
import net.slashie.utils.swing.CleanButton;

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
	
	private void initProperties(Properties p){
		uiProperties = p;
		IMG_TITLE = p.getProperty("IMG_TITLE");
		IMG_BLANK = p.getProperty("IMG_BLANK");
		COLOR_BOLD = PropertyFilters.getColor(p.getProperty("COLOR_BOLD"));
		
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
		STMusicManagerNew.thus.playKey("TITLE");
		
		si.setFont(FNT_TEXT);
		si.drawImage(IMG_TITLE);
		si.printAtPixel(30, 540, "Version "+ExpeditionGame.getVersion()+", Slashware Interactive 2009-2011", Color.WHITE);
		si.printAtPixel(30, 558, "Artwork by Oryx - Music by Mingos and Jice", Color.WHITE);
		
   	
    	// Read the license info 
    	LicenseInfo licenseInfo = GameFiles.getLicenseInfo();
		
		if (licenseInfo.licensee == null || licenseInfo.licensee.equals("unregistered")){
			si.printAtPixel(30, 586, "Unregistered Version", Color.WHITE);
		} else {
			si.printAtPixel(30, 586, "Registered for "+licenseInfo.licenseLevel+" "+licenseInfo.licensee+"!", Color.YELLOW);
		}
		
		CleanButton historyButton = new CleanButton(new ImageIcon(uiProperties.getProperty("BTN_HISTORY")));
		historyButton.setBounds(new Rectangle(558, 30, 223, 43));
		CleanButton expeditionButton = new CleanButton(new ImageIcon(uiProperties.getProperty("BTN_EXPEDITION")));
		expeditionButton.setBounds(new Rectangle(558, 78, 223, 43));
		CleanButton resumeButton = new CleanButton(new ImageIcon(uiProperties.getProperty("BTN_CONTINUE")));
		resumeButton.setBounds(new Rectangle(558, 126, 223, 43));
		CleanButton exitButton = new CleanButton(new ImageIcon(uiProperties.getProperty("BTN_EXIT")));
		exitButton.setBounds(new Rectangle(558, 174, 223, 43));
		
		
		si.add(historyButton);
		si.add(expeditionButton);
		si.add(resumeButton);
		si.add(exitButton);
		
		si.refresh();
		
		BlockingQueue<Integer> titleSelectionHandler = new LinkedBlockingQueue<Integer>();

		/*CallbackHandler titleSelectionHandler = new CallbackHandler();
		titleSelectionHandler.setCallback(null);*/
		
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
		
		Integer choice = null;
		try {
			while (choice == null)
				choice = titleSelectionHandler.take();
		} catch (InterruptedException e1) {
		}
		
		si.remove(historyButton);
		si.remove(expeditionButton);
		si.remove(resumeButton);
		si.remove(exitButton);
		si.recoverFocus();
		return choice;
		
	}
	
	public int selectScenario(){
		CleanButton theNewWorldButton = new CleanButton(new ImageIcon(uiProperties.getProperty("BTN_THE_NEW_WORLD")));
		theNewWorldButton.setBounds(new Rectangle(560, 15, 230, 264));
		si.printAtPixel(30, 440, "Please pick a scenario >>>>>", Color.WHITE);
		si.refresh();

		si.add(theNewWorldButton);
		
		BlockingQueue<Integer> titleSelectionHandler = new LinkedBlockingQueue<Integer>();

		/*CallbackHandler titleSelectionHandler = new CallbackHandler();
		titleSelectionHandler.setCallback(null);*/
		
		theNewWorldButton.addActionListener(new CallbackActionListener<Integer>(titleSelectionHandler){
			@Override
			public void actionPerformed(ActionEvent e) {
				((JButton)e.getSource()).removeActionListener(this);
				int xstart = 3;
				int ystart = 4;
				si.drawImage(uiProperties.getProperty("IMG_BLANK2"));
				si.print(xstart, ystart + 1, "The New World", COLOR_BOLD);
				si.print(xstart+3, ystart + 2, "Official Scenario by Slashware Interactive", Color.WHITE);
				si.print(xstart, ystart + 3, "Date", COLOR_BOLD);
				si.print(xstart+20, ystart + 3, "July 5, 1492", Color.WHITE);
				si.print(xstart, ystart + 4, "Location", COLOR_BOLD);
				si.print(xstart+20, ystart + 4, "Palos de la Frontera, Spain", Color.WHITE);
				si.print(xstart, ystart + 5, "Expeditionary", COLOR_BOLD);
				si.print(xstart+20, ystart + 5, "Cristoforo Colombo", Color.WHITE);
				si.print(xstart+3, ystart + 6, "Navigation", COLOR_BOLD);
				si.print(xstart+3, ystart + 7, "Cartography", COLOR_BOLD);
				si.print(xstart+3, ystart + 8, "Negociation", COLOR_BOLD);
				si.print(xstart+3, ystart + 9, "Land Combat", COLOR_BOLD);
				si.print(xstart+3, ystart + 10, "Sea Combat", COLOR_BOLD);
				
				si.print(xstart+20, ystart + 6, "Expert", Color.WHITE);
				si.print(xstart+20, ystart + 7, "Good", Color.WHITE);
				si.print(xstart+20, ystart + 8, "Normal", Color.WHITE);
				si.print(xstart+20, ystart + 9, "Unexperienced", Color.WHITE);
				si.print(xstart+20, ystart + 10, "Unexperienced", Color.WHITE);
				
				si.print(xstart+3, ystart + 17, "Use this scenario?", Color.WHITE);
				
				CleanButton okButton = new CleanButton(new ImageIcon(uiProperties.getProperty("BTN_OK")));
				okButton.setBounds(286,475,223,43);
				si.add(okButton);
				okButton.addActionListener(new CallbackActionListener<Integer>(handler){
					public void actionPerformed(ActionEvent ev2) {
						try {
							handler.put(0);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						si.remove((Component) ev2.getSource());
					};
				});
			}
		});
		
		Integer choice = null;
		try {
			choice = titleSelectionHandler.take();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		si.remove(theNewWorldButton);
		si.recoverFocus();
		return choice;
		
	}
	
	public void showIntro(Expedition e){
		si.drawImage(uiProperties.getProperty("IMG_THE_NEW_WORLD_INTRO"));
		si.printAtPixel(30, 500, "Press Space to Continue", Color.WHITE);
		si.waitKey(CharKey.SPACE);
	}

	public void showHelp(){
		si.saveBuffer();
		((ExpeditionOryxUI)UserInterface.getUI()).messageBox.setVisible(false);
		((ExpeditionOryxUI)UserInterface.getUI()).persistantMessageBox.setVisible(false);
		
		si.cls();
		si.print(3, 1, "== HELP ==", Color.CYAN);
		si.print(3, 2, "== Movement ==", Color.CYAN);
		si.print(3, 3, " On Foot ", Color.CYAN);
		si.print(3, 4, "Move Around using the numpad ", Color.WHITE);
		si.print(3, 5, "or the directional keys", Color.WHITE);
		
		si.print(40, 3, " Sailing", Color.CYAN);
		si.print(40, 4, "Rotate your ships using Left/Right", Color.WHITE);
		si.print(40, 5, "Advance with any other direction", Color.WHITE);
		
		si.print(3, 6, " ", Color.WHITE);
		si.print(3, 7, "== Commands ==", Color.CYAN);
		
		si.print(3, 8, "  a: Arm / Disarm expedition", Color.WHITE);
		si.print(3, 9, "  b: Build a Settlement", Color.WHITE);
		si.print(3,10, "  d: Drop equipment", Color.WHITE);
		si.print(3,11, "  i: Show inventory", Color.WHITE);
		si.print(3,12, "  l: Look around", Color.WHITE);
		si.print(3,13, "  m: Ride/Unmount your mounts", Color.WHITE);
		si.print(3,14, "  r: Repair damaged ships", Color.WHITE);
		si.print(3,15, "  R: Reset dead' reckon counter", Color.WHITE);
		si.print(3,16, "  w: Chop wood from forests", Color.WHITE);
		si.print(3,17, "  S: Save Game", Color.WHITE);
		si.print(3,18, "  Q: Quit", Color.WHITE);
		si.print(3,19, "  ", Color.WHITE);
		si.print(3,20, "  Press Space to continue", Color.CYAN);
		si.refresh();

		si.waitKey(CharKey.SPACE);
		   
		si.restore();
		si.refresh();
	}
	
	public void init(SwingSystemInterface syst){
		si = syst;
	}
	
	
	public int showSavedGames(File[] saveFiles){
		si.drawImage(IMG_BLANK);
		if (saveFiles == null || saveFiles.length == 0){
			si.print(3,6, "No adventurers available",Color.WHITE);
			si.print(4,8, "[Space to Cancel]",Color.WHITE);
			si.refresh();
			si.waitKey(CharKey.SPACE);
			return -1;
		}
			
		si.print(3,6, "Pick an adventurer",Color.WHITE);
		for (int i = 0; i < saveFiles.length; i++){
			String saveFileName = saveFiles[i].getName();
			si.print(5,7+i, (char)(CharKey.a+i+1)+ " - "+ saveFileName.substring(0,saveFileName.indexOf(".sav")), COLOR_BOLD);
		}
		si.print(3,9+saveFiles.length, "[Space to Cancel]", Color.WHITE);
		si.refresh();
		CharKey x = si.inkey();
		while ((x.code < CharKey.a || x.code > CharKey.a+saveFiles.length-1) && x.code != CharKey.SPACE){
			x = si.inkey();
		}
		if (x.code == CharKey.SPACE)
			return -1;
		else
			return x.code - CharKey.a;
	}
	
	
	public void showTextBox(String text, int consoleX, int consoleY, int consoleW, int consoleH){
		addornedTextArea.setBounds(consoleX, consoleY, consoleW, consoleH);
		addornedTextArea.setText(text);
		addornedTextArea.setVisible(true);
		si.waitKey(CharKey.SPACE);
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
		si.waitKey(CharKey.SPACE);
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
		si.saveBuffer();
		String screenText = (String) pScreen;
		showTextBox(screenText, 430, 70,340,375);
		//si.waitKey(CharKey.SPACE);
		si.restore();
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
		String name = "";
		while (name.trim().equals("")){
			si.printAtPixel(128, 428, "Enter a name for the expedition log", Color.WHITE);
			name = si.input(222, 463, Color.WHITE, 10);
		}
		return ExpeditionFactory.createPlayerExpedition(name, game);
	}
	
}