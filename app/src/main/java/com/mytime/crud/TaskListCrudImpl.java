package com.mytime.crud;

import android.annotation.SuppressLint;

import com.mytime.model.ScheduleListItemInfo;
import com.mytime.model.TaskListItemInfo;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TaskListCrudImpl extends TaskListCrud {
    @Override
    public void updateTaskListItemInfoByName(String itemOldName, String itemNewName, String itemDescription, String account) {
        List<ScheduleListItemInfo> scheduleListItemInfoList = LitePal.where("account=? and listName=?",account,itemOldName).find(ScheduleListItemInfo.class);
        for (ScheduleListItemInfo scheduleListItemInfo : scheduleListItemInfoList) {
            ScheduleListItemInfo scheduleListItemInfo1 = new ScheduleListItemInfo(
                    scheduleListItemInfo.getAccount(),
                    scheduleListItemInfo.getItemName(),
                    scheduleListItemInfo.getLabelName(),
                    itemNewName
            );
            scheduleListItemInfo1.updateAll("account=? and listName=? and itemName=?",account,itemOldName,scheduleListItemInfo.getItemName());

        }

        TaskListItemInfo taskListItemInfo = new TaskListItemInfo(account,itemNewName,itemDescription);
        taskListItemInfo.updateAll("taskListName=?",itemOldName);

    }

    @Override
    public void updateTaskListItemInfoByDescription(String listName, String description, String account) {
        TaskListItemInfo taskListItemInfo = new TaskListItemInfo(account,listName,description);
        taskListItemInfo.updateAll("taskListName=?",listName);
    }


    @Override
    public void deleteTaskListItem(String account, String itemName) {
        LitePal.deleteAll("TaskListItemInfo","account=? and taskListName=?",account,itemName);
        LitePal.deleteAll("ScheduleListItemInfo","account=? and listName=?", account,itemName);
    }

    @Override
    public void finishTaskListItem(String account, String itemName,String description) {
        Date date = new Date();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
        String finishDate = simpleDateFormat.format(date);

        List<ScheduleListItemInfo> scheduleListItemInfoList = LitePal.where("account=? and listName=?",account,itemName).find(ScheduleListItemInfo.class);
        for (ScheduleListItemInfo scheduleListItemInfo : scheduleListItemInfoList) {
            ScheduleListItemInfo scheduleListItemInfo1 = new ScheduleListItemInfo(
                    scheduleListItemInfo.getAccount(),
                    scheduleListItemInfo.getItemName(),
                    scheduleListItemInfo.getLabelName(),
                    scheduleListItemInfo.getListName()
            );
            scheduleListItemInfo1.setState(true);
            scheduleListItemInfo1.setFinishTime();
            scheduleListItemInfo1.updateAll("account=? and listName=? and itemName=?",account,itemName,scheduleListItemInfo.getItemName());
        }

        TaskListItemInfo taskListItemInfo = new TaskListItemInfo(account,itemName,description);
        taskListItemInfo.setTaskListState(true);
        taskListItemInfo.setFinishDate(finishDate);
        taskListItemInfo.updateAll("taskListName=?",itemName);



    }

}
