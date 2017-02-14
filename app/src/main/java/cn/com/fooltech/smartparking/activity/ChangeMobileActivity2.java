package cn.com.fooltech.smartparking.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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
import cn.com.fooltech.smartparking.utils.SPUtils;
import cn.com.fooltech.smartparking.utils.TimerUtils;
import cn.com.fooltech.smartparking.utils.ToastUtils;
import cn.com.fooltech.smartparking.utils.Utils;
import cn.com.fooltech.smartparking.view.CustomEditText;


public class ChangeMobileActivity2 extends BaseActivity {
    @Bind(R.id.back_change_mobile2)
    ImageView ivBack;
    @Bind(R.id.new_mobile)
    CustomEditText etNewMobile;
    @Bind(R.id.mobile_code2)
    EditText etCode;
    @Bind(R.id.btn_get_code2)
    Button btnGetCode;
    @Bind(R.id.sure_change_mobile)
    Button btnSure;
    private Context context = ChangeMobileActivity2.this;
    private String code, newMobile, md5Pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_mobile_activity2);
        ButterKnife.bind(this);

        intView();
    }

    private void intView() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //获取验证码
        btnGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newMobile = etNewMobile.getText().toString();
                if (newMobile.isEmpty()) {
                    ToastUtils.showShort(context, "请输入手机号");
                    return;
                } else if (newMobile.length() != 11) {
                    ToastUtils.showShort(context, "手机号错误");
                    return;
                }
                TimerUtils timer = new TimerUtils(btnGetCode, 60000, 1000);
                timer.start();
                getCode(newMobile);
            }
        });

        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                code = etCode.getText().toString();
                newMobile = etNewMobile.getText().toString();
                if (newMobile.isEmpty()) {
                    ToastUtils.showShort(context, "请输入新手机号");
                    return;
                } else if (code.isEmpty()) {
                    ToastUtils.showShort(context, "请输入验证码");
                    return;
                }
                String oldPwd = (String) SPUtils.get(context, "password", "");
                md5Pwd = MD5Utils.encode(newMobile + MD5Utils.encode(oldPwd));//密码加密
                changeMobile();
            }
        });
    }

    /**
     * 获取验证码
     *
     * @param newMobile
     */
    private void getCode(String newMobile) {
        long timestamp = System.currentTimeMillis();
        String sign = HMACUtils.getSign(newMobile + timestamp);

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("mobile", newMobile);
        paramMap.put("timestamp", timestamp);
        paramMap.put("sign", sign);
        paramMap.put("opType", 3);
        HttpUtils.sendHttpPostRequest(Urls.URL_MOBILE_CODE, handlerCode, paramMap, this);
    }

    /**
     * 更换手机号
     */
    private void changeMobile() {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userId", SPUtils.get(context, "userId", new Long(0l)));
        paramMap.put("token", SPUtils.get(context, "token", ""));
        paramMap.put("newMobile", newMobile);
        paramMap.put("validCode", code);
        paramMap.put("password", md5Pwd);

        HttpUtils.sendHttpPostRequest(Urls.URL_CHANGE_MOBILE, handlerSure, paramMap, this);
    }


    //验证码
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

    //更换手机号
    Handler handlerSure = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                int code = JsonUtils.getCodeResult(msg.obj.toString());
                if (code == 0) {
                    ToastUtils.showShort(context, "更换成功");
                    startActivity(new Intent(context, LoginActivity.class));
                    finish();
                } else {
                    ErrorUtils.errorCode(context, code);
                }
            }
            super.handleMessage(msg);
        }
    };


}
