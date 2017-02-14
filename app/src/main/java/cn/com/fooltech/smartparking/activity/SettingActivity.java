package cn.com.fooltech.smartparking.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.offline.MKOfflineMap;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.bean.AppInfo;
import cn.com.fooltech.smartparking.common.Urls;
import cn.com.fooltech.smartparking.bean.jsonbean.GetAppInfo;
import cn.com.fooltech.smartparking.sharesdk.OneKeyShareCallback;
import cn.com.fooltech.smartparking.sharesdk.ShareContentCustomizeDemo;
import cn.com.fooltech.smartparking.utils.AppUtils;
import cn.com.fooltech.smartparking.utils.DataCleanManager;
import cn.com.fooltech.smartparking.utils.ErrorUtils;
import cn.com.fooltech.smartparking.utils.HttpUtils;
import cn.com.fooltech.smartparking.utils.JsonUtils;
import cn.com.fooltech.smartparking.utils.ToastUtils;
import cn.com.fooltech.smartparking.utils.Utils;
import cn.com.fooltech.smartparking.view.ToggleButton;
import cn.sharesdk.framework.ShareSDK;

public class SettingActivity extends BaseActivity {
    @Bind(R.id.toggleButton_msg)
    ToggleButton tbIsReceiveMsg;
    @Bind(R.id.version_name)
    TextView tvVersionName;
    @Bind(R.id.cache_size)
    TextView tvCacheSize;
    private Context context = SettingActivity.this;
    private double currentVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        //初始化sharesdk
        ShareSDK.initSDK(this);

        initView();
    }

    private void initView() {
        String versionName = AppUtils.getVersionName(this);
        tvVersionName.setText(versionName);
        currentVersion = Utils.strToDouble(versionName);

        //接收推送消息
        tbIsReceiveMsg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(context, isChecked ? "ON" : "oFF", Toast.LENGTH_SHORT).show();
            }
        });

        String cacheSize = DataCleanManager.getTotalCacheSize(this);
        if (cacheSize.equals("0.0B")) {
            cacheSize = "0M";
        }
        tvCacheSize.setText(cacheSize);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            //返回
            case R.id.back_setting:
                finish();
                break;
            //意见反馈
            case R.id.feedback:
                startActivity(new Intent(this, FeedbackActivity.class));
                break;
            //分享
            case R.id.share:
                startActivity(new Intent(this,ShareFriendsActivity.class));
                break;
            //版本信息
            case R.id.version:
                Map<String, Object> paramMap = new HashMap<String, Object>();
                paramMap.put("appType", 2);
                HttpUtils.sendHttpPostRequest(Urls.URL_GET_APP_INFO, handlerVersion, paramMap, this);
                break;
            //清除缓存
            case R.id.clear_cache:
                DataCleanManager.clearAllCache(this);
                tvCacheSize.setText("0M");
                break;
            //地图下载
            case R.id.map_download:
                startActivity(new Intent(this, OfflineMapActivity.class));
                break;
        }
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
                        if ((Utils.strToDouble(info.getCurrentVer()) > currentVersion)) {  //更新
                            showVersionDialog(info);
                        } else {
                            ToastUtils.showShort(context, "当前已是最新版本");
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
        builder.setCancelable(true);
        final AlertDialog dialog = builder.create();
        dialog.show();

        Window window = dialog.getWindow();
        window.setContentView(R.layout.dialog_version);

        TextView tvVersion = (TextView) window.findViewById(R.id.version_new);
        Button btnCancle = (Button) window.findViewById(R.id.cancle_update);
        Button btnUpdate = (Button) window.findViewById(R.id.update);

        tvVersion.setText("发现新版本: " + info.getCurrentVer());

        //取消
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        //更新
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                updateVersion(info);
            }
        });
    }

    private void updateVersion(AppInfo info) {
//        String url = "http://g.pc6.com/3437484358/apk/360MobileSafe.apk";
        Intent intent = new Intent(Intent.ACTION_VIEW,
                (Uri.parse(info.getDownloadUrl()))
        ).addCategory(Intent.CATEGORY_BROWSABLE)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

//    private void share() {
//        OnekeyShare oks = new OnekeyShare();
//        //关闭sso授权
//        oks.disableSSOWhenAuthorize();
//        //通过OneKeyShareCallback来修改不同平台分享的内容
//        oks.setShareContentCustomizeCallback(new ShareContentCustomizeDemo());
//        oks.setCallback(new OneKeyShareCallback());
//        // 启动分享GUI
//        oks.show(this);
//    }

}
