package com.mytime.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mytime.R;
import com.mytime.adapter.AppInfoListViewAdapter;
import com.mytime.model.AppInfo;
import com.mytime.model.AppUsageDetailInfo;
import com.mytime.model.LabelInfo;
import com.mytime.model.LabelOfAppInfo;
import com.mytime.util.AppUtil;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AllAppInfoActivity extends AppCompatActivity {

    List<AppInfo> appInfoList;
    String account;
    String labelName;
    boolean selectFlag;
    AppInfoListViewAdapter appInfoListViewAdapter;
    List<LabelInfo> labelInfoList;

    ImageView backImageView;
    ImageView selectImageView;
    LinearLayout selectLinearLayout;
    CheckBox checkAll;
    TextView addTextView;
    ListView appListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_app_info);

        this.backImageView = findViewById(R.id.back_img);
        this.selectImageView = findViewById(R.id.select_app_list_item);
        this.selectLinearLayout = findViewById(R.id.select_list_layout);
        this.checkAll = findViewById(R.id.select_all);
        this.addTextView = findViewById(R.id.add_app);
        this.appListView = findViewById(R.id.app_info_list);
        this.appInfoListViewAdapter = new AppInfoListViewAdapter(this);



        Intent intent = getIntent();
        this.account = intent.getStringExtra("account");
        this.labelName = intent.getStringExtra("label");
        Log.d("分类名称：",this.labelName);
        init();

        this.backImageView.setOnClickListener(v-> finish());
        this.selectImageView.setOnClickListener(v->{
            if(!selectFlag){
                selectFlag = true;
                this.checkAll.setChecked(false);
                this.selectLinearLayout.setVisibility(View.VISIBLE);
                for(int i=0;i<this.appListView.getChildCount();i++){
                    this.appListView.getChildAt(i).findViewById(R.id.checkbox).setVisibility(View.VISIBLE);
                }
            }
            else{
                selectFlag = false;
                this.selectLinearLayout.setVisibility(View.GONE);
                appInfoListViewAdapter.initCheck(false);
                for(int i=0;i<this.appListView.getChildCount();i++){
                    this.appListView.getChildAt(i).findViewById(R.id.checkbox).setVisibility(View.GONE);
                }
            }
        });

        this.checkAll.setOnClickListener(
                v -> {
                    Map<Integer, Boolean> isCheck = appInfoListViewAdapter.getMap();
                    appInfoListViewAdapter.initCheck(checkAll.isChecked());
                    appInfoListViewAdapter.notifyDataSetChanged();
                }
        );

        this.addTextView.setOnClickListener(v->{
            Map<Integer, Boolean> isCheck = this.appInfoListViewAdapter.getMap();
            int count = this.appInfoListViewAdapter.getCount();
            for (int i = 0; i < count; i++) {
                int position = i - (count - this.appInfoListViewAdapter.getCount());
                if (isCheck.get(i) != null && isCheck.get(i)) {
                    String itemName = this.appInfoListViewAdapter.getItem(position).getAppName();
                    String itemPackageName = this.appInfoListViewAdapter.getItem(position).getPackageName();
                    Drawable itemIcon = this.appInfoListViewAdapter.getItem(position).getAppIcon();



                    AppInfo appInfo = this.appInfoListViewAdapter.getItem(position);
                    appInfo.setLabelName(this.labelName);
                    appInfo.updateAll("account=? and appName=?",this.account,appInfo.getAppName());

                    int labelType = -1;

                    for (int j = 0; j < this.labelInfoList.size(); j++) {
                        if (this.labelInfoList.get(j).getLabelName().equals(labelName))
                            labelType = j+1;
                    }


                    itemName = itemName.replaceAll(" ","");
                    List<AppUsageDetailInfo> appUsageDetailInfoList = LitePal.where("account=? and appName=?",account,itemName).find(AppUsageDetailInfo.class);
                    for (AppUsageDetailInfo usageDetailInfo : appUsageDetailInfoList) {
                        String appName = usageDetailInfo.getAppName();
                        Log.d("应用名称：",appName);
                        usageDetailInfo.setLabelType(labelType);
                        usageDetailInfo.updateAll("appName=? and account=?",itemName,account);
                    }

                    LabelOfAppInfo labelOfAppInfo = new LabelOfAppInfo();
                    labelOfAppInfo.setLabelType(labelType);
                    labelOfAppInfo.setAppName(itemName);
                    labelOfAppInfo.setAccount(account);
                    labelOfAppInfo.save();
                    isCheck.remove(i);
                    this.appInfoListViewAdapter.removeData(position);
                }
            }
            if(this.checkAll.isChecked()) this.checkAll.setChecked(false);
            this.appInfoListViewAdapter.notifyDataSetChanged();
        });
    }

    public void init(){
        labelInfoList = LitePal.findAll(LabelInfo.class);
        this.appInfoList = new ArrayList<>();


        this.appInfoList = AppUtil.scanLocalInstallAppList(AllAppInfoActivity.this.getPackageManager(),this.account);

        for (int i =0;i<this.appInfoList.size();i++){
            List<AppInfo> appInfoListTemp = LitePal.where("account=? and appName=?",this.account,this.appInfoList.get(i).getAppName()).find(AppInfo.class);
            if (!appInfoListTemp.isEmpty()) {
                if (appInfoListTemp.get(0).getLabelName() != null) {
                    this.appInfoList.remove(i);
                    i-=1;
                } else {
                    this.appInfoList.get(i).save();
                }
            }
        }
        this.appInfoListViewAdapter.setData(appInfoList);
        this.appListView.setAdapter(this.appInfoListViewAdapter);

        for (AppInfo appInfo : appInfoList) {
            Log.d("应用名称：",appInfo.getAppName());
            if (appInfo.getLabelName()==null)
            {
                Log.d("应用分类：","空");
                continue;
            }

            Log.d("应用分类：",appInfo.getLabelName());
        }
        Log.d(" ","-----------------------------------------------------------------------------");
        List<AppInfo> test = LitePal.findAll(AppInfo.class);
        for (AppInfo appInfo : test) {
            Log.d("应用名称：",appInfo.getAppName());
            if (appInfo.getLabelName()==null)
            {
                Log.d("应用分类：","空");
                continue;
            }

            Log.d("应用分类：",appInfo.getLabelName());        }
        this.selectFlag = false;
    }
}