package cn.com.fooltech.smartparking.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.adapter.ExchangeRecordAdapter;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.bean.ExchangeRecordInfo;
import cn.com.fooltech.smartparking.common.Urls;
import cn.com.fooltech.smartparking.bean.jsonbean.GetExchangeRecord;
import cn.com.fooltech.smartparking.utils.DateUtils;
import cn.com.fooltech.smartparking.utils.ErrorUtils;
import cn.com.fooltech.smartparking.utils.HttpUtils;
import cn.com.fooltech.smartparking.utils.JsonUtils;
import cn.com.fooltech.smartparking.utils.SPUtils;
import cn.com.fooltech.smartparking.utils.ToastUtils;
import cn.com.fooltech.smartparking.utils.Utils;
import cn.com.fooltech.smartparking.view.ListViewPlus;


public class VoucherExchangeActivity extends BaseActivity implements ListViewPlus.ListViewPlusListener {
    @Bind(R.id.total_voucher)
    TextView tvCountPoint;
    @Bind(R.id.point_count2)
    EditText etCount;
    @Bind(R.id.lv_exchange)
    ListViewPlus mListView;
    @Bind(R.id.empty_voucher2)
    TextView tvEmpty;
    private Context context = VoucherExchangeActivity.this;
    private int point;
    private List<ExchangeRecordInfo> resultList = new ArrayList<ExchangeRecordInfo>();
    private List<ExchangeRecordInfo> result;
    private ExchangeRecordAdapter mAdapter;
    private boolean isFirstLoad;
    private int index = 0, count = 20;
    private int flag = ListViewPlus.REFRESH;//0:刷新  1:加载
    private int voucherCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher_exchange);
        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        point = getIntent().getIntExtra("point", 0);
        initView();
    }

    private void initView() {
        isFirstLoad = true;
        mListView.setRefreshEnable(true);
        mListView.setLoadEnable(true);
        mListView.setAutoLoadEnable(true);
        mListView.setListViewPlusListener(this);
        tvCountPoint.setText(point + "");

        initAdapter();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_exchange:
                finish();
                break;
            //减少
            case R.id.reduce:
                int count = getCount();
                if (count == 0) {
                    etCount.setText(0 + "");
                } else {
                    etCount.setText(--count + "");
                }
                etCount.setSelection(etCount.getText().toString().length());
                break;
            //增加
            case R.id.inicrease:
                int count2 = getCount();
                etCount.setText(++count2 + "");
                etCount.setSelection(etCount.getText().toString().length());
                break;
            //确定
            case R.id.sure_exchange:
                voucherCount = getCount();
                Map<String, Object> paramMap = new HashMap<String, Object>();
                paramMap.put("userId", SPUtils.get(this, "userId", new Long(0l)));
                paramMap.put("token", SPUtils.get(this, "token", ""));
                paramMap.put("num", voucherCount);
                if (voucherCount != 0) {
                    HttpUtils.sendHttpPostRequest(Urls.URL_EXCHANGE_VOUCHER, handlerExchange, paramMap, this);
                }
                break;
        }
    }

    /**
     * 获取输入框中的停车券数量
     *
     * @return
     */
    private int getCount() {
        String countStr = etCount.getText().toString();
        if (!countStr.equals("")) {
            return Integer.parseInt(countStr);
        }
        return 0;
    }

    Handler handlerExchange = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                int code = JsonUtils.getCodeResult(msg.obj.toString());
                if (code == 0) {
                    ToastUtils.showShort(context, "兑换成功");
                    String count = etCount.getText().toString().substring(0, 1);
                    mAdapter.addData(new ExchangeRecordInfo("兑换了" + count + "张停车券", DateUtils.getTime(new Date())));//刷新列表

                    String pointStr = tvCountPoint.getText().toString();
                    int pointCount = Utils.strToInt(pointStr);
                    MyApplication.pointCount = pointCount - voucherCount * 1000;
                    tvCountPoint.setText(MyApplication.pointCount + "");//更新积分
                } else {
                    ErrorUtils.errorCode(context, code);
                }

            }
            super.handleMessage(msg);
        }
    };

    /**
     * 获取兑换记录
     */
    private void getExchangeRecord(int flag) {
        this.flag = flag;
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userId", SPUtils.get(this, "userId", new Long(0l)));
        paramMap.put("token", SPUtils.get(this, "token", ""));
        paramMap.put("from", index);
        paramMap.put("recnum", count);
        HttpUtils.sendHttpPostRequest(Urls.URL_GET_EXCHANGE_RECORD, handlerExchangeRecord, paramMap, this);
    }

    Handler handlerExchangeRecord = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                GetExchangeRecord record = (GetExchangeRecord) JsonUtils.jsonToObject(msg.obj.toString(), GetExchangeRecord.class);
                if (record != null) {
                    int code = record.getCode();
                    if (code == 0) {
                        result = record.getContent();
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
        mAdapter = new ExchangeRecordAdapter(this, resultList);
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
        getExchangeRecord(ListViewPlus.REFRESH);
    }

    @Override
    public void onLoadMore() {
        index += count;
        getExchangeRecord(ListViewPlus.LOAD);
    }

    private void onLoadComplete() {
        mListView.stopRefresh();
        mListView.stopLoadMore();
        mListView.setRefreshTime(DateUtils.getDate());
    }
}
