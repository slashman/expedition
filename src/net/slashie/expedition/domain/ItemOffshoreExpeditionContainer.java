package net.slashie.expedition.domain;

import java.util.List;

import net.slashie.serf.baseDomain.AbstractItem;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.ui.Appearance;

public class ItemOffshoreExpeditionContainer implements ItemContainer{
	private Expedition expedition;
	private Store store;
	
	public ItemOffshoreExpeditionContainer(Expedition expedition, Store store){
		this.expedition = expedition;
		this.store = store;
	}
	
	public Expedition getExpedition() {
		return expedition;
	}
	
	@Override
	public void addItem(ExpeditionItem item, int quantity) {
		getExpedition().addItemOffshore(item, quantity);
	}

	@Override
	public boolean canCarry(ExpeditionItem item, int quantity) {
		return getExpedition().canCarryOffshore(item, quantity);
	}

	@Override
	public Appearance getAppearance() {
		return getExpedition().getAppearance();
	}
	
	@Override
	public Appearance getDialogAppearance() {
		return getExpedition().getDialogAppearance();
	}

	@Override
	public int getCarryCapacity() {
		return getExpedition().getOffshoreCarryCapacity();

	}

	@Override
	public int getCarryable(ExpeditionItem item) {
		int maxCarriable = getExpedition().getOffshoreCarryable(item);
		StoreItemInfo buyInfo = store.getBuyInfo(item, getExpedition());
		int maxBuy = 0;
		if (buyInfo != null){
			maxBuy = (int)Math.floor((double)getExpedition().getAccountedGold() / (double)buyInfo.getPrice());
		}
		if (maxCarriable < maxBuy)
			maxBuy = maxCarriable;
		return maxBuy;
	}

	@Override
	public int getCurrentFood() {
		return getExpedition().getCurrentFood();
	}

	@Override
	public int getCurrentlyCarrying() {
		return getExpedition().getOffshoreCurrentlyCarrying();
	}

	@Override
	public String getDescription() {
		return getExpedition().getDescription();
	}

	@Override
	public int getFoodDays() {
		return getExpedition().getOffshoreFoodDays();
	}

	@Override
	public List<Equipment> getGoods(GoodType goodType) {
		return getExpedition().getGoods(goodType);
	}

	@Override
	public int getItemCount(String fullID) {
		return getExpedition().getItemCount(fullID);
	}
	
	public int getVehicleCount(String fullID) {
		return getExpedition().getVehicleCount(fullID);
	}

	@Override
	public int getItemCountBasic(String basicID) {
		return getExpedition().getItemCountBasic(basicID);
	}

	@Override
	public List<Equipment> getItems() {
		return getExpedition().getItems();
	}

	@Override
	public int getTotalShips() {
		return getExpedition().getTotalShips();
	}

	@Override
	public int getTotalUnits() {
		return getExpedition().getTotalUnits();
	}

	@Override
	public String getTypeDescription() {
		return getExpedition().getTypeDescription();
	}

	@Override
	public int getWaterDays() 
	{		
		return getExpedition().getOffShoreWaterDays();
	}

	@Override
	public boolean isPeopleContainer() {
		return getExpedition().isPeopleContainer();
	}

	@Override
	public void reduceQuantityOf(AbstractItem item, int quantity) {
		ExpeditionItem eitem = (ExpeditionItem) item;
		if (eitem.getGoodType() == GoodType.VEHICLE){
			getExpedition().removeVehicle(eitem);
		} else {
			getExpedition().reduceItemOffshore(eitem, quantity);
		}
	}

	@Override
	public boolean requiresUnitsToContainItems() {
		return getExpedition().requiresUnitsToContainItems();
	}
}