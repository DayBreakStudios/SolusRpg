package me.dbstudios.solusrpg.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.dbstudios.solusrpg.sys.RpgPlayer;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Creature;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class Util {
	public static LivingEntity getTargetLivingEntity(Player player, int range) {
		LivingEntity target = null;
		
		BlockIterator bIt = new BlockIterator(player, range);
		Block block;
		
		double bx, by, bz;
		double ex, ey, ez;
		
		while (bIt.hasNext()) {
			block = bIt.next();
			bx = block.getX();
			by = block.getY();
			bz = block.getZ();
			
			for (Entity ent : player.getNearbyEntities(range, range, range)) {
				if (ent instanceof LivingEntity) {
					ex = ent.getLocation().getX();
					ey = ent.getLocation().getY();
					ez = ent.getLocation().getZ();
					
					if ((bx - 0.75 <= ex && bx + 1.75 >= ex) && (bz - 0.75 <= ez && bz + 1.75 >= ez) && (by - 1.0 <= ey && by + 2.5 >= ey)) {
						target = (LivingEntity)ent;
						break;
					}
				}
			}
		}
		
		return target;
	}
	
	public static Entity getTargetEntity(Player player, int range) {
		Entity target = null;
		
		BlockIterator bIt = new BlockIterator(player, range);
		Block block;
		
		double bx, by, bz;
		double ex, ey, ez;
		
		while (bIt.hasNext()) {
			block = bIt.next();
			bx = block.getX();
			by = block.getY();
			bz = block.getZ();
			
			for (Entity ent : player.getNearbyEntities(range, range, range)) {
				ex = ent.getLocation().getX();
				ey = ent.getLocation().getY();
				ez = ent.getLocation().getZ();
				
				if ((bx - 0.75 <= ex && bx + 1.75 >= ex) && (bz - 0.75 <= ez && bz + 1.75 >= ez) && (by - 1.0 <= ey && by + 2.5 >= ey)) {
					target = ent;
					break;
				}
			}
		}
		
		return target;
	}
	
	public static String parseAll(String text, RpgPlayer caster, LivingEntity target) {
		String parsed = text;
		
		parsed = Parser.ParseColors(parsed);
		
		if (caster != null)
			parsed = parsed.replaceAll("\\{(caster)?\\}", caster.getDisplayName());

		String targetName = null;
		
		if (target instanceof Player) {
			targetName = ((Player)target).getDisplayName();
		} else if (target instanceof Creature) {
			Creature c = (Creature)target;
			
			if (CreatureType.BLAZE.getEntityClass().isInstance(c)) {
				targetName = "a blaze";
			} else if (CreatureType.CAVE_SPIDER.getEntityClass().isInstance(c)) {
				targetName = "a cave Spider";
			} else if (CreatureType.CHICKEN.getEntityClass().isInstance(c)) {
				targetName = "a chicken";
			} else if (CreatureType.COW.getEntityClass().isInstance(c)) {
				targetName = "a cow";
			} else if (CreatureType.CREEPER.getEntityClass().isInstance(c)) {
				targetName = "a creeper";
			} else if (CreatureType.ENDERMAN.getEntityClass().isInstance(c)) {
				targetName = "an enderman";
			} else if (CreatureType.MUSHROOM_COW.getEntityClass().isInstance(c)) {
				targetName = "a mooshroom";
			} else if (CreatureType.PIG.getEntityClass().isInstance(c)) {
				targetName = "a pig";
			} else if (CreatureType.PIG_ZOMBIE.getEntityClass().isInstance(c)) {
				targetName = "a pig-zombie";
			} else if (CreatureType.SHEEP.getEntityClass().isInstance(c)) {
				targetName = "a sheep";
			} else if (CreatureType.SILVERFISH.getEntityClass().isInstance(c)) {
				targetName = "a silverfish";
			} else if (CreatureType.SKELETON.getEntityClass().isInstance(c)) {
				targetName = "a skeleton";
			} else if (CreatureType.SNOWMAN.getEntityClass().isInstance(c)) {
				targetName = "a snowman";
			} else if (CreatureType.SPIDER.getEntityClass().isInstance(c)) {
				targetName = "a spider";
			} else if (CreatureType.SQUID.getEntityClass().isInstance(c)) {
				targetName = "a squid";
			} else if (CreatureType.WOLF.getEntityClass().isInstance(c)) {
				targetName = "a wolf";
			} else if (CreatureType.ZOMBIE.getEntityClass().isInstance(c)) {
				targetName = "a zombie";
			} else {
				targetName = "your target";
			}
		} else {
			targetName = "your target";
		}
		
		parsed = parsed.replaceAll("(?i)\\{target\\}", targetName);
		
		return parsed;
	}
	
	public static Integer parseInt(String s) {
		Matcher m = Pattern.compile("[0-9]*").matcher(s);
		
		if (m.find()) {
			try {
				return Integer.parseInt(m.group());
			} catch (Exception e) {
				return null;
			}
		} else {
			return null;
		}
	}
	
	public static int parseInt(String s, int def) {
		if (parseInt(s) != null) return parseInt(s);
		
		return def;
	}
	
	public static int convertCooldown(String cooldown) {
		Matcher matcher = Pattern.compile("[a-zA-Z]").matcher(cooldown);
		int index = -1;
		int coolTimeSecs;

		if (matcher.find()) {
			index = matcher.start();

			try {
				coolTimeSecs = Integer.parseInt(cooldown.substring(0, index));
			} catch (NumberFormatException e) {
				coolTimeSecs = -1;
			}
		} else {
			try {
				coolTimeSecs = Integer.parseInt(cooldown);
			} catch (NumberFormatException  e) {
				coolTimeSecs = -1;
			}
		}
		
		if (index != -1) {
			if (matcher.group().matches("m?")) {
				coolTimeSecs *= 60;
			} else if (matcher.group().matches("h?")) {
				coolTimeSecs *= 360;
			}
		}
		
		return coolTimeSecs;
	}
	
	public static int convertCooldown(String cooldown, int def) {
		if (convertCooldown(cooldown) >= 0) return convertCooldown(cooldown);
		
		return def;
	}
	
	public static int convertRange(String range) {
		if (range.equalsIgnoreCase("LoS")) return 50;
		
		return -1;
	}
	
	public static int convertRange(String range, int def) {
		if (convertRange(range) != -1) return convertRange(range);
		
		return def;
	}
	
	public static FileConfiguration getSkillsConfig() {
		return YamlConfiguration.loadConfiguration(new File(Directories.Skills + "skills.yml"));
	}
	
	public static FileConfiguration getMainConfig() {
		return YamlConfiguration.loadConfiguration(new File(Directories.Config + "config.yml"));
	}

	public static FileConfiguration getDataConfig() {
		return YamlConfiguration.loadConfiguration(new File(Directories.Data + "players.yml"));
	}
	
	public static FileConfiguration getGroupsConfig() {
		return YamlConfiguration.loadConfiguration(new File(Directories.Config + "ItemGroups.yml"));
	}

	public static String getMessage(String skillName, String msgName, RpgPlayer caster, LivingEntity target) {
		FileConfiguration config = getSkillsConfig();
		String msg = config.getString("skills." + skillName + "." + msgName, null);
		
		if (msg != null) {
			return parseAll(msg, caster, target);
		} else {
			return ChatColor.RED + "Error: No message configured for '" + skillName + "." + msgName + "'...";
		}
	}

	public static Vector getRelativeVector(Location a, Location b) {
		return new Vector(a.getX() - b.getX(), a.getY() - b.getY(), a.getZ() - b.getZ());
	}

	public static Vector getRelativeVector(Entity a, Entity b) {
		return getRelativeVector(a.getLocation(), b.getLocation());
	}
	
	public static Vector setVectorLength(Vector vector, double len) {
		if (len >= 0) {
			return setVectorLengthSquared(vector, len * len);
		} else {
			return setVectorLengthSquared(vector, -len * len);
		}
	}
	
	public static Vector setVectorLengthSquared(Vector vector, double lenSq) {
		double vLen = vector.lengthSquared();
		
		if (Math.abs(vLen) > 0.0001) {
			return vector.multiply(-Math.sqrt(-lenSq / vLen));
		} else {
			return vector.multiply(Math.sqrt(lenSq / vLen));
		}
	}
	
	public static void notifyNearby(String text, int range, RpgPlayer caster, LivingEntity target) {
		List<Entity> nearby = caster.getPlayer().getNearbyEntities(range, range, range);
		
		for (Entity ent : nearby)
			if (ent instanceof Player && ent != target)
				((Player)ent).sendMessage(text);
	}

	public static ArrayList<String> toStringList(List<?> generic) {
		ArrayList<String> string = new ArrayList<String>();
	
		if (generic != null)
			for (Object o : generic)
				if (o instanceof String)
					string.add((String)o);
		
		return string;
	}
	
	public static String getSpecialName(String item, byte data) {
		if (item.equalsIgnoreCase("step")) {
			switch (data) {
			case 0x0:
				item = "STONE_STEP";
				break;
			case 0x1:
				item = "SANDSTONE_STEP";
				break;
			case 0x2:
				item = "WOOD_STEP";
				break;
			case 0x3:
				item = "COBBLESTONE_STEP";
				break;
			case 0x4:
				item = "BRICK_STEP";
				break;
			case 0x5:
				item = "STONE_BRICK_STEP";
				break;
			case 0x6:
				item = "STONE_STEP";
				break;
			}
		} else if (item.equalsIgnoreCase("smooth_brick")) {
			switch (data) {
			case 0x0:
				item = "SMOOTH_BRICK";
				break;
			case 0x1:
				item = "MOSSY_SMOOTH_BRICK";
				break;
			case 0x2:
				item = "CRACKED_SMOOTH_BRICK";
				break;
			}
		} else if (item.equalsIgnoreCase("ink_sack")) {
			switch(data) {
			case 0x0:
				item = "BLACK_DYE";
				break;
			case 0x1:
				item = "ROSE_RED_DYE";
				break;
			case 0x2:
				item = "CACTUS_GREEN_DYE";
				break;
			case 0x3:
				item = "COCOA_BEAN_DYE";
				break;
			case 0x4:
				item = "LAPIS_LAZULI_DYE";
				break;
			case 0x5:
				item = "PURPLE_DYE";
				break;
			case 0x6:
				item = "CYAN_DYE";
				break;
			case 0x7:
				item = "LIGHT_GRAY_DYE";
				break;
			case 0x8:
				item = "GRAY_DYE";
				break;
			case 0x9:
				item = "PINK_DYE";
				break;
			case 0xA:
				item = "LIME_DYE";
				break;
			case 0xB:
				item = "DANDELION_YELLOW_DYE";
				break;
			case 0xC:
				item = "LIGHT_BLUE_DYE";
				break;
			case 0xD:
				item = "MAGENTA_DYE";
				break;
			case 0xE:
				item = "ORANGE_DYE";
				break;
			case 0xF:
				item = "BONE_MEAL";
				break;
			}
		}

		return item;
	}
}
