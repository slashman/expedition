package net.slashie.expedition.action;

import java.util.ArrayList;
import java.util.List;
import net.ck.expedition.utils.swing.MessengerService;
import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.Town;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.level.GlobeMapModel;
import net.slashie.expedition.town.Building;
import net.slashie.expedition.town.BuildingFactory;
import net.slashie.expedition.world.ExpeditionMacroLevel;
import net.slashie.expedition.world.OverworldExpeditionCell;
import net.slashie.expedition.world.agents.DayShiftAgent;
import net.slashie.serf.action.Action;
import net.slashie.serf.action.Actor;
import net.slashie.serf.game.Player;
import net.slashie.serf.ui.ActionCancelException;
import net.slashie.serf.ui.UserInterface;
import net.slashie.utils.OutParameter;
import net.slashie.utils.Position;

@SuppressWarnings("serial")
public class BuildSettlement extends Action{
	private static final List<Building> DEFAULT_FIRST_TOWN_BUILDINGS = new ArrayList<Building>();
	static {
		DEFAULT_FIRST_TOWN_BUILDINGS.add(BuildingFactory.createBuilding("PLAZA"));
		DEFAULT_FIRST_TOWN_BUILDINGS.add(BuildingFactory.createBuilding("HOUSE"));
		DEFAULT_FIRST_TOWN_BUILDINGS.add(BuildingFactory.createBuilding("HOUSE"));
		DEFAULT_FIRST_TOWN_BUILDINGS.add(BuildingFactory.createBuilding("HOUSE"));
		DEFAULT_FIRST_TOWN_BUILDINGS.add(BuildingFactory.createBuilding("HOUSE"));
	}
	private int netTimeCost;
	private List<Building> firstTownBuildings;

	/*
	Las expediciones continuaron por el interior del territorio con Gonzalo Jiménez de Quezada quien 
	fundó a Santafé de Bogotá el 6 de agosto de 1538. Para consolidar la ciudad se señalaron la plaza mayor, 
	las calles y las carreteras, el lugar donde edificar una iglesia, el Cabildo y otros edificios públicos.
	*/ 
	@Override
	public void execute() {
		Expedition expedition = (Expedition)performer;
		//Check if the land is claimed
		int longitudeDegrees = GlobeMapModel.getSingleton().getLongitudeDegrees(performer.getPosition().x);
		if (longitudeDegrees >= -30){
			msg("This land is claimed already, you can't build a town here!");
			netTimeCost = 0;
			return;
		}
		
		// Check distance from other settlements
		List<Town> towns = expedition.getTowns();
		for (Town town: towns){
			int milesDistance = GlobeMapModel.getSingleton().getMilesDistance(town.getPosition(), expedition.getPosition());
			if (milesDistance < 70){
				msg("We are about "+milesDistance+" miles away from "+town.getName()+", we can not establish another settlement that close!");
				netTimeCost = 0;
				return;
			}
		}
		
		//Confirm
		if (!UserInterface.getUI().promptChat("Establish a town?")){
			netTimeCost = 0;
			return;
		}

		firstTownBuildings = getFirstTownBuildings();
		OutParameter woodCost = new OutParameter();
		OutParameter netTimeCostObj = new OutParameter();
		try {
			BuildingFactory.getPlanCost(firstTownBuildings, expedition, netTimeCostObj, woodCost);
		} catch (ActionCancelException e) {
			netTimeCost = 0;
			return;
		}
		netTimeCost = netTimeCostObj.getIntValue();
		int daysCost = (int)Math.ceil((double)netTimeCost / (double)DayShiftAgent.TICKS_PER_DAY);
		
		if (!UserInterface.getUI().promptChat("Building these "+firstTownBuildings.size()+" buildings will cost "+woodCost.getIntValue()+" wood and will take about "+daysCost+" days. Are you sure?")){
			netTimeCost = 0;
			return;
		}
		
		if (daysCost > expedition.getFoodDays()){
			if (!UserInterface.getUI().promptChat("Your expedition will run out of supplies before finishing construction! Are you sure?")){
				netTimeCost = 0;
				return;
			}
		}
		
	}

	private List<Building> getFirstTownBuildings() {
		return DEFAULT_FIRST_TOWN_BUILDINGS;
	}

	@Override
	public String getID() {
		return "BuildSettlement";
	}
	
	public void msg(String message){
        MessengerService.showImportantMessage(message);
	}
	
	@Override
	public boolean canPerform(Actor a) {
		if (!(a.getLevel() instanceof ExpeditionMacroLevel))
			return false;
		OverworldExpeditionCell standingCell = (OverworldExpeditionCell) a.getLevel().getMapCell(a.getPosition()); 
		if (!standingCell.isLand())
			return false;
		if (standingCell.isRiver())
			return false;
		int longitudeDegrees = GlobeMapModel.getSingleton().getLongitudeDegrees(performer.getPosition().x);
		if (longitudeDegrees >= -30){
			return false;
		}
		return true;
	}
	
	@Override
	public String getInvalidationMessage() {
		if (!(performer.getLevel() instanceof ExpeditionMacroLevel))
			return "You can't build a town here!";
		OverworldExpeditionCell standingCell = (OverworldExpeditionCell) performer.getLevel().getMapCell(performer.getPosition()); 
		if (!standingCell.isLand())
			return "You can't build a town here!";
		if (standingCell.isRiver())
			return "You can't build a town here!";
			
		int longitudeDegrees = GlobeMapModel.getSingleton().getLongitudeDegrees(performer.getPosition().x);
		if (longitudeDegrees >= -30){
			return "This land is claimed already!";
		}
		return "";
	}
	
	@Override
	public int getCost() {
		return netTimeCost;
	}
	
	@Override
	public void executeDisplaced() {
		if (getCost() == 0)
			return;
		Expedition expedition = (Expedition)performer;
		String cityName = UserInterface.getUI().inputBox("Enter a name for the new town");
		Town town = new Town((ExpeditionGame)((Player)performer).getGame());
		town.setName(cityName);
		
		try {
			BuildingFactory.executeConstructionPlan(town, firstTownBuildings, expedition);
		} catch (ActionCancelException e) {
			return;
		}
		
		//Add the city
		town.setPosition(new Position(performer.getPosition()));
		expedition.addTown(town);
		performer.getLevel().addFeature(town);
		expedition.setPosition(town.getPosition());
		UserInterface.getUI().refresh();
	}
	
	@Override
	public void executionInterrupted() {
		msg("You stop the construction");
	}
}
