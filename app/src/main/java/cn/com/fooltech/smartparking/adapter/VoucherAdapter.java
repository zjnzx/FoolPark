package cn.com.fooltech.smartparking.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.bean.VoucherInfo;
import cn.com.fooltech.smartparking.utils.DateUtils;

/**
 * Created by YY on 2016/7/7.
 */
public class VoucherAdapter extends BaseAdapter {
    private Context context;
    public List<VoucherInfo> list;
    private LayoutInflater inflater;
    private Date date;
    public VoucherAdapter(Context context, List<VoucherInfo> list){
        this.context = context;
        this.list = list;
        date = new Date();
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
        VoucherInfo info = (VoucherInfo) list.get(i);
        ViewHolder holder = null;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.listview_voucher_item,null);
            holder = new ViewHolder();
            holder.tvTimeLimit = (TextView) convertView.findViewById(R.id.time_limit);
            holder.tvStatus = (TextView) convertView.findViewById(R.id.status);
            holder.layBg = (RelativeLayout) convertView.findViewById(R.id.voucher_bg);
            holder.ivStale = (ImageView) convertView.findViewById(R.id.icon_stale);
            holder.ivPrice = (ImageView) convertView.findViewById(R.id.privilege_price);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvTimeLimit.setText("有效日期至:  " + info.getExpiryDate());
        holder.tvStatus.setText(getStatus(info.getVoucherStatus(),holder.layBg,holder.ivStale));
        return convertView;
    }

    class ViewHolder{
        TextView tvTimeLimit,tvStatus;
        RelativeLayout layBg;
        ImageView ivStale,ivPrice;
    }

    private String getStatus(String status,RelativeLayout layBg,ImageView imageView){
        if(status.equals("0")){
            layBg.setBackgroundResource(R.drawable.voucher_unuse);
            imageView.setVisibility(View.GONE);
            return "未使用";
        }else if(status.equals("1")){
            layBg.setBackgroundResource(R.drawable.voucher_stale);
            imageView.setVisibility(View.GONE);
            return "已使用";
        }else if(status.equals("2")){
            layBg.setBackgroundResource(R.drawable.voucher_stale);
            imageView.setVisibility(View.VISIBLE);
            return "已过期";
        }
        return "";
    }
}
