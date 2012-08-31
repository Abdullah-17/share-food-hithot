package com.hobom.mobile.util;

import android.util.Log;

public class MyLog {

	private MyLog() {
	}

	// ������־
	public static void ErrLog(Class classname, String text) {
		Log.e(classname.getName().substring(classname.getName().lastIndexOf(".")+1), text);
	}

	// ��Ϣ��־
	public static void InfoLog(Class classname, String text) {
		
		Log.i(classname.getName().substring(classname.getName().lastIndexOf(".")+1), text);
	}

}
