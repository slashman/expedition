package net.slashie.expedition.world;

import java.util.List;
import net.slashie.expedition.domain.Expedition;
import net.slashie.serf.baseDomain.AbstractItem;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.level.AbstractLevel;
import net.slashie.utils.Position;

public interface FoodConsumer {
	public void consumeFood();
	
	public void consumeWater();

	public void checkDeath();

	public List<Equipment> getInventory();
	
	public int getDailyFoodConsumption();
	
	public int getDailyWaterConsumption();
	
	public double getFoodConsumptionMultiplier();
	
	public double getWaterConsumptionMultiplier();

	public void reduceQuantityOf(AbstractItem item, int killUnits);
	
	public void killUnits(int quantity, String cause);

	public int getTotalUnits();

	public AbstractLevel getLevel();

	public Position getPosition();

	public int getCurrentFood();

	public int getCurrentWater();

	public int getHungerResistance();

	public int getThirstResistance();

	public int getMaxThirstResistance();
	
	public int getMaxHungerResistance();

	public void setHungerResistance(int i);
	
	public void setThirstResistance(int i);
	}
