package net.slashie.expedition.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.slashie.serf.action.Message;
import net.slashie.util.Pair;
import net.slashie.utils.Util;

public enum Weather {
//                                 CLEAR,   CLOUDY,   RAIN,   STORM,   WINDY,   GALE_WIND,    FOG,   HURRICANE,   SNOW,   DUST_STORM
	   CLEAR(       "Clear",       50,      30,       0,      0,       20,      0,            0,     0,           0,      0 )   ,
	   CLOUDY(      "Cloudy",      20,      30,       30,     10,      10,      0,            25,    0,           40,     0 )   ,
	   RAIN(        "Rain",        5,       25,       60,     10,      0,       0,            80,    0,           80,     0 )   ,
	   STORM(       "Heavy Rain!", 0,       10,       25,     50,      10,      5,            0,     0,           0,      0 )   ,
	   WINDY(       "Windy",       15,      10,       0,      35,      35,      5,            0,     0,           0,      70)   ,
	   GALE_WIND(   "Gale Winds!", 0,       0,        0,      35,      10,      45,           0,     5,           0,      90)   ,
	   FOG(         "Fog",         0,       20,       20,     5,       5,       0,            50,    0,           50,     0 )   ,
	   HURRICANE(   "HURRICANE!",  0,       0,        10,     20,      0,       20,           0,     50,          0,      100),
	   SNOW(        "Snow",        5,       25,       10,     0,       0,       0,            0,     0,           60,     0 )   ,
	   DUST_STORM(  "Dust Storm",  0,       0,        0,      0,       50,      0,            0,     0,           0,      50 )
	   ;
	
	private int[] transitionsList;
	private Map<Weather, Integer> transitions;
	private List<Pair<Weather, Integer>> acumTransitions;
	private String description;
	Weather(String description, int... transitionsList){
		this.description = description;
		transitions = new HashMap<Weather, Integer>();
		acumTransitions = new ArrayList<Pair<Weather,Integer>>();
		this.transitionsList = transitionsList;
	}
	
	static {
		init();
	}
	static void init(){
		for (Weather weather: values()){
			int i = 0;
			int acum = 0;
			for (Integer transitionChance: weather.transitionsList){
				weather.transitions.put(values()[i], transitionChance);
				acum += transitionChance;
				weather.acumTransitions.add(new Pair<Weather, Integer>(values()[i], acum));
				i++;
			}
		}
	}
	
	public Map<Weather, Integer> getTransitions() {
		return transitions;
	}

	public int getTransitionChance(Weather weather) {
		Integer ret = transitions.get(weather);
		if (ret == null)
			return 0;
		else
			return ret.intValue();
	}

	public Weather nextWeather() {
		int pivot = Util.rand(1, 100) ;
		for (Pair<Weather, Integer> transition: acumTransitions){
			if (transition.getB() > pivot || transition.getB() >= 100) {
				return transition.getA();
			}
		}
		
		return this;
	}

	public String getDescription() {
		return description;
	}

	public String getChangeMessage(Weather from) {
		switch (this){
		case CLEAR:
			return "The sky clears";
		case CLOUDY:
			switch (from){
			case RAIN:
				return "The rain stops.";
			case STORM:
				return "The storm stops.";
			default:
				return "Clouds cover the sky";
			}
		case DUST_STORM:
			return "The sand rises fiercely!";
		case FOG:
			return "A thick fog layer covers your expedition";
		case GALE_WIND:
			return "Very strong winds blow!";
		case HURRICANE:
			return "The fury of the sea is unleashed!";
		case RAIN:
			return "Rain starts pouring";
		case SNOW:
			return "Snow falls over the land";
		case STORM:
			return "Dark, stormy clouds cover the sky";
		case WINDY:
			return "The wind blows strongly";
		}
		return "Nothing happens";
	}
	
}
