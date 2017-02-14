package cn.com.fooltech.smartparking.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.adapter.VoiceAdapter;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.baidumap.DemoRoutePlanListener;
import cn.com.fooltech.smartparking.bean.ParkInfo;
import cn.com.fooltech.smartparking.common.Common;
import cn.com.fooltech.smartparking.common.Urls;
import cn.com.fooltech.smartparking.bean.jsonbean.GetParkInfo;
import cn.com.fooltech.smartparking.interface1.PermissionListener;
import cn.com.fooltech.smartparking.utils.AppUtils;
import cn.com.fooltech.smartparking.utils.ErrorUtils;
import cn.com.fooltech.smartparking.utils.HttpUtils;
import cn.com.fooltech.smartparking.utils.JsonUtils;
import cn.com.fooltech.smartparking.utils.SPUtils;
import cn.com.fooltech.smartparking.utils.ToastUtils;
import cn.com.fooltech.smartparking.utils.Utils;
import cn.com.fooltech.smartparking.voice.VouceUtils;

public class VoiceActivity extends BaseActivity {
    @Bind(R.id.voice_status)
    TextView tvStatus;
    @Bind(R.id.lv_voice)
    ListView mListView;
    @Bind(R.id.voice)
    CheckBox cbVoice;
    private Context context = VoiceActivity.this;
    private SpeechRecognizer mIat;
    private VoiceAdapter mVoiceAdapter;
    private List<String> resultList = new ArrayList<String>();
    private LatLng startPoint, endPoint;
    private int type = 0;
    private static final int ANIMATIONEACHOFFSET = 1000; // 每个动画的播放时间间隔
    private AnimationSet aniSet, aniSet2, aniSet3;
    private ImageView wave1, wave2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aniSet = getNewAnimationSet();
        aniSet2 = getNewAnimationSet();
//        aniSet3 = getNewAnimationSet();
        setContentView(R.layout.activity_voice);
        ButterKnife.bind(this);

        if(AppUtils.isOs6()){
            requestRuntimePermission(new String[]{Manifest.permission.RECORD_AUDIO}, new PermissionListener() {
                @Override
                public void onGranted() {
//                    ToastUtils.showShort(context,"RECORD_AUDIO同意");
                    MediaRecorder mRecorders = new MediaRecorder();
                    mRecorders.setAudioSource(MediaRecorder.AudioSource.MIC);
                }

                @Override
                public void onDenied(List<String> deniedPermission) {
//                    ToastUtils.showShort(context,"RECORD_AUDIO拒绝");
                    showVoiceDialog();
                    return;
                }
            });
        }else {
            MediaRecorder mRecorders = new MediaRecorder();
            mRecorders.setAudioSource(MediaRecorder.AudioSource.MIC);
        }

        type = getIntent().getIntExtra("type", 0);

        initView();
    }

    private void initView() {
        mVoiceAdapter = new VoiceAdapter(this, resultList);
        mListView.setAdapter(mVoiceAdapter);

        wave1 = (ImageView) findViewById(R.id.wave1);
        wave2 = (ImageView) findViewById(R.id.wave2);
//        wave3 = (ImageView) findViewById(R.id.wave3);

        cbVoice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    if(AppUtils.voiceCanUse()) {
                        mIat = VouceUtils.startLinsten(context, mRecoListener);
                        tvStatus.setText("请说话...");
                        showWaveAnimation();
                    }else {
                        showVoiceDialog();
                        cbVoice.setChecked(false);
                    }
                }else {
                    mIat.stopListening();
                    tvStatus.setText("轻点麦克风说话");
                    cancalWaveAnimation();
                }
            }
        });
    }

    /**
     * 显示检测录音权限对话框
     */
    private void showVoiceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true)
                .setMessage(getResources().getString(R.string.dialog_voice))
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).show();
    }

    @OnClick(R.id.close_voice)
    public void onClick() {
        finish();
    }

    private AnimationSet getNewAnimationSet() {
        AnimationSet as = new AnimationSet(true);
        ScaleAnimation sa = new ScaleAnimation(1f, 2.3f, 1f, 2.3f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(ANIMATIONEACHOFFSET * 6);
        sa.setRepeatCount(-1);// 设置循环
        AlphaAnimation aniAlp = new AlphaAnimation(1, 0.1f);
        aniAlp.setRepeatCount(-1);// 设置循环
        as.setDuration(ANIMATIONEACHOFFSET * 2);
        as.addAnimation(sa);
        as.addAnimation(aniAlp);
        return as;
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x222) {
                wave2.setVisibility(View.VISIBLE);
                wave2.startAnimation(aniSet2);
            } else if (msg.what == 0x333) {
//                wave3.startAnimation(aniSet3);
            }
            super.handleMessage(msg);
        }

    };

    private void showWaveAnimation() {
        wave1.setVisibility(View.VISIBLE);
        wave1.startAnimation(aniSet);
        handler.sendEmptyMessageDelayed(0x222, ANIMATIONEACHOFFSET);
        handler.sendEmptyMessageDelayed(0x333, ANIMATIONEACHOFFSET * 2);

    }

    private void cancalWaveAnimation() {
        cbVoice.setChecked(false);
        wave1.setVisibility(View.GONE);
        wave2.setVisibility(View.GONE);
        wave1.clearAnimation();
        wave2.clearAnimation();
//        wave3.clearAnimation();
    }

    private void notifyData(String content) {
        mVoiceAdapter.list.add(content);
        mVoiceAdapter.notifyDataSetChanged();
        mListView.setSelection(mListView.getCount() - 1);
    }

    /**
     * 请求网络获取搜索的停车场数据
     */
    private void getSearchParkDatas(String name) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("lng", MyApplication.longtitude);
        paramMap.put("lat", MyApplication.latitude);
        paramMap.put("parkName", name);

        HttpUtils.sendHttpPostRequest(Urls.URL_SEARCH_PARK, handlerSearch, paramMap, this);
    }

    //搜索停车场停车场
    Handler handlerSearch = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                GetParkInfo parkInfo = (GetParkInfo) JsonUtils.jsonToObject(msg.obj.toString(), GetParkInfo.class);
                ArrayList<ParkInfo> searList = null;
                if (parkInfo != null) {
                    searList = parkInfo.getContent();
                    if (searList != null && searList.size() > 0) {
                        ParkInfo info = searList.get(0);
                        startPoint = new LatLng(MyApplication.latitude, MyApplication.longtitude);
                        endPoint = new LatLng(info.getParkLat(), info.getParkLng());
                        if (BaiduNaviManager.isNaviInited()) { //开始导航
                            Common.getDriver(context, handlerGetDriver);
                        }
                    } else {
                        ToastUtils.showShort(context, "对不起,没有搜索到结果");
                    }
                } else {
                    ToastUtils.showShort(context, "服务器请求错误");
                }
            }

            super.handleMessage(msg);
        }
    };

    Handler handlerGetDriver = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                Map<String, Object> resultMap = JsonUtils.jsonToMap(msg.obj.toString());
                if (resultMap != null && resultMap.size() > 0) {
                    int code = (int) resultMap.get("code");
                    if (code == 0) {
                        JSONObject object = (JSONObject) resultMap.get("content");
                        try {
                            long droverId = object.getLong("driverId");
                            isDriver(droverId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

    /**
     * 判断用户是否当前车辆的driver,如果不是就设置为driver
     */
    private void isDriver(long driverId) {
        long userId = (long) SPUtils.get(context, "userId", new Long(0l));
        if (driverId == userId) {//用户是当前车辆的driver
            routeplanToNavi(BNRoutePlanNode.CoordinateType.BD09LL, startPoint, endPoint);
        } else {//不是,设置成driver
            Common.setDriver(context, handlerSetDriver);
        }
    }

    Handler handlerSetDriver = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                int code = JsonUtils.getCodeResult(msg.obj.toString());
                if (code == 0) {
                    routeplanToNavi(BNRoutePlanNode.CoordinateType.BD09LL, startPoint, endPoint);
                } else {
                    ErrorUtils.errorCode(context, code);
                }
            }
            super.handleMessage(msg);
        }
    };

    //听写监听器
    private RecognizerListener mRecoListener = new RecognizerListener() {
        @Override
        public void onVolumeChanged(int i, byte[] bytes) {

        }

        @Override
        public void onBeginOfSpeech() {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            String res = VouceUtils.printResult(recognizerResult);
            if (b) {
                notifyData(res);
                tvStatus.setText("轻点麦克风说话");
                if (type == 0) {
                    getSearchParkDatas(res);
                } else if (type == 1) {
                    Intent mIntent = new Intent();
                    mIntent.putExtra("content", res);
                    // 设置结果，并进行传送
                    setResult(2, mIntent);
                    finish();
                }
                cancalWaveAnimation();
            }
        }

        @Override
        public void onError(SpeechError speechError) {
            int error = speechError.getErrorCode();
            if (error == 10118) {
                notifyData("");
            }
            tvStatus.setText("轻点麦克风说话");
            cancalWaveAnimation();
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
        //听写结果回调接口(返回Json格式结果，用户可参见附录12.1)；
        //一般情况下会通过onResults接口多次返回结果，完整的识别内容是多次结果的累加；
        //关于解析Json的代码可参见MscDemo中JsonParser类；
        //isLast等于true时会话结束。

    };

    public void routeplanToNavi(BNRoutePlanNode.CoordinateType coType, LatLng startPoint, LatLng endPoint) {
        BNRoutePlanNode sNode = null;
        BNRoutePlanNode eNode = null;
        double sLng = startPoint.longitude;
        double sLat = startPoint.latitude;
        double eLng = endPoint.longitude;
        double eLat = endPoint.latitude;
        switch (coType) {
            case BD09LL: {
                sNode = new BNRoutePlanNode(sLng, sLat, "", null, coType);
                eNode = new BNRoutePlanNode(eLng, eLat, "", null, coType);
                break;
            }
            default:
                ;
        }
        if (sNode != null && eNode != null) {
            List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
            list.add(sNode);
            list.add(eNode);
            BaiduNaviManager.getInstance().launchNavigator(this, list, 1, true, new DemoRoutePlanListener(sNode, this));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancalWaveAnimation();
    }

}
