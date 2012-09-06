package com.hobom.mobile.util;

import java.util.Calendar;

public class TimeUtil {

	/**
	 * 获取本周周一时间
	 * @return
	 */
	public static long getWeekStart(){
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, -(c.get(Calendar.DAY_OF_WEEK)-c.getFirstDayOfWeek()-1));
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
}
