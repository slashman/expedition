package net.slashie.expedition.action;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.world.ExpeditionMacroLevel;
import net.slashie.serf.action.Action;
import net.slashie.serf.action.Actor;

public class ForageFood extends Action
{
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void execute()
	{
		Expedition expedition = (Expedition) performer;
		expedition.forageFood();
	}

	public void execute(int i)
	{
		Expedition expedition = (Expedition) performer;
		expedition.forageFood(i);
	}

	private String invalidationMessage;

	@Override
	public boolean canPerform(Actor a)
	{
		performer = a;
		if (!(performer.getLevel() instanceof ExpeditionMacroLevel))
		{
			invalidationMessage = "You can't forage here";
			return false;
		}
		if (!((Expedition) a).forageFood())
		{
			a.getLevel().addMessage("You find nothing");
		}
		return true;
	}

	@Override
	public String getInvalidationMessage()
	{
		return invalidationMessage;
	}

	@Override
	public String getID()
	{
		return "ForageFood";
	}

	@Override
	public int getCost()
	{
		return 120;
	}

}
