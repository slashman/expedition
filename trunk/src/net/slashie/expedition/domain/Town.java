package net.slashie.expedition.domain;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import net.slashie.expedition.domain.Expedition.MovementMode;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.item.ItemFactory;
import net.slashie.expedition.ui.ExpeditionUserInterface;
import net.slashie.expedition.ui.console.ExpeditionConsoleUI;
import net.slashie.serf.action.Actor;
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
		String description =  getLongDescription()+" XXX ";
		if (foundedIn != null){
			description += "Founded on "+ DateFormat.getDateInstance(DateFormat.MEDIUM).format(foundedIn)+" by "+founderExpedition.getExpeditionaryTitle()+" XXX ";
		}
		description += "Current Population: "+getPopulation();
		townAction(UserInterface.getUI().switchChat(description, getTownActions()));
	}
	
	protected void townAction(int switchChat) {
		if (this instanceof NativeTown){
			if (switchChat == 1){
				((NativeTown)this).setHostile(true);
			}
		}
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
