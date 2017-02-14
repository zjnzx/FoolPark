package cn.com.fooltech.smartparking.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import com.orhanobut.logger.Logger;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import cn.com.fooltech.smartparking.application.MyApplication;
import okhttp3.MediaType;

/**
 * Created by YY on 2016/7/4.
 */
public class HttpUtils {
    public static String TAG= "HttpUtils";
    public static final int READ_TIME_OUT = 5000;
    public static final int CONNECT_TIME_OUT = 5000;
    private static final String CHARSET = "utf-8"; // 设置编码\
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    /**
     * 发送http请求网络(get方式)
     * @param urlSr
     * @param handler
     */
    public static void sendHttpGetRequest(final String urlSr, final Handler handler){
        new Thread(){
            @Override
            public void run() {
                HttpURLConnection conn = null;
                StringBuffer stringBuffer = null;
                stringBuffer = new StringBuffer();
                try{
                    URL url = new URL(urlSr);
                    conn = (HttpURLConnection) url.openConnection();//打开连接
                    conn.setDoInput(true);//允许输入流
                    conn.setDoInput(true);//允许输出流
                    conn.setUseCaches(false);//不使用缓存
                    conn.setRequestMethod("GET");//get请求方式
                    conn.setReadTimeout(READ_TIME_OUT);//设置超时时间
                    conn.setConnectTimeout(CONNECT_TIME_OUT);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine = "";
                    while((inputLine = reader.readLine()) != null){
                        stringBuffer.append(inputLine).append("\n");
                    }
                    //发送返回的数据
                    if(handler != null){
                        Message msg = new Message();
                        msg.obj = stringBuffer.toString();
                        handler.sendMessage(msg);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(conn != null){
                        conn.disconnect();
                    }
                }
            }
        }.start();
    }

    /**
     * 发送http请求网络(post方式)
     * @param urlSr
     * @param handler
     */
    public static void sendHttpPostRequest(final String urlSr, final Handler handler, final Map<String,Object> paramMap, final Context context){
        if(!NetUtils.isConn(context)){
            Message msg = new Message();
            msg.obj = MyApplication.ERROR_NET;
            handler.sendMessage(msg);
            return;
        }
        new Thread(){
            @Override
            public void run() {
                HttpURLConnection conn = null;
                StringBuffer buffer = null;
                StringBuffer stringBuffer = new StringBuffer();
                try{
                    URL url = new URL(urlSr);
                    conn = (HttpURLConnection) url.openConnection();//打开连接
                    conn.setDoInput(true);//允许输入流
                    conn.setDoInput(true);//允许输出流
                    conn.setUseCaches(false);//不使用缓存
                    conn.setRequestMethod("POST");//get请求方式
                    conn.setReadTimeout(READ_TIME_OUT);//设置超时时间
                    conn.setConnectTimeout(CONNECT_TIME_OUT);
                    conn.setRequestProperty("contentType","application/x-www-form-urlencoded");// 设置请求的头

                    DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                    //请求参数
                    if(paramMap != null && !paramMap.isEmpty()){
                        buffer = new StringBuffer();
                        for(Map.Entry<String, Object> entry: paramMap.entrySet()) {
                            String value = "";
                            if (entry.getValue() instanceof String){
                                value = (String) entry.getValue();
                            }else {
                                value = String.valueOf(entry.getValue());
                            }
                            buffer.append(entry.getKey()).append("=").append(URLEncoder.encode(value,"utf-8")).append("&");
                        }
                        buffer.deleteCharAt(buffer.length()-1);
                        out.writeBytes(buffer.toString());
                        out.flush();
                        out.close();
                        Logger.d(buffer.toString());
                    }
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine = "";
                    while((inputLine = reader.readLine()) != null){
                        stringBuffer.append(inputLine).append("\n");
                    }
                    //发送返回的数据
                    if(handler != null){
                        Message msg = new Message();
                        msg.obj = stringBuffer.toString();
                        handler.sendMessage(msg);
                    }
                    Logger.json(stringBuffer.toString());
                } catch (Exception e){
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.obj = MyApplication.ERROR_EXCE;
                    handler.sendMessage(msg);
                } finally {
                    if(conn != null){
                        conn.disconnect();
                    }
                }
            }
        }.start();
    }
}
