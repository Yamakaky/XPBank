package fr.yamakaky.pluginxpbank;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;

public class SignManager {

	private static Set<SignManager> liste = new HashSet<SignManager>();

	private Location sign;
	private Location relative;

	public SignManager(Location sign, Location relative) {
		if (sign != null && relative != null) {
			this.sign = sign;
			this.relative = relative;
			liste.add(this);
			this.RefreshSign();
		}
	}

	public static List<Location> getSigns() {
		List<Location> listeSigns = new ArrayList<Location>();

		for (SignManager sar : liste)
			listeSigns.add(sar.sign);

		return listeSigns;
	}

	public static List<Location> getSignsAndRelatives() {
		List<Location> listeSigns = new ArrayList<Location>();

		for (SignManager sar : liste) {
			listeSigns.add(sar.sign);
			listeSigns.add(sar.relative);
		}

		return listeSigns;
	}

	// Retourne le Relative du block.location en paramettre.
	public static Location getRelative(Location signloc) {
		for (SignManager sm : liste)
			if (sm.sign == signloc)
				return sm.relative;
		return null;
	}

	// Récupération du Relative et ajout du SignManager dans le Set
	public static void registerSign(Location locsign) {
		try {
			locsign.getBlock();
		} catch (Exception e) {
			return;
		} // Vérifie sur la locsign est un block

		if (!(locsign.getBlock().getState() instanceof org.bukkit.block.Sign))
			return;

		Block bs = locsign.getBlock();
		org.bukkit.material.Sign signmat = (org.bukkit.material.Sign) bs.getState().getData();
		Location relative = null;

		if (!signmat.isWallSign())
			relative = bs.getRelative(BlockFace.DOWN).getLocation();

		else if (signmat.getAttachedFace() == BlockFace.EAST)
			relative = bs.getRelative(BlockFace.EAST).getLocation();

		else if (signmat.getAttachedFace() == BlockFace.WEST)
			relative = bs.getRelative(BlockFace.WEST).getLocation();

		else if (signmat.getAttachedFace() == BlockFace.NORTH)
			relative = bs.getRelative(BlockFace.NORTH).getLocation();

		else if (signmat.getAttachedFace() == BlockFace.SOUTH)
			relative = bs.getRelative(BlockFace.SOUTH).getLocation();

		SignManager sm = new SignManager(locsign, relative);
		sm.RefreshSign();
	}

	// Mise à jour du panneau de l'objet SignManager
	public void RefreshSign() {
		Sign sign = (Sign) this.sign.getBlock().getState();
		sign.setLine(0, ChatColor.DARK_BLUE + "[Banque à XP]");
		sign.setLine(1, ChatColor.YELLOW + "" + ConfigManager.nombreComptes() + ChatColor.GREEN + " comptes");
		sign.setLine(2, ChatColor.GREEN + "Total XP :");
		sign.setLine(3, ChatColor.YELLOW + "" + ConfigManager.totalXP());
		sign.update();
	}

	public static void RefreshSigns() {
		for (SignManager sm : liste)
			try{ sm.RefreshSign(); } catch (Exception e) {}
	}

	// Methode sécurisée pour enlever un SignManager du Set
	public static void removeSignAndRelative(Location loc) {
		for (SignManager sm : liste)
			if (sm.sign.equals(loc) || sm.relative.equals(loc))
			{
				liste.remove(sm);
				return;
			}
	}

	public static void init() {
		liste.clear();
		for (Location loc : ConfigManager.loadLocations())
			registerSign(loc);
		for (SignManager sm : liste)
			sm.RefreshSign();
	}
}
