package cn.com.fooltech.smartparking.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
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
import cn.com.fooltech.smartparking.bean.CardInfo;
import cn.com.fooltech.smartparking.bean.VoucherInfo;

/**
 * Created by YY on 2016/7/7.
 */
public class CardMyAdapter extends BaseAdapter {
    private Context context;
    public List<CardInfo> list;
    private LayoutInflater inflater;
    public CardMyAdapter(Context context, List<CardInfo> list){
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
        CardInfo info = (CardInfo) list.get(i);
        ViewHolder holder = null;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.listview_card_item,null);
            holder = new ViewHolder();
            holder.tvTimeLimit = (TextView) convertView.findViewById(R.id.time_limit_card2);
            holder.tvParkName = (TextView) convertView.findViewById(R.id.park_name1);
            holder.tvPlateNum = (TextView) convertView.findViewById(R.id.plate_num1);
            holder.tvParkPrice = (TextView) convertView.findViewById(R.id.park_price);
            holder.layBg = (RelativeLayout) convertView.findViewById(R.id.bg_card);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        String plateNum = info.getPlateNumber();
        String province = "";
        if(plateNum != null && !plateNum.isEmpty()){
            province = plateNum.substring(0,2);
            plateNum = plateNum.substring(2);
        }

        String price = "￥" + info.getCardPrice() / 100;
        Spannable spanText1 = new SpannableString(price);
        spanText1.setSpan(new AbsoluteSizeSpan(24,true), 1, price.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        holder.tvParkName.setText(info.getParkName());
        holder.tvParkPrice.setText(spanText1);
        holder.tvPlateNum.setText(province + " · " + plateNum);
        setBackGround(info.getCardType(),holder.layBg);
        if(info.getIsVerified() == 0) { //未通过审核
            holder.tvTimeLimit.setText("生效日期以客服核定后为准");
        }else if(info.getIsVerified() == 1) { //通过审核
            holder.tvTimeLimit.setText("有效日期至:  " + info.getToDate());
        }
        return convertView;
    }

    class ViewHolder{
        TextView tvTimeLimit,tvParkName,tvPlateNum,tvParkPrice;
        RelativeLayout layBg;
    }

    private void setBackGround(int cardType,RelativeLayout layBg){
        if(cardType == 1){
            layBg.setBackgroundResource(R.drawable.card_month2);
        }else if(cardType == 2){
            layBg.setBackgroundResource(R.drawable.card_quarter2);
        }else if(cardType == 3){
            layBg.setBackgroundResource(R.drawable.card_year2);
        }
    }
}
