package cn.com.fooltech.smartparking.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.activity.AccountRelatingActivity;
import cn.com.fooltech.smartparking.activity.BillDetailActivity;
import cn.com.fooltech.smartparking.activity.RefundActivity;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.bean.BindCarInfo;
import cn.com.fooltech.smartparking.utils.GuideUtils;
import cn.com.fooltech.smartparking.utils.Utils;
import cn.com.fooltech.smartparking.view.PieChartView;
import cn.com.fooltech.smartparking.wxapi.WXPayEntryActivity;

/**
 * Created by YY on 2016/7/7.
 */
public class CarManagerAdapter extends BaseAdapter {
    private Activity context;
    public List<BindCarInfo> list;
    private LayoutInflater inflater;
    GuideUtils guideUtil;
    RelativeLayout mLayout;;
//    private String[] colors = {"#e4f6e4","#b2e5b2","#67c867"};
    public CarManagerAdapter(Activity context, List<BindCarInfo> list,RelativeLayout mLayout){
        this.context = context;
        this.list = list;
        this.mLayout = mLayout;
        inflater = LayoutInflater.from(context);
        guideUtil = GuideUtils.getInstance();
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
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        final BindCarInfo info = (BindCarInfo) list.get(position);
        ViewHolder holder = null;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.listview_car_manager_item,null);
            holder = new ViewHolder();
            holder.tvPlateNum = (TextView) convertView.findViewById(R.id.plate_num_show);
            holder.tvBalance = (TextView) convertView.findViewById(R.id.balance);
//            holder.tvBalance2 = (TextView) convertView.findViewById(R.id.balance2);
//            holder.pieChartView = (PieChartView) convertView.findViewById(R.id.pie_chart);
            holder.cbOnOff = (CheckBox) convertView.findViewById(R.id.on_off);
            holder.layout = (RelativeLayout) convertView.findViewById(R.id.lay_manage1);
            holder.layoutClick = (RelativeLayout) convertView.findViewById(R.id.lay_manage);
            holder.layRecharge = (LinearLayout) convertView.findViewById(R.id.lay_recharge);
            holder.layDeposit = (LinearLayout) convertView.findViewById(R.id.lay_deposit);
            holder.layPie = (RelativeLayout) convertView.findViewById(R.id.lay_pie);
//            holder.layBalance = (RelativeLayout) convertView.findViewById(R.id.lay_balance);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvPlateNum.setText(info.getPlateNumber());

//        List<UserInfo> userInfos = info.getUsers();
//        if(userInfos != null && userInfos.size() > 1){//绑定多个人
//            List<PieChartView.PieceDataHolder> pieceDataHolders = new ArrayList<>();
//            for(int i = 0,len = userInfos.size();i < len;i++){
//                UserInfo userInfo = userInfos.get(i);
//                pieceDataHolders.add(new PieChartView.PieceDataHolder(userInfo.getBalance(), Color.parseColor(colors[i]), userInfo.getNickName() + " " + userInfo.getBalance()));
//            }
//            holder.pieChartView.setData(pieceDataHolders);
//            holder.layPie.setVisibility(View.VISIBLE);
//            holder.layBalance.setVisibility(View.GONE);
            String balance = Utils.decimalFormat((double) info.getBalance() / 100);
            holder.tvBalance.setText(balance);
//        }else{//绑定了一个人
//            holder.layPie.setVisibility(View.GONE);
//            holder.layBalance.setVisibility(View.VISIBLE);
//            holder.tvBalance2.setText(info.getBalance() / 100 + "");
//        }

        final ViewHolder finalHolder = holder;
        //显示隐藏
        holder.cbOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    finalHolder.layout.setVisibility(View.VISIBLE);
                    guideUtil.addGuideImage(context, mLayout,R.drawable.ic_guide_4,"guide_amount");
                }else{
                    finalHolder.layout.setVisibility(View.GONE);
                }
            }
        });

        holder.layoutClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.isUpdateCar = false;
                Intent intent = new Intent(context,AccountRelatingActivity.class);
                intent.putExtra("plateNumber",info.getPlateNumber());
                intent.putExtra("balance",info.getBalance());
                intent.putExtra("count",list.size());
                context.startActivity(intent);
            }
        });

        //充值
        holder.layRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int balance = list.get(position).getBalance();
                Intent intent = new Intent(context, WXPayEntryActivity.class);
                intent.putExtra("balance",balance);
                intent.putExtra("carId",info.getCarId());
                intent.putExtra("plateNumber",info.getPlateNumber());
                context.startActivity(intent);
            }
        });
        //提现
        holder.layDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int balance = list.get(position).getBalance();
                Intent intent = new Intent(context, RefundActivity.class);
                intent.putExtra("balance",balance);
                intent.putExtra("carId",info.getCarId());
                context.startActivity(intent);
            }
        });

        //账单详情
//        holder.layBalance.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(context, BillDetailActivity.class);
//                intent.putExtra("carId",info.getCarId());
//                context.startActivity(intent);
//            }
//        });
        //账单详情
        holder.layPie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BillDetailActivity.class);
                intent.putExtra("carId",info.getCarId());
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    class ViewHolder{
        TextView tvPlateNum,tvBalance,tvBalance2;
        CheckBox cbOnOff;
        PieChartView pieChartView;
        RelativeLayout layout,layoutClick,layBalance,layPie;
        LinearLayout layRecharge,layDeposit;

    }
}
