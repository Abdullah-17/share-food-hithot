package com.hobom.mobile.ui;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.hobom.mobile.R;
import com.hobom.mobile.util.ActivityStackManager;

public class MapRouteActivity extends ActivityGroup  {
	private TabHost tabHost;

	// 目的地名称和经纬度
	private String destName;
	private int destLon;
	private int destLat;
	public static final String FROM_TYPE = "fromType";
	public static final int FROM_MAP = 1;
	public static final int FROM_POSITION = 2;
	public static final int FROM_FAV = 3;

	public static final String START_END_TYPE = "startEndType";
	public static final int START_POINT = 1;
	public static final int END_POINT = 2;
	public static String startName = "";
	public static String endName = "";
	public static int startLon = 0;
	public static int startLat = 0;
	public static int endLon = 0;
	public static int endLat = 0;
	private Intent intent;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.map_route_tab);
        ActivityStackManager.getInstance().addActivity(this);
        Window window = getWindow();
        LayoutParams params=window.getAttributes();
        params.x = 0;
        params.y = -220;
        window.setAttributes(params);
        
        
        intent = getIntent();
        if(intent!=null){
     
        if(intent.hasExtra("poiName")){
        	endName = intent.getStringExtra("poiName");
        }
        if(intent.hasExtra("lon"))
            endLon = intent.getIntExtra("lon", 0);
        if(intent.hasExtra("lat"))
            endLat = intent.getIntExtra("lat", 0);
      
        }
        createTab();
        
	}
	
	private void createTab() {

		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.tab_bg_map_route, null);

		TextView view1 = (TextView) layout.findViewById(R.id.tab_drive_route);
		TextView view2 = (TextView) layout.findViewById(R.id.tab_bus_change);

		tabHost = (TabHost) findViewById(R.id.tabhost_top);
		tabHost.setup(this.getLocalActivityManager());

		layout.removeAllViews();

		Intent driveRouteIntent = new Intent(this, DriveRouteActivity.class);
		
		
		tabHost.addTab(tabHost.newTabSpec("driveRoute").setIndicator(view1)
				.setContent(driveRouteIntent));

		Intent busChangeIntent = new Intent(this, BusChangeActivity.class);
		
		
		tabHost.addTab(tabHost.newTabSpec("busChange").setIndicator(view2)
				.setContent(busChangeIntent));

	}


	
	
}
