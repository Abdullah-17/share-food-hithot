package com.hobom.mobile.ui;

import java.util.HashMap;
import java.util.Map;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

import com.hobom.mobile.R;
import com.hobom.mobile.widgets.TabButton;

public class MainActivity extends TabActivity {
	private Map<String, TabButton> mCustomTabButtons = new HashMap<String, TabButton>(
			5);
	private LinearLayout mCustomButtonLayout = null;
	private Resources mResources = null;
	private TabButton mCurrentTabButton = null;
	private float mDensity = 1.0f;
	TabHost mTabhost;// The activity TabHost

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabs);
		mCustomButtonLayout = (LinearLayout) findViewById(R.id.custom_tabs);

		ViewGroup tabhost = (ViewGroup) findViewById(android.R.id.tabhost);
		ViewGroup.LayoutParams params = tabhost.getLayoutParams();
		if (params != null && params.height > 0) {
			Display display = getWindowManager().getDefaultDisplay();
			int width = display.getWidth();
			int height = display.getHeight();
			int maxWidthHeight = (int) (Math.max(width, height) - 25 * mDensity);

			if (params.height > maxWidthHeight) {
				float percentage = maxWidthHeight * 1.0f / params.height;
				params.height = maxWidthHeight;
				params.width = (int) (percentage * params.width);
				tabhost.setLayoutParams(params);
			}
		}

		mResources = getResources(); // Resource object to get Drawables
		mTabhost = getTabHost();
		initTab();
	}

	private void initTab() {

		mTabhost.clearAllTabs();

		Intent intent;

		intent = new Intent().setClass(this, FoodMapActivity.class);

		addCustomTab(0, mTabhost, "地图", R.drawable.icon_list_off,
				R.drawable.icon_list_on, "map", intent);

		intent = new Intent().setClass(this, FoodListActivity.class);
		addCustomTab(1, mTabhost, "美食", R.drawable.icon_list_off,
				R.drawable.icon_list_on, "list", intent);

		intent = new Intent().setClass(this, CameraActivity.class);
		//
		addCustomTab(2, mTabhost, "拍照", R.drawable.icon_search_off,
				R.drawable.icon_search_on, "camera", intent);

		intent = new Intent().setClass(this, AnalysisActivity.class);
		addCustomTab(3, mTabhost, "统计", R.drawable.icon_search_off,
				R.drawable.icon_search_on, "analysis", intent);
		intent = new Intent().setClass(this, MoreGroup.class);
		addCustomTab(4, mTabhost, "更多", R.drawable.icon_more_off,
				R.drawable.icon_more_on, "more", intent);

		mTabhost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tag) {
				setCurrentTab(mTabhost, tag);
			}

		});
		setCurrentTab(mTabhost, "map");
	}

	private void setCurrentTab(TabHost tabHost, String tag) {
		tabHost.setCurrentTabByTag(tag);

		TabButton tabButton = mCustomTabButtons.get(tag);
		if (tabButton != null && !tabButton.equals(mCurrentTabButton)) {
			if (mCurrentTabButton != null) {
				mCurrentTabButton.setSelected(false);
			}
			mCurrentTabButton = tabButton;
			mCurrentTabButton.setSelected(true);
		}
	}

	private void addCustomTab(final int pos, final TabHost tabHost,
			String label, int iconResourceId, int selectedIconResourceId,
			String tag, Intent intent) {
		TabHost.TabSpec spec = tabHost.newTabSpec(tag)
				.setIndicator(label, null).setContent(intent);
		tabHost.addTab(spec);

		BitmapDrawable iconDrawable = (BitmapDrawable) mResources
				.getDrawable(iconResourceId);
		BitmapDrawable selectedIconDrawable = (BitmapDrawable) mResources
				.getDrawable(selectedIconResourceId);

		TabButton tabButton = new TabButton(this, null);
		tabButton.setLabel(label);
		if (iconDrawable != null) {
			tabButton.setIconImage(iconDrawable.getBitmap());
		}
		if (selectedIconDrawable != null) {
			tabButton.setSelectedIconImage(selectedIconDrawable.getBitmap());
		}
		tabButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tabHost.setCurrentTab(pos);
			}
		});

		if (pos == 0) {
			mCustomButtonLayout.addView(getSpaceFillerView());
		}

		mCustomButtonLayout.addView(tabButton);
		mCustomTabButtons.put(tag, tabButton);
		mCustomButtonLayout.addView(getSpaceFillerView());
	}

	private View getSpaceFillerView() {
		View v = new View(this);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT, 1);
		params.weight = 1;
		v.setLayoutParams(params);

		return v;
	}

	@Override
	protected void onNewIntent(final Intent newIntent) {
		System.out.println("onNewIntent");
		super.onNewIntent(newIntent);
		String ac = newIntent.getAction();

		if ("android.intent.action.SEARCH".equals(ac)) {
			FoodMapActivity mapAct = (FoodMapActivity) getLocalActivityManager()
					.getActivity("map");
		
			mapAct.doSearchQuery(newIntent);
		}
	}

}