package com.hobom.mobile.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.hobom.mobile.FoodApplication;
import com.hobom.mobile.R;
import com.hobom.mobile.model.IChart;
import com.hobom.mobile.model.MonthChart;
import com.hobom.mobile.model.TypeChart;

/**
 * 统计类,包括价格,按月统计,按周统计等
 * 
 * @author zhangmingxun
 * 
 */
public class AnalysisActivity extends ListActivity {
	private IChart[] mCharts = new IChart[] { new MonthChart(), new TypeChart() };
	private String[] mMenuText;
	private String[] mMenuSummary;
	private FoodApplication app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		app = (FoodApplication) this.getApplication();
		int length = mCharts.length;
		mMenuText = new String[length];
		mMenuSummary = new String[length];

		for (int i = 0; i < length; i++) {
			mMenuText[i] = mCharts[i].getName();
			mMenuSummary[i] = mCharts[i].getDesc();
		}
		setListAdapter(new SimpleAdapter(this, getListValues(),
				android.R.layout.simple_list_item_2, new String[] {
						IChart.NAME, IChart.DESC }, new int[] {
						android.R.id.text1, android.R.id.text2 }));
	}

	private List<Map<String, String>> getListValues() {
		List<Map<String, String>> values = new ArrayList<Map<String, String>>();
		int length = mMenuText.length;
		for (int i = 0; i < length; i++) {
			Map<String, String> v = new HashMap<String, String>();
			v.put(IChart.NAME, mMenuText[i]);
			v.put(IChart.DESC, mMenuSummary[i]);
			values.add(v);
		}
		return values;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Intent intent = null;
		intent = mCharts[position].execute(app);
		if (intent != null)
			startActivity(intent);
		else
			Toast.makeText(this, "无数据", Toast.LENGTH_LONG).show();

	}
	
	/*// 按返回键
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			
			new AlertDialog.Builder(this)
					.setMessage(R.string.quitconfirm)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {

									quit();

								}

							}).setNegativeButton(R.string.cancel, null).show();
			return true;
			// this.stopService(SERVICE_INTENT);
			// this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	// 退出
	public void quit() {
		finish();


		android.os.Process.killProcess(android.os.Process.myPid());
		ActivityManager actMgr = (ActivityManager) this
				.getSystemService(ACTIVITY_SERVICE);
		actMgr.restartPackage(getPackageName());
	}
	*/

}
