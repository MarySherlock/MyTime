package com.mytime.util;


import android.util.Log;

import com.mytime.model.AppUsageDetailInfo;

import java.text.SimpleDateFormat;
import java.util.Date;


public class StringUtil {
    /**
     * 将使用应用的信息组合成用于保存在文件中的字符串
     * @param appUsageDetailInfo 应用使用详情
     * @return 用于保存的字符串
     */
    public static String appUsageInfoToString(AppUsageDetailInfo appUsageDetailInfo){
        String str = null;

        String appName = appUsageDetailInfo.getAppName();
        int labelType = appUsageDetailInfo.getLabelType();
//        double beginningTime = appUsageDetailInfo.getBeginningTime();
//        double endTime = appUsageDetailInfo.getEndTime();
//        double lastUsedTime = appUsageDetailInfo.getLastUsedTime(); //上次使用时间

        String beginningTime = appUsageDetailInfo.getBeginningTime();
        String endTime = appUsageDetailInfo.getEndTime();
        String lastUsedTime = appUsageDetailInfo.getLastUsedTime(); //上次使用时间
        int foregroundHours = appUsageDetailInfo.getForegroundHours(); //前台总共运行的时间
        int appLaunchCount = appUsageDetailInfo.getAppLaunchCount(); //应用被拉起启动次数
        int launchCount = appUsageDetailInfo.getLaunchCount();//应用前台启动次数(包括自己启动其他activity)

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
