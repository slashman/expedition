package net.slashie.expedition.ui.oryx.sfx;

import java.awt.Image;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.slashie.serf.ui.oryxUI.SwingSystemInterface;
import net.slashie.utils.ImageUtils;

public class RainEffect implements Runnable{
	protected SwingSystemInterface si;
	protected BlockingQueue<String> commandsQueue = new LinkedBlockingQueue<String>();
	
	public RainEffect(SwingSystemInterface si, BlockingQueue<String> commandsQueue) {
		this.si = si;
		this.commandsQueue = commandsQueue;
		try {
			RAIN_IMAGE = ImageUtils.createImage("res/sfx/rain1.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Image RAIN_IMAGE;
	
	@Override
	public void run() {
		int currentFrame = 0;
		while (true){
			if (!commandsQueue.isEmpty()){
				String command = null;
				while (command == null){
					try {
						command = commandsQueue.take();
					} catch (InterruptedException e) {}
				}
				if (command.equals("KILL"))
					break;
			}
			currentFrame++;
			if (currentFrame == 5)
				currentFrame = 0;
			int offset = currentFrame * 3;
			synchronized (si) {
				si.cleanLayer(1);
				si.drawImage(1, offset, 0, RAIN_IMAGE);
				si.commitLayer(1);
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {}
		}
	}
}
