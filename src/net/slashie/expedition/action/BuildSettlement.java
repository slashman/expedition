package net.slashie.expedition.action;

import java.util.ArrayList;
import java.util.List;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.GoodsCache;
import net.slashie.expedition.domain.Town;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.ui.ExpeditionUserInterface;
import net.slashie.expedition.world.ExpeditionLevel;
import net.slashie.expedition.world.ExpeditionMacroLevel;
import net.slashie.expedition.world.OverworldExpeditionCell;
import net.slashie.expedition.world.SettlementType;
import net.slashie.serf.action.Action;
import net.slashie.serf.action.Actor;
import net.slashie.serf.action.Message;
import net.slashie.serf.game.Player;
import net.slashie.serf.ui.UserInterface;
import net.slashie.utils.Position;

public class BuildSettlement extends Action{
	/*
	Las expediciones continuaron por el interior del territorio con Gonzalo Jiménez de Quezada quien 
	fundó a Santafé de Bogotá el 6 de agosto de 1538. Para consolidar la ciudad se señalaron la plaza mayor, 
	las calles y las carreteras, el lugar donde edificar una iglesia, el Cabildo y otros edificios públicos.
	*/ 
	@Override
	public void execute() {
		//Confirm
		if (!UserInterface.getUI().promptChat("Establish a town? (Y/n)"))
			return;
		Expedition expedition = (Expedition)performer;
		//Check if the land is claimed
		if (expedition.getLocation().getLocation().getB() >= -30){
			msg("This land is claimed already!");
			return;
		}
		
		//Check resources availability (for the plaza)
		if (expedition.getGoodCount("WOOD") < 200){
			msg("You need at least 200 wood for the plaza and the first building.");
			return;
		}
		//Check units availability
		if (expedition.getTotalUnits() < 101){
			msg("You need at least 100 people to found the town.");
			return;
		}
		
		expedition.reduceGood("WOOD", 200);
		
		String cityName = UserInterface.getUI().inputBox("Enter a name for the new town");
		
		// Transfer the units
		Town town = new Town((ExpeditionGame)((Player)performer).getGame());
		town.setName(cityName);
		((ExpeditionUserInterface)UserInterface.getUI()).transferFromExpedition(town, 100);

		//Add the city
		town.setPosition(new Position(performer.getPosition()));
		performer.getLevel().addFeature(town);
	}

	@Override
	public String getID() {
		return "BuildCache";
	}
	
	public void msg(String message){
        UserInterface.getUI().showMessage(message);
	}
	
	@Override
	public boolean canPerform(Actor a) {
		if (!(a.getLevel() instanceof ExpeditionMacroLevel))
			return false;
		if (!((OverworldExpeditionCell) a.getLevel().getMapCell(a.getPosition())).isLand())
			return false;
		if (((Expedition)performer).getLocation().getLocation().getB() >= -30){
			return false;
		}
		return true;
	}
	
	@Override
	public String getInvalidationMessage() {
		if (!(performer.getLevel() instanceof ExpeditionMacroLevel))
			return "You can't build a town here!";
		if (!((OverworldExpeditionCell) performer.getLevel().getMapCell(performer.getPosition())).isLand())
			return "You can't build a town here!";
		if (((Expedition)performer).getLocation().getLocation().getB() >= -30){
			return "This land is claimed already!";
		}
		return "";
	}

}
