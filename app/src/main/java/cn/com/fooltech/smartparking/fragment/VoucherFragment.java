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
import cn.com.fooltech.smartparking.adapter.VoucherAdapter;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.bean.VoucherInfo;
import cn.com.fooltech.smartparking.common.Urls;
import cn.com.fooltech.smartparking.bean.jsonbean.GetVoucher;
import cn.com.fooltech.smartparking.utils.DateUtils;
import cn.com.fooltech.smartparking.utils.ErrorUtils;
import cn.com.fooltech.smartparking.utils.HttpUtils;
import cn.com.fooltech.smartparking.utils.JsonUtils;
import cn.com.fooltech.smartparking.utils.SPUtils;
import cn.com.fooltech.smartparking.utils.ToastUtils;
import cn.com.fooltech.smartparking.utils.Utils;
import cn.com.fooltech.smartparking.view.ListViewPlus;

public class VoucherFragment extends Fragment  implements ListViewPlus.ListViewPlusListener {
    private ListViewPlus mListView;
    private TextView tvEmpty;
    private int status;
    private List<VoucherInfo> resultList = new ArrayList<VoucherInfo>();
    private List<VoucherInfo> result;
    private int index = 0,count = 20;
    private int flag = ListViewPlus.REFRESH;//0:刷新  1:加载
    private VoucherAdapter mAdapter;
    private static final String ARG_PARAM1 = "param1";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_voucher, container, false);
        mListView = (ListViewPlus) view.findViewById(R.id.lv_voucher);
        mListView.setRefreshEnable(true);
        mListView.setLoadEnable(true);
        mListView.setAutoLoadEnable(true);
        mListView.setListViewPlusListener(this);
        tvEmpty = (TextView) view.findViewById(R.id.empty_voucher);
        initAdapter();
        getVoucherList(ListViewPlus.REFRESH);
        return view;
    }
    public static VoucherFragment newInstance(int param1) {
        VoucherFragment fragment = new VoucherFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            status = getArguments().getInt(ARG_PARAM1);
        }
    }

    public void getVoucherList(int flag){
        this.flag = flag;
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("userId", SPUtils.get(getActivity(), "userId", new Long(0l)));
        paramMap.put("token", SPUtils.get(getActivity(), "token", ""));
        paramMap.put("from", index);
        paramMap.put("recnum", count);
        if(status != -1)
            paramMap.put("voucherStatus", status);
        HttpUtils.sendHttpPostRequest(Urls.URL_GET_VOUCHER, handlerVoucher, paramMap,getActivity());
    }

    Handler handlerVoucher = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(getActivity(), getString(R.string.net_socket_time));
            }else {
                GetVoucher voucher = (GetVoucher) JsonUtils.jsonToObject(msg.obj.toString(), GetVoucher.class);
                if (voucher != null) {
                    int code = voucher.getCode();
                    if (code == 0) {
                        result = voucher.getContent();
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
        mAdapter = new VoucherAdapter(getActivity(),resultList);
        mListView.setAdapter(mAdapter);
    }

    private void notifyData(){
        if(flag == ListViewPlus.REFRESH){
            resultList.clear();
            resultList.addAll(result);
        }else if(flag == ListViewPlus.LOAD){
            resultList.addAll(result);
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
        getVoucherList(ListViewPlus.REFRESH);
    }

    @Override
    public void onLoadMore() {
        index += count;
        getVoucherList(ListViewPlus.LOAD);
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
