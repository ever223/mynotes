package com.xg.mynotes.txt_xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.xg.mynotes.db.DateTimeUtil;
import com.xg.mynotes.db.DbInfo.NoteItems;
import com.xg.mynotes.log.Logs;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;


public class WriteTxt {
	private Context context;
	private FileWriter nWriter;
	private String BASE_DIR = Environment.getExternalStorageDirectory().getPath();
	private String MID_DIR = "Mynotes";
	private String FILE_NAME = "notes_backup.txt";
	
	private static final String TAG = "WriteTxt";
	
	public WriteTxt(Context context) {
		this.context = context;
	}
	
	public boolean writeTxt() throws IOException {
		
		String selection = NoteItems.IS_FOLDER + " = '" + "yes" + "' or " + NoteItems.PARENT_FOLDER + " = " + "-1";
		Cursor mCursor = context.getContentResolver().query(NoteItems.CONTENT_URI, null, selection, null, null);
		int totalCount = mCursor.getCount();
		mCursor.close();
		//Logs.d(TAG, "position:1");
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			//Logs.d(TAG, "position:2");
			if(totalCount > 0) {
				
				File dir = new File(BASE_DIR + File.separator + MID_DIR);
				if(!dir.exists()) {
					dir.mkdir();
					Logs.d(TAG, "==>" + dir + " is created successfully");
				} else {
					Logs.d(TAG, "==>" + dir + " has been created");
				}
				
				String path = BASE_DIR + File.separator + MID_DIR + File.separator + FILE_NAME;
				File file = new File(path);
				if (!file.exists()) {
					file.createNewFile();
					Logs.d(TAG, "==>" + file + " is created successfully");
				} else {
					Logs.d(TAG, "==>" + file + " has been created");
				}
				
				nWriter = new FileWriter(file, false);
				Cursor folderCursor = context.getContentResolver().query(NoteItems.CONTENT_URI, null, NoteItems.IS_FOLDER + " = ? ", new String[] {"yes"}, null);
				int countOfFolders = folderCursor.getCount();
				Cursor allNotes = context.getContentResolver().query(NoteItems.CONTENT_URI, null, NoteItems.IS_FOLDER + " = ? ", new String[] {"no"}, null);
				int countOfNotes = allNotes.getCount();
				allNotes.close();
				nWriter.write("Mynotes" + "\t" + DateTimeUtil.getDate() + "\t" + DateTimeUtil.getTime() + "\n");
				nWriter.write("共输出" + countOfFolders + "个文件夹和" + countOfNotes + "条便签" + "\n");
				
				if(countOfFolders > 0) {
					folderCursor.moveToFirst();
					for (int i = 0; i < countOfFolders; i++) {
						String name = folderCursor.getString(folderCursor.getColumnIndex(NoteItems.CONTENT));
						writeFolderName("文件夹名称：", name);
						int _id = folderCursor.getInt(folderCursor.getColumnIndex(NoteItems._ID));
						Cursor notesInOneFolder = context.getContentResolver().query(NoteItems.CONTENT_URI, null, NoteItems.PARENT_FOLDER + " = ? ", new String[] {String.valueOf(_id)}, null);
						int noteCountInOneFolder = notesInOneFolder.getCount();
						if (noteCountInOneFolder > 0) {
							notesInOneFolder.moveToFirst();
							for (int j = 0; j < noteCountInOneFolder; j++) {
								String date = notesInOneFolder.getString(notesInOneFolder.getColumnIndex(NoteItems.UPDATE_DATE));
								String time = notesInOneFolder.getString(notesInOneFolder.getColumnIndex(NoteItems.UPDATE_TIME));
								String content = notesInOneFolder.getString(notesInOneFolder.getColumnIndex(NoteItems.CONTENT));
								writeNoteInfo(date, time, content);
								notesInOneFolder.moveToNext();
							}
							notesInOneFolder.close();
						}
						folderCursor.moveToNext();
					}
				}
				folderCursor.close();
				
				
				writeFolderName("", "Mynotes");
				nWriter.write("\n" + "\n");
				Cursor  rootNotes = context.getContentResolver().query(NoteItems.CONTENT_URI, null, NoteItems.IS_FOLDER + " = '" + "no" + "' and " + NoteItems.PARENT_FOLDER + " = " + "-1", null, null);
				int countOfRootNotes = rootNotes.getCount();
				if (countOfRootNotes > 0) {
					rootNotes.moveToFirst();
					for (int j = 0; j < countOfRootNotes; j++) {
						String date = rootNotes.getString(rootNotes.getColumnIndex(NoteItems.UPDATE_DATE));
						String time = rootNotes.getString(rootNotes.getColumnIndex(NoteItems.UPDATE_TIME));
						String content = rootNotes.getString(rootNotes.getColumnIndex(NoteItems.CONTENT));
						writeNoteInfo(date, time, content);
						rootNotes.moveToNext();
					}
				}
				rootNotes.close();
				
				
				nWriter.flush();
				nWriter.close();
				
			} else {
				return false;
			}
			
			return true;
		}
		return false;
	}
	
	private void writeFolderName(String prefix, String name) throws IOException {
		
		nWriter.write("------------------" + "\n");
		nWriter.write(" " + prefix + " " + name + "\n");
		nWriter.write("------------------" + "\n");
		nWriter.write(" " + "\n");
		
	}
	
	private void writeNoteInfo(String date, String time, String content) throws IOException {
		
		nWriter.write(date + "\t" + time + "\n");
		nWriter.write(content + "\n" + "\n");
		
	}
	
}
