package cn.com.fooltech.smartparking.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.activity.GuideActivity;
import cn.com.fooltech.smartparking.activity.IndexActivity;
import cn.com.fooltech.smartparking.activity.LoginActivity;
import cn.com.fooltech.smartparking.bean.BindCarInfo;
import cn.com.fooltech.smartparking.common.Common;

/**
 * Created by YY on 2016/7/7.
 */
public class GuideAdapter extends PagerAdapter {
    private int[] images;
    private GuideActivity context;
    private LayoutInflater inflater;
    public GuideAdapter(int[] images,GuideActivity context){
        this.images = images;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public Object instantiateItem(final ViewGroup container, int position) {
        View view = inflater.inflate(R.layout.viewpager_guide_item, container, false);
        ImageView image = (ImageView) view.findViewById(R.id.image_guide);
        TextView tvUse = (TextView) view.findViewById(R.id.start_use);
        image.setImageResource(images[position]);
        if(position == images.length - 1){
            tvUse.setVisibility(View.VISIBLE);
        }else {
            tvUse.setVisibility(View.GONE);
        }
        tvUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Common.clearData(context);
                context.startActivity(new Intent(context, IndexActivity.class));
                context.finish();
            }
        });

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        container.removeView(images[position]);
    }
}
