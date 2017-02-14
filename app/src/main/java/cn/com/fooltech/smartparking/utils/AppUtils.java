package cn.com.fooltech.smartparking.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.orhanobut.logger.Logger;

/**
 * Created by YY on 2016/8/22.
 */
public class AppUtils {
    /**
     * 获取应用程序名称
     */
    public static String getAppName(Context context)
    {
        try
        {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * [获取应用程序版本名称信息]
     *
     * @param context
     * @return 当前应用的版本名称
     */
//    public static String getVersionName(Context context)
//    {
//        try
//        {
//            PackageManager packageManager = context.getPackageManager();
//            PackageInfo packageInfo = packageManager.getPackageInfo(
//                    context.getPackageName(), 0);
//            return packageInfo.versionName;
//
//        } catch (PackageManager.NameNotFoundException e)
//        {
//            e.printStackTrace();
//        }
//        return null;
//    }

    /**
     * 获取操作系统版本
     * @return
     */
    public static String getOsVersion(){
        return android.os.Build.VERSION.RELEASE;
    }

    //版本号
    public static int getVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
    }

    //版本名
    public static String getVersionName(Context context) {
        return getPackageInfo(context).versionName;
    }

    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }

    /**
     * 判断是否开启权限
     * @param context
     * @param permiss
     * @return
     */
//    public static boolean hasPermission(Context context,String permiss){
//        PackageManager pm = context.getPackageManager();
//        boolean permission = (PackageManager.PERMISSION_GRANTED ==
//                pm.checkPermission(permiss, "cn.com.fooltech.smartparking"));
//        if (!permission) {
//            ToastUtils.showShort(context,"没有权限,请在设置中开启权限");
//        }
//        return permission;
//    }

//    public static boolean checkWriteExternalPermission(Context context,String permission) {
//        int res = context.checkCallingOrSelfPermission(permission);
//        return (res == PackageManager.PERMISSION_GRANTED);  //ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
//    }

    /**
     * 判断录音权限是否可用
     *
     * @return true 同意 false 拒绝
     */
    public static boolean voiceCanUse() {

        try {
            AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.MIC, 22050, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, AudioRecord.getMinBufferSize(22050, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT));
            record.startRecording();
            int recordingState = record.getRecordingState();
            if(recordingState == AudioRecord.RECORDSTATE_STOPPED){
                return false;
            }
            record.release();
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    /**
     *  判断相机权限是否可用,返回true 表示可以使用  返回false表示不可以使用
     */
    public static boolean cameraIsCanUse(Context context) {
        boolean isCanUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
            Camera.Parameters mParameters = mCamera.getParameters();
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            isCanUse = false;
        }

        if (mCamera != null) {
            try {
                mCamera.release();
            } catch (Exception e) {
                e.printStackTrace();
                return isCanUse;
            }
        }
        return isCanUse;
    }


    /**
     * 判断系统是否是6.0以上
     * @return
     */
    public static boolean isOs6(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return true;
        }
        return false;
    }

}
