package net.fmchan.model;

import java.util.LinkedHashMap;
import java.util.UUID;

public class Queue {
	private String name;
	private String uuid;
	private static int INDEX = 5000;
	private LinkedHashMap<String, Story> stories = new LinkedHashMap<String, Story>();

	public Queue(String name) {
		super();
		this.name = name;
	}
	public String initPrompter() {
		this.uuid = UUID.randomUUID().toString().toUpperCase();
		return this.uuid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public LinkedHashMap<String, Story> getStories() {
		return stories;
	}
	public void setStories(LinkedHashMap<String, Story> stories) {
		this.stories = stories;
	}
	public static int getINDEX() {
		return INDEX;
	}
	public static void setINDEX(int iNDEX) {
		INDEX = iNDEX;
	}
}