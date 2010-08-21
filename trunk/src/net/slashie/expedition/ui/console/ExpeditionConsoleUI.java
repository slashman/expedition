package net.slashie.expedition.ui.console;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
import net.slashie.expedition.domain.Expedition.MovementSpeed;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.ui.CommonUI;
import net.slashie.expedition.ui.ExpeditionUserInterface;
import net.slashie.expedition.world.ExpeditionCell;
import net.slashie.expedition.world.ExpeditionLevel;
import net.slashie.expedition.world.ExpeditionMicroLevel;
import net.slashie.libjcsi.CharKey;
import net.slashie.libjcsi.ConsoleSystemInterface;
import net.slashie.libjcsi.textcomponents.ListBox;
import net.slashie.libjcsi.textcomponents.ListItem;
import net.slashie.libjcsi.textcomponents.MenuBox;
import net.slashie.libjcsi.textcomponents.MenuItem;
import net.slashie.libjcsi.textcomponents.TextBox;
import net.slashie.serf.action.Action;
import net.slashie.serf.action.Actor;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.game.Player;
import net.slashie.serf.level.AbstractCell;
import net.slashie.serf.sound.STMusicManagerNew;
import net.slashie.serf.ui.UserCommand;
import net.slashie.serf.ui.consoleUI.CharAppearance;
import net.slashie.serf.ui.consoleUI.ConsoleUserInterface;
import net.slashie.serf.ui.consoleUI.EquipmentMenuItem;
import net.slashie.util.Pair;
import net.slashie.utils.Position;

public class ExpeditionConsoleUI extends ConsoleUserInterface implements ExpeditionUserInterface{
	private ConsoleSystemInterface csi;
	private ListBox expeditionUnitBox;
	
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
		expeditionUnitBox.setPosition(53,7);
		expeditionUnitBox.setWidth(25);
		expeditionUnitBox.setHeight(12);
		
		idList.setPosition(2,13);
		idList.setWidth(23);
		idList.setHeight(6);
		
	}
	@Override
	public void beforeSeenListCompilation() {
		// TODO Auto-generated method stub
		
	}

	private Vector<Equipment> expeditionUnitsVector = new Vector<Equipment>();
	@Override
	public void drawStatus() {
		Expedition statsExpedition = getExpedition();
		
		//csi.print(1,1, getPlayer().getPosition().x()+","+getPlayer().getPosition().y());
		drawAddornment();
		// Box 1
		Calendar gameTime = ((ExpeditionGame)player.getGame()).getGameTime(); 
		csi.print(5, 1, gameTime.get(Calendar.YEAR)+"");
		csi.print(5, 2, months[gameTime.get(Calendar.MONTH)] +" "+ gameTime.get(Calendar.DATE));
		csi.print(5, 3, getExpedition().getExpeditionaryTitle());
		if (getExpedition().getTowns().size() == 1)
			csi.print(5, 4, "1 settlement ");
		else
			csi.print(5, 4, getExpedition().getTowns().size()+" settlements");
		csi.print(5, 5, getExpedition().getAccountedGold()+"$");
		
		// Box 2
		csi.print(12, 7, statsExpedition.getTotalShips()+" ships ("+statsExpedition.getShipHealth()+"%)");
		csi.print(12, 8, statsExpedition.getOffshoreFoodDays()+" days");
		csi.print(12, 9, statsExpedition.getPower()+(statsExpedition.isArmed()?"(Armed)":""));
		if (statsExpedition.getMovementSpeed() != MovementSpeed.NORMAL){
			csi.print(12, 10, statsExpedition.getMovementMode().getDescription()+"("+statsExpedition.getMovementSpeed().getDescription()+")");
		} else {
			csi.print(12, 10, statsExpedition.getMovementMode().getDescription());
		}
		if (getExpedition().getLevel() instanceof ExpeditionMicroLevel)
			csi.print(12, 11, statsExpedition.getOffshoreCurrentlyCarrying()+"%");
		else
			csi.print(12, 11, statsExpedition.getCurrentlyCarrying()+"%");
		
		//Box 3
		AbstractCell currentCell = getExpedition().getLocation().getMapCell(getExpedition().getPosition());
		Pair<String, String> locationDescription = getExpedition().getLocation().getLocationDescription();
		Pair<String, String> locationMeans = getExpedition().getLocation().getLocationMeans();
		csi.print(56, 1, getExpedition().getLocation().getDescription());
		csi.print(56, 2, currentCell.getDescription());
		csi.print(56, 3, locationDescription.getA()+" ("+locationMeans.getA()+")");
		csi.print(56, 4, locationDescription.getB()+" ("+locationMeans.getB()+")");
		csi.print(56, 5, getExpedition().getLocation().getWeather().getDescription()+", "+getExpedition().getLocation().getTemperatureDescription());
		
		//This must be replaced on the next version of libjcsi
		expeditionUnitsVector.clear();
		expeditionUnitsVector.addAll(statsExpedition.getUnits());
		
		Vector expeditionUnitItems = new Vector();
		for (Equipment expeditionUnit: expeditionUnitsVector){
			expeditionUnitItems.add(new UnitMenuItem(expeditionUnit));
		}
		Collections.sort(expeditionUnitItems, expeditionUnitsComparator);
		for (Vehicle expeditionVehicle: statsExpedition.getCurrentVehicles()){
			expeditionUnitItems.add(new VehicleMenuItem(expeditionVehicle));
		}
		
		expeditionUnitBox.setElements(expeditionUnitItems);
	}
	
	private void drawAddornment(){
		int addornmentColor = ConsoleSystemInterface.TEAL;
		csi.print(0, 0,  "    /------------------\\     /---------N---------\\     /--------------------\\   ", addornmentColor);
		csi.print(0, 1,  "    |                  |     |", addornmentColor);
		csi.print(49, 1, "|     |                    |   ", addornmentColor);
		csi.print(0, 2,  "    |                  |     |",addornmentColor);
		csi.print(49, 2, "|     |                    |   ", addornmentColor);
		csi.print(0, 3,  "    |                  |-----|",addornmentColor);
		csi.print(49, 3, "|-----|                    |   ", addornmentColor);
		csi.print(0, 4,  "    |                  |.....|", addornmentColor);
		csi.print(49, 4,"|.....|                    |   ", addornmentColor);
		csi.print(0, 5,  "    |                  |.....|", addornmentColor);
		csi.print(49, 5,  "|.....|                    |   ", addornmentColor);
		csi.print(0, 6,  " /----~ EXPEDITION ~------\\..|                   |../-------~ EXPEDITION ~-----\\", addornmentColor);
		csi.print(0, 7,  " |Ships                   |..|                   |..|                          |", addornmentColor);
		csi.print(0, 8,  " |Food Days               |..|                   |..|                          |", addornmentColor);
		csi.print(0, 9,  " |Power                   |..|                   |..|                          |", addornmentColor);
		csi.print(0, 10, " |Movement                |..W                   E..|                          |", addornmentColor);
		csi.print(0, 11, " |Carrying                |..|                   |..|                          |", addornmentColor);
		csi.print(0, 12, " \\------------------------/..|                   |..|                          |", addornmentColor);
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
		if (expeditionLevel.getMusicKey() != null)
			STMusicManagerNew.thus.playKey(expeditionLevel.getMusicKey());
	}

	private Expedition getExpedition(){
		return (Expedition)getPlayer();
	}
	
	@Override
	public void beforeRefresh() {
		expeditionUnitBox.draw();
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
  		menuBox.setWidth(54);
  		menuBox.setPosition(24,7);
  		
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
			StoreItemInfo storeItemInfo = store.getPriceFor(item);
			if (storeItemInfo.getPack() > 1)
				menuBox.setPrompt("How many "+storeItemInfo.getPackDescription()+" of "+item.getPluralDescription()+"?");
			else
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
			
			int quantity = buyQuantity * storeItemInfo.getPack();
			
			if (!getExpedition().canCarryOffshore(item, quantity)){
				prompt = "Your ships are full! Do you need anything else?";
				continue;
			}
			
			int gold = storeItemInfo.getPrice() * buyQuantity;	
			if (item instanceof ExpeditionUnit){
				if (quantity > 1)
					menuBox.setPrompt("Hire "+quantity+" "+item.getPluralDescription()+" for "+gold+" maravedíes? (Y/n)");
				else
					menuBox.setPrompt("Hire a "+item.getDescription()+" for "+gold+" maravedíes? (Y/n)");
			} else {
				if (quantity > 1)
					menuBox.setPrompt("Buy "+quantity+" "+item.getPluralDescription()+" for "+gold+" maravedíes? (Y/n)");
				else
					menuBox.setPrompt("Buy a "+item.getDescription()+" for "+gold+" maravedíes? (Y/n)");
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
		if (promptChat("Do you want to leave the ports of "+player.getLevel().getDescription()+"? (Y/n)", 28, 2, 23, 5)){
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
		csi.waitKey(CharKey.SPACE);
	}
	
	public boolean promptChat(String message){
		return super.promptChat(message, 28,2,23,5);
	}

	public void transferFromCache(GoodsCache cache) {
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
  		cacheBox.setPrompt("Transfer from "+cache.getDescription()+" to Expedition");
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
			
			if (item instanceof Good && !getExpedition().canCarry(item, quantity)){
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
  		cacheBox.setPrompt("Transfer from Expedition to "+ship.getDescription());
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
			
			if (item instanceof Good && !ship.canCarry(item, quantity)){
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
				continue;
			}
			
			ship.addItem(choice.getItem(), quantity);
			
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
	public int switchChat(String prompt, String... options) {
		MenuBox selectionBox = new MenuBox(csi);
		selectionBox.setPosition(20,2);
		selectionBox.setWidth(31);
		selectionBox.setHeight(8);
  		Vector<MenuItem> menuItems = new Vector<MenuItem>();
  		int i = 0;
  		for (String option: options){
  			menuItems.add(new SimpleItem(i,option));
  			i++;
  		}
  		selectionBox.setMenuItems(menuItems);
  		selectionBox.setPromptSize(2);
  		selectionBox.setBorder(true);
  		selectionBox.setPrompt(prompt);
  		selectionBox.draw();
  		
		while (true) {
			csi.refresh();
			SimpleItem itemChoice = ((SimpleItem)selectionBox.getSelection());
			if (itemChoice == null)
				break;
			return itemChoice.getValue();
		}
		return -1;	
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
  			csi.print(xpos,ypos+3,  "   ( ) Units        ( ) Tools        ( ) Goods        ( ) Valuables     ", ConsoleSystemInterface.BLUE);
  			csi.print(5+choice*17, ypos+3, "*", ConsoleSystemInterface.WHITE);
  			
  	  		List<Equipment> inventory = null;
  	  		switch (choice){
  	  		case 0:
  	  			inventory = getExpedition().getUnits();
  	  			break;
  	  		case 1:
  	  			inventory = getExpedition().getTools();
  	  			break;
  	  		case 2:
  	  			inventory = getExpedition().getGoods();
  	  			break;
  	  		case 3:
  	  			inventory = getExpedition().getValuables();
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
			while (x.code != CharKey.SPACE && !x.isArrow())
				x = csi.inkey();
			if (x.code == CharKey.SPACE || x.code == CharKey.ESC){
				break;
			}
			if (x.isLeftArrow()){
				choice--;
				if (choice == -1)
					choice = 0;
			}
			if (x.isRightArrow()){
				choice++;
				if (choice == 4)
					choice = 3;
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
				return quantity + " " + itemDescription + " ATK"+ unit.getAttack()+" DEF"+ unit.getDefense() +" {Weight: "+(eitem.getWeight() * quantity)+")";
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
}
