package cn.com.fooltech.smartparking.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.activity.ActivityDetailActivity;
import cn.com.fooltech.smartparking.bean.BannerInfo;
import cn.com.fooltech.smartparking.utils.ToastUtils;
import cn.com.fooltech.smartparking.utils.cache.MyBitmapUtils;

/**
 * Created by YY on 2016/7/14.
 */
public class BannerPagerParkAdapter extends PagerAdapter {
    private Context context;
    private List<BannerInfo> list;
    private LayoutInflater inflater;
    private MyBitmapUtils bitmapUtils;
    public BannerPagerParkAdapter(Context context, List<BannerInfo> list){
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
        bitmapUtils = new MyBitmapUtils();

    }
    @Override
    public int getCount() {
        return list == null || list.size() == 0 ? 1 : Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view.equals(o);
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = inflater.inflate(R.layout.viewpager_banner_item, container, false);
        ImageView image = (ImageView) view.findViewById(R.id.iv_banner);
        if(list == null || list.size() == 0) {
            image.setImageResource(R.drawable.image);
            container.addView(view);
            return view;
        }else {
            position %= list.size();
            String imageUrl = list.get(position).getBannerImage();
            if(imageUrl.contains("http")){
                bitmapUtils.disPlay(image,imageUrl);
            }else{
                image.setImageResource(R.drawable.default1);
            }
            container.addView(view);
        }
        return view;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        container.removeView((View) object);
    }
}
