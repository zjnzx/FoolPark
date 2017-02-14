package cn.com.fooltech.smartparking.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {
    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("====onCreate=========","==========");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.e("====onStart=========","==========");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent intent2 = new Intent();
        intent2.setAction("cn.com.fooltech.smartparking.offline");
        //要发送的内容
//        intent.putExtra("errCode", resp.errCode);
        //发送 一个无序广播
        sendBroadcast(intent2);
        Log.e("====onStartCommand=========","==========");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
