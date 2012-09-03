package com.hobom.mobile.ui;


import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hobom.mobile.R;
import com.hobom.mobile.model.Food;

public class FoodAdapter extends ArrayAdapter<Food> {

	private ArrayList<Food>foodData;
	private LayoutInflater inflater;
	private Context context;
	public FoodAdapter(Context context, ArrayList<Food>list) {
		super(context, R.layout.foodlist_item);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.foodData = list;
		inflater = LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return foodData.size();
	}
	@Override
	public Food getItem(int position) {
		// TODO Auto-generated method stub
		return foodData.get(position);
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Food food = getItem(position);
		if(convertView==null){
			convertView = inflater.inflate(R.layout.foodlist_item, null);
			
		}
		TextView title = (TextView) convertView.findViewById(R.id.item_title);
		TextView address = (TextView) convertView.findViewById(R.id.item_address);
		ImageView arrow = (ImageView)convertView.findViewById(R.id.item_arrow);
		title.setText(food.getName());
		address.setText(food.getAddress());
		arrow.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(context, FoodDetailInfoActivity.class);
				
			}
			
		});
		return super.getView(position, convertView, parent);
	}

	
}
