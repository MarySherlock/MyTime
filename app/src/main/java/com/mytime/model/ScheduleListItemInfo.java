package com.mytime.model;

import android.annotation.SuppressLint;

import org.litepal.crud.LitePalSupport;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ScheduleListItemInfo extends LitePalSupport {
    private String account;
    private String itemName;
    private String listName;
    private String labelName;
    private boolean state;
    private String createTime;
    private String finishTime;

    public ScheduleListItemInfo(){

    }

    public ScheduleListItemInfo(String account, String itemName, String labelName, String listName){
                this.account = account;
                this.itemName = itemName;
                this.labelName = labelName;
                this.state = false;
                this.listName = listName;
                this.setCreateTime();
                this.finishTime = null;

    }
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public boolean getSate() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
        Date curDate = new Date(System.currentTimeMillis());
        this.createTime = simpleDateFormat.format(curDate);
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
        Date curDate = new Date(System.currentTimeMillis());
        this.finishTime = simpleDateFormat.format(curDate);
    }



}
