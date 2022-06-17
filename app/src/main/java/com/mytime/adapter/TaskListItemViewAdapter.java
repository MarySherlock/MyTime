package com.mytime.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.mytime.R;
import com.mytime.model.TaskListItemInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskListItemViewAdapter extends BaseAdapter {

    private List<TaskListItemInfo> taskListItemInfo = new ArrayList<>();
    private Context context;
    private Map<Integer,Boolean> isCheck = new HashMap<Integer,Boolean>();

    public TaskListItemViewAdapter(Context context){
        super();
        this.context = context;

    }

    public void initCheck(boolean flag) {
        for (int i = 0; i < taskListItemInfo.size(); i++) {
            isCheck.put(i, flag);
        }
    }

    public void setData(List<TaskListItemInfo> taskListItemInfo){
        this.taskListItemInfo = taskListItemInfo;
    }

    public void addData(TaskListItemInfo taskListItemInfo){
        this.taskListItemInfo.add(0,taskListItemInfo);
    }

    @Override
    public int getCount() {
        return taskListItemInfo != null? taskListItemInfo.size():0;
    }

    @Override
    public TaskListItemInfo getItem(int position) {

        return taskListItemInfo.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        TaskListItemViewAdapter.ViewHolder viewHolder = null;
        View view = null;
        if (convertView == null) {
            view = LayoutInflater.from(this.context).inflate(R.layout.task_list_item_layout, null);
            viewHolder = new TaskListItemViewAdapter.ViewHolder();
            viewHolder.itemName = view.findViewById(R.id.task_list_name);
            viewHolder.itemImage = view.findViewById(R.id.task_list_item_img);
            viewHolder.checkBox = view.findViewById(R.id.select_task_list_item);
            viewHolder.finishItem = view.findViewById(R.id.finish_task_list_item_img);
            viewHolder.deleteItem = view.findViewById(R.id.delete_task_list_item);
            viewHolder.addItem = view.findViewById(R.id.add_schedule_item);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (TaskListItemViewAdapter.ViewHolder) view.getTag();
        }
        TaskListItemInfo bean = taskListItemInfo.get(position);
        viewHolder.itemName.setText(bean.getTaskListName());
        viewHolder.checkBox
                .setOnCheckedChangeListener((buttonView, isChecked) -> {
                    isCheck.put(position, isChecked);
                });
        if (isCheck.get(position) == null) {
            isCheck.put(position, false);
        }
        viewHolder.checkBox.setChecked(isCheck.get(position));
        return view;
    }

    public static class ViewHolder {
        public CheckBox checkBox;
        public TextView itemName;
        public ImageView itemImage;
        public ImageView finishItem;
        public ImageView deleteItem;
        public ImageView updateItem;
        public ImageView addItem;
    }

    public Map<Integer, Boolean> getMap() {
        return isCheck;
    }

    public void removeData(int position) {
        taskListItemInfo.remove(position);
    }


}
