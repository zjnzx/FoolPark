/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年9月7日 下午9:02:54   
 * @version 1.0   
 */
package cn.com.fooltech.smartparking.offlinemap.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;

import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.activity.OfflineMapActivity;
import cn.com.fooltech.smartparking.offlinemap.utils.FileUtil;
import cn.com.fooltech.smartparking.offlinemap.bean.OfflineMapItem;
import cn.com.fooltech.smartparking.offlinemap.interfaces.OnOfflineItemStatusChangeListener;

public class OfflineExpandableListAdapter extends BaseExpandableListAdapter{

	private Context context;
	private MKOfflineMap mOffline;
	protected LayoutInflater inflater;
	private OnOfflineItemStatusChangeListener listener;
	
	private List<OfflineMapItem> itemsProvince;
    private List<List<OfflineMapItem>> itemsProvinceCity;
    private ArrayList<MKOLSearchRecord> hotCities;
    private ArrayList<MKOLSearchRecord> wholeCities;
    private ArrayList<MKOLSearchRecord> currentCities;

    public OfflineExpandableListAdapter(Context context, MKOfflineMap mOffline, OnOfflineItemStatusChangeListener listener){
    	this.context = context;
    	this.mOffline = mOffline;
    	this.listener = listener;
    	inflater = LayoutInflater.from(context);
    }

    @Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
    	final OfflineMapItem item = (OfflineMapItem) getGroup(groupPosition);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.listitem_offline_province, null);
		}
		
		TextView provinceText = (TextView) convertView.findViewById(R.id.tvProvince);
		TextView cityType = (TextView) convertView.findViewById(R.id.tvType);
        String cityName = item.getCityName().toString();
        if(cityName.equals("当前城市")){
            currentCities =  item.getCityInfo().childCities;
            cityType.setVisibility(View.VISIBLE);
            provinceText.setVisibility(View.GONE);
            cityType.setText(cityName);
        }else if(cityName.equals("热门城市")){
            hotCities =  item.getCityInfo().childCities;
            cityType.setVisibility(View.VISIBLE);
            provinceText.setVisibility(View.GONE);
            cityType.setText(cityName);
        }else if(cityName.equals("全部城市")){
            wholeCities =  item.getCityInfo().childCities;
            cityType.setVisibility(View.VISIBLE);
            provinceText.setVisibility(View.GONE);
            cityType.setText(cityName);
        }else{
            provinceText.setVisibility(View.VISIBLE);
            cityType.setVisibility(View.GONE);
            provinceText.setText(cityName);
        }

		if (isExpanded) {
			provinceText.setCompoundDrawablesWithIntrinsicBounds(null, 
					null,
					context.getResources().getDrawable(R.drawable.ic_offline_u),
					null);
		} else {
			provinceText.setCompoundDrawablesWithIntrinsicBounds(null, 
					null,
					context.getResources().getDrawable(R.drawable.ic_offline_d),
					null);
		}
		return convertView;
	}
    
    @Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
    	ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.listitem_offline_province_child, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		OfflineMapItem data = (OfflineMapItem) getChild(groupPosition, childPosition);
        //设置默认展开的城市列表背景和隐藏的列表背景不同
        if(groupPosition == 0 && currentCities.get(childPosition).cityID  == (data.getCityId())){
            convertView.setBackgroundResource(R.drawable.selector_item);
        }else if(groupPosition == 1 && hotCities.get(childPosition).cityID  == (data.getCityId())){
            convertView.setBackgroundResource(R.drawable.selector_item);
        }else if(groupPosition == 2 && wholeCities.get(childPosition).cityID  == (data.getCityId())){
            convertView.setBackgroundResource(R.drawable.selector_item);
        }else{
            convertView.setBackgroundResource(R.drawable.selector_item2);
        }
		holder.setData(data);
		
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		if(itemsProvince != null){
			return itemsProvince.size();
		}
		return 0;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		if(itemsProvinceCity != null && groupPosition >= 0 && groupPosition < itemsProvinceCity.size()){
			List<OfflineMapItem> c = itemsProvinceCity.get(groupPosition);
			if(c != null){
				return c.size();
			}
		}
		return 0;
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		if(itemsProvince != null && groupPosition >= 0 && groupPosition < itemsProvince.size()){
			return itemsProvince.get(groupPosition);
		}
		return null;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		List<OfflineMapItem> pList = itemsProvinceCity.get(groupPosition);
		if(pList != null && childPosition >= 0 && childPosition < pList.size()){
			return pList.get(childPosition);
		}
		return null;
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	public List<OfflineMapItem> search(String key) {
		List<OfflineMapItem> list = new ArrayList<OfflineMapItem>();
		for (int i = 0; i < itemsProvinceCity.size(); i++) {
			for (OfflineMapItem item : itemsProvinceCity.get(i)) {
				if (item.getCityName().indexOf(key) >= 0) {
					list.add(item);
				}
			}
		}
		return list;
	}


	public void setDatas(List<OfflineMapItem> itemsProvince, List<List<OfflineMapItem>> itemsProvinceCity) {
		this.itemsProvince = itemsProvince;
		this.itemsProvinceCity = itemsProvinceCity;
		notifyDataSetChanged();
	}

	
	class ViewHolder implements OnClickListener{
		View lyRoot;
        TextView tvCityname;
        TextView tvSize;
        TextView tvStatus;
        ImageView ivDownload;
        
        private OfflineMapItem data;

        public ViewHolder(View convertView){
        	lyRoot = convertView.findViewById(R.id.lyRoot);
        	tvCityname = (TextView) convertView.findViewById(R.id.tvCityname);
        	tvSize = (TextView) convertView.findViewById(R.id.tvSize);
        	tvStatus = (TextView) convertView.findViewById(R.id.tvStatus);
        	ivDownload = (ImageView) convertView.findViewById(R.id.ivDownload);
        	
        	lyRoot.setOnClickListener(this);
        }
        
        public void setData(OfflineMapItem data) {
			this.data = data;
			
			tvCityname.setText(data.getCityName());
			tvSize.setText(FileUtil.getSizeStr(data.getSize()));
			
			if(data.getStatus() == MKOLUpdateElement.UNDEFINED){
				tvStatus.setText("");
				
			}else if(data.getStatus() == MKOLUpdateElement.DOWNLOADING){
				tvStatus.setTextColor(context.getResources().getColor(R.color.green));
				tvStatus.setText("正在下载");
				
			}else if(data.getStatus() == MKOLUpdateElement.FINISHED){
				tvStatus.setTextColor(context.getResources().getColor(R.color.green));
				tvStatus.setText("已下载");
				
			}else if(data.getStatus() == MKOLUpdateElement.SUSPENDED
					|| data.getStatus() >= MKOLUpdateElement.eOLDSMd5Error){
				//暂停、错误，都当作暂停，都是可以继续下载
				tvStatus.setTextColor(context.getResources().getColor(R.color.red3));
				tvStatus.setText("已暂停");
				
			}else if(data.getStatus() == MKOLUpdateElement.WAITING){
				tvStatus.setTextColor(context.getResources().getColor(R.color.green));
				tvStatus.setText("等待下载");
				
			}else{
				tvStatus.setText("");
			}
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.lyRoot:
				if(data.getStatus() == MKOLUpdateElement.UNDEFINED){
					int id = data.getCityId();
					if(id > 0){
						mOffline.start(id);
						data.setStatus(MKOLUpdateElement.WAITING);
						tvStatus.setTextColor(context.getResources().getColor(R.color.green));
						tvStatus.setText("正在下载");
						if(listener != null){
//							listener.statusChanged(null, false);
						}
					}
					
				}else{
					//跳转下载界面
					if(context instanceof OfflineMapActivity){
						((OfflineMapActivity)context).toDownloadPage();
					}
				}
				break;
			default:
				break;
			}
		}
    }
}
