package net.slashie.expedition.domain;

import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.world.ExpeditionMacroLevel;
import net.slashie.expedition.world.OverworldExpeditionCell;
import net.slashie.utils.Util;


@SuppressWarnings("serial")
public class NonPrincipalExpedition extends Expedition{
	private String classifierId;
	private boolean isHostile = true;
	private int initialPower;
	
	public void calculateInitialPower(){
		initialPower = getPower();
	}
	
	public boolean isHostile() {
		return isHostile;
	}

	public void setHostile(boolean isHostile) {
		this.isHostile = isHostile;
	}

	public NonPrincipalExpedition(ExpeditionGame game, String classifierId) {
		super(game);
		this.classifierId = classifierId;
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
		if (((OverworldExpeditionCell)getLevel().getMapCell(getPosition())).isForest()){
			if (Util.chance(50))
				return MovementSpeed.FAST;
			else
				return MovementSpeed.NORMAL;
		} else {
			return MovementSpeed.NORMAL;
		}
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
