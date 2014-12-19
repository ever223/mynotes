package com.xg.mynotes;

import java.lang.reflect.Method;
import java.util.ArrayList;

import android.R.integer;
import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.LoaderManager;
import android.app.backup.BackupDataInput;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.xg.mynotes.adapter.MyCursorAdapter;
import com.xg.mynotes.db.DateTimeUtil;
import com.xg.mynotes.db.DbInfo.NoteItems;
import com.xg.mynotes.log.*;
import com.xg.mynotes.txt_xml.RestoreDataFromXml;
import com.xg.mynotes.txt_xml.WriteTxt;
import com.xg.mynotes.txt_xml.WriteXml;
public class MainActivity extends Activity  {

	//gesture
//	private GestureOverlayView mGestureOverlayView;
//	private GestureLibrary mGestLibrary;
//	private String GestureName_Add = "add_Record";
	
	//layout 中的控件 
	private ImageButton addButton;
	private ListView mListView;
	
	private Cursor mCursor;
	private MyCursorAdapter mAdapter;
	
	public static final String TAG ="NoteActivity";
	private static final String SETTINGS = "user_configurations";
	
	//menu
	private static final int MENU_NEW_NOTE = Menu.FIRST;
	private static final int MENU_NEW_FOLDER = Menu.FIRST + 1;
	private static final int MENU_MOVE_TO_FOLDER = Menu.FIRST + 2;
	private static final int MENU_DELETE = Menu.FIRST + 3;
	private static final int MENU_EXPORT_TO_TEXT = Menu.FIRST + 4;
	private static final int MENU_BACKUP_DATA = Menu.FIRST + 5;
	private static final int MENU_RESTORE_DATA_FROM_SACARD = Menu.FIRST + 6;
	private static final int MENU_SET_PASSWORD = Menu.FIRST + 7;
	//private static final int MENU_ABOUT = Menu.FIRST + 8;
	
	private int count = 0;
	

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        requestWindowFeature(Window.FEATURE_NO_TITLE);		 //取消标题栏
        setContentView(R.layout.index_page);
        this.inputPsd();
        // * load gestureLibrary file 
//        mGestLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
//        if (mGestLibrary.load()) {
//			mGestureOverlayView = (GestureOverlayView) findViewById(R.id.gestureOverlayView);
//			mGestureOverlayView.addOnGesturePerformedListener(this);
//		}
        
        //update ListView's data
        mListView = (ListView) findViewById(R.id.list);
        this.updateDisplay();
        
        mListView.setOnItemClickListener(new OnItemClickListener() {

        	//点击文件夹或者便签执行该回调函数
        	@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
				//mCursor在 updateDisplay函数中进行初始化
				mCursor.moveToPosition(position);
				Logs.d(TAG, "MainActivity==>被点击的纪录的Position：" + position);
				//传递被选中纪录的ID
				intent.putExtra(NoteItems._ID, mCursor.getInt(mCursor.getColumnIndex(NoteItems._ID)));
				//取得此纪录的IS_FOLDER  字段的值，判断选中的是folder or note
				String is_Folder = mCursor.getString(mCursor.getColumnIndex(NoteItems.IS_FOLDER));
				if(is_Folder.equals("no")) {	//if it's not folder, go to this note's content page
					intent.putExtra(NoteItems.CONTENT, mCursor.getString(mCursor.getColumnIndex(NoteItems.CONTENT)));
					//tell NoteActivity edit this note
					intent.putExtra("Open_Type", "editNote");
					intent.setClass(MainActivity.this, NoteActivity.class);
					//Logs.d(TAG, "complete editNote");
				} else if(is_Folder.equals("yes")){
					intent.setClass(MainActivity.this, FolderNotesActivity.class);
				}
				startActivity(intent);
			}
		});
        initViews();
        
    }
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	setIconEnable(menu, true);
		menu.add(Menu.NONE, MENU_NEW_NOTE, 1, R.string.new_note)
			.setIcon(R.drawable.new_note);
		menu.add(Menu.NONE, MENU_NEW_FOLDER, 2, R.string.new_folder)
		.setIcon(R.drawable.new_folder);
		menu.add(Menu.NONE, MENU_MOVE_TO_FOLDER, 3, R.string.move_to_folder)
		.setIcon(R.drawable.move_to_folder);
		menu.add(Menu.NONE, MENU_DELETE, 4, R.string.delete)
		.setIcon(R.drawable.delete);
		menu.add(Menu.NONE, MENU_EXPORT_TO_TEXT, 5, R.string.export_to_text)
		.setIcon(R.drawable.export_to_text);
		menu.add(Menu.NONE, MENU_BACKUP_DATA, 6, R.string.backup_data)
		.setIcon(R.drawable.backup_data);
		menu.add(Menu.NONE, MENU_RESTORE_DATA_FROM_SACARD, 7, R.string.restore_data)
		.setIcon(R.drawable.restore_data);
		menu.add(Menu.NONE, MENU_SET_PASSWORD, 8, R.string.set_password)
		.setIcon(R.drawable.set_password);
		return super.onCreateOptionsMenu(menu);
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
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_NEW_NOTE:
			newNote();
			break;
		case MENU_NEW_FOLDER:
			newFolder();
			break;
		case MENU_MOVE_TO_FOLDER:
			moveToFolder();
			break;
		case MENU_DELETE:
			delete();
			break;
		case MENU_EXPORT_TO_TEXT:
			exportToTxt();
			break;
		case MENU_BACKUP_DATA:
			BackupData();
			break;
		case MENU_RESTORE_DATA_FROM_SACARD:
			restoreDataFromSDCard();
			break;
		case MENU_SET_PASSWORD:
			psdDialog();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void restoreDataFromSDCard() {
		RestoreDataFromXml rsd = new RestoreDataFromXml(getContentResolver());
		try {
			rsd.restoreData();
			mAdapter.notifyDataSetChanged();
			Toast.makeText(this, R.string.restoreDataSuc, Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Toast.makeText(this, R.string.restoreDataFailed, Toast.LENGTH_SHORT).show();
			Logs.d(TAG, "== > restoreData failed exception:" + e.getMessage());
			e.printStackTrace();
		}
	}
	private void BackupData() {
		WriteXml wx = new WriteXml(this);
		try {
			if(!wx.writeXml()) {
				Toast.makeText(this, R.string.backupDataFailed, Toast.LENGTH_SHORT)
					 .show();
				Logs.d(TAG, "==> backup to SDCard failed!");
			} else {
				Toast.makeText(this, R.string.backupDataSucc, Toast.LENGTH_SHORT)
				 	 .show();
				Logs.d(TAG, "==> backup to SDCard Successfully!");
			}
		} catch (Exception e) {
			Logs.d(TAG, "== > backupData get Exception: " + e.getMessage());
			e.printStackTrace();
		}
		
	}
	private void exportToTxt() {
		WriteTxt wt = new WriteTxt(this);
		try {
			
			if(!wt.writeTxt()) {
				Toast.makeText(this, R.string.exportTXTFailed, Toast.LENGTH_SHORT)
					 .show();
			} else {
				Toast.makeText(this, R.string.exportTXTSucc, Toast.LENGTH_SHORT)
					 .show();
			}
			
		} catch (Exception e) {
			
			Logs.d(TAG, "==> exportToTxt() get Exception: " + e.getMessage());
			e.printStackTrace();
			
		}
		
	}
	private void moveToFolder() {
		
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), MoveToFolderActivity.class);
		startActivity(intent);
		
	}
	private void delete() {
		
		Intent intent = new Intent(getApplicationContext(), DeleteNotesActivity.class);
		startActivity(intent);
		
	}
	private void newFolder() {
		Context mContext = MainActivity.this;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.new_folder);
		builder.setIcon(R.drawable.new_folder);
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		final View layout = inflater.inflate(R.layout.dialog_new_folder, (ViewGroup) findViewById(R.id.dialog_layout_new_folder_root));
		builder.setView(layout);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				EditText et_folder_name = (EditText) layout.findViewById(R.id.et_dialog_new_folder);
				String newFolderName = et_folder_name.getText().toString();
				if(!TextUtils.isEmpty(newFolderName)) {
					ContentValues values = new ContentValues();
					values.put(NoteItems.CONTENT, newFolderName);
					values.put(NoteItems.UPDATE_DATE, DateTimeUtil.getDate());
					values.put(NoteItems.UPDATE_TIME, DateTimeUtil.getTime());
					values.put(NoteItems.IS_FOLDER, "yes");
					values.put(NoteItems.PARENT_FOLDER, -1);
					getContentResolver().insert(NoteItems.CONTENT_URI, values);
					mAdapter.notifyDataSetChanged();
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
	private void psdDialog() {
		SharedPreferences settings = getSharedPreferences(SETTINGS, MODE_PRIVATE);
		String psd = settings.getString("psd", "");
		if(psd.length() > 0) {
			final CharSequence[] items = {
					getResources().getString(R.string.change_password),
					getResources().getString(R.string.clear_password)
			};
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setItems(items, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0:
						setPassword(R.string.change_password);
						break;
					case 1:
						SharedPreferences settings = getSharedPreferences(SETTINGS, MODE_PRIVATE);
						Editor editor = settings.edit();
						editor.putString("psd", "");
						editor.commit();
						Toast.makeText(MainActivity.this, getResources().getString(R.string.clear_password) + "成功", Toast.LENGTH_SHORT)
							.show();
						break;
					default:
						break;
					}
					
				}
			});
			builder.create().show();
		} else {
			this.setPassword(R.string.set_password);
		}
	}
	protected void setPassword(int resId) {
		final int name = resId;
		Context mconContext = MainActivity.this;
		AlertDialog.Builder builder = new AlertDialog.Builder(mconContext);
		LayoutInflater inflater = (LayoutInflater) mconContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		final View layout = inflater.inflate(R.layout.dialog_layout_set_password, (ViewGroup) findViewById(R.id.dialog_layout_set_password_root));
		builder.setView(layout);
		builder.setTitle(name);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				EditText et_psw1 = (EditText) layout.findViewById(R.id.et_password);
				String psw1 = et_psw1.getText().toString();
				EditText et_psw2 = (EditText) layout.findViewById(R.id.et_confirm_password);
				String psw2 = et_psw2.getText().toString();
				if (!TextUtils.isEmpty(psw1) && (psw1.equals(psw2))) {
					SharedPreferences settings = getSharedPreferences(SETTINGS, MODE_PRIVATE);
					Editor editor = settings.edit();
					editor.putString("psd", psw1);
					editor.commit();
					switch (name) {
					case R.string.change_password:
						Toast.makeText(MainActivity.this, getResources().getString(R.string.change_password_succ), Toast.LENGTH_SHORT)
							.show();
						break;
					case R.string.set_password:
						Toast.makeText(MainActivity.this, getResources().getString(R.string.set_password_succ), Toast.LENGTH_SHORT)
							.show();
						break;
					default:
						break;
					}
				} else {
					switch (name) {
					case R.string.change_password:
						Toast.makeText(MainActivity.this, getResources().getString(R.string.change_password_failed), Toast.LENGTH_SHORT)
							.show();
						break;
					case R.string.set_password:
						Toast.makeText(MainActivity.this, getResources().getString(R.string.set_password_failed), Toast.LENGTH_SHORT)
							.show();
						break;
					default:
						break;
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					setPassword(name);
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
	private void inputPsd() {
		count ++;
		SharedPreferences settings = getSharedPreferences(SETTINGS, MODE_PRIVATE);
		final String psd = settings.getString("psd", "");
		if(psd.length() > 0) {
			Context mContext = MainActivity.this;
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setTitle("输入密码");
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
			final View layout = inflater.inflate(R.layout.dialog_input_psd, (ViewGroup) findViewById(R.id.dialog_layout_set_password_root));
			builder.setView(layout);
			builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					EditText et_psd = (EditText) layout.findViewById(R.id.et_password);
					String psd_inputted = et_psd.getText().toString();
					if (!psd.equals(psd_inputted)) {
						Toast.makeText(MainActivity.this, "密码不正确,请重新输入！！！", Toast.LENGTH_SHORT)
							.show();
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if(count < 3) {
							inputPsd();
						} else {
							MainActivity.this.finish();
						}
					}
				}
			});
			builder.setCancelable(false);
			builder.create().show();
		}
	}
 	private void updateDisplay() {
		Logs.d(TAG, "updateDisplay : 1");
		String selection = NoteItems.IS_FOLDER + " = '" + "yes" + "' or " + NoteItems.PARENT_FOLDER + " = " + "-1";
		mCursor = getContentResolver().query(NoteItems.CONTENT_URI, null, selection, null, null);
		//*
		startManagingCursor(mCursor);
		mAdapter = new MyCursorAdapter(this, mCursor, true);
		mListView.setAdapter(mAdapter);
		Logs.d(TAG, "== > update display finished...");
		
	}
	private void initViews() {
		addButton = (ImageButton) findViewById(R.id.addButton);
		addButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				newNote();
				
			}

		});
	}
	private void newNote() {
		Intent intent = new Intent();
		intent.putExtra("Open_Type", "newNote");	//bug  error : Open_type
		intent.setClass(MainActivity.this, NoteActivity.class);
		startActivity(intent);
		
	}
//	@Override
//	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
//		ArrayList<Prediction> predictions = mGestLibrary.recognize(gesture);
//		for(Prediction prediction : predictions) {
//			Logs.d(TAG, "== > onGesturePerformed() -- > 手势相似度：" + prediction.score);
//			if(prediction.score > 2.0 && prediction.name.equals(this.GestureName_Add)) {
//				newNote();
//			}
//		}
//		
//	}
}
