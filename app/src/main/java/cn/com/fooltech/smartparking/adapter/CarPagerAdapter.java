package cn.com.fooltech.smartparking.adapter;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;


import java.util.List;

import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.activity.CarAddActivity;
import cn.com.fooltech.smartparking.activity.IndexActivity;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.bean.BindCarInfo;
import cn.com.fooltech.smartparking.common.Common;

/**
 * Created by YY on 2016/7/14.
 */
public class CarPagerAdapter extends PagerAdapter {
    private IndexActivity context;
    public List<BindCarInfo> list;
    private LayoutInflater inflater;
    private int cars[] = new int[]{R.drawable.car_add1,R.drawable.car_add2,R.drawable.car_add3};
    public CarPagerAdapter(IndexActivity context, List<BindCarInfo> list){
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return list == null ? 0 : list.size() == 4 ? list.size() - 1 : list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view.equals(o);
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if(list.size() != 0) {
            position %= list.size();
        }
        View view = inflater.inflate(R.layout.viewpager_car_item, container, false);
        ImageView image = (ImageView) view.findViewById(R.id.iv_car);
        Button btnPlateNum = (Button) view.findViewById(R.id.plate_num_show2);
        BindCarInfo carInfo = list.get(position);

        setCarShow(carInfo,btnPlateNum,image,position);

        container.addView(view);
        return view;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        container.removeView((View) object);
    }

    /**
     * 设置车辆显示
     * @param info
     * @param btnPlateNum
     * @param image
     * @param position
     */
    private void setCarShow(BindCarInfo info,Button btnPlateNum,ImageView image,int position){
        int len = list.size();
        image.setBackgroundResource(cars[info.getImageIndex()]);
        if(len == 1){ //绑定了0辆车
            setButton(btnPlateNum);
        }else if(len == 2){//绑定了1辆车
            if(position == list.size() - 1){
                setButton(btnPlateNum);
            }else {
                btnPlateNum.setText(info.getPlateNumber());
            }
        }else if(len == 3){//绑定了2辆车或3辆车
            if(position == list.size() - 1 && list.get(len - 1).getPlateNumber() == null){
                setButton(btnPlateNum);
            }else {
                btnPlateNum.setText(info.getPlateNumber());
            }
        }else if(len == 4){//当绑定了第三辆车后
            list.remove(len - 1);
            if(position == list.size() - 1 && list.get(len - 1).getPlateNumber() == null){
                setButton(btnPlateNum);
            }else {
                btnPlateNum.setText(info.getPlateNumber());
            }
        }
    }

    private void setButton(Button btnPlateNum){
        btnPlateNum.setText("添加车辆");
        btnPlateNum.setTextColor(context.getResources().getColor(R.color.white));
        btnPlateNum.setBackgroundResource(R.drawable.selector_btn_click);
        btnPlateNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Common.isLogin(context)) return;
                MyApplication.isUpdateCar = false;
                context.startActivity(new Intent(context, CarAddActivity.class));
            }
        });
    }
}
