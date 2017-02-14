package cn.com.fooltech.smartparking.activity;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.navisdk.adapter.BNOuterLogUtil;
import com.iflytek.cloud.SpeechRecognizer;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.adapter.BannerPagerAdapter;
import cn.com.fooltech.smartparking.adapter.CarPagerAdapter;
import cn.com.fooltech.smartparking.anim.Rotate3dAnimation;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.baidumap.Navigation;
import cn.com.fooltech.smartparking.bean.AppInfo;
import cn.com.fooltech.smartparking.bean.BannerInfo;
import cn.com.fooltech.smartparking.bean.BindCarInfo;
import cn.com.fooltech.smartparking.bean.PayInfo;
import cn.com.fooltech.smartparking.bean.UserInfo;
import cn.com.fooltech.smartparking.bean.VoucherInfo;
import cn.com.fooltech.smartparking.bean.jsonbean.GetBindCar;
import cn.com.fooltech.smartparking.cache.BitmapCache;
import cn.com.fooltech.smartparking.common.Common;
import cn.com.fooltech.smartparking.common.Urls;
import cn.com.fooltech.smartparking.dao.BannerDao;
import cn.com.fooltech.smartparking.bean.jsonbean.GetAppInfo;
import cn.com.fooltech.smartparking.bean.jsonbean.GetBanner;
import cn.com.fooltech.smartparking.bean.jsonbean.GetPayInfo;
import cn.com.fooltech.smartparking.bean.jsonbean.GetUserInfo;
import cn.com.fooltech.smartparking.bean.jsonbean.GetVoucher;
import cn.com.fooltech.smartparking.interface1.PermissionListener;
import cn.com.fooltech.smartparking.offlinemap.utils.PinyinUtil;
import cn.com.fooltech.smartparking.utils.AppUtils;
import cn.com.fooltech.smartparking.utils.DateUtils;
import cn.com.fooltech.smartparking.utils.ErrorUtils;
import cn.com.fooltech.smartparking.utils.GuideUtils;
import cn.com.fooltech.smartparking.utils.HttpUtils;
import cn.com.fooltech.smartparking.utils.JsonUtils;
import cn.com.fooltech.smartparking.utils.SPUtils;
import cn.com.fooltech.smartparking.utils.ToastUtils;
import cn.com.fooltech.smartparking.utils.Utils;
import cn.com.fooltech.smartparking.view.ClockView2;
import cn.com.fooltech.smartparking.view.PopWindowVoucher;
import cn.com.fooltech.smartparking.voice.VouceUtils;
import cn.com.fooltech.smartparking.zxing.activity.CaptureActivity;

public class IndexActivity extends BaseActivity implements BDLocationListener {
    @Bind(R.id.vp_pic)
    ViewPager mViewPager;
    @Bind(R.id.lay_dot)
    LinearLayout viewGroup;
    @Bind(R.id.rader_scan)
    ImageView ivRaderScan;
    @Bind(R.id.city)
    TextView tvCity;
    @Bind(R.id.pic_weather)
    ImageView ivPicWeather;
    @Bind(R.id.temp)
    TextView tvTemp;
    @Bind(R.id.pm)
    TextView tvPm;
    @Bind(R.id.weather)
    TextView tvWeather;
    @Bind(R.id.wind)
    TextView tvWind;
    @Bind(R.id.car_wash)
    TextView tvCarWash;
    @Bind(R.id.vp_car)
    ViewPager viewPagerCar;
    @Bind(R.id.lay_payment)
    RelativeLayout layPayment;
    @Bind(R.id.lay_content2)
    RelativeLayout mContent;
    @Bind(R.id.lay_index6)
    RelativeLayout mLayout;
    private Context context = IndexActivity.this;
    private static final long TIME = (long) (1.9 * 60 * 60 * 1000);
    private static final long MINUTE = 15 * 60 * 1000;//15分钟
    private long exitTime = 0;
    private SlidingMenu menu;
    private TextView tvNickName, tvMobile, tvUnLogin;
    private ImageView dots[] = null;
    private ImageView ivUserImg,ivNewMessage;
    private boolean isFirstLoc = true;// 是否首次定位
    private BannerPagerAdapter mBannerAdapter;
    private int currentItem;
    private int len;
    private String city;
    private static final int UPTATE_VIEWPAGER = 0;
    private Timer mTimer = new Timer();
    private TimerTask mTimerTask = null;
    private static int delay = 5000;
    private static int period = 5000;
    private Timer timerToken;
    private LocationClient mLocationClient;
    private UserInfo userInfo;
    private CarPagerAdapter carAdapter;
    private Button btnPayment;
    private PopWindowVoucher popWindowVoucher;
    private List<VoucherInfo> resultList;
    private Drawable drawableGray, drawableGreen;
    private TextView tvUseVoucher, tvVoucher;
    private int type = 1;
    private int centerX;
    private int centerY;
    private int depthZ = 500;
    private int duration = 600;
    private Rotate3dAnimation openAnimation;
    private Rotate3dAnimation closeAnimation;
    private boolean isOpen = false;
    private SpeechRecognizer mIat;
    private static final int PAY = 0;
    private AlertDialog dialogPay;
    private List<BannerInfo> bannerList = new ArrayList<BannerInfo>();
    private BannerDao bannerDao;
    private static final int BANNERTYPE = 1;
    private int versionCode;
    private List<BindCarInfo> bindCarList = new ArrayList<BindCarInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        ButterKnife.bind(this);

        GuideUtils guideUtil = GuideUtils.getInstance();
        guideUtil.addGuideImage(this,mLayout,R.drawable.ic_guide_1,"guide_index");

        if(AppUtils.isOs6()) {
            requestRuntimePermission(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, new PermissionListener() {
                @Override
                public void onGranted() {
//                    ToastUtils.showShort(context, "LOCATION同意");
                }

                @Override
                public void onDenied(List<String> deniedPermission) {
//                    ToastUtils.showShort(context, "LOCATION拒绝");
                    showLocationDialog();
                }
            });
        }

        updateVersion();

        //初始化导航
        BNOuterLogUtil.setLogSwitcher(true);
        if (Navigation.initDirs()) {
            Navigation.initNavi(this);
        }

        initSlideMenu();
        initView();
        initLocation();
        //登录成功后开启定时
        if (MyApplication.isLogin) {
            timerToken = new Timer();
            timeGetToken();
        }

    }

    private void initView() {
        bannerDao = new BannerDao(this);

        mBannerAdapter = new BannerPagerAdapter(context, bannerList);
        mViewPager.setAdapter(mBannerAdapter);
        HttpUtils.sendHttpPostRequest(Urls.URL_INDEX_BANNER, handlerBanner, null, this);

        raderScan();

        //轮播图
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Common.setTipBackground(position % len, dots);
                currentItem = position + 1;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        carAdapter = new CarPagerAdapter(this, bindCarList);
        viewPagerCar.setAdapter(carAdapter);

        //车辆滑动
        viewPagerCar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                BindCarInfo carInfo = carAdapter.list.get(position);
                MyApplication.currentCar = carInfo;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case -1:
                    Common.getAccessToken(context);
                    break;
            }
            super.handleMessage(msg);
        }

    };

    /**
     * 定时获取token,间隔1.9小时
     */
    private void timeGetToken() {
        timerToken.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = -1;
                handler.sendMessage(message);
            }
        }, TIME, TIME);
    }

    /**
     * 版本更新
     */
    private void updateVersion(){
        versionCode = AppUtils.getVersionCode(this);
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("appType", 2);
        HttpUtils.sendHttpPostRequest(Urls.URL_GET_APP_INFO, handlerVersion, paramMap, this);
    }

    Handler handlerVersion = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                GetAppInfo appInfo = (GetAppInfo) JsonUtils.jsonToObject(msg.obj.toString(), GetAppInfo.class);
                if (appInfo != null) {
                    int code = appInfo.getCode();
                    if (code == 0) {
                        AppInfo info = appInfo.getContent();
                        if (info.getForceUpgrade() == 1 && (Utils.strToInt(info.getCurrentVer()) > versionCode)) {  //强制更新
                            showVersionDialog(info);
                        }
                    } else {
                        ErrorUtils.errorCode(context, code);
                    }
                } else {
                    ToastUtils.showShort(context, "服务器请求错误");
                }
            }
        }
    };

    /**
     * 显示更新对话框
     */
    private void showVersionDialog(final AppInfo info) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();

        Window window = dialog.getWindow();
        window.setContentView(R.layout.dialog_version2);

        TextView tvVersion = (TextView) window.findViewById(R.id.version_new2);
        TextView tvUpdate = (TextView) window.findViewById(R.id.update_version);

        tvVersion.setText("发现新版本: " + info.getCurrentVer());

        //更新
        tvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateVersion(info);
            }
        });
    }

    private void updateVersion(AppInfo info) {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                (Uri.parse(info.getDownloadUrl()))
        ).addCategory(Intent.CATEGORY_BROWSABLE)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * 扫描动画
     */
    private void raderScan() {
        Animation operatingAnim = AnimationUtils.loadAnimation(this, R.anim.rader_scan);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        if (operatingAnim != null) {
            ivRaderScan.startAnimation(operatingAnim);
        }
    }


    private void showPopWindow() {
        if (resultList != null && resultList.size() > 0) {
            openAnimator();
            //实例化SelectPicPopupWindow
            popWindowVoucher = new PopWindowVoucher(this, resultList);
            popWindowVoucher.showAtLocation(IndexActivity.this.findViewById(R.id.lay_index), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
            popWindowVoucher.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    Common.setShadow(IndexActivity.this, 1f);
                    hideAnimator();
                    if (!MyApplication.isUse) {
                        tvUseVoucher.setCompoundDrawables(drawableGray, null, null, null);
                        tvUseVoucher.setTextColor(getResources().getColor(R.color.gray));
                        tvVoucher.setVisibility(View.GONE);
                        if (MyApplication.voucherMap != null && MyApplication.voucherMap.size() > 0) {
                            MyApplication.voucherMap.clear();
                        }
                    } else {
                        tvUseVoucher.setCompoundDrawables(drawableGreen, null, null, null);
                        tvUseVoucher.setTextColor(getResources().getColor(R.color.green));
                        tvVoucher.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
            ToastUtils.showShort(this, "您没有可使用的停车券");
        }
    }

    private void getVoucher() {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userId", SPUtils.get(this, "userId", new Long(0l)));
        paramMap.put("token", SPUtils.get(this, "token", ""));
        paramMap.put("voucherStatus", 0);
        HttpUtils.sendHttpPostRequest(Urls.URL_GET_VOUCHER, handlerVoucher, paramMap, this);
    }

    Handler handlerVoucher = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                GetVoucher voucher = (GetVoucher) JsonUtils.jsonToObject(msg.obj.toString(), GetVoucher.class);
                if (voucher != null) {
                    int code = voucher.getCode();
                    if (code == 0) {
                        resultList = voucher.getContent();
                        showPopWindow();
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
     * 侧滑菜单
     */
    private void initSlideMenu() {
        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT); //设置左滑菜单
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);//设置滑动的屏幕范围，该设置为全屏区域都可以滑动
        menu.setBehindOffsetRes(R.dimen.behind_off_setres);//SlidingMenu划出时主页面显示的剩余宽度
        menu.setFadeDegree(0.4f);//SlidingMenu滑动时的渐变程度
        menu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);//使SlidingMenu附加在Activity上
        menu.setFadeEnabled(true);
        menu.setMenu(R.layout.activity_index_slide_menu);//设置menu的布局文件

        tvNickName = (TextView) menu.findViewById(R.id.nick_name_show);
        tvMobile = (TextView) menu.findViewById(R.id.mobile_show);
        tvUnLogin = (TextView) menu.findViewById(R.id.unLogin);
        ivUserImg = (ImageView) menu.findViewById(R.id.user_head_show);
        ivNewMessage = (ImageView) menu.findViewById(R.id.new_message);

        setUserInfo();

    }

    private  void setUserInfo(){
        ImageLoader imageLoader = new ImageLoader(MyApplication.getHttpRequestQueue(), new BitmapCache());
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(ivUserImg, R.drawable.default_user, R.drawable.default_user);
        userInfo = (UserInfo) SPUtils.getObject(this, "userInfo");
        if (MyApplication.isLogin) { //已登录
            tvUnLogin.setVisibility(View.GONE);
            tvNickName.setVisibility(View.VISIBLE);
            tvMobile.setVisibility(View.VISIBLE);
            tvNickName.setText(userInfo.getNickName().equals(" ") ? "" : userInfo.getNickName());
            tvMobile.setText(userInfo.getMobile());
            if (userInfo.getAvatarUrl().contains("http")) {
                imageLoader.get(userInfo.getAvatarUrl(), listener);
            }
        } else {//未登录
            tvUnLogin.setVisibility(View.VISIBLE);
            tvNickName.setVisibility(View.GONE);
            tvMobile.setVisibility(View.GONE);
        }

        if(MyApplication.newMessage == 0){
            ivNewMessage.setVisibility(View.GONE);
        }else if(MyApplication.newMessage == 1){
            ivNewMessage.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 显示付款信息
     */
    private void showPayMent(final PayInfo info) {
        TextView tvEnterTime = (TextView) findViewById(R.id.entertime);
        final ClockView2 tvParkTime = (ClockView2) findViewById(R.id.parktime);
        TextView tvPayAmount = (TextView) findViewById(R.id.pay_amount);
        tvVoucher = (TextView) findViewById(R.id.voucher);
        tvUseVoucher = (TextView) findViewById(R.id.use_voucher);
        btnPayment = (Button) findViewById(R.id.btn_payment);

        drawableGray = getResources().getDrawable(R.drawable.oval_gray);
        drawableGray.setBounds(0, 0, drawableGray.getMinimumWidth(), drawableGray.getMinimumHeight());
        drawableGreen = getResources().getDrawable(R.drawable.oval_green);
        drawableGreen.setBounds(0, 0, drawableGray.getMinimumWidth(), drawableGray.getMinimumHeight());

        long duration = DateUtils.dateDiff(info.getEnterTime(), info.getServerTime());
        tvEnterTime.setText("入场时间: " + info.getEnterTime());
//        tvParkTime.setText("停车时长: " + duration);
        tvParkTime.setEndTime(duration / 1000, tvPayAmount, btnPayment, info.getParkPrice());
        String content = "预计费用: " + (double)(info.getTotalFee() - info.getAlreadyPay()) / 100 + "元";
        Spannable spanText = new SpannableString(content);
        spanText.setSpan(new AbsoluteSizeSpan(30,true), 6, content.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvPayAmount.setText(spanText);
        //付款
        btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyApplication.isFree) {
                    mContent.startAnimation(closeAnimation);
                    isOpen = !isOpen;
                } else {
                    String parkTime = tvParkTime.getText().toString();
                    showPayinfoDialog(info, parkTime);
                }
            }
        });
    }

    /**
     * 缴纳停车费用
     */
    private void payFee() {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userId", SPUtils.get(this, "userId", new Long(0l)));
        paramMap.put("token", SPUtils.get(this, "token", ""));
        paramMap.put("plateNumber", MyApplication.currentCar.getPlateNumber());
        if (MyApplication.voucherId != -1)
            paramMap.put("voucherId", MyApplication.voucherId);

        HttpUtils.sendHttpPostRequest(Urls.URL_PAY, handlerPay, paramMap, this);
    }

    Handler handlerPay = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                int code = JsonUtils.getCodeResult(msg.obj.toString());
                if (code == 0) {
                    ToastUtils.showShort(context, "付款成功");
                    dialogPay.cancel();
                    mContent.startAnimation(closeAnimation);
                    isOpen = !isOpen;
                } else if (code == 60009) {
                    showPromptDialog();
                } else if (code == 60008) {
                    ToastUtils.showShort(context, "费用已缴，不能重复缴费");
                    dialogPay.cancel();
                    mContent.startAnimation(closeAnimation);
                    isOpen = !isOpen;
                } else {
                    ErrorUtils.errorCode(context, code);
                }
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 获取付款信息
     */
    private void getPayInfo(Handler handler) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userId", SPUtils.get(this, "userId", new Long(0l)));
        paramMap.put("token", SPUtils.get(this, "token", ""));
        paramMap.put("plateNumber", MyApplication.currentCar.getPlateNumber());
        HttpUtils.sendHttpPostRequest(Urls.URL_GET_PAYINFO, handler, paramMap, this);
    }

    Handler handlerPayInfo = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                GetPayInfo payInfo = (GetPayInfo) JsonUtils.jsonToObject(msg.obj.toString(), GetPayInfo.class);
                if (payInfo != null) {
                    int code = payInfo.getCode();
                    if (code == 0) {
                        PayInfo info = payInfo.getContent();
                        openPayment(info);
                    } else if (code == 40005) { //到扫码页面
                        Intent intent = new Intent(context, CaptureActivity.class);
                        getPayInfo(handlerPayInfo2);
                        startActivityForResult(intent, 0);
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
    //扫码支付
    Handler handlerPayInfo2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                GetPayInfo payInfo = (GetPayInfo) JsonUtils.jsonToObject(msg.obj.toString(), GetPayInfo.class);
                if (payInfo != null) {
                    int code = payInfo.getCode();
                    if (code == 0) {

                    } else if (code == 40005) {

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

    private void showPromptDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Translucent);
        final AlertDialog dialog = builder.create();
        dialog.show();

        Window window = dialog.getWindow();
        window.setContentView(R.layout.dialog_prompt);
        Button btnOk = (Button) window.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                dialogPay.cancel();
                mContent.startAnimation(closeAnimation);
                isOpen = !isOpen;
            }
        });
    }

    private void showPayinfoDialog(PayInfo info, String parkTime) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Translucent);
        dialogPay = builder.create();
        dialogPay.show();

        Window window = dialogPay.getWindow();
        window.setContentView(R.layout.dialog_payinfo);
        TextView tvTotalFee = (TextView) window.findViewById(R.id.total_fee);
        TextView tvTime = (TextView) window.findViewById(R.id.park_time);
        TextView tvFreeFee = (TextView) window.findViewById(R.id.free_fee);
        TextView tvAlreadyFee = (TextView) window.findViewById(R.id.already_fee);
        TextView tvActualFee = (TextView) window.findViewById(R.id.actual_fee);
        Button btnCancle = (Button) window.findViewById(R.id.btn_cancle1);
        Button btnOk = (Button) window.findViewById(R.id.btn_ok1);

        double fee = (double)(info.getTotalFee() - info.getAlreadyPay()) / 100;
        parkTime = parkTime.substring(5, parkTime.lastIndexOf(":")).replace(":", "小时") + "分";
        tvTotalFee.setText("总停车费: " + (double)info.getTotalFee() / 100 + "元");
        tvTime.setText("停车时长:" + parkTime);
        tvFreeFee.setText(MyApplication.isUse == true ? "减免费用: 10元" : "减免费用: 0元");
        if(MyApplication.isUse){
            fee -= 10;
        }
        tvAlreadyFee.setText("已缴费用: " + (double)info.getAlreadyPay() / 100 + "元");
        tvActualFee.setText(fee + "元");
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogPay.cancel();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isFastClick()) return;
//                TYPE = PAY;
                Common.getDriver(context, handlerGetDriver);
            }
        });
    }

    /**
     * 支付
     */
    private void toPay(){
        if (MyApplication.voucherMap == null) {
            MyApplication.voucherMap = new HashMap<String, Long>();
        } else {
            MyApplication.voucherMap.clear();
        }
        MyApplication.lastImgVoucher = null;
        MyApplication.isUse = false;
        getPayInfo(handlerPayInfo);
    }

    /**
     * 点击事件
     *
     * @param view
     */
    public void onClick(View view) {
        switch (view.getId()) {
            //侧滑菜单
            case R.id.slide_menu:
                menu.showMenu();
                break;
            //二维码支付
            case R.id.pay:
                if (!Common.isLogin(this)) return;
                if(AppUtils.isOs6()){
                    requestRuntimePermission(new String[]{android.Manifest.permission.CAMERA}, new PermissionListener() {
                        @Override
                        public void onGranted() {
//                            ToastUtils.showShort(context,"CAMERA同意");
                            toPay();
                        }

                        @Override
                        public void onDenied(List<String> deniedPermission) {
//                            ToastUtils.showShort(context,"CAMERA拒绝");
                            showCameraDialog();
                            return;
                        }
                    });
                }else {
                    if(!AppUtils.cameraIsCanUse(this)){
                        showCameraDialog();
                        return;
                    }
                    toPay();
                }
                break;
            //使用停车券
            case R.id.use_voucher:
                if (Utils.isFastClick()) return;
                getVoucher();
                break;
            //车位查找
            case R.id.find_place:
                startActivity(new Intent(this, ParkMapActivity.class));
                break;
            //快速导航
            case R.id.navigation:
                if (!Common.isLogin(this)) return;
                VouceUtils.mIatResults.clear();
//                mIat = VouceUtils.startLinsten(this);
//                ToastUtils.showShort(this,"请开始说话...");
//                //1.创建SpeechRecognizer对象，第二个参数：本地听写时传InitListener
//                RecognizerDialog iatDialog = new RecognizerDialog(this, VouceUtils.mInitListener);
//                //2.设置听写参数，同上节
//                //3.设置回调接口
//                iatDialog.setListener(mRecognizerDialogListener);
//                //4.开始听写
//                iatDialog.show();
                Intent intent1 = new Intent(this, VoiceActivity.class);
                intent1.putExtra("type", 0);
                startActivity(intent1);
                break;
            //个人中心
            case R.id.user_center:
                if (!Common.isLogin(this)) return;
//                MyApplication.isUpdateUser = false;
                startActivity(new Intent(context, UserCenterActivity.class));
                break;
            //活动中心
            case R.id.activity:
                startActivity(new Intent(this, ActivityCenterActivity.class));
                break;
            //预约收藏
            case R.id.order_collect:
                if (!Common.isLogin(this)) return;
                startActivity(new Intent(this, BookOrCollectActivity.class));
                break;
            //我的消息
            case R.id.message:
                if (!Common.isLogin(this)) return;
                ivNewMessage.setVisibility(View.GONE);
                startActivity(new Intent(this, MessageActivity.class));
                break;
            //车辆管理
            case R.id.car_manage:
                if (!Common.isLogin(this)) return;
                startActivity(new Intent(this, CarManageActivity.class));
                break;
            //寻找爱车
            case R.id.search_car:
                if (!Common.isLogin(this)) return;
                startActivity(new Intent(this, PositionRecordActivity.class));
//                float eLat = 31.083988189697266f;
//                float eLng = 121.51983642578125f;
//                ParkInfo parkInfo = new ParkInfo();
//                parkInfo.setParkLat(eLat);
//                parkInfo.setParkLng(eLng);
//                Intent intent = new Intent(context, CarSearchActivity.class);
//                intent.putExtra("parkInfo",parkInfo);
//                startActivity(intent);
                break;
            //设置
            case R.id.setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;
            //关于
            case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                Bundle bundle = data.getExtras();
                String result = bundle.getString("result");

                // 把扫描并解析后的结果，显示在相应的 textView 上，或进行其它操作
                Log.i("二维码结果", "========" + result);
//                ToastUtils.showShort(this, "=========" + result);

                Intent intent = new Intent(this, ScanPayActivity.class);
                intent.putExtra("result", result);
                startActivity(intent);
            } else if (requestCode == 1) {
            }
        }
    }

    /**
     * 显示检测拍照权限对话框
     */
    private void showCameraDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true)
                .setMessage(getResources().getString(R.string.dialog_camera))
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).show();
    }

    /**
     * 显示定位权限对话框
     */
    private void showLocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true)
                .setMessage(getResources().getString(R.string.dialog_location))
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).show();
    }

    Handler handlerBanner = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_NET) {
                List<BannerInfo> infoList = bannerDao.queryBannerInfo(BANNERTYPE);//查询本地数据 1:首页banner 2:停车场banner
//                bannerList.addAll(infoList);
                notifyData(infoList);
            } else if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                GetBanner banner = (GetBanner) JsonUtils.jsonToObject(msg.obj.toString(), GetBanner.class);
                if (banner != null) {
                    int code = banner.getCode();
                    if (code == 0) {
                        List<BannerInfo> list = banner.getContent();
                        bannerDao.deleteByType(BANNERTYPE);
                        bannerDao.insertBannerInfo(list, BANNERTYPE);
                        SortList(list);
                        notifyData(list);
                    } else {
                        ErrorUtils.errorCode(context, code);
                    }
                } else {
                    ToastUtils.showShort(context, "服务器请求错误");
                }
            }
        }
    };

    private void notifyData(List<BannerInfo> infoList) {
        len = infoList.size();
        dots = new ImageView[len];
        Common.setViewGroup(context, dots, viewGroup);
        bannerList.addAll(infoList);
        mBannerAdapter.notifyDataSetChanged();

        Common.setTipBackground(0, dots);
        mViewPager.setCurrentItem(len * 100);
        timing();
    }

    /**
     * 图片排序
     *
     * @return
     */
    private void SortList(List<BannerInfo> list) {
        if (list == null) return;

        Collections.sort(list, new Comparator<BannerInfo>() {
            public int compare(BannerInfo arg1, BannerInfo arg2) {
                int dist1 = arg1.getImageOrder();
                int dist2 = arg2.getImageOrder();
                if (dist1 > dist2) {
                    return 1;
                } else if (dist1 == dist2) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPTATE_VIEWPAGER:
                    mViewPager.setCurrentItem(msg.arg1);
                    break;
            }
        }
    };

    //定时轮播图片，
    private void timing() {
        // 设置自动轮播图片，5s后执行，周期是5s
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = UPTATE_VIEWPAGER;
                message.arg1 = currentItem;
                mHandler.sendMessage(message);
            }
        }, delay, period);
    }

    /**
     * @author Jackie
     * 百度地图定位初始化
     */
    private void initLocation() {
        mLocationClient = new LocationClient(this);
        mLocationClient.registerLocationListener(this);  //设置地图定位回调监听
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置定位模式
        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02  国测局经纬度坐标系gcj02、百度墨卡托坐标系bd09、百度经纬度坐标系bd09ll
        option.setIsNeedAddress(true);//返回的定位结果包含地址信息
//        option.setOpenGps(true);
//        option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms 不设置或设置数值小于1000ms标识只定位一次
        mLocationClient.setLocOption(option);

    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        // 更新经纬度
        MyApplication.latitude = (float) bdLocation.getLatitude();
        MyApplication.longtitude = (float) bdLocation.getLongitude();

        if (isFirstLoc) {
            city = bdLocation.getCity();
            if (city.contains("市")) {
                city = city.replace("市", "");
            }
            requestWeather();
            isFirstLoc = false;
        }
    }

    private Handler handlerWeather = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg == null) return;
            String json = msg.obj.toString();
            try {
                JSONObject jsonObject = new JSONObject(json);
                int code = jsonObject.getInt("error");
                String states = jsonObject.getString("status");
                if (code == 0 && states.equals("success")) {
                    JSONArray results = jsonObject.getJSONArray("results");
                    JSONObject resultObj = results.getJSONObject(0);
                    String pm = (String) resultObj.get("pm25");
                    JSONObject carWash = (JSONObject) resultObj.getJSONArray("index").get(1);
//                    String title = (String) carWash.get("title");
                    String zs = (String) carWash.get("zs");
                    JSONArray weatherArray = resultObj.getJSONArray("weather_data");
                    JSONObject weatherObj = weatherArray.getJSONObject(0);

                    String weather = weatherObj.getString("weather");
                    String wind = weatherObj.getString("wind");
                    String temperature = weatherObj.getString("temperature");

                    tvCity.setText(city);
                    tvTemp.setText(temperature);
                    tvPm.setText("PM2.5  " + pm + "良");
                    tvWeather.setText(weather);
                    tvWind.setText(wind);
                    tvCarWash.setText("洗车指数: " + zs);

                    if (weather.contains("转")) {
                        int index = weather.indexOf("转");
                        String day = weather.substring(0, index);
                        String night = weather.substring(index + 1);
                        int result = DateUtils.DateCompare(DateUtils.getTime(), "18:00", "HH:MM");
                        if (result == 1) { //白天
                            String dayImg = PinyinUtil.converterToSpell(day);
                            int id = getResources().getIdentifier(dayImg, "drawable", getPackageName());
                            ivPicWeather.setImageResource(id);
                        } else { //晚上
                            String nightImg = PinyinUtil.converterToSpell(night);
                            int id = getResources().getIdentifier(nightImg, "drawable", getPackageName());
                            ivPicWeather.setImageResource(id);
                        }
                    } else {
                        String img = PinyinUtil.converterToSpell(weather);
                        int id = getResources().getIdentifier(img, "drawable", getPackageName());
                        ivPicWeather.setImageResource(id);
                    }

                } else {
//                    Log.d("=====", "天气信息获取失败,code=" + code);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 请求当地天气
     */
    private void requestWeather() {
        StringBuffer buffer = new StringBuffer();
        String url = null;
        try {
            url = buffer.append(Urls.URL_WEATHER).append("?location=").append(URLEncoder.encode(city, "UTF-8"))
                    .append("&output=json&ak=").append(MyApplication.APP_KEY)
                    .append("&mcode=").append(MyApplication.MCODE).toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpUtils.sendHttpGetRequest(url, handlerWeather);
    }

    /**
     * 听写UI监听器
     */
//    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
//        public void onResult(RecognizerResult results, boolean isLast) {
//            String res = VouceUtils.printResult(results);
//            if(isLast) {  //最后一句
//                getSearchParkDatas(res);
//            }
//        }
//
//        /**
//         * 识别回调错误.
//         */
//        public void onError(SpeechError error) {
//            String errStr = error.getPlainDescription(true);
//            String str = errStr.substring(0,errStr.indexOf("."));
//            ToastUtils.showShort(IndexActivity.this,str);
//        }
//    };

    Handler handlerGetDriver = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
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
     * 判断用户是否当前车辆的driver,如果不是就设置为driver
     */
    private void isDriver(long driverId) {
        long userId = (long) SPUtils.get(context, "userId", new Long(0l));
        if (driverId == userId) {//用户是当前车辆的driver
            payFee();
        } else {//不是,设置成driver
            Common.setDriver(context, handlerSetDriver);
        }
    }

    Handler handlerSetDriver = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                int code = JsonUtils.getCodeResult(msg.obj.toString());
                if (code == 0) {
                    payFee();
                } else {
                    ErrorUtils.errorCode(context, code);
                }
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 打开支付页面
     */
    private void openPayment(PayInfo info) {
        //以旋转对象的中心点为旋转中心点，这里主要不要再onCreate方法中获取，因为视图初始绘制时，获取的宽高为0
        centerX = mContent.getWidth() / 2;
        centerY = mContent.getHeight() / 2;
        if (openAnimation == null) {
            initOpenAnim();
            initCloseAnim();
        }

        //用作判断当前点击事件发生时动画是否正在执行
        if (openAnimation.hasStarted() && !openAnimation.hasEnded()) {
            return;
        }
        if (closeAnimation.hasStarted() && !closeAnimation.hasEnded()) {
            return;
        }

        //判断动画执行
        if (isOpen) {//关闭
            mContent.startAnimation(closeAnimation);
            tvUseVoucher.setTextColor(getResources().getColor(R.color.gray));
            tvUseVoucher.setCompoundDrawables(drawableGray, null, null, null);
            tvVoucher.setVisibility(View.GONE);
        } else { //打开
            showPayMent(info);
            MyApplication.voucherId = -1;
            mContent.startAnimation(openAnimation);
        }

        isOpen = !isOpen;
    }

    /**
     * 卡牌文本介绍打开效果：注意旋转角度
     */
    private void initOpenAnim() {
        //从0到90度，顺时针旋转视图，此时reverse参数为true，达到90度时动画结束时视图变得不可见，
        openAnimation = new Rotate3dAnimation(0, 90, centerX, centerY, depthZ, true);
        openAnimation.setDuration(duration);
        openAnimation.setFillAfter(true);
        openAnimation.setInterpolator(new AccelerateInterpolator());
        openAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                viewPagerCar.setVisibility(View.GONE);
                layPayment.setVisibility(View.VISIBLE);

                //从270到360度，顺时针旋转视图，此时reverse参数为false，达到360度动画结束时视图变得可见
                Rotate3dAnimation rotateAnimation = new Rotate3dAnimation(270, 360, centerX, centerY, depthZ, false);
                rotateAnimation.setDuration(duration);
                rotateAnimation.setFillAfter(true);
                rotateAnimation.setInterpolator(new DecelerateInterpolator());
                mContent.startAnimation(rotateAnimation);
            }
        });
    }

    /**
     * 卡牌文本介绍关闭效果：旋转角度与打开时逆行即可
     */
    private void initCloseAnim() {
        closeAnimation = new Rotate3dAnimation(360, 270, centerX, centerY, depthZ, true);
        closeAnimation.setDuration(duration);
        closeAnimation.setFillAfter(true);
        closeAnimation.setInterpolator(new AccelerateInterpolator());
        closeAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                viewPagerCar.setVisibility(View.VISIBLE);
                layPayment.setVisibility(View.GONE);

                Rotate3dAnimation rotateAnimation = new Rotate3dAnimation(90, 0, centerX, centerY, depthZ, false);
                rotateAnimation.setDuration(duration);
                rotateAnimation.setFillAfter(true);
                rotateAnimation.setInterpolator(new DecelerateInterpolator());
                mContent.startAnimation(rotateAnimation);
            }
        });
    }

    /**
     * 打开activity动画
     */
    protected void openAnimator() {
        long duration = 500;
        Display display = getWindowManager().getDefaultDisplay();
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.lay_index6);
        float[] scale = new float[2];
        scale[0] = 1.0f;
        scale[1] = 0.8f;
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(relativeLayout, "scaleX", scale).setDuration(duration);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(relativeLayout, "scaleY", scale).setDuration(duration);
        float[] rotation = new float[]{0, 10, 0};
        ObjectAnimator rotationX = ObjectAnimator.ofFloat(relativeLayout, "rotationX", rotation).setDuration(duration);

        float[] translation = new float[1];
        translation[0] = -display.getWidth() * 0.2f / 2;
        ObjectAnimator translationY = ObjectAnimator.ofFloat(relativeLayout, "translationY", translation).setDuration(duration);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, rotationX, translationY);
        animatorSet.setTarget(relativeLayout);
        animatorSet.start();
    }

    public void hideAnimator() {
        long duration = 500;
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.lay_index6);
        float[] scale = new float[2];
        scale[0] = 0.8f;
        scale[1] = 1.0f;
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(relativeLayout, "scaleX", scale).setDuration(duration);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(relativeLayout, "scaleY", scale).setDuration(duration);
        float[] rotation = new float[]{0, 10, 0};
        ObjectAnimator rotationX = ObjectAnimator.ofFloat(relativeLayout, "rotationX", rotation).setDuration(duration);

        float[] translation = new float[1];
        translation[0] = 0;
        ObjectAnimator translationY = ObjectAnimator.ofFloat(relativeLayout, "translationY", translation).setDuration(duration);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, rotationX, translationY);
        animatorSet.setTarget(relativeLayout);
        animatorSet.start();
    }

    /**
     * 获取用户信息
     */
    public void getUserInfo() {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userId", SPUtils.get(context, "userId", new Long(0l)));
        paramMap.put("token", SPUtils.get(context, "token", ""));

        HttpUtils.sendHttpPostRequest(Urls.URL_GET_USER_INFO, handlerGetUserInfo, paramMap, this);
    }

    //获取用户信息
    Handler handlerGetUserInfo = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                GetUserInfo jsonBean = (GetUserInfo) JsonUtils.jsonToObject(msg.obj.toString(), GetUserInfo.class);
                if (jsonBean != null) {
                    int code = jsonBean.getCode();
                    if (code == 0) {
                        userInfo = jsonBean.getContent();

                        tvNickName.setText(userInfo.getNickName().equals(" ") ? "" : userInfo.getNickName());
                        tvMobile.setText(userInfo.getMobile());

                        ImageLoader imageLoader = new ImageLoader(MyApplication.getHttpRequestQueue(), new BitmapCache());
                        ImageLoader.ImageListener listener = ImageLoader.getImageListener(ivUserImg, R.drawable.default_user, R.drawable.default_user);
                        if (userInfo.getAvatarUrl().contains("http")) {
                            imageLoader.get(userInfo.getAvatarUrl(), listener);
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
     * 获取绑定车辆信息
     */
    private void getBindCarList() {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userId", SPUtils.get(this, "userId", new Long(0l)));
        paramMap.put("token", SPUtils.get(this, "token", ""));
        HttpUtils.sendHttpPostRequest(Urls.URL_GET_BIND_CAR, handlerBindCar, paramMap, this);
    }

    Handler handlerBindCar = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                GetBindCar bindCar = (GetBindCar) JsonUtils.jsonToObject(msg.obj.toString(), GetBindCar.class);
                if (bindCar != null) {
                    int code = bindCar.getCode();
                    if (code == 0) {
                        List<BindCarInfo> resultList = bindCar.getContent();
                        bindCarList.clear();
                        bindCarList.addAll(resultList);
                        if (bindCarList.size() != 3) {
                            bindCarList.add(new BindCarInfo());
                        }
                        if (bindCarList.size() > 0 && MyApplication.currentCar.getPlateNumber() == null) {
                            MyApplication.currentCar = bindCarList.get(0);//设置当前车辆
                        }
                        if (MyApplication.isUpdateCar) { //更新
                            carAdapter = new CarPagerAdapter(IndexActivity.this, bindCarList);
                            viewPagerCar.setAdapter(carAdapter);
                            MyApplication.isUpdateCar = false;
                        } else{
                            carAdapter.notifyDataSetChanged();
                        }
                    } else if(code == 30004){
                        bindCarList.clear();
                        bindCarList.add(new BindCarInfo());
                        carAdapter.notifyDataSetChanged();
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
     * 重新开启定时器
     */
    private void startTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = UPTATE_VIEWPAGER;
                    message.arg1 = currentItem;
                    mHandler.sendMessage(message);
                }
            };
        }
        if (mTimer != null && mTimerTask != null) {
            mTimer.schedule(mTimerTask, delay, period);
        }
    }

    /**
     * 停止定时器
     */
    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mLocationClient.isStarted()) {
            mLocationClient.start();
        }

        getBindCarList();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (MyApplication.isUpdateUser) {
            getUserInfo();
            MyApplication.isUpdateUser = false;
        }
        if (!MyApplication.isLogin) {//注销
            tvUnLogin.setVisibility(View.VISIBLE);
            tvNickName.setVisibility(View.GONE);
            tvMobile.setVisibility(View.GONE);
            ivUserImg.setBackgroundResource(R.drawable.default_user);
        }
        if(MyApplication.isToLogin) {
            setUserInfo();
            MyApplication.isToLogin = false;
        }
        startTimer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLocationClient.stop();
        stopTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
//        if (mIat != null) {
//            mIat.cancel();
//            mIat.destroy();
//        }
        if (mTimer != null) {
            timerToken.cancel();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                ToastUtils.showShort(this, "再按一次退出程序");
                exitTime = System.currentTimeMillis();
            } else {
                //退出应用程序
                MyApplication.getInstance().AppExit();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}