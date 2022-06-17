package com.mytime.activity;


import androidx.appcompat.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mytime.model.UserPwdInfo;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.List;
import java.util.Objects;


public class BaseActivity extends AppCompatActivity {

    private AlertDialog alertDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("当前的活动是",getClass().getSimpleName());
        ActivityController.addActivity(this);

        List<UserPwdInfo> userPwdInfos = LitePal.findAll(UserPwdInfo.class);
        for (UserPwdInfo userPwdInfo : userPwdInfos) {
            Log.d("username: ",userPwdInfo.getAccount()+'\n');
            Log.d("password",userPwdInfo.getPassword()+'\n');
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityController.removeActivity(this);
    }


    public static void alertHandler(Context context, String string){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            Toast.makeText(context, string, Toast.LENGTH_LONG).show();
        });
    }


    public void showLoading() {
        showLoading("数据加载中...");
    }

    public void showLoading(String msg) {
        alertDialog = new AlertDialog.Builder(this).create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable());
        alertDialog.setCancelable(false);
        alertDialog.setOnKeyListener((dialog, keyCode, event) -> {
            return keyCode == KeyEvent.KEYCODE_SEARCH || keyCode == KeyEvent.KEYCODE_BACK;
        });
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);
    }

    public void hideLoading() {
        if (null != alertDialog && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        alertDialog = null;
    }
}
