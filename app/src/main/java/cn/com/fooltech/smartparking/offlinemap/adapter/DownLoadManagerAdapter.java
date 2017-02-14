/**
 * @description: 下载管理列表适配器
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年9月7日 下午4:53:40   
 * @version 1.0   
 */
package cn.com.fooltech.smartparking.offlinemap.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.fragment.DownLoadManageFragment;
import cn.com.fooltech.smartparking.offlinemap.bean.OfflineMapItem;
import cn.com.fooltech.smartparking.offlinemap.interfaces.OnOfflineItemStatusChangeListener;
import cn.com.fooltech.smartparking.offlinemap.utils.FileUtil;
import cn.com.fooltech.smartparking.service.MyService;

public class DownLoadManagerAdapter extends BaseAdapter {
	private Context context;
	private List<MKOLUpdateElement> list;
	private MKOfflineMap mOffline;
	private OnOfflineItemStatusChangeListener listener;
	private HashSet<Integer> expandedCityIds = new HashSet<Integer>();
	private LayoutInflater inflater;
	private int posi;
	private DownLoadManageFragment downLoadManageFragment;

	public DownLoadManagerAdapter(Context context, MKOfflineMap mOffline, OnOfflineItemStatusChangeListener listener,List<MKOLUpdateElement> list) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.list = list;
		this.mOffline = mOffline;
		this.listener = listener;
		inflater = LayoutInflater.from(context);
		downLoadManageFragment = DownLoadManageFragment.newInstance();
	}

	// -------- Methods for/from SuperClass/Interfaces -----------

	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public Object getItem(int i) {
		return list.get(i);
	}

	@Override
	public long getItemId(int i) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.listitem_offline_manager, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		posi = position;
		MKOLUpdateElement element = list.get(position);
//		OfflineMapItem data = (OfflineMapItem) getItem(position);
		holder.setData(element);
		
		return convertView;
	}
	
//	@Override
//	public void setDatas(List<OfflineMapItem> ds) {
//		// TODO Auto-generated method stub
//		super.setDatas(ds);
//
//		expandedCityIds.clear();
//	}
//
//	@Override
//	public void setArrayDatas(OfflineMapItem[] array) {
//		// TODO Auto-generated method stub
//		super.setArrayDatas(array);
//
//		expandedCityIds.clear();
//	}

	// --------------------- Methods public ----------------------
	
//	public List<OfflineMapItem> search(String key) {
//		List<OfflineMapItem> list = new ArrayList<OfflineMapItem>();
//		if(mDatas != null){
//			for (OfflineMapItem item : mDatas) {
//				if (item.getCityName().indexOf(key) >= 0) {
//					list.add(item);
//				}
//			}
//		}
//		return list;
//	}

	class ViewHolder implements OnClickListener{
		View lyCityInfo;
        TextView tvCityname,tvSize;
        ImageView ivExpande;
        
        View lyEditPanel;
        ProgressBar pbDownload;
        TextView tvStatus,tvStatus2;
        Button btnDown;
        Button btnRemove;
        
        private MKOLUpdateElement data;

        public ViewHolder(View convertView){
        	lyCityInfo = convertView.findViewById(R.id.lyCityInfo);
        	tvCityname = (TextView) convertView.findViewById(R.id.tvCityname);
        	ivExpande = (ImageView) convertView.findViewById(R.id.ivExpande);
        	lyEditPanel = convertView.findViewById(R.id.lyEditPanel);
        	pbDownload = (ProgressBar) convertView.findViewById(R.id.pbDownload);
			tvSize = (TextView) convertView.findViewById(R.id.tvSize);
        	tvStatus = (TextView) convertView.findViewById(R.id.tvStatus);
        	tvStatus2 = (TextView) convertView.findViewById(R.id.download_status);
        	btnDown = (Button) convertView.findViewById(R.id.btnDown);
        	btnRemove = (Button) convertView.findViewById(R.id.btnRemove);
        	
        	lyCityInfo.setOnClickListener(this);
        	btnDown.setOnClickListener(this);
        	btnRemove.setOnClickListener(this);
        }
        
        public void setData(MKOLUpdateElement data) {
			this.data = data;
			
			tvCityname.setText(data.cityName);

			tvSize.setText(FileUtil.getSizeStr(data.size));
			
			if(expandedCityIds.contains(data.cityID)){
				lyEditPanel.setVisibility(View.VISIBLE);
				ivExpande.setImageResource(R.drawable.ic_offline_u);
			}else{
				lyEditPanel.setVisibility(View.GONE);
				ivExpande.setImageResource(R.drawable.ic_offline_d);
			}
			
			if(data.status == MKOLUpdateElement.DOWNLOADING){
				tvStatus.setTextColor(context.getResources().getColor(R.color.green));
				tvStatus.setText("正在下载" + data.ratio + "%");
				btnDown.setText("暂停");
				btnDown.setEnabled(true);
				pbDownload.setProgress(data.ratio);

				if(data.ratio == 100){
					tvStatus.setText("已下载");
					btnDown.setText("更新");
					btnDown.setEnabled(false);
					pbDownload.setVisibility(View.GONE);
					if(list.size() > 1) {
						tvStatus2.setVisibility(View.GONE);
					}else {
						tvStatus2.setVisibility(View.VISIBLE);
						tvStatus2.setText("已下载");
					}
				}else {
					tvStatus2.setText("正在下载");
					pbDownload.setVisibility(View.VISIBLE);
				}

				if(list.size() == 0){
					if (list.get(posi).ratio != 100) {
						tvStatus2.setVisibility(View.VISIBLE);
//						Log.i("=======tvStatus2==111====","=="+data.cityName);
					} else {
						tvStatus2.setVisibility(View.GONE);
					}
				}else if(posi > 0) {
					if (list.get(posi - 1).ratio != 100) { //判断上一个是否已下载完(正在下载)
						tvStatus2.setVisibility(View.GONE);
					} else {
//						tvStatus2.setVisibility(View.VISIBLE);
//						Log.i("=======tvStatus2==222====",posi+"=="+data.cityName);
					}
				}
//				tvStatus2.setText("正在下载");
//				pbDownload.setVisibility(View.VISIBLE);
				btnDown.setVisibility(View.VISIBLE);
                tvStatus.setVisibility(View.VISIBLE);
				
			}else if(data.status == MKOLUpdateElement.SUSPENDED
					|| data.status >= MKOLUpdateElement.eOLDSMd5Error){
				//暂停、未错误，都当作暂停，可以继续下载
				pbDownload.setProgress(data.ratio);
				tvStatus.setTextColor(context.getResources().getColor(R.color.red3));
				tvStatus.setText("已暂停" + data.ratio + "%");
				btnDown.setText("继续");
				btnDown.setEnabled(true);
				pbDownload.setVisibility(View.VISIBLE);

				if(list.size() == 0){
					if (list.get(posi).ratio != 100) {
						tvStatus2.setVisibility(View.VISIBLE);
//						Log.i("=======tvStatus2==333====","=="+data.cityName);
					} else {
						tvStatus2.setVisibility(View.GONE);
					}
				}else if(posi > 0) {
					if (list.get(posi - 1).ratio != 100) { //判断上一个是否已下载完
						tvStatus2.setVisibility(View.GONE);
					} else {
						tvStatus2.setVisibility(View.VISIBLE);
//						Log.i("=======tvStatus2==444====","=="+data.cityName);
					}
				}
				tvStatus2.setText("正在下载");

			}else if(data.status == MKOLUpdateElement.FINISHED){
				tvStatus.setTextColor(context.getResources().getColor(R.color.green));
				tvStatus.setText("已下载");
				btnDown.setText("更新");
				btnDown.setEnabled(false);
				if(list.size() == 0){
					if (list.get(posi).ratio == 100) {
						tvStatus2.setVisibility(View.VISIBLE);
//						Log.i("=======tvStatus2==555====","=="+data.cityName);
					} else {
						tvStatus2.setVisibility(View.GONE);
					}
				}else if(posi > 0) {
					if (list.get(posi - 1).ratio == 100) { //判断上一个是否已下载完
						tvStatus2.setVisibility(View.GONE);
					} else {
						tvStatus2.setVisibility(View.VISIBLE);
//						Log.i("=======tvStatus2==666====","=="+data.cityName);
					}
				}
				tvStatus2.setText("已下载");
				btnDown.setVisibility(View.VISIBLE);
				pbDownload.setVisibility(View.GONE);
				tvStatus.setVisibility(View.VISIBLE);

			}else if(data.status == MKOLUpdateElement.FINISHED){
				if(data.update){
					tvStatus.setText("有更新");
					btnDown.setText("更新");
					btnDown.setEnabled(true);
					pbDownload.setProgress(data.ratio);
					
					btnDown.setVisibility(View.VISIBLE);
	                pbDownload.setVisibility(View.VISIBLE);
	                tvStatus.setVisibility(View.VISIBLE);
	                
				}else{
					btnDown.setVisibility(View.GONE);
	                pbDownload.setVisibility(View.GONE);
	                tvStatus.setVisibility(View.GONE);
				}
				
			}else if(data.status == MKOLUpdateElement.SUSPENDED
					|| data.status == MKOLUpdateElement.UNDEFINED
					|| data.status >= MKOLUpdateElement.eOLDSMd5Error){

				//暂停、未知、错误，都是继续下载
				tvStatus.setText("暂停");
				btnDown.setText("继续");
				btnDown.setEnabled(true);
				pbDownload.setProgress(data.ratio);
				
				btnDown.setVisibility(View.VISIBLE);
                pbDownload.setVisibility(View.VISIBLE);
                tvStatus.setVisibility(View.VISIBLE);
				
			}else if(data.status == MKOLUpdateElement.WAITING){
//				if(posi > 0) {
//					if (list.get(posi - 1).ratio == 100) { //判断上一个是否已下载完
//						tvStatus2.setVisibility(View.GONE);
//					} else {
//						tvStatus2.setVisibility(View.VISIBLE);
//						Log.i("=======tvStatus2==777====",posi+"=="+data.cityName);
//					}
//				}

				tvStatus.setTextColor(context.getResources().getColor(R.color.green));
				tvStatus.setText("等待下载");
				btnDown.setText("暂停");
				btnDown.setEnabled(true);
				pbDownload.setProgress(data.ratio);
				
				btnDown.setVisibility(View.VISIBLE);
                pbDownload.setVisibility(View.VISIBLE);
                tvStatus.setVisibility(View.VISIBLE);
				
			}else{
				btnDown.setVisibility(View.GONE);
                pbDownload.setVisibility(View.GONE);
                tvStatus.setVisibility(View.GONE);
			}
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.lyCityInfo:
				if(lyEditPanel.getVisibility() == View.VISIBLE){
					lyEditPanel.setVisibility(View.GONE);
					ivExpande.setImageResource(R.drawable.ic_offline_d);
					expandedCityIds.remove(data.cityID);
					
				}else{
					lyEditPanel.setVisibility(View.VISIBLE);
					ivExpande.setImageResource(R.drawable.ic_offline_u);
					expandedCityIds.add(data.cityID);
				}
				break;
				
			case R.id.btnDown:
				if(data.status == MKOLUpdateElement.DOWNLOADING
						|| data.status == MKOLUpdateElement.WAITING){
					//暂停
					int id = data.cityID;
					if(id > 0){
						mOffline.pause(id);
						data.status = MKOLUpdateElement.SUSPENDED;
						if(listener != null){
							listener.statusChanged(data, false);
						}
					}
					
				}else{
					//继续or更新
					if(data.update){
						//先删除,直接start貌似不行
						mOffline.remove(data.cityID);
						data.status = MKOLUpdateElement.UNDEFINED;
						if(listener != null){
							listener.statusChanged(data, true);
						}
						
						//再下载
						mOffline.start(data.cityID);
						
					}else{
						int id = data.cityID;
						if(id > 0){
							mOffline.start(id);
							data.status = MKOLUpdateElement.WAITING;
							if(listener != null){
//								listener.statusChanged(data, false);
							}
						}
					}
					
				}
				break;
				
			case R.id.btnRemove:
				AlertDialog dialog = new AlertDialog.Builder(context)
						.setTitle("提示")
						.setMessage("离线地图为您节省流量，删除后无法恢复，确定要删除？")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										mOffline.remove(data.cityID);
										data.status = MKOLUpdateElement.UNDEFINED;
										if(listener != null){
											listener.statusChanged(data, true);
										}
										
										dialog.dismiss();
									}
								})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										dialog.dismiss();
									}
								}).create();
				dialog.show();
				break;
			default:
				break;
			}
		}
    }
}
