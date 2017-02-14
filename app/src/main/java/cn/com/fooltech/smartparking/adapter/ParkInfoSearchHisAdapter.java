package cn.com.fooltech.smartparking.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.activity.ParkMapActivity;
import cn.com.fooltech.smartparking.bean.ParkInfo;
import cn.com.fooltech.smartparking.dao.SearchHisDao;
import cn.com.fooltech.smartparking.view.CustomTextView;

/**
 * Created by YY on 2016/7/7.
 */
public class ParkInfoSearchHisAdapter extends BaseAdapter {
    private ParkMapActivity context;
    private List<ParkInfo> list;
    private LayoutInflater inflater;
    public ParkInfoSearchHisAdapter(ParkMapActivity context, List<ParkInfo> list){
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }
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
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        final ParkInfo info = list.get(i);
        ViewHolder holder = null;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.listview_search_his_item,null);
            holder = new ViewHolder();
            holder.tvName = (TextView) convertView.findViewById(R.id.sear_name_his);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvName.setText(info.getParkName());

        //点击定位
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.locationSearch(info);
            }
        });
        return convertView;
    }

    class ViewHolder{
        TextView tvName;
    }
}
