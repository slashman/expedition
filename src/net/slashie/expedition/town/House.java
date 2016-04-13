/**
 * 
 */
package net.slashie.expedition.town;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import net.slashie.expedition.item.StorageType;
import net.slashie.expedition.world.agents.DayShiftAgent;
/**
 * @author Claus
 *
 */
public class House extends Building
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4411866466146665171L;
	final static Logger logger = Logger.getRootLogger();
	private final static Map<StorageType, Integer> NO_CAPABILITIES = new HashMap<StorageType, Integer>();

	public House()
	{		
		super("HOUSE", "House", "Simple wooden house, can hold 10 persons", 40, DayShiftAgent.TICKS_PER_DAY * 90, 10, 7, NO_CAPABILITIES);
	}
}

