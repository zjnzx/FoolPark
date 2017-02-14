package cn.com.fooltech.smartparking.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.activity.CardBuyActivity;
import cn.com.fooltech.smartparking.activity.CardBuyActivity2;
import cn.com.fooltech.smartparking.activity.ParkPriorityActivity;
import cn.com.fooltech.smartparking.bean.ParkInfo;

/**
 * Created by YY on 2016/7/7.
 */
public class CardBuyAdapter extends BaseAdapter {
    private CardBuyActivity context;
    private List<ParkInfo> list;
    private LayoutInflater inflater;
    public CardBuyAdapter(CardBuyActivity context, List<ParkInfo> list){
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
            convertView = inflater.inflate(R.layout.listview_card_buy_item,null);
            holder = new ViewHolder();
            holder.tvName = (TextView) convertView.findViewById(R.id.park_name_show4);
            holder.tvAddr = (TextView) convertView.findViewById(R.id.park_addr_show4);
            holder.tvBuy = (TextView) convertView.findViewById(R.id.card_buy);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvName.setText((i + 1) + "." + info.getParkName());
        holder.tvAddr.setText(info.getParkAddress());

        //购买月卡
        holder.tvBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CardBuyActivity2.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("parkInfo",info);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    class ViewHolder{
        TextView tvAddr,tvName,tvBuy;
    }
}
