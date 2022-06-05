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

public class TaskListItemViewHistoryAdapter extends BaseAdapter {

    private List<TaskListItemInfo> taskListItemInfo = new ArrayList<>();
    private Context context;
    private Map<Integer,Boolean> isCheck = new HashMap<Integer,Boolean>();

    public TaskListItemViewHistoryAdapter(Context context){
        super();
        this.context = context;

    }

    // 初始化map集合
    public void initCheck(boolean flag) {
        // map集合的数量和list的数量是一致的
        for (int i = 0; i < taskListItemInfo.size(); i++) {
            // 设置默认的显示
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
        // 假设标签列表信息为null就返回一个0
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
        TaskListItemViewHistoryAdapter.ViewHolder viewHolder = null;
        View view = null;
        // 推断是不是第一次进来
        if (convertView == null) {
            view = LayoutInflater.from(this.context).inflate(R.layout.task_list_history_item, null);
            viewHolder = new TaskListItemViewHistoryAdapter.ViewHolder();
            viewHolder.itemName = view.findViewById(R.id.task_list_name_history);
            viewHolder.itemImage = view.findViewById(R.id.task_list_item_img_history);
            viewHolder.checkBox = view.findViewById(R.id.select_task_list_item_history);
            viewHolder.deleteItem = view.findViewById(R.id.delete_task_list_item_history);
            viewHolder.updateItem = view.findViewById(R.id.update_task_list_item_history);
            // 标记，能够复用
            view.setTag(viewHolder);
        } else {
            view = convertView;
            // 直接拿过来用
            viewHolder = (TaskListItemViewHistoryAdapter.ViewHolder) view.getTag();
        }
        // 拿到对象
        TaskListItemInfo bean = taskListItemInfo.get(position);
        // 填充数据
        viewHolder.itemName.setText(bean.getTaskListName());
        // 勾选框的点击事件
        viewHolder.checkBox
                .setOnCheckedChangeListener((buttonView, isChecked) -> {
                    // 用map集合保存
                    isCheck.put(position, isChecked);
                });
        // 设置状态
        if (isCheck.get(position) == null) {
            isCheck.put(position, false);
        }
        viewHolder.checkBox.setChecked(isCheck.get(position));
        return view;
    }

    // 优化
    public static class ViewHolder {
        public CheckBox checkBox;
        public TextView itemName;
        public ImageView itemImage;
        public ImageView deleteItem;
        public ImageView updateItem;
    }

    // 全选button获取状态
    public Map<Integer, Boolean> getMap() {
        // 返回状态
        return isCheck;
    }

    // 删除一个数据
    public void removeData(int position) {
        taskListItemInfo.remove(position);
    }
}
