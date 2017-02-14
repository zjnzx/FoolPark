package cn.com.fooltech.smartparking.utils;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

/**
 * Created by YY on 2016/7/4.
 */
public class ToastUtils {

    public static void showShort(Context context,String message){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }
}
