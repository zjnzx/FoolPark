package cn.com.fooltech.smartparking.adapter;

import android.content.Context;
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
import cn.com.fooltech.smartparking.activity.PositionRecordActivity;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.bean.ParkInfo;
import cn.com.fooltech.smartparking.bean.PositionRecordInfo;
import cn.com.fooltech.smartparking.common.Common;
import cn.com.fooltech.smartparking.utils.DateUtils;
import cn.com.fooltech.smartparking.utils.Utils;

/**
 * Created by YY on 2016/7/7.
 */
public class PositionRecordAdapter extends BaseAdapter {
    private PositionRecordActivity context;
    public List<PositionRecordInfo> list;
    private LayoutInflater inflater;
    public PositionRecordAdapter(PositionRecordActivity context, List<PositionRecordInfo> list){
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    public void removeData(int position){
        list.remove(position);
        notifyDataSetChanged();
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
        final PositionRecordInfo info = (PositionRecordInfo) list.get(i);
        ViewHolder holder = null;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.listview_position_record_item,null);
            holder = new ViewHolder();
            holder.tvName = (TextView) convertView.findViewById(R.id.park_name_show3);
            holder.tvAddr = (TextView) convertView.findViewById(R.id.park_addr_show3);
            holder.tvDist = (TextView) convertView.findViewById(R.id.distance_show1);
            holder.tvTime = (TextView) convertView.findViewById(R.id.record_time);
            holder.tvlabel = (TextView) convertView.findViewById(R.id.space_label);
            holder.tvFloor = (TextView) convertView.findViewById(R.id.space_floor);
            holder.layFloor = (LinearLayout) convertView.findViewById(R.id.lay_navi1);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        int distance = (int) Common.GetLongDistance(MyApplication.longtitude,MyApplication.latitude,info.getSpaceLng(),info.getSpaceLat());
        holder.tvName.setText(info.getParkName());
        holder.tvAddr.setText(info.getParkAddress());
        holder.tvDist.setText(distance > 1000 ? Utils.decimalFormat1((double)distance / 1000) + "km" : distance + "m");
        holder.tvTime.setText(DateUtils.getTime4(info.getRecordTime()));
        holder.tvlabel.setText(info.getSpaceLabel());
        holder.tvFloor.setText(info.getSpaceFloor());

        //导航
        holder.layFloor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startNavi(info);
            }
        });

        return convertView;
    }

    class ViewHolder{
        TextView tvAddr,tvDist,tvName,tvTime,tvFloor,tvlabel;
        LinearLayout layFloor;
    }
}
