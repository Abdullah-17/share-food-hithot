package com.hobom.mobile.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Color;
import android.location.Address;

import com.amap.mapapi.core.GeoPoint;
import com.amap.mapapi.geocoder.Geocoder;
import com.amap.mapapi.map.MapActivity;


/**
 * 地图工具类
 * @author mingxunzh
 *
 */
public class MapUtil {

	public static final double PI = 3.1415926535897931;

	public static final int TILE_WIDTH = 256;
	public static final int TILE_HEIGHT = 256;

	private static int WIN_WIDTH = 480;
	private static int WIN_HEIGHT = 800;
	private static final double EARTH_R = 6371004.00; // 地球平均半径, 单位: 米

	
	public static void setScreenSize(int width, int height) {
		WIN_WIDTH = width;
		WIN_HEIGHT = height;
	}

	public static int getScreenWidth() {
		return WIN_WIDTH;
	}

	public static int getScreenHeight() {
		return WIN_HEIGHT;
	}
	
	/**
	 * 根据geopoint查找地址
	 * 
	 * @param context
	 * @param point
	 * @return
	 */
	public static String getAddressByGeoPoint(MapActivity context,
			GeoPoint point) {
		String address = "";
		if (point != null) {
			Geocoder gc = new Geocoder(context);
			try {
				List<Address> addresses = gc.getFromLocation(
						point.getLatitudeE6() / 1E6,
						point.getLongitudeE6() / 1E6, 1);
				StringBuilder sb = new StringBuilder();
				if (addresses.size() > 0) {
					Address add = addresses.get(0);

					for (int i = 0; i < add.getMaxAddressLineIndex(); i++) {
						sb.append(add.getAddressLine(i)).append("\n");
					}

					address = sb.toString();
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return address;
	}

	/**
	 * 根据地址获取geopoint
	 * 
	 * @param context
	 * @param address
	 * @return
	 */
	public static GeoPoint getGeoByAddress(MapActivity context, String address) {
		GeoPoint gp = null;
		try {
			if (address != "") {
				Geocoder mGeoCoder = new Geocoder(context);
				List<Address> add = mGeoCoder.getFromLocationName(address, 1);
				if (!add.isEmpty()) {

					Address ads = add.get(0);
					double lat = ads.getLatitude() * 1E6;
					double lon = ads.getLongitude() * 1E6;
					gp = new GeoPoint((int) lat, (int) lon);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return gp;

	}

	/**
	 * 根据个数获取颜色值
	 * 
	 * @param num
	 *            个数
	 * @return
	 */
	public static int[] getColorsByNum(int num) {
		int[] colors = new int[num];
		for (int i = 0; i < num; i++) {
			colors[i] = ANDROIDCOLOR[i];
		}

		return colors;
	}

	private static final Map<String, Integer> categorycolormap = new HashMap<String, Integer>();
	static {
		categorycolormap.put("吃", Color.BLUE);
		categorycolormap.put("穿", Color.GREEN);
		categorycolormap.put("住", Color.MAGENTA);
		categorycolormap.put("用", Color.YELLOW);
		categorycolormap.put("行", Color.CYAN);
	}

	public static int getCategoryColor(String category) {
		return categorycolormap.get(category);
	}

	private static int[] ANDROIDCOLOR = { Color.BLUE, Color.GREEN,
			Color.MAGENTA, Color.YELLOW, Color.CYAN, Color.DKGRAY, Color.GRAY,
			Color.LTGRAY, Color.RED, Color.WHITE

	};

	
}
