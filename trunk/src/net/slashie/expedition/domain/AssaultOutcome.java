package net.slashie.expedition.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.slashie.util.Pair;

public class AssaultOutcome {
	private List<Pair<ExpeditionUnit, Integer>> deaths = new ArrayList<Pair<ExpeditionUnit,Integer>>();
	private Map<ExpeditionUnit, Pair<ExpeditionUnit, Integer>> deathsMap =new HashMap<ExpeditionUnit, Pair<ExpeditionUnit,Integer>>();
	
	private List<Pair<ExpeditionUnit, Integer>> wounds = new ArrayList<Pair<ExpeditionUnit,Integer>>();
	private Map<ExpeditionUnit, Pair<ExpeditionUnit, Integer>> woundsMap =new HashMap<ExpeditionUnit, Pair<ExpeditionUnit,Integer>>();
	
	public void addDeath(ExpeditionUnit unit){
		Pair<ExpeditionUnit, Integer> death = deathsMap.get(unit);
		if (death == null){
			death = new Pair<ExpeditionUnit, Integer> (unit, 0);
			deaths.add(death);
			deathsMap.put(unit, death);
		}
		death.setB(death.getB()+1);
	}

	public void addWound(ExpeditionUnit unit) {
		Pair<ExpeditionUnit, Integer> wound = woundsMap.get(unit);
		if (wound == null){
			wound = new Pair<ExpeditionUnit, Integer> (unit, 0);
			wounds.add(wound);
			woundsMap.put(unit, wound);
		}
		wound.setB(wound.getB()+1);
	}

	public String getDeathsString() {
		Pair<String,Integer> killResume = getUnitsString(deaths);
		String killMessage = killResume.getA();
		if (killResume.getB() > 1)
			killMessage += " die.";
		else if (killResume.getB() == 1)
			killMessage +=" dies.";
		return killMessage;
	}
	
	public String getWoundsString() {
		Pair<String,Integer> killResume = getUnitsString(wounds);
		String killMessage = killResume.getA();
		if (killResume.getB() > 1)
			killMessage += " are wounded.";
		else if (killResume.getB() == 1)
			killMessage +=" is wounded.";
		return killMessage;
	}

	private Pair<String, Integer> getUnitsString(
			List<Pair<ExpeditionUnit, Integer>> list) {
		int i = 0;
		int deathCount = 0;
		String unitsString = "";
		for (Pair<ExpeditionUnit, Integer> killInfo: list){
			if (killInfo.getB() == 0){
				i++;
				continue;
			}
			if (killInfo.getB() == 1){
				unitsString += a(killInfo.getA().getFullDescription())+killInfo.getA().getFullDescription();
			} else
				unitsString += killInfo.getB()+" "+killInfo.getA().getPluralDescription();
			if (i == list.size()-2)
				unitsString += " and ";
			else if (i == list.size()-1)
				;
			else if (list.size()>1)
				unitsString += ", ";
			i++;
			deathCount += killInfo.getB();
		}
		return new Pair<String, Integer>(unitsString, deathCount);
	}

	private String a(String fullDescription) {
		if (fullDescription.startsWith("A") || 
				fullDescription.startsWith("E") ||
				fullDescription.startsWith("I") ||
				fullDescription.startsWith("O") ||
				fullDescription.startsWith("U")
				)
			return "An ";
		else
			return "A ";
	}

	public boolean hasDeaths() {
		return deaths.size() > 0;
	}
	
	public boolean hasWounds() {
		return deaths.size() > 0;
	}
	
	public boolean hasEvents (){
		return hasDeaths() || hasWounds();
	}
}
