package com.hobom.mobile.ui;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.hobom.mobile.R;
import com.hobom.mobile.widgets.CommonTitleView;
/**
 * 美食详情类
 * @author zhangmingxun
 *
 */
public class FoodDetailInfoActivity extends Activity implements OnClickListener{

	private CommonTitleView commonTitleView;
	private TextView title,address;
	private Button tel,map,nearby,route;
	private RatingBar ratingBar;
	private ImageView imageView;
	private TextView price,remark;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.food_detail);
		init();
	}
	private void init(){
		commonTitleView = (CommonTitleView)findViewById(R.id.header);
		title = (TextView)findViewById(R.id.title);
		address = (TextView)findViewById(R.id.address);
		tel = (Button)findViewById(R.id.tel);
		map = (Button)findViewById(R.id.map);
		nearby = (Button)findViewById(R.id.near_by);
		route = (Button)findViewById(R.id.to_here);
		ratingBar = (RatingBar)findViewById(R.id.rb);
		imageView = (ImageView)findViewById(R.id.image);
		price = (TextView)findViewById(R.id.price);
		remark = (TextView)findViewById(R.id.remark);
		
		
		
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}

	
}
