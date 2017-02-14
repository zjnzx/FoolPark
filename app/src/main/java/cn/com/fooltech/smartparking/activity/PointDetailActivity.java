package cn.com.fooltech.smartparking.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.common.Common;
import cn.com.fooltech.smartparking.fragment.PointDetailFragment;
import cn.com.fooltech.smartparking.utils.ErrorUtils;
import cn.com.fooltech.smartparking.utils.JsonUtils;
import cn.com.fooltech.smartparking.utils.ToastUtils;
import cn.com.fooltech.smartparking.utils.Utils;
import cn.com.fooltech.smartparking.view.ListViewPlus;


public class PointDetailActivity extends BaseActivity {
    @Bind(R.id.my_point)
    TextView tvPoint;
    @Bind(R.id.income)
    TextView tvIncome;
    @Bind(R.id.line_income)
    View lineIncome;
    @Bind(R.id.outlay)
    TextView tvOutlay;
    @Bind(R.id.line_outlay)
    View lineOutlay;
    private Context context = PointDetailActivity.this;
    private int point;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private PointDetailFragment[] fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_detail);
        ButterKnife.bind(this);

        point = getIntent().getIntExtra("point", 0);
        initView();
    }

    private void initView() {
        tvPoint.setText(point + "");
        fragmentManager = getFragmentManager();
        fragments = new PointDetailFragment[2];

        setSelection(tvIncome, 0, 1);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_point:
                finish();
                break;
            //积分规则
            case R.id.point_rule:
                Common.getLinkUrl(this, handlerRole, 2);
                break;
            //兑换停车券
            case R.id.lay_voucher:
                String pointStr = tvPoint.getText().toString();
                point = Utils.strToInt(pointStr);
                Intent intent = new Intent(this, VoucherExchangeActivity.class);
                intent.putExtra("point", point);
                startActivity(intent);
                break;
            //收入
            case R.id.income:
                setSelection(tvIncome, 0, 1);
                lineIncome.setVisibility(View.VISIBLE);
                lineOutlay.setVisibility(View.GONE);
                break;
            //支出
            case R.id.outlay:
                setSelection(tvOutlay, 1, 2);
                lineIncome.setVisibility(View.GONE);
                lineOutlay.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void setSelection(TextView textView, int index, int param) {
        transaction = fragmentManager.beginTransaction();
        hideFragment();
        clearState();
        textView.setTextColor(getResources().getColor(R.color.green));
        if (fragments[index] == null) {
            fragments[index] = PointDetailFragment.newInstance(param);
            transaction.add(R.id.lay_content_point, fragments[index]);
        } else {
            transaction.show(fragments[index]);
            fragments[index].setIndex();
            fragments[index].getPointDetailList(ListViewPlus.REFRESH);
        }
        transaction.commit();
    }

    private void hideFragment() {
        if (fragments[0] != null) {
            transaction.hide(fragments[0]);
        }
        if (fragments[1] != null) {
            transaction.hide(fragments[1]);
        }
    }

    private void clearState() {
        tvIncome.setTextColor(getResources().getColor(R.color.text));
        tvOutlay.setTextColor(getResources().getColor(R.color.text));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (MyApplication.pointCount != -1) {
            tvPoint.setText(MyApplication.pointCount + "");
        }
    }

    Handler handlerRole = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                Map<String, Object> resultMap = JsonUtils.jsonToMap(msg.obj.toString());
                if (resultMap != null && resultMap.size() > 0) {
                    int code = (int) resultMap.get("code");
                    if (code == 0) {
                        String linkUrl = (String) resultMap.get("linkUrl");
                        if (linkUrl.contains("http")) {
                            Intent intent = new Intent(context, ActivityDetailActivity.class);
                            intent.putExtra("linkUrl", linkUrl);
                            intent.putExtra("detail", "积分活动");
                            context.startActivity(intent);
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
}
