package cn.com.fooltech.smartparking.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.utils.SPUtils;
import cn.com.fooltech.smartparking.utils.ToastUtils;
import cn.com.fooltech.smartparking.view.CustomEditText;


public class ChangeMobileActivity extends BaseActivity {
    @Bind(R.id.back_change_mobile)
    ImageView ivBack;
    @Bind(R.id.old_mobile)
    TextView tvMobile;
    @Bind(R.id.change_mobile_pwd)
    CustomEditText etPassword;
    @Bind(R.id.vis_pwd)
    CheckBox cbVisPwd;
    @Bind(R.id.btn_next)
    Button btnNext;
    private Context context = ChangeMobileActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_mobile);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        tvMobile.setText(SPUtils.get(this, "mobile", "").toString());
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //是否显示密码
        cbVisPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String password = etPassword.getText().toString();
                if (b) { //如果选中，显示密码
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    etPassword.setSelection(password.length());
                } else { //否则隐藏密码
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    etPassword.setSelection(password.length());
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldPwd = (String) SPUtils.get(context, "password", "");
                String oldPwd2 = etPassword.getText().toString();
                if (etPassword.getText().toString().isEmpty()) {
                    ToastUtils.showShort(context, "请输入密码");
                    return;
                } else if (!oldPwd.equals(oldPwd2)) {
                    ToastUtils.showShort(context, "密码错误");
                    return;
                }
                startActivity(new Intent(context, ChangeMobileActivity2.class));
            }
        });
    }

}
