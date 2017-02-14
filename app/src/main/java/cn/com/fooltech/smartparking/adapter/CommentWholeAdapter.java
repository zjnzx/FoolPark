package cn.com.fooltech.smartparking.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.activity.CardBuyActivity;
import cn.com.fooltech.smartparking.activity.CardBuyActivity2;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.bean.CommentInfo;
import cn.com.fooltech.smartparking.bean.ParkInfo;
import cn.com.fooltech.smartparking.cache.BitmapCache;
import cn.com.fooltech.smartparking.common.Urls;
import cn.com.fooltech.smartparking.utils.ErrorUtils;
import cn.com.fooltech.smartparking.utils.HttpUtils;
import cn.com.fooltech.smartparking.utils.JsonUtils;
import cn.com.fooltech.smartparking.utils.SPUtils;
import cn.com.fooltech.smartparking.utils.ToastUtils;
import cn.com.fooltech.smartparking.utils.Utils;

/**
 * Created by YY on 2016/7/7.
 */
public class CommentWholeAdapter extends BaseAdapter {
    private Context context;
    private List<CommentInfo> list;
    private LayoutInflater inflater;
    private long commentId;
    private TextView tvCount;
    public CommentWholeAdapter(Context context, List<CommentInfo> list){
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
        final CommentInfo info = (CommentInfo) list.get(i);
        ViewHolder holder = null;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.listview_comment_item,null);
            holder = new ViewHolder();
            holder.ivUserHead = (ImageView) convertView.findViewById(R.id.user_image_show);
            holder.tvUserNick = (TextView) convertView.findViewById(R.id.user_nick_show);
            holder.tvTime = (TextView) convertView.findViewById(R.id.comment_time);
            holder.tvContent = (TextView) convertView.findViewById(R.id.comment_content);
            holder.tvSupportCount = (TextView) convertView.findViewById(R.id.comment_support_count);
            holder.ratingBar = (RatingBar) convertView.findViewById(R.id.comment_grade);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        ImageLoader imageLoader = new ImageLoader(MyApplication.getHttpRequestQueue(), new BitmapCache());
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(holder.ivUserHead, R.drawable.default_user, R.drawable.default_user);
        if(info.getAvatarUrl().contains("http")) {
            imageLoader.get(info.getAvatarUrl(), listener);
//            Log.e("=======listener====","-----------"+i);
        }

        holder.tvSupportCount.setEnabled((boolean)SPUtils.getData(context,info.getCommentId() + "",true));
        holder.tvUserNick.setText(info.getNickname());
        holder.tvTime.setText(info.getSubmitTime());
        holder.tvContent.setText(info.getParkComment());
        holder.tvSupportCount.setText(info.getSupportCount() + " 有用");
        holder.ratingBar.setRating(info.getParkLevel());

        final ViewHolder finalHolder = holder;
        holder.tvSupportCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentId = info.getCommentId();
                tvCount = finalHolder.tvSupportCount;
                supportComment(info);
            }
        });
        return convertView;
    }

    class ViewHolder{
        TextView tvUserNick,tvTime,tvSupportCount,tvContent;
        ImageView ivUserHead;
        RatingBar ratingBar;
    }

    /**
     * 支持评论
     * @param info
     */
    private void supportComment(CommentInfo info) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userId", SPUtils.get(context, "userId", new Long(0l)));
        paramMap.put("token", SPUtils.get(context, "token", ""));
        paramMap.put("parkId", info.getParkId());
        paramMap.put("commentId", info.getCommentId());
        HttpUtils.sendHttpPostRequest(Urls.URL_SUPPORT_COMMENT, handlerSupport, paramMap, context);
    }

    Handler handlerSupport = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.strToInt(msg.obj.toString()) == MyApplication.ERROR_EXCE) {
                ToastUtils.showShort(context, context.getString(R.string.net_socket_time));
            } else {
                Map<String,Object> map = JsonUtils.jsonToMap(msg.obj.toString());
                if(map != null && map.size() > 0) {
                    int code = (int) map.get("code");
                    if (code == 0) {
                        SPUtils.putData(context, commentId + "", false);
                        int count = (int) map.get("supportCount");
                        tvCount.setText(count + " 有用");
                        tvCount.setEnabled(false);
                    } else {
                        ErrorUtils.errorCode(context, code);
                    }
                }
            }
            super.handleMessage(msg);
        }
    };
}
