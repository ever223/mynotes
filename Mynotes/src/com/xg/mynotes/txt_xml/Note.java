package com.xg.mynotes.txt_xml;

public class Note {
	private int id;
	private String content;
	private String update_date;
	private String update_time;
	private String alarm_time;
	private int parent_folder;
	private String is_folder;
	private int background_color;
	
	public Note() {
		super();
	}
	public Note(int id, String content, String update_date, String update_time, String alarm_time, int background_color, String is_folder, int parent_folder) {
		
		this.id = id;
		this.content = content;
		this.update_date = update_date;
		this.update_time = update_time;
		this.background_color = background_color;
		this.alarm_time = alarm_time;
		this.is_folder = is_folder;
		this.parent_folder = parent_folder;
		
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getUpdate_date() {
		return update_date;
	}
	public void setUpdate_date(String update_date) {
		this.update_date = update_date;
	}
	public String getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}
	public String getAlarm_time() {
		return alarm_time;
	}
	public void setAlarm_time(String alarm_time) {
		this.alarm_time = alarm_time;
	}
	public int getParent_folder() {
		return parent_folder;
	}
	public void setParent_folder(int parent_folder) {
		this.parent_folder = parent_folder;
	}
	public String getIs_folder() {
		return is_folder;
	}
	public void setIs_folder(String is_folder) {
		this.is_folder = is_folder;
	}
	public int getBackground_color() {
		return background_color;
	}
	public void setBackground_color(int background_color) {
		this.background_color = background_color;
	}
	
}
