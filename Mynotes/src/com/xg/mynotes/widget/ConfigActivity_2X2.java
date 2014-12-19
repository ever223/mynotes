package com.xg.mynotes.widget;

import com.xg.mynotes.R;
import com.xg.mynotes.db.ChooseColor;
import com.xg.mynotes.db.DateTimeUtil;
import com.xg.mynotes.db.DbInfo.AppwidgetItems;
import com.xg.mynotes.log.Logs;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ConfigActivity_2X2 extends Activity {
	private LinearLayout mLinearLayout_Header;
	private LinearLayout mainBackground;
	private ImageButton ib_bgcolor;
	private TextView tv_widget_title;
	private EditText et_widget_content;
	private int mBackground_Color;
	private String createDate;
	private String createTime;
	private int _id;
	private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	
	private static final String TAG = "ConfigActivity_2X2";
	
	private View.OnClickListener listener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			AlertDialog.Builder builder = new AlertDialog.Builder(ConfigActivity_2X2.this);
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
					Logs.d(TAG, " == > choose background color");
					ChooseColor chooseColor = new ChooseColor();
					mBackground_Color = chooseColor.contentBackground(v.getId());
					mainBackground.setBackgroundResource(chooseColor.mainBackground(mBackground_Color));
					mLinearLayout_Header.setBackgroundResource(chooseColor.headerBackground(mBackground_Color));
					et_widget_content.setBackgroundResource(mBackground_Color);
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
		Logs.d(TAG, "== > oncreate");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.widget_note_layout);
		setResult(RESULT_CANCELED);
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
			
		}
		if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
			finish();
		}
		Logs.d(TAG, "== > onCreate() -- > AppWidget ID : " + mAppWidgetId);
		mLinearLayout_Header = (LinearLayout) findViewById(R.id.widget_detail_header);
		mainBackground = (LinearLayout) findViewById(R.id.widget40);
		tv_widget_title = (TextView) findViewById(R.id.tv_widget_date_time);
		et_widget_content = (EditText) findViewById(R.id.et_content);
		et_widget_content.setBackgroundResource(R.color.item_light_1);
		
		ib_bgcolor = (ImageButton) findViewById(R.id.imagebutton_bgcolor);
		ib_bgcolor.setOnClickListener(listener);
		createDate = DateTimeUtil.getDate();
		createTime = DateTimeUtil.getTime();
		tv_widget_title.setText(createDate + "\t" + createTime.substring(0, 5));
	}

	@Override
	public void onBackPressed() {
		Logs.d(TAG, "== > onBackPressed");
		if (mBackground_Color == 0) {
			mBackground_Color = R.color.item_light_1;
		}
		String newContent = et_widget_content.getText().toString();
		if (!TextUtils.isEmpty(newContent)) {
			ContentValues values = new ContentValues();
			values.put(AppwidgetItems.CONTENT, newContent);
			values.put(AppwidgetItems.UPDATE_DATE, DateTimeUtil.getDate());
			values.put(AppwidgetItems.UPDATE_TIME, DateTimeUtil.getTime());
			values.put(AppwidgetItems.BACKGROUND_COLOR, mBackground_Color);
			_id = (int) ContentUris.parseId(getContentResolver().insert(AppwidgetItems.CONTENT_URI, values));
		} else {
			super.onBackPressed();
		}
		
		SharedPreferences.Editor editor = this.getSharedPreferences(EditWidgetNoteActivity.SHAREDPREF, MODE_WORLD_READABLE).edit();
		editor.putLong(EditWidgetNoteActivity.SHAREDPREF + mAppWidgetId, _id);
		editor.commit();
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(ConfigActivity_2X2.this);
		NoteWidget_2X2.updateAppwidget(getApplicationContext(), appWidgetManager, mAppWidgetId);
		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
		setResult(RESULT_OK, resultValue);
		super.onBackPressed();
	}
	

}























