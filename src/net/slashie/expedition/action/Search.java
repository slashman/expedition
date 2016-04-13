package net.slashie.expedition.action;

import java.util.List;
import java.util.Random;
import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.item.ItemFactory;
import net.slashie.expedition.world.ExpeditionMacroLevel;
import net.slashie.expedition.world.OverworldExpeditionCell;
import net.slashie.serf.action.Action;
import net.slashie.util.Pair;
import net.slashie.util.Util;
import net.slashie.utils.Position;

public class Search extends Action{
	private static final long serialVersionUID = 1L;
	
	@Override
	public String getID() {
		return "Search around";
	}

	@Override
	public void execute() {
		Expedition e = (Expedition)performer;
		List<Pair<Position, OverworldExpeditionCell>> around = ((ExpeditionMacroLevel)e.getLevel()).getMapCellsAndPositionsAround(e.getPosition());
		for (Pair<Position, OverworldExpeditionCell> a: around){
			if (a.getB().isRiver())
			{
				Random random = new Random();
				int amount = random.nextInt(200-10 +1) + 10;				
				e.addItem(ItemFactory.createItem("FRESHWATER"), amount);
				e.getLevel().addMessage("Found " + amount + " of fresh water");
			}
		}
	}
	
}
