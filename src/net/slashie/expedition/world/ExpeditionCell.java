package net.slashie.expedition.world;

import net.slashie.expedition.domain.Store;
import net.slashie.serf.level.AbstractCell;
import net.slashie.serf.ui.AppearanceFactory;

public class ExpeditionCell extends AbstractCell {
	private Store store;
	private String stepCommand;
	
	public String getStepCommand() {
		return stepCommand;
	}

	public Store getStore() {
		return store;
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
	
	public ExpeditionCell(String pID, String pShortDescription, Store store){
		this(pID, pShortDescription);
		this.store = store;
	}
	
	public ExpeditionCell(String pID, String pShortDescription, String stepCommand){
		this(pID, pShortDescription);
		this.stepCommand = stepCommand;
	}
	
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

}
