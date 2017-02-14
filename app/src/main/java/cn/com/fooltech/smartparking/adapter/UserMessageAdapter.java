package cn.com.fooltech.smartparking.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.bean.MessageInfo;
import cn.com.fooltech.smartparking.cache.BitmapCache;
import cn.com.fooltech.smartparking.common.Urls;
import cn.com.fooltech.smartparking.utils.DateUtils;
import cn.com.fooltech.smartparking.utils.ErrorUtils;
import cn.com.fooltech.smartparking.utils.HttpUtils;
import cn.com.fooltech.smartparking.utils.JsonUtils;
import cn.com.fooltech.smartparking.utils.SPUtils;
import cn.com.fooltech.smartparking.utils.ToastUtils;
import cn.com.fooltech.smartparking.utils.Utils;

/**
 * Created by YY on 2016/7/7.
 */
public class UserMessageAdapter extends BaseAdapter {
    private Context context;
    public List<MessageInfo> list;
    private LayoutInflater inflater;
    private int position;
    public UserMessageAdapter(Context context, List<MessageInfo> list){
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
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        final MessageInfo info = (MessageInfo) list.get(i);
        ViewHolder holder = null;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.listview_user_message_item,null);
            holder = new ViewHolder();
            holder.tvMessageText = (TextView) convertView.findViewById(R.id.message_text2);
            holder.ivUserImg = (NetworkImageView) convertView.findViewById(R.id.user_image);
            holder.tvReject = (TextView) convertView.findViewById(R.id.reject);
            holder.tvAgree = (TextView) convertView.findViewById(R.id.agree);
            holder.tvSendTime = (TextView) convertView.findViewById(R.id.send_time2);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvMessageText.setText(info.getMessageText());
        holder.tvSendTime.setText(DateUtils.getTime1(info.getSendTime()));
        if(info.getExtraInfo1() != null) { //申请消息
            if (info.getAvatarUrl().contains("http")) {
                ImageLoader imageLoader = new ImageLoader(MyApplication.getHttpRequestQueue(), new BitmapCache());
                holder.ivUserImg.setDefaultImageResId(R.drawable.default_user);
                holder.ivUserImg.setErrorImageResId(R.drawable.default_user);
                holder.ivUserImg.setImageUrl(info.getAvatarUrl(), imageLoader);
            } else {
                holder.ivUserImg.setDefaultImageResId(R.drawable.default_user);
            }
            holder.tvAgree.setVisibility(View.VISIBLE);
            holder.tvReject.setVisibility(View.VISIBLE);
        }else{//拒绝和同意的消息
            holder.ivUserImg.setBackgroundResource(R.drawable.msg_user_result);
            holder.tvAgree.setVisibility(View.GONE);
            holder.tvReject.setVisibility(View.GONE);
        }

        //拒绝
        holder.tvReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String,Object> paramMap = new HashMap<String,Object>();
                paramMap.put("userId", SPUtils.get(context, "userId", new Long(0l)));
                paramMap.put("token", SPUtils.get(context, "token", ""));
                paramMap.put("messageId", info.getMessageId());
                HttpUtils.sendHttpPostRequest(Urls.URL_REJECT_BIND, handlerReject, paramMap,context);

                position = i;
            }
        });
        //同意
        holder.tvAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String,Object> paramMap = new HashMap<String,Object>();
                paramMap.put("userId", SPUtils.get(context, "userId", new Long(0l)));
                paramMap.put("token", SPUtils.get(context, "token", ""));
                paramMap.put("messageId", info.getMessageId());
                HttpUtils.sendHttpPostRequest(Urls.URL_ALLOW_BIND, handlerAgree, paramMap,context);

                position = i;
            }
        });
        return convertView;
    }

    Handler handlerReject = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, "服务器出现异常");
            }else {
                int code = JsonUtils.getCodeResult(msg.obj.toString());
                if (code == 0) {
                    removeData(position);
                } else {
                    ErrorUtils.errorCode(context, code);
                }
            }
            super.handleMessage(msg);
        }
    };
    Handler handlerAgree = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, "服务器出现异常");
            }else {
                int code = JsonUtils.getCodeResult(msg.obj.toString());
                if (code == 0) {
                    removeData(position);
                } else {
                    ErrorUtils.errorCode(context, code);
                }
            }
            super.handleMessage(msg);
        }
    };

    class ViewHolder{
        TextView tvMessageText,tvSendTime,tvReject,tvAgree;
        NetworkImageView ivUserImg;
    }

}
