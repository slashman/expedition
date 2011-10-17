package net.slashie.expedition.world;

import net.slashie.serf.level.AbstractCell;
import net.slashie.serf.ui.AppearanceFactory;

@SuppressWarnings("serial")
public class ExpeditionCell extends AbstractCell {
	private String stepCommand;
	
	public String getStepCommand() {
		return stepCommand;
	}

	public ExpeditionCell(String pID, String pShortDescription, boolean solid, boolean opaque){
		super(pID, pShortDescription, pShortDescription, AppearanceFactory.getAppearanceFactory().getAppearance(pID), solid, opaque);
	}
	
	public ExpeditionCell(String pID, String pShortDescription, boolean solid, boolean opaque, boolean water){
		super(pID, pShortDescription, pShortDescription, AppearanceFactory.getAppearanceFactory().getAppearance(pID), solid, opaque);
		setWater(water);
	}

	public ExpeditionCell(String pID, String pShortDescription){
		super(pID, pShortDescription, pShortDescription, AppearanceFactory.getAppearanceFactory().getAppearance(pID));
	}
	
	public ExpeditionCell(String pID, String pShortDescription, String stepCommand){
		this(pID, pShortDescription);
		this.stepCommand = stepCommand;
	}
	
	@Override
	public AbstractCell clone() {
		ExpeditionCell ret = (ExpeditionCell) super.clone();
		return ret;
	}
	
	public void setStepCommand(String stepCommand) {
		this.stepCommand = stepCommand;
	}

}
