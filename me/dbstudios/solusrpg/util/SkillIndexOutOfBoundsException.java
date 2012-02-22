package me.dbstudios.solusrpg.util;

public class SkillIndexOutOfBoundsException extends ArrayIndexOutOfBoundsException {
	private static final long serialVersionUID = -6599709191817515860L;

	public SkillIndexOutOfBoundsException() {
		this("No data available.");
	}
	
	public SkillIndexOutOfBoundsException(String msg) {
		super(msg);
	}
}