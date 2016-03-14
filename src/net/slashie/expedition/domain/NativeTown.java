package net.slashie.expedition.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.slashie.expedition.ai.NativeActionSelector;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.item.ItemFactory;
import net.slashie.expedition.ui.ExpeditionUserInterface;
import net.slashie.expedition.world.Culture;
import net.slashie.serf.action.ActionSelector;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.text.EnglishGrammar;
import net.slashie.serf.ui.Appearance;
import net.slashie.serf.ui.AppearanceFactory;
import net.slashie.serf.ui.UserInterface;
import net.slashie.util.Pair;
import net.slashie.utils.Util;

@SuppressWarnings("serial")
public class NativeTown extends Town{
	private static final String[] NATIVE_ACTIONS = new String [] {
		"Raid the settlement",
		"Trade Goods",
		"Threaten",
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
	private int scaredLevel;
	
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
		for (Pair<Double, String> itemDistribution: culture.getItemsDistribution()){
			int items = (int)Math.round((double)targetPopulation*itemDistribution.getA());
			if (items == 0.0d)
				continue;
			ExpeditionItem i = ItemFactory.createItem(itemDistribution.getB());
			items += Util.rand(-items*0.1d, items*0.1d);
			addItem(i, items);
		}
		
		setScaredLevel(getCulture().getAggresiveness()*-2);
	}
	
	@Override
	public Appearance getAppearance() {
		if (isTown()){
			setAppearanceId("NATIVE_TOWN");
			return AppearanceFactory.getAppearanceFactory().getAppearance(getAppearanceId());
		} else if (isCity()){
			setAppearanceId("NATIVE_CITY");
			return AppearanceFactory.getAppearanceFactory().getAppearance(getAppearanceId());
		} else
			setAppearanceId("NATIVE_VILLAGE");
			return AppearanceFactory.getAppearanceFactory().getAppearance(getAppearanceId());
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
		return 12;
	}

	public Culture getCulture() {
		return culture;
	}
	
	public Expedition deployTroops(int expeditionPower){
		Expedition ret = ExpeditionFactory.deployTroops(this, expeditionPower);
		turnsBeforeNextExpedition = expeditionPower * 10;
		return ret;
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

	public List<Equipment> calculateOffer(GoodType goodType, double offerValue) {
		List<Equipment> townOffer = new ArrayList<Equipment>();
		List<Equipment> townGoods = getGoods(goodType);
		for (Equipment townGood: townGoods){
			ExpeditionItem good = (ExpeditionItem) townGood.getItem();
			if (offerValue > 0){
				double goodValue = evalItem(good);
				if (goodValue == 0)
					continue;
				int maxQuantity = (int)Math.floor(offerValue/goodValue);
				if (maxQuantity == 0)
					continue;
				int quantity = townGood.getQuantity();
				if (quantity > maxQuantity){
					quantity = maxQuantity;
				}
				townOffer.add(new Equipment(good, quantity));
				offerValue -= goodValue * quantity;
			} else
				break;
		}
		return townOffer;
	}
	public List<Equipment> calculateOffer(GoodType goodType, List<Equipment> offer) {
		double value = 0;
		for (Equipment eqOffer: offer){
			ExpeditionItem good = (ExpeditionItem) eqOffer.getItem();
			value += evalItem(good) * eqOffer.getQuantity();
		}
		return calculateOffer(goodType, value);
	}

	private double evalItem(ExpeditionItem good) {
		return good.getBaseTradingValue()*getGoodTypeModifier(good.getGoodType());
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
			if (nativeTown.isUnfriendly() || UserInterface.getUI().promptChat("Raid "+nativeTown.getDescription()+"? Are you Sure?")){
				nativeTown.setUnfriendly(true);
				String battleName = "You raid the "+nativeTown.getDescription();
	    		BattleManager.battle(battleName, expedition, nativeTown);
			}
			break;
		case 1:
			if (nativeTown.wantsToTradeWith(expedition)){
				List<GoodType> mostValued = getCulture().getMostValuedGoodTypes();
				String interestedString = "";
				if (mostValued.size() == 0){
					// No particular interest
					interestedString = "The "+nativeTown.getCulture().getName()+" are open to trading any goods";
				} else {
					interestedString = "The "+nativeTown.getCulture().getName()+" are interested in ";
					List<String> strings = new ArrayList<String>();
					for (GoodType goodType: mostValued){
						strings.add(goodType.getDescription());
					}
					interestedString += EnglishGrammar.stringList(strings);
				}
				
				int goodTypeChoice = UserInterface.getUI().switchChat(interestedString, "What goods are you looking for?", AVAILABLE_GOOD_TYPES_TO_TRADE_NAMES);
				GoodType goodType = AVAILABLE_GOOD_TYPES_TO_TRADE[goodTypeChoice];
				if (goodType == null){
					//Cancelled
					break;
				}
				if (nativeTown.canTradeGoodType(goodType)){
					List<Equipment> offer = ((ExpeditionUserInterface)UserInterface.getUI()).selectItemsFromExpedition("What goods do you offer?", "offer", getDialogAppearance());
					if (offer == null){
						//Cancelled
						break;
					}
					// Check if offer contains units.
					boolean offerContainsUnits = false;
					for (Equipment equipment: offer){
						if (((ExpeditionItem)equipment.getItem()).getGoodType() == GoodType.PEOPLE){
							if (equipment.getQuantity() == 1){
								showBlockingMessage(EnglishGrammar.a(equipment.getItem().getDescription())+" "+ equipment.getItem().getDescription()+" refuses to take part on the deal..");
							} else {
								showBlockingMessage("The "+((ExpeditionUnit)equipment.getItem()).getPluralDescription()+" refuse to take part on the deal.");
							}
							offerContainsUnits = true;
							break;
						}
					}
					if (offerContainsUnits)
						break;
					
					
					if ( ((ExpeditionUserInterface)UserInterface.getUI()).promptUnitList(offer, "Offer", "Will you make this offer?")){
						List<Equipment> townOffer = nativeTown.calculateOffer(goodType, offer);
						if (townOffer == null || townOffer.size() == 0){
							showBlockingMessage("We can offer you nothing for that.");
						} else {
							if (((ExpeditionUserInterface)UserInterface.getUI()).promptUnitList(townOffer, "Native Offer","This is our offer, do you accept it?")){
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
			// If the town is scared, they will give you items
			if (getScaredLevel() <= 0 || Util.chance(getCulture().getAggresiveness()*25)){
				showBlockingMessage("The "+nativeTown.getCulture().getName()+" do not fear you, begone!");
				setUnfriendly(true);
				setScaredLevel(0);
			} else {
				int goodTypeChoice = UserInterface.getUI().switchChat("Threatening "+nativeTown.getDescription(),"Please spare us! what do you want?", AVAILABLE_GOOD_TYPES_TO_TRADE_NAMES);
				GoodType goodType = AVAILABLE_GOOD_TYPES_TO_TRADE[goodTypeChoice];
				if (goodType == null){
					//Cancelled
					break;
				}
				if (nativeTown.canTradeGoodType(goodType)){
					List<Equipment> townOffer = nativeTown.calculateOffer(goodType, getScaredLevel()*200);
					if (townOffer == null || townOffer.size() == 0){
						showBlockingMessage("We are unwilling to give you "+goodType.getDescription());
					} else {
						if (((ExpeditionUserInterface)UserInterface.getUI()).promptUnitList(townOffer, "Native Offer","This is our offer, do you accept it? [Y/N]")){
							expedition.addAllItems(townOffer);
							nativeTown.reduceAllItems(townOffer);
							reduceScaredLevel();
							showBlockingMessage("Begone now.");
						} else {
							showBlockingMessage("It is all we have!");
						}
					}
				} else {
					showBlockingMessage("We don't have any "+goodType.getDescription()+"!");
				}
				
			}
		case 3:
			break;
		}
	}

	private final static GoodType[] AVAILABLE_GOOD_TYPES_TO_TRADE = new GoodType[]{GoodType.TRADE_GOODS, GoodType.SUPPLIES, GoodType.LIVESTOCK, GoodType.ARMORY};
	private final static String[] AVAILABLE_GOOD_TYPES_TO_TRADE_NAMES = new String[]{"Trade Goods", "Supplies", "Livestock", "Weapons"};

	public int getScaredLevel() {
		return scaredLevel;
	}

	public void setScaredLevel(int scaredLevel) {
		this.scaredLevel = scaredLevel;
	}

	public void reduceScaredLevel() {
		scaredLevel --;
		if (scaredLevel <= -10){
			scaredLevel = -10;
		}
		checkFriendlyChange();
	}
	
	private void checkFriendlyChange() {
		if (scaredLevel > 0)
			setUnfriendly(false);
		
	}

	public void increaseScaredLevel(){
		scaredLevel++;
		if (scaredLevel > 5){
			scaredLevel = 5;	
		}
		checkFriendlyChange();
	}
	
	@Override
	public boolean requiresUnitsToContainItems() {
		return false;
	}
	
	@Override
	public void consumeFood() {
		// Do nothing
	}
}
