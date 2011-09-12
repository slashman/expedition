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
			/*GoodsCache cache = new GoodsCache(ExpeditionGame.getCurrentGame());
			cache.setPosition(new Position(getPosition()));
			List<Pair<String, Integer>> prizeList = getPrizesFor(initialPower);
			for (Pair<String,Integer> prize: prizeList){
				cache.addItem(ItemFactory.createItem(prize.getA()), prize.getB());
			}
			//((ExpeditionUserInterface)UserInterface.getUI()).transferFromExpedition(cache);
			AbstractFeature previousFeature = getLevel().getFeatureAt(getPosition());
			if (previousFeature != null && 
					previousFeature instanceof GoodsCache &&
					((GoodsCache)previousFeature).isInfiniteCapacity()){
				((GoodsCache)previousFeature).addAllGoods(cache);
			} else {
				getLevel().addFeature(cache);
			}*/
			((ExpeditionMacroLevel)getLevel()).addAllEquipment(this, getPosition());
			die();
		}
	}

	/*
	private final static Pair[] prizes = new Pair[]{
		new Pair<String, Integer>("GOLD_NUGGET",1),
		new Pair<String, Integer>("GOLD_BRACELET",2),
		new Pair<String, Integer>("NATIVE_ARTIFACT",3),
		new Pair<String, Integer>("NATIVE_FOOD",6)
	};
	
	private List<Pair<String, Integer>> getPrizesFor(int initialPower) {
		int waves = (int)Math.round((double)initialPower / 100.0d);
		List<Pair<String,Integer>> ret = new ArrayList<Pair<String,Integer>>();
		for (int i = 0; i < waves; i++){
			Pair<String, Integer> prize = prizes[Util.rand(0, 3)];
			ret.add(new Pair<String, Integer>(prize.getA(), prize.getB()*Util.rand(5,8)));
		}
		return ret;
	}
	*/
	
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
		MovementSpeed.NONE.getDescription();
	}
	
	@Override
	public void beforeActing() {
		super.beforeActing();
		checkDeath();
	}
}
