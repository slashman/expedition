package net.slashie.expedition.ui.oryx.sfx;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.slashie.expedition.ui.oryx.ExpeditionOryxUI;
import net.slashie.expedition.world.CardinalDirection;
import net.slashie.expedition.world.ExpeditionLevel;
import net.slashie.serf.ui.oryxUI.SwingSystemInterface;
import net.slashie.utils.Position;
import net.slashie.utils.Util;

public class RainEffect implements Runnable{
	private static final int RAIN_SIZE = 2;
	protected SwingSystemInterface si;
	protected BlockingQueue<String> commandsQueue = new LinkedBlockingQueue<String>();
	
	public RainEffect(SwingSystemInterface si, BlockingQueue<String> commandsQueue, int minSize, int maxSize, int deadSize, int maxRainlets, int rainSpeed, Color rainColor, ExpeditionLevel level) {
		this.si = si;
		this.commandsQueue = commandsQueue;
		this.minSize = minSize;
		this.maxSize = maxSize;
		this.deadSize = deadSize;
		this.maxRainlets = maxRainlets;
		this.rainColor = rainColor;
		this.rainSpeed = rainSpeed;
		this.level=level; 
	}

	private int deadSize;
	private int maxRainlets;
	private int minSize;
	private int maxSize;
	private int rainSpeed;
	private Color rainColor;
	private ExpeditionLevel level;
	
	enum FallDirection {
		LEFT_TO_RIGHT,
		RIGHT_TO_LEFT,
		UP_TO_DOWN;

		public static FallDirection fromCardinalDirection(CardinalDirection windDirection) {
			switch (windDirection){
			case NORTHWEST:
			case SOUTHWEST:
			case WEST:
				return LEFT_TO_RIGHT;
			case NORTHEAST:
			case SOUTHEAST:
			case EAST:
				return RIGHT_TO_LEFT;
			case NORTH:
			case SOUTH:
			case NULL:
				return UP_TO_DOWN;
			}
			return null;
		}

		public int xOffset() {
			switch (this){
			case LEFT_TO_RIGHT:
				return 1;
			case RIGHT_TO_LEFT:
				return -1;
			case UP_TO_DOWN:
				return 0;
			}
			return 0;
		}
	}
	
	class Rainlet {
		Position position;
		int size;
		int fall;
		int speed;
		boolean dead;
		Color color;
		//Determines the angle of the raindrop
		private int tearSize;
		
		private int initialSize;
		
		
		Rainlet(Position position, int size, int speed){
			this.position = position;
			this.size = size;
			initialSize = size;
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

		public void resurrect() {
			size = initialSize;
			fall = 0;
			dead = false;
		}
	}
	@Override
	public void run() {
		Rainlet[] rainlets = new Rainlet[maxRainlets];
		//int sleep = (int)Math.round((double)si.getFrameRate() * (5.0d/4.0d));
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
					si.commitLayer(ExpeditionOryxUI.SFX_LAYER, true);

					break;
				}
			}
			
			// Create new rainlets
			
			for (int i = 0; i < maxRainlets; i++){
				if (rainlets[i] == null){
					rainlets[i] = new Rainlet(new Position(Util.rand(0, si.getScreenWidth()), Util.rand(0, si.getScreenHeight())), Util.rand(minSize, maxSize), Util.rand(rainSpeed, rainSpeed+2));
					rainlets[i].color = rainColors[currentRainColor];
					currentRainColor++;
					if (currentRainColor > 4)
						currentRainColor = 0;
				} else if (rainlets[i].dead) {
					rainlets[i].position.x = Util.rand(0, si.getScreenWidth());
					rainlets[i].position.y = Util.rand(0, si.getScreenHeight());
					rainlets[i].size = rainlets[i].initialSize;
					//rainlets[i].speed = Util.rand(rainSpeed, rainSpeed+2);
					rainlets[i].dead = false;
					rainlets[i].fall = 0;
					//rainlets[i].resurrect();
				} else {
					rainlets[i].evolve();
				}
			}
			

			si.cleanLayer(ExpeditionOryxUI.SFX_LAYER);
			Graphics2D g = si.getDrawingGraphics(ExpeditionOryxUI.SFX_LAYER);
			
		
			// Check the wind direction
			FallDirection fallDirection = FallDirection.fromCardinalDirection(level.getWindDirection());

			// Draw rainlets
			
			for (Rainlet rainlet: rainlets){
				g.setColor(rainlet.color);
				int xOffset = 0;
				for (int i = 0; i < rainlet.size; i++){
					int xPosition =  rainlet.position.x - rainlet.fall * (rainlet.speed*RAIN_SIZE) * fallDirection.xOffset() + xOffset * RAIN_SIZE;
					int yPosition =  rainlet.position.y + rainlet.fall * (rainlet.speed*RAIN_SIZE) - i * RAIN_SIZE; 
					g.fillRect(xPosition, yPosition, RAIN_SIZE, RAIN_SIZE);
					if ( (i+1) % rainlet.tearSize == 0){
						xOffset += fallDirection.xOffset();
					}
				}
			}
			si.commitLayer(ExpeditionOryxUI.SFX_LAYER, true);
			
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {}
		}
	}
}
