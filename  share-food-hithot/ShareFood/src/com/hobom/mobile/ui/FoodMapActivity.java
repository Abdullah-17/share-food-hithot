package com.hobom.mobile.ui;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.SearchRecentSuggestions;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.amap.mapapi.core.AMapException;
import com.amap.mapapi.core.GeoPoint;
import com.amap.mapapi.core.PoiItem;
import com.amap.mapapi.map.MapActivity;
import com.amap.mapapi.map.MapController;
import com.amap.mapapi.map.MapView;
import com.amap.mapapi.map.MyLocationOverlay;
import com.amap.mapapi.poisearch.PoiPagedResult;
import com.amap.mapapi.poisearch.PoiSearch;
import com.amap.mapapi.poisearch.PoiSearch.SearchBound;
import com.amap.mapapi.poisearch.PoiTypeDef;
import com.hobom.mobile.FoodApplication;
import com.hobom.mobile.R;
import com.hobom.mobile.db.DatabaseAccessor;
import com.hobom.mobile.db.DatabaseAccessor.Tables;
import com.hobom.mobile.db.DatabaseColumns.FoodColumn;
import com.hobom.mobile.model.Food;
import com.hobom.mobile.model.POISearchResult;
import com.hobom.mobile.util.ActivityStackManager;
import com.hobom.mobile.util.Connector;
import com.hobom.mobile.util.Constant;
import com.hobom.mobile.util.MyLog;
import com.hobom.mobile.util.PreferenceUtil;

/**
 * 地图类,可以显示美食位置,我的位置等
 * 
 * @author zhangmingxun
 * 
 */
public class FoodMapActivity extends MapActivity {

	private GeoPoint point;
	private MapView mapView;
	private MapController mMapController;
	private String[] layers = { "收藏的位置" };
	private boolean[] flags = { false, true };
	private Button backRectSearchBtn;
	private boolean poiFromFav = true;// type=1是否从收藏夹进来的
	private boolean layerChecked = false;
	private int poi_showCur = 0;
	private String currentKeyword;// 当前的搜索关键字
	private String queryKeyword;// 最终POI列表中标题中显示的关键字(有可能当前搜索没有结果,此时应该不能用当前的关键字)
	private PoiPagedResult result;
	private ProgressDialog progDialog = null;
	private int latitude, longitude;
	private int curpage = 1;
	private int cnt = 0;
	private static final int REQUEST_POI_SEARCH = 300;
	// private PoiOverlay poiOverlay;
	private CustomPoiOverlay poiOverlay;
	private POISearchResult poiSearchResult;
	private boolean noAwaked1;
	private MyLocationOverlay mLocationOverlay;
	// 几个检测框
	private final int STEP1 = 0;
	private final int STEP2 = 1;
	private final int STEP3 = 2;
	private int type = 1;
	private final int REQUEST_SETTING_NETWORK = 100;
	private final int REQUEST_SETTING_ACCURACY = 200;
	private RadioGroup group;
    private RadioButton local,net;
   private FoodPoiOverlay foodPoiOverlay ;
	private Handler checkHandler = new Handler() {
		public void handleMessage(Message message) {
			int what = message.what;
			System.out.println("current step is:" + what);
			if (what == STEP1) {
				boolean result = checkNetwork();
				if (!result) {
					showNetworkDialog();
				} else {
					this.sendEmptyMessage(STEP2);
				}
			}
			if (what == STEP2) {
				if (!noAwaked1)
					showAccuracy();
				else {
					this.sendEmptyMessage(STEP3);
				}
			} else if (what == STEP3) {
				startLocation();
			}

		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		System.out.println("onCreate");
		super.onCreate(arg0);
		setContentView(R.layout.map);
		init();
          
		ActivityStackManager.getInstance().addActivity(this);

		noAwaked1 = PreferenceUtil.getSettingBoolean(
				Constant.INCREASE_ACCURACY_AWAKE, false);

		mapView.setBuiltInZoomControls(true); // 设置启用内置的缩放控件
		mMapController = mapView.getController();

		mLocationOverlay = new MyLocationOverlay(this, mapView);

		mapView.getOverlays().add(mLocationOverlay);
		mLocationOverlay.runOnFirstFix(new Runnable() {
			public void run() {
				handler.sendMessage(Message.obtain(handler,
						Constant.FIRST_LOCATION));
			}
		});

		Location loc = mLocationOverlay.getLastFix();
		if (loc==null||(loc.getLatitude() == 0 || loc.getLongitude() == 0) ){
			int lastLat = PreferenceUtil.getSettingInt(Constant.LASTLAT,
					(int) (39.98237 * 1E6));
			int lastLon = PreferenceUtil.getSettingInt(Constant.LASTLON,
					(int) (116.304923 * 1E6));
			latitude = lastLat;
			longitude = lastLon;
		} else {
			latitude = (int) (loc.getLatitude() * 1E6);
			longitude = (int) (loc.getLongitude() * 1E6);
		}
		System.out.println("the latitude and longitude is:" + latitude + ":"
				+ longitude);
		point = new GeoPoint(latitude, longitude); // 用给定的经纬度构造一个GeoPoint，单位是微度
													// (度 * 1E6)
		mMapController.setCenter(point); // 设置地图中心点
		mMapController.setZoom(12); // 设置地图zoom级别

		if (type == 1)
			checkHandler.sendEmptyMessage(STEP1);
		progDialog = new ProgressDialog(this);
		loadFoodInfo();

	}

	private void init() {
		mapView = (MapView) findViewById(R.id.main_mapView);

		group = (RadioGroup)findViewById(R.id.groups);
		local = (RadioButton)findViewById(R.id.local);
		net = (RadioButton)findViewById(R.id.net);

		// 路线
		Button routeBtn = (Button) findViewById(R.id.map_route);
		routeBtn.setOnClickListener(buttonListener);

		// 图层
		Button layerBtn = (Button) findViewById(R.id.map_layer);
		layerBtn.setOnClickListener(buttonListener);

		// 回原点
		Button originBtn = (Button) findViewById(R.id.map_origin);
		originBtn.setOnClickListener(buttonListener);

		final EditText editText = (EditText) findViewById(R.id.map_search_place);

		editText.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				System.out.println("start search");
				startSearch(null, false, null, false);
				// FoodUtil.hideSearchIconAndLabel(FoodMapActivity.this);

			}
		});

		// 左下角返回一框式搜索结果的按钮
		backRectSearchBtn = (Button) findViewById(R.id.back_rect_search);
		backRectSearchBtn.setOnClickListener(buttonListener);

	}

	private void loadFoodInfo(){
		
		FoodApplication app = (FoodApplication)getApplication();
		DatabaseAccessor accessor = app.getDatabase();
		Cursor foodCursor = accessor.query(true, Tables.FOOD, null, null, null,
				null, null, null, null);

		PoiItem item = null;
		String name,address;
		GeoPoint gp = null;
        List<PoiItem>poiItems = new ArrayList<PoiItem>();
		while (foodCursor.moveToNext()) {
			
			    
				address = foodCursor.getString(foodCursor
						.getColumnIndex(FoodColumn.ADDRESS));
				int lat = foodCursor.getInt(foodCursor
						.getColumnIndex(FoodColumn.LAT));
				int lon = foodCursor.getInt(foodCursor
						.getColumnIndex(FoodColumn.LON));
				name = foodCursor.getString(foodCursor
						.getColumnIndex(FoodColumn.NAME));
			    gp = new GeoPoint(lat,lon);
			    item = new PoiItem("",gp,name,address);
			    poiItems.add(item);
			

			}
		
		if(foodCursor!=null){
			foodCursor.close();
			foodCursor = null;
		}
		Drawable drawable = getResources().getDrawable(
				R.drawable.da_marker_red);
		if(poiItems.size()>0){
			foodPoiOverlay = new FoodPoiOverlay(this, drawable, poiItems);
			foodPoiOverlay.addToMap(mapView); // 将poiOverlay标注在地图上
			foodPoiOverlay.showPopupWindow(0);
		}
		
	}
	/**
	 * 打开网络状态对话框
	 */
	private void showNetworkDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.alert_dialog_icon)
				.setTitle(R.string.nonetwork_title)
				.setMessage(R.string.nonetwork_msg)
				.setPositiveButton(R.string.setnetwork,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intentSet = new Intent(
										Settings.ACTION_WIRELESS_SETTINGS);

								FoodMapActivity.this.startActivityForResult(
										intentSet, REQUEST_SETTING_NETWORK);
							}

						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								Message message = Message.obtain();
								message.what = STEP3;
								checkHandler.sendMessage(message);
							}

						});

		AlertDialog alert = builder.create();
		alert.show();

	}

	private View.OnClickListener buttonListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			Intent intent = new Intent();

			switch (v.getId()) {

			// 路线
			case R.id.map_route:
				// mapView.removeAll();

				PreferenceUtil.setSettingInt(Constant.LASTLAT,
						(int) (latitude * 3600 * 1000));
				PreferenceUtil.setSettingInt(Constant.LASTLON,
						(int) (longitude * 3600 * 1000));

				intent.setClass(FoodMapActivity.this, MapRouteActivity.class);
				MyLog.InfoLog(
						FoodMapActivity.class,
						"the zoomlevel passed to route is:"
								+ mapView.getZoomLevel());
				intent.putExtra("zoom", mapView.getZoomLevel());
				startActivityForResult(intent, 0);

				break;

			// 图层按钮
			case R.id.map_layer:
				AlertDialog.Builder builder = new AlertDialog.Builder(
						FoodMapActivity.this);
				builder.setTitle("选择图层");
				builder.setMultiChoiceItems(layers,
						new boolean[] { false, true },
						new DialogInterface.OnMultiChoiceClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which, boolean isChecked) {
								layerChecked = isChecked;
								if (layerChecked) {
									dialog.dismiss();
									showFavOverlay();
								}

							}
						});
				builder.setPositiveButton("清空结果",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// mHeaderLayout.setVisibility(View.GONE);
								backRectSearchBtn.setVisibility(View.GONE);

								// System.out.println("the visibility of naviplan is:"+naviPlanList.getVisibility());

								mapView.getOverlays().clear();
							}
						});

				AlertDialog alert = builder.create();
				alert.show();

				break;

			// 回原点
			case R.id.map_origin:

				// 重新定位
				startLocation();

				break;

			case R.id.back_rect_search:

				// mark
				Intent rectSearchIntent = new Intent();
				rectSearchIntent.setClass(FoodMapActivity.this,
						RectQueryResultActivity.class);

				startActivityForResult(rectSearchIntent, 0);

				break;

			}

		}

	};

	/**
	 * 打开提高精度对话框
	 */
	private void showAccuracy() {
		final String source = checkLocationSource();
		if (source.length() == 0) {
			checkHandler.sendEmptyMessage(STEP3);
			return;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.alert_dialog_icon)
				.setTitle(R.string.increase_accuracy_title)
				.setMessage(
						String.format(FoodMapActivity.this.getResources()
								.getString(R.string.increase_accuracy_body),
								source))
				.setPositiveButton(R.string.set,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								boolean containGPS = source.contains("GPS");
								boolean containWifi = source.contains("Wi-Fi");

								Intent intentSet = new Intent(
										Settings.ACTION_SETTINGS);
								if (containWifi && !containGPS) {
									intentSet = new Intent(
											Settings.ACTION_WIFI_SETTINGS);
								}
								if (containGPS && !containWifi) {
									intentSet = new Intent(
											Settings.ACTION_SECURITY_SETTINGS);
								}
								FoodMapActivity.this.startActivityForResult(
										intentSet, REQUEST_SETTING_ACCURACY);
							}

						})
				.setNegativeButton(R.string.skip,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								checkHandler.sendEmptyMessage(STEP3);

							}

						});
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.declare,
				(ViewGroup) findViewById(R.id.declare_layout_root));
		CheckBox noAwakeCheckBox = (CheckBox) layout
				.findViewById(R.id.no_awake_checkbox);
		noAwakeCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						PreferenceUtil.setSettingBoolean(
								Constant.INCREASE_ACCURACY_AWAKE, isChecked);

					}

				});
		builder.setView(layout);

		AlertDialog alert = builder.create();
		alert.show();
	}

	private void showFavOverlay() {

	}

	private void startLocation() {
		 Toast.makeText(this, "正在获取您的位置", Toast.LENGTH_SHORT).show();

	}

	@Override
	protected void onPause() {
		this.mLocationOverlay.disableMyLocation();
		this.mLocationOverlay.disableCompass();
		super.onPause();
	}

	@Override
	protected void onResume() {
		this.mLocationOverlay.enableMyLocation();
		this.mLocationOverlay.enableCompass();
		super.onResume();
	}

	public void doSearchQuery(Intent intent) {
		currentKeyword = intent.getStringExtra(SearchManager.QUERY);

		SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
				SearchSuggestionProvider.AUTHORITY,
				SearchSuggestionProvider.MODE);
		suggestions.saveRecentQuery(currentKeyword, null);
		curpage = 1;
		cnt = 0;
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					PoiSearch poiSearch = new PoiSearch(FoodMapActivity.this,
							new PoiSearch.Query(currentKeyword, PoiTypeDef.All,
									"010")); // 设置搜索字符串，"010为城市区号"
					poiSearch.setBound(new SearchBound(mapView));// 在当前地图显示范围内查找
					poiSearch.setPageSize(10);// 设置搜索每次最多返回结果数
					result = poiSearch.searchPOI();

					if (result != null) {
						cnt = result.getPageCount();
						poiSearchResult = FoodApplication.getApp()
								.getCurrentPOISearchResult();
						if (poiSearchResult == null) {
							poiSearchResult = new POISearchResult();
							FoodApplication.getApp().setCurrentPOISearchResult(
									poiSearchResult);
							poiSearchResult = FoodApplication.getApp()
									.getCurrentPOISearchResult();
						}
						poiSearchResult.setKeyword(currentKeyword);
						poiSearchResult.setCurrentPage(curpage);
						poiSearchResult.setTotalCount(cnt);
						poiSearchResult.setPoiPagedResult(result);

					}

					handler.sendMessage(Message.obtain(handler,
							Constant.POISEARCH));
				} catch (AMapException e) {
					handler.sendMessage(Message.obtain(handler, Constant.ERROR));
					e.printStackTrace();
				}
			}
		});

		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(true);
		progDialog.setMessage("正在搜索:\n" + currentKeyword);
		progDialog.show();
		t.start();
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == Constant.POISEARCH) {
				progDialog.dismiss();
				if (result == null) {
					Toast.makeText(getApplicationContext(), "无相关结果！",
							Toast.LENGTH_SHORT).show();
				} else {
					Intent mIntent = new Intent();
					mIntent.setClass(FoodMapActivity.this,
							RectQueryResultActivity.class);
					startActivityForResult(mIntent, REQUEST_POI_SEARCH);
				}
			} else if (msg.what == Constant.ERROR) {
				progDialog.dismiss();
				Toast.makeText(getApplicationContext(), "搜索失败,请检查网络连接！",
						Toast.LENGTH_SHORT).show();
			} else if (msg.what == Constant.FIRST_LOCATION) {
				mMapController.animateTo(mLocationOverlay.getMyLocation());

				point = mLocationOverlay.getMyLocation();
				latitude = point.getLatitudeE6();
				longitude = point.getLongitudeE6();
				System.out
						.println("the latitude and longitude in fisrt location is:"
								+ latitude + ":" + longitude);

				PreferenceUtil.setSettingInt(Constant.LASTLAT, latitude);
				PreferenceUtil.setSettingInt(Constant.LASTLON, longitude);

			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		System.out.println("onActivityResult");
		// super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_SETTING_NETWORK) { // 网络设置

			checkHandler.sendEmptyMessage(STEP2);
		} else if (requestCode == REQUEST_SETTING_ACCURACY) {// 提高精度设置

			checkHandler.sendEmptyMessage(STEP3);
		} else if (requestCode == REQUEST_POI_SEARCH && resultCode == 1) {
			System.out.println("from request poi serach");
			type = 2;
			backRectSearchBtn.setVisibility(View.VISIBLE);
			int pos = data.getIntExtra("position", 0);
			int currentPage = poiSearchResult.getCurrentPage();
			List<PoiItem> poiItems;
			try {
				poiItems = poiSearchResult.getPoiPagedResult().getPage(
						currentPage);
				mMapController.setZoom(13);
				mMapController.animateTo(poiItems.get(0).getPoint());
				if (poiOverlay != null) {
					poiOverlay.removeFromMap();
					// mapView.getOverlays().remove(poiOverlay);
				}
				Drawable drawable = getResources().getDrawable(
						R.drawable.marker_a);
				for (int i = 0; i < poiItems.size(); i++) {
					getDrawable(i);
				}
				/*
				 * for(int i=0;i<poiItems.size();i++){
				 * poiItems.get(i).setMarker(getDrawable(i));
				 * //poiOverlay.addOverlay(poiItems.get(i)); }
				 */
				// poiOverlay = new MyPoiOverlay(PoiSearchDemo.this,
				// drawable, poiItems); // 将结果的第一页添加到PoiOverlay
				poiOverlay = new CustomPoiOverlay(FoodMapActivity.this,
						drawable, poiItems);

				poiOverlay.addToMap(mapView); // 将poiOverlay标注在地图上
				poiOverlay.showPopupWindow(pos);

			} catch (AMapException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private Drawable getDrawable(int i) {
		String name = "marker_" + (char) ('a' + i);
		System.out.println("the name is:" + name);
		Field field;
		try {
			field = Drawable.class.getDeclaredField(String.valueOf(name));
			int resourceId = field.getInt(Drawable.class);

			return getResources().getDrawable(resourceId);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getResources().getDrawable(R.drawable.marker_a);

	}

	private boolean checkNetwork() {

		if (Connector.isWifi(this)) {

			return true;
		}
		if (Connector.isConnected(this)) {
			return true;
		}
		return false;
	}

	// 检测位置源
	private String checkLocationSource() {
		StringBuffer sb = new StringBuffer();

		if (!Connector.isGPSEnabled(this)) {

			sb.append("1.在位置设置中打开GPS和无线网络");
		}

		if (sb.length() != 0)
			sb.append("\n");
		if (!Connector.wifiOpened(this)) {
			if (sb.length() != 0)
				sb.append("2.打开Wi-Fi");
			else
				sb.append("打开Wi-Fi");
		}

		return sb.toString();
	}

}
