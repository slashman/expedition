package net.slashie.expedition.domain;

import net.slashie.expedition.action.NPCWalk;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.ui.ExpeditionUserInterface;
import net.slashie.serf.action.AwareActor;
import net.slashie.serf.action.NullSelector;
import net.slashie.serf.ai.SimpleAI;
import net.slashie.serf.ui.Appearance;
import net.slashie.serf.ui.UserInterface;
import net.slashie.utils.Util;

public class NPC extends AwareActor implements Cloneable{
	private ExpeditionUnit unit;
	private String[] talkLines;
	@Override
	public String getClassifierID() {
		return unit.getFullID()+super.toString();
	}

	@Override
	public String getDescription() {
		return unit.getDescription();
	}
	
	@Override
	public Appearance getAppearance() {
		return unit.getAppearance();
	}
	
	public NPC(ExpeditionUnit unit, boolean nullSelector, String... talkLines) {
		super();
		this.unit = unit;
		if (unit.getBasicId().equals("GUARD") || unit.getBasicId().equals("DOMINIK")){
			selector = new NullSelector();
		} else {
			selector = new SimpleAI(null, new NPCWalk());
		}
		this.talkLines = talkLines;
	}

	@Override
	public int getSightRange() {
		return 5;
	}
	
	@Override
	public void onPlayerBump() {
		if (unit.getBasicId().equals("DOMINIK")){
			Expedition e = ExpeditionGame.getCurrentGame().getExpedition();
			out: while (true){
				int choice = (UserInterface.getUI()).switchChat(unit.getDescription(),"Hello, friend Cristobal... how may I assist you?", 
						"What should I do next?", //how much wood and colonists you'll need when you get there
						"How long will be the trip?", //how much water and food you'll need to cross the ocean
						"How will I get there?", //aid on sailing 
						"What if I meet resistance?", //Tell them about combat and suggest how many soldiers they need.
						"Thank you, friend!" 
						);
				switch (choice){
				case 0:
					if (e.getFlag("DISCOVERED_NEW_WORLD")){
						m("You should establish a strong presence for Spain in the lands you have discovered!");
					} else {
						m("The crown is looking forward to gain a competitive edge against the portuguese navigators, which have discovered the african path to the Indias.");
						m("You should now sail eastward... if your calculations are right, you will find the Indias about 1200 nautical miles into the Atlantic Ocean");
						m("You may want to take some trading merchandise with you, but remember this is just an exploratory voyage, it is of more importance to lay the foundations for a trading outpost.");
					}
					m("Remember you need at least 200 wood to found an oustanding settlement. You can obtain wood by chopping or buying from the Supplies store. ");
					m("Once you build a settlement, you can leave people inhabiting it and they will look out for their own survival, so you can use settlements as exploration bases.");
					m("A settlement may host 40 people when created, but you can continue expanding it with more buildings if you want to push its growth forward.");
					break;
				case 1:
					if (e.getFlag("DISCOVERED_NEW_WORLD")){
						m("By your reckoning, the trip will take about two months.");
					} else {
						m("By your reckoning, the trip may take about one month into the unknown. You should however be equipped for the worst, you don't know what awaits you in the high sea!");
						m("Remember to check out your \"Food Days\" calculations before departing. The crown has given you enough supplies for a long trip with your sailing crew...");
						m("But if you decide to bring more people, you will have to get your own supplies");
						m("Your most important supply will be food: you can forage for fruits on the wilderness or fish on shallow waters, but it's better to keep your expedition stocked");
					}
					break;
				case 2:
					m("My friend... how can I give advise on sailing the seas to a great navigator such as you! You have been on board since you were a child!");
					m("As you used to say yourself, it's all matter of pointing your ships to your location and letting the wind run behind you, but sometimes wind just won't travel with you...");
					m("And that's where the ships' crew has work to do! a well trained crew can sail even against the wind, but it will take them much longer, and time is precious on the sea.");
					m("Your ships will sail much faster if you have a full crew for each one (1 captain and 25 sailors), and if they are in good shape (integrity higher than 3/4).");
					m("Our navigators have discovered strong soutwestern winds on the recently conquered Canary Islands, southwest of Palos. That may be a good spot for your trip to start.");
					break;
				case 3:
					if (e.getFlag("DISCOVERED_NEW_WORLD")){
						m("You may find some hostile native tribes in the new found lands... ");
					} else {
						m("You don't know what awaits you in your trip, but it's better to be prepared for the worst..");
					}
					m("Try to have at least some trained soldiers in your ships and buy them weapons and armor. and remember to 'a'rm your expedition when its time to combat.");
					m("Combat is divided on assaults: the sixty best equiped men from each expedition will be chosen for the assault, with a maximum of 20 ranged and 20 mounted.");
					m("An assault is divided into three phases: \"Ranged\", \"Mounted\" and \"Melee\", and you may suffer retailation from the defending party on any phase.");
					m("Each phase may bring wounded or dead units. Wounded units can not participate on the assault and are taken to the back row.");
					m("Units with ranged equipment and mounted units can participate more than once on battle, during the Ranged and Mounted phases.");
					m("Chosing when to fight and when to retreat may save your life!");
					break;
				case 4:
					m("May God be with you!");
					break out;
				}
			}
		} else if (unit.getBasicId().equals("GUARD")){
			switch (Util.rand(0, 2)){
			case 0:
				m("I guard the castle and all within.");
				break;
			case 1:
				m("I couldn't be better!");
				break;
			case 2:
				m("The alcazar is fair and strong.");
				if ((UserInterface.getUI()).promptChat("Do you seek the King and Queen?")){
					m("They are in the throne room.");
				} else {
					m("Then what's your business here!");
				}
			}
		} else {
			if (talkLines.length > 0){
				m(Util.randomElementOf(talkLines));
			}
		}

	}
	
	public String getUnitId(){
		return unit.getBasicId();
	}

	private void m(String string) {
		((ExpeditionUserInterface)UserInterface.getUI()).showBlockingMessage(string);		
	}
	
}
