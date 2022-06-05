package com.mytime.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.mytime.R;
import com.mytime.model.ScheduleListItemInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleListItemViewAdapter extends BaseAdapter {

    private List<ScheduleListItemInfo> scheduleItemInfoList = new ArrayList<>();
    private Context context;
    private Map<Integer,Boolean> isCheck = new HashMap<Integer,Boolean>();

    public ScheduleListItemViewAdapter(Context context){
        super();
        this.context = context;

    }

    // 初始化map集合
    public void initCheck(boolean flag) {
        // map集合的数量和list的数量是一致的
        for (int i = 0; i < scheduleItemInfoList.size(); i++) {
            // 设置默认的显示
            isCheck.put(i, flag);
        }
    }

    public void setData(List<ScheduleListItemInfo> scheduleListItemInfoList){
        this.scheduleItemInfoList = scheduleListItemInfoList;
    }

    public void addData(ScheduleListItemInfo scheduleListItemInfo){
        this.scheduleItemInfoList.add(0,scheduleListItemInfo);
    }

    @Override
    public int getCount() {
        // 假设标签列表信息为null就返回一个0
        return scheduleItemInfoList != null? scheduleItemInfoList.size():0;
    }

    @Override
    public ScheduleListItemInfo getItem(int position) {

        return scheduleItemInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ScheduleListItemViewAdapter.ViewHolder viewHolder = null;
        View view = null;
        // 推断是不是第一次进来
        if (convertView == null) {
            view = LayoutInflater.from(this.context).inflate(R.layout.schedule_list_item, null);
            viewHolder = new ScheduleListItemViewAdapter.ViewHolder();
            viewHolder.checkBox = view.findViewById(R.id.select_schedule_list_item);
            viewHolder.itemName = view.findViewById(R.id.schedule_item_name);
            viewHolder.finishItem = view.findViewById(R.id.manage_item);
            viewHolder.updateItem = view.findViewById(R.id.update_item);
            viewHolder.deleteItem = view.findViewById(R.id.delete_item);

            // 标记，能够复用
            view.setTag(viewHolder);
        } else {
            view = convertView;
            // 直接拿过来用
            viewHolder = (ScheduleListItemViewAdapter.ViewHolder) view.getTag();
        }
        // 拿到对象
        ScheduleListItemInfo bean = scheduleItemInfoList.get(position);
        if(bean.getSate()){
            viewHolder.itemName.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG);
            viewHolder.itemName.setTextColor(context.getColor(R.color.hint_color));
            viewHolder.updateItem.setVisibility(View.INVISIBLE);
            viewHolder.finishItem.setVisibility(View.INVISIBLE);
        }

        // 填充数据
        viewHolder.itemName.setText(bean.getItemName());
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
        public ImageView finishItem;
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
        scheduleItemInfoList.remove(position);
    }


}
