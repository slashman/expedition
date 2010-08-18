package net.slashie.expedition.world;

import net.slashie.expedition.domain.Expedition;
import net.slashie.serf.level.AbstractFeature;
import net.slashie.utils.Util;

public class Forest extends AbstractFeature{
	private int availableWood;
	public Forest(int availableWood) {
		super();
		this.availableWood = availableWood;
	}

	@Override
	public boolean isVisible() {
		return false;
	}
	
	@Override
	public boolean isSolid() {
		return false;
	}
	
	@Override
	public boolean isRelevant() {
		return false;
	}
	
	
	@Override
	public String getClassifierID() {
		return "FOREST";
	}

	@Override
	public String getDescription() {
		return "(Chopped)";
	}

	public int getAvailableWood() {
		return availableWood;
	}

	public int substractWood(Expedition expedition) {
		int expeditionUnits = expedition.getTotalUnits();
		int woodcutCapacity = (int) Math.round(( (Util.rand(100, 120)/100.0d) * expeditionUnits ) / 5.0d);
		int wood = 0;
		if (woodcutCapacity > availableWood)
			wood = availableWood;
		else
			wood = woodcutCapacity;
		availableWood -= wood;
		return wood;
	}
	

}
