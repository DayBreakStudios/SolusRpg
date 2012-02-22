package me.dbstudios.solusrpg.util;

public class SkillNotFoundException extends Exception {
	private static final long serialVersionUID = -7491442541724040546L;

	public SkillNotFoundException() {
		this("No data available.");
	}
	
	public SkillNotFoundException(String msg) {
		super(msg);
	}
}
