package com.hobom.mobile.ui;

import java.util.ArrayList;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MoreGroup extends ActivityGroup {

	public static MoreGroup mGroup;
	private ArrayList<View> mHistory;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mHistory = new ArrayList<View>();
		mGroup = MoreGroup.this;

		View view = getLocalActivityManager().startActivity(
				"More",
				new Intent(this, MoreActivity.class)
						.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
				.getDecorView();

		replaceView(view);

	}

	public void replaceView(View v) {
		// Adds the old one to history
		mHistory.add(v);
		// Changes this Groups View to the new View.
		setContentView(v);
	}

	public void back() {
		if (mHistory.size() > 0) {
			mHistory.remove(mHistory.size() - 1);
			if (mHistory.size() == 0) {
				finish();
				return;
			}

			setContentView(mHistory.get(mHistory.size() - 1));
		} else {
			finish();
		}
	}

	@Override
	public void onBackPressed() {
		MoreGroup.mGroup.back();
		return;
	}

	

}
