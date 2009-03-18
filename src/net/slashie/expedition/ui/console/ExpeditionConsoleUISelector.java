package net.slashie.expedition.ui.console;

import net.slashie.expedition.domain.NonPrincipalExpedition;
import net.slashie.serf.action.Actor;
import net.slashie.serf.ui.consoleUI.ConsoleUISelector;

public class ExpeditionConsoleUISelector extends ConsoleUISelector{

	@Override
	public int onActorStumble(Actor actor) {
		if (actor instanceof NonPrincipalExpedition){
			NonPrincipalExpedition npe = (NonPrincipalExpedition)actor;
			if (npe.isHostile()){
				return 1;
			} else {
				return 2;
			}
		}
		return 0;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

}
