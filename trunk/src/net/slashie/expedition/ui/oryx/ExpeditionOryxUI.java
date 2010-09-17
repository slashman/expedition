package net.slashie.expedition.ui.oryx;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import net.slashie.expedition.domain.AssaultOutcome;
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
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.ui.CommonUI;
import net.slashie.expedition.ui.ExpeditionUserInterface;
import net.slashie.expedition.world.ExpeditionLevel;
import net.slashie.expedition.world.ExpeditionMicroLevel;
import net.slashie.expedition.world.TemperatureRules;
import net.slashie.libjcsi.CharKey;
import net.slashie.serf.action.Action;
import net.slashie.serf.action.Actor;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.level.AbstractCell;
import net.slashie.serf.sound.STMusicManagerNew;
import net.slashie.serf.ui.UserCommand;
import net.slashie.serf.ui.oryxUI.AddornedBorderPanel;
import net.slashie.serf.ui.oryxUI.GFXAppearance;
import net.slashie.serf.ui.oryxUI.GFXUserInterface;
import net.slashie.serf.ui.oryxUI.SwingSystemInterface;
import net.slashie.util.Pair;
import net.slashie.utils.ImageUtils;
import net.slashie.utils.swing.BorderedMenuBox;
import net.slashie.utils.swing.GFXMenuItem;
import net.slashie.utils.swing.MenuBox;

public class ExpeditionOryxUI extends GFXUserInterface implements ExpeditionUserInterface{
	private final class ItemsComparator implements Comparator<GFXMenuItem> {
		@Override
		public int compare(GFXMenuItem arg0, GFXMenuItem arg1) {
			return arg0.getGroupClassifier().compareTo(arg1.getGroupClassifier());
		}
	}
	private ItemsComparator ITEMS_COMPARATOR = new ItemsComparator();
	private Color TITLE_COLOR = new Color(224,226,108);
	private Color TEXT_COLOR = Color.WHITE;

	private Image BATTLE_BACKGROUND;
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
		Equipment.eqMode = true;
		BorderedMenuBox menuBox = new BorderedMenuBox(BORDER1, BORDER2, BORDER3, BORDER4, si, COLOR_WINDOW_BACKGROUND, COLOR_BORDER_IN, COLOR_BORDER_OUT, tileSize, 6,9,12,tileSize+6, null);
   		menuBox.setItemsPerPage(12);
  		menuBox.setBounds(160, 16, 624,480);
  		menuBox.setTitle("Expedition Inventory [Space to exit]");
  		si.saveBuffer();
  		int choice = 0;
  		while (true){
  			String legend = "";
  			for (int i = 0; i < 4; i++){
  				if (i == choice){
  					legend += ">";
  				}
  				switch (i){
  				case 0:
  					legend += "Units";
  					break;
  				case 1:
  					legend += "Tools";
  					break;
  				case 2:
  					legend += "Goods";
  					break;
  				case 3:
  					legend += "Valuables";
  					break;
  				}
  				if (i == choice){
  					legend += "<";
  				}
  				legend += "    ";
  			}
  			
  			menuBox.setLegend(legend);
  			
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
  	  			menuItems.add(new InventoryGFXMenuItem(item));
  	  		}
  	  		Collections.sort(menuItems, inventoryItemsComparator);
  	  		menuBox.setMenuItems(menuItems);
  	  		menuBox.draw();
  	  		
	  		CharKey x = new CharKey(CharKey.NONE);
			while (x.code != CharKey.SPACE && !x.isArrow())
				x = si.inkey();
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
  		si.restore();
 		si.refresh();
 		Equipment.eqMode = false;
	}

	private Comparator<InventoryGFXMenuItem> inventoryItemsComparator = new Comparator<InventoryGFXMenuItem>(){
		public int compare(InventoryGFXMenuItem o1, InventoryGFXMenuItem o2) {
			if (o1.getEquipment().getItem() instanceof ExpeditionUnit){
				if (o2.getEquipment().getItem() instanceof ExpeditionUnit){
					return o1.getEquipment().getItem().getFullID().compareTo(o2.getEquipment().getItem().getFullID());
				} else {
					return -1;
				}
			} else if (o2.getEquipment().getItem() instanceof ExpeditionUnit){
				if (o1.getEquipment().getItem() instanceof ExpeditionUnit){
					return o1.getEquipment().getItem().getFullID().compareTo(o2.getEquipment().getItem().getFullID());
				} else {
					return 1;
				}
			} else {
				return o1.getEquipment().getItem().getDescription().compareTo(o2.getEquipment().getItem().getDescription());
			}
		};
	};
	
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
  		selectionBox.setLegend(prompt);
  		selectionBox.setTitleColor(TITLE_COLOR);
  		selectionBox.setForeColor(TEXT_COLOR);
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
		si.setColor(TEXT_COLOR);
		si.printAtPixel(x+tileSize, y+tileSize*2, prompt);
		
		String ret = si.input(xp,yp,TEXT_COLOR,length);
		
		return ret;
	}

	@Override
	public void onMusicOn() {
		ExpeditionLevel expeditionLevel = (ExpeditionLevel)getExpedition().getLevel();
		if (expeditionLevel.getMusicKey() != null)
			STMusicManagerNew.thus.playKey(expeditionLevel.getMusicKey());
	}

	public boolean depart() {
		if (promptChat("Do you want to leave the ports of Palos de la frontera? (Y/n)", 28, 2, 23, 5)){
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
  		menuBox.setItemsPerPage(12);
  		menuBox.setBounds(160, 16, 624,480);
  		int fontSize = si.getGraphics2D().getFont().getSize();
  		
  		Vector menuItems = new Vector();
  		for (Equipment item: merchandise){
  			menuItems.add(new StoreGFXMenuItem(item, store, getExpedition()));
  		}
  		
  		menuBox.setMenuItems(menuItems);
  		menuBox.draw();
  		menuBox.setTitleColor(TITLE_COLOR);
  		menuBox.setForeColor(TEXT_COLOR);
  		menuBox.setTitle(store.getOwnerName()+" [Press Space to exit]");
  		//menuBox.setBorder(true);
  		String prompt = "Welcome to the "+store.getOwnerName();
  		
		while (true) {
			menuBox.setLegend(prompt);
			si.refresh();
			//menuBox.setTitle(who.getName()+" (Gold:"+player.getGold()+")");
			StoreGFXMenuItem itemChoice = ((StoreGFXMenuItem)menuBox.getSelection());
			if (itemChoice == null)
				break;
			Equipment choice = itemChoice.getEquipment();
			ExpeditionItem item = (ExpeditionItem) choice.getItem();
			StoreItemInfo storeItemInfo = store.getPriceFor(item);
			if (storeItemInfo.getPack() > 1)
				menuBox.setLegend("How many "+storeItemInfo.getPackDescription()+" of "+item.getPluralDescription()+"?");
			else
				menuBox.setLegend("How many "+item.getPluralDescription()+"?");
			menuBox.draw();
			si.refresh();
			int buyQuantity = readQuantity(657, 86, "                       ", 5);
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
					menuBox.setLegend("Hire "+quantity+" "+item.getPluralDescription()+" for "+gold+" maravedíes? (Y/n)");
				else
					menuBox.setLegend("Hire a "+item.getDescription()+" for "+gold+" maravedíes? (Y/n)");
			} else {
				if (quantity > 1)
					menuBox.setLegend("Buy "+quantity+" "+item.getPluralDescription()+" for "+gold+" maravedíes? (Y/n)");
				else
					menuBox.setLegend("Buy a "+item.getDescription()+" for "+gold+" maravedíes? (Y/n)");
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
   		
   		BorderedMenuBox menuBox = new BorderedMenuBox(BORDER1, BORDER2, BORDER3, BORDER4, si, COLOR_WINDOW_BACKGROUND, COLOR_BORDER_IN, COLOR_BORDER_OUT, tileSize, 6,9,12,tileSize+6, null);
   		menuBox.setItemsPerPage(12);
  		menuBox.setBounds(160, 16, 624,480);
  		menuBox.setTitle("Transfer from "+cache.getDescription()+" [Space to exit]");
  		menuBox.setLegend("Select the units or equipment");
  		int typeChoice = 0;
  		while (true){
  			String legend = "";
  			for (int i = 0; i < 4; i++){
  				if (i == typeChoice){
  					legend += ">";
  				}
  				switch (i){
  				case 0:
  					legend += "Units";
  					break;
  				case 1:
  					legend += "Tools";
  					break;
  				case 2:
  					legend += "Goods";
  					break;
  				case 3:
  					legend += "Valuables";
  					break;
  				}
  				if (i == typeChoice){
  					legend += "<";
  				}
  				legend += "    ";
  			}
  			
  			menuBox.setLegend(legend);
  			
  	  		List<Equipment> inventory = null;
  	  		switch (typeChoice){
  	  		case 0:
  	  			inventory = cache.getUnits();
  	  			break;
  	  		case 1:
  	  			inventory = cache.getTools();
  	  			break;
  	  		case 2:
  	  			inventory = cache.getGoods();
  	  			break;
  	  		case 3:
  	  			inventory = cache.getValuables();
  	  		}
  	  		
  	  		Vector menuItems = new Vector();
  	  		for (Equipment item: inventory){
  	  			menuItems.add(new CacheGFXMenuItem(item, getExpedition()));
  	  		}
  	  		Collections.sort(menuItems, ITEMS_COMPARATOR);
  	  		menuBox.setMenuItems(menuItems);
  	  		menuBox.draw();
  	  		
	  		CharKey x = new CharKey(CharKey.NONE);
			while (x.code == CharKey.NONE)
				x = si.inkey();
			
			if (x.isLeftArrow()){
				typeChoice--;
				if (typeChoice == -1)
					typeChoice = 0;
				continue;
			}
			if (x.isRightArrow()){
				typeChoice++;
				if (typeChoice == 4)
					typeChoice = 3;
				continue;
			}
			
			CacheGFXMenuItem itemChoice = ((CacheGFXMenuItem)menuBox.getSelection(x));
			if (itemChoice == null){
				if (x.code != CharKey.SPACE){
					continue;
				}
				if (cache instanceof ShipCache){
					if (getExpedition().getTotalUnits() > 0)
						break;
					else {
						menuBox.setLegend("You must first disembark. [Press Space]");
			  	  		menuBox.draw();
			  	  		si.waitKey(CharKey.SPACE);
						continue;
					}
				} else {
					break;
				}
			}
			Equipment choice = itemChoice.getEquipment();
			ExpeditionItem item = (ExpeditionItem) choice.getItem();
			menuBox.setLegend("How many "+item.getDescription()+" will you transfer?");
			menuBox.draw();
			si.refresh();
			int quantity = readQuantity(657, 86, "                       ", 5);
			if (quantity == 0)
				continue;
			
			if (!(choice.getItem() instanceof ExpeditionUnit) && getExpedition().getTotalUnits() == 0){
				menuBox.setLegend("Someone must receive the goods! [Press Space]");
				menuBox.draw();
	  	  		si.waitKey(CharKey.SPACE);
				continue;
			}
			
			if (quantity > choice.getQuantity()){
				menuBox.setLegend("Not enough "+choice.getItem().getDescription()+" [Press Space]");
				menuBox.draw();
	  	  		si.waitKey(CharKey.SPACE);
				continue;
			}
			
			if (item instanceof Good && !getExpedition().canCarry(item, quantity)){
				menuBox.setLegend("Your expedition is full! [Press Space]");
				menuBox.draw();
	  	  		si.waitKey(CharKey.SPACE);
				continue;
			}
			choice.reduceQuantity(quantity);
			getExpedition().addItem(choice.getItem(), quantity);
			
			if (choice.getQuantity() == 0){
				cacheEquipment.remove(choice);
			}
			
			menuBox.setLegend(choice.getItem().getDescription()+" transfered. [Press Space]");
			menuBox.draw();
			si.waitKey(CharKey.SPACE);
			refresh();
	 		//menuBox.getSelection();
  		}
  		
 		if (cache.destroyOnEmpty() && cache.getItems().size() == 0)
			level.destroyFeature(cache);
		Equipment.eqMode = false;
		si.restore();
 		si.refresh();
 		
	}
	
	public void transferFromExpedition(GoodsCache ship, int minUnits) {
   		Equipment.eqMode = true;
   		BorderedMenuBox menuBox = new BorderedMenuBox(BORDER1, BORDER2, BORDER3, BORDER4, si, COLOR_WINDOW_BACKGROUND, COLOR_BORDER_IN, COLOR_BORDER_OUT, tileSize, 6,9,12,tileSize+6, null);
   		menuBox.setItemsPerPage(12);
  		menuBox.setBounds(160, 16, 624,480);
  		menuBox.setTitle("Transfer to "+ship.getDescription()+" [Space to exit]");
  		menuBox.setLegend("Select the units to remove from the expedition");
  		int typeChoice = 0;
  		while (true){
  			String legend = "";
  			for (int i = 0; i < 4; i++){
  				if (i == typeChoice){
  					legend += ">";
  				}
  				switch (i){
  				case 0:
  					legend += "Units";
  					break;
  				case 1:
  					legend += "Tools";
  					break;
  				case 2:
  					legend += "Goods";
  					break;
  				case 3:
  					legend += "Valuables";
  					break;
  				}
  				if (i == typeChoice){
  					legend += "<";
  				}
  				legend += "    ";
  			}
  			
  			menuBox.setLegend(legend);
  			
  	  		List<Equipment> inventory = null;
  	  		switch (typeChoice){
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
  	  			menuItems.add(new CacheGFXMenuItem(item, ship));
  	  		}
  	  		Collections.sort(menuItems, ITEMS_COMPARATOR);
  	  		menuBox.setMenuItems(menuItems);
  	  		menuBox.draw();
  	  		
	  		CharKey x = new CharKey(CharKey.NONE);
			while (x.code == CharKey.NONE)
				x = si.inkey();
			
			if (x.isLeftArrow()){
				typeChoice--;
				if (typeChoice == -1)
					typeChoice = 0;
				continue;
			}
			if (x.isRightArrow()){
				typeChoice++;
				if (typeChoice == 4)
					typeChoice = 3;
				continue;
			}
			
			CacheGFXMenuItem itemChoice = ((CacheGFXMenuItem)menuBox.getSelection(x));

			if (itemChoice == null){
				if (x.code != CharKey.SPACE){
					continue;
				}
				if (minUnits != -1){
					if (ship.getTotalUnits() < minUnits){
						menuBox.setLegend("At least "+minUnits+" should be transfered. [Press Space]");
						menuBox.draw();
						si.waitKey(CharKey.SPACE);
						continue;
					}
				}
				break;
			}
			Equipment choice = itemChoice.getEquipment();
			ExpeditionItem item = (ExpeditionItem) choice.getItem();
			menuBox.setLegend("How many "+item.getDescription()+" will you transfer?");
			menuBox.draw();
			si.refresh();
			int quantity = readQuantity(657, 86, "                       ", 5);
			
			if (quantity == 0)
				continue;
			
			if (quantity > choice.getQuantity()){
				menuBox.setLegend("Not enough "+choice.getItem().getDescription()+" [Press Space]");
				menuBox.draw();
				si.waitKey(CharKey.SPACE);
				continue;
			}
			
			if (item instanceof Good && !ship.canCarry(item, quantity)){
				menuBox.setLegend("Not enough room in the "+ship.getDescription()+" [Press Space]");
				menuBox.draw();
				si.waitKey(CharKey.SPACE);
				continue;
			}
			
			getExpedition().reduceQuantityOf(choice.getItem(), quantity);
			
			if (choice.getItem() instanceof ExpeditionUnit && 
					getExpedition().getCurrentlyCarrying()>100){
				menuBox.setLegend("The expedition can't carry the goods! [Press Space]");
				menuBox.draw();
				si.waitKey(CharKey.SPACE);
				getExpedition().addItem(choice.getItem(), quantity);
				if (choice.getQuantity() == 0){
					/* Guess this is no longer needed
					menuItems = new Vector();
			  		for (Equipment item2: getExpedition().getInventory()){
			  			menuItems.add(new CacheGFXMenuItem(item2, ship));
			  		}
			  		Collections.sort(menuItems, ITEMS_COMPARATOR);
			  		menuBox.setMenuItems(menuItems);
			  		*/				
			  	}
				continue;
			}
			
			ship.addItem(choice.getItem(), quantity);
			
			if (choice.getQuantity() == 0){
				menuItems.remove(choice);
			}
			menuBox.setLegend(choice.getItem().getDescription()+" transfered into the "+ship.getDescription()+" [Press Space]");
			menuBox.draw();
			si.waitKey(CharKey.SPACE);
			refresh();
  		}
  		
		Equipment.eqMode = false;
		si.restore();
 		si.refresh();
	}
	
	public void transferFromCacheOld(GoodsCache cache) {
		List<Equipment> cacheEquipment = cache.getItems();
   		Equipment.eqMode = true;

   		//Item.shopMode = true;
   		BorderedMenuBox cacheBox = new BorderedMenuBox(BORDER1, BORDER2, BORDER3, BORDER4, si, COLOR_WINDOW_BACKGROUND, COLOR_BORDER_IN, COLOR_BORDER_OUT, tileSize, 6,9,12,tileSize+6, null);
   		cacheBox.setItemsPerPage(12);
   		cacheBox.setBounds(160, 16, 624,480);
  		Vector<GFXMenuItem> menuItems = new Vector<GFXMenuItem>();
  		for (Equipment item: cacheEquipment){
  			menuItems.add(new CacheGFXMenuItem(item, getExpedition()));
  		}
  		Collections.sort(menuItems, ITEMS_COMPARATOR);
  		cacheBox.setMenuItems(menuItems);
  		
  		//cacheBox.setTitle("On Ship...");
  		cacheBox.setTitleColor(TITLE_COLOR);
  		cacheBox.setForeColor(TEXT_COLOR);
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
						cacheBox.setLegend("You must first disembark");
						continue;
					}
				} else {
					break;
				}
			}
			Equipment choice = itemChoice.getEquipment();
			ExpeditionItem item = (ExpeditionItem) choice.getItem();
			cacheBox.setLegend("How many "+item.getDescription()+" will you transfer?");
			cacheBox.draw();
			si.refresh();
			int quantity = readQuantity(657, 86, "                       ", 5);
			if (quantity == 0)
				continue;
			
			if (!(choice.getItem() instanceof ExpeditionUnit) && getExpedition().getTotalUnits() == 0){
				cacheBox.setLegend("Someone must receive the goods!");
				cacheBox.draw();
				continue;
			}
			
			if (quantity > choice.getQuantity()){
				cacheBox.setLegend("Not enough "+choice.getItem().getDescription());
				cacheBox.draw();
				continue;
			}
			
			if (item instanceof Good && !getExpedition().canCarry(item, quantity)){
				cacheBox.setLegend("Your expedition is full!");
				cacheBox.draw();
				continue;
			}
			choice.reduceQuantity(quantity);
			getExpedition().addItem(choice.getItem(), quantity);
			
			if (choice.getQuantity() == 0){
				cacheEquipment.remove(choice);
			}
			
			cacheBox.setLegend(choice.getItem().getDescription()+" transfered.");
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

	public void transferFromExpeditionOld(GoodsCache ship, int minUnits) {
		List<Equipment> expeditionEquipment = getExpedition().getInventory();
   		Equipment.eqMode = true;
   		BorderedMenuBox cacheBox = new BorderedMenuBox(BORDER1, BORDER2, BORDER3, BORDER4, si, COLOR_WINDOW_BACKGROUND, COLOR_BORDER_IN, COLOR_BORDER_OUT, tileSize, 6,9,12,tileSize+6, null);
   		cacheBox.setItemsPerPage(12);
   		cacheBox.setBounds(160, 16, 624,480);
  		
  		Vector menuItems = new Vector();
  		for (Equipment item: expeditionEquipment){
  			menuItems.add(new CacheGFXMenuItem(item, ship));
  		}
  		Collections.sort(menuItems, ITEMS_COMPARATOR);
  		cacheBox.setMenuItems(menuItems);
  		cacheBox.setTitle("Transfer from Expedition to "+ship.getDescription()+" [Space to exit]");
  		cacheBox.setLegend("Select the units to remove from the expedition");
  		//cacheBox.setTitle("On Ship...");
  		cacheBox.setTitleColor(TITLE_COLOR);
  		cacheBox.setForeColor(TEXT_COLOR);
  		cacheBox.draw();
  		
		while (true) {
			si.refresh();
			CacheGFXMenuItem itemChoice = ((CacheGFXMenuItem)cacheBox.getSelection());
			if (itemChoice == null){
				if (minUnits != -1){
					if (ship.getTotalUnits() < minUnits){
						cacheBox.setLegend("At least "+minUnits+" should be transfered");
						continue;
					}
				}
				break;
			}
			Equipment choice = itemChoice.getEquipment();
			ExpeditionItem item = (ExpeditionItem) choice.getItem();
			cacheBox.setLegend("How many "+item.getDescription()+" will you transfer?");
			cacheBox.draw();
			si.refresh();
			int quantity = readQuantity(657, 86, "                       ", 5);
			
			if (quantity == 0)
				continue;
			
			if (quantity > choice.getQuantity()){
				cacheBox.setLegend("Not enough "+choice.getItem().getDescription());
				cacheBox.draw();
				continue;
			}
			
			if (item instanceof Good && !ship.canCarry(item, quantity)){
				cacheBox.setLegend("Not enough room in the "+ship.getDescription());
				cacheBox.draw();
				continue;
			}
			
			getExpedition().reduceQuantityOf(choice.getItem(), quantity);
			
			if (choice.getItem() instanceof ExpeditionUnit && 
					getExpedition().getCurrentlyCarrying()>100){
				cacheBox.setLegend("The expedition can't carry the goods!");
				cacheBox.draw();
				getExpedition().addItem(choice.getItem(), quantity);
				if (choice.getQuantity() == 0){
					menuItems = new Vector();
			  		for (Equipment item2: getExpedition().getInventory()){
			  			menuItems.add(new CacheGFXMenuItem(item2, ship));
			  		}
			  		Collections.sort(menuItems, ITEMS_COMPARATOR);
			  		cacheBox.setMenuItems(menuItems);				
			  	}
				continue;
			}
			
			ship.addItem(choice.getItem(), quantity);
			
			if (choice.getQuantity() == 0){
				menuItems.remove(choice);
			}
			cacheBox.setLegend(choice.getItem().getDescription()+" transfered into the "+ship.getDescription());
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
		si.print(2, 1, gameTime.get(Calendar.YEAR)+", "+ months[gameTime.get(Calendar.MONTH)] +" "+ gameTime.get(Calendar.DATE));
		si.print(2, 2, getExpedition().getExpeditionaryTitle());
		if (getExpedition().getTowns().size() == 1)
			si.print(2, 3, "1 colony ");
		else
			si.print(2, 3, getExpedition().getTowns().size()+" colonies");
		si.print(2, 4, getExpedition().getAccountedGold()+"$");
		
		
		// Box 2
		si.print(2, 5, statsExpedition.getOffshoreFoodDays()+" food days "+TemperatureRules.getTemperatureFoodModifierString(getExpedition().getLocation().getTemperature()));
		if (getExpedition().getLevel() instanceof ExpeditionMicroLevel)
			si.print(2, 6, "Carrying "+statsExpedition.getOffshoreCurrentlyCarrying()+"%");
		else
			si.print(2, 6, "Carrying "+statsExpedition.getCurrentlyCarrying()+"%");		
		
		si.print(2, 7, statsExpedition.getPower()+(statsExpedition.isArmed()?" Power (Armed)":" Power"));
		

		
		int line2 = 63;
		//Box 3
		AbstractCell currentCell = getExpedition().getLocation().getMapCell(getExpedition().getPosition());
		Pair<String, String> locationDescription = getExpedition().getLocation().getLocationDescription();
		Pair<String, String> locationLabels = getExpedition().getLocation().getLocationLabels();
		//Pair<String, String> locationMeans = getExpedition().getLocation().getLocationMeans();
		
		si.print(line2, 1, getExpedition().getLocation().getDescription());
		si.print(line2, 2, currentCell.getDescription());
		si.print(line2, 3, getExpedition().getLocation().getWeather().getDescription());
		si.print(line2, 4, getExpedition().getLocation().getTemperatureDescription());
		si.print(line2, 5, locationLabels.getA(), TITLE_COLOR);
			si.print(line2+9, 5, locationDescription.getA());
		si.print(line2, 6, locationLabels.getB(), TITLE_COLOR);
			si.print(line2+9, 6, locationDescription.getB());
		si.print(line2, 7, "Wind", TITLE_COLOR);
			si.print(line2+9, 7, getExpedition().getLocation().getWindDirection().getAbbreviation());
		si.print(line2, 8, "Heading", TITLE_COLOR);
			si.print(line2+9, 8, getExpedition().getHeading().getAbbreviation());
		if (getExpedition().getMovementMode() == MovementMode.SHIP){
			si.print(line2+2, 9, getExpedition().getSailingPoint().getDescription());
		} else {
			si.print(line2+2, 9, statsExpedition.getMovementMode().getDescription());
		}
		si.print(line2, 10, getExpedition().getMovementSpeed().getDescription());
		int totalShips = statsExpedition.getTotalShips();
		if (totalShips > 0){
			if (totalShips == 1){
				si.print(line2, 11, "A ship ("+statsExpedition.getShipHealth()+"%)");
			} else {
				si.print(line2, 11, totalShips+" ships ("+statsExpedition.getShipHealth()+"%)");
			}
		}
		
		
		expeditionUnitsVector.clear();
		expeditionUnitsVector.addAll(statsExpedition.getUnits());
		
		expeditionUnitItems.clear();
		resumedEquipments.clear();
		for (Equipment expeditionUnit: expeditionUnitsVector){
			String basicId = ((ExpeditionUnit)expeditionUnit.getItem()).getBasicId();
			Equipment resumedEquipment = resumedEquipments.get(basicId) ;
			if (resumedEquipment == null){
				resumedEquipment = new Equipment(expeditionUnit.getItem(), expeditionUnit.getQuantity());
				resumedEquipments.put(basicId, resumedEquipment);
				expeditionUnitItems.add(new SimplifiedUnitGFXMenuItem(resumedEquipment));
			} else {
				resumedEquipment.setQuantity(resumedEquipment.getQuantity()+expeditionUnit.getQuantity());
			}
		}
		
		vehicleUnitItems.clear();
		for (Vehicle expeditionVehicle: statsExpedition.getCurrentVehicles()){
			vehicleUnitItems.add(new VehicleGFXMenuItem(expeditionVehicle));
		}
		
		unitsMenuBox.setMenuItems(expeditionUnitItems);
		Collections.sort(expeditionUnitItems, ITEMS_COMPARATOR);
		unitsMenuBox.draw();
		vehiclesMenuBox.setMenuItems(vehicleUnitItems);
		vehiclesMenuBox.draw();
	}
	private List<GFXMenuItem> expeditionUnitItems = new ArrayList<GFXMenuItem>();
	private List<GFXMenuItem> vehicleUnitItems = new ArrayList<GFXMenuItem>();
	private Map<String,Equipment> resumedEquipments = new HashMap<String, Equipment>();
	
	private Vector<Equipment> expeditionUnitsVector = new Vector<Equipment>();
	//BorderedMenuBox unitsMenuBox;
	private MenuBox unitsMenuBox;
	private MenuBox vehiclesMenuBox;
	
	public void init(SwingSystemInterface psi, String title, UserCommand[] gameCommands, Properties UIProperties, Action target){
		super.init(psi, title, gameCommands, UIProperties, target);
		//unitsMenuBox = new BorderedMenuBox(BORDER1, BORDER2, BORDER3, BORDER4, si, COLOR_WINDOW_BACKGROUND, COLOR_BORDER_IN, COLOR_BORDER_OUT, tileSize, null);
		unitsMenuBox = new MenuBox(si, null);
		unitsMenuBox.setGap(36);
		unitsMenuBox.setPosition(0,7);
		unitsMenuBox.setWidth(17);
		unitsMenuBox.setItemsPerPage(9);
  		unitsMenuBox.setShowOptions(false);
  		
  		vehiclesMenuBox = new MenuBox(si, null);
  		vehiclesMenuBox.setGap(36);
  		vehiclesMenuBox.setPosition(61,11);
  		vehiclesMenuBox.setWidth(17);
  		vehiclesMenuBox.setItemsPerPage(8);
  		vehiclesMenuBox.setShowOptions(false);
  		
		//unitsMenuBox.setTitle("Expedition");
  		try {
			BATTLE_BACKGROUND = ImageUtils.createImage(UIProperties.getProperty("BATTLE_BACKGROUND"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private int readQuantity(int x, int y, String spaces, int inputLength){
		int quantity = -1;
		while (quantity == -1){
			si.print(x,y,spaces);
			si.refresh();
			String strInput = si.input(x,y,TEXT_COLOR,inputLength);
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
	
	@Override
	public void showBattleScene(String battleTitle, List<Equipment> attackingUnits,
			List<Equipment> defendingUnits) {
		si.drawImage(168, 72, BATTLE_BACKGROUND);
		int xBase = 192;
		int yBase = 96; 
		int gridX = 0;
		int gridY = 0;
		for (Equipment equipment: attackingUnits){
			GFXAppearance appearance = (GFXAppearance) equipment.getItem().getAppearance();
			for (int i = 0; i < equipment.getQuantity(); i++){
				si.drawImage(xBase + gridX * 24, yBase + gridY*24, appearance.getImage());
				gridY ++;
				if (gridY > 12){
					gridX++;
					gridY = 0;
				}
			}
		}
		
		gridX = 15;
		gridY = 0;
		for (Equipment equipment: defendingUnits){
			GFXAppearance appearance = (GFXAppearance) equipment.getItem().getAppearance();
			Image img = ImageUtils.vFlip((BufferedImage)appearance.getImage());
			for (int i = 0; i < equipment.getQuantity(); i++){
				si.drawImage(xBase + gridX * 24, yBase + gridY*24, img);
				gridY ++;
				if (gridY > 12){
					gridX--;
					gridY = 0;
				}
			}
		}
		si.refresh();
		si.waitKey(CharKey.SPACE);
	}
	@Override
	public void showBattleResults(
			List<Equipment> originalAttackingUnits, List<Equipment> originalDefendingUnits,
			String battleTitle, AssaultOutcome attackerRangedAttackOutcome,
			AssaultOutcome defenderRangedAttackOutcome,
			AssaultOutcome[] mountedAttackOutcome,
			AssaultOutcome[] meleeAttackOutcome) {
		String message = CommonUI.getBattleResultsString(originalAttackingUnits, originalDefendingUnits, battleTitle,attackerRangedAttackOutcome,defenderRangedAttackOutcome,mountedAttackOutcome,meleeAttackOutcome);
		message = message.replaceAll("XXX", "\n");
		showTextBox(message, 16, 16, 776, 576);
	}
	
	@Override
	/**
	 * Shows the list of units and the message and prompts for confirmation
	 */
	public boolean promptUnitList(List<Equipment> unitList, String title, String prompt) {
		Equipment.eqMode = true;
   		BorderedMenuBox cacheBox = new BorderedMenuBox(BORDER1, BORDER2, BORDER3, BORDER4, si, COLOR_WINDOW_BACKGROUND, COLOR_BORDER_IN, COLOR_BORDER_OUT, tileSize, 6,9,12,tileSize+6, null);
   		cacheBox.setItemsPerPage(12);
   		cacheBox.setBounds(160, 16, 624,480);
  		
  		Vector menuItems = new Vector();
  		for (Equipment item: unitList){
  			menuItems.add(new InventoryGFXMenuItem(item));
  		}
  		Collections.sort(menuItems, ITEMS_COMPARATOR);
  		cacheBox.setMenuItems(menuItems);
  		cacheBox.setTitle(title);
  		cacheBox.setLegend(prompt);
  		cacheBox.setTitleColor(TITLE_COLOR);
  		cacheBox.setForeColor(TEXT_COLOR);
  		cacheBox.draw();
		return prompt();
	}
	
	@Override
	public List<Equipment> selectItemsFromExpedition(String prompt, String verb) {
		Equipment.eqMode = true;
		Map<String, Equipment> selectionMap = new HashMap<String, Equipment>();
		List<Equipment> selection = new ArrayList<Equipment>();
   		BorderedMenuBox menuBox = new BorderedMenuBox(BORDER1, BORDER2, BORDER3, BORDER4, si, COLOR_WINDOW_BACKGROUND, COLOR_BORDER_IN, COLOR_BORDER_OUT, tileSize, 6,9,12,tileSize+6, null);
   		menuBox.setItemsPerPage(12);
  		menuBox.setBounds(160, 16, 624,480);
  		menuBox.setTitle("Expedition Equipment [Space to exit]");
  		menuBox.setLegend(prompt);
  		int typeChoice = 0;
  		while (true){
  			String legend = "";
  			for (int i = 0; i < 4; i++){
  				if (i == typeChoice){
  					legend += ">";
  				}
  				switch (i){
  				case 0:
  					legend += "Units";
  					break;
  				case 1:
  					legend += "Tools";
  					break;
  				case 2:
  					legend += "Goods";
  					break;
  				case 3:
  					legend += "Artifacts";
  					break;
  				}
  				if (i == typeChoice){
  					legend += "<";
  				}
  				legend += "    ";
  			}
  			
  			menuBox.setLegend(legend);
  			
  	  		List<Equipment> inventory = null;
  	  		switch (typeChoice){
  	  		case 0:
  	  			inventory = getExpedition().getUnits(true);
  	  			break;
  	  		case 1:
  	  			inventory = getExpedition().getTools(true);
  	  			break;
  	  		case 2:
  	  			inventory = getExpedition().getGoods(true);
  	  			break;
  	  		case 3:
  	  			inventory = getExpedition().getValuables(true);
  	  		}
  	  		
  	  		Vector menuItems = new Vector();
  	  		for (Equipment item: inventory){
  	  			menuItems.add(new InventoryGFXMenuItem(item));
  	  		}
  	  		Collections.sort(menuItems, ITEMS_COMPARATOR);
  	  		menuBox.setMenuItems(menuItems);
  	  		menuBox.draw();
  	  		
	  		CharKey x = new CharKey(CharKey.NONE);
			while (x.code == CharKey.NONE)
				x = si.inkey();
			
			if (x.isLeftArrow()){
				typeChoice--;
				if (typeChoice == -1)
					typeChoice = 0;
				continue;
			}
			if (x.isRightArrow()){
				typeChoice++;
				if (typeChoice == 4)
					typeChoice = 3;
				continue;
			}
			
			InventoryGFXMenuItem itemChoice = ((InventoryGFXMenuItem)menuBox.getSelection(x));

			if (itemChoice == null){
				if (x.code != CharKey.SPACE){
					continue;
				}
				break;
			}
			Equipment choice = itemChoice.getEquipment();
			ExpeditionItem item = (ExpeditionItem) choice.getItem();
			menuBox.setLegend("How many "+item.getDescription()+" will you "+verb+"?");
			menuBox.draw();
			si.refresh();
			int quantity = readQuantity(657, 86, "                       ", 5);
			
			if (quantity == 0)
				continue;
			
			if (quantity > choice.getQuantity()){
				menuBox.setLegend("Not enough "+choice.getItem().getDescription()+" [Press Space]");
				menuBox.draw();
				si.waitKey(CharKey.SPACE);
				continue;
			}
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
			menuBox.setLegend("You "+verb+" "+quantity+" "+choice.getItem().getDescription()+" [Press Space]");
			menuBox.draw();
			si.waitKey(CharKey.SPACE);
			refresh();
  		}
  		
		Equipment.eqMode = false;
		si.restore();
 		si.refresh();
 		return selection;
	}
}
