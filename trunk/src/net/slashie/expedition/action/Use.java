package net.slashie.expedition.action;

import net.slashie.serf.action.Action;
import net.slashie.serf.action.Message;
import net.slashie.serf.ui.UserInterface;
import net.slashie.utils.Position;

public class Use extends Action{

	@Override
	public void execute() {
		UserInterface.getUI().addMessage(new Message("Hi there", new Position(25,9)));

	}

	@Override
	public String getID() {
		return "USE";
	}

}
