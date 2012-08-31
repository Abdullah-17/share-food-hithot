package com.hobom.mobile.ui;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amap.mapapi.core.AMapException;
import com.amap.mapapi.core.GeoPoint;
import com.amap.mapapi.core.PoiItem;
import com.amap.mapapi.location.LocationManagerProxy;
import com.amap.mapapi.map.MapView;
import com.amap.mapapi.map.RouteMessageHandler;
import com.amap.mapapi.map.RouteOverlay;
import com.amap.mapapi.poisearch.PoiPagedResult;
import com.amap.mapapi.poisearch.PoiSearch;
import com.amap.mapapi.poisearch.PoiTypeDef;
import com.amap.mapapi.poisearch.PoiSearch.Query;
import com.amap.mapapi.route.Route;
import com.hobom.mobile.R;
import com.hobom.mobile.ui.RouteSearchPoiDialog.OnListItemClick;
import com.hobom.mobile.util.Constant;

/**
 * 自驾路线
 * @author mingxunzh
 *
 */
public class DriveRouteActivity extends Activity  implements LocationListener, RouteMessageHandler {

	private static final String TAG = "DriveRouteActivity";

	private Button driveRouteStartBtn;
	private Button driveRouteDestBtn;
	private EditText driveRouteStartEdit;
	private EditText driveRouteDestEdit;
	private Button driveRouteSwapBtn;
	private Button driveRouteCalculateBtn;

	private String startKeyword;
	private String endKeyword;
	private int fromType;
	private int startEndType;
	private int mode = Route.BusDefault;
	private ProgressDialog progDialog;
	private Intent intent;
	private final int REQUEST_CHOOSEPOINT = 0;
	private final int REQUEST_FAV = 1;
	private LocationManagerProxy locationManager = null;
	private boolean changeByHand = true;
	private MapPointOverlay overlay;
	private String poiType;
	private List<Route> routeResult;
	private RouteOverlay ol;
	
	private GeoPoint currentPos;
	private PoiPagedResult startSearchResult;
	private PoiPagedResult endSearchResult;
	private GeoPoint startPoint=null;
	private GeoPoint endPoint=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		locationManager = LocationManagerProxy.getInstance(this);
	
		setContentView(R.layout.map_drive_route);
		

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		Log.i(TAG, "onResume");
		super.onResume();
		init();
		enableMyLocation();
	}

	@Override
	protected void onPause() {
		disableMyLocation();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		if (locationManager != null) {
			locationManager.removeUpdates(this);
			locationManager.destory();
		}
		locationManager = null;
		super.onDestroy();
	}
	
	
	public void init() {

		driveRouteStartEdit = (EditText) findViewById(R.id.driveroute_start_place_edit);
		driveRouteDestEdit = (EditText) findViewById(R.id.driveroute_dest_place_edit);
		if (!TextUtils.isEmpty(MapRouteActivity.endName)) {
			changeByHand = false;
			driveRouteDestEdit.setText(MapRouteActivity.endName);
			endKeyword = MapRouteActivity.endName;
			changeByHand = true;
		}
		if (!TextUtils.isEmpty(MapRouteActivity.startName)) {
			changeByHand = false;
			driveRouteStartEdit.setText(MapRouteActivity.startName);
			startKeyword = MapRouteActivity.startName;
			changeByHand = true;
		}

		driveRouteStartBtn = (Button) findViewById(R.id.driveroute_start_place_btn);
		driveRouteStartBtn.setOnClickListener(driveRouteListener);

		driveRouteDestBtn = (Button) findViewById(R.id.driveroute_dest_place_btn);
		driveRouteDestBtn.setOnClickListener(driveRouteListener);

		driveRouteSwapBtn = (Button) findViewById(R.id.driveroute_swap_btn);
		driveRouteSwapBtn.setOnClickListener(driveRouteListener);

		driveRouteCalculateBtn = (Button) findViewById(R.id.driveroute_calculate_route_btn);
		driveRouteCalculateBtn.setOnClickListener(driveRouteListener);
		driveRouteStartEdit.addTextChangedListener(new TextChanger(0));
		driveRouteDestEdit.addTextChangedListener(new TextChanger(1));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (data != null) {
			if (requestCode == REQUEST_CHOOSEPOINT) {
				startEndType = data.getIntExtra("mapMode", 0);
				

			    GeoPoint g = (GeoPoint) data.getParcelableExtra("LonLat");
				
					if (startEndType == MapPointChooseActivity.MODE_CHOOSE_START) {
						if (driveRouteStartEdit != null) {
							changeByHand = false;
							driveRouteStartEdit.setText("地图上的点");
							changeByHand = true;
							MapRouteActivity.startLon = g.getLongitudeE6();
							MapRouteActivity.startLat = g.getLatitudeE6();
							MapRouteActivity.startName = "地图上的点";
							startPoint = g;
						}

					} else if (startEndType == MapPointChooseActivity.MODE_CHOOSE_END) {
						if (driveRouteDestEdit != null) {
							changeByHand = false;
							driveRouteDestEdit.setText("地图上的点");
							changeByHand = true;
							MapRouteActivity.endLon = g.getLongitudeE6();
							MapRouteActivity.endLat = g.getLatitudeE6();
							MapRouteActivity.endName = "地图上的点";
							endPoint = g;
						}

					
				}

			}

		}

	}

	public void searchRouteResult(GeoPoint startPoint, GeoPoint endPoint) {
		progDialog = ProgressDialog.show(DriveRouteActivity.this, null,
				"正在获取线路", true, true);
		final Route.FromAndTo fromAndTo = new Route.FromAndTo(startPoint,
				endPoint);
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {

				try {
					routeResult = Route.calculateRoute(DriveRouteActivity.this,
							fromAndTo, mode);
					if(progDialog.isShowing()){
						if(routeResult!=null||routeResult.size()>0)
						routeHandler.sendMessage(Message
								.obtain(routeHandler, Constant.ROUTE_SEARCH_RESULT));
					}
				} catch (AMapException e) {
					Message msg = new Message();
					msg.what = Constant.ROUTE_SEARCH_ERROR;
					msg.obj =  e.getErrorMessage();
					routeHandler.sendMessage(msg);
				}
			}
		});
		t.start();

	}
	
	
	
	
	private View.OnClickListener driveRouteListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			switch (v.getId()) {

			// 开始位置选择按钮
			case R.id.driveroute_start_place_btn:

				AlertDialog.Builder builder1 = new AlertDialog.Builder(
						DriveRouteActivity.this);
				builder1.setTitle("设置起点");

				BaseAdapter adapter1 = new MapRouteAdapter(
						DriveRouteActivity.this);
				builder1.setAdapter(adapter1,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								switch (which) {
								case 0:
									changeByHand = false;
									driveRouteStartEdit.setText(Constant.MYLOC);
									changeByHand = true;
									MapRouteActivity.startName = Constant.MYLOC;

									break;

								case 1:
									Intent intent = new Intent();
									intent.setClass(DriveRouteActivity.this,
											MapPointChooseActivity.class);
									
									intent.putExtra("mapMode",
											MapPointChooseActivity.MODE_CHOOSE_START);
									startActivityForResult(intent,
											REQUEST_CHOOSEPOINT);

									break;
								
								}
							}
						});
				AlertDialog dialog1 = builder1.create();
				dialog1.show();

				break;

			// 结束位置选择按钮
			case R.id.driveroute_dest_place_btn:

				AlertDialog.Builder builder2 = new AlertDialog.Builder(
						DriveRouteActivity.this);
				builder2.setTitle("设置终点");

				BaseAdapter adapter2 = new MapRouteAdapter(
						DriveRouteActivity.this);
				builder2.setAdapter(adapter2,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								switch (which) {
								case 0:
									changeByHand = false;
									driveRouteDestEdit.setText(Constant.MYLOC);
									changeByHand = true;
									MapRouteActivity.endName = Constant.MYLOC;

									break;

								case 1:
									Intent intent = new Intent();
									intent.setClass(DriveRouteActivity.this,
											MapPointChooseActivity.class);
									
									intent.putExtra("mapMode",
											MapPointChooseActivity.MODE_CHOOSE_END);
									startActivityForResult(intent,
											REQUEST_CHOOSEPOINT);

									break;
								
								}
							}
						});
				AlertDialog dialog2 = builder2.create();
				dialog2.show();

				break;

			// 交换按钮
			case R.id.driveroute_swap_btn:

				// 起点和终点经纬度值交换
				int tmp;
				tmp = MapRouteActivity.startLon;
				MapRouteActivity.startLon = MapRouteActivity.endLon;
				MapRouteActivity.endLon = tmp;

				tmp = MapRouteActivity.startLat;
				MapRouteActivity.startLat = MapRouteActivity.endLat;
				MapRouteActivity.endLat = tmp;

				// 起点和终点名称交换
				String str = driveRouteStartEdit.getText().toString();
				MapRouteActivity.startName = driveRouteDestEdit.getText()
						.toString();
				MapRouteActivity.endName = str;
				changeByHand = false;
				driveRouteStartEdit.setText(MapRouteActivity.startName);
				driveRouteDestEdit.setText(MapRouteActivity.endName);
				changeByHand = true;
				break;

			// 算路按钮
			case R.id.driveroute_calculate_route_btn:

				// 判断起点经纬度是否为空
				if (MapRouteActivity.startLon == 0
						|| MapRouteActivity.startLat == 0) {
					if (TextUtils.isEmpty(driveRouteStartEdit.getText())) {
						Toast.makeText(DriveRouteActivity.this, "起点为空!", 0)
								.show();
						break;

					}

					else {
						startKeyword = driveRouteStartEdit.getText().toString();
						if (startKeyword.equals(Constant.MYLOC) && !checkGps(0)) {
							return;
						}
					}

				}

				// 判断终点经纬度是否为空
				if (MapRouteActivity.endLon == 0
						|| MapRouteActivity.endLat == 0) {
					if (TextUtils.isEmpty(driveRouteDestEdit.getText())) {
						Toast.makeText(DriveRouteActivity.this, "终点为空!", 0)
								.show();
						break;
					} else {

						endKeyword = driveRouteDestEdit.getText().toString();
						if (endKeyword.equals(Constant.MYLOC) && !checkGps(1)) {
							return;
						}

					}

				}
				hideKeyboard();
				if (startKeyword != null
						&& (MapRouteActivity.startLon == 0 || MapRouteActivity.startLat == 0)) {
					startSearchResult();
				} else if (endKeyword != null
						&& (MapRouteActivity.endLon == 0 || MapRouteActivity.endLat == 0)) {
					endSearchResult();
				} else {
					searchRouteResult(startPoint,endPoint);
				}
				break;
			}

		}

	};

	public boolean enableMyLocation() {
		boolean result = true;
		Criteria cri = new Criteria();
		cri.setAccuracy(Criteria.ACCURACY_COARSE);
		cri.setAltitudeRequired(false);
		cri.setBearingRequired(false);
		cri.setCostAllowed(false);
		String bestProvider = locationManager.getBestProvider(cri, true);
		locationManager.requestLocationUpdates(bestProvider, 2000, 10, this);
		return result;
	}

	public void disableMyLocation() {
		locationManager.removeUpdates(this);
	}
	
	
	class TextChanger implements TextWatcher {

		private String before = null;
		private int type;

		public TextChanger(int type) {
			this.type = type;

		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			String text = s.toString();
			Log.i(TAG, "after is:" + text);
			if (!text.equals(before) && changeByHand) {
				Log.i(TAG, "not the same");
				if (type == 0) {
					MapRouteActivity.startLon = 0;
					MapRouteActivity.startLat = 0;
					MapRouteActivity.startName = text;
				} else if (type == 1) {
					MapRouteActivity.endLon = 0;
					MapRouteActivity.endLat = 0;
					MapRouteActivity.endName = text;
				}
			}

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

			before = s.toString();
			Log.i(TAG, "before is:" + before);

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub

		}

	}

	private void hideKeyboard() {

		InputMethodManager in = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		if (in.isActive()) {
			in.hideSoftInputFromWindow(driveRouteDestEdit.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	private boolean checkGps(int type) {
		Log.i(TAG, "check gps");
		if (currentPos!=null) {
			if (type == 0) {
				MapRouteActivity.startName = Constant.MYLOC;
				MapRouteActivity.startLon = currentPos.getLatitudeE6();
				MapRouteActivity.startLat = currentPos.getLongitudeE6();
				startPoint = new GeoPoint(MapRouteActivity.startLat,MapRouteActivity.startLon);
				
			} else if (type == 1) {
				MapRouteActivity.endName = Constant.MYLOC;
				MapRouteActivity.endLon = currentPos.getLongitudeE6();
				MapRouteActivity.endLat = currentPos.getLatitudeE6();
				endPoint = new GeoPoint(MapRouteActivity.endLat,MapRouteActivity.endLon);
				
			}
			// driveRouteCalculateBtn.setEnabled(true);
			return true;

		}  else {
				Toast.makeText(DriveRouteActivity.this, "无法获取您的位置",
						Toast.LENGTH_LONG).show();
				return false;
			}

		

	}
	
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		if (location != null) {
			Double geoLat = location.getLatitude();
			Double geoLng = location.getLongitude();
			currentPos = new GeoPoint((int)(geoLat*1E6),(int)(geoLng*1E6));
			
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDrag(MapView arg0, RouteOverlay arg1, int arg2, GeoPoint arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDragBegin(MapView arg0, RouteOverlay arg1, int arg2,
			GeoPoint arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDragEnd(MapView mapView, RouteOverlay overlay, int arg2,
			GeoPoint arg3) {
		// TODO Auto-generated method stub
		try {
			startPoint = overlay.getStartPos();
			endPoint = overlay.getEndPos();
//			overlay.renewOverlay(mapView);
			searchRouteResult(startPoint, endPoint);
		} catch (IllegalArgumentException e) {
			ol.restoreOverlay(mapView);
			overlayToBack(ol, mapView);
		} catch (Exception e1) {
			overlay.restoreOverlay(mapView);
			overlayToBack(ol, mapView);
		}
	}
	private void overlayToBack(RouteOverlay overlay, MapView mapView) {
		startPoint = overlay.getStartPos();
		endPoint = overlay.getEndPos();
	}
	@Override
	public boolean onRouteEvent(MapView arg0, RouteOverlay arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
		return false;
	}
	private Handler routeHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == Constant.ROUTE_START_SEARCH) {
				progDialog.dismiss();
				try {
					List<PoiItem> poiItems;
					if (startSearchResult != null && (poiItems = startSearchResult.getPage(1)) != null 
							&& poiItems.size() > 0) {
						RouteSearchPoiDialog dialog = new RouteSearchPoiDialog(
								DriveRouteActivity.this, poiItems);

						dialog.setTitle("您要找的起点是:");
						dialog.show();
						dialog.setOnListClickListener(new OnListItemClick() {
							@Override
							public void onListItemClick(
									RouteSearchPoiDialog dialog,
									PoiItem startpoiItem) {
								startPoint = startpoiItem.getPoint();
								startKeyword = startpoiItem.getTitle();
								driveRouteStartEdit.setText(startKeyword);
								endSearchResult();
							}

						});
					} else {
						showToast("无搜索起点结果,建议重新设定...");
					}
				} catch (AMapException e) {
					e.printStackTrace();
				}

			} else if (msg.what == Constant.ROUTE_END_SEARCH) {
				progDialog.dismiss();
				try {
					List<PoiItem> poiItems;
					if (endSearchResult != null && (poiItems = endSearchResult.getPage(1)) != null 
							&& poiItems.size() > 0) {
						RouteSearchPoiDialog dialog = new RouteSearchPoiDialog(
								DriveRouteActivity.this, poiItems);
						dialog.setTitle("您要找的终点是:");
						dialog.show();
						dialog.setOnListClickListener(new OnListItemClick() {
							@Override
							public void onListItemClick(
									RouteSearchPoiDialog dialog,
									PoiItem endpoiItem) {
								// TODO Auto-generated method stub
								endPoint = endpoiItem.getPoint();
								endKeyword = endpoiItem.getTitle();
								driveRouteDestEdit.setText(endKeyword);
								searchRouteResult(startPoint, endPoint);
							}

						});
					} else {
						showToast("无搜索起点结果,建议重新设定...");
					}
				} catch (AMapException e) {
					e.printStackTrace();
				}

			} else if (msg.what == Constant.ROUTE_SEARCH_RESULT) {
				progDialog.dismiss();
				if (routeResult != null && routeResult.size()>0) {
					Route route = routeResult.get(0);
					/*if (route != null) {
						if (ol != null) {
							ol.removeFromMap(mMapView);
						}
						ol = new RouteOverlay(BusChangeActivity.this, route);
						ol.registerRouteMessage(BusChangeActivity.this); // 注册消息处理函数
						ol.addToMap(mMapView); // 加入到地图
						ArrayList<GeoPoint> pts = new ArrayList<GeoPoint>();
						pts.add(route.getLowerLeftPoint());
						pts.add(route.getUpperRightPoint());
						mMapView.getController().setFitView(pts);//调整地图显示范围
						mMapView.invalidate();
					}*/
				}
			} else if (msg.what == Constant.ROUTE_SEARCH_ERROR) {
				progDialog.dismiss();
				showToast((String)msg.obj);
			}
		}
	};
	
	public void showToast(String showString) {
		Toast.makeText(getApplicationContext(), showString, Toast.LENGTH_SHORT)
				.show();
	}
	
	
	// 查询路径规划起点
	public void startSearchResult() {
		startKeyword = driveRouteStartEdit.getText().toString().trim();
		if(startPoint!=null&&startKeyword.equals("地图上的点")){
			endSearchResult();
		}else{
			final Query startQuery = new Query(startKeyword, PoiTypeDef.All, "010");
			progDialog = ProgressDialog.show(DriveRouteActivity.this, null,
					"正在搜索您所需信息...", true, true);
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					// 调用搜索POI方法
					PoiSearch poiSearch = new PoiSearch(DriveRouteActivity.this, startQuery); // 设置搜索字符串
					try {
						startSearchResult = poiSearch.searchPOI();
						if(progDialog.isShowing()){
							routeHandler.sendMessage(Message.obtain(routeHandler,
									Constant.ROUTE_START_SEARCH));
					}
					} catch (AMapException e) {
						Message msg = new Message();
						msg.what = Constant.ROUTE_SEARCH_ERROR;
						msg.obj =  e.getErrorMessage();
						routeHandler.sendMessage(msg);
					} 
				}

			});
			t.start();
		}
	}
	
	// 查询路径规划终点
	public void endSearchResult() {
		
		endKeyword = driveRouteDestEdit.getText().toString().trim();
		if(endPoint!=null&&endKeyword.equals("地图上的点")){
			searchRouteResult(startPoint,endPoint);
		}else{
			final Query endQuery = new Query(endKeyword, PoiTypeDef.All, "010");
	        progDialog = ProgressDialog.show(DriveRouteActivity.this, null,
					"正在搜索您所需信息...", true, false);
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					PoiSearch poiSearch = new PoiSearch(DriveRouteActivity.this,endQuery); // 设置搜索字符串
					try {
						endSearchResult = poiSearch.searchPOI();
						if(progDialog.isShowing()){
						 routeHandler.sendMessage(Message.obtain(routeHandler,
								Constant.ROUTE_END_SEARCH));
						}
					} catch (AMapException e) {
						Message msg = new Message();
						msg.what = Constant.ROUTE_SEARCH_ERROR;
						msg.obj =  e.getErrorMessage();
						routeHandler.sendMessage(msg);
					} 
				}

			});
			t.start();
		}
	}
	
	

}
