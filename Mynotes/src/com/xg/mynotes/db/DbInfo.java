package com.xg.mynotes.db;

import android.net.Uri;
import android.provider.BaseColumns;

public final class DbInfo {
	public static final String AUTHORITY = "com.mynotes.provider.DbInfo";
	
	private DbInfo() {
		
	}
	// database：NoteItems
	public static final class NoteItems implements BaseColumns { //BaseColumns._ID
		
		private NoteItems() {
			
		}
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/noteitems");
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.mynotes.noteitem";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.mynotes.noteitem";
		
		//note content
		public static final String CONTENT = "content";
		
		//lastest update date and time
		public static final String UPDATE_DATE = "update_date";
		public static final String UPDATE_TIME = "update_time";
		
		//alarm clock time
		public static final String ALARM_TIME = "alarm_time";
		
		//notes's background color
		public static final String BACKGROUND_COLOR = "background_color";
		
		//is folder?
		public static final String IS_FOLDER = "is_folder";
		// parent folder
		public static final String PARENT_FOLDER = "parent_folder";
		//default sort order
		public static final String DEFAULT_SORT_ORDER = IS_FOLDER + " DESC, " + UPDATE_DATE
				+ " DESC, " + UPDATE_TIME + " DESC";
	}
	 public static final class AppwidgetItems implements BaseColumns {
		 private AppwidgetItems() {
			 
		 }
		 public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/appwidgetitems");
		 public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.mynotes.appwidgetitem";
		 public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.mynotes.appwidgetitem";
		 
		 public static final String CONTENT = "content";
		 public static final String UPDATE_DATE = "update_date";
		 public static final String UPDATE_TIME = "update_time";
		 public static final String BACKGROUND_COLOR = "background_color";
		 
	 }
}












