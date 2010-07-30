package net.slashie.expedition.ui.oryx;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.GoodsCache;
import net.slashie.expedition.domain.Store;
import net.slashie.expedition.domain.Vehicle;
import net.slashie.expedition.domain.Expedition.MovementSpeed;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.ui.ExpeditionUserInterface;
import net.slashie.expedition.ui.console.UnitMenuItem;
import net.slashie.expedition.ui.console.VehicleMenuItem;
import net.slashie.expedition.world.ExpeditionMicroLevel;
import net.slashie.libjcsi.CharKey;
import net.slashie.libjcsi.ConsoleSystemInterface;
import net.slashie.libjcsi.textcomponents.TextBox;
import net.slashie.serf.action.Action;
import net.slashie.serf.action.Actor;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.level.AbstractCell;
import net.slashie.serf.ui.UserCommand;
import net.slashie.serf.ui.oryxUI.GFXUserInterface;
import net.slashie.serf.ui.oryxUI.SwingSystemInterface;
import net.slashie.util.Pair;
import net.slashie.utils.swing.BorderedMenuBox;
import net.slashie.utils.swing.GFXMenuItem;

public class ExpeditionOryxUI extends GFXUserInterface implements ExpeditionUserInterface{

	@Override
	public void showDetailedInfo(Actor a) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getQuitMessage() {
		return "Quit?";
	}

	@Override
	public boolean promptChat(String message) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void showInventory() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int switchChat(String prompt, String... options) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String inputBox(String prompt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processHelp() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMusicOn() {
		// TODO Auto-generated method stub
		
	}

	public boolean depart() {
		if (promptChat("Do you want to leave the ports of "+player.getLevel().getDescription()+"? (Y/n)", 28, 2, 23, 5)){
			return true;
		} else
			return false;
	}

	public void launchStore(Store store) {
		// TODO Auto-generated method stub
		
	}

	public void showBlockingMessage(String message) {
		showTextBox(message, 192, 288, 408, 168);
	}

	public void transferFromCache(GoodsCache ship) {
		// TODO Auto-generated method stub
		
	}

	public void transferFromExpedition(GoodsCache ship) {
		// TODO Auto-generated method stub
		
	}

	public void transferFromExpedition(GoodsCache ship, int minUnits) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void beforeRefresh() {
		drawStatus();
	}

	private Expedition getExpedition(){
		return (Expedition)getPlayer();
	}
	
	private void drawStatus() {
		Expedition statsExpedition = getExpedition();
		si.setColor(Color.WHITE);
		// drawAddornment();
		
		// Box 1
		Calendar gameTime = ((ExpeditionGame)player.getGame()).getGameTime(); 
		si.print(5, 1, gameTime.get(Calendar.YEAR)+"");
		si.print(5, 2, months[gameTime.get(Calendar.MONTH)] +" "+ gameTime.get(Calendar.DATE));
		si.print(5, 3, getExpedition().getExpeditionaryTitle());
		if (getExpedition().getTowns().size() == 1)
			si.print(5, 4, "1 settlement ");
		else
			si.print(5, 4, getExpedition().getTowns().size()+" settlements");
		si.print(5, 5, getExpedition().getAccountedGold()+"$");
		
		
		// Box 2
		si.print(2, 7, statsExpedition.getTotalShips()+" ships ("+statsExpedition.getShipHealth()+"%)");
		si.print(2, 8, statsExpedition.getOffshoreFoodDays()+" food days");
		si.print(2, 9, statsExpedition.getPower()+(statsExpedition.isArmed()?" Power (Armed)":" Power"));
		if (statsExpedition.getMovementSpeed() != MovementSpeed.NORMAL){
			si.print(2, 10, statsExpedition.getMovementMode().getDescription()+"("+statsExpedition.getMovementSpeed().getDescription()+")");
		} else {
			si.print(2, 10, statsExpedition.getMovementMode().getDescription());
		}
		if (getExpedition().getLevel() instanceof ExpeditionMicroLevel)
			si.print(2, 11, "Carrying "+statsExpedition.getOffshoreCurrentlyCarrying()+"%");
		else
			si.print(2, 11, "Carrying "+statsExpedition.getCurrentlyCarrying()+"%");
		
		int line2 = 63;
		//Box 3
		AbstractCell currentCell = getExpedition().getLocation().getMapCell(getExpedition().getPosition());
		Pair<String, String> locationDescription = getExpedition().getLocation().getLocationDescription();
		si.print(line2, 1, getExpedition().getLocation().getDescription());
		si.print(line2, 2, currentCell.getDescription());
		si.print(line2, 3, locationDescription.getA());
		si.print(line2, 4, locationDescription.getB());
		si.print(line2, 5, getExpedition().getWeather()+", "+getExpedition().getTemperature()+"ºC");
		
		
		expeditionUnitsVector.clear();
		expeditionUnitsVector.addAll(statsExpedition.getUnits());
		
		List<GFXMenuItem> expeditionUnitItems = new ArrayList<GFXMenuItem>();
		for (Equipment expeditionUnit: expeditionUnitsVector){
			expeditionUnitItems.add(new UnitGFXMenuItem(expeditionUnit));
		}
		for (Vehicle expeditionVehicle: statsExpedition.getCurrentVehicles()){
			expeditionUnitItems.add(new VehicleGFXMenuItem(expeditionVehicle));
		}
		
		unitsMenuBox.setMenuItems(expeditionUnitItems);
		unitsMenuBox.draw();
	}
	
	private Comparator<UnitMenuItem> expeditionUnitsComparator = new Comparator<UnitMenuItem>(){
		public int compare(UnitMenuItem o1, UnitMenuItem o2) {
			return o1.getMenuColor() - o2.getMenuColor();
		};
	};
	
	private Vector<Equipment> expeditionUnitsVector = new Vector<Equipment>();
	BorderedMenuBox unitsMenuBox;
	public void init(SwingSystemInterface psi, String title, UserCommand[] gameCommands, Properties UIProperties, Action target){
		super.init(psi, title, gameCommands, UIProperties, target);
		unitsMenuBox = new BorderedMenuBox(BORDER1, BORDER2, BORDER3, BORDER4, si, COLOR_WINDOW_BACKGROUND, COLOR_BORDER_IN, COLOR_BORDER_OUT, tileSize, null);
		unitsMenuBox.setGap(35);
		unitsMenuBox.setPosition(62,8);
		unitsMenuBox.setWidth(17);
		unitsMenuBox.setItemsPerPage(9);
  		
		unitsMenuBox.setTitle("Expedition");
		
	}
	
}
