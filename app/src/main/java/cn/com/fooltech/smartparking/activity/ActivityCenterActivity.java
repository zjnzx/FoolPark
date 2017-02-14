package cn.com.fooltech.smartparking.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.adapter.ActivityCenterAdapter;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.bean.ActivityInfo;
import cn.com.fooltech.smartparking.common.Urls;
import cn.com.fooltech.smartparking.bean.jsonbean.GetActivity;
import cn.com.fooltech.smartparking.utils.ErrorUtils;
import cn.com.fooltech.smartparking.utils.HttpUtils;
import cn.com.fooltech.smartparking.utils.JsonUtils;
import cn.com.fooltech.smartparking.utils.ToastUtils;
import cn.com.fooltech.smartparking.utils.Utils;

/**
 * 活动中心
 */
public class ActivityCenterActivity extends BaseActivity {
    @Bind(R.id.back_act_center)
    ImageView ivBack;
    @Bind(R.id.lv_act_center)
    ListView mListView;
    private Context context = ActivityCenterActivity.this;
    private List<ActivityInfo> resultList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_center);
        ButterKnife.bind(this);

        initView();
        HttpUtils.sendHttpPostRequest(Urls.URL_GET_ACTIVITY_LIST, handlerActivity, null, this);

    }

    private void initView() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initAdapter() {
        ActivityCenterAdapter mAdapter = new ActivityCenterAdapter(this, resultList);
        mListView.setAdapter(mAdapter);
    }

    Handler handlerActivity = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                GetActivity activity = (GetActivity) JsonUtils.jsonToObject(msg.obj.toString(), GetActivity.class);
                if (activity != null) {
                    int code = activity.getCode();
                    if (code == 0) {
                        resultList = activity.getContent();
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
