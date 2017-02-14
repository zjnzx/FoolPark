package cn.com.fooltech.smartparking.fragment;

import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.offlinemap.adapter.OfflineExpandableListAdapter;
import cn.com.fooltech.smartparking.offlinemap.adapter.OfflineMapAdapter;
import cn.com.fooltech.smartparking.offlinemap.bean.OfflineMapItem;
import cn.com.fooltech.smartparking.offlinemap.interfaces.OnOfflineItemStatusChangeListener;
import cn.com.fooltech.smartparking.offlinemap.utils.CsqBackgroundTask;
import cn.com.fooltech.smartparking.offlinemap.utils.ToastUtil;

public class WholeCityFragment extends Fragment  implements OnOfflineItemStatusChangeListener {
    private ExpandableListView mExListView;
    private ListView lvSearchResult;
    private EditText etSearch;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";

    private String mParam1;
    private MKOfflineMap mOffline = null;
    private List<OfflineMapItem> itemsDown = new ArrayList<OfflineMapItem>(); //下载或下载中城市
    private List<OfflineMapItem> itemsAll;  //所有城市，与热门城市及下载管理对象相同
    private OfflineMapAdapter allSearchAdapter;
    private OfflineExpandableListAdapter allCountryAdapter;
    private boolean isWake = false;
    private boolean isResumed = false;
    private List<OfflineMapItem> itemsProvince;
    private List<List<OfflineMapItem>> itemsProvinceCity;
    private int lastGroupPosition = -1;

    public static WholeCityFragment newInstance() {
        WholeCityFragment fragment = new WholeCityFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadData();
        // 初始化离线地理
//        mOffline = new MKOfflineMap();
//        mOffline.init(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_whole_city, container, false);
        mExListView = (ExpandableListView) view.findViewById(R.id.lv_city);
        lvSearchResult = (ListView) view.findViewById(R.id.lvSearchResult);
        etSearch = (EditText) view.findViewById(R.id.et_search);
        initView();
        return view;
    }

    private void initView(){
        allSearchAdapter = new OfflineMapAdapter(getActivity(), MyApplication.mOffline, this);
        lvSearchResult.setAdapter(allSearchAdapter);

        allCountryAdapter = new OfflineExpandableListAdapter(getActivity(), MyApplication.mOffline, this);
        mExListView.setAdapter(allCountryAdapter);
        mExListView.setGroupIndicator(null);
        mExListView.expandGroup(0);//当前城市,热门城市,全部城市默认展开
        mExListView.expandGroup(1);
        mExListView.expandGroup(2);

//        allCountryAdapter.setDatas(itemsProvince, itemsProvinceCity);

        mExListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {
                if (groupPosition == 0 || groupPosition == 1 || groupPosition == 2) { //当前城市,热门城市,全部城市不能隐藏
                    return true;
                }
                return false;
            }
        });

        //展开一个group时，其他group都关闭
        mExListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
//                if(lastGroupPosition != -1) {
//                    mExListView.collapseGroup(lastGroupPosition);
//                }
//                lastGroupPosition = groupPosition;
//                mExListView.setSelectedGroup(groupPosition);
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                refreshAllSearchList();
            }
        });
    }

//    /**
//     *
//     * @author chenshiqiang E-mail:csqwyyx@163.com
//     * @param type 事件类型: MKOfflineMap.TYPE_NEW_OFFLINE, MKOfflineMap.TYPE_DOWNLOAD_UPDATE, MKOfflineMap.TYPE_VER_UPDATE.
//     * @param state 事件状态: 当type为TYPE_NEW_OFFLINE时，表示新安装的离线地图数目. 当type为TYPE_DOWNLOAD_UPDATE时，表示更新的城市ID.
//     */
//    @Override
//    public void onGetOfflineMapState(int type, int state) {
//        switch (type) {
//            case MKOfflineMap.TYPE_DOWNLOAD_UPDATE:
////                MKOLUpdateElement update = MyApplication.mOffline.getUpdateInfo(state);
////
////                if(setElement(update, true) != null){
////                    if (itemsDown != null && itemsDown.size() > 1) {
////                        Collections.sort(itemsDown);
////                    }
//////                    refreshDownList();
////                }else{
//////                    downAdapter.notifyDataSetChanged();
////                }
//////                allSearchAdapter.notifyDataSetChanged();
////                allCountryAdapter.notifyDataSetChanged();
//                break;
//
//            case MKOfflineMap.TYPE_NEW_OFFLINE:
//                // 有新离线地图安装
////                Log.d("OfflineDemo", String.format("add offlinemap num:%d", state));
//                break;
//
//            case MKOfflineMap.TYPE_VER_UPDATE:
//                // 版本更新提示
//                break;
//        }
//    }

    @Override
    public void statusChanged(MKOLUpdateElement item, boolean removed) {
        allSearchAdapter.notifyDataSetChanged();
        allCountryAdapter.notifyDataSetChanged();
    }

    /**
     * 返回itemsDown.add
     * @param element
     * @param ischeck
     * @return
     */
    private OfflineMapItem setElement(MKOLUpdateElement element, boolean ischeck) {
        OfflineMapItem ret = null;

        if (element == null || itemsAll == null) {
            return null;
        }

        if(element.status == MKOLUpdateElement.DOWNLOADING && element.ratio == 100){
            element.status = MKOLUpdateElement.FINISHED;
        }

        for (OfflineMapItem item : itemsAll) {
            if (element.cityID == item.getCityId()) {
                item.setDownInfo(element);

                // 过滤已下载数据
                if (item.getStatus() != MKOLUpdateElement.UNDEFINED) {
                    if (ischeck) {
                        if (!itemsDown.contains(item)) {
                            if (itemsDown.add(item)) {
                                ret = item;
                            }
                        }

                    } else {
                        if (itemsDown.add(item)) {
                            ret = item;
                        }
                    }
                }
                if (!isWake && item.getStatus() == MKOLUpdateElement.WAITING) {
                    int id = item.getCityId();
                    if (id > 0) {
                        MyApplication.mOffline.start(id);
                    }
                    isWake = true;
                }
                break;
            }
        }

        return ret;
    }

    /**
     * 刷新所有城市搜索结果
     */
    private void refreshAllSearchList(){
        String key = etSearch.getText().toString();
        if(key.equals("")){
            lvSearchResult.setVisibility(View.GONE);
            mExListView.setVisibility(View.VISIBLE);

            allSearchAdapter.setDatas(null);
        }else{
            lvSearchResult.setVisibility(View.VISIBLE);
            mExListView.setVisibility(View.GONE);

            List<OfflineMapItem> filterList = new ArrayList<OfflineMapItem>();
            if(itemsAll != null && !itemsAll.isEmpty()){
                for(OfflineMapItem i : itemsAll){
                    if(i.getCityName().contains(key)){
                        filterList.add(i);
                    }
                }
            }
            allSearchAdapter.setDatas(filterList);
        }
    }

    public void loadData() {

        new CsqBackgroundTask<Void>(getActivity()) {
            @Override
            protected Void onRun() {
                // TODO Auto-generated method stub
                // 导入离线地图包
                // 将从官网下载的离线包解压，把vmp文件夹拷入SD卡根目录下的BaiduMapSdk文件夹内。
                // 把网站上下载的文件解压，将\BaiduMap\vmp\l里面的.dat_svc文件，拷贝到手机BaiduMapSDK/vmp/h目录下
                int num = MyApplication.mOffline.importOfflineData();
                if (num > 0) {
                    ToastUtil.showToastInfo(getActivity(), "成功导入" + num + "个离线包", false);
                }

                List<MKOLSearchRecord> all = null;//全国基础包,省,市
                try {
                    all = MyApplication.mOffline.getOfflineCityList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (all == null || all.isEmpty()) {
                    ToastUtil.showToastInfo(getActivity(), "未获取到离线地图城市数据，可能有其他应用正在使用百度离线地图功能！", false);
                    return null;
                }

                List<MKOLSearchRecord> hotCity = MyApplication.mOffline.getHotCityList();//热门城市
                HashSet<Integer> hotCityIds = new HashSet<Integer>();
                if (!hotCity.isEmpty()) {
                    for (MKOLSearchRecord r : hotCity) {
                        hotCityIds.add(r.cityID);
                    }
                }

                itemsAll = new ArrayList<OfflineMapItem>();
                itemsDown = new ArrayList<OfflineMapItem>();
                itemsProvince = new ArrayList<OfflineMapItem>();
                itemsProvinceCity = new ArrayList<List<OfflineMapItem>>();

                //cityType  0:全国；1：省份；2：城市,如果是省份，可以通过childCities得到子城市列表
                //全国概略图、直辖市、港澳 子城市列表
                ArrayList<MKOLSearchRecord> childMunicipalities = new ArrayList<MKOLSearchRecord>();
                ArrayList<OfflineMapItem> childHotCitys = new ArrayList<OfflineMapItem>();

                for (MKOLSearchRecord province : all) {
                    OfflineMapItem p = new OfflineMapItem();
                    p.setCityInfo(province);

                    List<MKOLSearchRecord> childs = province.childCities;//省内的城市
                    if (childs != null && !childs.isEmpty()) {
                        //省
                        List<OfflineMapItem> itemList = new ArrayList<OfflineMapItem>();
                        for (MKOLSearchRecord itemCity : childs) {
                            OfflineMapItem c = new OfflineMapItem();
                            c.setCityInfo(itemCity);
                            itemList.add(c);

                            itemsAll.add(c);
                            if (hotCityIds.contains(itemCity.cityID)) {
                                //添加到热门城市，保证与省份下的城市是一个对象
                                childHotCitys.add(c);
                            }
                        }
                        itemsProvinceCity.add(itemList);
                        itemsProvince.add(p);

                    } else {
                        //全国概略图、直辖市、港澳
                        childMunicipalities.add(province);
                    }
                }

                //构建一个省份，放全国概略图、直辖市、港澳
                if (!childMunicipalities.isEmpty()) {
                    MKOLSearchRecord proMunicipalities = new MKOLSearchRecord();
                    proMunicipalities.cityName = "全部城市";
                    proMunicipalities.childCities = childMunicipalities;
                    proMunicipalities.cityType = 1;

                    List<OfflineMapItem> itemList = new ArrayList<OfflineMapItem>();
                    for (MKOLSearchRecord itemCity : childMunicipalities) {
                        OfflineMapItem c = new OfflineMapItem();
                        c.setCityInfo(itemCity);
                        itemList.add(c);

                        proMunicipalities.size += itemCity.size;
                        itemsAll.add(c);
                        if (hotCityIds.contains(itemCity.cityID)) {
                            //添加到热门城市，保证与省份下的城市是一个对象
                            childHotCitys.add(c);
                        }
                    }

                    OfflineMapItem item = new OfflineMapItem();
                    item.setCityInfo(proMunicipalities);
                    itemsProvinceCity.add(0, itemList);
                    itemsProvince.add(0, item);
                }

                //构建一个省份，放热门城市
                if (!childHotCitys.isEmpty()) {
                    int size = 0;
                    ArrayList<MKOLSearchRecord> cs = new ArrayList<MKOLSearchRecord>();
                    for (OfflineMapItem i : childHotCitys) {
                        cs.add(i.getCityInfo());
                        size += i.getSize();
                    }

                    MKOLSearchRecord proHot = new MKOLSearchRecord();
                    proHot.cityName = "热门城市";
                    proHot.childCities = cs;
                    proHot.cityType = 1;
                    proHot.size = size;

                    OfflineMapItem item1 = new OfflineMapItem();
                    item1.setCityInfo(proHot);
                    itemsProvinceCity.add(0, childHotCitys);
                    itemsProvince.add(0, item1);
                }
                //构建一个当前城市
                if (!childHotCitys.isEmpty()) {
                    ArrayList<OfflineMapItem> itemList = new ArrayList<OfflineMapItem>();
                    ArrayList<MKOLSearchRecord> cities = new ArrayList<MKOLSearchRecord>();

                    if (itemsAll != null && !itemsAll.isEmpty()) {
                        for (OfflineMapItem i : itemsAll) {
                            if (i.getCityName().equals(MyApplication.currentCity)) {
                                itemList.add(i);
                                cities.add(i.getCityInfo());
                                break;
                            }
                        }
                    }

                    MKOLSearchRecord proHot = new MKOLSearchRecord();
                    proHot.cityName = "当前城市";
                    proHot.childCities = cities;
                    proHot.cityType = 1;

                    OfflineMapItem item1 = new OfflineMapItem();
                    item1.setCityInfo(proHot);
                    itemsProvinceCity.add(0, itemList);
                    itemsProvince.add(0, item1);
                }


                // 刷新状态
                List<MKOLUpdateElement> updates = MyApplication.mOffline.getAllUpdateInfo();
                if (updates != null && updates.size() > 0) {
                    for (MKOLUpdateElement element : updates) {
                        setElement(element, false);
                    }

                    if (itemsDown != null && itemsDown.size() > 1) {
                        Collections.sort(itemsDown);
                    }
                }

                return null;
            }

            @Override
            protected void onResult(Void result) {
                // TODO Auto-generated method stub
//                refreshDownList();
                refreshAllSearchList();

                allCountryAdapter.setDatas(itemsProvince, itemsProvinceCity);
                allCountryAdapter.notifyDataSetChanged();
            }
        }.execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!isResumed){
            isResumed = true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyApplication.mOffline.destroy();
    }
}
