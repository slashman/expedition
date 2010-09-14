package net.slashie.expedition.ui.oryx;

import java.awt.Image;

import net.slashie.expedition.domain.ExpeditionItem;
import net.slashie.utils.swing.GFXMenuItem;

public class SimpleGFXMenuItem implements GFXMenuItem {
	private String message;
	private int value;

	public SimpleGFXMenuItem(String message, int value) {
		super();
		this.message = message;
		this.value = value;
	}

	public String getMenuDescription() {
		return message;
	}

	public String getMenuDetail() {
		return "";
	}

	public Image getMenuImage() {
		return null;
	}
	
	public int getValue() {
		return value;
	}
	
	public String getGroupClassifier() {
		return "1";
	}
	
}