package net.slashie.expedition.ui.oryx;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.ui.ExpeditionGenerator;
import net.slashie.serf.ui.oryxUI.SwingSystemInterface;

public class OryxPlayerGenerator extends ExpeditionGenerator{
	private SwingSystemInterface ssi;
	public OryxPlayerGenerator (SwingSystemInterface ssi){
		this.ssi = ssi;
	}
	
	@Override
	public Expedition createExpedition(ExpeditionGame game ) {
		return getExpeditionObject(game);
	}
}
