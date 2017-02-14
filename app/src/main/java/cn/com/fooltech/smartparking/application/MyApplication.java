package cn.com.fooltech.smartparking.application;

import android.app.Activity;
import android.app.Application;
import android.net.Uri;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.bean.BindCarInfo;
import cn.com.fooltech.smartparking.bean.ParkCollectInfo;
import cn.com.fooltech.smartparking.bean.VoucherInfo;
import cn.com.fooltech.smartparking.offlinemap.adapter.OfflineExpandableListAdapter;

public class MyApplication extends Application {
    private static String TAG = "FOOLPARK";
    public static final String APP_KEY = "BZq8Fn65tXaogqm52P9G9tYEQqlGhlj3";//百度地图
    public static final String MCODE = "F4:26:A8:29:18:F6:FD:37:D0:E0:06:B3:68:CE:F6:62:70:C9:52:50;cn.com.fooltech.smartparking";//百度天气
    public static final String VOICE_APID = "57a9be94";//讯飞语音
    public static final String SECRET_KEY = "FFHBAlogJBUVidS1CHA5871wZ9lEp2AF"; //密钥
    public static RequestQueue requestQueue;
    private static Stack<Activity> activityStack;
    private static MyApplication singleton;
//    public static List<BindCarInfo> bindCarList = new ArrayList<BindCarInfo>();;//绑定的车辆
    public static String imagePath = "/sdcard/FoolPark/";//图片存储路径
    public static Uri userHeadUri;//用户图像的uri
//    public static String imageUrl = "";//修改的用户图像url
    public static BindCarInfo currentCar = new BindCarInfo();//当前用户驾驶的车
    public static Map<String,Long> voucherMap;//选中的停车券
    public static ImageView lastImgVoucher;//记录上次选中的voucher
    public static boolean isUse = false;//是否使用了停车券
    public static boolean isUpdateCar = false;//是否更新车辆
    public static boolean isRecharge = false;//是否充值成功,成功后更新数据
    public static boolean isUpdateUser = false;//是否更新了用户信息
    public static boolean isLogin = false;//是否已经登录
    public static boolean isToLogin = false;//是否跳转到登录页登录
    public static boolean isFree = true; //是否免费,时间少于15分钟不收费用
    public static int pointCount = -1;//总积分数量
    public static float latitude = 0f;
    public static float longtitude = 0f;
    public static long voucherId = -1;//付款选择的优惠券id
    public static String currentCity = "";
    public static MKOfflineMap mOffline = null;
    public static int ERROR_EXCE = -1;//异常
    public static int ERROR_NET = -2;//网络连接错误
    public static int newMessage;//是否有新消息
    public static final String ROUTE_PLAN_NODE = "routePlanNode";
    public static final String APP_FOLDER_NAME = "BNSDK_FOOL";
    public static final String BANNER_IMAGE_PATH = imagePath + "banner";//保存banner图片
    public static final int REQUE_CODE_CAMERA = 1;
    public static final int REQUE_CODE_PHOTO = 2;
    public static final int REQUE_CODE_CROP = 3;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
        singleton=this;
		//创建requestQueue
		requestQueue = Volley.newRequestQueue(getApplicationContext());
        //语音初始化
        SpeechUtility.createUtility(getApplicationContext(), SpeechConstant.APPID + "=" + VOICE_APID);
        //初始化离线地图
        SDKInitializer.initialize(this);

        MobclickAgent.setDebugMode( true );

        Logger.init(TAG).logLevel(LogLevel.NONE);

    }
	
	public static RequestQueue getHttpRequestQueue(){
		return requestQueue;
	}

    public static MyApplication getInstance() {
        return singleton;
    }

    /**
     * add Activity 添加Activity到栈
     */
    public void addActivity(Activity activity){
        if(activityStack ==null){
            activityStack =new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    /**
     * 结束指定的Activity
     */
    public static void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }


    /**
     * 退出应用程序
     */
    public void AppExit() {
        try {
            finishAllActivity();
        } catch (Exception e) {
        }
    }
}
