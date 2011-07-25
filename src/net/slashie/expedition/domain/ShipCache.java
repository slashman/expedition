package net.slashie.expedition.domain;

import java.util.List;

import net.slashie.expedition.domain.Expedition.MovementMode;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.ui.ExpeditionUserInterface;
import net.slashie.serf.action.Actor;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.sound.STMusicManagerNew;
import net.slashie.serf.ui.UserInterface;

public class ShipCache extends GoodsCache{
	private List<Vehicle> vehicles;
	
	public ShipCache(ExpeditionGame game, List<Vehicle> vehicles) {
		super(game);
		this.vehicles = vehicles;
		setAppearanceId("SHIP");
	}

	public int getCarryable(ExpeditionItem item){
		return (int)Math.floor((getCarryCapacity()-getCurrentWeight())/item.getWeight());
	}
	
	public int getCarryCapacity(){
		int carryCapacity = 0;
		for (Vehicle equipment: vehicles){
			carryCapacity += equipment.getCarryCapacity();
		}
		return carryCapacity;
	}
	
	public int getTotalShips(){
		return vehicles.size();
	}

	public int getCurrentWeight(){
		int currentlyCarrying = 0;
		List<Equipment> inventory = getItems();
		for (Equipment equipment: inventory){
			if (!(equipment.getItem() instanceof Vehicle)){
				currentlyCarrying += ((ExpeditionItem)equipment.getItem()).getWeight() * equipment.getQuantity();
			}
			if (equipment.getItem() instanceof ExpeditionUnit){
				currentlyCarrying += ((ExpeditionItem)equipment.getItem()).getWeight() * equipment.getQuantity();
			}
		}
		return currentlyCarrying;
	}
	
	@Override
	public String getClassifierID() {
		return "Ship";
	}
	
	@Override
	public String getDescription(){
		return "Ships";
	}
	
	@Override
	public void onStep(Actor a) {
		if (a instanceof Expedition && !(a instanceof NonPrincipalExpedition)){
			switch (UserInterface.getUI().switchChat("Ships","What do you want to do?", "Transfer To Expedition", "Transfer To Ship", "Board Ship")){
			case 0:
				((ExpeditionUserInterface)UserInterface.getUI()).transferFromCache(this);
    			break;
			case 1:
				((ExpeditionUserInterface)UserInterface.getUI()).transferFromExpedition(this);
				break;
			case 2:
				if (canCarryWeight(((Expedition)a).getWeightToBoardShip())){
					((Expedition)a).setMovementMode(MovementMode.SHIP);
					((Expedition)a).setCurrentVehicles(vehicles);
					((Expedition)a).addAllItems(getItems());
					((Expedition)a).getLevel().destroyFeature(this);
					a.setPosition(getPosition());
					STMusicManagerNew.thus.playKey("SEA");
				} else {
					UserInterface.getUI().showMessage("The ships are too full!");
				}
				break;
    		}
		}
	}
	
	@Override
	public boolean canCarry(ExpeditionItem item, int quantity) {
		//return getCarryable(item) >= quantity;
		return getCurrentWeight() + item.getWeight() * quantity <= getCarryCapacity();
	}
	
	public boolean canCarryWeight(int weight){
		//return getCarryCapacity() >= weight;
		return getCurrentWeight() + weight <= getCarryCapacity();
	}
	
	@Override
	public boolean isInfiniteCapacity() {
		return false;
	}
	
	@Override
	public boolean destroyOnEmpty() {
		return false;
	}
}
