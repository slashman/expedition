package net.slashie.expedition.locations;

import net.slashie.serf.level.Unleasher;
import net.slashie.serf.levelGeneration.StaticPattern;

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
		charMap.put("F", "THRONE NPC KING_FERDINAND");
		charMap.put("I", "THRONE NPC QUEEN_ISABELLE");
		charMap.put("C", "SPAIN_CREST");
		charMap.put("c", "CASTLE_CURTAIN");
		unleashers = new Unleasher[]{};
	}

	@Override
	public String getDescription() {
		return "Spain Castle";
	}

}
