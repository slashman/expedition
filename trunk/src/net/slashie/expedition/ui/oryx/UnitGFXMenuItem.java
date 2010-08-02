package net.slashie.expedition.ui.oryx;

import java.awt.Image;

import net.slashie.expedition.domain.ExpeditionUnit;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.ui.oryxUI.GFXAppearance;
import net.slashie.utils.swing.GFXMenuItem;

public class UnitGFXMenuItem implements GFXMenuItem {
	protected Equipment e;

	public UnitGFXMenuItem(Equipment e) {
		this.e = e;
	}
	
	@Override
	public Image getMenuImage() {
		return getItemAppearance().getImage();
	}

	@Override
	public String getMenuDescription() {
		return ((ExpeditionUnit)e.getItem()).getDescription();
	}

	@Override
	public String getMenuDetail() {
		if (!(e.getItem() instanceof ExpeditionUnit)){
			return " x"+e.getQuantity();
		}

		if (e.getQuantity() == 1){
 			return " x1";
 		} else {
 			return " x"+e.getQuantity();
 		}
		
	}
	
	private GFXAppearance getItemAppearance(){
		return (GFXAppearance)e.getItem().getAppearance();
	}
}
