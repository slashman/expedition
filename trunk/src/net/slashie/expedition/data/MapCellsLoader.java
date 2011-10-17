package net.slashie.expedition.data;

import java.awt.FontFormatException;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.slashie.expedition.world.ExpeditionCell;
import net.slashie.expedition.world.OverworldExpeditionCell;
import net.slashie.serf.game.SworeGame;
import net.slashie.serf.level.AbstractCell;
import net.slashie.serf.ui.oryxUI.AnimatedGFXAppearance;
import net.slashie.serf.ui.oryxUI.GFXAppearance;
import net.slashie.util.Pair;
import net.slashie.utils.ImageUtils;
import net.slashie.utils.Position;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MapCellsLoader {
	private static Map<String, Image> images = new Hashtable<String, Image>();
	
	public static AnimatedGFXAppearance createAnimatedAppearance(String filename, int width, int height, String ID, int delay, Position... positions){
		BufferedImage bigImage = (BufferedImage) images.get(filename);
		if (bigImage == null){
			try {
				bigImage = ImageUtils.createImage(filename);
			} catch (Exception e){
				SworeGame.crash("Error loading image "+filename, e);
			}
			images.put(filename, bigImage);
		}
		
		Image[] frames = new Image[positions.length];
		int i = 0;
		for (Position position: positions){
			int xpos = position.x - 1;
			int ypos = position.y - 1;
			frames[i] = ImageUtils.crearImagen(bigImage,  xpos*width, ypos*height, width, height);
			i++;
		}
		AnimatedGFXAppearance ret = new AnimatedGFXAppearance(ID, frames, 0,0, delay);
		
		return ret;
	}
	
	public static GFXAppearance createAppearance(String filename, int width, int height, String ID, int xpos, int ypos){
		xpos--;
		ypos--;
		BufferedImage bigImage = (BufferedImage) images.get(filename);
		if (bigImage == null){
			try {
				bigImage = ImageUtils.createImage(filename);
			} catch (Exception e){
				SworeGame.crash("Error loading image "+filename, e);
			}
			images.put(filename, bigImage);
		}
		try {
			BufferedImage img = ImageUtils.crearImagen(bigImage, xpos*width, ypos*height, width, height);
			GFXAppearance ret = new GFXAppearance(ID, img, 0,0);
			return ret;
		} catch (Exception e){
			SworeGame.crash("Error loading image "+filename, e);
		}
		return null;
	}
	
	public static GFXAppearance sumAppearances(String filename, int width, int height, String ID, int xpos1, int ypos1, int xpos2, int ypos2){
		xpos1--;
		ypos1--;
		xpos2--;
		ypos2--;
		BufferedImage bigImage = (BufferedImage) images.get(filename);
		if (bigImage == null){
			try {
				bigImage = ImageUtils.createImage(filename);
			} catch (Exception e){
				SworeGame.crash("Error loading image "+filename, e);
			}
			images.put(filename, bigImage);
		}
		try {
			BufferedImage img1 = ImageUtils.crearImagen(bigImage,  xpos1*width, ypos1*height, width, height);
			BufferedImage img2 = ImageUtils.crearImagen(bigImage,  xpos2*width, ypos2*height, width, height);
			BufferedImage imgSum = ImageUtils.overlay(img1, img2, 0, 0);
			GFXAppearance ret = new GFXAppearance(ID, imgSum, 0,0);
			return ret;
		} catch (Exception e){
			SworeGame.crash("Error loading image "+filename, e);
		}
		return null;
		
	}

	public static List<AbstractCell> getCells(String cellsXMLFile){
		List<AbstractCell> ret = new ArrayList<AbstractCell>();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document document = builder.parse(new File(cellsXMLFile));
			NodeList baseNodes = document.getChildNodes();
			for (int i = 0; i < baseNodes.getLength(); i++){
				Node baseNode = baseNodes.item(i);
				if (baseNode.getNodeType() == Node.ELEMENT_NODE){
					Element baseElement = (Element) baseNode;
					if (baseElement.getTagName().equals("cells")){
						processCells(baseElement, ret);
					}
				}
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (FontFormatException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	private static void processCells (Element cellsElement, List<AbstractCell> ret) throws IOException, FontFormatException{
		NodeList baseNodes = cellsElement.getChildNodes();
		for (int i = 0; i < baseNodes.getLength(); i++){
			Node baseNode = baseNodes.item(i);
			if (baseNode.getNodeType() == Node.ELEMENT_NODE){
				Element baseElement = (Element) baseNode;
				if (baseElement.getTagName().equals("overworldCell")){
					ret.add(processOverworldCell(baseElement));
				} else if (baseElement.getTagName().equals("zoomedCell")){
					ret.add(processZoomedCell(baseElement));
				} 
			}
		}
	}
	
	private static AbstractCell processZoomedCell(Element baseElement) {
		ExpeditionCell ret =new ExpeditionCell(
				baseElement.getAttribute("id"),
				baseElement.getAttribute("description"),
				baseElement.getAttribute("isSolid").equals("true"),
				baseElement.getAttribute("isOpaque").equals("true"),
				baseElement.getAttribute("isWater").equals("true"));
		ret.setStepCommand(baseElement.getAttribute("stepCommand"));
		return ret;
	}

	private static AbstractCell processOverworldCell(Element cellElement) {
		List<Pair<String, Integer>> resources = null;
		NodeList baseNodes = cellElement.getChildNodes();
		for (int i = 0; i < baseNodes.getLength(); i++){
			Node baseNode = baseNodes.item(i);
			if (baseNode.getNodeType() == Node.ELEMENT_NODE){
				Element baseElement = (Element) baseNode;
				if (baseElement.getTagName().equals("resources")){
					resources = processResources(baseElement);
				}			
			}
		}
		
		return new OverworldExpeditionCell (
			cellElement.getAttribute("id"),
			cellElement.getAttribute("description"),
			cellElement.getAttribute("isLand").equals("true"),
			Integer.parseInt(cellElement.getAttribute("height")),
			cellElement.getAttribute("isShallowWater").equals("true"),
			cellElement.getAttribute("isSolid").equals("true"),
			cellElement.getAttribute("isWood").equals("true"),
			cellElement.getAttribute("isOpaque").equals("true"),
			Integer.parseInt(cellElement.getAttribute("forageChance")),
			Integer.parseInt(cellElement.getAttribute("forageQuantity")),
			cellElement.getAttribute("isForest").equals("true"),
			cellElement.getAttribute("isDeepWater").equals("true"), 
			resources);

	}

	private static List<Pair<String, Integer>> processResources(Element resourcesElement) {
		List<Pair<String, Integer>> resources = new ArrayList<Pair<String,Integer>>();
		NodeList baseNodes = resourcesElement.getChildNodes();
		for (int i = 0; i < baseNodes.getLength(); i++){
			Node baseNode = baseNodes.item(i);
			if (baseNode.getNodeType() == Node.ELEMENT_NODE){
				Element baseElement = (Element) baseNode;
				if (baseElement.getTagName().equals("resource")){
					resources.add(new Pair<String, Integer>(baseElement.getAttribute("id"), Integer.parseInt(baseElement.getAttribute("quantity"))));
				}			
			}
		}
		return resources;
	}
}
