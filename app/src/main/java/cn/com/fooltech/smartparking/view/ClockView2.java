package cn.com.fooltech.smartparking.view;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.math.BigDecimal;

import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.utils.Utils;

/**
 * @author : Tanck
 * @Description :倒计时
 * @date 8/12/2015
 */
public class ClockView2 extends TextView {

    private boolean mTickerStopped;
    private Handler mHandler;
    private Runnable mTicker;
    private long endTime;
    private TextView textView;
    private Button button;
    private int price;
    private static final long MINUTE = 15 * 60 * 1000;//15分钟

    private ClockListener mClockListener;


    public void setClockListener(ClockListener clockListener) {
        this.mClockListener = clockListener;
    }

    /**
     * Clock end time from now on.
     *
     * @param endTime
     */
    public void  setEndTime(long endTime, TextView textView, Button button,int price) {
        this.endTime = endTime;
        this.textView = textView;
        this.button = button;
        this.price = price;
    }


    public ClockView2(Context context) {
        this(context, null);
    }

    public ClockView2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onAttachedToWindow() {

        mTickerStopped = false;
        super.onAttachedToWindow();
        mHandler = new Handler();

        getVisibility();
        /**
         * requests a tick on the next hard-second boundary
         */
        mTicker = new Runnable() {
            public void run() {
                endTime++ ;
                if (endTime == 0) {
                    setText("停车时长: 00:00:00");
                    onDetachedFromWindow();
                    if (null != mClockListener)
                        mClockListener.timeEnd();
                } else if (endTime < 0) {
                    setText("停车时长: 00:00:00");
                } else {
                    setText(dealTime(endTime));
                }
                invalidate();
                long now = SystemClock.uptimeMillis();
                long next = now + (1000 - now % 1000);//
                mHandler.postAtTime(mTicker, next);
            }
        };
        mTicker.run();

    }

    /**
     * deal time string
     *
     * @param time
     * @return
     */
    public String dealTime(long time) {
        StringBuffer returnString = new StringBuffer();
        long day = time / (24 * 60 * 60);
        long hours = (time % (24 * 60 * 60)) / (60 * 60);
        long minutes = ((time % (24 * 60 * 60)) % (60 * 60)) / 60;
        long second = ((time % (24 * 60 * 60)) % (60 * 60)) % 60;
//        String dayStr = String.valueOf(day);
        String hoursStr = timeStrFormat(String.valueOf(hours + day * 24));
        String minutesStr = timeStrFormat(String.valueOf(minutes));
        String secondStr = timeStrFormat(String.valueOf(second));
        returnString.append("停车时长: ").append(hoursStr).append(":").append(minutesStr)
                .append(":").append(secondStr);
//        double hour1 = (hours * 3600 + minutes * 60 + second) / 3600;
        if(textView != null) {
            double hour2 = Math.ceil(((hours + day * 24) * 3600 + minutes * 60 + second) / 3600);
            int minute2 = Utils.strToInt(minutesStr);
            if(hour2 == 0 && minute2 <= 15){
                button.setText("确定");
                MyApplication.isFree = true;
            }else if(hour2 == 0 && minute2 > 15){
//                hour2 += 1;
                button.setText("付款");
                MyApplication.isFree = false;
            }else if(hour2 != 0 && minute2 == 0){
                button.setText("付款");
                MyApplication.isFree = false;
            }else if(hour2 != 0 && minute2 > 0 && minute2 <= 30){
//                hour2 += 0.5;
                button.setText("付款");
                MyApplication.isFree = false;
            }else if(hour2 != 0 && minute2 > 30){
//                hour2 += 1;
                button.setText("付款");
                MyApplication.isFree = false;
            }
//            int planFee = (int) (price * hour2);
//            String content = "预计费用: " + planFee + "元";
//            Spannable spanText = new SpannableString(content);
//            spanText.setSpan(new AbsoluteSizeSpan(30,true), 6, content.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            textView.setText(spanText);
        }
//        returnString.append(minutesStr).append("分").append(secondStr).append("秒");
        return returnString.toString();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mTickerStopped = true;
    }


    /**
     *
     */
    public void changeTicker() {
        mTickerStopped = !mTickerStopped;
        if (!mTickerStopped) {
            mHandler.post(mTicker);
        }else{
            mHandler.removeCallbacks(mTicker);
        }
    }

    /**
     * format time
     *
     * @param timeStr
     * @return
     */
    private static String timeStrFormat(String timeStr) {
        switch (timeStr.length()) {
            case 1:
                timeStr = "0" + timeStr;
                break;
        }
        return timeStr;
    }


    public interface ClockListener {
        void timeEnd();

        void remainFiveMinutes();
    }
}
