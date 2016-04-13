package net.ck.expedition.model.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.Logger;

public class ExpeditionProperties
{
	final static Logger logger = Logger.getRootLogger();
	private static Properties configuration;
	
	public static boolean isDebug()
	{
		configuration = new Properties();
		try
		{
			configuration.load(new FileInputStream("expedition.properties"));
		}
		catch (IOException e)
		{
			logger.fatal("Error loading configuration file, please confirm existence of expedition.properties");
			e.printStackTrace();
			System.exit(-1);
		}
		if (configuration.getProperty("debug").equalsIgnoreCase("true"))
		{
			return true;
		}
		return false;
	}
}
