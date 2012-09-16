package fr.yamakaky.pluginxpbank;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

public class TimeManager {

	static Map<String,Long> map = new HashMap<String, Long>(); // associates to a player the last date in time unix where he has withrawn/deposited xp points
	static long delay;
	
	public static void init()
	{
		delay = ConfigManager.getDelay();
	}
	
	/*
	 * returns false if 
	 */
	public static boolean canAccess(Player p)
	{
		long date = (new Date()).getTime();
		
		if ( !map.containsKey(p.getName()) || map.get(p.getName())+delay < date)
		{
			map.put(p.getName(), date);
			return true;
		}
		
		return false;
	}
}