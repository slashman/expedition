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
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ExpeditionUnit unit;
	private String[] talkLines;
	private boolean unique;
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
	
	public NPC(ExpeditionUnit unit, boolean nullSelector, boolean unique, String... talkLines) {
		super();
		this.unit = unit;
		this.unique = unique;
		if (nullSelector){
			selector = new NullSelector();
		} else {
			selector = new SimpleAI(null, new NPCWalk());
		}
		this.talkLines = talkLines;
	}

	@Override
	public int getSightRangeInDots() {
		return 5;
	}
	
	@Override
	public int getSightRangeInCells() {
		return getSightRangeInDots();
	}
	
	@Override
	public void onPlayerBump() {
		if (unit.getBaseID().equals("DOMINIK")){
			Expedition e = ExpeditionGame.getCurrentGame().getExpedition();
			out: while (true){
				int choice = (UserInterface.getUI()).switchChat(unit.getDescription(),"Hello, friend Cristobal... how may I assist you?", 
						"What should I do next?", //how much wood and colonists you'll need when you get there
						"Do you have any advice for the voyage?", //how much water and food you'll need to cross the ocean, aid on sailing
						"What about the crew?", //aid on units 
						"What if I meet resistance?", //Tell them about combat and suggest how many soldiers they need.
						"Thank you, friend!" 
						);
				switch (choice){
				case 0:
					if (e.getFlag("DISCOVERED_NEW_WORLD")){
						m("You should establish a strong presence for Spain in the lands you have discovered!");
					} else {
						m("The crown is looking forward to gain a competitive edge against the Portuguese navigators, whom have discovered the African path to the Indias.");
						m("You should now sail westward... if your calculations are right, you will find the Indias about 1200 nautical miles into the Atlantic Ocean");
						m("You may want to take some trading merchandise with you, but remember this is just an exploratory voyage, it is of more importance to lay the foundations for a trading outpost.");
					}
					m("Remember you need at least 200 wood to found an outstanding settlement. You can obtain wood by chopping or buying from the Supplies store. ");
					m("Once you build a settlement, you can leave people inhabiting it and they will look out for their own survival, so you can use settlements as exploration bases.");
					ml("A settlement may host 40 people when created, but you can continue expanding it with more buildings if you want to push its growth forward.");
					break;
				case 1:
					m("My friend... how can I give advice on sailing the seas to a great navigator such as you! You have been on board since you were a child!");
					m("As you say yourself, it is all matter of pointing your ships to your destination and letting wind run behind you, but sometimes wind just will not travel with you...");
					m("And that's where the ships' crew has work to do! a well trained crew can sail even against the wind, but it will take them much longer, and time is precious on the sea.");
					m("Your ships will sail much faster if you have a full crew for each one (1 officer and 25 sailors), and if they are in good shape (integrity higher than 3/4).");
					m("Our navigators have discovered strong southwestern winds on the recently conquered Canary Islands, southwest of Palos. That may be a good spot for your voyage to start.");
					if (e.getFlag("DISCOVERED_NEW_WORLD")){
						m("By your reckoning, the voyage will take about three months. Remember to check out your \"Food Days\" calculations before departing");
					} else {
						m("By your reckoning, the voyage may take about three months into the unknown. You should however be equipped for the worst, you don't know what awaits you in the high sea!");
						m("Remember to check out your \"Food Days\" calculations before departing. The crown has given you enough supplies for a long voyage with your sailing crew...");
						m("But if you decide to bring more people, you will have to get your own supplies");
					}
					m("You can forage for fruits on the wilderness or fish on shallow waters, but it's better to keep your expedition stocked");
					m("Also remember, the morale of your expedition greatly influences their performance on all activities, it varies depending on the hapennings on the voyage.");
					ml("You can tell it in the faces of your men, they change from Victorious to Calm to Depressed. Food supply, battle performance and voyage difficulties are the main factors to consider to keep your crew happy.");
					break;
				case 2:
					m("Your expedition is made up of men of different skills, it is important to know the role of each one in order to have a successful journey.");
					m("First off, the sailing crew. It is conformed of Sailors and Officers.");
					m("Sailors are weak in combat but you need 25 of them for each ship to achieve decent speed on adverse winds. Sailors can only use basic weapons (Lances and Maces, for instance)");
					m("Officers can use stronger weapons and armor (Harquebus and Swords, and Breastplates or Studded Leather). You need one per ship to complete the crew.");
					m("Rogues can make part of your expedition, they are survival wolves and will gladly take part of your adventure. They are stronger than sailors and can use swords.");
					m("Marines and Soldiers are specialized and strong warriors which are trained to fight under harsh conditions. They are the strongest men you can have on board.");
					m("Marines can handle and prefer ranged weapons like crossbows and harquebus, but they can't wear breastplate. They can also wear swords in addition to basic weapons.");
					m("Soldiers, on the other hand, prefer to use the sword and can wear breastplate, making them valuable in the combat field. They can also use harquebus");
					m("Archers are light warriors trained on the use of the bow, which may be valuable in the wilderness. They can wear basic armor and crossbows too.");
					m("Explorers are weak but indispensable in your journey; they extend the field of view of your expedition, and that may save your life. They can only wear basic weapons");
					m("Carpenters specialize on fixing your ships, they make much better use of wood, and having a healthy ship greatly increases your chances of success.");
					m("They also construct buildings twice as fast, in case you are settling in a colony.");
					m("Doctors can only wear basic gear, but they help your units to heal quickly.");
					m("A wounded unit can't perform its functions and is subject to die in battle, so they can be a great help in your expedition.");
					ml("Finally, you can also bring colonists with you, they are people willing to strengthen the presence of the crown in foreign lands.");
					break;
				case 3:
					if (e.getFlag("DISCOVERED_NEW_WORLD")){
						m("You may find some hostile native tribes in the new found lands... ");
					} else {
						m("You don't know what awaits you in your voyage, but it's better to be prepared for the worst..");
					}
					m("Try to have at least some trained soldiers in your ships and buy them weapons and armor. and remember to 'a'rm your expedition when its time to combat.");
					m("Combat is divided on assaults: the sixty best equipped men from each expedition will be chosen for the assault, with a maximum of 20 ranged and 20 mounted.");
					m("An assault is divided into three phases: \"Ranged\", \"Mounted\" and \"Melee\", and you may suffer retaliation from the defending party on any phase.");
					m("Each phase may bring wounded or dead units. Wounded units cannot participate on the assault and are taken to the back row.");
					m("Units with ranged equipment and mounted units can participate more than once on battle, during the Ranged and Mounted phases.");
					ml("Choosing when to fight and when to retreat may save your life!");
					break;
				case 4:
					ml("May God be with you!");
					break out;
				}
			}
		} else if (unit.getBaseID().equals("GUARD")){
			switch (Util.rand(0, 2)){
			case 0:
				ml(says()+"I guard the castle and all within.");
				break;
			case 1:
				ml(says()+"I couldn't be better!");
				break;
			case 2:
				ml(says()+"The alcazar is fair and strong.");
				if ((UserInterface.getUI()).promptChat(says()+"Do you seek the King and Queen?")){
					ml(says()+"They are in the throne room.");
				} else {
					ml(says()+"Then what's your business here!");
				}
			}
		} else {
			if (talkLines.length > 0){
				ml(says()+Util.randomElementOf(talkLines));
			}
		}

	}
	
	private String says() {
		if (isUnique())
			return getDescription()+" says: XXX ";
		else
			return "The "+getDescription()+" says: XXX ";
	}

	private boolean isUnique() {
		/*
		return unit.getBaseID().equals("SANTIAGO") ||
		unit.getBaseID().equals("CRISTOFORO") ||
		unit.getBaseID().equals("BIZCOCHO")
		;*/
		return unique;
	}

	public String getUnitId(){
		return unit.getBaseID();
	}

	private void m(String string) {
		((ExpeditionUserInterface)UserInterface.getUI()).showBlockingMessage(string, true);		
	}
	
	private void ml(String string) {
		((ExpeditionUserInterface)UserInterface.getUI()).showBlockingMessage(string, false);		
	}
	
}
