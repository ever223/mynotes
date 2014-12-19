package com.xg.mynotes.widget;

import com.xg.mynotes.R;
import com.xg.mynotes.db.ChooseColor;
import com.xg.mynotes.db.DateTimeUtil;
import com.xg.mynotes.db.DbInfo.AppwidgetItems;
import com.xg.mynotes.log.Logs;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.widget.RemoteViews;

public class NoteWidget_2X2 extends AppWidgetProvider {
	private static final String TAG = "NoteWidget_2X2";
	public ChooseColor chooseColor = new ChooseColor();
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Logs.d(TAG, "== >onUpdate()");
		int c = appWidgetIds.length;
		//Logs.d(TAG, "== >onUpdate() -- > c = " + c);
		if (c > 0) {
			for (int i = 0; i < c; i++) {
				Logs.d(TAG, "要更新的AppWidget的Id：" + appWidgetIds[i]);
				NoteWidget_2X2.updateAppwidget(context, appWidgetManager, appWidgetIds[i]);
			}
		}
		//super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		Logs.d(TAG, "== > onDeleted()");
		int count = appWidgetIds.length;
		Uri deleUri = null;
		SharedPreferences sp = context.getSharedPreferences(EditWidgetNoteActivity.SHAREDPREF, Context.MODE_PRIVATE);
		long id = 0;
		for (int i = 0; i < count; i++) {
			id = sp.getLong(EditWidgetNoteActivity.SHAREDPREF + appWidgetIds[i], -1);
			if (id != -1) {
				Logs.d(TAG, "onDeleted() -- > 被删除纪录的id：" + id);
				deleUri = ContentUris.withAppendedId(AppwidgetItems.CONTENT_URI, id);
				context.getContentResolver().delete(deleUri, null, null);
			}
		}
		super.onDeleted(context, appWidgetIds);
	}
	
	public static void updateAppwidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
		int mBackground = 0;
		String update_date = DateTimeUtil.getDate();
		String update_time = DateTimeUtil.getTime();
		String mContent = null;
		RemoteViews views = null;
		SharedPreferences prefs = context.getSharedPreferences(EditWidgetNoteActivity.SHAREDPREF, Context.MODE_PRIVATE);
		int id = (int) prefs.getLong(EditWidgetNoteActivity.SHAREDPREF + appWidgetId, -1);
		Logs.d(TAG, "== > AppWidget中的纪录在数据库中的id：" + id);
		
		if (id != -1) {
			Cursor cursor = context.getContentResolver().query(ContentUris.withAppendedId(AppwidgetItems.CONTENT_URI, id), null, null, null, null);
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				mBackground = cursor.getInt(cursor.getColumnIndex(AppwidgetItems.BACKGROUND_COLOR));
				mContent = cursor.getString(cursor.getColumnIndex(AppwidgetItems.CONTENT));
				update_date = cursor.getString(cursor.getColumnIndex(AppwidgetItems.UPDATE_DATE));
				update_time = cursor.getString(cursor.getColumnIndex(AppwidgetItems.UPDATE_TIME));
			}
			cursor.close();
			Intent widgetIntent = new Intent(context, EditWidgetNoteActivity.class);
			widgetIntent.putExtra("widget_id", appWidgetId);
			widgetIntent.putExtra("is4X4", false);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, widgetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			views = new RemoteViews(context.getPackageName(), R.layout.widget_2x2_layout);
			Logs.d(TAG, "== > context.getPackageName():" + context.getPackageName());
			views.setTextViewText(R.id.widget_2x2_date_time, update_date + "\t" + update_time.substring(0, 5));
			views.setTextViewText(R.id.widget_2x2_textView, mContent);
			views.setImageViewResource(R.id.widget_2x2_imageView, mBackground);
			views.setImageViewResource(R.id.widget_2x2_header, new ChooseColor().headerBackground(mBackground));
			Logs.d(TAG, "== > 要显示的内容：" + mContent);
			views.setOnClickPendingIntent(R.id.widget_2x2_imageView, pendingIntent);
			if (views != null) {
				Logs.d(TAG, "== > RemoteViews不为空！");
				appWidgetManager.updateAppWidget(appWidgetId, views);
			}
		}
	}
	
}
