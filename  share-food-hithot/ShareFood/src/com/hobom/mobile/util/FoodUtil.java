package com.hobom.mobile.util;

import java.lang.reflect.Field;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class FoodUtil {
	public static void hideSearchIconAndLabel(Activity activity) {
		try {
		
			Class activityClass = Class.forName("android.app.Activity");
			Field activityClassf = activityClass
					.getDeclaredField("mSearchManager");
			activityClassf.setAccessible(true);
			Object mSearchManager = activityClassf.get(activity);

			Class searchClass = Class.forName("android.app.SearchManager");
			Field mSearchDialogf = searchClass
					.getDeclaredField("mSearchDialog");
			mSearchDialogf.setAccessible(true);
			Object mSearchDialog = mSearchDialogf.get(mSearchManager);

			Class searchDialog = Class.forName("android.app.SearchDialog");
			Field mAppIcon = searchDialog.getDeclaredField("mAppIcon");
			mAppIcon.setAccessible(true);
			ImageView imageView = (ImageView) mAppIcon.get(mSearchDialog);
			imageView.setVisibility(View.GONE);
			
			Field mBadgeLabel = searchDialog.getDeclaredField("mBadgeLabel");
			mBadgeLabel.setAccessible(true);
			TextView textView = (TextView)mBadgeLabel.get(mSearchDialog);
			textView.setVisibility(View.GONE);
			
			Field mSearchPlate = searchDialog.getDeclaredField("mSearchPlate");
			mSearchPlate.setAccessible(true);
			View searchPlate = (View)mSearchPlate.get(mSearchDialog);
			searchPlate.setPadding(searchPlate.getPaddingLeft()+5, searchPlate.getPaddingTop()+2, searchPlate.getPaddingRight()+5, searchPlate.getPaddingBottom()+2);
			
			

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
