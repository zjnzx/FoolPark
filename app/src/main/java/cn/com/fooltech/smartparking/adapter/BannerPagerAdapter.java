package cn.com.fooltech.smartparking.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.activity.ActivityDetailActivity;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.bean.BannerInfo;
import cn.com.fooltech.smartparking.cache.BitmapCache;
import cn.com.fooltech.smartparking.offlinemap.utils.ToastUtil;
import cn.com.fooltech.smartparking.utils.ToastUtils;
import cn.com.fooltech.smartparking.utils.cache.MyBitmapUtils;

/**
 * Created by YY on 2016/7/14.
 */
public class BannerPagerAdapter extends PagerAdapter {
    private Context context;
    private List<BannerInfo> list;
    private LayoutInflater inflater;
    private MyBitmapUtils bitmapUtils;
    public BannerPagerAdapter(Context context, List<BannerInfo> list){
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
        bitmapUtils = new MyBitmapUtils();

    }
    @Override
    public int getCount() {
        return list.size() == 0 ? 0 :Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view.equals(o);
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = inflater.inflate(R.layout.viewpager_banner_item, container, false);
        ImageView image = (ImageView) view.findViewById(R.id.iv_banner);
//        ImageLoader imageLoader = new ImageLoader(MyApplication.getHttpRequestQueue(), new BitmapCache());
        if(list.size() != 0) {
            position %= list.size();
            String imageUrl = list.get(position).getBannerImage();
            if(imageUrl.contains("http")){
                bitmapUtils.disPlay(image,imageUrl);
            }else{
                image.setImageResource(R.drawable.default1);
            }
        }
        //点击事件
        final int finalPosition = position;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(list.get(finalPosition).getLinkUrl().contains("http")) {
                    Intent intent = new Intent(context, ActivityDetailActivity.class);
                    intent.putExtra("linkUrl", list.get(finalPosition).getLinkUrl());
                    intent.putExtra("detail", "");
                    context.startActivity(intent);
                }
            }
        });

        container.addView(view);
        return view;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        container.removeView((View) object);
    }
}
