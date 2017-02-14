package cn.com.fooltech.smartparking.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
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
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.adapter.BannerPagerParkAdapter;
import cn.com.fooltech.smartparking.adapter.CommentWholeAdapter;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.baidumap.DemoRoutePlanListener;
import cn.com.fooltech.smartparking.bean.BannerInfo;
import cn.com.fooltech.smartparking.bean.CommentInfo;
import cn.com.fooltech.smartparking.bean.ParkInfo;
import cn.com.fooltech.smartparking.bean.jsonbean.GetComment;
import cn.com.fooltech.smartparking.bean.jsonbean.GetParkInfoDetail;
import cn.com.fooltech.smartparking.common.Common;
import cn.com.fooltech.smartparking.common.Urls;
import cn.com.fooltech.smartparking.dao.BannerDao;
import cn.com.fooltech.smartparking.utils.ErrorUtils;
import cn.com.fooltech.smartparking.utils.HttpUtils;
import cn.com.fooltech.smartparking.utils.JsonUtils;
import cn.com.fooltech.smartparking.utils.NetUtils;
import cn.com.fooltech.smartparking.utils.SPUtils;
import cn.com.fooltech.smartparking.utils.ToastUtils;
import cn.com.fooltech.smartparking.utils.Utils;


public class ParkDetailActivity extends BaseActivity {
    @Bind(R.id.vp_pic)
    ViewPager mViewPager;
    @Bind(R.id.lay_dot)
    LinearLayout viewGroup;
    @Bind(R.id.name)
    TextView tvName;
    @Bind(R.id.park_status)
    TextView tvStatus;
    @Bind(R.id.distance2)
    TextView tvDistance;
    @Bind(R.id.addr)
    TextView tvAddr;
    @Bind(R.id.total)
    TextView tvTotal;
    @Bind(R.id.time)
    TextView tvTime;
    @Bind(R.id.price2)
    TextView tvPrice;
    @Bind(R.id.tel)
    TextView tvTel;
    @Bind(R.id.describe)
    TextView tvDesc;
    @Bind(R.id.lv_comment)
    ListView mListView;
    @Bind(R.id.comment_grade2)
    TextView tvGrade;
    @Bind(R.id.scrolview)
    ScrollView scrollView;
    private Context context = ParkDetailActivity.this;
    private ImageView dots[] = null;
    private String[] imageUrls;
    private static final int UPTATE_VIEWPAGER = 0;
    private Timer mTimer = new Timer();
    private TimerTask mTimerTask = null;
    private int currentItem;
    private int len;
    private List<BannerInfo> bannerList = new ArrayList<BannerInfo>();
    private ParkInfo parkInfo = new ParkInfo();
    private ParkInfo park;
    private static int delay = 5000;
    private static int period = 5000;
    private List<BannerInfo> list = new ArrayList<BannerInfo>();
    private BannerPagerParkAdapter mAdapter;
    private CommentWholeAdapter mAdapterComment;
    private LatLng startPoint, endPoint;
    private BannerDao bannerDao;
    private static final int BANNERTYPE = 2;
    private boolean isConnect = true;
    private int index = 0, count = 1;
    private List<CommentInfo> resultList = new ArrayList<CommentInfo>();
    private List<CommentInfo> result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_detail);
        ButterKnife.bind(this);

        park = (ParkInfo) getIntent().getSerializableExtra("parkInfo");
        initView();
        getParkData();
    }

    private void initView() {
        bannerDao = new BannerDao(this);
        scrollView.smoothScrollTo(0,0);

        initAdapter();
        getCommentList();

        //轮播图
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Common.setTipBackground(position % len, dots);
                currentItem = position + 1;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    private void getParkData() {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("parkId", park.getParkId());

        HttpUtils.sendHttpPostRequest(Urls.URL_PARK_INFO, handler, paramMap, this);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_NET) {
                List<BannerInfo> infoList = bannerDao.queryBannerInfo(BANNERTYPE);//查询本地数据 1:首页banner 2:停车场banner
                isConnect = false;
                list.addAll(infoList);
                len = infoList.size();
                notifyData();
            } else if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                GetParkInfoDetail parkInfoDetail = (GetParkInfoDetail) JsonUtils.jsonToObject(msg.obj.toString(), GetParkInfoDetail.class);
                if (parkInfoDetail != null) {
                    int code = parkInfoDetail.getCode();
                    if (code == 0) {
                        parkInfo = parkInfoDetail.getContent();
                        String images[] = parkInfo.getEntryImages();
                        len = images.length;
                        imageUrls = new String[len];
                        imageUrls = images;
                        notifyData();

                        tvName.setText(parkInfo.getParkName());
                        tvAddr.setText(parkInfo.getParkAddress());
                        tvTotal.setText(parkInfo.getTotalSpace() + "个车位");
                        int distance = (int) Common.GetLongDistance(MyApplication.longtitude,MyApplication.latitude,park.getParkLng(),park.getParkLat());
                        tvDistance.setText(distance > 1000 ? Utils.decimalFormat1((double)distance / 1000) + "km" : distance + "m");
//                        tvTime.setText(parkInfo.getBeginTime() + " - " + parkInfo.getCloseTime());
                        tvPrice.setText(parkInfo.getParkPrice());
                        tvTel.setText(parkInfo.getParkTel());
                        tvDesc.setText(parkInfo.getParkRemark());
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

    private void notifyData() {
        dots = new ImageView[len];
        Common.setViewGroup(context, dots, viewGroup);
        if (isConnect) {
            for (int i = 0; i < len; i++) {
                list.add(new BannerInfo(100 + i, imageUrls[i]));
            }
            bannerDao.deleteByType(BANNERTYPE);
            bannerDao.insertBannerInfo(list, BANNERTYPE);
        }
        bannerList.addAll(list);
        mAdapter.notifyDataSetChanged();

        Common.setTipBackground(0, dots);
        mViewPager.setCurrentItem(len * 100);
        timing();
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPTATE_VIEWPAGER:
                    mViewPager.setCurrentItem(msg.arg1);
                    break;
            }
        }
    };

    //定时轮播图片，
    private void timing() {
        // 设置自动轮播图片，5s后执行，周期是5s
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = UPTATE_VIEWPAGER;
                message.arg1 = currentItem;
                mHandler.sendMessage(message);
            }
        }, delay, period);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        startTimer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopTimer();
    }

    /**
     * 重新开启定时器
     */
    private void startTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = UPTATE_VIEWPAGER;
                    message.arg1 = currentItem;
                    mHandler.sendMessage(message);
                }
            };
        }
        if (mTimer != null && mTimerTask != null)
            mTimer.schedule(mTimerTask, delay, period);
    }

    /**
     * 停止定时器
     */
    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            //返回
            case R.id.back_park_info:
                finish();
                break;
            //导航
            case R.id.distance2:
                if (!Common.isLogin(context)) return;
                if (!NetUtils.isConn(context)) return;
                startPoint = new LatLng(MyApplication.latitude, MyApplication.longtitude);
                endPoint = new LatLng(park.getParkLat(), park.getParkLng());
                if (BaiduNaviManager.isNaviInited()) {
                    Common.getDriver(context, handlerGetDriver);
                }
                break;
            //车位状态  进入室内地图
            case R.id.park_status:
                Intent intent = new Intent(this, IndoorActivity.class);
                intent.putExtra("parkInfo", parkInfo);
                startActivity(intent);
                break;
            //查看全部评论
            case R.id.btn_comment_check:
                Intent intent2 = new Intent(this, CommentWholeActivity.class);
                intent2.putExtra("parkName", parkInfo.getParkName());
                intent2.putExtra("parkId", parkInfo.getParkId());
                startActivity(intent2);
                break;
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

    /**
     * 获取评论列表
     */
    private void getCommentList() {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("parkId", park.getParkId());
        paramMap.put("from", index);
        paramMap.put("recnum", count);
        HttpUtils.sendHttpPostRequest(Urls.URL_GET_COMMENT_LIST, handlerComment, paramMap, this);
    }

    Handler handlerComment = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, getString(R.string.net_socket_time));
            } else {
                GetComment comment = (GetComment) JsonUtils.jsonToObject(msg.obj.toString(), GetComment.class);
                if (comment != null) {
                    int code = comment.getCode();
                    if (code == 0) {
                        tvGrade.setText(comment.getContent().getAvgLevel() + "星");
                        result = comment.getContent().getCommentList();
                        notifyComment();
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
        mAdapter = new BannerPagerParkAdapter(context, bannerList);
        mViewPager.setAdapter(mAdapter);

        mAdapterComment = new CommentWholeAdapter(this, resultList);
        mListView.setAdapter(mAdapterComment);
    }

    private void notifyComment() {
        resultList.clear();
        if(result != null) {
            resultList.addAll(result);
        }
        mAdapterComment.notifyDataSetChanged();
    }

}
