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
import com.mytime.model.LabelInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LabelListViewAdapter extends BaseAdapter {

    private List<LabelInfo> labelInfoList = new ArrayList<>();
    private Context context;
    private Map<Integer,Boolean> isCheck = new HashMap<Integer,Boolean>();

    public LabelListViewAdapter(Context context){
        super();
        this.context = context;

    }

    public void initCheck(boolean flag) {
        for (int i = 0; i < labelInfoList.size(); i++) {
            isCheck.put(i, flag);
        }
    }

    public void setData(List<LabelInfo> labelInfoList){
        this.labelInfoList = labelInfoList;
    }

    public void addData(LabelInfo labelInfo){
        labelInfoList.add(0,labelInfo);
    }

    @Override
    public int getCount() {
        return labelInfoList != null?labelInfoList.size():0;
    }

    @Override
    public LabelInfo getItem(int position) {

        return labelInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        View view = null;
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.label_list_item_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.labelName = view.findViewById(R.id.schedule_item_name);
            viewHolder.checkBox = view.findViewById(R.id.select_task_list_item);
            viewHolder.addLabel = view.findViewById(R.id.add_in_label);
            viewHolder.updateLabel = view.findViewById(R.id.update_label);
            viewHolder.deleteLabel = view.findViewById(R.id.delete_label);
            viewHolder.explainLabel = view.findViewById(R.id.explain_label);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        LabelInfo bean = labelInfoList.get(position);
        viewHolder.labelName.setText(bean.getLabelName());
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
        public TextView labelName;
        public ImageView addLabel;
        public ImageView updateLabel;
        public ImageView deleteLabel;
        public ImageView explainLabel;
    }

    public Map<Integer, Boolean> getMap() {
        return isCheck;
    }

    public void removeData(int position) {
        labelInfoList.remove(position);
    }


}
