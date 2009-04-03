package net.slashie.expedition.world;

import java.util.List;

import net.slashie.serf.baseDomain.AbstractItem;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.level.AbstractLevel;
import net.slashie.utils.Position;

public interface FoodConsumer {
	public void consumeFood();

	public void checkDeath();

	public List<Equipment> getInventory();
	
	public int getDailyFoodConsumption();
	
	public int getFoodConsumptionMultiplier();

	public void reduceQuantityOf(AbstractItem item, int killUnits);
	
	public void killUnits(int quantity);

	public int getTotalUnits();

	public AbstractLevel getLevel();

	public Position getPosition();
}
