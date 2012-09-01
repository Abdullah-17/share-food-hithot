package com.hobom.mobile.ui;

import java.util.ArrayList;
import java.util.List;

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
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.mapapi.core.AMapException;
import com.amap.mapapi.core.GeoPoint;
import com.amap.mapapi.core.PoiItem;
import com.amap.mapapi.location.LocationManagerProxy;
import com.amap.mapapi.map.MapActivity;
import com.amap.mapapi.map.MapController;
import com.amap.mapapi.map.MapView;
import com.amap.mapapi.map.RouteMessageHandler;
import com.amap.mapapi.map.RouteOverlay;
import com.amap.mapapi.poisearch.PoiPagedResult;
import com.amap.mapapi.poisearch.PoiSearch;
import com.amap.mapapi.poisearch.PoiSearch.Query;
import com.amap.mapapi.poisearch.PoiTypeDef;
import com.amap.mapapi.route.Route;
import com.hobom.mobile.R;
import com.hobom.mobile.ui.RouteSearchPoiDialog.OnListItemClick;
import com.hobom.mobile.util.ActivityStackManager;
import com.hobom.mobile.util.Constant;
import com.hobom.mobile.util.PreferenceUtil;

public class MapRouteActivity extends MapActivity implements LocationListener, RouteMessageHandler {
	
	private final static String TAG ="MapRouteActivity";
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

	private Button driveRouteStartBtn;
	private Button driveRouteDestBtn;
	private EditText driveRouteStartEdit;
	private EditText driveRouteDestEdit;
	private Button driveRouteSwapBtn;
	private Button driveRouteCalculateBtn;

	private String startKeyword;
	private String endKeyword;
	private int startEndType;
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
	private int mode = Route.DrivingDefault;
	private GeoPoint currentPos;
	private PoiPagedResult startSearchResult;
	private PoiPagedResult endSearchResult;
	private GeoPoint startPoint=null;
	private GeoPoint endPoint=null;
    private MapView mMapView;
    private MapController mMapController;
   private TextView driveView,busView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tab_bg_map_route);
		ActivityStackManager.getInstance().addActivity(this);
		locationManager = LocationManagerProxy.getInstance(this);
		

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

		driveView = (TextView) findViewById(R.id.tab_drive_route);
		busView = (TextView) findViewById(R.id.tab_bus_change);
		mMapView = (MapView)findViewById(R.id.route_MapView);
		busView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mode = Route.BusDefault;
				driveView.setBackgroundResource(R.drawable.tab_two);
				busView.setBackgroundResource(R.drawable.tab_two_press);
				busView.setTextColor(R.color.blue);
				driveView.setTextColor(R.drawable.tab_textview_color);
			}
			
		});
		driveView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mode = Route.DrivingDefault;
				busView.setBackgroundResource(R.drawable.tab_two);
				driveView.setBackgroundResource(R.drawable.tab_two_press);
				driveView.setTextColor(R.color.blue);
				busView.setTextColor(R.drawable.tab_textview_color);
			}
			
		});
		mMapController = mMapView.getController();  // �õ�mMapView�Ŀ���Ȩ,�����������ƺ�����ƽ�ƺ�����
		int lastLat = PreferenceUtil.getSettingInt(Constant.LASTLAT,
				(int) (39.906033 * 1E6));
		int lastLon = PreferenceUtil.getSettingInt(Constant.LASTLON,
				(int) (116.397695 * 1E6));
		GeoPoint point = new GeoPoint(lastLat, lastLon); // �ø����ľ�γ�ȹ���һ��GeoPoint����λ��΢��
		mMapController.setCenter(point);  //���õ�ͼ���ĵ�
		mMapController.setZoom(12);    //���õ�ͼzoom����
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
							driveRouteStartEdit.setText("��ͼ�ϵĵ�");
							changeByHand = true;
							MapRouteActivity.startLon = g.getLongitudeE6();
							MapRouteActivity.startLat = g.getLatitudeE6();
							MapRouteActivity.startName = "��ͼ�ϵĵ�";
							startPoint = g;
						}

					} else if (startEndType == MapPointChooseActivity.MODE_CHOOSE_END) {
						if (driveRouteDestEdit != null) {
							changeByHand = false;
							driveRouteDestEdit.setText("��ͼ�ϵĵ�");
							changeByHand = true;
							MapRouteActivity.endLon = g.getLongitudeE6();
							MapRouteActivity.endLat = g.getLatitudeE6();
							MapRouteActivity.endName = "��ͼ�ϵĵ�";
							endPoint = g;
						}

					
				}

			}

		}

	}

	public void searchRouteResult(GeoPoint startPoint, GeoPoint endPoint) {
		progDialog = ProgressDialog.show(MapRouteActivity.this, null,
				"���ڻ�ȡ��·", true, true);
		final Route.FromAndTo fromAndTo = new Route.FromAndTo(startPoint,
				endPoint);
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {

				try {
				    Looper.prepare();
					routeResult = Route.calculateRoute(MapRouteActivity.this,
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
				Looper.loop();
				
			}
		});
		t.start();

	}
	
	
	
	
	private View.OnClickListener driveRouteListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			switch (v.getId()) {

			// ��ʼλ��ѡ��ť
			case R.id.driveroute_start_place_btn:

				AlertDialog.Builder builder1 = new AlertDialog.Builder(
						MapRouteActivity.this);
				builder1.setTitle("�������");

				BaseAdapter adapter1 = new MapRouteAdapter(
						MapRouteActivity.this);
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
									intent.setClass(MapRouteActivity.this,
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

			// ����λ��ѡ��ť
			case R.id.driveroute_dest_place_btn:

				AlertDialog.Builder builder2 = new AlertDialog.Builder(
						MapRouteActivity.this);
				builder2.setTitle("�����յ�");

				BaseAdapter adapter2 = new MapRouteAdapter(
						MapRouteActivity.this);
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
									intent.setClass(MapRouteActivity.this,
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

			// ������ť
			case R.id.driveroute_swap_btn:

				// �����յ㾭γ��ֵ����
				int tmp;
				tmp = MapRouteActivity.startLon;
				MapRouteActivity.startLon = MapRouteActivity.endLon;
				MapRouteActivity.endLon = tmp;

				tmp = MapRouteActivity.startLat;
				MapRouteActivity.startLat = MapRouteActivity.endLat;
				MapRouteActivity.endLat = tmp;

				// �����յ����ƽ���
				String str = driveRouteStartEdit.getText().toString();
				MapRouteActivity.startName = driveRouteDestEdit.getText()
						.toString();
				MapRouteActivity.endName = str;
				changeByHand = false;
				driveRouteStartEdit.setText(MapRouteActivity.startName);
				driveRouteDestEdit.setText(MapRouteActivity.endName);
				changeByHand = true;
				break;

			// ��·��ť
			case R.id.driveroute_calculate_route_btn:

				// �ж���㾭γ���Ƿ�Ϊ��
				if (MapRouteActivity.startLon == 0
						|| MapRouteActivity.startLat == 0) {
					if (TextUtils.isEmpty(driveRouteStartEdit.getText())) {
						Toast.makeText(MapRouteActivity.this, "���Ϊ��!", 0)
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

				// �ж��յ㾭γ���Ƿ�Ϊ��
				if (MapRouteActivity.endLon == 0
						|| MapRouteActivity.endLat == 0) {
					if (TextUtils.isEmpty(driveRouteDestEdit.getText())) {
						Toast.makeText(MapRouteActivity.this, "�յ�Ϊ��!", 0)
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
				Toast.makeText(MapRouteActivity.this, "�޷���ȡ����λ��",
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
								MapRouteActivity.this, poiItems);

						dialog.setTitle("��Ҫ�ҵ������:");
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
						showToast("�����������,���������趨...");
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
								MapRouteActivity.this, poiItems);
						dialog.setTitle("��Ҫ�ҵ��յ���:");
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
						showToast("�����������,���������趨...");
					}
				} catch (AMapException e) {
					e.printStackTrace();
				}

			} else if (msg.what == Constant.ROUTE_SEARCH_RESULT) {
				progDialog.dismiss();
				if (routeResult != null && routeResult.size()>0) {
					setContentView(R.layout.simplemap);
					mMapView = (MapView)findViewById(R.id.mapView);
					Route route = routeResult.get(0);
					
					if (route != null) {
						if (ol != null) {
							ol.removeFromMap(mMapView);
						}
						ol = new RouteOverlay(MapRouteActivity.this, route);
						ol.registerRouteMessage(MapRouteActivity.this); // ע����Ϣ������
						ol.addToMap(mMapView); // ���뵽��ͼ
						ArrayList<GeoPoint> pts = new ArrayList<GeoPoint>();
						pts.add(route.getLowerLeftPoint());
						pts.add(route.getUpperRightPoint());
						mMapView.getController().setFitView(pts);//������ͼ��ʾ��Χ
						mMapView.invalidate();
					}
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
	
	
	// ��ѯ·���滮���
	public void startSearchResult() {
		startKeyword = driveRouteStartEdit.getText().toString().trim();
		if(startPoint!=null&&startKeyword.equals("��ͼ�ϵĵ�")){
			endSearchResult();
		}else{
			final Query startQuery = new Query(startKeyword, PoiTypeDef.All, "010");
			progDialog = ProgressDialog.show(MapRouteActivity.this, null,
					"����������������Ϣ...", true, true);
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					Looper.prepare();
					// ��������POI����
					PoiSearch poiSearch = new PoiSearch(MapRouteActivity.this, startQuery); // ���������ַ���
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
					Looper.loop();
				}

			});
			t.start();
		}
	}
	
	// ��ѯ·���滮�յ�
	public void endSearchResult() {
		
		endKeyword = driveRouteDestEdit.getText().toString().trim();
		if(endPoint!=null&&endKeyword.equals("��ͼ�ϵĵ�")){
			searchRouteResult(startPoint,endPoint);
		}else{
			final Query endQuery = new Query(endKeyword, PoiTypeDef.All, "010");
	        progDialog = ProgressDialog.show(MapRouteActivity.this, null,
					"����������������Ϣ...", true, false);
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					Looper.prepare();
					PoiSearch poiSearch = new PoiSearch(MapRouteActivity.this,endQuery); // ���������ַ���
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
					Looper.loop();
				}

			});
			t.start();
		}
	}
	
	

}
