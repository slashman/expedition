package net.slashie.expedition.ui.oryx;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.FontFormatException;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
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
import net.slashie.expedition.domain.Vehicle;
import net.slashie.expedition.domain.Expedition.MovementMode;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.item.ItemFactory;
import net.slashie.expedition.item.Mount;
import net.slashie.expedition.level.ExpeditionLevelReader;
import net.slashie.expedition.level.GlobeMapModel;
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
import net.slashie.serf.ui.CommandListener;
import net.slashie.serf.ui.UserCommand;
import net.slashie.serf.ui.oryxUI.AddornedBorderPanel;
import net.slashie.serf.ui.oryxUI.GFXAppearance;
import net.slashie.serf.ui.oryxUI.GFXUISelector;
import net.slashie.serf.ui.oryxUI.GFXUserInterface;
import net.slashie.serf.ui.oryxUI.SwingSystemInterface;
import net.slashie.util.Pair;
import net.slashie.utils.ImageUtils;
import net.slashie.utils.Position;
import net.slashie.utils.PropertyFilters;
import net.slashie.utils.swing.BorderedGridBox;
import net.slashie.utils.swing.BorderedMenuBox;
import net.slashie.utils.swing.CallbackActionListener;
import net.slashie.utils.swing.CallbackKeyListener;
import net.slashie.utils.swing.CleanButton;
import net.slashie.utils.swing.GFXMenuItem;
import net.slashie.utils.swing.MenuBox;

public class ExpeditionOryxUI extends GFXUserInterface implements ExpeditionUserInterface{
	public static final int STANDARD_ITEM_WIDTH = 202;

	private final class ItemsComparator implements Comparator<GFXMenuItem> {
		@Override
		public int compare(GFXMenuItem arg0, GFXMenuItem arg1) {
			return arg0.getGroupClassifier().compareTo(arg1.getGroupClassifier());
		}
	}
	
	public static final int STANDARD_ITEM_HEIGHT = 62;
	private ItemsComparator ITEMS_COMPARATOR = new ItemsComparator();
	private Color TITLE_COLOR = new Color(224,226,108);
	private Color TEXT_COLOR = Color.WHITE;
	private Image BATTLE_BACKGROUND;
	private Image BTN_SPLIT_UP;
	private Image BTN_SPLIT_DOWN;
	private Image BTN_BUY;
	private static Image BTN_TRANSFER;
	private static Image BTN_BUILD;
	
	public static Image BTN_SIZE2;
	public static Image BTN_SIZE3;
	public static Image BTN_SIZE4;
	
	public static Image BTN_MOVE;
	
	
	private BufferedImage IMG_BOX;
	public Cursor HAND_CURSOR;
	public Cursor POINTER_CURSOR;
	private SimplifiedUnitGFXMenuItem mainUnitMenuItem;
	private AbstractItem HORSES_ITEM;
	
	@Override
	protected Position getRelativePosition(Position position, Position offset) {
		if (getPlayer().getLevel() instanceof ExpeditionLevelReader){
			offset = GlobeMapModel.scaleVar(offset, getPlayer().getPosition().y());
			offset.y *= -1;
			return Position.add(player.getPosition(), offset);
		} else {
			return super.getRelativePosition(position, offset);
		}
	}
	
	public BorderedMenuBox createBorderedMenuBox(int borderWidth, int outsideBound, int inBound, int insideBound, int itemHeight){
		final ExpeditionOryxUI this_ = this;
		BorderedMenuBox ret = new BorderedMenuBox(BORDER1, BORDER2, BORDER3, BORDER4, si, COLOR_WINDOW_BACKGROUND, COLOR_BORDER_IN, COLOR_BORDER_OUT, borderWidth, outsideBound, inBound, insideBound, itemHeight, null){
			@Override
			protected Cursor getHandCursor() {
				return this_.getHandCursor();
			}
			
			@Override
			protected Cursor getDefaultCursor() {
				return this_.getDefaultCursor();
			}
		};
		ret.setCursor(si.getCursor());
		return ret;
	}
	
	@Override
	public void showDetailedInfo(Actor a) {
		// TODO: Implement
	}

	@Override
	public String getQuitMessage() {
		return "Quit?";
	}

	@Override
	public void showInventory() {
		Equipment.eqMode = true;
		((GFXUISelector)getPlayer().getSelector()).deactivate();

		// Create the good type buttons
		CleanButton peopleButton = new CleanButton(new ImageIcon(UIProperties.getProperty("BTN_PEOPLE")), HAND_CURSOR);
		peopleButton.setBounds(540,41, 24,24);	
		CleanButton suppliesButton = new CleanButton(new ImageIcon(UIProperties.getProperty("BTN_SUPPLIES")), HAND_CURSOR);
		suppliesButton.setBounds(569,41, 24,24);	
		CleanButton tradeGoodsButton = new CleanButton(new ImageIcon(UIProperties.getProperty("BTN_MERCHANDISE")), HAND_CURSOR);
		tradeGoodsButton.setBounds(598,41, 24,24);	
		CleanButton armoryButton = new CleanButton(new ImageIcon(UIProperties.getProperty("BTN_WEAPONS")), HAND_CURSOR);
		armoryButton.setBounds(627,41, 24,24);	
		CleanButton livestockButton = new CleanButton(new ImageIcon(UIProperties.getProperty("BTN_LIVESTOCK")), HAND_CURSOR);
		livestockButton.setBounds(656,41, 24,24);	
		CleanButton closeButton = new CleanButton(new ImageIcon(UIProperties.getProperty("BTN_CLOSE")), HAND_CURSOR);
		closeButton.setBounds(730,41, 24,24);
		
		
		si.add(peopleButton);
		si.add(suppliesButton);
		si.add(tradeGoodsButton);
		si.add(armoryButton);
		si.add(livestockButton);
		si.add(closeButton);

		BlockingQueue<String> inventorySelectionQueue = new LinkedBlockingQueue<String>();
		
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
		menuBox.setCursor(si.getCursor());
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
					if (handler.size() == 0)
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
	
	@Override
	public String inputBox(String prompt) {
		return inputBox(prompt, 200, 40, 400, 200, 260, 120, 20);
	}
	
	@Override
	public int switchChat(String title, String prompt, String... options) {
		((GFXUISelector)getPlayer().getSelector()).deactivate();
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
   		((GFXUISelector)getPlayer().getSelector()).deactivate();
   		CleanButton closeButton = new CleanButton(new ImageIcon(UIProperties.getProperty("BTN_CLOSE")), HAND_CURSOR);
		closeButton.setBounds(730,41, 24,24);
		
		CleanButton buyButton = new CleanButton(new ImageIcon(BTN_BUY), HAND_CURSOR);
		buyButton.setSize(96,48);
		BlockingQueue<Integer> buyButtonSelectionHandler = new LinkedBlockingQueue<Integer>();

   		StoreBorderGridBox menuBox = new StoreBorderGridBox(BORDER1, BORDER2, BORDER3, BORDER4, si, COLOR_WINDOW_BACKGROUND, COLOR_BORDER_IN, COLOR_BORDER_OUT, tileSize, 6,9,12,62,202,2,5,null,
   				store, getExpedition(), buyButton, buyButtonSelectionHandler, BTN_SPLIT_UP, BTN_SPLIT_DOWN, closeButton);
   		menuBox.setCursor(si.getCursor());
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
		if (getPlayer() != null)
			((GFXUISelector)getPlayer().getSelector()).deactivate();
		message = message.replaceAll("XXX", "\n");
		showTextBox(message, 140, 300, 520, 250);
	}
	
	@Override
	public void showSystemMessage(String x) {
		showBlockingMessage(x);
	}
	
	@Override
	public boolean promptChat(String message) {
		if (getPlayer() != null)
			((GFXUISelector)getPlayer().getSelector()).deactivate();
		message = message.replaceAll("XXX", "\n");
		return promptChat(message, 140,388,520,200);
	}
	
	public void transferFromExpedition(GoodsCache ship) {
		transferFromExpedition(ship, -1);
	}
	
	public void transferFromExpedition(GoodsCache goodsCache, int minUnits) {
		// Create the button to confirm transfer and add it to the UI
		CleanButton transferButton = new CleanButton(new ImageIcon(BTN_TRANSFER), HAND_CURSOR);
		transferButton.setSize(BTN_TRANSFER.getWidth(null),BTN_TRANSFER.getHeight(null));
		ItemTransferFunctionality transferFromExpeditionFunctionality = new TransferFromExpeditionFunctionality(minUnits);
		transferItems(getExpedition(), goodsCache, transferFromExpeditionFunctionality, true, false, transferButton);
	}
	
	public void transferFromCache(GoodsCache goodsCache) {
		CleanButton transferButton = new CleanButton(new ImageIcon(BTN_TRANSFER), HAND_CURSOR);
		transferButton.setSize(BTN_TRANSFER.getWidth(null),BTN_TRANSFER.getHeight(null));
		ItemTransferFunctionality transferFromCacheFunctionality = new TransferFromCacheFunctionality();
		transferItems(goodsCache, getExpedition(), transferFromCacheFunctionality, false, false, transferButton);
		if (goodsCache.destroyOnEmpty() && goodsCache.getItems().size() == 0)
			level.destroyFeature(goodsCache);
	}
	
	public List<Equipment> selectItemsFromExpedition(String prompt, String verb) {
		CleanButton selectButton = new CleanButton(new ImageIcon(BTN_SIZE3), HAND_CURSOR);
		selectButton.setSize(BTN_SIZE3.getWidth(null),BTN_SIZE3.getHeight(null));
		selectButton.setFont(si.getFont());
		selectButton.setForeground(Color.WHITE);
		selectButton.setText("SELECT");
		
		List<Equipment> selection = new ArrayList<Equipment>();
		ItemTransferFunctionality selectItemsFunctionality = new SelectFromExpeditionFunctionality(selection, prompt, verb);
		ItemContainer tempItemContainer = new GoodsCache((ExpeditionGame)getPlayer().getGame()){
			@Override
			public String getDescription() {
				return "Offer";
			}
		};
		
		transferItems(getExpedition(), tempItemContainer, selectItemsFunctionality, true, true, selectButton);
		return selection;
	}

	/**
	 * 
	 * @param from
	 * @param to
	 * @param itemTransferFunctionality
	 * @param fromExpedition
	 * @param cloneEquipment Prevents alterations over the original equipments by cloning them. Used for selectItems
	 */
	public void transferItems(ItemContainer from, ItemContainer to, ItemTransferFunctionality itemTransferFunctionality, 
			boolean fromExpedition, boolean cloneEquipment, CleanButton transferButton) {
		// Change UI Mode
   		Equipment.eqMode = true;
   		((GFXUISelector)getPlayer().getSelector()).deactivate();
   		clearTextBox();
   		// Create the close button and add it to the UI
   		CleanButton closeButton = new CleanButton(new ImageIcon(UIProperties.getProperty("BTN_CLOSE")), HAND_CURSOR);
		closeButton.setBounds(730,41, 24,24);
		
   		// Create the buttons for good type selection and add them to the UI
   		CleanButton peopleButton = new CleanButton(new ImageIcon(UIProperties.getProperty("BTN_PEOPLE")), HAND_CURSOR);
		peopleButton.setBounds(540,41, 24,24);	
		CleanButton suppliesButton = new CleanButton(new ImageIcon(UIProperties.getProperty("BTN_SUPPLIES")), HAND_CURSOR);
		suppliesButton.setBounds(569,41, 24,24);	
		CleanButton tradeGoodsButton = new CleanButton(new ImageIcon(UIProperties.getProperty("BTN_MERCHANDISE")), HAND_CURSOR);
		tradeGoodsButton.setBounds(598,41, 24,24);	
		CleanButton armoryButton = new CleanButton(new ImageIcon(UIProperties.getProperty("BTN_WEAPONS")), HAND_CURSOR);
		armoryButton.setBounds(627,41, 24,24);	
		CleanButton livestockButton = new CleanButton(new ImageIcon(UIProperties.getProperty("BTN_LIVESTOCK")), HAND_CURSOR);
		livestockButton.setBounds(656,41, 24,24);
		si.add(peopleButton);
		si.add(suppliesButton);
		si.add(tradeGoodsButton);
		si.add(armoryButton);
		si.add(livestockButton);
		si.add(closeButton);
		
		// Create the blockingqueue
		BlockingQueue<String> transferFromExpeditionHandler = new LinkedBlockingQueue<String>();
		
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
   		menuBox.setCursor(si.getCursor());
  		menuBox.setBounds(16, 16, 768,480);
  		
  		
  		menuBox.setTitle(itemTransferFunctionality.getTitle(from, to));
  		menuBox.setLegend("Select the units or goods to transfer");
  		
  		int typeChoice = 0;
  		int selectedIndex;
  		GoodType[] goodTypes = GoodType.getGoodTypes();
  		
		Map<GoodType, List<Equipment>> expeditionGoodsMap = null;
		
  		if (cloneEquipment){
  			expeditionGoodsMap = new HashMap<GoodType, List<Equipment>>();
  			for (GoodType goodType: goodTypes){
  				expeditionGoodsMap.put(goodType, getExpedition().getGoods(goodType, true));
  			}
  		}
  		while (true){
  			menuBox.setHoverDisabled(false);
  			// Select data to draw and draw it
  			int currentPage = menuBox.getCurrentPage();
  			List<Equipment> inventory = null;
  			if (typeChoice < goodTypes.length){
  				if (cloneEquipment){
  					inventory = expeditionGoodsMap.get(goodTypes[typeChoice]);
  				} else {
  					inventory = from.getGoods(goodTypes[typeChoice]);
  				}
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
  	  			if (!itemTransferFunctionality.validateBreak(from, to)){
  	  				transferFromExpeditionHandler.clear();
	  	  			continue;
  	  			}
				break;
  	  		} else if (commandParts[0].equals("SELECT_UNIT")){
  	  			selectedIndex = Integer.parseInt(commandParts[1]);
  	  			menuBox.selectUnit(selectedIndex);
  	  		} else if (commandParts[0].equals("CONFIRM_TRANSFER")){
  	  			Equipment choice = menuBox.getSelectedUnit();
  	  			int quantity = menuBox.getQuantity();
  	  			if (quantity == 0){
  	  				continue;
  	  			}
  	  			
				if (quantity > choice.getQuantity()){
					showBlockingMessage("Not enough "+choice.getItem().getDescription());
					transferFromExpeditionHandler.clear();
					continue;
				}
				
				if (!itemTransferFunctionality.validateAndPerformTransfer(from, to, expeditionGoodsMap, choice, quantity)){
					transferFromExpeditionHandler.clear();
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
		// Get all data
		Expedition statsExpedition = getExpedition();
		AbstractCell currentCell = getExpedition().getLocation().getMapCell(getExpedition().getPosition());
		Calendar gameTime = ((ExpeditionGame)player.getGame()).getGameTime();
		Pair<String, String> locationDescription = getExpedition().getLocation().getLocationDescription();
		Pair<String, String> locationLabels = getExpedition().getLocation().getLocationLabels();
		int totalShips = statsExpedition.getTotalShips();
		String hourStr = gameTime.get(Calendar.HOUR) == 0 ? "12" : gameTime.get(Calendar.HOUR)+"";
		String minuteStr = gameTime.get(Calendar.MINUTE) < 10 ? "0"+gameTime.get(Calendar.MINUTE) : gameTime.get(Calendar.MINUTE)+"";
		String amPmStr = gameTime.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM";
		
		// Compose labels
		String ui_date = gameTime.get(Calendar.YEAR)+", "+ months[gameTime.get(Calendar.MONTH)] +" "+ gameTime.get(Calendar.DATE);
		String ui_time = hourStr +":"+ minuteStr+" "+ amPmStr;
		String ui_gold = getExpedition().getAccountedGold()+" Gold";
		String ui_food = statsExpedition.getOffshoreFoodDays()+" food days";
		String ui_foodModifier = TemperatureRules.getTemperatureFoodModifierString(getExpedition().getLocation().getTemperature()) + (statsExpedition.isForaging() ? " (foraging)" : "");
		String ui_carrying;
		if (getExpedition().getLevel() instanceof ExpeditionMicroLevel)
			ui_carrying = "Carrying "+statsExpedition.getOffshoreCurrentlyCarrying()+"%";
		else
			ui_carrying = "Carrying "+statsExpedition.getCurrentlyCarrying()+"%";
		String ui_morale = statsExpedition.getMoraleDescription();
		String ui_armed = statsExpedition.isArmed()?"(Armed)":"";
		String ui_locationDescription = getExpedition().getLocation().getDescription();
		String ui_terrainDescription = currentCell.getDescription();
		String ui_debug = "Scale "+GlobeMapModel.getLongitudeScale(statsExpedition.getPosition().y);
		String ui_weatherDescription =getExpedition().getLocation().getWeather().getDescription(); 
		String ui_temperatureDescription = getExpedition().getLocation().getTemperatureDescription();
		String ui_windDirection = getExpedition().getLocation().getWindDirection().getAbbreviation();
		String ui_headingDirection = getExpedition().getHeading().getAbbreviation();
		String ui_bearing;
		if (getExpedition().getMovementMode() == MovementMode.SHIP){
			ui_bearing = getExpedition().getSailingPoint().getDescription();
		} else {
			ui_bearing = statsExpedition.getMovementMode().getDescription();
		}
		String ui_movementSpeed = getExpedition().getMovementSpeed().getDescription();
		String ui_shipStatus; 
		if (totalShips > 0){
			if (totalShips == 1){
				ui_shipStatus = "A ship ("+statsExpedition.getShipHealth()+"%)";
			} else {
				ui_shipStatus = totalShips+" ships ("+statsExpedition.getShipHealth()+"%)";
			}
		} else {
			ui_shipStatus = "";
		}
		String ui_water = "WATER: <0>";
		String ui_windStrength = "<Strenght>";
		String ui_currentDirection = "<D>";
		String ui_currentStrength = "<Strength>";
		
		// Draw
		si.setColor(Color.WHITE);
		
		si.print(20, 1, ui_debug);
		
		// Left Column
		si.print(2, 1, ui_date);
		si.print(2, 2, ui_locationDescription);
		si.print(2, 3, locationLabels.getA(), TITLE_COLOR);
		si.print(8, 3, locationDescription.getA());
		si.print(2, 4, locationLabels.getB(), TITLE_COLOR);
		si.print(8, 4, locationDescription.getB());
		si.print(2, 5, ui_food + ui_foodModifier);
		si.print(2, 6, ui_water);
		si.print(2, 7, ui_morale + ui_armed);
		si.print(2, 8, ui_carrying);
		si.print(2, 9, ui_gold);
		
		// Right Column
		int line2 = 63;

		si.print(line2, 1, ui_time);
		si.print(line2, 2, ui_weatherDescription);
		si.print(line2, 3, ui_temperatureDescription);
		si.print(line2, 4, ui_terrainDescription);
			
		si.print(line2, 5, "WIND", TITLE_COLOR);
			si.print(line2+9, 5, ui_windDirection);
		si.print(line2+2, 6, ui_windStrength);
		si.print(line2, 7, "CURRENT", TITLE_COLOR);
			si.print(line2+9, 7, ui_currentDirection);
		si.print(line2+2, 8, ui_currentStrength);
		
		si.print(line2, 9, "HEADING", TITLE_COLOR);
			si.print(line2+9, 9, ui_headingDirection);
		si.print(line2+2, 10, ui_bearing);
		si.print(line2, 11, ui_movementSpeed);
		
		si.print(line2, 12, ui_shipStatus);
		
		
		if (getExpedition().getMovementMode() == MovementMode.SHIP){
			vehicleUnitItems.clear();
			for (Vehicle expeditionVehicle: statsExpedition.getCurrentVehicles()){
				vehicleUnitItems.add(new VehicleGFXMenuItem(expeditionVehicle));
			}
			vehiclesMenuBox.setMenuItems(vehicleUnitItems);
			vehiclesMenuBox.draw();
		} else {
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
			//expeditionUnitItems.add(0, mainUnitMenuItem);
			unitsMenuBox.setMenuItems(expeditionUnitItems);
			Collections.sort(expeditionUnitItems, ITEMS_COMPARATOR);
			unitsMenuBox.draw();
		}
		
		si.drawImage(774, 2, BTN_MOVE);
	}
	private List<GFXMenuItem> expeditionUnitItems = new ArrayList<GFXMenuItem>();
	private List<GFXMenuItem> vehicleUnitItems = new ArrayList<GFXMenuItem>();
	private Map<String,Equipment> resumedEquipments = new HashMap<String, Equipment>();
	
	private Vector<Equipment> expeditionUnitsVector = new Vector<Equipment>();
	private MenuBox unitsMenuBox;
	private MenuBox vehiclesMenuBox;
	private Properties UIProperties;
	
	public void init(SwingSystemInterface psi, String title, UserCommand[] gameCommands, Properties UIProperties, Action target){
		super.init(psi, title, gameCommands, UIProperties, target);
		try {
			FNT_TEXT = PropertyFilters.getFont(UIProperties.getProperty("FNT_TEXT"), UIProperties.getProperty("FNT_TEXT_SIZE"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (FontFormatException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		psi.setFont(FNT_TEXT);
		
		HAND_CURSOR = GFXUserInterface.createCursor(UIProperties.getProperty("IMG_CURSORS"), 6, 2, 10, 4);
		POINTER_CURSOR = GFXUserInterface.createCursor(UIProperties.getProperty("IMG_CURSORS"), 6, 3, 4, 4);

		//unitsMenuBox = new BorderedMenuBox(BORDER1, BORDER2, BORDER3, BORDER4, si, COLOR_WINDOW_BACKGROUND, COLOR_BORDER_IN, COLOR_BORDER_OUT, tileSize, null);
		unitsMenuBox = new MenuBox(si, null);
		unitsMenuBox.setGap(36);
		unitsMenuBox.setPosition(61,14);
		unitsMenuBox.setWidth(17);
		unitsMenuBox.setItemsPerPage(6);
  		unitsMenuBox.setShowOptions(false);
  		
  		vehiclesMenuBox = new MenuBox(si, null);
  		vehiclesMenuBox.setGap(36);
  		vehiclesMenuBox.setPosition(61,14);
  		vehiclesMenuBox.setWidth(17);
  		vehiclesMenuBox.setItemsPerPage(6);
  		vehiclesMenuBox.setShowOptions(false);
  		
		//unitsMenuBox.setTitle("Expedition");
  		try {
			BATTLE_BACKGROUND = ImageUtils.createImage(UIProperties.getProperty("BATTLE_BACKGROUND"));
			BTN_SPLIT_UP = PropertyFilters.getImage(UIProperties.getProperty("IMG_UI"), UIProperties.getProperty("BTN_SPLIT_UP_BOUNDS"));
			BTN_SPLIT_DOWN = PropertyFilters.getImage(UIProperties.getProperty("IMG_UI"), UIProperties.getProperty("BTN_SPLIT_DOWN_BOUNDS"));
			BTN_BUY = PropertyFilters.getImage(UIProperties.getProperty("IMG_UI"), UIProperties.getProperty("BTN_BUY_BOUNDS"));
			BTN_TRANSFER = PropertyFilters.getImage(UIProperties.getProperty("IMG_UI"), UIProperties.getProperty("BTN_TRANSFER_BOUNDS"));
			BTN_BUILD = PropertyFilters.getImage(UIProperties.getProperty("IMG_UI"), UIProperties.getProperty("BTN_DOBUILD_BOUNDS"));
			BTN_SIZE2 = PropertyFilters.getImage(UIProperties.getProperty("IMG_UI"), UIProperties.getProperty("BTN_SIZE2_BOUNDS"));
			BTN_SIZE3 = PropertyFilters.getImage(UIProperties.getProperty("IMG_UI"), UIProperties.getProperty("BTN_SIZE3_BOUNDS"));
			BTN_SIZE4 = PropertyFilters.getImage(UIProperties.getProperty("IMG_UI"), UIProperties.getProperty("BTN_SIZE4_BOUNDS"));
			BTN_MOVE = PropertyFilters.getImage(UIProperties.getProperty("IMG_UI"), UIProperties.getProperty("BTN_MOVE_BOUNDS"));
			IMG_BOX = ImageUtils.createImage(UIProperties.getProperty("IMG_BOX"));
		} catch (IOException e) {
			e.printStackTrace();
			ExpeditionGame.crash("Error loading images", e);
		}
		
		HORSES_ITEM = ItemFactory.createItem("HORSE");
		addornedTextArea.setCursor(si.getCursor());
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
		((GFXUISelector)getPlayer().getSelector()).deactivate();
				
		int xBase = 192;
		int yBase = 48;
		si.drawImage(168, yBase - 24, BATTLE_BACKGROUND);
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
		
		//si.waitKeyOrClick(CharKey.SPACE);
	}
	
	@Override
	public void showBattleResults(
			List<Equipment> originalAttackingUnits, List<Equipment> originalDefendingUnits,
			String battleTitle, AssaultOutcome attackerRangedAttackOutcome,
			AssaultOutcome defenderRangedAttackOutcome,
			AssaultOutcome[] mountedAttackOutcome,
			AssaultOutcome[] meleeAttackOutcome, int attackerScore, int defenderScore) {
		List<String> messages = CommonUI.getBattleResultsString(originalAttackingUnits, originalDefendingUnits, battleTitle,attackerRangedAttackOutcome,defenderRangedAttackOutcome,mountedAttackOutcome,meleeAttackOutcome, attackerScore, defenderScore);
		for (String message: messages){
			message = message.replaceAll("XXX", "\n");
			showTextBox(message, 16, 380, 776, 200);
		}
	}
	
	@Override
	/**
	 * Shows the list of units and the message and prompts for confirmation
	 */
	public boolean promptUnitList(List<Equipment> unitList, String title, String prompt) {
		Equipment.eqMode = true;
		clearTextBox();
		final ExpeditionOryxUI this_ = this;
   		BorderedGridBox cacheBox = new BorderedGridBox(BORDER1, BORDER2, BORDER3, BORDER4, si, COLOR_WINDOW_BACKGROUND, COLOR_BORDER_IN, COLOR_BORDER_OUT, tileSize, 6,9,12,STANDARD_ITEM_HEIGHT, STANDARD_ITEM_WIDTH,2,4,null, null) {
   			protected Cursor getDefaultCursor() {
   				return this_.getDefaultCursor();
   			}

   			protected Cursor getHandCursor() {
   				return this_.getHandCursor();
   			}
   		};
   		cacheBox.setCursor(si.getCursor());
   		cacheBox.setBounds(160, 16, 624,360);
  		
  		Vector menuItems = new Vector();
  		for (Equipment item: unitList){
  			menuItems.add(new InventoryCustomGFXMenuItem(item));
  		}
  		Collections.sort(menuItems, ITEMS_COMPARATOR);
  		cacheBox.setMenuItems(menuItems);
  		cacheBox.setTitle(title);
  		cacheBox.setTitleColor(TITLE_COLOR);
  		cacheBox.setForeColor(TEXT_COLOR);
  		cacheBox.draw(true);
		boolean ret = promptChat(prompt);
		cacheBox.kill();
		return ret;
	}
	
	@Override
	public List<Building> createBuildingPlan() {
		List<Building> knownBuildings = getExpedition().getKnownBuildings();
		List<BuildingCustomGFXMenuItem> buildingMenuItems = new ArrayList<BuildingCustomGFXMenuItem>();
		for (Building building: knownBuildings){
			buildingMenuItems.add(new BuildingCustomGFXMenuItem(building));
		}
		
		CleanButton closeButton = new CleanButton(new ImageIcon(UIProperties.getProperty("BTN_CLOSE")), HAND_CURSOR);
		closeButton.setBounds(730,41, 24,24);
		
		BlockingQueue<String> selectionHandler = new LinkedBlockingQueue<String>();
		closeButton.addActionListener(getStringCallBackActionListener(selectionHandler, "CANCEL"));
		
		BuildingPlanBorderGridBox menuBox = new BuildingPlanBorderGridBox(BORDER1, BORDER2, BORDER3, BORDER4, si, COLOR_WINDOW_BACKGROUND, COLOR_BORDER_IN, COLOR_BORDER_OUT, tileSize, 6,9,12,
				STANDARD_ITEM_HEIGHT, STANDARD_ITEM_WIDTH+43, 3, 5, selectionHandler, BTN_BUILD, BTN_SPLIT_UP, BTN_SPLIT_DOWN, closeButton);
		menuBox.setCursor(si.getCursor());
  		menuBox.setBounds(16, 16, 768,480);
  		menuBox.setTitle("Building Plan");
  		menuBox.setLegend("Please select the buldings you want to construct");
  		menuBox.setMenuItems(buildingMenuItems);
  		boolean cancel = false;
  		
		menuBox.draw(true);
  		String commandStr = null;
  		while (commandStr == null){
  			try {
				commandStr = selectionHandler.take();
			} catch (InterruptedException e) {}
  		}
  		
  		cancel = commandStr.equals("CANCEL");

  		menuBox.kill();
  		
  		si.restore();
 		si.refresh();
 		
  		if (!cancel){
	  		List<Building> buildingPlan = new ArrayList<Building>();
	  		for (BuildingCustomGFXMenuItem buildingMenuItem: buildingMenuItems){
	  			for (int i = 0; i < buildingMenuItem.getQuantity(); i++){
	  				buildingPlan.add(buildingMenuItem.getBuilding());
	  			}
			}
	  		return buildingPlan;
  		} else {
  			return null;
  		}
	}
	
	@Override
	public void showCityInfo(Town town) {
		String townInfo = CommonUI.getTownDescription(town);
		townInfo = townInfo.replaceAll("XXX", "\n");
   		printTextBox(townInfo, 80, 20, 600, 260);
	}
	
	@Override
	public void afterTownAction() {
		clearTextBox();
	}
	
	@Override
	public int getXScale() {
		if (getExpedition().getLevel() instanceof ExpeditionLevelReader)
			return GlobeMapModel.getLongitudeScale(getExpedition().getLatitude());
		else
			return 1;
	}
	
	@Override
	public int getYScale() {
		if (getExpedition().getLevel() instanceof ExpeditionLevelReader)
			//return -GlobeMapModel.getLongitudeScale(getExpedition().getLatitude());
			return -GlobeMapModel.getLatitudeHeight();
		else
			return 1;
	}
	
	@Override
	public Cursor getDefaultCursor() {
		return POINTER_CURSOR;
	}
	
	@Override
	public Cursor getHandCursor() {
		return HAND_CURSOR;
	}
	
	@Override
	public void onPlayerDeath() {
		super.onPlayerDeath();
		((GFXUISelector)getPlayer().getSelector()).deactivate();
	}
	
	interface ItemTransferFunctionality {
		String getTitle(ItemContainer from, ItemContainer to);
		boolean validateBreak(ItemContainer from, ItemContainer to);
		/**
		 * 
		 * @param from
		 * @param to
		 * @param fromMap Optional, only used when cloning inventory. This is a temporary map with the would-be contents of the "from" container
		 * @param choice
		 * @param quantity
		 * @return
		 */
		boolean validateAndPerformTransfer(ItemContainer from, ItemContainer to, Map<GoodType, List<Equipment>> fromMap, Equipment choice, int quantity);
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
		public boolean validateAndPerformTransfer(ItemContainer from, ItemContainer to, Map<GoodType, List<Equipment>> fromMap, Equipment choice, int quantity) {
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
		public boolean validateAndPerformTransfer(ItemContainer from, ItemContainer to, Map<GoodType, List<Equipment>> fromMap, Equipment choice, int quantity) {
  			ExpeditionItem item = (ExpeditionItem) choice.getItem();
			if (!to.canCarry(item, quantity)){
				showBlockingMessage("Not enough room in the "+to.getDescription());
				return false;
			}
			
			from.reduceQuantityOf(choice.getItem(), quantity);
		
			if (choice.getItem() instanceof ExpeditionUnit){
				if (from.getTotalUnits() == 0){
					from.addItem((ExpeditionItem) choice.getItem(), quantity);
					showBlockingMessage("At least a unit should remain in the expedition.");
					return false;
				} else if (from.getCurrentlyCarrying()>100){
					from.addItem((ExpeditionItem) choice.getItem(), quantity);
					showBlockingMessage("The expedition can't carry the goods! Be sure to leave enough men on the expedition.");
					return false;
				}
			}  else if (choice.getItem() instanceof Mount && from.getCurrentlyCarrying()>100){
				from.addItem((ExpeditionItem) choice.getItem(), quantity);
				if (quantity == 1){
					showBlockingMessage("The expedition can't carry the goods without that horse!");
				} else {
					showBlockingMessage("The expedition can't carry the goods without these horses!");
				}
				return false;
			}
		
			to.addItem((ExpeditionItem)choice.getItem(), quantity);
			return true;
		}
	}
	
	class SelectFromExpeditionFunctionality implements ItemTransferFunctionality {
		private List<Equipment> selection;
		private String prompt;
		private Map<String, Equipment> selectionMap = new HashMap<String, Equipment>();
		SelectFromExpeditionFunctionality (List<Equipment> selection, String prompt, String verb){
			this.selection = selection;
			this.prompt = prompt;
		}
		
		@Override
		public String getTitle(ItemContainer from, ItemContainer to) {
			return prompt;
		}
		
		@Override
		public boolean validateBreak(ItemContainer from, ItemContainer to) {
			return true;
		}
		
		@Override
		public boolean validateAndPerformTransfer(ItemContainer from, ItemContainer to, Map<GoodType, List<Equipment>> fromMap, Equipment choice, int quantity) {
			// Validate at least one man in the expedition
			if (((ExpeditionItem)choice.getItem()).getGoodType() == GoodType.PEOPLE){
				List<Equipment> allMen = fromMap.get(GoodType.PEOPLE);
				int men = 0;
				for (Equipment man: allMen){
					men += man.getQuantity();
				}
				if (men - quantity <= 0){
					showBlockingMessage("We can't stay all here, at least one should continue.");
					return false;
				}
			}
			
			to.addItem((ExpeditionItem)choice.getItem(), quantity);
			choice.reduceQuantity(quantity);
			
			AbstractItem item = choice.getItem();
			if (selectionMap.get(item.getFullID()) == null){
				Equipment e = new Equipment(item, quantity);
				selectionMap.put(item.getFullID(), e);
				selection.add(e);
			} else {
				Equipment e = selectionMap.get(item.getFullID());
				e.setQuantity(e.getQuantity()+quantity);
			}
			return true;
		}
	}
	
	@Override
	public void processSave(){
		if (!player.getGame().canSave()){
			level.addMessage("You cannot save your game here!");
			return;
		}
		
		if (promptChat("Save your game in journal \""+getPlayer().getName()+"\"?")){
			messageBox.setText("Saving... ");
			si.refresh();
			informPlayerCommand(CommandListener.SAVE);
			enterScreen();
		}
	}

}