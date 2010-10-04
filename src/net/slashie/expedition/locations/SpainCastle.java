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
			"#........#.....#.....#..................#..=T-T=..#.LLLLL.#",
			"#........#.....#.................o......+..=F-I=..+E..D..R#",
			"#........#.....#...........s...............=---=..#E.....R#",
			"#........#.....#........................#..=---=..#.......#",
			"#........#.....#.....#..................+..=---=..+.....d.#",
			"#........#.##.##########....#############..=---=..#.......#",
			"#........#.#....a.....s...m.....s.......#..=---=..###...###",
			"#..........#.............Z..............W..=---=..W.......W",
			"#....o...###..#.#..#...#....#...#..#.#..#.........#...g...W",
			"#........#..............................###g._.g###.......#",
			"#........#....#.,,,,,,,,,,,,,,,,,,,,.#..#.........#.......#",
			"#........###....,,,t,,,,,,,t,,,,,,,,....#.................#",
			"#........#......,,,,,,,,,,,,,,,,,,,,..............#.......#",
			"#........#....#.,,,,,,t,,,,,,,,,,,,,.#....................S",
			"#........#......,,,,,,,,,,,,,,,,,,,,..............#.......#",
			"#........###....,,,,,,,,,,,,t,,,,,,,....#......o..........#",
			"#........#....#.,,,,,,,,,,,,,,,,,,,,.#..#.........#.......#",
			"#........#..............................##.#.#.####.......#",
			"############..#.#..#...#....#...#..#.#..#.............g...W",
			"...........#............................#.m....m..#.......W",
			"...........#............................#.........#.......#",
			"...........##.##########....################.#########.####",
			"...........#...#.................#.....#..........#.......#",
			"...........#...#.......o....o....#.....#.#.#.#.#..#.......#",
			"...........#...#......................................s...W",
			"...........#...#.................#.....#.#.....#..#.......#",
			"...........#...#....#............#.....#..........#.......#",
			".........####.##########.##.############.#.#.#.#..#########",
			".........#.....#.................#.#......................#",
			".........#.....#########.##.######.#########.##############",
			".........#.....#.................#.#...#..........#.......#",
			".........#.....#########....######.#..................J...W",
			".........#.....#...................#.........m............#",
			".........#.........................#......................W",
			".........#.....#.....o.............#...#..........#.......#",
			".........####W##########WWWW#########W######W##############",
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
				((ExpeditionUserInterface)UserInterface.getUI()).showBlockingMessage("We, the Catholic King and Queen of Spain, have generously decided to grant you this audience. We have aproved your expedition to find the west path into the Indias.");
				((ExpeditionUserInterface)UserInterface.getUI()).showBlockingMessage("Should your journey be sucessful, you will be given the rank of Admiral of the Seas, as well as viceroy and governor of any of the new-found lands.");
				((ExpeditionUserInterface)UserInterface.getUI()).showBlockingMessage("We will grant you 6.000 royal maravedíes as well as equipment and men, and we will order every person in Spain to aid you in whatever they can.");
				((ExpeditionUserInterface)UserInterface.getUI()).showBlockingMessage("We have also instructed Fray Domenico to answer your questions about this journey, you will find him next to the Alcazar entrance.");
				((ExpeditionUserInterface)UserInterface.getUI()).showBlockingMessage("May God be with you in your journey, we await your safe return. XXX XXX You are dismissed.");
				stockExpedition(exp);
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
					((ExpeditionUserInterface)UserInterface.getUI()).showBlockingMessage("We await your safe return from the New World. You are dismissed.");
				}
			}
			
			 
			level.getPlayer().setPosition(new Position(level.getExitFor("SPAIN")));
			level.getPlayer().getPosition().x --;
			((Player)level.getPlayer()).darken();
			((Player)level.getPlayer()).see();
			UserInterface.getUI().refresh();
		}
		

		private void stockExpedition(Expedition ret) {
			ret.getTitle().grantTitle(Expedition.Title.EXPLORER, "of Spain");
			ret.setAccountedGold(6000);
			List<Vehicle> startingShips = ret.getCurrentVehicles();
			startingShips.add((Vehicle)ItemFactory.createItem("CARRACK"));
			startingShips.add((Vehicle)ItemFactory.createItem("CARAVEL"));
			startingShips.add((Vehicle)ItemFactory.createItem("CARAVEL"));
			
			ret.addItemOffshore(ItemFactory.createItem("SAILOR"), Util.rand(85,95));
			ret.addItemOffshore(ItemFactory.createItem("ROGUE"), Util.rand(15,25));
			ret.addItemOffshore(ItemFactory.createItem("CAPTAIN"), 3);
			ret.addItemOffshore(ItemFactory.createItem("EXPLORER"), 3);
			ret.addItemOffshore(ItemFactory.createItem("BISCUIT"), Util.rand(19000, 21000));
			ret.addItemOffshore(ItemFactory.createItem("WOOD"), Util.rand(250, 350));
		}
	}
}

