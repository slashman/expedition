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

public class NPC extends AwareActor{
	private ExpeditionUnit unit;
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
	
	public NPC(ExpeditionUnit unit) {
		super();
		this.unit = unit;
		if (unit.getBasicId().equals("GUARD") || unit.getBasicId().equals("DOMINIK")){
			selector = new NullSelector();
		} else {
			selector = new SimpleAI(null, new NPCWalk());
		}
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
						m("Remember you need at least 200 wood to found an oustanding settlement");
					} else {
						m("The crown is looking forward to gain a competitive edge against the portuguese navigators, which have discovered the african path to the Indias.");
						m("You should now sail eastward, if your calculations are right, you will find the Indias about 1200 nautical miles into the Atlantic Ocean");
						m("You may want to take some trading merchandise with you, but remember this is just an exploratory voyage, it is of more importance to lay the foundations for a trading outpost.");
						m("Remember you need at least 200 wood to found an oustanding settlement. You can obtain wood by 'c'hopping or buying from the Supplies store.");
					}
					break;
				case 1:
					if (e.getFlag("DISCOVERED_NEW_WORLD")){
						m("By your reckoning, the trip will take about two months.");
					} else {
						m("By your reckoning, the trip may take about one month into the unknown. You should however be equipped for the worst, you don't know what awaits you in the high sea!");
						m("Remember to check out your \"Food Days\" calculations before departing. The crown has given you enough supplies for a long trip with your sailing crew...");
						m("But if you decide to bring more people, you will have to get your own supplies");
					}
					break;
				case 2:
					m("My friend, how can I give advise on sailing the seas to a great navigator such as you! You have been on board since you were a child!");
					m("As you used to say yourself, it's all matter of pointing your ships to your location and letting the wind run behind you, but sometimes wind just won't travel with you...");
					m("And that's where the ships' crew has work to do! a well trained crew can sail even against the wind, but it will take them much longer, and time is precious on the sea.");
					break;
				case 3:
					if (e.getFlag("DISCOVERED_NEW_WORLD")){
						m("You may find some hostile native tribes in the new found lands... ");
					} else {
						m("You don't know what awaits you in your trip, but it's better to be prepared for the worst..");
					}
					m("Try to have at least some trained soldiers in your ships and buy them weapons and armor. Remember to 'a'rm your expedition when its time to combat!");
					m("The better equiped men will be chosen as the first row and no more than sixty units can combat at once.");
					m("Remember an assault is divided into Ranged, Mounted and Melee phase, and that you may suffer retailation on any phase..");
					m("Ranged units attack in the ranged phase, both parts attack at the same time and thus wounded units are disabled only on next phase.");
					m("Mounted units attack in the mounted phase, thus they get to attack twice on each combat round.");
					m("Only a maximum of 20 ranged and 20 cavalry units can participate on each combat round. Your units may be wounded or killed on each phase");
					m("Be careful when chosing when to fight and when to retreat!");
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
			
		}

	}

	private void m(String string) {
		((ExpeditionUserInterface)UserInterface.getUI()).showBlockingMessage(string);		
	}
	
}
