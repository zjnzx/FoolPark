/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年9月8日 上午1:25:10   
 * @version 1.0   
 */
package cn.com.fooltech.smartparking.offlinemap.interfaces;

import com.baidu.mapapi.map.offline.MKOLUpdateElement;

import cn.com.fooltech.smartparking.offlinemap.bean.OfflineMapItem;

public interface OnOfflineItemStatusChangeListener {
	public void statusChanged(MKOLUpdateElement item, boolean removed);
}
