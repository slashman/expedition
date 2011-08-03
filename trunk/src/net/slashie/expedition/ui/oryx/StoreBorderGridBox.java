package net.slashie.expedition.ui.oryx;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.BlockingQueue;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.slashie.expedition.domain.Armor;
import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.ExpeditionItem;
import net.slashie.expedition.domain.ExpeditionUnit;
import net.slashie.expedition.domain.Store;
import net.slashie.expedition.domain.StoreItemInfo;
import net.slashie.expedition.domain.Weapon;
import net.slashie.libjcsi.CharKey;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.ui.AppearanceFactory;
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
import net.slashie.utils.swing.BorderedGridBox.SelectedItem;

public class StoreBorderGridBox extends BorderedGridBox{
	private static final long serialVersionUID = 1L;
	private Expedition offShoreExpedition;
	private Equipment highlight;
	private CleanButton quantitySplitterUp;
	private CleanButton quantitySplitterDown;
	private CleanButton buyButton;
	private JLabel quantityLabel;
	private int selectedQuantity;
	private int maximumQuantity;
	private int changeSpeed;
	private int initialQuantity;
	private Store store;
	boolean buyButtonEnabled = true;
	
	@Override
	public GFXMenuItem getSelection() {
		
		return super.getSelection();
	}

	public StoreBorderGridBox(BufferedImage border1, BufferedImage border2,
			BufferedImage border3, BufferedImage border4,
			SwingSystemInterface g, Color backgroundColor, Color borderIn,
			Color borderOut, int borderWidth, int outsideBound, int inBound,
			int insideBound, int itemHeight, int itemWidth, int gridX,
			int gridY, BufferedImage box,
			final Store store, Expedition offShoreExpedition, CleanButton buyButton, final BlockingQueue<Integer> buyButtonSelectionHandler_, CleanButton closeButton) {
		super(border1, border2, border3, border4, g, backgroundColor, borderIn,
				borderOut, borderWidth, outsideBound, inBound, insideBound, itemHeight,
				itemWidth, gridX, gridY, box, closeButton);
		this.offShoreExpedition = offShoreExpedition;
		this.buyButton = buyButton;
		this.store = store;
		buyButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (buyButtonEnabled)
						buyButtonSelectionHandler_.put(selectedQuantity);
					buyButtonEnabled = false;
				} catch (InterruptedException e1) {
				}
			}
		});
		
		quantitySplitterUp = new CleanButton(ExpeditionOryxUI.BTN_SPLIT_UP, ExpeditionOryxUI.BTN_SPLIT_UP_HOVER, null, ((GFXUserInterface)UserInterface.getUI()).getHandCursor());

		quantitySplitterUp.setVisible(false);
		quantitySplitterUp.setLocation(512,149);
		final Action increaseQuantityAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent arg0) {
				StoreItemInfo itemInfo = store.getPriceFor((ExpeditionItem)highlight.getItem());
				if (initialQuantity - selectedQuantity == 0)
					changeSpeed = 1;
				else
					changeSpeed = (int) Math.ceil((selectedQuantity - initialQuantity )/ 5.0d); 
				selectedQuantity += changeSpeed;
				if (selectedQuantity > maximumQuantity)
					selectedQuantity = maximumQuantity;
			    quantityLabel.setText(selectedQuantity+", $"+(itemInfo.getPrice() * selectedQuantity));
			}
		};
		final Timer increaseQuantityTimer = new Timer(100, increaseQuantityAction);

		quantitySplitterUp.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				if (highlight == null)
					return;
				initialQuantity = selectedQuantity;
				
				StoreItemInfo itemInfo = store.getPriceFor((ExpeditionItem)highlight.getItem());
				selectedQuantity ++;
				if (selectedQuantity > maximumQuantity)
					selectedQuantity = maximumQuantity;
			    quantityLabel.setText(selectedQuantity+", $"+(itemInfo.getPrice() * selectedQuantity));
				increaseQuantityTimer.start();
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				increaseQuantityTimer.stop();
			}
		});
		
		quantitySplitterDown = new CleanButton(ExpeditionOryxUI.BTN_SPLIT_DOWN, ExpeditionOryxUI.BTN_SPLIT_DOWN_HOVER, null, ((GFXUserInterface)UserInterface.getUI()).getHandCursor());
		quantitySplitterDown.setVisible(false);
		quantitySplitterDown.setLocation(512,176);
		
		final Action decreaseQuantityAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent arg0) {
				StoreItemInfo itemInfo = store.getPriceFor((ExpeditionItem)highlight.getItem());
				if (initialQuantity - selectedQuantity == 0)
					changeSpeed = 1;
				else
					changeSpeed = (int) Math.ceil((initialQuantity - selectedQuantity)/ 5.0d); 
				selectedQuantity -= changeSpeed;
				if (selectedQuantity < 1)
					selectedQuantity = 1;
			    quantityLabel.setText(selectedQuantity+", $"+(itemInfo.getPrice() * selectedQuantity));
			}
		};
		final Timer decreaseQuantityTimer = new Timer(100, decreaseQuantityAction);

		quantitySplitterDown.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				if (highlight == null)
					return;
				initialQuantity = selectedQuantity;
				
				StoreItemInfo itemInfo = store.getPriceFor((ExpeditionItem)highlight.getItem());
				selectedQuantity --;
				if (selectedQuantity < 1)
					selectedQuantity = 1;
			    quantityLabel.setText(selectedQuantity+", $"+(itemInfo.getPrice() * selectedQuantity));
			    decreaseQuantityTimer.start();
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				decreaseQuantityTimer.stop();
			}
		});
		
		buyButton.setVisible(false);
		buyButton.setLocation(518,201);
		
		quantityLabel = new JLabel();
		quantityLabel.setFont(si.getFont());
		quantityLabel.setVisible(false);
		quantityLabel.setBounds(540,160,200,27);
		quantityLabel.setForeground(Color.WHITE);
		
		si.add(quantitySplitterUp);
		si.add(quantitySplitterDown);
		si.add(buyButton);
		si.add(quantityLabel);
		// Add the textfield
	}

	private ExpeditionItem lastChoice;
	private CallbackMouseListener<Integer> cbml;
	private CallbackKeyListener<Integer> cbkl;
	private CallbackActionListener<Integer> cbal;
	
	public void draw(Equipment highlight) {
		this.highlight = highlight;
		super.draw(false);
		// Draw the expedition and current unit box
		if (highlight != null){
			ExpeditionItem eitem = (ExpeditionItem) highlight.getItem();
			// Get some info
			Image unitImage = ((GFXAppearance)eitem.getAppearance()).getImage();
			String itemDescription = eitem.getDescription();
			int inventory = highlight.getQuantity();
			int stock = offShoreExpedition.getOffshoreCarryable(eitem);
			int current = offShoreExpedition.getItemCountBasic(eitem.getFullID());
			StoreItemInfo itemInfo = store.getPriceFor((ExpeditionItem)highlight.getItem());

			// Draw a cute border
			int x = 450;
			int y = 75;
			si.getGraphics2D().setColor(OryxExpeditionDisplay.COLOR_BOLD);
			si.getGraphics2D().drawRect(x+1, y+1, 280 - 2, 390 - 2);
			si.getGraphics2D().drawRect(x+2, y+2, 280 - 4, 390 - 4);
			
			// Draw the unit info
			si.drawImage(x + 12, y + 17, unitImage);
			si.printAtPixel(x+41, y + 17, itemDescription, OryxExpeditionDisplay.COLOR_BOLD);
			si.printAtPixel(x+41, y + 32, "On ships", OryxExpeditionDisplay.COLOR_BOLD);
			si.printAtPixel(x+41, y + 47, "Max", OryxExpeditionDisplay.COLOR_BOLD);
			si.printAtPixel(x+41, y + 62, "In store", OryxExpeditionDisplay.COLOR_BOLD);
			si.printAtPixel(x+146, y + 32, current+"", Color.WHITE);
			si.printAtPixel(x+146, y + 47, stock+"", Color.WHITE);
			si.printAtPixel(x+146, y + 62, inventory+"", Color.WHITE);

			if (eitem != lastChoice){		
				// Just Selected
				maximumQuantity = stock < inventory ? stock : inventory;
				selectedQuantity = 1;
			    quantityLabel.setText(selectedQuantity+", $"+(itemInfo.getPrice() * selectedQuantity));

				// Pop components up
			    quantitySplitterUp.setVisible(true);
			    quantitySplitterDown.setVisible(true);
				buyButton.setVisible(true);
				quantityLabel.setVisible(true);
				lastChoice = eitem;
			}
			
			
			// Draw a cute border
			x = 450;
			y = 263;
			
			
			// Draw the offshore expedition info
			
			si.drawImage(x + 12, y + 17, ((GFXAppearance)AppearanceFactory.getAppearanceFactory().getAppearance("SHIP")).getImage());
			si.printAtPixel(x+41, y + 17, "Expedition", OryxExpeditionDisplay.COLOR_BOLD);
			si.printAtPixel(x+41, y + 32, "Ships", OryxExpeditionDisplay.COLOR_BOLD);
			si.printAtPixel(x+41, y + 47, "Cargo", OryxExpeditionDisplay.COLOR_BOLD);
			si.printAtPixel(x+12, y + 62, "Max Cargo", OryxExpeditionDisplay.COLOR_BOLD);
			si.printAtPixel(x+12, y + 77, "Crew", OryxExpeditionDisplay.COLOR_BOLD);
			si.printAtPixel(x+12, y + 92, "Gold", OryxExpeditionDisplay.COLOR_BOLD);
			si.printAtPixel(x+12, y + 106, "Food Days", OryxExpeditionDisplay.COLOR_BOLD);
			si.printAtPixel(x+12, y + 120, "Water Days", OryxExpeditionDisplay.COLOR_BOLD);
			si.printAtPixel(x+146, y + 32, offShoreExpedition.getTotalShips()+"", Color.WHITE);
			si.printAtPixel(x+146, y + 47, offShoreExpedition.getCurrentlyCarrying()+"", Color.WHITE);
			si.printAtPixel(x+146, y + 62, offShoreExpedition.getCarryCapacity()+"", Color.WHITE);
			si.printAtPixel(x+146, y + 77, offShoreExpedition.getTotalUnits()+"", Color.WHITE);
			si.printAtPixel(x+146, y + 92, offShoreExpedition.getAccountedGold()+"", Color.WHITE);
			si.printAtPixel(x+146, y + 106, offShoreExpedition.getFoodDays()+"", Color.WHITE);
			si.printAtPixel(x+146, y + 120, offShoreExpedition.getWaterDays()+"", Color.WHITE);

		} else {
		    quantitySplitterUp.setVisible(false);
		    quantitySplitterDown.setVisible(false);
			buyButton.setVisible(false);
			quantityLabel.setVisible(false);
			lastChoice = null;
		}
		si.refresh();
	}
	
	@Override
	public void draw(boolean refresh) {
		draw(highlight);
	}
	
	@Override
	public void kill() {
		super.kill();
		si.remove(quantitySplitterUp);
		si.remove(quantitySplitterDown);
		si.remove(buyButton);
		si.remove(quantityLabel);
	}
	

	public void activateItemPseudoSelection(BlockingQueue<Integer> quantitySelectionQueue) {
		buyButtonEnabled = true;
		int itemsPerPage = gridX * gridY;
		final int pageElements = itemsPerPage;
		
		cbkl = new CallbackKeyListener<Integer>(quantitySelectionQueue){
			@Override
			public void keyPressed(KeyEvent e) {
				try {
					int code = SwingSystemInterface.charCode(e);
					if (code != CharKey.SPACE &&
						code != CharKey.ESC &&
						code != CharKey.UARROW &&
						code != CharKey.DARROW &&
						code != CharKey.N8 &&
						code != CharKey.N2 &&
						
						(code < CharKey.A || code > CharKey.A + pageElements-1) &&
						(code < CharKey.a || code > CharKey.a + pageElements-1)
						){
						
					} else {
						if (handler.isEmpty())
							handler.put(-1);
						preselectedCode = code;

					}
				} catch (InterruptedException e1) {}
			}
		}; 
		
		cbml = new CallbackMouseListener<Integer>(quantitySelectionQueue){
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					String[] legends = legend.split("XXX");
					int fontSize = getFont().getSize();
					final int lineHeight = (int)Math.round(fontSize*1.5);
					final int legendLines = legends.length > 0 ? legends.length: 1;
					SelectedItem selectedItem = getSelectedItemByClick(e.getPoint(), legendLines, lineHeight);
					if (selectedItem != null){
						if (handler.isEmpty())
							handler.put(-1);
						preselectedCode = selectedItem.selectedIndex + CharKey.a;
					}
					
				} catch (InterruptedException e1) {}
			}
		};
		
		cbal = new CallbackActionListener<Integer>(quantitySelectionQueue){
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (handler.isEmpty())
						handler.put(-1);
					preselectedCode = CharKey.SPACE;
				} catch (InterruptedException e1) {}
			}
		};
		si.addMouseListener(cbml);
		si.addKeyListener(cbkl);
		closeButton.addActionListener(cbal);
	}
	
	public void quantityObtained() {
		si.removeMouseListener(cbml);
		si.removeKeyListener(cbkl);
		closeButton.removeActionListener(cbal);
		//buyButtonEnabled = true;
	}
	
	@Override
	protected Cursor getDefaultCursor() {
		return ((ExpeditionOryxUI)ExpeditionOryxUI.getUI()).POINTER_CURSOR;
	}
	
	@Override
	protected Cursor getHandCursor() {
		return ((ExpeditionOryxUI)ExpeditionOryxUI.getUI()).HAND_CURSOR;
	}	


}
