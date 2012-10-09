package com.hobom.mobile.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Toast;

import com.amap.mapapi.core.GeoPoint;
import com.amap.mapapi.core.OverlayItem;
import com.amap.mapapi.map.ItemizedOverlay;
import com.amap.mapapi.map.MapActivity;
import com.amap.mapapi.map.MapController;
import com.amap.mapapi.map.MapView;
import com.amap.mapapi.map.Projection;
import com.hobom.mobile.R;

/**
 * �鿴ĳ����ʳλ��
 * 
 * @author mingxunzh
 * 
 */
public class SimpleFoodMapActivity extends MapActivity {
	MapView mMapView;
	MapController mMapController;
    private int lat,lon;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewonefood);
		mMapView = (MapView) findViewById(R.id.main_mapView);
		mMapView.setBuiltInZoomControls(true); // �����������õ����ſؼ�
		mMapController = mMapView.getController();
        lat = getIntent().getIntExtra("lat", 0);
        lon = getIntent().getIntExtra("lon", 0);
    	GeoPoint point = new GeoPoint(lat,
				lon);  //�ø����ľ�γ�ȹ���һ��GeoPoint����λ��΢�� (�� * 1E6)
		mMapController.setCenter(point);  //���õ�ͼ���ĵ�
		mMapController.setZoom(15);    //���õ�ͼzoom����
        System.out.println("the lat and lon is:"+lat+":"+lon);
		Drawable marker = getResources().getDrawable(R.drawable.da_marker_red); // �õ���Ҫ���ڵ�ͼ�ϵ���Դ
		marker.setBounds(0, 0, marker.getIntrinsicWidth(),
				marker.getIntrinsicHeight()); // Ϊmaker����λ�úͱ߽�
		mMapView.getOverlays().add(new OverItemT(marker, this,lat,lon)); // ���ItemizedOverlayʵ����mMapView
	}



}

/**
 * ĳ�����͵ĸ�����������������ͬ����ʾ��ʽ��ͬ������ʽ��ͬ����ʱ��ʹ�ô���.
 */
class OverItemT extends ItemizedOverlay<OverlayItem> {
	private List<OverlayItem> GeoList = new ArrayList<OverlayItem>();
	private Drawable marker;
	private Context mContext;
	
	public OverItemT(Drawable marker, Context context,long lat,long lon) {
		super(boundCenterBottom(marker));
        this.marker = marker;
		this.mContext = context;
        // �ø����ľ�γ�ȹ���GeoPoint����λ��΢�� (�� * 1E6)
		GeoPoint p1 = new GeoPoint(lat,lon);
		
        // ����OverlayItem��������������Ϊ��item��λ�ã������ı�������Ƭ��
		GeoList.add(new OverlayItem(p1, "P1", "point1"));
			
		populate();  //createItem(int)��������item��һ���������ݣ��ڵ�����������ǰ�����ȵ����������
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        // Projection�ӿ�������Ļ���ص�����ϵͳ�͵�����澭γ�ȵ�����ϵͳ֮��ı任
		Projection projection = mapView.getProjection(); 
		for (int index = size() - 1; index >= 0; index--) { // ����GeoList
			OverlayItem overLayItem = getItem(index); // �õ�����������item
            String title = overLayItem.getTitle();
			// �Ѿ�γ�ȱ任�������MapView���Ͻǵ���Ļ��������
			Point point = projection.toPixels(overLayItem.getPoint(), null); 
			// ���ڴ˴�������Ļ��ƴ���
			Paint paintText = new Paint();
			paintText.setColor(Color.BLACK);
			paintText.setTextSize(15);
			canvas.drawText(title, point.x-30, point.y - 25, paintText); // �����ı�
		}
        super.draw(canvas, mapView, shadow);
		//����һ��drawable�߽磬ʹ�ã�0��0�������drawable�ײ����һ�����ĵ�һ������
		boundCenterBottom(marker);
	}

	@Override
	protected OverlayItem createItem(int i) {
		// 
		return GeoList.get(i);
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return GeoList.size();
	}

	@Override
	// ��������¼�
	protected boolean onTap(int i) {
		setFocus(GeoList.get(i));
		Toast.makeText(this.mContext, GeoList.get(i).getSnippet(),
				Toast.LENGTH_SHORT).show();
		return true;
	}

	@Override
	public boolean onTap(GeoPoint point, MapView mapView) {
		// TODO Auto-generated method stub
		return super.onTap(point, mapView);
	}

}
