package net.slashie.expedition.world;

import net.ck.expedition.model.test.ExpeditionProperties;
import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.FriarTutorial;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.level.GlobeMapModel;
import net.slashie.expedition.locations.Spain;
import net.slashie.expedition.locations.SpainCastle;
import net.slashie.expedition.locations.TestSea;
import net.slashie.expedition.locations.World;
import net.slashie.expedition.worldGen.ExpeditionStaticGenerator;
import net.slashie.expedition.worldGen.WorldGenerator;
import net.slashie.serf.level.AbstractLevel;
import net.slashie.serf.levelGeneration.StaticGenerator;
import net.slashie.serf.levelGeneration.StaticPattern;
import net.slashie.util.Pair;
import net.slashie.utils.Position;

public class LevelMaster
{
	public static AbstractLevel createLevel(String levelID, Expedition expedition)
	{

		if (levelID.equals("PALOS"))
		{
			SettlementLevel ret = new SettlementLevel();
			ExpeditionGame.getCurrentGame().registerSettlement(ret);
			// LoadLevel spain.xml
			StaticPattern pattern = new Spain();
			StaticGenerator generator = new ExpeditionStaticGenerator();
			pattern.setup(generator);
			generator.createLevel(ret);
			ret.setDescription(pattern.getDescription());
			if (pattern.getUnleashers() != null)
			{
				ret.setUnleashers(pattern.getUnleashers());
			}
			ret.setLocation(new Pair<Integer, Integer>(38, -6));
			ret.getHelper().setMusicKey("SPAIN");
			ret.getHelper().setSuperLevelId("WORLD");
			ret.setID("PALOS");
			ret.setDock(true);
			ret.setZoomIn(false);
			ret.setTemperature(20);

			if (!expedition.getFlag("PALOS_TUTORIAL"))
			{
				expedition.setFlag("PALOS_TUTORIAL", true);
				FriarTutorial.activate(FriarTutorial.ENTERING_TOWN);
			}

			return ret;
		}
		if (levelID.equals("SPAIN_CASTLE"))
		{
			ExpeditionMicroLevel ret = new ExpeditionMicroLevel();
			// LoadLevel spain.xml
			StaticPattern pattern = new SpainCastle();
			StaticGenerator generator = new ExpeditionStaticGenerator();
			pattern.setup(generator);
			generator.createLevel(ret);
			ret.setDescription(pattern.getDescription());
			if (pattern.getUnleashers() != null)
			{
				ret.setUnleashers(pattern.getUnleashers());
			}
			ret.setLocation(new Pair<Integer, Integer>(38, -6));
			ret.getHelper().setMusicKey("SPAIN_CASTLE");
			ret.getHelper().setSuperLevelId("WORLD");
			ret.setID("SPAIN_CASTLE");
			ret.setZoomIn(true);
			ret.setTemperature(20);
			ret.setDock(true);
			return ret;
		}
		else if (levelID.equals("WORLD"))
		{

			StaticPattern pattern;
			if (ExpeditionProperties.isDebug())
			{
				//pattern = new TestSea();
				pattern = new World();
			}
			else
			{
				pattern = new World();
			}
			
			ExpeditionMacroLevel ret = new ExpeditionMacroLevel("scenarios/theNewWorld/world", 3374, 2939, 50, 50,
					pattern.getCharMap(), new Position(-427, 2235), GlobeMapModel.getSingleton());
			ret.setDescription(pattern.getDescription());
			if (pattern.getUnleashers() != null)
			{
				ret.setUnleashers(pattern.getUnleashers());
			}
			WorldGenerator.addCities(ret);
			WorldGenerator.addNativeSettlements(ret);
			ret.setID("WORLD");
			ret.setWindDirection(CardinalDirection.WEST);

			if (!expedition.getFlag("LEAVING_PALOS_TUTORIAL"))
			{
				expedition.setFlag("LEAVING_PALOS_TUTORIAL", true);
				FriarTutorial.activate(FriarTutorial.LEAVING_TOWN);
			}

			return ret;
		}
		else if (levelID.equals("NEW_WORLD"))
		{

		}
		return null;
	}
}
