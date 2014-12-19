package com.xg.mynotes;

import java.lang.reflect.Method;

import com.xg.mynotes.R.layout;
import com.xg.mynotes.adapter.MyCursorAdapter;
import com.xg.mynotes.db.DateTimeUtil;
import com.xg.mynotes.db.DbInfo.NoteItems;
import com.xg.mynotes.log.Logs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class FolderNotesActivity extends Activity {
	private ImageButton imageButton;
	private TextView tv_Title;
	private ListView mListView;
	
	
	private MyCursorAdapter mAdapter;
	
	private Cursor mCursor;
	
	private String oldFolderName;
	
	private final String ACTION_ADD_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
	
	private static final int MENU_NEW_NOTE = Menu.FIRST;
	private static final int MENU_UNDATE_FOLDER = Menu.FIRST + 1;
	private static final int MENU_MOVE_OUTOF_FOLDER = Menu.FIRST + 2;
	private static final int MENU_DELETE = Menu.FIRST + 3;
	private static final int MENU_SEND_HOME = Menu.FIRST + 4;
	
	private int _id;
	private static final String TAG = "FolderNotesActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.index_page);
		
		Intent intent = getIntent();
		if(intent.equals(null)) {
			//startActivity(new Intent(FolderNotesActivity.this, MainActivity.class));
			FolderNotesActivity.this.finish();
		}
		_id = intent.getIntExtra(NoteItems._ID, -1);
		Uri tmpUri = ContentUris.withAppendedId(NoteItems.CONTENT_URI, _id);
		//Logs.d(TAG, "position :1");
		Cursor cursor2 = getContentResolver().query(tmpUri, null, null, null, null);
		//Logs.d(TAG, "position :2");
		cursor2.moveToFirst();
		//Logs.d(TAG, "position :3");
		oldFolderName = cursor2.getString(cursor2.getColumnIndex(NoteItems.CONTENT));
		cursor2.close();
		//Logs.d(TAG, "position :4");
		initViews();
		//Logs.d(TAG, "position :5");
		mListView.setOnItemClickListener(new OnItemClickListener() {

        	//点击文件夹或者便签执行该回调函数
        	@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
				//mCursor在 updateDisplay函数中进行初始化
				mCursor.moveToPosition(position);
				Logs.d(TAG, "被点击的纪录的Position：" + position);
				//传递被选中纪录的ID
				intent.putExtra(NoteItems._ID, mCursor.getInt(mCursor.getColumnIndex(NoteItems._ID)));
				intent.putExtra("FolderId", _id);
				Logs.d(TAG, "进入id为：" + _id + "  的文件夹");
				intent.putExtra(NoteItems.CONTENT, mCursor.getString(mCursor.getColumnIndex(NoteItems.CONTENT)));
				intent.putExtra("Open_Type", "editFolderNote");
				intent.setClass(FolderNotesActivity.this, NoteActivity.class);
				startActivity(intent);
			}
		});
	}

	private void initViews() {
		imageButton = (ImageButton) findViewById(R.id.addButton);
		imageButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				newFolderNote();
			}
		});
		mListView = (ListView) findViewById(R.id.list);
		tv_Title = (TextView) findViewById(R.id.tvTitle);
		
		updateDisplay(oldFolderName);
		
	}

	private void updateDisplay(String folderName) {
		String selection = NoteItems.PARENT_FOLDER + " = ? ";
		String[] selectionArgs = new String[] {
				String.valueOf(_id)
		};
		mCursor = getContentResolver().query(NoteItems.CONTENT_URI, null, selection, selectionArgs, null);
		startManagingCursor(mCursor);
		mAdapter = new MyCursorAdapter(this, mCursor, true);
		mListView.setAdapter(mAdapter);
		tv_Title.setText("文件夹：" + folderName) ;
		
	}

	protected void newFolderNote() {
		
		Intent intent = new Intent();
		intent.putExtra("Open_Type", "newFolderNote");
		intent.putExtra("FolderId", _id);
		intent.setClass(FolderNotesActivity.this, NoteActivity.class);
		startActivity(intent);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		setIconEnable(menu, true);
		
		menu.add(Menu.NONE, MENU_NEW_NOTE, 1, R.string.new_note)
			.setIcon(R.drawable.new_note);
		menu.add(Menu.NONE, MENU_UNDATE_FOLDER, 2, R.string.update_folder_name)
			.setIcon(R.drawable.update_folder_name);
		menu.add(Menu.NONE, MENU_MOVE_OUTOF_FOLDER, 3, R.string.move_out_folder)
			.setIcon(R.drawable.move_out_folder);
		menu.add(Menu.NONE, MENU_DELETE, 4, R.string.delete_note)
			.setIcon(R.drawable.delete);
		menu.add(Menu.NONE, MENU_SEND_HOME, 5, R.string.add_shortcut_to_home)
			.setIcon(R.drawable.add_shortcut_to_home);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_NEW_NOTE:
			newFolderNote();
			break;
		case MENU_UNDATE_FOLDER:
			updateFolderName();
			break;
		case MENU_MOVE_OUTOF_FOLDER:
			MoveOutFolder();
			break;
		case MENU_DELETE:
			delete();
			break;
		case MENU_SEND_HOME:
			sendToHome();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	 private void MoveOutFolder() {
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), MoveOutFolderActivity.class);
		intent.putExtra("folderId", _id);
		startActivity(intent);
		
	}

	private void delete() {
		 Intent intent = new Intent(getApplicationContext(), DeleteNotesActivity.class);
		 intent.putExtra("folderId", _id);
		 startActivity(intent);
		
	}

	private void sendToHome() {
		 Intent addShortCut = new Intent(ACTION_ADD_SHORTCUT);
			addShortCut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(this, R.drawable.update_folder_name));
			addShortCut.putExtra(Intent.EXTRA_SHORTCUT_NAME, oldFolderName);
			addShortCut.putExtra("duplicate", false);
			Intent shortCutIntent = new Intent(FolderNotesActivity.this, FolderNotesActivity.class);
			shortCutIntent.putExtra(NoteItems._ID, _id);
			addShortCut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortCutIntent);
			sendBroadcast(addShortCut);
			Toast.makeText(this, "发送成功", Toast.LENGTH_SHORT).show();		
	}

	private void updateFolderName() {
		Context mContext = FolderNotesActivity.this;
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(R.string.update_folder_name);
		builder.setIcon(R.drawable.update_folder_name);
		
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		final View layout = inflater.inflate(R.layout.dialog_new_folder, (ViewGroup) findViewById(R.id.dialog_layout_new_folder_root));
		builder.setView(layout);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				EditText et_folder_name = (EditText) layout.findViewById(R.id.et_dialog_new_folder);
				String newFolderName = et_folder_name.getText().toString();
				if(!TextUtils.isEmpty(newFolderName) && !newFolderName.equals(oldFolderName)) {
					Uri tmpUri = ContentUris.withAppendedId(NoteItems.CONTENT_URI, _id);
					ContentValues values = new ContentValues();
					values.put(NoteItems.CONTENT, newFolderName);
					values.put(NoteItems.UPDATE_DATE, DateTimeUtil.getDate());
					values.put(NoteItems.UPDATE_TIME, DateTimeUtil.getTime());
					values.put(NoteItems.IS_FOLDER, "yes");
					values.put(NoteItems.PARENT_FOLDER, -1);
					getContentResolver().update(tmpUri, values, null, null);
					oldFolderName = newFolderName;
					tv_Title.setText( "文件夹：" + oldFolderName);
				}
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	private void setIconEnable(Menu menu, boolean enable){
		    try 
		    {
				Class<?> clazz = Class.forName("com.android.internal.view.menu.MenuBuilder");
				Method m = clazz.getDeclaredMethod("setOptionalIconsVisible", boolean.class);
				m.setAccessible(true);
			
				//MenuBuilder实现Menu接口，创建菜单时，传进来的menu其实就是MenuBuilder对象(java的多态特征)
				m.invoke(menu, enable);
		    
		    } catch (Exception e) {
				e.printStackTrace();
		    	}
		}
}
