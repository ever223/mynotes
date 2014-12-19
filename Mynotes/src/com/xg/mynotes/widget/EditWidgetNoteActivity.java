package com.xg.mynotes.widget;

import com.xg.mynotes.R;
import com.xg.mynotes.db.ChooseColor;
import com.xg.mynotes.db.DbInfo.AppwidgetItems;
import com.xg.mynotes.log.Logs;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EditWidgetNoteActivity extends Activity {
	public static final String SHAREDPREF = "widget_note_id";
	private LinearLayout mLinearLayout_Header;
	private LinearLayout mainBackground;
	private ImageButton ib_bgcolor;
	private TextView tv_widget_title;
	private EditText et_content;
	private int mBackground_Color;
	private boolean is4X4 = false;
	private String updateDate;
	private String updateTime;
	private int _id;
	private int mAppWidgetId;
	private static final String TAG = "EditWidgetNote";
	private ChooseColor chooseColor = new ChooseColor();
	private View.OnClickListener listener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			AlertDialog.Builder builder = new AlertDialog.Builder(EditWidgetNoteActivity.this);
			LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.dialog_note_bg_color, null);
			final AlertDialog dialog = builder.create();
			dialog.setView(view, 0, 0, 0, 0);
			ImageButton colorButton1 = (ImageButton) view.findViewById(R.id.styleButton1);
			ImageButton colorButton2 = (ImageButton) view.findViewById(R.id.styleButton2);
			ImageButton colorButton3 = (ImageButton) view.findViewById(R.id.styleButton3);
			ImageButton colorButton4 = (ImageButton) view.findViewById(R.id.styleButton4);
			ImageButton colorButton5 = (ImageButton) view.findViewById(R.id.styleButton5);
			ImageButton colorButton6 = (ImageButton) view.findViewById(R.id.styleButton6);
			ImageButton colorButton7 = (ImageButton) view.findViewById(R.id.styleButton7);
			ImageButton colorButton8 = (ImageButton) view.findViewById(R.id.styleButton8);
			ImageButton colorButton9 = (ImageButton) view.findViewById(R.id.styleButton9);
			ImageButton colorButton10 = (ImageButton) view.findViewById(R.id.styleButton10);
			ImageButton colorButton11 = (ImageButton) view.findViewById(R.id.styleButton11);
			ImageButton colorButton12 = (ImageButton) view.findViewById(R.id.styleButton12);
			ImageButton colorButton13 = (ImageButton) view.findViewById(R.id.styleButton13);
			ImageButton colorButton14 = (ImageButton) view.findViewById(R.id.styleButton14);
			ImageButton colorButton15 = (ImageButton) view.findViewById(R.id.styleButton15);
			ImageButton colorButton16 = (ImageButton) view.findViewById(R.id.styleButton16);
			
			Button.OnClickListener listener = new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					ChooseColor chooseColor = new ChooseColor();
					mBackground_Color = chooseColor.contentBackground(v.getId());
					et_content.setBackgroundResource(mBackground_Color);
					mainBackground.setBackgroundResource(chooseColor.mainBackground(mBackground_Color));
					mLinearLayout_Header.setBackgroundResource(chooseColor.headerBackground(mBackground_Color));
					dialog.dismiss();
				}
			};
			colorButton1.setOnClickListener(listener);
			colorButton2.setOnClickListener(listener);
			colorButton3.setOnClickListener(listener);
			colorButton4.setOnClickListener(listener);
			colorButton5.setOnClickListener(listener);
			colorButton6.setOnClickListener(listener);
			colorButton7.setOnClickListener(listener);
			colorButton8.setOnClickListener(listener);
			colorButton9.setOnClickListener(listener);
			colorButton10.setOnClickListener(listener);
			colorButton11.setOnClickListener(listener);
			colorButton12.setOnClickListener(listener);
			colorButton13.setOnClickListener(listener);
			colorButton14.setOnClickListener(listener);
			colorButton15.setOnClickListener(listener);
			colorButton16.setOnClickListener(listener);
			dialog.show();
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.widget_note_layout);
		Intent intent = getIntent();
		mAppWidgetId = intent.getIntExtra("widget_id", -1);
		is4X4 = intent.getBooleanExtra("is4X4", false);
		Logs.d(TAG, "== > 点击的Appwidget的id：" + mAppWidgetId);
		if (mAppWidgetId == -1) {
			finish();
		}
		SharedPreferences prefs = this.getSharedPreferences(EditWidgetNoteActivity.SHAREDPREF, Context.MODE_WORLD_READABLE);
		_id = (int) prefs.getLong(EditWidgetNoteActivity.SHAREDPREF + mAppWidgetId, -1);
		Logs.d(TAG, "==> 从SharePreferences中读到的AppWidget的id：" + _id);
		mLinearLayout_Header = (LinearLayout) findViewById(R.id.widget_detail_header);
		et_content = (EditText) findViewById(R.id.et_content);
		ib_bgcolor = (ImageButton) findViewById(R.id.imagebutton_bgcolor);
		mainBackground = (LinearLayout) findViewById(R.id.widget40);
		tv_widget_title = (TextView) findViewById(R.id.tv_widget_date_time);
		if(_id != -1) {
			Cursor cursor = getContentResolver().query(ContentUris.withAppendedId(AppwidgetItems.CONTENT_URI, _id), null, null, null, null);
			cursor.moveToFirst();
			String content = cursor.getString(cursor.getColumnIndex(AppwidgetItems.CONTENT));
			et_content.setText(content);
			mBackground_Color = cursor.getInt(cursor.getColumnIndex(AppwidgetItems.BACKGROUND_COLOR));
			et_content.setBackgroundResource(mBackground_Color);
			mainBackground.setBackgroundResource(chooseColor.mainBackground(mBackground_Color));
			mLinearLayout_Header.setBackgroundResource(chooseColor.headerBackground(mBackground_Color));
			updateDate = cursor.getString(cursor.getColumnIndex(AppwidgetItems.UPDATE_DATE));
			updateTime = cursor.getString(cursor.getColumnIndex(AppwidgetItems.UPDATE_TIME));
			ib_bgcolor.setOnClickListener(listener);
			tv_widget_title.setText(updateDate + "\t" + updateTime.substring(0,  5));
			cursor.close();
		}
	}
	@Override
	public void onBackPressed() {
		String newContent = et_content.getText().toString();
		if (!TextUtils.isEmpty(newContent)) {
			ContentValues values = new ContentValues();
			values.put(AppwidgetItems.CONTENT, newContent);
			values.put(AppwidgetItems.UPDATE_DATE, updateDate);
			values.put(AppwidgetItems.UPDATE_TIME, updateTime);
			values.put(AppwidgetItems.BACKGROUND_COLOR, mBackground_Color);
			getContentResolver().update(ContentUris.withAppendedId(AppwidgetItems.CONTENT_URI, _id), values, null, null);
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
			if (is4X4) {
				NoteWidget_4X4.updateAppwidget(getApplicationContext(), appWidgetManager, mAppWidgetId);
			} else {
				NoteWidget_2X2.updateAppwidget(getApplicationContext(), appWidgetManager, mAppWidgetId);
			}
		}
		super.onBackPressed();
	}
	
}
















