package cn.com.fooltech.smartparking.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.utils.ToastUtils;
import cn.com.fooltech.smartparking.view.CustomEditText;


public class ResetPasswordActivity extends BaseActivity {
    @Bind(R.id.back_reset)
    ImageView ivBack;
    @Bind(R.id.reset_mobile)
    CustomEditText etMobile;
    @Bind(R.id.bt_next)
    Button btnNext;
    private Context context = ResetPasswordActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mobile = etMobile.getText().toString();
                if (mobile.isEmpty()) {
                    ToastUtils.showShort(context, "请输入手机号");
                    return;
                } else if (mobile.length() != 11) {
                    ToastUtils.showShort(context, "手机号错误");
                    return;
                }
                Intent intent = new Intent(ResetPasswordActivity.this, ResetPasswordActivity2.class);
                intent.putExtra("mobile", mobile);
                startActivity(intent);
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


}
