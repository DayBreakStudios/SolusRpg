package me.dbstudios.solusrpg.sys;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class Skill extends JavaPlugin {
	public abstract String getSkillName();
	public abstract String getSkillTrueName();
	public abstract void echoDescription(RpgPlayer caster);

	public String getClassType() {
		if (this instanceof ActiveSkill) {
			return "Active";
		} else if (this instanceof PassiveSkill) {
			return "Passive";
		} else {
			return "Unknown";
		}
	}
}