package com.mytime.model;

import android.annotation.SuppressLint;

import org.litepal.crud.LitePalSupport;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TaskListItemInfo extends LitePalSupport {

    private String account;
    private String taskListName;
    private String taskListDescription;
    private boolean taskListState;
    private String createDate;
    private String finishDate;

    public TaskListItemInfo(String account, String taskListName, String taskListDescription){
        this.account = account;
        this.taskListName = taskListName;
        this.taskListDescription = taskListDescription;
        this.taskListState = false;
        this.setCreateDate();
        this.finishDate = null;

    }
    public TaskListItemInfo(){

    }

    public String getTaskListName() {
        return taskListName;
    }

    public void setTaskListName(String taskListName) {
        this.taskListName = taskListName;
    }

    public String getTaskListDescription() {
        return taskListDescription;
    }

    public void setTaskListDescription(String taskListDescription) {
        this.taskListDescription = taskListDescription;
    }

    public boolean isTaskListState() {
        return taskListState;
    }

    public void setTaskListState(boolean taskListState) {
        this.taskListState = taskListState;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public void setCreateDate() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
        Date curDate = new Date(System.currentTimeMillis());
        this.createDate = simpleDateFormat.format(curDate);
    }


    public String getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(String finishDate) {
        this.finishDate = finishDate;
    }


    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
