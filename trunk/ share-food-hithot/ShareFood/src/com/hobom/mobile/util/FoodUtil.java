package com.hobom.mobile.util;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.lang.reflect.Field;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hobom.mobile.R;
import com.hobom.mobile.model.AccessInfo;

public class FoodUtil {
	public static void hideSearchIconAndLabel(Activity activity) {
		try {

			Class activityClass = Class.forName("android.app.Activity");
			Field activityClassf = activityClass
					.getDeclaredField("mSearchManager");
			activityClassf.setAccessible(true);
			Object mSearchManager = activityClassf.get(activity);

			Class searchClass = Class.forName("android.app.SearchManager");
			Field mSearchDialogf = searchClass
					.getDeclaredField("mSearchDialog");
			mSearchDialogf.setAccessible(true);
			Object mSearchDialog = mSearchDialogf.get(mSearchManager);

			Class searchDialog = Class.forName("android.app.SearchDialog");
			Field mAppIcon = searchDialog.getDeclaredField("mAppIcon");
			mAppIcon.setAccessible(true);
			ImageView imageView = (ImageView) mAppIcon.get(mSearchDialog);
			imageView.setVisibility(View.GONE);

			Field mBadgeLabel = searchDialog.getDeclaredField("mBadgeLabel");
			mBadgeLabel.setAccessible(true);
			TextView textView = (TextView) mBadgeLabel.get(mSearchDialog);
			textView.setVisibility(View.GONE);

			Field mSearchPlate = searchDialog.getDeclaredField("mSearchPlate");
			mSearchPlate.setAccessible(true);
			View searchPlate = (View) mSearchPlate.get(mSearchDialog);
			searchPlate.setPadding(searchPlate.getPaddingLeft() + 5,
					searchPlate.getPaddingTop() + 2,
					searchPlate.getPaddingRight() + 5,
					searchPlate.getPaddingBottom() + 2);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 分享�?新浪微博'�?腾讯微博'的对话框
	 * 
	 * @param context
	 *            当前Activity
	 * @param title
	 *            分享的内容
	 * @param filepath
	 *            分享的图片路径	 */
	public static void showShareDialog(final Activity context,
			final String content, final String filePath) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(android.R.drawable.btn_star);
		builder.setTitle(context.getString(R.string.share));
		builder.setItems(R.array.app_share_items,
				new DialogInterface.OnClickListener() {
					AppConfig cfgHelper = AppConfig.getAppConfig(context);
					AccessInfo access = cfgHelper.getAccessInfo();

					public void onClick(DialogInterface arg0, int arg1) {
						switch (arg1) {
						case 0:
							// 分享的内�?
							final String shareMessage = content ;
							// 初始化微�?
							if (SinaWeiboHelper.isWeiboNull()) {
								SinaWeiboHelper.initWeibo();
							}
							
							// 判断之前是否登陆�?
							if (access != null) {
								SinaWeiboHelper.progressDialog = new ProgressDialog(
										context);
								SinaWeiboHelper.progressDialog
										.setProgressStyle(ProgressDialog.STYLE_SPINNER);
								SinaWeiboHelper.progressDialog
										.setMessage(context
												.getString(R.string.sharing));
								SinaWeiboHelper.progressDialog
										.setCancelable(true);
								SinaWeiboHelper.progressDialog.show();
								new Thread() {
									public void run() {
										SinaWeiboHelper.setAccessToken(
												access.getAccessToken(),
												access.getAccessSecret(),
												access.getExpiresIn());
										SinaWeiboHelper.shareMessage(context,
												shareMessage,filePath);
									}
								}.start();
							} else {
								SinaWeiboHelper
										.authorize(context, shareMessage,filePath);
							}
							break;
						case 1:
							QQWeiboHelper.shareToQQ(context, content, "");
							break;
						}
					}
				});
		builder.create().show();
	}

	public static byte[] getCompressedBitmap(String filepath) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filepath, opts);

		opts.inSampleSize = computeSampleSize(opts, -1, 128 * 128);
		opts.inJustDecodeBounds = false;
		try {
			Bitmap bmp = BitmapFactory.decodeFile(filepath, opts);
			return Bitmap2Bytes(bmp);

		} catch (OutOfMemoryError err) {
		}

		return null;
	}

	/**
	 * 压缩图片
	 * 
	 * @param url
	 * @param sampleSize
	 * @throws Exception
	 */
	public static byte[] compressedBitmap(String url, int sampleSize)
			throws Exception {
		FileInputStream fis = null;
		ByteArrayOutputStream out = null;
		Bitmap bitmap = null;
		byte[] data = null;
		try {
			fis = new FileInputStream(url);
			out = new ByteArrayOutputStream();
			int i = -1;
			byte buf[] = new byte[1024];
			while ((i = fis.read(buf)) != -1) {
				out.write(buf, 0, i);
			}
			byte imgData[] = out.toByteArray();
			// 进行抽样缩放
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inSampleSize = 1;
			opts.inJustDecodeBounds = true;
			bitmap = BitmapFactory.decodeByteArray(imgData, 0, imgData.length,
					opts);
			// 计算抽样比率
			opts.inSampleSize = computeSampleSize(opts, sampleSize);
			opts.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeByteArray(imgData, 0, imgData.length,
					opts);

		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			throw new Exception();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception();
		} finally {
			try {
				data = Bitmap2Bytes(bitmap);
				bitmap.recycle();
				fis.close();
				out.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return data;

	}

	static int computeSampleSize(BitmapFactory.Options options, int target) {
		int w = options.outWidth;
		int h = options.outHeight;
		int candidateW = w / target;
		int candidateH = h / target;
		int candidate = Math.max(candidateW, candidateH);
		if (candidate == 0)
			return 1;
		if (candidate > 1) {
			if ((w > target) && (w / candidate) < target)
				candidate -= 1;
		}
		if (candidate > 1) {
			if ((h > target) && (h / candidate) < target)
				candidate -= 1;
		}
		return candidate;
	}

	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	public static byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	/***
	 * 压缩放大图片
	 * 
	 * @param bitmap
	 * @param w
	 * @param h
	 * @return
	 */
	public static Bitmap resizeImage(Bitmap bitmap, int w, int h) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		float scaleWidth = ((float) w) / width;
		float scaleHeight = ((float) h) / height;

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);

		return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
	}

	/**
	 * drawable转化为bitmap
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {
		Bitmap bitmap = Bitmap
				.createBitmap(
						drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight(),
						drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
								: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		// canvas.setBitmap(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	/**
	 * bitmap转drawable
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Drawable bitmap2Drawable(Bitmap bitmap) {
		BitmapDrawable bd = new BitmapDrawable(bitmap);
		return (Drawable) bd;

	}

	public static void openAlertDialog(Context context, String message) {
		new AlertDialog.Builder(context).setTitle("注意")
				.setMessage(message).setPositiveButton("确定", null).show();

	}
}
