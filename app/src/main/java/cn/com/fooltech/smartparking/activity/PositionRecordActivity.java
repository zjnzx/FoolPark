package cn.com.fooltech.smartparking.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.adapter.PositionRecordAdapter;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.baidumap.DemoRoutePlanListener;
import cn.com.fooltech.smartparking.bean.PositionRecordInfo;
import cn.com.fooltech.smartparking.bean.SpaceInfo;
import cn.com.fooltech.smartparking.common.Common;
import cn.com.fooltech.smartparking.common.Urls;
import cn.com.fooltech.smartparking.bean.jsonbean.GetParkingInfo;
import cn.com.fooltech.smartparking.bean.jsonbean.GetPositionRecord;
import cn.com.fooltech.smartparking.slidelistview.SwipeMenu;
import cn.com.fooltech.smartparking.slidelistview.SwipeMenuCreator;
import cn.com.fooltech.smartparking.slidelistview.SwipeMenuItem;
import cn.com.fooltech.smartparking.slidelistview.SwipeMenuListView;
import cn.com.fooltech.smartparking.utils.DateUtils;
import cn.com.fooltech.smartparking.utils.ErrorUtils;
import cn.com.fooltech.smartparking.utils.HttpUtils;
import cn.com.fooltech.smartparking.utils.JsonUtils;
import cn.com.fooltech.smartparking.utils.NetUtils;
import cn.com.fooltech.smartparking.utils.SPUtils;
import cn.com.fooltech.smartparking.utils.ToastUtils;
import cn.com.fooltech.smartparking.utils.Utils;
import cn.com.fooltech.smartparking.view.ListViewPlus;
import cn.com.fooltech.smartparking.view.WheelView;

public class PositionRecordActivity extends BaseActivity implements ListViewPlus.ListViewPlusListener{
    @Bind(R.id.lv_space)
    SwipeMenuListView mListView;
    @Bind(R.id.empty_record)
    ImageView ivEmpty;
    private Context context = PositionRecordActivity.this;
    private List<PositionRecordInfo> resultList = new ArrayList<PositionRecordInfo>();
    private List<PositionRecordInfo> result;
    private String floor,space;
    private Dialog dialog;
    private List<String> floorList = new ArrayList<String>();
    private List<String> spaceList = new ArrayList<String>();
    private List<List<SpaceInfo>> list = new ArrayList<List<SpaceInfo>>();//停车场所有的车位
    private int selectFloor = 0; //选中楼层的下标
    private int selectSpace = 0;//选中车位的下标
    private WheelView wheelFloor,wheelSpace;
    private Button btnSure;
    private EditText etSpace;
    private boolean isFirst = true;
    private boolean isFirstLoad;
    private int index = 0, count = 10;
    private int flag = ListViewPlus.REFRESH;//0:刷新  1:加载
    private PositionRecordAdapter mAdapter;
    private int posi;
    private LatLng startPoint, endPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position_record);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        isFirstLoad = true;
        mListView.setRefreshEnable(true);
        mListView.setLoadEnable(true);
        mListView.setAutoLoadEnable(true);
        mListView.setListViewPlusListener(this);

        initDeleteBtn();
        initAdapter();
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.back_record:
                finish();
                break;
            case R.id.position_add:
                isFirst = true;
                selectFloor = 0;
                getParkInfo();
                break;
        }
    }

    /**
     * 设置删除按钮,事件
     */
    private void initDeleteBtn(){
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                createMenu2(menu);
            }
            private void createMenu2(SwipeMenu menu) {
                SwipeMenuItem openItem = new SwipeMenuItem(context.
                        getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F,
                        0x25)));
                // set item width
                openItem.setWidth(Common.dp2px(70, context));
                // set item title
                openItem.setTitle("删除");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);
            }
        };
        mListView.setMenuCreator(creator);
        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                PositionRecordInfo info = mAdapter.list.get(position);
                deleteRecord(info.getPositionId());
                posi = position;
                return false;
            }
        });
    }

    /**
     * 删除记录
     * @param positionId
     */
    private void deleteRecord(long positionId){
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("userId", SPUtils.get(this, "userId", new Long(0l)));
        paramMap.put("token", SPUtils.get(this, "token", ""));
        paramMap.put("positionId", positionId);
        HttpUtils.sendHttpPostRequest(Urls.URL_REMOVE_POSITION_RECORD, handlerDel, paramMap,this);
    }

    Handler handlerDel = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            }else {
                int code = JsonUtils.getCodeResult(msg.obj.toString());
                if (code == 0) {
                    mAdapter.removeData(posi);
                    if(mAdapter.list.size() == 0){
                        ivEmpty.setVisibility(View.VISIBLE);
                    }
                } else {
                    ErrorUtils.errorCode(context, code);
                }
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 获取车位记录
     * @param flag
     */
    private void getPositionRecord(int flag) {
        this.flag = flag;
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userId", SPUtils.get(this, "userId", new Long(0l)));
        paramMap.put("token", SPUtils.get(this, "token", ""));
        paramMap.put("lng", MyApplication.longtitude);
        paramMap.put("lat", MyApplication.latitude);
        paramMap.put("from", index);
        paramMap.put("recnum", count);
        HttpUtils.sendHttpPostRequest(Urls.URL_GET_POSITION_RECORD, handlerRecord, paramMap, this);
    }

    Handler handlerRecord = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                GetPositionRecord record = (GetPositionRecord) JsonUtils.jsonToObject(msg.obj.toString(), GetPositionRecord.class);
                if (record != null) {
                    int code = record.getCode();
                    if (code == 0) {
                        result = record.getContent();
//                        result.add(new PositionRecordInfo(0,"闵行停车场","闵行区",1000,"1","112"));
//                        result.add(new PositionRecordInfo(1,"浦东停车场","闵行区",2000,"2","234"));
//                        result.add(new PositionRecordInfo(2,"宝山停车场","闵行区",3000,"4","145"));
                        notifyData();
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

    private void initAdapter() {
        mAdapter = new PositionRecordAdapter(this, resultList);
        mListView.setAdapter(mAdapter);
    }

    private void notifyData() {
        if (flag == ListViewPlus.REFRESH) {
            resultList.clear();
            if(result != null) {
                resultList.addAll(result);
            }
        } else if (flag == ListViewPlus.LOAD) {
            if(result != null) {
                resultList.addAll(result);
            }
        }
        onLoadComplete();
        mAdapter.notifyDataSetChanged();
        if (resultList.size() == 0) {
            ivEmpty.setVisibility(View.VISIBLE);
        } else {
            ivEmpty.setVisibility(View.GONE);
        }
    }

    private void onLoadComplete() {
        mListView.stopRefresh();
        mListView.stopLoadMore();
        mListView.setRefreshTime(DateUtils.getDate());
    }

    /**
     * 获取停车场车位
     */
    public void getParkInfo() {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userId", SPUtils.get(context, "userId", new Long(0l)));
        paramMap.put("token", SPUtils.get(context, "token", ""));
        paramMap.put("lng",MyApplication.longtitude );
        paramMap.put("lat", MyApplication.latitude);
        paramMap.put("maxNum", 10);
        paramMap.put("plateNumber", MyApplication.currentCar.getPlateNumber());

        HttpUtils.sendHttpPostRequest(Urls.URL_GET_PARK_INFO, handlerParkInfo, paramMap, this);
    }

    Handler handlerParkInfo = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                GetParkingInfo jsonBean = (GetParkingInfo) JsonUtils.jsonToObject(msg.obj.toString(), GetParkingInfo.class);
                if (jsonBean != null) {
                    int code = jsonBean.getCode();
                    if (code == 0) {
                        floorList.clear();
                        floorList = jsonBean.getContent().getFloorList();
                        list = jsonBean.getContent().getSpaceList();
                        if (isFirst) {
                            showDialogSpace();
                        }
                    }else if(code == 40005){
                        ToastUtils.showShort(context,"无法找到匹配的停车记录信息");
                    } else {
                        ErrorUtils.errorCode(context, code);
                    }

//                    floorList.clear();
//                    floorList.add("1");
//                    floorList.add("2");
//                    floorList.add("3");
//                    for (int i = 0; i < 3;i++){
//                        List<SpaceInfo> infoList = new ArrayList<SpaceInfo>();
//                        infoList.add(new SpaceInfo(i,floorList.get(i) + i + 0));
//                        infoList.add(new SpaceInfo(i,floorList.get(i) + i + 1));
//                        list.add(infoList);
//                    }
//                    if (isFirst) {
//                        showDialogSpace();
//                    }
                } else {
                    ToastUtils.showShort(context, "服务器请求错误");
                }
            }
            super.handleMessage(msg);
        }
    };


    /**
     * 添加停车位
     */
    private void addPosition(String floor,String space){
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("userId", SPUtils.get(this, "userId", new Long(0l)));
        paramMap.put("token", SPUtils.get(this, "token", ""));
        paramMap.put("plateNumber",MyApplication.currentCar.getPlateNumber());
        paramMap.put("spaceFloor", floor);
        paramMap.put("spaceLabel", space);
        HttpUtils.sendHttpPostRequest(Urls.URL_ADD_POSITION, handlerAdd, paramMap, this);
    }

    Handler handlerAdd = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            }else {
                int code = JsonUtils.getCodeResult(msg.obj.toString());
                if (code == 0) {
                    ToastUtils.showShort(context, "添加成功");
                    dialog.cancel();
                    index = 0;
                    getPositionRecord(ListViewPlus.REFRESH);
                } else {
                    ErrorUtils.errorCode(context, code);
                }
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 设置默认显示的楼层
     * @param size
     */
//    private void setSelectionFloor(int size){
//        if(size % 2 == 0){
//            selectFloor = size / 2 - 1;
//        }else{
//            selectFloor = size / 2;
//        }
//    }
//    /**
//     * 设置默认显示的车位
//     * @param size
//     */
//    private void setSelectionSpace(int size){
//        if(size % 2 == 0){
//            selectSpace = size / 2 - 1;
//        }else{
//            selectSpace = size / 2;
//        }
//    }

    /**
     * dialog框
     */
    private void showDialogSpace(){
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout layout = (RelativeLayout)inflater.inflate(R.layout.dialog_space_select, null);
        ImageView ivClose = (ImageView) layout.findViewById(R.id.close_x1);
        etSpace = (EditText) layout.findViewById(R.id.space_select);
        btnSure = (Button) layout.findViewById(R.id.sure_space);
        wheelFloor = (WheelView) layout.findViewById(R.id.wheel_floor);
        wheelSpace = (WheelView) layout.findViewById(R.id.wheel_space);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(layout);
        dialog = builder.create();
        dialog.show();

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        wheelFloor.setOffset(2);
        wheelFloor.setItems(floorList);

        wheelSpace.setOffset(2);

        setSpaceList(0);
    }

    private void setData(){
        wheelSpace.clearItem();
        wheelSpace.setItems(spaceList);

        if(floorList.size() > 0) {
            wheelFloor.setSelection(selectFloor);
            floor = floorList.get(selectFloor);
        }
        if(spaceList.size() > 0) {
            wheelSpace.setSelection(selectSpace);
            space = spaceList.get(selectSpace);
        }else{
            space = "";
        }

        if(isFirst){
            String floor1 = "";
            String space2 = "";
            if(floorList.size() > 0){
                floor1 = floorList.get(selectFloor);
            }
            if(spaceList.size() > 0){
                space2 = spaceList.get(selectSpace);
            }
            etSpace.setHint("例: " + floor1 + "  " + space2);
        }else {
            etSpace.setText(floor + "  " + space);
        }
        isFirst = false;


        //楼层
        wheelFloor.setOnWheelPickerListener(new WheelView.OnWheelPickerListener() {
            @Override
            public void wheelSelect(int position, String content) {
//                Log.i("TAG", position + "+" + content);
                floor = content;
                selectFloor = position;
                spaceList.clear();
                setSpaceList(position);
                selectFloor = Integer.parseInt(content);
            }
        });
        //车位
        wheelSpace.setOnWheelPickerListener(new WheelView.OnWheelPickerListener() {
            @Override
            public void wheelSelect(int position, String content) {
//                Log.i("TAG", position + "+" + content);
                space = content;
                etSpace.setText(floor + "  " + space);
            }
        });

        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPosition(floor,space);
            }
        });
    }

    private void setSpaceList(int position){
        spaceList.clear();
        if(list.size() > 0) {
            for (int j = 0, len = list.get(position).size(); j < len; j++) {
                spaceList.add(list.get(position).get(j).getSpaceLabel());
            }
        }
        setData();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && isFirstLoad) {
            mListView.autoRefresh();
            isFirstLoad = false;
        }
    }

    @Override
    public void onRefresh() {
        index = 0;
        getPositionRecord(ListViewPlus.REFRESH);
    }

    @Override
    public void onLoadMore() {
        index += count;
        getPositionRecord(ListViewPlus.LOAD);
    }

    public void startNavi(PositionRecordInfo info) {
        if (!Common.isLogin(context)) return;
        if (!NetUtils.isConn(context)) return;
        startPoint = new LatLng(MyApplication.latitude, MyApplication.longtitude);
        endPoint = new LatLng(info.getSpaceLat(), info.getSpaceLng());
        if (BaiduNaviManager.isNaviInited()) {
            Common.getDriver(context, handlerGetDriver);
        }
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
