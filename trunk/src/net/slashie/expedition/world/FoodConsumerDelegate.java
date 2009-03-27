package net.slashie.expedition.world;

import java.io.Serializable;
import java.util.List;

import net.slashie.expedition.domain.ExpeditionUnit;
import net.slashie.expedition.domain.Food;
import net.slashie.expedition.domain.Good;
import net.slashie.expedition.domain.GoodType;
import net.slashie.serf.game.Equipment;
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
		return dailyFoodConsumption * foodConsumer.getFoodConsumptionMultiplier();
	}
	
	public void consumeFood(){
		int remainder = reduceFood(getDailyFoodConsumption());
		if (remainder > 0){
			//Reduce expedition resistance
			starveResistance --;
			if (starveResistance <= 0){
				killUnits((double)Util.rand(5, 40)/100.0d);
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
	
	public void killUnits(double proportion){
		List<Equipment> inventory = this.foodConsumer.getInventory();
		for (int i = 0; i < inventory.size(); i++){
			Equipment equipment = inventory.get(i);
			if (equipment.getItem() instanceof ExpeditionUnit){
				int killUnits = (int)Math.ceil(equipment.getQuantity() * proportion);
				foodConsumer.reduceQuantityOf(equipment.getItem(), killUnits);
			}
		}
		this.foodConsumer.checkDeath();
	}
}
