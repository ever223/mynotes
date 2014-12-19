package com.xg.mynotes;

import java.util.HashMap;

import com.xg.mynotes.adapter.ListItemView;
import com.xg.mynotes.adapter.MyCursorAdapter;
import com.xg.mynotes.db.DbInfo.NoteItems;
import com.xg.mynotes.log.Logs;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MoveToFolderActivity extends Activity {

	private Button okButton;
	private Button cancelButton;
	private ListView mListView;
	private HashMap<Integer, Integer> mIds;
	private Cursor mCursor;
	private MyCursorAdapter mAdapter;
	private OnClickListener listener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnOK:
				chooseFolder();
				break;
			case R.id.btnCancelDel:
				finish();
			default:
				break;
			}
			
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.listview_layout_del_or_move_records);
		
		okButton = (Button) findViewById(R.id.btnOK);
		cancelButton = (Button) findViewById(R.id.btnCancelDel);
		mListView = (ListView) findViewById(R.id.listview);
		mIds = new HashMap<Integer, Integer>();
		
		String selection = NoteItems.IS_FOLDER + " = '" +"no" + "' and " + NoteItems.PARENT_FOLDER + " = " + "-1";
		mCursor = getContentResolver().query(NoteItems.CONTENT_URI, null, selection, null, null);
		startManagingCursor(mCursor);
		
		mAdapter = new MyCursorAdapter(getApplicationContext(), mCursor, false);
		mListView.setAdapter(mAdapter);
		mListView.setItemsCanFocus(false);
		mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ListItemView listItemView = (ListItemView) view.getTag();
				listItemView.cb_right.toggle();
				MyCursorAdapter.isSelected.put(position, listItemView.cb_right.isChecked());
				mCursor.moveToPosition(position);
				int itemId = mCursor.getInt(mCursor.getColumnIndex(NoteItems._ID));
				if(MyCursorAdapter.isSelected.get(position)) {
					mIds.put(position, itemId);
					Logs.d("MoveToFolderActivity", "被点击的纪录的id：" + itemId + "\t" + position);
				} else {
					mIds.remove(position);
					Logs.d("MoveToFolderActivity", "remove==>被点击的纪录的id：" + itemId + "\t" + position);
				}
			}
			
		});
		okButton.setOnClickListener(listener );
		cancelButton.setOnClickListener(listener);
	}

	protected void chooseFolder() {
		final int noteCount = mIds.size();
		Logs.d("MoveToFolderActivity", "被选择的纪录的数量：" + noteCount);
		if(noteCount > 0) {
			String selection = NoteItems.IS_FOLDER + " = ? ";
			String[] selectionArgs = new String[] { "yes" };
			final Cursor folderCursor = getContentResolver().query(NoteItems.CONTENT_URI, null, selection, selectionArgs, null);
			int count = folderCursor.getCount();
			Logs.d("MoveToFolderActivity", "文件夹的数量：" + count);
			if(count > 0) {
				String[] folders = new String[count];
				startManagingCursor(folderCursor);
				for (int i = 0; i < count; i++) {
					folderCursor.moveToPosition(i);
					folders[i] = folderCursor.getString(folderCursor.getColumnIndex(NoteItems.CONTENT));
					
				}
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setItems(folders, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						folderCursor.moveToPosition(which);
						int folderId = folderCursor.getInt(folderCursor.getColumnIndex(NoteItems._ID));
						int count = mCursor.getCount();
						for (int i = 0; i < count; i++) {
							String strTmp = String.valueOf(mIds.get(i));
							if (!(strTmp == "null")) {
								int noteId = mIds.get(i);
								Uri tmpuUri = ContentUris.withAppendedId(NoteItems.CONTENT_URI, noteId);
								ContentValues values = new ContentValues();
								values.put(NoteItems.PARENT_FOLDER, folderId);
								getContentResolver().update(tmpuUri, values, null, null);
								Logs.d("MoveToFolderActivity", "要将选中的笔记移进id为：" + folderId);
							}
						}
						finish();
						
					}
				});
				builder.create().show();
				
			} else {
				Toast.makeText(getApplicationContext(), " 没有文件夹！", Toast.LENGTH_SHORT)
				.show();
			}
		} else {
			Toast.makeText(getApplicationContext(), " 您没有选中任何笔记！", Toast.LENGTH_SHORT)
				.show();
		}
		
	}
	
}
















