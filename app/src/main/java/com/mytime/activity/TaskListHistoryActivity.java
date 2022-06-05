package com.mytime.activity;

import androidx.annotation.RequiresApi;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mytime.R;
import com.mytime.adapter.TaskListItemViewHistoryAdapter;
import com.mytime.crud.TaskListCrud;
import com.mytime.crud.TaskListCrudImpl;
import com.mytime.model.ScheduleListItemInfo;
import com.mytime.model.TaskListItemInfo;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TaskListHistoryActivity extends BaseActivity{

    private ImageView selectHistoryTaskItem;
    private ListView taskListHistoryItem;
    private CheckBox checkAllHistory;
    private LinearLayout selectHistoryLinearLayout;
    private TextView deleteHistoryTextView;
    private ImageView historyList;

    private TaskListItemViewHistoryAdapter taskListItemViewHistoryAdapter;
    private TaskListCrud taskListCrud;

    private String account;
    private boolean selectFlag = false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list_history);

        init();

        this.historyList.setOnClickListener(v->{

            finish();
        });

        // listview item的点击事件反应
        this.taskListHistoryItem.setOnItemClickListener((parent, view1, position, id) -> {
            ImageView updateImage = view1.findViewById(R.id.update_task_list_item_history);
            ImageView deleteImage = view1.findViewById(R.id.delete_task_list_item_history);

            String itemName = this.taskListItemViewHistoryAdapter.getItem(position).getTaskListName();
            String description = this.taskListItemViewHistoryAdapter.getItem(position).getTaskListDescription();

            TaskListItemInfo taskListItemInfo = this.taskListItemViewHistoryAdapter.getItem(position);

            updateImage.setOnClickListener(v-> this.showBottomSheetDialog(itemName,description));

            deleteImage.setOnClickListener(v -> this.popDeleteAlertDialog(itemName,position));

            view1.setOnClickListener(v -> {
                Intent intent = new Intent(TaskListHistoryActivity.this,TaskListDetailActivity.class);
                intent.putExtra("account",this.account);
                intent.putExtra("listName",itemName);
                startActivity(intent);
            });
        });


        // 选中清单
        this.selectHistoryTaskItem.setOnClickListener(v -> {
            if(!selectFlag){
                selectFlag = true;
                checkAllHistory.setChecked(false);
                selectHistoryLinearLayout.setVisibility(View.VISIBLE);
                for (int i = 0; i < taskListHistoryItem.getChildCount(); i++) {
                    taskListHistoryItem.getChildAt(i).findViewById(R.id.select_task_list_item_history).setVisibility(View.VISIBLE);
                }
            }
            else{
                selectFlag = false;
                selectHistoryLinearLayout.setVisibility(View.GONE);
                taskListItemViewHistoryAdapter.initCheck(false);
                for (int i = 0; i < taskListHistoryItem.getChildCount(); i++) {
                    taskListHistoryItem.getChildAt(i).findViewById(R.id.select_task_list_item_history).setVisibility(View.GONE);
                }
            }
        });

        //全选
        this.checkAllHistory.setOnClickListener(
                v -> {
                    taskListItemViewHistoryAdapter.initCheck(checkAllHistory.isChecked());
                    taskListItemViewHistoryAdapter.notifyDataSetChanged();
                }
        );

        this.deleteHistoryTextView.setOnClickListener(v->{
            Map<Integer, Boolean> isCheck = this.taskListItemViewHistoryAdapter.getMap();
            // 获取到条目数量。map.size = list.size,所以
            int count = this.taskListItemViewHistoryAdapter.getCount();
            // 遍历
            for (int i = 0; i < count; i++) {
                // 删除有两个map和list都要删除 ,计算方式
                int position = i - (count - this.taskListItemViewHistoryAdapter.getCount());
                // 推断状态 true为删除
                if (isCheck.get(i) != null && isCheck.get(i)) {
                    // 数据库删除数据
                    String itemName = this.taskListItemViewHistoryAdapter.getItem(position).getTaskListName();
                    LitePal.deleteAll(TaskListItemInfo.class,"taskListName=?",itemName);
                    // listview删除数据
                    isCheck.remove(i);
                    this.taskListItemViewHistoryAdapter.removeData(position);
                }
            }
            if(this.checkAllHistory.isChecked()) this.checkAllHistory.setChecked(false);
            this.taskListItemViewHistoryAdapter.notifyDataSetChanged();
        });
    }

    public void init(){

        this.taskListHistoryItem = findViewById(R.id.task_list_history_view);
        this.selectHistoryTaskItem = findViewById(R.id.select_task_list_item);
        this.checkAllHistory = findViewById(R.id.select_all_history);
        this.selectHistoryLinearLayout = findViewById(R.id.select_list_layout_history);
        this.deleteHistoryTextView = findViewById(R.id.delete_items_history);
        this.taskListItemViewHistoryAdapter = new TaskListItemViewHistoryAdapter(this);
        taskListCrud = new TaskListCrudImpl();

        this.historyList = findViewById(R.id.history_list);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        this.account = bundle.getString("account");



        // 待办事项清单内部待办事项数据绑定
        List<TaskListItemInfo> taskListItemList = LitePal.where("account=? and taskListState=?",this.account,"1").find(TaskListItemInfo.class);

//        if(taskListItemList.isEmpty()){
//
//            ScheduleListItemInfo scheduleListItemInfo = new ScheduleListItemInfo(this.account,"好好学习","学习","海绵宝宝和派大星历史清单");
//            ScheduleListItemInfo scheduleListItemInfo1 = new ScheduleListItemInfo(this.account,"天天向上","学习","海绵宝宝和派大星历史清单");
//            ScheduleListItemInfo scheduleListItemInfo2 = new ScheduleListItemInfo(this.account,"天道酬勤","学习","海绵宝宝和派大星历史清单");
//
//            scheduleListItemInfo1.setState(true);
//            scheduleListItemInfo2.setState(true);
//            scheduleListItemInfo.setState(true);
//
//            scheduleListItemInfo1.save();
//            scheduleListItemInfo.save();
//            scheduleListItemInfo2.save();
//
//            // 待办事项数据绑定
//            TaskListItemInfo taskListItemInfo = new TaskListItemInfo(this.account,"海绵宝宝和派大星历史清单","章鱼哥才是我们社畜的真实写照");
//            taskListItemInfo.setTaskListState(true);
//            taskListItemInfo.save();
//        }
//        else {
//            for (TaskListItemInfo taskListItemInfo : taskListItemList) {
//                Log.d("清单名称",taskListItemInfo.getTaskListName());
//
//            }
//        }
        this.taskListItemViewHistoryAdapter.setData(taskListItemList);
        this.taskListHistoryItem.setAdapter(this.taskListItemViewHistoryAdapter);


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showBottomSheetDialog(String itemName,String itemDescription){

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);

        View view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_dialog,null);
        EditText taskListName = view.findViewById(R.id.task_list_name);
        EditText taskListDescription = view.findViewById(R.id.task_list_description);
        Button yesBtn = view.findViewById(R.id.yes);
        Button cancelBtn = view.findViewById(R.id.no);

        taskListName.setText(itemName);
        taskListDescription.setText(itemDescription);

        yesBtn.setOnClickListener(v->{
            String name = taskListName.getText().toString();
            String description = taskListDescription.getText().toString();

            if(name.isEmpty()){
                BaseActivity.alertHandler(this,"待办事项清单名称不能为空！");
            }else{

                taskListCrud.updateTaskListItemInfoByName(itemName,name,description,this.account);
                List<TaskListItemInfo> taskListItemList = LitePal.where("account=? and taskListState=?",this.account,"1").find(TaskListItemInfo.class);
                this.taskListItemViewHistoryAdapter.setData(taskListItemList);
                this.taskListItemViewHistoryAdapter.notifyDataSetChanged();
                this.taskListHistoryItem.setAdapter(this.taskListItemViewHistoryAdapter);

                BaseActivity.alertHandler(this,"待办事项清单修改成功！");

            }

            bottomSheetDialog.dismiss();
        });

        cancelBtn.setOnClickListener(v-> bottomSheetDialog.cancel());
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.show();
    }

    private void popDeleteAlertDialog(String itemName,int position){

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("提示");
        dialog.setMessage("删除待办事项清单，其中待办事项也会同时删除，是否确定删除选中待办事项清单？");
        dialog.setCancelable(false); //设置按下返回键不能消失
        dialog.setPositiveButton("确定", (dialog1, which) -> {
            this.taskListCrud.deleteTaskListItem(account,itemName);
            this.taskListItemViewHistoryAdapter.removeData(position);
            this.taskListItemViewHistoryAdapter.notifyDataSetChanged();
            this.taskListHistoryItem.setAdapter(this.taskListItemViewHistoryAdapter);
        });
        dialog.setNegativeButton("取消", (dialog12, which) -> dialog12.cancel());
        dialog.show();//显示弹出窗口
    }

}