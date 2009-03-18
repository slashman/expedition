package net.slashie.expedition.world;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.locations.Spain;
import net.slashie.expedition.locations.TestSea;
import net.slashie.expedition.locations.World;
import net.slashie.expedition.worldGen.ExpeditionStaticGenerator;
import net.slashie.serf.level.AbstractLevel;
import net.slashie.serf.levelGeneration.StaticGenerator;
import net.slashie.serf.levelGeneration.StaticPattern;
import net.slashie.util.Pair;

public class LevelMaster {

	public static AbstractLevel createLevel(String levelID,
			Expedition expedition) {
		
		if (levelID.equals("SPAIN")){
			ExpeditionMicroLevel ret = new ExpeditionMicroLevel();
			// LoadLevel spain.xml
			StaticPattern pattern = new Spain();
			StaticGenerator generator = new ExpeditionStaticGenerator();
			pattern.setup(generator);
			generator.createLevel(ret);
			ret.setDescription(pattern.getDescription());
			if (pattern.getUnleashers() != null){
				ret.setUnleashers(pattern.getUnleashers());
			}
			ret.setLocation(new Pair<Integer,Integer>(37,-6));
			ret.setMusicKey("SPAIN");
			ret.setSuperLevelId("WORLD");
			ret.setID("SPAIN");
			return ret;
		} else if (levelID.equals("WORLD")){
			ExpeditionMacroLevel ret = new ExpeditionMacroLevel();
			//StaticPattern pattern = new TestSea();
			StaticPattern pattern = new World();
			StaticGenerator generator = new ExpeditionStaticGenerator();
			pattern.setup(generator);
			generator.createLevel(ret);
			ret.setDescription(pattern.getDescription());
			if (pattern.getUnleashers() != null){
				ret.setUnleashers(pattern.getUnleashers());
			}
			ret.setMusicKey("SEA");
			ret.setID("WORLD");
			return ret;
		} else if (levelID.equals("NEW_WORLD")){
			
		}
		return null;
	}

}
