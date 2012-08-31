package com.hobom.mobile.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hobom.mobile.R;

public class CommonTitleView extends LinearLayout {
	private ImageView leftImageView;
	private TextView titleView;
	private ImageView rightImageView;
	public CommonTitleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CommonTitleView(Context context) {
		super(context);
		init();
	}

	private void init() {
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View view = inflater.inflate(R.layout.head, this, true);
		leftImageView = (ImageView)view.findViewById(R.id.left_btn);
		rightImageView = (ImageView)view.findViewById(R.id.right_btn);
		titleView = (TextView)view.findViewById(R.id.title);
	}
	
	public void setLeft(int resouceId){
		leftImageView.setBackgroundResource(resouceId);
	}
	public void setRight(int resourceId){
		rightImageView.setBackgroundResource(resourceId);
	}
	public void setTitle(String title){
		titleView.setText(title);
	}
	public ImageView getLeftView(){
		return leftImageView;
	}
	public ImageView getRightView(){
		return rightImageView;
	}
	public TextView getTitle(){
		return titleView;
	}
	
}
