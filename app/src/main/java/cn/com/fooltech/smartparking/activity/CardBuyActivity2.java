package cn.com.fooltech.smartparking.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.bean.ParkInfo;
import cn.com.fooltech.smartparking.common.Urls;
import cn.com.fooltech.smartparking.utils.DateUtils;
import cn.com.fooltech.smartparking.utils.ErrorUtils;
import cn.com.fooltech.smartparking.utils.HttpUtils;
import cn.com.fooltech.smartparking.utils.JsonUtils;
import cn.com.fooltech.smartparking.utils.SPUtils;
import cn.com.fooltech.smartparking.utils.ToastUtils;
import cn.com.fooltech.smartparking.utils.Utils;
import cn.com.fooltech.smartparking.view.MyRadioGroup;

public class CardBuyActivity2 extends BaseActivity {
    @Bind(R.id.park_name_show5)
    TextView tvParkName;
    @Bind(R.id.park_addr_show5)
    TextView tvParkAddr;
    @Bind(R.id.time_service)
    TextView tvParkTime;
    @Bind(R.id.image_month)
    ImageView ivMonth;
    @Bind(R.id.image_quarter)
    ImageView ivQuarter;
    @Bind(R.id.image_year)
    ImageView ivYear;
    @Bind(R.id.card_month)
    RadioButton rbtnCardMonth;
    @Bind(R.id.card_quarter)
    RadioButton rbtnCardQuarter;
    @Bind(R.id.card_year)
    RadioButton rbtnCardYear;
    private Context context = CardBuyActivity2.this;
    @Bind(R.id.radiogroup_card)
    MyRadioGroup radiogroupCard;
    @Bind(R.id.btn_card_buy2)
    Button btnCardBuy;
    private ParkInfo parkInfo;
    private int cardType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_buy2);
        ButterKnife.bind(this);

        parkInfo = (ParkInfo) getIntent().getSerializableExtra("parkInfo");

        tvParkName.setText(parkInfo.getParkName());
        tvParkAddr.setText(parkInfo.getParkAddress());
        tvParkTime.setText(parkInfo.getBeginTime() + "-" + parkInfo.getCloseTime());
        rbtnCardMonth.setText("￥" + parkInfo.getMonthPrice() / 100 + "元");
        rbtnCardQuarter.setText("￥" + parkInfo.getSeasonPrice() / 100 + "元");
        rbtnCardYear.setText("￥" + parkInfo.getYearPrice() / 100 + "元");

        setTextSize();

        radiogroupCard.setOnCheckedChangeListener(new MyRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(MyRadioGroup group, int checkedId) {
                hideImage();
                if (checkedId == R.id.card_month) {
                    cardType = 1;
                    ivMonth.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.card_quarter) {
                    cardType = 2;
                    ivQuarter.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.card_year) {
                    cardType = 3;
                    ivYear.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setTextSize(){
        String cardMonth = rbtnCardMonth.getText().toString();
        int start1 = cardMonth.indexOf("￥");
        int end1 = cardMonth.indexOf("元");
        Spannable spanText1 = new SpannableString(cardMonth);
        spanText1.setSpan(new AbsoluteSizeSpan(14,true), start1 + 1, end1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        rbtnCardMonth.setText(spanText1);

        String cardQuarter = rbtnCardQuarter.getText().toString();
        int start2 = cardQuarter.indexOf("￥");
        int end2 = cardQuarter.indexOf("元");
        Spannable spanText2 = new SpannableString(cardQuarter);
        spanText2.setSpan(new AbsoluteSizeSpan(14,true), start2 + 1, end2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        rbtnCardQuarter.setText(spanText2);

        String cardYear = rbtnCardYear.getText().toString();
        int start3 = cardYear.indexOf("￥");
        int end3 = cardYear.indexOf("元");
        Spannable spanText3 = new SpannableString(cardYear);
        spanText3.setSpan(new AbsoluteSizeSpan(14,true), start3 + 1, end3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        rbtnCardYear.setText(spanText3);
    }

    @OnClick({R.id.back_card_buy, R.id.radiogroup_card, R.id.btn_card_buy2})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_card_buy:
                finish();
                break;
            //购买
            case R.id.btn_card_buy2:
                buyCard();
                break;
        }
    }

    private void hideImage() {
        ivMonth.setVisibility(View.GONE);
        ivQuarter.setVisibility(View.GONE);
        ivYear.setVisibility(View.GONE);
    }

    /**
     * 购买月卡
     */
    private void buyCard() {
        if (cardType == 0) {
            ToastUtils.showShort(this, "请选择要购买的卡券");
            return;
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userId", SPUtils.get(this, "userId", new Long(0l)));
        paramMap.put("token", SPUtils.get(this, "token", ""));
        paramMap.put("plateNumber", MyApplication.currentCar.getPlateNumber());
        paramMap.put("parkId", parkInfo.getParkId());
        paramMap.put("cardType", cardType);
        HttpUtils.sendHttpPostRequest(Urls.URL_BUY_CARD, handlerBuyCard, paramMap, this);
    }

    Handler handlerBuyCard = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                int code = JsonUtils.getCodeResult(msg.obj.toString());
                if (code == 0) {
                    showDialogCard();
                } else {
                    ErrorUtils.errorCode(context, code);
                }
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 购买成功
     */
    private void showDialogCard() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final Dialog dialog = builder.create();
        dialog.show();

        Window window = dialog.getWindow();
        window.setContentView(R.layout.dialog_card_buy);
        TextView tvTime = (TextView) window.findViewById(R.id.time_limit_card);
        TextView tvPrice = (TextView) window.findViewById(R.id.card_price);
        TextView tvCardType = (TextView) window.findViewById(R.id.tv_dialog_card1);
        RelativeLayout layout = (RelativeLayout) window.findViewById(R.id.lay_card1);
        Button btnOk = (Button) window.findViewById(R.id.btn_ok2);

        String cardPrice = "";
        if(cardType == 1){
            layout.setBackgroundResource(R.drawable.card_month2);
            tvCardType.setText("恭喜您购买成功!\n获得1张月卡");
            cardPrice = rbtnCardMonth.getText().toString();
        }else if(cardType == 2){
            layout.setBackgroundResource(R.drawable.card_quarter2);
            tvCardType.setText("恭喜您购买成功!\n获得1张季卡");
            cardPrice = rbtnCardQuarter.getText().toString();
        }else if(cardType == 3){
            layout.setBackgroundResource(R.drawable.card_year2);
            tvCardType.setText("恭喜您购买成功!\n获得1张年卡");
            cardPrice = rbtnCardYear.getText().toString();
        }
        int start1 = cardPrice.indexOf("￥");
        int end1 = cardPrice.indexOf("元");
        Spannable spanText1 = new SpannableString(cardPrice);
        spanText1.setSpan(new AbsoluteSizeSpan(22,true), start1 + 1, end1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvPrice.setText(spanText1);

        Date date = new Date();
        String endDate = DateUtils.getDate2(DateUtils.getNextDate(date));
        tvTime.setText("有效期至: " + endDate);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

    }
}
