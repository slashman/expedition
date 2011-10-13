package net.slashie.expedition.ui.oryx;

import java.awt.Rectangle;
import java.util.Properties;

import net.slashie.utils.Position;
import net.slashie.utils.PropertyFilters;

public class Layout {
	public Rectangle MSGBOX_BOUNDS;
	public Position POS_UNITS_BOX;
	public Position POS_VEHICLES_BOX;
	public Position POS_WEATHER;
	public Position POS_TEMPERATURE;
	public Position POS_TERRAIN;
	public Position POS_WIND_TITLE;
	public Position POS_WIND;
	public Position POS_LAT_TITLE;
	public Position POS_LAT;
	public Position POS_LONG_TITLE;
	public Position POS_LONG;
	public Position POS_HEADING_TITLE;
	public Position POS_HEADING;
	public Position POS_SPEED;
	public Position POS_BURDEN_TITLE;
	public Position POS_BURDEN;
	public Position POS_MOOD_TITLE;
	public Position POS_MOOD;
	public Position POS_MOOD_ICON;
	public Position POS_SUPPLIES_TITLE;
	public Position POS_SUPPLIES;
	public int POS_LOCATION_Y;
	public Position POS_DATE;
	public Position POS_TIME;
	public Rectangle ACTIONS_PANEL_BOUNDS; 
	public Position POS_JOURNAL_ACTION;
	public Position POS_MUSIC_ACTION;
	public Position POS_SFX_ACTION;
	public Position POS_SAVE_ACTION;
	public Position POS_EXIT_ACTION;
	
	public Position POS_BEARING;
	public Position POS_SEADAYS;
	public Position POS_SUPPLIES_MOD;
	public Position POS_VERSION;
	public Position POS_MOVE_ACTION;
	
	public void initialize(Properties p){
		MSGBOX_BOUNDS = PropertyFilters.getRectangle(p.getProperty("MSGBOX_BOUNDS"));
		ACTIONS_PANEL_BOUNDS = PropertyFilters.getRectangle(p.getProperty("ACTIONS_PANEL_BOUNDS"));
		
		POS_UNITS_BOX = PropertyFilters.getPosition(p.getProperty("POS_UNITS_BOX"));
		POS_VEHICLES_BOX = PropertyFilters.getPosition(p.getProperty("POS_VEHICLES_BOX"));
		POS_WEATHER = PropertyFilters.getPosition(p.getProperty("POS_WEATHER"));
		POS_TEMPERATURE = PropertyFilters.getPosition(p.getProperty("POS_TEMPERATURE"));
		POS_TERRAIN = PropertyFilters.getPosition(p.getProperty("POS_TERRAIN"));
		POS_WIND_TITLE = PropertyFilters.getPosition(p.getProperty("POS_WIND_TITLE"));
		POS_WIND = PropertyFilters.getPosition(p.getProperty("POS_WIND"));
		POS_LAT_TITLE = PropertyFilters.getPosition(p.getProperty("POS_LAT_TITLE"));
		POS_LAT = PropertyFilters.getPosition(p.getProperty("POS_LAT"));
		POS_LONG_TITLE = PropertyFilters.getPosition(p.getProperty("POS_LONG_TITLE"));
		POS_LONG = PropertyFilters.getPosition(p.getProperty("POS_LONG"));
		POS_HEADING_TITLE = PropertyFilters.getPosition(p.getProperty("POS_HEADING_TITLE"));
		POS_HEADING = PropertyFilters.getPosition(p.getProperty("POS_HEADING"));
		POS_SPEED = PropertyFilters.getPosition(p.getProperty("POS_SPEED"));
		POS_BURDEN_TITLE = PropertyFilters.getPosition(p.getProperty("POS_BURDEN_TITLE"));
		POS_BURDEN = PropertyFilters.getPosition(p.getProperty("POS_BURDEN"));
		POS_MOOD_TITLE = PropertyFilters.getPosition(p.getProperty("POS_MOOD_TITLE"));
		POS_MOOD = PropertyFilters.getPosition(p.getProperty("POS_MOOD"));
		POS_MOOD_ICON = PropertyFilters.getPosition(p.getProperty("POS_MOOD_ICON"));
		POS_SUPPLIES_TITLE = PropertyFilters.getPosition(p.getProperty("POS_SUPPLIES_TITLE"));
		POS_SUPPLIES = PropertyFilters.getPosition(p.getProperty("POS_SUPPLIES"));
		POS_DATE = PropertyFilters.getPosition(p.getProperty("POS_DATE"));
		POS_TIME = PropertyFilters.getPosition(p.getProperty("POS_TIME"));
		POS_BEARING = PropertyFilters.getPosition(p.getProperty("POS_BEARING"));
		POS_SEADAYS = PropertyFilters.getPosition(p.getProperty("POS_SEADAYS"));
		POS_SUPPLIES_MOD = PropertyFilters.getPosition(p.getProperty("POS_SUPPLIES_MOD"));
		POS_VERSION = PropertyFilters.getPosition(p.getProperty("POS_VERSION"));
		
		POS_LOCATION_Y = PropertyFilters.inte(p.getProperty("POS_LOCATION_Y"));
		
		POS_MOVE_ACTION = PropertyFilters.getPosition(p.getProperty("POS_MOVE_ACTION"));		
		POS_JOURNAL_ACTION = PropertyFilters.getPosition(p.getProperty("POS_JOURNAL_ACTION"));
		POS_MUSIC_ACTION = PropertyFilters.getPosition(p.getProperty("POS_MUSIC_ACTION"));
		POS_SFX_ACTION = PropertyFilters.getPosition(p.getProperty("POS_SFX_ACTION"));
		POS_SAVE_ACTION = PropertyFilters.getPosition(p.getProperty("POS_SAVE_ACTION"));
		POS_EXIT_ACTION = PropertyFilters.getPosition(p.getProperty("POS_EXIT_ACTION"));
	}
}
