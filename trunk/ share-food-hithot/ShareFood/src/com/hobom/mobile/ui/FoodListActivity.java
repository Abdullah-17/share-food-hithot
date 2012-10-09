package com.hobom.mobile.ui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hobom.mobile.FoodApplication;
import com.hobom.mobile.R;
import com.hobom.mobile.db.DatabaseAccessor;
import com.hobom.mobile.db.DatabaseAccessor.Tables;
import com.hobom.mobile.db.DatabaseColumns.ConsumeColumn;
import com.hobom.mobile.db.DatabaseColumns.FoodColumn;
import com.hobom.mobile.model.Consume;
import com.hobom.mobile.model.ConsumeDataNameComparator;
import com.hobom.mobile.model.ConsumeDataNewestDateComparator;
import com.hobom.mobile.model.ConsumeDataOldestDateComparator;
import com.hobom.mobile.model.ConsumeDataPriceComparator;
import com.hobom.mobile.model.Food;
import com.hobom.mobile.util.TimeUtil;
import com.hobom.mobile.util.ViewUtils;
import com.hobom.mobile.widgets.CommonTitleView;
import com.hobom.mobile.widgets.MyListView;

public class FoodListActivity extends Activity implements OnClickListener {
	private CommonTitleView commonTitleView;
	private EditText mSearchText;
	private View lastMonthEmptyView;
	private MyListView lastMonthListView;
	private View lastMonthFooterView;
	private ArrayList<Consume> lastMonthFoodDatas = new ArrayList<Consume>();
	private ArrayList<Consume> lastMonthFinalFoodDatas = new ArrayList<Consume>();
	private View lastWeekEmptyView;
	private MyListView lastWeekListView;
	private View lastWeekFooterView;
	private ArrayList<Consume> lastWeekFoodDatas = new ArrayList<Consume>();
	private ArrayList<Consume> lastWeekFinalFoodDatas = new ArrayList<Consume>();
	private View thisWeekEmptyView;
	private MyListView thisWeekListView;
	private View thisWeekFooterView;
	private ArrayList<Consume> thisWeekFoodDatas = new ArrayList<Consume>();
	private ArrayList<Consume> thisWeekFinalFoodDatas = new ArrayList<Consume>();
	private RelativeLayout mSearchBox;
	private RelativeLayout mSearchPanel;
	private ImageView mSearchPanelImg;
	private View mBack, mPlaceOrder, mCancel, mSort;
	private View lastWeekTitle, thisWeekTitle, lastMonthTitle;
	private FoodAdapter lastMonthFoodAdapter, lastWeekFoodAdapter,
			thisWeekFoodAdapter;
	private ProgressDialog mProgressDialog;
	private DatabaseAccessor accessor;
	private int thisWeekPage = 0;
	private int thisMonthPage = 0;
	private int lastMonthPage = 0;
	private static final int DATA_PER_PAGE = 10;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.foodlist);

		init();
		FoodApplication app = (FoodApplication)getApplication();
		accessor = app.getDatabase();
		loadData();
	}

	private void init() {
		commonTitleView = (CommonTitleView) findViewById(R.id.header);
		commonTitleView.getLeftView().setVisibility(View.GONE);
		commonTitleView.getRightView().setVisibility(View.GONE);
		lastMonthFooterView = (RelativeLayout) getLayoutInflater().inflate(
				R.layout.report_footer, null);
		lastWeekFooterView = (RelativeLayout) getLayoutInflater().inflate(
				R.layout.report_footer, null);
		thisWeekFooterView = (RelativeLayout) getLayoutInflater().inflate(
				R.layout.report_footer, null);

		lastMonthListView = (MyListView) findViewById(R.id.lastmonth_list);
		lastWeekListView = (MyListView) findViewById(R.id.lastweek_list);
		thisWeekListView = (MyListView) findViewById(R.id.thisweek_list);

		lastMonthListView.addFooterView(lastMonthFooterView);
		lastMonthFoodAdapter = new FoodAdapter(this, lastMonthFoodDatas);
		lastMonthListView.setAdapter(lastMonthFoodAdapter);
		lastMonthListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				viewFoodDetail(lastMonthFoodDatas.get(arg2));
			}
		});
		lastMonthFooterView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				resetSearch();
				loadLastMonthData(++lastMonthPage);

			}
		});

		lastWeekListView.addFooterView(lastWeekFooterView);
		
		lastWeekFooterView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				resetSearch();
				loadThisMonthData(++thisMonthPage);
			}
			
		});
		lastWeekFoodAdapter = new FoodAdapter(this, lastWeekFoodDatas);
		lastWeekListView.setAdapter(lastWeekFoodAdapter);
		lastWeekListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				viewFoodDetail(lastWeekFoodDatas.get(arg2));
			}
		});

		thisWeekListView.addFooterView(thisWeekFooterView);
		thisWeekFooterView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				resetSearch();
				loadThisWeekData(++thisWeekPage);
				
			}
			
		});
		thisWeekFoodAdapter = new FoodAdapter(this, thisWeekFoodDatas);
		thisWeekListView.setAdapter(thisWeekFoodAdapter);
		thisWeekListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				viewFoodDetail(thisWeekFoodDatas.get(arg2));
			}
		});

		thisWeekListView.requestFocus();
		findViewById(R.id.ivRefresh).setOnClickListener(this);

		mSort = findViewById(R.id.sort);
		mSort.setOnClickListener(this);
		mSearchBox = (RelativeLayout) findViewById(R.id.search_box);
		mSearchPanel = (RelativeLayout) findViewById(R.id.search_panel);
		mSearchPanelImg = (ImageView) findViewById(R.id.search_box_img);
		mCancel = findViewById(R.id.search_cancel_button);
		mCancel.setOnClickListener(this);

		lastWeekTitle = findViewById(R.id.lastweek_title);
		thisWeekTitle = findViewById(R.id.thisweek_title);
		lastMonthTitle = findViewById(R.id.lastmonth_title);
		lastWeekTitle.setOnClickListener(this);
		thisWeekTitle.setOnClickListener(this);
		lastMonthTitle.setOnClickListener(this);

		lastMonthEmptyView = findViewById(R.id.lastmonth_list_empty);
		lastWeekEmptyView = findViewById(R.id.lastweek_list_empty);
		thisWeekEmptyView = findViewById(R.id.thisweek_list_empty);

		mSearchText = (EditText) findViewById(R.id.search_text);
		mSearchText.clearFocus();
		mSearchText.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				try {
					String key = s.toString();
					if (TextUtils.isEmpty(key)) {
						lastMonthFoodDatas.clear();
						if (lastMonthFinalFoodDatas.size() > 0) {
							lastMonthFoodDatas.addAll(lastMonthFinalFoodDatas);
						}
						lastMonthFoodAdapter.notifyDataSetChanged();

						lastWeekFoodDatas.clear();
						if (lastWeekFinalFoodDatas.size() > 0) {
							lastWeekFoodDatas.addAll(lastWeekFinalFoodDatas);
						}
						lastWeekFoodAdapter.notifyDataSetChanged();

						thisWeekFoodDatas.clear();
						if (thisWeekFinalFoodDatas.size() > 0) {
							thisWeekFoodDatas.addAll(thisWeekFinalFoodDatas);
						}
						thisWeekFoodAdapter.notifyDataSetChanged();
					} else {
						List<Consume> plist = new ArrayList<Consume>();
						for (int i = 0; i < lastMonthFinalFoodDatas.size(); i++) {
							Consume order = lastMonthFinalFoodDatas.get(i);

							if (order.getFood().getName().indexOf(key) >= 0) {
								plist.add(order);
							}
						}

						lastMonthFoodDatas.clear();
						if (plist.size() > 0) {
							lastMonthFoodDatas.addAll(plist);
						}
						lastMonthFoodAdapter.notifyDataSetChanged();

						List<Consume> clist = new ArrayList<Consume>();
						for (int i = 0; i < lastWeekFinalFoodDatas.size(); i++) {
							Consume order = lastWeekFinalFoodDatas.get(i);
							if (order.getFood().getName().indexOf(key) >= 0) {
								clist.add(order);
							}
						}
						lastWeekFoodDatas.clear();
						if (clist.size() > 0) {
							lastWeekFoodDatas.addAll(clist);
						}
						lastWeekFoodAdapter.notifyDataSetChanged();

						List<Consume> slist = new ArrayList<Consume>();
						for (int i = 0; i < thisWeekFinalFoodDatas.size(); i++) {
							Consume order = thisWeekFinalFoodDatas.get(i);
							if (order.getFood().getName().indexOf(key) >= 0) {
								slist.add(order);
							}
						}
						thisWeekFoodDatas.clear();
						if (slist.size() > 0) {
							thisWeekFoodDatas.addAll(slist);
						}
						thisWeekFoodAdapter.notifyDataSetChanged();
					}
				} catch (Exception e) {
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				if (TextUtils.isEmpty(mSearchText.getText().toString())) {
					lastMonthFinalFoodDatas.clear();
					if (lastMonthFoodDatas.size() > 0)
						lastMonthFinalFoodDatas.addAll(lastMonthFoodDatas);

					lastWeekFinalFoodDatas.clear();
					if (lastWeekFoodDatas.size() > 0)
						lastWeekFinalFoodDatas.addAll(lastWeekFoodDatas);

					thisWeekFinalFoodDatas.clear();
					if (thisWeekFoodDatas.size() > 0)
						thisWeekFinalFoodDatas.addAll(thisWeekFoodDatas);
				}
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

		});

		mSearchText.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				mCancel.setVisibility(View.VISIBLE);
				mSearchPanelImg.setPadding(0, 0, 105, 0);
				mSearchPanelImg.invalidate();
				mSearchPanelImg.requestLayout();
				mSearchPanel.requestLayout();
				mSearchBox.requestLayout();
				return false;
			}
		});

	}

	private void setListState() {
		if (lastMonthFoodDatas.size() == 0) {
			lastMonthListView.setVisibility(View.VISIBLE);
			lastMonthEmptyView.setVisibility(View.VISIBLE);
		} else {
			lastMonthListView.setVisibility(View.VISIBLE);
			lastMonthEmptyView.setVisibility(View.GONE);
		}
		if (lastWeekFoodDatas.size() == 0) {
			lastWeekListView.setVisibility(View.VISIBLE);
			lastWeekEmptyView.setVisibility(View.VISIBLE);
		} else {
			lastWeekListView.setVisibility(View.VISIBLE);
			lastWeekEmptyView.setVisibility(View.GONE);
		}

		if (thisWeekFoodDatas.size() == 0) {
			thisWeekListView.setVisibility(View.VISIBLE);

			thisWeekEmptyView.setVisibility(View.VISIBLE);
		} else {
			thisWeekListView.setVisibility(View.VISIBLE);

			thisWeekEmptyView.setVisibility(View.GONE);
		}
	}

	private void cancelSearch() {

		mCancel.setVisibility(View.GONE);
		ViewUtils.hideKeyboard(FoodListActivity.this, mSearchText);
		mSearchText.setText("");
		mSearchPanelImg.setPadding(0, 0, 0, 0);
		mSearchPanelImg.invalidate();
		mSearchPanelImg.requestLayout();
		mSearchPanel.requestLayout();
		mSearchBox.requestLayout();
	}

	private void clearFilter() {
		if (mSearchText != null) {
			mSearchText.setText("");
		}
	}

	private void resetSearch() {
		if (!TextUtils.isEmpty(mSearchText.getText().toString())) {
			mSearchText.setText("");
		}
	}

	public void showProgress(String message) {
		if (mProgressDialog != null) {
			mProgressDialog.setMessage(message);
			mProgressDialog.show();
		} else {
			mProgressDialog = ProgressDialog.show(this, null, message, true,
					true);
		}
	}

	public void hideProgress() {
		try {
			mProgressDialog.dismiss();
		} catch (Exception e) {
		}
		mProgressDialog = null;
	}

	private void loadData() {
		
		loadThisWeekData(thisWeekPage);
		loadThisMonthData(thisMonthPage);
		loadLastMonthData(lastMonthPage);

		/*int weekStart = (int) (TimeUtil.getWeekStart() / 1000);
		int lastMonthStart = (int) (TimeUtil.getLastMonthStart() / 1000);

		Cursor thisWeekCursor = accessor.query(true, Tables.CONSUME, null,
				ConsumeColumn.DATE + " >=?",
				new String[] { String.valueOf(weekStart) }, null, null, null,
				"0,10");
		loadData(thisWeekCursor, thisWeekFoodDatas);
		thisWeekFoodAdapter.notifyDataSetChanged();
		if (thisWeekFoodDatas.size() % 2 == 0) {
			thisWeekFooterView.setBackgroundResource(R.color.white);
		} else {
			thisWeekFooterView.setBackgroundResource(R.color.light_grey);
		}
		Cursor thisMonthCursor = accessor
				.query(true,
						Tables.CONSUME,
						null,
						ConsumeColumn.DATE + " >= ? and " + ConsumeColumn.DATE
								+ " <=?",
						new String[] { String.valueOf(weekStart),
								String.valueOf(lastMonthStart) }, null, null,
						null, "0,10");
		loadData(thisMonthCursor, lastWeekFoodDatas);
		lastWeekFoodAdapter.notifyDataSetChanged();
		if (lastWeekFoodDatas.size() % 2 == 0) {
			lastWeekFooterView.setBackgroundResource(R.color.white);
		} else {
			lastWeekFooterView.setBackgroundResource(R.color.light_grey);
		}
		Cursor lastMonthCursor = accessor.query(true, Tables.CONSUME, null,
				ConsumeColumn.DATE + " <? ",
				new String[] { String.valueOf(lastMonthStart) }, null, null,
				null, "0,10");
		loadData(lastMonthCursor, lastMonthFoodDatas);
		lastMonthFoodAdapter.notifyDataSetChanged();

		if (lastMonthFoodDatas.size() % 2 == 0) {
			lastMonthFooterView.setBackgroundResource(R.color.white);
		} else {
			lastMonthFooterView.setBackgroundResource(R.color.light_grey);
		}*/
	}

	private void loadThisWeekData(int page) {
		int weekStart = (int) (TimeUtil.getWeekStart() / 1000);
		System.out.println("this weekstart is:"+weekStart);
		System.out.println("current is:"+TimeUtil.getCurrTimeInt());
		StringBuffer sb = new StringBuffer();
		sb.append(page * DATA_PER_PAGE);
		sb.append(",");
		sb.append((page + 1) * DATA_PER_PAGE);
		Cursor thisWeekCursor = accessor.query(true, Tables.CONSUME, null,
				ConsumeColumn.DATE + " >=?",
				new String[] { String.valueOf(weekStart) }, null, null, null,
				sb.toString());
		if (thisWeekCursor.getCount() == 0) {
			if (thisWeekListView.getFooterViewsCount() > 0)
				thisWeekListView.removeFooterView(thisWeekFooterView);
		} else {
			loadData(thisWeekCursor, thisWeekFoodDatas);
			thisWeekFoodAdapter.notifyDataSetChanged();

			if (thisWeekFoodDatas.size() % 2 == 0) {
				thisWeekFooterView.setBackgroundResource(R.color.white);
			} else {
				thisWeekFooterView.setBackgroundResource(R.color.light_grey);
			}
		}
		if (thisWeekCursor != null) {
			thisWeekCursor.close();
			thisWeekCursor = null;

		}
	}

	private void loadThisMonthData(int page) {
		int weekStart = (int) (TimeUtil.getWeekStart() / 1000);
		int lastMonthStart = (int) (TimeUtil.getLastMonthStart() / 1000);
		System.out.println("the lastmonth start is:"+lastMonthStart);
		StringBuffer sb = new StringBuffer();
		sb.append(page * DATA_PER_PAGE);
		sb.append(",");
		sb.append((page + 1) * DATA_PER_PAGE);
		Cursor thisMonthCursor = accessor
				.query(true,
						Tables.CONSUME,
						null,
						ConsumeColumn.DATE + " >= ? and " + ConsumeColumn.DATE
								+ " <=?",
						new String[] { String.valueOf(lastMonthStart),
								String.valueOf(weekStart) }, null, null,
						null, sb.toString());
		if (thisMonthCursor.getCount() == 0) {
			if (lastWeekListView.getFooterViewsCount() > 0)
				lastWeekListView.removeFooterView(lastWeekFooterView);
		} else {
			loadData(thisMonthCursor, lastWeekFoodDatas);
			lastWeekFoodAdapter.notifyDataSetChanged();
			if (lastWeekFoodDatas.size() % 2 == 0) {
				lastWeekFooterView.setBackgroundResource(R.color.white);
			} else {
				lastWeekFooterView.setBackgroundResource(R.color.light_grey);
			}
		}
		if (thisMonthCursor != null) {
			thisMonthCursor.close();
			thisMonthCursor = null;

		}
	}

	private void loadLastMonthData(int page) {
		int lastMonthStart = (int) (TimeUtil.getLastMonthStart() / 1000);
		StringBuffer sb = new StringBuffer();
		sb.append(page * DATA_PER_PAGE);
		sb.append(",");
		sb.append((page + 1) * DATA_PER_PAGE);
		Cursor lastMonthCursor = accessor.query(true, Tables.CONSUME, null,
				ConsumeColumn.DATE + " <? ",
				new String[] { String.valueOf(lastMonthStart) }, null, null,
				null, sb.toString());
		if (lastMonthCursor.getCount() == 0) {
			if (lastMonthListView.getFooterViewsCount() > 0)
				lastMonthListView.removeFooterView(lastMonthFooterView);
		} else {
			loadData(lastMonthCursor, lastMonthFoodDatas);
			lastMonthFoodAdapter.notifyDataSetChanged();

			if (lastMonthFoodDatas.size() % 2 == 0) {
				lastMonthFooterView.setBackgroundResource(R.color.white);
			} else {
				lastMonthFooterView.setBackgroundResource(R.color.light_grey);
			}
		}
		if (lastMonthCursor != null) {
			lastMonthCursor.close();
			lastMonthCursor = null;

		}
	}

	private void loadData(Cursor cursor, List<Consume> list) {
		System.out.println("load Data");
		int foodId = 0;
		long consumeDate = 0;
		String remark = null;
		String picPath = null;
		int rating = 0;
		Consume consume = null;
		Food food = null;
		while (cursor.moveToNext()) {
			consume = new Consume();
			consumeDate = cursor.getInt(cursor
					.getColumnIndex(ConsumeColumn.DATE));
			remark = cursor.getString(cursor
					.getColumnIndex(ConsumeColumn.COMMENT));
			picPath = cursor.getString(cursor
					.getColumnIndex(ConsumeColumn.PICPATH));
			rating = cursor.getInt(cursor.getColumnIndex(ConsumeColumn.RATING));
			foodId = cursor.getInt(cursor.getColumnIndex(ConsumeColumn.FOODID));
			consume.setDate(consumeDate);
			consume.setPicPath(picPath);
			consume.setRating(rating);
			consume.setRemark(remark);

			Cursor foodCursor = accessor.query(true, Tables.FOOD, null, ""
					+ FoodColumn._ID + " = ?",
					new String[] { String.valueOf(foodId) }, null, null, null,
					null);
			if (foodCursor.moveToFirst()) {
				String name = foodCursor.getString(foodCursor
						.getColumnIndex(FoodColumn.NAME));
				String address = foodCursor.getString(foodCursor
						.getColumnIndex(FoodColumn.ADDRESS));
				String price = foodCursor.getString(foodCursor
						.getColumnIndex(FoodColumn.PRICE));
				int lat = foodCursor.getInt(foodCursor
						.getColumnIndex(FoodColumn.LAT));
				int lon = foodCursor.getInt(foodCursor
						.getColumnIndex(FoodColumn.LON));
				String type = foodCursor.getString(foodCursor
						.getColumnIndex(FoodColumn.TYPE));
				String tel = foodCursor.getString(foodCursor
						.getColumnIndex(FoodColumn.TEL));

				food = new Food();
				food.setName(name);
				food.setAddress(address);
				food.setPrice(Float.parseFloat(price));
				food.setLatitude(lat);
				food.setLongitude(lon);
				food.setType(type);
				food.setTelephone(tel);

			}

			if(foodCursor!=null){
				foodCursor.close();
				foodCursor = null;
			}
			consume.setFood(food);
			list.add(consume);

		}

		

	}

	private void viewFoodDetail(Consume consume) {
		Intent intent = new Intent();
		intent.setClass(this, FoodDetailInfoActivity.class);
		intent.putExtra("consume", consume);
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == lastWeekTitle) {
			if (lastWeekListView.getVisibility() == View.VISIBLE) {
				lastWeekListView.setVisibility(View.GONE);
			} else {
				lastWeekListView.setVisibility(View.VISIBLE);
			}
		} else if (v == thisWeekTitle) {
			if (thisWeekListView.getVisibility() == View.VISIBLE) {
				thisWeekListView.setVisibility(View.GONE);
			} else {
				thisWeekListView.setVisibility(View.VISIBLE);
			}
		} else if (v == lastMonthTitle) {
			if (lastMonthListView.getVisibility() == View.VISIBLE) {
				lastMonthListView.setVisibility(View.GONE);
			} else {
				lastMonthListView.setVisibility(View.VISIBLE);
			}
		} else if (v == mSort) {
			new AlertDialog.Builder(this)
					.setTitle("Sort  by")
					.setItems(R.array.info_array,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int item) {

									if (thisWeekFoodDatas.size() > 0) {
										java.util.Collections.sort(
												thisWeekFoodDatas,
												comparators.get(item));
										thisWeekFoodAdapter
												.notifyDataSetChanged();
									}
									if (lastWeekFoodDatas.size() > 0) {
										java.util.Collections.sort(
												lastWeekFoodDatas,
												comparators.get(item));
										lastWeekFoodAdapter
												.notifyDataSetChanged();
									}
									if (lastMonthFoodDatas.size() > 0) {
										java.util.Collections.sort(
												lastMonthFoodDatas,
												comparators.get(item));
										lastMonthFoodAdapter
												.notifyDataSetChanged();
									}
									setListState();
								}
							}).create().show();
		} else if (v == mCancel) {
			cancelSearch();
		}

	}

	private List<Comparator<Consume>> comparators = new ArrayList<Comparator<Consume>>();
	{
		comparators.add(new ConsumeDataNewestDateComparator());
		comparators.add(new ConsumeDataOldestDateComparator());
		comparators.add(new ConsumeDataNameComparator());
		comparators.add(new ConsumeDataPriceComparator());

	}
}
