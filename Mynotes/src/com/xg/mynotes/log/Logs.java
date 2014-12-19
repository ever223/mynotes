package com.xg.mynotes.log;

import android.util.Log;

public class Logs {
	private static boolean openLog = true;
	public static void d(String tag, String msg) {
		if(openLog) {
			Log.d(tag, msg);
		}
	}
	public static void w(String tag, String msg) {
		if(openLog) {
			Log.w(tag, msg);
		}
	}
	public static void e(String tag, String msg) {
		if(openLog) {
			Log.e(tag, msg);
		}
	}
}
