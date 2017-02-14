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
import cn.com.fooltech.smartparking.adapter.SysMessageAdapter;
import cn.com.fooltech.smartparking.adapter.UserMessageAdapter;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.bean.MessageInfo;
import cn.com.fooltech.smartparking.common.Common;
import cn.com.fooltech.smartparking.common.Urls;
import cn.com.fooltech.smartparking.bean.jsonbean.GetMessage;
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

public class MessageFragment extends Fragment implements ListViewPlus.ListViewPlusListener {
    private static final String ARG_PARAM1 = "messageType";
    private  int USER = 1;
    private  int SYS = 2;
    private int MESSAGETYPE;
    private SwipeMenuListView mListView;
    private ImageView ivEmpty;
    private UserMessageAdapter mAdapterUser;
    private SysMessageAdapter mAdapterSys;
    private int posiUser,posiSys;
    private List<MessageInfo> resultList = new ArrayList<MessageInfo>();
    private List<MessageInfo> result;
    private int index = 0,count = 20;
    private int flag = ListViewPlus.REFRESH;//0:刷新  1:加载

    public static MessageFragment newInstance(int param1) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            MESSAGETYPE = getArguments().getInt(ARG_PARAM1);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        mListView = (SwipeMenuListView) view.findViewById(R.id.lv_message);
        mListView.setRefreshEnable(true);
        mListView.setLoadEnable(true);
        mListView.setAutoLoadEnable(true);
        mListView.setListViewPlusListener(this);
        ivEmpty = (ImageView) view.findViewById(R.id.empty_message);
        initDeleteBtn();
        initAdapter();
        getMessageList(ListViewPlus.REFRESH);
        return view;
    }


    public void getMessageList(int flag){
        this.flag = flag;
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("userId", SPUtils.get(getActivity(), "userId", new Long(0l)));
        paramMap.put("token", SPUtils.get(getActivity(), "token", ""));
        paramMap.put("from", index);
        paramMap.put("recnum", count);
        if(MESSAGETYPE == USER){
            HttpUtils.sendHttpPostRequest(Urls.URL_GET_USER_MESSAGE, handlerMessage, paramMap,getActivity());
        }else if(MESSAGETYPE == SYS){
            HttpUtils.sendHttpPostRequest(Urls.URL_GET_SYSTEM_MESSAGE, handlerMessage, paramMap,getActivity());
        }
    }

    Handler handlerMessage = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(getActivity(), "网络请求超时");
            }else {
                GetMessage message = (GetMessage) JsonUtils.jsonToObject(msg.obj.toString(), GetMessage.class);
                if (message != null) {
                    int code = message.getCode();
                    if (code == 0) {
                        result = message.getContent();
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
        if(MESSAGETYPE == USER) { //用户消息
            mAdapterUser = new UserMessageAdapter(getActivity(), resultList);
            mListView.setAdapter(mAdapterUser);
        }else if(MESSAGETYPE == SYS){//系统消息
            mAdapterSys = new SysMessageAdapter(getActivity(), resultList);
            mListView.setAdapter(mAdapterSys);
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
                if(MESSAGETYPE == USER){ //删除用户消息
                    MessageInfo info = mAdapterUser.list.get(position);
                    deleteMessage(info.getMessageId());
                    posiUser = position;
                }else if(MESSAGETYPE == SYS){ //删除系统消息
                    MessageInfo info = mAdapterSys.list.get(position);
                    deleteMessage(info.getMessageId());
                    posiSys = position;
                }
                return false;
            }
        });
    }

    /**
     * 删除消息
     * @param messageId
     */
    private void deleteMessage(long messageId){
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("userId", SPUtils.get(getActivity(), "userId", new Long(0l)));
        paramMap.put("token", SPUtils.get(getActivity(), "token", ""));
        paramMap.put("messageId", messageId);
        if(MESSAGETYPE == USER){
            HttpUtils.sendHttpPostRequest(Urls.URL_DELETE_USER_MESSAGE, handlerDelMessage, paramMap,getActivity());
        }else if(MESSAGETYPE == SYS){
            HttpUtils.sendHttpPostRequest(Urls.URL_DELETE_SYS_MESSAGE, handlerDelMessage, paramMap,getActivity());
        }
    }

    Handler handlerDelMessage = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(getActivity(), "网络请求超时");
            }else {
                int code = JsonUtils.getCodeResult(msg.obj.toString());
                if (code == 0) {
                    if(MESSAGETYPE == USER) {
                        mAdapterUser.removeData(posiUser);
                        if(mAdapterUser.list.size() == 0){
                            ivEmpty.setVisibility(View.VISIBLE);
                        }
                    }else if(MESSAGETYPE == SYS){
                        mAdapterSys.removeData(posiSys);
                        if(mAdapterSys.list.size() == 0){
                            ivEmpty.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    ErrorUtils.errorCode(getActivity(), code);
                }
            }
            super.handleMessage(msg);
        }
    };

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
        if(MESSAGETYPE == USER) {
            mAdapterUser.notifyDataSetChanged();
            if(resultList.size() == 0){
                ivEmpty.setBackgroundResource(R.drawable.empty_msg_user);
                ivEmpty.setVisibility(View.VISIBLE);
            }else {
                ivEmpty.setVisibility(View.GONE);
            }
        }else if(MESSAGETYPE == SYS){
            mAdapterSys.notifyDataSetChanged();
            if(resultList.size() == 0){
                ivEmpty.setBackgroundResource(R.drawable.empty_msg_sys);
                ivEmpty.setVisibility(View.VISIBLE);
            }else {
                ivEmpty.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onRefresh() {
        index = 0;
        getMessageList(ListViewPlus.REFRESH);
    }

    @Override
    public void onLoadMore() {
        index += count;
        getMessageList(ListViewPlus.LOAD);
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
