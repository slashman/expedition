package net.slashie.expedition.action.debug;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.item.ItemFactory;
import net.slashie.serf.action.Action;

public class MagicAction extends Action{
	@Override
	public void execute() {
		((Expedition)performer).addItem(ItemFactory.createItem("PLATE"), 10);
	}
	
	@Override
	public String getID() {
		return "MagicAction";
	}
	
	@Override
	public int getCost() {
		return 0;
	}
}
