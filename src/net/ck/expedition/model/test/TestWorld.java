package net.ck.expedition.model.test;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import net.slashie.expedition.DisplayMode;
import net.slashie.expedition.RunExpedition;
import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.domain.NativeTown;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.level.FlatMapModelSeconds;
import net.slashie.expedition.level.GlobeMapModel;
import net.slashie.expedition.locations.World;
import net.slashie.expedition.world.ExpeditionMacroLevel;
import net.slashie.expedition.worldGen.WorldGenerator;
import net.slashie.serf.levelGeneration.StaticPattern;
import net.slashie.utils.Position;

public class TestWorld
{

	static private Expedition ret;

	final static Logger logger = Logger.getRootLogger();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		RunExpedition.setMode(DisplayMode.SWING_GFX);
		RunExpedition.readConfiguration();
		RunExpedition.initializeGraphics();
		RunExpedition.initializeData();

		ExpeditionGame game = new ExpeditionGame();
		ret = new Expedition(game);
		ret.setGame(game);
		ret.setAppearanceId("EXPEDITION");
		ret.setName("Colombus");
		ret.setExpeditionary("Colombus");
		ret.getTitle().grantTitle(Expedition.Title.EXPLORER, "of Spain");

	}

	@Before
	public void setUp() throws Exception
	{
	}

	@Test
	public void testExecute()
	{
		logger.debug("------------------------------------------------------------");
		logger.debug("test 1: create the world");
		StaticPattern pattern = new World();
		GlobeMapModel.setSingleton(new FlatMapModelSeconds());
		ExpeditionMacroLevel level = new ExpeditionMacroLevel("scenarios/theNewWorld/world", 3374, 2939, 50, 50,
				pattern.getCharMap(), new Position(-427, 2235), GlobeMapModel.getSingleton());
		logger.debug("add settlements");
		WorldGenerator.addNativeSettlements(level);
		logger.debug("add animals");
		WorldGenerator.addAnimalNests();
		logger.debug("add crops");
		WorldGenerator.addBotanyCrops();
		logger.debug("add cities");
		WorldGenerator.addCities(level);

		List<NativeTown> nativeTowns = new ArrayList<NativeTown>();

		nativeTowns = level.getNativeTowns();

		assertTrue(nativeTowns.size() > 0);
	}

	
	@After
	public void tearDown() throws Exception
	{
		logger.debug("Start: tearDown Arguments: " + null);
	}
	
}
