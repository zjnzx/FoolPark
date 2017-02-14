package cn.com.fooltech.smartparking.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.adapter.RefundAdapter;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.bean.OrderInfo;
import cn.com.fooltech.smartparking.common.Urls;
import cn.com.fooltech.smartparking.bean.jsonbean.GetRefund;
import cn.com.fooltech.smartparking.bean.jsonbean.GetRefundContent;
import cn.com.fooltech.smartparking.utils.DateUtils;
import cn.com.fooltech.smartparking.utils.ErrorUtils;
import cn.com.fooltech.smartparking.utils.GuideUtils;
import cn.com.fooltech.smartparking.utils.HttpUtils;
import cn.com.fooltech.smartparking.utils.JsonUtils;
import cn.com.fooltech.smartparking.utils.SPUtils;
import cn.com.fooltech.smartparking.utils.ToastUtils;
import cn.com.fooltech.smartparking.utils.Utils;
import cn.com.fooltech.smartparking.view.ListViewPlus;

/**
 * 提现
 */
public class RefundActivity extends BaseActivity implements ListViewPlus.ListViewPlusListener {
    @Bind(R.id.balance3)
    TextView tvBalance;
    @Bind(R.id.lv_refund)
    ListViewPlus mListView;
    @Bind(R.id.empty_refund2)
    TextView tvEmpty;
    @Bind(R.id.lay_root_refund)
    RelativeLayout mLayout;
    private Context context = RefundActivity.this;
    private List<OrderInfo> resultList = new ArrayList<OrderInfo>();
    private List<OrderInfo> result;
    private int balance;
    private long carId;
    private boolean isFirstLoad;
    private int index = 0, count = 10;
    private int flag = ListViewPlus.REFRESH;//0:刷新  1:加载
    private RefundAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refund);
        ButterKnife.bind(this);

        GuideUtils guideUtil = GuideUtils.getInstance();
        guideUtil.addGuideImage(this,mLayout,R.drawable.ic_guide_5,"guide_refund");


        balance = getIntent().getIntExtra("balance", 0);
        carId = this.getIntent().getLongExtra("carId", 0l);

        initView();
    }

    private void initView() {
        isFirstLoad = true;
        mListView.setRefreshEnable(true);
        mListView.setLoadEnable(true);
        mListView.setAutoLoadEnable(true);
        mListView.setListViewPlusListener(this);
        tvBalance.setText(Utils.decimalFormat((double) balance / 100));

        initAdapter();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_refund:
                finish();
                break;
            //明细
            case R.id.detail_refund:
                Intent intent = new Intent(this, RefundDetailActivity.class);
                intent.putExtra("carId", carId);
                startActivity(intent);
                break;
        }
    }

    private void getRefundInfo(int flag) {
        this.flag = flag;
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userId", SPUtils.get(this, "userId", new Long(0l)));
        paramMap.put("token", SPUtils.get(this, "token", ""));
        paramMap.put("carId", carId);
        paramMap.put("from", index);
        paramMap.put("recnum", count);
        HttpUtils.sendHttpPostRequest(Urls.URL_GET_REFUND, handlerGetRefund, paramMap, this);
    }

    Handler handlerGetRefund = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                GetRefund refund = (GetRefund) JsonUtils.jsonToObject(msg.obj.toString(), GetRefund.class);
                if (refund != null) {
                    int code = refund.getCode();
                    if (code == 0) {
                        GetRefundContent content = refund.getContent();
                        result = content.getOrders();
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
        mAdapter = new RefundAdapter(this, resultList);
        mListView.setAdapter(mAdapter);
    }

    public void refund(String orderNo, int amount) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userId", SPUtils.get(this, "userId", new Long(0l)));
        paramMap.put("token", SPUtils.get(this, "token", ""));
        paramMap.put("orderNo", orderNo);
        paramMap.put("amount", amount);
        HttpUtils.sendHttpPostRequest(Urls.URL_REFUND, handlerRefund, paramMap, this);
    }

    Handler handlerRefund = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                int code = JsonUtils.getCodeResult(msg.obj.toString());
                if (code == 0) {
                    ToastUtils.showShort(context, "提现请求成功");
                } else {
                    ErrorUtils.errorCode(context, code);
                }
            }
            super.handleMessage(msg);
        }
    };

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
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
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
        getRefundInfo(ListViewPlus.REFRESH);
    }

    @Override
    public void onLoadMore() {
        index += count;
        getRefundInfo(ListViewPlus.LOAD);
    }

    private void onLoadComplete() {
        mListView.stopRefresh();
        mListView.stopLoadMore();
        mListView.setRefreshTime(DateUtils.getDate());
    }
}
