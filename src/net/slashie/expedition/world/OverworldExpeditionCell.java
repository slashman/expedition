package net.slashie.expedition.world;

import net.slashie.serf.level.AbstractCell;
import net.slashie.serf.ui.AppearanceFactory;

public class OverworldExpeditionCell extends AbstractCell{
	private boolean 
	isLand, 
	isShallowWater,
	isDeepWater,
	isWood;
	private int heightMod;
	private int forageChance, forageQuantity;
	private boolean isForest;
	
	public boolean isRiver() {
		return isShallowWater;
	}

	public boolean isLand() {
		return isLand;
	}

	public OverworldExpeditionCell(String pid, String description, boolean isLand, int heightMod, boolean isShallowWater, boolean isSolid, boolean isWood, boolean isOpaque, int forageChance, int forageQuantity, boolean isForest, boolean isDeepWater) {
		super(pid, description, description, AppearanceFactory.getAppearanceFactory().getAppearance(pid), isSolid, isOpaque);
		this.isLand = isLand;
		this.heightMod = heightMod;
		this.isShallowWater = isShallowWater;
		this.isForest = isForest;
		this.isWood = isWood;
		this.isDeepWater = isDeepWater;
		setWater(isDeepWater || isShallowWater);
		this.forageChance = forageChance;
		this.forageQuantity = forageQuantity;
	}

	@Override
	public boolean cloneRequired() {
		return false;
	}

	public boolean isWood() {
		return isWood;
	}

	public boolean isMarsh() {
		// TODO Implement marshes
		return false;
	}

	public boolean isDesert() {
		// TODO Implement deserts
		return false;
	}

	public boolean isSea() {
		return isDeepWater;
	}

	public int getHeightMod() {
		return heightMod;
	}

	public int getForageChance() {
		return forageChance;
	}

	public int getForageQuantity() {
		return forageQuantity;
	}


	public boolean isForest() {
		return isForest;
	}


}
