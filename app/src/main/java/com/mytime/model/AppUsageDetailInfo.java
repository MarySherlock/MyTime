package com.mytime.model;

import org.litepal.crud.LitePalSupport;

public class AppUsageDetailInfo extends LitePalSupport {
    String appName;
    String account;
    int labelType;
    String beginningTime;
    String endTime;
    String lastUsedTime;
    int foregroundHours;
    int appLaunchCount;
    int launchCount;

    public AppUsageDetailInfo(){
        this.labelType = -1;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }


    public int getLabelType() {
        return labelType;
    }

    public void setLabelType(int labelType) {
        this.labelType = labelType;
    }




    public String getBeginningTime() {
        return beginningTime;
    }

    public void setBeginningTime(String beginningTime) {
        this.beginningTime = beginningTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getLastUsedTime() {
        return lastUsedTime;
    }

    public void setLastUsedTime(String lastUsedTime) {
        this.lastUsedTime = lastUsedTime;
    }

    public int getForegroundHours() {
        return foregroundHours;
    }

    public void setForegroundHours(int foregroundHours) {
        this.foregroundHours = foregroundHours;
    }

    public int getAppLaunchCount() {
        return appLaunchCount;
    }

    public void setAppLaunchCount(int appLaunchCount) {
        this.appLaunchCount = appLaunchCount;
    }

    public int getLaunchCount() {
        return launchCount;
    }

    public void setLaunchCount(int launchCount) {
        this.launchCount = launchCount;
    }
}
