package com.mytime;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.mytime.activity.LoginActivity;
import com.mytime.activity.MainActivity;
import com.mytime.model.AppInfo;
import com.mytime.model.AppUsageDetailInfo;
import com.mytime.model.LabelInfo;
import com.mytime.model.UserPwdInfo;

import org.litepal.LitePal;

import java.util.List;

public class MyTimeApplication extends Application {

    private static MyTimeApplication mApplication;

    List<UserPwdInfo> userPwdInfoList;

    private boolean checkUserPwdInfo(){
        userPwdInfoList = LitePal.findAll(UserPwdInfo.class);
        return !userPwdInfoList.isEmpty();
    }
    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);


        if (this.checkUserPwdInfo()){
            String[] accounts = new String[userPwdInfoList.size()];
            for (UserPwdInfo userPwdInfo : this.userPwdInfoList) {
                accounts[this.userPwdInfoList.indexOf(userPwdInfo)]=(userPwdInfo.getAccount());
            }
            new AlertDialog.Builder(this)
                    .setTitle("请选择要登录的账号：")
                    .setSingleChoiceItems(accounts, 0, (dialog, which) -> {
                        Intent intent = new Intent(getApplication(), MainActivity.class);
                        intent.putExtra("account", accounts[which]);
                        startActivity(intent);
                    });
        }


        LitePal.deleteAll(AppUsageDetailInfo.class);
        mApplication = this;
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        mApplication = null;
    }


    public static Context getApplication() {
        return mApplication;
    }
}
