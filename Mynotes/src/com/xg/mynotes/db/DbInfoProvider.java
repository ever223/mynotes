package com.xg.mynotes.db;

import java.util.HashMap;

import android.R.integer;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException; // 是这个
//import java.sql.SQLException; 
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.xg.mynotes.MainActivity;
import com.xg.mynotes.db.DbInfo.AppwidgetItems;
import com.xg.mynotes.db.DbInfo.NoteItems;
import com.xg.mynotes.log.Logs;

public class DbInfoProvider extends ContentProvider {
	//database name
	private static final String DATABASE_NAME = "note.db";
	// database version
	private static final int DATABASE_VERSION = 1;
	//database table name
	private static final String TABLE_NOTEITEMS = "noteitems";
	private static final String TABLE_APPWIDGETITEMS = "appwidgetitems";
	
	private DatabaseHelper mOpenHelper;
	private static UriMatcher mUriMatcher;
	
	private static final int NOTEITEMS = 1;
	private static final int NOTEITEMS_ITEM = 2;
	
	private static final int APPWIDGETITEMS = 3;
	private static final int APPWIDGETITEMS_ITEM = 4;
	
	private static HashMap<String, String> mProjectionMap_NoteItems;
	private static HashMap<String, String> mProjectionMap_AppwidgetItems;
	
	// static initialize UriMatcher
	static {
		mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		//NoteItems table
		mUriMatcher.addURI(DbInfo.AUTHORITY, "noteitems", NOTEITEMS);
		mUriMatcher.addURI(DbInfo.AUTHORITY, "noteitems/#", NOTEITEMS_ITEM);
		//AppwidgetItems table
		mUriMatcher.addURI(DbInfo.AUTHORITY, "appwidgetitems", APPWIDGETITEMS);
		mUriMatcher.addURI(DbInfo.AUTHORITY, "appwidgetitems/#", APPWIDGETITEMS_ITEM);
		
		mProjectionMap_NoteItems = new HashMap<String, String>();
		mProjectionMap_NoteItems.put(NoteItems._ID, NoteItems._ID);
		mProjectionMap_NoteItems.put(NoteItems.CONTENT, NoteItems.CONTENT);
		mProjectionMap_NoteItems.put(NoteItems.UPDATE_DATE, NoteItems.UPDATE_DATE);
		mProjectionMap_NoteItems.put(NoteItems.UPDATE_TIME, NoteItems.UPDATE_TIME);
		mProjectionMap_NoteItems.put(NoteItems.ALARM_TIME, NoteItems.ALARM_TIME);
		mProjectionMap_NoteItems.put(NoteItems.BACKGROUND_COLOR, NoteItems.BACKGROUND_COLOR);
		mProjectionMap_NoteItems.put(NoteItems.IS_FOLDER, NoteItems.IS_FOLDER);
		mProjectionMap_NoteItems.put(NoteItems.PARENT_FOLDER, NoteItems.PARENT_FOLDER);
		
		mProjectionMap_AppwidgetItems = new HashMap<String, String>();
		mProjectionMap_AppwidgetItems.put(AppwidgetItems._ID, AppwidgetItems._ID);
		mProjectionMap_AppwidgetItems.put(AppwidgetItems.CONTENT, AppwidgetItems.CONTENT);
		mProjectionMap_AppwidgetItems.put(AppwidgetItems.UPDATE_DATE, AppwidgetItems.UPDATE_DATE);
		mProjectionMap_AppwidgetItems.put(AppwidgetItems.UPDATE_TIME, AppwidgetItems.UPDATE_TIME);
		mProjectionMap_AppwidgetItems.put(AppwidgetItems.BACKGROUND_COLOR, AppwidgetItems.BACKGROUND_COLOR);

	}
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			//create noteitems
			String sql_noteitems = "CREATE TABLE " + TABLE_NOTEITEMS + " ("
					+ NoteItems._ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
					+ NoteItems.CONTENT + " TEXT, "
					+ NoteItems.UPDATE_DATE + " TEXT , "
					+ NoteItems.UPDATE_TIME + " TEXT , "
					+ NoteItems.ALARM_TIME + " TEXT , "
					+ NoteItems.BACKGROUND_COLOR + " INTEGER , "
					+ NoteItems.IS_FOLDER + " TEXT , "
					+ NoteItems.PARENT_FOLDER + " INTEGER" + ");";
			Logs.w(MainActivity.TAG, "create table:" + TABLE_NOTEITEMS + " 的SQL语句：" + sql_noteitems);
			
			//create appwidgetitems
			String sql_appwidgetitems = "CREATE TABLE " + TABLE_APPWIDGETITEMS + " ("
					+ AppwidgetItems._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ AppwidgetItems.CONTENT + " TEXT, "
					+ AppwidgetItems.UPDATE_DATE + " TEXT, "
					+ AppwidgetItems.UPDATE_TIME + " TEXT, "
					+ AppwidgetItems.BACKGROUND_COLOR + " INTEGER " + ");";
			Logs.w(MainActivity.TAG, "create table:" + TABLE_APPWIDGETITEMS + "的SQL语句：" + sql_appwidgetitems);
			db.execSQL(sql_noteitems);
			db.execSQL(sql_appwidgetitems);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Logs.w(MainActivity.TAG, "Upgrading database from version " + oldVersion + " to " 
					+ newVersion + ", which will destory all old data");
			//delete old table
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTEITEMS);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPWIDGETITEMS);
			//recreate table
			onCreate(db);
			
		}
	}
	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		String orderBy = "";
		switch (mUriMatcher.match(uri)) {
		
		case NOTEITEMS_ITEM:
			queryBuilder.appendWhere(NoteItems._ID + " = " + uri.getPathSegments().get(1));
		case NOTEITEMS:
			queryBuilder.setTables(TABLE_NOTEITEMS);
			queryBuilder.setProjectionMap(mProjectionMap_NoteItems);
			if(TextUtils.isEmpty(sortOrder)) {
				orderBy = NoteItems.DEFAULT_SORT_ORDER;
			} else {
				orderBy = sortOrder;
			}
			break;
			
		case APPWIDGETITEMS_ITEM:
			queryBuilder.appendWhere(AppwidgetItems._ID + " = " + uri.getPathSegments().get(1));
		case APPWIDGETITEMS:
			queryBuilder.setTables(TABLE_APPWIDGETITEMS);
			queryBuilder.setProjectionMap(mProjectionMap_AppwidgetItems);
			if (!TextUtils.isEmpty(sortOrder)) {
				orderBy = sortOrder;
			}
			break;
			
		default:
			throw new IllegalArgumentException("query:Unknown URI" + uri);
		}
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor c = queryBuilder.query(db, projection, selection, selectionArgs, null, null, orderBy);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		Logs.d("DBInfoProvider","contentprovider== > query()");
		return c;
	}

	@Override
	public String getType(Uri uri) {
		switch (mUriMatcher.match(uri)) {
		case NOTEITEMS:
			return NoteItems.CONTENT_TYPE;
		case NOTEITEMS_ITEM:
			return NoteItems.CONTENT_ITEM_TYPE;
		case APPWIDGETITEMS:
			return AppwidgetItems.CONTENT_TYPE;
		case APPWIDGETITEMS_ITEM:
			return AppwidgetItems.CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Unkonown URI " +uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		String table;
		long id;
		Uri tmpUri;
		// *
		switch (mUriMatcher.match(uri)) {
		case NOTEITEMS: //matched table NoteItems
			table = TABLE_NOTEITEMS;
			id = db.insert(table, NoteItems.CONTENT, values);
			if(id > 0) {
				tmpUri = ContentUris.withAppendedId(NoteItems.CONTENT_URI, id);
				getContext().getContentResolver().notifyChange(tmpUri, null);
				Logs.d(MainActivity.TAG, "ContentProvider == > insert()");
				return tmpUri;
			}
			break;
		case APPWIDGETITEMS: //matched table NoteItems
			table = TABLE_APPWIDGETITEMS;
			id = db.insert(table, AppwidgetItems.CONTENT, values);
			if(id > 0) {
				tmpUri = ContentUris.withAppendedId(AppwidgetItems.CONTENT_URI, id);
				getContext().getContentResolver().notifyChange(tmpUri, null);
				Logs.d(MainActivity.TAG, "ContentProvider == > insert()");
				return tmpUri;
			}
			break;
		default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		throw new SQLException("Failed to insert row into " + uri);
		//return null;
		
	}
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count = 0;
		String tmpId;
		switch (mUriMatcher.match(uri)) {
		case NOTEITEMS:
			count = db.delete(TABLE_NOTEITEMS, selection, selectionArgs);
			break;
		case NOTEITEMS_ITEM:
			tmpId = uri.getPathSegments().get(1);
			count = db.delete(TABLE_NOTEITEMS, 
					NoteItems._ID + "=" + tmpId + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), 
							selectionArgs);
			break;
		case APPWIDGETITEMS:
			count = db.delete(TABLE_APPWIDGETITEMS, selection, selectionArgs);
			break;
		case APPWIDGETITEMS_ITEM:
			tmpId = uri.getPathSegments().get(1);
			count = db.delete(TABLE_APPWIDGETITEMS, AppwidgetItems._ID + " = " + tmpId 
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		Logs.d("DbInfoProvider", "== > delete()");
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count = 0;
		String tmpId;
		switch (mUriMatcher.match(uri)) {
		case NOTEITEMS:
			count = db.update(TABLE_NOTEITEMS, values, selection, selectionArgs);
			break;
		case NOTEITEMS_ITEM:
			tmpId = uri.getPathSegments().get(1);
			count = db.update(TABLE_NOTEITEMS, values, NoteItems._ID + "=" + tmpId
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : ""), selectionArgs);
			break;
		case APPWIDGETITEMS:
			count = db.update(TABLE_APPWIDGETITEMS, values, selection, selectionArgs);
			break;
		case APPWIDGETITEMS_ITEM:
			tmpId = uri.getPathSegments().get(1);
			count = db.update(TABLE_APPWIDGETITEMS, values, AppwidgetItems._ID + " = " + tmpId +
					(!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		Logs.d("DbInfoProvider", "== > update()");
		return count;
	}

}
