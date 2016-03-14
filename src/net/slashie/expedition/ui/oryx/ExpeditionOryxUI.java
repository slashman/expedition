package net.slashie.expedition.ui.oryx;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
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
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import net.ck.expedition.utils.swing.MessengerService;
import net.ck.expedition.utils.swing.TutorialComponent;
import net.slashie.expedition.domain.AssaultOutcome;
import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.Expedition.MovementMode;
import net.slashie.expedition.domain.ExpeditionItem;
import net.slashie.expedition.domain.ExpeditionUnit;
import net.slashie.expedition.domain.FriarTutorial;
import net.slashie.expedition.domain.GoodType;
import net.slashie.expedition.domain.GoodsCache;
import net.slashie.expedition.domain.ItemContainer;
import net.slashie.expedition.domain.ItemOffshoreExpeditionContainer;
import net.slashie.expedition.domain.LandingParty;
import net.slashie.expedition.domain.ShipCache;
import net.slashie.expedition.domain.Store;
import net.slashie.expedition.domain.StoreItemInfo;
import net.slashie.expedition.domain.Town;
import net.slashie.expedition.domain.Vehicle;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.game.ExpeditionMusicManager;
import net.slashie.expedition.item.ItemFactory;
import net.slashie.expedition.item.Mount;
import net.slashie.expedition.level.ExpeditionLevelReader;
import net.slashie.expedition.level.GlobeMapModel;
import net.slashie.expedition.town.Building;
import net.slashie.expedition.ui.CommonUI;
import net.slashie.expedition.ui.ExpeditionUserInterface;
import net.slashie.expedition.ui.oryx.sfx.EffectsServer;
import net.slashie.expedition.world.ExpeditionLevel;
import net.slashie.expedition.world.ExpeditionMacroLevel;
import net.slashie.expedition.world.ExpeditionMicroLevel;
import net.slashie.expedition.world.TemperatureRules;
import net.slashie.expedition.world.Weather;
import net.slashie.libjcsi.CharKey;
import net.slashie.serf.action.Action;
import net.slashie.serf.action.Actor;
import net.slashie.serf.baseDomain.AbstractItem;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.game.Player;
import net.slashie.serf.level.AbstractCell;
import net.slashie.serf.ui.Appearance;
import net.slashie.serf.ui.CommandListener;
import net.slashie.serf.ui.UserCommand;
import net.slashie.serf.ui.oryxUI.AddornedBorderPanel;
import net.slashie.serf.ui.oryxUI.Assets;
import net.slashie.serf.ui.oryxUI.GFXAppearance;
import net.slashie.serf.ui.oryxUI.GFXUISelector;
import net.slashie.serf.ui.oryxUI.GFXUserInterface;
import net.slashie.serf.ui.oryxUI.SwingSystemInterface;
import net.slashie.util.Pair;
import net.slashie.utils.ImageUtils;
import net.slashie.utils.Position;
import net.slashie.utils.PropertyFilters;
import net.slashie.utils.Util;
import net.slashie.utils.swing.BorderedGridBox;
import net.slashie.utils.swing.BorderedMenuBox;
import net.slashie.utils.swing.CallbackActionListener;
import net.slashie.utils.swing.CallbackKeyListener;
import net.slashie.utils.swing.CallbackMouseListener;
import net.slashie.utils.swing.CleanButton;
import net.slashie.utils.swing.CustomGFXMenuItem;
import net.slashie.utils.swing.GFXMenuItem;
import net.slashie.utils.swing.GridBox;

@SuppressWarnings("serial")
public class ExpeditionOryxUI extends GFXUserInterface implements ExpeditionUserInterface
{
	public static final int STANDARD_ITEM_WIDTH = 237;
	public static final int STANDARD_ITEM_HEIGHT = 62;
	public static final int MAP_LAYER = 0;
	public static final int SFX_LAYER = 0;
	public static final int UI_WIDGETS_LAYER = 0;

	public static final Color ITEM_BOX_COLOR = new Color(90, 51, 7);
	public static final Color ITEM_BOX_BORDER_COLOR = new Color(188, 158, 76);
	public static final Color ITEM_BOX_HIGHLIGHT_COLOR = new Color(140, 76, 12);
	final static Logger logger = Logger.getRootLogger();
	private final class ItemsComparator implements Comparator<GFXMenuItem>
	{
		@Override
		public int compare(GFXMenuItem arg0, GFXMenuItem arg1)
		{
			return arg0.getGroupClassifier().compareTo(arg1.getGroupClassifier());
		}
	}

	private ItemsComparator ITEMS_COMPARATOR = new ItemsComparator();

	public static Color TITLE_COLOR = new Color(224, 226, 108);
	public static Color TEXT_COLOR = Color.WHITE;
	public static Image BTN_SPLIT_UP;
	public static Image BTN_SPLIT_DOWN;
	public static Image BTN_SPLIT_UP_HOVER;
	public static Image BTN_SPLIT_DOWN_HOVER;

	public static Image BTN_SPLIT_LEFT;
	public static Image BTN_SPLIT_RIGHT;
	public static Image BTN_SPLIT_LEFT_HOVER;
	public static Image BTN_SPLIT_RIGHT_HOVER;

	public static Image BTN_SPLIT_LEFT_ALL;
	public static Image BTN_SPLIT_RIGHT_ALL;
	public static Image BTN_SPLIT_LEFT_HOVER_ALL;
	public static Image BTN_SPLIT_RIGHT_HOVER_ALL;

	public static Image BTN_PEOPLE;
	public static Image BTN_SUPPLIES;
	public static Image BTN_MERCHANDISE;
	public static Image BTN_WEAPONS;
	public static Image BTN_LIVESTOCK;
	public static Image BTN_VEHICLES;
	public static Image BTN_CLOSE;
	public static Image IMG_SMALL_BUTTON_BACK;
	public static Image IMG_SMALL_BUTTON_HOVER_BACK;
	public static Image[] MORALE_IMAGES;
	public static Map<String, Image> FLOWERS;

	private Image IMG_BOX;
	public static Cursor HAND_CURSOR;
	public static Cursor POINTER_CURSOR;
	private SimplifiedUnitGFXMenuItem mainUnitMenuItem;
	private AbstractItem HORSES_ITEM;
	TutorialComponent tutorial;
	private List<GFXMenuItem> expeditionUnitItems = new ArrayList<GFXMenuItem>();
	private List<GFXMenuItem> expeditionVehicleItems = new ArrayList<GFXMenuItem>();
	private Map<String, Equipment> resumedEquipments = new HashMap<String, Equipment>();
	private List<Equipment> expeditionUnitsTemp = new ArrayList<Equipment>();
	private GridBox unitsMenuBox;
	private GridBox vehiclesMenuBox;
	private Layout layout;

	@Override
	protected Position getRelativePosition(Position position, Position offset)
	{
		if (getPlayer().getLevel() instanceof ExpeditionLevelReader)
		{
			offset = GlobeMapModel.getSingleton().scaleVar(offset, getPlayer().getPosition().y());
			offset.y *= -1;
			return Position.add(player.getPosition(), offset);
		}
		else
		{
			return super.getRelativePosition(position, offset);
		}
	}

	public BorderedMenuBox createBorderedMenuBox()
	{
		// Standard measures
		int borderWidth = 20;
		int outsideBound = 6;
		int inBound = 9;
		int insideBound = 12;
		int itemHeight = 20;
		final ExpeditionOryxUI this_ = this;
		BorderedMenuBox ret = new BorderedMenuBox(BORDER1, BORDER2, BORDER3, BORDER4, si, COLOR_WINDOW_BACKGROUND,
				COLOR_BORDER_IN, COLOR_BORDER_OUT, borderWidth, outsideBound, inBound, insideBound, itemHeight, null)
		{
			@Override
			protected Cursor getHandCursor()
			{
				return this_.getHandCursor();
			}

			@Override
			protected Cursor getDefaultCursor()
			{
				return this_.getDefaultCursor();
			}
		};
		ret.setCursor(si.getCursor());
		return ret;
	}

	public BorderedGridBox createBorderedGridBox(int itemHeight, int itemWidth, int gridX, int gridY)
	{
		final ExpeditionOryxUI this_ = this;
		int borderWidth = 20;
		int outsideBound = 6;
		int inBound = 9;
		int insideBound = 12;
		BorderedGridBox ret = new BorderedGridBox(BORDER1, BORDER2, BORDER3, BORDER4, si, COLOR_WINDOW_BACKGROUND,
				COLOR_BORDER_IN, COLOR_BORDER_OUT, borderWidth, outsideBound, inBound, insideBound, itemHeight,
				itemWidth, gridX, gridY, null, null, BTN_SPLIT_UP, BTN_SPLIT_DOWN, ExpeditionOryxUI.BTN_SPLIT_UP_HOVER,
				ExpeditionOryxUI.BTN_SPLIT_DOWN_HOVER, HAND_CURSOR)
		{
			@Override
			protected Cursor getHandCursor()
			{
				return this_.getHandCursor();
			}

			@Override
			protected Cursor getDefaultCursor()
			{
				return this_.getDefaultCursor();
			}

			@Override
			public int getDrawingLayer()
			{
				return UI_WIDGETS_LAYER;
			}
		};
		ret.setCursor(si.getCursor());
		return ret;
	}

	@Override
	public void showDetailedInfo(Actor a)
	{
		// TODO: Implement
	}

	@Override
	public String getQuitMessage()
	{		
		return "Quit?";
	}

	@Override
	public void shutdown()
	{
		unitsMenuBox.deactivate();
		vehiclesMenuBox.deactivate();
		try
		{
			sfxQueue.put("STOP");
			ExpeditionMusicManager.stopWeather();
		}
		catch (InterruptedException e)
		{
		}
		if (getPlayer().getSelector() != null)
			((GFXUISelector) getPlayer().getSelector()).shutdown();
		super.shutdown();
	}

	@Override
	public void showInventory()
	{
		Equipment.eqMode = true;
		enterScreen();

		int startX = 480 - 40;
		int gapX = 40;

		// Create the good type buttons
		CleanButton peopleButton = new CleanButton(IMG_SMALL_BUTTON_BACK, IMG_SMALL_BUTTON_HOVER_BACK, BTN_PEOPLE,
				HAND_CURSOR);
		peopleButton.setPopupText("People");
		peopleButton.setLocation(startX, 31);
		CleanButton suppliesButton = new CleanButton(IMG_SMALL_BUTTON_BACK, IMG_SMALL_BUTTON_HOVER_BACK, BTN_SUPPLIES,
				HAND_CURSOR);
		suppliesButton.setLocation(startX + gapX * 1, 31);
		suppliesButton.setPopupText("Supplies");
		CleanButton tradeGoodsButton = new CleanButton(IMG_SMALL_BUTTON_BACK, IMG_SMALL_BUTTON_HOVER_BACK,
				BTN_MERCHANDISE, HAND_CURSOR);
		tradeGoodsButton.setLocation(startX + gapX * 2, 31);
		tradeGoodsButton.setPopupText("Trade Goods");
		CleanButton armoryButton = new CleanButton(IMG_SMALL_BUTTON_BACK, IMG_SMALL_BUTTON_HOVER_BACK, BTN_WEAPONS,
				HAND_CURSOR);
		armoryButton.setLocation(startX + gapX * 3, 31);
		armoryButton.setPopupText("Armory");
		CleanButton livestockButton = new CleanButton(IMG_SMALL_BUTTON_BACK, IMG_SMALL_BUTTON_HOVER_BACK, BTN_LIVESTOCK,
				HAND_CURSOR);
		livestockButton.setLocation(startX + gapX * 4, 31);
		livestockButton.setPopupText("Livestock");
		CleanButton vehiclesButton = new CleanButton(IMG_SMALL_BUTTON_BACK, IMG_SMALL_BUTTON_HOVER_BACK, BTN_VEHICLES,
				HAND_CURSOR);
		vehiclesButton.setLocation(startX + gapX * 5, 31);
		vehiclesButton.setPopupText("Vehicles");
		CleanButton closeButton = new CleanButton(IMG_SMALL_BUTTON_BACK, IMG_SMALL_BUTTON_HOVER_BACK, BTN_CLOSE,
				HAND_CURSOR);
		closeButton.setLocation(startX + gapX * 7, 31);

		si.add(peopleButton);
		si.add(suppliesButton);
		si.add(tradeGoodsButton);
		si.add(armoryButton);
		si.add(livestockButton);
		si.add(vehiclesButton);
		si.add(closeButton);

		BlockingQueue<String> inventorySelectionQueue = new LinkedBlockingQueue<String>();

		peopleButton.addActionListener(getStringCallBackActionListener(inventorySelectionQueue, "0"));
		suppliesButton.addActionListener(getStringCallBackActionListener(inventorySelectionQueue, "1"));
		tradeGoodsButton.addActionListener(getStringCallBackActionListener(inventorySelectionQueue, "2"));
		armoryButton.addActionListener(getStringCallBackActionListener(inventorySelectionQueue, "3"));
		livestockButton.addActionListener(getStringCallBackActionListener(inventorySelectionQueue, "4"));
		vehiclesButton.addActionListener(getStringCallBackActionListener(inventorySelectionQueue, "5"));
		closeButton.addActionListener(getStringCallBackActionListener(inventorySelectionQueue, "BREAK"));

		CallbackKeyListener<String> cbkl = new CallbackKeyListener<String>(inventorySelectionQueue)
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				try
				{
					CharKey x = new CharKey(SwingSystemInterface.charCode(e));
					if (x.code == CharKey.ENTER || x.code == CharKey.SPACE || x.code == CharKey.ESC)
					{
						handler.put("BREAK");
					}
					else if (x.isLeftArrow())
					{
						handler.put("<");
					}
					else if (x.isRightArrow())
					{
						handler.put(">");
					}
					else if (x.code == CharKey.DARROW || x.code == CharKey.N2)
					{
						handler.put("DOWN");
					}
					else if (x.code == CharKey.UARROW || x.code == CharKey.N8)
					{
						handler.put("UP");
					}

				}
				catch (InterruptedException e1)
				{
				}
			}
		};

		si.addKeyListener(cbkl);

		saveMapLayer();

		InventoryBorderGridBox menuBox = new InventoryBorderGridBox(BORDER1, BORDER2, BORDER3, BORDER4, si,
				COLOR_WINDOW_BACKGROUND, COLOR_BORDER_IN, COLOR_BORDER_OUT, borderSize, 6, 9, 12, STANDARD_ITEM_HEIGHT,
				STANDARD_ITEM_WIDTH, 3, 6, null, inventorySelectionQueue);
		menuBox.setCursor(si.getCursor());
		menuBox.setBounds(16, 16, 768, 480);
		menuBox.setTitle("Examine Expedition Inventory");

		// si.saveLayer(getUILayer());
		int typeChoice = 0;
		while (true)
		{
			GoodType[] goodTypes = GoodType.getGoodTypes();
			menuBox.setLegend(" - ");

			List<Equipment> inventory = null;
			if (typeChoice < goodTypes.length)
			{
				inventory = getExpedition().getGoods(goodTypes[typeChoice]);
			}

			List<InventoryCustomGFXMenuItem> invMenuItems = new Vector<InventoryCustomGFXMenuItem>();
			for (Equipment item : inventory)
			{
				invMenuItems.add(new InventoryCustomGFXMenuItem(item));
			}
			Collections.sort(invMenuItems, inventoryCustomItemsComparator);

			List<GFXMenuItem> menuItems = new Vector<GFXMenuItem>();
			for (InventoryCustomGFXMenuItem menuItem : invMenuItems)
			{
				menuItems.add(menuItem);
			}
			int currentPage = menuBox.getCurrentPage();
			menuBox.setMenuItems(menuItems);
			if (menuBox.isValidPage(currentPage))
			{
				menuBox.setCurrentPage(currentPage);
			}
			else
			{
				currentPage = menuBox.getPages() - 1;
				menuBox.setCurrentPage(currentPage);
			}
			menuBox.updatePageButtonStatus();
			// si.loadLayer(getUILayer()); // ???
			int boxX = startX + typeChoice * gapX - 21;
			int boxY = 41 - 24;
			menuBox.draw(boxX, boxY, IMG_BOX);

			String command = null;
			while (command == null)
			{
				try
				{
					command = inventorySelectionQueue.take();
				}
				catch (InterruptedException e1)
				{
				}
			}

			if (command.equals("BREAK"))
			{
				break;
			}
			else if (command.equals("<"))
			{
				typeChoice--;
				if (typeChoice == -1)
					typeChoice = 0;
			}
			else if (command.equals(">"))
			{
				typeChoice++;
				if (typeChoice == goodTypes.length)
					typeChoice = goodTypes.length - 1;
			}
			else if (command.equals("DOWN"))
			{
				menuBox.avPag();
			}
			else if (command.equals("UP"))
			{
				menuBox.rePag();
			}
			else
			{
				typeChoice = Integer.parseInt(command);
			}
		}
		menuBox.kill();
		si.remove(peopleButton);
		si.remove(suppliesButton);
		si.remove(tradeGoodsButton);
		si.remove(armoryButton);
		si.remove(livestockButton);
		si.remove(vehiclesButton);
		si.remove(closeButton);
		si.removeKeyListener(cbkl);
		resetMapLayer();
		// ((GFXUISelector)getPlayer().getSelector()).setMouseMovementActive(false);
		leaveScreen();
		Equipment.eqMode = false;
	}

	private CallbackActionListener<String> getStringCallBackActionListener(BlockingQueue<String> queue,
			final String option)
	{
		return new CallbackActionListener<String>(queue)
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					if (handler.size() == 0)
						handler.put(option);
				}
				catch (InterruptedException e1)
				{
				}
				si.recoverFocus();
			}
		};
	}

	private Comparator<InventoryCustomGFXMenuItem> inventoryCustomItemsComparator = new Comparator<InventoryCustomGFXMenuItem>()
	{
		public int compare(InventoryCustomGFXMenuItem o1, InventoryCustomGFXMenuItem o2)
		{
			if (o1.getEquipment().getItem() instanceof ExpeditionUnit)
			{
				if (o2.getEquipment().getItem() instanceof ExpeditionUnit)
				{
					return o1.getEquipment().getItem().getFullID().compareTo(o2.getEquipment().getItem().getFullID());
				}
				else
				{
					return -1;
				}
			}
			else if (o2.getEquipment().getItem() instanceof ExpeditionUnit)
			{
				if (o1.getEquipment().getItem() instanceof ExpeditionUnit)
				{
					return o1.getEquipment().getItem().getFullID().compareTo(o2.getEquipment().getItem().getFullID());
				}
				else
				{
					return 1;
				}
			}
			else
			{
				return o1.getEquipment().getItem().getDescription()
						.compareTo(o2.getEquipment().getItem().getDescription());
			}
		};
	};

	@Override
	public String inputBox(String prompt)
	{
		return inputBox(prompt, 200, 40, 400, 200, 260, 120, 20);
	}

	@Override
	public int switchChat(String title, String prompt, String... options)
	{
		((GFXUISelector) getPlayer().getSelector()).deactivate();
		hideStandardMessageBox();
		int ret = super.switchChat(title, prompt, TITLE_COLOR, TEXT_COLOR, options);
		showStandardMessageBox();
		return ret;
	}

	public String inputBox(String prompt, int x, int y, int w, int h, int xp, int yp, int length)
	{
		AddornedBorderPanel p = new AddornedBorderPanel(BORDER1, BORDER2, BORDER3, BORDER4, COLOR_BORDER_OUT,
				COLOR_BORDER_IN, COLOR_WINDOW_BACKGROUND, borderSize, 6, 9, 12);

		p.setBounds(x, y, w, h);
		p.paintAt(si.getDrawingGraphics(getUILayer()), x, y);
		si.setColor(getUILayer(), TEXT_COLOR);
		si.printAtPixel(getUILayer(), x + tileWidth, y + tileHeight * 2, prompt);
		si.commitLayer(getUILayer());
		String ret = si.input(getUILayer(), xp, yp, TEXT_COLOR, length);

		return ret;
	}
	
	@Override
	public void onMusicOn()
	{
		logger.debug("on music on");
		ExpeditionLevel expeditionLevel = (ExpeditionLevel) getExpedition().getLevel();
		expeditionLevel.playMusic();
	}

	public boolean depart()
	{
		if (promptChat("Do you want to leave the ports of Palos de la Frontera?"))
		{
			return true;
		}
		else
			return false;
	}

	public void launchStore(final Store store)
	{
		ItemTransferFunctionality buyItemsFunctionality = new BuyItemsFunctionality(store);
		ItemOffshoreExpeditionContainer offShoreExpeditionContainer = new ItemOffshoreExpeditionContainer(
				getExpedition(), store);
		transferItems("Welcome to the " + store.getOwnerName(), store.getMainGoodType(), store,
				offShoreExpeditionContainer, buyItemsFunctionality);
	}

	public void showBlockingMessage(String message)
	{
		showBlockingMessage(message, false);
	}

	public void showBlockingMessage(String message, boolean keepMessage)
	{
		showBlockingMessage(message, layout.POPUPMESSAGE_BOUNDS.x, layout.POPUPMESSAGE_BOUNDS.y,
				layout.POPUPMESSAGE_BOUNDS.width, layout.POPUPMESSAGE_BOUNDS.height, keepMessage);
	}

	public void showBlockingMessage(String message, int x, int y, int w, int h, boolean keepMessage)
	{
		if (getPlayer() != null)
			((GFXUISelector) getPlayer().getSelector()).deactivate();
		message = message.replaceAll("XXX", "\n");
		showTextBox(message, x, y, w, h, keepMessage);
	}

	@Override
	public void showSystemMessage(String x)
	{
		showBlockingMessage(x, false);
	}

	public void showImportantMessage(String message)
	{
		showBlockingMessage(message);
	}

	@Override
	public boolean promptChat(String message)
	{
		if (getPlayer() != null && getPlayer().getSelector() != null)
			((GFXUISelector) getPlayer().getSelector()).deactivate();
		message = message.replaceAll("XXX", "\n");
		return promptChat(message, layout.PROMPTBOX_BOUNDS.x, layout.PROMPTBOX_BOUNDS.y, layout.PROMPTBOX_BOUNDS.width,
				layout.PROMPTBOX_BOUNDS.height);
	}

	public void transferFromExpedition(GoodsCache toCache)
	{
		transferFromExpedition(toCache, -1);
	}

	public void transferFromExpedition(GoodsCache toCache, int destinationMinUnits)
	{
		ItemTransferFunctionality transferFromExpeditionFunctionality = new DualTransferFunctionality(
				destinationMinUnits, 1);
		transferItems("Select the goods to transfer", null, getExpedition(), toCache,
				transferFromExpeditionFunctionality);
	}

	public void transferFromCache(String prompt, GoodType preselectedGoodType, GoodsCache fromCache)
	{
		ItemTransferFunctionality dualTransferFunctionality = new DualTransferFunctionality(-1, -1);
		transferItems(prompt, preselectedGoodType, fromCache, getExpedition(), dualTransferFunctionality);
		if (fromCache.destroyOnEmpty() && fromCache.getItems().size() == 0)
			level.destroyFeature(fromCache);
	}

	public List<Equipment> selectItemsFromExpedition(String prompt, String verb, final Appearance containerAppearance)
	{
		ItemTransferFunctionality selectItemsFunctionality = new SelectFromExpeditionFunctionality(prompt, verb);
		ItemContainer tempOfferContainer = new GoodsCache(true)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public String getDescription()
			{
				return "Offer";
			}

			@Override
			public boolean isPeopleContainer()
			{
				return false;
			}

			@Override
			public boolean requiresUnitsToContainItems()
			{
				return false;
			}

			@Override
			public Appearance getAppearance()
			{
				return containerAppearance;
			}
		};

		ItemContainer tempInventoryContainer = new GoodsCache(true)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public String getDescription()
			{
				return "Inventory";
			}

			@Override
			public boolean isPeopleContainer()
			{
				return true;
			}

			@Override
			public boolean requiresUnitsToContainItems()
			{
				return false;
			}

			@Override
			public Appearance getAppearance()
			{
				return getExpedition().getDialogAppearance();
			}
		};

		for (Equipment equipment : getExpedition().getInventory())
		{
			tempInventoryContainer.addItem((ExpeditionItem) equipment.getItem(), equipment.getQuantity());
		}

		transferItems("-", GoodType.TRADE_GOODS, tempInventoryContainer, tempOfferContainer, selectItemsFunctionality);
		return tempOfferContainer.getItems();
	}

	/**
	 * 
	 * @param preselectedGoodType
	 * @param prompt
	 * @param from
	 * @param to
	 * @param itemTransferFunctionality
	 * @param fromExpedition
	 */
	public void transferItems(String prompt, GoodType preselectedGoodType, ItemContainer from, ItemContainer to,
			ItemTransferFunctionality itemTransferFunctionality)
	{
		// Change UI Mode
		Equipment.eqMode = true;
		enterScreen();

		int startX = 480 - 40;
		int gapX = 40;

		// Create the good type buttons
		CleanButton peopleButton = new CleanButton(IMG_SMALL_BUTTON_BACK, IMG_SMALL_BUTTON_HOVER_BACK, BTN_PEOPLE,
				HAND_CURSOR);
		peopleButton.setPopupText("People");
		peopleButton.setLocation(startX, 31);
		CleanButton suppliesButton = new CleanButton(IMG_SMALL_BUTTON_BACK, IMG_SMALL_BUTTON_HOVER_BACK, BTN_SUPPLIES,
				HAND_CURSOR);
		suppliesButton.setLocation(startX + gapX * 1, 31);
		suppliesButton.setPopupText("Supplies");
		CleanButton tradeGoodsButton = new CleanButton(IMG_SMALL_BUTTON_BACK, IMG_SMALL_BUTTON_HOVER_BACK,
				BTN_MERCHANDISE, HAND_CURSOR);
		tradeGoodsButton.setLocation(startX + gapX * 2, 31);
		tradeGoodsButton.setPopupText("Trade Goods");
		CleanButton armoryButton = new CleanButton(IMG_SMALL_BUTTON_BACK, IMG_SMALL_BUTTON_HOVER_BACK, BTN_WEAPONS,
				HAND_CURSOR);
		armoryButton.setLocation(startX + gapX * 3, 31);
		armoryButton.setPopupText("Armory");
		CleanButton livestockButton = new CleanButton(IMG_SMALL_BUTTON_BACK, IMG_SMALL_BUTTON_HOVER_BACK, BTN_LIVESTOCK,
				HAND_CURSOR);
		livestockButton.setLocation(startX + gapX * 4, 31);
		livestockButton.setPopupText("Livestock");

		CleanButton vehiclesButton = new CleanButton(IMG_SMALL_BUTTON_BACK, IMG_SMALL_BUTTON_HOVER_BACK, BTN_VEHICLES,
				HAND_CURSOR);
		vehiclesButton.setLocation(startX + gapX * 5, 31);
		vehiclesButton.setPopupText("Vehicles");

		CleanButton closeButton = new CleanButton(IMG_SMALL_BUTTON_BACK, IMG_SMALL_BUTTON_HOVER_BACK, BTN_CLOSE,
				HAND_CURSOR);
		closeButton.setLocation(startX + gapX * 7, 31);

		si.add(peopleButton);
		si.add(suppliesButton);
		si.add(tradeGoodsButton);
		si.add(armoryButton);
		si.add(livestockButton);
		si.add(vehiclesButton);
		si.add(closeButton);

		// Create the blockingqueue
		BlockingQueue<String> transferFromExpeditionHandler = new LinkedBlockingQueue<String>();

		// Add callback listeners for good type selection
		peopleButton.addActionListener(getStringCallBackActionListener(transferFromExpeditionHandler, "GOOD_TYPE:0"));
		suppliesButton.addActionListener(getStringCallBackActionListener(transferFromExpeditionHandler, "GOOD_TYPE:1"));
		tradeGoodsButton
				.addActionListener(getStringCallBackActionListener(transferFromExpeditionHandler, "GOOD_TYPE:2"));
		armoryButton.addActionListener(getStringCallBackActionListener(transferFromExpeditionHandler, "GOOD_TYPE:3"));
		livestockButton
				.addActionListener(getStringCallBackActionListener(transferFromExpeditionHandler, "GOOD_TYPE:4"));
		vehiclesButton.addActionListener(getStringCallBackActionListener(transferFromExpeditionHandler, "GOOD_TYPE:5"));

		// Add callback listeners for screen close
		closeButton.addActionListener(getStringCallBackActionListener(transferFromExpeditionHandler, "BREAK"));

		// Add a general callbacklistener for keyboard
		CallbackKeyListener<String> cbkl = new CallbackKeyListener<String>(transferFromExpeditionHandler)
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				try
				{
					CharKey x = new CharKey(SwingSystemInterface.charCode(e));
					if (x.code == CharKey.SPACE || x.code == CharKey.ENTER)
					{
						handler.put("CONFIRM_TRANSFER");
					}
					else if (x.code == CharKey.ESC)
					{
						handler.put("BREAK");
					} /*
						 * else if (x.isLeftArrow()){
						 * handler.put("GOOD_TYPE:<"); } else if
						 * (x.isRightArrow()){ handler.put("GOOD_TYPE:>"); }
						 */
					else if (x.code == CharKey.N1)
					{
						handler.put("GOOD_TYPE:0");
					}
					else if (x.code == CharKey.N2)
					{
						handler.put("GOOD_TYPE:1");
					}
					else if (x.code == CharKey.N3)
					{
						handler.put("GOOD_TYPE:2");
					}
					else if (x.code == CharKey.N4)
					{
						handler.put("GOOD_TYPE:3");
					}
					else if (x.code == CharKey.N5)
					{
						handler.put("GOOD_TYPE:4");
					}
					else if (x.code == CharKey.N6)
					{
						handler.put("GOOD_TYPE:5");
					}
				}
				catch (InterruptedException e1)
				{
				}
			}
		};
		si.addKeyListener(cbkl);

		// Create the gridbox component. Send the transferFromExpeditionHandler
		// to allow item selection with both mouse and keyb
		TransferBorderGridBox menuBox = new TransferBorderGridBox(BORDER1, BORDER2, BORDER3, BORDER4, si,
				COLOR_WINDOW_BACKGROUND, COLOR_BORDER_IN, COLOR_BORDER_OUT, borderSize, 6, 9, 12, STANDARD_ITEM_HEIGHT,
				STANDARD_ITEM_WIDTH, 2, 6, IMG_BOX, null, from, to, transferFromExpeditionHandler,
				itemTransferFunctionality);
		menuBox.setCursor(si.getCursor());
		menuBox.setBounds(16, 16, 768, 480);

		menuBox.setTitle(itemTransferFunctionality.getTitle(from, to));
		menuBox.setLegend(prompt);

		int typeChoice = 0;
		int selectedIndex;
		GoodType[] goodTypes = GoodType.getGoodTypes();
		if (preselectedGoodType != null)
		{
			int index = 0;
			for (GoodType goodType : goodTypes)
			{
				if (preselectedGoodType.equals(goodType))
					typeChoice = index;
				index++;
			}
		}

		Map<GoodType, List<ExpeditionItem>> expeditionItemMap = null;
		expeditionItemMap = new HashMap<GoodType, List<ExpeditionItem>>();

		for (GoodType goodType : goodTypes)
		{
			// expeditionGoodsMap.put(goodType,
			// getExpedition().getGoods(goodType, true));
			expeditionItemMap.put(goodType, new ArrayList<ExpeditionItem>());
			List<Equipment> fromGoods = from.getGoods(goodType);
			for (Equipment fromGood : fromGoods)
			{
				expeditionItemMap.get(goodType).add((ExpeditionItem) fromGood.getItem());
			}

			List<Equipment> toGoods = to.getGoods(goodType);
			for (Equipment toGood : toGoods)
			{
				if (expeditionItemMap.get(goodType) == null)
				{
					expeditionItemMap.put(goodType, new ArrayList<ExpeditionItem>());
				}
				if (!expeditionItemMap.get(goodType).contains((ExpeditionItem) toGood.getItem()))
				{
					expeditionItemMap.get(goodType).add((ExpeditionItem) toGood.getItem());
				}

			}
		}

		while (true)
		{
			menuBox.setHoverDisabled(false);
			// Select data to draw and draw it
			int currentPage = menuBox.getCurrentPage();
			List<ExpeditionItem> inventory = null;
			if (typeChoice < goodTypes.length)
			{
				inventory = expeditionItemMap.get(goodTypes[typeChoice]);
			}

			Vector<CustomGFXMenuItem> menuItems = new Vector<CustomGFXMenuItem>();
			for (ExpeditionItem item : inventory)
			{
				switch (itemTransferFunctionality.getMenuItemType())
				{
				case CACHE:
					menuItems.add(new CacheCustomGFXMenuItem(item, from, to));
					break;
				case STORE:
					menuItems.add(new StoreCustomGFXMenuItem(item,
							((BuyItemsFunctionality) itemTransferFunctionality).store, getExpedition()));
					break;
				}

			}
			Collections.sort(menuItems, ITEMS_COMPARATOR);
			menuBox.setMenuItems(menuItems);
			if (menuBox.isValidPage(currentPage))
			{
				menuBox.setCurrentPage(currentPage);
				menuBox.updatePageButtonStatus();
			}
			else
			{
				currentPage = menuBox.getPages() - 1;
				menuBox.setCurrentPage(currentPage);
				menuBox.updatePageButtonStatus();
			}

			int boxX = startX + typeChoice * gapX - 21;
			menuBox.draw(true, boxX);

			// Wait for item or command selection
			String command = null;
			while (command == null)
			{
				try
				{
					command = transferFromExpeditionHandler.take();
				}
				catch (InterruptedException ie)
				{
				}
			}
			menuBox.setHoverDisabled(true);

			String[] commandParts = command.split(":");
			if (commandParts[0].equals("GOOD_TYPE"))
			{
				int currentType = typeChoice;
				// Change the good type
				if (commandParts[1].equals("<"))
				{
					typeChoice--;
					if (typeChoice == -1)
						typeChoice = 0;
					if (currentType != typeChoice)
					{
						menuBox.resetSelection();
					}
					continue;
				}
				else if (commandParts[1].equals(">"))
				{
					typeChoice++;
					if (typeChoice == goodTypes.length)
						typeChoice = goodTypes.length - 1;
					if (currentType != typeChoice)
					{
						menuBox.resetSelection();
					}
					continue;
				}
				else
				{
					typeChoice = Integer.parseInt(commandParts[1]);
					if (currentType != typeChoice)
					{
						menuBox.resetSelection();
					}
					continue;
				}
			}
			else if (commandParts[0].equals("BREAK"))
			{
				if (!itemTransferFunctionality.validateBreak(from, to))
				{
					transferFromExpeditionHandler.clear();
					continue;
				}
				break;
			}
			else if (commandParts[0].equals("SELECT_UNIT"))
			{
				selectedIndex = Integer.parseInt(commandParts[1]);
				menuBox.selectUnit(selectedIndex);
			}
			else if (commandParts[0].equals("CHANGE_PAGE"))
			{
				// Do nothing other than changing page and redrawing
			}
		}

		si.remove(peopleButton);
		si.remove(suppliesButton);
		si.remove(tradeGoodsButton);
		si.remove(armoryButton);
		si.remove(livestockButton);
		si.remove(vehiclesButton);
		si.remove(closeButton);
		si.removeKeyListener(cbkl);
		menuBox.kill();
		Equipment.eqMode = false;
		leaveScreen();
		si.loadLayer(getUILayer());
	}

	public void beforeDrawLevel()
	{
		if (getExpedition().getMovementMode() == MovementMode.SHIP)
			setFlipEnabled(false);
		else
			setFlipEnabled(true);
	}

	@Override
	public synchronized void beforeRefresh()
	{
		drawStatus();

	}

	@Override
	public void setPlayer(Player player)
	{
		super.setPlayer(player);
		mainUnitMenuItem = new SimplifiedUnitGFXMenuItem(new Equipment(getExpedition().getLeaderUnit(), 1));
	}

	private Expedition getExpedition()
	{
		return (Expedition) getPlayer();
	}

	public void drawStatus()
	{
		// Get all data
		Expedition statsExpedition = getExpedition();
		AbstractCell currentCell = getExpedition().getLocation().getMapCell(getExpedition().getPosition());
		Calendar gameTime = ((ExpeditionGame) player.getGame()).getGameTime();
		Pair<String, String> locationDescription = getExpedition().getLocation().getLocationDescription();
		Pair<String, String> locationLabels = getExpedition().getLocation().getLocationLabels();
		int totalShips = statsExpedition.getTotalShips();
		/*
		 * String hourStr = gameTime.get(Calendar.HOUR) == 0 ? "12" :
		 * gameTime.get(Calendar.HOUR)+""; String minuteStr =
		 * gameTime.get(Calendar.MINUTE) < 10 ?
		 * "0"+gameTime.get(Calendar.MINUTE) : gameTime.get(Calendar.MINUTE)+"";
		 * String amPmStr = gameTime.get(Calendar.AM_PM) == Calendar.AM ? "AM" :
		 * "PM";
		 */

		String generalStr = MessengerService.getTimeDescriptionFromHour(gameTime.get(Calendar.HOUR_OF_DAY));
		// Define showing
		boolean showWind = statsExpedition.getMovementMode().equals(MovementMode.SHIP);
		boolean showCurrent = false;
		boolean showHeading = statsExpedition.getMovementMode().equals(MovementMode.SHIP);
		boolean isOnMacroLevel = statsExpedition.getLevel() instanceof ExpeditionMacroLevel;

		// Compose labels
		String ui_date = ExpeditionUserInterface.months[gameTime.get(Calendar.MONTH)] + " "
				+ gameTime.get(Calendar.DATE);
		String ui_time = generalStr;
		String ui_gold = getExpedition().getAccountedGold() + " Gold";
		/**
		 * What difference does the movement type make? food consumption is food consumption ???
		 */
		String ui_food = isOnMacroLevel ? statsExpedition.getProjectedFoodDays()+ " days of food" : "";
		//String ui_food = isOnMacroLevel ? statsExpedition.getOffshoreFoodDays() + " days of food" : "";
		String ui_water = isOnMacroLevel ? statsExpedition.getProjectedWaterDays() + " days of water" : "";
		String ui_foodModifier = TemperatureRules.getTemperatureFoodModifierString(
				getExpedition().getLocation().getTemperature()) + (statsExpedition.isForaging() ? " (foraging)" : "");
		String ui_carrying;
		if (getExpedition().getLevel() instanceof ExpeditionMicroLevel)
			ui_carrying = statsExpedition.getOffshoreCurrentlyCarrying() + "%";
		else
			ui_carrying = statsExpedition.getCurrentlyCarrying() + "%";
		String ui_morale = statsExpedition.getMoraleDescription();
		String ui_armed = statsExpedition.isArmed() ? " (Armed)" : "";
		String ui_locationDescription = getExpedition().getLocation().getDescription() + ", "
				+ gameTime.get(Calendar.YEAR);
		String ui_terrainDescription = currentCell.getDescription();
		// String ui_debug = "X " +statsExpedition.getPosition().x +" Y
		// "+statsExpedition.getPosition().y+" Scale
		// "+GlobeMapModel.getSingleton().getLongitudeScale(statsExpedition.getPosition().y);
		String ui_debug = "Whata " + statsExpedition.getPosition().x + "," + statsExpedition.getPosition().y;
		String ui_weatherDescription = getExpedition().getLocation().getWeather().getDescription();
		String ui_temperatureDescription = getExpedition().getLocation().getTemperatureDescription();
		String ui_windDirection = getExpedition().getLocation().getWindDirection().getAbbreviation();
		String ui_headingDirection = getExpedition().getHeading().getAbbreviation();
		String ui_bearing;
		if (getExpedition().getMovementMode() == MovementMode.SHIP)
		{
			if (getExpedition().isAnchored())
			{
				ui_bearing = "Anchored";
			}
			else
			{
				ui_bearing = getExpedition().getSailingPoint().getDescription();
			}
		}
		else
		{
			ui_bearing = statsExpedition.getMovementMode().getDescription();
		}
		String ui_movementSpeed = "";
		if (isOnMacroLevel)
		{
			if (statsExpedition.isAnchored())
			{
				ui_movementSpeed = "";
			}
			else
			{
				ui_movementSpeed = getExpedition().getMovementSpeed().getDescription();
			}
		}

		String ui_shipStatus;
		if (getExpedition().getMovementMode() == MovementMode.SHIP && totalShips > 0)
		{
			if (totalShips == 1)
			{
				ui_shipStatus = "A ship (" + statsExpedition.getShipHealth() + "%)";
			}
			else
			{
				ui_shipStatus = totalShips + " ships (" + statsExpedition.getShipHealth() + "%)";
			}
		}
		else
		{
			ui_shipStatus = "";
		}
		//String ui_water = "WATER: <0>";
		String ui_windStrength = "<Strenght>";
		String ui_currentDirection = "<D>";
		String ui_currentStrength = "<Strength>";
		String ui_seaDays = statsExpedition.getDaysOnSea() > 0 ? statsExpedition.getDaysOnSea() + " days on sea" : "";

		int leftColumnX = 1;
		int leftColumnY = 3;

		// Draw
		si.setColor(getUILayer(), Color.WHITE);

		si.printAtPixel(getUILayer(), 200, 40, ui_debug);

		// Left Column
		si.printAtPixel(getUILayer(), layout.POS_WEATHER.x, layout.POS_WEATHER.y, ui_weatherDescription);
		si.printAtPixel(getUILayer(), layout.POS_TEMPERATURE.x, layout.POS_TEMPERATURE.y, ui_temperatureDescription);
		si.printAtPixel(getUILayer(), layout.POS_TERRAIN.x, layout.POS_TERRAIN.y, ui_terrainDescription);

		if (showWind)
		{
			si.printAtPixel(getUILayer(), layout.POS_WIND_TITLE.x, layout.POS_WIND_TITLE.y, "WIND", TITLE_COLOR);
			si.printAtPixel(getUILayer(), layout.POS_WIND.x, layout.POS_WIND.y, ui_windDirection);
			// si.printAtPixel(line2+2, 6, ui_windStrength); TODO: Implement
		}

		if (showCurrent)
		{
			// si.printAtPixel(leftColumnX, 5, "CURRENT", TITLE_COLOR); TODO:
			// Implement
			// si.printAtPixel(leftColumnX+9, 5, ui_currentDirection); TODO:
			// Implement
			// si.printAtPixel(leftColumnX+2, 6, ui_currentStrength); TODO:
			// Implement
		}

		si.printAtPixel(getUILayer(), layout.POS_LAT_TITLE.x, layout.POS_LAT_TITLE.y, locationLabels.getA(),
				TITLE_COLOR);
		si.printAtPixel(getUILayer(), layout.POS_LAT.x, layout.POS_LAT.y, locationDescription.getA());
		si.printAtPixel(getUILayer(), layout.POS_LONG_TITLE.x, layout.POS_LONG_TITLE.y, locationLabels.getB(),
				TITLE_COLOR);
		si.printAtPixel(getUILayer(), layout.POS_LONG.x, layout.POS_LONG.y, locationDescription.getB());

		if (showHeading)
		{
			si.printAtPixel(getUILayer(), layout.POS_HEADING_TITLE.x, layout.POS_HEADING_TITLE.y, "HEADING",
					TITLE_COLOR);
			si.printAtPixel(getUILayer(), layout.POS_HEADING.x, layout.POS_HEADING.y, ui_headingDirection);
			si.printAtPixel(getUILayer(), layout.POS_BEARING.x, layout.POS_BEARING.y, ui_bearing);
		}
		si.printAtPixel(getUILayer(), layout.POS_SPEED.x, layout.POS_SPEED.y, ui_movementSpeed);
		if (isOnMacroLevel)
		{
			si.printAtPixel(getUILayer(), layout.POS_BURDEN_TITLE.x, layout.POS_BURDEN_TITLE.y, "Burden", TITLE_COLOR);
			si.printAtPixel(getUILayer(), layout.POS_BURDEN.x, layout.POS_BURDEN.y, ui_carrying);
			si.printAtPixel(getUILayer(), layout.POS_MOOD_TITLE.x, layout.POS_MOOD_TITLE.y, "Mood", TITLE_COLOR);
			si.printAtPixel(getUILayer(), layout.POS_MOOD.x, layout.POS_MOOD.y, ui_morale);
			si.printAtPixel(getUILayer(), layout.POS_SEADAYS.x, layout.POS_SEADAYS.y, ui_seaDays);
			si.printAtPixel(getUILayer(), layout.POS_SUPPLIES_TITLE.x, layout.POS_SUPPLIES_TITLE.y, "Supplies",
					TITLE_COLOR);
			si.printAtPixel(getUILayer(), layout.POS_SUPPLIES.x, layout.POS_SUPPLIES.y, ui_food);
			si.printAtPixel(getUILayer(), layout.POS_SUPPLIES_MOD.x, layout.POS_SUPPLIES_MOD.y, ui_foodModifier);
			si.printAtPixel(getUILayer(), layout.POS_WATER.x, layout.POS_WATER.y, ui_water);
			si.printAtPixel(getUILayer(), layout.POS_GOLD.x, layout.POS_GOLD.y, ui_gold);

			si.drawImage(getUILayer(), layout.POS_MOOD_ICON.x, layout.POS_MOOD_ICON.y,
					MORALE_IMAGES[statsExpedition.getMorale()]);
		}

		// Middle
		si.printCentered(getUILayer(), layout.POS_LOCATION_Y, ui_locationDescription, Color.WHITE);
		si.printAtPixel(getUILayer(), layout.POS_DATE.x, layout.POS_DATE.y, ui_date);
		si.printAtPixel(getUILayer(), layout.POS_TIME.x, layout.POS_TIME.y, ui_time);

		expeditionVehicleItems.clear();
		for (Vehicle expeditionVehicle : statsExpedition.getCurrentVehicles())
		{
			expeditionVehicleItems.add(new IconVehicleCustomGFXMenuItem(expeditionVehicle, false));
		}

		expeditionUnitItems.clear();
		expeditionUnitsTemp.clear();
		expeditionUnitsTemp.addAll(statsExpedition.getGoods(GoodType.PEOPLE));
		int horses = statsExpedition.getItemCountBasic("HORSE");
		if (horses > 0)
		{
			Equipment forgedEquipment = new Equipment(HORSES_ITEM, horses);
			expeditionUnitsTemp.add(forgedEquipment);
		}
		resumedEquipments.clear();

		for (Equipment expeditionUnit : expeditionUnitsTemp)
		{
			String basicId = ((ExpeditionItem) expeditionUnit.getItem()).getBaseID();
			Equipment resumedEquipment = resumedEquipments.get(basicId);
			if (resumedEquipment == null)
			{
				resumedEquipment = new Equipment(expeditionUnit.getItem(), expeditionUnit.getQuantity());
				resumedEquipments.put(basicId, resumedEquipment);
				expeditionUnitItems.add(new IconUnitCustomGFXMenuItem(resumedEquipment, false, true));
			}
			else
			{
				resumedEquipment.setQuantity(resumedEquipment.getQuantity() + expeditionUnit.getQuantity());
			}
		}
		// expeditionUnitItems.add(0, mainUnitMenuItem);
		if (FriarTutorial.active)
		{
			tutorial.setVisible();
		}
		else
		{
			unitsMenuBox.setMenuItems(expeditionUnitItems);
			unitsMenuBox.setUsedBuffer(1);
			Collections.sort(expeditionUnitItems, ITEMS_COMPARATOR);
			unitsMenuBox.draw(false);

			vehiclesMenuBox.setMenuItems(expeditionVehicleItems);
			vehiclesMenuBox.setUsedBuffer(1);
			vehiclesMenuBox.draw(false);
		}

		si.printAtPixel(getUILayer(), layout.POS_VERSION.x, layout.POS_VERSION.y, ExpeditionGame.getVersion());
	}

	
	public void init(SwingSystemInterface psi, String title, UserCommand[] gameCommands, Properties UIProperties,
			Assets assets, Action target)
	{
		super.init(psi, title, gameCommands, UIProperties, assets, target);
		layout = new Layout();
		layout.initialize(UIProperties);
		ExpeditionCleanButton.init(si, assets);

		psi.setFont(getUILayer(), getFontAsset("FNT_TEXT"));
		psi.setFont(getMapLayer(), getFontAsset("FNT_TEXT"));

		JLabel legendLabel = new JLabel();
		legendLabel.setFont(getFontAsset("FNT_TEXT"));
		legendLabel.setVisible(false);
		legendLabel.setForeground(Color.WHITE);
		legendLabel.setSize(PropertyFilters.inte(UIProperties.getProperty("WINDOW_WIDTH")), 15);
		si.add(legendLabel);
		CleanButton.init(legendLabel, si);

		HAND_CURSOR = getCursorAsset("HAND_CURSOR");
		POINTER_CURSOR = getCursorAsset("POINTER_CURSOR");

		unitsMenuBox = new GridBox(si, 42, 42, 5, 5)
		{
			;
			@Override
			public int getDrawingLayer()
			{
				return getUILayer();
			}

			@Override
			protected Cursor getHandCursor()
			{
				return HAND_CURSOR;
			}
		};
		unitsMenuBox.setLocation(PropertyFilters.getPoint(UIProperties.getProperty("POS_UNITS_BOX")));

		vehiclesMenuBox = new GridBox(si, 42, 42, 5, 3)
		{
			;
			@Override
			public int getDrawingLayer()
			{
				return getUILayer();
			}

			@Override
			protected Cursor getHandCursor()
			{
				return HAND_CURSOR;
			}
		};
		vehiclesMenuBox.setLocation(PropertyFilters.getPoint(UIProperties.getProperty("POS_VEHICLES_BOX")));

		BTN_SPLIT_UP = assets.getImageAsset("BTN_SPLIT_UP");
		BTN_SPLIT_DOWN = assets.getImageAsset("BTN_SPLIT_DOWN");
		BTN_SPLIT_UP_HOVER = assets.getImageAsset("BTN_SPLIT_UP_HOVER");
		BTN_SPLIT_DOWN_HOVER = assets.getImageAsset("BTN_SPLIT_DOWN_HOVER");

		BTN_SPLIT_LEFT = assets.getImageAsset("BTN_SPLIT_LEFT");
		BTN_SPLIT_RIGHT = assets.getImageAsset("BTN_SPLIT_RIGHT");
		BTN_SPLIT_LEFT_HOVER = assets.getImageAsset("BTN_SPLIT_LEFT_HOVER");
		BTN_SPLIT_RIGHT_HOVER = assets.getImageAsset("BTN_SPLIT_RIGHT_HOVER");

		BTN_SPLIT_LEFT_ALL = assets.getImageAsset("BTN_SPLIT_LEFT_ALL");
		BTN_SPLIT_RIGHT_ALL = assets.getImageAsset("BTN_SPLIT_RIGHT_ALL");
		BTN_SPLIT_LEFT_HOVER_ALL = assets.getImageAsset("BTN_SPLIT_LEFT_HOVER_ALL");
		BTN_SPLIT_RIGHT_HOVER_ALL = assets.getImageAsset("BTN_SPLIT_RIGHT_HOVER_ALL");

		BTN_PEOPLE = assets.getImageAsset("BTN_PEOPLE");
		BTN_SUPPLIES = assets.getImageAsset("BTN_SUPPLIES");
		BTN_MERCHANDISE = assets.getImageAsset("BTN_MERCHANDISE");
		BTN_WEAPONS = assets.getImageAsset("BTN_WEAPONS");
		BTN_LIVESTOCK = assets.getImageAsset("BTN_LIVESTOCK");
		BTN_VEHICLES = assets.getImageAsset("BTN_VEHICLES");
		BTN_CLOSE = assets.getImageAsset("BTN_CLOSE");

		IMG_BOX = assets.getImageAsset("IMG_BOX");
		IMG_SMALL_BUTTON_BACK = assets.getImageAsset("IMG_SMALL_BUTTON_BACK");
		IMG_SMALL_BUTTON_HOVER_BACK = assets.getImageAsset("IMG_SMALL_BUTTON_HOVER_BACK");

		MORALE_IMAGES = new Image[11];
		for (int i = 0; i <= 10; i++)
		{
			MORALE_IMAGES[i] = assets.getImageAsset("IMG_MORALE_" + i);
		}

		FLOWERS = new HashMap<String, Image>();
		FLOWERS.put("FLW_ACONITUM", assets.getImageAsset("FLW_ACONITUM"));

		HORSES_ITEM = ItemFactory.createItem("HORSE");
		addornedTextArea.setCursor(si.getCursor());

		sfxQueue = new LinkedBlockingQueue<String>();
		EffectsServer sfxServer = new EffectsServer(si, sfxQueue);

		tutorial = new TutorialComponent(si, assets, layout, this);
		si.add(tutorial);
	}

	private BlockingQueue<String> sfxQueue;

	@Override
	public void showBattleScene(String battleTitle, List<Equipment> attackingUnits, List<Equipment> defendingUnits)
	{
		clearTextBox();
		((GFXUISelector) getPlayer().getSelector()).deactivate();

		// Show the armies
		List<GFXMenuItem> attackingMenuItems = new ArrayList<GFXMenuItem>();
		for (Equipment equipment : attackingUnits)
		{
			attackingMenuItems.add(new IconUnitCustomGFXMenuItem(equipment, false, false));
		}

		List<GFXMenuItem> defendingMenuItems = new ArrayList<GFXMenuItem>();

		for (Equipment equipment : defendingUnits)
		{
			defendingMenuItems.add(new IconUnitCustomGFXMenuItem(equipment, true, false));
		}
		si.saveLayer(getUILayer());
		BorderedGridBox defendantsGridBox = createBorderedGridBox(28, 38, 5, 5);
		defendantsGridBox.setMenuItems(defendingMenuItems);
		defendantsGridBox.setBounds(408, 94, 245, 234);
		defendantsGridBox.setTitle("Defenders");
		defendantsGridBox.setUsedBuffer(1);
		defendantsGridBox.draw(false);

		BorderedGridBox combatantsGridBox = createBorderedGridBox(28, 38, 5, 5);
		combatantsGridBox.setMenuItems(attackingMenuItems);
		combatantsGridBox.setBounds(143, 94, 245, 234);
		combatantsGridBox.setTitle("Attackers");
		combatantsGridBox.draw(false);

		si.commitLayer(getUILayer());

		// si.waitKeyOrClick(CharKey.SPACE);
		showBlockingMessage(battleTitle, 140, 350, 520, 200, false);

		si.loadLayer(getUILayer());

		combatantsGridBox.kill();
		defendantsGridBox.kill();

		// Show battlescape

		int xBase = 192;
		int yBase = 48;
		si.drawImage(getUILayer(), 168, yBase - 24, getImageAsset("BATTLE_BACKGROUND"));
		int gridX = 0;
		int gridY = 0;
		for (Equipment equipment : attackingUnits)
		{
			GFXAppearance appearance = (GFXAppearance) ((ExpeditionItem) equipment.getItem()).getDialogAppearance();
			for (int i = 0; i < equipment.getQuantity(); i++)
			{
				gridX = Util.rand(0, 15);
				gridY = Util.rand(0, 12);
				si.drawImage(getUILayer(), xBase + gridX * 24, yBase + gridY * 24, appearance.getImage());
				gridY++;
				if (gridY > 12)
				{
					gridX++;
					gridY = 0;
				}
			}
		}

		gridX = 15;
		gridY = 0;

		for (Equipment equipment : defendingUnits)
		{
			GFXAppearance appearance = (GFXAppearance) ((ExpeditionItem) equipment.getItem()).getDialogAppearance();
			Image img = ImageUtils.vFlip((BufferedImage) appearance.getImage());
			for (int i = 0; i < equipment.getQuantity(); i++)
			{
				gridX = Util.rand(0, 15);
				gridY = Util.rand(0, 12);
				si.drawImage(getUILayer(), xBase + gridX * 24, yBase + gridY * 24, img);
				gridY++;
				if (gridY > 12)
				{
					gridX--;
					gridY = 0;
				}
			}
		}

		si.commitLayer(getUILayer());

	}

	@Override
	public void showBattleResults(List<Equipment> originalAttackingUnits, List<Equipment> originalDefendingUnits,
			String battleTitle, AssaultOutcome attackerRangedAttackOutcome, AssaultOutcome defenderRangedAttackOutcome,
			Pair<AssaultOutcome, AssaultOutcome> mountedAttackOutcome,
			Pair<AssaultOutcome, AssaultOutcome> meleeAttackOutcome, int attackerScore, int defenderScore)
	{
		List<String> messages = CommonUI.getBattleResultsString(originalAttackingUnits, originalDefendingUnits,
				battleTitle, attackerRangedAttackOutcome, defenderRangedAttackOutcome, mountedAttackOutcome,
				meleeAttackOutcome, attackerScore, defenderScore);
		messages.remove(0); // Ignore the first message, since it's replaced
							// with the formation grid
		int i = 0;
		for (String message : messages)
		{
			message = message.replaceAll("XXX", "\n");
			showTextBox(message, getLayout().BATTLE_OUTCOME_BOUNDS.x, getLayout().BATTLE_OUTCOME_BOUNDS.y,
					getLayout().BATTLE_OUTCOME_BOUNDS.width, getLayout().BATTLE_OUTCOME_BOUNDS.height,
					i < messages.size() - 1);
			i++;
		}
	}

	@Override
	/**
	 * Shows the list of units and the message and prompts for confirmation
	 */
	public boolean promptUnitList(List<Equipment> unitList, String title, String prompt)
	{
		Equipment.eqMode = true;
		clearTextBox();
		final ExpeditionOryxUI this_ = this;
		BorderedGridBox cacheBox = new BorderedGridBox(BORDER1, BORDER2, BORDER3, BORDER4, si, COLOR_WINDOW_BACKGROUND,
				COLOR_BORDER_IN, COLOR_BORDER_OUT, borderSize, 6, 9, 12, STANDARD_ITEM_HEIGHT, STANDARD_ITEM_WIDTH, 2,
				4, null, null, BTN_SPLIT_UP, BTN_SPLIT_DOWN, ExpeditionOryxUI.BTN_SPLIT_UP_HOVER,
				ExpeditionOryxUI.BTN_SPLIT_DOWN_HOVER, HAND_CURSOR)
		{
			KeyListener changePageKeyListener;

			@Override
			protected void customInit()
			{
				super.customInit();
				avPagButton.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						avPag();
						draw(true);
					}
				});
				rePagButton.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						rePag();
						draw(true);
					}
				});
				changePageKeyListener = new KeyAdapter()
				{
					@Override
					public void keyPressed(KeyEvent e)
					{
						if (e.getKeyCode() == KeyEvent.VK_UP)
						{
							rePag();
							draw(true);
						}
						else if (e.getKeyCode() == KeyEvent.VK_DOWN)
						{
							avPag();
							draw(true);
						}
					}
				};
				si.addKeyListener(changePageKeyListener);
			}

			protected Cursor getDefaultCursor()
			{
				return this_.getDefaultCursor();
			}

			protected Cursor getHandCursor()
			{
				return this_.getHandCursor();
			}

			@Override
			public int getDrawingLayer()
			{
				return getUILayer();
			}

			@Override
			public void kill()
			{
				super.kill();
				si.removeKeyListener(changePageKeyListener);
			}
		};
		cacheBox.setCursor(si.getCursor());
		cacheBox.setBounds(160, 16, 624, 360);

		Vector menuItems = new Vector();
		for (Equipment item : unitList)
		{
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
	public List<Building> createBuildingPlan()
	{
		List<Building> knownBuildings = getExpedition().getKnownBuildings();
		List<BuildingCustomGFXMenuItem> buildingMenuItems = new ArrayList<BuildingCustomGFXMenuItem>();
		for (Building building : knownBuildings)
		{
			buildingMenuItems.add(new BuildingCustomGFXMenuItem(building));
		}

		CleanButton closeButton = new CleanButton(IMG_SMALL_BUTTON_BACK, IMG_SMALL_BUTTON_HOVER_BACK, BTN_CLOSE,
				HAND_CURSOR);
		closeButton.setLocation(730, 31);

		BlockingQueue<String> selectionHandler = new LinkedBlockingQueue<String>();
		closeButton.addActionListener(getStringCallBackActionListener(selectionHandler, "CANCEL"));

		BuildingPlanBorderGridBox menuBox = new BuildingPlanBorderGridBox(BORDER1, BORDER2, BORDER3, BORDER4, si,
				COLOR_WINDOW_BACKGROUND, COLOR_BORDER_IN, COLOR_BORDER_OUT, borderSize, 6, 9, 12, STANDARD_ITEM_HEIGHT,
				STANDARD_ITEM_WIDTH + 43, 3, 5, selectionHandler, closeButton);
		menuBox.setCursor(si.getCursor());
		menuBox.setBounds(16, 16, 768, 480);
		menuBox.setTitle("Building Plan");
		menuBox.setLegend("Please select the buldings you want to construct");
		menuBox.setMenuItems(buildingMenuItems);
		boolean cancel = false;

		menuBox.draw(true);
		String commandStr = null;
		while (commandStr == null)
		{
			try
			{
				commandStr = selectionHandler.take();
			}
			catch (InterruptedException e)
			{
			}
		}

		cancel = commandStr.equals("CANCEL");

		menuBox.kill();

		si.loadLayer(getUILayer());

		if (!cancel)
		{
			List<Building> buildingPlan = new ArrayList<Building>();
			for (BuildingCustomGFXMenuItem buildingMenuItem : buildingMenuItems)
			{
				for (int i = 0; i < buildingMenuItem.getQuantity(); i++)
				{
					buildingPlan.add(buildingMenuItem.getBuilding());
				}
			}
			return buildingPlan;
		}
		else
		{
			return null;
		}
	}

	@Override
	public void showCityInfo(Town town)
	{
		String townInfo = CommonUI.getTownDescription(town);
		townInfo = townInfo.replaceAll("XXX", "\n");
		printTextBox(townInfo, 40, 20, 720, 260);
	}

	@Override
	public void afterTownAction()
	{
		clearTextBox();
	}

	@Override
	public Cursor getDefaultCursor()
	{
		return POINTER_CURSOR;
	}

	@Override
	public Cursor getHandCursor()
	{
		return HAND_CURSOR;
	}

	@Override
	public void onPlayerDeath()
	{
		super.onPlayerDeath();
	}

	interface ItemTransferFunctionality
	{
		public enum MenuItemType
		{
			CACHE, STORE
		}

		MenuItemType getMenuItemType();

		String getTitle(ItemContainer from, ItemContainer to);

		String getTransferedLegend(int quantity, ExpeditionItem choice, ItemContainer to);

		boolean validateBreak(ItemContainer from, ItemContainer to);

		/**
		 * 
		 * @param from
		 * @param to
		 * @param fromMap
		 *            Optional, only used when cloning inventory. This is a
		 *            temporary map with the would-be contents of the "from"
		 *            container
		 * @param choice
		 * @param quantity
		 * @return
		 */
		boolean validateAndPerformTransfer(ItemContainer from, ItemContainer to, ExpeditionItem choice, int quantity);
	}

	class BuyItemsFunctionality implements ItemTransferFunctionality
	{
		private Store store;

		public BuyItemsFunctionality(Store store)
		{
			this.store = store;
		}

		@Override
		public String getTitle(ItemContainer from, ItemContainer to)
		{
			return store.getOwnerName();
		}

		@Override
		public String getTransferedLegend(int quantity, ExpeditionItem choice, ItemContainer to)
		{
			if (to instanceof Expedition)
				return "You buy " + quantity + " " + choice.getDescription();
			else
				return "You sell " + quantity + " " + choice.getDescription();

		}

		@Override
		public boolean validateAndPerformTransfer(ItemContainer from, ItemContainer to, ExpeditionItem choice,
				int quantity)
		{
			if (to instanceof ItemOffshoreExpeditionContainer)
			{
				// Buying
				StoreItemInfo storeItemInfo = store.getBuyInfo(choice, getExpedition());

				if (quantity > from.getItemCount(choice.getFullID()))
				{
					showBlockingMessage("I don't have that many...");
					return false;
				}

				if (!to.canCarry(choice, quantity))
				{
					showBlockingMessage("Your ships are full!");
					return false;
				}

				if (quantity <= 0)
				{
					return false;
				}

				int gold = storeItemInfo.getPrice() * quantity;

				if (getExpedition().getAccountedGold() >= gold)
				{
					getExpedition().reduceAccountedGold(gold);
					to.addItem(choice, quantity);
					from.reduceQuantityOf(choice, quantity);
					// prompt = "Thank you! Do you need anything else?";
				}
				else
				{
					// prompt = "You can't afford it! Do you need anything
					// else?";
					return false;
				}
				return true;
			}
			else
			{
				// Selling
				if (store.canBuy(choice, quantity))
				{
					int sellPrice = store.getSellPrice(choice);
					// Remove from expedition and add gold
					to.addItem(choice, quantity);
					from.reduceQuantityOf(choice, quantity);
					getExpedition().addAccountedGold(sellPrice * quantity);
					return true;
				}
				else
				{
					showBlockingMessage("I am not interested on " + choice.getDescription());
					return false;
				}
			}
		}
		 
		@Override
		public boolean validateBreak(ItemContainer from, ItemContainer to)
		{
			// Can stop buying anytime
			return true;
		}

		@Override
		public MenuItemType getMenuItemType()
		{
			return MenuItemType.STORE;
		}
	}

	class DualTransferFunctionality implements ItemTransferFunctionality
	{
		int destinationMinExpeditionUnits;
		int sourceMinExpeditionUnits;

		DualTransferFunctionality(int destinationMinExpeditionUnits, int sourceMinExpeditionUnits)
		{
			this.destinationMinExpeditionUnits = destinationMinExpeditionUnits;
			this.sourceMinExpeditionUnits = sourceMinExpeditionUnits;
		}

		@Override
		public String getTransferedLegend(int quantity, ExpeditionItem choice, ItemContainer to)
		{
			return choice.getDescription() + " transfered into " + to.getDescription();
		}

		@Override
		public String getTitle(ItemContainer from, ItemContainer to)
		{
			return "Units and equipment transfer";
		}

		@Override
		public boolean validateBreak(ItemContainer from, ItemContainer to)
		{

			if (destinationMinExpeditionUnits != -1)
			{
				if (to.getTotalUnits() < destinationMinExpeditionUnits)
				{
					if (destinationMinExpeditionUnits == 1)
					{
						showBlockingMessage("At least an unit should be on " + to.getDescription() + ".");
					}
					else
					{
						showBlockingMessage("At least " + destinationMinExpeditionUnits + " unit should be on "
								+ to.getDescription() + ".");
					}
					return false;
				}
			}

			if (sourceMinExpeditionUnits != -1)
			{
				if (from.getTotalUnits() < sourceMinExpeditionUnits)
				{
					if (sourceMinExpeditionUnits == 1)
						showBlockingMessage("At least an unit should be on " + from.getDescription() + ".");
					else
						showBlockingMessage("At least " + sourceMinExpeditionUnits + " unit should be on "
								+ from.getDescription() + ".");
					return false;
				}
			}

			// ----

			if (from instanceof ShipCache)
			{
				if (to.getTotalUnits() > 0)
				{
					if (to.getFoodDays() == 0 && from.getCurrentFood() > 0)
					{
						showBlockingMessage("You must transfer enough supplies for the expedition.");
						return false;
					}
				}
			}

			if (to instanceof ShipCache)
			{
				if (from.getTotalUnits() > 0)
				{
					if (from.getFoodDays() == 0 && to.getCurrentFood() > 0)
					{
						showBlockingMessage("You must keep enough supplies for the expedition.");
						return false;
					}
				}
			}

			// ----

			return true;
		}

		@Override
		public boolean validateAndPerformTransfer(ItemContainer from, ItemContainer to, ExpeditionItem choice,
				int quantity)
		{
			if (choice.getGoodType() == GoodType.VEHICLE)
			{
				showBlockingMessage("You can't abandon " + ((Vehicle) choice).getName());
				return false;
			}

			if (!to.canCarry(choice, quantity))
			{
				showBlockingMessage("Not enough room in the " + to.getDescription());
				return false;
			}

			from.reduceQuantityOf(choice, quantity);

			if (choice instanceof ExpeditionUnit)
			{
				if (from.getCurrentlyCarrying() > 100)
				{
					from.addItem((ExpeditionItem) choice, quantity);
					showBlockingMessage(
							"The expedition can't carry the goods! Be sure to leave enough men on the expedition.");
					return false;
				}
			}
			else if (choice instanceof Mount && from.getCurrentlyCarrying() > 100)
			{
				from.addItem((ExpeditionItem) choice, quantity);
				if (quantity == 1)
				{
					showBlockingMessage("The expedition can't carry the goods without that horse!");
				}
				else
				{
					showBlockingMessage("The expedition can't carry the goods without these horses!");
				}
				return false;
			}
			to.addItem((ExpeditionItem) choice, quantity);

			// --

			if (to.requiresUnitsToContainItems() && !(choice instanceof ExpeditionUnit) && to.getTotalUnits() == 0)
			{
				showBlockingMessage("Someone must receive the goods!");
				return false;
			}

			// --
			return true;
		}

		@Override
		public MenuItemType getMenuItemType()
		{
			return MenuItemType.CACHE;
		}
	}

	class SelectFromExpeditionFunctionality implements ItemTransferFunctionality
	{
		private String prompt;

		SelectFromExpeditionFunctionality(String prompt, String verb)
		{
			this.prompt = prompt;
		}

		@Override
		public String getTransferedLegend(int quantity, ExpeditionItem choice, ItemContainer to)
		{
			return quantity + " " + choice.getDescription() + " selected";
		}

		@Override
		public String getTitle(ItemContainer from, ItemContainer to)
		{
			return prompt;
		}

		@Override
		public boolean validateBreak(ItemContainer from, ItemContainer to)
		{
			return true;
		}

		@Override
		public boolean validateAndPerformTransfer(ItemContainer from, ItemContainer to, ExpeditionItem choice,
				int quantity)
		{
			// Validate at least one man in the expedition
			if (((ExpeditionItem) choice).getGoodType() == GoodType.PEOPLE)
			{
				List<Equipment> allMen = from.getGoods(GoodType.PEOPLE);
				int men = 0;
				for (Equipment man : allMen)
				{
					men += man.getQuantity();
				}
				if (men - quantity <= 0)
				{
					showBlockingMessage("We can't stay all here, at least one should continue.");
					return false;
				}
			}

			to.addItem((ExpeditionItem) choice, quantity);
			from.reduceQuantityOf(choice, quantity);
			return true;
		}

		@Override
		public MenuItemType getMenuItemType()
		{
			return MenuItemType.CACHE;
		}
	}

	@Override
	public void notifyWeatherChange(Weather weather)
	{
		try
		{
			switch (weather)
			{
			case RAIN:
				sfxQueue.put("RAIN 4 8 2 150 1 DARK");
				break;
			case STORM:
				sfxQueue.put("RAIN 6 10 4 300 2 DARK");
				break;
			case GALE_WIND:
				sfxQueue.put("RAIN 6 10 4 500 3 DARK");
				break;
			case HURRICANE:
				sfxQueue.put("RAIN 6 10 4 500 3 DARK");
				break;
			default:
				sfxQueue.put("STOP");
			}
		}
		catch (InterruptedException e)
		{
		}
	}

	@Override
	public void processSave()
	{
		/*
		 * try { sfxQueue.put("RAIN 4 8 2 200 DARK"); } catch
		 * (InterruptedException e) { }
		 */
		if (!player.getGame().canSave())
		{
			level.addMessage("You cannot save your game here!");
			return;
		}

		if (promptChat("Save your game in journal \"" + getPlayer().getName() + "\"?"))
		{
			messageBox.setText("Saving... ");
			si.commitLayer(getUILayer());
			informPlayerCommand(CommandListener.Command.SAVE);
			enterScreen();
		}
	}

	@Override
	public LandingParty selectLandingParty()
	{
		List<LandingParty> landingParties = CommonUI.getLandingParties();
		String[] landingPartiesDescription = new String[landingParties.size()];
		int i = 0;
		for (LandingParty landingParty : landingParties)
		{
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
	public int getUILayer()
	{
		return UI_WIDGETS_LAYER;
	}

	@Override
	public int getMapLayer()
	{
		return MAP_LAYER;
	}

	public int getFeaturesLayer()
	{
		return MAP_LAYER; // 1
	}

	public int getItemsLayer()
	{
		return MAP_LAYER; // 1
	}

	public int getActorsLayer()
	{
		return MAP_LAYER; // 1
	}

	@Override
	public void reactivate()
	{
		unitsMenuBox.reactivate();
		vehiclesMenuBox.reactivate();
	}

	@Override
	protected void enterScreen()
	{
		super.enterScreen();
		clearTextBox();
		unitsMenuBox.setHoverDisabled(true);
		vehiclesMenuBox.setHoverDisabled(true);
	}

	@Override
	protected void leaveScreen()
	{
		super.leaveScreen();
		unitsMenuBox.setHoverDisabled(false);
		vehiclesMenuBox.setHoverDisabled(false);
	}

	public Layout getLayout()
	{
		return layout;
	}

	public void showImageBlockingMessage(String message, String imageIndex)
	{
		showImageBlockingMessage(message, imageIndex, false);
	}

	public void showImageBlockingMessage(String message, String imageIndex, boolean keepMessage)
	{
		showImageBlockingMessage(message, imageIndex, layout.POPUPMESSAGE_BOUNDS.x, layout.POPUPMESSAGE_BOUNDS.y,
				layout.POPUPMESSAGE_BOUNDS.width, layout.POPUPMESSAGE_BOUNDS.height, keepMessage);
	}

	public void showImageBlockingMessage(String message, String imageIndex, int x, int y, int w, int h,
			boolean keepMessage)
	{
		if (getPlayer() != null)
			((GFXUISelector) getPlayer().getSelector()).deactivate();
		message = message.replaceAll("XXX", "\n");
		showImageTextBox(message, imageIndex, x, y, w, h, keepMessage);
	}

	public void showImageTextBox(String text, String imageIndex, int x, int y, int w, int h, final boolean keep)
	{
		final BlockingQueue<String> selectionQueue = new LinkedBlockingQueue<String>();

		final CallbackKeyListener<String> cbkl = new CallbackKeyListener<String>(selectionQueue)
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				try
				{
					CharKey x = new CharKey(SwingSystemInterface.charCode(e));
					if (x.code == CharKey.ENTER || x.code == CharKey.SPACE || x.code == CharKey.ESC)
						handler.put("OK");
				}
				catch (InterruptedException e1)
				{
				}
			}
		};

		final CallbackMouseListener<String> cbml = new CallbackMouseListener<String>(selectionQueue)
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				try
				{
					handler.put("OK");
				}
				catch (InterruptedException e1)
				{
				}
			}
		};

		si.addKeyListener(cbkl);
		si.addMouseListener(cbml);
		addornedTextArea.addMouseListener(cbml);

		printTextBox(text, x, y, w, h);

		si.drawImage(getUILayer(), x + (w / 2) - 48, y - 112, FLOWERS.get("FLW_" + imageIndex.toUpperCase()));
		si.commitLayer(getUILayer());

		Runnable r = new Runnable()
		{
			@Override
			public void run()
			{
				String choice = null;
				while (choice == null)
				{
					try
					{
						choice = selectionQueue.take();
					}
					catch (InterruptedException e1)
					{
					}
				}
				if (!keep)
					clearTextBox();
				si.removeKeyListener(cbkl);
				si.removeMouseListener(cbml);
				addornedTextArea.removeMouseListener(cbml);
			}
		};

		if (SwingUtilities.isEventDispatchThread())
		{
			// To prevent locking, should perform the selection on a separate
			// thread
			new Thread(r).start();
		}
		else
		{
			r.run();
		}
	}
}