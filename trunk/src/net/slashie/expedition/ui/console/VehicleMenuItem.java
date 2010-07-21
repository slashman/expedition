package net.slashie.expedition.ui.console;

import net.slashie.expedition.domain.Vehicle;
import net.slashie.libjcsi.textcomponents.ListItem;
import net.slashie.libjcsi.textcomponents.MenuItem;
import net.slashie.serf.ui.consoleUI.CharAppearance;

public class VehicleMenuItem implements MenuItem, ListItem{
	protected Vehicle v;
	
	public VehicleMenuItem(Vehicle v) {
		this.v = v;
	}
	
	private CharAppearance getItemAppearance(){
		return (CharAppearance)v.getAppearance();
	}
 	
	public char getMenuChar() {
		return getItemAppearance().getChar();
	}
	
	public int getMenuColor() {
		return getItemAppearance().getColor();
	}
	
	public String getMenuDescription(){
		return v.getDescription()+"("+v.getResistance()+")";
 	}
	
	public char getIndex() {
		return getMenuChar();
	}

	public int getIndexColor() {
		return getMenuColor();
	}

	public String getRow() {
		return getMenuDescription();
	}
}