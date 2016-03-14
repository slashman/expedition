package net.slashie.expedition.domain;
import java.util.List;
import org.apache.log4j.Logger;
import net.slashie.expedition.world.ExpeditionMacroLevel;
import net.slashie.expedition.world.OverworldExpeditionCell;
import net.slashie.serf.level.AbstractLevel;
import net.slashie.util.Pair;
import net.slashie.utils.Position;

/**
 * 
 * @author Claus
 * the big fat cross of the town,
 * currently only the fields directly adjecent to the town.
 * these are the tiles that can be worked on.
 */
public class BFC
{
	final static Logger logger = Logger.getRootLogger();
	private Town town;

	public BFC(Town t)
	{
		setTown(t);
		computeCross(t.getPosition(), t.getLevel());
	}

	private void computeCross(Position position, AbstractLevel level)
	{		
		List<Pair<Position,OverworldExpeditionCell>> cellsAround = ((ExpeditionMacroLevel) level).getMapCellsAndPositionsAround(position);
		logger.debug("position of the town: " + position);
		for (Pair<Position,OverworldExpeditionCell> cell: cellsAround)
		{
			logger.debug("cell position: " + cell.getA().toString() + " cell type: " +  cell.getB().getDescription());
		}
	}

	public Town getTown()
	{
		return town;
	}

	public void setTown(Town town)
	{
		this.town = town;
	}
	
	
}

