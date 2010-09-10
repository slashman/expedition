package net.slashie.expedition.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.slashie.serf.text.EnglishGrammar;
import net.slashie.util.Pair;

public class AssaultOutcome implements Serializable{
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
		Pair<String,Integer> killResume = ExpeditionUnit.getUnitsString(deaths);
		String killMessage = killResume.getA();
		if (killResume.getB() > 1)
			killMessage += " die.";
		else if (killResume.getB() == 1)
			killMessage +=" dies.";
		return killMessage;
	}
	
	public String getWoundsString() {
		Pair<String,Integer> killResume = ExpeditionUnit.getUnitsString(wounds);
		String killMessage = killResume.getA();
		if (killResume.getB() > 1)
			killMessage += " are wounded.";
		else if (killResume.getB() == 1)
			killMessage +=" is wounded.";
		return killMessage;
	}

	

	public boolean hasDeaths() {
		return deaths.size() > 0;
	}
	
	public boolean hasWounds() {
		return wounds.size() > 0;
	}
	
	public boolean hasEvents (){
		return hasDeaths() || hasWounds();
	}

	
	public List<Pair<ExpeditionUnit, Integer>> getDeaths() {
		return deaths;
	}

	public List<Pair<ExpeditionUnit, Integer>> getWounds() {
		return wounds;
	}
}
