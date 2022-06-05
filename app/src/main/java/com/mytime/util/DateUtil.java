package com.mytime.util;

import java.util.Calendar;
import java.util.TimeZone;

public class DateUtil {

    /**
     * 获取当天的零点时间戳
     *
     * @return 当天的零点时间戳
     */
    public static long getTodayStartTime() {
        //设置时区
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
    }
}
