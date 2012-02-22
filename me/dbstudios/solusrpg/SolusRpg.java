package me.dbstudios.solusrpg;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.dbstudios.solusrpg.sys.RpgPlayer;
import me.dbstudios.solusrpg.sys.Skill;
import me.dbstudios.solusrpg.util.Directories;
import me.dbstudios.solusrpg.util.Util;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.SpoutManager;

public class SolusRpg extends JavaPlugin {
	private Hashtable<String, Skill> skillList = new Hashtable<String, Skill>();
	private Hashtable<Player, RpgPlayer> playerList = new Hashtable<Player, RpgPlayer>();
	private SRPlayerListener pListener;
	private SREntityListener eListener;
	private SRInventoryListener iListener;
	private SRBlockListener bListener;
	private Logger logger;
	
	public void onEnable() {
		long startTime = System.currentTimeMillis();
		
		PluginManager manager = this.getServer().getPluginManager();
		
		pListener = new SRPlayerListener(this);
		eListener = new SREntityListener(this);
		iListener = new SRInventoryListener(this);
		bListener = new SRBlockListener(this);
		
		manager.registerEvents(pListener, this);
		manager.registerEvents(eListener, this);
		manager.registerEvents(iListener, this);
		manager.registerEvents(bListener, this);
		
		logger = this.getServer().getLogger();
		
		File tmpFile;
		
		tmpFile = new File(Directories.Config + "config.yml");
		if (!tmpFile.exists()) {
			this.extract(Directories.Config.toString(), "config.yml");
			this.log(Level.INFO, "Extracted config.yml");
		}
		
		tmpFile = new File(Directories.Data + "players.yml");
		if (!tmpFile.exists()) {
			this.extract(Directories.Data.toString(), "players.yml");
			this.log(Level.INFO, "Extracted players.yml");
		}
		
		tmpFile = new File(Directories.Config + "hud.yml");
		if (!tmpFile.exists()) {
			this.extract(Directories.Config.toString(), "hud.yml");
			this.log(Level.INFO, "Extracted hud.yml");
		}
		
		tmpFile = new File(Directories.Skills + "skills.yml");
		if (!tmpFile.exists()) {
			this.extract(Directories.Skills.toString(), "skills.yml");
			this.log(Level.INFO, "Extracted skills.yml");
		}
		
		tmpFile = new File(Directories.Config + "ItemGroups.yml");
		if (!tmpFile.exists()) {
			this.extract(Directories.Config.toString(), "ItemGroups.yml");
			this.log(Level.INFO, "Extracted ItemGroups.yml");
		}
		
		FileConfiguration config = YamlConfiguration.loadConfiguration(new File(Directories.Config + "config.yml"));
		ArrayList<String> skills = this.makeStringList(config.getList("config.skills", null));
		
		if (skills != null) {
			for (String skill : skills) {
				try {					
					manager.loadPlugin(new File(Directories.Skills + skill + ".jar"));
					manager.enablePlugin(manager.getPlugin(skill));
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (manager.getPlugin(skill) != null && manager.getPlugin(skill).isEnabled()) {
						this.log(Level.INFO, "Loaded skill: " + skill);
						
						skillList.put(skill, (Skill)manager.getPlugin(skill));
					} else {
						this.log(Level.WARNING, "Could not load skill: " + skill);
					}
				}
			}
		}
		
		long bootTime = System.currentTimeMillis() - startTime;
		this.log(Level.INFO, "SolusRpg (v" + this.getDescription().getVersion() + ") loaded with " + skillList.size() + " skills in " + bootTime
				+ " milliseconds.");
	}
	
	public void onDisable() {
		this.log(Level.INFO, "SolusRpg disabled.");
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("class")) {
			return (new SRCommander()).classCommand(sender, args, this);
		} else if (label.equalsIgnoreCase("skill")) {
			return (new SRCommander()).skillCommand(sender, args, this);
		} else if (label.equalsIgnoreCase("stats")) {
			return (new SRCommander()).statsCommand(sender, args, this);
		} else if (label.equalsIgnoreCase("sr")) {
			return (new SRCommander()).srCommand(sender, args, this);
		} else if (label.equalsIgnoreCase("solus") && args.length == 1 && (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("halp"))) {
			return (new SRCommander()).solusCommand(sender, this);
		} else {
			return false;
		}
	}
	
	public void log(Level level, String msg) {
		logger.log(level, "[SolusRpg] " + msg);
	}
	
	public void extract(String destination, String filename) {
		File file = new File(destination);
		
		if (!file.exists()) {
			file.mkdirs();
		}
		
		file = new File(destination + filename);
		
		try {
			file.createNewFile();
			
			InputStream input = this.getClass().getResourceAsStream("/resources/" + filename);
			OutputStream output = new FileOutputStream(file);
			
			int b;
			while ((b = input.read()) != -1) {
				output.write(b);
			}
			
			input.close();
			output.close();
		} catch (Exception e) {
			this.log(Level.WARNING, "Could not extract " + filename);
		}
	}
	
	public ArrayList<String> makeStringList(List<?> generic) {
		ArrayList<String> string = new ArrayList<String>();
		
		if (generic != null) {
			for (Object o : generic) {
				if (o instanceof String) {
					string.add((String)o);
				}
			}
		}
		
		return string;
	}
	
	public Skill getSkill(String skillName) {
		if (skillList.containsKey(skillName)) {
			return skillList.get(skillName);
		} else {
			return null;
		}
	}
	
	public String matchToClass(String pattern) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(new File(Directories.Config + "config.yml"));
		String match = "";
		
		if (config.getConfigurationSection("config.classes") != null) {
			for (String cl : config.getConfigurationSection("config.classes").getKeys(false)) {
				if (cl.equalsIgnoreCase(pattern)) {
					match = cl;
					break;
				} else if (config.getString("config.classes." + cl + ".name", "null").equalsIgnoreCase(pattern)) {
					match = cl;
					break;
				} else if (cl.toLowerCase().startsWith(pattern.toLowerCase())) {
					match = cl;
					break;
				} else if (config.getString("config.classes." + cl + ".name", "null").toLowerCase().equalsIgnoreCase(pattern.toLowerCase())) {
					match = cl;
					break;
				}
			}
		}
		
		if (match.length() < 1) {
			match = null;
		}
		
		return match;
	}
	
	public void addRpgPlayer(Player newPlayer) {
		RpgPlayer player = new RpgPlayer(SpoutManager.getPlayer(newPlayer), this);
		
		playerList.put(newPlayer, player);
	}
	
	public RpgPlayer getRpgPlayer(Player player) {
		if (playerList.containsKey(player)) {
			return playerList.get(player);
		} else {
			return null;
		}
	}
	
	public void removeRpgPlayer(Player player) {
		if (playerList.containsKey(player)) {
			this.getRpgPlayer(player).storeHealth();
			playerList.remove(player);
		}
	}
	
	public void changeJob(String player, String className) {
		RpgPlayer p = this.getRpgPlayer(this.getServer().getPlayer(player));

		int currentHealth = p.getHealth();
		int maxHealth = p.getMaxHealth();

		p.hideHud();

		this.removeRpgPlayer(this.getServer().getPlayer(player));
		
		FileConfiguration data = YamlConfiguration.loadConfiguration(new File(Directories.Data + "players.yml"));
		FileConfiguration config = Util.getMainConfig();

		int newMaxHealth = config.getInt("config.classes." + className + ".stats.health", 20);
		
		data.set("players." + player + ".class", className);
		data.set("players." + player + ".health", (int)Math.ceil((double)currentHealth / (double)maxHealth) * (double)newMaxHealth);
		
		try {
			data.save(new File(Directories.Data + "players.yml"));
		} catch (Exception e) {
			e.printStackTrace();
			this.log(Level.WARNING, "Could not write player data, changes will not be saved.");
		}
		
		this.addRpgPlayer(this.getServer().getPlayer(player));
	}
	
	public Location getRespawnPoint() {
		FileConfiguration config = YamlConfiguration.loadConfiguration(new File(Directories.Config + "config.yml"));
		ConfigurationSection spawn = config.getConfigurationSection("config.spawn");
		Location loc = this.getServer().getWorld("flatstone").getSpawnLocation();
		
		if (spawn != null) {
			loc.setX(spawn.getDouble("x", loc.getX()));
			loc.setY(spawn.getDouble("y", loc.getY()));
			loc.setZ(spawn.getDouble("z", loc.getZ()));
			loc.setPitch((float)spawn.getDouble("pitch", loc.getPitch()));
			loc.setYaw((float)spawn.getDouble("yaw", loc.getYaw()));
		}
		
		return loc;
	}
	
	public void setRespawnPoint(Location loc) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(new File(Directories.Config + "config.yml"));

		config.set("config.spawn.x", loc.getX());
		config.set("config.spawn.y", loc.getY());
		config.set("config.spawn.z", loc.getZ());
		config.set("config.spawn.pitch", loc.getPitch());
		config.set("config.spawn.yaw", loc.getYaw());
		
		try {
			config.save(new File(Directories.Config + "config.yml"));
		} catch (Exception e) {
			e.printStackTrace();
			this.log(Level.INFO, "Could not save spawn data, changes will not be kept past server restart.");
		}
	}
}
