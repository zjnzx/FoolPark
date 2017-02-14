package cn.com.fooltech.smartparking.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.baidu.mapapi.model.LatLng;
import com.baidu.navisdk.adapter.BNOuterLogUtil;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;
import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.baidumap.DemoRoutePlanListener;
import cn.com.fooltech.smartparking.baidumap.Navigation;
import cn.com.fooltech.smartparking.bean.ParkInfo;
import cn.com.fooltech.smartparking.common.Common;
import cn.com.fooltech.smartparking.fragment.BookOrCollectFragment;
import cn.com.fooltech.smartparking.utils.ErrorUtils;
import cn.com.fooltech.smartparking.utils.JsonUtils;
import cn.com.fooltech.smartparking.utils.NetUtils;
import cn.com.fooltech.smartparking.utils.SPUtils;
import cn.com.fooltech.smartparking.utils.ToastUtils;
import cn.com.fooltech.smartparking.utils.Utils;
import cn.com.fooltech.smartparking.view.ListViewPlus;


public class BookOrCollectActivity extends BaseActivity {
    @Bind(R.id.back_book)
    ImageView ivBack;
    @Bind(R.id.book)
    RadioButton rbBook;
    @Bind(R.id.collect)
    RadioButton rbCollect;
    @Bind(R.id.rg_book)
    RadioGroup radioGroup;
    @BindColor( R.color.green )
    int green;
    @BindColor( R.color.white )
    int white;
    private Context context = BookOrCollectActivity.this;
    private String mSDCardPath = null;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private BookOrCollectFragment[] fragments;
    private LatLng startPoint, endPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_or_collect);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        fragmentManager = getFragmentManager();
        fragments = new BookOrCollectFragment[2];

        setSelection(rbBook, 0, 1);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                if (id == R.id.book) { //预约
                    setSelection(rbBook, 0, 1);
                } else if (id == R.id.collect) { //收藏
                    setSelection(rbCollect, 1, 2);
                }
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setSelection(RadioButton rb, int index, int param) {
        transaction = fragmentManager.beginTransaction();
        hideFragment();
        clearState();
        rb.setTextColor(white);
        if (fragments[index] == null) {
            fragments[index] = BookOrCollectFragment.newInstance(param);
            transaction.add(R.id.lay_content_book, fragments[index]);
        } else {
            transaction.show(fragments[index]);
            fragments[index].setIndex();
            fragments[index].getRecordList(ListViewPlus.REFRESH);
        }
        transaction.commit();
    }

    private void hideFragment() {
        if (fragments[0] != null) {
            transaction.hide(fragments[0]);
        }
        if (fragments[1] != null) {
            transaction.hide(fragments[1]);
        }
    }

    private void clearState() {
        rbBook.setTextColor(green);
        rbCollect.setTextColor(green);
    }

    public void startNavi(ParkInfo info) {
        if (!Common.isLogin(context)) return;
        if (!NetUtils.isConn(context)) return;
        startPoint = new LatLng(MyApplication.latitude, MyApplication.longtitude);
        endPoint = new LatLng(info.getParkLat(), info.getParkLng());
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
