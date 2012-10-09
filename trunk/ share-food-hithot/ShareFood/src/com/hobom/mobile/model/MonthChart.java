package com.hobom.mobile.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;

import com.hobom.mobile.FoodApplication;
import com.hobom.mobile.db.DatabaseAccessor;
import com.hobom.mobile.util.TimeUtil;
/**
 * 按月显示团购开销
 * @author zhangmingxun
 *
 */
public class MonthChart extends AbstractChart {

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "月开销图";
	}

	@Override
	public String getDesc() {
		// TODO Auto-generated method stub
		return "按月显示美食开销";
	}

	@Override
	public Intent execute(FoodApplication context) {
		// TODO Auto-generated method stub
		 Map<Date,Double>monthcost = new HashMap<Date, Double>();
		
		DatabaseAccessor accessor = context.getDatabase();
		Cursor cursor = accessor.rawQuery("select a.date,sum(b.price) from consume a,food b where a.foodId = b._id" +
				"  group by a.date" ,
				null);
		while(cursor.moveToNext()){
			int time = cursor.getInt(0);
			double price = cursor.getDouble(1);
			Date date = TimeUtil.int2Date(time);
			Date originDate = TimeUtil.getMonthOrigin(date);
			Log.i(TAG,"the date is:"+date.getDate());
			if (monthcost.containsKey(originDate)) {
				Log.i(TAG,"costs map contain the originDate");
				monthcost.put(originDate, monthcost.get(originDate) + price);
			} else {
				Log.i(TAG,"costs map didnt contain the origindate");
				monthcost.put(originDate, price);
				
			}

		}
		
		if(cursor!=null){
			cursor.close();
			cursor = null;
		}
	   
	    if(monthcost.size()==0)
	    	return null;
	    String[] titles = new String[] { "按月显示美食开销" };
	    List<Date[]> dates = new ArrayList<Date[]>();
	    List<double[]> values = new ArrayList<double[]>();
	    
	    Date[]dateValues = new Date[monthcost.size()];
	    double[]costvalue = new double[monthcost.size()];
	    int i = 0;
	    double min = 0.0;
	    double max = 0.0;
	    for(Map.Entry<Date, Double>cost:monthcost.entrySet())
	    {
	    	
	    	dateValues[i] = cost.getKey();
	    	costvalue[i] = cost.getValue();
	    	if(costvalue[i]-max>=0.001)
	    		max = costvalue[i];
	    	if(min- costvalue[i]>=0.001)
	    		min = costvalue[i];
	    	i = i+1;
	    }
	    dates.add(dateValues);
        values.add(costvalue);
	  
	    int[] colors = new int[] { Color.GREEN };
	    PointStyle[] styles = new PointStyle[] { PointStyle.DIAMOND};
	    XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
	    Log.i(TAG,"the length of datevalue is:"+dateValues.length);
	    Log.i(TAG,"the mintime is:"+dateValues[0].getTime());
	    Log.i(TAG,"the maxtime is:"+dateValues[dateValues.length-1].getTime());
	    setChartSettings(renderer, "月开销", "时间", "人民币(元)", dateValues[0].getTime(),
	        dateValues[dateValues.length - 1].getTime(), min, max, Color.RED, Color.WHITE);
	    renderer.setYLabels(10);
	    return ChartFactory.getTimeChartIntent(context, buildDateDataset(titles, dates, values),
	        renderer, "MM yyyy");
		
	}


}
