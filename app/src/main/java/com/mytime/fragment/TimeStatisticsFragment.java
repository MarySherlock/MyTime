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
        endTime = calendar.getTimeInMillis();//结束时间  系统时间
        calendar.add(Calendar.DATE, -730);//时间间隔为两年
        startTime = calendar.getTimeInMillis();//开始时间

        // 打印检查时间设置是否正群
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:SS");
        String s = df.format(startTime);
        String s1 = df.format(endTime);
        Log.d("开始时间",s);
        Log.d("结束时间",s1);

        // 获取本应用的包管理器类
        mPackageManager = MyTimeApplication.getApplication().getPackageManager();


        // 获取账户信息和许可证信息
        MainActivity mainActivity = (MainActivity) getActivity();
        this.permissionFlag = mainActivity.getPermissionFlag();
        this.account = mainActivity.getAccount();
        if(this.permissionFlag) {

            // 获取数据库中应用使用的信息
            List<AppUsageDetailInfo> appUsageDetailInfoList = LitePal.findAll(AppUsageDetailInfo.class);
            // 获取应用使用信息的列表（所有） 获取的是UsageStatus的list，没有对自己需要的数据进行提取
            usageStatsList = getAppUsageState(this.getContext(), this.startTime, this.endTime);

            // 遍历这个list，提取需要的数据，存放进数据库
            for (UsageStats usageStats : usageStatsList) {
                this.saveUsageStatsInfo(usageStats);
            }
        }

//            // 如果为空，说明是第一次使用本应用，需要调用本应用进行数据的保存
//            if (appUsageDetailInfoList.isEmpty()) {
//
//                // 获取应用使用信息的列表（所有） 获取的是UsageStatus的list，没有对自己需要的数据进行提取
//                usageStatsList = getAppUsageState(this.getContext(),this.startTime,this.endTime);
//
//                // 遍历这个list，提取需要的数据，存放进数据库
//                for (UsageStats usageStats : usageStatsList) {
//
//                    this.saveUsageStatsInfo(usageStats);
//                }
//
//            }else {
//
//                // 如果数据库中已经存在了应用的使用信息，说明不是第一次使用本应用
//                // 遍历循环打印数据库中的信息
//                for (AppUsageDetailInfo appUsageDetailInfo : appUsageDetailInfoList) {
//                    String beginningTime = appUsageDetailInfo.getBeginningTime();
//                    String endTime = appUsageDetailInfo.getEndTime();
//                    String lastUsedTime = appUsageDetailInfo.getLastUsedTime(); //上次使用时间
//                    int foregroundTime = appUsageDetailInfo.getForegroundHours(); //前台总共运行的时间
//                    int appLaunchCount = appUsageDetailInfo.getAppLaunchCount(); //应用被拉起启动次数
//                    int launchCount = appUsageDetailInfo.getLaunchCount();//应用前台启动次数(包括自己启动其他activity)
//
//                    String appName = appUsageDetailInfo.getAppName();
//
//                    Log.d("bqt", "| " + appName + " | " + beginningTime + " | " + endTime + " | " + lastUsedTime + " | " + foregroundTime + " | " + appLaunchCount + " | " + launchCount + " |" + appUsageDetailInfo.getLabelType());
//
//                }
//
//                // 因为不是第一次使用本应用，所以之后只用在特定的时间点进行数据的保存即可
//                // 例如新增今天的应用使用情况的分析
//                // 获取应用使用信息的列表（所有） 获取的是UsageStatus的list，没有对自己需要的数据进行提取
//
//                long endTime = DateUtil.getTodayStartTime();
//                long startTime = endTime - 1000 * 60 * 60 * 24;
//                usageStatsList = getAppUsageState(this.getContext(),startTime,endTime);
//
//                // 遍历这个list，提取需要的数据，存放进数据库
//                for (UsageStats usageStats : usageStatsList) {
//
//                    this.saveUsageStatsInfo(usageStats);
//                }
//            }
//        }
    }


    /**
     * 初始化今天的应用使用情况的数据的可视化图表展示
     */
    public void initTodayTimeStatistic(){

        long endTime = DateUtil.getTodayStartTime();
        long startTime = endTime - 1000 * 60 * 60 * 24;

        roseChartSmall.setShowChartLable(false);    //是否在图表上显示指示lable
        roseChartSmall.setShowChartNum(false);     //是否在图表上显示指示num
        roseChartSmall.setShowNumTouched(false);   //点击显示数量
        roseChartSmall.setShowRightNum(false);      //右侧显示数量
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
//        roseList.add(new RoseBean(8, "数据4"));
//        roseList.add(new RoseBean(10, "数据1"));
//        roseList.add(new RoseBean(13, "数据2"));
//        roseList.add(new RoseBean(21, "数据5"));
//        roseList.add(new RoseBean(31, "数据3"));

        //参数1：数据对象class， 参数2：数量属性字段名称， 参数3：名称属性字段名称， 参数4：数据集合
        roseChartSmall.setData(RoseBean.class, "count", "ClassName", roseList);
        roseChartSmall.setLoading(false);//是否正在加载，数据加载完毕后置为false
    }

    /**
     * queryUsageStats ： 获取应用统计信息
     * 参数一：intervalType:时间间隔类型，有五种
     *      INTERVAL_DAILY: 日长短级别数据,最长7天内的数据;
     *      INTERVAL_WEEKLY: 星期长短级别数据,最长4个星期内的数据;
     *      INTERVAL_MONTHLY: 月长短级别数据,最长6个月内的数据;
     *      INTERVAL_YEARLY: 年长短级别数据,最长2年内的数据，也就是说，数据最长保存2年;
     *      INTERVAL_BEST: 根据提供的时间间隔（根据与第二个参数和第三个参数获取），自动搭配最好的级别
     * 参数二：beginTime:开始统计的时间
     * 参数三： endTime:结束的时间
     */

    @RequiresApi(api = Build.VERSION_CODES.M)
    public List<UsageStats> getAppUsageState(Context context,long startTime,long endTime) throws PackageManager.NameNotFoundException {

        //获取指定时间范围内的应用统计信息列表
        UsageStatsManager usageStatsManager=(UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // 过滤系统软件
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

            // 如果是第一次运行这个程序，就将过去一年的软件使用信息按照天数进行保存
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
            int foregroundTime = (int) (usageStats.getTotalTimeInForeground() / 1000 / 60 / 60); //前台总共运行的时间
            int appLaunchCount = 0; //应用被拉起启动次数
            int launchCount = 0;//应用前台启动次数(包括自己启动其他activity)

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


//            Log.i("bqt", "| " + appName + "|" + packageName + " | " + beginningDateStr + " | " + endDateStr + " | " + lastUsedDateStr + " | " + foregroundTime + " | " + appLaunchCount + " | " + launchCount + " |" + appUsageDetailInfo.getLabelType());
//            Log.i("bqt", "| " + appName + "|" + lastUsedDateStr + " | " + foregroundTime + " | " + appLaunchCount + " | " + launchCount + " |" + appUsageDetailInfo.getLabelType());

        }catch (PackageManager.NameNotFoundException ignored){

        }
    }

    private ApplicationInfo getAppInfo(String pkgName) {
        try {

            return mPackageManager.getApplicationInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            // e.printStackTrace();
//            Log.e("TAG", "已经找不到包名为[" + pkgName + "]的应用");
        }
        return null;
    }

    private ArrayList<AppUsageInfo> readAppUsageList(Long startTime,Long endTime,int n) {
        ArrayList<AppUsageInfo> mItems = JListKit.newArrayList();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            UsageStatsManager usage = (UsageStatsManager) MyTimeApplication.getApplication().getSystemService(Context.USAGE_STATS_SERVICE);
            if (usage == null) return mItems;

            // 查询并按包名进行聚合操作
            Map<String, UsageStats> statsMap = usage.queryAndAggregateUsageStats(startTime, endTime);
            Set<String> keySet = statsMap.keySet();
            for (String packageName : keySet) {
                UsageStats usageStats = statsMap.get(packageName);
                if (usageStats == null) continue;
                long totalTimeInForeground = usageStats.getTotalTimeInForeground();
                if (totalTimeInForeground <= 0) continue;// 小于1秒的都按照没有打开过处理

                AppUsageInfo appUsageBean = new AppUsageInfo(packageName, usageStats);
                ApplicationInfo info = getAppInfo(packageName);
                if (info==null) continue;
                // 排除手机系统应用
                if ((ApplicationInfo.FLAG_SYSTEM & info.flags) != 0) {
                    continue;
                }
                appUsageBean.setAppInfo(info);
                // 获取应用名称
                String label = (String) info.loadLabel(mPackageManager);
                Drawable icon = info.loadIcon(mPackageManager);
                appUsageBean.setAppName(label);
                appUsageBean.setAppIcon(icon);

                // 保存应用信息
//                AppInfo appInfo = new AppInfo();
//                appInfo.setAccount(this.account);
//                appInfo.setAppName(label);
//                appInfo.setAppIcon(icon);
//                appInfo.save();

                appUsageBean.save();
                mItems.add(appUsageBean);
                // 打印日志
//                if (BuildConfig.DEBUG) {
//                    String fmt = "yyyy-MM-dd HH:mm:ss.SSS";
//                    Log.d("UsageStats: ",label + "|"+JDateKit.timeToStringChineChinese(totalTimeInForeground)
//                            + "|"+ JDateKit.timeToDate(fmt, usageStats.getFirstTimeStamp())
//                            + "|"+ JDateKit.timeToDate(fmt, usageStats.getLastTimeStamp())
//                            + "|"+ JDateKit.timeToDate(fmt, usageStats.getLastTimeUsed()));
//
//                        Log.d("UsageStats", "**********************************************");
//                        Log.d("UsageStats", label);
//                        // Log.d("UsageStats", "运行时长:" + JDateKit.timeToStringChineChinese(totalTimeInForeground));
//                        Log.d("UsageStats", String.format("运行时长:%s (%sms)", JDateKit.timeToStringChineChinese(totalTimeInForeground), totalTimeInForeground));
////                        String fmt = "yyyy-MM-dd HH:mm:ss.SSS";
//                        Log.d("UsageStats", "开始启动:" + JDateKit.timeToDate(fmt, usageStats.getFirstTimeStamp()));
//                        Log.d("UsageStats", "最后启动:" + JDateKit.timeToDate(fmt, usageStats.getLastTimeStamp()));
//                        Log.d("UsageStats", "最近使用:" + JDateKit.timeToDate(fmt, usageStats.getLastTimeUsed()));
//                }
//                endTime = startTime;
//                startTime -= 1000*60*60*24;
            }
        }
        return mItems;
    }


    public void updateTime(){
            Calendar calendar=Calendar.getInstance();
            calendar.setTime(new Date(this.startTime));
            this.endTime = calendar.getTimeInMillis();//结束时间
            calendar.add(Calendar.DATE, -1);//时间间隔为一天
            this.startTime = calendar.getTimeInMillis();//开始时间
    }



}