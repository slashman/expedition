package net.slashie.expedition.town;

import java.util.Calendar;
import java.util.HashMap;

import net.slashie.expedition.domain.ExpeditionItem;
import net.slashie.expedition.domain.Town;
import net.slashie.expedition.item.ItemFactory;
import net.slashie.expedition.item.StorageType;
import net.slashie.expedition.ui.oryx.ExpeditionOryxUI;
import net.slashie.expedition.world.agents.DayShiftAgent;
import net.slashie.utils.Util;

@SuppressWarnings("serial")
public class Farm extends Building {
	private Calendar nextCrop;

	public Farm() {
		super("FARM", "Farm", "Produces 5000 food each six months", 60, DayShiftAgent.TICKS_PER_DAY * 240, 0, 14, new HashMap<StorageType, Integer>());
	}
	
	public void plant(Calendar date){
		nextCrop = Calendar.getInstance();
		nextCrop.setTime(date.getTime());
		nextCrop.add(Calendar.DATE, 6*30 + Util.rand(-20,20));
		//nextCrop.add(Calendar.DATE, 6);
	}
	
	public boolean checkCrop(Calendar date, Town t){
		if (nextCrop != null && nextCrop.before(date)){
			String food = "WHEAT";
			ExpeditionItem foodSample = ItemFactory.createItem(food);
			t.addItem(foodSample, 5000);
			plant(date);
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public String getDescription() {
		if (nextCrop != null){
			return super.getDescription() + " (Crop: around "+ExpeditionOryxUI.months[nextCrop.get(Calendar.MONTH)]+")";
		} else {
			return super.getDescription();
		}
	}
	
	@Override
	public String getId() {
		if (nextCrop != null){
			return super.getId()+ " (Crop: "+ExpeditionOryxUI.months[nextCrop.get(Calendar.MONTH)]+" "+nextCrop.get(Calendar.YEAR)+")";
		} else {
			return super.getId();
		}
	}
	
	@Override
	public boolean isPluralizableDescription() {
		return false;
	}

}
