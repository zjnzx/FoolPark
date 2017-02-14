package cn.com.fooltech.smartparking.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.bean.PayInfoScan;
import cn.com.fooltech.smartparking.bean.VoucherInfo;
import cn.com.fooltech.smartparking.common.Common;
import cn.com.fooltech.smartparking.common.Urls;
import cn.com.fooltech.smartparking.bean.jsonbean.GetVoucher;
import cn.com.fooltech.smartparking.utils.ErrorUtils;
import cn.com.fooltech.smartparking.utils.HttpUtils;
import cn.com.fooltech.smartparking.utils.JsonUtils;
import cn.com.fooltech.smartparking.utils.SPUtils;
import cn.com.fooltech.smartparking.utils.ToastUtils;
import cn.com.fooltech.smartparking.utils.Utils;
import cn.com.fooltech.smartparking.view.PopWindowVoucher;

public class ScanPayActivity extends BaseActivity {
    @Bind(R.id.pay_amount1)
    TextView tvAmount;
    @Bind(R.id.use_voucher2)
    TextView tvUseVoucher;
    private Context context = ScanPayActivity.this;
    private String result;
    private PopWindowVoucher popWindowVoucher;
    private List<VoucherInfo> resultList;
    private Drawable drawableGray, drawableGreen;
    private int amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_pay);
        ButterKnife.bind(this);

        result = getIntent().getStringExtra("result");
        if (!("").equals(result)) {
            PayInfoScan payInfo = (PayInfoScan) JsonUtils.jsonToObject(result, PayInfoScan.class);
            if (payInfo != null) {
//                amount = Utils.decimalFormat(Utils.strToDouble(payInfo.getTotalFee()) / 100);
                amount = Utils.strToInt(payInfo.getTotalFee()) / 100;
            } else {
                ToastUtils.showShort(context, "服务器请求错误");
            }
        }

        initView();
    }

    private void initView() {
        drawableGray = getResources().getDrawable(R.drawable.oval_gray);
        drawableGray.setBounds(0, 0, drawableGray.getMinimumWidth(), drawableGray.getMinimumHeight());
        drawableGreen = getResources().getDrawable(R.drawable.oval_green);
        drawableGreen.setBounds(0, 0, drawableGray.getMinimumWidth(), drawableGray.getMinimumHeight());

        String content = amount + "元";
        Spannable spanText = new SpannableString(content);
        spanText.setSpan(new AbsoluteSizeSpan(40, true), 0, content.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvAmount.setText(spanText);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_pay:
                finish();
                break;
            //取消
            case R.id.cancle_pay:
                finish();
                break;
            //确定
            case R.id.sure_pay:
                if (Utils.isFastClick()) return;
                scanPay();
                break;
            //使用停车券
            case R.id.use_voucher2:
                if (Utils.isFastClick()) return;
                getVoucher();
                break;
        }
    }

    private void getVoucher() {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userId", SPUtils.get(this, "userId", new Long(0l)));
        paramMap.put("token", SPUtils.get(this, "token", ""));
        paramMap.put("voucherStatus", 0);
        HttpUtils.sendHttpPostRequest(Urls.URL_GET_VOUCHER, handlerVoucher, paramMap, this);
    }

    Handler handlerVoucher = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                GetVoucher voucher = (GetVoucher) JsonUtils.jsonToObject(msg.obj.toString(), GetVoucher.class);
                if (voucher != null) {
                    int code = voucher.getCode();
                    if (code == 0) {
                        resultList = voucher.getContent();
                        showPopWindow();
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

    private void showPopWindow() {
        if (resultList != null && resultList.size() > 0) {
            openAnimator();
            //实例化SelectPicPopupWindow
            popWindowVoucher = new PopWindowVoucher(this, resultList);
            popWindowVoucher.showAtLocation(ScanPayActivity.this.findViewById(R.id.lay_pay), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
            popWindowVoucher.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    Common.setShadow(ScanPayActivity.this, 1f);
                    hideAnimator();
                    if (!MyApplication.isUse) {
                        tvUseVoucher.setCompoundDrawables(drawableGray, null, null, null);
                        tvUseVoucher.setTextColor(getResources().getColor(R.color.gray));
                        if (MyApplication.voucherMap != null && MyApplication.voucherMap.size() > 0) {
                            MyApplication.voucherMap.clear();
                        }
                    } else {
                        tvUseVoucher.setCompoundDrawables(drawableGreen, null, null, null);
                        tvUseVoucher.setTextColor(getResources().getColor(R.color.green));
                    }
                }
            });
        } else {
            ToastUtils.showShort(this, "您没有可使用的停车券");
        }
    }

    /**
     * 缴纳停车费用
     */
    private void scanPay() {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userId", SPUtils.get(this, "userId", new Long(0l)));
        paramMap.put("token", SPUtils.get(this, "token", ""));
        paramMap.put("plateNumber", MyApplication.currentCar.getPlateNumber());
        if (MyApplication.voucherId != -1)
            paramMap.put("voucherId", MyApplication.voucherId);
        paramMap.put("payInfo", result);

        HttpUtils.sendHttpPostRequest(Urls.URL_SCAN_PAY, handlerPay, paramMap, this);
    }

    Handler handlerPay = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                int code = JsonUtils.getCodeResult(msg.obj.toString());
                if (code == 0) {
                    ToastUtils.showShort(context, "付款成功");
                    finish();
                } else {
                    ErrorUtils.errorCode(context, code);
                }
            }
            super.handleMessage(msg);
        }
    };

    protected void openAnimator() {
        long duration = 500;
        Display display = getWindowManager().getDefaultDisplay();
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.lay_payment);
        float[] scale = new float[2];
        scale[0] = 1.0f;
        scale[1] = 0.8f;
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(relativeLayout, "scaleX", scale).setDuration(duration);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(relativeLayout, "scaleY", scale).setDuration(duration);
        float[] rotation = new float[]{0, 10, 0};
        ObjectAnimator rotationX = ObjectAnimator.ofFloat(relativeLayout, "rotationX", rotation).setDuration(duration);

        float[] translation = new float[1];
        translation[0] = -display.getWidth() * 0.2f / 2;
        ObjectAnimator translationY = ObjectAnimator.ofFloat(relativeLayout, "translationY", translation).setDuration(duration);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, rotationX, translationY);
        animatorSet.setTarget(relativeLayout);
        animatorSet.start();
    }

    public void hideAnimator() {
        long duration = 500;
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.lay_payment);
        float[] scale = new float[2];
        scale[0] = 0.8f;
        scale[1] = 1.0f;
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(relativeLayout, "scaleX", scale).setDuration(duration);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(relativeLayout, "scaleY", scale).setDuration(duration);
        float[] rotation = new float[]{0, 10, 0};
        ObjectAnimator rotationX = ObjectAnimator.ofFloat(relativeLayout, "rotationX", rotation).setDuration(duration);

        float[] translation = new float[1];
        translation[0] = 0;
        ObjectAnimator translationY = ObjectAnimator.ofFloat(relativeLayout, "translationY", translation).setDuration(duration);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, rotationX, translationY);
        animatorSet.setTarget(relativeLayout);
        animatorSet.start();
    }

}
