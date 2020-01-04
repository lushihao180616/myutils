package com.lushihao.myutils.time;

import com.lushihao.myutils.check.LSHCheckUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public final class LSHDateUtils {
    public static final String YYYY_MM_DD_HH_MM_SS1 = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYY_MM_DD_HH_MM_SS2 = "yyyy/MM/dd HH:mm:ss";
    public static final String YYYY_MM_DD1 = "yyyy-MM-dd";
    public static final String YYYY_MM_DD2 = "yyyy/MM/dd";
    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static final String YYYYMMDD = "yyyyMMdd";

    public static final Integer YEAR = 0;
    public static final Integer MONTH = 1;
    public static final Integer DATE = 2;
    public static final Integer HOUR_OF_DAY = 3;
    public static final Integer MINUTE = 4;
    public static final Integer SECOND = 5;
    public static final Integer DAY_OF_WEEK = 6;
    public static final Integer WEEK_OF_MONTH = 7;
    public static final Integer DAY_OF_YEAR = 8;
    public static final Integer WEEK_OF_YEAR = 9;

    /**
     * 判断字符串是否是符合指定格式的时间
     *
     * @param date   时间字符串
     * @param format 时间格式
     * @return 是否符合
     */
    public static boolean isDate(String date, String format) {
        try {
            DateFormat df = new SimpleDateFormat(format);
            df.parse(date);
            return true;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 日期转字符串
     *
     * @param date   日期
     * @param format 格式化的格式
     * @return
     */
    public static String date2String(Date date, String format) {
        DateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    /**
     * 字符串转日期
     *
     * @param str
     * @param format
     * @return
     * @throws ParseException
     */
    public static Date string2Date(String str, String format) throws ParseException {
        if (!isDate(str, format)) {
            return null;
        }
        DateFormat df = new SimpleDateFormat(format);
        return df.parse(str);
    }

    /**
     * 日期转map
     *
     * @param date
     * @return
     */
    public static Map<Integer, Integer> date2Map(Date date) {
        if (!LSHCheckUtils.valid(date)) {
            return null;
        }
        Calendar calendar = date2Calendar(date);
        return calendar2Map(calendar);
    }

    /**
     * 日期相减（年月日）,不分先后
     *
     * @param date1
     * @param date2
     * @param flag
     * @return
     */
    public static Integer dateSub(Date date1, Date date2, Integer flag) {
        //参数校验
        if (!LSHCheckUtils.valid(date1) || !LSHCheckUtils.valid(date2)) {
            return -1;
        }
        if (!(flag == YEAR || flag == MONTH || flag == DATE)) {
            return -1;
        }
        Date begin;//开始时间
        Date end;//结束时间
        if (date1.getTime() > date2.getTime()) {
            begin = date2;
            end = date1;
        } else {
            begin = date1;
            end = date2;
        }
        Map<Integer, Integer> map1 = date2Map(begin);
        Map<Integer, Integer> map2 = date2Map(end);

        Integer yearSub = map2.get(YEAR) - map1.get(YEAR);
        Integer monthSub = map2.get(MONTH) - map1.get(MONTH);
        Integer daySub = map2.get(DATE) - map1.get(DATE);
        if (flag == YEAR) {//年差
            if (monthSub > 0) {
                return yearSub + 1;
            } else if (monthSub == 0) {
                if (daySub > 0) {
                    return yearSub + 1;
                } else {
                    return yearSub;
                }
            } else {
                return yearSub;
            }
        } else if (flag == MONTH) {//月差
            if (daySub > 0) {
                return monthSub + yearSub * 12 + 1;
            } else {
                return monthSub + yearSub * 12;
            }
        } else if (flag == DATE) {//日差
            return (int) ((end.getTime() - begin.getTime()) / (1000 * 3600 * 24));
        }
        return -1;
    }

    /**
     * 日期转日历类
     *
     * @param date
     * @return
     */
    private static Calendar date2Calendar(Date date) {
        if (!LSHCheckUtils.valid(date)) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    /**
     * 日历转map
     *
     * @param calendar
     * @return
     */
    private static Map<Integer, Integer> calendar2Map(Calendar calendar) {
        if (!LSHCheckUtils.valid(calendar)) {
            return null;
        }
        Map<Integer, Integer> map = new HashMap<>();
        map.put(YEAR, calendar.get(Calendar.YEAR));
        map.put(MONTH, calendar.get(Calendar.MONTH) + 1);
        map.put(DATE, calendar.get(Calendar.DATE));
        map.put(HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
        map.put(MINUTE, calendar.get(Calendar.MINUTE));
        map.put(SECOND, calendar.get(Calendar.SECOND));
        map.put(DAY_OF_WEEK, calendar.get(Calendar.DAY_OF_WEEK));
        map.put(WEEK_OF_MONTH, calendar.get(Calendar.WEEK_OF_MONTH));
        map.put(DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR));
        map.put(WEEK_OF_YEAR, calendar.get(Calendar.WEEK_OF_YEAR));
        return map;
    }
}