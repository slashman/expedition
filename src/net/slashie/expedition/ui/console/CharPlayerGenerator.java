package net.slashie.expedition.ui.console;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.ui.ExpeditionGenerator;
import net.slashie.libjcsi.ConsoleSystemInterface;

public class CharPlayerGenerator extends ExpeditionGenerator{
	private ConsoleSystemInterface csi;
	public CharPlayerGenerator (ConsoleSystemInterface csi){
		this.csi = csi;
	}
	
	@Override
	public Expedition createExpedition(ExpeditionGame game ) {
		return getExpeditionObject(game);
	}
}
