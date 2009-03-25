package net.slashie.expedition.action;

import java.util.List;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.ExpeditionUnit;
import net.slashie.expedition.domain.Good;
import net.slashie.expedition.domain.Weapon;
import net.slashie.expedition.item.ItemFactory;
import net.slashie.expedition.world.ExpeditionMicroLevel;
import net.slashie.serf.action.Action;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.ui.UserInterface;

public class ArmExpedition extends Action{
	@Override
	public void execute() {
		if (!getExpedition().isArmed()){
			if (!UserInterface.getUI().promptChat("Arm Expedition: Are you sure?"))
				return;
			
			List<Equipment> units = getExpedition().getUnarmedUnits();
			for (Equipment unit: units){
				String[] preferredWeapons = ((ExpeditionUnit)unit.getItem()).getWeaponTypes();
				for (String weaponType: preferredWeapons){
					int available = getExpedition().getGoodCount(weaponType);
					int unitsToArm = available > unit.getQuantity() ? unit.getQuantity() : available;
					getExpedition().reduceGood(weaponType, unitsToArm);
					//Split equipment in armed and disarmed
					if (unitsToArm > 0){
						getExpedition().reduceQuantityOf(unit.getItem(), unitsToArm);
						ExpeditionUnit newUnit = (ExpeditionUnit)ItemFactory.createItem(unit.getItem().getFullID());
						newUnit.setArm((Weapon)ItemFactory.createItem(weaponType));
						getExpedition().addItem(newUnit, unitsToArm);
					}
				}
			}
			getExpedition().setArmed(true);
		} else {
			if (!UserInterface.getUI().promptChat("Disarm Expedition: Are you sure?"))
				return;
			List<Equipment> units = getExpedition().getUnits();
			for (Equipment unit: units){
				ExpeditionUnit eUnit = ((ExpeditionUnit)unit.getItem()); 
				boolean useOffShore = getExpedition().getLocation() instanceof ExpeditionMicroLevel && ((ExpeditionMicroLevel)getExpedition().getLocation()).isDock(); 
				if (eUnit.getWeapon() != null){
					int quantity = unit.getQuantity();
					Weapon weapon = eUnit.getWeapon();
					getExpedition().reduceQuantityOf(eUnit, quantity);
					eUnit.setArm(null);
					
					if (useOffShore){
						getExpedition().addItemOffshore(eUnit, quantity);
						getExpedition().addItemOffshore(weapon, quantity);
					} else {
						getExpedition().addItem(eUnit, quantity);
						getExpedition().addItem(weapon, quantity);
					}
				}
			}
			getExpedition().setArmed(false);
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
		return getExpedition().getTotalUnits();
	}

}
