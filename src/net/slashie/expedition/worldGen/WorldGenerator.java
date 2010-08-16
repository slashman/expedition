package net.slashie.expedition.worldGen;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.slashie.expedition.data.ExpeditionDAO;
import net.slashie.expedition.domain.NativeTown;
import net.slashie.expedition.game.ExpeditionGame;
import net.slashie.expedition.world.Culture;
import net.slashie.expedition.world.ExpeditionMacroLevel;
import net.slashie.serf.game.SworeGame;
import net.slashie.util.FileUtil;
import net.slashie.util.Pair;
import net.slashie.utils.Position;
import net.slashie.utils.Util;

public class WorldGenerator {
	public static void addNativeSettlements(ExpeditionMacroLevel level){
		try {
			List<Pair<Position, Culture>> cultureCenters = new ArrayList<Pair<Position,Culture>>();
			//Get the culture centers
			try {
				BufferedReader r = FileUtil.getReader("data/culture.properties");
				String line = r.readLine();
				while (line != null){
					String[] row = line.split(",");
					Culture c = ExpeditionDAO.getCulture(row[2]);
					Position p = new Position(Integer.parseInt(row[0]),Integer.parseInt(row[1]));
					cultureCenters.add(new Pair<Position, Culture>(p,c));
					line = r.readLine();
				}
			} catch (FileNotFoundException fnfe){
				SworeGame.crash("Culture File Not Found");
				return;
			}
			cultureCenters.add(new Pair<Position, Culture>(new Position(3236,834), ExpeditionDAO.getCulture("AZTEC")));
			
			//Create settlements around each culture center
			for (Pair<Position,Culture> cultureCenter: cultureCenters){
				int numberOfSettlements = 0;
				int range = 15;
				numberOfSettlements = Util.rand(1, 3);
				int fussible = 0;
				for (int i = 0; i < numberOfSettlements; i++){
					Position settlementPosition = new Position(
							Util.rand(cultureCenter.getA().x-range, cultureCenter.getA().x+range),
							Util.rand(cultureCenter.getA().y-range, cultureCenter.getA().y+range));
					//Check if this is land
					if (level.getMapCell(settlementPosition )== null || level.getMapCell(settlementPosition).isWater() || level.getFeatureAt(settlementPosition) != null){
						fussible++;
						if (fussible < 1000){
							i--;
							continue;
						}
					} else {
						//Place a settlement
						NativeTown t = new NativeTown(ExpeditionGame.getCurrentGame());
						t.setPosition(new Position(cultureCenter.getA()));
						level.addFeature(t);
					}
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}