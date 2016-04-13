package net.slashie.expedition.ui.oryx;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Properties;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import org.apache.log4j.Logger;
import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.Expedition.MovementMode;
import net.slashie.expedition.level.GlobeMapModel;
import net.slashie.expedition.world.CardinalDirection;
import net.slashie.expedition.world.ExpeditionMicroLevel;
import net.slashie.serf.action.Action;
import net.slashie.serf.action.Actor;
import net.slashie.serf.ui.UserAction;
import net.slashie.serf.ui.UserInterface.SoundCycle;
import net.slashie.serf.ui.oryxUI.Assets;
import net.slashie.serf.ui.oryxUI.ContinuousGFXUISelector;
import net.slashie.serf.ui.oryxUI.GFXUserInterface;
import net.slashie.serf.ui.oryxUI.SwingSystemInterface;
import net.slashie.utils.Position;
import net.slashie.utils.swing.CleanButton;

@SuppressWarnings("serial")
public class ExpeditionContinuousGFXUISelector extends ContinuousGFXUISelector
{
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
	private CleanButton searchAroundButton;
	private CleanButton musicButton;
	private CleanButton sfxButton;
	private Image unmountImage;
	private Image disarmImage;
	private Image mountImage;
	private Image armImage;
	final static Logger logger = Logger.getRootLogger();
	private Cursor HAND_CURSOR;

	private Image smallButtonBack;
	private Image smallButtonHover;
	private JPanel buttonsPanel;
	private JPanel commandPanel;
	private GFXUserInterface ui;

	@Override
	public void init(SwingSystemInterface psi, UserAction[] gameActions, Properties uiProperties, Action advance,
			Action target, Action attack, GFXUserInterface ui, Properties keyBindings, Assets assets)
	{
		super.init(psi, gameActions, uiProperties, advance, target, attack, ui, keyBindings, assets);
		Layout layout = new Layout();
		layout.initialize(uiProperties);

		HAND_CURSOR = assets.getCursorAsset("HAND_CURSOR");

		smallButtonBack = assets.getImageAsset("IMG_SMALL_BUTTON_BACK");
		smallButtonHover = assets.getImageAsset("IMG_SMALL_BUTTON_HOVER_BACK");
		armImage = assets.getImageAsset("BTN_ARM");
		mountImage = assets.getImageAsset("BTN_MOUNT");
		unmountImage = assets.getImageAsset("BTN_UNMOUNT");
		disarmImage = assets.getImageAsset("BTN_DISARM");

		initializeButtonsPanel(layout, assets, keyBindings);
		initializeCommandsPanel(layout, assets, keyBindings);

		this.ui = ui;
	}

	/**
	 * @param layout
	 *            PropertyFilters get layout read from expedition-denzi.ui file
	 * @param assets - graphics assets from uiassets
	 * @param keyBindings from keys.properties
	 */
	private void initializeCommandsPanel(Layout layout, Assets assets, Properties keyBindings)
	{
		commandPanel = new JPanel();
		commandPanel.setLayout(new GridLayout(5, 1));
		commandPanel.setBounds(layout.COMMAND_PANEL_BOUNDS);
		commandPanel.setOpaque(false);
		commandPanel.setBorder(BorderFactory.createLineBorder(Color.yellow));
		commandPanel.setCursor(HAND_CURSOR);

		musicButton = new CleanButton(null, assets.getImageAsset("BTN_MUSIC"), assets.getImageAsset("BTN_MUSIC"),
				HAND_CURSOR);
		sfxButton = new CleanButton(null, assets.getImageAsset("BTN_SFX"), assets.getImageAsset("BTN_SFX"),
				HAND_CURSOR);
		saveButton = new CleanButton(null, assets.getImageAsset("BTN_SAVE"), assets.getImageAsset("BTN_SAVE"),
				HAND_CURSOR);
		quitButton = new CleanButton(null, assets.getImageAsset("BTN_QUIT"), assets.getImageAsset("BTN_QUIT"),
				HAND_CURSOR);
		logButton = new CleanButton(null, assets.getImageAsset("BTN_LOG"), assets.getImageAsset("BTN_LOG"),
				HAND_CURSOR);
		musicButton.addActionListener(
				getStringCallBackActionListener(Integer.parseInt(keyBindings.getProperty("SWITCH_MUSIC_KEY"))));
		sfxButton.addActionListener(
				getStringCallBackActionListener(Integer.parseInt(keyBindings.getProperty("SWITCH_SFX_KEY"))));
		saveButton.addActionListener(
				getStringCallBackActionListener(Integer.parseInt(keyBindings.getProperty("PROMPT_SAVE_KEY"))));
		quitButton.addActionListener(
				getStringCallBackActionListener(Integer.parseInt(keyBindings.getProperty("QUIT_KEY"))));
		logButton.addActionListener(
				getStringCallBackActionListener(Integer.parseInt(keyBindings.getProperty("MESSAGE_LOG_KEY"))));

		musicButton.setPopupText("Switch Music (M)");
		sfxButton.setPopupText("Switch SFX (F)");
		saveButton.setPopupText("Save and Quit (S)");
		quitButton.setPopupText("Quit (Q)");
		logButton.setPopupText("Message Log (L)");

		
		musicButton.addMouseListener(new java.awt.event.MouseAdapter() {
		    public void mouseClicked(java.awt.event.MouseEvent evt) 
		    {
		    	if (ui.getCurrentSoundCycle().nextCycle() == SoundCycle.MUTE)
		    	{
		    		musicButton.setFace(assets.getImageAsset("BTN_MUSIC_DIS"));	
		    		musicButton.repaint();
		    	}
		    	else
		    	{
		    		musicButton.setFace(assets.getImageAsset("BTN_MUSIC"));
		    		musicButton.repaint();
		    	}
		    }
		});

		sfxButton.addMouseListener(new java.awt.event.MouseAdapter() {
		    public void mouseClicked(java.awt.event.MouseEvent evt) 
		    {
		    	if (ui.getCurrentSFXCycle().nextCycle() == SoundCycle.MUTE)
		    	{
		    		sfxButton.setFace(assets.getImageAsset("BTN_SFX_DIS"));	
		    		sfxButton.repaint();
		    	}
		    	else
		    	{
		    		sfxButton.setFace(assets.getImageAsset("BTN_SFX"));
		    		sfxButton.repaint();
		    	}
		    }
		});
		
		commandPanel.add(musicButton);
		commandPanel.add(sfxButton);
		commandPanel.add(saveButton);
		commandPanel.add(quitButton);
		commandPanel.add(logButton);
		commandPanel.setVisible(false);
		si.add(commandPanel);
	}

	/**
	 * @param layout
	 *            PropertyFilters get layout read from expedition-denzi.ui file
	 * @param assets - graphics assets from uiassets
	 * @param keyBindings keys.properties
	 */
	private void initializeButtonsPanel(Layout layout, Assets assets, Properties keyBindings)
	{
		buttonsPanel = new JPanel();
		buttonsPanel.setOpaque(false);		
		buttonsPanel.setLayout(new FlowLayout());
		buttonsPanel.setBorder(BorderFactory.createLineBorder(Color.white));
		buttonsPanel.setBounds(layout.ACTIONS_PANEL_BOUNDS);
		buttonsPanel.setCursor(HAND_CURSOR);
		buildButton = new CleanButton(smallButtonBack, smallButtonHover, assets.getImageAsset("BTN_BUILD"),
				HAND_CURSOR);
		dropButton = new CleanButton(smallButtonBack, smallButtonHover, assets.getImageAsset("BTN_DROP"), HAND_CURSOR);
		inventoryButton = new CleanButton(smallButtonBack, smallButtonHover, assets.getImageAsset("BTN_INVENTORY"),
				HAND_CURSOR);
		lookButton = new CleanButton(smallButtonBack, smallButtonHover, assets.getImageAsset("BTN_LOOK"), HAND_CURSOR);
		armButton = new CleanButton(smallButtonBack, smallButtonHover, armImage, HAND_CURSOR);
		mountButton = new CleanButton(smallButtonBack, smallButtonHover, mountImage, HAND_CURSOR);

		repairButton = new CleanButton(smallButtonBack, smallButtonHover, assets.getImageAsset("BTN_REPAIR"),
				HAND_CURSOR);
		resetButton = new CleanButton(smallButtonBack, smallButtonHover, assets.getImageAsset("BTN_RESET"),
				HAND_CURSOR);
		chopButton = new CleanButton(smallButtonBack, smallButtonHover, assets.getImageAsset("BTN_CHOP"), HAND_CURSOR);
		anchorButton = new CleanButton(smallButtonBack, smallButtonHover, assets.getImageAsset("BTN_ANCHOR"),
				HAND_CURSOR);
		searchAroundButton = new CleanButton(smallButtonBack, smallButtonHover, assets.getImageAsset("BTN_LOOK"),
				HAND_CURSOR);
		armButton.addActionListener(
				getStringCallBackActionListener(Integer.parseInt(keyBindings.getProperty("ARM_EXPEDITION_KEY"))));
		buildButton.addActionListener(
				getStringCallBackActionListener(Integer.parseInt(keyBindings.getProperty("BUILD_SETTLEMENT_KEY"))));
		dropButton.addActionListener(
				getStringCallBackActionListener(Integer.parseInt(keyBindings.getProperty("DROP_EQUIPMENT_KEY"))));
		inventoryButton.addActionListener(
				getStringCallBackActionListener(Integer.parseInt(keyBindings.getProperty("SHOW_INVENTORY_KEY"))));
		lookButton.addActionListener(
				getStringCallBackActionListener(Integer.parseInt(keyBindings.getProperty("LOOK_KEY"))));
		mountButton.addActionListener(
				getStringCallBackActionListener(Integer.parseInt(keyBindings.getProperty("MOUNT_KEY"))));
		repairButton.addActionListener(
				getStringCallBackActionListener(Integer.parseInt(keyBindings.getProperty("REPAIR_SHIPS_KEY"))));
		resetButton.addActionListener(
				getStringCallBackActionListener(Integer.parseInt(keyBindings.getProperty("RESET_RECKON_KEY"))));
		chopButton.addActionListener(
				getStringCallBackActionListener(Integer.parseInt(keyBindings.getProperty("CHOP_WOODS_KEY"))));
		anchorButton.addActionListener(
				getStringCallBackActionListener(Integer.parseInt(keyBindings.getProperty("ANCHOR_KEY"))));
		searchAroundButton.addActionListener(
				getStringCallBackActionListener(Integer.parseInt(keyBindings.getProperty("SEARCH_AROUND_KEY"))));
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
		searchAroundButton.setPopupText("Search Around (z)");

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
		buttonsPanel.add(searchAroundButton);
		buttonsPanel.setVisible(false);
		si.add(buttonsPanel);
	}

	@Override
	public void activate()
	{
		super.activate();

		commandPanel.setEnabled(true);
		buttonsPanel.setVisible(true);
		buttonsPanel.setEnabled(true);
		commandPanel.setVisible(true);
		updateButtonStatus();
	}

	public void shutdown()
	{
		super.shutdown();
		buttonsPanel.setVisible(false);
		commandPanel.setVisible(false);
	}

	@Override
	public void deactivate()
	{
		super.deactivate();
		buttonsPanel.setEnabled(false);
		commandPanel.setEnabled(false);
	}

	private ActionListener getStringCallBackActionListener(final int charkey)
	{
		return new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{				
				selectedCharCode = charkey;
				si.recoverFocus();
			}
		};
	}

	@Override
	public Action selectAction(Actor who)
	{
		updateButtonStatus();
		return super.selectAction(who);
	}

	private void updateButtonStatus()
	{
		Expedition expedition = (Expedition) getUI().getPlayer();
		boolean doRevalidate = false;
		if (expedition.isMounted())
		{
			mountButton.setVisible(true);
			mountButton.setFace(unmountImage);
			mountButton.setPopupText("Unmount (m)");
			doRevalidate = true;
		}
		else if (expedition.getItemCountBasic("HORSE") > 0)
		{
			mountButton.setVisible(true);
			mountButton.setFace(mountImage);
			mountButton.setPopupText("Ride Mounts (m)");
			doRevalidate = true;
		}
		else
		{
			mountButton.setVisible(false);
		}

		if (expedition.isArmed())
		{
			armButton.setFace(disarmImage);
			armButton.setPopupText("Disarm Expedition (a)");
			doRevalidate = true;
		}
		else
		{
			armButton.setFace(armImage);
			armButton.setPopupText("Arm Expedition (a)");
			doRevalidate = true;
		}

		if (expedition.isAnchored())
		{
			anchorButton.setPopupText("Weigh Anchors (A)");
		}
		else
		{
			anchorButton.setPopupText("Drop Anchors (A)");
		}

		if (expedition.getLevel() instanceof ExpeditionMicroLevel)
		{
			buildButton.setVisible(false);
			dropButton.setVisible(false);
			repairButton.setVisible(false);
			resetButton.setVisible(false);
			chopButton.setVisible(false);
			anchorButton.setVisible(false);
			searchAroundButton.setVisible(false);
		}
		else if (expedition.getMovementMode().isLandMovement())
		{
			buildButton.setVisible(true);
			dropButton.setVisible(true);
			repairButton.setVisible(false);
			resetButton.setVisible(true);
			chopButton.setVisible(true);
			anchorButton.setVisible(false);
			searchAroundButton.setVisible(true);
			dropButton.setPopupText("Cach� Equipment (d)");
		}
		else
		{
			buildButton.setVisible(false);
			dropButton.setVisible(true);
			repairButton.setVisible(true);
			resetButton.setVisible(true);
			chopButton.setVisible(false);
			anchorButton.setVisible(true);
			searchAroundButton.setVisible(false);
			dropButton.setPopupText("Make Landfall (d)");
		}

		if (doRevalidate)
		{
			si.revalidate();
		}
	}

	private void performMovement()
	{
		if (!selectionActive)
			return;
		// If there's a command already on the queue, do naught to avoid
		// confusion
		if (hasPolling()) // CHECK HERE
			return;

		int quadrant = defineQuadrant(mousePosition.x, mousePosition.y);
		mouseDirection = QDIRECTIONS[quadrant - 1];

		Expedition expedition = (Expedition) getUI().getPlayer();
		if (expedition.getMovementMode() == MovementMode.SHIP)
		{
			// Compare with heading and move in the correct direction
			CardinalDirection heading = expedition.getHeading();
			CardinalDirection wantedHeading = CardinalDirection.translateFromActionDirection(mouseDirection);
			// System.out.println("WH: "+wantedHeading+", H: "+heading);
			if (wantedHeading == CardinalDirection.NULL)
			{
				// move forward
				selectedMouseDirection = Action.UP;
				mouseDirection = Action.UP;
			}
			else if (heading.getReferenceAngle() != wantedHeading.getReferenceAngle())
			{
				int wantedAngle = wantedHeading.getReferenceAngle();
				int headingAngle = heading.getReferenceAngle();

				// Normalize angles
				wantedAngle = wantedAngle - headingAngle;
				if (wantedAngle < 0)
					wantedAngle += 360;

				int degreeDifference = wantedAngle;
				if (degreeDifference < 180)
				{
					selectedMouseDirection = Action.LEFT;
					mouseDirection = Action.LEFT;
				}
				else
				{
					selectedMouseDirection = Action.RIGHT;
					mouseDirection = Action.RIGHT;
				}
			}
			else
			{
				// move forward
				selectedMouseDirection = Action.UP;
				mouseDirection = Action.UP;
			}
		}
		else
		{
			// Normal movement
			selectedMouseDirection = mouseDirection;
		}
	}

	private Position _advanceInDirection = new Position(0, 0);

	protected Action advanceInDirection(int direction)
	{
		Position variation = Action.directionToVariation(direction);
		_advanceInDirection.x = variation.x * GlobeMapModel.getSingleton().getLongitudeScale(player.getPosition().y);
		_advanceInDirection.y = variation.y * GlobeMapModel.getSingleton().getLatitudeHeight() * -1;
		Actor vMonster = player.getLevel().getActorAt(Position.add(player.getPosition(), _advanceInDirection));
		if (vMonster != null && vMonster.isHostile() && attack.canPerform(player))
		{
			attack.setDirection(direction);
			return attack;
		}
		else
		{
			advance.setDirection(direction);
			switch (direction)
			{
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
			if (advance.canPerform(player))
			{
				return advance;
			}
			else
			{
				player.getLevel().addMessage(advance.getInvalidationMessage());
				return null;
			}
		}
	}

	@Override
	protected MouseListener getMouseClickListener()
	{
		javax.swing.Action gotoDirectionAction = new AbstractAction()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				performMovement();
			}
		};
		final Timer gotoDirectionTimer = new Timer(50, gotoDirectionAction);
		return new MouseAdapter()
		{
			public void mousePressed(final MouseEvent e)
			{
				if (SwingUtilities.isRightMouseButton(e))
				{
					return;
				}
				if (!selectionActive)
					return;
				if (e.getY() <= 26 && e.getX() >= si.getScreenWidth() - 26)
					return;
				if (e.getButton() == MouseEvent.BUTTON1)
				{
					mousePosition = e.getPoint();
					performMovement();
					gotoDirectionTimer.start();
				}
				else if (e.getButton() == MouseEvent.BUTTON3)
				{
					selectedMousePosition = translatePosition(e.getPoint().x, e.getPoint().y);
				}
			}

			public void mouseReleased(MouseEvent e)
			{
				if (SwingUtilities.isRightMouseButton(e))
				{
					return;
				}
				mouseDirection = -1;
				gotoDirectionTimer.stop();
			}

			private Position tempRel = new Position(0, 0);

			private Position translatePosition(int x, int y)
			{
				int bigx = (int) Math.ceil(x / 32.0);
				int bigy = (int) Math.ceil(y / 32.0);
				tempRel.x = bigx - ui().PC_POS.x - 1;
				tempRel.y = bigy - ui().PC_POS.y - 1;
				return Position.add(player.getPosition(), tempRel);
			}
		};
	}
}
