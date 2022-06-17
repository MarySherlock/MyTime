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

    public void initCheck(boolean flag) {
        for (int i = 0; i < appInfoList.size(); i++) {
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
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.app_list_item_layout, null);
            viewHolder = new AppInfoListViewAdapter.ViewHolder();
            viewHolder.checkBox = view.findViewById(R.id.checkbox);
            viewHolder.appName = view.findViewById(R.id.app_name);
            viewHolder.appIcon = view.findViewById(R.id.app_icon);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (AppInfoListViewAdapter.ViewHolder) view.getTag();
        }
        AppInfo bean = appInfoList.get(position);
        viewHolder.appName.setText(bean.getAppName());
        viewHolder.appIcon.setImageDrawable(bean.getAppIcon());

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
        public TextView appName;
        public ImageView appIcon;
    }

    public Map<Integer, Boolean> getMap() {
        return isCheck;
    }

    public void removeData(int position) {
        appInfoList.remove(position);
    }
}
