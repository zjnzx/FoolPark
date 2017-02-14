package cn.com.fooltech.smartparking.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.bean.TradeInfo;
import cn.com.fooltech.smartparking.utils.DateUtils;
import cn.com.fooltech.smartparking.utils.Utils;

/**
 * Created by YY on 2016/7/7.
 */
public class BillDetailAdapter extends BaseAdapter {
    private Context context;
    public List<TradeInfo> list;
    private LayoutInflater inflater;
    public BillDetailAdapter(Context context, List<TradeInfo> list){
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
        TradeInfo info = (TradeInfo) list.get(i);
        ViewHolder holder = null;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.listview_bill_detail_item,null);
            holder = new ViewHolder();
            holder.tvNickName = (TextView) convertView.findViewById(R.id.nick_name_show4);
            holder.tvAmount = (TextView) convertView.findViewById(R.id.amount_show1);
            holder.tvDate = (TextView) convertView.findViewById(R.id.date_bill);
            holder.tvTradeType = (TextView) convertView.findViewById(R.id.trade_type);
            holder.tvOrderNo = (TextView) convertView.findViewById(R.id.order_num_show);
            holder.tvResult = (TextView) convertView.findViewById(R.id.bill_result);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        String tradeAmount = Utils.decimalFormat((double) info.getTradeAmount() / 100);
        holder.tvAmount.setText("￥" + tradeAmount + "元");
        holder.tvNickName.setText(info.getNickName() + info.getMobile());
        holder.tvDate.setText(DateUtils.getTime2(info.getTradeTime()));
        holder.tvTradeType.setText(info.getTradeRemark());
        holder.tvOrderNo.setText("账单号: " + info.getOrderNo());
        holder.tvResult.setText(getTypeStr(info));
        return convertView;
    }

    class ViewHolder{
        TextView tvNickName,tvOrderNo,tvDate,tvTradeType,tvAmount,tvResult;
    }

    private String getTypeStr(TradeInfo info) {
        String typeStr = "";
        ForegroundColorSpan colorSpan = null;
        if (info.getTradeType() == 1) {
            typeStr = "充值成功";
//            colorSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.green));
        } else if (info.getTradeType() == 2) {
            typeStr = "提现成功";
//            colorSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.red2));
        } else if (info.getTradeType() == 3) {
            typeStr = "缴费成功";
//            colorSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.red2));
        }else if (info.getTradeType() == 4) {
            typeStr = "返还成功";
//            colorSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.red2));
        }
//        String content = typeStr + info.getTradeAmount() / 100 + ".00元";
//        int index = content.indexOf("元");
//        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(content);
//        stringBuilder.setSpan(colorSpan, 2, index, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return typeStr;
    }

}
