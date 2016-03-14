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
public class Plaza extends Building
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final static Logger logger = Logger.getRootLogger();
	private final static Map<StorageType, Integer> PLAZA_CAPABILITIES = new HashMap<StorageType, Integer>();
	static {
		PLAZA_CAPABILITIES.put(StorageType.WAREHOUSE, 50);
	}
	/**
	 * @param id
	 * @param description
	 * @param longDescription
	 * @param woodCost
	 * @param buildTimeCost
	 * @param populationCapacity
	 * @param minBuildDays
	 * @param storageCapacity
	 */
	public Plaza()
	{
		//super(id, description, longDescription, woodCost, buildTimeCost, populationCapacity, minBuildDays,storageCapacity);
		super("PLAZA", "Plaza", "Center of community life", 40, DayShiftAgent.TICKS_PER_DAY * 120, 0, 14, PLAZA_CAPABILITIES);
	}
}

