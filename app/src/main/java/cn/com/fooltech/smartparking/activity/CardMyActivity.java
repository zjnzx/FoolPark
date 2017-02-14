package cn.com.fooltech.smartparking.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.adapter.CardMyAdapter;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.bean.CardInfo;
import cn.com.fooltech.smartparking.bean.jsonbean.GetCard;
import cn.com.fooltech.smartparking.common.Urls;
import cn.com.fooltech.smartparking.utils.ErrorUtils;
import cn.com.fooltech.smartparking.utils.HttpUtils;
import cn.com.fooltech.smartparking.utils.JsonUtils;
import cn.com.fooltech.smartparking.utils.SPUtils;
import cn.com.fooltech.smartparking.utils.ToastUtils;
import cn.com.fooltech.smartparking.utils.Utils;

public class CardMyActivity extends BaseActivity {
    private Context context = CardMyActivity.this;
    @Bind(R.id.lv_card_my)
    ListView mListView;
    @Bind(R.id.empty_card2)
    TextView tvEmpty;
    private List<CardInfo> resultList = new ArrayList<CardInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_my);
        ButterKnife.bind(this);

        getMyCard();
    }

    @OnClick({R.id.back_card_my, R.id.btn_card_buy})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_card_my:
                finish();
                break;
            case R.id.btn_card_buy:
                startActivity(new Intent(this, CardBuyActivity.class));
                break;
        }
    }

    private void getMyCard() {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userId", SPUtils.get(this, "userId", new Long(0l)));
        paramMap.put("token", SPUtils.get(this, "token", ""));
        HttpUtils.sendHttpPostRequest(Urls.URL_CARD_LIST, mHandler, paramMap, this);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                GetCard card = (GetCard) JsonUtils.jsonToObject(msg.obj.toString(), GetCard.class);
                if (card != null) {
                    int code = card.getCode();
                    if (code == 0) {
                        resultList = card.getContent();
                        initAdapter();
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

    private void initAdapter(){
        CardMyAdapter mAdapter = new CardMyAdapter(this,resultList);
        mListView.setAdapter(mAdapter);
        mListView.setEmptyView(tvEmpty);
    }
}
