package cn.com.fooltech.smartparking.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.List;

public class DBHelper extends SQLiteOpenHelper{
	private static final String TAG = "SQLite";
	private static final String DATABASE_NAME = "fooltech.db";
	private static final int VERSION = 1; 
	private SQLiteDatabase db;

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, VERSION);
		// TODO Auto-generated constructor stub
	}
	
	public SQLiteDatabase open() {
		if (db == null || !db.isOpen()) {
			db = this.getWritableDatabase();
		}
		return db;
	}
	
	public void close() {
		if (db != null && db.isOpen()) {
			db.close();
		}
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
//		Log.i(TAG, "create Database----111111111--------->");
		String sql1 = "create table banner(bannerid long primary key not null,bannerimage varchar(200),imageorder int,type int,linkurl varchar(200))";
		String sql2 = "create table searchhis(parkid long primary key not null,parkname varchar(200),address varchar(200),parklat real,parklng real,distance int)";
		db.execSQL(sql1);
		db.execSQL(sql2);
//		Log.i(TAG, "create Database------33333333------->");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

	public void inertDataList(List<String> sqls) {
		SQLiteDatabase db = open();
		db.beginTransaction();
		try {
			for (String sql : sqls) {
				db.execSQL(sql);
			}
			// 设置事务标志为成功，当结束事务时就会提交事务
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 结束事务
			db.endTransaction();
			db.close();
			}
	}

}
