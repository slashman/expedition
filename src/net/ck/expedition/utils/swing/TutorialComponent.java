package net.ck.expedition.utils.swing;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import org.apache.log4j.Logger;
import net.slashie.expedition.domain.FriarTutorial;
import net.slashie.expedition.ui.oryx.ExpeditionOryxUI;
import net.slashie.expedition.ui.oryx.Layout;
import net.slashie.serf.ui.AppearanceFactory;
import net.slashie.serf.ui.oryxUI.Assets;
import net.slashie.serf.ui.oryxUI.GFXAppearance;
import net.slashie.serf.ui.oryxUI.SwingSystemInterface;
import net.slashie.utils.swing.CleanButton;
import net.slashie.utils.swing.PlainTextArea;

/**
 * 
 * @author Claus
 *
 *         This is the Tutorial JPanel, extracted from ExpeditionOryxUI for
 *         easier handling
 * 
 *         All components now appear at the same time and also disappear at the
 *         same time, i.e. if you click close then the Icon of the Friar also
 *         disappears right away and not after one step.
 *
 */
public class TutorialComponent extends JPanel
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	SwingSystemInterface si;
	final static Logger logger = Logger.getRootLogger();
	private PlainTextArea friarTutorialTextBox;
	private CleanButton tutorialNextButton;
	private CleanButton tutorialCloseButton;
	private CleanButton friarButton;

	public TutorialComponent(SwingSystemInterface sin, Assets assets, Layout layout, ExpeditionOryxUI expeditionOryxUI)
	{
		Cursor HAND_CURSOR = assets.getCursorAsset("HAND_CURSOR");
		Image IMG_SMALL_BUTTON_BACK = assets.getImageAsset("IMG_SMALL_BUTTON_BACK");
		Image IMG_SMALL_BUTTON_HOVER_BACK = assets.getImageAsset("IMG_SMALL_BUTTON_HOVER_BACK");
		friarTutorialTextBox = new PlainTextArea();
		friarTutorialTextBox.setBounds(layout.MSGBOX_FRIAR_TEXT_TUTORIAL);
		friarTutorialTextBox.setFont(assets.getFontAsset("FNT_DIALOGUE"));
		friarTutorialTextBox.setText("");
		friarTutorialTextBox.setForeground(Color.WHITE);
		friarTutorialTextBox.setVisible(false);

		sin.add(friarTutorialTextBox);
		sin.changeZOrder(friarTutorialTextBox, 0);

		tutorialNextButton = new CleanButton(IMG_SMALL_BUTTON_BACK, IMG_SMALL_BUTTON_HOVER_BACK, null, HAND_CURSOR,
				">");
		tutorialNextButton.setForeground(Color.WHITE);
		tutorialNextButton.setPopupText("Next");
		tutorialNextButton.setLocation(layout.BTN_TUTORIAL_NEXT.x, layout.BTN_TUTORIAL_NEXT.y);
		tutorialNextButton.setVisible(false);
		tutorialNextButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (FriarTutorial.nextPage())
				{
					friarTutorialTextBox.setText(FriarTutorial.getText());
				}
				else
				{
					friarTutorialTextBox.setVisible(false);
					friarTutorialTextBox.setText("");
					tutorialNextButton.setVisible(false);
					tutorialCloseButton.setVisible(false);
					expeditionOryxUI.drawStatus();
				}
			}
		});
		sin.add(tutorialNextButton);

		tutorialCloseButton = new CleanButton(IMG_SMALL_BUTTON_BACK, IMG_SMALL_BUTTON_HOVER_BACK, null, HAND_CURSOR,
				"X");
		tutorialCloseButton.setForeground(Color.WHITE);
		tutorialCloseButton.setPopupText("Close");
		tutorialCloseButton.setLocation(layout.BTN_TUTORIAL_CLOSE.x, layout.BTN_TUTORIAL_CLOSE.y);
		tutorialCloseButton.setVisible(false);
		tutorialCloseButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				FriarTutorial.deactivate();

				friarTutorialTextBox.setVisible(false);
				friarTutorialTextBox.setText("");
				tutorialNextButton.setVisible(false);
				tutorialCloseButton.setVisible(false);
				friarButton.setVisible(false);
				// drawStatus();
			}
		});

		Image friarImage = ((GFXAppearance) AppearanceFactory.getAppearanceFactory().getAppearance("DOMINIK"))
				.getImage();

		friarButton = new CleanButton(friarImage, HAND_CURSOR, "");
		friarButton.setLocation(layout.POS_FRIAR_TUTORIAL.x, layout.POS_FRIAR_TUTORIAL.y);
		friarButton.setVisible(false);
		sin.add(tutorialCloseButton);
		sin.add(friarButton);
	}

	public void setVisible()
	{
		friarButton.setVisible(true);
		friarTutorialTextBox.setVisible(true);
		tutorialNextButton.setVisible(true);
		tutorialCloseButton.setVisible(true);
		friarTutorialTextBox.setText(FriarTutorial.getText());
	}

}
