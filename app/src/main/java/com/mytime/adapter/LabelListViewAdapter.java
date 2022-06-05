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

    // 初始化map集合
    public void initCheck(boolean flag) {
        // map集合的数量和list的数量是一致的
        for (int i = 0; i < labelInfoList.size(); i++) {
            // 设置默认的显示
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
        // 假设标签列表信息为null就返回一个0
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
        // 推断是不是第一次进来
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.label_list_item_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.labelName = view.findViewById(R.id.schedule_item_name);
            viewHolder.checkBox = view.findViewById(R.id.select_task_list_item);
            viewHolder.addLabel = view.findViewById(R.id.add_in_label);
            viewHolder.updateLabel = view.findViewById(R.id.update_label);
            viewHolder.deleteLabel = view.findViewById(R.id.delete_label);
            viewHolder.explainLabel = view.findViewById(R.id.explain_label);
            // 标记，能够复用
            view.setTag(viewHolder);
        } else {
            view = convertView;
            // 直接拿过来用
            viewHolder = (ViewHolder) view.getTag();
        }
        // 拿到对象
        LabelInfo bean = labelInfoList.get(position);
        // 填充数据
        viewHolder.labelName.setText(bean.getLabelName());
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
        public TextView labelName;
        public ImageView addLabel;
        public ImageView updateLabel;
        public ImageView deleteLabel;
        public ImageView explainLabel;
    }

    // 全选button获取状态
    public Map<Integer, Boolean> getMap() {
        // 返回状态
        return isCheck;
    }

    // 删除一个数据
    public void removeData(int position) {
        labelInfoList.remove(position);
    }


}
