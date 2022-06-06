package com.mytime.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mytime.R;
import com.mytime.adapter.AppInfoListViewAdapter;
import com.mytime.crud.AppLabelsCrud;
import com.mytime.crud.AppLabelsCrudImpl;
import com.mytime.model.AppInfo;
import com.mytime.model.AppUsageDetailInfo;
import com.mytime.model.LabelInfo;
import com.mytime.model.LabelOfAppInfo;
import com.mytime.util.AppUtil;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AppInfoInLabelActivity extends AppCompatActivity {

    ImageView backImageView;
    TextView labelNameTextView;
    ImageView appSelectImageView;
    ImageView addAppInfoImageView;
    LinearLayout selectLinearLayout;
    CheckBox checkAll;
    TextView appRemoveTextView;
    ListView appInfoListView;

    String account;
    String labelName;
    List<AppInfo> appInfoList;
    AppLabelsCrud appLabelsCrud = new AppLabelsCrudImpl();
    AppInfoListViewAdapter appInfoListViewAdapter;


    private boolean selectFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);

        backImageView = findViewById(R.id.back_img);
        labelNameTextView = findViewById(R.id.label);
        appSelectImageView = findViewById(R.id.select_app_item);
        addAppInfoImageView = findViewById(R.id.add_app);
        selectLinearLayout = findViewById(R.id.select_app_list_layout);
        checkAll = findViewById(R.id.select_all);
        appRemoveTextView = findViewById(R.id.app_remove);
        appInfoListView = findViewById(R.id.app_info_list);
        appInfoListViewAdapter = new AppInfoListViewAdapter(this);


        // 获取当前标签名
        Intent intent = getIntent();
        this.labelName = intent.getStringExtra("label");

        this.account = intent.getStringExtra("account");
        this.labelNameTextView.setText(this.labelName);
        this.selectFlag = false;

        init();

        // 返回源界面
        this.backImageView.setOnClickListener(v-> finish());

        // 修改标签名
        this.labelNameTextView.setOnClickListener(v->{
            String name = this.labelNameTextView.getText().toString();
            this.popUpdateAlertDialog(name);
        });

        // 选择应用信息进行移除操作
        this.appSelectImageView.setOnClickListener(v->{
            if(!selectFlag){
                selectFlag = true;
                this.checkAll.setChecked(false);
                this.selectLinearLayout.setVisibility(View.VISIBLE);
                for(int i=0;i<this.appInfoListView.getChildCount();i++){
                    this.appInfoListView.getChildAt(i).findViewById(R.id.checkbox).setVisibility(View.VISIBLE);
                }
            }
            else{
                selectFlag = false;
                this.selectLinearLayout.setVisibility(View.GONE);
                appInfoListViewAdapter.initCheck(false);
                for(int i=0;i<this.appInfoListView.getChildCount();i++){
                    this.appInfoListView.getChildAt(i).findViewById(R.id.checkbox).setVisibility(View.GONE);
                }
            }
        });

        // 为标签添加应用信息
        this.addAppInfoImageView.setOnClickListener(v->{
            Intent intent1 = new Intent(AppInfoInLabelActivity.this,AllAppInfoActivity.class);
            intent1.putExtra("account",this.account);
            intent1.putExtra("label",this.labelName);
            startActivity(intent1);
        });

        //全选
        this.checkAll.setOnClickListener(
                v -> {
                    Map<Integer, Boolean> isCheck = appInfoListViewAdapter.getMap();
                    appInfoListViewAdapter.initCheck(checkAll.isChecked());
                    appInfoListViewAdapter.notifyDataSetChanged();
                }
        );

        // 移除
        this.appRemoveTextView.setOnClickListener(v->{
            Map<Integer, Boolean> isCheck = this.appInfoListViewAdapter.getMap();
            int count = this.appInfoListViewAdapter.getCount();
            for (int i = 0; i < count; i++) {
                int position = i - (count - this.appInfoListViewAdapter.getCount());
                if (isCheck.get(i) != null && isCheck.get(i)) {
                    String appName = this.appInfoListViewAdapter.getItem(position).getAppName();

                    AppInfo appInfo = this.appInfoListViewAdapter.getItem(position);
                    this.appInfoList.remove(appInfo);
                    appInfo.setLabelName(null);
                    appInfo.save();

                    List<AppUsageDetailInfo> appUsageDetailInfoList = LitePal.where("appName=? and account=?",appName,account).find(AppUsageDetailInfo.class);
                    for (AppUsageDetailInfo usageDetailInfo : appUsageDetailInfoList) {
                        AppUsageDetailInfo appUsageDetailInfo = usageDetailInfo;
                        appUsageDetailInfo.setLabelType(-1);
                        appUsageDetailInfo.save();
                        usageDetailInfo.delete();
                        Log.d("移除某分类下的应用：","移除成功！");
                    }

                    LitePal.deleteAll(AppInfo.class,"appName=?",appName);
                    LitePal.deleteAll(LabelOfAppInfo.class,"appName=?",appName);
                    isCheck.remove(i);
                    this.selectLinearLayout.setVisibility(View.GONE);

                }
            }
            if(this.checkAll.isChecked()) this.checkAll.setChecked(false);
//            this.appInfoListViewAdapter.notifyDataSetChanged();
            init();
        });
    }


    public void init(){

        this.appInfoList = AppUtil.scanLocalInstallAppList(AppInfoInLabelActivity.this.getPackageManager(),this.account);

        for (int i =0;i<this.appInfoList.size();i++){
            List<AppInfo> appInfoListTemp = LitePal.where("account=? and appName=?",this.account,this.appInfoList.get(i).getAppName()).find(AppInfo.class);
            if (!appInfoListTemp.isEmpty()) {
                if ("".equals(appInfoListTemp.get(0).getLabelName()) || !this.labelName.equals(appInfoListTemp.get(0).getLabelName())) {
                    this.appInfoList.remove(i);
                    i-=1;
                }
            }
        }
        appInfoListViewAdapter.setData(appInfoList);
        appInfoListView.setAdapter(appInfoListViewAdapter);

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
            String newName = editText.getText().toString();
            if(appLabelsCrud.updateLabelName(this.account,labelName,newName)){

                for (AppInfo appInfo : this.appInfoList) {
                    appInfo.setLabelName(newName);
                    appInfo.updateAll("account=? and labelName=?",this.account,labelName);
                }
                this.appInfoListViewAdapter.setData(this.appInfoList);
                this.appInfoListViewAdapter.notifyDataSetChanged();
                this.appInfoListView.setAdapter(this.appInfoListViewAdapter);
                this.labelNameTextView.setText(newName);
                this.labelName = newName;
            }else{
                BaseActivity.alertHandler(this,"已有分类！");
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
    }
}