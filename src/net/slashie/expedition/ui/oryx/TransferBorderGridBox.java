package net.slashie.expedition.ui.oryx;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.BlockingQueue;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;

import net.slashie.expedition.domain.ExpeditionItem;
import net.slashie.expedition.domain.GoodType;
import net.slashie.expedition.domain.ItemContainer;
import net.slashie.expedition.domain.ShipCache;
import net.slashie.expedition.domain.UnitContainer;
import net.slashie.libjcsi.CharKey;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.ui.oryxUI.GFXAppearance;
import net.slashie.serf.ui.oryxUI.SwingSystemInterface;
import net.slashie.utils.swing.BorderedGridBox;
import net.slashie.utils.swing.CallbackKeyListener;
import net.slashie.utils.swing.CallbackMouseListener;
import net.slashie.utils.swing.CleanButton;

public class TransferBorderGridBox extends BorderedGridBox{
	private static final long serialVersionUID = 1L;
	private Equipment highlight;
	private ItemContainer from;
	private ItemContainer to;
	private CleanButton transferButton;
	
	// Splitter attributes
	private CleanButton quantitySplitterUp;
	private CleanButton quantitySplitterDown;
	private JLabel quantityLabel;
	private int selectedQuantity;
	private int maximumQuantity;
	private int changeSpeed;
	private int initialQuantity;
	private CallbackKeyListener<String> cbkl;
	private CallbackMouseListener<String> cbml;
	
	
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
			
			BlockingQueue<String> transferSelectionHandler, 
			
			Image splitterImgUp, Image splitterImgDown) {
		super(border1, border2, border3, border4, g, backgroundColor, borderIn,
				borderOut, borderWidth, outsideBound, inBound, insideBound, itemHeight,
				itemWidth, gridX, gridY, box, closeButton);
		
		initializeSplitters(splitterImgUp, splitterImgDown);
		
		this.from = from;
		this.to = to;
		this.transferButton = transferButton;
		
		quantityLabel = new JLabel();
		quantityLabel.setFont(si.getFont());
		quantityLabel.setVisible(false);
		quantityLabel.setBounds(540,221,200,27);
		quantityLabel.setForeground(Color.WHITE);
		si.add(quantityLabel);
		
		transferButton.setVisible(false);
		transferButton.setLocation(515,260);
		si.add(transferButton);
		
		
		final int pageElements = gridX * gridY;;
		
		cbkl = new CallbackKeyListener<String>(transferSelectionHandler){
			@Override
			public void keyPressed(KeyEvent e) {
				try {
					int code = SwingSystemInterface.charCode(e);
					if (code != CharKey.UARROW &&
						code != CharKey.DARROW &&
						code != CharKey.N8 &&
						code != CharKey.N2){
						// Whoops??? Change page and redraw?
					} else if (code < CharKey.A || code > CharKey.A + pageElements-1) {
						handler.put("SELECT_UNIT:"+(code-CharKey.A));
					} else if (code < CharKey.a || code > CharKey.a + pageElements-1) {
						handler.put("SELECT_UNIT:"+(code-CharKey.a));
					}
				} catch (InterruptedException e1) {}
			}
		}; 
		
		cbml = new CallbackMouseListener<String>(transferSelectionHandler){
			@Override
			public void mouseClicked(MouseEvent e) {
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

	private void initializeSplitters(Image splitterImgUp, Image splitterImgDown) {
		quantitySplitterUp = new CleanButton(new ImageIcon(splitterImgUp));
		quantitySplitterUp.setVisible(false);
		quantitySplitterUp.setBounds(512,211,24,24);
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
			    quantityLabel.setText(selectedQuantity+"");
			}
		};
		final Timer increaseQuantityTimer = new Timer(100, increaseQuantityAction);

		quantitySplitterUp.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				if (highlight == null)
					return;
				initialQuantity = selectedQuantity;
				selectedQuantity ++;
				if (selectedQuantity > maximumQuantity)
					selectedQuantity = maximumQuantity;
			    quantityLabel.setText(selectedQuantity+"");
				increaseQuantityTimer.start();
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				increaseQuantityTimer.stop();
			}
		});
		
		quantitySplitterDown = new CleanButton(new ImageIcon(splitterImgDown));
		quantitySplitterDown.setVisible(false);
		quantitySplitterDown.setBounds(512,233,24,24);
		
		final Action decreaseQuantityAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (initialQuantity - selectedQuantity == 0)
					changeSpeed = 1;
				else
					changeSpeed = (int) Math.ceil((initialQuantity - selectedQuantity)/ 5.0d); 
				selectedQuantity -= changeSpeed;
				if (selectedQuantity < 1)
					selectedQuantity = 1;
			    quantityLabel.setText(selectedQuantity+"");
			}
		};
		final Timer decreaseQuantityTimer = new Timer(100, decreaseQuantityAction);

		quantitySplitterDown.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				if (highlight == null)
					return;
				initialQuantity = selectedQuantity;
				
				selectedQuantity --;
				if (selectedQuantity < 1)
					selectedQuantity = 1;
			    quantityLabel.setText(selectedQuantity+"");
			    decreaseQuantityTimer.start();
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				decreaseQuantityTimer.stop();
			}
		});
		si.add(quantitySplitterUp);
		si.add(quantitySplitterDown);
		
	}

	private ExpeditionItem lastChoice;
	
	public void draw(Equipment highlight) {
		this.highlight = highlight;
		super.draw(false);
		// Draw a cute border
		int x = 450;
		int y = 75;
		si.getGraphics2D().setColor(OryxExpeditionDisplay.COLOR_BOLD);
		si.getGraphics2D().drawRect(x+1, y+1, 280 - 2, 390 - 2);
		si.getGraphics2D().drawRect(x+2, y+2, 280 - 4, 390 - 4);

		drawContainerInfo(x, 75, from);
		drawContainerInfo(x, 313, to);
		
		// Draw current unit
		if (highlight != null){
			ExpeditionItem eitem = (ExpeditionItem) highlight.getItem();
			// Get some info
			Image unitImage = ((GFXAppearance)eitem.getAppearance()).getImage();
			String itemDescription = eitem.getDescription();

			y = 200;

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

				selectedQuantity = 0;
			    quantityLabel.setText(selectedQuantity+"");

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
		si.refresh();
	}
	
	private void drawContainerInfo(int x, int y, ItemContainer container) {
		si.drawImage(x + 12, y + 17, ((GFXAppearance)container.getAppearance()).getImage());
		
		si.printAtPixel(x+12, y + 92, "Food Days", OryxExpeditionDisplay.COLOR_BOLD);
		si.printAtPixel(x+12, y + 106, "Water Days", OryxExpeditionDisplay.COLOR_BOLD);
		si.printAtPixel(x+146, y + 92, container.getFoodDays()+"", Color.WHITE);
		si.printAtPixel(x+146, y + 106, container.getWaterDays()+"", Color.WHITE);
		
		if (to instanceof ShipCache){
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
			si.printAtPixel(x+41, y + 47, "Carrying", OryxExpeditionDisplay.COLOR_BOLD);
			si.printAtPixel(x+12, y + 62, "Capacity", OryxExpeditionDisplay.COLOR_BOLD);
			si.printAtPixel(x+12, y + 77, "People", OryxExpeditionDisplay.COLOR_BOLD);
			si.printAtPixel(x+146, y + 47, container.getCurrentlyCarrying()+"", Color.WHITE);
			si.printAtPixel(x+146, y + 62, container.getCarryCapacity()+"", Color.WHITE);
			si.printAtPixel(x+146, y + 77, container.getTotalUnits()+"", Color.WHITE);
		}
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
		si.remove(transferButton);
		si.remove(quantityLabel);
		si.removeKeyListener(cbkl);
		si.removeMouseListener(cbml);
	}

	public void selectUnit(int index) {
		highlight = ((CacheCustomGFXMenuItem)items.get(index)).getEquipment();
		//draw(highlight);
	}

	public Equipment getSelectedUnit() {
		return highlight;
	}

	public int getQuantity() {
		return selectedQuantity;
	}
}
