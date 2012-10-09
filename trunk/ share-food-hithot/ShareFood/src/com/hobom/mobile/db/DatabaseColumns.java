package com.hobom.mobile.db;

import android.provider.BaseColumns;

/**
 * ���ݿ��
 * @author mingxunzh
 *
 */

public class DatabaseColumns {

	/**
	 * ʳ���
	 * 
	 *
	 */
	public interface FoodColumn  extends BaseColumns{
		
		final static String NAME ="foodname";
		final static String ADDRESS = "address";
		final static String LAT  ="latitude";
		final static String LON ="longitude";
		final static String TYPE ="type";
		final static String PRICE ="price";
		final static String TEL = "tel";
	
		
	}
	/**
	 * ���ѱ�
	 * 
	 *
	 */
	public interface ConsumeColumn extends BaseColumns{
		final static String FOODID ="foodId";
		final static String DATE = "date";
		final static String COMMENT  ="remark";
		final static String PICPATH ="path";
		final static String  RATING =  "rating";
	}
}
