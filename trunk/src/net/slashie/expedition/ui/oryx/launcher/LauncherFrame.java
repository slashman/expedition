package net.slashie.expedition.ui.oryx.launcher;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.game.ExpeditionGame.ExpeditionVersion;
import net.slashie.serf.ui.UserInterface;

import org.apache.commons.httpclient.HttpException;

public class LauncherFrame extends JFrame{
	public LauncherFrame(BlockingQueue<String> bq) {
		this.bq = bq;
		JPanel textPane = getTextPanel();
		JPanel gfxPane = getGFXPanel();
		JPanel buttonsPane = getButtonsPanel();
		BorderLayout b = new BorderLayout();
		b.setHgap(30);
		b.setVgap(30);
		getContentPane().setLayout(b);
		getContentPane().add(textPane, BorderLayout.NORTH);
		getContentPane().add(gfxPane, BorderLayout.CENTER);
		getContentPane().add(buttonsPane, BorderLayout.EAST);
		
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		
		setLocation((size.width - 400)/2,(size.height-600)/2);
		
		setSize(400, 600);
		setResizable(false);
		setTitle("Expedition: The New World / Slashware Interactive");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private JPanel getButtonsPanel() {
		final LauncherFrame _this = this;
		JButton checkButton = new JButton("Check for new versions");
		checkButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					ExpeditionVersion latestVersion = ExpeditionGame.checkNewVersion();
					if (latestVersion == null){
						JOptionPane.showMessageDialog(_this, "Error connecting to expeditionworld.net.", "Error", JOptionPane.ERROR_MESSAGE);
					} else if (latestVersion.equals(ExpeditionGame.getExpeditionVersion())){
						JOptionPane.showMessageDialog(_this, "You are up to date :)", "AOK!", JOptionPane.INFORMATION_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(_this, "A newer version, "+latestVersion.getCode()+" from "+latestVersion.getFormattedDate()+" is available at the website! Please download it from http://expeditionworld.net", "Newest Version Available", JOptionPane.INFORMATION_MESSAGE);
					}
				} catch (HttpException ex) {
					JOptionPane.showMessageDialog(_this, "Error connecting to expeditionworld.net.", "Error", JOptionPane.ERROR_MESSAGE);
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(_this, "Error connecting to expeditionworld.net.", "Error", JOptionPane.ERROR_MESSAGE);
				}
				
			}
		});
		JButton launchButton = new JButton("LAUNCH!");
		
		launchButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					bq.put(command1+(fullScreenButton.isSelected()?" fullscreen":" windowed"));
					_this.setVisible(false);
				} catch (InterruptedException e1) {
				}
				
			}
		});
		JPanel ret = new JPanel();
		ret.setLayout(new BoxLayout(ret, BoxLayout.Y_AXIS));
		ret.add (checkButton);
		ret.add (launchButton);
		ret.setBorder(new EmptyBorder(0, 20,20,20));
		return ret;
	}

	private JPanel getTextPanel() {
		JTextArea text = new JTextArea("Thank you for trying out this version of Expedition: The New World.\n\nThis game is in active development, if you like the game please visit http://expeditionworld.net to learn about ways to help us complete it!");
	
		text.setEditable(false);  
		text.setCursor(null);  
		text.setOpaque(false);  
		text.setFocusable(false);
		text.setWrapStyleWord(true);
		text.setLineWrap(true);  
		
		ImageIcon icon = new ImageIcon("res/logo.png");
		JLabel logo = new JLabel(icon);
	      
		JPanel ret = new JPanel();
		ret.setLayout(new BorderLayout());
		ret.add(logo, BorderLayout.NORTH);
		ret.add(text, BorderLayout.CENTER);
		ret.setBorder(new EmptyBorder(20,20,0,20));
		return ret;
	}
	
	private BlockingQueue<String> bq;
	private JCheckBox fullScreenButton;
	private String command1 = "gfx res/bigDenzi/expedition-denzi.ui";
	
	private JPanel getGFXPanel() {
		JRadioButton bigDenzi = new JRadioButton("BigDenzi");
		bigDenzi.setActionCommand("gfx res/bigDenzi/expedition-denzi.ui");
		bigDenzi.setSelected(true);
		JRadioButton denzi = new JRadioButton("Denzi");
		denzi.setActionCommand("gfx res/denzi/expedition-denzi.ui");
		JRadioButton oryx = new JRadioButton("Oryx");
		oryx.setActionCommand("gfx res/oryx/expedition-oryx.ui");
		JRadioButton swingBox = new JRadioButton("SwingBox");
		swingBox.setActionCommand("sc");
		JRadioButton curses = new JRadioButton("Curses");
		curses.setActionCommand("jc");
		
		ButtonGroup group = new ButtonGroup();
		group.add(bigDenzi);
		group.add(denzi);
		group.add(oryx);
		group.add(swingBox);
		group.add(curses);
		
		ActionListener gfxActionListener = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				command1 = ((JRadioButton)e.getSource()).getActionCommand();
				if (command1.startsWith("gfx")){
					fullScreenButton.setEnabled(true);
				} else {
					fullScreenButton.setEnabled(false);
				}
			}
		};
		
		bigDenzi.addActionListener(gfxActionListener);
		denzi.addActionListener(gfxActionListener);
		oryx.addActionListener(gfxActionListener);
		swingBox.addActionListener(gfxActionListener);
		curses.addActionListener(gfxActionListener);
		
		fullScreenButton = new JCheckBox("Full Screen");
        
		JPanel ret = new JPanel();
		ret.setLayout(new BoxLayout(ret, BoxLayout.Y_AXIS));
		ret.add(fullScreenButton);
		ret.add(bigDenzi);
		ret.add(denzi);
		ret.add(oryx);
		ret.add(swingBox);
		ret.add(curses);
		ret.setBorder(new EmptyBorder(0,20,20,20));
		return ret;
	}
}

