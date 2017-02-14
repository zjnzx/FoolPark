package cn.com.fooltech.smartparking.adapter;

import android.content.Context;
import android.content.Intent;
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
import cn.com.fooltech.smartparking.activity.ActivityDetailActivity;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.bean.ActivityInfo;
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
public class ActivityCenterAdapter extends BaseAdapter {
    private Context context;
    private List<ActivityInfo> list;
    private LayoutInflater inflater;
    private TextView support;
    private long activityId;
    public ActivityCenterAdapter(Context context, List<ActivityInfo> list){
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
        final ActivityInfo info = (ActivityInfo) list.get(i);
        ViewHolder holder = null;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.listview_activity_center_item,null);
            holder = new ViewHolder();
            holder.ivImg = (NetworkImageView) convertView.findViewById(R.id.act_img);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.act_title);
            holder.tvTime = (TextView) convertView.findViewById(R.id.publishtime);
            holder.tvSupport = (TextView) convertView.findViewById(R.id.support);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        if(info.getActivityImage().contains("http")) {
            ImageLoader imageLoader = new ImageLoader(MyApplication.getHttpRequestQueue(), new BitmapCache());
            holder.ivImg.setDefaultImageResId(R.drawable.default1);
            holder.ivImg.setErrorImageResId(R.drawable.default1);
            holder.ivImg.setImageUrl(info.getActivityImage(), imageLoader);
        }else{
            holder.ivImg.setDefaultImageResId(R.drawable.default1);
        }

        holder.tvSupport.setEnabled((boolean)SPUtils.getData(context,info.getActivityId() + "",true));

        holder.tvTitle.setText(info.getActivityTitle());
        holder.tvSupport.setText(info.getVisitCount() + "");
        holder.tvTime.setText(DateUtils.getTime3(info.getPublishTime()));
        final ViewHolder finalHolder = holder;
        //点赞
        holder.tvSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String,Object> paramMap = new HashMap<String,Object>();
                paramMap.put("activityId",info.getActivityId());
                HttpUtils.sendHttpPostRequest(Urls.URL_UPDATE_ACTIVITY_COUNT, handlerSupport, paramMap, context);
                support = finalHolder.tvSupport;
                activityId = info.getActivityId();
            }
        });

        //查看详情
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(info.getLinkUrl().contains("http")) {
                    Intent intent = new Intent(context, ActivityDetailActivity.class);
                    intent.putExtra("linkUrl", info.getLinkUrl());
                    intent.putExtra("title", info.getActivityTitle());
                    context.startActivity(intent);
                }
            }
        });


        return convertView;
    }

    class ViewHolder{
        TextView tvTitle,tvDetail,tvTime,tvSupport;
        NetworkImageView ivImg;
    }

    Handler handlerSupport = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, context.getString(R.string.net_socket_time));
            }else {
                Map<String,Object> map = JsonUtils.jsonToMap(msg.obj.toString());
                if(map != null && map.size() > 0) {
                    int code = (int) map.get("code");
                    if(code == 0) {
                        SPUtils.putData(context,activityId + "", false);
                        int count = (int) map.get("visitCount");
                        support.setText(count + "");
                        support.setEnabled(false);
                        return;
                    } else {
                        ErrorUtils.errorCode(context, code);
                    }
                }else {
                    ToastUtils.showShort(context, "服务器请求错误");
                }
            }
            super.handleMessage(msg);
        }
    };
}
