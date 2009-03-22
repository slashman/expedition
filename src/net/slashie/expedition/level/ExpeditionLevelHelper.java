package net.slashie.expedition.level;

import net.slashie.expedition.world.ExpeditionLevel;
import net.slashie.util.Pair;

public class ExpeditionLevelHelper {
	private ExpeditionLevel expeditionLevel;
	public ExpeditionLevelHelper(ExpeditionLevel expeditionLevel) {
		super();
		this.expeditionLevel = expeditionLevel;
	}

	private String musicKey;
	private String superLevelId;
	
	public String getMusicKey() {
		return musicKey;
	}
	public void setMusicKey(String musicKey) {
		this.musicKey = musicKey;
	}
	public String getSuperLevelId() {
		return superLevelId;
	}
	public void setSuperLevelId(String superLevelId) {
		this.superLevelId = superLevelId;
	}

	private Pair<String,String> handyReusableObject = new Pair<String, String>("H","H");
	public Pair<String,String> getLocationDescription(){
		Pair<Integer, Integer> location = expeditionLevel.getLocation();
		handyReusableObject.setA(Math.abs(location.getA()) + (location.getA() > 0?"N":"S"));
		handyReusableObject.setB(Math.abs(location.getB()) + (location.getB() > 0?"W":"E"));
		
		return handyReusableObject;
	}

	
}
