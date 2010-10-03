package net.slashie.expedition.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.slashie.expedition.action.ArmExpedition;
import net.slashie.expedition.action.Bump;
import net.slashie.expedition.ai.NativeActionSelector;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.item.ItemFactory;
import net.slashie.expedition.ui.ExpeditionUserInterface;
import net.slashie.expedition.world.Culture;
import net.slashie.serf.action.Action;
import net.slashie.serf.action.ActionSelector;
import net.slashie.serf.ai.SimpleAI;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.ui.Appearance;
import net.slashie.serf.ui.AppearanceFactory;
import net.slashie.serf.ui.UserInterface;
import net.slashie.util.Pair;
import net.slashie.utils.Util;

public class NativeTown extends Town{
	private static final String[] NATIVE_ACTIONS = new String [] {
		"Raid the settlement",
		"Trade Goods",
		"Leave",
		//Peaceful
		/*
		"Amaze the natives", 
		"Demand tribute",
		"Beg for help",
		"Learn about enemies",*/
		//War
		/*"Offer Peace",
		"Capture natives",
		"Recruit"*/
	};
	private Culture culture;
	private boolean isDisabled;
	private int turnsBeforeNextExpedition;
	private Map<GoodType, Double> goodTypeModifiers = new HashMap<GoodType, Double>();
	private boolean isUnfriendly;
	
	public void setGoodTypeModifier(GoodType goodType, double modifier){
		goodTypeModifiers.put(goodType, modifier);
	}
	
	public void resetTurnsBeforeNextExpedition(){
		turnsBeforeNextExpedition = 0;
	}
	
	public NativeTown(ExpeditionGame game, Culture culture, int size) {
		super(game);
		this.culture = culture;
		founderExpedition = null;
		foundedIn = null;
		int targetPopulation = size * (1000 + Util.rand(0, 150));
		int specializedPopulation = 0;
		for(Pair<Double, String> classD: culture.getClassDistribution()){
			int classPopulation = (int) (classD.getA().doubleValue() * targetPopulation);
			if (classPopulation > 0)
				addItem(ItemFactory.createItem(classD.getB()), classPopulation);
			specializedPopulation += classPopulation;
		}
		int commoners = targetPopulation - specializedPopulation - 1;
		if (commoners > 0)
			addItem(ItemFactory.createItem("NATIVE_COMMONER"), commoners);
		addItem(ItemFactory.createItem("NATIVE_SHAMAN"), 1);
		
		List<Pair<GoodType, Double>> goodTypeValuationModifiers = culture.getGoodTypeValuationModifiers();
		for (Pair<GoodType, Double> goodTypeValuationModifier: goodTypeValuationModifiers){
			double range = goodTypeValuationModifier.getB() * 0.1d;
			double var = Util.rand(-range, range);
			setGoodTypeModifier(goodTypeValuationModifier.getA(), goodTypeValuationModifier.getB()+var);
		}
		
		//Add items
		for (String item: culture.getItems()){
			ExpeditionItem i = ItemFactory.createItem(item);
			double value = i.getAmericaValue();
			if (value == 0.0d)
				continue;
			double scarse = 1.0d / value; 
			int items = (int)Math.round((double)targetPopulation*scarse); 
			items += Util.rand(-items*0.1d, items*0.1d);
			addItem(i, items);
		}
		/*addItem(ItemFactory.createItem("GOLD_NUGGET"), Util.rand(0, size*culture.getGoldModifier()*20));
		addItem(ItemFactory.createItem("GOLD_BRACELET"), Util.rand(0, size*culture.getGoldModifier()*40));
		addItem(ItemFactory.createItem("NATIVE_ARTIFACT"), Util.rand(0, size*culture.getArtifactModifier()*30));
		addItem(ItemFactory.createItem("NATIVE_FOOD"), Util.rand(100, size*culture.getAgricultureModifier()*100));
		*/
		
		
	}
	
	@Override
	public Appearance getAppearance() {
		if (isTown()){
			setAppearanceId("NATIVE_TOWN");
			return AppearanceFactory.getAppearanceFactory().getAppearance("NATIVE_TOWN");
		} else if (isCity()){
			setAppearanceId("NATIVE_CITY");
			return AppearanceFactory.getAppearanceFactory().getAppearance("NATIVE_CITY");
		} else
			setAppearanceId("NATIVE_VILLAGE");
			return AppearanceFactory.getAppearanceFactory().getAppearance("NATIVE_VILLAGE");
	}
	
	@Override
	public String getDescription() {
		return culture.getName()+" "+super.getTitle();
	}
	
	@Override
	public String getName() {
		return getDescription();
	}
	
	@Override
	public String getLongDescription() {
		return getDescription();
	}
	
	@Override
	protected String[] getTownActions() {
		return NATIVE_ACTIONS;
	}

	private static ActionSelector sharedSelector = new NativeActionSelector();
	public ActionSelector getSelector() {
		return sharedSelector;
	}

	public int getPotentialPower() {
		if (turnsBeforeNextExpedition > 0){
			turnsBeforeNextExpedition --;
			return 0;
		}
		// This depends on how many warrior classes are into the town
		int commoners = getItemCount("NATIVE_COMMONER");
		int warriors = getTotalUnits() - commoners;
		return (int)(warriors / 50.0d);
	}
	

	public int getSightRange() {
		return 8;
	}

	public Expedition deployTroops(int expeditionPower) {
		ExpeditionGame game = ExpeditionGame.getCurrentGame();
		NonPrincipalExpedition ret = new NonPrincipalExpedition(game, "nativeExpedition"+game.getLastExpeditionId());
		ret.setGame(game);
		ret.setAppearanceId("HOSTILE_EXPEDITION");
		ret.setName(getCulture().getName()+" group");
		ret.setExpeditionary("-");
		
		SimpleAI ai = new SimpleAI(game.getPlayer(), new Bump()) ;
		ai.setBumpEnemy(true);
		ret.setSelector(ai);
		int targetPopulation = expeditionPower*30 + Util.rand(-expeditionPower*20, expeditionPower * 20);
		int specializedPopulation = 0;
		for(Pair<Double, String> classD: culture.getClassDistribution()){
			int wantedClassPopulation = (int) (classD.getA().doubleValue() * targetPopulation);
			if (wantedClassPopulation > 0){
				int availableClassPopulation = getItemCount(classD.getB()); 
				if (availableClassPopulation < wantedClassPopulation){
					wantedClassPopulation = availableClassPopulation;
				}
				if (wantedClassPopulation > 0){
					ExpeditionUnit unit = (ExpeditionUnit)ItemFactory.createItem(classD.getB());
					ret.addItem(unit, wantedClassPopulation);
					reduceQuantityOf(classD.getB(), wantedClassPopulation);
					if (unit.getWeaponTypes().length>0){
						ret.addItem(ItemFactory.createItem(unit.getWeaponTypes()[0]), wantedClassPopulation);
					}
				}
			}
			specializedPopulation += wantedClassPopulation;
			
		}
		int commoners = targetPopulation - specializedPopulation - 1;
		if (commoners > 0){
			ret.addItem(ItemFactory.createItem("NATIVE_COMMONER"), commoners);
			reduceQuantityOf("NATIVE_COMMONER", commoners);
		}
		 
		
		Action armExpedition = new ArmExpedition();
		armExpedition.setPerformer(ret);
		armExpedition.execute();
		
		ret.calculateInitialPower();
		
		turnsBeforeNextExpedition = expeditionPower * 3;
		return ret;
	}

	public Culture getCulture() {
		return culture;
	}
	
	@Override
	public void onSeenByPlayer() {
		if (isDisabled){
			getLevel().addActor(this);
			resurrect();
			isDisabled = false;
		}
	}

	public void setDisabled(boolean isDisabled) {
		this.isDisabled = isDisabled;
	}


	public boolean wantsToTradeWith(Expedition expedition) {
		return !isUnfriendly();
	}

	public boolean isUnfriendly() {
		return isUnfriendly;
	}

	public boolean canTradeGoodType(GoodType goodType) {
		int goodTypeCount = getGoodTypeCount(goodType);
		return goodTypeCount > 0;
	}

	public List<Equipment> calculateOffer(
			GoodType goodType,
			List<Equipment> offer) {
		int value = 0;
		for (Equipment eqOffer: offer){
			ExpeditionItem good = (ExpeditionItem) eqOffer.getItem();
			value += evalItem(good) * eqOffer.getQuantity();
			//System.out.println("Offer value is:" + value+" with "+good.getDescription());
		}
		List<Equipment> townOffer = new ArrayList<Equipment>();
		List<Equipment> townGoods = getGoods(goodType);
		for (Equipment townGood: townGoods){
			ExpeditionItem good = (ExpeditionItem) townGood.getItem();
			if (value > 0){
				double goodValue = evalItem(good);
				if (goodValue == 0)
					continue;
				int maxQuantity = (int)Math.floor((double)value/(double)goodValue);
				if (maxQuantity == 0)
					continue;
				int quantity = townGood.getQuantity();
				if (quantity > maxQuantity){
					quantity = maxQuantity;
				}
				townOffer.add(new Equipment(good, quantity));
				value -= goodValue * quantity;
			} else
				break;
		}
		return townOffer;
	}

	private int evalItem(ExpeditionItem good) {
		return (int)Math.round(good.getAmericaValue()*getGoodTypeModifier(good.getGoodType()));
	}

	private double getGoodTypeModifier(GoodType goodType) {
		return goodTypeModifiers.get(goodType);
	}

	public void setUnfriendly(boolean b) {
		isUnfriendly = b;
	}
	
	@Override
	protected void townAction(int switchChat, Expedition expedition) {
		NativeTown nativeTown = (NativeTown) this;
		switch (switchChat){
		case 0:
			nativeTown.setUnfriendly(true);
			String battleName = "You raid the "+nativeTown.getDescription();
    		BattleManager.battle(battleName, expedition, nativeTown);
			break;
		case 1:
			if (nativeTown.wantsToTradeWith(expedition)){
				int goodTypeChoice = UserInterface.getUI().switchChat("Trading with "+nativeTown.getDescription(),"What goods are you looking for?", GoodType.getChoicesList());
				GoodType goodType = GoodType.fromChoice(goodTypeChoice);
				if (goodType == null){
					//Cancelled
					break;
				}
				if (nativeTown.canTradeGoodType(goodType)){
					List<Equipment> offer = ((ExpeditionUserInterface)UserInterface.getUI()).selectItemsFromExpedition("What goods do you offer?", "offer");
					if (offer == null){
						//Cancelled
						break;
					}
					if (UserInterface.getUI().promptChat("Are you sure?")){
						List<Equipment> townOffer = nativeTown.calculateOffer(goodType, offer);
						if (townOffer == null || townOffer.size() == 0){
							showBlockingMessage("We can offer you nothing for that.");
						} else {
							if (((ExpeditionUserInterface)UserInterface.getUI()).promptUnitList(townOffer, "Native Offer","This is our offer, do you accept it? [Y/N]")){
								expedition.reduceAllItems(offer);
								expedition.addAllItems(townOffer);
								nativeTown.reduceAllItems(townOffer);
								nativeTown.addAllItems(offer);
								showBlockingMessage("Thank you, friend..");
							} else {
								showBlockingMessage("Some other time then..");
							}
						}
					}
				} else {
					showBlockingMessage("We have no "+goodType.getDescription()+" to trade.");
				}
			} else {
				showBlockingMessage("The "+nativeTown.getDescription()+" refuses to trade with you.");
			}
			break;
		case 2:
			break;
		}
	}
}
