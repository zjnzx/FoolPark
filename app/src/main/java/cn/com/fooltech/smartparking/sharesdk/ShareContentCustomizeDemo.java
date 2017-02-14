package cn.com.fooltech.smartparking.sharesdk;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;

/**
 * Created by YY on 2016/8/3.
 */
public class ShareContentCustomizeDemo implements ShareContentCustomizeCallback {

    public void onShare(Platform platform, Platform.ShareParams paramsToShare) {

//        String title = platform.getContext().getString(R.string.app_name);
        String text = "我是分享文本111";
        paramsToShare.setUrl("http://sharesdk.cn");
        paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
        if ("Wechat".equals(platform.getName())) {
            paramsToShare.setText(text);
        }else if("WechatMoments".equals(platform.getName())){
            paramsToShare.setText(text);
        }
    }

}
