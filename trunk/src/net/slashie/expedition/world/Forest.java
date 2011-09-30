package net.slashie.expedition.world;

import net.slashie.expedition.domain.Expedition;
import net.slashie.serf.level.AbstractFeature;
import net.slashie.serf.ui.Appearance;
import net.slashie.serf.ui.AppearanceFactory;
import net.slashie.utils.Util;

@SuppressWarnings("serial")
public class Forest extends AbstractFeature{
	private static final Appearance CHOPPED_FOREST = AppearanceFactory.getAppearanceFactory().getAppearance("CHOPPED_FOREST");
	private int availableWood;
	public Forest(int availableWood) {
		super();
		this.availableWood = availableWood;
	}
	
	@Override
	public Appearance getAppearance() {
		if (getAvailableWood() <= 0)
			return CHOPPED_FOREST;
		else
			return null;
	}

	@Override
	public boolean isVisible() {
		return getAvailableWood() <= 0;
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
		int woodcutCapacity = (int) Math.ceil(( (Util.rand(100, 120)/100.0d) * expeditionUnits ) / 5.0d);
		int wood = 0;
		if (woodcutCapacity > availableWood)
			wood = availableWood;
		else
			wood = woodcutCapacity;
		availableWood -= wood;
		return wood;
	}
	
	public boolean overrideOpacity() {
		return getAvailableWood() <= 0;
	}

}
