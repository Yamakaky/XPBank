package fr.yamakaky.pluginxpbank;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class BankListener implements Listener {

	@EventHandler
	public void onSignChange (SignChangeEvent e)
	{
		if (e.getLine(0).equalsIgnoreCase("[XPBank]") && e.getPlayer().hasPermission("xpbank.place") && SignManager.registerSign(e.getBlock().getLocation())) {
            e.setLine(0, ChatColor.DARK_BLUE + "[Banque à XP]");
            e.setLine(1, ChatColor.YELLOW + "" + ConfigManager.nombreComptes() + ChatColor.GREEN + " comptes");
            e.setLine(2, ChatColor.GREEN + "Total XP :");
            e.setLine(3, ChatColor.YELLOW + "" + ConfigManager.totalXP());
            Main.SendMsg(e.getPlayer(), "Panneau à XP posé !");
        }
	}
	
	@EventHandler
	public void onExplosion (EntityExplodeEvent e)
	{
		for (Block block : e.blockList())
			SignManager.removeSignAndRelative(block.getLocation());
		SignManager.RefreshSigns();
	}
	
	@EventHandler
	public void onBlockBreak (BlockBreakEvent e)
	{
		if (!SignManager.getSignsAndRelatives().contains(e.getBlock().getLocation()))
			return;
		
		if (!e.getPlayer().hasPermission("xpbank.place"))
		{
			Main.SendMsgError(e.getPlayer(), "Vous n'avez pas la permission de casser ce panneau");
			e.setCancelled(true);
			SignManager.RefreshSigns();
			return;
		}
		
		SignManager.removeSignAndRelative(e.getBlock().getLocation());
		SignManager.RefreshSigns();
	}
	
	@EventHandler
	public void onPlayerInteract (PlayerInteractEvent e)
	{
		try {@SuppressWarnings("unused")
		BlockState block = e.getClickedBlock().getState();} catch (Exception ex) {return;} // Vérifie si il s'agit d'une interaction avec un bloc
		
		if (!(e.getClickedBlock().getState() instanceof Sign)) return;
		
		Player p = e.getPlayer();
		Location loc = e.getClickedBlock().getLocation();
		
		if (SignManager.getSigns().contains(loc))
		{
			if (e.getAction() == Action.LEFT_CLICK_BLOCK)
			{
				ConfigManager.DeposerXP(p);
			}
			else
			{
				ConfigManager.RetirerXP(p);
			}
			
			SignManager.RefreshSigns();
		}
	}
}
