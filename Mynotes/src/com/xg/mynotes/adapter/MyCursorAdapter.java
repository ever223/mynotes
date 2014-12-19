package com.xg.mynotes.adapter;

import java.util.HashMap;
import java.util.Map;

import com.xg.mynotes.R;
import com.xg.mynotes.db.DbInfo.NoteItems;
import com.xg.mynotes.log.Logs;

import android.R.integer;
import android.app.LauncherActivity.ListItem;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyCursorAdapter extends CursorAdapter {
	public static final String TAG = "MyCursorAdapter";
	//标记是删除、移动纪录还是显示纪录
	private boolean isShowingRecords = true;
	
	private ListItemView listItemView = null;
	
	public static Map<Integer, Boolean> isSelected;
	
	private LayoutInflater mListContainer;
	
	public MyCursorAdapter(Context context, Cursor c, boolean autoRequery) {
		// *
		super(context, c, autoRequery);
		this.isShowingRecords = autoRequery;
		mListContainer = LayoutInflater.from(context);
		isSelected = new HashMap<Integer, Boolean>();
		int count = c.getCount();
		for (int i = 0; i < count; i ++) {
			isSelected.put(i, false);
		}
		
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View convertView = null;
		listItemView  = new ListItemView();
		if(!isShowingRecords) {
			convertView = mListContainer.inflate(R.layout.listview_del_or_move_item_layout, null);
			listItemView.tv_left = (TextView) convertView.findViewById(R.id.tv_left);
			listItemView.cb_right = (CheckBox) convertView.findViewById(R.id.cb_right);
			
		} else {
			convertView = mListContainer.inflate(R.layout.listview_item_layout, null);
			listItemView.tv_left = (TextView) convertView.findViewById(R.id.tv_left);
			listItemView.tv_right = (TextView) convertView.findViewById(R.id.tv_right);
		}
		listItemView.linearLayout = (LinearLayout) convertView.findViewById(R.id.listview_linearlayout);
		convertView.setTag(listItemView);
		return convertView;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		listItemView = (ListItemView) view.getTag();
		int position = cursor.getPosition();
		String is_Folder = cursor.getString(cursor.getColumnIndex(NoteItems.IS_FOLDER));
		if(is_Folder.equals("no")) {
			int bg_color = cursor.getInt(cursor.getColumnIndex(NoteItems.BACKGROUND_COLOR));
			Logs.d(TAG, "== >  数据库中存储的纪录的背景颜色" + bg_color);
			listItemView.linearLayout.setBackgroundResource(bg_color);
		} else if(is_Folder.equals("yes")) {
			listItemView.linearLayout.setBackgroundResource(R.drawable.folder_background);
			
		}
		String content = cursor.getString(cursor.getColumnIndex(NoteItems.CONTENT));
		int count = content.indexOf("\n");
		Logs.d(TAG, "== > 第一个换行符的位置：" + count);
		if(count > -1 && count < 16) {
			if(is_Folder.equals("yes")) {
				listItemView.tv_left.setText("文件夹：" + content.substring(0,count));
			} else {
				listItemView.tv_left.setText(content.substring(0,count));
			}
		} else if(content.length() > 16) {
			if(is_Folder.equals("yes")) {
				listItemView.tv_left.setText("文件夹：" + content.substring(0,10));
			} else {
				listItemView.tv_left.setText(content.substring(0,16));
			}
		} else {
			if(is_Folder.equals("yes")) {
				listItemView.tv_left.setText("文件夹：" + content);
			} else {
				listItemView.tv_left.setText(content);
			}
		}
		
		if(!isShowingRecords) {
			listItemView.cb_right.setChecked(isSelected.get(position));
		} else {
			listItemView.tv_right.setText(cursor.getString(cursor.getColumnIndex(NoteItems.UPDATE_DATE))
					+ "\t"
					+ cursor.getString(cursor.getColumnIndex(NoteItems.UPDATE_TIME)).substring(0, 5));
		}
	}

}
