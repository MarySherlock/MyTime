package com.mytime.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mytime.R;
import com.mytime.adapter.LabelListViewAdapter;
import com.mytime.crud.AppLabelsCrud;
import com.mytime.crud.AppLabelsCrudImpl;
import com.mytime.model.AppInfo;
import com.mytime.model.LabelInfo;

import org.litepal.LitePal;

import java.util.List;
import java.util.Map;

public class SettingActivity extends AppCompatActivity {

    private ListView listView;
    private ImageView addLabel;
    private ImageView selectLabel;
    private TextView deleteTextView;
    private boolean selectFlag = false;
    private boolean selectAllFlag = true;
    private CheckBox checkAll;

    LabelListViewAdapter labelListViewAdapter;

    AppLabelsCrud appLabelsCrud = new AppLabelsCrudImpl();

    private String account;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        this.listView = findViewById(R.id.app_label_list);
        this.addLabel = findViewById(R.id.add_label);
        this.selectLabel = findViewById(R.id.select_task_list_item);
        this.checkAll = findViewById(R.id.select_all);
        this.deleteTextView = findViewById(R.id.delete_label);
        this.account = this.getAccount();

        init();

        this.listView.setOnItemClickListener((parent, view, position, id) -> {
            ImageView addAppImageView = view.findViewById(R.id.add_in_label);
            ImageView updateLabelImageView = view.findViewById(R.id.update_label);
            ImageView deleteLabelImageView = view.findViewById(R.id.delete_label);

            String labelName = this.labelListViewAdapter.getItem(position).getLabelName();

            // 修改标签名
            updateLabelImageView.setOnClickListener(v->{
                this.popUpdateAlertDialog(labelName);
            });

            // 删除标签名
            deleteLabelImageView.setOnClickListener(v->{
                Log.d("当前点击的标签是：", labelName);
                this.popDeleteAlertDialog(labelName,position);
            });

            // 增加标签下应用信息
            addAppImageView.setOnClickListener(v->{
                Intent intent = new Intent(SettingActivity.this, AllAppInfoActivity.class);
                intent.putExtra("label",labelName);
                intent.putExtra("account",account);
                startActivity(intent);
            });

            // 标签下的应用信息
            view.setOnClickListener(v->{
                Intent intent = new Intent(SettingActivity.this, AppInfoInLabelActivity.class);
                intent.putExtra("label",labelName);
                intent.putExtra("account",account);
                startActivity(intent);
            });

        });

        // 新增分类
        this.addLabel.setOnClickListener(v->{
            this.popInsertAlertDialog();
        });

        // 选中分类
        this.selectLabel.setOnClickListener(v->{
            if(!selectFlag){
                selectFlag = true;
                this.checkAll.setChecked(false);
                this.findViewById(R.id.select_list_layout).setVisibility(View.VISIBLE);
                for(int i=0;i<this.listView.getChildCount();i++){
                    this.listView.getChildAt(i).findViewById(R.id.select_task_list_item).setVisibility(View.VISIBLE);
                }
            }
            else{
                selectFlag = false;
                this.findViewById(R.id.select_list_layout).setVisibility(View.GONE);
                labelListViewAdapter.initCheck(false);
                for(int i=0;i<this.listView.getChildCount();i++){
                    this.listView.getChildAt(i).findViewById(R.id.select_task_list_item).setVisibility(View.GONE);
                }
            }
        });

        //全选
        this.checkAll.setOnClickListener(
            v -> {
                Map<Integer, Boolean> isCheck = labelListViewAdapter.getMap();
                labelListViewAdapter.initCheck(checkAll.isChecked());
                labelListViewAdapter.notifyDataSetChanged();
            }
        );

        this.deleteTextView.setOnClickListener(v->{
            Map<Integer, Boolean> isCheck = this.labelListViewAdapter.getMap();
            // 获取到条目数量。map.size = list.size,所以
            int count = this.labelListViewAdapter.getCount();
            // 遍历
            for (int i = 0; i < count; i++) {
                // 删除有两个map和list都要删除 ,计算方式
                int position = i - (count - this.labelListViewAdapter.getCount());
                // 推断状态 true为删除
                if (isCheck.get(i) != null && isCheck.get(i)) {
                    // 数据库删除数据
                    String itemName = this.labelListViewAdapter.getItem(position).getLabelName();
                    LitePal.deleteAll(LabelInfo.class,"labelName=?",itemName);
                    // listview删除数据
                    isCheck.remove(i);
                    this.labelListViewAdapter.removeData(position);
                }
            }
            if(this.checkAll.isChecked()) this.checkAll.setChecked(false);
            this.labelListViewAdapter.notifyDataSetChanged();
        });
    }

    private void init(){


        labelListViewAdapter = new LabelListViewAdapter(this);
        labelListViewAdapter.setData(LitePal.findAll(LabelInfo.class));
        listView.setAdapter(labelListViewAdapter);
    }


    private String getAccount(){
        List<Activity> activities = ActivityController.activities;
        for (Activity activity : activities) {
            if(activity.getClass()==MainActivity.class){
                return ((MainActivity) activity).getAccount();
            }
        }
        return null;
    }

    private void popUpdateAlertDialog(String labelName){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("修改分类");
        builder.setMessage("修改类名");
        EditText editText = new EditText(this);
        editText.setMaxLines(1);
        editText.setSingleLine(true);
        editText.setText(labelName);
        builder.setView(editText);
        builder.setPositiveButton("确定", (dialog, which) -> {
            if(appLabelsCrud.updateLabelName(this.account,labelName,editText.getText().toString())){
                this.labelListViewAdapter.setData(LitePal.findAll(LabelInfo.class));
                this.labelListViewAdapter.notifyDataSetChanged();
            }else{
                BaseActivity.alertHandler(this,"已有分类！");
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void popInsertAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("新增分类");
        builder.setMessage("输入内容");
        EditText editText = new EditText(this);
        editText.setMaxLines(1);
        editText.setSingleLine(true);
        builder.setView(editText);
        builder.setPositiveButton("确定", (dialog, which) -> {
            LabelInfo labelInfo = new LabelInfo(this.account,editText.getText().toString());
            if(appLabelsCrud.insertLabel(labelInfo)){
                this.labelListViewAdapter.addData(labelInfo);
                this.labelListViewAdapter.notifyDataSetChanged();
            }else{
                BaseActivity.alertHandler(this,"已有分类！");
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void popDeleteAlertDialog(String labelName,int position){

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("提示");
        dialog.setMessage("删除分类会同时清空此分类下的应用信息，是否确定删除选中分类？");
        dialog.setCancelable(false); //设置按下返回键不能消失
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                labelListViewAdapter.removeData(position);
                appLabelsCrud.deleteLabel(account,labelName);
                labelListViewAdapter.notifyDataSetChanged();

                List<AppInfo> appInfoList = LitePal.where("account=? and labelName=?",account,labelName).find(AppInfo.class);
                if (!appInfoList.isEmpty()){
                    for (AppInfo appInfo : appInfoList) {

                        AppInfo appInfo1 = appInfo;
                        appInfo1.setLabelName(null);
                        appInfo1.save();
                        appInfo.delete();

                    }
                }
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.show();//显示弹出窗口
    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
    }
}