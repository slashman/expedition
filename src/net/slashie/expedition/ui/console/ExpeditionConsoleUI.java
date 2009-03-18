package net.slashie.expedition.ui.console;

import java.util.Calendar;
import java.util.List;
import java.util.Vector;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.ExpeditionItem;
import net.slashie.expedition.domain.ExpeditionUnit;
import net.slashie.expedition.domain.Good;
import net.slashie.expedition.domain.GoodsCache;
import net.slashie.expedition.domain.ShipCache;
import net.slashie.expedition.domain.Store;
import net.slashie.expedition.domain.Vehicle;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.ui.ExpeditionUserInterface;
import net.slashie.expedition.world.ExpeditionCell;
import net.slashie.expedition.world.ExpeditionLevel;
import net.slashie.expedition.world.ExpeditionMicroLevel;
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
import net.slashie.serf.sound.STMusicManagerNew;
import net.slashie.serf.ui.UserCommand;
import net.slashie.serf.ui.consoleUI.CharAppearance;
import net.slashie.serf.ui.consoleUI.ConsoleUserInterface;
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
		xrange = 10;
		yrange = 10;
		
		expeditionUnitBox = new ListBox(csi);
		expeditionUnitBox.setPosition(55,8);
		expeditionUnitBox.setWidth(22);
		expeditionUnitBox.setHeight(11);
		
		idList.setPosition(2,13);
		idList.setWidth(21);
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
		csi.print(5, 4, getExpedition().getExpeditionary());
		csi.print(5, 5, getExpedition().getExpeditionaryTitle());
		csi.print(5, 6, getExpedition().getAccountedGold()+"$");
		csi.print(12, 8, statsExpedition.getFoodDays()+"days");
		csi.print(12, 9, statsExpedition.getPower()+"");
		csi.print(12, 10, statsExpedition.getMovementMode().getDescription());
		if (getExpedition().getLevel() instanceof ExpeditionMicroLevel)
			csi.print(12, 11, statsExpedition.getOffshoreCurrentlyCarrying()+"%");
		else
			csi.print(12, 11, statsExpedition.getCurrentlyCarrying()+"%");
		
		Calendar gameTime = ((ExpeditionGame)player.getGame()).getGameTime(); 
		csi.print(5, 1, gameTime.get(Calendar.YEAR)+"");
		csi.print(5, 2, months[gameTime.get(Calendar.MONTH)] +" "+ gameTime.get(Calendar.DATE));
		csi.print(5, 3, getExpedition().getLocation().getDescription());
		
		Pair<String, String> locationDescription = getExpedition().getLocation().getLocationDescription(); 
		csi.print(70, 2, locationDescription.getA());
		csi.print(70, 3, locationDescription.getB());
		AbstractCell currentCell = getExpedition().getLocation().getMapCell(getExpedition().getPosition()); 
		csi.print(59, 1, currentCell.getDescription());
		csi.print(59, 4, getExpedition().getWeather());
		csi.print(70, 5, getExpedition().getTemperature()+"ºC");
		
		//This must be replaced on the next version of libjcsi
		expeditionUnitsVector.clear();
		expeditionUnitsVector.addAll(statsExpedition.getUnits());
		expeditionUnitBox.setElements(expeditionUnitsVector);
	}
	
	private void drawAddornment(){
		int addornmentColor = ConsoleSystemInterface.TEAL;
		 csi.print(0, 0, "    /----------------\\           /-----N-----\\           /------------------\\   ", addornmentColor);
		 csi.print(0, 1, "    |                |          /             \\          |                  |   ", addornmentColor);
		 csi.print(0, 2, "    |                |         /               \\         | LAT              |   ", addornmentColor);
		 csi.print(0, 3, "    |                |\\-------/                 \\-------/| LONG             |   ", addornmentColor);
		 csi.print(0, 4, "    |                |       /                   \\       |                  |   ", addornmentColor);
		 csi.print(0, 5, "    |                |       |                   |       | Temp             |   ", addornmentColor);
		 csi.print(0, 6, "    |                |       |                   |       |                  |   ", addornmentColor);
		 csi.print(0, 7, " /----~ EXPEDITION ~----\\    |                   |    /-----~ EXPEDITION ~----\\ ", addornmentColor);
		 csi.print(0, 8, " |Food Days             |    |                   |    |                       | ", addornmentColor);
		 csi.print(0, 9, " |Power                 |    |                   |    |                       | ", addornmentColor);
		 csi.print(0, 10, " |Movement              |    W         @         E    |                       | ", addornmentColor);
		 csi.print(0, 11, " |Carrying              |    |                   |    |                       | ", addornmentColor);
		 csi.print(0, 12, " \\----------------------/    |                   |    |                       | ", addornmentColor);
		 csi.print(0, 13, " |                      |    |                   |    |                       | ", addornmentColor);
		 csi.print(0, 14, " |                      |    |                   |    |                       | ", addornmentColor);
		 csi.print(0, 15, " |                      |    |                   |    |                       | ", addornmentColor);
		 csi.print(0, 16, " |                      |    \\                   /    |                       | ", addornmentColor);
		 csi.print(0, 17, " |                      |-----\\                 /-----|                       | ", addornmentColor);
		 csi.print(0, 18, " |                      |      \\               /      |                       | ", addornmentColor);
		 csi.print(0, 19, " |                      |       \\             /       |                       | ", addornmentColor);
		 csi.print(0, 20, " \\----------------------/        \\-----S-----/        \\-----------------------/ ", addornmentColor);
		 
	}
	
	private String[] months = new String[]{
		"Janvary",
		"Febrvary",
		"March",
		"April",
		"May",
		"Jvne",
		"Jvly",
		"Avgvst",
		"September",
		"October",
		"November",
		"December"
	};

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
		// TODO Auto-generated method stub
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
			String itemDescription = item.getItem().getDescription();
			int inventory = item.getQuantity();
			int stock = offShore.getOffshoreCarryable((ExpeditionItem)item.getItem());
			if (item.getItem() instanceof ExpeditionUnit){
				return itemDescription + ", "+store.getPrizeFor((ExpeditionItem)item.getItem())+"$/month (max "+stock+") {"+inventory+" Available}";
			} else {
				return itemDescription + " for "+store.getPrizeFor((ExpeditionItem)item.getItem())+"$ (max "+stock+") {Stock:"+inventory+"}";
			}
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
			String itemDescription = item.getItem().getDescription();
			int inventory = item.getQuantity();
			int stock = 0;
			if (expedition != null){
				if (item.getItem() instanceof Good){ 
					stock = expedition.getCarryable((ExpeditionItem)item.getItem());
					return itemDescription + " (Max "+stock+") {Available "+inventory+"}";
				} else {
					return itemDescription + " {Available "+inventory+"}";
				}
			} else {
				stock = cache.getCarryable((ExpeditionItem)item.getItem());
				if (stock == -1)
					return itemDescription + " {On Expedition "+inventory+"}";
				else 
					return itemDescription + " (Max "+stock+") {On Expedition "+inventory+"}";
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
		while (true) {
	  		menuBox.setPrompt("Welcome to the "+store.getOwnerName()+" [Space to leave]");
			csi.refresh();
			//menuBox.setTitle(who.getName()+" (Gold:"+player.getGold()+")");
			StoreItem itemChoice = ((StoreItem)menuBox.getSelection());
			if (itemChoice == null)
				break;
			Equipment choice = itemChoice.getEquipment();
			ExpeditionItem item = (ExpeditionItem) choice.getItem();
			menuBox.setPrompt("How many "+item.getDescription()+"?");
			menuBox.draw();
			csi.refresh();
			int quantity = readQuantity(27, 9, "                       ", 5);
			if (quantity == 0)
				continue;
			
			if (!getExpedition().canCarryOffshore(item, quantity)){
				menuBox.setPrompt("Your ships are full!");
				continue;
			}
			
			int gold = store.getPrizeFor(item)*quantity;	
			if (item instanceof ExpeditionUnit){
				menuBox.setPrompt("Hire "+quantity+" "+item.getDescription()+" for "+gold+" maravedíes per month? (Y/n)");
			} else {
				menuBox.setPrompt("Buy "+quantity+" "+item.getDescription()+" for "+gold+" maravedíes? (Y/n)");
			}
			menuBox.draw();
	 		if (prompt())
	 			if (getExpedition().getAccountedGold() >= gold) {
	 				getExpedition().reduceAccountedGold(gold);
	 				getExpedition().addItemOffshore((ExpeditionItem) choice.getItem(), quantity);
	 				choice.reduceQuantity(quantity);
					menuBox.setPrompt("Thanks!");
					refresh();
			 	} else {
			 		menuBox.setPrompt("You can't afford it!");
			 	}
			else {
				menuBox.setPrompt("Welcome...");
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
			choice.reduceQuantity(quantity);
			
			if (getExpedition().getCurrentlyCarrying()>100){
				cacheBox.setPrompt("The expedition can't carry the goods!");
				cacheBox.draw();
				choice.increaseQuantity(quantity);
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
}
