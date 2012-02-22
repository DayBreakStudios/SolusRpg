package me.dbstudios.solusrpg.util;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.dbstudios.solusrpg.sys.RpgPlayer;

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

public class SkillLibrary {
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
		
		parsed = parsed.replace("{target}", targetName);
		
		return parsed;
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
	
	public static String getMessage(String skillName, String msgName, RpgPlayer caster, LivingEntity target) {
		FileConfiguration config = getSkillsConfig();
		String msg = config.getString("skills." + skillName + "." + msgName, null);
		
		if (msg != null) {
			return parseAll(msg, caster, target);
		} else {
			return "";
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
	
	public static void notifyNearby(String text, int range, RpgPlayer caster) {
		List<Entity> nearby = caster.getPlayer().getNearbyEntities(range, range, range);
		
		for (Entity ent : nearby) {
			if (ent instanceof Player) {
				((Player)ent).sendMessage(text);
			}
		}
	}
}
