package net.slashie.expedition.world;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.slashie.expedition.level.GlobeMapModel;
import net.slashie.utils.Position;
import net.slashie.utils.Util;

@SuppressWarnings("serial")
public class Storm implements Serializable{
	private Position position;
	private List<Position> stormlets = new ArrayList<Position>();
	private Map<Position, Position> stormletsMap = new HashMap<Position, Position>();
	private int usedFuel;

	public Storm(Position p) {
		p.y = GlobeMapModel.getSingleton().normalizeLat(p.y);
		p.x = GlobeMapModel.getSingleton().normalizeLong(p.y, p.x);
		this.position = p;
	}
	
	public Map<Position, Position> getStormlets() {
		return stormletsMap;
	}
	
	private void addStormletAt(Position relativePosition){
		if (stormletsMap.get(relativePosition) == null)
			stormlets.add(relativePosition);
		stormletsMap.put(relativePosition, relativePosition);
	}
	
	private void removeStormletAt(Position relativePosition){
		stormlets.remove(relativePosition);
		stormletsMap.put(relativePosition, null);
	}
	
	private Position relativePosition = new Position(0,0);
	public boolean hasStormlet(Position absolutePosition) {
		// Compare absolute position with position
		relativePosition.x = absolutePosition.x - this.position.x();
		relativePosition.y = absolutePosition.y - this.position.y();
		int xScale = GlobeMapModel.getSingleton().getLongitudeScale(absolutePosition.y);
		int yScale = GlobeMapModel.getSingleton().getLatitudeHeight();
		relativePosition.x /= xScale;
		relativePosition.y /= yScale;
		return stormletsMap.get(relativePosition) != null;
	}
	
	public void grow(){
		usedFuel += 10;
		List<Position> newStormlets = new ArrayList<Position>();
		for (Position stormlet: stormlets){
			if (Util.chance(80)){
				newStormlets.add(new Position(stormlet.x-1,stormlet.y));
			}
			if (Util.chance(80)){
				newStormlets.add(new Position(stormlet.x+1,stormlet.y));
			}
			if (Util.chance(80)){
				newStormlets.add(new Position(stormlet.x,stormlet.y-1));
			}
			if (Util.chance(80)){
				newStormlets.add(new Position(stormlet.x,stormlet.y+1));
			}
		}
		for (Position stormlet: newStormlets ){
			addStormletAt(stormlet);
		}
	}
	
	public void shrink(){
		List<Position> removableStormlets = new ArrayList<Position>();
		Position runner = new Position(0,0);
		for (Position stormlet: stormlets){
			int surrounding = 0;
			runner.x = stormlet.x+1;
			runner.y = stormlet.y;
			if (stormletsMap.get(runner) != null){
				surrounding++;
			}
			runner.x = stormlet.x-1;
			runner.y = stormlet.y;
			if (stormletsMap.get(runner) != null){
				surrounding++;
			}
			runner.x = stormlet.x;
			runner.y = stormlet.y+1;
			if (stormletsMap.get(runner) != null){
				surrounding++;
			}
			runner.x = stormlet.x;
			runner.y = stormlet.y-1;
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
		else if (Util.chance(20))
			shrink();		
	}
	
	private int getGrowChance() {
		return 100-usedFuel;
	}
	
	public void move(CardinalDirection windDirection) {
		int xScale = GlobeMapModel.getSingleton().getLongitudeScale(position.y);
		int yScale = GlobeMapModel.getSingleton().getLatitudeHeight();
		position.x += windDirection.getVectors().x * xScale;
		position.y += windDirection.getVectors().y * yScale;
		position.y = GlobeMapModel.getSingleton().normalizeLat(position.y);
		position.x = GlobeMapModel.getSingleton().normalizeLong(position.y, position.x);
	}
	
}
