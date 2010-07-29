package net.slashie.expedition.ui.console;

import net.slashie.expedition.domain.ExpeditionUnit;
import net.slashie.expedition.domain.Vehicle;
import net.slashie.libjcsi.textcomponents.ListItem;
import net.slashie.libjcsi.textcomponents.MenuItem;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.ui.consoleUI.CharAppearance;

public class UnitMenuItem implements MenuItem, ListItem{
	protected Equipment e;
	
	public UnitMenuItem(Equipment e) {
		this.e = e;
	}
	
	private CharAppearance getItemAppearance(){
		return (CharAppearance)e.getItem().getAppearance();
	}
 	
	public char getMenuChar() {
		return getItemAppearance().getChar();
	}
	
	public int getMenuColor() {
		return getItemAppearance().getColor();
	}
	
	public String getMenuDescription(){
		if (!(e.getItem() instanceof ExpeditionUnit)){
			return e.getItem().getDescription() +" x"+e.getQuantity();
		}

		if (e.getQuantity() == 1){
 			return ((ExpeditionUnit)e.getItem()).getMenuDescription();
 		} else {
 			return ((ExpeditionUnit)e.getItem()).getMenuDescription() +" x"+e.getQuantity();
 		}
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