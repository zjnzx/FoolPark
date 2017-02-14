package cn.com.fooltech.smartparking.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.common.Urls;
import cn.com.fooltech.smartparking.utils.ErrorUtils;
import cn.com.fooltech.smartparking.utils.HttpUtils;
import cn.com.fooltech.smartparking.utils.JsonUtils;
import cn.com.fooltech.smartparking.utils.SPUtils;
import cn.com.fooltech.smartparking.utils.ToastUtils;
import cn.com.fooltech.smartparking.utils.Utils;
import cn.com.fooltech.smartparking.view.ToggleButton;


public class FeedbackActivity extends BaseActivity {
    @Bind(R.id.image_park)
    ImageView ivPark;
    @Bind(R.id.image_app)
    ImageView ivApp;
    @Bind(R.id.feedback_advice)
    EditText etAdvice;
    @Bind(R.id.contact_way)
    EditText etContactWay;
    @Bind(R.id.toggleButton_visit)
    ToggleButton tbVisit;
    @Bind(R.id.tv_text_count)
    TextView tvTextCount;
    private Context context = FeedbackActivity.this;
    private String type = "1", isVisit = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        //是否愿意接受回访 0:不接受  1:接受
        tbVisit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isVisit = "1";
                } else {
                    isVisit = "0";
                }
            }
        });

    }
    @OnTextChanged(value = R.id.feedback_advice,callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterTextChanged(Editable editable){
        String content = editable.toString();
        tvTextCount.setText(content.length() + "/" + 2000);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            //返回
            case R.id.back_feedback:
                finish();
                break;
            //发送
            case R.id.send_advice:
                String advice = etAdvice.getText().toString();
                String contactWay = etContactWay.getText().toString();
                if ("".equals(advice)) {
                    ToastUtils.showShort(this, "请填写您的建议");
                    return;
                }
                Map<String, Object> paramMap = new HashMap<String, Object>();
                paramMap.put("type", type);
                paramMap.put("content", advice);
                paramMap.put("acceptVisit", isVisit);
                if (("1".equals(isVisit) && !"".equals(contactWay)) || "0".equals(isVisit)) {
                    paramMap.put("contact", contactWay);
                } else {
                    ToastUtils.showShort(this, "联系方式不能为空");
                    return;
                }
                paramMap.put("userId", SPUtils.get(this, "userId", new Long(0l)));
                HttpUtils.sendHttpPostRequest(Urls.URL_FEEDBACK, mHandler, paramMap, this);
                break;
            //停车场相关问题:1
            case R.id.park_question:
                ivPark.setVisibility(View.VISIBLE);
                ivApp.setVisibility(View.GONE);
                type = "1";
                break;
            //App相关问题:2
            case R.id.app_question:
                ivApp.setVisibility(View.VISIBLE);
                ivPark.setVisibility(View.GONE);
                type = "2";
                break;
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                int code = JsonUtils.getCodeResult(msg.obj.toString());
                if (code == 0) {
                    ToastUtils.showShort(context, "反馈成功");
                    finish();
                } else {
                    ErrorUtils.errorCode(context, code);
                }
            }
            super.handleMessage(msg);
        }
    };
}
