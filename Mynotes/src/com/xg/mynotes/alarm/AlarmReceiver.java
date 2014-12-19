package com.xg.mynotes.alarm;

import com.xg.mynotes.NoteActivity;
import com.xg.mynotes.db.DbInfo.NoteItems;
import com.xg.mynotes.log.Logs;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
	
	private final String TAG = "AlarmReceiver";
	private int _id;
	private String openType;
	private int folderId;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		Logs.d(TAG, "== > acquire wakelock");
		WakeLockOpration.acquire(context);
		KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
		
		KeyguardLock kl = km.newKeyguardLock(TAG);
		kl.disableKeyguard();
		openType = intent.getStringExtra("Open_Type");
		_id = intent.getIntExtra(NoteItems._ID, -1);
		Logs.d(TAG, " == > 要提醒的纪录的id: " + _id);
		folderId = intent.getIntExtra("FolderId", -1);
		
		Intent intent2 = new Intent();
		intent2.setClass(context, NoteActivity.class);
		intent2.putExtra("Open_Type", openType);
		intent2.putExtra(NoteItems._ID, _id);
		intent2.putExtra("FolderId", folderId);
		intent2.putExtra("alarm", 1234567);
		intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent2);
		Logs.d(TAG, " complete ");
	}

}
