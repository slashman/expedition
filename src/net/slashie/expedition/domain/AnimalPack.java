package net.slashie.expedition.domain;

import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.world.ExpeditionMacroLevel;

@SuppressWarnings("serial")
public class AnimalPack extends NonPrincipalExpedition{
	private String classifierId;
	private boolean isHostile = false;
	
	public AnimalPack(ExpeditionGame game, String classifierId){
		super(game, classifierId);
		this.classifierId = classifierId;
	}

	public boolean isHostile() {
		return isHostile;
	}

	public void setHostile(boolean isHostile) {
		this.isHostile = isHostile;
	}
	
	@Override
	public String getClassifierID() {
		return classifierId;
	}
	
	@Override
	public String getDescription() {
		return getName();
	}
	
	@Override
	public double getFoodConsumptionMultiplier() {
		return 0;
	}
	
	public void checkDeath(){
		if (getTotalUnits() <= 0){
			((ExpeditionMacroLevel)getLevel()).addAllEquipment(this, getPosition());
			die();
		}
	}
	
	@Override
	public MovementSpeed getMovementSpeed() {
		return MovementSpeed.NORMAL;
	}
	
	public void consumeFood() {
		//Do Nothing
	}
	
	@Override
	public void afterActing() {
		super.afterActing();
		checkDeath();
	}
}
