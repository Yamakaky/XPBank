package fr.yamakaky.pluginxpbank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class ConfigManager {
	
	private static FileConfiguration file = Main.main.getConfig();
	private static String pathCompte = "Accounts.";
	private static String pathLoc = "Locations.";
	private static String pathDelay = "Delay";

	public static void RetirerXP(Player p)
	{
		if (!file.contains(pathCompte + p.getName()))
			Main.SendMsgError(p, "You don't have enough xp in your account");
		else if (!TimeManager.canAccess(p))
			Main.SendMsgError(p, "You must wait before use your account");
		else
		{
			p.giveExp(file.getInt(pathCompte + p.getName()));
			file.set(pathCompte + p.getName(), null);
			Main.SendMsg(p, "XP withdrawn from your account");
		}
		
		Main.main.saveConfig();
	}

	public static void DeposerXP(Player p)
	{
		int xp = totalXP(p);
		
		if (xp == 0)
		{
			Main.SendMsgError(p, "You don't have xp to deposit");
			return;
		}
		else if (!TimeManager.canAccess(p))
		{
			Main.SendMsgError(p, "You must wait before use your account");
			return;
		}
		
		file.set(pathCompte + p.getName(), xp + file.getInt(pathCompte + p.getName(), 0));
		p.setExp(0);
		p.setLevel(0);
		Main.SendMsg(p, "XP deposited in your account");
		
		Main.main.saveConfig();
	}

	public static int nombreComptes()
	{
		return file.getConfigurationSection("Comptes").getKeys(false).size();
	}

	public static int totalXP()
	{
		int total = 0;
		for (String s : file.getConfigurationSection("Accounts").getKeys(false)) 
			total += file.getInt("Accounts." + s);
		return total;
	}
	
	private static int totalXP(Player player)
	{
		int level = player.getLevel();
		long xp = 0;
		 
		if (level < 17)
			xp = (17*level);
		
		else if (level >= 17 && level <= 30)
			xp = Math.round(272+1.5*(level-16)*(level-16)+18.5*(level-16)) + 1;
			
		else if (level > 30)
			xp = Math.round(level*17 + (level-16)*(level-15)*1.5 + (level-31)*(level-30)*2) + 1;
		
		return (int) xp;
	}
	
	public static Set<Location> loadLocations()
	{
		Set<Location> set = new HashSet<Location>();
		
		for (World world : Bukkit.getServer().getWorlds())
			if (file.contains(pathLoc + world.getName()))
				for (String loc : file.getStringList(pathLoc + world.getName()))
					if (isSign(StringToLoc(loc, world)))
						set.add(StringToLoc(loc, world));
				
		return set;
	}
	
	public static void saveLocations()
	{
		Map<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>(); // "== Map<world, List<x/y/z>>"
		
		for (Location loc : SignManager.getSigns())
		{
			String world = loc.getWorld().getName();
			
			if (!map.containsKey(world))
				map.put(world, new ArrayList<String>());
			
			map.get(world).add(LocToString(loc));
		}
		
		file.set("Locations", null);
		
		for (String world : map.keySet())
			file.set(pathLoc + world, map.get(world));
		
		Main.main.saveConfig();
	}

	private static Location StringToLoc(String loc, World world)
	{
		String[] s = loc.split("/");
		int x = Integer.parseInt(s[0]);
		int y = Integer.parseInt(s[1]);
		int z = Integer.parseInt(s[2]);
		return new Location (world, x, y, z);
	}

	private static String LocToString(Location loc)
	{
		return loc.getBlockX() + "/" + loc.getBlockY() + "/" + loc.getBlockZ();
	}
	
	private static boolean isSign (Location loc)
	{
		try
		{
			Sign sign = (Sign) loc.getBlock().getState();
			if (ChatColor.stripColor(sign.getLine(0)).equalsIgnoreCase("[XP Bank]"))
			{
				return true;
			}
		} catch (Exception e) {}
		return false;
	}

	public static long getDelay()
	{
		return file.getInt(pathDelay);
	}
}