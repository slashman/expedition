package net.slashie.expedition.world;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import net.slashie.expedition.domain.ExpeditionUnit;
import net.slashie.expedition.domain.Food;
import net.slashie.expedition.domain.Good;
import net.slashie.expedition.domain.GoodType;
import net.slashie.serf.game.Equipment;
import net.slashie.util.Pair;
import net.slashie.utils.Util;

public class FoodConsumerDelegate implements Serializable{
	private FoodConsumer foodConsumer;
	
	private int starveResistance;
	public FoodConsumerDelegate(FoodConsumer foodConsumer) {
		super();
		this.foodConsumer = foodConsumer;
	}

	public void setStarveResistance(int starveResistance) {
		this.starveResistance = starveResistance;
	}

	public int getDailyFoodConsumption(){
		int dailyFoodConsumption = 0;
		List<Equipment> inventory = this.foodConsumer.getInventory();
		for (Equipment equipment: inventory){
			if (equipment.getItem() instanceof ExpeditionUnit){
				ExpeditionUnit unit = (ExpeditionUnit)equipment.getItem();
				dailyFoodConsumption += unit.getDailyFoodConsumption() * equipment.getQuantity();
			}
		}
		return (int)Math.floor(dailyFoodConsumption * foodConsumer.getFoodConsumptionMultiplier());
	}
	
	public void consumeFood(){
		int remainder = reduceFood(getDailyFoodConsumption());
		if (remainder > 0){
			//Reduce expedition resistance
			starveResistance --;
			if (starveResistance <= 0){
				int unitsToKill = (int)Math.ceil((double) foodConsumer.getTotalUnits()*(double)Util.rand(5, 40)/100.0d);
				if (unitsToKill > 0)
					foodConsumer.killUnits(unitsToKill);
			}
		} else {
			if (starveResistance < 5)
				starveResistance++;
		}
	}
	
	public int reduceFood(int quantity){
		int foodToSpend = quantity;
		List<Equipment> inventory = this.foodConsumer.getInventory();
		int originalSize = inventory.size();
		for (int i = 0; i < inventory.size(); i++){
			Equipment equipment = (Equipment) inventory.get(i);
			if (equipment.getItem() instanceof Food){
				Food good = (Food)equipment.getItem();
				int unitsToSpend = (int)Math.ceil((double)foodToSpend / (double)good.getUnitsFedPerGood());
				if (unitsToSpend > equipment.getQuantity()){
					unitsToSpend = equipment.getQuantity();
				}
				foodToSpend -= unitsToSpend * good.getUnitsFedPerGood();
				foodConsumer.reduceQuantityOf(equipment.getItem(), unitsToSpend);
				if (inventory.size() < originalSize){
					//Means the people ate all of a kind of item, and it was removed from the inventory
					// And so, we must check again the same position of the inventory array
					i--;
					originalSize = inventory.size();
				}
				if (foodToSpend <= 0){
					return 0;
				}

			}
		}
		return foodToSpend;
	}
	private Equipment chooseRandomEquipmentUsingWeights(List<Pair<Equipment,Double>> weights){
		double pin = Util.rand(0, 100) / 100.0d;
		for (Pair<Equipment,Double> weightedEquipment: weights){
			if (pin < weightedEquipment.getB())
				return weightedEquipment.getA();
		}
		return weights.get(weights.size()-1).getA();
	}
	
	private final static Comparator<Pair<Equipment,Double>> WEIGHTED_EQUIPMENT_COMPARATOR = new Comparator<Pair<Equipment,Double>> (){
		public int compare(Pair<Equipment, Double> o1,
				Pair<Equipment, Double> o2) {
			return o1.getB().compareTo(o2.getB());
		}
	};
	/**
	 * Kills a number of unit from the Food Consumer
	 * 
	 * @param deaths The number of units to kill
	 * @return A collection describing the number of individuals killed from each kind of unit
	 */
	public Collection<Pair<ExpeditionUnit, Integer>> killUnits(int deaths) {
		List<Equipment> inventory = foodConsumer.getInventory();
		Hashtable<String, Pair<ExpeditionUnit, Integer>> acumHash = new Hashtable<String, Pair<ExpeditionUnit,Integer>>();
		int totalUnits = foodConsumer.getTotalUnits();
		List<Pair<Equipment,Double>> weights = new ArrayList<Pair<Equipment,Double>>();

		for (int i = 0; i < deaths; i++){
			weights.clear();
			for (Equipment equipment: inventory){
				if (equipment.getItem() instanceof ExpeditionUnit){
					weights.add(new Pair<Equipment, Double>(equipment, equipment.getQuantity()/(double)totalUnits));
				}
			}
			Collections.sort(weights, WEIGHTED_EQUIPMENT_COMPARATOR);
			double acum = 0;
			for (Pair<Equipment,Double> weightedEquipment: weights){
				weightedEquipment.setB(acum+weightedEquipment.getB());
				acum = weightedEquipment.getB(); 
			}
			Equipment choosenToKill = chooseRandomEquipmentUsingWeights(weights);
			String itemId = choosenToKill.getItem().getFullID();
			Pair<ExpeditionUnit, Integer> currentlyKilled = acumHash.get(itemId);
			if (currentlyKilled == null){
				currentlyKilled = new Pair<ExpeditionUnit, Integer>((ExpeditionUnit)choosenToKill.getItem(), 1);
				acumHash.put(itemId, currentlyKilled);
			} else {
				currentlyKilled.setB(currentlyKilled.getB()+1);
			}
			foodConsumer.reduceQuantityOf(choosenToKill.getItem(), 1);
			
			ExpeditionUnit unit = ((ExpeditionUnit)choosenToKill.getItem()); 
			
			if (unit.getWeapon() != null && foodConsumer.getLevel() instanceof ExpeditionMacroLevel && Util.chance(60)){
				((ExpeditionMacroLevel)foodConsumer.getLevel()).addEquipment(unit.getWeapon(), 1, foodConsumer.getPosition());
			}
			
			if (unit.getArmor() != null && foodConsumer.getLevel() instanceof ExpeditionMacroLevel && Util.chance(40)){
				((ExpeditionMacroLevel)foodConsumer.getLevel()).addEquipment(unit.getArmor(), 1, foodConsumer.getPosition());
			}
				
			
			
			if (foodConsumer.getTotalUnits() == 0)
				break;
			
		}
		foodConsumer.checkDeath();
		return acumHash.values();
	}
}
