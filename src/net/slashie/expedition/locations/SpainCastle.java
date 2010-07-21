package net.slashie.expedition.locations;

import java.util.ArrayList;
import java.util.List;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.ExpeditionItem;
import net.slashie.expedition.domain.Town;
import net.slashie.expedition.domain.Vehicle;
import net.slashie.expedition.domain.Expedition.Title;
import net.slashie.expedition.game.ExpeditionGame;
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

public class SpainCastle extends StaticPattern {

	public SpainCastle () {
		this.cellMap = new String [][]{{
			"###########C###########",
			"#cccccc..=F-I=..cccccc#",
			"#........=---=........#",
			"W.+......=---=......+.W",
			"#........=---=........#",
			"#........=---=........#",
			"#........=---=........#",
			"W.+......=---=......+.W",
			"#........=---=........#",
			"#........=---=........#",
			"#........=---=........#",
			"W.+......=---=......+.W",
			"#........=---=........#",
			"#........=---=........#",
			"###########S###########",
		}};
		
		charMap.put(".", "CASTLE_FLOOR");
		charMap.put("=", "BLUE_CARPET");
		charMap.put("-", "RED_CARPET");
		charMap.put("#", "CASTLE_WALL");
		charMap.put("+", "SPAIN_BANNER");
		charMap.put("W", "CASTLE_WINDOW");
		charMap.put("S", "CASTLE_FLOOR EXIT SPAIN");
		charMap.put("F", "KING_FERDINAND");
		charMap.put("I", "QUEEN_ISABELLE");
		charMap.put("C", "SPAIN_CREST");
		charMap.put("c", "CASTLE_CURTAIN");
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
		Position CREST_POSITION = new Position(11,0);
		@Override
		public void unleash(AbstractLevel level, SworeGame game) {
			Actor p = level.getPlayer();
			int distance = net.slashie.utils.Position.distance(p.getPosition(), CREST_POSITION);
			if (distance <= 6){
				interactWithKings(level);
			}
		}
		
		private void interactWithKings(AbstractLevel level) {
			Expedition exp = (Expedition) level.getPlayer();
			if (!exp.getFlag("MET_WITH_KINGS")){
				//First meeting with kings
				exp.setFlag("MET_WITH_KINGS", true);
				((ExpeditionUserInterface)UserInterface.getUI()).showBlockingMessage("We, the Catholic Kings of the Kingdom of Spain, have generously decided to grant you this audience. chosen you to support our enterprise into the west path into the Indias.");
				((ExpeditionUserInterface)UserInterface.getUI()).showBlockingMessage("Should your journey be sucessful, you will be given the rank of Admiral of the Seas, as well as viceroy and governor of all the new lands.");
				((ExpeditionUserInterface)UserInterface.getUI()).showBlockingMessage("We will give you 12000 maravedíes as well as two caravels and a carrack for your first exploratory voyage.");
				((ExpeditionUserInterface)UserInterface.getUI()).showBlockingMessage("May God be with you in your journey, we await your safe return from the New World. You are dismissed.");
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
			level.getPlayer().getPosition().y --;
			((Player)level.getPlayer()).darken();
			((Player)level.getPlayer()).see();
			UserInterface.getUI().refresh();
		}
		

		private void stockExpedition(Expedition ret) {
			ret.getTitle().grantTitle(Expedition.Title.EXPLORER, "of Spain");
			ret.setAccountedGold(12000);
			List<Vehicle> startingShips = ret.getCurrentVehicles();
			startingShips.add((Vehicle)ItemFactory.createItem("CARRACK"));
			startingShips.add((Vehicle)ItemFactory.createItem("CARAVEL"));
			startingShips.add((Vehicle)ItemFactory.createItem("CARAVEL"));
			
			ExpeditionItem food = ItemFactory.createItem("FOOD");
			ExpeditionItem sailor = ItemFactory.createItem("SAILOR");
			ExpeditionItem captain = ItemFactory.createItem("CAPTAIN");
			
			
			
			ret.addItemOffshore(sailor, 30);
			ret.addItemOffshore(captain, 3);
			ret.addItemOffshore(food, 100);
			
			
		}
		
		
		
	}
}
