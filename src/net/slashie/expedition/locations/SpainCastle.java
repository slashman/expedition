package net.slashie.expedition.locations;

import java.io.Serializable;
import java.util.List;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.Vehicle;
import net.slashie.expedition.domain.Expedition.Title;
import net.slashie.expedition.item.ItemFactory;
import net.slashie.expedition.ui.ExpeditionUserInterface;
import net.slashie.serf.action.Actor;
import net.slashie.serf.game.Player;
import net.slashie.serf.game.SworeGame;
import net.slashie.serf.level.AbstractLevel;
import net.slashie.serf.level.Unleasher;
import net.slashie.serf.levelGeneration.StaticPattern;
import net.slashie.serf.ui.UserInterface;
import net.slashie.utils.Position;
import net.slashie.utils.Util;

public class SpainCastle extends StaticPattern implements Serializable {
	public SpainCastle () {
		this.cellMap = new String [][]{{
			/*"#cc#+#cc###C###cc#+#cc#",
			"#........=T-T=........#",
			"#........=F-I=........#",
			"W........=---=........W",
			"#........=---=........#",
			"+........=---=........+",
			"#........=---=........#",
			"W........=---=........W",
			"#........=---=........#",
			"+........=---=........+",
			"#........=---=........#",
			"W........=---=........W",
			"#........=---=........#",
			"#........=---=........#",
			"###########S###########",*/
			
			"############W#####W######################ccccCcccc#########",
			"#........#.....#.....#..................#..=T-T=..#.......#",
			"#........#.....#.................o......+..=F-I=..+.......#",
			"#........#.....#...........s...............=---=..#.......#",
			"#........#.....#........................#..=---=..#....J..#",
			"#........#.....#.....#..................+..=---=..+.......#",
			"#........#.##.##########....#############..=---=..#.......#",
			"#........#.#....a.....s...m.....s.......#..=---=..###...###",
			"#..........#.............Z..............W..=---=..W.......W",
			"#....o...###..#.#..#...#....#...#..#.#..#.........#...g...W",
			"#........#..............................###g._.g###.......#",
			"#........#....#.,,,,,,,,,,,,,,,,,,,,.#..#.........#.......#",
			"#........###....,,,t,,,,,,,t,,,,,,,,....#.................#",
			"#........#......,,,,,,,,,,,,,,,,,,,,..............#.......#",
			"#........#....#.,,,,,,t,,,,,,,,,,,,,.#....................W",
			"#........#......,,,,,,,,,,,,,,,,,,,,..............#.......#",
			"#........###....,,,,,,,,,,,,t,,,,,,,....#......o..........#",
			"#........#....#.,,,,,,,,,,,,,,,,,,,,.#..#.........#.......#",
			"#........#..............................##.#...#.##.......#",
			"############..#.#..#...#....#...#..#.#..#.............g...W",
			"...........#............................#.m....m..#.......W",
			"...........#............................#.........#.......#",
			"...........##.##########....################...#######.####",
			"...........#...#.................#.....#..........#.......#",
			"...........#...#.......o....o....#.....#.#.#...#..#.......#",
			"...........#...#......................................s...W",
			"...........#...#.................#.....#.#.....#..#.......#",
			"...........#...#....#............#.....#..........#.......#",
			".........####.##########.##.############.#.#...#..#########",
			".........#.....#.................#.#..............#.LLLLL.#",
			".........#.....#########.##.######.#########...####E..D..R#",
			".........#.....#.................#.#...#..........#E.....R#",
			".........#.....#########....######.#......................W",
			".........#.....#...................#.........m............#",
			".........#.........................#....................d.W",
			".........#.....#.....o.............#...#..........#.......#",
			".........####W##########WWWW#########W#######S########W####",
			"......,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,XXX,,,,,,,,,,,,",
			"......,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,",
			"......,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,",
			
		}};
		
		charMap.put(".", "CASTLE_FLOOR");
		charMap.put(",", "CASTLE_PLAZA");
		charMap.put("t", "CASTLE_TREE");
		charMap.put("=", "BLUE_CARPET");
		charMap.put("-", "RED_CARPET");
		charMap.put("#", "CASTLE_WALL");
		charMap.put("+", "SPAIN_BANNER");
		charMap.put("W", "CASTLE_WINDOW");
		charMap.put("S", "CASTLE_FLOOR EXIT SPAIN");
		charMap.put("T", "THRONE");
		charMap.put("F", "RED_CARPET ITEM KING_FERDINAND");
		charMap.put("I", "RED_CARPET ITEM QUEEN_ISABELLE");
		charMap.put("L", "BOOKSHELF");
		charMap.put("E", "BOOKSHELF_L");
		charMap.put("R", "BOOKSHELF_R");
		charMap.put("D", "CASTLE_FLOOR NPC DOMINIK");
		charMap.put("d", "CASTLE_FLOOR NPC BIZCOCHO");
		charMap.put("J", "CASTLE_FLOOR NPC CRISTOFORO");
		charMap.put("Z", "CASTLE_FLOOR NPC SANTIAGO");
		charMap.put("s", "CASTLE_FLOOR NPC SOLDIER");
		charMap.put("m", "CASTLE_FLOOR NPC MARINE");
		charMap.put("a", "CASTLE_FLOOR NPC ARCHER");
		charMap.put("g", "CASTLE_FLOOR NPC GUARD");
		charMap.put("o", "CASTLE_FLOOR NPC COLONIST");
		charMap.put("C", "SPAIN_CREST");
		charMap.put("c", "CASTLE_CURTAIN");
		charMap.put("_", "CASTLE_FLOOR EXIT _START");
		charMap.put("X", "SPAIN_GRASS_BLOCKED");
		unleashers = new Unleasher[]{new KingsChat()};
	}

	@Override
	public String getDescription() {
		return "Spain Castle";
	}

	@Override
	public Unleasher[] getUnleashers() {
		return super.getUnleashers();
	}

	class KingsChat extends Unleasher {
		Position CREST_POSITION = new Position(45,0);
		@Override
		public void unleash(AbstractLevel level, SworeGame game) {
			Actor p = level.getPlayer();
			int distance = net.slashie.utils.Position.distance(p.getPosition(), CREST_POSITION);
			if (distance <= 5){
				interactWithKings(level);
			}
		}
		
		private void interactWithKings(AbstractLevel level) {
			Expedition exp = (Expedition) level.getPlayer();
			if (!exp.getFlag("MET_WITH_KINGS")){
				//First meeting with kings
				exp.setFlag("MET_WITH_KINGS", true);
				m("We, the Catholic King and Queen of Spain, have generously decided to grant you this audience. In spite of our advisors words, we have approved your expedition to find the west path into the Indies.");
				m("Should your journey be successful, you will be given the rank of Admiral of the Seas, as well as viceroy and governor of any of the new-found lands.");
				m("We have commissioned two caravels and a carrack for your trip. What other aid should the crown grant you?");
				int choice = -1;
				while (choice == -1){
					choice = (UserInterface.getUI()).switchChat("Audience with the King and Queen","What other aid should the crown grant you?",
							"Provide me with a sailing crew",
							"Provide me with an exploratory expedition",
							"Please let me outfit the expedition myself."
							);
				}
				switch (choice){
				case 0:
					m("We will outfit the ships with sailors, captains and carpenters, we will also grant you 2.400 royal maravedíes and supplies for a five months trip.");
					break;
				case 1:
					m("We will outfit the ships with crew and will assign ten soldiers to your expedition, we will also grant you 1.000 royal maravedíes and supplies for a four months trip.");
					break;
				}
				stockExpedition(exp, choice);
				if (choice == 2){
					choice = -1;
					while (choice == -1){
						choice = (UserInterface.getUI()).switchChat("Audience with the King and Queen","How much would you need?",
								"By my accounts, I need 11.530 Maravedíes.",
								"6.000 Maravedíes will be more than enough",
								"2.000 Maravedíes, and your blessing."
								);
					}
					switch (choice){
					case 0:
						m("Then, we trust 11.530 royal maravedíes in you, and we hope you use your best judgment to outfit the expedition.");
						exp.setAccountedGold(11530);
						break;
					case 1:
						m("Then, we trust 6.000 royal maravedíes in you, and we hope you use your best judgment to outfit the expedition.");
						exp.setAccountedGold(6000);
						break;
					case 2:
						m("Are you sure? You are a brave explorer. We trust 2.000 royal maravedíes in you, and we hope you use your best judgment to outfit the expedition.");
						exp.setAccountedGold(2000);
						break;
					}
					exp.addItemOffshore(ItemFactory.createItem("SAILOR"), 15);
				}
				
				m("We have also instructed Friar Domenico to answer your questions about this journey, you will find him next to the Alcazar entrance.");
				m("May God be with you in your journey, we await your safe return. XXX XXX You are dismissed.");
				
			} else {
				boolean earnedTitle = false;
				for (Title title: Expedition.Title.values()){
					if (title.getRank() > exp.getTitle().getTitle().getRank()){
						if (title.attainsRank(exp)){
							earnedTitle = true;
							exp.getTitle().grantTitle(title, title.pickRealm(exp));
						}
					}
				}
				if (earnedTitle) {
					((ExpeditionUserInterface)UserInterface.getUI()).showBlockingMessage("Because of your service to the crown, We, the Catholic Kings of the Kingdom of Spain, name you "+exp.getTitle().getFullDescription(exp.getExpeditionary())+"..");
					((ExpeditionUserInterface)UserInterface.getUI()).showBlockingMessage("Take "+exp.getTitle().getPrize()+" maravedíes from the royal treasure, and continue defending our flag in the new world.");					
					((ExpeditionUserInterface)UserInterface.getUI()).showBlockingMessage("May God be with you in your journey. You are dismissed.");
					exp.setAccountedGold(exp.getAccountedGold()+exp.getTitle().getPrize());
				} else {
					if (exp.getFlag("DISCOVERED_NEW_WORLD")){
						((ExpeditionUserInterface)UserInterface.getUI()).showBlockingMessage("We await your safe return from the New World. XXX XXX  You are dismissed.");
					} else {
						((ExpeditionUserInterface)UserInterface.getUI()).showBlockingMessage("We await your safe return.  XXX XXX You are dismissed.");
					}
				}
			}
			
			 
			level.getPlayer().setPosition(new Position(level.getExitFor("SPAIN")));
			level.getPlayer().getPosition().y --;
			((Player)level.getPlayer()).darken();
			((Player)level.getPlayer()).see();
			UserInterface.getUI().refresh();
		}
		

		private void m(String string) {
			((ExpeditionUserInterface)UserInterface.getUI()).showBlockingMessage(string);
		}

		private void stockExpedition(Expedition ret, int choice) {
			ret.getTitle().grantTitle(Expedition.Title.EXPLORER, "of Spain");
			List<Vehicle> startingShips = ret.getCurrentVehicles();
			startingShips.add((Vehicle)ItemFactory.createItem("CARRACK"));
			startingShips.add((Vehicle)ItemFactory.createItem("CARAVEL"));
			startingShips.add((Vehicle)ItemFactory.createItem("CARAVEL"));
			switch (choice){
			case 0:
				ret.addItemOffshore(ItemFactory.createItem("SAILOR"), 75);
				ret.addItemOffshore(ItemFactory.createItem("CAPTAIN"), 3);
				ret.addItemOffshore(ItemFactory.createItem("CARPENTER"), 5);
				ret.addItemOffshore(ItemFactory.createItem("STEEL_SPEAR"), 82);
				ret.addItemOffshore(ItemFactory.createItem("STEEL_SWORD"), 3);
				ret.addItemOffshore(ItemFactory.createItem("BREASTPLATE"), 3);
				ret.addItemOffshore(ItemFactory.createItem("FRESHWATER"), 1000);
				ret.addItemOffshore(ItemFactory.createItem("EXPLORER"), 2);
				ret.addItemOffshore(ItemFactory.createItem("BISCUIT"), 15000);
				ret.addItemOffshore(ItemFactory.createItem("WOOD"), 100);
				ret.setAccountedGold(2400);
				break;
			case 1:
				ret.addItemOffshore(ItemFactory.createItem("SAILOR"), 75);
				ret.addItemOffshore(ItemFactory.createItem("CAPTAIN"), 3);
				ret.addItemOffshore(ItemFactory.createItem("CARPENTER"), 5);
				ret.addItemOffshore(ItemFactory.createItem("STEEL_SPEAR"), 82);
				ret.addItemOffshore(ItemFactory.createItem("STEEL_SWORD"), 3);
				ret.addItemOffshore(ItemFactory.createItem("BREASTPLATE"), 3);
				ret.addItemOffshore(ItemFactory.createItem("FRESHWATER"), 1000);
				ret.addItemOffshore(ItemFactory.createItem("EXPLORER"), 2);
				ret.addItemOffshore(ItemFactory.createItem("BISCUIT"), 15000);
				ret.addItemOffshore(ItemFactory.createItem("WOOD"), 100);
				
				ret.addItemOffshore(ItemFactory.createItem("SOLDIER"), 5);
				ret.addItemOffshore(ItemFactory.createItem("BREASTPLATE"), 5);
				ret.addItemOffshore(ItemFactory.createItem("STEEL_SWORD"), 5);
				ret.addItemOffshore(ItemFactory.createItem("MARINE"), 5);
				ret.addItemOffshore(ItemFactory.createItem("STUDDED_VEST"), 5);
				ret.addItemOffshore(ItemFactory.createItem("HARQUEBUS"), 5);
				
				ret.setAccountedGold(1000);
				break;
			case 2:
				ret.addItemOffshore(ItemFactory.createItem("EXPLORER"), 1);
				break;
			
			}
			/*ret.addItemOffshore(ItemFactory.createItem("SAILOR"), Util.rand(85,95));
			ret.addItemOffshore(ItemFactory.createItem("ROGUE"), Util.rand(15,25));
			ret.addItemOffshore(ItemFactory.createItem("CAPTAIN"), 3);
			ret.addItemOffshore(ItemFactory.createItem("EXPLORER"), 3);
			ret.addItemOffshore(ItemFactory.createItem("BISCUIT"), Util.rand(19000, 21000));
			ret.addItemOffshore(ItemFactory.createItem("WOOD"), Util.rand(250, 350));*/
		}
	}
}

