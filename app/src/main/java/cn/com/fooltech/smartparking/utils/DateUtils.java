package cn.com.fooltech.smartparking.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by YY on 2016/7/20.
 */
public class DateUtils {
    public static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static SimpleDateFormat format2 = new SimpleDateFormat("yyyy.MM.dd");
    public static SimpleDateFormat format3 = new SimpleDateFormat("yyyy/MM/dd");
    public static SimpleDateFormat format4 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    public static SimpleDateFormat format5 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    public static SimpleDateFormat format6 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat format7 = new SimpleDateFormat("HH:mm");
    public static SimpleDateFormat format8 = new SimpleDateFormat("MM-dd HH:mm");
    public static SimpleDateFormat format9 = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    public static String getTime(Date date) {
        if(date == null) return "";
        return format.format(date);
    }
    public static String getTime1(Date date) {
        if(date == null) return "";
        return format4.format(date);
    }
    public static String getTime2(Date date) {
        if(date == null) return "";
        return format5.format(date);
    }
    public static String getTime3(Date date) {
        if(date == null) return "";
        return format1.format(date);
    }
    public static String getDate2(Date date) {
        if(date == null) return "";
        return format2.format(date);
    }

    public static String getDate3(Date date) {
        if(date == null) return "";
        return format3.format(date);
    }
    public static String getTime4(Date date) {
        String time = format7.format(date);
        return time;
    }
    public static String getDate() {
        Date date = new Date();
        if(date == null) return "";
        return format8.format(date);
    }
    public static Date strToDate(String str) {
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 获取毫秒数
     * @param str
     * @return
     */
    public static long getMillions(String str){
        if(("").equals(str)) return 0;
        long million = 0;
        try {
            million = format6.parse(str).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return million;
    }

    /**
     * 当前日期往前推一个月
     * @param date
     * @return
     */
    public static Date getLastDate(Date date){
        date.setMonth(date.getMonth() - 1);
        return date;
    }
    /**
     * 当前日期往后推一个月
     * @param date
     * @return
     */
    public static Date getNextDate(Date date){
        date.setMonth(date.getMonth() + 1);
        return date;
    }

    public static String getTime() {
        Date date = new Date();
        String time = format7.format(date);
        return time;
    }


    /**
     * 0 ：source和traget时间相同
     *  -1 ：source比traget时间大
     *  1：source比traget时间小
     * @param source
     * @param traget
     * @param type
     * @return
     * @throws Exception
     */
    public static int DateCompare(String source, String traget, String type) {
        int ret = 2;
        SimpleDateFormat format = new SimpleDateFormat(type);
        Date sourcedate = null;
        try {
            sourcedate = format.parse(source);
            Date tragetdate = format.parse(traget);
            ret = sourcedate.compareTo(tragetdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ret;
    }


    /**
     * 计算时长
     * @param startTime
     * @return
     */
    public static long dateDiff(String startTime,String endTime) {
        // 按照传入的格式生成一个simpledateformate对象
        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
        long nh = 1000 * 60 * 60;// 一小时的毫秒数
        long nm = 1000 * 60;// 一分钟的毫秒数
        long ns = 1000;// 一秒钟的毫秒数
        long duration = 0;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        // 获得两个时间的毫秒时间差异
        try {
            long endMillion = format9.parse(endTime).getTime();
            long startMillion = format9.parse(startTime).getTime();
            duration = endMillion - startMillion;
            day = duration / nd;// 计算差多少天
            hour = duration % nd / nh + day * 24;// 计算差多少小时
            min = duration % nd % nh / nm + day * 24 * 60;// 计算差多少分钟
            sec = duration % nd % nh % nm / ns;// 计算差多少秒
            // 输出结果
//            System.out.println("时间相差：" + day + "天" + (hour - day * 24) + "小时"
//                    + (min - day * 24 * 60) + "分钟" + sec + "秒。");
            long min2 = min - day * 24 * 60;
            String h = hour < 10 ? ("0" + hour) : (hour + "");
            String m = min2 < 10 ? ("0" + min2) : (min2 + "");
            String s = sec < 10 ? ("0" + sec) : (sec + "");
//            duration = h + ":" + m + ":" + s;
//            System.out.println("hour=" + hour + ",min=" + min);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return duration;
    }

}
