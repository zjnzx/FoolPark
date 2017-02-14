package cn.com.fooltech.smartparking.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cn.com.fooltech.smartparking.bean.BannerInfo;
import cn.com.fooltech.smartparking.database.DBHelper;

/**
 * Created by YY on 2016/9/14.
 */
public class BannerDao {
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    public BannerDao(Context context){
        dbHelper = new DBHelper(context);
        db = dbHelper.open();
    }

    public void insertBannerInfo(List<BannerInfo> list,int type){
        if(list != null && list.size() > 0){
            List<String> sqlList = new ArrayList<String>();
            for(BannerInfo info : list){
                String sql = "replace into banner (bannerid,bannerimage,imageorder,type,linkurl) values (" +
                        info.getBannerId() + "," +
                        "'" + info.getBannerImage() + "'," +
                        info.getImageOrder() + "," +
                        type + "," +
                        "'" + info.getLinkUrl() + "'" +
                        ")";
                sqlList.add(sql);
            }
            dbHelper.inertDataList(sqlList);
        }
    }

    /**
     * 查询全部
     * @return
     */
    public List<BannerInfo> queryBannerInfo(int type){
        List<BannerInfo> list = new ArrayList<BannerInfo>();
        String sql = "select * from banner where type = " + type + " order by imageorder asc";
        Cursor cur = db.rawQuery(sql, null);
        while(cur.moveToNext()){
            BannerInfo stu = new BannerInfo();
            stu.setBannerId(cur.getLong(cur.getColumnIndex("bannerid")));
            stu.setBannerImage(cur.getString(cur.getColumnIndex("bannerimage")));
            stu.setImageOrder(cur.getInt(cur.getColumnIndex("imageorder")));
            stu.setLinkUrl(cur.getString(cur.getColumnIndex("linkurl")));
            list.add(stu);
        }
        db.close();
        cur.close();
        return list;
    }

    /**
     * 删除
     * @param
     */
    public void deleteByType(int type){
        String sql = "delete from banner where type = " + type;
        db.execSQL(sql);
//        db.close();
    }
}
