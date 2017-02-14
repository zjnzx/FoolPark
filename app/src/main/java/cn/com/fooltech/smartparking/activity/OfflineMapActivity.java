package cn.com.fooltech.smartparking.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;
import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.fragment.DownLoadManageFragment;
import cn.com.fooltech.smartparking.fragment.WholeCityFragment;
import cn.com.fooltech.smartparking.offlinemap.adapter.OfflineExpandableListAdapter;
import cn.com.fooltech.smartparking.offlinemap.adapter.OfflineMapAdapter;
import cn.com.fooltech.smartparking.offlinemap.bean.OfflineMapItem;
import cn.com.fooltech.smartparking.offlinemap.interfaces.OnOfflineItemStatusChangeListener;
import cn.com.fooltech.smartparking.offlinemap.utils.CsqBackgroundTask;
import cn.com.fooltech.smartparking.offlinemap.utils.ToastUtil;
import cn.com.fooltech.smartparking.service.MyService;

public class OfflineMapActivity extends BaseActivity implements BDLocationListener {
    @Bind(R.id.back_offline)
    ImageView ivBack;
    @Bind(R.id.download)
    RadioButton rbDownload;
    @Bind(R.id.all_city)
    RadioButton rbCity;
    @Bind(R.id.rg_offline)
    RadioGroup radioGroup;
    @BindColor( R.color.green )
    int green;
    @BindColor( R.color.white )
    int white;
    private Context context = OfflineMapActivity.this;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private WholeCityFragment wholeCityFragment;
    private DownLoadManageFragment downLoadManageFragment;
    private LocationClient mLocationClient;
    private List<OfflineMapItem> itemsDown; //下载或下载中城市
    private List<OfflineMapItem> itemsAll;  //所有城市，与热门城市及下载管理对象相同
    private OfflineMapAdapter allSearchAdapter;
    private OfflineExpandableListAdapter allCountryAdapter;
    private boolean isWake = false;
    private boolean isResumed = false;
    private List<OfflineMapItem> itemsProvince;
    private List<List<OfflineMapItem>> itemsProvinceCity;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_map);
        ButterKnife.bind(this);

        initView();
        initLocation();
        setSelection(rbDownload, 0);
    }

    private void initView() {
        fragmentManager = getFragmentManager();

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                if (id == R.id.download) { //下载管理
                    setSelection(rbDownload, 0);
                } else if (id == R.id.all_city) { //全部城市
                    setSelection(rbCity, 1);
                }
            }
        });
    }

    private void setSelection(RadioButton rb, int index) {
        transaction = fragmentManager.beginTransaction();
        hideFragment();
        clearState();
        rb.setTextColor(white);
        switch (index) {
            case 0:
                if(downLoadManageFragment == null){
                    downLoadManageFragment = DownLoadManageFragment.newInstance();
                    transaction.add(R.id.lay_content_offline, downLoadManageFragment);
                }else{
                    transaction.show(downLoadManageFragment);
                    downLoadManageFragment.getUpdateInfo();
                }
                break;
            case 1:
                if(wholeCityFragment == null){
                    wholeCityFragment = WholeCityFragment.newInstance();
                    transaction.add(R.id.lay_content_offline, wholeCityFragment);
                }else{
                    transaction.show(wholeCityFragment);
                    wholeCityFragment.loadData();
                }
                break;
        }
        transaction.commit();
    }

    private void hideFragment() {
        if (wholeCityFragment != null) {
            transaction.hide(wholeCityFragment);
        }
        if (downLoadManageFragment != null) {
            transaction.hide(downLoadManageFragment);
        }
    }

    private void clearState() {
        rbDownload.setTextColor(green);
        rbCity.setTextColor(green);
    }

    public void toDownloadPage() {
        rbDownload.setChecked(true);
    }


    /**
     * @author Jackie
     * 百度地图定位初始化
     */
    private void initLocation() {
        mLocationClient = new LocationClient(this);
        mLocationClient.registerLocationListener(this);  //设置地图定位回调监听
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置定位模式
        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02  国测局经纬度坐标系gcj02、百度墨卡托坐标系bd09、百度经纬度坐标系bd09ll
        option.setIsNeedAddress(true);//返回的定位结果包含地址信息
//        option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms 不设置或设置数值小于1000ms标识只定位一次
        mLocationClient.setLocOption(option);

    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        MyApplication.currentCity = bdLocation.getCity();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mLocationClient.isStarted())
            mLocationClient.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isResumed) {
            isResumed = true;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLocationClient.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
    }

}
