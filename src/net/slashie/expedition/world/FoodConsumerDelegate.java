package net.slashie.expedition.world;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import org.apache.log4j.Logger;
import net.ck.expedition.utils.swing.MessengerService;
import net.slashie.expedition.domain.ExpeditionUnit;
import net.slashie.expedition.domain.Food;
import net.slashie.expedition.domain.NonPrincipalExpedition;
import net.slashie.expedition.domain.Water;
import net.slashie.serf.game.Equipment;
import net.slashie.util.Pair;
import net.slashie.utils.Position;
import net.slashie.utils.Util;

public class FoodConsumerDelegate implements Serializable
{
	private static final long serialVersionUID = 1L;

	private FoodConsumer foodConsumer;
	final static Logger logger = Logger.getRootLogger();

	public FoodConsumerDelegate(FoodConsumer foodConsumer)
	{
		super();
		this.foodConsumer = foodConsumer;

	}

	public int getDailyFoodConsumption()
	{
		if (foodConsumer instanceof NonPrincipalExpedition)
			return 0;
		int dailyFoodConsumption = 0;
		List<Equipment> inventory = this.foodConsumer.getInventory();
		for (Equipment equipment : inventory)
		{
			if (equipment.getItem() instanceof ExpeditionUnit)
			{
				ExpeditionUnit unit = (ExpeditionUnit) equipment.getItem();
				dailyFoodConsumption += unit.getDailyFoodConsumption() * equipment.getQuantity();
			}
		}
		return (int) Math.floor(dailyFoodConsumption * foodConsumer.getFoodConsumptionMultiplier());
	}

	/**
	 * hunger Resistance: after 7 days, people start starving to death. hunger
	 * resistance itself is moved to expedition, learned to hate that java does
	 * only call by value. I had forgotten about that nonsense. Max hunger
	 * resistance could be made depending on difficulty level.
	 * 
	 */
	public void consumeFood()
	{
		int foodUnderflow = reduceFood(foodConsumer.getDailyFoodConsumption());
		if (foodUnderflow > 0)
		{
			// Reduce expedition resistance
			foodConsumer.setHungerResistance(foodConsumer.getHungerResistance() - 1);
			MessengerService.addMessage("Your Crew is starving!!", foodConsumer.getPosition());
			if (foodConsumer.getHungerResistance() <= 0)
			{
				int unitsToKill = (int) Math
						.ceil((double) foodConsumer.getTotalUnits() * (double) Util.rand(5, 20) / 100.0d);
				if (unitsToKill > 0)
				{
					String cause = "starve";
					if (unitsToKill == 1)
						cause = "starves";
					foodConsumer.killUnits(unitsToKill, cause);					
				}
			}
		}
		else
		{
			// only do so if there are units left
			if (foodConsumer.getTotalUnits() > 0)
			{
				if (foodConsumer.getHungerResistance() < foodConsumer.getMaxHungerResistance())
				{
					foodConsumer.setHungerResistance(foodConsumer.getHungerResistance() + 1);
				}
			}
		}
	}

	/**
	 * thirst Resistance is lower, after 5 days, people start thirsting to
	 * death. Moved to expedition, learned to hate that java does only call by
	 * value. I had forgotten about that nonsense. Max Thirst resistance could
	 * be made depending on difficulty level.
	 * 
	 * Difference to hunger: when next to a river, do not thirst, but remove
	 * Thirst instead. Remove thirst however means, set thirst Resistance back
	 * to maximum
	 */
	public void consumeWater()
	{
		// Consume water only if is not near to a sweet water spot
		List<Pair<Position, OverworldExpeditionCell>> around = ((ExpeditionMacroLevel) foodConsumer.getLevel())
				.getMapCellsAndPositionsAround(foodConsumer.getPosition());
		for (Pair<Position, OverworldExpeditionCell> a : around)
		{
			if (a.getB().isRiver())
			{
				removeThirst();
				return;
			}
		}

		int waterUnderFlow = reduceWater(foodConsumer.getDailyWaterConsumption());
		/*
		 * there was not enough water for all units
		 */
		if (waterUnderFlow > 0)
		{
			// Reduce expedition thirst
			foodConsumer.setThirstResistance(foodConsumer.getThirstResistance() - 1);
			MessengerService.addMessage("Your Crew is thirsty!!", foodConsumer.getPosition());
			/*
			 * if thirst resistance has reached zero, kill units
			 */
			if (foodConsumer.getThirstResistance() <= 0)
			{
				int unitsToKill = (int) Math
						.ceil((double) foodConsumer.getTotalUnits() * (double) Util.rand(5, 20) / 100.0d);
				if (unitsToKill > 0)
				{
					String cause = "thirst";
					if (unitsToKill == 1)
						cause = "thirsts";
					foodConsumer.killUnits(unitsToKill, cause);					
				}
			}
		}
		/*
		 * all units were drinking, increase thirst resistance again
		 */
		else
		{
			// only do so if there are units left
			if (foodConsumer.getTotalUnits() > 0)
			{
				if (foodConsumer.getThirstResistance() < foodConsumer.getMaxThirstResistance())
				{
					foodConsumer.setThirstResistance(foodConsumer.getThirstResistance() + 1);
				}
			}
		}
	}

	/**
	 * 
	 * @param quantity
	 *            - the amount of food needed, created by adding up the daily
	 *            food consumption over all units
	 * @return - does not return the remaining food, it does return the food
	 *         which is still needed
	 */
	public int reduceFood(int quantity)
	{
		if (quantity == 0)
			return 0;
		int foodToSpend = quantity;
		List<Equipment> inventory = this.foodConsumer.getInventory();
		int originalSize = inventory.size();
		for (int i = 0; i < inventory.size(); i++)
		{
			Equipment equipment = (Equipment) inventory.get(i);
			if (equipment.getItem() instanceof Food)
			{
				Food good = (Food) equipment.getItem();

				int unitsToSpend = (int) Math.ceil((double) foodToSpend / (double) good.getUnitsFedPerGood());
				if (unitsToSpend > equipment.getQuantity())
				{
					unitsToSpend = equipment.getQuantity();
				}
				foodToSpend -= unitsToSpend * good.getUnitsFedPerGood();
				foodConsumer.reduceQuantityOf(good, unitsToSpend);
				if (inventory.size() < originalSize)
				{
					// Means the people ate all of a kind of item, and it was
					// removed from the inventory
					// And so, we must check again the same position of the
					// inventory array
					i--;
					originalSize = inventory.size();
				}
				if (foodToSpend <= 0)
				{
					return 0;
				}
			}
		}
		return foodToSpend;
	}

	private Equipment chooseRandomEquipmentUsingWeights(List<Pair<Equipment, Double>> weights)
	{
		double pin = Util.rand(0, 100) / 100.0d;
		for (Pair<Equipment, Double> weightedEquipment : weights)
		{
			if (pin < weightedEquipment.getB())
				return weightedEquipment.getA();
		}
		return weights.get(weights.size() - 1).getA();
	}

	private final static Comparator<Pair<Equipment, Double>> WEIGHTED_EQUIPMENT_COMPARATOR = new Comparator<Pair<Equipment, Double>>()
	{
		public int compare(Pair<Equipment, Double> o1, Pair<Equipment, Double> o2)
		{
			return o1.getB().compareTo(o2.getB());
		}
	};

	/**
	 * Kills a number of unit from the Food Consumer
	 * 
	 * @param deaths
	 *            The number of units to kill
	 * @return A collection describing the number of individuals killed from
	 *         each kind of unit
	 */
	public Collection<Pair<ExpeditionUnit, Integer>> killUnits(int deaths)
	{
		List<Equipment> inventory = foodConsumer.getInventory();
		Hashtable<String, Pair<ExpeditionUnit, Integer>> acumHash = new Hashtable<String, Pair<ExpeditionUnit, Integer>>();
		int totalUnits = foodConsumer.getTotalUnits();
		List<Pair<Equipment, Double>> weights = new ArrayList<Pair<Equipment, Double>>();

		for (int i = 0; i < deaths; i++)
		{
			weights.clear();
			for (Equipment equipment : inventory)
			{
				if (equipment.getItem() instanceof ExpeditionUnit)
				{
					weights.add(new Pair<Equipment, Double>(equipment, equipment.getQuantity() / (double) totalUnits));
				}
			}
			Collections.sort(weights, WEIGHTED_EQUIPMENT_COMPARATOR);
			double acum = 0;
			for (Pair<Equipment, Double> weightedEquipment : weights)
			{
				weightedEquipment.setB(acum + weightedEquipment.getB());
				acum = weightedEquipment.getB();
			}
			Equipment choosenToKill = chooseRandomEquipmentUsingWeights(weights);
			String itemId = choosenToKill.getItem().getFullID();
			Pair<ExpeditionUnit, Integer> currentlyKilled = acumHash.get(itemId);
			if (currentlyKilled == null)
			{
				currentlyKilled = new Pair<ExpeditionUnit, Integer>((ExpeditionUnit) choosenToKill.getItem(), 1);
				acumHash.put(itemId, currentlyKilled);
			}
			else
			{
				currentlyKilled.setB(currentlyKilled.getB() + 1);
			}
			foodConsumer.reduceQuantityOf(choosenToKill.getItem(), 1);

			ExpeditionUnit unit = ((ExpeditionUnit) choosenToKill.getItem());

			if (unit.getWeapon() != null && foodConsumer.getLevel() instanceof ExpeditionMacroLevel && Util.chance(60))
			{
				((ExpeditionMacroLevel) foodConsumer.getLevel()).addEquipment(unit.getWeapon(), 1,
						foodConsumer.getPosition());
			}

			if (unit.getArmor() != null && foodConsumer.getLevel() instanceof ExpeditionMacroLevel && Util.chance(40))
			{
				((ExpeditionMacroLevel) foodConsumer.getLevel()).addEquipment(unit.getArmor(), 1,
						foodConsumer.getPosition());
			}

			if (foodConsumer.getTotalUnits() == 0)
				break;

		}
		// foodConsumer.checkDeath();
		return acumHash.values();
	}

	public int getFoodDays()
	{
		if (getDailyFoodConsumption() == 0)
		{
			return 0;
		}
		return (int) Math.round((double) getCurrentFood() / (double) getDailyFoodConsumption());
	}

	private int getCurrentFood()
	{
		return foodConsumer.getCurrentFood();
	}

	public int getDailyWaterConsumption()
	{
		if (foodConsumer instanceof NonPrincipalExpedition)
			return 0;
		int dailyWaterConsumption = 0;
		List<Equipment> inventory = this.foodConsumer.getInventory();
		for (Equipment equipment : inventory)
		{
			if (equipment.getItem() instanceof ExpeditionUnit)
			{
				ExpeditionUnit unit = (ExpeditionUnit) equipment.getItem();				
				dailyWaterConsumption += unit.getDailyWaterConsumption() * equipment.getQuantity();
			}
		}
		return (int) Math.floor(dailyWaterConsumption * foodConsumer.getWaterConsumptionMultiplier());
	}

	/**
	 * 
	 * @param quantity
	 *            - the amount of water needed, created by adding up the daily
	 *            water consumption over all units
	 * @return - does not return the remaining water, it does return the water
	 *         which is still needed
	 */
	public int reduceWater(int quantity)
	{
		if (quantity == 0)
			return 0;
		int waterToSpend = quantity;

		List<Equipment> inventory = this.foodConsumer.getInventory();
		int originalSize = inventory.size();
		for (int i = 0; i < inventory.size(); i++)
		{
			Equipment equipment = (Equipment) inventory.get(i);

			if (equipment.getItem() instanceof Water)
			{
				Water good = (Water) equipment.getItem();
				int unitsToSpend = (int) Math.ceil((double) waterToSpend / (double) good.getUnitsFedPerGood());
				if (unitsToSpend > equipment.getQuantity())
				{
					unitsToSpend = equipment.getQuantity();
				}
				waterToSpend -= unitsToSpend * good.getUnitsFedPerGood();
				foodConsumer.reduceQuantityOf(good, unitsToSpend);
				if (inventory.size() < originalSize)
				{
					// Means the people ate all of a kind of item, and it was
					// removed from the inventory
					// And so, we must check again the same position of the
					// inventory array
					i--;
					originalSize = inventory.size();
				}
				if (waterToSpend <= 0)
				{
					return 0;
				}
			}
		}
		return waterToSpend;
	}

	/**
	 * remove thirst: sets thirst resistance in the expedition object back to
	 * maximum
	 */
	public void removeThirst()
	{
		foodConsumer.setThirstResistance(foodConsumer.getMaxThirstResistance());
	}
}
