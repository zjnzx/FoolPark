package cn.com.fooltech.smartparking.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.application.MyApplication;

/**
 * Created by YY on 2016/12/22.
 */
public class GuideUtils {
    private static GuideUtils instance = null;

    public static GuideUtils getInstance() {
        synchronized (GuideUtils.class) {
            if (null == instance) {
                instance = new GuideUtils();
            }
        }
        return instance;
    }

    //显示引导图片
    public static void addGuideImage(final Activity context, View view, int guideResourceId, final String flag) {
        if (view == null)
            return;
        boolean isFirst = (boolean) SPUtils.get(context,flag,true);//是否第一次使用
        if (!isFirst) {
            // 有过功能引导，就跳出
            return;
        }
        ViewParent viewParent = view.getParent();
        if (viewParent instanceof FrameLayout) {
            final FrameLayout frameLayout = (FrameLayout) viewParent;
            if (guideResourceId != 0) {
                // 设置了引导图片
                final ImageView guideImage = new ImageView(context);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                guideImage.setLayoutParams(params);
                guideImage.setScaleType(ImageView.ScaleType.FIT_XY);
                guideImage.setImageResource(guideResourceId);
                //添加动画
                Animation animation = AnimationUtils.loadAnimation(context,R.anim.fade_in);
                guideImage.startAnimation(animation);
                guideImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Animation animation = AnimationUtils.loadAnimation(context,R.anim.fade_out);
                        guideImage.startAnimation(animation);
                        //删除引导图片
                        frameLayout.removeView(guideImage);
                        //保存记录
                        SPUtils.put(context,flag, false);
                    }
                });

                frameLayout.addView(guideImage);// 添加引导图片

            }
        }
    }
}
