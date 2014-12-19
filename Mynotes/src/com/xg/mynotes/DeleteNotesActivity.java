package com.xg.mynotes;

import java.util.HashMap;
import java.util.Map;

import com.xg.mynotes.adapter.ListItemView;
import com.xg.mynotes.adapter.MyCursorAdapter;
import com.xg.mynotes.db.DbInfo.NoteItems;
import com.xg.mynotes.log.Logs;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class DeleteNotesActivity extends Activity {
	private MyCursorAdapter mAdapter;
	
	private ListView mListView;
	private Button okButton, cancelButton;
	
	private Map<Integer, Integer> mIds;
	private Cursor mCursor;
	
	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnOK:
				int count = mCursor.getCount();
				if(count > 0) {
					for(int i = 0; i < count; i++) {
						String strTmp = String.valueOf(mIds.get(i));
						if(!TextUtils.isEmpty(strTmp)) {
							String  id = String.valueOf(mIds.get(i));
							getContentResolver().delete(NoteItems.CONTENT_URI, "_id = ? or " + NoteItems.PARENT_FOLDER + " = ? ", new String[] {id, id});
						}
					}
				}
				finish();
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
		
		int folderId = getIntent().getIntExtra("folderId", -1);
		if(folderId == -1) {
			String selection = NoteItems.IS_FOLDER + " = '" + "yes" + "' or " + NoteItems.PARENT_FOLDER + " = " + "-1";
			mCursor = getContentResolver().query(NoteItems.CONTENT_URI, null, selection, null, null);
			
		} else {
			String selection = NoteItems.PARENT_FOLDER + " = ? ";
			String[] selectionArgs = new String[] {
					String.valueOf(folderId)
					};
			mCursor = getContentResolver().query(NoteItems.CONTENT_URI, null, selection, selectionArgs, null);
		}
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
					Logs.d("DeleteNotesActivity", "== >被点击的纪录的id：" + itemId + "\t" + position);
					
				} else {
					mIds.remove(position);
				}
			}
		});
		okButton.setOnClickListener(listener);
		cancelButton.setOnClickListener(listener);
	}
}























