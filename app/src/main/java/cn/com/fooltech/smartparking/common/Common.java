package cn.com.fooltech.smartparking.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.activity.LoginActivity;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.bean.UserInfo;
import cn.com.fooltech.smartparking.bean.jsonbean.GetUserInfo;
import cn.com.fooltech.smartparking.utils.ErrorUtils;
import cn.com.fooltech.smartparking.utils.HttpUtils;
import cn.com.fooltech.smartparking.utils.JsonUtils;
import cn.com.fooltech.smartparking.utils.SPUtils;
import cn.com.fooltech.smartparking.utils.ToastUtils;
import cn.com.fooltech.smartparking.utils.Utils;

/**
 * Created by YY on 2016/7/22.
 */
public class Common {
    private static Context context;
    static double DEF_PI = 3.14159265359; // PI
    static double DEF_2PI= 6.28318530712; // 2*PI
    static double DEF_PI180= 0.01745329252; // PI/180.0
    static double DEF_R =6370693.5; // radius of earth
    /**
     * 用户是否登录,如果没有登录跳转到登录页面
     */
    public static boolean isLogin(Context context) {
//        UserInfo userInfo = (UserInfo) SPUtils.getObject(context, "userInfo");
        if (!MyApplication.isLogin) {
            MyApplication.isToLogin = true;
            context.startActivity(new Intent(context, LoginActivity.class));
            return false;
        }
        return true;
    }

    /**
     * 重新获取token
     * @param ctx
     */
    public static void getAccessToken(Context ctx){
        context = ctx;
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("userId", SPUtils.get(ctx, "userId", new Long(0l)));
        paramMap.put("token", SPUtils.get(ctx, "token", ""));

        HttpUtils.sendHttpPostRequest(Urls.URL_GET_ACCESS_TOKEN, handlerGetToken, paramMap,ctx);
    }
    static Handler handlerGetToken = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, "服务器出现异常");
            }else {
                GetUserInfo userInfo = (GetUserInfo) JsonUtils.jsonToObject(msg.obj.toString(), GetUserInfo.class);
                if (userInfo != null) {
                    int code = userInfo.getCode();
                    UserInfo info = userInfo.getContent();
                    if (code == 0) {
                        SPUtils.put(context, "token", info.getToken());
                    }else {
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
     * 获取车辆当前driver
     */
    public static void getDriver(Context context,Handler handlerGetDriver){
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userId", SPUtils.get(context, "userId", new Long(0l)));
        paramMap.put("token", SPUtils.get(context, "token", ""));
        paramMap.put("plateNumber", MyApplication.currentCar.getPlateNumber() );
        HttpUtils.sendHttpPostRequest(Urls.URL_GET_DRIVER, handlerGetDriver, paramMap,context);
    }

    public static void setDriver(Context context,Handler handlerSetDriver){
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userId", SPUtils.get(context, "userId", new Long(0l)));
        paramMap.put("token", SPUtils.get(context, "token", ""));
        paramMap.put("plateNumber", MyApplication.currentCar.getPlateNumber() );
        HttpUtils.sendHttpPostRequest(Urls.URL_SET_DRIVER, handlerSetDriver, paramMap,context);
    }

    public static void getLinkUrl(Context context,Handler handlerLink,int type){
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("linkType", type);
        HttpUtils.sendHttpPostRequest(Urls.URL_GET_LINK, handlerLink, paramMap,context);
    }

    public static int dp2px(int dp,Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,context.
                getResources().getDisplayMetrics());
    }

    /**
     * 判断是上午还是下午
     * @return  结果为“0”是上午     结果为“1”是下午
     */
    public static int judgeAmOrPm(){
        GregorianCalendar ca = new GregorianCalendar();
        return ca.get(GregorianCalendar.AM_PM);
    }

    /**
     * 获取SD卡路径
     * @return
     */
    public static String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }

    /**
     * 设置背景变暗
     * @param act
     */
    public static void setShadow(Activity act,float alpha){
        WindowManager.LayoutParams lp = act.getWindow().getAttributes();
        lp.alpha = alpha;
        act.getWindow().setAttributes(lp);
    }


    /**
     * 切换小圆点
     * @param index
     */
    public static void setTipBackground(int index, ImageView dots[]){
        for (int i = 0; i < dots.length; i++) {
            if(i == index){
                dots[i].setBackgroundResource(R.drawable.banner_seleccted);
            }else{
                dots[i].setBackgroundResource(R.drawable.banner_normal);
            }
        }
    }

    public static void setViewGroup(Context context,ImageView dots[],ViewGroup viewGroup){
        for (int i = 0; i < dots.length; i++) {
            ImageView imageView = new ImageView(context);
            imageView.setImageResource(R.drawable.banner_normal);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(15, 0, 15, 0);//4个参数按顺序分别是左上右下
            imageView.setLayoutParams(layoutParams);

            dots[i] = imageView;
            viewGroup.addView(dots[i]);
        }
    }

    public static double GetLongDistance(double lon1, double lat1, double lon2, double lat2)
    {
        double ew1, ns1, ew2, ns2;
        double distance;
        // 角度转换为弧度
        ew1 = lon1 * DEF_PI180;
        ns1 = lat1 * DEF_PI180;
        ew2 = lon2 * DEF_PI180;
        ns2 = lat2 * DEF_PI180;
        // 求大圆劣弧与球心所夹的角(弧度)
        distance = Math.sin(ns1) * Math.sin(ns2) + Math.cos(ns1) * Math.cos(ns2) * Math.cos(ew1 - ew2);
        // 调整到[-1..1]范围内，避免溢出
        if (distance > 1.0)
            distance = 1.0;
        else if (distance < -1.0)
            distance = -1.0;
        // 求大圆劣弧长度
        distance = DEF_R * Math.acos(distance);
        return distance;
    }


}
