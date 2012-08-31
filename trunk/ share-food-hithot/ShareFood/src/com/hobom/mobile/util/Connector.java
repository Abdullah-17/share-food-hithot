package com.hobom.mobile.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.telephony.TelephonyManager;

public class Connector {

	
	/**
     * Checks if is connected.（没有网络连接则不能登录，并提示用户)
     * @param ctx the ctx
     * @return true, if is connected
     */
    public static boolean isConnected(final Context ctx) {
	ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(
	    Context.CONNECTIVITY_SERVICE);
	NetworkInfo ni = cm.getActiveNetworkInfo();
	return ni != null && ni.isConnected();
    }

    /**
     * Checks if is wifi.
     * @param ctx the ctx
     * @return true, if is wifi
     */
    public static boolean isWifi(final Context ctx) {
	WifiManager wm = (WifiManager) ctx.getSystemService(
	    Context.WIFI_SERVICE);
	WifiInfo wi = wm.getConnectionInfo();
	if (wi != null
	    && (WifiInfo.getDetailedStateOf(wi.getSupplicantState())
		== DetailedState.OBTAINING_IPADDR
		|| WifiInfo.getDetailedStateOf(wi.getSupplicantState())
		== DetailedState.CONNECTED)) {
	    return false;
	}
	return false;
    }
    public static boolean wifiOpened(final Context context){
    	WifiManager wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
    	return wm.isWifiEnabled();
    }
    /**
     * Checks if is umts.
     * @param ctx the ctx
     * @return true, if is umts
     */
    public static boolean isUmts(final Context ctx) {
	TelephonyManager tm = (TelephonyManager) ctx.getSystemService(
	    Context.TELEPHONY_SERVICE);
	return tm.getNetworkType() >= TelephonyManager.NETWORK_TYPE_UMTS;
    }

    /**
     * Checks if is edge.
     * @param ctx the ctx
     * @return true, if is edge
     */
    public static boolean isEdge(final Context ctx) {
	TelephonyManager tm = (TelephonyManager) ctx.getSystemService(
	    Context.TELEPHONY_SERVICE);
	return tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_EDGE;
    }

    
    /**
     * 判断是否GPS可用
     * @param context
     * @return true if the gps is available
     */
    public  static boolean isGPSEnabled(final Context context){
		LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
	
		
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

	}
    /**
     *  判断是否SD卡可用，不可用则不让登录，并提示用户
     * @param context
     * @return
     */
    public static boolean isSDCARDAvaible(final Context context){
    	
    	return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
    
    
    public static boolean isGPSInService(final Context context){
    	
    	LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
    	if(!(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)))
    		return false;
    	GpsStatus status = locationManager.getGpsStatus(null); 
        
        Iterable<GpsSatellite> sats = status.getSatellites(); 
        if(sats.iterator().hasNext()){
        	return true;
        } 
        else return false;
    }
    
    
    public static String request(String urlString, Map<String, String> params) {
		String strReturn = null;
		StringBuffer sb = new StringBuffer(urlString);

		for (String key : params.keySet()) {
			sb.append(key).append("=").append(params.get(key)).append("&");
		}

		HttpGet httpRequest = new HttpGet(sb.toString());
		try {
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = httpResponse.getEntity();
				InputStream is = entity.getContent();
				StringBuffer buffer = new StringBuffer();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						is, "utf-8"));
				String data = "";
				while ((data = br.readLine()) != null) {
					// data = EncodingUtils.getString(data.getBytes(), "UTF-8");
					buffer.append(data);
				}
				strReturn = java.net.URLDecoder.decode(buffer.toString(),
						"utf-8");
			}

			// strReturn = EntityUtils.toString(httpResponse.getEntity());

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strReturn;
	}

    
    public static String post(String actionUrl, Map<String, String> params) {

		HttpPost httpPost = new HttpPost(actionUrl);

		List<NameValuePair> list = new ArrayList<NameValuePair>();

		for (Map.Entry<String, String> entry : params.entrySet()) {

			list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));

		}

		try {

			httpPost.setEntity(new UrlEncodedFormEntity(list, HTTP.UTF_8));

			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpPost);

			if (httpResponse.getStatusLine().getStatusCode() == 200) {

				return EntityUtils.toString(httpResponse.getEntity());

			}

		} catch (Exception e) {

			throw new RuntimeException(e);

		}

		return null;

	}
    public static String getHttpContent(String rometURL, String encoding)
	{
		try {
			 URL url = new URL (rometURL);
		        URLConnection uc = url.openConnection();
		        uc.setRequestProperty  ("Authorization", "Basic " + encoding);
		        uc.setRequestProperty("User-Agent", "Mozilla/5.0");
		        uc.setRequestProperty("method", "post");  
		        
		        InputStream content = (InputStream)uc.getInputStream();
		        BufferedReader in = new BufferedReader (new InputStreamReader (content,encoding));
		        StringBuffer buffer = new StringBuffer();
		        while (in.ready()) {
					String inString = in.readLine().trim();
					if (inString.length() != 0)
					{
						buffer.append(inString);
					}
				}
		        return buffer.toString();
		} catch (Exception e) {
		}
       return null;
	}
	
    
    
}
