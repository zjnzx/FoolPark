package cn.com.fooltech.smartparking.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.adapter.CardBuyAdapter;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.bean.ParkInfo;
import cn.com.fooltech.smartparking.bean.jsonbean.GetParkInfo;
import cn.com.fooltech.smartparking.common.Urls;
import cn.com.fooltech.smartparking.utils.ErrorUtils;
import cn.com.fooltech.smartparking.utils.HttpUtils;
import cn.com.fooltech.smartparking.utils.JsonUtils;
import cn.com.fooltech.smartparking.utils.SPUtils;
import cn.com.fooltech.smartparking.utils.ToastUtils;
import cn.com.fooltech.smartparking.utils.Utils;

public class CardBuyActivity extends BaseActivity {
    @Bind(R.id.empty_card)
    TextView tvEmpty;
    private Context context = CardBuyActivity.this;
    @Bind(R.id.lv_card)
    ListView mListView;
    private List<ParkInfo> resultList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_buy);
        ButterKnife.bind(this);

        getLeasableParks();
    }

    private void initAdapter() {
        CardBuyAdapter mAdapter = new CardBuyAdapter(this, resultList);
        mListView.setAdapter(mAdapter);
        mListView.setEmptyView(tvEmpty);
    }

    @OnClick(R.id.back_card)
    public void onClick() {
        finish();
    }

    /**
     * 获取支持月卡停车场列表
     */
    private void getLeasableParks() {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userId", SPUtils.get(this, "userId", new Long(0l)));
        paramMap.put("token", SPUtils.get(this, "token", ""));
        HttpUtils.sendHttpPostRequest(Urls.URL_GET_LEASABLE_PARK, handlerBindCar, paramMap, this);
    }

    Handler handlerBindCar = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                GetParkInfo parkInfo = (GetParkInfo) JsonUtils.jsonToObject(msg.obj.toString(), GetParkInfo.class);
                if (parkInfo != null) {
                    int code = parkInfo.getCode();
                    if (code == 0) {
                        resultList = parkInfo.getContent();
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
}
