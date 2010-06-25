package net.slashie.expedition.action;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.Vehicle;
import net.slashie.expedition.domain.Expedition.MovementMode;
import net.slashie.serf.action.Action;

/**
 * Recovers resistance to the expedition ships
 * Carpenters are ten times better than other crew for this
 * This action will take one day (must consume food for the whole expedition)
 * This action requires 5 wood for each man to work
 * The amount recovered depends on the number of carpenters
 * If the repairs are done off-shore, recuperation is decimated  
 * @author Slash
 *
 */
public class RepairShips extends Action{

	private static final double RECOVERY_INDEX = 0.1;
	private static final int WOOD_PER_REPAIRMEN = 5;
	private static final int CARPENTER_MULTIPLIER = 10;

	@Override
	public void execute() {
		Expedition expedition = (Expedition) performer;
		if (expedition.getMovementMode() != MovementMode.SHIP){
			expedition.getLevel().addMessage("What ship?");
			return;
		}
		int availableWood = expedition.getItemCount("WOOD");
		int usedWood = availableWood; // This gets reduced according to expenses
		int maxRepairmen = availableWood / WOOD_PER_REPAIRMEN;
		int remainingRepairmen = maxRepairmen;
		int carpenters = expedition.getItemCount("CARPENTER");
		int normalCrew = expedition.getTotalUnits() - carpenters;
		
		if (carpenters >= remainingRepairmen){
			carpenters = remainingRepairmen;
			remainingRepairmen = 0;
		} else {
			remainingRepairmen -= carpenters;
		}
		
		if (normalCrew >= remainingRepairmen){
			normalCrew = remainingRepairmen;
			remainingRepairmen = 0;
		} else {
			remainingRepairmen -= normalCrew;
		}
		
		int recoveryPower = carpenters * CARPENTER_MULTIPLIER + normalCrew;
		
		int recovery = (int)Math.round(recoveryPower*RECOVERY_INDEX); 
		int remainingRecovery = recovery;
		// Select ships, starting from the most damaged ones, to repair
		List<Vehicle> ships = expedition.getCurrentVehicles();
		Collections.sort(ships, new Comparator<Vehicle>(){
			public int compare(Vehicle o1, Vehicle o2) {
				return o1.getResistance() - o2.getResistance();
			}
		});
		for (Vehicle ship: ships){
			int damage = ship.getMaxResistance() - ship.getResistance();
			if (damage >= remainingRecovery){
				ship.recoverResistance(remainingRecovery);
				remainingRecovery = 0;
			} else {
				ship.recoverResistance(damage);
				remainingRecovery -= damage;
			}
		}
		
		// TODO: Use up wood, but how much of it???
		TODO: Use up wood, but how much of it???
		// If there's still recovery, it means we didnt use up all wood
		if (remainingRecovery > 0){
			
		}
		
	}

	@Override
	public String getID() {
		return "RepairShips";
	}

}
