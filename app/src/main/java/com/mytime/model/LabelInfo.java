package com.mytime.model;


import org.litepal.crud.LitePalSupport;

public class LabelInfo extends LitePalSupport {
    private String account;
    private String labelName;

    public LabelInfo(String account, String labelName) {
        this.account = account;
        this.labelName = labelName;
    }

    public LabelInfo(){

    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }
}
