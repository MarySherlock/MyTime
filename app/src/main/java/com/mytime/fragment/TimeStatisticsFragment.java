package com.mytime.fragment;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Fragment;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.mytime.BuildConfig;
import com.mytime.MyTimeApplication;
import com.mytime.R;
import com.mytime.activity.MainActivity;
import com.mytime.model.AppInfo;
import com.mytime.model.AppUsageDetailInfo;
import com.mytime.model.AppUsageInfo;
import com.mytime.model.LabelInfo;
import com.mytime.model.LabelOfAppInfo;
import com.mytime.model.RoseBean;
import com.mytime.util.DateUtil;
import com.mytime.util.JDateKit;
import com.mytime.util.JListKit;
import com.openxu.cview.chart.rosechart.NightingaleRoseChart;


import org.litepal.LitePal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class TimeStatisticsFragment extends Fragment {

    List<LabelInfo> labelInfoList;
    List<UsageStats> usageStatsList;
    List<LabelOfAppInfo> labelOfAppInfoList;
    boolean permissionFlag = false;

    NightingaleRoseChart roseChartSmall;

    long startTime;
    long endTime;
    String account;
    PackageManager mPackageManager;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_time_statistics, container, false);

        try {
            init();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        roseChartSmall = view.findViewById(R.id.roseChartSmall);



        initTodayTimeStatistic();
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void init() throws PackageManager.NameNotFoundException {

        labelInfoList = LitePal.findAll(LabelInfo.class);
        labelOfAppInfoList = LitePal.findAll(LabelOfAppInfo.class);
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date());
        endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.DATE, -730);
        startTime = calendar.getTimeInMillis();

        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:SS");
        String s = df.format(startTime);
        String s1 = df.format(endTime);
        Log.d("开始时间",s);
        Log.d("结束时间",s1);

        mPackageManager = MyTimeApplication.getApplication().getPackageManager();


        MainActivity mainActivity = (MainActivity) getActivity();
        this.permissionFlag = mainActivity.getPermissionFlag();
        this.account = mainActivity.getAccount();
        if(this.permissionFlag) {

            List<AppUsageDetailInfo> appUsageDetailInfoList = LitePal.findAll(AppUsageDetailInfo.class);
            usageStatsList = getAppUsageState(this.getContext(), this.startTime, this.endTime);

            for (UsageStats usageStats : usageStatsList) {
                this.saveUsageStatsInfo(usageStats);
            }
        }

    }


    public void initTodayTimeStatistic(){

        long endTime = DateUtil.getTodayStartTime();
        long startTime = endTime - 1000 * 60 * 60 * 24;

        roseChartSmall.setShowChartLable(false);
        roseChartSmall.setShowChartNum(false);
        roseChartSmall.setShowNumTouched(false);
        roseChartSmall.setShowRightNum(false);
        List<Object> roseList = new ArrayList<>();

        long sumTime = 0;
        List<AppUsageInfo> appUsageInfoList;
        appUsageInfoList=this.readAppUsageList(startTime,endTime,1);

        for (AppUsageInfo appUsageInfo : appUsageInfoList) {
            sumTime += appUsageInfo.getTotalTimeInForeground();
            Log.d("前台运行时长： ", String.valueOf(appUsageInfo.getTotalTimeInForeground()));
        }

        int minute;
        for (AppUsageInfo appUsageInfo : appUsageInfoList) {
            minute = (int) (appUsageInfo.getTotalTimeInForeground()/1000/60/60);
            Log.d("比例", String.valueOf(minute));
            roseList.add(new RoseBean(minute,appUsageInfo.getAppName()));
        }


        roseChartSmall.setData(RoseBean.class, "count", "ClassName", roseList);
        roseChartSmall.setLoading(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public List<UsageStats> getAppUsageState(Context context,long startTime,long endTime) throws PackageManager.NameNotFoundException {

        UsageStatsManager usageStatsManager=(UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            queryUsageStats = queryUsageStats.parallelStream().filter(data ->
                (
                        data.getLastTimeUsed() > -1585 && data.getLastTimeUsed()>=startTime
                )).collect(Collectors.toList());

        }
        return queryUsageStats;
    }


    @SuppressLint({"SoonBlockedPrivateApi", "DiscouragedPrivateApi"})
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void saveUsageStatsInfo(UsageStats usageStats) {

        try{
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");

            String packageName = usageStats.getPackageName();


            PackageManager pm = getContext().getPackageManager();
            String appName = pm.getApplicationLabel(pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA)).toString();
            appName = appName.replaceAll(" ", "");

            List<AppInfo> appInfoList = LitePal.where("labelName!=null").find(AppInfo.class);
            for (AppInfo appInfo : appInfoList) {
                if (!appInfo.getAppName().equals(appName)) return;
            }

            Date beginningDate = new Date(usageStats.getFirstTimeStamp());
            Date endDate = new Date(usageStats.getLastTimeStamp());
            Date lastUsedDate = new Date(usageStats.getLastTimeUsed());


            String beginningDateStr = df.format(beginningDate);
            String endDateStr = df.format(endDate);
            String lastUsedDateStr = df.format(lastUsedDate);
            int foregroundTime = (int) (usageStats.getTotalTimeInForeground() / 1000 / 60 / 60);
            int appLaunchCount = 0;
            int launchCount = 0;

            long beginning;
            String beginningStr = null;
            try {
                beginning = (long) usageStats.getClass().getDeclaredMethod("getFirstTimeStamp").invoke(usageStats);
                beginningStr = df.format(beginning);
                appLaunchCount = (int) usageStats.getClass().getDeclaredMethod("getAppLaunchCount").invoke(usageStats);
                launchCount = usageStats.getClass().getDeclaredField("mLaunchCount").getInt(usageStats);
            } catch (Exception e) {
                e.printStackTrace();
            }

            int labelType = -1;
            for (LabelOfAppInfo labelOfAppInfo : this.labelOfAppInfoList) {
                if (labelOfAppInfo.getAppName().equals(appName)){

                    labelType = labelOfAppInfo.getLabelType();
                }
            }

            AppUsageDetailInfo appUsageDetailInfo = new AppUsageDetailInfo();

            appUsageDetailInfo.setAccount(this.account);
            appUsageDetailInfo.setAppName(appName);
            appUsageDetailInfo.setBeginningTime(beginningDateStr);
            appUsageDetailInfo.setEndTime(endDateStr);
            appUsageDetailInfo.setLastUsedTime(lastUsedDateStr);
            appUsageDetailInfo.setForegroundHours(foregroundTime);
            appUsageDetailInfo.setAppLaunchCount(appLaunchCount);
            appUsageDetailInfo.setLaunchCount(launchCount);
            appUsageDetailInfo.setLabelType(labelType);
            appUsageDetailInfo.save();


        }catch (PackageManager.NameNotFoundException ignored){

        }
    }

    private ApplicationInfo getAppInfo(String pkgName) {
        try {

            return mPackageManager.getApplicationInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
        }
        return null;
    }

    private ArrayList<AppUsageInfo> readAppUsageList(Long startTime,Long endTime,int n) {
        ArrayList<AppUsageInfo> mItems = JListKit.newArrayList();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            UsageStatsManager usage = (UsageStatsManager) MyTimeApplication.getApplication().getSystemService(Context.USAGE_STATS_SERVICE);
            if (usage == null) return mItems;

            Map<String, UsageStats> statsMap = usage.queryAndAggregateUsageStats(startTime, endTime);
            Set<String> keySet = statsMap.keySet();
            for (String packageName : keySet) {
                UsageStats usageStats = statsMap.get(packageName);
                if (usageStats == null) continue;
                long totalTimeInForeground = usageStats.getTotalTimeInForeground();
                if (totalTimeInForeground <= 0) continue;

                AppUsageInfo appUsageBean = new AppUsageInfo(packageName, usageStats);
                ApplicationInfo info = getAppInfo(packageName);
                if (info==null) continue;
                if ((ApplicationInfo.FLAG_SYSTEM & info.flags) != 0) {
                    continue;
                }
                appUsageBean.setAppInfo(info);
                String label = (String) info.loadLabel(mPackageManager);
                Drawable icon = info.loadIcon(mPackageManager);
                appUsageBean.setAppName(label);
                appUsageBean.setAppIcon(icon);

                appUsageBean.save();
                mItems.add(appUsageBean);
            }
        }
        return mItems;
    }

}