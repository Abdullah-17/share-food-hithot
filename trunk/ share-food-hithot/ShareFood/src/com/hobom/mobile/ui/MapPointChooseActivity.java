package com.hobom.mobile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.amap.mapapi.core.GeoPoint;
import com.amap.mapapi.map.MapActivity;
import com.amap.mapapi.map.MapController;
import com.amap.mapapi.map.MapView;
import com.hobom.mobile.R;
import com.hobom.mobile.ui.MapPointOverlay.BalloonListener;
import com.hobom.mobile.util.Constant;
import com.hobom.mobile.util.PreferenceUtil;

/**
 * 点击选择
 * 
 * @author mingxunzh
 * 
 */
public class MapPointChooseActivity extends MapActivity {
	public static final int MODE_CHOOSE_START = 0;
	public static final int MODE_CHOOSE_END = 1;
	private MapView mapView;
	private MapController mc;
	private MapPointOverlay overlay;
	private int type;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_point_choose);
		type = getIntent().getIntExtra("mapMode", MODE_CHOOSE_START);
		init();

	}

	private void init() {
		mapView = (MapView) findViewById(R.id.main_mapView);
		mc = mapView.getController();
		int lastLat = PreferenceUtil.getSettingInt(Constant.LASTLAT,
				(int) (39.906033 * 1E6));
		int lastLon = PreferenceUtil.getSettingInt(Constant.LASTLON,
				(int) (116.397695 * 1E6));
		GeoPoint point = new GeoPoint(lastLat, lastLon); // 用给定的经纬度构造一个GeoPoint，单位是微度
		// (度 * 1E6)
		mapView.setBuiltInZoomControls(true); // 设置启用内置的缩放控件
	
		mc.setCenter(point); // 设置地图中心点
		mc.setZoom(12); // 设置地图zoom级别
	
		overlay=new MapPointOverlay(this,type);
		overlay.setListener(new BalloonListener(){

			@Override
			public void onBalloonTap(GeoPoint point) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("mapMode", type);
				intent.putExtra("LonLat", point);
				setResult(1,intent);
				finish();
			}
			
		});
		mapView.getOverlays().add(overlay);
		 Toast.makeText(this, "点击地图点选", 0).show();
	}

	
	

}
