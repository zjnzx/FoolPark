package cn.com.fooltech.smartparking.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.adapter.ParkPriorityAdapter;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.baidumap.DemoRoutePlanListener;
import cn.com.fooltech.smartparking.bean.ParkInfo;
import cn.com.fooltech.smartparking.common.Common;
import cn.com.fooltech.smartparking.utils.ErrorUtils;
import cn.com.fooltech.smartparking.utils.JsonUtils;
import cn.com.fooltech.smartparking.utils.NetUtils;
import cn.com.fooltech.smartparking.utils.SPUtils;
import cn.com.fooltech.smartparking.utils.ToastUtils;
import cn.com.fooltech.smartparking.utils.Utils;


public class ParkPriorityActivity extends BaseActivity {
    @Bind(R.id.back_park)
    ImageView ivBack;
    @Bind(R.id.lv_park)
    ListView mListView;
    @Bind(R.id.empty_park)
    TextView tvEmpty;
    private Context context = ParkPriorityActivity.this;
    private ArrayList<ParkInfo> distList;
    private ArrayList<ParkInfo> priceList;
    private LatLng startPoint, endPoint;
    private  ArrayList<ParkInfo> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_prio);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        //获取传递的数据
        Intent intent = getIntent();
//        list = new ArrayList<ParkInfo>();
        ArrayList<ParkInfo> dataList = (ArrayList<ParkInfo>) intent.getSerializableExtra("list");

        distList = getDistSortList(dataList);

        initAdapter();
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }

    /**
     * 距离升序排列
     *
     * @return
     */
    private ArrayList<ParkInfo> getDistSortList(ArrayList<ParkInfo> list) {
        if (list == null) return list;

        Collections.sort(list, new Comparator<ParkInfo>() {
            public int compare(ParkInfo arg1, ParkInfo arg2) {
                int dist1 = arg1.getDistance();
                int dist2 = arg2.getDistance();
                if (dist1 > dist2) {
                    return 1;
                } else if (dist1 == dist2) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });
        return list;
    }

    private void initAdapter(){
        ParkPriorityAdapter mAdapter = new ParkPriorityAdapter((cn.com.fooltech.smartparking.activity.ParkPriorityActivity) this,distList);
        mListView.setAdapter(mAdapter);
        mListView.setEmptyView(tvEmpty);
    }

//    class sortDist implements Comparator<ParkInfo>{
//        @Override
//        //实现Comparator的compare方法
//        public int compare(ParkInfo arg1, ParkInfo arg2) {
//            // TODO Auto-generated method stub
//            return arg1.getDistance() - arg2.getDistance();
//        }
//
//    }


    /**
     * 启动百度地图导航(Native)
     */
    public void startNavi(ParkInfo info) {
        if (!Common.isLogin(context)) return;
        if (!NetUtils.isConn(context)) return;
        startPoint = new LatLng(MyApplication.latitude, MyApplication.longtitude);
        endPoint = new LatLng(info.getParkLat(), info.getParkLng());
        if (BaiduNaviManager.isNaviInited()) {
            Common.getDriver(context, handlerGetDriver);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mIntent = new Intent();
        // 设置结果，并进行传送
        this.setResult(0, mIntent);
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

    Handler handlerGetDriver = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                Map<String, Object> resultMap = JsonUtils.jsonToMap(msg.obj.toString());
                if (resultMap != null && resultMap.size() > 0) {
                    int code = (int) resultMap.get("code");
                    if (code == 0) {
                        JSONObject object = (JSONObject) resultMap.get("content");
                        try {
                            long droverId = object.getLong("driverId");
                            isDriver(droverId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
     * 判断用户是否当前车辆的driver,如果不是就设置为driver
     */
    private void isDriver(long driverId) {
        long userId = (long) SPUtils.get(context, "userId", new Long(0l));
        if (driverId == userId) {//用户是当前车辆的driver
            routeplanToNavi(BNRoutePlanNode.CoordinateType.BD09LL, startPoint, endPoint);
        } else {//不是,设置成driver
            Common.setDriver(context, handlerSetDriver);
        }
    }

    Handler handlerSetDriver = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                int code = JsonUtils.getCodeResult(msg.obj.toString());
                if (code == 0) {
                    routeplanToNavi(BNRoutePlanNode.CoordinateType.BD09LL, startPoint, endPoint);
                } else {
                    ErrorUtils.errorCode(context, code);
                }
            }
            super.handleMessage(msg);
        }
    };

}
