package net.slashie.expedition.ui.oryx;

import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.Timer;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.Expedition.MovementMode;
import net.slashie.expedition.level.GlobeMapModel;
import net.slashie.expedition.world.CardinalDirection;
import net.slashie.expedition.world.ExpeditionMicroLevel;
import net.slashie.serf.action.Action;
import net.slashie.serf.action.Actor;
import net.slashie.serf.ui.UserAction;
import net.slashie.serf.ui.oryxUI.Assets;
import net.slashie.serf.ui.oryxUI.GFXUISelector;
import net.slashie.serf.ui.oryxUI.GFXUserInterface;
import net.slashie.serf.ui.oryxUI.SwingSystemInterface;
import net.slashie.utils.Position;
import net.slashie.utils.swing.CallbackActionListener;
import net.slashie.utils.swing.CallbackMouseListener;
import net.slashie.utils.swing.CleanButton;

@SuppressWarnings("serial")
public class ExpeditionGFXUISelector extends GFXUISelector{
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
	private CleanButton logButton;
	private CleanButton anchorButton;
	private CleanButton musicButton;
	private CleanButton sfxButton;
	private Image unmountImage;
	private Image disarmImage;
	private Image mountImage;
	private Image armImage;
	
	private Cursor HAND_CURSOR;
	
	private JPanel buttonsPanel;
	
	@Override
	public void init(SwingSystemInterface psi, UserAction[] gameActions,
			Properties uiProperties, Action advance, Action target,
			Action attack, GFXUserInterface ui, Properties keyBindings, Assets assets) {
		super.init(psi, gameActions, uiProperties, advance, target, attack, ui, keyBindings, assets);
		Layout layout = new Layout();
		layout.initialize(uiProperties);
		
		HAND_CURSOR = assets.getCursorAsset("HAND_CURSOR");
		
		
		Image smallButtonBack = assets.getImageAsset("IMG_SMALL_BUTTON_BACK");
		Image smallButtonHover = assets.getImageAsset("IMG_SMALL_BUTTON_HOVER_BACK");
		
		buildButton = new CleanButton(smallButtonBack, smallButtonHover, assets.getImageAsset("BTN_BUILD"), HAND_CURSOR);
		dropButton = new CleanButton(smallButtonBack, smallButtonHover, assets.getImageAsset("BTN_DROP"), HAND_CURSOR);
		inventoryButton = new CleanButton(smallButtonBack, smallButtonHover, assets.getImageAsset("BTN_INVENTORY"), HAND_CURSOR);
		lookButton = new CleanButton(smallButtonBack, smallButtonHover, assets.getImageAsset("BTN_LOOK"), HAND_CURSOR);
		
		armImage = assets.getImageAsset("BTN_ARM");
		mountImage = assets.getImageAsset("BTN_MOUNT");
		unmountImage = assets.getImageAsset("BTN_UNMOUNT");
		disarmImage = assets.getImageAsset("BTN_DISARM");
		
		armButton = new CleanButton(smallButtonBack, smallButtonHover, armImage, HAND_CURSOR);			
		mountButton = new CleanButton(smallButtonBack, smallButtonHover, mountImage, HAND_CURSOR);
		
		repairButton = new CleanButton(smallButtonBack, smallButtonHover, assets.getImageAsset("BTN_REPAIR"), HAND_CURSOR);
		resetButton = new CleanButton(smallButtonBack, smallButtonHover, assets.getImageAsset("BTN_RESET"), HAND_CURSOR);
		chopButton = new CleanButton(smallButtonBack, smallButtonHover, assets.getImageAsset("BTN_CHOP"), HAND_CURSOR);
		anchorButton = new CleanButton(smallButtonBack, smallButtonHover, assets.getImageAsset("BTN_ANCHOR"), HAND_CURSOR);
		
		musicButton = new CleanButton(null, assets.getImageAsset("BTN_MUSIC"), assets.getImageAsset("BTN_MUSIC"), HAND_CURSOR);
		sfxButton = new CleanButton(null, assets.getImageAsset("BTN_SFX"), assets.getImageAsset("BTN_SFX"), HAND_CURSOR);
		saveButton = new CleanButton(null, assets.getImageAsset("BTN_SAVE"), assets.getImageAsset("BTN_SAVE"), HAND_CURSOR);
		quitButton = new CleanButton(null, assets.getImageAsset("BTN_QUIT"), assets.getImageAsset("BTN_QUIT"), HAND_CURSOR);
		logButton = new CleanButton(null, assets.getImageAsset("BTN_LOG"), assets.getImageAsset("BTN_LOG"), HAND_CURSOR);
		
		
		armButton.addActionListener(getStringCallBackActionListener(selectionHandler, "KEY:"+keyBindings.getProperty("ARM_EXPEDITION_KEY")));
		buildButton.addActionListener(getStringCallBackActionListener(selectionHandler, "KEY:"+keyBindings.getProperty("BUILD_SETTLEMENT_KEY")));
		dropButton.addActionListener(getStringCallBackActionListener(selectionHandler, "KEY:"+keyBindings.getProperty("DROP_EQUIPMENT_KEY")));
		inventoryButton.addActionListener(getStringCallBackActionListener(selectionHandler, "KEY:"+keyBindings.getProperty("SHOW_INVENTORY_KEY")));
		lookButton.addActionListener(getStringCallBackActionListener(selectionHandler, "KEY:"+keyBindings.getProperty("LOOK_KEY")));
		mountButton.addActionListener(getStringCallBackActionListener(selectionHandler, "KEY:"+keyBindings.getProperty("MOUNT_KEY")));
		repairButton.addActionListener(getStringCallBackActionListener(selectionHandler, "KEY:"+keyBindings.getProperty("REPAIR_SHIPS_KEY")));
		resetButton.addActionListener(getStringCallBackActionListener(selectionHandler, "KEY:"+keyBindings.getProperty("RESET_RECKON_KEY")));
		chopButton.addActionListener(getStringCallBackActionListener(selectionHandler, "KEY:"+keyBindings.getProperty("CHOP_WOODS_KEY")));
		anchorButton.addActionListener(getStringCallBackActionListener(selectionHandler, "KEY:"+keyBindings.getProperty("ANCHOR_KEY")));
		musicButton.addActionListener(getStringCallBackActionListener(selectionHandler, "KEY:"+keyBindings.getProperty("SWITCH_MUSIC_KEY")));
		sfxButton.addActionListener(getStringCallBackActionListener(selectionHandler, "KEY:"+keyBindings.getProperty("SWITCH_SFX_KEY")));
		saveButton.addActionListener(getStringCallBackActionListener(selectionHandler, "KEY:"+keyBindings.getProperty("PROMPT_SAVE_KEY")));
		quitButton.addActionListener(getStringCallBackActionListener(selectionHandler, "KEY:"+keyBindings.getProperty("QUIT_KEY")));
		logButton.addActionListener(getStringCallBackActionListener(selectionHandler, "KEY:"+keyBindings.getProperty("MESSAGE_LOG_KEY")));
		
		armButton.setPopupText("Arm Expedition (a)");
		buildButton.setPopupText("Build Settlement (b)");
		dropButton.setPopupText("Transfer Equipment (d)");
		inventoryButton.setPopupText("Inventory (i)");
		lookButton.setPopupText("Examine Surroundings (l)");
		mountButton.setPopupText("Ride Mounts (m)");
		repairButton.setPopupText("Repair Ships (r)");
		resetButton.setPopupText("Reset Dead' Reckon (R)");
		chopButton.setPopupText("Chop Woods (w)");
		anchorButton.setPopupText("Anchor Ships (A)");
		
		musicButton.setPopupText("Switch Music (m)");
		sfxButton.setPopupText("Switch SFX (f)");
		saveButton.setPopupText("Save and Quit (S)");
		quitButton.setPopupText("Quit (Q)");
		logButton.setPopupText("Message Log (L)");
		
		armButton.setVisible(true);
		buildButton.setVisible(true);
		dropButton.setVisible(true);
		inventoryButton.setVisible(true);
		lookButton.setVisible(true);
		mountButton.setVisible(true);
		repairButton.setVisible(true);
		resetButton.setVisible(true);
		chopButton.setVisible(true);
		anchorButton.setVisible(true);
		
		buttonsPanel = new JPanel();
		buttonsPanel.setOpaque(false);
		//buttonsPanel.setLayout(new GridLayout(3,4));
		buttonsPanel.setLayout(new FlowLayout());
		buttonsPanel.setBounds(layout.ACTIONS_PANEL_BOUNDS);
		
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
		
		// Add these in the border
		musicButton.setVisible(false);
		sfxButton.setVisible(false);
		saveButton.setVisible(false);
		quitButton.setVisible(false);
		logButton.setVisible(false);
		
		logButton.setLocation(layout.POS_JOURNAL_ACTION.x, layout.POS_JOURNAL_ACTION.y);
		musicButton.setLocation(layout.POS_MUSIC_ACTION.x, layout.POS_MUSIC_ACTION.y);
		sfxButton.setLocation(layout.POS_SFX_ACTION.x, layout.POS_SFX_ACTION.y);
		saveButton.setLocation(layout.POS_SAVE_ACTION.x, layout.POS_SAVE_ACTION.y);
		quitButton.setLocation(layout.POS_EXIT_ACTION.x, layout.POS_EXIT_ACTION.y);
		
		si.add(musicButton);
		si.add(sfxButton);
		si.add(saveButton);
		si.add(quitButton);
		si.add(logButton);
		
		buttonsPanel.setVisible(false);
		si.add(buttonsPanel);
	}

	@Override
	public void activate() {
		super.activate();
		// Reenable buttons
		armButton.setEnabled(true);
		inventoryButton.setEnabled(true);
		lookButton.setEnabled(true);
		buildButton.setEnabled(true);
		dropButton.setEnabled(true);
		mountButton.setEnabled(true);
		repairButton.setEnabled(true);
		resetButton.setEnabled(true);
		chopButton.setEnabled(true);
		anchorButton.setEnabled(true);
		musicButton.setEnabled(true);
		sfxButton.setEnabled(true);
		saveButton.setEnabled(true);
		quitButton.setEnabled(true);
		logButton.setEnabled(true);
		
		// Make buttons visible
		buttonsPanel.setVisible(true);
		musicButton.setVisible(true);
		sfxButton.setVisible(true);
		saveButton.setVisible(true);
		quitButton.setVisible(true);
		logButton.setVisible(true);
		updateButtonStatus();
	}
	
	public void shutdown() {
		buttonsPanel.setVisible(false);
		musicButton.setVisible(false);
		sfxButton.setVisible(false);
		saveButton.setVisible(false);
		quitButton.setVisible(false);
		logButton.setVisible(false);
	}
		
	@Override
	public void deactivate() {
		super.deactivate();
		buttonsPanel.setEnabled(false);

		armButton.setEnabled(false);
		buildButton.setEnabled(false);
		dropButton.setEnabled(false);
		inventoryButton.setEnabled(false);
		lookButton.setEnabled(false);
		mountButton.setEnabled(false);
		repairButton.setEnabled(false);
		resetButton.setEnabled(false);
		anchorButton.setEnabled(false);
		chopButton.setEnabled(false);
		
		musicButton.setEnabled(false);
		sfxButton.setEnabled(false);
		saveButton.setEnabled(false);
		quitButton.setEnabled(false);
		logButton.setEnabled(false);

		
		
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
	
	@Override
	public Action selectAction(Actor who) {
		updateButtonStatus();
		return super.selectAction(who);
	}
	
	private void updateButtonStatus(){
		Expedition expedition = (Expedition) getUI().getPlayer();
		boolean doRevalidate = false;
		if (expedition.isMounted()){
			mountButton.setVisible(true);
			mountButton.setFace(unmountImage);
			mountButton.setPopupText("Unmount (m)");
			doRevalidate = true;
		} else if (expedition.getItemCountBasic("HORSE") > 0){
			mountButton.setVisible(true);
			mountButton.setFace(mountImage);
			mountButton.setPopupText("Ride Mounts (m)");
			doRevalidate = true;
		} else {
			mountButton.setVisible(false);
		}
		
		if (expedition.isArmed()){
			armButton.setFace(disarmImage);
			armButton.setPopupText("Disarm Expedition (a)");
			doRevalidate = true;
		} else {
			armButton.setFace(armImage);
			armButton.setPopupText("Arm Expedition (a)");
			doRevalidate = true;
		}
		
		if (doRevalidate){
			si.revalidate();
		}
		
		if (expedition.isAnchored()){
			anchorButton.setPopupText("Weigh Anchors (A)");
		} else {
			anchorButton.setPopupText("Drop Anchors (A)");
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
			dropButton.setPopupText("Caché Equipment (d)");
			repairButton.setVisible(false);
			resetButton.setVisible(true);
			chopButton.setVisible(true);
			anchorButton.setVisible(false);
		} else {
			buildButton.setVisible(false);
			dropButton.setVisible(true);
			dropButton.setPopupText("Landfall / Drop (d)");
			repairButton.setVisible(true);
			resetButton.setVisible(true);
			chopButton.setVisible(false);
			anchorButton.setVisible(true);
		}
	}

	private void performMovement() {
		if (!selectionActive)
			return;
		// If there's a command already on the queue, do naught to avoid confusion
		if (selectionHandler.size() > 0)
			return;
		
		int quadrant = defineQuadrant(mousePosition.x, mousePosition.y);
		mouseDirection = QDIRECTIONS[quadrant-1];
		
		Expedition expedition = (Expedition) getUI().getPlayer();				
		if (expedition.getMovementMode() == MovementMode.SHIP){
			// Compare with heading and move in the correct direction
			CardinalDirection heading = expedition.getHeading();
			CardinalDirection wantedHeading = CardinalDirection.translateFromActionDirection(mouseDirection);
			//System.out.println("WH: "+wantedHeading+", H: "+heading);
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
	
	private Position _advanceInDirection = new Position(0,0);
	protected Action advanceInDirection(int direction) {
		Position variation = Action.directionToVariation(direction);
		_advanceInDirection.x = variation.x * GlobeMapModel.getSingleton().getLongitudeScale(player.getPosition().y);
		_advanceInDirection.y = variation.y * GlobeMapModel.getSingleton().getLatitudeHeight() * -1;
		Actor vMonster = player.getLevel().getActorAt(Position.add(player.getPosition(), _advanceInDirection));
		if (vMonster != null && vMonster.isHostile() && attack.canPerform(player)){
			attack.setDirection(direction);
			return attack;
		} else {
			advance.setDirection(direction);
			switch (direction){
			case Action.UPLEFT:
			case Action.LEFT:
			case Action.DOWNLEFT:
				ui().setFlipFacing(true);
				break;
			case Action.UPRIGHT:
			case Action.RIGHT:
			case Action.DOWNRIGHT:
				ui().setFlipFacing(false);
				break;
			}
			if (advance.canPerform(player)){
				return advance;
			} else {
				player.getLevel().addMessage(advance.getInvalidationMessage());
				return null;
			}
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
				if (e.getY() <= 26 && e.getX() >= si.getScreenWidth() - 26)
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
