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
import cn.com.fooltech.smartparking.fragment.VoucherFragment;
import cn.com.fooltech.smartparking.utils.ErrorUtils;
import cn.com.fooltech.smartparking.utils.JsonUtils;
import cn.com.fooltech.smartparking.utils.ToastUtils;
import cn.com.fooltech.smartparking.utils.Utils;
import cn.com.fooltech.smartparking.view.ListViewPlus;

/**
 * 停车券
 */
public class VoucherActivity extends BaseActivity {
    @Bind(R.id.whole)
    TextView tvWhole;
    @Bind(R.id.unused)
    TextView tvUnused;
    @Bind(R.id.used)
    TextView tvUsed;
    @Bind(R.id.stale)
    TextView tvStale;
    private Context context = VoucherActivity.this;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private VoucherFragment[] fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        fragmentManager = getFragmentManager();
        fragments = new VoucherFragment[4];

        setSelection(tvWhole, 0, -1);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            //返回
            case R.id.back_voucher:
                finish();
                break;
            //使用规则
            case R.id.use_rule:
                Common.getLinkUrl(this, handlerRole, 3);
                break;
            //全部
            case R.id.whole:
                setSelection(tvWhole, 0, -1);
                break;
            //未使用
            case R.id.unused:
                setSelection(tvUnused, 1, 0);
                break;
            //已使用
            case R.id.used:
                setSelection(tvUsed, 2, 1);
                break;
            //已过期
            case R.id.stale:
                setSelection(tvStale, 3, 2);
                break;
        }
    }

    private void setSelection(TextView textView, int index, int param) {
        transaction = fragmentManager.beginTransaction();
        hideFragment();
        clearState();
        textView.setTextColor(getResources().getColor(R.color.green));
        textView.setBackgroundColor(getResources().getColor(R.color.background));
        if (fragments[index] == null) {
            fragments[index] = VoucherFragment.newInstance(param);
            transaction.add(R.id.lay_content, fragments[index]);
        } else {
            transaction.show(fragments[index]);
            fragments[index].setIndex();
            fragments[index].getVoucherList(ListViewPlus.REFRESH);
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
        if (fragments[2] != null) {
            transaction.hide(fragments[2]);
        }
        if (fragments[3] != null) {
            transaction.hide(fragments[3]);
        }
    }

    private void clearState() {
        tvWhole.setTextColor(getResources().getColor(R.color.text));
        tvUnused.setTextColor(getResources().getColor(R.color.text));
        tvUsed.setTextColor(getResources().getColor(R.color.text));
        tvStale.setTextColor(getResources().getColor(R.color.text));
        tvWhole.setBackgroundColor(getResources().getColor(R.color.white));
        tvUnused.setBackgroundColor(getResources().getColor(R.color.white));
        tvUsed.setBackgroundColor(getResources().getColor(R.color.white));
        tvStale.setBackgroundColor(getResources().getColor(R.color.white));
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
                            intent.putExtra("detail", "停车券使用规则");
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
