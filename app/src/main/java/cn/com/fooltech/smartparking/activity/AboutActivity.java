package cn.com.fooltech.smartparking.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.common.Common;
import cn.com.fooltech.smartparking.interface1.PermissionListener;
import cn.com.fooltech.smartparking.utils.AppUtils;
import cn.com.fooltech.smartparking.utils.ErrorUtils;
import cn.com.fooltech.smartparking.utils.JsonUtils;
import cn.com.fooltech.smartparking.utils.ToastUtils;
import cn.com.fooltech.smartparking.utils.Utils;

public class AboutActivity extends BaseActivity {
    @Bind(R.id.mobile_show4)
    TextView tvMobile;
    private Context context = AboutActivity.this;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_about:
                finish();
                break;
            case R.id.help:
//                    startActivity(new Intent(this, PositionAddActivity.class));
                Common.getLinkUrl(this, handlerHelp, 1);
                break;
            case R.id.wechat:
                startActivity(new Intent(this,WechatActivity.class));
                break;
            case R.id.lay_tel:
                showPhoneDialog();
                break;
        }
    }

    /**
     * 显示打电话对话框
     */
    private void showPhoneDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        dialog = builder.create();
        dialog.show();

        Window window = dialog.getWindow();
        window.setContentView(R.layout.dialog_phone);

        TextView phone = (TextView) window.findViewById(R.id.phone);
        Button btnCancle = (Button) window.findViewById(R.id.cancle_phone);
        Button btnCall = (Button) window.findViewById(R.id.phone_call);

        phone.setText(tvMobile.getText().toString());

        //取消
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        //呼叫
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if(AppUtils.isOs6()){
                        requestRuntimePermission(new String[]{Manifest.permission.CALL_PHONE}, new PermissionListener() {
                            @Override
                            public void onGranted() {
                                makeCall();
                            }

                            @Override
                            public void onDenied(List<String> deniedPermission) {
                                showLocationDialog();
                            }
                        });
                    }else {
                        makeCall();
                    }
            }
        });
    }

    private void makeCall(){
        try {
            dialog.dismiss();
            //用intent启动拨打电话
            Intent intent = new Intent(Intent.ACTION_CALL);
            Uri uri = Uri.parse("tel:" + tvMobile.getText().toString());
            intent.setData(uri);
            startActivity(intent);
        }catch (SecurityException e){
            e.printStackTrace();
        }
    }

    Handler handlerHelp = new Handler() {
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
                            intent.putExtra("detail", "使用帮助");
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

    /**
     * 显示打电话对话框
     */
    private void showLocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true)
                .setMessage(getResources().getString(R.string.dialog_phone))
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).show();
    }

}
