package cn.com.fooltech.smartparking.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by YY on 2016/7/14.
 * 自定义ScrollView,解决scrollview嵌套viewpager滑动问题
 */
public class MyScrollView extends ScrollView {
    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 添加了一个手势选择器
        gestureDetector=new GestureDetector(new Yscroll() );
        setFadingEdgeLength(0);
    }

    GestureDetector gestureDetector;
    OnTouchListener onTouchListener;
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev) && gestureDetector.onTouchEvent(ev);
    }
    class Yscroll extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            //控制手指滑动的距离
            if (Math.abs(distanceY)>=Math.abs(distanceX)) {
                return true;
            }
            return false;
        }

    }}
