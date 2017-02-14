package cn.com.fooltech.smartparking.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.common.Urls;
import cn.com.fooltech.smartparking.utils.ErrorUtils;
import cn.com.fooltech.smartparking.utils.HMACUtils;
import cn.com.fooltech.smartparking.utils.HttpUtils;
import cn.com.fooltech.smartparking.utils.JsonUtils;
import cn.com.fooltech.smartparking.utils.MD5Utils;
import cn.com.fooltech.smartparking.utils.TimerUtils;
import cn.com.fooltech.smartparking.utils.ToastUtils;
import cn.com.fooltech.smartparking.utils.Utils;
import cn.com.fooltech.smartparking.view.CustomEditText;


public class ResetPasswordActivity2 extends BaseActivity {
    @Bind(R.id.mobile)
    TextView tvPhone;
    @Bind(R.id.mobile_code)
    EditText etCode;
    @Bind(R.id.btn_get_code)
    Button btnGetCode;
    @Bind(R.id.et_new_pwd)
    CustomEditText etNewPwd;
    @Bind(R.id.vis_new_pwd)
    CheckBox cbVisPwd;
    private Context context = ResetPasswordActivity2.this;
    private String mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password_activity2);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        mobile = getIntent().getStringExtra("mobile");

        tvPhone.setText(mobile);

        //是否显示密码
        cbVisPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) { //如果选中，显示密码
                    etNewPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else { //否则隐藏密码
                    etNewPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_reset2:
                finish();
                break;
            //获取验证码
            case R.id.btn_get_code:
                TimerUtils timer = new TimerUtils(btnGetCode, 60000, 1000);
                timer.start();

                long timestamp = System.currentTimeMillis();
                String sign = HMACUtils.getSign(mobile + timestamp);

                Map<String, Object> paramMap = new HashMap<String, Object>();
                paramMap.put("mobile", mobile);
                paramMap.put("timestamp", timestamp);
                paramMap.put("sign", sign);
                paramMap.put("opType", 2);

                HttpUtils.sendHttpPostRequest(Urls.URL_MOBILE_CODE, handlerCode, paramMap, ResetPasswordActivity2.this);
                break;
            //确认
            case R.id.btn_sure:
                String code = etCode.getText().toString();
                String newPwd = etNewPwd.getText().toString();
                if (code.isEmpty()) {
                    ToastUtils.showShort(context, "请输入验证码");
                    return;
                } else if (newPwd.isEmpty()) {
                    ToastUtils.showShort(context, "请输入新密码");
                    return;
                }else if (newPwd.length() < 6) {
                    ToastUtils.showShort(this, "密码不能少于6位");
                    return;
                }
                String md5Pwd = MD5Utils.encode(mobile + MD5Utils.encode(newPwd));//密码加密
                Map<String, Object> paramMap2 = new HashMap<String, Object>();
                paramMap2.put("mobile", mobile);
                paramMap2.put("validCode", code);
                paramMap2.put("newPassword", md5Pwd);

//                HttpUtils.sendHttpPostRequest(Urls.URL_RESET_PASSWORD, handlerSure, paramMap2, ResetPasswordActivity2.this);
                break;
        }
    }

    Handler handlerCode = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                Map<String, Object> map = JsonUtils.jsonToMap(msg.obj.toString());
                if (map != null && map.size() > 0) {
                    int code = (int) map.get("code");
                    if (code == 0) {
//                        String validCode = map.get("validCode").toString();
//                        etCode.setText(validCode);
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

    //确定
    Handler handlerSure = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                int code = JsonUtils.getCodeResult(msg.obj.toString());
                if (code == 0) {
                    ToastUtils.showShort(context, "修改成功");
                    startActivity(new Intent(context, LoginActivity.class));
                } else {
                    ErrorUtils.errorCode(context, code);
                }
            }
            super.handleMessage(msg);
        }
    };

}
