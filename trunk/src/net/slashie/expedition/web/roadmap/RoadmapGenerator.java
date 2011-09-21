package net.slashie.expedition.web.roadmap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class RoadmapGenerator {
	static class Facet {
		String id;
		String name;
		List<FunctionalComponent> functionalComponents;
		int implementationOrder;
		public int getTotalComplexityPoints() {
			int ret = 0;
			for (FunctionalComponent functionalComponent : functionalComponents){
				ret += functionalComponent.complexity;
			}
			return ret;
		}
	}
	
	static class FunctionalComponent {
		String name;
		String description;
		int complexity;
	}
	
	public static void main(String... args){
		try {
			List<Facet> facets = parseXML("roadmap.xml");
			BufferedWriter br = new BufferedWriter(new FileWriter("roadmap.html"));
			writeHeader(br);
			writeFacets(br,facets);
			writeFooter(br);
			br.close();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static List<Facet> parseXML(String string) throws ParserConfigurationException, SAXException, IOException {
		List<Facet> facets = new ArrayList<Facet>();

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbf.newDocumentBuilder();
		Document document = builder.parse(new File("roadmap.xml"));
		NodeList baseNodes = document.getChildNodes();
		for (int i = 0; i < baseNodes.getLength(); i++){
			Node baseNode = baseNodes.item(i);
			if (baseNode.getNodeType() == Node.ELEMENT_NODE){
				Element baseElement = (Element) baseNode;
				if (baseElement.getTagName().equals("roadmap")){
					processRoadmap(baseElement, facets);
				}
			}
		}

		return facets;
	}

	private static void processRoadmap(Element roadmapElement, List<Facet> facets) {
		NodeList baseNodes = roadmapElement.getChildNodes();
		for (int i = 0; i < baseNodes.getLength(); i++){
			Node baseNode = baseNodes.item(i);
			if (baseNode.getNodeType() == Node.ELEMENT_NODE){
				Element baseElement = (Element) baseNode;
				if (baseElement.getTagName().equals("facet")){
					Facet f = new Facet();
					f.id = baseElement.getAttribute("id");
					f.name = baseElement.getAttribute("name");
					f.functionalComponents = new ArrayList<FunctionalComponent>();
					f.implementationOrder = Integer.parseInt(baseElement.getAttribute("implementationOrder"));
					processFunctionalComponents(baseElement, f.functionalComponents);
					facets.add(f);
				}
			}
		}
	}
	
	private static void processFunctionalComponents(Element facetElement, List<FunctionalComponent> functionalComponents) {
		NodeList baseNodes = facetElement.getChildNodes();
		for (int i = 0; i < baseNodes.getLength(); i++){
			Node baseNode = baseNodes.item(i);
			if (baseNode.getNodeType() == Node.ELEMENT_NODE){
				Element baseElement = (Element) baseNode;
				if (baseElement.getTagName().equals("functionalComponent")){
					FunctionalComponent f = new FunctionalComponent();
					f.name = baseElement.getAttribute("name");
					f.description = baseElement.getAttribute("description");
					f.complexity = Integer.parseInt(baseElement.getAttribute("complexity"));
					functionalComponents.add(f);
				}
			}
		}
	}

	private static void writeFacets(BufferedWriter br, List<Facet> facets) throws IOException {
		// Sort facets by implementationOrder
		Collections.sort(facets, new Comparator<Facet>(){
			@Override
			public int compare(Facet o1, Facet o2) {
				return o1.implementationOrder - o2.implementationOrder;
			}
		});
		for (Facet facet: facets){
			writeFacet(br, facet);
		}
	}
	
	
	private static void writeFacet(BufferedWriter br, Facet facet) throws IOException {
		// Open Casing
		br.write("\n<table class = \"facetTable\">");
		br.write("<tr><td class = \"nwCell\"/><td class = \"nCell\"/><td class = \"neCell\"/></tr>");
		br.write("<tr><td class = \"wCell\"/><td class = \"cCell\">");
		
		// Inner table
		br.write("<table class = \"innerTable\"><tr><td colspan = \"2\"><b>"+facet.name+" Facet</b> ("+facet.getTotalComplexityPoints()+" complexity points, Ranked #"+facet.implementationOrder+")</td></tr>");
		br.write("<tr><td><img src = \"img/"+facet.id+".png\"/></td><td>");
		br.write("<ul>");
		for (FunctionalComponent functionalComponent: facet.functionalComponents){
			br.write("<li><b>"+functionalComponent.name+"</b>: "+functionalComponent.description+". <b>"+functionalComponent.complexity+" complexity points</b>");
		}
		br.write("</ul>");
		br.write("</td>");
		br.write("</table>");
		
		// Close casing
		br.write("<td class = \"eCell\"/></tr>");
		br.write("<tr><td class = \"swCell\"/><td class = \"sCell\"/><td class = \"seCell\"/></tr>");
		br.write("</table>");
	}

	private static void writeFooter(BufferedWriter br) throws IOException {
		BufferedReader r = new BufferedReader(new FileReader("footer.html"));
		String line = r.readLine();
		while (line != null){
			br.write(line);
			line = r.readLine();
		}
	}

	private static void writeHeader(BufferedWriter br) throws IOException {
		BufferedReader r = new BufferedReader(new FileReader("header.html"));
		String line = r.readLine();
		while (line != null){
			br.write(line);
			line = r.readLine();
		}
	}
}
