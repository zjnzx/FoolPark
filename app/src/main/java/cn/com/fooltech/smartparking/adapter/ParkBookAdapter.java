package cn.com.fooltech.smartparking.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.baidu.mapapi.model.LatLng;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManager;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.activity.BookOrCollectActivity;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.bean.ParkBookInfo;
import cn.com.fooltech.smartparking.common.Common;
import cn.com.fooltech.smartparking.common.Urls;
import cn.com.fooltech.smartparking.utils.DateUtils;
import cn.com.fooltech.smartparking.utils.ErrorUtils;
import cn.com.fooltech.smartparking.utils.HttpUtils;
import cn.com.fooltech.smartparking.utils.JsonUtils;
import cn.com.fooltech.smartparking.utils.NetUtils;
import cn.com.fooltech.smartparking.utils.SPUtils;
import cn.com.fooltech.smartparking.utils.TimerUtils;
import cn.com.fooltech.smartparking.utils.ToastUtils;
import cn.com.fooltech.smartparking.utils.Utils;
import cn.com.fooltech.smartparking.view.ClockView;

/**
 * Created by YY on 2016/7/7.
 */
public class ParkBookAdapter extends BaseAdapter {
    private BookOrCollectActivity context;
    public List<ParkBookInfo> list;
    private LayoutInflater inflater;
    private ParkBookInfo parkBookInfo;
    private long serviceTime;
    public ParkBookAdapter(BookOrCollectActivity context, List<ParkBookInfo> list){
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    public void setServiceTime(String serviceTime){
        this.serviceTime = DateUtils.getMillions(serviceTime);
    }
    public void removeData(int position){
        list.remove(position);
        notifyDataSetChanged();
    }

    public void updateStatus(ParkBookInfo info){
        info.setBookStatus(0);
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
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        final ParkBookInfo info = (ParkBookInfo) list.get(i);
        ViewHolder holder = null;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.listview_park_book_item,null);
            holder = new ViewHolder();
            holder.tvParkName = (TextView) convertView.findViewById(R.id.park_name_show);
            holder.tvParkAddr = (TextView) convertView.findViewById(R.id.park_addr_show);
            holder.tvDistance = (TextView) convertView.findViewById(R.id.distance_show);
            holder.tvPlateNum = (TextView) convertView.findViewById(R.id.plate_num_show4);
            holder.tvStatus = (TextView) convertView.findViewById(R.id.status_show);
            holder.tvBookTime = (TextView) convertView.findViewById(R.id.book_time);
            holder.tvTiming = (ClockView) convertView.findViewById(R.id.timing);
            holder.tvCancle = (TextView) convertView.findViewById(R.id.cancle_book);
            holder.layBook = (RelativeLayout) convertView.findViewById(R.id.lay_book2);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvTiming.setEndTime(((3600 * 1000) - (serviceTime - info.getBookTime().getTime())) / 1000 ,convertView);//定时一小时

        String plateNum = info.getPlateNumber();
        String province = "";
        if(plateNum != null && !plateNum.isEmpty()){
            province = plateNum.substring(0,2);
            plateNum = plateNum.substring(2);
        }

        if(info.getBookStatus() == 1){ //未入场
            holder.layBook.setVisibility(View.VISIBLE);
        }else {
            holder.layBook.setVisibility(View.GONE);
        }
        int distance = (int) Common.GetLongDistance(MyApplication.longtitude,MyApplication.latitude,info.getParkLng(),info.getParkLat());

        holder.tvPlateNum.setText(province + " · " + plateNum);
        holder.tvDistance.setText(distance > 1000 ? Utils.decimalFormat1((double)distance / 1000) + "km" : distance + "m");
        holder.tvStatus.setText(getStatus(info.getBookStatus()));
        holder.tvParkName.setText(info.getParkName());
        holder.tvParkAddr.setText(info.getParkAddress());
        holder.tvBookTime.setText(DateUtils.getTime1(info.getBookTime()));
        //取消预约
        holder.tvCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parkBookInfo = info;
                showCancleDialog();
            }
        });

        //导航
        holder.tvDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startNavi(info);
            }
        });

        return convertView;
    }

    class ViewHolder{
        TextView tvParkName,tvParkAddr,tvDistance,tvPlateNum,tvStatus,tvBookTime,tvCancle;
        ClockView tvTiming;
        RelativeLayout layBook;
    }

    Handler handlerCancle = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, "服务器出现异常");
            }else {
                int code = JsonUtils.getCodeResult(msg.obj.toString());
                if (code == 0) {
                    ToastUtils.showShort(context, "取消预约成功");
                    updateStatus(parkBookInfo);
                } else {
                    ErrorUtils.errorCode(context, code);
                }
            }
            super.handleMessage(msg);
        }
    };

    private String getStatus(int bookStatus){
        String status = "";
        if(bookStatus == 0){
            status = "已取消";
        }else if(bookStatus == 1){
            status = "未入场";
        }else if(bookStatus == 2){
            status = "已入场";
        }else if(bookStatus == 3){
            status = "已超时";
        }
        return status;
    }

    /**
     * 显示取消对话框
     */
    private void showCancleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true)
                .setMessage(context.getResources().getString(R.string.dialog_cancle_book))
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Map<String,Object> paramMap = new HashMap<String,Object>();
                        paramMap.put("userId", SPUtils.get(context, "userId", new Long(0l)));
                        paramMap.put("token", SPUtils.get(context, "token", ""));
                        paramMap.put("bookId", parkBookInfo.getBookId());
                        HttpUtils.sendHttpPostRequest(Urls.URL_CANCLE_BOOK_SPACE, handlerCancle, paramMap, context);
                    }
                })
                .setNegativeButton("点错了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).show();
    }

}
