package me.dbstudios.solusrpg.util;

public class MessageNotFoundException extends Exception {
	private static final long serialVersionUID = 1868524304617695334L;

	public MessageNotFoundException() {
		this("No data available.");
	}
	
	public MessageNotFoundException(String msg) {
		super(msg);
	}
}