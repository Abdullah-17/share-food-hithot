package com.hobom.mobile.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.amap.mapapi.core.GeoPoint;
import com.amap.mapapi.map.MapView;
import com.amap.mapapi.map.Overlay;
import com.hobom.mobile.R;

public class MapPointOverlay extends Overlay{
    private Context context;
    private LayoutInflater inflater;
    private View popUpView;
    private BalloonListener listener;
    private int poiType;
    public MapPointOverlay(Context context,int poiType){
    	this.context=context;
    	this.poiType = poiType;
    	inflater = (LayoutInflater)context.getSystemService(
    	        Context.LAYOUT_INFLATER_SERVICE);
    }
    
   
	
	public interface BalloonListener {
		void onBalloonTap(GeoPoint point);
	};
	public void setListener(BalloonListener listener){
		this.listener = listener;
	}
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		// TODO Auto-generated method stub
		super.draw(canvas, mapView, shadow);                
	}

	@Override
	public boolean onTap(final GeoPoint point, final MapView view) {
		if(popUpView!=null){
			view.removeView(popUpView);
		}
	   // Projection接口用于屏幕像素点坐标系统和地球表面经纬度点坐标系统之间的变换
	    popUpView=inflater.inflate(R.layout.popup, null);
		TextView textView=(TextView) popUpView.findViewById(R.id.PoiName);
		textView.setText("点击即可选择此点");
		MapView.LayoutParams lp;
		lp = new MapView.LayoutParams(MapView.LayoutParams.WRAP_CONTENT,
				MapView.LayoutParams.WRAP_CONTENT,
				point,0,0,
				MapView.LayoutParams.BOTTOM_CENTER);
			view.addView(popUpView,lp);
		popUpView.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				if(poiType==MapPointChooseActivity.MODE_CHOOSE_START){
					if(listener!=null)
						listener.onBalloonTap(point);
					
				}
				
		         else if(poiType==MapPointChooseActivity.MODE_CHOOSE_END){
		        	 if(listener!=null)
							listener.onBalloonTap(point);
				}
				
				view.removeView(popUpView);
				
			}
		});
        return super.onTap(point, view);
	}

	
}
