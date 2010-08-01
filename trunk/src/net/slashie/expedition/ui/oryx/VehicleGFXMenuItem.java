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
		return " "+v.getResistance();

	}

	@Override
	public String getMenuDetail() {
		return "";
	}

	private GFXAppearance getItemAppearance(){
		return (GFXAppearance)v.getAppearance();
	}
}
