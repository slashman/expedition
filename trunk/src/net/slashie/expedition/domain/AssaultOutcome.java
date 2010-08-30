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
		Pair<ExpeditionUnit, Integer> wound = deathsMap.get(unit);
		if (wound == null){
			wound = new Pair<ExpeditionUnit, Integer> (unit, 0);
			wounds.add(wound);
			woundsMap.put(unit, wound);
		}
		wound.setB(wound.getB()+1);
	}

	public String getDeathsString() {
		String killMessage = "";
		int i = 0;
		int deathCount = 0;
		for (Pair<ExpeditionUnit, Integer> killInfo: deaths){
			if (killInfo.getB() == 0){
				i++;
				continue;
			}
			if (killInfo.getB() == 1){
				killMessage += a(killInfo.getA().getFullDescription())+killInfo.getA().getFullDescription();
			} else
				killMessage += killInfo.getB()+" "+killInfo.getA().getPluralDescription();
			if (i == deaths.size()-2)
				killMessage += " and ";
			else if (i == deaths.size()-1)
				;
			else if (deaths.size()>1)
				killMessage += ", ";
			i++;
			deathCount += killInfo.getB();
		}
		if (deathCount > 1)
			killMessage += " die.";
		else
			killMessage +=" dies.";
		return killMessage;
	}

	private String a(String fullDescription) {
		if (fullDescription.startsWith("a") || 
				fullDescription.startsWith("e") ||
				fullDescription.startsWith("i") ||
				fullDescription.startsWith("o") ||
				fullDescription.startsWith("u")
				)
			return "An ";
		else
			return "A ";
	}

	public boolean hasDeaths() {
		return deaths.size() > 0;
	}
}
