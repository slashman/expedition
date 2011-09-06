package net.slashie.expedition.world;

import net.slashie.expedition.domain.Expedition;
import net.slashie.expedition.level.GlobeMapModel;
import net.slashie.serf.fov.FOV;
import net.slashie.serf.fov.FOVMap;
import net.slashie.utils.Position;

public class GlobeFOV extends FOV{
	private Expedition e;
	public GlobeFOV(Expedition e){
		this.e = e;
	}
	@Override
	public boolean scanCell(FOVMap map, int x, int y)
	{
		if (e.getLevel() instanceof ExpeditionMicroLevel)
			return super.scanCell(map, x, y);
		int relx = x - startX;
		int rely = y - startY;
		int gridY = startY + rely * yScale;
		int gridX = startX + relx * GlobeMapModel.getLongitudeScale(gridY);
		return map.blockLOS(gridX,gridY);
	}
	
	@Override
	public void applyCell(FOVMap map, int x, int y)
	{
		if (e.getLevel() instanceof ExpeditionMicroLevel){
			super.applyCell(map, x, y);
			return;
		}
		int relx = x - startX;
		int rely = y - startY;
		int gridY = startY + rely * yScale;
		int gridX = startX + relx * GlobeMapModel.getLongitudeScale(gridY);
		
		if (circle){
			int distance = Position.flatDistanceRound(relx, rely, 0, 0);
			if (distance > maxRadius)
				return;
		}
		map.setSeen(gridX,gridY);
	}

}
