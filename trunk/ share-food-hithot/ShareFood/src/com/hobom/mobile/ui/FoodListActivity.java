package com.hobom.mobile.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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

import com.hobom.mobile.R;
import com.hobom.mobile.model.Food;
import com.hobom.mobile.util.ViewUtils;
import com.hobom.mobile.widgets.CommonTitleView;
import com.hobom.mobile.widgets.MyListView;

public class FoodListActivity extends Activity implements OnClickListener{
	private CommonTitleView commonTitleView;
	private EditText mSearchText;
	private View lastMonthEmptyView;
	private MyListView lastMonthListView;
	private View lastMonthFooterView;
	private ArrayList<Food>lastMonthFoodDatas = new ArrayList<Food>();
	private ArrayList<Food>lastMonthFinalFoodDatas = new ArrayList<Food>();
	private View lastWeekEmptyView;
	private MyListView lastWeekListView;
	private View lastWeekFooterView;
	private ArrayList<Food>lastWeekFoodDatas = new ArrayList<Food>();
	private ArrayList<Food>lastWeekFinalFoodDatas = new ArrayList<Food>();
	private View thisWeekEmptyView;
	private MyListView thisWeekListView;
	private View thisWeekFooterView;
	private ArrayList<Food>thisWeekFoodDatas = new ArrayList<Food>();
	private ArrayList<Food>thisWeekFinalFoodDatas = new ArrayList<Food>();
	private RelativeLayout mSearchBox;
	private RelativeLayout mSearchPanel;
	private ImageView mSearchPanelImg;
	private View mBack, mPlaceOrder, mCancel, mSort;
	private View lastWeekTitle,thisWeekTitle,lastMonthTitle;
	private FoodAdapter lastMonthFoodAdapter,lastWeekFoodAdapter,thisWeekFoodAdapter;
	private ProgressDialog mProgressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.foodlist);
		init();
	}
	
	private void init(){
		commonTitleView = (CommonTitleView)findViewById(R.id.header);
		lastMonthFooterView = (RelativeLayout) getLayoutInflater().inflate(
				R.layout.report_footer, null);
		lastWeekFooterView = (RelativeLayout) getLayoutInflater().inflate(
				R.layout.report_footer, null);
		thisWeekFooterView = (RelativeLayout) getLayoutInflater().inflate(
				R.layout.report_footer, null);
		
		
		lastMonthListView = (MyListView)findViewById(R.id.lastmonth_list);
		lastWeekListView = (MyListView)findViewById(R.id.lastweek_list);
		thisWeekListView = (MyListView)findViewById(R.id.thisweek_list);
		
		lastMonthListView.addFooterView(lastMonthFooterView);
		lastMonthFoodAdapter = new FoodAdapter(this,lastMonthFoodDatas);
		lastMonthListView.setAdapter(lastMonthFoodAdapter);
		lastMonthListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
			      viewFoodDetail(lastMonthFoodDatas.get(arg2));
			}
		});
		
		
		lastWeekListView.addFooterView(lastWeekFooterView);
		lastWeekFoodAdapter = new FoodAdapter(this,lastWeekFoodDatas);
		lastWeekListView.setAdapter(lastWeekFoodAdapter);
		lastWeekListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
			      viewFoodDetail(lastWeekFoodDatas.get(arg2));
			}
		});
		
		thisWeekListView.addFooterView(thisWeekFooterView);
		thisWeekFoodAdapter = new FoodAdapter(this,thisWeekFoodDatas);
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
					}else{
						List<Food> plist = new ArrayList<Food>();
						for (int i = 0; i < lastMonthFinalFoodDatas.size(); i++) {
							Food order = lastMonthFinalFoodDatas.get(i);

							if (order.getName().indexOf(key) >= 0) {
								plist.add(order);
							}
						}
						
						lastMonthFoodDatas.clear();
						if (plist.size() > 0) {
							lastMonthFoodDatas.addAll(plist);
						}
						lastMonthFoodAdapter.notifyDataSetChanged();

						
						List<Food> clist = new ArrayList<Food>();
						for (int i = 0; i < lastWeekFinalFoodDatas.size(); i++) {
							Food order = lastWeekFinalFoodDatas.get(i);
							if (order.getName().indexOf(key) >= 0) {
								clist.add(order);
							}
						}
						lastWeekFoodDatas.clear();
						if (clist.size() > 0) {
							lastWeekFoodDatas.addAll(clist);
						}
						lastWeekFoodAdapter.notifyDataSetChanged();

						List<Food> slist = new ArrayList<Food>();
						for (int i = 0; i < thisWeekFinalFoodDatas.size(); i++) {
							Food order = thisWeekFinalFoodDatas.get(i);
							if (order.getName().indexOf(key) >= 0) {
								slist.add(order);
							}
						}
						thisWeekFoodDatas.clear();
						if (slist.size() > 0) {
							thisWeekFoodDatas.addAll(slist);
						}
						thisWeekFoodAdapter.notifyDataSetChanged();
					}
				}catch (Exception e) {
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
	
	
	
	private void viewFoodDetail(Food food){
		Intent intent = new Intent();
		intent.setClass(this, FoodDetailInfoActivity.class);
		intent.putExtra("food", food);
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v==lastWeekTitle){
			
		}else if(v==thisWeekTitle){
			
		}else if(v==lastMonthTitle){
			
		}else if(v==mSort){
			
		}else if(v==mCancel){
			
		}
		
	}

}
