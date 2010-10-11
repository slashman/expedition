package net.slashie.expedition.action;

import java.util.ArrayList;
import java.util.List;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.Town;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.town.Building;
import net.slashie.expedition.town.BuildingFactory;
import net.slashie.expedition.ui.ExpeditionUserInterface;
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

public class BuildBuildings extends Action{
	private int netTimeCost;
	private Town town;
	private List<Building> buildingPlan;
	@Override
	public void execute() {
		//Confirm
		if (getTown() == null){
			msg("Invalid");
			return;
		}
		Expedition expedition = (Expedition)performer;
		buildingPlan = ((ExpeditionUserInterface)UserInterface.getUI()).createBuildingPlan();
		if (buildingPlan.size() == 0){
			netTimeCost = 0;
			return;
		}
		OutParameter woodCost = new OutParameter();
		OutParameter netTimeCostObj = new OutParameter();
		try {
			BuildingFactory.getPlanCost(buildingPlan, expedition, netTimeCostObj, woodCost);
		} catch (ActionCancelException e) {
			netTimeCost = 0;
			return;
		}
		netTimeCost = netTimeCostObj.getIntValue();
		int daysCost = (int)Math.ceil((double)netTimeCost / (double)DayShiftAgent.TICKS_PER_DAY);
		String message = "";
		if (buildingPlan.size() == 1){
			message = "Building that "+buildingPlan.get(0).getDescription()+ " will cost "+woodCost.getIntValue()+" wood and will take about "+daysCost+" days. Are you sure?";			
		} else {
			message = "Building these "+buildingPlan.size()+" buildings will cost "+woodCost.getIntValue()+" wood and will take about "+daysCost+" days. Are you sure?";
		}
		if (!UserInterface.getUI().promptChat(message)){
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

	@Override
	public String getID() {
		return "BuildBuildings";
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
		return true;
	}
	
	@Override
	public String getInvalidationMessage() {
		if (!(performer.getLevel() instanceof ExpeditionMacroLevel))
			return "You can't build a town here!";
		if (!((OverworldExpeditionCell) performer.getLevel().getMapCell(performer.getPosition())).isLand())
			return "You can't build a town here!";
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
		try {
			BuildingFactory.executeConstructionPlan(town, buildingPlan, expedition);
		} catch (ActionCancelException e) {
			return;
		}
	}
	
	@Override
	public void executionInterrupted() {
		msg("You stop the construction");
	}

	public Town getTown() {
		return town;
	}

	public void setTown(Town town) {
		this.town = town;
	}

}
