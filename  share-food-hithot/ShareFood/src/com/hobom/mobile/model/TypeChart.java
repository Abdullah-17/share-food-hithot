package com.hobom.mobile.model;

import java.util.HashMap;
import java.util.Map;

import org.achartengine.ChartFactory;
import org.achartengine.renderer.DefaultRenderer;

import android.content.Intent;
import android.database.Cursor;

import com.hobom.mobile.FoodApplication;
import com.hobom.mobile.db.DatabaseAccessor;
import com.hobom.mobile.util.MapUtil;

public class TypeChart extends AbstractChart {

	public TypeChart() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "美食类别开销";
	}

	@Override
	public String getDesc() {
		// TODO Auto-generated method stub
		return "按类别显示美食开销";
	}

	@Override
	public Intent execute(FoodApplication context) {
		// TODO Auto-generated method stub
		Map<String,Double>costs = new HashMap<String,Double>();
		DatabaseAccessor accessor = context.getDatabase();
		Cursor cursor = accessor.rawQuery("select b.type,sum(b.price) from consume a,food b where a.foodId = b._id group by b.type ", null);
		while(cursor.moveToNext()){
			String type = cursor.getString(0);
			double price = cursor.getDouble(1);
			costs.put(type, price);
			
			
		}
		
		if(cursor!=null){
			cursor.close();
			cursor = null;
		}
		if(costs.size()==0)
			return null;
		int[]colors = MapUtil.getColorsByNum(costs.size());
		
	    DefaultRenderer renderer = buildCategoryRenderer(colors);
	    renderer.setLabelsTextSize(10);
	    return ChartFactory.getPieChartIntent(context, buildCategoryDataset("按美食类别计算开销", costs), renderer);
	}

}
