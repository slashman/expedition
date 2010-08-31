package net.slashie.expedition.domain;

import net.slashie.expedition.action.Bump;
import net.slashie.expedition.ai.NativeActionSelector;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.item.ItemFactory;
import net.slashie.expedition.world.Culture;
import net.slashie.serf.action.ActionSelector;
import net.slashie.serf.ai.SimpleAI;
import net.slashie.serf.ui.Appearance;
import net.slashie.serf.ui.AppearanceFactory;
import net.slashie.util.Pair;
import net.slashie.utils.Util;

public class NativeTown extends Town{
	private static final String[] NATIVE_ACTIONS = new String [] {
		"Leave",
		//Peaceful
		"Transact goods", 
		"Amaze the natives", 
		"Raid the settlement",
		"Demand tribute",
		"Beg for help",
		"Learn about enemies",
		//War
		"Offer Peace",
		"Capture natives",
		"Recruit"
	};
	private Culture culture;
	private boolean isHostile;
	private boolean isDisabled;
	private int turnsBeforeNextExpedition;
	
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
		addItem(ItemFactory.createItem("NATIVE_LEADER"), 1);
		
		addItem(ItemFactory.createItem("GOLD_NUGGET"), Util.rand(0, size*culture.getGoldModifier()*20));
		addItem(ItemFactory.createItem("GOLD_BRACELET"), Util.rand(0, size*culture.getGoldModifier()*40));
		addItem(ItemFactory.createItem("NATIVE_ARTIFACT"), Util.rand(0, size*culture.getArtifactModifier()*30));
		addItem(ItemFactory.createItem("NATIVE_FOOD"), Util.rand(100, size*culture.getAgricultureModifier()*100));
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

	public boolean isHostile() {
		return isHostile;
	}

	public void setHostile(boolean isHostile) {
		this.isHostile = isHostile;
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
		ret.setName("natives");
		ret.setExpeditionary("-");
		
		SimpleAI ai = new SimpleAI(game.getPlayer(), new Bump()) ;
		ai.setBumpEnemy(true);
		ret.setSelector(ai);
		int targetPopulation = expeditionPower*50 + Util.rand(-expeditionPower*20, expeditionPower * 20);
		int specializedPopulation = 0;
		for(Pair<Double, String> classD: culture.getClassDistribution()){
			int wantedClassPopulation = (int) (classD.getA().doubleValue() * targetPopulation);
			if (wantedClassPopulation > 0){
				int availableClassPopulation = getItemCount(classD.getB()); 
				if (availableClassPopulation < wantedClassPopulation){
					wantedClassPopulation = availableClassPopulation;
				}
				if (wantedClassPopulation > 0){
					ret.addItem(ItemFactory.createItem(classD.getB()), wantedClassPopulation);
					reduceQuantityOf(classD.getB(), wantedClassPopulation);
				}
			}
			specializedPopulation += wantedClassPopulation;
		}
		int commoners = targetPopulation - specializedPopulation - 1;
		if (commoners > 0){
			ret.addItem(ItemFactory.createItem("NATIVE_COMMONER"), commoners);
			reduceQuantityOf("NATIVE_COMMONER", commoners);
		}
		 
		ret.addItem(ItemFactory.createItem("ARROWS"), Util.rand(0, expeditionPower*30));
		ret.addItem(ItemFactory.createItem("NATIVE_ARTIFACT"), Util.rand(0, expeditionPower*10));
		ret.addItem(ItemFactory.createItem("NATIVE_FOOD"), Util.rand(expeditionPower*100, expeditionPower*500));
		
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
			isDisabled = false;
		}
	}

	public void setDisabled(boolean isDisabled) {
		this.isDisabled = isDisabled;
	}


	
}
