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

    public void initCheck(boolean flag) {
        for (int i = 0; i < scheduleItemInfoList.size(); i++) {
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
        view = LayoutInflater.from(this.context).inflate(R.layout.schedule_list_item, null);
        viewHolder = new ScheduleListItemViewAdapter.ViewHolder();
        viewHolder.checkBox = view.findViewById(R.id.select_schedule_list_item);
        viewHolder.itemName = view.findViewById(R.id.schedule_item_name);
        viewHolder.finishItem = view.findViewById(R.id.manage_item);
        viewHolder.updateItem = view.findViewById(R.id.update_item);
        viewHolder.deleteItem = view.findViewById(R.id.delete_item);

        ScheduleListItemInfo bean = scheduleItemInfoList.get(position);
        if(bean.getSate()){
            viewHolder.itemName.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG);
            viewHolder.itemName.setTextColor(context.getColor(R.color.hint_color));
            viewHolder.updateItem.setVisibility(View.INVISIBLE);
            viewHolder.finishItem.setVisibility(View.INVISIBLE);
        }

        viewHolder.itemName.setText(bean.getItemName());
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
        public ImageView finishItem;
        public ImageView deleteItem;
        public ImageView updateItem;
    }

    public Map<Integer, Boolean> getMap() {
        return isCheck;
    }

    public void removeData(int position) {
        scheduleItemInfoList.remove(position);
    }


}
