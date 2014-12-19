package com.xg.mynotes.txt_xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.xmlpull.v1.XmlSerializer;

import com.xg.mynotes.db.DbInfo.NoteItems;
import com.xg.mynotes.log.Logs;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.util.Xml;

public class WriteXml {
	private Context context;
	private FileWriter nWriter;
	private String BASE_DIR = Environment.getExternalStorageDirectory().getPath();
	private String MID_DIR = "Mynotes";
	private String FILE_NAME = "notes_backup.xml";
	
	private static final String TAG = "WriteXml";
	public WriteXml(Context context) {
		this.context = context;
	}
	public boolean writeXml() throws Exception {
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File dir = new File(BASE_DIR + File.separator + MID_DIR);
			
			if(!dir.exists()) {
				dir.mkdir();
				Logs.d(TAG, "== > " + dir +" is created successfully!");
			} else {
				Logs.d(TAG, "== > " + dir +" has been created");
			}
			
			String path = BASE_DIR + File.separator + MID_DIR + File.separator + FILE_NAME;
			File file = new File(path);
			if(file.exists()) {
				if(file.delete()) {
					file.createNewFile();
				}
			}
			nWriter = new FileWriter(file, false);
			if(!writeNote(nWriter)) {
				return false;
			}
			return true;
		} else {
			Logs.d(TAG, "== >" + "can not access SDCard");
			return false;
		}
		
	}
	private boolean writeNote(FileWriter nWriter) throws Exception{
		Logs.d(TAG, "== > begin to write XML...");
		
		XmlSerializer serializer = Xml.newSerializer();
		serializer.setOutput(nWriter);
		serializer.startDocument("UTF-8", true);
		serializer.startTag(null, "records");
		
		Cursor cursorAllRecords = context.getContentResolver().query(NoteItems.CONTENT_URI, null, null, null, null);
		int itemCount = cursorAllRecords.getCount();
		cursorAllRecords.close();
		
		if (itemCount > 0) {
			
			Cursor folderCursor = context.getContentResolver().query(NoteItems.CONTENT_URI, null, NoteItems.IS_FOLDER + " = ? ", new String[] {"yes"}, null);
			int countOfFolders = folderCursor.getCount();
			if (countOfFolders > 0) {
				folderCursor.moveToFirst();
				for (int i = 0; i < countOfFolders; i++) {
					int id = writeRecord(serializer, folderCursor);
					Cursor notesInOneFolder = context.getContentResolver().query(NoteItems.CONTENT_URI, null, NoteItems.PARENT_FOLDER + " = ? ", new String[] { String.valueOf(id)}, null);
					int noteCountInOneFolder = notesInOneFolder.getCount();
					if (noteCountInOneFolder > 0) {
						notesInOneFolder.moveToFirst();
						for (int j = 0; j < noteCountInOneFolder; j++) {
							writeRecord(serializer, notesInOneFolder);
							notesInOneFolder.moveToNext();
						}
					}
					notesInOneFolder.close();
					folderCursor.moveToNext();
				}
			}
			folderCursor.close();
			
			Cursor rootNotes = context.getContentResolver().query(NoteItems.CONTENT_URI, null, NoteItems.IS_FOLDER + " = '" + "no" + "' and " + NoteItems.PARENT_FOLDER + " = " + "-1", null, null);
			int countOfRootNotes = rootNotes.getCount();
			if (countOfRootNotes > 0) {
				rootNotes.moveToFirst();
				for(int i = 0; i < countOfRootNotes; i++) {
					writeRecord(serializer, rootNotes);
					rootNotes.moveToNext();
				}
			}
			rootNotes.close();
			
			serializer.endTag(null, "records");
			serializer.endDocument();
			
			nWriter.flush();
			nWriter.close();
			return true;
			
		} else {
			nWriter.close();
			return false;
		}
	}
	private int writeRecord(XmlSerializer serializer, Cursor cursor)  throws IOException {
		
		int id = cursor.getInt(cursor.getColumnIndex(NoteItems._ID));
		String content = cursor.getString(cursor.getColumnIndex(NoteItems.CONTENT));
		String update_date = cursor.getString(cursor.getColumnIndex(NoteItems.UPDATE_DATE));
		String update_time = cursor.getString(cursor.getColumnIndex(NoteItems.UPDATE_TIME));
		int background_color = cursor.getInt(cursor.getColumnIndex(NoteItems.BACKGROUND_COLOR));
		String alarm_time = cursor.getString(cursor.getColumnIndex(NoteItems.ALARM_TIME));
		String is_folder = cursor.getString(cursor.getColumnIndex(NoteItems.IS_FOLDER));
		String parent_folder = cursor.getString(cursor.getColumnIndex(NoteItems.PARENT_FOLDER));
		
		serializer.startTag(null, "record");
		
		serializer.startTag(null, "id");
		serializer.text(String.valueOf(id));
		serializer.endTag(null, "id");
		
		serializer.startTag(null, "content");
		serializer.text(String.valueOf(content));
		serializer.endTag(null, "content");
		
		serializer.startTag(null, "update_date");
		serializer.text(String.valueOf(update_date));
		serializer.endTag(null, "update_date");

		serializer.startTag(null, "update_time");
		serializer.text(String.valueOf(update_time));
		serializer.endTag(null, "update_time");

		serializer.startTag(null, "alarm_time");
		serializer.text(String.valueOf(alarm_time));
		serializer.endTag(null, "alarm_time");
		
		serializer.startTag(null, "background_color");
		serializer.text(String.valueOf(background_color));
		serializer.endTag(null, "background_color");

		serializer.startTag(null, "is_folder");
		serializer.text(String.valueOf(is_folder));
		serializer.endTag(null, "is_folder");

		serializer.startTag(null, "parent_folder");
		serializer.text(String.valueOf(parent_folder));
		serializer.endTag(null, "parent_folder");
		
		serializer.endTag(null, "record");
		
		return id;
	}
}











