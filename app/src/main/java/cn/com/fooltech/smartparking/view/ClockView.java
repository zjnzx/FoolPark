package cn.com.fooltech.smartparking.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import cn.com.fooltech.smartparking.R;

/**
 * @author : Tanck
 * @Description :倒计时
 * @date 8/12/2015
 */
public class ClockView extends TextView {

    private boolean mTickerStopped;
    private Handler mHandler;
    private Runnable mTicker;
    private long endTime;
    private View convertView;

    private ClockListener mClockListener;


    public void setClockListener(ClockListener clockListener) {
        this.mClockListener = clockListener;
    }

    /**
     * Clock end time from now on.
     *
     * @param endTime
     */
    public void setEndTime(long endTime,View view) {
        this.endTime = endTime;
        this.convertView = view;
    }


    public ClockView(Context context) {
        this(context, null);
    }

    public ClockView(Context context, AttributeSet attrs) {
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
                if (mTickerStopped) {
                    View lay = convertView.findViewById(R.id.lay_book2);
                    TextView textView = (TextView) convertView.findViewById(R.id.status_show);
                    lay.setVisibility(GONE);
                    textView.setText("已超时");
                    return;
                }
//                long currentTime = System.currentTimeMillis();
//                if (currentTime / 1000 == endTime / 1000 - 5 * 60 && null != mClockListener) {
//                    mClockListener.remainFiveMinutes();
//                }
//                long distanceTime = endTime - currentTime;
                endTime-- ;
                if (endTime == 0) {
                    setText("00分00秒");
                    onDetachedFromWindow();
                    if (null != mClockListener)
                        mClockListener.timeEnd();
                } else if (endTime < 0) {
                    setText("00分00秒");
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
    public static String dealTime(long time) {
        StringBuffer returnString = new StringBuffer();
//        long day = time / (24 * 60 * 60);
//        long hours = (time % (24 * 60 * 60)) / (60 * 60);
        long minutes = ((time % (24 * 60 * 60)) % (60 * 60)) / 60;
        long second = ((time % (24 * 60 * 60)) % (60 * 60)) % 60;
//        String dayStr = String.valueOf(day);
//        String hoursStr = timeStrFormat(String.valueOf(hours));
        String minutesStr = timeStrFormat(String.valueOf(minutes));
        String secondStr = timeStrFormat(String.valueOf(second));
//        returnString.append(hoursStr).append(":").append(minutesStr)
//                .append(":").append(secondStr);
        returnString.append(minutesStr).append("分").append(secondStr).append("秒");
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
