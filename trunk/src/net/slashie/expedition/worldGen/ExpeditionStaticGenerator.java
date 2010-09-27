package net.slashie.expedition.worldGen;

import net.slashie.expedition.domain.ExpeditionItem;
import net.slashie.expedition.domain.ExpeditionUnit;
import net.slashie.expedition.domain.NPC;
import net.slashie.expedition.item.ItemFactory;
import net.slashie.serf.level.AbstractLevel;
import net.slashie.serf.levelGeneration.StaticGenerator;
import net.slashie.utils.Position;

public class ExpeditionStaticGenerator extends StaticGenerator{
	@Override
	public void handleSpecialRenderCommand(AbstractLevel l, Position where, String[] cmds, int x, int y) {
		if (cmds[1].equals("ITEM")){
			ExpeditionItem item = ItemFactory.createItem(cmds[2]);
			l.addItem(Position.add(where, new Position(x,y)), item);
		} else if (cmds[1].equals("NPC")){
			NPC npc = new NPC ((ExpeditionUnit)ItemFactory.createItem(cmds[2]));
			npc.setPosition(where.x+x,where.y+y,where.z);
			l.addActor(npc);
		}
	}
}
