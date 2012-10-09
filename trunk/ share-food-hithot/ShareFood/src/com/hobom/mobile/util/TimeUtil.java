package com.hobom.mobile.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.util.Log;

public class TimeUtil {

	/**
	 * 获取本周周一时间
	 * @return
	 */
	public static long getWeekStart(){
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, -((c.get(Calendar.DAY_OF_WEEK)-c.getFirstDayOfWeek()-1+7)%7));
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTimeInMillis();
	}
	
	/**
	 * 获取前一月开头时间
	 * @return
	 */
	public static long getLastMonthStart(){
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -1);
		return c.getTimeInMillis();
	}
	
	public static int getDateToInt(Date date) {
		return (int) (date.getTime()/1000);
	}
	
	public static int getCurrTimeInt() {
	//	return getDateToInt(new Date());
		return (int) (System.currentTimeMillis() / 1000);
	}

	public static String intDate2String(int intDate,String format) {
		String strDate = "";
		SimpleDateFormat simf = new SimpleDateFormat(format);
		TimeZone timeZoneChina = TimeZone.getTimeZone("Asia/Shanghai");
		simf.setTimeZone(timeZoneChina);
		Date date = new Date(intDate * 1000L);
		strDate = simf.format(date);
		return strDate;
	}
	public static Date int2Date(int intDate) {
		
		Date date = new Date(intDate * 1000L);
		return date;
	}
	public static String date2String(Date date, String format) {
		String strDate = "";
		SimpleDateFormat simf = new SimpleDateFormat(format);
		TimeZone timeZoneChina = TimeZone.getTimeZone("Asia/Shanghai");
		simf.setTimeZone(timeZoneChina);
		strDate = simf.format(date);
		return strDate;
	}

	public static String getCompareDate(String  date) {
		return getCompareDate( getStringToDate(date,"yyyy-MM-dd HH:mm:SS"));
	}
	
	public static String getCompareDate(Date date) {
		if (date == null)
			return null;
		 return getCompareDate(TimeUtil.getDateToInt(date));
	}
	public static String getCompareDate( int dateInt) {
		Date dateNow = new Date();
		return getCompareDate(dateNow,dateInt);
	}
	public static String getCompareDate(Date dateNow ,int dateInt) {
		
		int now = TimeUtil.getDateToInt(dateNow);
		int compareInt = now - dateInt;
		Log.d("compareTime", compareInt+","+now+","+dateInt);
		
		if (compareInt < 5) {
			return "不到5秒前";
		} else if (compareInt >= 5 && compareInt < 30) {
			return "不到半分钟前";
		} else if (compareInt >= 30 && compareInt < 60) { // 60*1
			return "1分钟前";
		} else if (compareInt >= 60 && compareInt < 1200) {
			return (compareInt / 60 + 1) + "分钟前";
		} else if (compareInt >= 1200 && compareInt < 1800) {
			return "不到半小时";
		} else if (compareInt >= 1800 && compareInt < 3600) {
			return "1小时前";
		} else if (compareInt >= 3600 && compareInt < 86400) {
			return (compareInt / 3600 + 1) + "小时前";
		} else if (compareInt >= 86400 && compareInt < 604800) {
			return (compareInt / 86400 + 1) + "天前";
		} else if (compareInt >= 604800 && compareInt < 2592000) {
			return (compareInt / 604800 + 1) + "周前";
		} else if (compareInt >= 2592000 && compareInt < 31536000) {
			return (compareInt / 2592000 + 1) + "月前";
		} else if (compareInt >= 31536000) {
			return (compareInt / 31536000 + 1) + "年前";
		}
		return null;
	}


	public static String getStrTimeByNoSecond(Date now) {
		SimpleDateFormat myFmt = new SimpleDateFormat("yyyy-M-d HH:mm");
		TimeZone timeZoneChina = TimeZone.getTimeZone("Asia/Shanghai");
		myFmt.setTimeZone(timeZoneChina);
		
		String dateFormat = myFmt.format(now);
		return dateFormat;
	}
	
	public static String getStrTime(Date now,String format) {
		SimpleDateFormat myFmt = new SimpleDateFormat(format);
		TimeZone timeZoneChina = TimeZone.getTimeZone("Asia/Shanghai");
		myFmt.setTimeZone(timeZoneChina);
		String dateFormat = myFmt.format(now);
		return dateFormat;
	}
	

	public static Date getStringToDate(String time,String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				format);
		TimeZone timeZoneChina = TimeZone.getTimeZone("Asia/Shanghai");
		dateFormat.setTimeZone(timeZoneChina);
		Date dateNow = null;;
		try {
			dateNow = dateFormat.parse(time);
		} catch (ParseException e) {
			return null;
		}
		return dateNow;
	}
	
	/**获取 当前天与指定天的间隔天数
	 * @param dateInt 指定天
	 */
	public static int getDistanceDay(int dateInt){
		Date dateNow = new Date();
		int now = TimeUtil.getDateToInt(dateNow);
		int compareInt = dateInt-now;
		return compareInt/86400;
		
	}
	/**
	 * 获取当月第一天日期
	 * @param date
	 * @return
	 */
	public static Date getMonthOrigin(Date date) {
		
		Calendar calendar = Calendar.getInstance();
		//calendar.set(1900+date.getYear(), date.getMonth(), date.getDate());
		calendar.setTime(date);
		int day = date.getDate();
		calendar.add(Calendar.DAY_OF_MONTH, 1-day);
		
		return calendar.getTime();
	}
	
	
}
