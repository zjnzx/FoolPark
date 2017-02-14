package cn.com.fooltech.smartparking.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.bean.MessageInfo;
import cn.com.fooltech.smartparking.utils.DateUtils;

/**
 * Created by YY on 2016/7/7.
 */
public class SysMessageAdapter extends BaseAdapter {
    private Context context;
    public List<MessageInfo> list;
    private LayoutInflater inflater;
    public SysMessageAdapter(Context context, List<MessageInfo> list){
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }
    public void removeData(int position){
        list.remove(position);
        notifyDataSetChanged();
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
        MessageInfo info = (MessageInfo) list.get(i);
        ViewHolder holder = null;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.listview_sys_message_item,null);
            holder = new ViewHolder();
            holder.tvMessageText = (TextView) convertView.findViewById(R.id.message_text);
            holder.ivImgType = (ImageView) convertView.findViewById(R.id.img_type);
            holder.tvSendTime = (TextView) convertView.findViewById(R.id.send_time);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvMessageText.setText(info.getMessageText());
        holder.tvSendTime.setText(DateUtils.getTime1(info.getSendTime()));
        setImage(info.getMessageType(),holder.ivImgType);
        return convertView;
    }

    class ViewHolder{
        TextView tvMessageText,tvSendTime;
        ImageView ivImgType;
    }

    private void setImage(int type,ImageView imageView){
        if(type == 1){
            imageView.setBackgroundResource(R.drawable.msg_sys_voucher);
        }else if(type == 2){
            imageView.setBackgroundResource(R.drawable.msg_sys_point);
        }else if(type == 3){
            imageView.setBackgroundResource(R.drawable.msg_sys_inform);
        }
    }

}
