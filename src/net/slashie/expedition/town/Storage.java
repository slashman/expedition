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
public class Storage extends Building
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final static Logger logger = Logger.getRootLogger();
	private final static Map<StorageType, Integer> STORAGE_CAPABILITIES = new HashMap<StorageType, Integer>();
	static {
		STORAGE_CAPABILITIES.put(StorageType.WAREHOUSE, 300);
	}

	
	public Storage()
	{
		//
		super("STORAGE", "Storage Tower", "Can hold 300 units of foraged food", 60, DayShiftAgent.TICKS_PER_DAY * 240, 0, 14, STORAGE_CAPABILITIES);
	}
}

