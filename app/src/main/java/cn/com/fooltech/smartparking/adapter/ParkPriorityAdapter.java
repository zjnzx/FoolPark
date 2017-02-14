package cn.com.fooltech.smartparking.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.activity.ParkPriorityActivity;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.bean.ParkInfo;
import cn.com.fooltech.smartparking.common.Common;
import cn.com.fooltech.smartparking.utils.Utils;

/**
 * Created by YY on 2016/7/7.
 */
public class ParkPriorityAdapter extends BaseAdapter {
    private ParkPriorityActivity context;
    private ArrayList<ParkInfo> list;
    private LayoutInflater inflater;
    public ParkPriorityAdapter(ParkPriorityActivity context, ArrayList<ParkInfo> list){
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
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
        final ParkInfo info = (ParkInfo) list.get(i);
        ViewHolder holder = null;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.listview_prio_park_list_item,null);
            holder = new ViewHolder();
            holder.tvName = (TextView) convertView.findViewById(R.id.park_name_pri);
            holder.tvAddr = (TextView) convertView.findViewById(R.id.park_addr_pri);
            holder.tvDist = (TextView) convertView.findViewById(R.id.distance_pri);
            holder.layNavi = (LinearLayout) convertView.findViewById(R.id.lay_navi);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvName.setText((i + 1) + "." + info.getParkName());
        holder.tvAddr.setText(info.getParkAddress());
        int distance = (int) Common.GetLongDistance(MyApplication.longtitude,MyApplication.latitude,info.getParkLng(),info.getParkLat());
        holder.tvDist.setText(distance > 1000 ? Utils.decimalFormat1((double)distance / 1000) + "km" : distance + "m");

        //定位
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent();
                mIntent.putExtra("info",info);
                // 设置结果，并进行传送
                context.setResult(1, mIntent);
                context.finish();
            }
        });
        //导航
        holder.layNavi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startNavi(info);
            }
        });

        return convertView;
    }

    class ViewHolder{
        TextView tvAddr,tvDist,tvName;
        LinearLayout layNavi;
    }
}
