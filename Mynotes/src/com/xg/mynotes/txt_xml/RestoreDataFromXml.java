package com.xg.mynotes.txt_xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.client.utils.URIUtils;

import com.xg.mynotes.db.DbInfo.NoteItems;
import com.xg.mynotes.log.Logs;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;

public class RestoreDataFromXml {
	private ContentResolver mContentResolver;
	private String BASE_DIR = Environment.getExternalStorageDirectory().getPath();
	private String MID_DIR = "Mynotes";
	private String FILE_NAME = "notes_backup.xml";
	
	private static final String TAG = "RestoreDataFromXml";
	public  RestoreDataFromXml(ContentResolver contentResolver) {
		this.mContentResolver  = contentResolver;
	}
	
	public List<Note> readXml(InputStream is) throws Exception {
		SAXParserFactory pf = SAXParserFactory.newInstance();
		SAXParser saxp = pf.newSAXParser();
		MyDefaultHandler mdh = new MyDefaultHandler();
		saxp.parse(is, mdh);
		is.close();
		return mdh.getNotes();
	}
	
	public void restoreData() throws Exception {
		Logs.d(TAG, "start to restore data from SDCard");
		String path = BASE_DIR + File.separator + MID_DIR + File.separator + FILE_NAME;
		File file = new File(path);
		FileInputStream fis = new FileInputStream(file);
		List<Note> records = this.readXml(fis);
		Logs.d(TAG, "==> XML文件中的Note Item 的数量：" +records.size());
		//int id = -1;
		
		
		for (Note tmpNote : records) {
			
			int _id = tmpNote.getId();
			String content = tmpNote.getContent();
			String update_date = tmpNote.getUpdate_date();
			String update_time = tmpNote.getUpdate_time();
			String alarm_time = tmpNote.getAlarm_time();
			int bgColor = tmpNote.getBackground_color();
			String is_folder = tmpNote.getIs_folder();
			
			Logs.d(TAG, "== > is_folder:" + is_folder);
			int parent_folder = tmpNote.getParent_folder();
			Logs.d(TAG, "== > 原来的parent_folder:" + parent_folder);
			
			ContentValues cv = new ContentValues();
			cv.put(NoteItems.CONTENT, content);
			cv.put(NoteItems.UPDATE_DATE, update_date);
			cv.put(NoteItems.UPDATE_TIME, update_time);
			cv.put(NoteItems.ALARM_TIME, alarm_time);
			cv.put(NoteItems.BACKGROUND_COLOR, bgColor);
			cv.put(NoteItems.IS_FOLDER, is_folder);
			cv.put(NoteItems.PARENT_FOLDER, parent_folder);
			Cursor cursor = mContentResolver.query(ContentUris.withAppendedId(NoteItems.CONTENT_URI, _id), null, null, null, null);
			if (cursor.getCount() != 0) {
				mContentResolver.update(ContentUris.withAppendedId(NoteItems.CONTENT_URI, _id), cv, null, null);
			} else {
				mContentResolver.insert(NoteItems.CONTENT_URI, cv);
			}
			
			
//			if (is_folder.equals("no") && id != -1 && parent_folder != -1) {
//				cv.put(NoteItems.PARENT_FOLDER, id);
//				Logs.d(TAG, "==> 恢复数据后的parentfolder：" + id);
//				
//			} else {
//				cv.put(NoteItems.PARENT_FOLDER, -1);
//			}
//			Logs.d(TAG, "insert one record...");

//			if(is_folder.equals("yes")) {
//				mContentResolver.update(ContentUris.withAppendedId(NoteItems.CONTENT_URI, _id), cv, null, null);
//				String strId = (mContentResolver.insert(NoteItems.CONTENT_URI, cv)).getPathSegments().get(1);
//				id = Integer.parseInt(strId);
//				Logs.d(TAG, "文件夹被插入到数据库后的id：" + strId);
//			} else {
//				mContentResolver.insert(NoteItems.CONTENT_URI, cv);
//			}
		}
		Logs.d(TAG, "==> Restore finished...");
	}
}
