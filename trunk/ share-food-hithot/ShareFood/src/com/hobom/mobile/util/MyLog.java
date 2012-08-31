package com.hobom.mobile.util;

import android.util.Log;

public class MyLog {

	private MyLog() {
	}

	// 错误日志
	public static void ErrLog(Class classname, String text) {
		Log.e(classname.getName().substring(classname.getName().lastIndexOf(".")+1), text);
	}

	// 信息日志
	public static void InfoLog(Class classname, String text) {
		
		Log.i(classname.getName().substring(classname.getName().lastIndexOf(".")+1), text);
	}

}
