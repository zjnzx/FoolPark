package cn.com.fooltech.smartparking.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.bean.PayResult;
import cn.com.fooltech.smartparking.common.Urls;
import cn.com.fooltech.smartparking.utils.ErrorUtils;
import cn.com.fooltech.smartparking.utils.HttpUtils;
import cn.com.fooltech.smartparking.utils.JsonUtils;
import cn.com.fooltech.smartparking.utils.SPUtils;
import cn.com.fooltech.smartparking.utils.ToastUtils;
import cn.com.fooltech.smartparking.utils.Utils;
import cn.com.fooltech.smartparking.view.MyRadioGroup;

/**
 * 此类被WXPayEntryActivity代替
 */
public class RechargeActivity extends BaseActivity {
    @Bind(R.id.plate_num_show5)
    TextView tvPlate;
    @Bind(R.id.balance2)
    TextView tvBalance;
    @Bind(R.id.balance_exchange)
    EditText etRechargeBalance;
    @Bind(R.id.image_fifty)
    ImageView ivfifty;
    @Bind(R.id.image_one)
    ImageView ivOneH;
    @Bind(R.id.image_two)
    ImageView ivTwoH;
    @Bind(R.id.radiogroup)
    MyRadioGroup radioGroup;
    private Context context = RechargeActivity.this;
    private static final String APP_ID = "wxb4ba3c02aa476ea1";
    private int balance;
    private long carId;
    private int amount = 50;//充值金额
    private int type;
    private static final int SDK_PAY_FLAG = 1;
    private int rechargeAmount;
    private String plateNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        ButterKnife.bind(this);

        balance = getIntent().getIntExtra("balance", 0);
        plateNumber = getIntent().getStringExtra("plateNumber");
        carId = getIntent().getLongExtra("carId", 0l);

        initView();
    }

    private void initView() {
        tvBalance.setText(Utils.decimalFormat((double) balance / 100));
        tvPlate.setText("余额充值(" + plateNumber + ")");

        radioGroup.setOnCheckedChangeListener(new MyRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(MyRadioGroup group, int checkedId) {
                hideImage();
                if (checkedId == R.id.fifty) {
                    amount = 50;
                    ivfifty.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.one_hundred) {
                    amount = 100;
                    ivOneH.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.two_hundred) {
                    amount = 200;
                    ivTwoH.setVisibility(View.VISIBLE);
                }
            }
        });

        ReceiverWXpay receiver = new ReceiverWXpay();
        IntentFilter filter = new IntentFilter("cn.com.fooltech.smartparking.wxpay");
        //注册广播接收器
        registerReceiver(receiver, filter);

    }

    private void hideImage() {
        ivfifty.setVisibility(View.GONE);
        ivOneH.setVisibility(View.GONE);
        ivTwoH.setVisibility(View.GONE);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_recharge:
                finish();
                break;
            //明细
            case R.id.detail_recharge:
                Intent intent = new Intent(this, RechargeDetailActivity.class);
                intent.putExtra("carId", carId);
                startActivity(intent);
                break;
            //支付宝充值
            case R.id.lay_alipay:
                rechargeAmount = getRechargeBalance();
                type = 2;
                recharge(rechargeAmount, 2);
                break;
            //微信充值
            case R.id.lay_wechat:
                rechargeAmount = getRechargeBalance();
                type = 1;
                recharge(rechargeAmount, 1);
                break;
        }
    }

    /**
     * 获得充值金额,输入框优先
     *
     * @return
     */
    private int getRechargeBalance() {
        String balance = etRechargeBalance.getText().toString();
        if (("").equals(balance)) {
            return amount;
        } else {
            return Integer.parseInt(balance) * 1;
        }
    }

    private void recharge(int amount, int payment) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userId", SPUtils.get(this, "userId", new Long(0l)));
        paramMap.put("token", SPUtils.get(this, "token", ""));
        paramMap.put("carId", carId);
        paramMap.put("amount", amount * 100);
        paramMap.put("payment", payment);
        HttpUtils.sendHttpPostRequest(Urls.URL_RECHARGE, handlerRecharge, paramMap, this);
    }

    Handler handlerRecharge = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                Map<String, Object> resultMap = JsonUtils.jsonToMap(msg.obj.toString());
                if (resultMap != null && resultMap.size() > 0) {
                    int code = (int) resultMap.get("code");
                    JSONObject content = (JSONObject) resultMap.get("content");
                    Map<String, Object> contentMap = JsonUtils.json2Map(content);
                    if (code == 0) {
                        if (type == 1) {
                            invokeWXPay(contentMap);
                        } else if (type == 2) {
                            invokeAlipay(contentMap);
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
     * 调用微信支付
     *
     * @param contentMap
     */
    private void invokeWXPay(Map<String, Object> contentMap) {
        if (contentMap != null && contentMap.size() > 0) {
            String sign = (String) contentMap.get("sign");
            String appid = (String) contentMap.get("appid");
            String timestamp = (String) contentMap.get("timestamp");
            String noncestr = (String) contentMap.get("noncestr");
            String partnerid = (String) contentMap.get("partnerid");
            String prepayid = (String) contentMap.get("prepayid");
            String package2 = (String) contentMap.get("package");
            IWXAPI api = WXAPIFactory.createWXAPI(this, APP_ID);
            api.registerApp(appid);// 将该app注册到微信
            PayReq request = new PayReq();
            request.appId = appid;
            request.partnerId = partnerid;
            request.prepayId = prepayid;
            request.packageValue = package2;
            request.nonceStr = noncestr;
            request.timeStamp = timestamp;
            request.sign = sign;
            ToastUtils.showShort(context, "正在调起支付...");
            api.sendReq(request);
        } else {
            ToastUtils.showShort(context, "服务器请求错误");
        }
    }

    /**
     * 调用支付宝支付
     *
     * @param contentMap
     */
    private void invokeAlipay(Map<String, Object> contentMap) {
        if (contentMap != null && contentMap.size() > 0) {
            final String reqStr = (String) contentMap.get("requestString");
//        Log.i("RechargeActivity","====="+reqStr);

            Runnable payRunnable = new Runnable() {

                @Override
                public void run() {
                    // 构造PayTask 对象
                    PayTask alipay = new PayTask(RechargeActivity.this);
                    // 调用支付接口，获取支付结果
                    String result = alipay.pay(reqStr, true);

                    Message msg = new Message();
                    msg.what = SDK_PAY_FLAG;
                    msg.obj = result;
                    mHandler.sendMessage(msg);
                }
            };

            // 必须异步调用
            Thread payThread = new Thread(payRunnable);
            payThread.start();
        } else {
            ToastUtils.showShort(context, "服务器请求错误");
        }
    }

    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息

                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        ToastUtils.showShort(context, "支付成功");
                        String amountStr = tvBalance.getText().toString();
                        double amount = Utils.strToDouble(amountStr);
                        tvBalance.setText(Utils.decimalFormat(amount + (double) rechargeAmount));
                        MyApplication.isRecharge = true;//更新绑定车辆信息
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            ToastUtils.showShort(context, "支付结果确认中");
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            ToastUtils.showShort(context, "支付失败");
                        }

                    }
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };

    class ReceiverWXpay extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int errCode = intent.getIntExtra("errCode", 0);
            if (errCode == 0) {
                String amountStr = tvBalance.getText().toString();
                double amount = Utils.strToDouble(amountStr);
                tvBalance.setText(Utils.decimalFormat(amount + (double) rechargeAmount));
                MyApplication.isRecharge = true;//更新绑定车辆信息
            }

        }
    }

}
