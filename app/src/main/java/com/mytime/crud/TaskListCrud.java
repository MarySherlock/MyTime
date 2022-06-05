package com.mytime.crud;

import com.mytime.model.ScheduleListItemInfo;
import com.mytime.model.TaskListItemInfo;

import org.litepal.LitePal;

import java.util.List;

public abstract class TaskListCrud {
    public static boolean checkName(String taskListName){
        List<TaskListItemInfo> taskListInfoItemList = LitePal.where("taskListName=?",taskListName).find(TaskListItemInfo.class);
        return taskListInfoItemList.isEmpty();
    }

    public abstract void updateTaskListItemInfoByName(String itemOldName, String itemNewName, String itemDescription, String account);
    public abstract void updateTaskListItemInfoByDescription(String listName, String description, String account);

    public abstract void deleteTaskListItem(String account,String itemName);
    public abstract void finishTaskListItem(String account,String itemName,String description);
}
