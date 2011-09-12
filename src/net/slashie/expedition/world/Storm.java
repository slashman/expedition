package net.slashie.expedition.world;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.slashie.expedition.level.GlobeMapModel;
import net.slashie.utils.Position;
import net.slashie.utils.Util;

public class Storm implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private Position position;
	private List<Position> stormlets = new ArrayList<Position>();
	private Map<Position, Position> stormletsMap = new HashMap<Position, Position>();
	private int usedFuel;

	public Storm(Position p) {
		this.position = p;
	}
	public Map<Position, Position> getStormlets() {
		return stormletsMap;
	}
	
	private void addStormletAt(Position p){
		if (stormletsMap.get(p) == null)
			stormlets.add(p);
		stormletsMap.put(p, p);
	}
	
	private void removeStormletAt(Position p){
		stormlets.remove(p);
		stormletsMap.put(p, null);
	}
	
	public void grow(){
		int xScale = GlobeMapModel.getSingleton().getLongitudeScale(position.y);
		int yScale = GlobeMapModel.getSingleton().getLatitudeHeight();
		usedFuel += 10;
		List<Position> newStormlets = new ArrayList<Position>();
		for (Position stormlet: stormlets){
			if (Util.chance(80)){
				newStormlets.add(new Position(stormlet.x-1*xScale,stormlet.y));
			}
			if (Util.chance(80)){
				newStormlets.add(new Position(stormlet.x+1*xScale,stormlet.y));
			}
			if (Util.chance(80)){
				newStormlets.add(new Position(stormlet.x,stormlet.y-1*yScale));
			}
			if (Util.chance(80)){
				newStormlets.add(new Position(stormlet.x,stormlet.y+1*yScale));
			}
		}
		for (Position stormlet: newStormlets ){
			addStormletAt(stormlet);
		}
	}
	
	public void shrink(){
		int xScale = GlobeMapModel.getSingleton().getLongitudeScale(position.y);
		int yScale = GlobeMapModel.getSingleton().getLatitudeHeight();
		List<Position> removableStormlets = new ArrayList<Position>();
		Position runner = new Position(0,0);
		for (Position stormlet: stormlets){
			int surrounding = 0;
			runner.x = stormlet.x+1*xScale;
			runner.y = stormlet.y;
			if (stormletsMap.get(runner) != null){
				surrounding++;
			}
			runner.x = stormlet.x-1*xScale;
			runner.y = stormlet.y;
			if (stormletsMap.get(runner) != null){
				surrounding++;
			}
			runner.x = stormlet.x;
			runner.y = stormlet.y+1*yScale;
			if (stormletsMap.get(runner) != null){
				surrounding++;
			}
			runner.x = stormlet.x;
			runner.y = stormlet.y-1*yScale;
			if (stormletsMap.get(runner) != null){
				surrounding++;
			}
			if (surrounding < 3){
				removableStormlets.add(stormlet);
			}
		}
		for (Position stormlet: removableStormlets){
			removeStormletAt(stormlet);
		}
	}

	private Position reusablePosition = new Position(0,0);
	public boolean hasStormlet(Position position) {
		reusablePosition.x = position.x - this.position.x();
		reusablePosition.y = position.y - this.position.y();
		return stormletsMap.get(reusablePosition) != null;
	}
	public void seed(int size, int mass) {
		for (int i = 0; i < mass; i++){
			addStormletAt(new Position(Util.rand(-size, size), Util.rand(-size, size)));
		}
	}
	public int getMass() {
		return stormlets.size();
	}
	
	public void evolve() {
		if (Util.chance(getGrowChance()))
			grow();
		else
			shrink();		
	}
	
	private int getGrowChance() {
		return 100-usedFuel;
	}
	
	public void move(CardinalDirection windDirection) {
		position.x += windDirection.getVectors().x;
		position.y += windDirection.getVectors().y;
	}
	
}
