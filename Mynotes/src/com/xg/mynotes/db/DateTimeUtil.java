package com.xg.mynotes.db;

import java.util.Calendar;
import java.sql.Date;
import java.sql.Time;

import java.util.GregorianCalendar;

public final class DateTimeUtil {
	
	//date
	public static String getDate() {
		Calendar c = Calendar.getInstance();
		int theYear = c.get(Calendar.YEAR) - 1900;
		int theMonth = c.get(Calendar.MONTH);
		int theDay = c.get(Calendar.DAY_OF_MONTH);
		//return (new GregorianCalendar(theYear, theMonth, theDay)).toString();
		return (new Date(theYear, theMonth, theDay)).toString();

	}
	
	//time
	public static String getTime() {
		Calendar c = Calendar.getInstance();
		int theHour = c.get(Calendar.HOUR_OF_DAY);
		int theMinute = c.get(Calendar.MINUTE);
		int theSecond = c.get(Calendar.SECOND);
		//long time = c.get(Calendar.)
		return (new Time(theHour, theMinute, theSecond)).toString();
	}
}
