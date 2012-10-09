package com.hobom.mobile.util;

import java.io.File;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

/**
 * 照片选择器
 * @author zhangmingxun
 *
 */
public class ImageChooser {

	public static final int REQUEST_CAMERA = 1000; 
	public static final int REQUEST_LIBRARY = 2000;
	private static String[]sources ={"手机照相","手机相册"};
	private static String chooserTitle = "选择图片来源";
	public static void choosePicture(final Activity context,final ImageListener listener){
		new AlertDialog.Builder(context.getParent())
		.setTitle(chooserTitle)
		.setItems(sources, new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if(which==0){
					Intent intent = new Intent(
							MediaStore.ACTION_IMAGE_CAPTURE);
					String filename = TimeUtil.getDateToInt(new Date())+".png";
					
					File out = new File(Environment
							.getExternalStorageDirectory(),
							filename);
					Uri uri = Uri.fromFile(out);
				//	intent.putExtra("filename", filename);
					intent.putExtra(
							MediaStore.EXTRA_OUTPUT, uri);
					listener.recall(out.getAbsolutePath());
					context.startActivityForResult(intent, REQUEST_CAMERA);
					
				}else if(which==1){
					Intent intent = new Intent(
							Intent.ACTION_GET_CONTENT);
					intent.addCategory(Intent.CATEGORY_OPENABLE);
					intent.setType("image/*");
					context.startActivityForResult(intent, REQUEST_LIBRARY);
					
				}
			}
			
		})
		.create()
		.show();
	}
	
	public interface ImageListener{
		public void recall(String filename);
	}
}
