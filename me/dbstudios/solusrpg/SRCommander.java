package me.dbstudios.solusrpg;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

import me.dbstudios.solusrpg.sys.ActiveSkill;
import me.dbstudios.solusrpg.sys.PassiveSkill;
import me.dbstudios.solusrpg.sys.RpgPlayer;
import me.dbstudios.solusrpg.sys.Skill;
import me.dbstudios.solusrpg.util.Directories;
import me.dbstudios.solusrpg.util.Util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class SRCommander {
	public boolean classCommand(CommandSender sender, String args[], SolusRpg common) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(new File(Directories.Config + "config.yml"));
		boolean result = false;
		
		if (sender instanceof Player) {
			RpgPlayer player = common.getRpgPlayer((Player)sender);
			
			switch (args.length) {
			case 1:
				if (args[0].equalsIgnoreCase("info") && player.hasPerm("player.info.self")) {
					ChatColor color1;
					ChatColor color2;
					
					try {
						color1 = ChatColor.valueOf(config.getString("config.messages.colors.info1", "white").toUpperCase());
					} catch (IllegalArgumentException e) {
						color1 = ChatColor.WHITE;
					}

					try {
						color2 = ChatColor.valueOf(config.getString("config.messages.colors.info2", "white").toUpperCase());
					} catch (IllegalArgumentException e) {
						color2 = ChatColor.WHITE;
					}
					
					player.sendMessage(ChatColor.RED + "---------------------------");
					player.sendMessage(color1 + "Class lookup for " + color2 + player.getClassTrueName() + ":");
					player.sendMessage(color1 + "    Class: " + color2 + player.getClassName());
					player.sendMessage(color1 + "    Bio: " + color2 + config.getString("config.classes." + player.getClassTrueName() + ".bio"));
					player.sendMessage(color1 + "    Max weight: " + color2 + player.getMaxCarryWeight());
					
					String tmp;
					
					tmp = player.getAllowed("can-use");
					if (tmp != null)
						player.sendMessage(color1 + "    Can use: " + color2 + tmp);
					
					tmp = player.getAllowed("can-wear");
					if (tmp != null)
						player.sendMessage(color1 + "    Can wear: " + color2 + tmp);
					
					tmp = player.getAllowed("can-craft");
					if (tmp != null)
						player.sendMessage(color1 + "    Can craft: " + color2 + tmp);
					
					tmp = player.getAllowed("can-smelt");
					if (tmp != null)
						player.sendMessage(color1 + "    Can smelt: " + color2 + tmp);
					
					tmp = player.getAllowed("can-break");
					if (tmp != null)
						player.sendMessage(color1 + "    Can break: " + color2 + tmp);
					
					tmp = player.getAllowed("can-place");
					if (tmp != null)
						player.sendMessage(color1 + "    Can place: " + color2 + tmp);
					
					player.sendMessage(ChatColor.RED + "---------------------------");
					
					result = true;
				} else if (args[0].equalsIgnoreCase("list") && player.hasPerm("player.list")) {
					ChatColor color;
					
					try {
						color = ChatColor.valueOf(config.getString("config.messages.colors.list", "white").toUpperCase());
					} catch (IllegalArgumentException e) {
						color = ChatColor.WHITE;
					}
					
					player.sendMessage(ChatColor.RED + "---------------------------");
					player.sendMessage(color + "Available classes:");
					
					if (config.getConfigurationSection("config.classes") != null) {						
						for (String c : config.getConfigurationSection("config.classes").getKeys(false)) {
							player.sendMessage(color + "    > " + config.getString("config.classes." + c + ".name", "null") + " (" + c + ")");
						}
					} else {
						player.sendMessage(color + "    > No classes found");
					}
					
					player.sendMessage(ChatColor.RED + "---------------------------");
					
					result = true;
				}

				break;
				
			case 2:
				if (args[0].equalsIgnoreCase("info") && player.hasPerm("player.info")) {
					String c = common.matchToClass(args[1]);
					ChatColor color1;
					ChatColor color2;
					
					try {
						color1 = ChatColor.valueOf(config.getString("config.messages.colors.info", "white").toUpperCase());
					} catch (IllegalArgumentException e) {
						color1 = ChatColor.WHITE;
					}
					
					try {
						color2 = ChatColor.valueOf(config.getString("config.messages.colors.info2", "white").toUpperCase());
					} catch (IllegalArgumentException e) {
						color2 = ChatColor.WHITE;
					}
					
					if (c != null) {
						player.sendMessage(ChatColor.RED + "---------------------------");
						player.sendMessage(color1 + "Class lookup for " + c + ":");
						player.sendMessage(color1 + "    Class: " + color2 + config.getString("config.classes." + c + ".name"));
						player.sendMessage(color1 + "    Bio: " + color2 + config.getString("config.classes." + c + ".bio"));
						player.sendMessage(color1 + "    Max weight: " + color2 + config.getDouble("config.classes." + c + ".stats.max-weight", 350.0));
						
						String tmp;
						
						tmp = this.getAllowed(c, "can-use", common);
						if (tmp != null)
							player.sendMessage(color1 + "    Can use: " + color2 + tmp);
						
						tmp = this.getAllowed(c, "can-wear", common);
						if (tmp != null)
							player.sendMessage(color1 + "    Can wear: " + color2 + tmp);
						
						tmp = this.getAllowed(c, "can-craft", common);
						if (tmp != null)
							player.sendMessage(color1 + "    Can craft: " + color2 + tmp);
						
						tmp = this.getAllowed(c, "can-smelt", common);
						if (tmp != null)
							player.sendMessage(color1 + "    Can smelt: " + color2 + tmp);
						
						tmp = player.getAllowed("can-break");
						if (tmp != null)
							player.sendMessage(color1 + "    Can break: " + color2 + tmp);
						
						tmp = player.getAllowed("can-place");
						if (tmp != null)
							player.sendMessage(color1 + "    Can place: " + color2 + tmp);
						
						player.sendMessage(ChatColor.RED + "---------------------------");
						
						result = true;
					} else {
						player.sendMessage(color1 + "The class '" + args[1] + "' could not be found.");
					}
					
					result = true;
				} else if (args[0].equalsIgnoreCase("whois") && player.hasPerm("admin.whois")) {
					String p = this.matchPlayerName(player.getPlayer(), args[1]);
					
					if (p != null) {
						player.sendMessage(ChatColor.GOLD + "[SolusRpg] Whois lookup for " + p + ":");
						player.sendMessage(ChatColor.GOLD + "--> Class: " + common.getRpgPlayer(common.getServer().getPlayer(p)).getClassName());
					} else {
						player.sendMessage(ChatColor.RED + "Error: Could not match pattern '" + args[0] + "'.");
					}
					
					result = true;
				}
				
				break;
				
			case 3:
				if (args[0].equalsIgnoreCase("admin") && player.hasPerm("admin.change-class")) {
					String targetName = this.matchPlayerName(player.getPlayer(), args[1]);
					String className = common.matchToClass(args[2]);
					
					if (targetName != null && className != null) {
						common.changeJob(targetName, className);
						
						common.getServer().getPlayer(targetName).sendMessage(ChatColor.AQUA + "Your class has been changed to " + className + " by "
								+ player.getName() + ".");
						sender.sendMessage(ChatColor.AQUA + "Changed " + targetName + " to " + className + ".");
						
						common.getRpgPlayer(common.getServer().getPlayer(targetName)).updateHud();
					} else if (targetName == null) {
						player.sendMessage(ChatColor.DARK_AQUA + "Could not find player: " + args[1]);
					} else {
						player.sendMessage(ChatColor.DARK_AQUA + "Could not match job: " + args[2]);
					}
					
					result = true;
				}
				
				break;
			}
		} else if (sender instanceof ConsoleCommandSender) {
			ConsoleCommandSender console = (ConsoleCommandSender)sender;
			
			switch (args.length) {
			case 1:
				if (args[0].equalsIgnoreCase("list")) {
					console.sendMessage("    Available classes:");
				
					if (config.getConfigurationSection("config.classes") != null) {						
						for (String c : config.getConfigurationSection("config.classes").getKeys(false)) {
							console.sendMessage("        > " + config.getString("config.classes." + c + ".name", "null") + " (" + c + ")");
						}
					} else {
						console.sendMessage("        > No classes found");
					}
				
					result = true;
				}
				
				break;
			case 2:
				if (args[0].equalsIgnoreCase("info")) {
					String c = common.matchToClass(args[1]);
					
					if (c != null) {
						console.sendMessage("    Class: " + config.getString("config.classes." + c + ".name"));
						console.sendMessage("    Bio: " + config.getString("config.classes." + c + ".bio"));
						
						String tmp;
						
						tmp = this.getAllowed(c, "can-use", common);
						if (tmp != null)
							console.sendMessage("    Can use: " + tmp);
						
						tmp = this.getAllowed(c, "can-wear", common);
						if (tmp != null)
							console.sendMessage("    Can wear: " + tmp);
						
						tmp = this.getAllowed(c, "can-craft", common);
						if (tmp != null)
							console.sendMessage("    Can craft: " + tmp);
						
						tmp = this.getAllowed(c, "can-smelt", common);
						if (tmp != null)
							console.sendMessage("    Can smelt: " + tmp);
						
						result = true;
					} else {
						console.sendMessage("    The class '" + args[1] + "' could not be found.");
					}
					
					result = true;
				}
				
				break;
			}
		}
		
		return result;
	}
	
	public boolean skillCommand(CommandSender sender, String[] args, SolusRpg common) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(new File(Directories.Config + "config.yml"));
		ChatColor color;
		
		try {
			color = ChatColor.valueOf(config.getString("config.messages.colors.stats", "white").toUpperCase());
		} catch (IllegalArgumentException e) {
			color = ChatColor.WHITE;
		}
		
		boolean result = false;
		
		if (sender instanceof Player) {
			RpgPlayer caster = common.getRpgPlayer((Player)sender);
			Hashtable<ActiveSkill, Boolean> active = caster.getActiveSkills();
			Hashtable<PassiveSkill, Boolean> passive = caster.getPassiveSkills();
			
			switch (args.length) {
			case 0:
				caster.sendMessage(color + "Your active skills:");
				
				if (!active.keySet().isEmpty()) {
					for (ActiveSkill skill : active.keySet()) {
						if (active.get(skill)) {
							caster.sendMessage(color + "    - " + skill.getSkillName() + " is on cooldown.");
						} else {
							caster.sendMessage(color + "    - " + skill.getSkillName() + " is ready to use!");
						}
					}
				} else {
					caster.sendMessage(color + "    - None");
				}
				
				caster.sendMessage(" ");
				caster.sendMessage(color + "Your passive skills:");
				
				if (!passive.keySet().isEmpty()) {
					for (PassiveSkill skill : passive.keySet()) {
						caster.sendMessage(color + "    - " + skill.getSkillName());
					}
				} else {
					caster.sendMessage(color + "    - None");
				}
				
				result = true;
				
				break;
				
			case 1:
				ActiveSkill skill = null;
				
				for (ActiveSkill s : active.keySet()) {
					if (s.getSkillTrueName().equalsIgnoreCase(args[0])) {
						skill = s;
						break;
					}
				}
				
				if (skill != null) {
					caster.cast(skill);
				} else {
					caster.sendMessage(ChatColor.RED + "[SolusRpg] Could not find skill: " + args[0]);
				}
				
				result = true;
				
				break;
				
			case 2:
				if (args[0].equalsIgnoreCase("info")) {
					Skill s = null;
					
					for (ActiveSkill tmp : active.keySet()) {
						if (tmp.getSkillTrueName().equalsIgnoreCase(args[1])) {
							s = tmp;
							break;
						}
					}
					
					if (s == null) {
						for (PassiveSkill tmp : passive.keySet()) {
							if (tmp.getSkillTrueName().equalsIgnoreCase(args[1])) {
								s = tmp;
								break;
							}
						}
					}
					
					if (s != null) {
						s.echoDescription(caster);
					} else {
						caster.sendMessage(color + "Could not find skill '" + args[1] + "'.");
					}
					
					result = true;
				}
				
				break;
			}
		}
		
		return result;
	}
	
	public boolean statsCommand(CommandSender sender, String[] args, SolusRpg common) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(new File(Directories.Config + "config.yml"));
		ChatColor color;
		
		try {
			color = ChatColor.valueOf(config.getString("config.messages.colors.stats", "white").toUpperCase());
		} catch (IllegalArgumentException e) {
			color = ChatColor.WHITE;
		}
		
		if (args.length > 1 || !(sender instanceof Player))
			return false;
		
		RpgPlayer player = common.getRpgPlayer((Player)sender);
		
		if (!player.hasPerm("player.stats"))
			return false;
		
		player.sendMessage(ChatColor.RED + "---------------------------");
		player.sendMessage(color + "You are " + player.getDisplayName() + ", a " + player.getClassName() + ".");
		player.sendMessage(color + "    - Your health is at " + player.getHealth() + " of a max " + player.getMaxHealth() + ".");
		player.sendMessage(color + "    - You are carrying " + player.getCarryingWeight() + " units of a max " + player.getMaxCarryWeight() + ".");
		
		if (player.getCarryingWeight() > player.getMaxCarryWeight())
			player.sendMessage(ChatColor.DARK_RED + "        You are overweight!");
		
		String tmp;
		
		tmp = player.getDamageBonus();
		if (tmp != null)
			player.sendMessage(color + "    - Your class grants you a " + tmp + " point damage bonus.");
		
		tmp = player.getDefenseBonus();
		if (tmp != null)
			player.sendMessage(color + "    - Your class grants you a " + tmp + " point damage resistance.");
		
		tmp = player.getSpeed();
		if (tmp != null)
			player.sendMessage(color + "    - You are currently moving at " + tmp + "% of normal speed.");
		
		tmp = player.getJumpHeight();
		if (tmp != null)
			player.sendMessage(color + "    - Your class grants you a " + tmp + "% jump height.");
		
		player.sendMessage(ChatColor.RED + "---------------------------");
		
		return true;
	}
	
	public boolean srCommand(CommandSender sender, String[] args, SolusRpg common) {
		if (!(sender instanceof Player))
			return false;
		
		FileConfiguration config = YamlConfiguration.loadConfiguration(new File(Directories.Config + "config.yml"));
		boolean result = false;
		ChatColor color;
		RpgPlayer player = common.getRpgPlayer((Player)sender);
		
		try {
			color = ChatColor.valueOf(config.getString("config.messages.colors.sr", "white").toUpperCase());
		} catch (IllegalArgumentException e) {
			color = ChatColor.WHITE;
		}
		
		switch (args.length) {
		case 1:
			if (args[0].equalsIgnoreCase("setrespawn") && player.hasPerm("admin.setrespawn")) {
				common.setRespawnPoint(player.getPlayer().getLocation());
				player.sendMessage(color + "[SolusRpg] Set respawn point to your current location.");
				
				result = true;
			}
			
			break;
			
		case 2:
			if (args[0].equalsIgnoreCase("gui") && player.hasPerm("player.gui")) {
				if (args[1].equalsIgnoreCase("show")) {
					player.showHud();
					player.sendMessage(color + "[SolusRpg] Showing heads up display.");
				} else if (args[1].equalsIgnoreCase("hide")) {
					player.hideHud();
					player.sendMessage(color + "[SolusRpg] Hiding heads up display.");
				} else {
					player.sendMessage(color + "[SolusRpg] The command /sr gui does not accept the argument '" + args[1] + "'.");
				}
				
				result = true;
			}
			
			break;
		}
		
		return result;
	}
	
	public boolean solusCommand(CommandSender sender, SolusRpg common) {
		if (sender instanceof Player) {
			RpgPlayer player = common.getRpgPlayer((Player)sender);
			FileConfiguration config = Util.getMainConfig();			
			ChatColor color1;
			ChatColor color2;
			
			try {
				color1 = ChatColor.valueOf(config.getString("config.messages.colors.help1", "white").toUpperCase());
			} catch (IllegalArgumentException e) {
				color1 = ChatColor.WHITE;
			}
			
			try {
				color2 = ChatColor.valueOf(config.getString("config.messages.colors.help-2", "white").toUpperCase());
			} catch (IllegalArgumentException e) {
				color2 = ChatColor.WHITE;
			}
			
			if (player.hasPerm("player.info")) {
				player.sendMessage(color1 + "/class info [<class>]");
				player.sendMessage(color2 + "    Get info on a class (your class if no args)");
			}
			
			if (player.hasPerm("player.list")) {
				player.sendMessage(color1 + "/class list");
				player.sendMessage(color2 + "    Lists all available classes");
			}

			if (player.hasPerm("player.stats")) {
				player.sendMessage(color1 + "/stats");
				player.sendMessage(color2 + "    Prints your current stats");
			}
			
			if (player.hasPerm("player.gui")) {
				player.sendMessage(color1 + "/sr gui hide|show");
				player.sendMessage(color2 + "    Shows or hides the heads up display");
			}
			
			player.sendMessage(color1 + "/skill");
			player.sendMessage(color2 + "    Displays a list of available active and passive skills");
			
			player.sendMessage(color1 + "/skill <skill>");
			player.sendMessage(color2 + "    Activates <skill> (if applicable)");
			
			if (player.hasPerm("admin.change-class")) {
				player.sendMessage(color1 + "/class admin <player-name> <new-class>");
				player.sendMessage(color2 + "    Changes <player-name>'s class to <new-class>");
			}
						
			if (player.hasPerm("admin.whois")) {
				player.sendMessage(color1 + "/class whois <player-name>");
				player.sendMessage(color2 + "    Get's <player-name>'s class");
			}
			
			return true;
		}
		
		return false;
	}
	
	private String matchPlayerName(Player helper, String pattern) {
		String playerName = null;
		Player[] online = helper.getServer().getOnlinePlayers();
		
		for (Player player : online) {
			if (player.getName().equalsIgnoreCase(pattern)) {
				playerName = player.getName();
			}else if (player.getDisplayName().equalsIgnoreCase(pattern)) {
					playerName = player.getName();
			} else if (player.getName().toLowerCase().startsWith(pattern.toLowerCase())) {
				playerName = player.getName();
			} else if (player.getDisplayName().toLowerCase().startsWith(pattern.toLowerCase())) {
				playerName = player.getName();
			} else if (player.getDisplayName().toLowerCase().contains(pattern.toLowerCase())) {
				playerName = player.getName();
			} else if (player.getName().toLowerCase().contains(pattern.toLowerCase())) {
				playerName = player.getName();
			}
		}
		
		return playerName;
	}
	
	private String getAllowed(String c, String type, SolusRpg common) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(new File(Directories.Config + "config.yml"));
		String output = "";
		ArrayList<String> data = common.makeStringList(config.getList("config.classes." + c + "." + type, null));
		
		if (data != null) {
			for (String item : data) {
				output += item + ", ";
			}	
		
			output = output.substring(0, output.length() - 2);
			output = output.replace("_", " ");
			output = output.substring(0, 1).toUpperCase() + output.substring(1).toLowerCase();
		} else {
			output = null;
		}
		
		return output;
	}
}