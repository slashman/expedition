package net.slashie.expedition.worldGen;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.ExpeditionFactory;
import net.slashie.expedition.domain.NonPrincipalExpedition;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.serf.level.AbstractFeature;
import net.slashie.serf.level.AbstractLevel;
import net.slashie.serf.level.FeatureFactory;
import net.slashie.serf.levelGeneration.StaticGenerator;
import net.slashie.utils.Position;

public class ExpeditionStaticGenerator extends StaticGenerator{
	@Override
	public void handleSpecialRenderCommand(AbstractLevel l, Position where, String[] cmds, int x, int y) {
		if (cmds[1].equals("EXPEDITION")){
			Expedition expedition = ExpeditionFactory.getExpedition(cmds[2]);
			expedition.setPosition(where.x+x,where.y+y,where.z);
			l.addActor(expedition);
		} 
	}
}
