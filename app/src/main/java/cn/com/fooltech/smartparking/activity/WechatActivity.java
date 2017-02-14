package cn.com.fooltech.smartparking.activity;

import android.os.Bundle;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.fooltech.smartparking.R;

public class WechatActivity extends BaseActivity {

    @Bind(R.id.back_wechat)
    ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wechat);
        ButterKnife.bind(this);
    }


    @OnClick(R.id.back_wechat)
    public void onClick() {
        finish();
    }
}
