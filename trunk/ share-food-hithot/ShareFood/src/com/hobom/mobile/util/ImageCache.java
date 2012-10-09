package com.hobom.mobile.util;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageCache {

	private HashMap<String,SoftReference<Bitmap> >cache = new HashMap<String,SoftReference<Bitmap>>();
	
	private static ImageCache instance;
	private ImageCache(){
		
	}
	public static ImageCache getInstance(){
		if(instance==null){
			instance = new ImageCache();
		}
		return instance;
	}
	
	public Bitmap getBitmap(String picpath){
		Bitmap bitmap = null;
		if(cache.containsKey(picpath)){
			SoftReference<Bitmap>ref = cache.get(picpath);
			bitmap = ref.get();
			if(bitmap!=null&&!bitmap.isRecycled()){
				return bitmap;
			}
		}
		 bitmap = BitmapFactory.decodeFile(picpath);
		 cache.put(picpath, new SoftReference<Bitmap>(bitmap));
		 return bitmap;
		
	}
	
	
}
