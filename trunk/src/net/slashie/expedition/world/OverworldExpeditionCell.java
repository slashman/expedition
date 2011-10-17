package net.slashie.expedition.world;

import java.util.List;

import net.slashie.expedition.domain.ExpeditionItem;
import net.slashie.serf.level.AbstractCell;
import net.slashie.serf.ui.AppearanceFactory;
import net.slashie.util.Pair;

/**
 * Represents a cell in the overworld.
 * @author Slash
 *
 */
@SuppressWarnings("serial")
public class OverworldExpeditionCell extends AbstractCell{
	private boolean 
		isLand, 
		isShallowWater,
		isDeepWater,
		isWood;
	private int heightMod;
	private int forageChance, forageQuantity;
	private boolean isForest;
	
	private List<Pair<String, Integer>> dailyResources;
	
	public boolean isRiver() {
		return isShallowWater;
	}

	public boolean isLand() {
		return isLand;
	}

	public OverworldExpeditionCell(String pid, String description, boolean isLand, int heightMod, boolean isShallowWater, boolean isSolid, boolean isWood, boolean isOpaque, int forageChance, int forageQuantity, boolean isForest, boolean isDeepWater, List<Pair<String, Integer>> dailyResources) {
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
		this.dailyResources = dailyResources;
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

	public List<Pair<String, Integer>> getDailyResources() {
		return dailyResources;
	}

}
