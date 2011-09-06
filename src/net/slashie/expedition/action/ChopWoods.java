package net.slashie.expedition.action;

import java.util.List;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.ExpeditionItem;
import net.slashie.expedition.domain.GoodsCache;
import net.slashie.expedition.item.ItemFactory;
import net.slashie.expedition.world.ExpeditionMacroLevel;
import net.slashie.expedition.world.Forest;
import net.slashie.expedition.world.OverworldExpeditionCell;
import net.slashie.serf.action.Action;
import net.slashie.serf.action.Actor;
import net.slashie.serf.baseDomain.AbstractItem;
import net.slashie.serf.level.AbstractFeature;
import net.slashie.utils.Position;
import net.slashie.utils.Util;

public class ChopWoods extends Action{

	@Override
	public void execute() {
		Expedition expedition = (Expedition) performer;
		
		ExpeditionMacroLevel level = (ExpeditionMacroLevel)  expedition.getLocation();
		
		if (expedition.getLocation().getLocation().getB() >= -30){
			level.addMessage("This forest is not your property!");
			return;
		}

		
		OverworldExpeditionCell cell = (OverworldExpeditionCell) performer.getLevel().getMapCell(performer.getPosition());
		if (cell.isWood()){
			AbstractFeature f = null;
			List<AbstractFeature> otherFeatures = level.getFeaturesAt(expedition.getPosition());
			if (otherFeatures == null){
				
			} else {
				for (AbstractFeature feature: otherFeatures){
					if (feature instanceof Forest){
						f = feature;
						break;
					}
				}
			}
			if ( f == null ) {
				//No feature here yet, but since this is a forest, let's add a forest feature;
				f = new Forest(Util.rand(150, 250));
				f.setPosition(new Position(expedition.getPosition()));
				level.addFeature(f);
				expedition.setPosition(f.getPosition());
			} else if (((Forest)f).getAvailableWood() == 0){
				level.addMessage("There's no more wood in this forest.");
				return ;
			}
			
			int wood = ((Forest)f).substractWood(expedition);
			ExpeditionItem woodSample = ItemFactory.createItem("WOOD");
			int currentWood = expedition.getItemCount("WOOD");
			GoodsCache cache = level.getCache(f.getPosition());
			int currentLandWood = 0;
			if (cache != null)
				currentLandWood = cache.getItemCount("WOOD");
			if (currentLandWood > 0){
				level.addMessage("You chop "+wood+" wood. ("+currentWood+" in expedition, "+currentLandWood+" on land)");
			} else {
				level.addMessage("You chop "+wood+" wood. ("+currentWood+" in expedition)");
			}
			
			if (expedition.canCarry(woodSample, wood)){
				
				expedition.addItem(woodSample, wood);
			} else {
				level.addEquipment(woodSample, wood, expedition.getPosition());
			}
			if (((Forest)f).getAvailableWood() == 0){
				//level.destroyFeature(f);
				level.addMessage("There's no more wood in this forest.");
			}
			
		}
		
	}
	
	private String invalidationMessage;
	@Override
	public boolean canPerform(Actor a) {
		Expedition expedition = (Expedition) a;
		performer = a;
		if (!(performer.getLevel() instanceof ExpeditionMacroLevel)){
			invalidationMessage = "You can't chop here";
			return false;
		}
		ExpeditionMacroLevel level = (ExpeditionMacroLevel)  expedition.getLocation();
		
		if (expedition.getLocation().getLocation().getB() >= -30){
			invalidationMessage = "This forest is not your property!";
			return false;
		}
		
		OverworldExpeditionCell cell = (OverworldExpeditionCell) performer.getLevel().getMapCell(performer.getPosition());
		if (cell.isWood()){
			AbstractFeature f = null;
			List<AbstractFeature> otherFeatures = level.getFeaturesAt(expedition.getPosition());
			if (otherFeatures == null){
				
			} else {
				for (AbstractFeature feature: otherFeatures){
					if (feature instanceof Forest){
						f = feature;
						break;
					}
				}
			}
			if ( f == null ) {
				
			} else if (((Forest)f).getAvailableWood() == 0){
				invalidationMessage = "There is no more wood here.";
				return false;
			}
		} else {
			invalidationMessage = "There is no wood here.";
			return false;
		}
		return true;
	}
	
	@Override
	public String getInvalidationMessage() {
		return invalidationMessage;
	}

	@Override
	public String getID() {
		return "ChopWoods";
	}

}
