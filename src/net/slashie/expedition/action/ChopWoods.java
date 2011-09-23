package net.slashie.expedition.action;

import java.util.List;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.ExpeditionItem;
import net.slashie.expedition.domain.GoodsCache;
import net.slashie.expedition.domain.NativeTown;
import net.slashie.expedition.item.ItemFactory;
import net.slashie.expedition.level.GlobeMapModel;
import net.slashie.expedition.world.ExpeditionMacroLevel;
import net.slashie.expedition.world.Forest;
import net.slashie.expedition.world.OverworldExpeditionCell;
import net.slashie.serf.action.Action;
import net.slashie.serf.level.AbstractFeature;
import net.slashie.utils.Position;
import net.slashie.utils.Util;

@SuppressWarnings("serial")
public class ChopWoods extends Action{

	@Override
	public void execute() {
		actionCost = 50;
		Expedition expedition = (Expedition) performer;
		
		ExpeditionMacroLevel level = (ExpeditionMacroLevel)  expedition.getLocation();
		
		OverworldExpeditionCell cell = (OverworldExpeditionCell) performer.getLevel().getMapCell(performer.getPosition());
		if (cell.isWood()){
			// Are we in the old world?
			if (GlobeMapModel.getSingleton().getLongitudeDegrees(expedition.getPosition().x()) >= -30){
				level.addMessage("This forest is not your property!");
				actionCost = 0;
				return;
			}
			
			// Is there a neaby village?
			// Check distance from other settlements
			List<NativeTown> towns = level.getNativeTowns();
			for (NativeTown town: towns){
				int milesDistance = GlobeMapModel.getSingleton().getMilesDistance(town.getPosition(), expedition.getPosition());
				if (milesDistance < 40){
					level.addMessage("This forest is claimed by the "+town.getCulture().getName());
					actionCost = 0;
					return;
				}
			}
			
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
				actionCost = 0;
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
	
	@Override
	public String getInvalidationMessage() {
		return invalidationMessage;
	}

	@Override
	public String getID() {
		return "ChopWoods";
	}
	
	private int actionCost;
	@Override
	public int getCost() {
		return actionCost;
	}
}
