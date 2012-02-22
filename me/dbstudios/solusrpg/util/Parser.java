package me.dbstudios.solusrpg.util;

import me.dbstudios.solusrpg.sys.RpgPlayer;

import org.bukkit.ChatColor;

public class Parser {
	public static String ParseColors(String text) {
		String parsed = text;
		
		if (parsed.startsWith("0"))
			parsed = parsed.replaceFirst("0", "");
		
		for (ChatColor color : ChatColor.values()) {
			parsed = parsed.replaceAll("(?i)\\{" + color.name() + "\\}", color.toString());
		}
		
		return parsed;
	}
	
	public static String ParseSpecialStrings(String text, RpgPlayer player) {
		String parsed = text;
		
		if (parsed.startsWith("0"))
			parsed = parsed.replaceFirst("0", "");
		
		parsed = parsed.replaceAll("(?i)\\{player\\}", player.getDisplayName());
		parsed = parsed.replace("(?i)\\{player-real\\}", player.getName());
		
		return parsed;
	}
	
	public static String ParseSpecialStrings(String text, RpgPlayer player, String item) {
		String parsed = text;
		
		String itemText = item;
		
		itemText = itemText.replace("_", " ");
		
		String[] split = itemText.split(" ");
		itemText = "";
		
		for (String s : split) {
			itemText += s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase() + " ";
		}
		
		itemText = itemText.trim();
		
		parsed = parsed.replaceAll("(?i)\\{item\\}", itemText);
		
		return ParseSpecialStrings(parsed, player);
	}
}