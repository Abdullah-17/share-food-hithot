/***
 * Copyright (c) 2010 readyState Software Ltd
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.hobom.mobile.ui;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.mapapi.core.GeoPoint;
import com.amap.mapapi.core.OverlayItem;
import com.hobom.mobile.R;
import com.hobom.mobile.util.MapUtil;

/**
 * A view representing a MapView marker information balloon.
 * <p>
 * This class has a number of Android resource dependencies:
 * <ul>
 * <li>drawable/balloon_overlay_bg_selector.xml</li>
 * <li>drawable/balloon_overlay_close.png</li>
 * <li>drawable/balloon_overlay_focused.9.png</li>
 * <li>drawable/balloon_overlay_unfocused.9.png</li>
 * <li>layout/balloon_map_overlay.xml</li>
 * </ul>
 * </p>
 * 
 * @author Jeff Gilfelt
 * 
 */
public class BalloonOverlayView extends FrameLayout implements View.OnClickListener {

	private LinearLayout layout;
	private GeoPoint point;
	private LinearLayout textLayout;
	private LinearLayout textLayout2;
	private TextView title, subtitle;
	private TextView title2;
	private ImageButton nearby, route;
	public static final int POPUP_POI = 0; // POI
	public static final int POPUP_MYLOC = 1; // 我的位置
	public static final int POPUP_CHOOSEPOINT = 2;// 选择起点和终点
	public static final int POPUP_LONGPRESSPOINT = 3;// 长按点选
	public static final int POPUP_FAV = 4;// 收藏位置点选
	private int type;
	private Context context;
	private OverlayItem item;
	/**
	 * Create a new BalloonOverlayView.
	 * 
	 * @param context
	 *            - The activity context.
	 * @param balloonBottomOffset
	 *            - The bottom padding (in pixels) to be applied when rendering
	 *            this view.
	 */
	public BalloonOverlayView(Context context, int balloonBottomOffset,
			GeoPoint point,int type) {

		super(context);
		this.point = point;
		setPadding(10, 0, 10, balloonBottomOffset);
		layout = new LinearLayout(context);
		layout.setVisibility(VISIBLE);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.popup_overlay, layout);
		textLayout = (LinearLayout) v.findViewById(R.id.textlayout);
		textLayout2 = (LinearLayout) v.findViewById(R.id.textlayout2);
		title = (TextView) v.findViewById(R.id.map_pop_name);
		subtitle = (TextView) v.findViewById(R.id.map_pop_subname);
		title2 = (TextView) v.findViewById(R.id.map_pop_name_1);
		nearby = (ImageButton) v.findViewById(R.id.map_pop_nearby_btn);
		route = (ImageButton) v.findViewById(R.id.map_pop_route_btn);
		// title.setMaxWidth(MapUtil.getScreenWidth()*3/4);

		nearby.setOnClickListener(this);
		route.setOnClickListener(this);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.NO_GRAVITY;

		addView(layout, params);

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int widthSpec = MeasureSpec.makeMeasureSpec(
				MapUtil.getScreenWidth() * 3 / 4, MeasureSpec.AT_MOST);
     
		layout.measure(widthSpec, heightMeasureSpec);
		int width = layout.getMeasuredWidth();
		int height =layout.getMeasuredHeight();
		if(width>MapUtil.getScreenWidth()*3/4)
			width = MapUtil.getScreenWidth()*3/4;
		
		
        setMeasuredDimension(width,height);
	}

	
	
	private void hide(View view) {
		view.setVisibility(View.GONE);
	}

	private void show(View view) {
		view.setVisibility(View.VISIBLE);
	}
	
	public void setData(OverlayItem item) {
		if (item == null)
			return;
		this.item = item;
		layout.setVisibility(VISIBLE);

		show(textLayout2);
		hide(textLayout);
		if (item.getTitle() != null) {
			if ((type == POPUP_MYLOC || type == POPUP_LONGPRESSPOINT)
					&& !TextUtils.isEmpty(item.getSnippet())) {
				show(textLayout);
				hide(textLayout2);
				title.setText(item.getTitle());
			} else
				title2.setText(item.getTitle());
		}
		if (!TextUtils.isEmpty(item.getSnippet())) {
			subtitle.setText(item.getSnippet());
		}

	}
	
	
	
	

	public void setData(Object obj) {

	}

	public void updateGeoPoint(GeoPoint geoPoint) {
		this.point = geoPoint;
	}

	public LinearLayout getLayout() {
		return layout;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

}
