package cn.com.fooltech.smartparking.adapter;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.activity.ParkDetailActivity;
import cn.com.fooltech.smartparking.activity.ParkMapActivity;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.bean.ParkInfo;
import cn.com.fooltech.smartparking.bean.PoiInfo;
import cn.com.fooltech.smartparking.common.Common;
import cn.com.fooltech.smartparking.common.Urls;
import cn.com.fooltech.smartparking.utils.ErrorUtils;
import cn.com.fooltech.smartparking.utils.HttpUtils;
import cn.com.fooltech.smartparking.utils.JsonUtils;
import cn.com.fooltech.smartparking.utils.NetUtils;
import cn.com.fooltech.smartparking.utils.SPUtils;
import cn.com.fooltech.smartparking.utils.ToastUtils;
import cn.com.fooltech.smartparking.utils.Utils;

/**
 * Created by YY on 2016/7/6.
 */
public class ParkInfoPagerAdapter extends PagerAdapter {
    private ParkMapActivity context;
    private List<View> viewList;
    public List<PoiInfo> dataList;
    private String parkId;
    private int isCollected;
    private CheckBox collect;
    private AlertDialog dialog;
    private static final int BOOK = 0;
    private static final int NAVI = 1;
    private static int TYPE = BOOK;
    private LatLng startPoint,endPoint;
    private Drawable unCollect,unBook;
    public ParkInfoPagerAdapter(ParkMapActivity context,List<View> viewList,List<PoiInfo> dataList){
        this.context = context;
        this.viewList = viewList;
        this.dataList = dataList;
        unCollect = context.getResources().getDrawable(R.drawable.uncollect);
        unBook = context.getResources().getDrawable(R.drawable.unbook);
        unCollect.setBounds(0, 0, unCollect.getMinimumWidth(), unCollect.getMinimumHeight());
        unBook.setBounds(0, 0, unBook.getMinimumWidth(), unBook.getMinimumHeight());
    }
    @Override
    public int getCount() {
        return viewList == null ? 0 : viewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view.equals(o);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // 实例化一个页卡 
        container.addView(viewList.get(position));
        final PoiInfo poiInfo = (PoiInfo) dataList.get(position);
        View convertView = viewList.get(position);
        TextView tvParkName = (TextView) convertView.findViewById(R.id.park_name);
        TextView tvSpaceTotal = (TextView) convertView.findViewById(R.id.space);
        TextView tvParkPrice = (TextView) convertView.findViewById(R.id.price);
        TextView tvDistance = (TextView) convertView.findViewById(R.id.distance);
        TextView tvParkDetail = (TextView) convertView.findViewById(R.id.park_detail);
        TextView tvBook = (TextView) convertView.findViewById(R.id.book_space);
        LinearLayout layNavi = (LinearLayout) convertView.findViewById(R.id.lay_park_navi);
        LinearLayout layBookSpace = (LinearLayout) convertView.findViewById(R.id.lay_book);
        final CheckBox cbCollect = (CheckBox) convertView.findViewById(R.id.park_collect);

        if(poiInfo.getPoiType() == 0) {  //停车场
            final ParkInfo parkInfo = poiInfo.getDetailInfo();

            if (parkInfo.getParkType() == 2) { //已加盟 可预约
                String spaceCount = "车位: " + parkInfo.getFreeSpace() + "/" + parkInfo.getTotalSpace();
                int index = spaceCount.indexOf("/");
                SpannableStringBuilder stringBuilder = new SpannableStringBuilder(spaceCount);
                ForegroundColorSpan span = new ForegroundColorSpan(context.getResources().getColor(R.color.orange));
                stringBuilder.setSpan(span, 4, index, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvSpaceTotal.setText(stringBuilder);
            } else { //未加盟
                tvSpaceTotal.setText("车位: " + parkInfo.getTotalSpace());
                layBookSpace.setEnabled(false);
                tvBook.setTextColor(Color.parseColor("#c4c4c4"));
                tvBook.setCompoundDrawables(null,null,unBook,null);
            }

            cbCollect.setChecked(parkInfo.getIsCollected() == 1 ? true : false);
            tvParkPrice.setText("价格: " + parkInfo.getParkPrice());

            //详情
            tvParkDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ParkDetailActivity.class);
                    intent.putExtra("parkInfo", parkInfo);
                    context.startActivity(intent);
                }
            });
            //预定车位
            layBookSpace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showBindCarDialog(poiInfo);
                }
            });

            //收藏
            cbCollect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                    if (!NetUtils.isConn(context)) {
//                        cbCollect.setChecked(false);
//                        return;
//                    }

                    if (!Common.isLogin(context)) {
                        cbCollect.setChecked(false);
                        return;
                    }
                    Map<String, Object> paramMap = new HashMap<String, Object>();
                    paramMap.put("userId", SPUtils.get(context, "userId", new Long(0l)));
                    paramMap.put("token", SPUtils.get(context, "token", ""));
                    paramMap.put("parkId", poiInfo.getPoiId());
                    HttpUtils.sendHttpPostRequest(Urls.URL_COLLECT_PARK, handlerCollect, paramMap, context);
                    parkId = poiInfo.getPoiId();
                    isCollected = parkInfo.getIsCollected();
                    collect = cbCollect;
                }
            });
        }else { //充电站和加油站
            layBookSpace.setEnabled(false);
            cbCollect.setEnabled(false);
            cbCollect.setTextColor(Color.parseColor("#c4c4c4"));
            tvBook.setTextColor(Color.parseColor("#c4c4c4"));
            tvBook.setCompoundDrawables(null,null,unBook,null);
            cbCollect.setCompoundDrawables(null,null,unCollect,null);
            tvParkPrice.setVisibility(View.GONE);
            tvParkDetail.setVisibility(View.GONE);
            tvSpaceTotal.setText("地址: " + poiInfo.getPoiAddress());
        }
        tvParkName.setText(position + 1 + "." + poiInfo.getPoiName());
        int distance = (int) Common.GetLongDistance(MyApplication.longtitude,MyApplication.latitude,poiInfo.getPoiLng(),poiInfo.getPoiLat());
        tvDistance.setText(distance > 1000 ? "距离: " + Utils.decimalFormat1((double)distance / 1000) + "km" : "距离: " + distance + "m");
        //导航
        layNavi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Common.isLogin(context)) return;
                if (!NetUtils.isConn(context)) return;
                startPoint = new LatLng(MyApplication.latitude, MyApplication.longtitude);
                endPoint = new LatLng(poiInfo.getPoiLat(), poiInfo.getPoiLng());
//                Navigation.startNavigation(startPoint, endPoint, context);
                if (BaiduNaviManager.isNaviInited()) {
                    TYPE = NAVI;
                    Common.getDriver(context,handlerGetDriver);
                }
            }
        });

        return viewList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //销毁一个页卡
        container.removeView(viewList.get(position));
    }

    //收藏
    Handler handlerCollect = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_NET) {
                collect.setChecked(false);
            }else if(Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, "服务器出现异常");
            }else {
                int code = JsonUtils.getCodeResult(msg.obj.toString());
                if (code == 0) {
                    ToastUtils.showShort(context, "收藏成功");
                    return;
                } else if (code == 40008) {
                    ToastUtils.showShort(context, "当前停车场已经收藏");
                    collect.setChecked(true);
                    return;
                }else {
                    ErrorUtils.errorCode(context, code);
                }
            }
            collect.setChecked(isCollected == 1 ? true : false);
            super.handleMessage(msg);
        }
    };
    //预约
    Handler handlerBook = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, "服务器出现异常");
            }else {
                int code = JsonUtils.getCodeResult(msg.obj.toString());
                if (code == 0) {
                    ToastUtils.showShort(context, "预约成功");
                    dialog.cancel();
                    return;
                }else if (code == 40006) {
                    ToastUtils.showShort(context, "您已预约成功,不能重复预约");
                    dialog.cancel();
                } else {
                    ErrorUtils.errorCode(context, code);
                }
            }
            super.handleMessage(msg);
        }
    };

    private void bookSpace(){
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userId", SPUtils.get(context, "userId", new Long(0l)));
        paramMap.put("token", SPUtils.get(context, "token", ""));
        paramMap.put("parkId", parkId);
        paramMap.put("plateNumber", MyApplication.currentCar.getPlateNumber());
        HttpUtils.sendHttpPostRequest(Urls.URL_BOOK_SPACE, handlerBook, paramMap,context);
    }

    /**
     * 判断用户是否当前车辆的driver,如果不是就设置为driver
     */
    private void isDriver(long driverId){
//        if(MyApplication.currentCar != null){
            long userId = (long) SPUtils.get(context, "userId", new Long(0l));
            if(driverId == userId){ //预约用户是当前车辆的driver
                if(TYPE == BOOK) {
                    bookSpace();
                }else if(TYPE == NAVI){
                    context.routeplanToNavi(BNRoutePlanNode.CoordinateType.BD09LL, startPoint, endPoint);
                }
            }else {//不是,设置成driver
                Common.setDriver(context,handlerSetDriver);
            }
//        }
    }

    Handler handlerSetDriver = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, "服务器出现异常");
            }else {
                int code = JsonUtils.getCodeResult(msg.obj.toString());
                if (code == 0) {
                    if(TYPE == BOOK) {
                        bookSpace();
                    }else if(TYPE == NAVI){
                        context.routeplanToNavi(BNRoutePlanNode.CoordinateType.BD09LL, startPoint, endPoint);
                    }
                } else {
                    ErrorUtils.errorCode(context, code);
                }
            }
            super.handleMessage(msg);
        }
    };

    Handler handlerGetDriver = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, "服务器出现异常");
            }else {
                Map<String, Object> resultMap = JsonUtils.jsonToMap(msg.obj.toString());
                if (resultMap != null && resultMap.size() > 0) {
                    int code = (int) resultMap.get("code");
                    if (code == 0) {
                        JSONObject object = (JSONObject) resultMap.get("content");
                        try {
                            long droverId = object.getLong("driverId");
                            isDriver(droverId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        ErrorUtils.errorCode(context, code);
                    }
                } else {
                    ToastUtils.showShort(context, "服务器请求错误");
                }
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 预约对话框
     * @param
     * @param info
     */
    private void showBindCarDialog( final PoiInfo info) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        dialog = builder.create();
        dialog.show();

        Window window = dialog.getWindow();
        window.setContentView(R.layout.dialog_book);
        TextView tvCancle = (TextView) window.findViewById(R.id.dialog_cancle);
        TextView tvBook = (TextView) window.findViewById(R.id.dialog_book);

        tvCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        //马上预约
        tvBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(!NetUtils.isConn(context)) return;
                if(!Common.isLogin(context)) return;

                parkId = info.getPoiId();
                TYPE = BOOK;
                Common.getDriver(context,handlerGetDriver);

            }
        });
    }

}
