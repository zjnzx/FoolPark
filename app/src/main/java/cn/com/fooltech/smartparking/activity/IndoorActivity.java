package cn.com.fooltech.smartparking.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapBaseIndoorMapInfo;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.bean.ParkInfo;
import cn.com.fooltech.smartparking.bean.SpaceInfo;
import cn.com.fooltech.smartparking.common.Urls;
import cn.com.fooltech.smartparking.bean.jsonbean.GetSpaceStatus;
import cn.com.fooltech.smartparking.utils.ErrorUtils;
import cn.com.fooltech.smartparking.utils.HttpUtils;
import cn.com.fooltech.smartparking.utils.JsonUtils;
import cn.com.fooltech.smartparking.utils.ToastUtils;
import cn.com.fooltech.smartparking.utils.Utils;


public class IndoorActivity extends BaseActivity {
    @Bind(R.id.bmapView_indoor)
    MapView mMapView;
    private Context context = IndoorActivity.this;
    private BaiduMap mBaiduMap;
    private ParkInfo parkInfo;
    private BitmapDescriptor mMarkerFree, mMarkerOrder, mMarkerCar;
    private List<Overlay> mOverlayList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indoor);
        ButterKnife.bind(this);

        initView();
        initIndoorMap();
        initMarker();
    }

    private void initView() {
        //获取传递的数据
        Intent intent = getIntent();
        parkInfo = (ParkInfo) intent.getSerializableExtra("parkInfo");

        mBaiduMap = mMapView.getMap();

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("parkId", 3510);
        HttpUtils.sendHttpPostRequest(Urls.URL_SPACE_STATUS, mHandler, paramMap, this);
    }

    /**
     * 室内地图
     */
    private void initIndoorMap() {
//        LatLng centerpos = new LatLng(parkInfo.getParkLat(), parkInfo.getParkLng());
//        MapStatus.Builder builder = new MapStatus.Builder();
//        builder.target(centerpos).zoom(22.0f);
//        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(22.0f);//设置比例尺
        mBaiduMap.animateMapStatus(msu);
        mBaiduMap.setIndoorEnable(true);

        mBaiduMap.setOnBaseIndoorMapListener(new BaiduMap.OnBaseIndoorMapListener() {
            @Override
            public void onBaseIndoorMapMode(boolean b, MapBaseIndoorMapInfo mapBaseIndoorMapInfo) {
                if (b) {
                    // 进入室内图
                    // 通过获取回调参数 mapBaseIndoorMapInfo 来获取室内图信息，包含楼层信息，室内ID等
                } else {
                    // 移除室内图
                }
            }
        });
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                GetSpaceStatus spaceStatus = (GetSpaceStatus) JsonUtils.jsonToObject(msg.obj.toString(), GetSpaceStatus.class);
                if (spaceStatus != null) {
                    int code = spaceStatus.getCode();
                    List<SpaceInfo> spaceList = null;
                    if (code == 0) {
                        spaceList = spaceStatus.getContent();
//                    locationToLocation(spaceList.get(1).getSpaceLat(), spaceList.get(1).getSpaceLng());

                        addOverlays(spaceList);
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

    /**
     * 覆盖物图标
     */
    private void initMarker() {
        mMarkerFree = BitmapDescriptorFactory.fromResource(R.drawable.space_free);
        mMarkerCar = BitmapDescriptorFactory.fromResource(R.drawable.space_car);
    }

    private void addOverlays(List<SpaceInfo> spaceList) {
        mBaiduMap.clear();
        mOverlayList = new ArrayList<Overlay>();
        LatLng latLng = null;
        Marker marker = null;
        MarkerOptions options = null;
        for (SpaceInfo info : spaceList) {
            latLng = new LatLng(info.getSpaceLat(), info.getSpaceLng());// 经纬度
            int spaceStatus = info.getSpaceStatus();
            if (spaceStatus == 0) { //空闲
                options = new MarkerOptions().position(latLng).icon(mMarkerFree).zIndex(5);// 图标
            } else if (spaceStatus == 1) {//已有车辆
                options = new MarkerOptions().position(latLng).icon(mMarkerCar).zIndex(5);// 图标
            }
            options.animateType(MarkerOptions.MarkerAnimateType.grow);//添加地上生长动画
            marker = (Marker) mBaiduMap.addOverlay(options);
            mOverlayList.add(marker);
        }

        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.setMapStatus(msu);
    }

    /**
     * 根据经纬度定位
     */
    private void locationToLocation(double lat, double lng) {
        LatLng latLng = new LatLng(lat, lng);
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.animateMapStatus(msu);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 停止定位
        mBaiduMap.setMyLocationEnabled(false);

    }
}