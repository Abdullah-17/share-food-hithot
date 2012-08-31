package com.hobom.mobile.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.AttributeSet;
import android.view.View;

import com.hobom.mobile.R;

public class TabButton extends View {
	public final static String TAG = "TabButton";

	private Bitmap mImage;
	private Bitmap mSelectedImage;
	private Paint mPaint;
	private float mScale;
	private Matrix mIconMatrix;
	private String mLabel = null;
	private PorterDuffColorFilter mColorFilter = null;

	public TabButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.TabButton);
		int iconImage = a.getResourceId(R.styleable.TabButton_iconImage, 0);

		mScale = 1.0f;

		if (iconImage != 0) {
			Resources resources = context.getResources();
			mImage = BitmapFactory.decodeResource(resources, iconImage);
		}

		setBackgroundResource(R.drawable.tab_button_sel);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(0xffffffff);
		mColorFilter = new PorterDuffColorFilter(0xffffffff,
				PorterDuff.Mode.SRC_ATOP);
		mPaint.setColorFilter(mColorFilter);
		mPaint.setTextSize(12);
		mIconMatrix = new Matrix();
	}

	public TabButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		int w = this.getMeasuredWidth();
		int h = this.getMeasuredHeight();

		if (mImage != null) {
			if (!isSelected()) {
				mPaint.setColor(0xff939393);
				mColorFilter = new PorterDuffColorFilter(0xff939393,
						PorterDuff.Mode.SRC_ATOP);
				mPaint.setColorFilter(mColorFilter);
				mPaint.setShadowLayer(0, 0.0f, 0.0f, 0x00000000);
			} else {
				mPaint.setColor(0xffffffff);
				mColorFilter = new PorterDuffColorFilter(0xffffffff,
						PorterDuff.Mode.SRC_ATOP);
				mPaint.setColorFilter(mColorFilter);
				mPaint.setShadowLayer(10, 0.0f, 0.0f, 0xffffffff);
			}

			float labelHeight = 0;
			float imageHeight = mImage.getHeight() * mScale;
			float labelContentsOffset = -1;

			if (mLabel != null) {
				float labelWidth = mPaint.measureText(mLabel);
				labelHeight = Math.abs(mPaint.ascent())
						+ Math.abs(mPaint.descent());
				labelContentsOffset = (h - labelHeight - imageHeight) / 2.0f;

				float textX = (w - labelWidth) / 2.0f;
				float textY = h - labelContentsOffset;

				canvas.drawText(mLabel, textX, textY, mPaint);
			}

			float iconX = (w - (mImage.getWidth() * mScale)) / 2.0f;
			if (labelContentsOffset == -1) {
				labelContentsOffset = (h - imageHeight) / 2.0f;
			}
			float iconY = labelContentsOffset;

			mIconMatrix.setTranslate(iconX, iconY);
			mIconMatrix.preScale(mScale, mScale);

			mPaint = new Paint();
			if (!isSelected()) {
				canvas.drawBitmap(mImage, mIconMatrix, mPaint);
			} else {
				canvas.drawBitmap(mSelectedImage, mIconMatrix, mPaint);
			}
		}
	}

	private int sizeByMeasureSpec(int size, int measureSize, int measureSpec) {
		int newSize;
		if (measureSpec == View.MeasureSpec.EXACTLY) {
			newSize = measureSize;
		} else {
			newSize = size;
			if (measureSpec == View.MeasureSpec.AT_MOST
					&& newSize > measureSize) {
				newSize = measureSize;
			}
		}

		return newSize;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int specWidthMode = MeasureSpec.getMode(widthMeasureSpec);
		int specWidth = MeasureSpec.getSize(widthMeasureSpec);

		int specHeightMode = MeasureSpec.getMode(heightMeasureSpec);
		int specHeight = MeasureSpec.getSize(heightMeasureSpec);

		int width = sizeByMeasureSpec(getCalculatedWidth(), specWidth,
				specWidthMode);
		int height = sizeByMeasureSpec(getCalculatedHeight(), specHeight,
				specHeightMode);

		setMeasuredDimension(width, height);

		mScale = width * 1.0f / mImage.getWidth();
	}

	private int getCalculatedWidth() {
		// return mImage.getWidth();
		return this.getBackground().getIntrinsicWidth();
	}

	private int getCalculatedHeight() {
		// return mImage.getHeight();
		return this.getBackground().getIntrinsicHeight();
	}

	public void setIconImage(Bitmap image) {
		mImage = image;
		invalidate();
	}

	public void setSelectedIconImage(Bitmap image) {
		mSelectedImage = image;
		invalidate();
	}

	public void setLabel(String label) {
		mLabel = label;
	}

	public String getLabel() {
		return mLabel;
	}
}
