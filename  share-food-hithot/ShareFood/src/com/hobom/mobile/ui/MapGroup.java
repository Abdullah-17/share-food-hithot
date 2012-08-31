package com.hobom.mobile.ui;

import java.util.ArrayList;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MapGroup extends ActivityGroup{

	public static MapGroup mMapGroup;
	private ArrayList<View> mHistory;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mHistory = new ArrayList<View>();
		mMapGroup = MapGroup.this;

		View view = getLocalActivityManager().startActivity(
				"FoodMapActivity",
				new Intent(this, FoodMapActivity.class)
						.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
				.getDecorView();

		replaceView(view);

	}

	public void replaceView(View v) {
		// Adds the old one to history
		mHistory.add(v);
		// Changes this Groups View to the new View.
		setContentView(v);
	}

	public boolean isHistoryEmpty() {
		return mHistory.size() == 0;
	}

	public void back() {
		if (mHistory.size() > 0) {
			mHistory.remove(mHistory.size() - 1);
			if (mHistory.size() > 0)
				setContentView(mHistory.get(mHistory.size() - 1));
			else
				quit();
		} else {
			// finish();
			quit();
		}
	}

	



	
	@Override
	public void onBackPressed() {
		mMapGroup.back();
		return;
	}

	private void quit() {
		
	}
}
