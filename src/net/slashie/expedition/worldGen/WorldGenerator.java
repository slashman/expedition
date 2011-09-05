package net.slashie.expedition.worldGen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import net.slashie.expedition.data.ExpeditionDAO;
import net.slashie.expedition.domain.NativeTown;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.level.GlobeMapModel;
import net.slashie.expedition.world.Culture;
import net.slashie.expedition.world.ExpeditionMacroLevel;
import net.slashie.expedition.world.OverworldExpeditionCell;
import net.slashie.expedition.world.Settlement;
import net.slashie.serf.game.SworeGame;
import net.slashie.util.FileUtil;
import net.slashie.util.Pair;
import net.slashie.utils.Position;
import net.slashie.utils.Util;

public class WorldGenerator {
	public static void addNativeSettlements(ExpeditionMacroLevel level){
		try {
			List<Pair<Position, Culture>> cultureCenters = new ArrayList<Pair<Position,Culture>>();
			//Get the culture centers
			try {
				BufferedReader r = FileUtil.getReader("scenarios/theNewWorld/culture.properties");
				String line = r.readLine();
				while (line != null){
					String[] row = line.split(",");
					Culture c = ExpeditionDAO.getCulture(row[2]);
					Position p = new Position(Integer.parseInt(row[0]),Integer.parseInt(row[1]));
					// Translate position to lat-long
					GlobeMapModel.transformIntoLatLong(p);
					cultureCenters.add(new Pair<Position, Culture>(p,c));
					line = r.readLine();
				}
			} catch (FileNotFoundException fnfe){
				SworeGame.crash("Culture File Not Found");
				return;
			}
			
			//Create settlements around each culture center
			for (Pair<Position,Culture> cultureCenter: cultureCenters){
				int numberOfSettlements = 0;
				int range = 15;
				numberOfSettlements = Util.rand(3, 6);
				int fussible = 0;
				for (int i = 0; i < numberOfSettlements; i++){
					Position settlementPosition = new Position(Util.rand(cultureCenter.getA().x-range, cultureCenter.getA().x+range), Util.rand(cultureCenter.getA().y-range, cultureCenter.getA().y+range));
					settlementPosition.x = GlobeMapModel.normalizeLong(settlementPosition.y, settlementPosition.x);
					settlementPosition.y = GlobeMapModel.normalizeLat(settlementPosition.y);
					OverworldExpeditionCell cell = (OverworldExpeditionCell) level.getMapCell(settlementPosition);
					
					//Check if this is land
					if ( cell != null && !cell.isRiver() && cell.isLand() && level.getFeaturesAt(settlementPosition) == null){
						//Place a settlement
						NativeTown t = new NativeTown(ExpeditionGame.getCurrentGame(), cultureCenter.getB(), cultureCenter.getB().getASize());
						t.setPosition(new Position(settlementPosition));
						t.setUnfriendly(Util.chance(30*cultureCenter.getB().getAggresiveness()));
						level.addFeature(t);
					} else {
						fussible++;
						if (fussible < 1000){
							i--;
							continue;
						}
					}
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static class GenerationContext {
		int year;
		ExpeditionMacroLevel level;
	}
	

	public static void addCities(ExpeditionMacroLevel ret) {
		GenerationContext context = new GenerationContext();
		context.year = 1492;
		context.level = ret;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document document = builder.parse(new File("scenarios/theNewWorld/settlements.xml"));
			NodeList baseNodes = document.getChildNodes();
			for (int i = 0; i < baseNodes.getLength(); i++){
				Node baseNode = baseNodes.item(i);
				if (baseNode.getNodeType() == Node.ELEMENT_NODE){
					Element baseElement = (Element) baseNode;
					if (baseElement.getTagName().equals("geopolitical")){
						processGeopolitical(baseElement, context);
					}
				}
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private static void processGeopolitical(Element geoPoliticalElement, GenerationContext context) {
		NodeList baseNodes = geoPoliticalElement.getChildNodes();
		for (int i = 0; i < baseNodes.getLength(); i++){
			Node baseNode = baseNodes.item(i);
			if (baseNode.getNodeType() == Node.ELEMENT_NODE){
				Element baseElement = (Element) baseNode;
				if (baseElement.getTagName().equals("settlements")){
					processSettlements(baseElement, context);
				}
			}
		}
	}


	private static void processSettlements(Element settlementsElement, GenerationContext context) {
		NodeList baseNodes = settlementsElement.getChildNodes();
		for (int i = 0; i < baseNodes.getLength(); i++){
			Node baseNode = baseNodes.item(i);
			if (baseNode.getNodeType() == Node.ELEMENT_NODE){
				Element baseElement = (Element) baseNode;
				if (baseElement.getTagName().equals("settlement")){
					processSettlement(baseElement, context);
				}
			}
		}
	}

	/**
	 * Add a settlement to the world, based on its location.
	 * Also set the cell to port city?
	 * @param settlementElement
	 */
	private static void processSettlement(Element settlementElement, GenerationContext context) {
		// Check if the settlement existed in that year
		NodeList baseNodes = settlementElement.getChildNodes();
		for (int i = 0; i < baseNodes.getLength(); i++){
			Node baseNode = baseNodes.item(i);
			if (baseNode.getNodeType() == Node.ELEMENT_NODE){
				Element baseElement = (Element) baseNode;
				if (baseElement.getTagName().equals("history")){
					processSettlementHistory(settlementElement, baseElement, context);
				}
			}
		}
	}


	private static void processSettlementHistory(Element settlementElement, Element historyElement, GenerationContext context) {
		boolean firstEvent = true;
		NodeList baseNodes = historyElement.getChildNodes();
		for (int i = 0; i < baseNodes.getLength(); i++){
			Node baseNode = baseNodes.item(i);
			if (baseNode.getNodeType() == Node.ELEMENT_NODE){
				Element baseElement = (Element) baseNode;
				if (baseElement.getTagName().equals("event")){
					Element eventElement = (Element) baseNode;
					if (firstEvent){
						int eventYear = Integer.parseInt(eventElement.getAttribute("date"));
						if (eventYear <= context.year){
							createSettlement(settlementElement, eventElement.getAttribute("name"), context);
						}
						firstEvent = false;
					}
				}
			}
		}
	}


	private static void createSettlement(Element settlementElement, String settlementName, GenerationContext context) {
		String latStr = settlementElement.getAttribute("lat");
		String longStr = settlementElement.getAttribute("long");
		String settlementId = settlementElement.getAttribute("id");
		int lat = parseLatitude(latStr);
		int longi = parseLongitude(longStr);
		
		// Create the settlement feature
		Settlement s = new Settlement(settlementName, "TOWN");
		s.setPosition(longi, lat, 0);
		context.level.addFeature(s);
		
		// Add an exit
		context.level.addExit(new Position(longi, lat, 0), settlementId);
		
	}


	private static int parseLongitude(String longStr) {
		int degrees = Integer.parseInt(longStr.substring(0, longStr.indexOf('�')));
		int minutes = Integer.parseInt(longStr.substring(longStr.indexOf('�')+1, longStr.indexOf('\'')));
		boolean west = longStr.substring(longStr.indexOf('\'')+1).equals("W");
		return (degrees * 60 + minutes) * (west ? -1 : 1);
	}


	private static int parseLatitude(String latStr) {
		int degrees = Integer.parseInt(latStr.substring(0, latStr.indexOf("�")));
		int minutes = Integer.parseInt(latStr.substring(latStr.indexOf("�")+1, latStr.indexOf("'")));
		boolean north = latStr.substring(latStr.indexOf("'")+1).equals("N");
		return (degrees * 60 + minutes )* (north ? 1 : -1);
	}
}
