package net.slashie.expedition.world;

import java.util.HashMap;
import java.util.Map;

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
		if (temperatureC > 25)
			return "Very Hot!";
		else if (temperatureC > 20)
			return "Hot!";
		else if (temperatureC > 15)
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
}
