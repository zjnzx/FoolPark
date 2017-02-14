package cn.com.fooltech.smartparking.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.bean.ExchangeRecordInfo;
import cn.com.fooltech.smartparking.bean.PointInfo;
import cn.com.fooltech.smartparking.bean.PositionRecordInfo;
import cn.com.fooltech.smartparking.utils.DateUtils;

/**
 * Created by YY on 2016/7/7.
 */
public class ExchangeRecordAdapter extends BaseAdapter {
    private Context context;
    public List<ExchangeRecordInfo> list;
    private LayoutInflater inflater;
    public ExchangeRecordAdapter(Context context, List<ExchangeRecordInfo> list){
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }
    public void addData(ExchangeRecordInfo info){
        list.add(0,info);
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
        ExchangeRecordInfo info = (ExchangeRecordInfo) list.get(i);
        ViewHolder holder = null;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.listview_exchange_record_item,null);
            holder = new ViewHolder();
            holder.tvContent = (TextView) convertView.findViewById(R.id.exchange_content);
            holder.tvDate = (TextView) convertView.findViewById(R.id.exchange_date);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvContent.setText(info.getExchangeInfo());
        holder.tvDate.setText(info.getExchangeDate());
        return convertView;
    }

    class ViewHolder{
        TextView tvContent,tvDate;
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
