package cn.com.fooltech.smartparking.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;
import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.adapter.CarManagerAdapter;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.bean.BindCarInfo;
import cn.com.fooltech.smartparking.common.Urls;
import cn.com.fooltech.smartparking.bean.jsonbean.GetBindCar;
import cn.com.fooltech.smartparking.utils.ErrorUtils;
import cn.com.fooltech.smartparking.utils.GuideUtils;
import cn.com.fooltech.smartparking.utils.HttpUtils;
import cn.com.fooltech.smartparking.utils.JsonUtils;
import cn.com.fooltech.smartparking.utils.SPUtils;
import cn.com.fooltech.smartparking.utils.ToastUtils;
import cn.com.fooltech.smartparking.utils.Utils;

public class CarManageActivity extends BaseActivity {
    @Bind(R.id.lv_car_manager)
    ListView mListView;
    @Bind(R.id.empty_car)
    ImageView ivEmpty;
    @Bind(R.id.lay_root_car_manage1)
    RelativeLayout mLayout;
    @BindColor( R.color.gray )
    int gray;
    private Context context = CarManageActivity.this;
    private List<BindCarInfo> resultList;
    private LinearLayout layRecharge, layDeposit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_manage);
        ButterKnife.bind(this);

        GuideUtils guideUtil = GuideUtils.getInstance();
        guideUtil.addGuideImage(this,mLayout,R.drawable.ic_guide_3,"guide_car_add");


        getBindCarList();

    }

    /**
     * 获取绑定车辆信息
     */
    private void getBindCarList() {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userId", SPUtils.get(this, "userId", new Long(0l)));
        paramMap.put("token", SPUtils.get(this, "token", ""));
        HttpUtils.sendHttpPostRequest(Urls.URL_GET_BIND_CAR, handlerBindCar, paramMap, this);
    }

    private void initAdapter() {
        CarManagerAdapter mAdapter = new CarManagerAdapter(this, resultList,mLayout);
        mListView.setAdapter(mAdapter);
        mListView.setEmptyView(ivEmpty);

    }

    Handler handlerBindCar = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_NET) {
                ivEmpty.setBackgroundResource(R.drawable.empty_err);
            } else if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                GetBindCar bindCar = (GetBindCar) JsonUtils.jsonToObject(msg.obj.toString(), GetBindCar.class);
                if (bindCar != null) {
                    int code = bindCar.getCode();
                    if (code == 0) {
                        resultList = bindCar.getContent();
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

    public void onClick(View view) {
        switch (view.getId()) {
            //返回
            case R.id.back_car_mana:
                finish();
                break;
            //添加车辆
            case R.id.add_car:
                MyApplication.isUpdateCar = false;
                startActivity(new Intent(this, CarAddActivity.class));
                break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (MyApplication.isUpdateCar || MyApplication.isRecharge) {
            getBindCarList();
        }
        MyApplication.isRecharge = false;
    }
}
