package net.slashie.expedition.action;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.slashie.expedition.domain.Armor;
import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.ExpeditionUnit;
import net.slashie.expedition.domain.GoodType;
import net.slashie.expedition.domain.Weapon;
import net.slashie.expedition.domain.Expedition.MovementMode;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.item.ItemFactory;
import net.slashie.expedition.item.Mount;
import net.slashie.expedition.world.ExpeditionMicroLevel;
import net.slashie.serf.action.Action;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.ui.UserInterface;

public class MountMounts extends Action{
	private boolean actionCancelled;
	@Override
	public void execute() {
		actionCancelled = false;
		boolean isPlayer = getExpedition() == ExpeditionGame.getCurrentGame().getPlayer(); 
		if (!getExpedition().isMounted()){
			if ( isPlayer && !UserInterface.getUI().promptChat("Ride your Mounts: Are you sure?")){
				actionCancelled = true;
				return;
			}
			
			List<Equipment> mounts = getExpedition().getMounts();
			for (Equipment mount: mounts){
				// For each kind of mount, try to mount all unmounted units
				List<Equipment> units = getExpedition().getUnmountedUnits();
				Collections.sort(units, new Comparator<Equipment>() {
					public int compare(Equipment arg0, Equipment arg1) {
						return ((ExpeditionUnit)arg1.getItem()).getAttack().getMax() - ((ExpeditionUnit)arg0.getItem()).getAttack().getMax();
					}
				});
				for (Equipment unit: units){
					int available = mount.getQuantity();
					int unitsToMount = available > unit.getQuantity() ? unit.getQuantity() : available;
					getExpedition().reduceQuantityOf(mount.getItem(), unitsToMount);
					//Split equipment in mounted and unmounted
					if (unitsToMount > 0){
						getExpedition().reduceQuantityOf(unit.getItem(), unitsToMount);
						ExpeditionUnit newUnit = (ExpeditionUnit)((ExpeditionUnit)unit.getItem()).clone();
						newUnit.setMount((Mount)mount.getItem());
						getExpedition().addItem(newUnit, unitsToMount);
					}
				}
			}
			
			getExpedition().setMounted(true);
			
			if (getExpedition().getMovementMode() == MovementMode.FOOT){
				if (getExpedition().getUnmountedUnits().size() == 0){
					getExpedition().setMovementMode(MovementMode.HORSE);
				}
			}

			
		} else {
			if (isPlayer && !UserInterface.getUI().promptChat("Dismount your units: Are you sure?")){
				actionCancelled = true;
				return;
			}
			
			if (getExpedition().getMovementMode() == MovementMode.HORSE){
				getExpedition().setMovementMode(MovementMode.FOOT);
			}

				
			List<Equipment> units = getExpedition().getGoods(GoodType.PEOPLE);
			for (Equipment unit: units){
				ExpeditionUnit eUnit = ((ExpeditionUnit)unit.getItem()); 
				boolean useOffShore = isPlayer 
					&& 
					getExpedition().getLocation() instanceof ExpeditionMicroLevel && 
					((ExpeditionMicroLevel)getExpedition().getLocation()).isDock(); 
				if (eUnit.getMount() != null){
					int quantity = unit.getQuantity();
					Mount mount = eUnit.getMount();
					getExpedition().reduceQuantityOf(eUnit, quantity);
					eUnit.setMount(null);
					
					if (useOffShore){
						getExpedition().addItemOffshore(eUnit, quantity);
						getExpedition().addItemOffshore(mount, quantity);
					} else {
						getExpedition().addItem(eUnit, quantity);
						getExpedition().addItem(mount, quantity);
					}
				}
			}
			getExpedition().setMounted(false);
		}
		
	}
	
	private Expedition getExpedition(){
		return (Expedition) performer;
	}
	
	@Override
	public String getID() {
		return "ArmExpedition";
	}
	
	@Override
	public int getCost() {
		if (actionCancelled)
			return 0;
		return getExpedition().getTotalUnits();
	}

}
