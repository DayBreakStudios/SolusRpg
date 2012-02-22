package me.dbstudios.solusrpg.util;

import java.io.File;

public enum Directories {
	Base("plugins" + File.separator + "dbstudios" + File.separator + "SolusRpg" + File.separator),
	Config(Base + "config" + File.separator),
	Data(Base + "data" + File.separator),
	Skills(Base + "skills" + File.separator);
	
	private String path;
	
	private Directories(String path) {
		this.path = path;
	}
	
	public String toString() {
		return path;
	}
}