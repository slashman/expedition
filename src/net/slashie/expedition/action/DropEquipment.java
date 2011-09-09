package net.slashie.expedition.action;

import java.util.ArrayList;
import java.util.List;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.ExpeditionUnit;
import net.slashie.expedition.domain.GoodType;
import net.slashie.expedition.domain.GoodsCache;
import net.slashie.expedition.domain.LandingParty;
import net.slashie.expedition.domain.SeaPseudoCache;
import net.slashie.expedition.domain.ShipCache;
import net.slashie.expedition.domain.Town;
import net.slashie.expedition.domain.Vehicle;
import net.slashie.expedition.domain.Expedition.MovementMode;
import net.slashie.expedition.domain.LandingParty.LandingSpec;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.game.ExpeditionMusicManager;
import net.slashie.expedition.ui.ExpeditionUserInterface;
import net.slashie.expedition.world.ExpeditionMacroLevel;
import net.slashie.expedition.world.OverworldExpeditionCell;
import net.slashie.serf.action.Action;
import net.slashie.serf.action.Actor;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.game.Player;
import net.slashie.serf.sound.STMusicManagerNew;
import net.slashie.serf.ui.ActionCancelException;
import net.slashie.serf.ui.UserInterface;
import net.slashie.utils.Position;

/**
 * Allow transfering equipment from an expedition into a caché, or drop it in the sea, or landing
 * @author Slash
 *
 */
public class DropEquipment extends Action{
	private static final long serialVersionUID = 1L;

	@Override
	public void execute(){
		Expedition expedition = (Expedition)performer;
		OverworldExpeditionCell standingCell = ((OverworldExpeditionCell) performer.getLevel().getMapCell(performer.getPosition()));
		if (standingCell.isLand()){
			// Droping things from expedition into land caché
			GoodsCache cache = ((ExpeditionMacroLevel)performer.getLevel()).getOrCreateCache(performer.getPosition());
			performer.setPosition(cache.getPosition());
			((ExpeditionUserInterface)UserInterface.getUI()).transferFromExpedition(cache);
			if (cache.destroyOnEmpty() && cache.getItems().size() == 0)
				performer.getLevel().destroyFeature(cache);
		} else {
			Town town = expedition.getDockingTown();
			if (town != null){
    			((ExpeditionUserInterface)UserInterface.getUI()).transferFromExpedition(town);
			} else if (expedition.canDisembark()){
				int choice = UserInterface.getUI().switchChat("Landfall","What do you want to do?",
		        		"Land using a predefined group",
		        		"Arm people and land using a predefined group",
		        		"Select the members of the expedition",
		        		"Continue sailing"
		        		); 
				
				if (choice == 1){
					expedition.arm();
				}
				
				ShipCache ship = new ShipCache((ExpeditionGame)expedition.getGame(), expedition.getCurrentVehicles());
				ship.addAllGoods(expedition);
    			
        		switch (choice){
        		case 0: case 1:
        			LandingParty landingParty = ((ExpeditionUserInterface)UserInterface.getUI()).selectLandingParty();
        			
        			List<Equipment> transferredEquipment = selectUnitsForLanding(landingParty, ship);
        			if (transferredEquipment.size() == 0){
        				UserInterface.getUI().showImportantMessage("Your expedition doesn't have units to make that landing group.");
    					expedition.removeAllGoods();
            			expedition.setMovementMode(MovementMode.FOOT);
            			expedition.setCurrentVehicles(new ArrayList<Vehicle>());
            			((ExpeditionUserInterface)UserInterface.getUI()).transferFromCache("Select the units and goods to transfer", GoodType.PEOPLE, ship);
        			} else if ( ((ExpeditionUserInterface)UserInterface.getUI()).promptUnitList(transferredEquipment, "Landing Group", "These units will disembark, is this ok?")){
        				// Initialize the transfer
        				expedition.removeAllGoods();
            			expedition.setMovementMode(MovementMode.FOOT);
            			expedition.setCurrentVehicles(new ArrayList<Vehicle>());
        				
        				// Transfer Units
            			ship.reduceAllItems(transferredEquipment);
            			expedition.addAllItems(transferredEquipment);
            			
            			if (landingParty.isMounted()){
    	        			if (expedition.getItemCountBasic("HORSE") > 0){
    	        				expedition.mount();
    	        			}
            			}
            			// Allow transfering supplies
            			((ExpeditionUserInterface)UserInterface.getUI()).transferFromCache("Transfer supplies to the expedition", GoodType.SUPPLIES, ship);
        			} else {
        				return;
        			}
        			break;
        		case 2:
        			expedition.removeAllGoods();
        			expedition.setMovementMode(MovementMode.FOOT);
        			expedition.setCurrentVehicles(new ArrayList<Vehicle>());
        			
        			((ExpeditionUserInterface)UserInterface.getUI()).transferFromCache("Select the units and goods to transfer", GoodType.PEOPLE, ship);
        			break;
        		case 3:
        			// Continue sailing
        			return;
        		}

        		ship.setPosition(new Position(expedition.getPosition()));
    			expedition.getLevel().addFeature(ship);
    			//expedition.setPosition(ship.getPosition());
    			if (expedition.getUnmountedUnits().size() == 0){
    				expedition.setMovementMode(MovementMode.HORSE);
				}
    			
    			// Check if this was a null disembarkment (No units)
    			if (expedition.getTotalUnits() == 0) {
    				// "Board Ships" again
    				ship.boardShips(expedition);
    				expedition.getLevel().addMessage("Disembarkment aborted");
    			} else {
	    			try {
	    				expedition.landOn(expedition.getLandCellAround());
	    			} catch (ActionCancelException ace){
	    				ace.printStackTrace();
	    			}
    			}
			} else {
				//Drop things into the big sea
        		if (UserInterface.getUI().promptChat("No land nearby; do you want to drop equipment into the sea?")){
        			GoodsCache cache = new SeaPseudoCache((ExpeditionGame)((Player)performer).getGame());
    				((ExpeditionUserInterface)UserInterface.getUI()).transferFromExpedition(cache);
        		}
			}
		}
	}

	private List<Equipment> selectUnitsForLanding(LandingParty landingParty, GoodsCache ship) {
		List<Equipment> ret = new ArrayList<Equipment>();
		if (landingParty.getCrew() != LandingSpec.NONE){
			addItemsForLanding(ship, ret, landingParty.getCrew(), "CAPTAIN", "SAILOR");
		}
		if (landingParty.getDoctors() != LandingSpec.NONE){
			addItemsForLanding(ship, ret, landingParty.getDoctors(), "DOCTOR");
		}
		if (landingParty.getExplorers() != LandingSpec.NONE){
			addItemsForLanding(ship, ret, landingParty.getExplorers(), "EXPLORER");
		}
		if (landingParty.getHorses() != LandingSpec.NONE){
			addItemsForLanding(ship, ret, landingParty.getHorses(), "HORSE");
		}
		if (landingParty.getSoldiers() != LandingSpec.NONE){
			addItemsForLanding(ship, ret, landingParty.getSoldiers(), "ROGUE", "MARINE", "SOLDIER", "GUARD", "ARCHER", "NATIVE_WARRIOR", "NATIVE_ARCHER", 
					"EAGLE_WARRIOR", "JAGUAR_WARRIOR", "QUETZAL_ARCHER");
		}
		if (landingParty.getCarpenters() != LandingSpec.NONE){
			addItemsForLanding(ship, ret, landingParty.getCarpenters(), "CARPENTER");
		}
		
		if (landingParty.getColonists() != LandingSpec.NONE){
			addItemsForLanding(ship, ret, landingParty.getColonists(), "COLONIST");
		}

		return ret;
	}
	
	private void addItemsForLanding(GoodsCache from, List<Equipment> to, LandingSpec spec, String... baseIds) {
		//int quantity = 0;
		switch (spec){
		case NONE:
			return;
		case ALL:
			for (String baseId: baseIds){
				List<Equipment> equipments = from.getItemsWithBaseID(baseId);
				for (Equipment equipment: equipments){
					to.add(equipment.clone());
				}
			}
			return;
		case HALF:
			for (String baseId: baseIds){
				List<Equipment> equipments = from.getItemsWithBaseID(baseId);
				for (Equipment equipment: equipments){
					int quantity = equipment.getQuantity();
					if (baseId.equals("HORSE")){
						// Check if there are already mounted units on the expedition
						for (Equipment mountedEquipment: to){
							if (mountedEquipment.getItem() instanceof ExpeditionUnit && ((ExpeditionUnit)mountedEquipment.getItem()).isMounted()){
								quantity -= mountedEquipment.getQuantity();
							}
						}
					} 
					quantity = (int)Math.ceil((double)quantity/2.0d);
					
					Equipment clone = equipment.clone();
					clone.setQuantity(quantity);
					to.add(clone);
				}
			}
			
			return;
		case ONE:
			next: for (String baseId: baseIds){
				List<Equipment> equipments = from.getItemsWithBaseID(baseId);
				for (Equipment equipment: equipments){
					if (baseId.equals("HORSE")){
						// Check if there are already mounted units on the expedition
						for (Equipment mountedEquipment: to){
							if (mountedEquipment.getItem() instanceof ExpeditionUnit && ((ExpeditionUnit)mountedEquipment.getItem()).isMounted()){
								// No need to add more horses
								continue next;
							}
						}
					}
					if (from.getItemCountBasic(baseId) > 0){
						Equipment clone = equipment.clone();
						clone.setQuantity(1);
						to.add(clone);
					}
				}
			}
			return;
		}
	}

	@Override
	public String getID() {
		return "DropEquipment";
	}
	
	@Override
	public boolean canPerform(Actor a) {
		if (!(a.getLevel() instanceof ExpeditionMacroLevel))
			return false;
		return true;
	}
	
	@Override
	public String getInvalidationMessage() {
		if (!(performer.getLevel() instanceof ExpeditionMacroLevel))
			return "You can't drop your equipment here!";
		return "";
	}
}
