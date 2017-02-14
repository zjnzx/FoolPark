package cn.com.fooltech.smartparking.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.baidu.vi.VDeviceAPI;
import com.iflytek.cloud.SpeechRecognizer;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.adapter.ParkInfoPagerAdapter;
import cn.com.fooltech.smartparking.adapter.ParkInfoSearchAdapter;
import cn.com.fooltech.smartparking.adapter.ParkInfoSearchHisAdapter;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.baidumap.DemoRoutePlanListener;
import cn.com.fooltech.smartparking.baidumap.MyOrientationListener;
import cn.com.fooltech.smartparking.bean.ParkInfo;
import cn.com.fooltech.smartparking.bean.PoiInfo;
import cn.com.fooltech.smartparking.common.Common;
import cn.com.fooltech.smartparking.common.Urls;
import cn.com.fooltech.smartparking.bean.jsonbean.GetParkInfo;
import cn.com.fooltech.smartparking.bean.jsonbean.GetPoiInfo;
import cn.com.fooltech.smartparking.dao.SearchHisDao;
import cn.com.fooltech.smartparking.utils.AppUtils;
import cn.com.fooltech.smartparking.utils.ErrorUtils;
import cn.com.fooltech.smartparking.utils.HttpUtils;
import cn.com.fooltech.smartparking.utils.JsonUtils;
import cn.com.fooltech.smartparking.utils.SPUtils;
import cn.com.fooltech.smartparking.utils.ToastUtils;
import cn.com.fooltech.smartparking.utils.Utils;
import cn.com.fooltech.smartparking.view.MyRadioGroup;
import cn.com.fooltech.smartparking.voice.VouceUtils;


public class ParkMapActivity extends BaseActivity {
    @Bind(R.id.park_name_input)
    EditText etParkName;
    @Bind(R.id.bmapView)
    MapView mMapView;
    @Bind(R.id.layer_type)
    ImageView ivLayerType;
    @Bind(R.id.cb_traffic)
    CheckBox cbTrafic;
    @Bind(R.id.vp_park_info)
    ViewPager mViewPager;
    @Bind(R.id.seek_bar)
    SeekBar seekBar;
    @Bind(R.id.all_park)
    TextView tvAllPark;
    @Bind(R.id.pay_park)
    TextView tvPayPark;
    @Bind(R.id.guide_park)
    TextView tvGuidePark;
    @Bind(R.id.lay_nav)
    RelativeLayout layNav;
    @Bind(R.id.listview_search)
    ListView mListView;
    @Bind(R.id.lay_list)
    RelativeLayout layList;
    @Bind(R.id.title_layout)
    RelativeLayout layTitle;
    private Context context = ParkMapActivity.this;
    private int distance = 1000;
    private BaiduMap mBaiduMap = null;
    private LocationClient mLocationClient = null;
    private LocationMode mLocationMode;
    private boolean isFirstLoc = true;// 是否首次定位
    private BitmapDescriptor mIconLocation;// 自定义定位图标
    private BitmapDescriptor bitMapParkCheck, bitMapGasCheck, bitMapElecCheck;//选中的图标
    private BitmapDescriptor bitMapNormal;//未选中的图标
    private BitmapDescriptor bitMapPark;//停车场图标
    private BitmapDescriptor bitMapGas;//加油站图标
    private BitmapDescriptor bitMapElec;//充电站图标
    private MyOrientationListener myOrientationListener;
    public MyLocationListener mLocationListener;
    private float mCurrentX;
    private float mLatitude,mLongtitude;//中心位置坐标
    private float mCurrentLat,mCurrentLng;//当前位置坐标
    private List<PoiInfo> allList = null;
    private ArrayList<ParkInfo> allParkList = null; //全部停车场
    private ArrayList<ParkInfo> payList = null;//可支付的
    private ArrayList<ParkInfo> guideList = null;//可引导的
    private ParkInfoPagerAdapter mAdapter;
    private TextView tvPark, tvElec, tvGas, tvAll;
    private String keyWord;
    private RadioButton rbPark, rbGasStation, rbElecStation, rbAll;
    private UiSettings mSettings;
    private PoiSearch mPoiSearch;
    private OnGetPoiSearchResultListener poiListener;
    private Marker lastMarker;//上次选中的marker
    private Marker currentMarker;//当前选中的marker
    private List<Overlay> mOverlayList;
    private MyRadioGroup radioGroup;
    private int parkType = 0;//停车场类型
    private int poiType = 0; //图层兴趣点类型
    private String emptyPrompt = "附近暂无停车场";
    private boolean isSearch = true;  //移动地图后是否搜索
    private boolean isScroll = false;  //是否滑动了停车场选项卡
    private SpeechRecognizer mIat;
    private ParkInfo parkCollectInfo;
    private int posi;
    private boolean isFirstIn = true;
    private int count;
    private List<ParkInfo> searchList;
    private List<ParkInfo> searchHisList;
    private ParkInfoSearchAdapter searchAdapter;
    private ParkInfoSearchHisAdapter searchHisAdapter;
    private View clearView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_park_map);
        ButterKnife.bind(this);

        initView();

        parkCollectInfo = (ParkInfo) getIntent().getSerializableExtra("parkInfo");//收藏页面传递的数据

        //设置为false,显示默认图层
        resetDefault();
    }

    private boolean checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) { //没有权限
            return false;
        }else {
            return true;
        }
    }

    private void initView() {
        tvAllPark.setTextColor(getResources().getColor(R.color.green));

//        seekBar.setPadding(Common.dp2px(50, this), 0, Common.dp2px(50, this), 0);

        initLocation();
        mBaiduMap = mMapView.getMap();
        mSettings = mBaiduMap.getUiSettings();
//        mPoiSearch = PoiSearch.newInstance();//创建POI检索实例
        mMapView.showZoomControls(false);//隐藏缩放按钮
        mMapView.removeViewAt(1);//隐藏百度logo
        mSettings.setCompassEnabled(false);//隐藏指南针
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(16.0f);//设置比例尺
        mBaiduMap.animateMapStatus(msu);

        allList = new ArrayList<PoiInfo>();
        allParkList = new ArrayList<ParkInfo>();
        payList = new ArrayList<ParkInfo>();
        guideList = new ArrayList<ParkInfo>();
        searchList = new ArrayList<>();;
        searchHisList = new ArrayList<>();;

        bitMapParkCheck = BitmapDescriptorFactory.fromResource(R.drawable.icon_park_check);
        bitMapGasCheck = BitmapDescriptorFactory.fromResource(R.drawable.icon_gas_check);
        bitMapElecCheck = BitmapDescriptorFactory.fromResource(R.drawable.icon_elec_check);
        bitMapNormal = BitmapDescriptorFactory.fromResource(R.drawable.icon_park);
        bitMapPark = BitmapDescriptorFactory.fromResource(R.drawable.icon_park);//构建Marker图标
        bitMapGas = BitmapDescriptorFactory.fromResource(R.drawable.icon_gas);//构建Marker图标
        bitMapElec = BitmapDescriptorFactory.fromResource(R.drawable.icon_elec);//构建Marker图标

        setListener();
        initSearchAdapter();
    }

    private void initSearchAdapter(){
        searchAdapter = new ParkInfoSearchAdapter(ParkMapActivity.this, searchList);
        mListView.setAdapter(searchAdapter);
        mListView.setEmptyView(findViewById(R.id.empty));


        searchHisAdapter = new ParkInfoSearchHisAdapter(this,searchHisList);
        clearView = LayoutInflater.from(this).inflate(R.layout.listview_footer,null);

        //删除搜索历史记录
        clearView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchHisDao searchHisDao = new SearchHisDao(context);
                searchHisDao.deleteHis();
                clearView.setVisibility(View.GONE);
                searchHisList.clear();
                searchHisAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 监听事件
     */
    private void setListener() {
        //底部seekbar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                clearState();
                if (seekBar.getProgress() <= 25) {
                    seekBar.setProgress(0);
                    parkType = 0;
                    tvAllPark.setTextColor(getResources().getColor(R.color.green));
                } else if (seekBar.getProgress() > 25 && seekBar.getProgress() < 75) {
                    seekBar.setProgress(50);
                    parkType = 1;
                    tvPayPark.setTextColor(getResources().getColor(R.color.green));
                } else if (seekBar.getProgress() >= 75) {
                    seekBar.setProgress(100);
                    parkType = 2;
                    tvGuidePark.setTextColor(getResources().getColor(R.color.green));
                }
                getPoiDatas(mLongtitude, mLatitude);
            }
        });
        //路况
        cbTrafic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mBaiduMap.setTrafficEnabled(true);
                } else {
                    mBaiduMap.setTrafficEnabled(false);
                }
            }
        });
        etParkName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) {
                    mListView.addFooterView(clearView);
                    mListView.setAdapter(searchHisAdapter);
                    SearchHisDao searchHisDao = new SearchHisDao(context);
                    List<ParkInfo> searchList = searchHisDao.querySearchHis();
                    if (searchList.size() > 0) {
                        searchHisList.clear();
                        searchHisList.addAll(searchList);
                        layList.setVisibility(View.VISIBLE);
                        searchHisAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int position) {
                currentMarker = (Marker) mOverlayList.get(position);
                if (lastMarker != null) {
                    lastMarker.setIcon(bitMapNormal);
                }
                bitMapNormal = currentMarker.getIcon();
                setMarkerIcon(currentMarker);
                lastMarker = currentMarker;
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        //设置marker的点击事件
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String title = marker.getTitle();
                if (marker.getIcon() != bitMapParkCheck) { //只有一条数据
                    if (mOverlayList != null && mOverlayList.size() == 1) {
                        currentMarker = marker;
                        bitMapNormal = currentMarker.getIcon();
                        setMarkerIcon(marker);
                        mViewPager.setCurrentItem(0);
                    } else {
                        if (title.equals("1")) {//第一个特殊处理
                            mViewPager.setCurrentItem(1, false);
                            mViewPager.setCurrentItem(0, true);
                        } else {
                            mViewPager.setCurrentItem(Integer.parseInt(title) - 1, true);
//                            marker.setIcon(bitMapParkCheck);
                            setMarkerIcon(marker);
                        }
                        marker.setToTop();//置顶
                    }
                    mViewPager.setVisibility(View.VISIBLE);
                    layNav.setVisibility(View.GONE);
                }
                return true;
            }
        });
        //地图点击事件
        mBaiduMap.setOnMapClickListener(new OnMapClickListener() {

            @Override
            public boolean onMapPoiClick(MapPoi arg0) {
                return false;
            }

            @Override
            public void onMapClick(LatLng arg0) {
                if (poiType == -1 || poiType == 0) {
                    layNav.setVisibility(View.VISIBLE);
                } else {
                    layNav.setVisibility(View.GONE);
                }
                mViewPager.setVisibility(View.GONE);
//                isSearch = true;

                if (currentMarker != null) {
                    currentMarker.setIcon(bitMapNormal);
                }

            }
        });
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {
                if (!isScroll) {
                    isSearch = true;
                }
            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {
            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                mLatitude = (float) mapStatus.target.latitude;
                mLongtitude = (float) mapStatus.target.longitude;
                if (isSearch) {
                    if (poiType == -1 || poiType == 0) {
                        layNav.setVisibility(View.VISIBLE);
                    } else {
                        layNav.setVisibility(View.GONE);
                    }

                    lastMarker = null;
                    mViewPager.setVisibility(View.GONE);
                    if(AppUtils.isOs6()) {
                        if (checkPermission()) {
                            getPoiDatas(mLongtitude, mLatitude);
                        }
                    }else {
                        getPoiDatas(mLongtitude, mLatitude);
                    }
                }
            }
        });
    }
    //搜索框监听事件
    @OnTextChanged(value = R.id.park_name_input,callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterTextChanged(Editable editable){
        keyWord = editable.toString();
        if ("".equals(keyWord)) {
            layList.setVisibility(View.GONE);
            return;
        }
        clearView.setVisibility(View.GONE);
        getSearchParkDatas(keyWord);
    }

    private void setMarkerIcon(Marker marker) {
        int type = (int) marker.getExtraInfo().get("type");
        if (type == 0) {
            marker.setIcon(bitMapParkCheck);
        } else if (type == 1) {
            marker.setIcon(bitMapGasCheck);
        } else if (type == 2) {
            marker.setIcon(bitMapElecCheck);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            //定位到当前位置
            case R.id.my_location:
                centerToLocation(MyApplication.latitude, MyApplication.longtitude);
                break;
            //图层显示
            case R.id.layer:
                initLayer();
                break;
            //停车场列表
            case R.id.park_list:
                Intent intent = new Intent(this, ParkPriorityActivity.class);
                intent.putExtra("list", allParkList);
                startActivityForResult(intent, 0);
                break;
            //放大地图
            case R.id.plus:
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomIn());
                break;
            //缩小地图
            case R.id.less:
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomOut());
                break;
            //全部
            case R.id.all_park:
                seekBar.setProgress(0);
                parkType = 0;
                getPoiDatas(mLongtitude, mLatitude);
                clearState();
                tvAllPark.setTextColor(getResources().getColor(R.color.green));
                break;
            //可支付
            case R.id.pay_park:
                seekBar.setProgress(50);
                parkType = 1;
                getPoiDatas(mLongtitude, mLatitude);
                clearState();
                tvPayPark.setTextColor(getResources().getColor(R.color.green));
                break;
            //可引导
            case R.id.guide_park:
                seekBar.setProgress(100);
                parkType = 2;
                getPoiDatas(mLongtitude, mLatitude);
                clearState();
                tvGuidePark.setTextColor(getResources().getColor(R.color.green));
                break;
            //返回
            case R.id.back_park_map:
                finish();
                break;
            //语音
            case R.id.voice:
                VouceUtils.mIatResults.clear();
                etParkName.setText("");
//                mIat = VouceUtils.startLinsten(this);
//                ToastUtils.showShort(this,"请开始说话...");
//                //1.创建SpeechRecognizer对象，第二个参数：本地听写时传InitListener
//                RecognizerDialog  iatDialog = new RecognizerDialog(this, VouceUtils.mInitListener);
//                //2.设置听写参数，同上节
//                //3.设置回调接口
//                iatDialog.setListener(mRecognizerDialogListener);
//                //4.开始听写
//                iatDialog.show();
                Intent intent1 = new Intent(this, VoiceActivity.class);
                intent1.putExtra("type", 1);
                startActivityForResult(intent1, 2);
                break;
        }
    }

    /**
     * 初始化图层
     */
    private void initLayer() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        final AlertDialog dialog = builder.create();
        dialog.show();

        Window window = dialog.getWindow();
        window.setContentView(R.layout.dialog_layer);

        radioGroup = (MyRadioGroup) window.findViewById(R.id.radiogroup_layer);

        rbPark = (RadioButton) window.findViewById(R.id.layer_park);
        rbGasStation = (RadioButton) window.findViewById(R.id.layer_gas_station);
        rbElecStation = (RadioButton) window.findViewById(R.id.layer_elec_station);
        rbAll = (RadioButton) window.findViewById(R.id.layer_all);

        tvPark = (TextView) window.findViewById(R.id.tv_park);
        tvElec = (TextView) window.findViewById(R.id.tv_elec);
        tvGas = (TextView) window.findViewById(R.id.tv_gas);
        tvAll = (TextView) window.findViewById(R.id.tv_all);

        rbPark.setChecked((Boolean) SPUtils.get(this, "park", false));
        rbGasStation.setChecked((Boolean) SPUtils.get(this, "gasStation", false));
        rbElecStation.setChecked((Boolean) SPUtils.get(this, "elecStation", false));
        rbAll.setChecked((Boolean) SPUtils.get(this, "all", false));

        if (rbPark.isChecked()) {
            tvPark.setTextColor(getResources().getColor(R.color.green));
        } else if (rbElecStation.isChecked()) {
            tvElec.setTextColor(getResources().getColor(R.color.green));
        }
        if (rbGasStation.isChecked()) {
            tvGas.setTextColor(getResources().getColor(R.color.green));
        }
        if (rbAll.isChecked()) {
            tvAll.setTextColor(getResources().getColor(R.color.green));
        }

        //选择图层显示
        radioGroup.setOnCheckedChangeListener(new MyRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(MyRadioGroup group, int checkedId) {
//                if(!NetUtils.isConn(context)) return;
                resetDefault();
                if (checkedId == R.id.layer_park) { //停车场
                    poiType = 0;
                    emptyPrompt = "附件暂无停车场";
                    layNav.setVisibility(View.VISIBLE);
                    ivLayerType.setBackgroundResource(R.drawable.icon_park);
                    SPUtils.put(context, "park", true);
                    getPoiDatas(mLongtitude, mLatitude);
                } else if (checkedId == R.id.layer_gas_station) {//加油站
                    poiType = 1;
                    emptyPrompt = "附件暂无加油站";
                    layNav.setVisibility(View.GONE);
                    ivLayerType.setBackgroundResource(R.drawable.icon_gas);
                    SPUtils.put(context, "gasStation", true);
                    getPoiDatas(mLongtitude, mLatitude);//获取兴趣点
                } else if (checkedId == R.id.layer_elec_station) { //充电站
                    poiType = 2;
                    emptyPrompt = "附件暂无充电站";
                    layNav.setVisibility(View.GONE);
                    ivLayerType.setBackgroundResource(R.drawable.icon_elec);
                    SPUtils.put(context, "elecStation", true);
                    getPoiDatas(mLongtitude, mLatitude);//获取兴趣点
                } else if (checkedId == R.id.layer_all) { //全部
                    poiType = -1;
                    emptyPrompt = "未找到结果";
                    layNav.setVisibility(View.VISIBLE);
                    ivLayerType.setBackgroundResource(R.drawable.icon_all);
                    SPUtils.put(context, "all", true);
                    getPoiDatas(mLongtitude, mLatitude);//获取兴趣点
                }
                mViewPager.setVisibility(View.GONE);
                dialog.cancel();
            }
        });

    }

    /**
     * 初始化定位
     */
    private void initLocation() {
        mLocationMode = LocationMode.NORMAL;
        mIconLocation = BitmapDescriptorFactory.fromResource(R.drawable.location);// 初始化图标
        mLocationClient = new LocationClient(this);
        mLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mLocationListener);
        this.setLocationOption();   //设置定位参数
        mLocationClient.start();// 开启定位

        //方向传感器
        myOrientationListener = new MyOrientationListener(this);
        myOrientationListener.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {
                mCurrentX = x;
            }
        });
    }

    /**
     * 清除颜色状态
     */
    private void clearState() {
        tvAllPark.setTextColor(getResources().getColor(R.color.text));
        tvPayPark.setTextColor(getResources().getColor(R.color.text));
        tvGuidePark.setTextColor(getResources().getColor(R.color.text));
    }

    /**
     * 定位到中心位置
     */
    private void centerToLocation(double lat, double lng) {
        LatLng latLng = new LatLng(lat, lng);
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
        if(mBaiduMap == null){
            mBaiduMap = mMapView.getMap();
        }
        mBaiduMap.animateMapStatus(msu);
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .direction(mCurrentX) //GPS定位时方向角度
                    .accuracy(location.getRadius()) //定位精度
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            mBaiduMap.setMyLocationEnabled(true);

            // 设置自定义图标
            MyLocationConfiguration config = new MyLocationConfiguration(
                    mLocationMode, true, mIconLocation);
            mBaiduMap.setMyLocationConfigeration(config);
            // 更新经纬度
            MyApplication.latitude = (float) location.getLatitude();
            MyApplication.longtitude = (float) location.getLongitude();
//            Logger.d(MyApplication.longtitude+"==onReceiveLocation==="+MyApplication.latitude);

            if (isFirstLoc) {
                mCurrentLat = (float) location.getLatitude();
                mCurrentLng = (float) location.getLongitude();
                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(msu);
                isFirstLoc = false;
                etParkName.setHint(location.getAddrStr());
            }
        }
    }

    /**
     * 设置定位参数
     */
    private void setLocationOption() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开GPS
        option.setCoorType("bd09ll"); // 返回的定位结果是百度经纬度,默认值gcj02
//        option.setScanSpan(5000); // 设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true); // 返回的定位结果包含地址信息
        mLocationClient.setLocOption(option);
    }

    /**
     * 请求网络获取搜索的停车场数据
     */
    private void getSearchParkDatas(String name) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("lng", MyApplication.longtitude);
        paramMap.put("lat", MyApplication.latitude);
        paramMap.put("parkName", name);
        paramMap.put("userId", SPUtils.get(context, "userId", new Long(0l)));
        paramMap.put("maxNum", 10);
        if (parkType != 0)
            paramMap.put("parkType", parkType);

        HttpUtils.sendHttpPostRequest(Urls.URL_SEARCH_PARK, handlerSearch, paramMap, this);
    }

    /**
     * 获取图层兴趣点
     */
    private void getPoiDatas(float mLongtitude, float mLatitude) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("lng", mLongtitude);
        paramMap.put("lat", mLatitude);
        paramMap.put("distance", distance);
        paramMap.put("maxNum", 10);
        paramMap.put("userId", SPUtils.get(context, "userId", new Long(0l)));
        if (poiType != -1)
            paramMap.put("poiType", poiType);
        if (parkType != 0)
            paramMap.put("parkType", parkType);

        HttpUtils.sendHttpPostRequest(Urls.URL_POI_LIST, handlerPoi, paramMap, this);
    }


    /**
     * 添加兴趣点
     *
     * @param resultList
     */
    private void addOverlaysPoi(List<PoiInfo> resultList) {
        mOverlayList = new ArrayList<Overlay>();
        MarkerOptions options = null;
        for (int i = 0, len = resultList.size(); i < len; i++) {
            PoiInfo poiInfo = (PoiInfo) resultList.get(i);
            LatLng latLng = new LatLng(poiInfo.getPoiLat(), poiInfo.getPoiLng());// 经纬度
            if (poiInfo.getPoiType() == 0) { //停车场图标
                options = new MarkerOptions().position(latLng).icon(bitMapPark).zIndex(5);
            } else if (poiInfo.getPoiType() == 1) {//加油站图标
                options = new MarkerOptions().position(latLng).icon(bitMapGas).zIndex(5);
            } else if (poiInfo.getPoiType() == 2) {//充电站图标
                options = new MarkerOptions().position(latLng).icon(bitMapElec).zIndex(5);
            }
            options.animateType(MarkerOptions.MarkerAnimateType.grow);//添加生长动画
            Marker marker = (Marker) (mBaiduMap.addOverlay(options));
            marker.setTitle(i + 1 + "");
            Bundle bundle = new Bundle();
            if (poiInfo.getPoiType() == 0) {
                bundle.putInt("type", 0);
            } else if (poiInfo.getPoiType() == 1) {
                bundle.putInt("type", 1);
            } else if (poiInfo.getPoiType() == 2) {
                bundle.putInt("type", 2);
            }
            marker.setExtraInfo(bundle);
            mOverlayList.add(marker);
        }

//        //使所有Overlay都在合适的视野内
//        if (mOverlayList.size() > 0) {
//            LatLngBounds.Builder builder = new LatLngBounds.Builder();
//            for (Overlay overlay : mOverlayList) {
//                if (overlay instanceof Marker) {
//                    builder.include(((Marker) overlay).getPosition());
//                }
//            }
//            if(mBaiduMap != null) {
//                mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngBounds(builder.build()));
//            }
//        }

        if (parkCollectInfo != null && isFirstIn) {
            int dist = (int) Common.GetLongDistance(mCurrentLng,mCurrentLat,parkCollectInfo.getParkLng(),parkCollectInfo.getParkLat());
            if(dist <= distance){
                isScroll = true;
                isSearch = false;
                isFirstIn = false;
            }else {
                if(count == 1){
                    isFirstIn = false;
                }
                count++;
            }
            centerToLocation(parkCollectInfo.getParkLat(), parkCollectInfo.getParkLng());
            locationPark(parkCollectInfo);
        }
    }

    //获取附近的停车场
    Handler handlerPoi = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                GetPoiInfo poiInfo = (GetPoiInfo) JsonUtils.jsonToObject(msg.obj.toString(), GetPoiInfo.class);
                if (poiInfo != null) {
                    int code = poiInfo.getCode();
                    if (code == 0) {
                        mBaiduMap.clear();
                        allList = poiInfo.getContent();
                        allParkList.clear();
                        if (allList != null && allList.size() > 0) {
                            for (PoiInfo info : allList) { //筛选出停车场的详细信息
                                if (info.getPoiType() == 0) {
                                    ParkInfo parkInfo = info.getDetailInfo();
                                    parkInfo.setParkName(info.getPoiName());
                                    parkInfo.setParkAddress(info.getPoiAddress());
                                    parkInfo.setParkLat(info.getPoiLat());
                                    parkInfo.setParkLng(info.getPoiLng());
                                    parkInfo.setDistance(info.getDistance());
                                    parkInfo.setParkId((info.getPoiId() != null && !info.getPoiId().isEmpty()) ? Long.parseLong(info.getPoiId()) : 0);
                                    allParkList.add(parkInfo);
                                }
                            }
//                            initAdapter();`
//                            addOverlaysPoi(relultList);
                            addData();
                        } else {
                            ToastUtils.showShort(context, emptyPrompt);
                        }
                        initAdapter();
                        addOverlaysPoi(allList);
                    } else {
                        ErrorUtils.errorCode(context, code);
                    }
                } else {
                    ToastUtils.showShort(context, "服务器请求错误");
                }
            }

            //初始化适配器
            super.handleMessage(msg);
        }
    };


    private void addData() {
        for (ParkInfo info : allParkList) {
            if (info.getParkType() == 1) {//可支付
                payList.add(info);
            } else if (info.getParkType() == 2) {//可引导
                guideList.add(info);
            }
        }
    }

    private void initAdapter() {
        List<View> viewList = new ArrayList<View>();
        LayoutInflater inflater = getLayoutInflater();
        for (int i = 0, len = allList.size(); i < len; i++) {
            viewList.add(inflater.inflate(R.layout.park_info_item, null));
        }
        mAdapter = new ParkInfoPagerAdapter(ParkMapActivity.this, viewList, allList);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setPageMargin(30);
        mViewPager.setOffscreenPageLimit(3);
    }

    //搜索停车场停车场
    Handler handlerSearch = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                GetParkInfo parkInfo = (GetParkInfo) JsonUtils.jsonToObject(msg.obj.toString(), GetParkInfo.class);
                if (parkInfo != null) {
                    List<ParkInfo> searList = parkInfo.getContent();
                    searchList.clear();
                    searchList.addAll(searList);
                    mListView.setAdapter(searchAdapter);
                    searchAdapter.setKerWord(keyWord);
                    searchAdapter.notifyDataSetChanged();
                } else {
                    ToastUtils.showShort(context, "服务器请求错误");
                }
                layList.setVisibility(View.VISIBLE);
            }
            super.handleMessage(msg);
        }
    };

    public void locationSearch(ParkInfo info) {
        layTitle.setFocusable(true);
        layTitle.setFocusableInTouchMode(true);
        layTitle.requestFocus();
        isFirstIn = true;
//        locationPark(info);
        parkCollectInfo = info;
        getPoiDatas(info.getParkLng(),info.getParkLat());
        layList.setVisibility(View.GONE);
        isSearch = false;//定位后不需要重新加载
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case 0: //返回按钮返回
                break;
            case 1:  //定位
                ParkInfo info = (ParkInfo) data.getSerializableExtra("info");//得到新Activity返回的数据
                isScroll = true;
//                isSearch = false;//定位后不需要重新加载
                locationPark(info);
                break;
            case 2:  //语音搜索返回
                String content = data.getStringExtra("content");
                etParkName.setText(content);
                break;
        }
    }

    /**
     * 定位选中的停车场
     *
     * @param info
     */
    public void locationPark(ParkInfo info) {
        for (int i = 0, len = mOverlayList.size(); i < len; i++) {
            Marker marker = (Marker) mOverlayList.get(i);
            float lat1 = info.getParkLat();
            float lng1 = info.getParkLng();
            float lat2 = (float) marker.getPosition().latitude;
            float lng2 = (float) marker.getPosition().longitude;
            if (lat1 == lat2 && lng1 == lng2) { //坐标相同
//                marker.setIcon(bitMapParkCheck);
                String title = marker.getTitle();
                posi = Integer.parseInt(title) - 1;
                if (title.equals("1")) {//第一个特殊处理
                    mViewPager.setCurrentItem(1, false);
                    mViewPager.setCurrentItem(0, true);
                } else {
                    mViewPager.setCurrentItem(posi, true);
                }
                mViewPager.setVisibility(View.VISIBLE);
                layNav.setVisibility(View.GONE);
                isScroll = false; //从排序页面跳转过来滑动地图需要重新加载
                break;
            }
        }
    }

    /**
     * 距离升序排列
     *
     * @return
     */
//    private void SortListByDistance(List<ParkInfo> list) {
//        if (list == null) return;
//
//        Collections.sort(list, new Comparator<ParkInfo>() {
//            public int compare(ParkInfo arg1, ParkInfo arg2) {
//                int dist1 = arg1.getDistance();
//                int dist2 = arg2.getDistance();
//                if (dist1 > dist2) {
//                    return 1;
//                } else if (dist1 == dist2) {
//                    return 0;
//                } else {
//                    return -1;
//                }
//            }
//        });
//    }

    /**
     * 重置为默认
     */
    private void resetDefault() {
        SPUtils.put(this, "park", true);
        SPUtils.put(this, "elecStation", false);
        SPUtils.put(this, "gasStation", false);
        SPUtils.put(this, "all", false);

    }

    @Override
    protected void onStart() {
        super.onStart();
        // 开启方向传感器
        myOrientationListener.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        //退出时销毁定位
        mLocationClient.stop();
        myOrientationListener.stop();
//        mPoiSearch.destroy();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView = null;

        if (mIat != null) {
            mIat.cancel();
            mIat.destroy();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onStop() {
        VDeviceAPI.unsetNetworkChangedCallback();
        super.onStop();

        // 停止定位
        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();
        // 停止方向传感器
        myOrientationListener.stop();
    }


    public void routeplanToNavi(BNRoutePlanNode.CoordinateType coType, LatLng startPoint, LatLng endPoint) {
        BNRoutePlanNode sNode = null;
        BNRoutePlanNode eNode = null;
        double sLng = startPoint.longitude;
        double sLat = startPoint.latitude;
        double eLng = endPoint.longitude;
        double eLat = endPoint.latitude;
        switch (coType) {
            case BD09LL: {
                sNode = new BNRoutePlanNode(sLng, sLat, "", null, coType);
                eNode = new BNRoutePlanNode(eLng, eLat, "", null, coType);
                break;
            }
            default:
                ;
        }
        if (sNode != null && eNode != null) {
            List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
            list.add(sNode);
            list.add(eNode);
            BaiduNaviManager.getInstance().launchNavigator(this, list, 1, true, new DemoRoutePlanListener(sNode, this));
        }
    }

    /**
     * 听写UI监听器
     */
//    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
//        public void onResult(RecognizerResult results, boolean isLast) {
//            String res = VouceUtils.printResult(results);
//            if(isLast) {
//                etParkName.setText(res);
//            }
//        }
//
//        /**
//         * 识别回调错误.
//         */
//        public void onError(SpeechError error) {
//            String errStr = error.getPlainDescription(true);
//            String str = errStr.substring(0,errStr.indexOf("."));
//            ToastUtils.showShort(ParkMapActivity.this,str);
//        }
//    };

}
