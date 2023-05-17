package com.kee.common.core.utils;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.lang.management.ManagementFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * 时间工具类
 *
 * @author zms
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {
    public static String YYYY = "yyyy";

    public static String YYYY_MM = "yyyy-MM";

    public static String YYYY_MM_DD = "yyyy-MM-dd";

    public static String YY_MM_DD = "yy-MM-dd";

    public static String HH_MM = "HH:mm";

    public static String HH_MM_SS = "HH:mm:ss";

    public static String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    private static String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

    /**
     * 获取当前Date型日期
     *
     * @return Date() 当前日期
     */
    public static Date getNowDate() {
        return new Date();
    }

    /**
     * 获取当前Date型日期
     *
     * @return Date() 当前日期
     */
    public static Date getNow() {
        Date date = new Date();
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(YYYY_MM_DD);
            date = simpleDateFormat.parse(simpleDateFormat.format(new Date()));
        } catch (ParseException e) {
            e.getMessage();
        }
        return date;
    }

    /**
     * 获取一个午别的时间段
     *
     * @param type
     * @return
     */
    public static HashMap getStartAndEnd(String type) {
        HashMap hashMap = new HashMap();
        String dateStart;
        String dateEnd;
        switch (type) {
            case "上午":
                dateStart = "00:01:00";
                dateEnd = "12:00:00";
                hashMap.put("dateStart", dateStart);
                hashMap.put("dateEnd", dateEnd);
                break;
            case "下午":
                dateStart = "12:01:00";
                dateEnd = "18:00:00";
                hashMap.put("dateStart", dateStart);
                hashMap.put("dateEnd", dateEnd);
                break;
            case "晚上":
                dateStart = "18:01:00";
                dateEnd = "23:30:00";
                hashMap.put("dateStart", dateStart);
                hashMap.put("dateEnd", dateEnd);
                break;
        }
        return hashMap;
    }

    /**
     * 判断某个日期属于上午、下午、晚上
     *
     * @return Date() 当前日期
     */
    public static String getTime(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("HH");
        String str = df.format(date);
        int a = Integer.parseInt(str);
        if (a >= 0 && a <= 12) {
            return "上午";
        }
        if (a > 12 && a <= 18) {
            return "下午";
        }
        if (a > 18 && a <= 24) {
            return "晚上";
        }
        return "格式有误！";
    }


    /**
     * 获取当前Date型日期(当前日期向后推一个周)
     *
     * @return Date() 当前日期
     */
    public static Date getWeek() {
        Date date = new Date();
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DATE, 7);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(YYYY_MM_DD);
            date = simpleDateFormat.parse(simpleDateFormat.format(calendar.getTime()));
        } catch (ParseException e) {
            e.getMessage();
        }
        return date;
    }

    /**
     * 获取当前日期是星期几
     *
     * @param time
     * @return
     * @throws ParseException
     */
    public static String dateForWeek(String time) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        Date tmpDate = format.parse(time);

        Calendar cal = Calendar.getInstance();

        String[] weekDays = {"日", "一", "二", "三", "四", "五", "六"};

        try {

            cal.setTime(tmpDate);

        } catch (Exception e) {

            e.printStackTrace();

        }

        int w = cal.get(Calendar.DAY_OF_WEEK) - 1; // 指示一个星期中的某天。

        if (w < 0) {
            w = 0;
        }
        return weekDays[w];
    }

    /**
     * 获取当前日期, 默认格式为yyyy-MM-dd
     *
     * @return String
     */
    public static String getDate() {
        return dateTimeNow(YYYY_MM_DD);
    }

    public static final String getTime() {
        return dateTimeNow(YYYY_MM_DD_HH_MM_SS);
    }

    public static final String dateTimeNow() {
        return dateTimeNow(YYYYMMDDHHMMSS);
    }

    public static final String dateTimeNow(final String format) {
        return parseDateToStr(format, new Date());
    }

    public static final String dateTime(final Date date) {
        return parseDateToStr(YYYY_MM_DD, date);
    }

    public static final String parseDateToStr(final String format, final Date date) {
        return new SimpleDateFormat(format).format(date);
    }

    public static final Date dateTime(final String format, final String ts) {
        try {
            return new SimpleDateFormat(format).parse(ts);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 日期路径 即年/月/日 如2018/08/08
     */
    public static final String datePath() {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyy/MM/dd");
    }

    /**
     * 日期路径 即年/月/日 如20180808
     */
    public static final String dateTime() {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyyMMdd");
    }

    /**
     * 日期型字符串转化为日期 格式
     */
    public static Date parseDate(Object str) {
        if (str == null) {
            return null;
        }
        try {
            return parseDate(str.toString(), parsePatterns);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 获取服务器启动时间
     */
    public static Date getServerStartDate() {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(time);
    }

    /**
     * 计算两个时间差
     */
    public static String getDatePoor(Date endDate, Date nowDate) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟";
    }


    /**
     * 获取时间的年月日YY-MM-DD格式
     *
     * @param date 传入日期
     */
    public static String getYYMMDD(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(YY_MM_DD);
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD);
        try {
            String format = simpleDateFormat.format(sdf.parse(date));
            return format.replaceAll("-", " ") + " ";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
}
