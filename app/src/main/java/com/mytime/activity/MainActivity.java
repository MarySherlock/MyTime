package com.mytime.activity;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mytime.R;
import com.mytime.crud.AppLabelsCrud;
import com.mytime.crud.AppLabelsCrudImpl;
import com.mytime.fragment.TaskListFragment;
import com.mytime.fragment.TimeStatisticsFragment;
import com.mytime.fragment.UserInfoFragment;



public class MainActivity extends BaseActivity{

    TimeStatisticsFragment timeStatisticsFragment;
    TaskListFragment taskListFragment;
    UserInfoFragment userInfoFragment;

    String account;
    boolean permissionFlag = false;
    AppLabelsCrud appLabelsCrud = new AppLabelsCrudImpl();

    @SuppressLint("NonConstantResourceId")
    private final BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.time_statistics:
                replaceFragment(timeStatisticsFragment);
                return true;
            case R.id.task_list_view:
                replaceFragment(taskListFragment);
                return true;
            case R.id.user:
                replaceFragment(userInfoFragment);
                return true;
        }

        return false;
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        replaceFragment(timeStatisticsFragment);
        BottomNavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction translation = fragmentManager.beginTransaction();
        translation.replace(R.id.linearlayout1, fragment);
        translation.commit();
    }
    public void init(){
        timeStatisticsFragment = new TimeStatisticsFragment();
        taskListFragment = new TaskListFragment();
        userInfoFragment = new UserInfoFragment();

        Intent intent = getIntent();
        this.account = intent.getStringExtra("account");

        if(this.checkUsagePermission()) this.permissionFlag=true;

    }


    public String getAccount(){
        return this.account;
    }
    public boolean getPermissionFlag(){ return this.permissionFlag; }

    private boolean checkUsagePermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            mode = appOps.checkOpNoThrow("android:get_usage_stats", android.os.Process.myUid(), getPackageName());
            boolean granted = mode == AppOpsManager.MODE_ALLOWED;
            if (!granted) {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                startActivityForResult(intent, 1);
                return false;
            }
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            mode = appOps.checkOpNoThrow("android:get_usage_stats", android.os.Process.myUid(), getPackageName());
            boolean granted = mode == AppOpsManager.MODE_ALLOWED;
            if (!granted) {
                Toast.makeText(this, "请开启该权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

}