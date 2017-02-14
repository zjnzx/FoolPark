package cn.com.fooltech.smartparking.fragment;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.activity.BookOrCollectActivity;
import cn.com.fooltech.smartparking.adapter.ParkBookAdapter;
import cn.com.fooltech.smartparking.adapter.ParkCollectAdapter;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.bean.ParkBookInfo;
import cn.com.fooltech.smartparking.bean.ParkCollectInfo;
import cn.com.fooltech.smartparking.common.Common;
import cn.com.fooltech.smartparking.common.Urls;
import cn.com.fooltech.smartparking.bean.jsonbean.GetBookPark;
import cn.com.fooltech.smartparking.bean.jsonbean.GetCollectPark;
import cn.com.fooltech.smartparking.slidelistview.SwipeMenu;
import cn.com.fooltech.smartparking.slidelistview.SwipeMenuCreator;
import cn.com.fooltech.smartparking.slidelistview.SwipeMenuItem;
import cn.com.fooltech.smartparking.slidelistview.SwipeMenuListView;
import cn.com.fooltech.smartparking.utils.DateUtils;
import cn.com.fooltech.smartparking.utils.ErrorUtils;
import cn.com.fooltech.smartparking.utils.HttpUtils;
import cn.com.fooltech.smartparking.utils.JsonUtils;
import cn.com.fooltech.smartparking.utils.SPUtils;
import cn.com.fooltech.smartparking.utils.ToastUtils;
import cn.com.fooltech.smartparking.utils.Utils;
import cn.com.fooltech.smartparking.view.ListViewPlus;

public class BookOrCollectFragment extends Fragment implements ListViewPlus.ListViewPlusListener{
    private static final String ARG_PARAM1 = "param1";
    private  int BOOK = 1;
    private  int COLLECT = 2;
    private  int TYPE;
    private SwipeMenuListView mListView;
    private ImageView ivEmpty;
    private List<ParkBookInfo> resultBookList = new ArrayList<ParkBookInfo>();
    private List<ParkCollectInfo> resultCollectList = new ArrayList<ParkCollectInfo>();
    private ParkCollectAdapter mAdapterCollect;
    private ParkBookAdapter mAdapterBook;
    private int posi;
    private long parkId;
    private String serviceTime = "";
    private int index = 0,count = 20;
    private int flag = ListViewPlus.REFRESH;//0:刷新  1:加载

    public static BookOrCollectFragment newInstance(int param1) {
        BookOrCollectFragment fragment = new BookOrCollectFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            TYPE = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_book_or_collect, container, false);
        mListView = (SwipeMenuListView) view.findViewById(R.id.lv_book);
        mListView.setRefreshEnable(true);
        mListView.setLoadEnable(true);
        mListView.setAutoLoadEnable(true);
        mListView.setListViewPlusListener(this);
        ivEmpty = (ImageView) view.findViewById(R.id.empty_book);
        initDeleteBtn();
        initAdapter();
        getRecordList(ListViewPlus.REFRESH);
        return view;
    }

    public void getRecordList(int flag){
        this.flag = flag;
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("userId", SPUtils.get(getActivity(), "userId", new Long(0l)));
        paramMap.put("token", SPUtils.get(getActivity(), "token", ""));
        paramMap.put("from", index);
        paramMap.put("recnum", count);
        if(TYPE == BOOK){
            paramMap.put("lng", (double)MyApplication.longtitude);
            paramMap.put("lat", (double)MyApplication.latitude);
            HttpUtils.sendHttpPostRequest(Urls.URL_GET_BOOK_RECORD, handlerBook, paramMap,getActivity());
        }else if(TYPE == COLLECT){
            HttpUtils.sendHttpPostRequest(Urls.URL_GET_COLLECT_PARK, handlerCollect, paramMap,getActivity());
        }
    }

    Handler handlerBook = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(getActivity(), "服务器出现异常");
            }else {
                GetBookPark bookPark = (GetBookPark) JsonUtils.jsonToObject(msg.obj.toString(), GetBookPark.class);
                if (bookPark != null) {
                    int code = bookPark.getCode();
                    if (code == 0) {
                        List<ParkBookInfo> result = bookPark.getContent();
                        serviceTime = bookPark.getServerTime();
                        notifyBookData(result);
                    } else {
                        ErrorUtils.errorCode(getActivity(), code);
                    }
                } else {
                    ToastUtils.showShort(getActivity(), "服务器请求错误");
                }
            }
            super.handleMessage(msg);
        }
    };
    Handler handlerCollect = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(getActivity(), "服务器出现异常");
            }else {
                GetCollectPark collectPark = (GetCollectPark) JsonUtils.jsonToObject(msg.obj.toString(), GetCollectPark.class);
                if (collectPark != null) {
                    int code = collectPark.getCode();
                    if (code == 0) {
                        List<ParkCollectInfo> result = collectPark.getContent();
                        notifyCollData(result);
                    } else {
                        ErrorUtils.errorCode(getActivity(), code);
                    }
                } else {
                    ToastUtils.showShort(getActivity(), "服务器请求错误");
                }
            }
            super.handleMessage(msg);
        }
    };

    private void initAdapter(){
        if(TYPE == BOOK) { //预约
            mAdapterBook = new ParkBookAdapter((BookOrCollectActivity)getActivity(), resultBookList);
            mListView.setAdapter(mAdapterBook);
        }else if(TYPE == COLLECT){//收藏
            mAdapterCollect = new ParkCollectAdapter((BookOrCollectActivity)getActivity(), resultCollectList);
            mListView.setAdapter(mAdapterCollect);
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
                SwipeMenuItem openItem = new SwipeMenuItem(getActivity().
                        getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F,
                        0x25)));
                // set item width
                openItem.setWidth(Common.dp2px(70, getActivity()));
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
                posi = position;
                if(TYPE == COLLECT) {
                    ParkCollectInfo info1 = mAdapterCollect.list.get(position);
                    unCollect(info1.getCollectId());
                    parkId = info1.getParkId();
                }else if(TYPE == BOOK){
                    ParkBookInfo info = mAdapterBook.list.get(position);
                    delBook(info.getBookId());
                }
                return false;
            }
        });
    }

    /**
     * 取消收藏
     */
    private void unCollect(long collectId){
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("userId", SPUtils.get(getActivity(), "userId", new Long(0l)));
        paramMap.put("token", SPUtils.get(getActivity(), "token", ""));
        paramMap.put("collectId",collectId);
        HttpUtils.sendHttpPostRequest(Urls.URL_UNCOLLECT_PARK, handlerUnCollect, paramMap,getActivity());
    }
    /**
     * 删除预约
     */
    private void delBook(long bookId){
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("userId", SPUtils.get(getActivity(), "userId", new Long(0l)));
        paramMap.put("token", SPUtils.get(getActivity(), "token", ""));
        paramMap.put("bookId",bookId);
        HttpUtils.sendHttpPostRequest(Urls.URL_REMOVE_BOOK_SPACE, handlerDelBook, paramMap,getActivity());
    }

    //取消收藏
    Handler handlerUnCollect = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(getActivity(), "服务器出现异常");
            }else {
                int code = JsonUtils.getCodeResult(msg.obj.toString());
                if (code == 0) {
                    mAdapterCollect.removeData(posi);
                    if(mAdapterCollect.list.size() == 0){
                        ivEmpty.setVisibility(View.VISIBLE);
                    }
                } else {
                    ErrorUtils.errorCode(getActivity(), code);
                }
            }
            super.handleMessage(msg);
        }
    };

    //删除预约
    Handler handlerDelBook = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(getActivity(), "服务器出现异常");
            }else {
                int code = JsonUtils.getCodeResult(msg.obj.toString());
                if (code == 0) {
                    mAdapterBook.removeData(posi);
                    if(mAdapterBook.list.size() == 0){
                        ivEmpty.setVisibility(View.VISIBLE);
                    }
                } else {
                    ErrorUtils.errorCode(getActivity(), code);
                }
            }
            super.handleMessage(msg);
        }
    };

    private void notifyBookData(List<ParkBookInfo> result){
        if(flag == ListViewPlus.REFRESH){
            resultBookList.clear();
            resultBookList.addAll(result);
        }else if(flag == ListViewPlus.LOAD){
            resultBookList.addAll(result);
        }
        onLoadComplete();
        mAdapterBook.notifyDataSetChanged();
        mAdapterBook.setServiceTime(serviceTime);
        if(resultBookList.size() == 0){
            ivEmpty.setVisibility(View.VISIBLE);
            ivEmpty.setBackgroundResource(R.drawable.empty_book);
        }else {
            ivEmpty.setVisibility(View.GONE);
        }
    }
    private void notifyCollData(List<ParkCollectInfo> result){
        if(flag == ListViewPlus.REFRESH){
            resultCollectList.clear();
            resultCollectList.addAll(result);
        }else if(flag == ListViewPlus.LOAD){
            resultCollectList.addAll(result);
        }
        onLoadComplete();
        mAdapterCollect.notifyDataSetChanged();
        if(resultCollectList.size() == 0){
            ivEmpty.setVisibility(View.VISIBLE);
            ivEmpty.setBackgroundResource(R.drawable.empty_coll);
        }else {
            ivEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRefresh() {
        index = 0;
        getRecordList(ListViewPlus.REFRESH);
    }

    @Override
    public void onLoadMore() {
        index += count;
        getRecordList(ListViewPlus.LOAD);
    }

    private void onLoadComplete() {
        mListView.stopRefresh();
        mListView.stopLoadMore();
        mListView.setRefreshTime(DateUtils.getDate());
    }

    public void setIndex(){
        index = 0;
    }

}
