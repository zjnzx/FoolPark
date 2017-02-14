package cn.com.fooltech.smartparking.utils;

import cn.com.fooltech.smartparking.bean.Param;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
/**
 * Created by YY on 2016/9/19.
 */
public class OkHttpUtils {
    private static final String TAG = "OkHttpUtils";

    private static OkHttpUtils mInstance;
    private static OkHttpClient mOkHttpClient;
    private Handler mHandler;
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    private OkHttpUtils() {
        /**
         * 构建OkHttpClient
         */
        mOkHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS).build();
        /**
         * 获取主线程的handler
         */
        mHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 通过单例模式构造对象
     * @return OkHttpUtils
     */
    public synchronized static OkHttpUtils getmInstance() {
        if (mInstance == null) {
            mInstance = new OkHttpUtils();
        }
        return mInstance;
    }

    /**
     * 构造Get请求
     * @param url  请求的url
     * @param callback  结果回调的方法
     */
    private void getRequest(String url, final BaseCallback callback) {
        final Request request = new Request.Builder().url(url).build();
        deliveryResult(callback, request);
    }

    /**
     * 构造post 请求
     * @param url 请求的url
     * @param callback 结果回调的方法
     * @param params 请求参数
     */
    private void postRequest(String url, final BaseCallback callback, List<Param> params) {
        Request request = buildPostRequest(url, params);
        deliveryResult(callback, request);
    }

    /**
     * 处理请求结果的回调
     * @param callback
     * @param request
     */
    private void deliveryResult(final BaseCallback callback, Request request) {
        //在请求之前所做的事，比如弹出对话框等
        callback.onRequestBefore();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendFailCallback(callback, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                sendSuccessCallBack(callback, response);
            }

        });
    }

    /**
     * 发送失败的回调
     * @param callback
     * @param e
     */
    private void sendFailCallback(final BaseCallback callback, final Exception e) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onFailure(e);
                }
            }
        });
    }

    /**
     * 发送成功的调
     * @param callback
     * @param response
     */
    private void sendSuccessCallBack(final BaseCallback callback, final Response response) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onSuccess(response);
                }
            }
        });
    }

    /**
     * 构造post请求
     * @param url  请求url
     * @param params 请求的参数
     * @return 返回 Request
     */
    private Request buildPostRequest(String url, List<Param> params) {
        FormBody.Builder builder = new FormBody.Builder();
        for (Param param : params) {
            builder.add(param.key, param.value);
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder().url(url).post(requestBody).build();
    }


    /**********************对外接口************************/

    /**
     * get请求
     * @param url  请求url
     * @param callback  请求回调
     */
    public static void get(String url, BaseCallback callback) {
        getmInstance().getRequest(url, callback);
    }

    /**
     * post请求
     * @param url       请求url
     * @param callback  请求回调
     * @param params    请求参数
     */
    public static void post(String url, final BaseCallback callback, List<Param> params) {
        getmInstance().postRequest(url, callback, params);
    }


    public static RequestBody setFileRequestBody(Map<String, String> BodyParams, Map<String, String> fileParams){
        //带文件的Post参数
        RequestBody body=null;
        okhttp3.MultipartBody.Builder MultipartBodyBuilder=new okhttp3.MultipartBody.Builder();
        MultipartBodyBuilder.setType(MultipartBody.FORM);
        RequestBody fileBody = null;

        if(BodyParams != null){
            Iterator<String> iterator = BodyParams.keySet().iterator();
            String key = "";
            while (iterator.hasNext()) {
                key = iterator.next().toString();
                MultipartBodyBuilder.addFormDataPart(key, BodyParams.get(key));
//                Log.d("post http", "post_Params==="+key+"===="+BodyParams.get(key));
            }
        }

        if(fileParams != null){
            Iterator<String> iterator = fileParams.keySet().iterator();
            String key = "";
            int i=0;
            while (iterator.hasNext()) {
                key = iterator.next().toString();
                i++;
                MultipartBodyBuilder.addFormDataPart(key, fileParams.get(key));
//                Log.d("post http", "post_Params==="+key+"===="+fileParams.get(key));
                fileBody = RequestBody.create(MEDIA_TYPE_PNG, new File(fileParams.get(key)));
                MultipartBodyBuilder.addFormDataPart(key, i+".png", fileBody);
            }
        }
        body=MultipartBodyBuilder.build();
        return body;

    }

    /**
     * 上传图片
     * @param BodyParams
     * @param fileParams
     * @return
     */
    public void uploadFile(String url, Map<String, String> BodyParams, Map<String, String> fileParams, final BaseCallback callback, Context context){
        RequestBody requestBody = setFileRequestBody(BodyParams,fileParams);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        if(!NetUtils.isConnected(context)){
            ToastUtils.showShort(context,"请检查网络连接");
            return;
        }
        callback.onRequestBefore();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendFailCallback(callback,e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    sendSuccessCallBack(callback, response);
                }
            }
        });
    }

}
