package com.hobom.mobile.db;

import com.hobom.mobile.db.DatabaseColumns.ConsumeColumn;
import com.hobom.mobile.db.DatabaseColumns.FoodColumn;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseAccessor {
	private static final String TAG = "[DatabaseAccessor]";
    private static final String DATABASENAME ="food.db";
	private final static int VERSION = 1;
	private DatabaseHelper dbHelper = null;
	private SQLiteDatabase db = null;
	
	public interface Tables{
    	String FOOD ="food";
    	String CONSUME ="consume";
    }

	          
	final String CREATE_FOOD =
		"CREATE TABLE IF NOT EXISTS " + Tables.FOOD
		+ " (" + FoodColumn._ID +" INTEGER PRIMARY KEY,"
		+ FoodColumn.NAME +  "TEXT ,"
		+ FoodColumn.ADDRESS + " TEXT ,"
		+ FoodColumn.LAT + " INTEGER ,"
		+ FoodColumn.LON + " INTEGER ,"
		+ FoodColumn.PRICE + " TEXT ,"
		+ FoodColumn.RATING + " INTEGER ,"
		+ FoodColumn.PICPATH + " TEXT)"
		
		;
	
	final String CREATE_CONSUME = 
		" CREATE TABLE IF NOT EXISTS "+ Tables.CONSUME
		+ " (" +ConsumeColumn._ID +" INTEGER PRIMARY KEY,"
		+ ConsumeColumn.FOODID + " INTEGER ,"
		+ ConsumeColumn.DATE +" INTEGER ,"
	    + ConsumeColumn.COMMENT +" TEXT )"
	    ;
	
	public DatabaseAccessor(Context context) {
		
		dbHelper = new DatabaseHelper(context);
	}

	private class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DATABASENAME, null, VERSION);

		}

		@Override
		public void onCreate(SQLiteDatabase db) {
		
			db.execSQL(CREATE_FOOD);
			db.execSQL(CREATE_CONSUME);
			
			
			

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
            db.execSQL("drop table if exists " + Tables.FOOD);
            db.execSQL("drop table if exists " + Tables.CONSUME);
			onCreate(db);
		

		}

	}
	
	/**
	 * get the database
	 * 
	 * @return
	 */
	private SQLiteDatabase getDB() {

		if (db == null || !db.isOpen()) {

			try {
				db = dbHelper.getWritableDatabase();
			} catch (Exception e) {
				db = dbHelper.getReadableDatabase();
			}
		}
		return db;
	}

	public boolean isClose() {
		if (db == null) {
			return true;
		}
		return !db.isOpen();
	}

	public void closeDB() {
		if (db != null) {
			db.close();
		}
		if (dbHelper != null) {
			dbHelper.close();
		}

	}
	
	/**
	 * 
	 * @param sql
	 */
	public void execSQL(String sql) {
		getDB().execSQL(sql);
	}

	/**
	 * 
	 * @param sql
	 * @param selectionArgs
	 * @return
	 */
	public Cursor rawQuery(String sql, String[] selectionArgs) {
		return getDB().rawQuery(sql, selectionArgs);
	}

	/**
	 * insert
	 * 
	 * @param table
	 * @param nullColumnHack
	 * @param initialValues
	 * @return
	 */
	public long insert(String table, String nullColumnHack,
			ContentValues initialValues) {
		return getDB().insert(table, nullColumnHack, initialValues);

	}
	/**
	 * 
	 * @param table
	 * @param values
	 * @param whereClause
	 * @param whereArgs
	 * @return
	 */
	public int update(String table, ContentValues values, String whereClause,
			String[] whereArgs) {

		return getDB().update(table, values, whereClause, whereArgs);

	}
	
	public void insertOrUpdate(String table,String nullColumnHack, ContentValues values, String whereClause,
			String[] whereArgs){
		Log.i(TAG,"insert or update");
		Cursor cursor = query(true,table,null,whereClause,whereArgs,null,null,null,null);
		if(cursor.moveToFirst()){
			Log.i(TAG,"update");
			update(table,values,whereClause,whereArgs);
			
		}else{
			Log.i(TAG,"insert");
			insert(table,nullColumnHack,values);
		}
		if(cursor!=null){
			cursor.close();
			cursor = null;
		}
		
	}
	/**
	 * delete the rows in table
	 * 
	 * @param table
	 * @param whereClause
	 * @param whereArgs
	 * @return
	 */
	public int delete(String table, String whereClause, String[] whereArgs) {
		return getDB().delete(table, whereClause, whereArgs);

	}

	/**
	 * 
	 * @param distinct
	 * @param table
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @param limit
	 * @return
	 */
	public Cursor query(boolean distinct, String table, String[] columns,
			String selection, String[] selectionArgs, String groupBy,
			String having, String orderBy, String limit) {
		Log.i(TAG,"query");
		return getDB().query(distinct, table, columns, selection,
				selectionArgs, groupBy, having, orderBy, limit);
	}

	
}
