package net.slashie.expedition.ui.oryx;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Timer;

import net.slashie.expedition.domain.ExpeditionItem;
import net.slashie.expedition.domain.ExpeditionUnit;
import net.slashie.expedition.domain.GoodType;
import net.slashie.expedition.domain.ItemContainer;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.ui.oryx.ExpeditionOryxUI.ItemTransferFunctionality;
import net.slashie.libjcsi.CharKey;
import net.slashie.serf.ui.Appearance;
import net.slashie.serf.ui.UserInterface;
import net.slashie.serf.ui.oryxUI.AnimatedGFXAppearance;
import net.slashie.serf.ui.oryxUI.GFXAppearance;
import net.slashie.serf.ui.oryxUI.GFXUserInterface;
import net.slashie.serf.ui.oryxUI.SwingSystemInterface;
import net.slashie.utils.swing.BorderedGridBox;
import net.slashie.utils.swing.CallbackActionListener;
import net.slashie.utils.swing.CallbackKeyListener;
import net.slashie.utils.swing.CallbackMouseListener;
import net.slashie.utils.swing.CleanButton;
import net.slashie.utils.swing.GFXMenuItem;

@SuppressWarnings("serial")
public class TransferBorderGridBox extends BorderedGridBox{
	private ExpeditionItem highlight;
	private ItemContainer source;
	private ItemContainer destination;
	private CallbackKeyListener<String> cbkl;
	private CallbackMouseListener<String> cbml;
	private Image goodTypeBox;
	
	// Splitter attributes
	private CleanButton quantitySplitterToSource;
	private CleanButton quantitySplitterToDestination;
	
	private CleanButton quantitySplitterToSourceAll;
	private CleanButton quantitySplitterToDestinationAll;
	
	
	private int sourceCurrentQuantity;
	private int sourceMaximumQuantity;
	
	private int destinationCurrentQuantity;
	private int destinationMaximumQuantity;
	
	private int initialQuantity;
	private KeyListener splitterArrowsListener;
	private boolean kbLaunchedTimer = false;
	private ItemTransferFunctionality itemTransferFunctionality;
	
	public TransferBorderGridBox(
			// Standard parameters, sent to super()
			BufferedImage border1, BufferedImage border2,
			BufferedImage border3, BufferedImage border4,
			SwingSystemInterface g, Color backgroundColor, Color borderIn,
			Color borderOut, int borderWidth, int outsideBound, int inBound,
			int insideBound, int itemHeight, int itemWidth, int gridX,
			int gridY, Image box, 
			CleanButton closeButton, 
			ItemContainer source, ItemContainer destination, 
			BlockingQueue<String> transferSelectionHandler,
			ItemTransferFunctionality itemTransferFunctionality) {
		super(border1, border2, border3, border4, g, backgroundColor, borderIn,
				borderOut, borderWidth, outsideBound, inBound, insideBound, itemHeight,
				itemWidth, gridX, gridY, box, closeButton, ExpeditionOryxUI.BTN_SPLIT_UP, ExpeditionOryxUI.BTN_SPLIT_DOWN, ExpeditionOryxUI.BTN_SPLIT_UP_HOVER, ExpeditionOryxUI.BTN_SPLIT_DOWN_HOVER, ExpeditionOryxUI.HAND_CURSOR);
		this.itemTransferFunctionality = itemTransferFunctionality;
		initializeSplitters();
		this.goodTypeBox = box;
		this.source = source;
		this.destination = destination;
		
		final int pageElements = gridX * gridY;
		
		cbkl = new CallbackKeyListener<String>(transferSelectionHandler){
			@Override
			public void keyPressed(KeyEvent e) {
				if (hoverDisabled)
					return;
				try {
					int code = SwingSystemInterface.charCode(e);
					if (code == CharKey.UARROW){
						rePag();
						handler.put("CHANGE_PAGE");
					} else if (code == CharKey.DARROW) {
						avPag();
						handler.put("CHANGE_PAGE");
					} else if (code >= CharKey.A && code <= CharKey.A + pageElements-1 && code <= CharKey.A + items.size() - 1) {
						handler.put("SELECT_UNIT:"+(code-CharKey.A));
					} else if (code >= CharKey.a && code <= CharKey.a + pageElements-1 && code <= CharKey.a + items.size() - 1) {
						handler.put("SELECT_UNIT:"+(code-CharKey.a));
					}
				} catch (InterruptedException e1) {}
			}
		}; 
		
		cbml = new CallbackMouseListener<String>(transferSelectionHandler){
			@Override
			public void mouseClicked(MouseEvent e) {
				if (hoverDisabled)
					return;
				try {
					String[] legends = legend.split("XXX");
					int fontSize = getFont().getSize();
					final int lineHeight = (int)Math.round(fontSize*1.5);
					final int legendLines = legends.length > 0 ? legends.length: 1;
					SelectedItem selectedItem = getSelectedItemByClick(e.getPoint(), legendLines, lineHeight);
					if (selectedItem != null){
						handler.put("SELECT_UNIT:"+selectedItem.selectedIndex);
					}
				} catch (InterruptedException e1) {}
			}
		};
		
		avPagButton.addActionListener(new CallbackActionListener<String>(transferSelectionHandler){
			@Override
			public void actionPerformed(ActionEvent e) {
				avPag();
				try {
					handler.put("CHANGE_PAGE");
				} catch (InterruptedException e1) {}
			}
		});
		
		rePagButton.addActionListener(new CallbackActionListener<String>(transferSelectionHandler){
			@Override
			public void actionPerformed(ActionEvent e) {
				rePag();
				try {
					handler.put("CHANGE_PAGE");
				} catch (InterruptedException e1) {}
			}
		});
		
		si.addMouseListener(cbml);
		si.addKeyListener(cbkl);
	}

	private void transferToSource(int transferQuantity){
		if (transferQuantity == 0 || highlight == null){
			return;
		}
			
		if (transferQuantity + sourceCurrentQuantity > sourceMaximumQuantity){
			if (sourceCurrentQuantity >= sourceMaximumQuantity){
				return;
			} else {
				transferQuantity = sourceMaximumQuantity - sourceCurrentQuantity;
			}
		}
		
		if (!itemTransferFunctionality.validateAndPerformTransfer(destination, source, highlight, transferQuantity)){
			return;
		}
		
		// Transfer was sucessful... yay...
		
		sourceCurrentQuantity += transferQuantity;
		destinationCurrentQuantity -= transferQuantity;
		
		// Perform the transfer
		if (sourceCurrentQuantity > sourceMaximumQuantity)
			sourceCurrentQuantity = sourceMaximumQuantity;
		if (destinationCurrentQuantity < 0)
			destinationCurrentQuantity = 0;
		afterQuantityChange();
	}
	
	private void transferToSource() {
		int changeSpeed = 0;
		if (initialQuantity - sourceCurrentQuantity == 0)
			changeSpeed = 1;
		else
			changeSpeed = (int) Math.ceil((sourceCurrentQuantity - initialQuantity )/ 5.0d);
		
		int transferQuantity = changeSpeed;
		
		// Try to do the transfer
		updateMaximumQuantities(highlight);
		transferToSource(transferQuantity);
	}
	
	private void transferToSourceAll() {
		// Try to do the transfer
		int changeSpeed = sourceMaximumQuantity - sourceCurrentQuantity;
		updateMaximumQuantities(highlight);
		transferToSource(changeSpeed);
	}
	
	private void transferToDestination(){
		int changeSpeed = 0;
		if (initialQuantity - destinationCurrentQuantity == 0)
			changeSpeed = 1;
		else
			changeSpeed = (int) Math.ceil((destinationCurrentQuantity - initialQuantity)/ 5.0d);
		
		int transferQuantity = changeSpeed;

		
		// Try to do the transfer
		updateMaximumQuantities((ExpeditionItem) highlight);
		transferToDestination(transferQuantity);
	}
	
	private void transferToDestinationAll(){
		// Try to do the transfer
		int changeSpeed = destinationMaximumQuantity - destinationCurrentQuantity;
		updateMaximumQuantities((ExpeditionItem) highlight);
		transferToDestination(changeSpeed);	
	}
	
	private void transferToDestination(int transferQuantity){
		if (transferQuantity == 0 || highlight == null){
			return;
		}
		
		if (transferQuantity + destinationCurrentQuantity > destinationMaximumQuantity){
			if (destinationCurrentQuantity >= destinationMaximumQuantity){
				return;
			} else {
				transferQuantity = destinationMaximumQuantity - destinationCurrentQuantity;
			}
		}
		
		if (!itemTransferFunctionality.validateAndPerformTransfer(source, destination, highlight, transferQuantity)){
			return;
		}
		/*menuBox.resetSelection();*/
		//setLegend(itemTransferFunctionality.getTransferedLegend(transferQuantity, highlight, destination));
		
		// Transfer was sucessful... yay...
		
		destinationCurrentQuantity += transferQuantity;
		sourceCurrentQuantity -= transferQuantity;
		if (destinationCurrentQuantity > destinationMaximumQuantity)
			destinationCurrentQuantity = destinationMaximumQuantity;
		if (sourceCurrentQuantity < 0)
			sourceCurrentQuantity = 0;

		afterQuantityChange();
	}
	
	private void afterQuantityChange(){
		super.draw(false);
		updateContainerInfo();
		si.commitLayer(getDrawingLayer());
	}
	
	private void updateContainerInfo(){
		
		// Draw a cute border
		int x = 490; //55
		int y = 75;
		
		si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).setColor(ExpeditionOryxUI.ITEM_BOX_COLOR);
		si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).fillRect(x+1, y+1, 270 - 2, 390 - 2);
		si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).setColor(ExpeditionOryxUI.ITEM_BOX_BORDER_COLOR);
		si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).drawRect(x+1, y+1, 270 - 2, 390 - 2);
		si.getDrawingGraphics(ExpeditionOryxUI.UI_WIDGETS_LAYER).drawRect(x+2, y+2, 270 - 4, 390 - 4);
		
		si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+105, y + 76, "Carrying", OryxExpeditionDisplay.COLOR_BOLD);
		si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+105, y + 90, "People", OryxExpeditionDisplay.COLOR_BOLD);
		si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+105, y + 104, "Food Days", OryxExpeditionDisplay.COLOR_BOLD);
		si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+105, y + 118, "Water Days", OryxExpeditionDisplay.COLOR_BOLD);
		
		if (highlight != null){
			int textWidth = (int) (si.getTextWidth(ExpeditionOryxUI.UI_WIDGETS_LAYER, highlight.getDescription()) / 2.0d);
			si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+(145-textWidth)-10, y + 174, highlight.getDescription(), Color.RED);
			
			if (highlight instanceof ExpeditionUnit){
				textWidth = (int) (si.getTextWidth(ExpeditionOryxUI.UI_WIDGETS_LAYER, ((ExpeditionUnit)highlight).getWeaponDescription()) / 2.0d);
				si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+(145-textWidth)-10, y + 188, ((ExpeditionUnit)highlight).getWeaponDescription(), Color.WHITE);
				textWidth = (int) (si.getTextWidth(ExpeditionOryxUI.UI_WIDGETS_LAYER, ((ExpeditionUnit)highlight).getArmorDescription()) / 2.0d);
				si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+(145-textWidth)-10, y + 202, ((ExpeditionUnit)highlight).getArmorDescription(), Color.WHITE);
				textWidth = (int) (si.getTextWidth(ExpeditionOryxUI.UI_WIDGETS_LAYER, ((ExpeditionUnit)highlight).getStatusModifiersString()) / 2.0d);
				si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+(145-textWidth)-10, y + 216, ((ExpeditionUnit)highlight).getStatusModifiersString(), Color.WHITE);
			}
			
			si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+93, y + 230, "Current", OryxExpeditionDisplay.COLOR_BOLD);
			si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+114, y + 244, "Max", OryxExpeditionDisplay.COLOR_BOLD);
		}
		//draw left side, meaning either store or good cache	
		drawContainerInfo(x+10, y, source, false);
		//draw right side, meaning expedition
		drawContainerInfo(x+260, y, destination, true);	
		// Draw current unit
		if (highlight != null){
			// Get some info
			Image unitImage = ((GFXAppearance)highlight.getDialogAppearance()).getImage();

			// Draw the unit info
			si.drawImage(ExpeditionOryxUI.UI_WIDGETS_LAYER, x + 117, y + 132, unitImage);
			si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+10, y + 230, sourceCurrentQuantity+"", Color.WHITE, false);
			si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+10, y + 244, sourceMaximumQuantity+"", Color.WHITE, false);
			si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+260, y + 230, destinationCurrentQuantity+"", Color.WHITE, true);
			si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x+260, y + 244, destinationMaximumQuantity+"", Color.WHITE, true);
			
			if (highlight != lastChoice){
				// Just Selected
				updateMaximumQuantities(highlight);
				
				/*sourceCurrentQuantity = source.getItemCount(highlight.getFullID());
				destinationCurrentQuantity = destination.getItemCount(highlight.getFullID());*/

				// Pop components up
			    quantitySplitterToSource.setVisible(true);
			    quantitySplitterToSourceAll.setVisible(true);
			    quantitySplitterToDestination.setVisible(true);
			    quantitySplitterToDestinationAll.setVisible(true);
				lastChoice = highlight;
			}
		} else {
			quantitySplitterToSource.setVisible(false);
			quantitySplitterToSourceAll.setVisible(false);
			quantitySplitterToDestination.setVisible(false);
			quantitySplitterToDestinationAll.setVisible(false);
			lastChoice = null;
		}
		
		drawExpeditionCurrencies(x+10, y + 274);
		
		// Draw Box
		int boxY = 41 - 24;
		si.drawImage(ExpeditionOryxUI.UI_WIDGETS_LAYER, boxX, boxY, goodTypeBox);
	}
	
	private void drawExpeditionCurrencies(int x, int y) {
		int textWidth = (int) ((260 - si.getTextWidth(ExpeditionOryxUI.UI_WIDGETS_LAYER, "Expedition Valuables") ) / 2.0d);
		si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x + textWidth, y, "Expedition Valuables", OryxExpeditionDisplay.COLOR_BOLD);
		si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x, y + 28, "Spanish Maravedíes:", OryxExpeditionDisplay.COLOR_BOLD);
		si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x + 255, y + 42, ExpeditionGame.getCurrentGame().getExpedition().getAccountedGold()+"", Color.WHITE, true);
		
	}

	private void updateMaximumQuantities(ExpeditionItem eitem) {
		if (source == null || destination == null)
			return;
		if (eitem.getGoodType() == GoodType.VEHICLE){
			sourceCurrentQuantity = source.getVehicleCount(eitem.getFullID());
			destinationCurrentQuantity = destination.getVehicleCount(eitem.getFullID());
		} else {
			sourceCurrentQuantity = source.getItemCount(eitem.getFullID());
			destinationCurrentQuantity = destination.getItemCount(eitem.getFullID());
		}
		
		int allQuantity = sourceCurrentQuantity + destinationCurrentQuantity;
		if (eitem.getGoodType() == GoodType.VEHICLE){
			sourceMaximumQuantity = -1; // Can always add more vehicles to the expedition (Unless they are nested vehicles but those aren't yet supported)
		} else {
			sourceMaximumQuantity = source.getCarryable(eitem); // This is the maximum possible, unless the destination has infinite capacity
		}
		
		if (sourceMaximumQuantity == -1){
			// Infinite capacity, can carry all available
			sourceMaximumQuantity = allQuantity;
		} else {
			sourceMaximumQuantity += sourceCurrentQuantity;
			// Finite capacity, can carry up to this
			if (sourceMaximumQuantity > allQuantity)
				sourceMaximumQuantity = allQuantity;
		}
		
		if (sourceMaximumQuantity < 0)
			sourceMaximumQuantity = 0;
		if (eitem.getGoodType() == GoodType.VEHICLE){
			destinationMaximumQuantity = -1; // Can always add more vehicles to the expedition (Unless they are nested vehicles but those aren't yet supported)
		} else {
			destinationMaximumQuantity = destination.getCarryable(eitem); // This is the maximum possible, unless the destination has infinite capacity
		}
		
		if (destinationMaximumQuantity == -1){
			// Infinite capacity, can carry all available
			destinationMaximumQuantity = allQuantity;
		} else {
			destinationMaximumQuantity += destinationCurrentQuantity;
			// Finite capacity, can carry up to this
			if (destinationMaximumQuantity > allQuantity)
				destinationMaximumQuantity = allQuantity;
		}
		
		if (destinationMaximumQuantity < 0)
			destinationMaximumQuantity = 0;
	}

	private void initializeSplitters() {
		quantitySplitterToSource = new CleanButton(ExpeditionOryxUI.BTN_SPLIT_LEFT, ExpeditionOryxUI.BTN_SPLIT_LEFT_HOVER, null, ((GFXUserInterface)UserInterface.getUI()).getHandCursor());
		quantitySplitterToSource.setVisible(false);
		quantitySplitterToSource.setLocation(556,206);
		final Action transferToSourceAction = new AbstractAction() {
			
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent arg0) {
				transferToSource();
			}
			
		};
		final Timer transferToSourceTimer = new Timer(100, transferToSourceAction);
		quantitySplitterToSource.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				if (highlight == null)
					return;
				initialQuantity = sourceCurrentQuantity;
				transferToSource();
				transferToSourceTimer.start();
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				transferToSourceTimer.stop();
			}
		});
		
		quantitySplitterToDestination = new CleanButton(ExpeditionOryxUI.BTN_SPLIT_RIGHT, ExpeditionOryxUI.BTN_SPLIT_RIGHT_HOVER, null, ((GFXUserInterface)UserInterface.getUI()).getHandCursor());
		quantitySplitterToDestination.setVisible(false);
		quantitySplitterToDestination.setLocation(661,206);
		final Action transferToDestinationAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent arg0) {
				transferToDestination();
			}
		};
		final Timer transferToDestinationTimer = new Timer(100, transferToDestinationAction);
		quantitySplitterToDestination.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				if (highlight == null)
					return;
				initialQuantity = destinationCurrentQuantity;
				transferToDestination();
				transferToDestinationTimer.start();
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				transferToDestinationTimer.stop();
			}
		});
		
		quantitySplitterToSourceAll = new CleanButton(ExpeditionOryxUI.BTN_SPLIT_LEFT_ALL, ExpeditionOryxUI.BTN_SPLIT_LEFT_HOVER_ALL, null, ((GFXUserInterface)UserInterface.getUI()).getHandCursor());
		quantitySplitterToSourceAll.setVisible(false);
		quantitySplitterToSourceAll.setLocation(532,206);
		quantitySplitterToSourceAll.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				if (highlight == null)
					return;
				transferToSourceAll();
			}
		});
		
		quantitySplitterToDestinationAll = new CleanButton(ExpeditionOryxUI.BTN_SPLIT_RIGHT_ALL, ExpeditionOryxUI.BTN_SPLIT_RIGHT_HOVER_ALL, null, ((GFXUserInterface)UserInterface.getUI()).getHandCursor());
		quantitySplitterToDestinationAll.setVisible(false);
		quantitySplitterToDestinationAll.setLocation(685,206);
		quantitySplitterToDestinationAll.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				if (highlight == null)
					return;
				transferToDestinationAll();
			}
		});
		
		si.add(quantitySplitterToSource);
		si.add(quantitySplitterToSourceAll);
		si.add(quantitySplitterToDestination);
		si.add(quantitySplitterToDestinationAll);
		splitterArrowsListener = new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent e) {
				if (highlight == null)
					return;
				if (kbLaunchedTimer)
					return;
				int code = SwingSystemInterface.charCode(e);
				if (code == CharKey.LARROW){
					initialQuantity = sourceCurrentQuantity;
					transferToSource();
					transferToSourceTimer.start();
					kbLaunchedTimer = true;
				} else if (code == CharKey.RARROW){
					if (highlight == null)
						return;
					initialQuantity = destinationCurrentQuantity;
					transferToDestination();
					transferToDestinationTimer.start();
				    kbLaunchedTimer = true;
				}
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				if (highlight == null)
					return;
				int code = SwingSystemInterface.charCode(e);
				if (code == CharKey.LARROW){
					transferToSourceTimer.stop();
					kbLaunchedTimer = false;
				} else if (code == CharKey.RARROW){
					transferToDestinationTimer.stop();
					kbLaunchedTimer = false;

				}
			}
		}; 
		si.addKeyListener(splitterArrowsListener);
	}

	private ExpeditionItem lastChoice;
	private int boxX;
	
	public void draw(ExpeditionItem highlight, int boxX) {
		this.highlight = highlight;
		super.draw(false);
		updateContainerInfo();
		si.commitLayer(ExpeditionOryxUI.UI_WIDGETS_LAYER);
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param container
	 * @param alignRight
	 * 
	 * This method draws the store container info with item, currently carrying in percent, total units, food days and water days
	 */
	private void drawContainerInfo(int x, int y, ItemContainer container, boolean alignRight) {
		Appearance containerAppearance = container.getDialogAppearance();
		if (containerAppearance != null){
			Image containerImage = null;
			if (containerAppearance instanceof GFXAppearance){
				containerImage = ((GFXAppearance)containerAppearance).getImage();
			} else if (containerAppearance instanceof AnimatedGFXAppearance){
				containerImage = ((AnimatedGFXAppearance)containerAppearance).getImage(0);
			}
			if (alignRight)
				si.drawImage(ExpeditionOryxUI.UI_WIDGETS_LAYER, x - 40, y+10, containerImage);
			else
				si.drawImage(ExpeditionOryxUI.UI_WIDGETS_LAYER, x + 20, y+10, containerImage);
		}
		
		si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x, y+52, container.getTypeDescription(), OryxExpeditionDisplay.COLOR_BOLD, alignRight);		
		
		if (container.getCarryCapacity() != -1){
			si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x, y + 76, container.getCurrentlyCarrying()+"%", Color.WHITE, alignRight);
		}
		
		if (container.isPeopleContainer()){
			si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x, y + 90, container.getTotalUnits()+"", Color.WHITE, alignRight);

			int foodDays = container.getFoodDays();
			if (foodDays != -1){
				si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x, y + 104, container.getFoodDays()+"", Color.WHITE, alignRight);
			}
			int waterDays = container.getWaterDays();
			if (waterDays != -1){
				si.printAtPixel(ExpeditionOryxUI.UI_WIDGETS_LAYER, x, y + 118, container.getWaterDays()+"", Color.WHITE, alignRight);
			}
		}
	}

	public void draw(boolean refresh, int boxX) {
		this.boxX = boxX;
		draw(highlight, boxX);
	}
	
	public void draw(boolean refresh){
		draw(refresh, boxX);
	}
	
	@Override
	public void kill() {
		super.kill();
		si.remove(quantitySplitterToSource);
		si.remove(quantitySplitterToDestination);
		si.remove(quantitySplitterToSourceAll);
		si.remove(quantitySplitterToDestinationAll);
		si.removeKeyListener(cbkl);
		si.removeKeyListener(splitterArrowsListener);
		si.removeMouseListener(cbml);
	}

	public void selectUnit(int index) {
		if (index != -1){
			index += getCurrentPage() *  getItemsPerPage();
			highlight = ((CacheCustomGFXMenuItem)items.get(index)).getItem();
			updateMaximumQuantities(highlight);
			wasJustOnHovered = false;
		}
		
		//draw(highlight);
	}

	public void resetSelection() {
		highlight = null;
	}
	
	@Override
	public void setMenuItems(List<? extends GFXMenuItem> items) {
		super.setMenuItems(items);
		if (highlight != null){
			for (GFXMenuItem item: items){
				CacheCustomGFXMenuItem cacheItem = (CacheCustomGFXMenuItem) item; 
				if (cacheItem.getItem().getFullID().equals(highlight.getFullID())){
					highlight = cacheItem.getItem();
					return;
				}
			}
			resetSelection();
		}
	}

	@Override
	protected Cursor getDefaultCursor() {
		return ExpeditionOryxUI.POINTER_CURSOR;
	}
	
	@Override
	protected Cursor getHandCursor() {
		return ExpeditionOryxUI.HAND_CURSOR;
	}
	
	@Override
	public int getDrawingLayer() {
		return ExpeditionOryxUI.UI_WIDGETS_LAYER;
	}
}
