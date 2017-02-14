package cn.com.fooltech.smartparking.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.adapter.PointDetailAdapter;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.bean.PointInfo;
import cn.com.fooltech.smartparking.common.Urls;
import cn.com.fooltech.smartparking.bean.jsonbean.GetPoint;
import cn.com.fooltech.smartparking.utils.DateUtils;
import cn.com.fooltech.smartparking.utils.ErrorUtils;
import cn.com.fooltech.smartparking.utils.HttpUtils;
import cn.com.fooltech.smartparking.utils.JsonUtils;
import cn.com.fooltech.smartparking.utils.SPUtils;
import cn.com.fooltech.smartparking.utils.ToastUtils;
import cn.com.fooltech.smartparking.utils.Utils;
import cn.com.fooltech.smartparking.view.ListViewPlus;

public class PointDetailFragment extends Fragment  implements ListViewPlus.ListViewPlusListener {
    private static final String ARG_PARAM1 = "pointsType";
    private int pointsType;
    private ListViewPlus mListView;
    private TextView tvEmpty;
    private List<PointInfo> resultList = new ArrayList<PointInfo>();
    private List<PointInfo> result;
    private int index = 0,count = 20;
    private int flag = ListViewPlus.REFRESH;//0:刷新  1:加载
    private PointDetailAdapter mAdapter;

    public static PointDetailFragment newInstance(int param1) {
        PointDetailFragment fragment = new PointDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pointsType = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_point_detail, container, false);
        mListView = (ListViewPlus) view.findViewById(R.id.lv_point);
        mListView.setRefreshEnable(true);
        mListView.setLoadEnable(true);
        mListView.setAutoLoadEnable(true);
        mListView.setListViewPlusListener(this);
        tvEmpty = (TextView) view.findViewById(R.id.empty_point);
        initAdapter();
        getPointDetailList(ListViewPlus.REFRESH);
        return view;
    }

    public void getPointDetailList(int flag){
        this.flag = flag;
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("userId", SPUtils.get(getActivity(), "userId", new Long(0l)));
        paramMap.put("token", SPUtils.get(getActivity(), "token", ""));
        paramMap.put("pointsType", pointsType);
        paramMap.put("from", index);
        paramMap.put("recnum", count);
        HttpUtils.sendHttpPostRequest(Urls.URL_GET_POINT, handlerPoint, paramMap,getActivity());
    }

    Handler handlerPoint = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(getActivity(), "网络请求超时");
            }else {
                GetPoint point = (GetPoint) JsonUtils.jsonToObject(msg.obj.toString(), GetPoint.class);
                if (point != null) {
                    int code = point.getCode();
                    if (code == 0) {
                        result = point.getContent();
                        notifyData();
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
        mAdapter = new PointDetailAdapter(getActivity(),resultList);
        mListView.setAdapter(mAdapter);
    }

    private void notifyData(){
        if(flag == ListViewPlus.REFRESH){
            resultList.clear();
            if(result != null) {
                resultList.addAll(result);
            }
        }else if(flag == ListViewPlus.LOAD){
            if(result != null) {
                resultList.addAll(result);
            }
        }
        onLoadComplete();
        mAdapter.notifyDataSetChanged();
        if(resultList.size() == 0){
            tvEmpty.setVisibility(View.VISIBLE);
        }else {
            tvEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRefresh() {
        index = 0;
        getPointDetailList(ListViewPlus.REFRESH);
    }

    @Override
    public void onLoadMore() {
        index += count;
        getPointDetailList(ListViewPlus.LOAD);
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
