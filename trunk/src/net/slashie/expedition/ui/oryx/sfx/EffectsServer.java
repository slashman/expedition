package net.slashie.expedition.ui.oryx.sfx;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.slashie.serf.ui.oryxUI.SwingSystemInterface;

public class EffectsServer implements Runnable{
	protected BlockingQueue<String> commandsQueue = new LinkedBlockingQueue<String>();
	protected BlockingQueue<String> sfxQueue = new LinkedBlockingQueue<String>();
	protected SwingSystemInterface si;
	public EffectsServer(SwingSystemInterface si, BlockingQueue<String> commandsQueue) {
		this.si = si;
		this.commandsQueue = commandsQueue;
	}
	@Override
	public void run() {
		while (true) {
			String command = null;
			while (command == null){
				try {
					command = commandsQueue.take();
				} catch (InterruptedException e) {}
			}
			
			String[] cmd = command.split(" ");
			if (cmd[0].equals("KILL"))
				break;
			doCommand(cmd);
		}
	}
	
	private Runnable currentEffect = null;
	
	public void doCommand(String[] cmd){
		if (cmd[0].equals("RAIN")){
			// stopEffect();
			currentEffect = new RainEffect(si, sfxQueue);
			new Thread(currentEffect).start();
		}
	}
	
	private void stopEffect() {
		if (currentEffect != null){
			try {
				sfxQueue.put("KILL");
			} catch (InterruptedException e) {}
		}
	}	
}
