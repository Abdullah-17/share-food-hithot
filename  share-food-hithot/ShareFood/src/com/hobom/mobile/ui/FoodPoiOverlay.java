package com.hobom.mobile.ui;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.mapapi.core.GeoPoint;
import com.amap.mapapi.core.PoiItem;
import com.amap.mapapi.map.MapView;
import com.amap.mapapi.map.PoiOverlay;
import com.amap.mapapi.map.MapView.LayoutParams;
import com.hobom.mobile.R;
/**
 * √¿ ≥Õº≤„
 * @author mingxunzh
 *
 */
public class FoodPoiOverlay extends PoiOverlay {
	private Context context;
	private Drawable drawable;
	private int number = 0;
	private List<PoiItem> poiItem;
	private LayoutInflater mInflater;
	private int height;
	private LinearLayout layout;
	private GeoPoint point;
	private LinearLayout textLayout;
	private LinearLayout textLayout2;
	private TextView title, subtitle;
	private TextView title2;
	private ImageButton nearby, route;

	public FoodPoiOverlay(Context context, Drawable drawable,
			List<PoiItem> poiItems) {
		super(drawable, poiItems);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.poiItem = poiItems;
		mInflater = LayoutInflater.from(context);
		height = drawable.getIntrinsicHeight();
	}

	@Override
	protected Drawable getPopupBackground() {
		// TODO Auto-generated method stub
		drawable = context.getResources().getDrawable(
				R.drawable.popup_pointer_bg);
		return drawable;
	}

	@Override
	public void addToMap(MapView arg0) {
		// TODO Auto-generated method stub
		super.addToMap(arg0);
	}

	@Override
	protected Drawable getPopupMarker(PoiItem arg0) {
		// TODO Auto-generated method stub
		return super.getPopupMarker(arg0);
	}

	@Override
	protected View getPopupView(PoiItem item) {
		// TODO Auto-generated method stub
		View v = mInflater.inflate(R.layout.popup_overlay, null);
		textLayout = (LinearLayout) v.findViewById(R.id.textlayout);
		textLayout2 = (LinearLayout) v.findViewById(R.id.textlayout2);
		title = (TextView) v.findViewById(R.id.map_pop_name);
		subtitle = (TextView) v.findViewById(R.id.map_pop_subname);
		title2 = (TextView) v.findViewById(R.id.map_pop_name_1);
		nearby = (ImageButton) v.findViewById(R.id.map_pop_nearby_btn);
		route = (ImageButton) v.findViewById(R.id.map_pop_route_btn);
		// title.setMaxWidth(MapUtil.getScreenWidth()*3/4);
		System.out.println("the title is:"+item.getTitle());
		System.out.println("the address is:"+item.getSnippet());
		nearby.setVisibility(View.GONE);
		route.setVisibility(View.GONE);
		if(item.getTitle()==null||item.getSnippet()==null){
			textLayout.setVisibility(View.GONE);
			textLayout2.setVisibility(View.VISIBLE);
			title2.setText(item.getTitle()==null?item.getSnippet():item.getTitle());
			
		}else {
			textLayout.setVisibility(View.VISIBLE);
			textLayout2.setVisibility(View.GONE);
			title.setText(item.getTitle());
			subtitle.setText(item.getSnippet());
			
		}
		return v;
	}
	
	@Override
	protected LayoutParams getLayoutParam(int arg0) {
		// TODO Auto-generated method stub
		LayoutParams params = new MapView.LayoutParams(
				MapView.LayoutParams.WRAP_CONTENT,
				MapView.LayoutParams.WRAP_CONTENT, poiItem.get(number)
						.getPoint(), 0, -height, LayoutParams.BOTTOM_CENTER);

		return params;
	}

	@Override
	protected boolean onTap(int index) {
		// TODO Auto-generated method stub
		number = index;
		return super.onTap(index);
	}
}
