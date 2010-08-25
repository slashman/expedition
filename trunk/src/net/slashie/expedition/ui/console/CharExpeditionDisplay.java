package net.slashie.expedition.ui.console;

import java.io.File;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.ui.ExpeditionDisplay;
import net.slashie.libjcsi.CharKey;
import net.slashie.libjcsi.ConsoleSystemInterface;
import net.slashie.serf.sound.STMusicManagerNew;

public class CharExpeditionDisplay extends ExpeditionDisplay{
	private ConsoleSystemInterface csi;
	public CharExpeditionDisplay(ConsoleSystemInterface csi){
		this.csi = csi;	
	}
	
	@Override
	public void showIntro(Expedition expedition) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int showSavedGames(File[] saveFiles) {
		csi.cls();
		if (saveFiles == null || saveFiles.length == 0){
			csi.print(3,6, "No expeditions available");
			csi.print(4,8, "[Space to Cancel]");
			csi.refresh();
			csi.waitKey(CharKey.SPACE);
			return -1;
		}
			
		csi.print(3,6, "Pick an expedition");
		for (int i = 0; i < saveFiles.length; i++){
			csi.print(5,7+i, (char)(CharKey.a+i+1)+ " - "+ saveFiles[i].getName());
		}
		csi.print(3,9+saveFiles.length, "[Space to Cancel]");
		csi.refresh();
		CharKey x = csi.inkey();
		while ((x.code < CharKey.a || x.code > CharKey.a+saveFiles.length) && x.code != CharKey.SPACE){
			x = csi.inkey();
		}
		csi.cls();
		if (x.code == CharKey.SPACE)
			return -1;
		else
			return x.code - CharKey.a;
	}

	@Override
	public int showTitleScreen() {
		csi.cls();
		csi.print(18, 4, "EXPEDITION", ConsoleSystemInterface.CYAN);
		csi.print(20, 5, "The New World", ConsoleSystemInterface.BLUE);
		csi.print(20,12, "a. Create Expedition", ConsoleSystemInterface.WHITE);
		csi.print(20,13, "b. Resume Expedition", ConsoleSystemInterface.WHITE);
		csi.print(20,14, "c. Quit", ConsoleSystemInterface.WHITE);
		csi.print(18,21, ExpeditionGame.getVersion(), ConsoleSystemInterface.CYAN);
		csi.print(18,22, "Santiago Zapata - 2009-2010", ConsoleSystemInterface.CYAN);
		
		csi.refresh();
    	STMusicManagerNew.thus.playKey("TITLE");
    	CharKey x = new CharKey(CharKey.NONE);
		while (x.code != CharKey.A && x.code != CharKey.a &&
				x.code != CharKey.B && x.code != CharKey.b &&
				x.code != CharKey.C && x.code != CharKey.c)
			x = csi.inkey();
		csi.cls();
		switch (x.code){
		case CharKey.A: case CharKey.a:
			return 0;
		case CharKey.B: case CharKey.b:
			return 1;
		case CharKey.C: case CharKey.c:
			return 2;
		}
		return 0;
	}
	
	@Override
	public void showHelp() {
		csi.saveBuffer();
		csi.cls();
		csi.print(6, 1, " == Commands ==", ConsoleSystemInterface.CYAN);
		csi.print(6, 3, "  /---\\     ", ConsoleSystemInterface.WHITE);
		csi.print(6, 4, "  |789|  Move Around using the numpad or", ConsoleSystemInterface.WHITE);
		csi.print(6, 5, "  |4 6|  The directional keys.", ConsoleSystemInterface.WHITE);
		csi.print(6, 6, "  |123|", ConsoleSystemInterface.WHITE);
		csi.print(6, 7, "  \\---/", ConsoleSystemInterface.WHITE);
		csi.print(6, 8, "  ", ConsoleSystemInterface.WHITE);
		csi.print(6, 9, " == In the overworld ==", ConsoleSystemInterface.CYAN);
		csi.print(6,10, "  ", ConsoleSystemInterface.WHITE);
		csi.print(6,11, "  a: Arm / Disarm expedition", ConsoleSystemInterface.WHITE);
		csi.print(6,12, "  b: Build a Settlement", ConsoleSystemInterface.WHITE);
		csi.print(6,13, "  d: Drop equipment", ConsoleSystemInterface.WHITE);
		csi.print(6,14, "  i: Show inventory", ConsoleSystemInterface.WHITE);
		csi.print(6,15, "  l: Look around", ConsoleSystemInterface.WHITE);
		csi.print(6,16, "  r: Repair damaged ships", ConsoleSystemInterface.WHITE);
		csi.print(6,17, "  R: Reset dead' reckon counter", ConsoleSystemInterface.WHITE);
		csi.print(6,18, "  w: Chop wood from forests", ConsoleSystemInterface.WHITE);
		csi.print(6,19, "  S: Save Game", ConsoleSystemInterface.WHITE);
		csi.print(6,20, "  Q: Quit", ConsoleSystemInterface.WHITE);
		
		csi.print(6,21, "  ", ConsoleSystemInterface.WHITE);
		csi.print(6,22, "  Press Space to continue", ConsoleSystemInterface.CYAN);
		csi.refresh();

		csi.waitKey(CharKey.SPACE);
		   
		csi.restore();
		csi.refresh();
	}

}
