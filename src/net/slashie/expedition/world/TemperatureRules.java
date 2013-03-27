package net.slashie.expedition.world;

import java.util.HashMap;
import java.util.Map;

import net.slashie.expedition.game.ExpeditionGame;

public class TemperatureRules {
	private static Map<Integer, double[]> rulingTemperatures = new HashMap<Integer, double[]>();
	static {
		// Temperature Data in C
		double [][] temperatureData = new double [][]{
		{-10, -10, -10, -7.5, -5, -5, -5, -5, -5, -5, -5, -7.5},
		{-5, -5, -5, -2.5, 0, 2.5, 5, 2.5, 0, -2.5, -5, -5},
		{0, 2.5, 5, 7.5, 10, 12.5, 15, 12.5, 10, 7.5, 5, 2.5},
		{5, 7.5, 10, 12.5, 15, 17.5, 20, 17.5, 15, 12.5, 10, 7.5},
		{15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15},
		{15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15},
		{ 20, 17.5, 15, 12.5, 10, 7.5, 5, 7.5, 10, 12.5, 15, 17.5},
		{15, 12.5, 10, 7.5, 5, 2.5, 0, 2.5, 5, 7.5, 10, 12.5},
		{5, 2.5, 0, -2.5, -5, -5, -5, -5, -5, -2.5, 0, 2.5},
		{-5, -5, -5, -5, -5, -7.5, -10, -10, -10, -7.5, -5, -5}
		};
		int upperBound = 100;
		for (double[] latitudeData: temperatureData){
			rulingTemperatures.put(upperBound, latitudeData);
			upperBound -= 20;
		}
	}
	
	public static double getRulingTemperature(int lat, int month){
		int upperBound = 0;
		if (lat > 0)
			upperBound = (int) Math.ceil((double)lat / 20.0d) * 20;
		else
			upperBound = (int) Math.floor((double)lat / 20.0d) * 20;
		if (upperBound > 100)
			upperBound = 100;
		if (upperBound < -80)
			upperBound = -80;
		return rulingTemperatures.get(upperBound)[month-1];
	}
	
	public static String getTemperatureDescription(int temperatureC){
		if (temperatureC > 35)
			return "Very Hot!";
		else if (temperatureC > 30)
			return "Hot!";
		else if (temperatureC > 25)
			return "Very Warm";
		else if (temperatureC > 15)
			return "Warm";
		else if (temperatureC > 10)
			return "Fresh";
		else if (temperatureC > 5)
			return "Cold";
		else if (temperatureC > 0)
			return "Cold";
		else if (temperatureC > -5)
			return "Very Cold!";
		else 
			return "Freezing";
	}
	
	public static double getTemperatureFoodModifier(int temperatureC){
		if (temperatureC > 5)
			return 1;
		else if (temperatureC > -5)
			return 1.2;
		else 
			return 1.4;
	}
	
	public static String getTemperatureFoodModifierString(int temperatureC){
		if (temperatureC > 5)
			return "";
		else if (temperatureC > -5)
			return "(Cold)";
		else 
			return "(Very Cold)";
	}

	private static int[] itczLats = new int[]{-30,-30,-15,-5,5,15,30,30,15,5,-5,-15}; 
	public static int getITCZ(int month) {
		if (month < 0 || month  > 11 ){
			System.out.println("Invalid Month! "+ExpeditionGame.getCurrentGame().getGameTime().toString());
			return 0;
		}
		return itczLats[month];
	}
	
	public static double getTemperatureWaterModifier(int temperatureC){
		if (temperatureC > 5)
			return 1.2;
		else if (temperatureC > -5)
			return 1.4;
		else 
			return 1.6;
	}
}
