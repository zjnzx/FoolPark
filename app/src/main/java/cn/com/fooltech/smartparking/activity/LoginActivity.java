package cn.com.fooltech.smartparking.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
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
import cn.com.fooltech.smartparking.view.CustomEditText;


public class LoginActivity extends BaseActivity {
    @Bind(R.id.et_phone_num)
    CustomEditText etPhone;
    @Bind(R.id.et_pwd)
    CustomEditText etPassword;
    @Bind(R.id.vis_pwd)
    CheckBox cbVisPwd;
    private Context context = LoginActivity.this;
    private String md5Pwd, phoneNum, sign;
    private long timestamp;
    private Dialog dialog;
    private String osVer = "", pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        initView();


    }

    private void initView() {
        etPhone.setText((String) SPUtils.get(this, "mobile", ""));
        etPassword.setText((String) SPUtils.get(this, "password", ""));

        osVer = AppUtils.getOsVersion();

        //是否显示密码
        cbVisPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String password = etPassword.getText().toString();
                if (b) { //如果选中，显示密码
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    etPassword.setSelection(password.length());
                } else { //否则隐藏密码
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    etPassword.setSelection(password.length());
                }
            }
        });
    }
    //账号监听事件
    @OnTextChanged(value = R.id.et_phone_num,callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterTextChanged(Editable editable){
        String phone = editable.toString();
        if (("").equals(phone)) {
            etPassword.setText("");
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_login:
//                Common.clearData(context);
                startActivity(new Intent(this, IndexActivity.class));
                break;
            case R.id.register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.bt_login:
                phoneNum = etPhone.getText().toString();
                pwd = etPassword.getText().toString();
                if (phoneNum.isEmpty()) {
                    ToastUtils.showShort(this, "请输入手机号");
                    break;
                } else if (pwd.isEmpty()) {
                    ToastUtils.showShort(this, "请输入密码");
                    break;
                }
                showDialog();
                md5Pwd = MD5Utils.encode(phoneNum + MD5Utils.encode(pwd));//密码加密
                timestamp = System.currentTimeMillis();
                sign = HMACUtils.getSign(phoneNum + md5Pwd + timestamp);

                login();
                break;
            //忘记密码
            case R.id.forget_pwd:
                startActivity(new Intent(this, ResetPasswordActivity.class));
                break;
        }
    }

    private void login() {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("mobile", phoneNum);
        paramMap.put("password", md5Pwd);
        paramMap.put("timestamp", timestamp);
        paramMap.put("sign", sign);
        paramMap.put("osType", "Android");
        paramMap.put("appVer", "1.0");
        paramMap.put("osVer", osVer);
        HttpUtils.sendHttpPostRequest(Urls.URL_LOGIN, mHandler, paramMap, this);

    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_NET) {
                dialog.cancel();
            } else if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                Login jsonLogin = (Login) JsonUtils.jsonToObject(msg.obj.toString(), Login.class);
                if (jsonLogin != null) {
                    int code = jsonLogin.getCode();
                    if (code == 0) { //登录成功
                        MyApplication.isLogin = true;
                        SPUtils.put(context, "isLogin", true);
                        startActivity(new Intent(context, IndexActivity.class));
                        UserInfo userInfo = jsonLogin.getContent().getUser();
                        String token = jsonLogin.getContent().getToken();
                        MyApplication.newMessage = jsonLogin.getContent().getNewMessage();

                        SPUtils.putObject(context, "userInfo", userInfo);
                        SPUtils.put(context, "password", pwd);
                        SPUtils.put(context, "token", token);
                        SPUtils.put(context, "userId", userInfo.getUserId());
                        SPUtils.put(context, "mobile", userInfo.getMobile());
                        MobclickAgent.onProfileSignIn(userInfo.getUserId() + "");//友盟统计
                    } else {
                        ErrorUtils.errorCode(context, code);
                    }
                } else {
                    ToastUtils.showShort(context, "服务器请求错误");
                }
            }
            dialog.dismiss();
        }
    };


    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        dialog = builder.create();
        dialog.show();

        Window window = dialog.getWindow();
        window.setContentView(R.layout.dialog_login);

    }

}
