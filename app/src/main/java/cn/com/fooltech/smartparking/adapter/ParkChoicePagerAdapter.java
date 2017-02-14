package cn.com.fooltech.smartparking.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cn.com.fooltech.smartparking.R;

/**
 * Created by YY on 2016/7/7.
 */
public class ParkChoicePagerAdapter extends PagerAdapter {
    private int[] images;
    private LayoutInflater inflater;
    public ParkChoicePagerAdapter(int[] images,Context context){
        this.images = images;
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
    public Object instantiateItem(ViewGroup container, int position) {
        View view = inflater.inflate(R.layout.viewpager_car_add_item, container, false);
        ImageView image = (ImageView) view.findViewById(R.id.image_car_add);
        TextView ivBind = (TextView) view.findViewById(R.id.isbind);
        image.setImageResource(images[position]);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        container.removeView(images[position]);
    }
}
