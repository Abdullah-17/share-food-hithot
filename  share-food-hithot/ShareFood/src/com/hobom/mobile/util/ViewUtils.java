package com.hobom.mobile.util;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ViewUtils {

   /**
    * 后台运行(回home界面)
    * @return
    */
	public static Intent getBackgroundIntent(){
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addCategory(Intent.CATEGORY_HOME);
		return intent;
	}
	public static Intent getCaptureIntent(){
		// 照相机
		Intent intent = new Intent(
				MediaStore.ACTION_IMAGE_CAPTURE);
		File out = new File(Environment
				.getExternalStorageDirectory(),
				"camera.png");
		Uri uri = Uri.fromFile(out);
		intent.putExtra(
				MediaStore.EXTRA_OUTPUT, uri);
		
		return intent;
	}
	public static Intent getResizedPhotoPickIntent() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 96);
		intent.putExtra("outputY", 96);
		intent.putExtra("return-data", true);
		return intent;
	}

	public static Intent getPhotoPickIntent() {
		Intent intent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
		return intent;
	}

	public static Intent getViewPdfIntent(String fullFileName) {
		if (fullFileName == null) {
			return null;
		}
		File file = new File(fullFileName);
		if (file.exists()) {
			Uri uri = Uri.fromFile(file);
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(uri, "application/pdf");
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			return intent;
		}
		return null;
	}

	public static Intent getViewImageIntent(String fullFileName) {
		if (fullFileName == null) {
			return null;
		}
		File file = new File(fullFileName);
		if (file.exists()) {
			Uri uri = Uri.fromFile(file);
			Uri modified_uri = Uri.parse("file://" + uri.getPath());
			Intent intent = new Intent(Intent.ACTION_VIEW, modified_uri);
			intent.setDataAndType(modified_uri, "image/*");
			return intent;
		}
		return null;
	}

	public static void hideKeyboard(final Activity activity, final EditText et) {
		InputMethodManager imm = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
		}
	}

	
}
