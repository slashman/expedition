package net.slashie.expedition.ui.oryx.sfx;

import java.awt.Color;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.slashie.serf.ui.oryxUI.SwingSystemInterface;

public class EffectsServer implements Runnable{
	protected BlockingQueue<String> commandsQueue = new LinkedBlockingQueue<String>();
	protected BlockingQueue<String> currentEffectQueue;
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
			if (cmd[0].equals("KILL")){
				stopEffect();
				break;
			} else if (cmd[0].equals("STOP")){
				stopEffect();
			} else if (cmd[0].equals("RAIN")){
				rain(cmd);
			}
		}
	}
	
	
	private void stopEffect() {
		if (currentEffectQueue != null){
			try {
				currentEffectQueue.put("KILL");
			} catch (InterruptedException e) {}
		}
	}
	
	public void rain(String[] cmd){
		stopEffect();
		currentEffectQueue = new LinkedBlockingQueue<String>();
		int minSize = Integer.parseInt(cmd[1]);
		int maxSize = Integer.parseInt(cmd[2]);
		int deadSize = Integer.parseInt(cmd[3]);
		int maxRainlets = Integer.parseInt(cmd[4]);
		int rainSpeed = Integer.parseInt(cmd[5]);
		String rainColorStr = cmd[6];
		Color rainColor = null;
		if (rainColorStr.equals("DARK"))
			rainColor = new Color(150,150,200);
		//rainColor = Color.GRAY;
		Runnable currentEffect = new RainEffect(si, currentEffectQueue, minSize, maxSize, deadSize, maxRainlets, rainSpeed, rainColor);
		Thread effectThread = new Thread(currentEffect);
		effectThread.setPriority(Thread.MIN_PRIORITY);
		effectThread.start();
	}
	
	
}
