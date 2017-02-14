package cn.com.fooltech.smartparking.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.activity.ParkMapActivity;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.bean.ParkInfo;
import cn.com.fooltech.smartparking.common.Common;
import cn.com.fooltech.smartparking.dao.SearchHisDao;
import cn.com.fooltech.smartparking.utils.Utils;
import cn.com.fooltech.smartparking.view.CustomTextView;

/**
 * Created by YY on 2016/7/7.
 */
public class ParkInfoSearchAdapter extends BaseAdapter {
    private ParkMapActivity context;
    private List<ParkInfo> list;
    private LayoutInflater inflater;
    private String keyWork;
    private SearchHisDao searchHisDao;
    public ParkInfoSearchAdapter(ParkMapActivity context,List<ParkInfo> list){
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }
    public void setKerWord(String keyWork){
        this.keyWork = keyWork;
    }
    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        final ParkInfo info = list.get(i);
        ViewHolder holder = null;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.listview_search_item,null);
            holder = new ViewHolder();
            holder.tvName = (CustomTextView) convertView.findViewById(R.id.sear_name);
            holder.tvAddr = (TextView) convertView.findViewById(R.id.sear_addr);
            holder.tvDist = (TextView) convertView.findViewById(R.id.sear_dist);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvName.setSpecifiedTextsColor((i + 1) + "." + info.getParkName(), keyWork, context.getResources().getColor(R.color.green));//关键字变色
        holder.tvAddr.setText(info.getParkAddress());
        int distance = (int) Common.GetLongDistance(MyApplication.longtitude,MyApplication.latitude,info.getParkLng(),info.getParkLat());
        holder.tvDist.setText(distance > 1000 ? Utils.decimalFormat1((double)distance / 1000) + "km" : distance + "m");

        //点击定位
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //存入数据库
                SearchHisDao searchHisDao = new SearchHisDao(context);
                searchHisDao.insertSearchHis(info);
                context.locationSearch(info);
            }
        });
        return convertView;
    }

    class ViewHolder{
        TextView tvAddr,tvDist;
        CustomTextView tvName;
    }
}
