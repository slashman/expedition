package net.slashie.expedition.action;



import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.ExpeditionItem;
import net.slashie.expedition.item.ItemFactory;
import net.slashie.expedition.world.ExpeditionMacroLevel;
import net.slashie.expedition.world.OverworldExpeditionCell;
import net.slashie.serf.action.Action;
import net.slashie.serf.action.Actor;
import net.slashie.utils.Util;

public class ForageFood extends Action{

	@Override
	public void execute() {
		Expedition expedition = (Expedition) performer;
		expedition.forageFood();
	}
	
	private String invalidationMessage;
	@Override
	public boolean canPerform(Actor a) {
		performer = a;
		if (!(performer.getLevel() instanceof ExpeditionMacroLevel)){
			invalidationMessage = "You can't forage here";
			return false;
		}
		if (!((Expedition)a).forageFood()){
			a.getLevel().addMessage("You find nothing");
		}
		return true;
	}
	
	@Override
	public String getInvalidationMessage() {
		return invalidationMessage;
	}

	@Override
	public String getID() {
		return "ForageFood";
	}
	
	@Override
	public int getCost() {
		return 120;
	}

}
