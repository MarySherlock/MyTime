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
import com.mytime.model.AppInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppInfoListViewAdapter extends BaseAdapter {
    private List<AppInfo> appInfoList = new ArrayList<>();
    private Context context;
    private Map<Integer,Boolean> isCheck = new HashMap<>();

    public AppInfoListViewAdapter(Context context){
        super();
        this.context = context;

    }

    // 初始化map集合
    public void initCheck(boolean flag) {
        // map集合的数量和list的数量是一致的
        for (int i = 0; i < appInfoList.size(); i++) {
            // 设置默认的显示
            isCheck.put(i, flag);
        }
    }

    public void setData(List<AppInfo> appInfoList){
        this.appInfoList = appInfoList;
    }

    public void addData(AppInfo appInfoList){
        this.appInfoList.add(0,appInfoList);
    }

    @Override
    public int getCount() {
        // 假设标签列表信息为null就返回一个0
        return appInfoList != null?appInfoList.size():0;
    }

    @Override
    public AppInfo getItem(int position) {

        return appInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        AppInfoListViewAdapter.ViewHolder viewHolder = null;
        View view = null;
        // 推断是不是第一次进来
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.app_list_item_layout, null);
            viewHolder = new AppInfoListViewAdapter.ViewHolder();
            viewHolder.checkBox = view.findViewById(R.id.checkbox);
            viewHolder.appName = view.findViewById(R.id.app_name);
            viewHolder.appIcon = view.findViewById(R.id.app_icon);
            // 标记，能够复用
            view.setTag(viewHolder);
        } else {
            view = convertView;
            // 直接拿过来用
            viewHolder = (AppInfoListViewAdapter.ViewHolder) view.getTag();
        }
        // 拿到对象
        AppInfo bean = appInfoList.get(position);
        // 填充数据
//        viewHolder.labelName.setText(bean.getLabelName());
        viewHolder.appName.setText(bean.getAppName());
        viewHolder.appIcon.setImageDrawable(bean.getAppIcon());

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
        public TextView appName;
        public ImageView appIcon;
    }

    // 全选button获取状态
    public Map<Integer, Boolean> getMap() {
        // 返回状态
        return isCheck;
    }

    // 删除一个数据
    public void removeData(int position) {
        appInfoList.remove(position);
    }
}
