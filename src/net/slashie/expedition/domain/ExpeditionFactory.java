package net.slashie.expedition.domain;

import java.util.List;

import net.slashie.expedition.action.ArmExpedition;
import net.slashie.expedition.action.Bump;
import net.slashie.expedition.domain.Armor.ArmorType;
import net.slashie.expedition.domain.Expedition.MovementMode;
import net.slashie.expedition.domain.Weapon.WeaponType;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.item.ItemFactory;
import net.slashie.serf.action.Action;
import net.slashie.serf.ai.SimpleAI;
import net.slashie.serf.game.Equipment;
import net.slashie.util.Pair;
import net.slashie.util.Util;

public class ExpeditionFactory {
	public static Expedition getExpedition(String classifierId, int expeditionPower){
		ExpeditionGame game = ExpeditionGame.getCurrentGame();
		NonPrincipalExpedition ret = new NonPrincipalExpedition(game, "hostileExpedition"+game.getLastExpeditionId());
		ret.setGame(game);
		ret.setAppearanceId("HOSTILE_EXPEDITION");
		ret.setName("natives");
		ret.setExpeditionary("-");
		
		SimpleAI ai = new SimpleAI(game.getPlayer(), new Bump()) ;
		ai.setBumpEnemy(true);
		ret.setSelector(ai);
		
		//int expeditionPower = Util.rand(1, 4);

		ret.addItem(ItemFactory.createItem("NATIVE_WARRIOR"), Util.rand(10, expeditionPower*10));
		ret.addItem(ItemFactory.createItem("NATIVE_BRAVE"), Util.rand(0, expeditionPower*5));
		ret.addItem(ItemFactory.createItem("ARROWS"), Util.rand(0, expeditionPower*30));
		ret.addItem(ItemFactory.createItem("NATIVE_ARCHER"), Util.rand(0, expeditionPower*10));
		ret.addItem(ItemFactory.createItem("GOLD_NUGGET"), Util.rand(0, expeditionPower*5));
		ret.addItem(ItemFactory.createItem("GOLD_BRACELET"), Util.rand(0, expeditionPower*7));
		ret.addItem(ItemFactory.createItem("NATIVE_ARTIFACT"), Util.rand(0, expeditionPower*10));
		ret.addItem(ItemFactory.createItem("NATIVE_FOOD"), Util.rand(expeditionPower*100, expeditionPower*700));
		
		ret.calculateInitialPower();
		return ret;
	}
	
	public static Expedition deployTroops(NativeTown town, int expeditionPower){
		ExpeditionGame game = ExpeditionGame.getCurrentGame();
		NonPrincipalExpedition ret = new NonPrincipalExpedition(game, "nativeExpedition"+game.getLastExpeditionId());
		ret.setGame(game);
		ret.setAppearanceId("HOSTILE_EXPEDITION");
		ret.setName(town.getCulture().getName()+" group");
		ret.setExpeditionary("-");
		
		SimpleAI ai = new SimpleAI(game.getPlayer(), new Bump()) ;
		ai.setBumpEnemy(true);
		ret.setSelector(ai);
		int targetPopulation = expeditionPower*30 + Util.rand(-expeditionPower*20, expeditionPower * 20);
		int specializedPopulation = 0;
		for(Pair<Double, String> classD: town.getCulture().getClassDistribution()){
			int wantedClassPopulation = (int) (classD.getA().doubleValue() * targetPopulation);
			if (wantedClassPopulation > 0){
				int availableClassPopulation = town.getItemCount(classD.getB()); 
				if (availableClassPopulation < wantedClassPopulation){
					wantedClassPopulation = availableClassPopulation;
				}
				if (wantedClassPopulation > 0){
					ExpeditionUnit unit = (ExpeditionUnit)ItemFactory.createItem(classD.getB());
					ret.addItem(unit, wantedClassPopulation);
					town.reduceQuantityOf(classD.getB(), wantedClassPopulation);
					if (unit.getWeaponTypes().length>0){
						int wantedWeapons = wantedClassPopulation;
						for (WeaponType preferredType: unit.getWeaponTypes()){
							List<Weapon> itemIds = ItemFactory.getItemsByWeaponType(preferredType);
							for (Weapon itemId: itemIds){
								int count = town.getItemCount(itemId.getFullID());
								if (count == 0)
									continue;
								int quantityToAdd = count;
								if (quantityToAdd > wantedWeapons){
									quantityToAdd = wantedWeapons;
								}
								ret.addItem(ItemFactory.createItem(itemId.getFullID()), quantityToAdd);
								town.reduceQuantityOf(itemId, quantityToAdd);
								wantedWeapons -= quantityToAdd;
							}
						}
						int wantedArmor = wantedClassPopulation;

						// Armors
						for (ArmorType preferredType: unit.getArmorTypes()){
							List<Armor> itemIds = ItemFactory.getItemsByArmorType(preferredType);
							for (Armor itemId: itemIds){
								int count = town.getItemCount(itemId.getFullID());
								if (count == 0)
									continue;
								int quantityToAdd = count;
								if (quantityToAdd > wantedArmor){
									quantityToAdd = wantedArmor;
								}
								ret.addItem(ItemFactory.createItem(itemId.getFullID()), quantityToAdd);
								town.reduceQuantityOf(itemId, quantityToAdd);
								wantedArmor -= quantityToAdd;
							}
						}
						
					}
				}
			}
			specializedPopulation += wantedClassPopulation;
			
		}
		int commoners = targetPopulation - specializedPopulation - 1;
		if (commoners > 0){
			ret.addItem(ItemFactory.createItem("NATIVE_COMMONER"), commoners);
			town.reduceQuantityOf("NATIVE_COMMONER", commoners);
		}
		
		// Add some treasure
		List<Equipment> items = town.getItems();
		int treasure = Util.rand(0, (int)Math.round(ret.getTotalUnits() / 2.0d));
		for (int i = 0; i < items.size(); i++){
			Equipment item = (Equipment) items.get(i);
			GoodType goodType = ((ExpeditionItem)item.getItem()).getGoodType(); 
			if (goodType == GoodType.ARMORY || goodType == GoodType.PEOPLE)
				continue;
			int quantity = Util.rand(0, expeditionPower*3);
			int max = town.getItemCount(item.getItem().getFullID()) - 1;
			if (quantity > max)
				quantity = max;
			if (quantity > treasure){
				quantity = treasure;
				treasure = 0;
			} else {
				treasure -= quantity;
			}
			
			ret.addItem(ItemFactory.createItem(item.getItem().getFullID()), quantity);
			town.reduceQuantityOf(ItemFactory.createItem(item.getItem().getFullID()), quantity);
			if (treasure <= 0)
				break;
		}
		
		
		Action armExpedition = new ArmExpedition();
		armExpedition.setPerformer(ret);
		armExpedition.execute();
		
		ret.calculateInitialPower();
		
		return ret;
		
	}

	
	public static Expedition createPlayerExpedition(String expeditionName, String expeditionaryName, ExpeditionGame game) {
		expeditionName = expeditionName.trim();
		expeditionaryName = expeditionaryName.trim();
		Expedition ret = new Expedition(game);
		ret.setGame(game);
		ret.setAppearanceId("EXPEDITION");
		ret.setName(expeditionName);
		ret.setExpeditionary(expeditionaryName);
		ret.setAccountedGold(0);
		ExpeditionUnit explorer = (ExpeditionUnit)ItemFactory.createItem("COLOMBUS");
		explorer.setDescription(expeditionaryName);
		explorer.setName(expeditionaryName);
		explorer.updateCompositeVariables();
		ret.setLeaderUnit(explorer);
		ret.setMovementMode(MovementMode.FOOT);
		
		return ret;
	}
}
