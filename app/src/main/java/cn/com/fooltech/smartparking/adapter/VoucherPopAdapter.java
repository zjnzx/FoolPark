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
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.bean.VoucherInfo;
import cn.com.fooltech.smartparking.utils.DateUtils;

/**
 * Created by YY on 2016/7/7.
 */
public class VoucherPopAdapter extends BaseAdapter {
    private Context context;
    public List<VoucherInfo> list;
    private LayoutInflater inflater;
    private Date date;
    public VoucherPopAdapter(Context context, List<VoucherInfo> list){
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
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        final VoucherInfo info = (VoucherInfo) list.get(i);
        ViewHolder holder = null;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.listview_voucher_pop_item,null);
            holder = new ViewHolder();
            holder.tvTimeLimit = (TextView) convertView.findViewById(R.id.time_limit);
            holder.ivPrice = (ImageView) convertView.findViewById(R.id.privilege_price);
            holder.ivChecked = (ImageView) convertView.findViewById(R.id.voucher_checked);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvTimeLimit.setText("有效日期至:  " + info.getExpiryDate());
        holder.ivChecked.setVisibility(isCheck(info) == true ? View.VISIBLE : View.GONE);

        if(MyApplication.isUse){ //解决下次进来不能单选的问题
            if(i == MyApplication.voucherMap.get("position")) {
                MyApplication.lastImgVoucher = holder.ivChecked;
            }
        }

        final ViewHolder finalHolder = holder;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(MyApplication.lastImgVoucher != null)
                    MyApplication.lastImgVoucher.setVisibility(View.GONE);

                finalHolder.ivChecked.setVisibility(View.VISIBLE);
                MyApplication.voucherMap.put("voucherId",info.getVoucherId());
                MyApplication.voucherMap.put("position", (long) i);
                MyApplication.lastImgVoucher = finalHolder.ivChecked;
            }
        });
        return convertView;
    }

    private boolean isCheck(VoucherInfo info){
        if(MyApplication.voucherMap != null && MyApplication.voucherMap.size() > 0){
            long voucherId = MyApplication.voucherMap.get("voucherId");
            if(voucherId == info.getVoucherId()){
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }

    class ViewHolder{
        TextView tvTimeLimit,tvStatus;
        ImageView ivPrice,ivChecked;
    }

}
