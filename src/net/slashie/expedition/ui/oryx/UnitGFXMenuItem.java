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
		if (!(e.getItem() instanceof ExpeditionUnit)){
			return e.getItem().getDescription() +" x"+e.getQuantity();
		}

		if (e.getQuantity() == 1){
 			return ((ExpeditionUnit)e.getItem()).getMenuDescription();
 		} else {
 			return ((ExpeditionUnit)e.getItem()).getMenuDescription() +" x"+e.getQuantity();
 		}
	}

	@Override
	public String getMenuDetail() {
		return "";
	}

	private GFXAppearance getItemAppearance(){
		return (GFXAppearance)e.getItem().getAppearance();
	}
}
