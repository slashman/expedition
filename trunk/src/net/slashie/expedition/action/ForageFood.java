package net.slashie.expedition.action;

import java.util.List;

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
		
		ExpeditionMacroLevel level = (ExpeditionMacroLevel)  expedition.getLocation();
		
		OverworldExpeditionCell cell = (OverworldExpeditionCell) performer.getLevel().getMapCell(performer.getPosition());
		if (Util.chance(cell.getForageChance())){
			String food = "";
			if (cell.isRiver()){
				level.addMessage("You get some fish.");
				food = "FISH";
			} else {
				level.addMessage("You forage some fruits.");
				food = "FRUIT";
			}
			ExpeditionItem foodSample = ItemFactory.createItem(food);
			expedition.addItem(foodSample, cell.getForageQuantity());
		} else {
			level.addMessage("You find nothing.");
		}
	}
	
	private String invalidationMessage;
	@Override
	public boolean canPerform(Actor a) {
		performer = a;
		if (!(performer.getLevel() instanceof ExpeditionMacroLevel)){
			invalidationMessage = "You can't forage here";
			return false;
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
