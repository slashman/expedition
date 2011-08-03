package net.slashie.expedition.ui.oryx;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.concurrent.BlockingQueue;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;

import net.slashie.libjcsi.CharKey;
import net.slashie.serf.ui.UserInterface;
import net.slashie.serf.ui.oryxUI.GFXUserInterface;
import net.slashie.serf.ui.oryxUI.SwingSystemInterface;
import net.slashie.utils.swing.BorderedGridBox;
import net.slashie.utils.swing.CallbackActionListener;
import net.slashie.utils.swing.CallbackKeyListener;
import net.slashie.utils.swing.CleanButton;

public class BuildingPlanBorderGridBox extends BorderedGridBox{
		private static final long serialVersionUID = 1L;
		private ExpeditionCleanButton okButton;
		private BuildingCustomGFXMenuItem selectedBuilding;
		
		// Splitter attributes
		private CleanButton quantitySplitterUp;
		private CleanButton quantitySplitterDown;
		private MouseMotionAdapter mml;
		private CallbackKeyListener<String> cbkl;
		
		public BuildingPlanBorderGridBox(
				// Standard parameters, sent to super()
				BufferedImage border1, BufferedImage border2,
				BufferedImage border3, BufferedImage border4,
				SwingSystemInterface g, Color backgroundColor, Color borderIn,
				Color borderOut, int borderWidth, int outsideBound, int inBound,
				int insideBound, final int itemHeight, final int itemWidth, int gridX, int gridY, 
				BlockingQueue<String> selectionHandler,
				CleanButton closeButton
				) {
			super(border1, border2, border3, border4, g, backgroundColor, borderIn,
					borderOut, borderWidth, outsideBound, inBound, insideBound, itemHeight,
					itemWidth, gridX, gridY, null, closeButton);
			initializeSplitters();
			
			/*quantityLabel = new JLabel();
			quantityLabel.setFont(si.getFont());
			quantityLabel.setVisible(false);
			quantityLabel.setBounds(540,231,200,27);
			quantityLabel.setForeground(Color.WHITE);
			si.add(quantityLabel);*/
			
			okButton = new ExpeditionCleanButton(4, "Build");
			okButton.setVisible(true);
			okButton.setLocation(615,370);
			si.add(okButton);
			
			okButton.addActionListener(new CallbackActionListener<String>(selectionHandler){
				@Override
				public void actionPerformed(ActionEvent e) {
					if (hoverDisabled)
						return;
					try {
						handler.put("OK");
					} catch (InterruptedException e1) {}
					si.recoverFocus();
				}
			});
			
			cbkl = new CallbackKeyListener<String>(selectionHandler){
				@Override
				public void keyPressed(KeyEvent e) {
					if (hoverDisabled)
						return;
					
					int code = SwingSystemInterface.charCode(e);
					if (code == CharKey.UARROW || code == CharKey.N8){
						rePag();
					} else if (code == CharKey.DARROW || code == CharKey.N2){
						avPag();
					}
				}
			};
			
			String[] legends = legend.split("XXX");
			int fontSize = getFont().getSize();
			final int lineHeight = (int)Math.round(fontSize*1.5);
			final int legendLines = legends.length > 0 ? legends.length: 1;
			
			mml = new MouseMotionAdapter(){
				public void mouseMoved(MouseEvent e) {
					draw(true);
					SelectedItem selectedItem = getSelectedItemByClick(e.getPoint(), legendLines, lineHeight);
					if (selectedItem != null){
						selectedBuilding = (BuildingCustomGFXMenuItem) shownItems.get(selectedItem.selectedIndex);
						// Move splitters and count label
						int xpos = selectedItem.cursorX * itemWidth + getLocation().x + getBorderWidth();
						int ypos = selectedItem.cursorY * itemHeight + getLocation().y + getBorderWidth() + (legendLines + 1) * lineHeight;
						quantitySplitterUp.setLocation(xpos + ExpeditionOryxUI.STANDARD_ITEM_WIDTH + 3, ypos + 17);
						quantitySplitterDown.setLocation(xpos + ExpeditionOryxUI.STANDARD_ITEM_WIDTH + 3, ypos + 44);
						/*quantityLabel.setLocation(xpos + ExpeditionOryxUI.STANDARD_ITEM_WIDTH + 43, ypos + 30);
						quantityLabel.setText(selectedBuilding.getQuantity()+"");
						quantityLabel.setVisible(true);*/
						quantitySplitterUp.setVisible(true);
						quantitySplitterDown.setVisible(true);
						
						
						
					} else {
						// No grid selected
						quantitySplitterUp.setVisible(false);
						quantitySplitterDown.setVisible(false);
						//quantityLabel.setVisible(false);
					}
				}
				
			};
			
			si.addKeyListener(cbkl);
			si.addMouseMotionListener(mml);
		}

		private void initializeSplitters() {
			quantitySplitterUp = new CleanButton(ExpeditionOryxUI.BTN_SPLIT_UP, ExpeditionOryxUI.BTN_SPLIT_UP_HOVER, null, ((GFXUserInterface)UserInterface.getUI()).getHandCursor());
			quantitySplitterUp.setVisible(false);
			quantitySplitterUp.setLocation(512,221);
			final Action increaseQuantityAction = new AbstractAction() {
				private static final long serialVersionUID = 1L;
				@Override
				public void actionPerformed(ActionEvent arg0) {
					selectedBuilding.add();
					//quantityLabel.setText(selectedBuilding.getQuantity()+"");
					draw(true);
				}
			};
			final Timer increaseQuantityTimer = new Timer(100, increaseQuantityAction);

			quantitySplitterUp.addMouseListener(new MouseAdapter(){
				@Override
				public void mousePressed(MouseEvent e){
					selectedBuilding.add();
					//quantityLabel.setText(selectedBuilding.getQuantity()+"");
					draw(true);
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
					selectedBuilding.remove();
					//quantityLabel.setText(selectedBuilding.getQuantity()+"");
					draw(true);
				}
			};
			final Timer decreaseQuantityTimer = new Timer(100, decreaseQuantityAction);

			quantitySplitterDown.addMouseListener(new MouseAdapter(){
				@Override
				public void mousePressed(MouseEvent e){
					selectedBuilding.remove();
					//quantityLabel.setText(selectedBuilding.getQuantity()+"");
					draw(true);
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

		@Override
		public void kill() {
			super.kill();
			si.remove(quantitySplitterUp);
			si.remove(quantitySplitterDown);
			si.remove(okButton);
			//si.remove(quantityLabel);
			si.removeKeyListener(cbkl);
			si.removeMouseMotionListener(mml);
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