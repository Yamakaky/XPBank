package fr.yamakaky.pluginxpbank;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	
	public static Main main;

	@Override
	public void onEnable()
	{
		main = this;
		this.getServer().getPluginManager().registerEvents(new BankListener(), this);
		if (!this.getConfig().contains("Delay"))
			this.getConfig().set("Delay",0);
		if (!this.getConfig().contains("Accounts"))
			this.getConfig().createSection("Accounts");
		if (!this.getConfig().contains("Locations"))
			this.getConfig().createSection("Locations");
		SignManager.init();
		TimeManager.init();
		this.saveConfig();
	}

	@Override
	public void onDisable()
	{
		ConfigManager.saveLocations();
	}
	
	public static void SendMsg (Player p, String msg)
	{
		p.sendMessage(ChatColor.GREEN + msg);
	}
	
	public static void SendMsgError (Player p, String msg)
	{
		p.sendMessage(ChatColor.RED + msg);
	}
}