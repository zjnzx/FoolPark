/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年9月7日 下午4:16:41   
 * @version 1.0   
 */
package cn.com.fooltech.smartparking.offlinemap.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

public abstract class ArrayListAdapter<T> extends BaseAdapter {
	protected Context mContext;
	protected List<T> mDatas;
    protected LayoutInflater inflater;

	public ArrayListAdapter(Context context) {
		this.mContext = context;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		if(mDatas == null){
			return 0;
		}
		return mDatas.size();
	}

	@Override
	public Object getItem(int position) {
		if(mDatas == null || position < 0 || position > mDatas.size()-1){
			return null;
		}
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public void setDatas(List<T> ds) {
		this.mDatas = ds;
		notifyDataSetChanged();
	}
	
	public void setArrayDatas(T[] array) {
		List<T> list = new ArrayList<T>();
		
		if(array != null && array.length > 0){
			for (T t : array) {
				list.add(t);
			}
		}
		setDatas(list);
	}
	
	public void setItem(T one, int pos) {
		if (mDatas == null || pos < 0 || pos >= mDatas.size()) {
			return;
			
		} else {
			synchronized (mDatas) {
				mDatas.set(pos, one);
			}
		}
		
		notifyDataSetChanged();
	}
	
	public void addItems(List<T> list) {
		if (list == null || list.isEmpty())
			return;
		
		if(mDatas == null){
			mDatas = new ArrayList<T>();
		}
		
		synchronized (mDatas) {
			this.mDatas.addAll(list);
		}
		notifyDataSetChanged();
	}
	
	public void addItems(T[] array) {
		if (array == null || array.length < 1)
			return;
		
		if(mDatas == null){
			mDatas = new ArrayList<T>();
		}
		
		synchronized (mDatas) {
			for (T value : array) {
				this.mDatas.add(value);
			}
		}
		notifyDataSetChanged();
	}
	
	public void addItemsPre(List<T> list) {
		if (list == null || list.isEmpty())
			return;
		
		if(mDatas == null){
			mDatas = new ArrayList<T>();
		}
		
		synchronized (mDatas) {
			this.mDatas.addAll(0, list);
		}
		notifyDataSetChanged();
	}
	
	public void addItem(T item) {
		if (item == null)
			return;
		
		if(mDatas == null){
			mDatas = new ArrayList<T>();
		}
		
		synchronized (mDatas) {
			this.mDatas.add(item);
		}
		
		notifyDataSetChanged();
	}
	
	public void removeItem(T item) {
		if (item == null || mDatas == null)
			return;
		
		synchronized (mDatas) {
			this.mDatas.remove(item);
		}
		notifyDataSetChanged();
	}
	
	public void removeItem(int pos) {
		if (mDatas == null || pos < 0 || pos >= mDatas.size()) {
			return;
			
		} else {
			synchronized (mDatas) {
				this.mDatas.remove(pos);
			}
		}
		
		notifyDataSetChanged();
	}
	
	public void removeAll() {
		if (mDatas != null) {
			synchronized (mDatas) {
				mDatas.clear();
			}
		}
		
		notifyDataSetChanged();
	}

}
