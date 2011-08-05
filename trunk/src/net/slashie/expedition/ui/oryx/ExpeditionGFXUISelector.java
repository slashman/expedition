package net.slashie.expedition.ui.oryx;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.Expedition.MovementMode;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.world.CardinalDirection;
import net.slashie.expedition.world.ExpeditionMicroLevel;
import net.slashie.libjcsi.CharKey;
import net.slashie.serf.action.Action;
import net.slashie.serf.action.Actor;
import net.slashie.serf.ui.UserAction;
import net.slashie.serf.ui.oryxUI.GFXUISelector;
import net.slashie.serf.ui.oryxUI.GFXUserInterface;
import net.slashie.serf.ui.oryxUI.SwingSystemInterface;
import net.slashie.utils.Position;
import net.slashie.utils.PropertyFilters;
import net.slashie.utils.swing.CallbackActionListener;
import net.slashie.utils.swing.CallbackMouseListener;
import net.slashie.utils.swing.CleanButton;

public class ExpeditionGFXUISelector extends GFXUISelector{
	private static final long serialVersionUID = 1L;
	private CleanButton armButton;
	private CleanButton buildButton;
	private CleanButton dropButton;
	private CleanButton inventoryButton;
	private CleanButton lookButton;
	private CleanButton mountButton;
	private CleanButton repairButton;
	private CleanButton resetButton;
	private CleanButton chopButton;
	private CleanButton saveButton;
	private CleanButton quitButton;
	private CleanButton anchorButton;
	private CleanButton musicButton;
	private Image unmountImage;
	private Image disarmImage;
	private Image mountImage;
	private Image armImage;
	
	private Cursor HAND_CURSOR;
	
	private JPanel buttonsPanel;
	
	@Override
	public void init(SwingSystemInterface psi, UserAction[] gameActions,
			Properties uiProperties, Action advance, Action target,
			Action attack, GFXUserInterface ui, Properties keyBindings) {
		super.init(psi, gameActions, uiProperties, advance, target, attack, ui,
				keyBindings);
		HAND_CURSOR = GFXUserInterface.createCursor(uiProperties.getProperty("IMG_CURSORS"), 6, 2, 10, 4);
		
		try {
			Image smallButtonBack = PropertyFilters.getImage(uiProperties.getProperty("IMG_SMALL_BUTTON"), uiProperties.getProperty("IMG_SMALL_BUTTON_BACK_BOUNDS"));
			Image smallButtonHover = PropertyFilters.getImage(uiProperties.getProperty("IMG_SMALL_BUTTON"), uiProperties.getProperty("IMG_SMALL_BUTTON_HOVER_BACK_BOUNDS"));

			
			buildButton = new CleanButton(smallButtonBack, smallButtonHover, PropertyFilters.getImage(uiProperties.getProperty("IMG_UI"), uiProperties.getProperty("BTN_BUILD_BOUNDS")), HAND_CURSOR);
			dropButton = new CleanButton(smallButtonBack, smallButtonHover, PropertyFilters.getImage(uiProperties.getProperty("IMG_UI"), uiProperties.getProperty("BTN_DROP_BOUNDS")), HAND_CURSOR);
			inventoryButton = new CleanButton(smallButtonBack, smallButtonHover, PropertyFilters.getImage(uiProperties.getProperty("IMG_UI"), uiProperties.getProperty("BTN_INVENTORY_BOUNDS")), HAND_CURSOR);
			lookButton = new CleanButton(smallButtonBack, smallButtonHover, PropertyFilters.getImage(uiProperties.getProperty("IMG_UI"), uiProperties.getProperty("BTN_LOOK_BOUNDS")), HAND_CURSOR);
			
			armImage = PropertyFilters.getImage(uiProperties.getProperty("IMG_UI"), uiProperties.getProperty("BTN_ARM_BOUNDS"));
			mountImage = PropertyFilters.getImage(uiProperties.getProperty("IMG_UI"), uiProperties.getProperty("BTN_MOUNT_BOUNDS"));
			unmountImage = PropertyFilters.getImage(uiProperties.getProperty("IMG_UI"), uiProperties.getProperty("BTN_UNMOUNT_BOUNDS"));
			disarmImage = PropertyFilters.getImage(uiProperties.getProperty("IMG_UI"), uiProperties.getProperty("BTN_DISARM_BOUNDS"));
			
			armButton = new CleanButton(smallButtonBack, smallButtonHover, armImage, HAND_CURSOR);			
			mountButton = new CleanButton(smallButtonBack, smallButtonHover, mountImage, HAND_CURSOR);
			
			repairButton = new CleanButton(smallButtonBack, smallButtonHover, PropertyFilters.getImage(uiProperties.getProperty("IMG_UI"), uiProperties.getProperty("BTN_REPAIR_BOUNDS")), HAND_CURSOR);
			resetButton = new CleanButton(smallButtonBack, smallButtonHover, PropertyFilters.getImage(uiProperties.getProperty("IMG_UI"), uiProperties.getProperty("BTN_RESET_BOUNDS")), HAND_CURSOR);
			chopButton = new CleanButton(smallButtonBack, smallButtonHover, PropertyFilters.getImage(uiProperties.getProperty("IMG_UI"), uiProperties.getProperty("BTN_CHOP_BOUNDS")), HAND_CURSOR);
			anchorButton = new CleanButton(smallButtonBack, smallButtonHover, PropertyFilters.getImage(uiProperties.getProperty("IMG_UI"), uiProperties.getProperty("BTN_ANCHOR_BOUNDS")), HAND_CURSOR);
			musicButton = new CleanButton(smallButtonBack, smallButtonHover, PropertyFilters.getImage(uiProperties.getProperty("IMG_UI"), uiProperties.getProperty("BTN_MUSIC_BOUNDS")), HAND_CURSOR);
			saveButton = new CleanButton(smallButtonBack, smallButtonHover, PropertyFilters.getImage(uiProperties.getProperty("IMG_UI"), uiProperties.getProperty("BTN_SAVE_BOUNDS")), HAND_CURSOR);
			quitButton = new CleanButton(smallButtonBack, smallButtonHover, PropertyFilters.getImage(uiProperties.getProperty("IMG_UI"), uiProperties.getProperty("BTN_QUIT_BOUNDS")), HAND_CURSOR);
		} catch (IOException e) {
			ExpeditionGame.crash("Error loading buttons", e);
		}
		armButton.addActionListener(getStringCallBackActionListener(selectionHandler, "KEY:"+CharKey.a));
		buildButton.addActionListener(getStringCallBackActionListener(selectionHandler, "KEY:"+CharKey.b));
		dropButton.addActionListener(getStringCallBackActionListener(selectionHandler, "KEY:"+CharKey.d));
		inventoryButton.addActionListener(getStringCallBackActionListener(selectionHandler, "KEY:"+CharKey.i));
		lookButton.addActionListener(getStringCallBackActionListener(selectionHandler, "KEY:"+CharKey.l));
		mountButton.addActionListener(getStringCallBackActionListener(selectionHandler, "KEY:"+CharKey.m));
		repairButton.addActionListener(getStringCallBackActionListener(selectionHandler, "KEY:"+CharKey.r));
		resetButton.addActionListener(getStringCallBackActionListener(selectionHandler, "KEY:"+CharKey.R));
		chopButton.addActionListener(getStringCallBackActionListener(selectionHandler, "KEY:"+CharKey.w));
		anchorButton.addActionListener(getStringCallBackActionListener(selectionHandler, "KEY:"+CharKey.A));
		musicButton.addActionListener(getStringCallBackActionListener(selectionHandler, "KEY:"+CharKey.T));
		saveButton.addActionListener(getStringCallBackActionListener(selectionHandler, "KEY:"+CharKey.S));
		quitButton.addActionListener(getStringCallBackActionListener(selectionHandler, "KEY:"+CharKey.Q));
		
		armButton.setPopupText("Arm Expedition");
		buildButton.setPopupText("Build Settlement");
		dropButton.setPopupText("Transfer Equipment");
		inventoryButton.setPopupText("Inventory");
		lookButton.setPopupText("Examine Surroundings");
		mountButton.setPopupText("Ride Mounts");
		repairButton.setPopupText("Repair Ships");
		resetButton.setPopupText("Reset Dead' Reckon");
		chopButton.setPopupText("Chop Woods");
		anchorButton.setPopupText("Anchor Ships");
		musicButton.setPopupText("Switch Music");
		saveButton.setPopupText("Save");
		quitButton.setPopupText("Quit");
		
		armButton.setVisible(false);
		buildButton.setVisible(false);
		dropButton.setVisible(false);
		inventoryButton.setVisible(false);
		lookButton.setVisible(false);
		mountButton.setVisible(false);
		repairButton.setVisible(false);
		resetButton.setVisible(false);
		chopButton.setVisible(false);
		anchorButton.setVisible(false);
		musicButton.setVisible(false);
		saveButton.setVisible(false);
		quitButton.setVisible(false);
		
		buttonsPanel = new JPanel();
		buttonsPanel.setOpaque(false);
		//buttonsPanel.setLayout(new GridLayout(3,4));
		buttonsPanel.setLayout(new FlowLayout());
		buttonsPanel.setLocation(12,350);
		buttonsPanel.setSize(144,178);
		
		
		buttonsPanel.add(inventoryButton);
		buttonsPanel.add(lookButton);
		buttonsPanel.add(dropButton);
		
		buttonsPanel.add(armButton);
		buttonsPanel.add(mountButton);
		
		buttonsPanel.add(chopButton);
		buttonsPanel.add(buildButton);
		
		buttonsPanel.add(repairButton);
		buttonsPanel.add(resetButton);
		buttonsPanel.add(anchorButton);
		
		buttonsPanel.add(musicButton);
		buttonsPanel.add(saveButton);
		buttonsPanel.add(quitButton);
		
		
		si.add(buttonsPanel);
	}
	/*
	private MouseListener getPopupMouseListener(final CleanButton cleanButton) {
		return new MouseAdapter(){
			@Override
			public void mouseEntered(MouseEvent e) {
				//setSelectionActive(false);
				Component b = (Component) e.getSource();
				legendLabel.setText(cleanButton.getPopupText());
				legendLabel.setLocation(b.getX()+36+12, b.getY()+18+350);
				legendLabel.setVisible(true);
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				legendLabel.setVisible(false);
			}
		};
	}*/

	@Override
	public void activate() {
		super.activate();
		buttonsPanel.setVisible(true);
		
		armButton.setVisible(true);
		//buildButton.setVisible(true);
		//dropButton.setVisible(true);
		inventoryButton.setVisible(true);
		lookButton.setVisible(true);
		//mountButton.setVisible(true);
		//repairButton.setVisible(true);
		//resetButton.setVisible(true);
		//chopButton.setVisible(true);
		musicButton.setVisible(true);
		saveButton.setVisible(true);
		quitButton.setVisible(true);
		updateButtonStatus();
	}
	
	@Override
	public void deactivate() {
		super.deactivate();
		buttonsPanel.setVisible(false);

		armButton.setVisible(false);
		buildButton.setVisible(false);
		dropButton.setVisible(false);
		inventoryButton.setVisible(false);
		lookButton.setVisible(false);
		mountButton.setVisible(false);
		repairButton.setVisible(false);
		resetButton.setVisible(false);
		anchorButton.setVisible(false);
		chopButton.setVisible(false);
		saveButton.setVisible(false);
		quitButton.setVisible(false);
	}

	private CallbackActionListener<String> getStringCallBackActionListener (BlockingQueue<String> queue, final String option) {
		return new CallbackActionListener<String>(queue) {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					handler.put(option);
				} catch (InterruptedException e1) {}
				// legendLabel.setVisible(false); HERE
				si.recoverFocus();
			}
		};
	}
	
	@Override
	public Action selectAction(Actor who) {
		updateButtonStatus();
		return super.selectAction(who);
	}
	
	private void updateButtonStatus(){
		Expedition expedition = (Expedition) getUI().getPlayer();
		
		if (expedition.isMounted()){
			mountButton.setVisible(true);
			mountButton.setFace(unmountImage);
			mountButton.setPopupText("Unmount");
			si.revalidate();
		} else if (expedition.getItemCountBasic("HORSE") > 0){
			mountButton.setVisible(true);
			mountButton.setFace(mountImage);
			mountButton.setPopupText("Ride Mounts");
			si.revalidate();
		} else {
			mountButton.setVisible(false);
		}
		
		if (expedition.isArmed()){
			armButton.setFace(disarmImage);
			armButton.setPopupText("Disarm Expedition");
			si.revalidate();
		} else {
			armButton.setFace(armImage);
			armButton.setPopupText("Arm Expedition");
			si.revalidate();
		}
		
		if (expedition.isAnchored()){
			anchorButton.setPopupText("Weigh Anchors");
		} else {
			anchorButton.setPopupText("Drop Anchors");
		}
		
		
		if (expedition.getLevel() instanceof ExpeditionMicroLevel){
			buildButton.setVisible(false);
			dropButton.setVisible(false);
			repairButton.setVisible(false);
			resetButton.setVisible(false);
			chopButton.setVisible(false);
			anchorButton.setVisible(false);
		} else if (expedition.getMovementMode().isLandMovement()){
			buildButton.setVisible(true);
			dropButton.setVisible(true);
			dropButton.setPopupText("Caché Equipment");
			repairButton.setVisible(false);
			resetButton.setVisible(true);
			chopButton.setVisible(true);
			anchorButton.setVisible(false);
		} else {
			buildButton.setVisible(false);
			dropButton.setVisible(true);
			dropButton.setPopupText("Landfall / Drop");
			repairButton.setVisible(true);
			resetButton.setVisible(true);
			chopButton.setVisible(false);
			anchorButton.setVisible(true);
		}
	}

	private void performMovement() {
		if (!selectionActive)
			return;
		int quadrant = defineQuadrant(mousePosition.x, mousePosition.y);
		mouseDirection = QDIRECTIONS[quadrant-1];
		
		Expedition expedition = (Expedition) getUI().getPlayer();				
		if (expedition.getMovementMode() == MovementMode.SHIP){
			// Compare with heading and move in the correct direction
			CardinalDirection heading = expedition.getHeading();
			CardinalDirection wantedHeading = CardinalDirection.translateFromActionDirection(mouseDirection);
			if (wantedHeading == CardinalDirection.NULL){
				// move forward
				try {
					selectionHandler.put("MOUSE_MOVE:"+Action.UP);
					mouseDirection = Action.UP;
				} catch (InterruptedException e1) {}
			} else if (heading.getReferenceAngle() != wantedHeading.getReferenceAngle()){
				int wantedAngle = wantedHeading.getReferenceAngle();
				int headingAngle = heading.getReferenceAngle();
				
				// Normalize angles 
				wantedAngle = wantedAngle - headingAngle;
				if (wantedAngle < 0)
					wantedAngle += 360;
					
				int degreeDifference = wantedAngle;
				try {
					if (degreeDifference < 180){
						selectionHandler.put("MOUSE_MOVE:"+Action.LEFT);
						mouseDirection = Action.LEFT;
					} else {
						selectionHandler.put("MOUSE_MOVE:"+Action.RIGHT);
						mouseDirection = Action.RIGHT;
					}
				} catch (InterruptedException e) {}
			} else {
				// move forward
				try {
					selectionHandler.put("MOUSE_MOVE:"+Action.UP);
					mouseDirection = Action.UP;
				} catch (InterruptedException e1) {}
			}
		} else {
			// Normal movement
			try {
				selectionHandler.put("MOUSE_MOVE:"+mouseDirection);
			} catch (InterruptedException e1) {}
		}
	}
	
	@Override
	protected MouseListener getMouseClickListener(BlockingQueue<String> selectionHandler_) {
		javax.swing.Action gotoDirectionAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent arg0) {
				performMovement();
			}
		};
		final Timer gotoDirectionTimer = new Timer(200, gotoDirectionAction);
		return new CallbackMouseListener<String>(selectionHandler_){
			public void mousePressed(final MouseEvent e) {
				if (!selectionActive)
					return;
				if (e.getY() <= 26 && e.getX() >= 800 - 26)
					return;
				if (e.getButton() == MouseEvent.BUTTON1){
					mousePosition = e.getPoint();
					performMovement();
					gotoDirectionTimer.start();
				} else if (e.getButton() == MouseEvent.BUTTON3){
					Position p = translatePosition(e.getPoint().x, e.getPoint().y);
					try {
						handler.put("MOUSE:"+p.x+":"+p.y);
					} catch (InterruptedException e1) {}
				}
			}

			public void mouseReleased(MouseEvent e) {
				mouseDirection = -1;
				gotoDirectionTimer.stop();
			}
			
			private Position tempRel = new Position(0,0);
			private Position translatePosition(int x, int y){
				int bigx = (int)Math.ceil(x/32.0);
				int bigy = (int)Math.ceil(y/32.0);
				tempRel.x = bigx-ui().PC_POS.x-1;
				tempRel.y = bigy-ui().PC_POS.y-1;
				return Position.add(player.getPosition(), tempRel);
			}
		};
	}
}
