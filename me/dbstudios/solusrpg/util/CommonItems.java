package me.dbstudios.solusrpg.util;

import org.bukkit.Material;

public class CommonItems {
	private static Material[] items = {
                Material.AIR,
                Material.BOOK,
                Material.BUCKET,
                Material.CAKE_BLOCK,
                Material.CHEST,
                Material.DEAD_BUSH,
                Material.DIRT,
                Material.FURNACE,
                Material.GRASS,
                Material.GLASS,
                Material.GLOWSTONE,
                Material.JACK_O_LANTERN,
                Material.LADDER,
                Material.LEAVES,
                Material.LONG_GRASS,
                Material.MYCEL,
                Material.NETHER_STALK,
                Material.PAINTING,
                Material.POTION,
                Material.RED_ROSE,
                Material.SAPLING,
                Material.SIGN,
                Material.SIGN_POST,
                Material.SNOW,
                Material.THIN_GLASS,
                Material.TORCH,
                Material.TRAP_DOOR,
                Material.VINE,
                Material.WALL_SIGN,
                Material.WATER_LILY,
                Material.WOOL,
                Material.YELLOW_FLOWER,
                Material.WOOD,
                Material.WOOD_DOOR,
                Material.WORKBENCH};

	public static boolean contains(Material type) {
		for (Material m : items) {
			if (m == type) return true;
		}

		return false;
	}

	public static boolean contains(String item) {
		for (Material m : items) {
			if (m.name().equalsIgnoreCase(item)) return true;
			if (m == Material.AIR && item.equalsIgnoreCase("Fists")) return true;
		}

		return false;
	}
}