package cn.com.fooltech.smartparking.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import cn.com.fooltech.smartparking.R;

/**
 * Created by YY on 2016/7/7.
 */
public class VoiceAdapter extends BaseAdapter {
    private Context context;
    public List<String> list;
    private LayoutInflater inflater;
    public VoiceAdapter(Context context, List<String> list){
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
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        String content = list.get(i);
        ViewHolder holder = null;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.listview_voice_item,null);
            holder = new ViewHolder();
            holder.tvContent = (TextView) convertView.findViewById(R.id.voice_text1);
            holder.tvText = (TextView) convertView.findViewById(R.id.voice_text2);
            holder.layout = (RelativeLayout) convertView.findViewById(R.id.lay_voice2);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        if(!("").equals(content)){
            holder.tvContent.setText(content);
            holder.tvText.setVisibility(View.GONE);
            holder.tvContent.setTextColor(context.getResources().getColor(R.color.white));
            holder.layout.setBackgroundResource(R.drawable.bg_voice_green);
        }else {
            holder.tvText.setVisibility(View.VISIBLE);
            holder.tvContent.setText("抱歉,没有听清");
            holder.tvText.setText("尝试提高音量,语速保持适中");
            holder.tvContent.setTextColor(context.getResources().getColor(R.color.text));
            holder.layout.setBackgroundResource(R.drawable.bg_voice_gray);
        }
        return convertView;
    }

    class ViewHolder{
        TextView tvContent,tvText;
        RelativeLayout layout;
    }
}
