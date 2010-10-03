package net.slashie.expedition.world;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.locations.Spain;
import net.slashie.expedition.locations.SpainCastle;
import net.slashie.expedition.locations.TestSea;
import net.slashie.expedition.locations.World;
import net.slashie.expedition.worldGen.ExpeditionStaticGenerator;
import net.slashie.expedition.worldGen.WorldGenerator;
import net.slashie.serf.level.AbstractLevel;
import net.slashie.serf.level.Dispatcher;
import net.slashie.serf.levelGeneration.StaticGenerator;
import net.slashie.serf.levelGeneration.StaticPattern;
import net.slashie.util.Pair;
import net.slashie.utils.Position;

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
			ret.setLocation(new Pair<Integer,Integer>(38,-6));
			ret.getHelper().setMusicKey("SPAIN");
			ret.getHelper().setSuperLevelId("WORLD");
			ret.setID("SPAIN");
			ret.setDock(true);
			ret.setZoomIn(false);
			ret.setTemperature(20);
			return ret;
		} if (levelID.equals("SPAIN_CASTLE")){
			ExpeditionMicroLevel ret = new ExpeditionMicroLevel();
			// LoadLevel spain.xml
			StaticPattern pattern = new SpainCastle();
			StaticGenerator generator = new ExpeditionStaticGenerator();
			pattern.setup(generator);
			generator.createLevel(ret);
			ret.setDescription(pattern.getDescription());
			if (pattern.getUnleashers() != null){
				ret.setUnleashers(pattern.getUnleashers());
			}
			ret.setLocation(new Pair<Integer,Integer>(38,-6));
			ret.getHelper().setMusicKey("SPAIN_CASTLE");
			ret.getHelper().setSuperLevelId("WORLD");
			ret.setID("SPAIN_CASTLE");
			ret.setZoomIn(true);
			ret.setTemperature(20);
			return ret;
		} else if (levelID.equals("WORLD")){
			StaticPattern pattern = new World();
			
			/*ExpeditionMacroLevel ret = new ExpeditionMacroLevel(
			"world", 150,150,50,50, pattern.getCharMap(), new Pair<String, Position>("_START", new Position(3198,801)));*/
			ExpeditionMacroLevel ret = new ExpeditionMacroLevel(
					"data/world", 3374,2939,50,50, pattern.getCharMap(), new Position(3236,834));
			ret.setDescription(pattern.getDescription());
			if (pattern.getUnleashers() != null){
				ret.setUnleashers(pattern.getUnleashers());
			}
			WorldGenerator.addNativeSettlements(ret);
			ret.getHelper().setMusicKey("SEA");
			ret.setID("WORLD");
			ret.setWindDirection(CardinalDirection.WEST);
			return ret;
		} else if (levelID.equals("NEW_WORLD")){
			
		}
		return null;
	}

}
