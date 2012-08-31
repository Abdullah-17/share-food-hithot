package com.hobom.mobile.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtil {

	protected static Context mContext = null;
	protected static SharedPreferences mSharedPreferences = null;
	public static final String PREFS_NAME = "PreferenceUtil";
	public static void setContext(Context context) {
		mContext = context;
		
	}

	public static SharedPreferences getSettings() {
		if (mSharedPreferences == null) {
			mSharedPreferences = mContext.getSharedPreferences(PREFS_NAME, 0);
		}
		return mSharedPreferences;
	}
	public static String getSettingString(final String key,
			final String defaultValue) {
		return getSettings().getString(key, defaultValue);
	}

	public static boolean getSettingBoolean(final String key,
			final boolean defaultValue) {
		return getSettings().getBoolean(key, defaultValue);
	}

	public static int getSettingInt(final String key, final int defaultValue) {
		return getSettings().getInt(key, defaultValue);
	}

	public static long getSettingLong(final String key, final long defaultValue) {
		return getSettings().getLong(key, defaultValue);
	}

	public static boolean setSettingString(final String key, final String value) {
		final SharedPreferences.Editor settingsEditor = getSettings().edit();
		settingsEditor.putString(key, value);
		return settingsEditor.commit();
	}

	public static boolean setSettingBoolean(final String key,
			final boolean value) {
		final SharedPreferences.Editor settingsEditor = getSettings().edit();
		settingsEditor.putBoolean(key, value);
		return settingsEditor.commit();
	}

	public static boolean setSettingInt(final String key, final int value) {
		final SharedPreferences.Editor settingsEditor = getSettings().edit();
		settingsEditor.putInt(key, value);
		return settingsEditor.commit();
	}

	public static boolean setSettingLong(final String key, final long value) {
		final SharedPreferences.Editor settingsEditor = getSettings().edit();
		settingsEditor.putLong(key, value);
		return settingsEditor.commit();
	}
}
