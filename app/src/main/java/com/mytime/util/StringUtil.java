package com.mytime.util;


import android.util.Log;

import com.mytime.model.AppUsageDetailInfo;

import java.text.SimpleDateFormat;
import java.util.Date;


public class StringUtil {

    public static String appUsageInfoToString(AppUsageDetailInfo appUsageDetailInfo){
        String str = null;

        String appName = appUsageDetailInfo.getAppName();
        int labelType = appUsageDetailInfo.getLabelType();

        String beginningTime = appUsageDetailInfo.getBeginningTime();
        String endTime = appUsageDetailInfo.getEndTime();
        String lastUsedTime = appUsageDetailInfo.getLastUsedTime();
        int foregroundHours = appUsageDetailInfo.getForegroundHours();
        int appLaunchCount = appUsageDetailInfo.getAppLaunchCount();
        int launchCount = appUsageDetailInfo.getLaunchCount();

        str =   appName + ","
                + labelType + ','
                + beginningTime + ','
                + endTime + ','
                + lastUsedTime + ','
                + foregroundHours + ','
                + appLaunchCount + ','
                + launchCount
                +'\n';

        Log.d("输出字符串：",str);
        return str;
    }

    public static long dateToStamp(String dateStr) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(dateStr);
        long ts = date.getTime();
        return ts;
    }

}
