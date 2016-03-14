package net.slashie.expedition.worldGen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import net.slashie.expedition.data.ExpeditionDAO;
import net.slashie.expedition.domain.NativeTown;
import net.slashie.expedition.domain.Ruin;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.level.GlobeMapModel;
import net.slashie.expedition.world.AnimalNest;
import net.slashie.expedition.world.BotanyCrop;
import net.slashie.expedition.world.Culture;
import net.slashie.expedition.world.ExpeditionMacroLevel;
import net.slashie.expedition.world.OverworldExpeditionCell;
import net.slashie.expedition.world.Settlement;
import net.slashie.serf.game.SworeGame;
import net.slashie.util.FileUtil;
import net.slashie.util.Pair;
import net.slashie.utils.Position;
import net.slashie.utils.Util;

public class WorldGenerator
{

	final static Logger logger = Logger.getRootLogger();

	/**
	 * 
	 * @param level
	 *            - the Macro Level meaning the overworld game world
	 */
	public static void addNativeSettlements(ExpeditionMacroLevel level)
	{
		logger.debug("Start: addNativeSettlements Arguments: " + level.toString());
		try
		{
			List<Pair<Position, Culture>> cultureCenters = new ArrayList<Pair<Position, Culture>>();
			// Get the culture centers
			try
			{
				BufferedReader r = FileUtil.getReader("scenarios/theNewWorld/culture.properties");
				String line = r.readLine();
				while (line != null)
				{
					String[] row = line.split(",");
					Culture c = ExpeditionDAO.getCulture(row[2].trim());
					Position p = new Position(Integer.parseInt(row[0].trim()), Integer.parseInt(row[1].trim()));
					// Translate position to lat-long
					GlobeMapModel.getSingleton().transformIntoLatLong(p);
					cultureCenters.add(new Pair<Position, Culture>(p, c));
					line = r.readLine();
				}
			}
			catch (FileNotFoundException fnfe)
			{
				SworeGame.crash("Culture File Not Found");
				return;
			}

			logger.debug("finished reading culture file");

			// Create settlements around each culture center
			for (Pair<Position, Culture> cultureCenter : cultureCenters)
			{
				int numberOfSettlements = 0;
				int range = 15;
				numberOfSettlements = Util.rand(3, 6);
				int fussible = 0;
				for (int i = 0; i < numberOfSettlements; i++)
				{
					Position settlementPosition = new Position(
							Util.rand(cultureCenter.getA().x - range, cultureCenter.getA().x + range),
							Util.rand(cultureCenter.getA().y - range, cultureCenter.getA().y + range));
					settlementPosition.x = GlobeMapModel.getSingleton().normalizeLong(settlementPosition.y,
							settlementPosition.x);
					settlementPosition.y = GlobeMapModel.getSingleton().normalizeLat(settlementPosition.y);
					OverworldExpeditionCell cell = (OverworldExpeditionCell) level.getMapCell(settlementPosition);

					// Check if this is land
					if (cell != null && !cell.isRiver() && cell.isLand()
							&& level.getFeaturesAt(settlementPosition) == null)
					{
						// Make a chance depending for place a ruin or a
						// settlement
						// if (Util.chance(100))

						Random random = new Random();
						int ret = random.nextInt(100 - 1 + 1) + 1;

						if (ret == 100)
						{
							logger.debug("ruin added: " + settlementPosition.toString());
							Ruin r = new Ruin(ExpeditionGame.getCurrentGame(), cultureCenter.getB());
							r.setPosition(new Position(settlementPosition));
							level.addActor(r);
						}
						else
						{
							//logger.debug("native town added: " + settlementPosition.toString());
							// Place a settlement
							NativeTown t = new NativeTown(ExpeditionGame.getCurrentGame(), cultureCenter.getB(),
									cultureCenter.getB().getASize());
							t.setPosition(new Position(settlementPosition));
							t.setUnfriendly(Util.chance(30 * cultureCenter.getB().getAggresiveness()));
							level.addNativeTown(t);
							// level.addFeature(t);
						}
					}
					else
					{
						fussible++;
						if (fussible < 1000)
						{
							i--;
							continue;
						}
					}
				}
			}

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private static class GenerationContext
	{
		int year;
		ExpeditionMacroLevel level;
	}
/**
 * ret adding western cities to the world
 * @param - the macrolevel meaning the huge overworld map
 */
	public static void addCities(ExpeditionMacroLevel ret)
	{
		logger.info("Start: addCities Arguments: " + ret);
		GenerationContext context = new GenerationContext();
		context.year = 1492;
		context.level = ret;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try
		{
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document document = builder.parse(new File("scenarios/theNewWorld/settlements.xml"));
			NodeList baseNodes = document.getChildNodes();
			for (int i = 0; i < baseNodes.getLength(); i++)
			{
				Node baseNode = baseNodes.item(i);
				if (baseNode.getNodeType() == Node.ELEMENT_NODE)
				{
					Element baseElement = (Element) baseNode;
					if (baseElement.getTagName().equals("geopolitical"))
					{
						processGeopolitical(baseElement, context);
					}
				}
			}
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
		}
		catch (SAXException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param geoPoliticalElement
	 * @param context
	 */
	private static void processGeopolitical(Element geoPoliticalElement, GenerationContext context)
	{
		NodeList baseNodes = geoPoliticalElement.getChildNodes();
		for (int i = 0; i < baseNodes.getLength(); i++)
		{
			Node baseNode = baseNodes.item(i);
			if (baseNode.getNodeType() == Node.ELEMENT_NODE)
			{
				Element baseElement = (Element) baseNode;
				if (baseElement.getTagName().equals("settlements"))
				{
					processSettlements(baseElement, context);
				}
			}
		}
	}
/**
 * 
 * @param settlementsElement
 * @param context
 */
	private static void processSettlements(Element settlementsElement, GenerationContext context)
	{
		NodeList baseNodes = settlementsElement.getChildNodes();
		for (int i = 0; i < baseNodes.getLength(); i++)
		{
			Node baseNode = baseNodes.item(i);
			if (baseNode.getNodeType() == Node.ELEMENT_NODE)
			{
				Element baseElement = (Element) baseNode;
				if (baseElement.getTagName().equals("settlement"))
				{
					processSettlement(baseElement, context);
				}
			}
		}
	}

	/**
	 * Add a settlement to the world, based on its location. Also set the cell
	 * to port city?
	 * 
	 * @param settlementElement
	 */
	private static void processSettlement(Element settlementElement, GenerationContext context)
	{
		// Check if the settlement existed in that year
		NodeList baseNodes = settlementElement.getChildNodes();
		for (int i = 0; i < baseNodes.getLength(); i++)
		{
			Node baseNode = baseNodes.item(i);
			if (baseNode.getNodeType() == Node.ELEMENT_NODE)
			{
				Element baseElement = (Element) baseNode;
				if (baseElement.getTagName().equals("history"))
				{
					processSettlementHistory(settlementElement, baseElement, context);
				}
			}
		}
	}

	/**
	 * 
	 * @param settlementElement
	 * @param historyElement
	 * @param context
	 */
	private static void processSettlementHistory(Element settlementElement, Element historyElement,
			GenerationContext context)
	{
		boolean firstEvent = true;
		NodeList baseNodes = historyElement.getChildNodes();
		for (int i = 0; i < baseNodes.getLength(); i++)
		{
			Node baseNode = baseNodes.item(i);
			if (baseNode.getNodeType() == Node.ELEMENT_NODE)
			{
				Element baseElement = (Element) baseNode;
				if (baseElement.getTagName().equals("event"))
				{
					Element eventElement = (Element) baseNode;
					if (firstEvent)
					{
						int eventYear = Integer.parseInt(eventElement.getAttribute("date"));
						if (eventYear <= context.year)
						{
							createSettlement(settlementElement, eventElement.getAttribute("name"), context);
						}
						firstEvent = false;
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param settlementElement
	 * @param settlementName
	 * @param context
	 */
	private static void createSettlement(Element settlementElement, String settlementName, GenerationContext context)
	{
		logger.debug("Start: createSettlement Arguments: " + settlementElement.toString() + " " + settlementName + " "
				+ context.toString());
		String latStr = settlementElement.getAttribute("lat");
		String longStr = settlementElement.getAttribute("long");
		String settlementId = settlementElement.getAttribute("id");
		int lat = parseLatitude(latStr);
		int longi = parseLongitude(longStr);

		lat = GlobeMapModel.getSingleton().normalizeLat(lat);
		longi = GlobeMapModel.getSingleton().normalizeLong(lat, longi);

		// Create the settlement feature
		Settlement s = new Settlement(settlementName, "TOWN");
		s.setPosition(longi, lat, 0);
		context.level.addFeature(s);

		// Add an exit
		context.level.addExit(new Position(longi, lat, 0), settlementId);
		logger.debug("End: createSettlement");
	}

	/**
	 * 
	 * @param longStr
	 * @return
	 */
	private static int parseLongitude(String longStr)
	{
		//logger.debug("Start: parseLongitude Arguments: " + longStr);
		int degrees = Integer.parseInt(longStr.substring(0, longStr.indexOf("�")));
		int minutes = Integer.parseInt(longStr.substring(longStr.indexOf("�") + 1, longStr.indexOf('\'')));
		boolean west = longStr.substring(longStr.indexOf('\'') + 1).equals("W");
		return (degrees * 60 + minutes) * 60 * (west ? -1 : 1);
	}

	/**
	 * 
	 * @param latStr
	 * @return
	 */
	private static int parseLatitude(String latStr)
	{
		//logger.info("Start: parseLatitude Arguments: " + latStr);
		int degrees = Integer.parseInt(latStr.substring(0, latStr.indexOf("�")));
		int minutes = Integer.parseInt(latStr.substring(latStr.indexOf("�") + 1, latStr.indexOf("'")));
		boolean north = latStr.substring(latStr.indexOf("'") + 1).equals("N");
		return (degrees * 60 + minutes) * 60 * (north ? 1 : -1);
	}

	public static List<Pair<Position, AnimalNest>> animalNests = new ArrayList<Pair<Position, AnimalNest>>();

	/**
	 *  adding animal nests to the game
	 *  reading from animalNests.properties
	 *  
	 *  not yet sure what that actually is meant to be, seems like those are animal spawners
	 */
	public static void addAnimalNests()
	{
		logger.info("Start: addAnimalNests Arguments: ");
		try
		{
			// read the animal nests
			try
			{
				BufferedReader r = FileUtil.getReader("scenarios/theNewWorld/animalNests.properties");
				String line = r.readLine();
				while (line != null)
				{
					String[] row = line.split(",");
					AnimalNest n = ExpeditionDAO.getAnimalNest(row[2].trim());
					Position p = new Position(Integer.parseInt(row[0].trim()), Integer.parseInt(row[1].trim()));
					animalNests.add(new Pair<Position, AnimalNest>(p, n));
					line = r.readLine();
				}
			}
			catch (FileNotFoundException fnfe)
			{
				SworeGame.crash("Animal Nests File Not Found");
				return;
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static List<Pair<Position, BotanyCrop>> botanyCrops = new ArrayList<Pair<Position, BotanyCrop>>();

	/**
	 * adding additional crops to the world
	 * reading from botanyCrops.properties
	 * 
	 * not yet sure what that actually does
	 */
	public static void addBotanyCrops()
	{
		logger.info("Start: addBotanyCrops Arguments: ");
		try
		{
			// Read the botany crops
			BufferedReader r = FileUtil.getReader("scenarios/theNewWorld/botanyCrops.properties");
			String line = r.readLine();
			while (line != null)
			{
				String[] row = line.split(",");
				BotanyCrop c = ExpeditionDAO.getBotanyCrop(row[2].trim());
				Position p = new Position(Integer.parseInt(row[0].trim()), Integer.parseInt(row[1].trim()));
				botanyCrops.add(new Pair<Position, BotanyCrop>(p, c));
				line = r.readLine();
			}
		}
		catch (FileNotFoundException fnfe)
		{
			SworeGame.crash("Botany Crops File Not Found");
			return;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
