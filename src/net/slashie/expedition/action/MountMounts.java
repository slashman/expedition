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
			getExpedition().mount();
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
