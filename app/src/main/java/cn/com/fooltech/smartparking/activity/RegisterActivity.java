package cn.com.fooltech.smartparking.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;
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
import cn.com.fooltech.smartparking.utils.DateUtils;
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


public class RegisterActivity extends BaseActivity {
    @Bind(R.id.et_phone)
    CustomEditText etPhone;
    @Bind(R.id.et_identify_code)
    CustomEditText etIdentifyCode;
    @Bind(R.id.bt_get_code)
    Button btCode;
    @Bind(R.id.et_password)
    CustomEditText etPassword;
    @Bind(R.id.vis_pwd_res)
    CheckBox cbVisPwd;
    @Bind(R.id.cb_agree)
    CheckBox cbIsAgree;
    @Bind(R.id.user_agreement)
    TextView tvAgreement;
    private Context context = RegisterActivity.this;
    private String phoneNum = "";
    private String md5Str = "", pwd;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        initView();

    }

    private void initView() {
        tvAgreement.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        tvAgreement.getPaint().setAntiAlias(true);//抗锯齿

        //是否显示密码
        cbVisPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String password = etPassword.getText().toString();
                if (b) {
                    //如果选中，显示密码
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    etPassword.setSelection(password.length());
                } else {
                    //否则隐藏密码
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    etPassword.setSelection(password.length());
                }
            }
        });

    }
    @OnTextChanged(value = R.id.et_password,callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterTextChanged(Editable editable){
        if (!"".equals(editable.toString())) {
            cbIsAgree.setChecked(true);
        } else {
            cbIsAgree.setChecked(false);
        }
    }

    /**
     * 设置textview颜色和点击事件
     */
//    private void setTextView() {
//        tvCopyright.setText("我已阅读并接受");
//        String copyright = "版权声明";
//        String privacy = "隐私保护";
//        SpannableString copyStr = new SpannableString(copyright);
//        SpannableString priStr = new SpannableString(privacy);
//
//        copyStr.setSpan(new ClickableSpan() {
//            @Override
//            public void updateDrawState(TextPaint ds) {
//                super.updateDrawState(ds);
//                ds.setColor(getResources().getColor(R.color.text_link));       //设置文件颜色
//                ds.setUnderlineText(true);      //设置下划线
//            }
//
//            @Override
//            public void onClick(View widget) {
//                Log.i("", "onTextClick....copyright....");
//            }
//        }, 0, copyright.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//        priStr.setSpan(new ClickableSpan() {
//            @Override
//            public void updateDrawState(TextPaint ds) {
//                super.updateDrawState(ds);
//                ds.setColor(getResources().getColor(R.color.text_link));       //设置文件颜色
//                ds.setUnderlineText(true);      //设置下划线
//            }
//
//            @Override
//            public void onClick(View widget) {
//                Log.i("", "onTextClick...privacy.....");
//            }
//        }, 0, copyright.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        tvCopyright.setHighlightColor(Color.TRANSPARENT); //设置点击后的颜色为透明，否则会一直出现高亮
//        tvCopyright.append(copyStr);
//        tvCopyright.append("和");
//        tvCopyright.append(priStr);
//        tvCopyright.append("条款");
//        tvCopyright.setMovementMethod(LinkMovementMethod.getInstance());//开始响应点击事件
//    }

    public void onClick(View view) {
        switch (view.getId()) {
            //获取验证码
            case R.id.bt_get_code:
                getValidCode();
                break;
            //注册
            case R.id.bt_register:
                register();
                break;
            //返回
            case R.id.back_resg:
                finish();
                break;
            //用户协议
            case R.id.user_agreement:
                Common.getLinkUrl(this, handlerAgree, 4);
                break;
        }
    }

    Handler handlerAgree = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                Map<String, Object> resultMap = JsonUtils.jsonToMap(msg.obj.toString());
                if (resultMap != null && resultMap.size() > 0) {
                    int code = (int) resultMap.get("code");
                    if (code == 0) {
                        String linkUrl = (String) resultMap.get("linkUrl");
                        if (linkUrl.contains("http")) {
                            Intent intent = new Intent(context, ActivityDetailActivity.class);
                            intent.putExtra("linkUrl", linkUrl);
                            intent.putExtra("detail", "用户协议");
                            context.startActivity(intent);
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

    private void getValidCode() {
        phoneNum = etPhone.getText().toString();
        if (phoneNum.isEmpty()) {
            ToastUtils.showShort(this, "请输入手机号");
            return;
        } else if (phoneNum.length() != 11) {
            ToastUtils.showShort(this, "手机号错误");
            return;
        }
        TimerUtils timer = new TimerUtils(btCode, 60000, 1000);
        timer.start();

        long timestamp = System.currentTimeMillis();
        String sign = HMACUtils.getSign(phoneNum + timestamp);

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("mobile", phoneNum);
        paramMap.put("timestamp", timestamp);
        paramMap.put("sign", sign);
        paramMap.put("opType", 1);
        HttpUtils.sendHttpPostRequest(Urls.URL_MOBILE_CODE, handlerCode, paramMap, this);
    }


    /**
     * 注册
     */
    private void register() {
        phoneNum = etPhone.getText().toString();
        String idenCode = etIdentifyCode.getText().toString();
        pwd = etPassword.getText().toString();
        if (phoneNum.isEmpty()) {
            ToastUtils.showShort(this, "请输入手机号");
            return;
        } else if (idenCode.isEmpty()) {
            ToastUtils.showShort(this, "请输入验证码");
            return;
        } else if (pwd.isEmpty()) {
            ToastUtils.showShort(this, "请输入密码");
            return;
        } else if (pwd.length() < 6) {
            ToastUtils.showShort(this, "密码不能少于6位");
            return;
        } else if(!cbIsAgree.isChecked()){
            ToastUtils.showShort(this, "请选择用户协议");
            return;
        }

        String md5Str = MD5Utils.encode(phoneNum + MD5Utils.encode(pwd));
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("mobile", phoneNum);
        paramMap.put("validCode", idenCode);
        paramMap.put("password", md5Str);

        HttpUtils.sendHttpPostRequest(Urls.URL_REGISTER, handlerRes, paramMap, this);
    }

    //获取验证码
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
//                        etIdentifyCode.setText(validCode);
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

    //注册
    Handler handlerRes = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                Login jsonLogin = (Login) JsonUtils.jsonToObject(msg.obj.toString(), Login.class);
                if (jsonLogin != null) {
                    int code = jsonLogin.getCode();
                    if (code == 0) { //成功
                        ToastUtils.showShort(context, "注册成功");
                        MyApplication.isLogin = true;
                        SPUtils.put(context, "isLogin", true);
//                        SPUtils.clear(context);

                        UserInfo userInfo = jsonLogin.getContent().getUser();
                        String token = jsonLogin.getContent().getToken();
                        MyApplication.newMessage = jsonLogin.getContent().getNewMessage();

                        SPUtils.putObject(context, "userInfo", userInfo);
                        SPUtils.put(context, "password", pwd);
                        SPUtils.put(context, "token", token);
                        SPUtils.put(context, "userId", userInfo.getUserId());
                        SPUtils.put(context, "mobile", userInfo.getMobile());

                        showDialogVoucher();
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
     * 送优惠券
     */
    private void showDialogVoucher() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Translucent);
        dialog = builder.create();
        dialog.show();

        Window window = dialog.getWindow();
        window.setContentView(R.layout.dialog_register);
        TextView tvTime = (TextView) window.findViewById(R.id.time_limit2);
//        TextView tvStatus = (TextView) window.findViewById(R.id.status2);
        Button btnBind = (Button) window.findViewById(R.id.btn_bind_car);
        ImageView ivClose = (ImageView) window.findViewById(R.id.close_x);

        Date date = new Date();
        String endDate = DateUtils.getDate2(DateUtils.getNextDate(date));
        tvTime.setText("有效日期至: " + endDate);

        btnBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(new Intent(context, CarAddActivity.class));
                intent.putExtra("reg", true);//注册成功跳转的标志
                startActivity(intent);
                finish();
            }
        });
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                startActivity(new Intent(context, IndexActivity.class));
                finish();
            }
        });
    }

}
