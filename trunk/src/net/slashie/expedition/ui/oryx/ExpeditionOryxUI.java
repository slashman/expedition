package net.slashie.expedition.ui.oryx;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.ImageIcon;

import net.slashie.expedition.domain.AssaultOutcome;
import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.ExpeditionItem;
import net.slashie.expedition.domain.ExpeditionUnit;
import net.slashie.expedition.domain.GoodType;
import net.slashie.expedition.domain.GoodsCache;
import net.slashie.expedition.domain.ItemContainer;
import net.slashie.expedition.domain.ShipCache;
import net.slashie.expedition.domain.Store;
import net.slashie.expedition.domain.StoreItemInfo;
import net.slashie.expedition.domain.Town;
import net.slashie.expedition.domain.UnitContainer;
import net.slashie.expedition.domain.Vehicle;
import net.slashie.expedition.domain.Expedition.MovementMode;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.item.ItemFactory;
import net.slashie.expedition.level.ExpeditionLevelReader;
import net.slashie.expedition.town.Building;
import net.slashie.expedition.ui.CommonUI;
import net.slashie.expedition.ui.ExpeditionUserInterface;
import net.slashie.expedition.world.ExpeditionLevel;
import net.slashie.expedition.world.ExpeditionMicroLevel;
import net.slashie.expedition.world.TemperatureRules;
import net.slashie.libjcsi.CharKey;
import net.slashie.serf.action.Action;
import net.slashie.serf.action.Actor;
import net.slashie.serf.baseDomain.AbstractItem;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.game.Player;
import net.slashie.serf.level.AbstractCell;
import net.slashie.serf.sound.STMusicManagerNew;
import net.slashie.serf.ui.UserCommand;
import net.slashie.serf.ui.oryxUI.AddornedBorderPanel;
import net.slashie.serf.ui.oryxUI.GFXAppearance;
import net.slashie.serf.ui.oryxUI.GFXUISelector;
import net.slashie.serf.ui.oryxUI.GFXUserInterface;
import net.slashie.serf.ui.oryxUI.SwingSystemInterface;
import net.slashie.util.Pair;
import net.slashie.utils.ImageUtils;
import net.slashie.utils.PropertyFilters;
import net.slashie.utils.swing.BorderedMenuBox;
import net.slashie.utils.swing.CallbackActionListener;
import net.slashie.utils.swing.CallbackKeyListener;
import net.slashie.utils.swing.CleanButton;
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
	private Image BTN_SPLIT_UP;
	private Image BTN_SPLIT_DOWN;
	private Image BTN_BUY;
	private Image BTN_TRANSFER;
	private BufferedImage IMG_BOX;
	
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
		((GFXUISelector)getPlayer().getSelector()).setMouseMovementActive(false);
		// Create the good type buttons
		CleanButton peopleButton = new CleanButton(new ImageIcon(UIProperties.getProperty("BTN_PEOPLE")));
		peopleButton.setBounds(540,41, 24,24);	
		CleanButton suppliesButton = new CleanButton(new ImageIcon(UIProperties.getProperty("BTN_SUPPLIES")));
		suppliesButton.setBounds(569,41, 24,24);	
		CleanButton tradeGoodsButton = new CleanButton(new ImageIcon(UIProperties.getProperty("BTN_MERCHANDISE")));
		tradeGoodsButton.setBounds(598,41, 24,24);	
		CleanButton armoryButton = new CleanButton(new ImageIcon(UIProperties.getProperty("BTN_WEAPONS")));
		armoryButton.setBounds(627,41, 24,24);	
		CleanButton livestockButton = new CleanButton(new ImageIcon(UIProperties.getProperty("BTN_LIVESTOCK")));
		livestockButton.setBounds(656,41, 24,24);	
		CleanButton closeButton = new CleanButton(new ImageIcon(UIProperties.getProperty("BTN_CLOSE")));
		closeButton.setBounds(730,41, 24,24);
		
		
		si.add(peopleButton);
		si.add(suppliesButton);
		si.add(tradeGoodsButton);
		si.add(armoryButton);
		si.add(livestockButton);
		si.add(closeButton);

		
		BlockingQueue<String> inventorySelectionQueue = new LinkedBlockingQueue<String>(1);
		
		peopleButton.addActionListener(getStringCallBackActionListener(inventorySelectionQueue, "0"));
		suppliesButton.addActionListener(getStringCallBackActionListener(inventorySelectionQueue, "1"));
		tradeGoodsButton.addActionListener(getStringCallBackActionListener(inventorySelectionQueue, "2"));
		armoryButton.addActionListener(getStringCallBackActionListener(inventorySelectionQueue, "3"));
		livestockButton.addActionListener(getStringCallBackActionListener(inventorySelectionQueue, "4"));
		closeButton.addActionListener(getStringCallBackActionListener(inventorySelectionQueue, "BREAK"));
		
		
		CallbackKeyListener<String> cbkl = new CallbackKeyListener<String>(inventorySelectionQueue){
			@Override
			public void keyPressed(KeyEvent e) {
				try {
					CharKey x = new CharKey(SwingSystemInterface.charCode(e));
					if (x.code == CharKey.SPACE || x.code == CharKey.ESC){
						handler.put("BREAK");
					} else if (x.isLeftArrow()){
						handler.put("<");
					} else if (x.isRightArrow()){
						handler.put(">");
					} else if (x.code == CharKey.DARROW || x.code == CharKey.N2){
						handler.put("DOWN");
					} else if (x.code == CharKey.UARROW || x.code == CharKey.N8){
						handler.put("UP");
					}

				} catch (InterruptedException e1) {}
			}
		}; 
		
		
		si.addKeyListener(cbkl);
		
		InventoryBorderGridBox menuBox = new InventoryBorderGridBox(BORDER1, BORDER2, BORDER3, BORDER4, si, COLOR_WINDOW_BACKGROUND, COLOR_BORDER_IN, COLOR_BORDER_OUT, tileSize, 6,9,12,62, 202, 3, 6, null);
  		menuBox.setBounds(16, 16, 768,480);
  		menuBox.setTitle("Examine Expedition Inventory");
  		si.saveBuffer();
  		int typeChoice = 0;
  		while (true){
  			GoodType[] goodTypes = GoodType.getGoodTypes();
  			menuBox.setLegend(" - ");
  			
  			List<Equipment> inventory = null;
  	  		if (typeChoice < goodTypes.length){
  	  			inventory = getExpedition().getGoods(goodTypes[typeChoice]);
  	  		}
  	  		
  	  		List<InventoryCustomGFXMenuItem> invMenuItems = new Vector<InventoryCustomGFXMenuItem> ();
	  		for (Equipment item: inventory){
	  			invMenuItems.add(new InventoryCustomGFXMenuItem(item));
	  		}
	  		Collections.sort(invMenuItems, inventoryCustomItemsComparator);
  	  		
  	  		List<GFXMenuItem> menuItems = new Vector<GFXMenuItem> ();
  	  		for (InventoryCustomGFXMenuItem menuItem: invMenuItems){
  	  			menuItems.add(menuItem);
  	  		}
  	  		int currentPage = menuBox.getCurrentPage();
  	  		menuBox.setMenuItems(menuItems);
  	  		if (menuBox.isValidPage(currentPage)){
	  			menuBox.setCurrentPage(currentPage);
	  		} else {
	  			currentPage = menuBox.getPages() - 1;
	  			menuBox.setCurrentPage(currentPage);
	  		}
  	  		si.restore();
  	  		int boxX = 540 + typeChoice * 29 - 24;
			int boxY = 41 - 24;
  	  		menuBox.draw(boxX, boxY, IMG_BOX);
  	  		
  	  		String command = null;
  	  		while (command == null){
				try {
					command = inventorySelectionQueue.take();
				} catch (InterruptedException e1) {
				}
	  		}
			
  	  		if (command.equals("BREAK")){
  	  			break;
  	  		} else if (command.equals("<")) {
	  	  		typeChoice--;
				if (typeChoice == -1)
					typeChoice = 0;
			} else if (command.equals(">")){
		  	  	typeChoice++;
				if (typeChoice == goodTypes.length)
					typeChoice = goodTypes.length-1;
			} else if (command.equals("DOWN")){
				menuBox.avPag();
			} else if (command.equals("UP")){
				menuBox.rePag();
			} else {
				typeChoice = Integer.parseInt(command);
			}
  		}
  		menuBox.kill();
  		si.remove(peopleButton);
		si.remove(suppliesButton);
		si.remove(tradeGoodsButton);
		si.remove(armoryButton);
		si.remove(livestockButton);
		si.remove(closeButton);
		si.removeKeyListener(cbkl);
  		si.restore();
 		si.refresh();
 		//((GFXUISelector)getPlayer().getSelector()).setMouseMovementActive(false);
 		Equipment.eqMode = false;
	}

	private CallbackActionListener<String> getStringCallBackActionListener (BlockingQueue<String> queue, final String option) {
		return new CallbackActionListener<String>(queue) {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					handler.put(option);
				} catch (InterruptedException e1) {}
				si.recoverFocus();
			}
		};
	}

	private Comparator<InventoryCustomGFXMenuItem> inventoryCustomItemsComparator = new Comparator<InventoryCustomGFXMenuItem>(){
		public int compare(InventoryCustomGFXMenuItem o1, InventoryCustomGFXMenuItem o2) {
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
	
	private SimplifiedUnitGFXMenuItem mainUnitMenuItem;
	private AbstractItem HORSES_ITEM;
	
	@Override
	public String inputBox(String prompt) {
		return inputBox(prompt, 200, 40, 400, 200, 260, 120, 20);
	}
	
	@Override
	public int switchChat(String title, String prompt, String... options) {
		((GFXUISelector)getPlayer().getSelector()).setMouseMovementActive(false);
		return super.switchChat(title, prompt, TITLE_COLOR, TEXT_COLOR, options);
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
		if (promptChat("Do you want to leave the ports of Palos de la Frontera?")){
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
		((GFXUISelector)getPlayer().getSelector()).setMouseMovementActive(false);
		
		CleanButton closeButton = new CleanButton(new ImageIcon(UIProperties.getProperty("BTN_CLOSE")));
		closeButton.setBounds(730,41, 24,24);
		
		CleanButton buyButton = new CleanButton(new ImageIcon(BTN_BUY));
		buyButton.setSize(96,48);
		BlockingQueue<Integer> buyButtonSelectionHandler = new LinkedBlockingQueue<Integer>();

   		StoreBorderGridBox menuBox = new StoreBorderGridBox(BORDER1, BORDER2, BORDER3, BORDER4, si, COLOR_WINDOW_BACKGROUND, COLOR_BORDER_IN, COLOR_BORDER_OUT, tileSize, 6,9,12,62,202,2,5,null,
   				store, getExpedition(), buyButton, buyButtonSelectionHandler, BTN_SPLIT_UP, BTN_SPLIT_DOWN, closeButton);

  		menuBox.setBounds(16, 16, 768,480);
  		
  		List<StoreCustomGFXMenuItem> invMenuItems = new Vector<StoreCustomGFXMenuItem> ();
  		for (Equipment item: merchandise){
  			invMenuItems.add(new StoreCustomGFXMenuItem(item, store));
  		}
  		//Collections.sort(invMenuItems, inventoryCustomItemsComparator);
  		
  		menuBox.setMenuItems(invMenuItems);
  		menuBox.draw(null);
  		menuBox.setTitleColor(TITLE_COLOR);
  		menuBox.setForeColor(TEXT_COLOR);
  		menuBox.setTitle(store.getOwnerName());
  		//menuBox.setBorder(true);
  		String prompt = "Welcome to the "+store.getOwnerName();
  		StoreCustomGFXMenuItem itemChoice = null;
  		boolean keepItemChoice = false;
		while (true) {
			/*menuBox.draw(null);*/
			menuBox.setLegend(prompt);
			//si.refresh();
			menuBox.buyButtonEnabled = true;

			if (!keepItemChoice)
				itemChoice = ((StoreCustomGFXMenuItem)menuBox.getSelection());
			

			if (itemChoice == null)
				break;
			
			// Pick quantity
			Equipment choice = itemChoice.getEquipment();
			ExpeditionItem item = (ExpeditionItem) choice.getItem();
			StoreItemInfo storeItemInfo = store.getPriceFor(item);
			
			if(!keepItemChoice){
				if (storeItemInfo.getPack() > 1)
					menuBox.setLegend("How many "+storeItemInfo.getPackDescription()+" of "+item.getPluralDescription()+"?");
				else
					menuBox.setLegend("How many "+item.getPluralDescription()+"?");
			}
			keepItemChoice = false;
			
			menuBox.draw(choice);
			menuBox.activateItemPseudoSelection(buyButtonSelectionHandler);
			int buyQuantity = 0;
			while (buyQuantity == 0){
				try {
					buyQuantity = buyButtonSelectionHandler.take();
				} catch (InterruptedException e) {}
			}
			menuBox.quantityObtained();
			
			if (buyQuantity == -1){
				//prompt = "Ok... Do you need anything else?";
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
			String question = "";
			if (item instanceof ExpeditionUnit){
				if (quantity > 1)
					question = "Hire "+quantity+" "+item.getPluralDescription()+" for "+gold+" maravedíes?";
				else
					question = "Hire a "+item.getDescription()+" for "+gold+" maravedíes?";
			} else {
				if (quantity > 1)
					question = "Buy "+quantity+" "+item.getPluralDescription()+" for "+gold+" maravedíes?";
				else
					question = "Buy a "+item.getDescription()+" for "+gold+" maravedíes?";
			}
			
	 		if (promptChat(question))
	 			if (getExpedition().getAccountedGold() >= gold) {
	 				getExpedition().reduceAccountedGold(gold);
	 				getExpedition().addItemOffshore((ExpeditionItem) choice.getItem(), quantity);
	 				choice.reduceQuantity(buyQuantity);
	 				prompt = "Thank you! Do you need anything else?";
			 	} else {
			 		prompt = "You can't afford it! Do you need anything else?";
			 	}
			else {
				prompt = "Ok, do you need anything else?";
			}
	 		keepItemChoice = true;
		}
		menuBox.kill();
		Equipment.eqMode = false;

	}

	public void showBlockingMessage(String message) {
		message = message.replaceAll("XXX", "\n");
		showTextBox(message, 140, 300, 520, 250);
	}
	
	@Override
	public boolean promptChat(String message) {
		((GFXUISelector)getPlayer().getSelector()).setMouseMovementActive(false);
		message = message.replaceAll("XXX", "\n");
		return promptChat(message, 140,288,520,200);
	}

	interface ItemTransferFunctionality {
		String getTitle(ItemContainer from, ItemContainer to);
		boolean validateBreak(ItemContainer from, ItemContainer to);
		boolean validateAndPerformTransfer(ItemContainer from, ItemContainer to, Equipment choice, int quantity);
	}
	
	class TransferFromCacheFunctionality implements ItemTransferFunctionality {
		@Override
		public String getTitle(ItemContainer from, ItemContainer to) {
			return "Transfer from "+from.getDescription();
		}
		
		@Override
		public boolean validateBreak(ItemContainer from, ItemContainer to) {
			if (from instanceof ShipCache){
				if (to.getTotalUnits() > 0)
					return true;
				else {
					showBlockingMessage("You must first disembark.");
		  	  		return false;
				}
			} else {
				return true;
			}
		}
		
		@Override
		public boolean validateAndPerformTransfer(ItemContainer from, ItemContainer to, Equipment choice, int quantity) {
  			ExpeditionItem item = (ExpeditionItem) choice.getItem();
			if (!(choice.getItem() instanceof ExpeditionUnit) && to.getTotalUnits() == 0){
				showBlockingMessage("Someone must receive the goods!");
				return false;
			}
			
			if (quantity > choice.getQuantity()){
				showBlockingMessage("Not enough "+choice.getItem().getDescription());
				return false;
			}
			
			if (item.getGoodType() != GoodType.PEOPLE && !to.canCarry(item, quantity)){
				showBlockingMessage("Your expedition is full!");
				return false;
			}
			from.reduceQuantityOf(choice.getItem(), quantity);
			to.addItem((ExpeditionItem)choice.getItem(), quantity);
			if (choice.getQuantity() == 0){
				from.getItems().remove(choice);
			}
			return true;
		}
	}
	
	class TransferFromExpeditionFunctionality implements ItemTransferFunctionality {
		int minUnits;
		TransferFromExpeditionFunctionality (int minUnits){
			this.minUnits = minUnits;
		}
		
		@Override
		public String getTitle(ItemContainer from, ItemContainer to) {
			return "Transfer to "+to.getDescription();
		}
		
		@Override
		public boolean validateBreak(ItemContainer from, ItemContainer to) {
			if (minUnits != -1){
				if (to.getTotalUnits() < minUnits){
					showBlockingMessage("At least "+minUnits+" should be transfered.");
					return false;
				}
			}
			return true;
		}
		
		@Override
		public boolean validateAndPerformTransfer(ItemContainer from, ItemContainer to, Equipment choice, int quantity) {
  			ExpeditionItem item = (ExpeditionItem) choice.getItem();
			if (!to.canCarry(item, quantity)){
				showBlockingMessage("Not enough room in the "+to.getDescription());
				return false;
			}
			
			from.reduceQuantityOf(choice.getItem(), quantity);
		
			if (choice.getItem() instanceof ExpeditionUnit && from.getCurrentlyCarrying()>100){
				from.addItem((ExpeditionItem) choice.getItem(), quantity);
				showBlockingMessage("The expedition can't carry the goods! Be sure to leave enough men on the expedition.");
				return false;
			}
		
			to.addItem((ExpeditionItem)choice.getItem(), quantity);
			return true;
		}
	}
	
	public void transferFromExpedition(GoodsCache ship) {
		transferFromExpedition(ship, -1);
	}
	
	public void transferFromExpedition(GoodsCache goodsCache, int minUnits) {
		ItemTransferFunctionality transferFromExpeditionFunctionality = new TransferFromExpeditionFunctionality(minUnits);
		transferItems(getExpedition(), goodsCache, transferFromExpeditionFunctionality, true);
	}
	
	public void transferFromCache(GoodsCache goodsCache) {
		ItemTransferFunctionality transferFromCacheFunctionality = new TransferFromCacheFunctionality();
		transferItems(goodsCache, getExpedition(), transferFromCacheFunctionality, false);
		if (goodsCache.destroyOnEmpty() && goodsCache.getItems().size() == 0)
			level.destroyFeature(goodsCache);
	}

	public void transferItems(ItemContainer from, ItemContainer to, ItemTransferFunctionality itemTransferFunctionality, boolean fromExpedition) {
		// Change UI Mode
   		Equipment.eqMode = true;
		((GFXUISelector)getPlayer().getSelector()).setMouseMovementActive(false);
   		clearTextBox();
   		
   		// Create the close button and add it to the UI
   		CleanButton closeButton = new CleanButton(new ImageIcon(UIProperties.getProperty("BTN_CLOSE")));
		closeButton.setBounds(730,41, 24,24);
		
   		// Create the buttons for good type selection and add them to the UI
   		CleanButton peopleButton = new CleanButton(new ImageIcon(UIProperties.getProperty("BTN_PEOPLE")));
		peopleButton.setBounds(540,41, 24,24);	
		CleanButton suppliesButton = new CleanButton(new ImageIcon(UIProperties.getProperty("BTN_SUPPLIES")));
		suppliesButton.setBounds(569,41, 24,24);	
		CleanButton tradeGoodsButton = new CleanButton(new ImageIcon(UIProperties.getProperty("BTN_MERCHANDISE")));
		tradeGoodsButton.setBounds(598,41, 24,24);	
		CleanButton armoryButton = new CleanButton(new ImageIcon(UIProperties.getProperty("BTN_WEAPONS")));
		armoryButton.setBounds(627,41, 24,24);	
		CleanButton livestockButton = new CleanButton(new ImageIcon(UIProperties.getProperty("BTN_LIVESTOCK")));
		livestockButton.setBounds(656,41, 24,24);
		si.add(peopleButton);
		si.add(suppliesButton);
		si.add(tradeGoodsButton);
		si.add(armoryButton);
		si.add(livestockButton);
		si.add(closeButton);

		
		// Create the button to confirm transfer and add it to the UI
		CleanButton transferButton = new CleanButton(new ImageIcon(BTN_TRANSFER));
		transferButton.setSize(BTN_TRANSFER.getWidth(null),BTN_TRANSFER.getHeight(null));
		
		// Create the blockingqueue
		BlockingQueue<String> transferFromExpeditionHandler = new LinkedBlockingQueue<String>(1);
		
		// Add callback listeners for good type selection
		peopleButton.addActionListener(getStringCallBackActionListener(transferFromExpeditionHandler, "GOOD_TYPE:0"));
		suppliesButton.addActionListener(getStringCallBackActionListener(transferFromExpeditionHandler, "GOOD_TYPE:1"));
		tradeGoodsButton.addActionListener(getStringCallBackActionListener(transferFromExpeditionHandler, "GOOD_TYPE:2"));
		armoryButton.addActionListener(getStringCallBackActionListener(transferFromExpeditionHandler, "GOOD_TYPE:3"));
		livestockButton.addActionListener(getStringCallBackActionListener(transferFromExpeditionHandler, "GOOD_TYPE:4"));
		
		// Add callback listeners for screen close
		closeButton.addActionListener(getStringCallBackActionListener(transferFromExpeditionHandler, "BREAK"));
		
		// Add a general callbacklistener for keyboard
		CallbackKeyListener<String> cbkl = new CallbackKeyListener<String>(transferFromExpeditionHandler){
			@Override
			public void keyPressed(KeyEvent e) {
				try {
					CharKey x = new CharKey(SwingSystemInterface.charCode(e));
					if (x.code == CharKey.SPACE || x.code == CharKey.ESC){
						handler.put("BREAK");
					} else if (x.isLeftArrow()){
						handler.put("GOOD_TYPE:<");
					} else if (x.isRightArrow()){
						handler.put("GOOD_TYPE:>");
					}
				} catch (InterruptedException e1) {}
			}
		};
		si.addKeyListener(cbkl);
		
		// Create the gridbox component. Send the transferFromExpeditionHandler to allow item selection with both mouse and keyb
   		TransferBorderGridBox menuBox = new TransferBorderGridBox(
   				BORDER1, BORDER2, BORDER3, BORDER4, si, COLOR_WINDOW_BACKGROUND, COLOR_BORDER_IN, COLOR_BORDER_OUT, tileSize, 6,9,12,
   				62,202,2,6, IMG_BOX, null, transferButton,
   				from, to, 
   				transferFromExpeditionHandler, BTN_SPLIT_UP, BTN_SPLIT_DOWN);
  		menuBox.setBounds(16, 16, 768,480);
  		
  		
  		menuBox.setTitle(itemTransferFunctionality.getTitle(from, to));
  		menuBox.setLegend("Select the units or goods to transfer");
  		
  		int typeChoice = 0;
  		int selectedIndex;
  		GoodType[] goodTypes = GoodType.getGoodTypes();
  		while (true){
  			menuBox.setHoverDisabled(false);
  			// Select data to draw and draw it
  			int currentPage = menuBox.getCurrentPage();
  			List<Equipment> inventory = null;
  			if (typeChoice < goodTypes.length){
  	  			inventory = from.getGoods(goodTypes[typeChoice]);
  	  		}
  	  		
  	  		Vector<CacheCustomGFXMenuItem> menuItems = new Vector<CacheCustomGFXMenuItem>();
  	  		for (Equipment item: inventory){
  	  			menuItems.add(new CacheCustomGFXMenuItem(item, from, to));
  	  		}
  	  		Collections.sort(menuItems, ITEMS_COMPARATOR);
  	  		menuBox.setMenuItems(menuItems);
  	  		if (menuBox.isValidPage(currentPage)){
  	  			menuBox.setCurrentPage(currentPage);
  	  		} else {
  	  			currentPage = menuBox.getPages() - 1;
  	  			menuBox.setCurrentPage(currentPage);
  	  		}
  	  		
  	  		int boxX = 540 + typeChoice * 29 - 24;
  	  		menuBox.draw(true, boxX);
  	  		
  	  		// Wait for item or command selection
  	  		String command = null;
  	  		while (command == null){
  	  			try {
  	  				command = transferFromExpeditionHandler.take();
  	  			} catch (InterruptedException ie){}
  	  		}
  	  		menuBox.setHoverDisabled(true);
  	  		
  	  		String[] commandParts = command.split(":");
  	  		if (commandParts[0].equals("GOOD_TYPE")){
  	  			int currentType = typeChoice;
  	  			// Change the good type
  	  			if (commandParts[1].equals("<")){
	  	  			typeChoice--;
					if (typeChoice == -1)
						typeChoice = 0;
					if (currentType != typeChoice) menuBox.resetSelection();
					continue;
  	  			} else if (commandParts[1].equals(">")){
	  	  			typeChoice++;
					if (typeChoice == goodTypes.length)
						typeChoice = goodTypes.length-1;
					if (currentType != typeChoice) menuBox.resetSelection();
					continue;
  	  			} else {
  	  				typeChoice = Integer.parseInt(commandParts[1]);
  	  			if (currentType != typeChoice) menuBox.resetSelection();
  	  				continue;
  	  			}
  	  		} else if (commandParts[0].equals("BREAK")){
  	  			if (!itemTransferFunctionality.validateBreak(from, to))
	  	  			continue;
				break;
  	  		} else if (commandParts[0].equals("SELECT_UNIT")){
  	  			selectedIndex = Integer.parseInt(commandParts[1]);
  	  			menuBox.selectUnit(selectedIndex);
  	  		} else if (commandParts[0].equals("CONFIRM_TRANSFER")){
  	  			Equipment choice = menuBox.getSelectedUnit();
  	  			int quantity = menuBox.getQuantity();
  	  			if (quantity == 0)
  	  				continue;
  	  			
				if (quantity > choice.getQuantity()){
					showBlockingMessage("Not enough "+choice.getItem().getDescription());
					continue;
				}
				
				if (!itemTransferFunctionality.validateAndPerformTransfer(from, to, choice, quantity)){
					continue;
				}
				
				if (choice.getQuantity() == 0){
					menuItems.remove(choice);
				}
				menuBox.resetSelection();
				menuBox.setLegend(quantity+" " +choice.getItem().getDescription()+" transfered into "+to.getDescription());
  	  		} else if (commandParts[0].equals("CHANGE_PAGE")){
  	  			// Do nothing other than changing page and redrawing
  	  		}
  		}
  		
		si.remove(peopleButton);
		si.remove(suppliesButton);
		si.remove(tradeGoodsButton);
		si.remove(armoryButton);
		si.remove(livestockButton);
		si.remove(closeButton);
  		si.removeKeyListener(cbkl);
		menuBox.kill();
		Equipment.eqMode = false;
		si.restore();
 		si.refresh();
	}
	
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
	
	@Override
	public void setPlayer(Player player) {
		super.setPlayer(player);
		mainUnitMenuItem = new SimplifiedUnitGFXMenuItem(new Equipment(getExpedition().getLeaderUnit(), 1));
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
		si.print(2, 5, statsExpedition.getOffshoreFoodDays()+" food days "+
				TemperatureRules.getTemperatureFoodModifierString(getExpedition().getLocation().getTemperature())
				+ (statsExpedition.isForaging() ? " (foraging)" : "")
		);
		if (getExpedition().getLevel() instanceof ExpeditionMicroLevel)
			si.print(2, 6, "Carrying "+statsExpedition.getOffshoreCurrentlyCarrying()+"%");
		else
			si.print(2, 6, "Carrying "+statsExpedition.getCurrentlyCarrying()+"%");		
		
		si.print(2, 7, statsExpedition.getMoraleDescription()+(statsExpedition.isArmed()?"(Armed)":""));
		si.print(20, 1, "Scale "+ExpeditionLevelReader.getLongitudeScale(statsExpedition.getPosition().y));


		
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
		expeditionUnitsVector.addAll(statsExpedition.getGoods(GoodType.PEOPLE));
		int horses = statsExpedition.getItemCountBasic("HORSE");
		if (horses > 0){
			Equipment forgedEquipment = new Equipment(HORSES_ITEM, horses);
			expeditionUnitsVector.add(forgedEquipment);
		}
		
		expeditionUnitItems.clear();
		resumedEquipments.clear();
		for (Equipment expeditionUnit: expeditionUnitsVector){
			String basicId = ((ExpeditionItem)expeditionUnit.getItem()).getBaseID();
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
		expeditionUnitItems.add(0, mainUnitMenuItem);
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
	private Properties UIProperties;
	
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
			BTN_SPLIT_UP = PropertyFilters.getImage(UIProperties.getProperty("IMG_UI"), UIProperties.getProperty("BTN_SPLIT_UP_BOUNDS"));
			BTN_SPLIT_DOWN = PropertyFilters.getImage(UIProperties.getProperty("IMG_UI"), UIProperties.getProperty("BTN_SPLIT_DOWN_BOUNDS"));
			BTN_BUY = PropertyFilters.getImage(UIProperties.getProperty("IMG_UI"), UIProperties.getProperty("BTN_BUY_BOUNDS"));
			BTN_TRANSFER = PropertyFilters.getImage(UIProperties.getProperty("IMG_UI"), UIProperties.getProperty("BTN_TRANSFER_BOUNDS"));
			IMG_BOX = ImageUtils.createImage(UIProperties.getProperty("IMG_BOX"));
		} catch (IOException e) {
			e.printStackTrace();
			ExpeditionGame.crash("Error loading images", e);
		}
		
		HORSES_ITEM = ItemFactory.createItem("HORSE");
		this.UIProperties = UIProperties;
		
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
		clearTextBox();
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
			AssaultOutcome[] meleeAttackOutcome, int attackerScore, int defenderScore) {
		String message = CommonUI.getBattleResultsString(originalAttackingUnits, originalDefendingUnits, battleTitle,attackerRangedAttackOutcome,defenderRangedAttackOutcome,mountedAttackOutcome,meleeAttackOutcome, attackerScore, defenderScore);
		message = message.replaceAll("XXX", "\n");
		showTextBox(message, 16, 16, 776, 576);
	}
	
	@Override
	/**
	 * Shows the list of units and the message and prompts for confirmation
	 */
	public boolean promptUnitList(List<Equipment> unitList, String title, String prompt) {
		Equipment.eqMode = true;
		clearTextBox();

   		BorderedMenuBox cacheBox = new BorderedMenuBox(BORDER1, BORDER2, BORDER3, BORDER4, si, COLOR_WINDOW_BACKGROUND, COLOR_BORDER_IN, COLOR_BORDER_OUT, tileSize, 6,9,12,tileSize+10, null);
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
		clearTextBox();
		Map<GoodType, List<Equipment>> expeditionGoodsMap = new HashMap<GoodType, List<Equipment>>();
		GoodType[] goodTypes = GoodType.getGoodTypes();
		for (GoodType goodType: goodTypes){
			expeditionGoodsMap.put(goodType, getExpedition().getGoods(goodType, true));
		}
			
   		BorderedMenuBox menuBox = new BorderedMenuBox(BORDER1, BORDER2, BORDER3, BORDER4, si, COLOR_WINDOW_BACKGROUND, COLOR_BORDER_IN, COLOR_BORDER_OUT, tileSize, 6,9,12,tileSize+10, null);
   		menuBox.setItemsPerPage(12);
  		menuBox.setBounds(16, 16, 768,480);
  		menuBox.setTitle(prompt+" [Space to exit]");
  		menuBox.setLegend(prompt);
  		int typeChoice = 0;
  		while (true){
  			String legend = "";
  			for (int i = 0; i < goodTypes.length; i++){
  				if (i == typeChoice){
  					legend += ">";
  				}
  				legend += goodTypes[i].getDescription();
  				if (i == typeChoice){
  					legend += "<";
  				}
  				legend += "    ";
  			}
  			menuBox.setLegend(legend);
  			List<Equipment> inventory = null;
  	  		if (typeChoice < goodTypes.length){
  	  			inventory = expeditionGoodsMap.get(goodTypes[typeChoice]);
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

	@Override
	public List<Building> createBuildingPlan() {
		List<Building> knownBuildings = getExpedition().getKnownBuildings();
		List<BuildingGFXMenuItem> buildingMenuItems = new ArrayList<BuildingGFXMenuItem>();
		for (Building building: knownBuildings){
			buildingMenuItems.add(new BuildingGFXMenuItem(building));
		}
		BorderedMenuBox menuBox = new BorderedMenuBox(BORDER1, BORDER2, BORDER3, BORDER4, si, COLOR_WINDOW_BACKGROUND, COLOR_BORDER_IN, COLOR_BORDER_OUT, tileSize, 6,9,12,tileSize+10, null);
   		menuBox.setItemsPerPage(12);
  		menuBox.setBounds(16, 16, 768,480);
  		menuBox.setTitle("Building Plan [Space to exit]");
  		menuBox.setLegend("");
  		menuBox.setMenuItems(buildingMenuItems);
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
  			menuBox.setLegend(legend);
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
				if (typeChoice == choices.length)
					typeChoice = choices.length-1;
				continue;
			}
			
			BuildingGFXMenuItem buildingChoice = ((BuildingGFXMenuItem)menuBox.getSelection(x));

			if (buildingChoice == null){
				if (x.code != CharKey.SPACE){
					continue;
				}
				break;
			}
			
			if (typeChoice == 0){
				buildingChoice.add();
			} else {
				buildingChoice.remove();
			}
			
			menuBox.draw();
			//refresh();
  		}
  		List<Building> buildingPlan = new ArrayList<Building>();
  		for (BuildingGFXMenuItem buildingMenuItem: buildingMenuItems){
  			for (int i = 0; i < buildingMenuItem.getQuantity(); i++){
  				buildingPlan.add(buildingMenuItem.getBuilding());
  			}
		}
  		
  		si.restore();
 		si.refresh();
 		return buildingPlan;
	}
	
	@Override
	public void showCityInfo(Town town) {
		String townInfo = CommonUI.getTownDescription(town);
		townInfo = townInfo.replaceAll("XXX", "\n");
   		printTextBox(townInfo, 80, 20, 600, 200);
	}
	
	@Override
	public void afterTownAction() {
		clearTextBox();
	}
	
	@Override
	public int getXScale() {
		if (getExpedition().getLevel() instanceof ExpeditionLevelReader)
			return ExpeditionLevelReader.getLongitudeScale(getExpedition().getLatitude());
		else
			return 1;
	}
	
	@Override
	public int getYScale() {
		if (getExpedition().getLevel() instanceof ExpeditionLevelReader)
			return -ExpeditionLevelReader.getLongitudeScale(getExpedition().getLatitude());
		else
			return 1;
	}
}
