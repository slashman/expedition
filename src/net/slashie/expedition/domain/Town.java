package net.slashie.expedition.domain;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.slashie.expedition.domain.Expedition.MovementMode;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.item.ItemFactory;
import net.slashie.expedition.ui.ExpeditionUserInterface;
import net.slashie.expedition.ui.console.ExpeditionConsoleUI;
import net.slashie.serf.action.Actor;
import net.slashie.serf.game.Equipment;
import net.slashie.serf.ui.AppearanceFactory;
import net.slashie.serf.ui.UserInterface;
import net.slashie.utils.Util;

public class Town extends GoodsCache{
	private static final String[] TOWN_ACTIONS = new String[] { "Leave" };
	private String name;
	protected Expedition founderExpedition;
	protected Date foundedIn;
	
	public Town(ExpeditionGame game) {
		super(game);
		setAppearanceId("TOWN");
		founderExpedition = game.getExpedition();
		foundedIn = game.getGameTime().getTime();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String getDescription() {
		return getName();
	}
	
	@Override
	public String getClassifierID() {
		return "Town"+name;
	}
	
	public String getLongDescription(){
		return "The "+getTitle()+" of "+getName(); 
	}
	
	@Override
	public void onStep(Actor a) {
		if (a != ExpeditionGame.getCurrentGame().getExpedition()){
			return;
		}
		String description =  getLongDescription()+" XXX ";
		if (foundedIn != null){
			description += "Founded on "+ DateFormat.getDateInstance(DateFormat.MEDIUM).format(foundedIn)+" by "+founderExpedition.getExpeditionaryTitle()+" XXX ";
		}
		description += "Current Population: "+getPopulation();
		townAction(UserInterface.getUI().switchChat(description, getTownActions()), (Expedition)a);
	}
	
	protected void townAction(int switchChat, Expedition expedition) {
		if (this instanceof NativeTown){
			NativeTown nativeTown = (NativeTown) this;
			switch (switchChat){
			case 0:
				nativeTown.setHostile(true);
				break;
			case 1:
				if (nativeTown.wantsToTradeWith(expedition)){
					int goodTypeChoice = UserInterface.getUI().switchChat("What goods are you looking for?", GoodType.getChoicesList());
					GoodType goodType = GoodType.fromChoice(goodTypeChoice);
					if (nativeTown.canTradeGoodType(goodType)){
						List<Equipment> offer = ((ExpeditionUserInterface)UserInterface.getUI()).selectItemsFromExpedition("What goods do you offer?", "offer");
						if (UserInterface.getUI().promptChat("Are you sure?")){
							List<Equipment> townOffer = nativeTown.calculateOffer(goodType, offer);
							if (townOffer.size() == 0){
								showBlockingMessage("We can offer you nothing for that.");
							} else {
								if (((ExpeditionUserInterface)UserInterface.getUI()).promptUnitList(townOffer, "Native Offer","This is our offer, do you accept it? [Y/N]")){
									expedition.reduceAllItems(offer);
									expedition.addAllItems(townOffer);
									nativeTown.reduceAllItems(townOffer);
									nativeTown.addAllItems(offer);
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

	private void showBlockingMessage(String message) {
		((ExpeditionUserInterface)UserInterface.getUI()).showBlockingMessage(message);
	}

	protected String[] getTownActions() {
		return TOWN_ACTIONS;
	}

	@Override
	public void consumeFood() {
		//Do nothing, this must be handled differently
	}
	
	@Override
	public boolean isInfiniteCapacity() {
		return false;
	}

	
	public int getSize(){
		return (getPopulation() / 1000)+1;
	}

	private int getPopulation() {
		return getTotalUnits();
	}

	public boolean isTown() {
		return getSize() > 5 && getSize() < 20;
	}

	public boolean isCity() {
		return getSize() > 20;
	}
	
	public void tryGrowing(){
		//This is called each 30 days
		if (Util.chance(95)){
			int growth = (int)Math.round(getPopulation() * ((double)Util.rand(1, 5)/100.0d));
			addItem(ItemFactory.createItem("COLONIST"), growth);
		}
	}
	
	public String getTitle(){
		if (isCity())
			return "city";
		if (isTown())
			return "town";
		return "village";
	}
	
	

}
