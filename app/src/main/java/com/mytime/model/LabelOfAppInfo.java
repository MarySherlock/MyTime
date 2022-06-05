package com.mytime.model;

import org.litepal.crud.LitePalSupport;

public class LabelOfAppInfo extends LitePalSupport {

    int labelType;
    String appName;
    String account;


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
}
