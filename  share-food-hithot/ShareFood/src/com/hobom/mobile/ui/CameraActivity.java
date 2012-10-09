package com.hobom.mobile.ui;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

import com.hobom.mobile.FoodApplication;
import com.hobom.mobile.R;
import com.hobom.mobile.db.DatabaseAccessor;
import com.hobom.mobile.db.DatabaseAccessor.Tables;
import com.hobom.mobile.db.DatabaseColumns.ConsumeColumn;
import com.hobom.mobile.db.DatabaseColumns.FoodColumn;
import com.hobom.mobile.model.Consume;
import com.hobom.mobile.model.Food;
import com.hobom.mobile.util.FoodUtil;
import com.hobom.mobile.util.ImageCache;

/**
 * ≈ƒ’’¿‡
 * 
 * @author zhangmingxun
 * 
 */
public class CameraActivity extends Activity implements
		AdapterView.OnItemSelectedListener, ViewFactory, OnClickListener {

	private final static int REQUEST_CAMERA_CODE = 100;

	private ImageSwitcher switcher;
	private TextView name, address;
	private ImageView map, detail, share;
	private ProgressDialog mProgressDialog;
	private DatabaseAccessor accessor;
	private List<Consume> consumeList = new LinkedList<Consume>();
	private Consume currentConsume;
	private ImageAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera);
		FoodApplication app = (FoodApplication)getApplication();
		accessor = app.getDatabase();
		initView();
		initData();
	}

	private void initView() {
		switcher = (ImageSwitcher) findViewById(R.id.switcher);
		name = (TextView) findViewById(R.id.name);
		address = (TextView) findViewById(R.id.address);
		map = (ImageView) findViewById(R.id.footbar_map);
		detail = (ImageView) findViewById(R.id.footbar_detail);
		share = (ImageView) findViewById(R.id.footbar_share);
		map.setOnClickListener(this);
		detail.setOnClickListener(this);
		share.setOnClickListener(this);
		switcher.setFactory(this);
		switcher.setInAnimation(AnimationUtils.loadAnimation(this,
				android.R.anim.fade_in));
		switcher.setOutAnimation(AnimationUtils.loadAnimation(this,
				android.R.anim.fade_out));
		Gallery g = (Gallery) findViewById(R.id.gallery);
		adapter = new ImageAdapter(this);
		g.setAdapter(adapter);
		g.setOnItemSelectedListener(this);

	}

	private void initData() {
		showProgress("º”‘ÿ÷–£¨«Î…‘∫Ú...");
		Cursor cursor = accessor.query(true, Tables.CONSUME, null, null, null,
				null, null, null, null);
	
		int foodId = 0;
		String picPath = null;
		long consumeDate = 0;
		int rating = 0;
		String remark = null;
		Consume consume = null;
		Food food = null;

		while (cursor.moveToNext()) {
			consume = new Consume();
			foodId = cursor.getInt(cursor.getColumnIndex(ConsumeColumn.FOODID));
			Cursor foodCursor = accessor.query(true, Tables.FOOD, null, ""
					+ FoodColumn._ID + "=?",
					new String[] { String.valueOf(foodId) }, null, null, null,
					null);
			if (foodCursor.moveToFirst()) {
				food = new Food();
				food.setAddress(foodCursor.getString(foodCursor
						.getColumnIndex(FoodColumn.ADDRESS)));
				food.setLatitude(foodCursor.getInt(foodCursor
						.getColumnIndex(FoodColumn.LAT)));
				food.setLongitude(foodCursor.getInt(foodCursor
						.getColumnIndex(FoodColumn.LON)));
				food.setName(foodCursor.getString(foodCursor
						.getColumnIndex(FoodColumn.NAME)));
				food.setPrice(foodCursor.getFloat(foodCursor
						.getColumnIndex(FoodColumn.PRICE)));
				food.setType(foodCursor.getString(foodCursor
						.getColumnIndex(FoodColumn.TYPE)));
				consume.setFood(food);

			}
			consumeDate = cursor.getLong(cursor
					.getColumnIndex(ConsumeColumn.DATE));
			picPath = cursor.getString(cursor
					.getColumnIndex(ConsumeColumn.PICPATH));
			rating = cursor.getInt(cursor.getColumnIndex(ConsumeColumn.RATING));
			remark = cursor.getString(cursor
					.getColumnIndex(ConsumeColumn.COMMENT));
			consume.setDate(consumeDate);
			consume.setPicPath(picPath);
			consume.setRating(rating);
			consume.setRemark(remark);

			
			if(foodCursor!=null){
				foodCursor.close();
				foodCursor = null;
			}
			consumeList.add(consume);
		}
		if(cursor!=null){
			cursor.close();
			cursor = null;
		}
		adapter.notifyDataSetChanged();
		hideProgress();

	}

	public void showProgress(String message) {
		if (mProgressDialog != null) {
			mProgressDialog.setMessage(message);
			mProgressDialog.show();
		} else {
			mProgressDialog = ProgressDialog.show(this.getParent(), "", message, true,
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

	@Override
	public View makeView() {
		// TODO Auto-generated method stub
		ImageView i = new ImageView(this);
		i.setBackgroundColor(0xFF000000);
		i.setScaleType(ImageView.ScaleType.FIT_CENTER);
		i.setLayoutParams(new ImageSwitcher.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		return i;
	}

	public void onItemSelected(AdapterView<?> parent, View v, int position,
			long id) {

		currentConsume = consumeList.get(position);
		Bitmap bm = ImageCache.getInstance().getBitmap(
				currentConsume.getPicPath());

		switcher.setImageDrawable(new BitmapDrawable(bm));

		name.setText(currentConsume.getFood().getName());
		address.setText(currentConsume.getFood().getAddress());

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	public class ImageAdapter extends BaseAdapter {
		public ImageAdapter(Context c) {
			mContext = c;
		}

		public int getCount() {
			return consumeList.size();
		}

		public Object getItem(int position) {
			return consumeList.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			Consume consume = consumeList.get(position);

			ImageView i = new ImageView(mContext);

			i.setImageBitmap(ImageCache.getInstance().getBitmap(
					consume.getPicPath()));

			i.setAdjustViewBounds(true);
			i.setLayoutParams(new Gallery.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			// i.setBackgroundResource(R.drawable.picture_frame);
			return i;
		}

		private Context mContext;

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub;
		if(currentConsume==null)
			return;
		Intent intent = new Intent();
		if (v == map) {

			intent.setClass(this, SimpleFoodMapActivity.class);
			intent.putExtra("lat", currentConsume.getFood().getLatitude());
			intent.putExtra("lon", currentConsume.getFood().getLongitude());
			startActivity(intent);
			
		} else if (v == detail) {

			intent.setClass(this, FoodDetailInfoActivity.class);
			intent.putExtra("consume", currentConsume);
			startActivity(intent);
		} else if (v == share) {

			FoodUtil.showShareDialog(this, currentConsume.getFood().getAddress()+":"+currentConsume.getFood().getName()+":"+currentConsume.getRemark(), currentConsume.getPicPath());
			
		}

	}
}
