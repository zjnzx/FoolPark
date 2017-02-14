package cn.com.fooltech.smartparking.utils;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import cn.com.fooltech.smartparking.application.MyApplication;

/**
 * Created by YY on 2016/8/30.
 */
public class HMACUtils {
    public static String getSign(String content){
        String result = "";
        SecretKeySpec localSecretKeySpec = null;//加密密钥
        try {
            localSecretKeySpec = new SecretKeySpec(MyApplication.SECRET_KEY.getBytes("UTF-8"), "HmacSHA1");
            Mac localMac = Mac.getInstance("HmacSHA1");
            localMac.init(localSecretKeySpec);
            localMac.update(content.getBytes("UTF-8"));//加密内容，这里使用时间
            result = Base64.encodeToString(localMac.doFinal(), 0).trim(); //获取加密结果并转BASE64
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return result;
    }
}
