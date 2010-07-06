package net.slashie.expedition.locations;

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
			((ExpeditionUserInterface)UserInterface.getUI()).showBlockingMessage("Leave!");
			level.getPlayer().setPosition(new Position(level.getExitFor("SPAIN")));
			level.getPlayer().getPosition().y --;
			((Player)level.getPlayer()).darken();
			((Player)level.getPlayer()).see();
			UserInterface.getUI().refresh();
		}
		
		
		
	}
}
