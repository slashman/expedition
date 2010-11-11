package net.slashie.expedition.ui.oryx;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JTextArea;

import net.slashie.libjcsi.CharKey;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.ExpeditionFactory;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.game.GameFiles;
import net.slashie.expedition.ui.ExpeditionDisplay;
import net.slashie.serf.game.SworeGame;
import net.slashie.serf.sound.STMusicManagerNew;
import net.slashie.serf.ui.UserInterface;
import net.slashie.serf.ui.oryxUI.AddornedBorderTextArea;
import net.slashie.serf.ui.oryxUI.SwingSystemInterface;
import net.slashie.utils.FileUtil;
import net.slashie.utils.ImageUtils;
import net.slashie.utils.PropertyFilters;

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
	
	public static Color COLOR_BOLD;
	
	private void initProperties(Properties p){
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
		((ExpeditionOryxUI)UserInterface.getUI()).messageBox.setVisible(false);
		((ExpeditionOryxUI)UserInterface.getUI()).persistantMessageBox.setVisible(false);
		STMusicManagerNew.thus.playKey("TITLE");
		
		si.setFont(FNT_TEXT);
		si.drawImage(IMG_TITLE);
		//si.drawImage(215,60,IMG_TITLE_NAME);
		si.printAtPixel(91, 540, "Expedition "+ExpeditionGame.getVersion()+", Developed by Santiago Zapata 2009-2010", Color.WHITE);
		si.printAtPixel(164, 558, "Artwork by Oryx - Music by Mingos and Jice", Color.WHITE);
		CharKey x = new CharKey(CharKey.NONE);
    	int choice = 0;
    	si.saveBuffer();
    	out: while (true) {
    		String registrant = null;
    		String supporterLevel = "Unregistered";
    		try {
				BufferedReader r = FileUtil.getReader("registration.key");
				String key = r.readLine();
				r.close();
				String decoded = GameFiles.decode(key);
				registrant = decoded.split(",")[0];
				supporterLevel = decoded.split(",")[1];
    		} catch (FileNotFoundException e) {
    			registrant = null;
			} catch (Exception e) {
				e.printStackTrace();
				registrant = null;
			}
    		si.restore();
    		if (registrant == null || registrant.equals("unregistered")){
    			si.printAtPixel(10, 586, "Unregistered Version", Color.WHITE);
    		} else {
    			si.printAtPixel(10, 586, "Registered for "+supporterLevel+" "+registrant+"!", Color.YELLOW);
    		}
    		si.drawImage(320, 404+choice*26, IMG_PICKER);
    		si.printAtPixel(350,428, "a. Create Expedition", Color.WHITE);
    		si.printAtPixel(350,454, "b. Resume Expedition", Color.WHITE);
    		
    		si.printAtPixel(350,480, "c. Quit", Color.WHITE);
    		si.refresh();
			while (x.code != CharKey.A && x.code != CharKey.a &&
					x.code != CharKey.B && x.code != CharKey.b &&
					x.code != CharKey.C && x.code != CharKey.c &&
					x.code != CharKey.UARROW && x.code != CharKey.DARROW &&
					x.code != CharKey.SPACE && x.code != CharKey.ENTER)
				x = si.inkey();
			switch (x.code){
			case CharKey.A: case CharKey.a:
				return 0;
			case CharKey.B: case CharKey.b:
				return 1;
			case CharKey.C: case CharKey.c:
				return 2;
			case CharKey.UARROW:
				if (choice > 0)
					choice--;
				break;
			case CharKey.DARROW:
				if (choice < 2)
					choice++;
				break;
			case CharKey.SPACE: case CharKey.ENTER:
				return choice;
			}
			x.code = CharKey.NONE;
		}
	}
	
	

	public void showIntro(Expedition e){
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
		((ExpeditionOryxUI)UserInterface.getUI()).messageBox.setVisible(false);
		((ExpeditionOryxUI)UserInterface.getUI()).persistantMessageBox.setVisible(false);
		STMusicManagerNew.thus.playKey("TITLE");
		
		si.setFont(FNT_TEXT);
		String name = "";
		while (name.trim().equals("")){
			si.drawImage(IMG_TITLE);
			si.printAtPixel(91, 540, "Expedition "+ExpeditionGame.getVersion()+", Developed by Santiago Zapata 2009-2010", Color.WHITE);
			si.printAtPixel(164, 558, "Artwork by Oryx - Music by Mingos and Jice", Color.WHITE);
			
			si.printAtPixel(128, 428, "Please, by what name are your explorations to be known?", Color.WHITE);
			name = si.input(222, 463, Color.WHITE, 10);
		}
		return ExpeditionFactory.createPlayerExpedition(name, game);
	}
	
}