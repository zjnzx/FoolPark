package cn.com.fooltech.smartparking.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cn.com.fooltech.smartparking.bean.ParkInfo;
import cn.com.fooltech.smartparking.database.DBHelper;

/**
 * Created by YY on 2016/9/14.
 */
public class SearchHisDao {
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    public SearchHisDao(Context context){
        dbHelper = new DBHelper(context);
        db = dbHelper.open();
    }

    public void insertSearchHis(ParkInfo info){
        String sql = "replace into searchhis (parkid,parkname,address,parklat,parklng,distance) values (" +
                info.getParkId() + "," +
                "'" + info.getParkName() + "'," +
                "'" + info.getParkAddress() + "'," +
                info.getParkLat() + "," +
                info.getParkLng() + "," +
                info.getDistance() +
                ")";
        db.execSQL(sql);
        db.close();
    }

    /**
     * 查询全部
     * @return
     */
    public List<ParkInfo> querySearchHis(){
        List<ParkInfo> list = new ArrayList<ParkInfo>();
        String sql = "select * from searchhis";
        Cursor cur = db.rawQuery(sql, null);
        while(cur.moveToNext()){
            ParkInfo info = new ParkInfo();
            info.setParkId(cur.getLong(cur.getColumnIndex("parkid")));
            info.setParkName(cur.getString(cur.getColumnIndex("parkname")));
            info.setParkAddress(cur.getString(cur.getColumnIndex("address")));
            info.setParkLat(cur.getFloat(cur.getColumnIndex("parklat")));
            info.setParkLng(cur.getFloat(cur.getColumnIndex("parklng")));
            info.setDistance(cur.getInt(cur.getColumnIndex("distance")));
            list.add(info);
        }
        db.close();
        cur.close();
        return list;
    }

    /**
     * 删除
     * @param
     */
    public void deleteHis(){
        String sql = "delete from searchhis";
        db.execSQL(sql);
        db.close();
    }
}
