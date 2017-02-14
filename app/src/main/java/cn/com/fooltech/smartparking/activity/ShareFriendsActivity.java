package cn.com.fooltech.smartparking.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.common.Common;
import cn.com.fooltech.smartparking.common.Urls;
import cn.com.fooltech.smartparking.utils.ErrorUtils;
import cn.com.fooltech.smartparking.utils.HttpUtils;
import cn.com.fooltech.smartparking.utils.JsonUtils;
import cn.com.fooltech.smartparking.utils.SPUtils;
import cn.com.fooltech.smartparking.utils.ToastUtils;
import cn.com.fooltech.smartparking.utils.Utils;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.favorite.WechatFavorite;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

public class ShareFriendsActivity extends BaseActivity {
    private Context context = ShareFriendsActivity.this;
    @Bind(R.id.back_share)
    ImageView ivBack;
    private Platform platform;
    private String shareUrl,imgUrl,shareTitle,shareDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_friends);
        ButterKnife.bind(this);
        //初始化sharesdk
        ShareSDK.initSDK(this);
    }

    @OnClick({R.id.back_share, R.id.share_moment, R.id.share_wechat, R.id.share_favorite})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_share:
                finish();
                break;
            case R.id.share_moment:
                if (!Common.isLogin(this)) return;
                platform = ShareSDK.getPlatform(WechatMoments.NAME);
                getShareUrl();
                break;
            case R.id.share_wechat:
                if (!Common.isLogin(this)) return;
                platform = ShareSDK.getPlatform(Wechat.NAME);
                getShareUrl();
                break;
            case R.id.share_favorite:
                if (!Common.isLogin(this)) return;
                platform = ShareSDK.getPlatform(WechatFavorite.NAME);
                getShareUrl();
                break;
        }
    }

    /**
     * 获取分享的url
     */
    private void getShareUrl() {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userId", SPUtils.get(this, "userId", new Long(0l)));
        paramMap.put("token", SPUtils.get(this, "token", ""));
        paramMap.put("isAppInstalled", 1);
        HttpUtils.sendHttpPostRequest(Urls.URL_GET_SHARE_URL, mHandler, paramMap, this);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                Map<String, Object> map = JsonUtils.jsonToMap(msg.obj.toString());
                if (map != null && map.size() > 0) {
                    int code = (int) map.get("code");
                    if (code == 0) {
                        shareUrl = map.get("shareUrl").toString();
                        imgUrl = map.get("imgUrl").toString();
                        shareTitle = map.get("shareTitle").toString();
                        shareDesc = map.get("shareDesc").toString();
                        shareFriends(platform);
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

    private void shareFriends(Platform platform){
        Platform.ShareParams sp = new Platform.ShareParams();
        sp.setTitle(shareTitle);
        sp.setText(shareDesc);
        sp.setUrl(shareUrl);
        sp.setImageUrl(imgUrl);
        sp.setShareType(Platform.SHARE_WEBPAGE);
        platform.setPlatformActionListener(platformActionListener);
        //执行分享
        platform.share(sp);
    }

    PlatformActionListener platformActionListener = new PlatformActionListener(){

        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {

        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
//            ToastUtils.showShort(context,"---"+platform+"--"+i);
        }

        @Override
        public void onCancel(Platform platform, int i) {

        }
    };
}
