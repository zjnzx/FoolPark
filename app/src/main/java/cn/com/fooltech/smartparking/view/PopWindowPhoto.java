package cn.com.fooltech.smartparking.view;

/**
 * Created by YY on 2016/7/20.
 */
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.common.Common;


public class PopWindowPhoto extends PopupWindow {


    private View mMenuView;
    private TextView tvTakePhoto,tvPhotoChoose,tvCancle;
    private WindowManager.LayoutParams lp;

    public PopWindowPhoto(final Activity context, OnClickListener itemsOnClick) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.pop_photo_layout, null);
        tvTakePhoto = (TextView) mMenuView.findViewById(R.id.take_photo);
        tvPhotoChoose = (TextView) mMenuView.findViewById(R.id.photo_choose);
        tvCancle = (TextView) mMenuView.findViewById(R.id.cancle);

        //设置背景透明
        Common.setShadow(context, 0.6f);

        //设置按钮监听
        tvTakePhoto.setOnClickListener(itemsOnClick);
        tvPhotoChoose.setOnClickListener(itemsOnClick);
        tvCancle.setOnClickListener(itemsOnClick);

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

                int height = mMenuView.findViewById(R.id.pop_photo_lay).getTop();
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
