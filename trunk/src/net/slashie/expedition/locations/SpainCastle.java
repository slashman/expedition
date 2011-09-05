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

public class SpainCastle extends StaticPattern implements Serializable {
	private static final long serialVersionUID = 1L;

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
			"#........#.....#.....#..................+..=-_-=..+.......#",
			"#........#.##.##########....#############..=---=..#.......#",
			"#........#.#....a.....s...m.....s.......#..=---=..###...###",
			"#..........#.............Z..............W..=---=..W.......W",
			"#....o...###..#.#..#...#....#...#..#.#..#.........#...g...W",
			"#........#..............................###g...g###.......#",
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
		charMap.put("F", "RED_CARPET NPC KING_FERDINAND");
		charMap.put("I", "RED_CARPET NPC QUEEN_ISABELLE");
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
		charMap.put("_", "RED_CARPET EXIT _START");
		charMap.put("X", "SPAIN_GRASS_BLOCKED");
		unleashers = new Unleasher[]{new KingsChat()};
	}

	@Override
	public String getDescription() {
		return "Alcazar of Córdova";
	}

	@Override
	public Unleasher[] getUnleashers() {
		return super.getUnleashers();
	}

	class KingsChat extends Unleasher {
		private static final long serialVersionUID = 1L;
		Position CREST_POSITION = new Position(45,0);
		private int previousPlayerY = 0;
		@Override
		public void unleash(AbstractLevel level, SworeGame game) {
			Actor p = level.getPlayer();
			int distance = net.slashie.utils.Position.distance(p.getPosition(), CREST_POSITION);
			if (distance <= 5){
				interactWithKings(level);
			}
			previousPlayerY = level.getPlayer().getPosition().y;

		}
		
		private void interactWithKings(AbstractLevel level) {
			Expedition exp = (Expedition) level.getPlayer();
			if (!exp.getFlag("MET_WITH_KINGS")){
				//First meeting with kings
				exp.setFlag("MET_WITH_KINGS", true);
				m("You stand again in front of King Ferdinand and Queen Isabella, the royal monarchs of the recently born Kingdom of Spain.");
				m("Your last audience in their throne room ended in yet another disappointment, but when you were leaving Cordova a group of royal guards requested you to visit again the Alcazar.");
				m("You approach the throne and wait for the monarchs' words.");
				m(ferdinandSays()+"Welcome back, Christopher Colombus of Genoa.");
				m(ferdinandSays()+"We, the Catholic King and Queen of Spain, have generously decided to grant you this audience. In spite of our advisors words, we have approved your expedition to find the west path into the Indies.");
				m(ferdinandSays()+"Should your journey be successful, you will be given the rank of Admiral of the Seas, as well as viceroy and governor of any of the new-found lands.");
				ml(isabellaSays()+"We have commissioned two caravels and a carrack for your voyage. What other aid should the crown grant you?");
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
					m(ferdinandSays()+"We will outfit the ships with sailors, captains and carpenters, we will also grant you 2.400 royal maravedíes and supplies for a five months voyage.");
					break;
				case 1:
					m(ferdinandSays()+"We will outfit the ships with crew and will assign ten soldiers to your expedition, we will also grant you 1.000 royal maravedíes and supplies for a four months voyage.");
					break;
				}
				stockExpedition(exp, choice);
				if (choice == 2){
					choice = -1;
					while (choice == -1){
						choice = (UserInterface.getUI()).switchChat("Audience with the King and Queen","How much would you need?",
								"By my accounts, I need 24.990 Maravedíes.",
								"12.000 Maravedíes will be more than enough",
								"6.000 Maravedíes, and your blessing."
								);
					}
					switch (choice){
					case 0:
						m(isabellaSays()+"Then, we trust 24.990 royal maravedíes in you, and we hope you use your best judgment to outfit the expedition.");
						exp.setAccountedGold(24990);
						break;
					case 1:
						m(isabellaSays()+"Then, we trust 12.000 royal maravedíes in you, and we hope you use your best judgment to outfit the expedition.");
						exp.setAccountedGold(12000);
						break;
					case 2:
						m(ferdinandSays()+"Are you sure? You are a brave explorer. We trust 6.000 royal maravedíes in you, and we hope you use your best judgment to outfit the expedition.");
						exp.setAccountedGold(6000);
						break;
					}
				}
				
				m(isabellaSays()+"We have instructed Friar Domenico to answer your questions about this journey, you will find him next to the Alcazar entrance.");
				ml(ferdinandSays()+"May God be with you in your journey, we await your safe return. XXX XXX You are dismissed.");
				level.addMessage("You see the exit of the Alcazar to the south");
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
					m(ferdinandSays()+"Because of your service to the crown, We, the Catholic Kings of Spain, name you "+exp.getTitle().getFullDescription(exp.getExpeditionary())+"..");
					m(ferdinandSays()+"Take "+exp.getTitle().getPrize()+" maravedíes from the royal treasure, and continue defending our flag in the new world.");					
					ml(ferdinandSays()+"May God be with you in your journey. You are dismissed.");
					exp.setAccountedGold(exp.getAccountedGold()+exp.getTitle().getPrize());
				} else {
					if (exp.getFlag("DISCOVERED_NEW_WORLD")){
						ml(isabellaSays()+"We await your safe return from the West Indies. XXX You are dismissed.");
					} else {
						if (level.getPlayer().getPosition().y < previousPlayerY){
							// Advancing toward the kings
							ml(ferdinandSays()+"We await your safe return.  XXX You are dismissed.");
						}
					}
				}
			}
			
			/*
			level.getPlayer().setPosition(new Position(level.getExitFor("SPAIN")));
			level.getPlayer().getPosition().y --;*/
			((Player)level.getPlayer()).darken();
			((Player)level.getPlayer()).see();
			UserInterface.getUI().refresh();
		}
		

		private String isabellaSays() {
			return "Isabella, Queen of Castile and León says: XXX XXX ";
		}

		private String ferdinandSays() {
			return "Ferdinand II, King of Aragón says: XXX XXX " ;
		}

		private void m(String string) {
			((ExpeditionUserInterface)UserInterface.getUI()).showBlockingMessage(string, true);		
		}
		
		private void ml(String string) {
			((ExpeditionUserInterface)UserInterface.getUI()).showBlockingMessage(string, false);		
		}

		private void stockExpedition(Expedition ret, int choice) {
			ret.getTitle().grantTitle(Expedition.Title.EXPLORER, "of Spain");
			List<Vehicle> startingShips = ret.getCurrentVehicles();
			startingShips.add((Vehicle)ItemFactory.createItem("CARRACK"));
			startingShips.add((Vehicle)ItemFactory.createItem("CARAVEL"));
			startingShips.add((Vehicle)ItemFactory.createItem("CARAVEL"));
			//choice = 99;
			switch (choice){
			case 0:
				ret.addItemOffshore(ItemFactory.createItem("SAILOR"), 75);
				ret.addItemOffshore(ItemFactory.createItem("CAPTAIN"), 3);
				ret.addItemOffshore(ItemFactory.createItem("CARPENTER"), 5);
				ret.addItemOffshore(ItemFactory.createItem("STEEL_SPEAR"), 82);
				ret.addItemOffshore(ItemFactory.createItem("STEEL_SWORD"), 3);
				ret.addItemOffshore(ItemFactory.createItem("BREASTPLATE"), 3);
				//ret.addItemOffshore(ItemFactory.createItem("FRESHWATER"), 1000);
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
				//ret.addItemOffshore(ItemFactory.createItem("FRESHWATER"), 1000);
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
			}
		}
	}
}

