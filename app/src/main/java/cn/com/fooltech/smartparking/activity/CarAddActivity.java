package cn.com.fooltech.smartparking.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.adapter.ParkChoicePagerAdapter;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.bean.BindCarInfo;
import cn.com.fooltech.smartparking.common.Urls;
import cn.com.fooltech.smartparking.utils.ErrorUtils;
import cn.com.fooltech.smartparking.utils.HttpUtils;
import cn.com.fooltech.smartparking.utils.JsonUtils;
import cn.com.fooltech.smartparking.utils.KeyboardUtil;
import cn.com.fooltech.smartparking.utils.SPUtils;
import cn.com.fooltech.smartparking.utils.ToastUtils;
import cn.com.fooltech.smartparking.utils.Utils;


public class CarAddActivity extends BaseActivity {
    @Bind(R.id.vp_add_car)
    ViewPager mViewPager;
    EditText etGeneralInput1,etGeneralInput2,etGeneralInput3,etGeneralInput4,etGeneralInput5,etGeneralInput6,etGeneralInput7,
            etEnergyInput1,etEnergyInput2,etEnergyInput3,etEnergyInput4,etEnergyInput5,etEnergyInput6,etEnergyInput7,etEnergyInput8;
//    @Bind(R.id.input1)
//    EditText etInput1;
//    @Bind(R.id.input2)
//    EditText etInput2;
//    @Bind(R.id.input3)
//    EditText etInput3;
//    @Bind(R.id.input4)
//    EditText etInput4;
//    @Bind(R.id.input5)
//    EditText etInput5;
//    @Bind(R.id.input6)
//    EditText etInput6;
//    @Bind(R.id.input7)
//    EditText etInput7;
    @Bind(R.id.sure)
    Button btnSure;
    @Bind(R.id.switch_plate)
    CheckBox cbSwitch;
    @Bind(R.id.stub_general)
    ViewStub vsGeneral;
    @Bind(R.id.stub_energy)
    ViewStub vsEnergy;
    private Context context = CarAddActivity.this;
    private int currentItem, len;
    private int[] imgId;
    private KeyboardUtil keyboardUtil;
    private String plateNumber;
    private AlertDialog dialog;
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_add);
        ButterKnife.bind(this);

        vsGeneral.setVisibility(View.VISIBLE);
        flag = getIntent().getBooleanExtra("reg", false);

        initView();
        initViewPager();
        popupKeyBoark();

        cbSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    vsEnergy.setVisibility(View.VISIBLE);
                    vsGeneral.setVisibility(View.GONE);
                    cbSwitch.setText(R.string.switch_general);
                }else {
                    vsGeneral.setVisibility(View.VISIBLE);
                    vsEnergy.setVisibility(View.GONE);
                    cbSwitch.setText(R.string.switch_energy);
                }
            }
        });
    }

    private void initView(){
        etGeneralInput1 = (EditText) findViewById(R.id.general_input1);
        etGeneralInput2 = (EditText) findViewById(R.id.general_input2);
        etGeneralInput3 = (EditText) findViewById(R.id.general_input3);
        etGeneralInput4 = (EditText) findViewById(R.id.general_input4);
        etGeneralInput5 = (EditText) findViewById(R.id.general_input5);
        etGeneralInput6 = (EditText) findViewById(R.id.general_input6);
        etGeneralInput7 = (EditText) findViewById(R.id.general_input7);
        etEnergyInput1 = (EditText) findViewById(R.id.energy_input1);
        etEnergyInput2 = (EditText) findViewById(R.id.energy_input2);
        etEnergyInput3 = (EditText) findViewById(R.id.energy_input3);
        etEnergyInput4 = (EditText) findViewById(R.id.energy_input4);
        etEnergyInput5 = (EditText) findViewById(R.id.energy_input5);
        etEnergyInput6 = (EditText) findViewById(R.id.energy_input6);
        etEnergyInput7 = (EditText) findViewById(R.id.energy_input7);
        etEnergyInput8 = (EditText) findViewById(R.id.energy_input8);
    }

    /**
     * 弹出键盘
     */
    private void popupKeyBoark() {
        keyboardUtil = new KeyboardUtil(this, new EditText[]{etGeneralInput1, etGeneralInput2, etGeneralInput3, etGeneralInput4, etGeneralInput5, etGeneralInput6, etGeneralInput7}, btnSure);
        keyboardUtil.showKeyboard();

    }

    /**
     * 选择车辆viewpager
     */
    private void initViewPager() {
        imgId = new int[]{R.drawable.car_add1, R.drawable.car_add2, R.drawable.car_add3};
        len = imgId.length;
        ParkChoicePagerAdapter mAdapter = new ParkChoicePagerAdapter(imgId, this);
        mViewPager.setAdapter(mAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentItem = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    public void onClick(View view) {
        switch (view.getId()) {
            //返回
            case R.id.back_add_car:
                finish();
                onBackPressed();
                break;
            //左滑
            case R.id.slide_left:
                if (currentItem != 0) {
                    currentItem--;
                    mViewPager.setCurrentItem(currentItem, true);
                }
                break;
            //右滑
            case R.id.slide_right:
                if (currentItem != len - 1) {
                    currentItem++;
                    mViewPager.setCurrentItem(currentItem, true);
                }
                break;
            //关闭键盘
            case R.id.close:
                keyboardUtil.hideKeyboard();
                break;
            case R.id.sure:
                StringBuffer buffer = new StringBuffer();
                buffer.append(etGeneralInput1.getText().toString()).append(etGeneralInput2.getText().toString()).append(etGeneralInput3.getText().toString())
                        .append(etGeneralInput4.getText().toString()).append(etGeneralInput5.getText().toString()).append(etGeneralInput6.getText().toString())
                        .append(etGeneralInput7.getText().toString());
                plateNumber = buffer.toString();
                String number = plateNumber.substring(2);
                if (containNum(number)) {
                    bindPlateNum();
                } else {
                    ToastUtils.showShort(this, "格式错误");
                }
                break;
            case R.id.general_input1:
                keyboardUtil.showKeyboard(1);
                keyboardUtil.setBackground(0);
                break;
            case R.id.general_input2:
                keyboardUtil.showKeyboard(2);
                keyboardUtil.setBackground(1);
                break;
            case R.id.general_input3:
                keyboardUtil.showKeyboard(3);
                keyboardUtil.setBackground(2);
                break;
            case R.id.general_input4:
                keyboardUtil.showKeyboard(3);
                keyboardUtil.setBackground(3);
                break;
            case R.id.general_input5:
                keyboardUtil.showKeyboard(3);
                keyboardUtil.setBackground(4);
                break;
            case R.id.general_input6:
                keyboardUtil.showKeyboard(3);
                keyboardUtil.setBackground(5);
                break;
            case R.id.general_input7:
                keyboardUtil.showKeyboard(4);
                keyboardUtil.setBackground(6);
                break;
        }
    }

    /**
     * 是否包含三位数字
     *
     * @param str
     * @return
     */
    private boolean containNum(String str) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i))) {
                count++;
                if (count >= 3) {
                    return true;
                }
            }
        }
        return false;
    }

    //绑定车牌
    private void bindPlateNum() {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userId", SPUtils.get(this, "userId", new Long(0l)));
        paramMap.put("token", SPUtils.get(this, "token", ""));
        paramMap.put("plateNumber", plateNumber);
        paramMap.put("imageIndex", currentItem);
        HttpUtils.sendHttpPostRequest(Urls.URL_BIND_CAR, handlerBindCar, paramMap, this);

//        Intent intent = new Intent(context,CarRegainActivity.class);
//        intent.putExtra("plateNumber",plateNumber);
//        startActivity(intent);
    }

    Handler handlerBindCar = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                int code = JsonUtils.getCodeResult(msg.obj.toString());
                if (code == 0) {
                    ToastUtils.showShort(context, "绑定成功");
                    MyApplication.isUpdateCar = true;
//                    setResult(-1);
                    finish();
                } else if (code == 50003) {//当前车辆已被其他用户绑定，需要车主授权
                    showBindCarDialog();
                } else {
                    ErrorUtils.errorCode(context, code);
                }
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 显示绑定对话框
     */
    private void showBindCarDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        dialog = builder.create();
        dialog.show();

        Window window = dialog.getWindow();
        window.setContentView(R.layout.dialog_car_bind);
        TextView tvApply = (TextView) window.findViewById(R.id.continue_apply);
        TextView tvRegain = (TextView) window.findViewById(R.id.apply_regain);
        ImageView ivClose = (ImageView) window.findViewById(R.id.close_x2);

        //申请绑定
        tvApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> paramMap = new HashMap<String, Object>();
                paramMap.put("userId", SPUtils.get(context, "userId", new Long(0l)));
                paramMap.put("token", SPUtils.get(context, "token", ""));
                paramMap.put("plateNumber", plateNumber);
                paramMap.put("imageIndex", currentItem);
                HttpUtils.sendHttpPostRequest(Urls.URL_APPLY_BIND, handlerApplyBind, paramMap, CarAddActivity.this);
            }
        });
        //申请找回
        tvRegain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CarRegainActivity.class);
                intent.putExtra("plateNumber", plateNumber);
                startActivity(intent);
                dialog.cancel();
            }
        });
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
    }

    Handler handlerApplyBind = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                int code = JsonUtils.getCodeResult(msg.obj.toString());
                if (code == 0) {
                    dialog.cancel();
                } else {
                    ErrorUtils.errorCode(context, code);
                }
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (flag) {
            startActivity(new Intent(this, IndexActivity.class));
        }
    }
}
