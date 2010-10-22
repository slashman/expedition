package net.slashie.expedition.town;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import net.slashie.expedition.domain.ExpeditionItem;
import net.slashie.expedition.domain.Town;
import net.slashie.expedition.item.ItemFactory;
import net.slashie.expedition.world.agents.DayShiftAgent;
import net.slashie.utils.Util;

public class Farm extends Building {
	private final static Map<SpecialCapability, Object> NO_CAPABILITIES = new HashMap<SpecialCapability, Object>();
	
	private Calendar nextCrop;

	public Farm() {
		super("FARM", "Farm", "Produces 5000 food each six months", 60, DayShiftAgent.TICKS_PER_DAY * 240, 0, 14, NO_CAPABILITIES);
	}
	
	public void plant(Calendar date){
		nextCrop = Calendar.getInstance();
		nextCrop.setTime(date.getTime());
		nextCrop.add(Calendar.DATE, 6*30 + Util.rand(-20,20));
		//nextCrop.add(Calendar.DATE, 6);
	}
	
	public boolean checkCrop(Calendar date, Town t){
		if (nextCrop.before(date)){
			String food = "WHEAT";
			ExpeditionItem foodSample = ItemFactory.createItem(food);
			t.addItem(foodSample, 5000);
			plant(date);
			return true;
		} else {
			return false;
		}
	}

}
