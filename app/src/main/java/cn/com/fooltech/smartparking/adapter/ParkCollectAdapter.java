package cn.com.fooltech.smartparking.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManager;

import java.util.List;

import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.activity.BookOrCollectActivity;
import cn.com.fooltech.smartparking.activity.ParkMapActivity;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.bean.ParkCollectInfo;
import cn.com.fooltech.smartparking.common.Common;
import cn.com.fooltech.smartparking.utils.NetUtils;

/**
 * Created by YY on 2016/7/7.
 */
public class ParkCollectAdapter extends BaseAdapter {
    private BookOrCollectActivity context;
    public List<ParkCollectInfo> list;
    private LayoutInflater inflater;
    public ParkCollectAdapter(BookOrCollectActivity context, List<ParkCollectInfo> list){
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
        final ParkCollectInfo info = (ParkCollectInfo) list.get(i);
        ViewHolder holder = null;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.listview_park_collect_item,null);
            holder = new ViewHolder();
            holder.tvParkName = (TextView) convertView.findViewById(R.id.park_name_show2);
            holder.tvParkAddr = (TextView) convertView.findViewById(R.id.park_addr_show2);
            holder.tvNavi = (ImageView) convertView.findViewById(R.id.navi);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvParkName.setText(info.getParkName());
        holder.tvParkAddr.setText(info.getParkAddress());

        //定位
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,ParkMapActivity.class);
                intent.putExtra("parkInfo",info);
                context.startActivity(intent);
            }
        });
        //导航
        holder.tvNavi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startNavi(info);
            }
        });
        return convertView;
    }

    class ViewHolder{
        TextView tvParkName,tvParkAddr;
        ImageView tvNavi;
    }

}
