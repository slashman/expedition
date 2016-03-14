package net.ck.expedition.utils.swing;
import java.util.Calendar;
import org.apache.log4j.Logger;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.ui.ExpeditionUserInterface;
import net.slashie.serf.action.Message;
import net.slashie.serf.ui.UserInterface;
import net.slashie.utils.Position;
public class MessengerService
{
	final static Logger logger = Logger.getRootLogger();

	public MessengerService()
	{
		// TODO Auto-generated constructor stub
	}
	
	
	public static void showImportantMessage(UserInterface userInterface, String string)
	{
		if (userInterface != null)
		{
			userInterface.showImportantMessage(string);
		}
		else
		{
			logger.debug(string);
		}
	}


	public static void showImportantMessage(String message)
	{
		if (UserInterface.getUI() != null)
		{
			UserInterface.getUI().showImportantMessage(message);
		}
		else
		{
			logger.debug(message);
		}
	}
	
	public static void addMessage(String message, Position position)
	{
		if (UserInterface.getUI() != null)
		{
			
			UserInterface.getUI().addMessage(new Message(message, position, formatTime(ExpeditionGame.getCurrentGame().getGameTime())));
		}
		else
		{
			logger.debug(message);
		}
	}
	
	private static String formatTime(Calendar gameTime)
	{
		return ExpeditionUserInterface.months[gameTime.get(Calendar.MONTH)] + " " + gameTime.get(Calendar.DATE) + ", "
				+ getTimeDescriptionFromHour(gameTime.get(Calendar.HOUR_OF_DAY));
	}
	
	public static String getTimeDescriptionFromHour(int i)
	{
		if (i > 22)
		{
			return "Midnight";
		}
		else if (i > 18)
		{
			return "Night";
		}
		else if (i > 14)
		{
			return "Afternoon";
		}
		else if (i > 10)
		{
			return "Noon";
		}
		else if (i > 6)
		{
			return "Morning";
		}
		else if (i > 4)
		{
			return "Dawn";
		}
		else
		{
			return "Midnight";
		}
	}


	public static void showBlockingMessage(String string)
	{		
		if (UserInterface.getUI() != null)
		{
			((ExpeditionUserInterface) UserInterface.getUI()).showBlockingMessage(string);
		}
		else
		{
			logger.debug(string);
		}
	}
	
	
}

