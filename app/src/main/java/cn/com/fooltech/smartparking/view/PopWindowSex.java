package cn.com.fooltech.smartparking.view;

/**
 * Created by YY on 2016/7/20.
 */
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.common.Common;

public class PopWindowSex extends PopupWindow {


    private View mMenuView;
    private RelativeLayout tvMan,tvWoman;
    private WindowManager.LayoutParams lp;

    public PopWindowSex(final Activity context, OnClickListener itemsOnClick,String sex) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.pop_sex_layout, null);
        tvMan = (RelativeLayout) mMenuView.findViewById(R.id.sex_man);
        tvWoman = (RelativeLayout) mMenuView.findViewById(R.id.sex_woman);
        ImageView ivMan = (ImageView) mMenuView.findViewById(R.id.image_man);
        ImageView ivWoman = (ImageView) mMenuView.findViewById(R.id.image_woman);
        if(sex.equals("男")){
            ivMan.setVisibility(View.VISIBLE);
            ivWoman.setVisibility(View.GONE);
        }else if(sex.equals("女")){
            ivWoman.setVisibility(View.VISIBLE);
            ivMan.setVisibility(View.GONE);
        }
        //设置背景透明
        Common.setShadow(context, 0.6f);

        //设置按钮监听
        tvMan.setOnClickListener(itemsOnClick);
        tvWoman.setOnClickListener(itemsOnClick);

        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.FILL_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.pop_sex_anim);
        //实例化一个ColorDrawable颜色为透明
        ColorDrawable dw = new ColorDrawable(Color.parseColor("#00000000"));
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = mMenuView.findViewById(R.id.pop_sex_lay).getTop();
                int y=(int) event.getY();
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(y<height){
                        dismiss();
                    }
                }
                return true;
            }
        });

    }

}
