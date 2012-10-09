package com.hobom.mobile.ui;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.amap.mapapi.core.AMapException;
import com.amap.mapapi.core.GeoPoint;
import com.amap.mapapi.geocoder.Geocoder;
import com.hobom.mobile.FoodApplication;
import com.hobom.mobile.R;
import com.hobom.mobile.db.DatabaseAccessor;
import com.hobom.mobile.db.DatabaseAccessor.Tables;
import com.hobom.mobile.db.DatabaseColumns;
import com.hobom.mobile.model.Consume;
import com.hobom.mobile.model.Food;
import com.hobom.mobile.util.FoodUtil;
import com.hobom.mobile.util.ImageCache;
import com.hobom.mobile.util.ImageChooser;
import com.hobom.mobile.util.ImageChooser.ImageListener;
import com.hobom.mobile.util.TimeUtil;
import com.hobom.mobile.widgets.CommonTitleView;

/**
 * 新增一次美食
 * 
 * @author zhangmingxun
 * 
 */
public class MoreActivity extends Activity implements OnClickListener,
		ImageListener {

	private EditText name, address;
	private ImageView useMap;
	private EditText price;
	private RatingBar rating;
	private ImageView image;
	private Button addphoto;
	private EditText remark;
	private static final int REQUEST_CODE_USEMAP = 120;
	private Consume consume;
	private Geocoder coder;
	public static final int ERROR = 1001;
	public static final int REOCODER_RESULT = 3000;// 地理编码结果
	public static final int REVCODE_RESULT = 2000;// 逆编码结果
	private ProgressDialog progDialog;
	private CommonTitleView commonTitle;
	private ImageView leftBtn, rightBtn;
	private String addressName;
	private EditText tel;
	private Spinner spinner;
	private GeoPoint gp;
	private DatabaseAccessor accessor;
	private boolean firstTime = true;
	private String cameraFilepath = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addfood);
		coder = new Geocoder(this);
		consume = new Consume();
		consume.setFood(new Food());
		initView();
		progDialog = new ProgressDialog(this.getParent());
		FoodApplication app = (FoodApplication)getApplication();
		accessor = app.getDatabase();
	}

	private void initView() {
		commonTitle = (CommonTitleView) findViewById(R.id.header);
		commonTitle.setTitle("增加美食");
	
		leftBtn = commonTitle.getLeftView();
		leftBtn.setBackgroundResource(R.drawable.btn_cancel_selector);
		leftBtn.setOnClickListener(this);
		rightBtn = commonTitle.getRightView();
		rightBtn.setBackgroundResource(R.drawable.btn_save_selector);
		rightBtn.setOnClickListener(this);
		name = (EditText) findViewById(R.id.name);
		address = (EditText) findViewById(R.id.address);
		useMap = (ImageView) findViewById(R.id.fix);
		price = (EditText) findViewById(R.id.price);
		rating = (RatingBar) findViewById(R.id.rating);
		image = (ImageView) findViewById(R.id.img);
		addphoto = (Button) findViewById(R.id.addphoto);
		remark = (EditText) findViewById(R.id.remark);
		tel = (EditText) findViewById(R.id.tel);
		spinner = (Spinner) findViewById(R.id.type);
		final String[] array = getResources().getStringArray(
				R.array.foodtype_items);

		spinner.setAdapter(new ArrayAdapter<String>(this.getParent(),
				android.R.layout.simple_spinner_item, array));
		consume.getFood().setType(array[0]);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View arg1,
					int pos, long arg3) {
				// TODO Auto-generated method stub
				// System.out.println("onitemselected");
				String name = parent.getItemAtPosition(pos).toString();

				// System.out.println("在下啦框理选择的 类型--------" + name);

				if (firstTime) {
					firstTime = false;
					return;
				}

				consume.getFood().setType(array[pos]);

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});

		useMap.setOnClickListener(this);
		addphoto.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == addphoto) {
			ImageChooser.choosePicture(this, this);
		} else if (v == useMap) {
			Intent intent = new Intent(this.getParent(),
					MapPointChooseActivity.class);
			intent.putExtra("mapMode", MapPointChooseActivity.MODE_CHOOSE_START);
			startActivityForResult(intent, REQUEST_CODE_USEMAP);
		} else if (v == leftBtn) {

			name.setText("");
			address.setText("");
			price.setText("");
			rating.setRating(0);
			remark.setText("");
			tel.setText("");
			image.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.zlui_no_recipe_250x250));

			
			
		} else if (v == rightBtn) {

			if (TextUtils.isEmpty(name.getText().toString()))
				FoodUtil.openAlertDialog(this.getParent(), "美食名称不能为空");

			else if (TextUtils.isEmpty(address.getText().toString()))
				FoodUtil.openAlertDialog(this.getParent(), "地址不能为空");
			else if (TextUtils.isEmpty(price.getText().toString()))
				FoodUtil.openAlertDialog(this.getParent(), "美食价格不能为空");
			else if (TextUtils.isEmpty(remark.getText().toString()))
				FoodUtil.openAlertDialog(this.getParent(), "评论不能为空");
			else {
				System.out.println("save the info");
				progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progDialog.setIndeterminate(false);
				progDialog.setCancelable(true);
				progDialog.setMessage("正在保存信息");
				progDialog.show();

				if (gp == null) {

					getLatlon(name.getText().toString());
				} else {
					save();
				}
				// save();

			}

		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (progDialog != null && progDialog.isShowing())
			progDialog.dismiss();
		progDialog = null;
	}

	private void save() {

		consume.setDate(TimeUtil.getCurrTimeInt());
		consume.setRating((int) rating.getRating());

		consume.setRemark(remark.getText().toString());

		consume.getFood().setName(name.getText().toString());

		consume.getFood().setAddress(address.getText().toString());

		consume.getFood()
				.setPrice(Float.parseFloat(price.getText().toString()));
		if (!TextUtils.isEmpty(tel.getText().toString()))

			consume.getFood().setTelephone(tel.getText().toString());

		ContentValues foodValues = new ContentValues();
		foodValues.put(DatabaseColumns.FoodColumn.NAME, consume.getFood()
				.getName());
		foodValues.put(DatabaseColumns.FoodColumn.ADDRESS, consume.getFood()
				.getAddress());
		foodValues.put(DatabaseColumns.FoodColumn.PRICE, consume.getFood()
				.getPrice());
		foodValues.put(DatabaseColumns.FoodColumn.TEL, consume.getFood()
				.getTelephone());
		foodValues.put(DatabaseColumns.FoodColumn.TYPE, consume.getFood()
				.getType());
		foodValues.put(DatabaseColumns.FoodColumn.LAT, consume.getFood()
				.getLatitude());
		foodValues.put(DatabaseColumns.FoodColumn.LON, consume.getFood()
				.getLongitude());

		long id = accessor.insert(Tables.FOOD, null, foodValues);

		ContentValues consumeValues = new ContentValues();
		consumeValues.put(DatabaseColumns.ConsumeColumn.FOODID, id);
		consumeValues.put(DatabaseColumns.ConsumeColumn.COMMENT,
				consume.getRemark());
		consumeValues
				.put(DatabaseColumns.ConsumeColumn.DATE, consume.getDate());
		consumeValues.put(DatabaseColumns.ConsumeColumn.PICPATH,
				consume.getPicPath());
		consumeValues.put(DatabaseColumns.ConsumeColumn.RATING,
				consume.getRating());

		accessor.insert(Tables.CONSUME, null, consumeValues);
        Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
		progDialog.dismiss();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == ImageChooser.REQUEST_CAMERA) {
			System.out.println("the filename is:" + cameraFilepath);
			
			image.setImageBitmap(ImageCache.getInstance().getBitmap(cameraFilepath));

			consume.setPicPath(cameraFilepath);
		} else if (requestCode == ImageChooser.REQUEST_LIBRARY) {
			System.out.println("library");
			Uri uri = data.getData();
			System.out.println("the uri is:" + uri.toString());
			Cursor cursor = getContentResolver().query(uri, null, null, null,
					null);
			cursor.moveToFirst();
			String srcFile = cursor.getString(1);
			cursor.close();
			image.setImageBitmap(ImageCache.getInstance().getBitmap(srcFile));
			// upload(srcFile);
			consume.setPicPath(srcFile);

		} else if (requestCode == REQUEST_CODE_USEMAP) {
			System.out.println("return from  usemap");
			gp = (GeoPoint) data.getParcelableExtra("LonLat");
			getAddress((double) gp.getLatitudeE6() / 1E6,
					(double) gp.getLongitudeE6() / 1E6);

			consume.getFood().setLatitude(gp.getLatitudeE6());
			consume.getFood().setLongitude(gp.getLongitudeE6());
		}
	}

	// 地理编码
	public void getAddress(final double mlat, final double mLon) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					List<Address> address = coder
							.getFromLocation(mlat, mLon, 3);
					if (address != null && address.size() > 0) {
						Address addres = address.get(0);
						addressName = addres.getAdminArea()
								+ addres.getSubLocality()
								+ addres.getFeatureName() + "附近";
						handler.sendMessage(Message.obtain(handler,
								REOCODER_RESULT));

					}
				} catch (AMapException e) {
					// TODO Auto-generated catch block
					handler.sendMessage(Message.obtain(handler, ERROR));
				}

			}
		});

		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(true);
		progDialog.setMessage("正在获取地址");
		progDialog.show();
		t.start();
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == REOCODER_RESULT) {
				progDialog.dismiss();
				address.setText(addressName);
			} else if (msg.what == REVCODE_RESULT) {
				save();
			} else if (msg.what == ERROR) {
				progDialog.dismiss();
				Toast.makeText(getApplicationContext(), "请检查网络连接是否正确?",
						Toast.LENGTH_SHORT).show();
			}
		}
	};

	// 逆地理编码
	public void getLatlon(final String name) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					List<Address> address = coder.getFromLocationName(name, 3);
					if (address != null && address.size() > 0) {
						Address addres = address.get(0);
						gp = new GeoPoint((int) (addres.getLatitude() * 1E6),
								(int) (addres.getLongitude() * 1E6));

						addressName = addres.getLatitude() + ","
								+ addres.getLongitude();
						handler.sendMessage(Message.obtain(handler,
								REVCODE_RESULT));

					}
				} catch (AMapException e) {
					handler.sendMessage(Message.obtain(handler, ERROR));
				}

			}
		});

		/*
		 * progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		 * progDialog.setIndeterminate(false); progDialog.setCancelable(true);
		 * progDialog.setMessage("正在获取地址"); progDialog.show();
		 */
		t.start();
	}

	@Override
	public void recall(String filename) {
		// TODO Auto-generated method stub
		cameraFilepath = filename;
		
		

	}

}
