package net.slashie.expedition.ui.oryx;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.ExpeditionItem;
import net.slashie.expedition.domain.ExpeditionUnit;
import net.slashie.expedition.domain.Good;
import net.slashie.expedition.domain.GoodsCache;
import net.slashie.expedition.domain.ShipCache;
import net.slashie.expedition.domain.Store;
import net.slashie.expedition.domain.StoreItemInfo;
import net.slashie.expedition.domain.Vehicle;
import net.slashie.expedition.domain.Expedition.MovementMode;
import net.slashie.expedition.domain.Expedition.MovementSpeed;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.ui.ExpeditionUserInterface;
import net.slashie.expedition.world.ExpeditionLevel;
import net.slashie.expedition.world.ExpeditionMicroLevel;
import net.slashie.libjcsi.CharKey;
import net.slashie.libjcsi.ConsoleSystemInterface;
import net.slashie.libjcsi.textcomponents.MenuItem;
import net.slashie.libjcsi.textcomponents.TextBox;
import net.slashie.serf.action.Action;
import net.slashie.serf.action.Actor;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.level.AbstractCell;
import net.slashie.serf.sound.STMusicManagerNew;
import net.slashie.serf.ui.UserCommand;
import net.slashie.serf.ui.oryxUI.AddornedBorderPanel;
import net.slashie.serf.ui.oryxUI.GFXUserInterface;
import net.slashie.serf.ui.oryxUI.SwingSystemInterface;
import net.slashie.util.Pair;
import net.slashie.utils.swing.BorderedMenuBox;
import net.slashie.utils.swing.GFXMenuItem;
import net.slashie.utils.swing.MenuBox;

public class ExpeditionOryxUI extends GFXUserInterface implements ExpeditionUserInterface{
	private Color ORANGE = new Color(255,127,39);
	@Override
	public void showDetailedInfo(Actor a) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getQuitMessage() {
		return "Quit?";
	}

	@Override
	public void showInventory() {
		// TODO Auto-generated method stub
		showMessage("Inventory Screen not yet implemented.");
	}

	@Override
	public int switchChat(String prompt, String... options) {
   		BorderedMenuBox selectionBox = new BorderedMenuBox(BORDER1, BORDER2, BORDER3, BORDER4, si, COLOR_WINDOW_BACKGROUND, COLOR_BORDER_IN, COLOR_BORDER_OUT, tileSize, 6,9,12,tileSize+6, null);
   		selectionBox.setItemsPerPage(8);
   		selectionBox.setBounds(80, 300, 600,250);
  		Vector<GFXMenuItem> menuItems = new Vector<GFXMenuItem>();
  		int i = 0;
  		for (String option: options){
  			menuItems.add(new SimpleGFXMenuItem(option,i));
  			i++;
  		}
  		selectionBox.setMenuItems(menuItems);
  		selectionBox.setTitle(prompt);
  		selectionBox.setForeColor(ORANGE);
  		selectionBox.draw();
  		
		while (true) {
			si.refresh();
			SimpleGFXMenuItem itemChoice = ((SimpleGFXMenuItem)selectionBox.getSelection());
			if (itemChoice == null)
				break;
			return itemChoice.getValue();
		}
		return -1;	
	}

	@Override
	public String inputBox(String prompt) {
		return inputBox(prompt, 200, 40, 400, 200, 260, 120, 20);
	}
	
	public String inputBox(String prompt, int x, int y, int w, int h, int xp, int yp, int length){
		AddornedBorderPanel p = new AddornedBorderPanel(BORDER1,
				BORDER2,
				BORDER3,
				BORDER4,
				COLOR_BORDER_OUT,
				COLOR_BORDER_IN,
				COLOR_WINDOW_BACKGROUND,
				tileSize,
				6,9,12 );

		p.setBounds(x, y, w, h);
		p.paintAt(si.getGraphics2D(), x, y);
		si.setColor(ORANGE);
		si.printAtPixel(x+tileSize, y+tileSize*2, prompt);
		
		String ret = si.input(xp,yp,ORANGE,length);
		
		return ret;
	}

	@Override
	public void onMusicOn() {
		ExpeditionLevel expeditionLevel = (ExpeditionLevel)getExpedition().getLevel();
		if (expeditionLevel.getMusicKey() != null)
			STMusicManagerNew.thus.playKey(expeditionLevel.getMusicKey());
	}

	public boolean depart() {
		if (promptChat("Do you want to leave the ports of "+player.getLevel().getDescription()+"? (Y/n)", 28, 2, 23, 5)){
			return true;
		} else
			return false;
	}

	public void launchStore(Store store) {
    	List<Equipment> merchandise = store.getInventory();
    	if (merchandise == null || merchandise.size() == 0){
    		return;
    	}
   		Equipment.eqMode = true;
   		//Item.shopMode = true;
   		BorderedMenuBox menuBox = new BorderedMenuBox(BORDER1, BORDER2, BORDER3, BORDER4, si, COLOR_WINDOW_BACKGROUND, COLOR_BORDER_IN, COLOR_BORDER_OUT, tileSize, 6,9,12,tileSize+6, null);
  		menuBox.setItemsPerPage(6);
  		menuBox.setBounds(80, 300, 600,250);
  		int fontSize = si.getGraphics2D().getFont().getSize();
  		
  		Vector menuItems = new Vector();
  		for (Equipment item: merchandise){
  			menuItems.add(new StoreGFXMenuItem(item, store, getExpedition()));
  		}
  		
  		menuBox.setMenuItems(menuItems);
  		menuBox.draw();
  		menuBox.setForeColor(ORANGE);
  		
  		//menuBox.setBorder(true);
  		String prompt = "Welcome to the "+store.getOwnerName();

		while (true) {
			menuBox.setTitle(prompt);
			si.refresh();
			//menuBox.setTitle(who.getName()+" (Gold:"+player.getGold()+")");
			StoreGFXMenuItem itemChoice = ((StoreGFXMenuItem)menuBox.getSelection());
			if (itemChoice == null)
				break;
			Equipment choice = itemChoice.getEquipment();
			ExpeditionItem item = (ExpeditionItem) choice.getItem();
			StoreItemInfo storeItemInfo = store.getPriceFor(item);
			if (storeItemInfo.getPack() > 1)
				menuBox.setTitle("How many "+storeItemInfo.getPackDescription()+" of "+item.getPluralDescription()+"?");
			else
				menuBox.setTitle("How many "+item.getPluralDescription()+"?");
			menuBox.draw();
			si.refresh();
			int buyQuantity = readQuantity(80+tileSize+300, 308+fontSize, "                       ", 5);
			if (buyQuantity == 0){
				prompt = "Ok... Do you need anything else?";
				continue;
			}
			if (buyQuantity > choice.getQuantity()){
				prompt = "I don't have that many...";
				continue;
			}
			
			int quantity = buyQuantity * storeItemInfo.getPack();
			
			if (!getExpedition().canCarryOffshore(item, quantity)){
				prompt = "Your ships are full!";
				continue;
			}
			
			int gold = storeItemInfo.getPrice() * buyQuantity;	
			if (item instanceof ExpeditionUnit){
				if (quantity > 1)
					menuBox.setTitle("Hire "+quantity+" "+item.getPluralDescription()+" for "+gold+" maravedíes? (Y/n)");
				else
					menuBox.setTitle("Hire a "+item.getDescription()+" for "+gold+" maravedíes? (Y/n)");
			} else {
				if (quantity > 1)
					menuBox.setTitle("Buy "+quantity+" "+item.getPluralDescription()+" for "+gold+" maravedíes? (Y/n)");
				else
					menuBox.setTitle("Buy a "+item.getDescription()+" for "+gold+" maravedíes? (Y/n)");
			}
			menuBox.draw();
	 		if (prompt())
	 			if (getExpedition().getAccountedGold() >= gold) {
	 				getExpedition().reduceAccountedGold(gold);
	 				getExpedition().addItemOffshore((ExpeditionItem) choice.getItem(), quantity);
	 				choice.reduceQuantity(buyQuantity);
	 				prompt = "Thank you! Do you need anything else?";
					refresh();
			 	} else {
			 		prompt = "You can't afford it! Do you need anything else?";
			 	}
			else {
				prompt = "Ok, do you need anything else?";
			}
	 		//menuBox.draw();
		}
		Equipment.eqMode = false;
		//Item.shopMode = false;
		//si.restore();
	}

	public void showBlockingMessage(String message) {
		message = message.replaceAll("XXX", "\n");
		showTextBox(message, 140, 288, 520, 200);
	}

	public void transferFromCache(GoodsCache cache) {
		List<Equipment> cacheEquipment = cache.getItems();
		//List<Equipment> expeditionEquipment = getExpedition().getInventory();
    	
   		Equipment.eqMode = true;
   		//Item.shopMode = true;
   		BorderedMenuBox cacheBox = new BorderedMenuBox(BORDER1, BORDER2, BORDER3, BORDER4, si, COLOR_WINDOW_BACKGROUND, COLOR_BORDER_IN, COLOR_BORDER_OUT, tileSize, 6,9,12,tileSize+6, null);
   		cacheBox.setItemsPerPage(10);
   		cacheBox.setBounds(80, 30, 600,500);
  		Vector<GFXMenuItem> menuItems = new Vector<GFXMenuItem>();
  		for (Equipment item: cacheEquipment){
  			menuItems.add(new CacheGFXMenuItem(item, getExpedition()));
  		}
  		cacheBox.setMenuItems(menuItems);
  		cacheBox.setTitle("Transfer from "+cache.getDescription()+" to Expedition");
  		//cacheBox.setTitle("On Ship...");
  		cacheBox.setForeColor(ORANGE);
  		cacheBox.draw();
  		
  		//menuBox.setBorder(true);
		while (true) {
			si.refresh();
			//menuBox.setTitle(who.getName()+" (Gold:"+player.getGold()+")");
			CacheGFXMenuItem itemChoice = ((CacheGFXMenuItem)cacheBox.getSelection());
			if (itemChoice == null){
				if (cache instanceof ShipCache){
					if (getExpedition().getTotalUnits() > 0)
						break;
					else {
						cacheBox.setTitle("You must first disembark");
						continue;
					}
				} else {
					break;
				}
			}
			Equipment choice = itemChoice.getEquipment();
			ExpeditionItem item = (ExpeditionItem) choice.getItem();
			cacheBox.setTitle("How many "+item.getDescription()+" will you transfer?");
			cacheBox.draw();
			si.refresh();
			int quantity = readQuantity(80+tileSize+420, 38+getFontSize(), "                       ", 5);
			if (quantity == 0)
				continue;
			
			if (!(choice.getItem() instanceof ExpeditionUnit) && getExpedition().getTotalUnits() == 0){
				cacheBox.setTitle("Someone must receive the goods!");
				cacheBox.draw();
				continue;
			}
			
			if (quantity > choice.getQuantity()){
				cacheBox.setTitle("Not enough "+choice.getItem().getDescription());
				cacheBox.draw();
				continue;
			}
			
			if (item instanceof Good && !getExpedition().canCarry(item, quantity)){
				cacheBox.setTitle("Your expedition is full!");
				cacheBox.draw();
				continue;
			}
			choice.reduceQuantity(quantity);
			getExpedition().addItem(choice.getItem(), quantity);
			
			if (choice.getQuantity() == 0){
				cacheEquipment.remove(choice);
			}
			
			cacheBox.setTitle(choice.getItem().getDescription()+" transfered.");
			refresh();
	 		//menuBox.draw();
		}
		
		if (cache.destroyOnEmpty() && cache.getItems().size() == 0)
			level.destroyFeature(cache);
		Equipment.eqMode = false;
		//Item.shopMode = false;
		//si.restore();
	}

	public void transferFromExpedition(GoodsCache ship) {
		transferFromExpedition(ship, -1);
	}

	public void transferFromExpedition(GoodsCache ship, int minUnits) {
		List<Equipment> expeditionEquipment = getExpedition().getInventory();
   		Equipment.eqMode = true;
   		BorderedMenuBox cacheBox = new BorderedMenuBox(BORDER1, BORDER2, BORDER3, BORDER4, si, COLOR_WINDOW_BACKGROUND, COLOR_BORDER_IN, COLOR_BORDER_OUT, tileSize, 6,9,12,tileSize+6, null);
   		cacheBox.setItemsPerPage(10);
   		cacheBox.setBounds(80, 30, 600,500);
  		
  		Vector menuItems = new Vector();
  		for (Equipment item: expeditionEquipment){
  			menuItems.add(new CacheGFXMenuItem(item, ship));
  		}
  		cacheBox.setMenuItems(menuItems);
  		cacheBox.setTitle("Transfer from Expedition to "+ship.getDescription());
  		//cacheBox.setTitle("On Ship...");
  		cacheBox.setForeColor(ORANGE);
  		cacheBox.draw();
  		
		while (true) {
			si.refresh();
			CacheGFXMenuItem itemChoice = ((CacheGFXMenuItem)cacheBox.getSelection());
			if (itemChoice == null){
				if (minUnits != -1){
					if (ship.getTotalUnits() < minUnits){
						cacheBox.setTitle("At least "+minUnits+" should be transfered");
						continue;
					}
				}
				break;
			}
			Equipment choice = itemChoice.getEquipment();
			ExpeditionItem item = (ExpeditionItem) choice.getItem();
			cacheBox.setTitle("How many "+item.getDescription()+" will you transfer?");
			cacheBox.draw();
			si.refresh();
			int quantity = readQuantity(80+tileSize+420, 38+getFontSize(), "                       ", 5);
			
			if (quantity == 0)
				continue;
			
			if (quantity > choice.getQuantity()){
				cacheBox.setTitle("Not enough "+choice.getItem().getDescription());
				cacheBox.draw();
				continue;
			}
			
			if (item instanceof Good && !ship.canCarry(item, quantity)){
				cacheBox.setTitle("Not enough room in the "+ship.getDescription());
				cacheBox.draw();
				continue;
			}
			
			getExpedition().reduceQuantityOf(choice.getItem(), quantity);
			
			if (choice.getItem() instanceof ExpeditionUnit && 
					getExpedition().getCurrentlyCarrying()>100){
				cacheBox.setTitle("The expedition can't carry the goods!");
				cacheBox.draw();
				getExpedition().addItem(choice.getItem(), quantity);
				continue;
			}
			
			ship.addItem(choice.getItem(), quantity);
			
			if (choice.getQuantity() == 0){
				menuItems.remove(choice);
			}
			cacheBox.setTitle(choice.getItem().getDescription()+" transfered into the "+ship.getDescription());
			refresh();
	 		//menuBox.draw();
		}
		Equipment.eqMode = false;		
	}
	
	private int getFontSize() {
		return si.getGraphics2D().getFont().getSize();
	}

	
	@Override
	public void beforeDrawLevel() {
		if (getExpedition().getMovementMode() == MovementMode.SHIP)
			setFlipEnabled(false);
		else
			setFlipEnabled(true);
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
		si.print(2, 1, getExpedition().getExpeditionaryTitle());
		if (getExpedition().getTowns().size() == 1)
			si.print(2, 2, "1 colony ");
		else
			si.print(2, 2, getExpedition().getTowns().size()+" colonies");
		si.print(2, 3, getExpedition().getAccountedGold()+"$");
		
		
		// Box 2
		si.print(2, 5, statsExpedition.getOffshoreFoodDays()+" food days");
		if (getExpedition().getLevel() instanceof ExpeditionMicroLevel)
			si.print(2, 6, "Carrying "+statsExpedition.getOffshoreCurrentlyCarrying()+"%");
		else
			si.print(2, 6, "Carrying "+statsExpedition.getCurrentlyCarrying()+"%");		
		if (statsExpedition.getMovementSpeed() != MovementSpeed.NORMAL){
			si.print(2, 7, statsExpedition.getMovementMode().getDescription()+"("+statsExpedition.getMovementSpeed().getDescription()+")");
		} else {
			si.print(2, 7, statsExpedition.getMovementMode().getDescription());
		}
		si.print(2, 8, statsExpedition.getTotalShips()+" ships ("+statsExpedition.getShipHealth()+"%)");
		
		si.print(2, 9, statsExpedition.getPower()+(statsExpedition.isArmed()?" Power (Armed)":" Power"));
		

		
		int line2 = 63;
		//Box 3
		AbstractCell currentCell = getExpedition().getLocation().getMapCell(getExpedition().getPosition());
		Pair<String, String> locationDescription = getExpedition().getLocation().getLocationDescription();
		Pair<String, String> locationMeans = getExpedition().getLocation().getLocationMeans();
		si.print(line2, 1, gameTime.get(Calendar.YEAR)+", "+ months[gameTime.get(Calendar.MONTH)] +" "+ gameTime.get(Calendar.DATE));
		si.print(line2, 2, getExpedition().getLocation().getDescription());
		si.print(line2, 3, currentCell.getDescription());
		si.print(line2, 4, getExpedition().getWeather()+", "+getExpedition().getTemperature());
		
		si.print(line2, 6, locationMeans.getA());
		si.print(line2+2, 7, locationDescription.getA());
		si.print(line2, 8, locationMeans.getB());
		si.print(line2+2, 9, locationDescription.getB());
		si.print(line2, 10, "Wind: "+getExpedition().getLocation().getWindDirection().getAbbreviation());
		si.print(line2, 11, "Heading "+getExpedition().getHeading().getAbbreviation());
		si.print(line2, 12, getExpedition().getSailingPoint().getDescription());
		si.print(line2+2, 13, getExpedition().getSailingPoint().getSpeed().getDescription());
		
		
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
	
	private Vector<Equipment> expeditionUnitsVector = new Vector<Equipment>();
	//BorderedMenuBox unitsMenuBox;
	MenuBox unitsMenuBox;
	public void init(SwingSystemInterface psi, String title, UserCommand[] gameCommands, Properties UIProperties, Action target){
		super.init(psi, title, gameCommands, UIProperties, target);
		//unitsMenuBox = new BorderedMenuBox(BORDER1, BORDER2, BORDER3, BORDER4, si, COLOR_WINDOW_BACKGROUND, COLOR_BORDER_IN, COLOR_BORDER_OUT, tileSize, null);
		unitsMenuBox = new MenuBox(si, null);
		unitsMenuBox.setGap(36);
		unitsMenuBox.setPosition(0,9);
		unitsMenuBox.setWidth(17);
		unitsMenuBox.setItemsPerPage(9);
  		unitsMenuBox.setShowOptions(false);
		//unitsMenuBox.setTitle("Expedition");
		
	}
	
	private int readQuantity(int x, int y, String spaces, int inputLength){
		int quantity = -1;
		while (quantity == -1){
			si.print(x,y,spaces);
			si.refresh();
			String strInput = si.input(x,y,ORANGE,inputLength);
			if (strInput == null)
				continue;
			strInput = strInput.trim();
			try {
				quantity = Integer.parseInt(strInput);
			}catch (NumberFormatException e) {
			}
			if (quantity < 0)
				quantity = -1;
		}
		return quantity;
	}
	
}
