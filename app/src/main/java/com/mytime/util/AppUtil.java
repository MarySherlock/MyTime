package com.mytime.util;


import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.mytime.model.AppInfo;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

/**
 * created on 2020/8/3 20:24
 *
 * @author Scarf Gong
 */
public class AppUtil {
    private static final String TAG = "AppUtil";

    public static List<AppInfo> scanLocalInstallAppList(PackageManager packageManager,String account) {
        List myAppInfos = new ArrayList();
        try {
            List packageInfos = packageManager.getInstalledPackages(0);
            for (int i = 0; i < packageInfos.size(); i++) {
                PackageInfo packageInfo = (PackageInfo) packageInfos.get(i);
                //过滤掉系统app
                if ((ApplicationInfo.FLAG_SYSTEM & packageInfo.applicationInfo.flags) != 0) {
                    continue;
                }
                AppInfo myAppInfo = new AppInfo();
                String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                myAppInfo.setAppName(appName);
                myAppInfo.setPackageName(packageInfo.packageName);
                myAppInfo.setAppIcon(packageInfo.applicationInfo.loadIcon(packageManager));
                myAppInfo.setAccount(account);
                if (packageInfo.applicationInfo.loadIcon(packageManager) == null) {
                    continue;
                }
                myAppInfos.add(myAppInfo);
                List<AppInfo> appInfoListTemp = LitePal.where("account=? and appName=?",account,appName).find(AppInfo.class);
                if (appInfoListTemp.isEmpty()) {
                    myAppInfo.save();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "获取应用包信息失败");
        }
        List<AppInfo> appInfoListTemp = LitePal.findAll(AppInfo.class);
        for (AppInfo appInfo : appInfoListTemp) {
            Log.d("AppUtil: ",appInfo.getAppName());
        }
        return myAppInfos;
    }

}


