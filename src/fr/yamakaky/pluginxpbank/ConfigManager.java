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
	private static String pathCompte = "Comptes.";
	private static String pathLoc = "Locations.";

	public static void RetirerXP(Player p)
	{
		if (file.contains(pathCompte + p.getName()))
		{
			p.giveExp(file.getInt(pathCompte + p.getName()));
			file.set(pathCompte + p.getName(), null);
			Main.SendMsg(p, "XP retiré de votre compte");
		}
		else
			Main.SendMsgError(p, "Vous n'avez plus d'xp sur votre compte");
		
		Main.main.saveConfig();
	}

	public static void DeposerXP(Player p)
	{
		int xp = totalXP(p);
		
		if (xp == 0)
		{
			Main.SendMsgError(p, "Vous n'avez pas d'xp à déposer");
			return;
		}
		
		file.set(pathCompte + p.getName(), xp + file.getInt(pathCompte + p.getName(), 0));
		p.setExp(0);
		p.setLevel(0);
		Main.SendMsg(p, "XP déposé sur votre compte");
		
		Main.main.saveConfig();
	}

	public static int nombreComptes()
	{
		return file.getConfigurationSection("Comptes").getKeys(false).size();
	}

	public static int totalXP()
	{
		int total = 0;
		for (String s : file.getConfigurationSection("Comptes").getKeys(false)) 
			total += file.getInt("Comptes." + s);
		return total;
	}
	
	private static int totalXP(Player player)
	{
		int level = player.getLevel();
		float percent = player.getExp();
		 
		int xp = (int) Math.round(1.75*Math.pow(level, 2) + 5*level + percent*(3.5*level + 6.7));//3.5F*level*(level + 1) + (level + 1)*7*percent;
		return xp;
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
			if (ChatColor.stripColor(sign.getLine(0)).equalsIgnoreCase("[Banque à XP]"))
			{
				return true;
			}
		} catch (Exception e) {}
		return false;
	}
}
