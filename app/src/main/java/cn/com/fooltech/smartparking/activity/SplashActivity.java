package cn.com.fooltech.smartparking.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.bean.BindCarInfo;
import cn.com.fooltech.smartparking.bean.UserInfo;
import cn.com.fooltech.smartparking.common.Common;
import cn.com.fooltech.smartparking.common.Urls;
import cn.com.fooltech.smartparking.bean.jsonbean.Login;
import cn.com.fooltech.smartparking.utils.AppUtils;
import cn.com.fooltech.smartparking.utils.ErrorUtils;
import cn.com.fooltech.smartparking.utils.HMACUtils;
import cn.com.fooltech.smartparking.utils.HttpUtils;
import cn.com.fooltech.smartparking.utils.JsonUtils;
import cn.com.fooltech.smartparking.utils.MD5Utils;
import cn.com.fooltech.smartparking.utils.SPUtils;
import cn.com.fooltech.smartparking.utils.ToastUtils;
import cn.com.fooltech.smartparking.utils.Utils;

public class SplashActivity extends BaseActivity {
    private Context context = SplashActivity.this;
    private  boolean isFirst,isLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        isFirst = (boolean) SPUtils.get(this,"isFirst",true);//是否第一次使用
        isLogin = (boolean) SPUtils.get(this,"isLogin",false);//是否已经登陆

        Handler handler = new Handler();
        handler.postDelayed(new RunSplash(), 1000);
    }

    class RunSplash implements Runnable{
        public void run() {
            if(isFirst){
                startActivity(new Intent(getApplication(), GuideActivity.class));
                SPUtils.put(SplashActivity.this,"isFirst", false);
            }else {
//                Common.clearData(getApplication());
                if(isLogin){
                    MyApplication.isLogin = true;
                    login();
                }else {
                    startActivity(new Intent(getApplication(), IndexActivity.class));
                    finish();
                }
            }
        }
    }

    public void login(){
        String osVer = AppUtils.getOsVersion();
        String mobile = (String) SPUtils.get(this,"mobile","");
        String password = (String) SPUtils.get(this,"password","");
        String md5Pwd = MD5Utils.encode(mobile + MD5Utils.encode(password));//密码加密
        long timestamp = System.currentTimeMillis();
        String sign = HMACUtils.getSign(mobile + md5Pwd + timestamp);

        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("mobile",mobile);
        paramMap.put("password",md5Pwd);
        paramMap.put("timestamp",timestamp);
        paramMap.put("sign",sign);
        paramMap.put("osType","Android");
        paramMap.put("appVer","1.0");
        paramMap.put("osVer",osVer);
        HttpUtils.sendHttpPostRequest(Urls.URL_LOGIN, handlerLogin, paramMap,this);
    }

    Handler handlerLogin = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            }else{
                Login jsonLogin = (Login) JsonUtils.jsonToObject(msg.obj.toString(), Login.class);
                if (jsonLogin != null) {
                    int code = jsonLogin.getCode();
                    if (code == 0) { //登录成功
//                        startActivity(new Intent(context, IndexActivity.class));
//                        finish();
                        UserInfo userInfo = jsonLogin.getContent().getUser();
                        String token = jsonLogin.getContent().getToken();
                        MyApplication.newMessage = jsonLogin.getContent().getNewMessage();
                        SPUtils.put(context, "token", token);
                        SPUtils.putObject(context, "userInfo", userInfo);
                        MobclickAgent.onProfileSignIn(userInfo.getUserId() + "");//友盟统计
                    } else {
                        ErrorUtils.errorCode(context, code);
                    }
                } else {
                    ToastUtils.showShort(context, "服务器请求错误");
                }
            }
            startActivity(new Intent(context, IndexActivity.class));
            finish();
        }
    };

}
