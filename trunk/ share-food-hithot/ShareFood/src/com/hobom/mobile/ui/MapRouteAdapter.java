package com.hobom.mobile.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hobom.mobile.R;

class MapRouteAdapter extends BaseAdapter {

    Context context;
    
    String[] texts = {"使用我的位置", "在地图上选取"};
    int[] iconIds = {R.drawable.icon_myloc, R.drawable.icon_mapselected};
    
    public MapRouteAdapter(Context context){
        this.context = context;
    }
    
    
    
    @Override
    public int getCount() {
        
        return texts.length;
    }

    @Override
    public Object getItem(int position) {
        
        return null;
    }

    @Override
    public long getItemId(int position) {
        
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
        TextView textView = new TextView(context);
        textView.setText(texts[position]);
        
        textView.setTextSize(18);   
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(   
                LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
        
        textView.setLayoutParams(layoutParams);   
        textView.setGravity(android.view.Gravity.CENTER_VERTICAL);   
        textView.setHeight(80);
        textView.setTextColor(Color.BLACK);     
        textView.setCompoundDrawablesWithIntrinsicBounds(iconIds[position], 0, 0, 0);   
        textView.setPadding(15, 0, 15, 0);   
        //设置文字和图标之间的padding大小   
        textView.setCompoundDrawablePadding(15);
        
        return textView;
    }
    
}
