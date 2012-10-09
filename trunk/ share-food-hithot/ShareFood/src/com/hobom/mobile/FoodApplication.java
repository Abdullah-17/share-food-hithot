package com.hobom.mobile;

import android.app.Application;

import com.hobom.mobile.db.DatabaseAccessor;
import com.hobom.mobile.model.POISearchResult;
import com.hobom.mobile.util.PreferenceUtil;

public class FoodApplication extends Application {

	private static FoodApplication mApp;
	private POISearchResult poiSearchResult;
	private DatabaseAccessor accessor;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mApp = this;
		PreferenceUtil.setContext(this);
		accessor = new DatabaseAccessor(this);
		
	}

	public static FoodApplication getApp(){
		return mApp;
	}
	
	public void setCurrentPOISearchResult(POISearchResult result){
		this.poiSearchResult = result;
	}
	public POISearchResult getCurrentPOISearchResult(){
		return poiSearchResult;
	}
	
	public DatabaseAccessor getDatabase(){
		return accessor;
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
		if(accessor!=null){
			accessor.closeDB();
			accessor = null;
		}
	}
	
	
	
	
}
