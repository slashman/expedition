package net.slashie.expedition.ui.oryx;

import java.awt.Image;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.ExpeditionItem;
import net.slashie.expedition.domain.ExpeditionUnit;
import net.slashie.expedition.town.Building;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.ui.oryxUI.GFXAppearance;
import net.slashie.utils.swing.GFXMenuItem;

public class BuildingGFXMenuItem implements GFXMenuItem {
	private Building building;
	private int quantity;
	
	public BuildingGFXMenuItem(Building building) {
		this.building = building;
	}
	
	public Building getBuilding(){
		return building;
	}
	
	public Image getMenuImage() {
		//TODO: Building Images
		return null;
	}
	
	public String getMenuDetail() {
		return "  "+building.getLongDescription();
	}

	public String getMenuDescription() {
		return "["+quantity + "] " + building.getDescription() + " ("+(building.getWoodCost() * quantity)+" wood, "+(building.getBuildTimeCost()*quantity)+" workpower)";
	}
	
	public String getGroupClassifier() {
		return "DITTO";
	}

	public void add() {
		quantity++;
	}
	
	public void remove() {
		if (quantity > 0)
			quantity--;
	}

	public int getQuantity() {
		return quantity;
	}
}
