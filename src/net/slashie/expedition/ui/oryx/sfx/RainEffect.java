package net.slashie.expedition.ui.oryx.sfx;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.slashie.expedition.ui.oryx.ExpeditionOryxUI;
import net.slashie.serf.ui.oryxUI.SwingSystemInterface;
import net.slashie.utils.ImageUtils;
import net.slashie.utils.Position;
import net.slashie.utils.Util;

public class RainEffect implements Runnable{
	protected SwingSystemInterface si;
	protected BlockingQueue<String> commandsQueue = new LinkedBlockingQueue<String>();
	
	public RainEffect(SwingSystemInterface si, BlockingQueue<String> commandsQueue, int minSize, int maxSize, int deadSize, int maxRainlets, Color rainColor) {
		this.si = si;
		this.commandsQueue = commandsQueue;
		this.minSize = minSize;
		this.maxSize = maxSize;
		this.deadSize = deadSize;
		this.maxRainlets = maxRainlets;
		this.rainColor = rainColor;
	}

	private int deadSize;
	private int maxRainlets;
	private int minSize;
	private int maxSize;
	private Color rainColor;
	
	class Rainlet {
		Position position;
		int size;
		int fall;
		
		Rainlet(Position position, int size){
			this.position = position;
			this.size = size;
		}
		
		boolean evolve(){
			fall++;
			if (size < deadSize){
				return true; // die
			} else {
				size --;
				return false;
			}
		}
	}
	@Override
	public void run() {
		List<Rainlet> rainlets = new ArrayList<Rainlet>();
		while (true){
			if (!commandsQueue.isEmpty()){
				String command = null;
				while (command == null){
					try {
						command = commandsQueue.take();
					} catch (InterruptedException e) {}
				}
				if (command.equals("KILL")){
					si.cleanLayer(ExpeditionOryxUI.SFX_LAYER);
					si.commitLayer(ExpeditionOryxUI.SFX_LAYER);

					break;
				}
			}
			
			// Create new rainlets
			if (rainlets.size() < maxRainlets){
				int newRainlets = maxRainlets - rainlets.size();
				for (int i = 0; i < newRainlets; i++){
					rainlets.add(new Rainlet(new Position(Util.rand(0, 800), Util.rand(0, 600)), Util.rand(minSize, maxSize)));
				}
			}
			
			// Evolve rainlets				
			for (int i = 0; i < rainlets.size(); i++){
				Rainlet rainlet = rainlets.get(i);
				boolean dead = rainlet.evolve();
				if (dead){
					rainlets.remove(i);
					i--;
				}
			}
			synchronized (si) {
				si.cleanLayer(ExpeditionOryxUI.SFX_LAYER);
				Graphics2D g = si.getDrawingGraphics(ExpeditionOryxUI.SFX_LAYER);
				g.setColor(rainColor);
				// Draw rainlets				
				for (Rainlet rainlet: rainlets){
					for (int i = 0; i < rainlet.size; i++){
						g.fillRect(-rainlet.fall+rainlet.position.x + i*2, rainlet.fall+rainlet.position.y-i*2, 2, 2);
					}
				}
				
				si.commitLayer(ExpeditionOryxUI.SFX_LAYER);
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {}
		}
	}
}
