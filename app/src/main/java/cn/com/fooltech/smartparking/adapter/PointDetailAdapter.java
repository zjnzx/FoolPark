package cn.com.fooltech.smartparking.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.List;

import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.bean.PointInfo;
import cn.com.fooltech.smartparking.utils.DateUtils;

/**
 * Created by YY on 2016/7/7.
 */
public class PointDetailAdapter extends BaseAdapter {
    private Context context;
    public List<PointInfo> list;
    private LayoutInflater inflater;
    public PointDetailAdapter(Context context, List<PointInfo> list){
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
        PointInfo info = (PointInfo) list.get(i);
        ViewHolder holder = null;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.listview_point_detail_item,null);
            holder = new ViewHolder();
            holder.tvPointDec = (TextView) convertView.findViewById(R.id.point_declare);
            holder.tvPointDate = (TextView) convertView.findViewById(R.id.point_date);
            holder.tvPointCount = (TextView) convertView.findViewById(R.id.point_count);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvPointDec.setText(info.getPointsRemark());
        holder.tvPointDate.setText(DateUtils.getDate3(info.getUpdateTime()));
        holder.tvPointCount.setText(getSign(info.getPointsType(),holder.tvPointCount) + info.getPointsNum());
        return convertView;
    }

    class ViewHolder{
        TextView tvPointDec,tvPointDate,tvPointCount;
    }

    private String getSign(int type,TextView count){
        if(type == 1){ //收入
            count.setTextColor(context.getResources().getColor(R.color.green));
            return "+";
        }else if(type == 2){ //支出
            count.setTextColor(context.getResources().getColor(R.color.red));
            return "-";
        }
        return "";
    }

}
