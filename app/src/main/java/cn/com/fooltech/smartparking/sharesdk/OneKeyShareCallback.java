package cn.com.fooltech.smartparking.sharesdk;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;

/**
 * Created by YY on 2016/8/3.
 */
public class OneKeyShareCallback implements PlatformActionListener {
    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> stringObjectHashMap) {

    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {

    }

    @Override
    public void onCancel(Platform platform, int i) {

    }
}
