package com.xg.mynotes;

import java.lang.reflect.Method;
import java.util.Calendar;

import com.xg.mynotes.alarm.AlarmReceiver;
import com.xg.mynotes.alarm.WakeLockOpration;
import com.xg.mynotes.db.ChooseColor;
import com.xg.mynotes.db.DateTimeUtil;
import com.xg.mynotes.db.DbInfo.NoteItems;
import com.xg.mynotes.log.Logs;

import android.R.integer;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener; //非自动导入
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener; //非自动导入 手动导入
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class NoteActivity extends Activity {
	private LinearLayout mainBackground;
	private LinearLayout mLinearLayout_Header;
	private ImageButton ib_bgcolor;
	private TextView tv_note_title;
	private EditText et_content;
	//存储背景图片在R.java  中的值
	private int mBackground_Color;
	
	private String updateDate;
	private String updateTime;
	//alarm clock information
	private int mYear;
	private int mMonth;
	private int mDay;
	private int mHour;
	private int mMinute;
	private boolean hasSetAlartTime = false;
	//create new note or update note 
	private String openType;
	
	private String oldContent;
	//接收传递过来的Intent对象
	private Intent intent;
	
	private int _id;
	private int folderId;
	private int oldBackground_Color;
	private final String ACTION_ADD_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
	
	public static final String TAG = "NoteActivity";
	
	//menu
	private static final int MENU_DELETE = Menu.FIRST;
	private static final int MENU_REMIND = Menu.FIRST + 1;
	private static final int MENU_SEND_HOME = Menu.FIRST + 2;
	private static final int MENU_SHARE = Menu.FIRST + 3;
	
	ChooseColor chooseColor = new ChooseColor();
	
	private Calendar setCalendar = Calendar.getInstance();
	@Override
	protected void onResume() {
		KeyguardManager km = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
		KeyguardLock kl = km.newKeyguardLock(MainActivity.TAG);
		kl.reenableKeyguard();
		super.onResume();
	}

	@Override
	protected void onPause() {
		WakeLockOpration.release();
		super.onPause();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.note_detail);
		//get Intent
		intent = getIntent();
		if(intent.equals(null)) {
			startActivity(new Intent(NoteActivity.this, MainActivity.class)); // *
		}
		//get Open_Type, create new note or update note 
		openType = intent.getStringExtra("Open_Type");
		Logs.d(TAG, "NoteActivity==>" + String.valueOf(openType));
		
		_id = intent.getIntExtra(NoteItems._ID, -1);
		Logs.d(TAG, "NoteActivity ==> 被编辑的便签的id：" + _id);
		folderId = intent.getIntExtra("FolderId", -1);
		Logs.d(TAG, "NoteActivity==>要操作的文件夹的id：" + folderId);
		
		if(intent.getIntExtra("alarm", -1) == 1234567) {
			noteAlarm(_id);
		}
		initViews();
	}

	private void noteAlarm(int noteId) {
		Logs.d(TAG, "== > 闹钟时间到达，要显示的纪录的id：" + noteId);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("提醒");
		Cursor cursor = getContentResolver().query(ContentUris.withAppendedId(NoteItems.CONTENT_URI, noteId), null, null, null, null);
		cursor.moveToFirst();
		String content = cursor.getString(cursor.getColumnIndex(NoteItems.CONTENT));
		Logs.d(TAG, "闹钟显示时显示的内容：" + content);
		cursor.close();
		
		builder.setMessage(content);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Logs.d(TAG, "realease wakelock");
				WakeLockOpration.release();
				dialog.dismiss();
				
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Logs.d(TAG, "realease wakelock");
				WakeLockOpration.release();
				NoteActivity.this.finish();
				
			}
		});
		
		builder.create().show();
	}

	private void initViews() {
		mainBackground = (LinearLayout) findViewById(R.id.widget40);
		mLinearLayout_Header = (LinearLayout) findViewById(R.id.note_detail_header);
		ib_bgcolor = (ImageButton) findViewById(R.id.imagebutton_bgcolor); //change note's background color
		tv_note_title = (TextView) findViewById(R.id.tv_note_date_time);
		et_content = (EditText) findViewById(R.id.et_content);
		//Logs.d(TAG + " Position:", "NoteActivity==>1");
		if(_id != -1) {
			//according to _id, query this note's detail 
			Cursor c = getContentResolver().query(
					ContentUris.withAppendedId(NoteItems.CONTENT_URI, _id), null, null, null, null);
			c.moveToFirst();
			//get the lastest update date, time and content 
			oldContent = c.getString(c.getColumnIndex(NoteItems.CONTENT));
			updateDate = c.getString(c.getColumnIndex(NoteItems.UPDATE_DATE));
			updateTime = c.getString(c.getColumnIndex(NoteItems.UPDATE_TIME));
			mBackground_Color = c.getInt(c.getColumnIndex(NoteItems.BACKGROUND_COLOR));
			oldBackground_Color = mBackground_Color;
			
			c.close();
		}
		//Logs.d("Position", "NoteActivity==>2");
		if(openType.equals("newNote")) {
			//initialize new note's date and time
			updateDate = DateTimeUtil.getDate();
			updateTime = DateTimeUtil.getTime();
			
			//use default background color
			et_content.setBackgroundResource(R.color.item_light_1);
			//Logs.d("Position", "NoteActivity==>4");
		} else if(openType.equals("editNote")) {
			Logs.d(TAG, "editNote");
			//content
			et_content.setText(oldContent);
			//background color
			if(mBackground_Color != 0) {
				et_content.setBackgroundResource(mBackground_Color);
				//et_content.setTextColor(mainBackground(mBackground_Color));
				mainBackground.setBackgroundResource(chooseColor.mainBackground(mBackground_Color));
				mLinearLayout_Header.setBackgroundResource(chooseColor.headerBackground(mBackground_Color));
			}
		} else if (openType.equals("newFolderNote")) {
			updateDate = DateTimeUtil.getDate();
			updateTime = DateTimeUtil.getTime();
			et_content.setBackgroundResource(R.color.item_light_1);
		} else if (openType.equals("editFolderNote")) {
			et_content.setText(oldContent);
			if(mBackground_Color != 0) {
				et_content.setBackgroundResource(mBackground_Color);
				//et_content.setTextColor(mainBackground(mBackground_Color));
				mainBackground.setBackgroundResource(chooseColor.mainBackground(mBackground_Color));
				mLinearLayout_Header.setBackgroundResource(chooseColor.headerBackground(mBackground_Color));
			}
		}
		ib_bgcolor.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(NoteActivity.this);
				// *
				LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
				View view = inflater.inflate(R.layout.dialog_note_bg_color, null);
				
				final AlertDialog dialog = builder.create();
				dialog.setView(view, 0, 0, 0, 0);
				
				//initialize ImageButton
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
						Logs.d(MainActivity.TAG, "NoteActivity == > choose background color");
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
		});
		tv_note_title.setText(updateDate + "\t" + updateTime.substring(0, 5));
	}
	
	@Override
	public void onBackPressed() {
		Logs.d(TAG, "NateActivity == > 选择的背景颜色是：" + mBackground_Color);
		//default background color
		if(mBackground_Color == 0) {
			mBackground_Color = R.color.item_light_1;
		}
		String content = et_content.getText().toString();
		
		ContentValues values = new ContentValues();
		values.put(NoteItems.CONTENT, content);
		values.put(NoteItems.UPDATE_DATE, DateTimeUtil.getDate());
		values.put(NoteItems.UPDATE_TIME, DateTimeUtil.getTime());
		values.put(NoteItems.BACKGROUND_COLOR, mBackground_Color);
		if(hasSetAlartTime) {
			values.put(NoteItems.ALARM_TIME, mYear + "-" + mMonth + "-" + mDay + " " + mHour + ":" + mMinute);
			Logs.d(TAG, "== > 提醒时间: " + mYear + "-" + mMonth + "-" + mDay + " " + mHour + ":" + mMinute);
		}
		if(openType.equals("newNote")) {
			if(!TextUtils.isEmpty(content)) {
				values.put(NoteItems.IS_FOLDER, "no");
				values.put(NoteItems.PARENT_FOLDER, -1);
				_id = (int) ContentUris.parseId(getContentResolver().insert(NoteItems.CONTENT_URI, values));
				
			}
		} else if(openType.equals("editNote")) {
			if(!TextUtils.isEmpty(content) && (!oldContent.equals(content) || (oldBackground_Color != mBackground_Color))) {				
				getContentResolver().update(ContentUris.withAppendedId(NoteItems.CONTENT_URI, _id), values, null, null);
			}
		} else if(openType.equals("newFolderNote")) {
			if(!TextUtils.isEmpty(content)) {
				values.put(NoteItems.IS_FOLDER, "no");
				values.put(NoteItems.PARENT_FOLDER, folderId);
				_id = (int) ContentUris.parseId(getContentResolver().insert(NoteItems.CONTENT_URI, values));
			}
		} else if(openType.equals("editFolderNote")) {
			if(!TextUtils.isEmpty(content) && (!oldContent.equals(content) || (oldBackground_Color != mBackground_Color))) {
				getContentResolver().update(ContentUris.withAppendedId(NoteItems.CONTENT_URI, _id), values, null, null);
			}
		}
		
		if (hasSetAlartTime) {
			if (openType.equals("newNote")) {
				openType = "editNote";
			} else if(openType.equals("newFolderNote")){
				openType = "editFolderNote";
			}
			this.startAlarmTime();
		}
		
		super.onBackPressed();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		setIconEnable(menu, true);
		menu.add(Menu.NONE, MENU_DELETE, 1, R.string.delete)
			.setIcon(R.drawable.delete);
		menu.add(Menu.NONE, MENU_REMIND, 2, R.string.alarm_time)
			.setIcon(R.drawable.alarm_time);
		menu.add(Menu.NONE, MENU_SEND_HOME, 3, R.string.add_shortcut_to_home)
			.setIcon(R.drawable.add_shortcut_to_home);
		menu.add(Menu.NONE, MENU_SHARE, 4, R.string.share_sms_or_email)
			.setIcon(R.drawable.share);
		return super.onCreateOptionsMenu(menu);
	}
	
	//让菜单显示图标
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
		case MENU_DELETE:
			//Toast.makeText(this, "MENU_DELETE", Toast.LENGTH_LONG).show();
			deleteNote();
			break;
		case MENU_REMIND:
			//Toast.makeText(this, "MENU_REMIND", Toast.LENGTH_LONG).show();
			setAlarm();
			break;
		case MENU_SEND_HOME:
			//Toast.makeText(this, "MENU_SEND_HOME", Toast.LENGTH_LONG).show();
			addShortCut();
			break;
		case MENU_SHARE:
			//Toast.makeText(this, "MENU_SHARE", Toast.LENGTH_LONG).show();
			shareNote();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void shareNote() {
		final CharSequence[] items = {
				getResources().getString(R.string.share_with_sms),
				getResources().getString(R.string.share_with_email)
		};
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setItems(items,new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String strContent = et_content.getText().toString();
				switch (which) {
				case 0://share with sms
					Uri smsToUri = Uri.parse("smsto:");
					Intent mIntent = new Intent(android.content.Intent.ACTION_SENDTO, smsToUri);
					mIntent.putExtra("sms_body", strContent);
					startActivity(mIntent);
					Toast.makeText(NoteActivity.this, "启动" + items[which] + "程序中...", Toast.LENGTH_SHORT)
						.show();
					break;
				case 1://share with email
					Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
					emailIntent.setType("text/plain");
					emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, "");
					emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "通过 彩色便签 分享信息");
					emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, strContent);
					startActivity(Intent.createChooser(emailIntent, "选择邮件客户端"));
					Toast.makeText(NoteActivity.this, "启动" + items[which] + "程序中...", Toast.LENGTH_SHORT)
						.show();
					break;
				default:
					break;
				}
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void addShortCut() {
		Intent addShortCut = new Intent(ACTION_ADD_SHORTCUT);
		addShortCut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(this, R.drawable.ic_launcher));
		addShortCut.putExtra(Intent.EXTRA_SHORTCUT_NAME, oldContent);
		addShortCut.putExtra("duplicate", false);
		
		Intent shortCutIntent = new Intent(NoteActivity.this, NoteActivity.class);
		shortCutIntent.putExtra(NoteItems._ID, _id);
		if(openType.equals("editNote")) {
			shortCutIntent.putExtra("Open_Type", "editNote");
		} else if (openType.equals("editFolderNote")) {
			//shortCutIntent.putExtra(, value)
		}
		addShortCut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortCutIntent);
		sendBroadcast(addShortCut);
		Toast.makeText(this, "发送成功", Toast.LENGTH_SHORT).show();
	}

	private void setAlarm() {
		
		Logs.d(TAG, "== > Set Alarm");
		//final AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		final Calendar c = Calendar.getInstance();
		
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		mHour = c.get(Calendar.HOUR_OF_DAY);
		mMinute = c.get(Calendar.MINUTE);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.alarm_time);
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.set_alarm, null);
		builder.setView(view);
		
		final Button btnAlarmDate = (Button) view.findViewById(R.id.btnAlarmDate);
		final Button btnAlarmTime = (Button) view.findViewById(R.id.btnAlarmTime);
		btnAlarmDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DatePickerDialog dpd = new DatePickerDialog(NoteActivity.this, new OnDateSetListener() {
							@Override
							public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
								
								mYear = year;
								mMonth = monthOfYear;
								mDay = dayOfMonth;
								String alarmDate = mYear + "-" + mMonth + "-" + mDay;
								btnAlarmDate.setText(alarmDate);
								Logs.d(TAG, "== >设置的闹钟日期：" + alarmDate);
								
							}
						}, mYear, mMonth, mDay);
				
				dpd.show();
			}
		});
		btnAlarmTime.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				TimePickerDialog tpd = new TimePickerDialog(NoteActivity.this, new OnTimeSetListener() {
					
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
						
						mHour = hourOfDay;
						mMinute = minute;
						String alarmTime = mHour + ":" + mMinute;
						btnAlarmTime.setText(alarmTime);
						Logs.d(TAG, "== >设置闹钟时间：" + alarmTime);
						
					}
				}, mHour, mMinute, true);
				tpd.show();
			}
		});
		
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(checkAlarmTime()) {
					hasSetAlartTime = true;
					dialog.dismiss();
					Toast.makeText(getApplicationContext(), "设置提醒时间成功", Toast.LENGTH_SHORT)
							.show();
				} else {
					Toast.makeText(getApplicationContext(), "设置提醒时间失败", Toast.LENGTH_SHORT)
						 .show();
				}
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				hasSetAlartTime = false;
				dialog.dismiss();
				
			}
		});
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}
	
	private boolean checkAlarmTime() {
		Logs.d(TAG, "== > checkAlarmTime()");
		setCalendar.set(mYear, mMonth, mDay, mHour, mMinute, 0);
		if(!setCalendar.before(Calendar.getInstance())) {
			return true;
		}
		return false;
	}
	private void startAlarmTime() {
		Logs.d(TAG, "== > startAlarmTime()");
		final AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		Intent intent = new Intent();
		
		intent.setClass(NoteActivity.this, AlarmReceiver.class);
		intent.putExtra("Open_Type", openType);
		intent.putExtra(NoteItems._ID, _id);
		intent.putExtra("FolderId", folderId);
		PendingIntent pi = PendingIntent.getBroadcast(NoteActivity.this, _id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		if(!setCalendar.before(Calendar.getInstance())) {
			am.set(AlarmManager.RTC_WAKEUP, setCalendar.getTimeInMillis(), pi);
		}
	}
	private void deleteNote() {
		Context mcContext = NoteActivity.this;
		AlertDialog.Builder builder = new AlertDialog.Builder(mcContext);
		builder.setTitle(R.string.delete_note);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Uri deleUri = ContentUris.withAppendedId(NoteItems.CONTENT_URI, _id);
				getContentResolver().delete(deleUri, null, null);
				Logs.d(TAG, "== >deleteNote() in NoteActivity");
					NoteActivity.this.finish();			
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		AlertDialog ad = builder.create();
		ad.show();
	}
	
}
























