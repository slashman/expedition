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
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;

import net.slashie.expedition.domain.ExpeditionItem;
import net.slashie.expedition.domain.Food;
import net.slashie.expedition.domain.ItemContainer;
import net.slashie.expedition.domain.ShipCache;
import net.slashie.expedition.world.FoodConsumer;
import net.slashie.libjcsi.CharKey;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.ui.UserInterface;
import net.slashie.serf.ui.oryxUI.GFXAppearance;
import net.slashie.serf.ui.oryxUI.GFXUserInterface;
import net.slashie.serf.ui.oryxUI.SwingSystemInterface;
import net.slashie.utils.swing.BorderedGridBox;
import net.slashie.utils.swing.CallbackActionListener;
import net.slashie.utils.swing.CallbackKeyListener;
import net.slashie.utils.swing.CallbackMouseListener;
import net.slashie.utils.swing.CleanButton;
import net.slashie.utils.swing.GFXMenuItem;

public class TransferBorderGridBox extends BorderedGridBox{
	private static final long serialVersionUID = 1L;
	private Equipment highlight;
	private ItemContainer from;
	private ItemContainer to;
	private CleanButton transferButton;
	private CallbackKeyListener<String> cbkl;
	private CallbackMouseListener<String> cbml;
	private Image goodTypeBox;
	
	// Splitter attributes
	private CleanButton quantitySplitterUp;
	private CleanButton quantitySplitterDown;
	private JLabel quantityLabel;
	private int selectedQuantity;
	private int maximumQuantity;
	private String transferUnit;
	private int changeSpeed;
	private int initialQuantity;
	private KeyListener splitterArrowsListener;
	private boolean kbLaunchedTimer = false;
	
	public TransferBorderGridBox(
			// Standard parameters, sent to super()
			BufferedImage border1, BufferedImage border2,
			BufferedImage border3, BufferedImage border4,
			SwingSystemInterface g, Color backgroundColor, Color borderIn,
			Color borderOut, int borderWidth, int outsideBound, int inBound,
			int insideBound, int itemHeight, int itemWidth, int gridX,
			int gridY, BufferedImage box, 
			CleanButton closeButton, CleanButton transferButton,
			
			ItemContainer from, ItemContainer to, 
			
			BlockingQueue<String> transferSelectionHandler) {
		super(border1, border2, border3, border4, g, backgroundColor, borderIn,
				borderOut, borderWidth, outsideBound, inBound, insideBound, itemHeight,
				itemWidth, gridX, gridY, box, closeButton);
		
		initializeSplitters();
		this.goodTypeBox = box;
		this.from = from;
		this.to = to;
		this.transferButton = transferButton;
		
		quantityLabel = new JLabel();
		quantityLabel.setFont(si.getFont());
		quantityLabel.setVisible(false);
		quantityLabel.setBounds(540,231,200,27);
		quantityLabel.setForeground(Color.WHITE);
		si.add(quantityLabel);
		
		transferButton.setVisible(false);
		transferButton.setLocation(515,270);
		si.add(transferButton);
		transferButton.addActionListener(new CallbackActionListener<String>(transferSelectionHandler){
			@Override
			public void actionPerformed(ActionEvent e) {
				if (hoverDisabled)
					return;
				try {
					handler.put("CONFIRM_TRANSFER");
				} catch (InterruptedException e1) {}
				si.recoverFocus();
			}
		});
		
		final int pageElements = gridX * gridY;
		
		cbkl = new CallbackKeyListener<String>(transferSelectionHandler){
			@Override
			public void keyPressed(KeyEvent e) {
				if (hoverDisabled)
					return;
				try {
					int code = SwingSystemInterface.charCode(e);
					if (code == CharKey.PAGEUP){
						rePag();
						handler.put("CHANGE_PAGE");
					} else if (code == CharKey.PAGEDOWN) {
						avPag();
						handler.put("CHANGE_PAGE");
					} else if (code >= CharKey.A && code <= CharKey.A + pageElements-1) {
						handler.put("SELECT_UNIT:"+(code-CharKey.A));
					} else if (code >= CharKey.a && code <= CharKey.a + pageElements-1) {
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
		
		si.addMouseListener(cbml);
		si.addKeyListener(cbkl);
	}

	private void initializeSplitters() {
		quantitySplitterUp = new CleanButton(ExpeditionOryxUI.BTN_SPLIT_UP, ExpeditionOryxUI.BTN_SPLIT_UP_HOVER, null, ((GFXUserInterface)UserInterface.getUI()).getHandCursor());
		quantitySplitterUp.setVisible(false);
		quantitySplitterUp.setLocation(512,218);
		final Action increaseQuantityAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (initialQuantity - selectedQuantity == 0)
					changeSpeed = 1;
				else
					changeSpeed = (int) Math.ceil((selectedQuantity - initialQuantity )/ 5.0d); 
				selectedQuantity += changeSpeed;
				if (selectedQuantity > maximumQuantity)
					selectedQuantity = maximumQuantity;
			    quantityLabel.setText(selectedQuantity+"/"+maximumQuantity+transferUnit);
			}
		};
		final Timer increaseQuantityTimer = new Timer(100, increaseQuantityAction);

		quantitySplitterUp.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				if (highlight == null)
					return;
				initialQuantity = selectedQuantity;
				increaseQuantityAction.actionPerformed(null);
			    increaseQuantityTimer.start();
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				increaseQuantityTimer.stop();
			}
		});
		
		
		quantitySplitterDown = new CleanButton(ExpeditionOryxUI.BTN_SPLIT_DOWN, ExpeditionOryxUI.BTN_SPLIT_DOWN_HOVER, null, ((GFXUserInterface)UserInterface.getUI()).getHandCursor());
		quantitySplitterDown.setVisible(false);
		quantitySplitterDown.setLocation(512,243);
		
		final Action decreaseQuantityAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (initialQuantity - selectedQuantity == 0)
					changeSpeed = 1;
				else
					changeSpeed = (int) Math.ceil((initialQuantity - selectedQuantity)/ 5.0d); 
				selectedQuantity -= changeSpeed;
				if (selectedQuantity < 0)
					selectedQuantity = 0;
			    quantityLabel.setText(selectedQuantity+"/"+maximumQuantity+transferUnit);
			}
		};
		final Timer decreaseQuantityTimer = new Timer(100, decreaseQuantityAction);

		quantitySplitterDown.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				if (highlight == null)
					return;
				initialQuantity = selectedQuantity;
				decreaseQuantityAction.actionPerformed(null);
			    decreaseQuantityTimer.start();
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				decreaseQuantityTimer.stop();
			}
		});
		si.add(quantitySplitterUp);
		si.add(quantitySplitterDown);
		splitterArrowsListener = new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent e) {
				if (highlight == null)
					return;
				if (kbLaunchedTimer)
					return;
				int code = SwingSystemInterface.charCode(e);
				if (code == CharKey.UARROW || code == CharKey.N8){
					initialQuantity = selectedQuantity;
					increaseQuantityAction.actionPerformed(null);
					increaseQuantityTimer.start();
					kbLaunchedTimer = true;
				} else if (code == CharKey.DARROW || code == CharKey.N2){
					if (highlight == null)
						return;
					initialQuantity = selectedQuantity;
					decreaseQuantityAction.actionPerformed(null);
				    decreaseQuantityTimer.start();
				    kbLaunchedTimer = true;
				}
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				if (highlight == null)
					return;
				int code = SwingSystemInterface.charCode(e);
				if (code == CharKey.UARROW || code == CharKey.N8){
					increaseQuantityTimer.stop();
					kbLaunchedTimer = false;
				} else if (code == CharKey.DARROW || code == CharKey.N2){
					decreaseQuantityTimer.stop();
					kbLaunchedTimer = false;

				}
			}
		}; 
		si.addKeyListener(splitterArrowsListener);
	}

	private ExpeditionItem lastChoice;
	private int boxX;
	private boolean daysFoodTransfer = false;
	
	public void draw(Equipment highlight, int boxX) {
		si.restore();
		si.saveBuffer();
		this.highlight = highlight;
		super.draw(false);
		// Draw a cute border
		int x = 450;
		int y = 75;
		si.getGraphics2D().setColor(OryxExpeditionDisplay.COLOR_BOLD);
		si.getGraphics2D().drawRect(x+1, y+1, 310 - 2, 390 - 2);
		si.getGraphics2D().drawRect(x+2, y+2, 310 - 4, 390 - 4);

		drawContainerInfo(x, 85, from);
		drawContainerInfo(x, 323, to);
		
		// Draw current unit
		if (highlight != null){
			ExpeditionItem eitem = (ExpeditionItem) highlight.getItem();
			// Get some info
			Image unitImage = ((GFXAppearance)eitem.getAppearance()).getImage();
			String itemDescription = eitem.getDescription();

			y = 210;

			// Draw the unit info
			si.drawImage(x + 12, y + 17, unitImage);
			si.printAtPixel(x+41, y + 17, "How many "+itemDescription, OryxExpeditionDisplay.COLOR_BOLD);

			if (eitem != lastChoice){
				// Just Selected
				maximumQuantity = to.getCarryable(eitem); // This is the maximum possible, unless the destination has infinite capacity
				if (maximumQuantity == -1){
					// Infinite capacity, can carry all available
					maximumQuantity = highlight.getQuantity();
				} else {
					// Finite capacity, can carry up to this
					if (maximumQuantity > highlight.getQuantity())
						maximumQuantity = highlight.getQuantity();
				}
  	  			if (eitem instanceof Food){
  	  				if (to instanceof FoodConsumer){
  	  					FoodConsumer toFoodConsumer = (FoodConsumer) to;
  	  					int dailyFoodConsumption = toFoodConsumer.getDailyFoodConsumption();
  	  					if (dailyFoodConsumption == 0){
  	  						// Destination has no units, transfer by quantity
  	  						transferUnit = "";
	  						daysFoodTransfer = false;
  	  					} else {
	  	  	  				// Player picks supply days, not item quantity, scale things
	  	  					int unitMaximumQuantity = maximumQuantity;
	  	  					maximumQuantity = (int) Math.floor((double)unitMaximumQuantity / (double)dailyFoodConsumption);
	  	  					if (maximumQuantity == 0){
	  	  						maximumQuantity = unitMaximumQuantity;
	  	  						transferUnit = "";
	  	  						daysFoodTransfer = false;
	  	  					} else {
	  	  						transferUnit = " days";
	  	  						daysFoodTransfer = true;
	  	  					}
  	  					}
  	  				} else {
  	  					maximumQuantity = -1;
  	  				}
  	  			} else {
  	  				transferUnit = "";
  	  				daysFoodTransfer  = false;
  	  			}

				selectedQuantity = 0;
			    quantityLabel.setText(selectedQuantity+"/"+maximumQuantity+transferUnit);


				// Pop components up
			    quantitySplitterUp.setVisible(true);
			    quantitySplitterDown.setVisible(true);
				transferButton.setVisible(true);
				quantityLabel.setVisible(true);
				lastChoice = eitem;
			}
		} else {
		    quantitySplitterUp.setVisible(false);
		    quantitySplitterDown.setVisible(false);
		    transferButton.setVisible(false);
			quantityLabel.setVisible(false);
			lastChoice = null;
		}
		
		// Draw Box
		int boxY = 41 - 24;
		si.drawImage(boxX, boxY, goodTypeBox);
		
		
		si.refresh();
	}
	
	private void drawContainerInfo(int x, int y, ItemContainer container) {
		GFXAppearance containerAppearance = (GFXAppearance)container.getAppearance();
		if (containerAppearance != null){
			si.drawImage(x + 12, y + 17, containerAppearance.getImage());
		}
		int foodDays = container.getFoodDays();
		if (foodDays != -1){
			si.printAtPixel(x+12, y + 92, "Food Days", OryxExpeditionDisplay.COLOR_BOLD);
			si.printAtPixel(x+146, y + 92, container.getFoodDays()+"", Color.WHITE);
		}
		int waterDays = container.getWaterDays();
		if (waterDays != -1){
			si.printAtPixel(x+12, y + 106, "Water Days", OryxExpeditionDisplay.COLOR_BOLD);
			si.printAtPixel(x+146, y + 106, container.getWaterDays()+"", Color.WHITE);
		}
		
		if (container instanceof ShipCache){
			si.printAtPixel(x+41, y + 17, "Sea Expedition", OryxExpeditionDisplay.COLOR_BOLD);
			si.printAtPixel(x+41, y + 32, "Ships", OryxExpeditionDisplay.COLOR_BOLD);
			si.printAtPixel(x+41, y + 47, "Cargo", OryxExpeditionDisplay.COLOR_BOLD);
			si.printAtPixel(x+12, y + 62, "Max Cargo", OryxExpeditionDisplay.COLOR_BOLD);
			si.printAtPixel(x+12, y + 77, "Crew", OryxExpeditionDisplay.COLOR_BOLD);
			si.printAtPixel(x+146, y + 32, container.getTotalShips()+"", Color.WHITE);
			si.printAtPixel(x+146, y + 47, container.getCurrentlyCarrying()+"", Color.WHITE);
			si.printAtPixel(x+146, y + 62, container.getCarryCapacity()+"", Color.WHITE);
			si.printAtPixel(x+146, y + 77, container.getTotalUnits()+"", Color.WHITE);
		} else {
			si.printAtPixel(x+41, y + 17, container.getDescription(), OryxExpeditionDisplay.COLOR_BOLD);
			if (container.getCarryCapacity() != -1){
				si.printAtPixel(x+12, y + 62, "Capacity", OryxExpeditionDisplay.COLOR_BOLD);
				si.printAtPixel(x+146, y + 62, container.getCarryCapacity()+"", Color.WHITE);
				si.printAtPixel(x+41, y + 47, "Carrying", OryxExpeditionDisplay.COLOR_BOLD);
				si.printAtPixel(x+146, y + 47, container.getCurrentlyCarrying()+"%", Color.WHITE);
			}
			si.printAtPixel(x+12, y + 77, "People", OryxExpeditionDisplay.COLOR_BOLD);
			si.printAtPixel(x+146, y + 77, container.getTotalUnits()+"", Color.WHITE);
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
		si.remove(quantitySplitterUp);
		si.remove(quantitySplitterDown);
		si.remove(transferButton);
		si.remove(quantityLabel);
		si.removeKeyListener(cbkl);
		si.removeKeyListener(splitterArrowsListener);
		si.removeMouseListener(cbml);
	}

	public void selectUnit(int index) {
		if (index != -1){
			index += getCurrentPage() *  getItemsPerPage();
			highlight = ((CacheCustomGFXMenuItem)items.get(index)).getEquipment();
		}
		//draw(highlight);
	}

	public Equipment getSelectedUnit() {
		return highlight;
	}

	public int getQuantity() {
		return selectedQuantity;
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
				if (cacheItem.getEquipment().getItem().getFullID().equals(highlight.getItem().getFullID())){
					highlight = cacheItem.getEquipment();
					return;
				}
			}
			resetSelection();
		}
	}

	@Override
	protected Cursor getDefaultCursor() {
		return ((ExpeditionOryxUI)ExpeditionOryxUI.getUI()).POINTER_CURSOR;
	}
	
	@Override
	protected Cursor getHandCursor() {
		return ((ExpeditionOryxUI)ExpeditionOryxUI.getUI()).HAND_CURSOR;
	}

	public boolean isDaysFoodTransfer() {
		return daysFoodTransfer;
	}
}
