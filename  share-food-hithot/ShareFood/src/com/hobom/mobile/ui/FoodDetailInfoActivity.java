package com.hobom.mobile.ui;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.hobom.mobile.R;
import com.hobom.mobile.model.Consume;
import com.hobom.mobile.util.ImageCache;
import com.hobom.mobile.widgets.CommonTitleView;
/**
 * 美食详情类
 * @author zhangmingxun
 *
 */
public class FoodDetailInfoActivity extends Activity implements OnClickListener{

	private CommonTitleView commonTitleView;
	private TextView title,address;
	private Button tel;
	private RatingBar ratingBar;
	private ImageView imageView;
	private TextView price,remark;
	private TextView name;
	private Consume consume;
	private ImageView left;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.food_detail);
		consume = (Consume) getIntent().getSerializableExtra("consume");
		init();
	}
	private void init(){
		commonTitleView = (CommonTitleView)findViewById(R.id.header);
		commonTitleView.setTitle("美食详情");
		left = commonTitleView.getLeftView();
		left.setOnClickListener(this);
		commonTitleView.getRightView().setVisibility(View.GONE);
		name = (TextView)findViewById(R.id.name);
		address = (TextView)findViewById(R.id.address);
		tel = (Button)findViewById(R.id.tel);
		
		ratingBar = (RatingBar)findViewById(R.id.rb);
		imageView = (ImageView)findViewById(R.id.image);
		price = (TextView)findViewById(R.id.price);
		remark = (TextView)findViewById(R.id.remark);
		name.setText(consume.getFood().getName());
		address.setText(consume.getFood().getAddress());
	
		tel.setText(consume.getFood().getTelephone());
		tel.setOnClickListener(this);
		ratingBar.setRating(consume.getRating());
		price.setText(""+consume.getFood().getPrice());
		remark.setText(consume.getRemark());
		Bitmap bm = ImageCache.getInstance().getBitmap(
				consume.getPicPath());

		
		imageView.setImageBitmap(bm);
		
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v==tel){
			if(TextUtils.isEmpty(tel.getText()))
				return;
			Intent intent = new Intent(Intent.ACTION_CALL);
			intent.setData(Uri.parse("tel://"+consume.getFood().getTelephone()));
		    startActivity(intent);
		}else if(v==left){
			finish();
		}
		
	}

	
}
