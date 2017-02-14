package cn.com.fooltech.smartparking.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.vi.VDeviceAPI;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.baidumap.DrivingRouteOverlay;
import cn.com.fooltech.smartparking.baidumap.MyOrientationListener;
import cn.com.fooltech.smartparking.bean.ParkInfo;
import cn.com.fooltech.smartparking.utils.SPUtils;
import cn.com.fooltech.smartparking.utils.ToastUtils;


public class CarSearchActivity extends Activity {
    @Bind(R.id.back_routeline)
    ImageView ivBack;
    @Bind(R.id.routline_map)
    MapView mMapView;
    private Context context = CarSearchActivity.this;
    private BaiduMap mBaiduMap;
    private float lat, lng;
    private ParkInfo parkInfo;
    private RoutePlanSearch mSearch;
    private RouteLine route = null;
    DrivingRouteResult nowResultd = null;
    private LocationClient mLocationClient = null;
    private LocationMode mLocationMode;
    private boolean isFirstLoc = true;// 是否首次定位
    private MyOrientationListener myOrientationListener;
    public MyLocationListener mLocationListener;
    private BitmapDescriptor mIconLocation;// 自定义定位图标
    private float mCurrentX;
    private float mLatitude;
    private float mLongtitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_car_search);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        mBaiduMap = mMapView.getMap();

        //获取传递的数据
        Intent intent = getIntent();
        parkInfo = (ParkInfo) intent.getSerializableExtra("parkInfo");

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        initLocation();

    }

    /**
     * 初始化定位
     */
    private void initLocation() {
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(16.0f);//设置比例尺
        mBaiduMap.animateMapStatus(msu);
        mLocationMode = LocationMode.NORMAL;
        mIconLocation = BitmapDescriptorFactory.fromResource(R.drawable.location);// 初始化图标
        mLocationClient = new LocationClient(this);
        mLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mLocationListener);
        this.setLocationOption();   //设置定位参数

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
     * 设置定位参数
     */
    private void setLocationOption() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开GPS
        option.setCoorType("bd09ll"); // 返回的定位结果是百度经纬度,默认值gcj02
        option.setScanSpan(3000); // 设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true); // 返回的定位结果包含地址信息
        mLocationClient.setLocOption(option);
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
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);

            // 设置自定义图标
            MyLocationConfiguration config = new MyLocationConfiguration(
                    mLocationMode, true, mIconLocation);
            mBaiduMap.setMyLocationConfigeration(config);
            // 更新经纬度
            mLatitude = (float) location.getLatitude();
            mLongtitude = (float) location.getLongitude();

            if (isFirstLoc) {
                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(msu);
                isFirstLoc = false;

                routePlan();
            }
        }
    }

    /**
     * 路线规划
     */
    public void routePlan() {
        //创建驾车线路规划检索实例；
        mSearch = RoutePlanSearch.newInstance();
        //创建驾车线路规划检索监听者；
        OnGetRoutePlanResultListener listener = new OnGetRoutePlanResultListener() {
            public void onGetWalkingRouteResult(WalkingRouteResult result) {
                //获取步行线路规划结果
            }

            public void onGetTransitRouteResult(TransitRouteResult result) {
                //获取公交换乘路径规划结果
            }

            public void onGetDrivingRouteResult(DrivingRouteResult result) {
                //获取驾车线路规划结果
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    ToastUtils.showShort(context, "抱歉，未找到结果");
                }
                if (result.error == SearchResult.ERRORNO.NO_ERROR) {
//                    nodeIndex = -1;
                    if (result.getRouteLines().size() > 1) {
                        nowResultd = result;

//                        MyTransitDlg myTransitDlg = new MyTransitDlg(context,
//                                result.getRouteLines(),
//                                RouteLineAdapter.Type.DRIVING_ROUTE);
//                        myTransitDlg.setOnItemInDlgClickLinster(new OnItemInDlgClickListener() {
//                            public void onItemClick(int position) {
//                                route = nowResultd.getRouteLines().get(position);
//                                DrivingRouteOverlay overlay = new DrivingRouteOverlay(mBaiduMap);
//                                mBaiduMap.setOnMarkerClickListener(overlay);
////                                routeOverlay = overlay;
//                                overlay.setData(nowResultd.getRouteLines().get(position));
//                                overlay.addToMap();
//                                overlay.zoomToSpan();
//                            }
//
//                        });
//                        myTransitDlg.show();

                    } else if (result.getRouteLines().size() == 1) {
                        route = result.getRouteLines().get(0);
                        DrivingRouteOverlay overlay = new DrivingRouteOverlay(mBaiduMap);
//                        routeOverlay = overlay;
                        mBaiduMap.setOnMarkerClickListener(overlay);
                        overlay.setData(result.getRouteLines().get(0));
                        overlay.addToMap();
                        overlay.zoomToSpan();
                    }

                }
            }

            @Override
            public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

            }
        };
        LatLng stLatLng = new LatLng(mLatitude, mLongtitude);
        LatLng enLatLng = new LatLng(parkInfo.getParkLat(), parkInfo.getParkLng());
        //设置驾车线路规划检索监听者；
        mSearch.setOnGetRoutePlanResultListener(listener);
        //准备检索起、终点信息；
        PlanNode stNode = PlanNode.withLocation(stLatLng);
        PlanNode enNode = PlanNode.withLocation(enLatLng);
        //发起驾车线路规划检索；
        mSearch.drivingSearch((new DrivingRoutePlanOption()).from(stNode).to(enNode));

    }

    // 响应DLg中的List item 点击
//    interface OnItemInDlgClickListener {
//        public void onItemClick(int position);
//    }
    // 供路线选择的Dialog
//    class MyTransitDlg extends Dialog {
//
//        private List<? extends RouteLine> mtransitRouteLines;
//        private ListView transitRouteList;
//        private RouteLineAdapter mTransitAdapter;
//
//        OnItemInDlgClickListener onItemInDlgClickListener;
//
//        public MyTransitDlg(Context context, int theme) {
//            super(context, theme);
//        }
//
//        public MyTransitDlg(Context context, List< ? extends RouteLine> transitRouteLines, RouteLineAdapter.Type
//                type) {
//            this( context, 0);
//            mtransitRouteLines = transitRouteLines;
//            mTransitAdapter = new RouteLineAdapter( context, mtransitRouteLines , type);
//            requestWindowFeature(Window.FEATURE_NO_TITLE);
//        }
//
//        @Override
//        protected void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            setContentView(R.layout.activity_transit_dialog);
//
//            transitRouteList = (ListView) findViewById(R.id.transitList);
//            transitRouteList.setAdapter(mTransitAdapter);
//
//            transitRouteList.setOnItemClickListener( new AdapterView.OnItemClickListener() {
//
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    onItemInDlgClickListener.onItemClick( position);
//                    dismiss();
//
//                }
//            });
//        }
//
//        public void setOnItemInDlgClickLinster( OnItemInDlgClickListener itemListener) {
//            onItemInDlgClickListener = itemListener;
//        }
//    }


    @Override
    protected void onStart() {
        super.onStart();
        // 开启定位
        mBaiduMap.setMyLocationEnabled(true);
        if (!mLocationClient.isStarted())
            mLocationClient.start();
        // 开启方向传感器
        myOrientationListener.start();

        //设置为false,显示默认图层
        SPUtils.put(this, "park", false);
        SPUtils.put(this, "eye", false);
        SPUtils.put(this, "gasStation", false);
        SPUtils.put(this, "traffic", false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        //退出时销毁定位
        mLocationClient.stop();
        myOrientationListener.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView = null;

        if (mSearch != null)
            mSearch.destroy();
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
}
