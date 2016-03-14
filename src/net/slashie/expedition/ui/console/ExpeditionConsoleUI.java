package net.slashie.expedition.ui.console;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import net.slashie.expedition.domain.AssaultOutcome;
import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.Expedition.MovementMode;
import net.slashie.expedition.domain.ExpeditionItem;
import net.slashie.expedition.domain.ExpeditionUnit;
import net.slashie.expedition.domain.GoodType;
import net.slashie.expedition.domain.GoodsCache;
import net.slashie.expedition.domain.LandingParty;
import net.slashie.expedition.domain.ShipCache;
import net.slashie.expedition.domain.Store;
import net.slashie.expedition.domain.StoreItemInfo;
import net.slashie.expedition.domain.Town;
import net.slashie.expedition.domain.Vehicle;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.town.Building;
import net.slashie.expedition.ui.CommonUI;
import net.slashie.expedition.ui.ExpeditionUserInterface;
import net.slashie.expedition.world.ExpeditionLevel;
import net.slashie.expedition.world.ExpeditionMicroLevel;
import net.slashie.expedition.world.TemperatureRules;
import net.slashie.expedition.world.Weather;
import net.slashie.libjcsi.CSIColor;
import net.slashie.libjcsi.CharKey;
import net.slashie.libjcsi.ConsoleSystemInterface;
import net.slashie.libjcsi.textcomponents.ListBox;
import net.slashie.libjcsi.textcomponents.MenuBox;
import net.slashie.libjcsi.textcomponents.MenuItem;
import net.slashie.libjcsi.textcomponents.TextBox;
import net.slashie.serf.action.Action;
import net.slashie.serf.action.Actor;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.game.Player;
import net.slashie.serf.level.AbstractCell;
import net.slashie.serf.ui.Appearance;
import net.slashie.serf.ui.UserCommand;
import net.slashie.serf.ui.consoleUI.CharAppearance;
import net.slashie.serf.ui.consoleUI.ConsoleUserInterface;
import net.slashie.util.Pair;
import net.slashie.utils.Position;


public class ExpeditionConsoleUI extends ConsoleUserInterface implements ExpeditionUserInterface{
	private ConsoleSystemInterface csi;
	private ListBox expeditionUnitBox;
	private ListBox vehiclesBox;
	private SimplifiedUnitMenuItem mainUnitMenuItem;
	
	@Override
	public void setPlayer(Player player) {
		super.setPlayer(player);
		mainUnitMenuItem = new SimplifiedUnitMenuItem(new Equipment(getExpedition().getLeaderUnit(),1));
	}
	
	public ExpeditionConsoleUI (ConsoleSystemInterface csi){
		this.csi = csi;
	}
	
	public void init(ConsoleSystemInterface psi, UserCommand[] gameCommands, Action target){
		super.init(psi, gameCommands, target);
		VP_START = new Position(29,0);
		VP_END = new Position (49,20);
		PC_POS = new Position(39, 10);
		xrange = 9;
		yrange = 9;
		
		expeditionUnitBox = new ListBox(csi);
		expeditionUnitBox.setPosition(3,8);
		expeditionUnitBox.setWidth(22);
		expeditionUnitBox.setHeight(10);
		
		vehiclesBox = new ListBox(csi);
		vehiclesBox.setPosition(54,12);
		vehiclesBox.setWidth(24);
		vehiclesBox.setHeight(7);
		
		/*idList.setPosition(2,13);
		idList.setWidth(23);
		idList.setHeight(6);*/
		
	}
	@Override
	public void beforeSeenListCompilation() {
		// TODO Auto-generated method stub
		
	}

	private Vector expeditionUnitsVector = new Vector();
	@Override
	public void drawStatus() {
		Expedition statsExpedition = getExpedition();
		
		//csi.print(1,1, getPlayer().getPosition().x()+","+getPlayer().getPosition().y());
		drawAddornment();
		// Box 1
		Calendar gameTime = ((ExpeditionGame)player.getGame()).getGameTime(); 
		csi.print(3, 1, gameTime.get(Calendar.YEAR)+", " +months[gameTime.get(Calendar.MONTH)] +" "+ gameTime.get(Calendar.DATE));
		csi.print(3, 2, getExpedition().getExpeditionaryTitle());
		if (getExpedition().getTowns().size() == 1)
			csi.print(3, 3, "1 colony ");
		else
			csi.print(3, 3, getExpedition().getTowns().size()+" colonies");
		csi.print(3, 4, getExpedition().getAccountedGold()+"$");
		
		csi.print(3, 5, statsExpedition.getOffshoreFoodDays()+" food days "+TemperatureRules.getTemperatureFoodModifierString(getExpedition().getLocation().getTemperature()));
		if (getExpedition().getLevel() instanceof ExpeditionMicroLevel)
			csi.print(3, 6, "Carrying "+statsExpedition.getOffshoreCurrentlyCarrying()+"%");
		else
			csi.print(3, 6, "Carrying "+statsExpedition.getCurrentlyCarrying()+"%");
		csi.print(3, 7, statsExpedition.getMoraleDescription()+(statsExpedition.isArmed()?"(Armed)":""));
		
		//Box 3
		AbstractCell currentCell = getExpedition().getLocation().getMapCell(getExpedition().getPosition());
		Pair<String, String> locationDescription = getExpedition().getLocation().getLocationDescription();
		Pair<String, String> locationMeans = getExpedition().getLocation().getLocationMeans();
		Pair<String, String> locationLabels = getExpedition().getLocation().getLocationLabels();
		csi.print(54, 1, getExpedition().getLocation().getDescription());
		csi.print(54, 2, currentCell.getDescription());
		csi.print(54, 3, getExpedition().getLocation().getWeather().getDescription());
		csi.print(54, 4, getExpedition().getLocation().getTemperatureDescription());
		csi.print(54, 5, locationLabels.getA(), CSIColor.YELLOW);
			csi.print(54+9, 5, locationDescription.getA());
		csi.print(54, 6, locationLabels.getB(), CSIColor.YELLOW);
			csi.print(54+9, 6, locationDescription.getB());
		csi.print(54, 7, "Wind", CSIColor.YELLOW);
			csi.print(54+9, 7, getExpedition().getLocation().getWindDirection().getAbbreviation());
		csi.print(54, 8, "Heading", CSIColor.YELLOW);
			csi.print(54+9, 8, getExpedition().getHeading().getAbbreviation());
		if (getExpedition().getMovementMode() == MovementMode.SHIP){
			if (getExpedition().isAnchored())
				csi.print(54+2, 9, "Anchored");
			else
				csi.print(54+2, 9, getExpedition().getSailingPoint().getDescription());
		} else {
			csi.print(54+2, 9, statsExpedition.getMovementMode().getDescription());
		}
		csi.print(54, 10, getExpedition().getMovementSpeed().getDescription());
		csi.print(54, 11, statsExpedition.getTotalShips()+" ships ("+statsExpedition.getShipHealth()+"%)");
		//This must be replaced on the next version of libjcsi
		
		expeditionUnitsVector.clear();
		resumedEquipments.clear();
		for (Equipment expeditionUnit: statsExpedition.getGoods(GoodType.PEOPLE)){
			String basicId = ((ExpeditionUnit)expeditionUnit.getItem()).getBaseID();
			Equipment resumedEquipment = resumedEquipments.get(basicId) ;
			if (resumedEquipment == null){
				resumedEquipment = new Equipment(expeditionUnit.getItem(), expeditionUnit.getQuantity());
				resumedEquipments.put(basicId, resumedEquipment);
				expeditionUnitsVector.add(new UnitMenuItem(resumedEquipment));
			} else {
				resumedEquipment.setQuantity(resumedEquipment.getQuantity()+expeditionUnit.getQuantity());
			}
		}
		Collections.sort(expeditionUnitsVector, expeditionUnitsComparator);
		expeditionUnitsVector.add(0, mainUnitMenuItem);
		expeditionUnitBox.setElements(expeditionUnitsVector);
		
		Vector vehicleItems = new Vector();
		for (Vehicle expeditionVehicle: statsExpedition.getCurrentVehicles()){
			vehicleItems.add(new VehicleMenuItem(expeditionVehicle));
		}
		vehiclesBox.setElements(vehicleItems);
		
	}
	private Map<String,Equipment> resumedEquipments = new HashMap<String, Equipment>();

	
	private void drawCumbersomeAddornment(){
		int addornmentColor = ConsoleSystemInterface.TEAL;
		csi.print(0, 0,  "    /------------------\\     /---------N---------\\     /--------------------\\   ", addornmentColor);
		csi.print(0, 1,  "    |                  |     |", addornmentColor);csi.print(49, 1, "|     |                    |   ", addornmentColor);
		csi.print(0, 2,  "    |                  |     |",addornmentColor);csi.print(49, 2, "|     |                    |   ", addornmentColor);
		csi.print(0, 3,  "    |                  |-----|",addornmentColor);csi.print(49, 3, "|-----|                    |   ", addornmentColor);
		csi.print(0, 4,  "    |                  |.....|", addornmentColor);csi.print(49, 4,"|.....|                    |   ", addornmentColor);
		csi.print(0, 5,  "    |                  |.....|", addornmentColor); csi.print(49, 5,  "|.....|                    |   ", addornmentColor);
		csi.print(0, 6,  "    |                  |.....|                   |.....|                    |   ", addornmentColor);
		csi.print(0, 7,  " /--/                  \\--\\..|                   |.....|                    |   ", addornmentColor);
		csi.print(0, 8,  " |                        |..|                   |.....|                    |   ", addornmentColor);
		csi.print(0, 9,  " |                        |..|                   |.....|                    |   ", addornmentColor);
		csi.print(0, 10, " |                        |..W                   E.....|                    |   ", addornmentColor);
		csi.print(0, 11, " |                        |..|                   |..|                          |", addornmentColor);
		csi.print(0, 12, " |                        |..|                   |..|                          |", addornmentColor);
		csi.print(0, 13, " |                        |..|                   |..|                          |", addornmentColor);
		csi.print(0, 14, " |                        |..|                   |..|                          |", addornmentColor);
		csi.print(0, 15, " |                        |..|                   |..|                          |", addornmentColor);
		csi.print(0, 16, " |                        |..|                   |..|                          |", addornmentColor);
		csi.print(0, 17, " |                        |--|                   |--|                          |", addornmentColor);
		csi.print(0, 18, " |                        |  |                   |  |                          |", addornmentColor);
		csi.print(0, 19, " |                        |  |                   |  |                          |", addornmentColor);
		csi.print(0, 20, " \\------------------------/  \\---------S---------/  \\--------------------------/", addornmentColor);
		 
	}
	private void drawAddornment(){
		int addornmentColor = ConsoleSystemInterface.TEAL;
		csi.print(0, 0,  " /------------------------\\  /---------N---------\\  /--------------------------\\", addornmentColor);
		csi.print(0, 1,  " |                        |  |                   |  |                          |", addornmentColor);
		csi.print(0, 2,  " |                        |--|                   |--|                          |", addornmentColor);
		csi.print(0, 3,  " |                        |..|                   |..|                          |", addornmentColor);
		csi.print(0, 4,  " |                        |..|                   |..|                          |", addornmentColor);
		csi.print(0, 5,  " |                        |..|                   |..|                          |", addornmentColor);
		csi.print(0, 6,  " |                        |..|                   |..|                          |", addornmentColor);
		csi.print(0, 7,  " |                        |..|                   |..|                          |", addornmentColor);
		csi.print(0, 8,  " |                        |..|                   |..|                          |", addornmentColor);
		csi.print(0, 9,  " |                        |..|                   |..|                          |", addornmentColor);
		csi.print(0, 10, " |                        |..W                   E..|                          |", addornmentColor);
		csi.print(0, 11, " |                        |..|                   |..|                          |", addornmentColor);
		csi.print(0, 12, " |                        |..|                   |..|                          |", addornmentColor);
		csi.print(0, 13, " |                        |..|                   |..|                          |", addornmentColor);
		csi.print(0, 14, " |                        |..|                   |..|                          |", addornmentColor);
		csi.print(0, 15, " |                        |..|                   |..|                          |", addornmentColor);
		csi.print(0, 16, " |                        |..|                   |..|                          |", addornmentColor);
		csi.print(0, 17, " |                        |--|                   |--|                          |", addornmentColor);
		csi.print(0, 18, " |                        |  |                   |  |                          |", addornmentColor);
		csi.print(0, 19, " |                        |  |                   |  |                          |", addornmentColor);
		csi.print(0, 20, " \\------------------------/  \\---------S---------/  \\--------------------------/", addornmentColor);
		 
	}
	
	

	@Override
	public String getQuitPrompt() {
		return "Quit?";
	}

	@Override
	public void showDetailedInfo(Actor a) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMusicOn() {
		ExpeditionLevel expeditionLevel = (ExpeditionLevel)getExpedition().getLevel();
		expeditionLevel.playMusic();
	}

	private Expedition getExpedition(){
		return (Expedition)getPlayer();
	}
	
	@Override
	public void beforeRefresh() {
		expeditionUnitBox.draw();
		vehiclesBox.draw();
	}
	private int readQuantity(int x, int y, String spaces, int inputLength){
		int quantity = -1;
		while (quantity == -1){
			csi.print(x,y,spaces);
			csi.refresh();
			csi.locateCaret(x, y);
			String strInput = csi.input(inputLength);
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
	
	public void launchStore(Store store) {
    	//si.saveBuffer();
		//Expedition offShore = getExpedition().getOffshoreExpedition();
    	List<Equipment> merchandise = store.getInventory();
    	if (merchandise == null || merchandise.size() == 0){
    		return;
    	}
   		Equipment.eqMode = true;
   		//Item.shopMode = true;
   		MenuBox menuBox = new MenuBox(csi);
  		menuBox.setHeight(13);
  		menuBox.setWidth(76);
  		menuBox.setPosition(2,7);
  		
  		Vector menuItems = new Vector();
  		for (Equipment item: merchandise){
  			menuItems.add(new StoreItem(item, store, getExpedition()));
  		}
  		
  		menuBox.setMenuItems(menuItems);
  		menuBox.setPromptSize(2);
  		menuBox.setBorder(true);
  		menuBox.draw();
  		menuBox.setForeColor(ConsoleSystemInterface.RED);
  		menuBox.setBorderColor(ConsoleSystemInterface.TEAL);
  		//menuBox.setBorder(true);
  		String prompt = "Welcome to the "+store.getOwnerName();

		while (true) {
			menuBox.setPrompt(prompt+" [Space to leave]");
			csi.refresh();
			//menuBox.setTitle(who.getName()+" (Gold:"+player.getGold()+")");
			StoreItem itemChoice = ((StoreItem)menuBox.getSelection());
			if (itemChoice == null)
				break;
			Equipment choice = itemChoice.getEquipment();
			ExpeditionItem item = (ExpeditionItem) choice.getItem();
			StoreItemInfo storeItemInfo = store.getBuyInfo(item, getExpedition());
			menuBox.setPrompt("How many "+item.getPluralDescription()+"?");
			menuBox.draw();
			csi.refresh();
			int buyQuantity = readQuantity(27, 9, "                       ", 5);
			if (buyQuantity == 0){
				prompt = "Ok... Do you need anything else?";
				continue;
			}
			if (buyQuantity > choice.getQuantity()){
				prompt = "I don't have that many "+item.getPluralDescription()+"... Do you need anything else?";
				continue;
			}
			
			int quantity = buyQuantity;
			
			if (!getExpedition().canCarryOffshore(item, quantity)){
				prompt = "Your ships are full! Do you need anything else?";
				continue;
			}
			
			int gold = storeItemInfo.getPrice() * buyQuantity;	
			if (item instanceof ExpeditionUnit){
				if (quantity > 1)
					menuBox.setPrompt("Hire "+quantity+" "+item.getPluralDescription()+" for "+gold+" maraved�es? (Y/n)");
				else
					menuBox.setPrompt("Hire a "+item.getDescription()+" for "+gold+" maraved�es? (Y/n)");
			} else {
				if (quantity > 1)
					menuBox.setPrompt("Buy "+quantity+" "+item.getPluralDescription()+" for "+gold+" maraved�es? (Y/n)");
				else
					menuBox.setPrompt("Buy a "+item.getDescription()+" for "+gold+" maraved�es? (Y/n)");
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
	
	public boolean depart(){
		if (promptChat("Do you want to leave the ports of Palos de la frontera? (Y/n)", 27, 2, 24, 5)){
			TextBox chatBox = new TextBox(csi);
			chatBox.setHeight(3);
			chatBox.setWidth(20);
			chatBox.setPosition(30, 5);
			chatBox.setBorder(true);
			chatBox.setForeColor(ConsoleSystemInterface.WHITE);
			chatBox.setBorderColor(ConsoleSystemInterface.WHITE);
			chatBox.setText("Loading Map");
			chatBox.draw();
			csi.refresh();
			return true;
		} else
			return false;
	}
	
	public void showBlockingMessage(String message, boolean keep) {
		showBlockingMessage(message);
	}
	
	public void showBlockingMessage(String message) {
		TextBox chatBox = new TextBox(csi);
		chatBox.setHeight(8);
		chatBox.setWidth(50);
		chatBox.setPosition(15, 12);
		chatBox.setBorder(true);
		chatBox.setForeColor(ConsoleSystemInterface.WHITE);
		chatBox.setBorderColor(ConsoleSystemInterface.RED);
		chatBox.setText(message);
		chatBox.setTitle("[Space] to continue");
		chatBox.draw();
		csi.refresh();
		csi.waitKeys(CharKey.SPACE, CharKey.ENTER);
	}
	
	public boolean promptChat(String message){
		return super.promptChat(message, 22,1,34,8);
	}

	public void transferFromCache(String prompt, GoodType preselectedGoodType, GoodsCache cache) {
		List<Equipment> cacheEquipment = cache.getItems();
		//List<Equipment> expeditionEquipment = getExpedition().getInventory();
    	
   		Equipment.eqMode = true;
   		//Item.shopMode = true;
   		MenuBox cacheBox = new MenuBox(csi);
   		cacheBox.setHeight(13);
   		cacheBox.setWidth(54);
   		cacheBox.setPosition(24,7);
  		Vector<MenuItem> menuItems = new Vector<MenuItem>();
  		for (Equipment item: cacheEquipment){
  			menuItems.add(new CacheItem(item, getExpedition()));
  		}
  		cacheBox.setMenuItems(menuItems);
  		cacheBox.setPromptSize(2);
  		cacheBox.setBorder(true);
  		cacheBox.setPrompt(prompt+" [Space to exit]");
  		//cacheBox.setTitle("On Ship...");
  		cacheBox.setForeColor(ConsoleSystemInterface.RED);
  		cacheBox.setBorderColor(ConsoleSystemInterface.TEAL);
  		cacheBox.draw();
  		
  		//menuBox.setBorder(true);
		while (true) {
			csi.refresh();
			//menuBox.setTitle(who.getName()+" (Gold:"+player.getGold()+")");
			CacheItem itemChoice = ((CacheItem)cacheBox.getSelection());
			if (itemChoice == null){
				if (cache instanceof ShipCache){
					if (getExpedition().getTotalUnits() > 0)
						break;
					else {
						cacheBox.setPrompt("You must first disembark");
						continue;
					}
				} else {
					break;
				}
			}
			Equipment choice = itemChoice.getEquipment();
			ExpeditionItem item = (ExpeditionItem) choice.getItem();
			cacheBox.setPrompt("How many "+item.getDescription()+" will you transfer?");
			cacheBox.draw();
			csi.refresh();
			int quantity = readQuantity(25, 9, "                       ", 5);
			if (quantity == 0)
				continue;
			
			if (!(choice.getItem() instanceof ExpeditionUnit) && getExpedition().getTotalUnits() == 0){
				cacheBox.setPrompt("Someone must receive the goods!");
				cacheBox.draw();
				continue;
			}
			
			if (quantity > choice.getQuantity()){
				cacheBox.setPrompt("Not enough "+choice.getItem().getDescription());
				cacheBox.draw();
				continue;
			}
			
			if (item.getGoodType() != GoodType.PEOPLE && !getExpedition().canCarry(item, quantity)){
				cacheBox.setPrompt("Your expedition is full!");
				cacheBox.draw();
				continue;
			}
			choice.reduceQuantity(quantity);
			getExpedition().addItem(choice.getItem(), quantity);
			
			if (choice.getQuantity() == 0){
				cacheEquipment.remove(choice);
			}
			
			cacheBox.setPrompt(choice.getItem().getDescription()+" transfered.");
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
   		MenuBox cacheBox = new MenuBox(csi);
   		cacheBox.setHeight(13);
   		cacheBox.setWidth(54);
   		cacheBox.setPosition(24,7);
  		Vector menuItems = new Vector();
  		for (Equipment item: expeditionEquipment){
  			menuItems.add(new CacheItem(item, ship));
  		}
  		cacheBox.setMenuItems(menuItems);
  		cacheBox.setPromptSize(2);
  		cacheBox.setBorder(true);
  		cacheBox.setPrompt("Transfer from Expedition to "+ship.getDescription()+" [Space to exit]");
  		//cacheBox.setTitle("On Ship...");
  		cacheBox.setForeColor(ConsoleSystemInterface.RED);
  		cacheBox.setBorderColor(ConsoleSystemInterface.TEAL);
  		cacheBox.draw();
  		
		while (true) {
			csi.refresh();
			CacheItem itemChoice = ((CacheItem)cacheBox.getSelection());
			if (itemChoice == null){
				if (minUnits != -1){
					if (ship.getTotalUnits() < minUnits){
						cacheBox.setPrompt("At least "+minUnits+" should be transfered");
						continue;
					}
				}
				break;
			}
			Equipment choice = itemChoice.getEquipment();
			ExpeditionItem item = (ExpeditionItem) choice.getItem();
			cacheBox.setPrompt("How many "+item.getDescription()+" will you transfer?");
			cacheBox.draw();
			csi.refresh();
			int quantity = readQuantity(25, 9, "                       ", 5);
			if (quantity == 0)
				continue;
			
			if (quantity > choice.getQuantity()){
				cacheBox.setPrompt("Not enough "+choice.getItem().getDescription());
				cacheBox.draw();
				continue;
			}
			
			if (!ship.canCarry(item, quantity)){
				cacheBox.setPrompt("Not enough room in the "+ship.getDescription());
				cacheBox.draw();
				continue;
			}
			
			getExpedition().reduceQuantityOf(choice.getItem(), quantity);
			
			if (choice.getItem() instanceof ExpeditionUnit && 
					getExpedition().getCurrentlyCarrying()>100){
				cacheBox.setPrompt("The expedition can't carry the goods!");
				cacheBox.draw();
				getExpedition().addItem(choice.getItem(), quantity);
				if (choice.getQuantity() == 0){
					menuItems = new Vector();
					for (Equipment item2: getExpedition().getInventory()){
			  			menuItems.add(new CacheItem(item2, ship));
			  		}
			  		cacheBox.setMenuItems(menuItems);				
			  	}
				continue;
			}
			
			ship.addItem((ExpeditionItem)choice.getItem(), quantity);
			
			if (choice.getQuantity() == 0){
				menuItems.remove(choice);
			}
			cacheBox.setPrompt(choice.getItem().getDescription()+" transfered into the "+ship.getDescription());
			refresh();
	 		//menuBox.draw();
		}
		Equipment.eqMode = false;
	}
	
	@Override
	public int switchChat(String title, String prompt, String... options) {
		MenuBox selectionBox = new MenuBox(csi);
		selectionBox.setPosition(15,10);
		selectionBox.setWidth(50);
		selectionBox.setHeight(10);
		
  		Vector<MenuItem> menuItems = new Vector<MenuItem>();
  		int i = 0;
  		for (String option: options){
  			menuItems.add(new SimpleItem(i,option));
  			i++;
  		}
  		selectionBox.setMenuItems(menuItems);
  		selectionBox.setPromptSize(2);
  		selectionBox.setBorder(true);
  		selectionBox.setBorderColor(CSIColor.RED);
  		selectionBox.setTitle(title);
  		selectionBox.setPrompt(prompt);
  		selectionBox.draw();
  		
		while (true) {
			csi.refresh();
			SimpleItem itemChoice = ((SimpleItem)selectionBox.getSelection());
			if (itemChoice == null)
				continue;
			return itemChoice.getValue();
		}
	}
	
	public String inputBox(String prompt){
		return inputBox(prompt, 20, 2, 31, 8, 22, 6,20);
	}
	
	@Override
	public void showInventory() {
		Equipment.eqMode = true;
		int xpos = 1, ypos = 0;
  		MenuBox menuBox = new MenuBox(csi);
  		menuBox.setHeight(17);
  		menuBox.setWidth(50);
  		menuBox.setPosition(1,5);
  		menuBox.setBorder(false);
  		TextBox itemDescription = new TextBox(csi);
  		itemDescription.setBounds(52,9,25,5);
  		csi.saveBuffer();
  		csi.cls();
  		csi.print(xpos,ypos,    "------------------------------------------------------------------------", ConsoleSystemInterface.BLUE);
  		csi.print(xpos,ypos+1,  "Inventory", ConsoleSystemInterface.WHITE);
  		csi.print(xpos,ypos+2,    "------------------------------------------------------------------------", ConsoleSystemInterface.BLUE);
  		csi.print(xpos,24,  "[Space] to continue, Up and Down to browse");
  		int choice = 0;
  		while (true){
  			csi.print(xpos,ypos+3,  "( ) Units      ( ) Supplies   ( ) Armory     ( ) TradeGoods ( ) Livestock", ConsoleSystemInterface.BLUE);
  			csi.print(2+choice*15, ypos+3, "*", ConsoleSystemInterface.WHITE);
  			
  	  		List<Equipment> inventory = null;
  	  		switch (choice){
  	  		case 0:
  	  			inventory = getExpedition().getGoods(GoodType.PEOPLE);
  	  			break;
  	  		case 1:
  	  			inventory = getExpedition().getGoods(GoodType.SUPPLIES);
  	  			break;
  	  		case 2:
  	  			inventory = getExpedition().getGoods(GoodType.ARMORY);
  	  			break;
  	  		case 3:
  	  			inventory = getExpedition().getGoods(GoodType.TRADE_GOODS);
  	  			break;
  	  		case 4:
	  			inventory = getExpedition().getGoods(GoodType.LIVESTOCK);
	  			break;
  	  		}
  	  		
  	  		Vector menuItems = new Vector();
  	  		for (Equipment item: inventory){
  	  			menuItems.add(new InventoryItem(item, getExpedition()));
  	  		}
  	  		Collections.sort(menuItems, inventoryItemsComparator);
  	  		menuBox.setMenuItems(menuItems);
  	  		menuBox.draw();
  	  		csi.refresh();
  	  		
	  		CharKey x = new CharKey(CharKey.NONE);
			while (x.code != CharKey.ENTER && x.code != CharKey.SPACE && !x.isArrow())
				x = csi.inkey();
			if (x.code == CharKey.ENTER || x.code == CharKey.SPACE || x.code == CharKey.ESC){
				break;
			}
			if (x.isLeftArrow()){
				choice--;
				if (choice == -1)
					choice = 0;
			}
			if (x.isRightArrow()){
				choice++;
				if (choice == 5)
					choice = 4;
			}
	 		
	 		//menuBox.getSelection();
  		}
 		
 		
		csi.restore();
		csi.refresh();
		Equipment.eqMode = false;
	}

	private Comparator<UnitMenuItem> expeditionUnitsComparator = new Comparator<UnitMenuItem>(){
		public int compare(UnitMenuItem o1, UnitMenuItem o2) {
			return o1.getMenuColor() - o2.getMenuColor();
		};
	};
	
	private Comparator<InventoryItem> inventoryItemsComparator = new Comparator<InventoryItem>(){
		public int compare(InventoryItem o1, InventoryItem o2) {
			if (o1.getEquipment().getItem() instanceof ExpeditionUnit){
				if (o2.getEquipment().getItem() instanceof ExpeditionUnit){
					return o1.getMenuColor() - o2.getMenuColor();
				} else {
					return -1;
				}
			} else if (o2.getEquipment().getItem() instanceof ExpeditionUnit){
				if (o1.getEquipment().getItem() instanceof ExpeditionUnit){
					return o1.getMenuColor() - o2.getMenuColor();
				} else {
					return 1;
				}
			} else {
				return o1.getEquipment().getItem().getDescription().compareTo(o2.getEquipment().getItem().getDescription());
			}
		};
	};
	
	private class StoreItem implements MenuItem{
		private Equipment item;
		private Store store;
		private Expedition offShore;

		public StoreItem(Equipment item, Store store, Expedition offShore) {
			super();
			this.item = item;
			this.store = store;
			this.offShore = offShore;
		}
		
		public Equipment getEquipment(){
			return item;
		}

		public char getMenuChar() {
			return ((CharAppearance)item.getItem().getAppearance()).getChar();
		}
		
		public int getMenuColor() {
			return ((CharAppearance)item.getItem().getAppearance()).getColor();
		}
		
		public String getMenuDescription() {
			return CommonUI.getMenuStoreDescription(item, offShore, store);
		}
	}
	
	private class CacheItem implements MenuItem{
		private Equipment item;
		private GoodsCache cache;
		private Expedition expedition;

		public CacheItem(Equipment item, GoodsCache cache) {
			this.item = item;
			this.cache = cache;
		}
		
		public CacheItem(Equipment item, Expedition expedition) {
			this.item = item;
			this.expedition = expedition;
		}
		
		public Equipment getEquipment(){
			return item;
		}

		public char getMenuChar() {
			return ((CharAppearance)item.getItem().getAppearance()).getChar();
		}
		
		public int getMenuColor() {
			return ((CharAppearance)item.getItem().getAppearance()).getColor();
		}
		
		public String getMenuDescription() {
			return CommonUI.getMenuCacheDescription(item,expedition,cache);
		}
	}
	
	private class InventoryItem implements MenuItem{
		private Equipment item;
		private Expedition expedition;

		public InventoryItem(Equipment item, Expedition expedition) {
			this.item = item;
			this.expedition = expedition;
		}
		
		public Equipment getEquipment(){
			return item;
		}

		public char getMenuChar() {
			return ((CharAppearance)item.getItem().getAppearance()).getChar();
		}
		
		public int getMenuColor() {
			return ((CharAppearance)item.getItem().getAppearance()).getColor();
		}
		
		public String getMenuDescription() {
			String itemDescription = item.getItem().getDescription();
			int quantity = item.getQuantity();
			ExpeditionItem eitem = (ExpeditionItem)item.getItem();
			if (eitem instanceof ExpeditionUnit){
				ExpeditionUnit unit = (ExpeditionUnit) eitem;
				itemDescription = unit.getFullDescription();
				return quantity + " " + itemDescription + " ATK"+ unit.getAttack().getMax()+" DEF"+ unit.getDefense().getMax() +" {Weight: "+(eitem.getWeight() * quantity)+")";
			} else {
				return quantity + " " + itemDescription + " {Weight: "+(eitem.getWeight() * quantity)+")";
			}
		}
	}
	
	private class SimpleItem implements MenuItem{
		private String text;
		private int value;
	
		private SimpleItem (int value, String text){
			this.text = text;
			this.value = value;
		}
		
		public char getMenuChar() {
			return '*';
		}
		
		public int getMenuColor() {
			return ConsoleSystemInterface.WHITE;
		}
		
		public String getMenuDescription() {
			return text;
		}
		
		public int getValue(){
			return value;
		}
	}
	
	@Override
	public void showBattleResults(
			List<Equipment> originalAttackingUnits, List<Equipment> originalDefendingUnits,
			String battleName, 
			AssaultOutcome attackerRangedAttackOutcome,
			AssaultOutcome defenderRangedAttackOutcome,
			Pair<AssaultOutcome,AssaultOutcome> mountedAttackOutcome,
			Pair<AssaultOutcome,AssaultOutcome> meleeAttackOutcome, int attackerScore, int defenderScore) {
		csi.saveBuffer();
		List<String> messages = CommonUI.getBattleResultsString(originalAttackingUnits, originalDefendingUnits,battleName,attackerRangedAttackOutcome,defenderRangedAttackOutcome,mountedAttackOutcome,meleeAttackOutcome, attackerScore, defenderScore);
		for (String message: messages){
			showTextBox(message, 1, 0, 78, 24, CSIColor.YELLOW);
		}
		csi.restore();

		
	}
	
	@Override
	public void showBattleScene(String battleTitle,
			List<Equipment> attackingUnits, List<Equipment> defendingUnits) {
		csi.saveBuffer();
		int xBase = 31;
		int yBase = 4; 
		int gridX = 0;
		int gridY = 0;
		for (Equipment equipment: attackingUnits){
			CharAppearance appearance = (CharAppearance) equipment.getItem().getAppearance();
			for (int i = 0; i < equipment.getQuantity(); i++){
				csi.print(xBase + gridX, yBase + gridY,  appearance.getChar(), appearance.getColor());
				gridY ++;
				if (gridY > 12){
					gridX++;
					gridY = 0;
				}
			}
		}
		
		gridX = 16;
		gridY = 0;
		for (Equipment equipment: defendingUnits){
			CharAppearance appearance = (CharAppearance) equipment.getItem().getAppearance();
			for (int i = 0; i < equipment.getQuantity(); i++){
				csi.print(xBase + gridX, yBase + gridY,  appearance.getChar(), appearance.getColor());
				gridY ++;
				if (gridY > 12){
					gridX--;
					gridY = 0;
				}
			}
		}
		csi.refresh();
		csi.waitKeys(CharKey.SPACE, CharKey.ENTER);
		csi.restore();
	}
	
	@Override
	public boolean drawIdList() {
		return false;
	}
	
	@Override
	public boolean promptUnitList(List<Equipment> unitList, String title,
			String prompt) {
		Equipment.eqMode = true;
		MenuBox cacheBox = new MenuBox(csi);
   		cacheBox.setHeight(13);
   		cacheBox.setWidth(54);
   		cacheBox.setPosition(24,7);
  		Vector<InventoryItem> menuItems = new Vector<InventoryItem>();
  		for (Equipment item: unitList){
  			menuItems.add(new InventoryItem(item, getExpedition()));
  		}
  		cacheBox.setMenuItems(menuItems);
  		Collections.sort(menuItems, inventoryItemsComparator);
  		cacheBox.setPromptSize(2);
  		cacheBox.setBorder(true);
  		cacheBox.setPrompt(prompt);
  		cacheBox.setTitle(title);
  		cacheBox.draw();
		return prompt();
	}
	
	@Override
	public List<Equipment> selectItemsFromExpedition(String prompt, String verb, Appearance containerAppearance) {
		List<Equipment> expeditionEquipment = getExpedition().getInventory();
    	csi.saveBuffer();
   		Equipment.eqMode = true;
   		Map<String, Equipment> selectionMap = new HashMap<String, Equipment>();
		List<Equipment> selection = new ArrayList<Equipment>();
   		MenuBox cacheBox = new MenuBox(csi);
   		cacheBox.setHeight(13);
   		cacheBox.setWidth(54);
   		cacheBox.setPosition(24,7);
  		Vector menuItems = new Vector();
  		for (Equipment item: expeditionEquipment){
  			menuItems.add(new InventoryItem(new Equipment(item.getItem(), item.getQuantity()), getExpedition()));
  		}
  		cacheBox.setMenuItems(menuItems);
  		cacheBox.setPromptSize(2);
  		cacheBox.setBorder(true);
  		cacheBox.setPrompt(prompt+" [Space to exit]");
  		//cacheBox.setTitle("On Ship...");
  		cacheBox.setForeColor(ConsoleSystemInterface.RED);
  		cacheBox.setBorderColor(ConsoleSystemInterface.TEAL);
  		cacheBox.draw();
  		
		while (true) {
	  		cacheBox.setPrompt(prompt+" [Space to exit]");
			csi.refresh();
			InventoryItem itemChoice = ((InventoryItem)cacheBox.getSelection());
			if (itemChoice == null){
				break;
			}
			Equipment choice = itemChoice.getEquipment();
			ExpeditionItem item = (ExpeditionItem) choice.getItem();
			cacheBox.setPrompt("How many "+item.getDescription()+" will you "+verb+"?");
			cacheBox.draw();
			csi.refresh();
			int quantity = readQuantity(25, 9, "                       ", 5);
			if (quantity == 0)
				continue;
			
			if (quantity > choice.getQuantity()){
				cacheBox.setPrompt("Not enough "+choice.getItem().getDescription());
				cacheBox.draw();
				continue;
			}
			
			if (quantity == 0)
				continue;
			
			choice.reduceQuantity(quantity);
			
			if (selectionMap.get(item.getFullID()) == null){
				Equipment e = new Equipment(item, quantity);
				selectionMap.put(item.getFullID(), e);
				selection.add(e);
			} else {
				Equipment e = selectionMap.get(item.getFullID());
				e.setQuantity(e.getQuantity()+quantity);
			}
			
			if (choice.getQuantity() == 0){
				menuItems.remove(choice);
			}
			cacheBox.setPrompt("You "+verb+" "+quantity+" "+choice.getItem().getDescription()+" [Press Space]");
			cacheBox.draw();
			csi.refresh();
			csi.waitKeys(CharKey.SPACE, CharKey.ENTER);
	 		//menuBox.draw();
		}
		Equipment.eqMode = false;
		csi.restore();
 		csi.refresh();
 		return selection;
	}
	
	@Override
	public List<Building> createBuildingPlan() {
		List<Building> knownBuildings = getExpedition().getKnownBuildings();
		List<BuildingMenuItem> buildingMenuItems = new ArrayList<BuildingMenuItem>();
		for (Building building: knownBuildings){
			buildingMenuItems.add(new BuildingMenuItem(building));
		}
		MenuBox cacheBox = new MenuBox(csi);
   		cacheBox.setHeight(13);
   		cacheBox.setWidth(54);
   		cacheBox.setPosition(24,7);
  		cacheBox.setMenuItems(buildingMenuItems);
  		cacheBox.setPromptSize(2);
  		cacheBox.setBorder(true);
  		cacheBox.setPrompt("Building Plan [Space to exit]");
  		//cacheBox.setTitle("On Ship...");
  		cacheBox.setForeColor(ConsoleSystemInterface.RED);
  		cacheBox.setBorderColor(ConsoleSystemInterface.TEAL);
  		cacheBox.draw();
  		
  		String[] choices = new String[]{"Add", "Remove"};
  		int typeChoice = 0;
  		while (true){
  			String legend = "";
  			for (int i = 0; i < choices.length; i++){
  				if (i == typeChoice){
  					legend += ">";
  				}
  				legend += choices[i];
  				if (i == typeChoice){
  					legend += "<";
  				}
  				legend += "    ";
  			}
  			cacheBox.setPrompt(legend);
  			cacheBox.draw();
  	  		
	  		CharKey x = new CharKey(CharKey.NONE);
			while (x.code == CharKey.NONE)
				x = csi.inkey();
			
			if (x.isLeftArrow()){
				typeChoice--;
				if (typeChoice == -1)
					typeChoice = 0;
				continue;
			}
			if (x.isRightArrow()){
				typeChoice++;
				if (typeChoice == choices.length)
					typeChoice = choices.length-1;
				continue;
			}
			
			BuildingMenuItem buildingChoice = ((BuildingMenuItem)cacheBox.getSelection(x));

			if (buildingChoice == null){
				if (x.code != CharKey.SPACE && x.code != CharKey.ENTER){
					continue;
				}
				break;
			}
			
			if (typeChoice == 0){
				buildingChoice.add();
			} else {
				buildingChoice.remove();
			}
			
			cacheBox.draw();
			csi.refresh();
			//refresh();
  		}
  		List<Building> buildingPlan = new ArrayList<Building>();
  		for (BuildingMenuItem buildingMenuItem: buildingMenuItems){
  			for (int i = 0; i < buildingMenuItem.getQuantity(); i++){
  				buildingPlan.add(buildingMenuItem.getBuilding());
  			}
		}
  		
  		csi.restore();
 		csi.refresh();
 		return buildingPlan;
	}
	
	@Override
	public void showCityInfo(Town town) {
		String townInfo = CommonUI.getTownDescription(town);
   		printTextBox(townInfo, 15, 1, 50, 8, CSIColor.RED);
	}
	
	@Override
	public void afterTownAction() {
		csi.restore();
	}

	@Override
	public LandingParty selectLandingParty() {
		List<LandingParty> landingParties = CommonUI.getLandingParties();
		String[] landingPartiesDescription = new String[landingParties.size()];
		int i = 0;
		for (LandingParty landingParty: landingParties){
			landingPartiesDescription[i] = landingParty.getName();
			i++;
		}
		int choice = switchChat("Landing Parties", "Select a landing party", landingPartiesDescription);
		if (choice != -1)
			return landingParties.get(choice);
		else
			return null;
	}
	
	@Override
	public void notifyWeatherChange(Weather weather) {

	}
	
	@Override
	public void reactivate() {

	}

	@Override
	public void showImageBlockingMessage(String message, String imageIndex) {
		showBlockingMessage(message);
	}

	@Override
	public void showImageBlockingMessage(String message, String imageIndex, boolean keepMessage) {
		showBlockingMessage(message, keepMessage);
	}
}
