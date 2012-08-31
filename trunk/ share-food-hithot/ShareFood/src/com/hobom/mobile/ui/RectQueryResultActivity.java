package com.hobom.mobile.ui;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

import com.amap.mapapi.core.AMapException;
import com.amap.mapapi.core.PoiItem;
import com.amap.mapapi.poisearch.PoiPagedResult;
import com.hobom.mobile.FoodApplication;
import com.hobom.mobile.R;
import com.hobom.mobile.model.POISearchResult;
import com.hobom.mobile.util.ActivityStackManager;

/***
 * poiËÑË÷ÁÐ±í
 * 
 * @author zhangmingxun
 * 
 */
public class RectQueryResultActivity extends Activity {
	int pageIndex = 1;
	int pageNum = 10;
	int totalPage = 1;
	private static final String TAG = "RectQueryResultActivity";

	private ListView listView;

	private Intent intent;
	private String keyword;
	private TextView keywordTx;
	private RectQueryResultAdapter adapter;
	private View btnLayout;
	private Button preBtn;
	private Button nextBtn;
	private Resources res;
	private POISearchResult poiSearchResult;
	private PoiPagedResult pagedSearchResult;
	private List<PoiItem> poiItems;
	private int[] drawableIds = { R.drawable.marker_a, R.drawable.marker_b,
			R.drawable.marker_c, R.drawable.marker_d, R.drawable.marker_e,
			R.drawable.marker_f, R.drawable.marker_g, R.drawable.marker_h,
			R.drawable.marker_i, R.drawable.marker_j };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		setContentView(R.layout.rect_search);

		ActivityStackManager.getInstance().addActivity(this);

		listView = (ListView) findViewById(R.id.rect_search_listview);

		intent = getIntent();
		poiSearchResult = FoodApplication.getApp().getCurrentPOISearchResult();
		pagedSearchResult = poiSearchResult.getPoiPagedResult();
		pageIndex = poiSearchResult.getCurrentPage();
		totalPage = poiSearchResult.getTotalCount();
		keywordTx = (TextView) findViewById(R.id.rect_search_keyword);
		keywordTx.setText(poiSearchResult.getKeyword());
		btnLayout = LayoutInflater.from(this)
				.inflate(R.layout.listfooter, null);
		btnLayout.setTag("footer");
		// btnLayout = (LinearLayout) findViewById(R.id.btnLayout);

		preBtn = (Button) btnLayout.findViewById(R.id.preBtn);
		preBtn.setOnClickListener(new TheClick());
		nextBtn = (Button) btnLayout.findViewById(R.id.nextBtn);
		nextBtn.setOnClickListener(new TheClick());
		
		try {
			poiItems = pagedSearchResult.getPage(pageIndex);
		} catch (AMapException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		adapter = new RectQueryResultAdapter();
		listView.addFooterView(btnLayout);
		listView.setAdapter(adapter);

		// listView.setOnItemClickListener(itemClickListener);
		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				int lastitem = firstVisibleItem + visibleItemCount + 1;

				if (lastitem >= adapter.getCount()) {
					Log.i(TAG, "add");
					if (listView.findViewWithTag("footer") == null)
						listView.addFooterView(btnLayout);
					// btnLayout.setVisibility(View.VISIBLE);
				} else {
					Log.i(TAG, "remove");
					if (pageIndex==totalPage) {
						if (listView.findViewWithTag("footer") == null)
							listView.addFooterView(btnLayout);
						// listView.addFooterView(btnLayout);
						// btnLayout.setVisibility(View.VISIBLE);
						preBtn.setEnabled(true);
						nextBtn.setEnabled(false);
					} else  {
						// btnLayout.setVisibility(View.GONE);
						listView.removeFooterView(btnLayout);
					}
				}

			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

		});

	}

	private final class TheClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			// LIUQUANXING

			switch (v.getId()) {
			case R.id.nextBtn:
				pageIndex++;
				if (!preBtn.isEnabled()) {
					preBtn.setEnabled(true);
				}
				if (pageIndex == totalPage) {
					nextBtn.setEnabled(false);
				}
				break;
			case R.id.preBtn:
				pageIndex--;
				if (!nextBtn.isEnabled()) {
					nextBtn.setEnabled(true);
				}
				if (pageIndex == 1) {
					preBtn.setEnabled(false);
				}
				break;
			}

			try {
				
				poiItems = pagedSearchResult.getPage(pageIndex);
				adapter.notifyDataSetChanged();
				listView.setSelection(0);
			} catch (AMapException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		ActivityStackManager.getInstance().removeActivity(this);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ActivityStackManager.getInstance().removeActivity(this);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		ActivityStackManager.getInstance().back(this);
	}

	private class RectQueryResultAdapter extends BaseAdapter {

		@Override
		public int getCount() {

			return poiItems.size();
			// return 10;
		}

		@Override
		public Object getItem(int position) {

			return poiItems.get(position);
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			LayoutInflater layoutInflater = LayoutInflater
					.from(RectQueryResultActivity.this);
			LinearLayout layout = (LinearLayout) layoutInflater.inflate(
					R.layout.rect_search_item, null);

			final PoiItem r = (PoiItem) getItem(position);

			TextView poiTx = (TextView) layout
					.findViewById(R.id.rect_search_item_poi);
			poiTx.setText(r.getTitle());

			TextView addressTx = (TextView) layout
					.findViewById(R.id.rect_search_item_address);
			addressTx.setText(r.getSnippet());

			View leftLayout = layout.findViewById(R.id.leftlayout);
			leftLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					

				}

			});

			Button rectSearchBtn = (Button) layout
					.findViewById(R.id.rect_search_item_btn);
			rectSearchBtn.setBackgroundResource(drawableIds[position]);
			rectSearchBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					Log.i(TAG, "the pos is:" + position);
					Intent intent = new Intent();
					intent.putExtra("position", position);
					
					setResult(1, intent);
					finish();

					/*
					 * Intent intent = new Intent();
					 * intent.setClass(RectQueryResultActivity.this,
					 * MapPoiDetailActivity.class); intent.putExtra("address",
					 * r.getAddress()); intent.putExtra("id", r.getId());
					 * intent.putExtra("name", r.getName());
					 * intent.putExtra("lon", r.getLon());
					 * intent.putExtra("lat", r.getLat());
					 * 
					 * 
					 * startActivity(intent);
					 */

				}
			});

			return layout;
		}

	}

	
	

}
