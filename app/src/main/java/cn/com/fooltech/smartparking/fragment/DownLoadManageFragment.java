package cn.com.fooltech.smartparking.fragment;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.offlinemap.adapter.DownLoadManagerAdapter;
import cn.com.fooltech.smartparking.offlinemap.bean.OfflineMapItem;
import cn.com.fooltech.smartparking.offlinemap.interfaces.OnOfflineItemStatusChangeListener;

public class DownLoadManageFragment extends Fragment implements MKOfflineMapListener, OnOfflineItemStatusChangeListener {
    private ListView mListView;
    private MKOfflineMap mOffline = null;
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    private RelativeLayout tvEmpty;
    private List<OfflineMapItem> itemsDown; //下载或下载中城市
    private boolean isWake = false;
    private List<OfflineMapItem> itemsAll;  //所有城市，与热门城市及下载管理对象相同
    /**
     * 已下载的离线地图信息列表
     */
    public List<MKOLUpdateElement> elementList = new ArrayList<MKOLUpdateElement>();
    public List<MKOLUpdateElement> elementDown = new ArrayList<MKOLUpdateElement>();//已下载的城市
    private DownLoadManagerAdapter mAdapter;

    public static DownLoadManageFragment newInstance() {
        DownLoadManageFragment fragment = new DownLoadManageFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化离线地理
        mOffline = new MKOfflineMap();
        mOffline.init(this);
        MyApplication.mOffline = mOffline;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_down_load_manage, container, false);
        mListView = (ListView) view.findViewById(R.id.lv_download);
        tvEmpty = (RelativeLayout) view.findViewById(R.id.empty_offline);
        initView();
        getUpdateInfo();
        return view;
    }

    public void getUpdateInfo(){
        List<MKOLUpdateElement> list = MyApplication.mOffline.getAllUpdateInfo();
        elementList.clear();
        elementDown.clear();
        if(list != null) {
            for (MKOLUpdateElement element : list){
                if(element.ratio != 100) {
                    elementList.add(element);
                }else {
                    elementDown.add(element);
                }
            }
            elementList.addAll(elementDown);
        }
        mAdapter.notifyDataSetChanged();
    }

    private void initView() {
        mAdapter = new DownLoadManagerAdapter(getActivity(), MyApplication.mOffline, this,elementList);
        mListView.setAdapter(mAdapter);
        mListView.setEmptyView(tvEmpty);
    }


    @Override
    public void onGetOfflineMapState(int type, int state) {
        switch (type) {
            case MKOfflineMap.TYPE_DOWNLOAD_UPDATE:
                getUpdateInfo();
                break;

            case MKOfflineMap.TYPE_NEW_OFFLINE:
                // 有新离线地图安装
//                Log.d("OfflineDemo", String.format("add offlinemap num:%d", state));
                break;

            case MKOfflineMap.TYPE_VER_UPDATE:
                // 版本更新提示
                break;
        }
    }

    @Override
    public void statusChanged(MKOLUpdateElement item, boolean removed) {
        if(removed){
            for (int i = elementList.size() - 1; i >= 0; i--) {
                MKOLUpdateElement element = elementList.get(i);
                if (element.cityID == item.cityID) {
                    elementList.remove(i);
                }
            }
            mAdapter.notifyDataSetChanged();
//            refreshDownList();

        }else{
            mAdapter.notifyDataSetChanged();
        }
    }

//    /**
//     * 返回itemsDown.add
//     * @param element
//     * @param ischeck
//     * @return
//     */
//    private OfflineMapItem setElement(MKOLUpdateElement element, boolean ischeck) {
//        OfflineMapItem ret = null;
//
//        if (element == null || itemsAll == null) {
//            return null;
//        }
//
//        if(element.status == MKOLUpdateElement.DOWNLOADING && element.ratio == 100){
//            element.status = MKOLUpdateElement.FINISHED;
//        }
//
//        for (OfflineMapItem item : itemsAll) {
//            if (element.cityID == item.getCityId()) {
//                item.setDownInfo(element);
//
//                // 过滤已下载数据
//                if (item.getStatus() != MKOLUpdateElement.UNDEFINED) {
//                    if (ischeck) {
//                        if (!itemsDown.contains(item)) {
//                            if (itemsDown.add(item)) {
//                                ret = item;
//                            }
//                        }
//
//                    } else {
//                        if (itemsDown.add(item)) {
//                            ret = item;
//                        }
//                    }
//                }
//                if (!isWake && item.getStatus() == MKOLUpdateElement.WAITING) {
//                    int id = item.getCityId();
//                    if (id > 0) {
//                        mOffline.start(id);
//                    }
//                    isWake = true;
//                }
//                break;
//            }
//        }
//        return ret;
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        MyApplication.mOffline.destroy();
    }
}
