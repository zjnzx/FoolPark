package cn.com.fooltech.smartparking.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.adapter.BillDetailAdapter;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.bean.TradeInfo;
import cn.com.fooltech.smartparking.common.Urls;
import cn.com.fooltech.smartparking.bean.jsonbean.GetTradeDetail;
import cn.com.fooltech.smartparking.utils.DateUtils;
import cn.com.fooltech.smartparking.utils.ErrorUtils;
import cn.com.fooltech.smartparking.utils.HttpUtils;
import cn.com.fooltech.smartparking.utils.JsonUtils;
import cn.com.fooltech.smartparking.utils.SPUtils;
import cn.com.fooltech.smartparking.utils.ToastUtils;
import cn.com.fooltech.smartparking.utils.Utils;
import cn.com.fooltech.smartparking.view.ListViewPlus;


public class RefundDetailActivity extends BaseActivity implements ListViewPlus.ListViewPlusListener {
    @Bind(R.id.back_refund_detail)
    ImageView ivBack;
    @Bind(R.id.lv_refund_detail)
    ListViewPlus mListView;
    @Bind(R.id.empty_refund)
    ImageView ivEmpty;
    private Context context = RefundDetailActivity.this;
    private List<TradeInfo> resultList = new ArrayList<TradeInfo>();
    private List<TradeInfo> result;
    private long carId;
    private Date date;
    private boolean isFirstLoad;
    private int index = 0, count = 20;
    private int flag = ListViewPlus.REFRESH;//0:刷新  1:加载
    private BillDetailAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refund_detail);
        ButterKnife.bind(this);

        carId = getIntent().getLongExtra("carId", 0l);
        date = new Date();

        initView();
    }

    private void initView() {
        isFirstLoad = true;
        mListView.setRefreshEnable(true);
        mListView.setLoadEnable(true);
        mListView.setAutoLoadEnable(true);
        mListView.setListViewPlusListener(this);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        initAdapter();
    }

    /**
     * 获取提现明细
     */
    private void getDetail(int flag) {
        this.flag = flag;
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userId", SPUtils.get(this, "userId", new Long(0l)));
        paramMap.put("token", SPUtils.get(this, "token", ""));
        paramMap.put("carId", carId);
        paramMap.put("type", 2);
        paramMap.put("from", index);
        paramMap.put("recnum", count);
        HttpUtils.sendHttpPostRequest(Urls.URL_DETAIL, handlerDetail, paramMap, this);
    }

    Handler handlerDetail = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                GetTradeDetail detail = (GetTradeDetail) JsonUtils.jsonToObject(msg.obj.toString(), GetTradeDetail.class);
                if (detail != null) {
                    int code = detail.getCode();
                    if (code == 0) {
                        result = detail.getContent();
                        notifyData();
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

    private void initAdapter() {
        mAdapter = new BillDetailAdapter(this, resultList);
        mListView.setAdapter(mAdapter);
    }

    private void notifyData() {
        if (flag == ListViewPlus.REFRESH) {
            resultList.clear();
            if(result != null) {
                resultList.addAll(result);
            }
        } else if (flag == ListViewPlus.LOAD) {
            if(result != null) {
                resultList.addAll(result);
            }
        }
        onLoadComplete();
        mAdapter.notifyDataSetChanged();
        if (resultList.size() == 0) {
            ivEmpty.setVisibility(View.VISIBLE);
        } else {
            ivEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && isFirstLoad) {
            mListView.autoRefresh();
            isFirstLoad = false;
        }
    }

    @Override
    public void onRefresh() {
        index = 0;
        getDetail(ListViewPlus.REFRESH);
    }

    @Override
    public void onLoadMore() {
        index += count;
        getDetail(ListViewPlus.LOAD);
    }

    private void onLoadComplete() {
        mListView.stopRefresh();
        mListView.stopLoadMore();
        mListView.setRefreshTime(DateUtils.getDate());
    }

}
