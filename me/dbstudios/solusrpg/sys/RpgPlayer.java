package me.dbstudios.solusrpg.sys;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.dbstudios.solusrpg.SolusRpg;
import me.dbstudios.solusrpg.gui.PlayerHud;
import me.dbstudios.solusrpg.util.Directories;
import me.dbstudios.solusrpg.util.MessageNotFoundException;
import me.dbstudios.solusrpg.util.Parser;
import me.dbstudios.solusrpg.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.gui.InGameHUD;
import org.getspout.spoutapi.player.SpoutPlayer;

import ru.tehkode.permissions.bukkit.PermissionsEx;

public class RpgPlayer {
	private int health;
	private int maxHealth;
	private int damageModifier;
	private int defenseModifier;
	private int speedPer;
	private int jumpPer;
	private double maxCarryWeight;
	private Hashtable<ActiveSkill, Boolean> activeSkills = new Hashtable<ActiveSkill, Boolean>();
	private Hashtable<PassiveSkill, Boolean> passiveSkills = new Hashtable<PassiveSkill, Boolean>();
	private Hashtable<String, ArrayList<Pattern>> permitNodes = new Hashtable<String, ArrayList<Pattern>>();
	private SpoutPlayer player;
	private String className;
	private SolusRpg common;
	private PlayerHud hud;
	
	public RpgPlayer(SpoutPlayer player, SolusRpg common) {
		this.player = player;
		this.common = common;
		this.attatchHud(new PlayerHud(this));
		
		this.init();
	}
	
	private void init() {
		FileConfiguration data = Util.getDataConfig();
		FileConfiguration config = Util.getMainConfig();
		
		className = data.getString("players." + player.getName() + ".class", config.getString("config.default-class", null));
		
		maxHealth = config.getInt("config.classes." + className + ".stats.health", 20);
		health = data.getInt("players." + player.getName() + ".health", 0);

		if (health <= 0) health = maxHealth;

		this.setHealth(health);

		damageModifier = Util.parseInt(config.getString("config.classes." + className + ".stats.damage", "+0"), 0);
		defenseModifier = Util.parseInt(config.getString("config.classes." + className + ".stats.defense", "+0"), 0);
		speedPer = Util.parseInt(config.getString("config.classes." + className + ".stats.speed", "100"), 100);
		jumpPer = Util.parseInt(config.getString("config.classes." + className + ".stats.jump-height", "100"), 100);
		maxCarryWeight = config.getDouble("config.classes." + className + ".stats.max-weight", 350.0);
		
		ArrayList<String> tmp;
		
		tmp = Util.toStringList(config.getList("config.classes." + className + ".skills.active", null));
		if (tmp != null) {
			for (String s : tmp) {
				Plugin p = common.getSkill(s);
				
				if (p != null && p instanceof ActiveSkill) {
					activeSkills.put((ActiveSkill)p, false);
				}
			}
		}
		
		tmp = Util.toStringList(config.getList("config.classes." + className + ".skills.passive", null));
		if (tmp != null) {
			for (String s : tmp) {
				Plugin p = common.getSkill(s);
				
				if (p != null && p instanceof PassiveSkill) {
					passiveSkills.put((PassiveSkill)p, false);
				}
			}
		}
		
		FileConfiguration groups = Util.getGroupsConfig();
		ConfigurationSection section = groups.getConfigurationSection("groups");
		Set<String> groupNames = new HashSet<String>();
		
		if (section != null)
				groupNames = section.getKeys(false);
		
		section = config.getConfigurationSection("config.classes." + this.getClassTrueName());
		
		if (section != null) {
			for (String key : section.getKeys(false)) {
				if (key.startsWith("can-")) {
					ArrayList<Pattern> nodes = new ArrayList<Pattern>();
					
					for (String val : Util.toStringList(section.getList(key))) {
						boolean added = false;
						
						for (String group : groupNames) {
							Matcher m = Pattern.compile("(?i)" + val).matcher(group);

							if (m.find()) {
								for (String v : Util.toStringList(groups.getList("groups." + m.group()))) {
									nodes.add(Pattern.compile("(?i)" + v));
								}
								
								added = true;
							}
						}
						
						if (!added)
							nodes.add(Pattern.compile("(?i)" + val));
					}
					
					permitNodes.put(key, nodes);
				}
			}
		}
				
		
		player.setJumpingMultiplier(jumpPer / 100);
		this.updateSpeed();
	}
	
	public String getDisplayName() {
		return player.getDisplayName();
	}
	
	public String getName() {
		return player.getName();
	}
	
	public SpoutPlayer getPlayer() {
		return player;
	}
	
	public void sendMessage(String text) {
		player.sendMessage(text);
	}
	
	public InGameHUD getMainScreen() {
		return player.getMainScreen();
	}
	
	public SolusRpg getParentPlugin() {
		return common;
	}
	
	public int getMaxHealth() {
		return maxHealth;
	}
	
	public int getHealth() {
		return health;
	}
	
	public boolean isAllowed(String item, String type) {
		if (permitNodes.keySet().contains(type)) {
			for (Pattern p : permitNodes.get(type)) {
				Matcher m = p.matcher("all");
				
				if (m.find() && m.group().length() == 3)
					return true;
					
				m = p.matcher(item);
				
				if (m.find() && m.group().length() == item.length())
					return true;
			}
		}
		
		return false;
	}
	
	public void damage(int amount, boolean trueDamage) {
		player.damage(1);

		if (trueDamage) {
			this.setHealth(health - (amount + defenseModifier));
		} else {
			this.setHealth(health - amount);
		}
	}

	public void setHealth(int amount) {
		health = amount;
		player.setHealth(Math.max((int)Math.ceil(Math.min(((double)health / (double)maxHealth) * 20.0, 20.0)), 0));

		this.updateHud();
	}
	
	public void damageHunger(int amount) {
		player.setFoodLevel(Math.max(player.getFoodLevel() - amount, 0));
	}
	
	public void updateHud() {
		hud.refresh();
		
		common.getServer().getScheduler().scheduleSyncDelayedTask(common, new Runnable() {
			public void run() {
				hud.refresh();
			}
		}, 60L);
	}
	
	public void attatchHud(PlayerHud hud) {
		this.hud = hud;
		this.hud.attatch();
	}
	
	public void showHud() {
		hud.show();
	}
	
	public void hideHud() {
		hud.hide();
	}
	
	public int getModifiedDamageDealt(int base) {
		return base + damageModifier;
	}
	
	public int getModifiedDamageReceived(int base) {
		return Math.max(base - defenseModifier, 1);
	}
	
	public String getClassName() {
		return Util.getMainConfig().getString("config.classes." + className + ".name");
	}
	
	public String getClassTrueName() {
		return className;
	}
	
	public void updateSpeed() {
		FileConfiguration config = Util.getMainConfig();
		double speedPercentage = 0;
		
		for (ItemStack armor : player.getInventory().getArmorContents()) {			
			if (armor.getType().name().contains("_")) {
				String[] split = armor.getType().name().split("_");
			
				speedPercentage += config.getDouble("config.speeds." + split[0].toLowerCase() + "." + split[1].toLowerCase(), 0);
			}
		}
		
		if (maxCarryWeight >= 0 && this.getCarryingWeight() > maxCarryWeight)
			speedPercentage += config.getDouble("config.item-weight.overweight-slow", 35.0);
		
		double speed = (double)(speedPer - speedPercentage) / 100.0;
		player.setWalkingMultiplier(speed);
		player.setAirSpeedMultiplier(speed);

		this.updateHud();
	}
	
	public double getCarryingWeight() {
		FileConfiguration config = Util.getMainConfig();
		double carryingWeight = 0.0;
		
		for (ItemStack item : player.getInventory().getContents()) {
            if (item != null) {
                carryingWeight += config.getDouble("config.item-weight." + item.getType().name(), 
                        config.getDouble("config.item-weight.default", 0.01)) * Math.max((double)item.getAmount(), 1);
            }
		}
		
		return Math.round((carryingWeight * (double)100)) / (double)100;
	}
	
	public double getMaxCarryWeight() {
		return maxCarryWeight;
	}
	
	public String getMessage(String name) throws MessageNotFoundException {
		FileConfiguration config = Util.getMainConfig();
		String msg = config.getString("config.messages." + name);
		
		if (msg != null) {
			msg = Parser.ParseColors(Parser.ParseSpecialStrings(msg, this));
			
			return msg;
		} else {
			throw new MessageNotFoundException("No message defined for " + name);
		}
	}
	
	public String getMessage(String name, String item) throws MessageNotFoundException {
		try {
			return Parser.ParseSpecialStrings(this.getMessage(name), this, item);
		} catch (MessageNotFoundException mnfE) {
			throw new MessageNotFoundException(mnfE.getMessage());
		} catch (NullPointerException npE) {
			throw new MessageNotFoundException("No message defined for " + name);
		}
	}
	
	public boolean hasPerm(String perm) {
		return PermissionsEx.getPermissionManager().getUser(player).has("dbstudios.solusrpg." + perm);
	}
	
	public String getEffects() {
		String effects = "";
		
		if (this.getSpeedVal() != this.getMaxSpeed()) {
			effects += (int)(this.getSpeedVal() * 100) + "% speed, ";
		}
		
		if (this.getJumpHeightVal() != 1.0) {
			effects += (int)(this.getJumpHeightVal() * 100) + "% jump height, ";
		}
		
		if (effects.length() > 1) {
			effects = effects.substring(0, effects.length() - 2);
		} else {
			effects = "None";
		}
		
		return effects;
	}
	
	public String getAllowed(String type) {
		FileConfiguration config = Util.getMainConfig();
		String output = "";
		ArrayList<String> data = common.makeStringList(config.getList("config.classes." + className + "." + type, null));
		
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

	private double getJumpHeightVal() {
		return player.getJumpingMultiplier();
	}

	private double getMaxSpeed() {
		return speedPer / 100;
	}

	private double getSpeedVal() {
		return player.getWalkingMultiplier();
	}
	
	public Hashtable<ActiveSkill, Boolean> getActiveSkills() {
		return activeSkills;
	}
	
	public Hashtable<PassiveSkill, Boolean> getPassiveSkills() {
		return passiveSkills;
	}
	
	public String getDamageBonus() {
		if (damageModifier > 0) {
			return String.valueOf(damageModifier);
		} else {
			return null;
		}
	}
	
	public String getDefenseBonus() {
		if (defenseModifier > 0) {
			return String.valueOf(defenseModifier);
		} else {
			return null;
		}
	}
	
	public String getSpeed() {
		if (player.getWalkingMultiplier() * 100 != 100) {
			return String.valueOf(Math.floor(player.getWalkingMultiplier() * 100));
		} else {
			return null;
		}
	}
	
	public String getJumpHeight() {
		if (player.getJumpingMultiplier() != 1) {
			return String.valueOf(player.getJumpingMultiplier() * 100);
		} else {
			return null;
		}	
	}
	
	public void cast(ActiveSkill skill) {
		if (activeSkills.keySet().contains(skill)) {
			if (!activeSkills.get(skill)) {
				activeSkills.put(skill, true);
				skill.cast(this);
			} else {
				this.sendMessage(ChatColor.RED + skill.getSkillName() + " is still on cool down!");
			}
		} else {
			this.sendMessage(ChatColor.RED + "You do not know how to use " + skill.getSkillName() + ".");
		}
	}
	
	public void cooldown(ActiveSkill skill) {
		if (activeSkills.keySet().contains(skill)) {
			activeSkills.put(skill, false);
		}
	}

	public void storeHealth() {
		FileConfiguration data = Util.getDataConfig();

		if (health > 0) {
			data.set("players." + player.getName() + ".health", health);
		} else {
			data.set("players." + player.getName() + ".health", maxHealth);
		}

		try {
			data.save(new File(Directories.Data + "players.yml"));
		} catch (Exception e) {}
	}

	public void regenerate(int amount, boolean callDelayedUpdate) {
		if (player.getHealth() == 20) {
			this.setHealth(maxHealth);
		} else if (amount > 0) {
			this.setHealth(Math.min(health + amount, maxHealth));
		}
		
		if (callDelayedUpdate) {
			final RpgPlayer thisInstance = this;
		
			common.getServer().getScheduler().scheduleAsyncDelayedTask(common, new Runnable() {
				public void run() {
					thisInstance.regenerate(0, false);
				}
			}, 40L);
		}
	}
}