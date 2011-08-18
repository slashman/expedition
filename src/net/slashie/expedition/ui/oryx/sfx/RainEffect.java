package net.slashie.expedition.ui.oryx.sfx;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.slashie.expedition.ui.oryx.ExpeditionOryxUI;
import net.slashie.serf.ui.oryxUI.SwingSystemInterface;
import net.slashie.utils.Position;
import net.slashie.utils.Util;

public class RainEffect implements Runnable{
	private static final int RAIN_SIZE = 2;
	protected SwingSystemInterface si;
	protected BlockingQueue<String> commandsQueue = new LinkedBlockingQueue<String>();
	
	public RainEffect(SwingSystemInterface si, BlockingQueue<String> commandsQueue, int minSize, int maxSize, int deadSize, int maxRainlets, int rainSpeed, Color rainColor) {
		this.si = si;
		this.commandsQueue = commandsQueue;
		this.minSize = minSize;
		this.maxSize = maxSize;
		this.deadSize = deadSize;
		this.maxRainlets = maxRainlets;
		this.rainColor = rainColor;
		this.rainSpeed = rainSpeed;
	}

	private int deadSize;
	private int maxRainlets;
	private int minSize;
	private int maxSize;
	private int rainSpeed;
	private Color rainColor;
	
	class Rainlet {
		Position position;
		int size;
		int fall;
		int speed;
		boolean dead;
		Color color;
		//Determines the angle of the raindrop
		private int tearSize;
		
		
		Rainlet(Position position, int size, int speed){
			this.position = position;
			this.size = size;
			this.speed = speed;
			this.tearSize = Util.chance(85) ? 1 : 2;
			if (this.tearSize > 1)
				this.speed = (int)(speed / 2.0d);
		}
		
		void evolve(){
			fall++;
			if (size < deadSize){
				dead = true;
			} else {
				size --;
			}
		}
	}
	@Override
	public void run() {
		Rainlet[] rainlets = new Rainlet[maxRainlets];
		int sleep = (int)Math.round((double)si.getFrameRate() * (5.0d/4.0d));
		Color[] rainColors = new Color[5];
		for (int i = 0; i < 5; i++){
			int variation = Util.rand(-20, 20);
			rainColors[i] = new Color( rainColor.getRed() + variation, rainColor.getGreen() + variation, rainColor.getBlue() + variation);
		}
		int currentRainColor = 0;
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
			
			for (int i = 0; i < maxRainlets; i++){
				if (rainlets[i] == null){
					rainlets[i] = new Rainlet(new Position(Util.rand(0, 800), Util.rand(0, 600)), Util.rand(minSize, maxSize), Util.rand(rainSpeed, rainSpeed+2));
					rainlets[i].color = rainColors[currentRainColor];
					currentRainColor++;
					if (currentRainColor > 4)
						currentRainColor = 0;
				} else if (rainlets[i].dead) {
					rainlets[i].position.x = Util.rand(0, 800);
					rainlets[i].position.y = Util.rand(0, 600);
					rainlets[i].size = Util.rand(minSize, maxSize);
					rainlets[i].speed = Util.rand(rainSpeed, rainSpeed+2);
					rainlets[i].fall = 0;
					rainlets[i].dead = false;
				} else {
					rainlets[i].evolve();
				}
			}
			

			si.cleanLayer(ExpeditionOryxUI.SFX_LAYER);
			Graphics2D g = si.getDrawingGraphics(ExpeditionOryxUI.SFX_LAYER);
			
			// Draw rainlets				
			for (Rainlet rainlet: rainlets){
				g.setColor(rainlet.color);
				int xOffset = 0;
				for (int i = 0; i < rainlet.size; i++){
					int xPosition =  rainlet.position.x - rainlet.fall*(rainlet.speed*RAIN_SIZE) + xOffset * RAIN_SIZE;
					int yPosition =  rainlet.position.y + rainlet.fall*(rainlet.speed*RAIN_SIZE) - i * RAIN_SIZE; 
					g.fillRect(xPosition, yPosition, RAIN_SIZE, RAIN_SIZE);
					if ( (i+1) % rainlet.tearSize == 0){
						xOffset++;
					}
				}
			}
			si.commitLayer(ExpeditionOryxUI.SFX_LAYER);
			
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {}
		}
	}
}
