package net.slashie.expedition.ui.oryx;

import java.awt.Image;

import net.slashie.expedition.domain.Vehicle;
import net.slashie.serf.ui.oryxUI.GFXAppearance;
import net.slashie.utils.swing.GFXMenuItem;

public class VehicleGFXMenuItem implements GFXMenuItem {
	protected Vehicle v;

	public VehicleGFXMenuItem(Vehicle v) {
		this.v = v;
	}
	
	@Override
	public Image getMenuImage() {
		return getItemAppearance().getImage();
	}

	@Override
	public String getMenuDescription() {
		return v.getDescription();

	}

	@Override
	public String getMenuDetail() {
		return "Int: "+v.getResistance();
	}

	private GFXAppearance getItemAppearance(){
		return (GFXAppearance)v.getAppearance();
	}
	
	public String getGroupClassifier() {
		return v.getGroupClassifier();
	}
}
