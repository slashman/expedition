package net.slashie.expedition.world;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.slashie.expedition.domain.GoodType;
import net.slashie.util.Pair;
import net.slashie.utils.Util;

@SuppressWarnings("serial")
public class Culture implements Serializable{
	private String code;
	private String name;
	private boolean isCivilization;
	private int aggresiveness;
	private List<Pair<Double, String>> classDistribution;
	private List<Pair<Double, String>> itemsDistribution;
	private List<Pair<GoodType, Double>> goodTypeValuationModifiers;
	private List<GoodType> mostValuedGoodTypes = new ArrayList<GoodType>();
	public List<Pair<GoodType, Double>> getGoodTypeValuationModifiers() {
		return goodTypeValuationModifiers;
	}

	private int goldModifier, artifactModifier, agricultureModifier;
	
	public Culture(String code, String name, boolean isCivilization, int aggresiveness, 
			List<Pair<Double, String>> classDistribution, 
			List<Pair<GoodType, Double>> goodTypeValuationModifiers,
			List<Pair<Double, String>> itemsDistribution,
			int goldModifier, int artifactModifier, int agricultureModifier) {
		super();
		this.code = code;
		this.name = name;
		this.isCivilization = isCivilization;
		this.aggresiveness = aggresiveness;
		this.classDistribution = classDistribution;
		this.goldModifier = goldModifier;
		this.artifactModifier = artifactModifier;
		this.agricultureModifier = agricultureModifier;
		this.goodTypeValuationModifiers = goodTypeValuationModifiers;
		this.itemsDistribution = itemsDistribution;
		for (Pair<GoodType, Double> goodTypeValuationModifier: goodTypeValuationModifiers){
			if (goodTypeValuationModifier.getB() > 1.0d){
				mostValuedGoodTypes.add(goodTypeValuationModifier.getA());
			}
		}
			
		
	}
	
	public String getCode() {
		return code;
	}
	public String getName() {
		return name;
	}
	public boolean isCivilization() {
		return isCivilization;
	}
	public int getAggresiveness() {
		return aggresiveness;
	}

	public List<Pair<Double, String>> getClassDistribution() {
		return classDistribution;
	}

	public int getASize() {
		if (isCivilization)
			return Util.rand(2, 5);
		else
			return Util.rand(1, 3);
	}

	public int getGoldModifier() {
		return goldModifier;
	}

	public int getArtifactModifier() {
		return artifactModifier;
	}

	public int getAgricultureModifier() {
		return agricultureModifier;
	}

	public List<Pair<Double, String>> getItemsDistribution() {
		return itemsDistribution;
	}

	public List<GoodType> getMostValuedGoodTypes() {
		return mostValuedGoodTypes;
	}
	


		
}
