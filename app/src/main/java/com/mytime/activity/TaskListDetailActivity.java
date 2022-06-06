package com.mytime.activity;

import androidx.annotation.RequiresApi;


import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mytime.R;
import com.mytime.adapter.ScheduleListItemViewAdapter;
import com.mytime.crud.TaskListCrud;
import com.mytime.crud.TaskListCrudImpl;
import com.mytime.model.AppUsageDetailInfo;
import com.mytime.model.LabelInfo;
import com.mytime.model.ScheduleListItemInfo;
import com.mytime.model.TaskListItemInfo;
import com.mytime.util.FileUtil;
import com.mytime.util.StringUtil;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TaskListDetailActivity extends BaseActivity {

    private List<LabelInfo> labelInfoList;
    private List<ScheduleListItemInfo> scheduleListItemInfoList;
    private TaskListItemInfo taskListItemInfo;
    private ScheduleListItemViewAdapter scheduleListItemViewAdapter;
    private TaskListCrud taskListCrud;
    private String account;
    String listName;


    private ImageView listDetailImage;
    private TextView listNameTextView;
    private TextView listDescription;
    private TextView listCreateTime;
    private TextView listFinishTime;
    private Button insertScheduleButton;
    private LinearLayout selectScheduleList;
    private CheckBox selectAllSchedule;
    private ImageView listUpdate;
    private TextView deleteScheduleItem;
    private ListView scheduleListView;
    private Button generateRoutineButton;
    private TextView generateTextView;

    private boolean selectFlag;

    String directory = "./src/main/res/file";
    String fileName = "appUsageInfo.csv";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list_detail);

        this.listDetailImage = findViewById(R.id.list_detail);
        this.listNameTextView = findViewById(R.id.list_name);
        this.listDescription = findViewById(R.id.description_list);
        this.listCreateTime = findViewById(R.id.create_time);
        this.listFinishTime = findViewById(R.id.finish_list);
        this.insertScheduleButton = findViewById(R.id.insert_schedule);
        this.selectScheduleList = findViewById(R.id.select_schedule_list);
        this.selectAllSchedule = findViewById(R.id.select_all_schedule);
        this.listUpdate = findViewById(R.id.update_list);
        this.deleteScheduleItem = findViewById(R.id.delete_schedule_item);
        this.scheduleListView = findViewById(R.id.schedule_item_list);
        this.generateRoutineButton = findViewById(R.id.generate_route);
        this.generateTextView = findViewById(R.id.generate_route_textview);
        this.scheduleListItemViewAdapter = new ScheduleListItemViewAdapter(this);

        this.selectFlag = false;
        init();

        if (this.taskListItemInfo.isTaskListState()){
            this.insertScheduleButton.setEnabled(false);
            this.generateRoutineButton.setEnabled(false);
        }
        this.listDetailImage.setOnClickListener(v->{
            finish();
        });
        this.listNameTextView.setOnClickListener(v->{
            String taskListName = this.listNameTextView.getText().toString();
            String description = this.listDescription.getText().toString();
            this.popUpdateAlertDialog(taskListName,description);

        });

        this.scheduleListView.setOnItemClickListener((parent, view, position, id) -> {
            ImageView finishItem = view.findViewById(R.id.manage_item);
            ImageView deleteItem = view.findViewById(R.id.delete_item);
            ImageView updateItem = view.findViewById(R.id.update_item);

            ScheduleListItemInfo scheduleListItemInfo = this.scheduleListItemViewAdapter.getItem(position);
            String itemName = scheduleListItemInfo.getItemName();
            String itemLabel = scheduleListItemInfo.getLabelName();
            String listName = scheduleListItemInfo.getListName();
            boolean state = scheduleListItemInfo.getSate();

            updateItem.setOnClickListener(v -> {
                this.showInsertScheduleDialog(listName,itemName,itemLabel);
            });

            finishItem.setOnClickListener(v->{
                this.popFinishAlertDialog(itemName,listName,itemLabel,scheduleListItemInfo);
            });

            deleteItem.setOnClickListener(v->{
                this.popDeleteAlertDialog(itemName,listName,position);
            });

        });




        this.listUpdate.setOnClickListener(v -> {
            if(!selectFlag){
                selectFlag = true;
                selectAllSchedule.setChecked(false);
                selectScheduleList.setVisibility(View.VISIBLE);
                for (int i = 0; i < this.scheduleListView.getChildCount(); i++) {
                    this.scheduleListView.getChildAt(i).findViewById(R.id.select_schedule_list_item).setVisibility(View.VISIBLE);
                }


            }
            else{
                selectFlag = false;
                selectScheduleList.setVisibility(View.GONE);
                this.scheduleListItemViewAdapter.initCheck(false);
                for (int i = 0; i < this.scheduleListView.getChildCount(); i++) {
                    this.scheduleListView.getChildAt(i).findViewById(R.id.select_schedule_list_item).setVisibility(View.INVISIBLE);
                }
            }
        });

        this.selectAllSchedule.setOnClickListener(
                v -> {
                    this.scheduleListItemViewAdapter.initCheck(selectAllSchedule.isChecked());
                    this.scheduleListItemViewAdapter.notifyDataSetChanged();

                }
        );

        this.deleteScheduleItem.setOnClickListener(v->{
            Map<Integer, Boolean> isCheck = this.scheduleListItemViewAdapter.getMap();
            int count = this.scheduleListItemViewAdapter.getCount();
            for (int i = 0; i < count; i++) {
                int position = i - (count - this.scheduleListItemViewAdapter.getCount());
                if (isCheck.get(i) != null && isCheck.get(i)) {
                    String itemName = this.scheduleListItemViewAdapter.getItem(position).getItemName();
                    LitePal.deleteAll(ScheduleListItemInfo.class,"itemName=?",itemName);
                    isCheck.remove(i);
                    this.scheduleListItemViewAdapter.removeData(position);
                }
            }
            if(this.selectAllSchedule.isChecked()) this.selectAllSchedule.setChecked(false);
            this.scheduleListItemViewAdapter.notifyDataSetChanged();
        });

        this.insertScheduleButton.setOnClickListener(v -> {
            String taskListName = this.listNameTextView.getText().toString();
            this.showInsertScheduleDialog(taskListName,"","");
        });

        this.generateRoutineButton.setOnClickListener(v->{

            List<ScheduleListItemInfo> sListItemInfoList = LitePal.where("account=? and listName=? and state = 0", this.account, this.listName)
                    .find(ScheduleListItemInfo.class);
            List<LabelInfo> labelInfoList = LitePal.findAll(LabelInfo.class);

            if (labelInfoList.isEmpty()){
                BaseActivity.alertHandler(this,"请先在设置中进行软件分类！");
            }

            boolean flag = false;
            for (ScheduleListItemInfo scheduleListItemInfo : sListItemInfoList) {
                if (scheduleListItemInfo.getLabelName()!=null){
                    flag = true;
                    break;
                }
            }
            if(sListItemInfoList.isEmpty()|| !flag){
                BaseActivity.alertHandler(this,"请先创建带标签的待办事项！");
            }

            int len = labelInfoList.size();
            List<List<Integer>> timeArray = new ArrayList<>();
            for (int i = 0; i < len; i++) {
                List<Integer> temp = new ArrayList<>();
                timeArray.add(temp);
            }

            List<AppUsageDetailInfo> appUsageDetailInfoList = LitePal.where("labelType>=0").find(AppUsageDetailInfo.class);

            for (AppUsageDetailInfo appUsageDetailInfo : appUsageDetailInfoList) {
                String str = StringUtil.appUsageInfoToString(appUsageDetailInfo);
                try {
                    FileUtil.context = this;
                    FileUtil.writeInFile(str,this.fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                String date = appUsageDetailInfo.getLastUsedTime();
                long timeStamp = 0L;
                try {
                    timeStamp = StringUtil.dateToStamp(date);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date(timeStamp));
                int hour = calendar.get(Calendar.HOUR);
                int labelType = appUsageDetailInfo.getLabelType();
                timeArray.get(labelType-1).add(hour);
            }

            List<Integer> sumList = new ArrayList<>();
            for (int i = 0; i < len; i++) {
                sumList.add(timeArray.get(i).size());
            }

            List<List<Double>> probabilities = new ArrayList<>();
            boolean flagAll = false;
            for (List<Integer> integers : timeArray) {

                int size = integers.size();
                double p =1.0/size;


                double[] pList = {0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
                for (Integer integer : integers) {
                    pList[integer] += p;
                    if (pList[integer]!=0) flagAll = true;
                }

                List<Double> tempList = new ArrayList<>();
                for (int i = pList.length-1; i >=0 ; i--) {
                    tempList.add(pList[i]);
                }
                probabilities.add(tempList);

            }

            if (!flagAll){
                BaseActivity.alertHandler(this,"没有可参考的数据，分类失败！");
            }

            List<String> suggestion = new ArrayList<>();
            String str = "";
            for (ScheduleListItemInfo scheduleListItemInfo : sListItemInfoList) {
                String labelName = scheduleListItemInfo.getLabelName();
                int labelType = -1;
                for (int i = 0; i < this.labelInfoList.size(); i++) {
                    if (this.labelInfoList.get(i).getLabelName().equals(labelName)){
                        labelType = i;
                        break;
                    }
                }
                if (labelType!=-1){
                    List<Double> p = probabilities.get(labelType);
                    Double max = p.get(0);
                    int i_max = -1;
                    for (int i = 0; i < p.size(); i++) {
                        if(p.get(i)>max){
                            max = p.get(i);
                            i_max = i;
                        }
                    }
                    if (i_max>=0) {
                        str += scheduleListItemInfo.getItemName() + "可以在" + (i_max) + "点开始进行\n";
                        suggestion.add(str);
                    }
                }

            }
            this.generateTextView.setText(str);
        });
    }

    private void init(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        this.account = bundle.getString("account");
        this.listName = bundle.getString("listName");
        Log.d("任务清单名为：", listName);

        List<TaskListItemInfo> taskListItemInfo = LitePal.where("account=? and taskListName=?",this.account, listName).find(TaskListItemInfo.class);
        String description = taskListItemInfo.get(0).getTaskListDescription();
        String createTime = taskListItemInfo.get(0).getCreateDate();
        String finishTime = taskListItemInfo.get(0).getFinishDate();
        this.taskListItemInfo = taskListItemInfo.get(0);

        if("".equals(description)) description = "--";
        if ("".equals(finishTime)) finishTime = "--";


        this.listNameTextView.setText(listName);
        this.listDescription.setText(description);
        this.listCreateTime.setText(createTime);
        this.listFinishTime.setText(finishTime);


        this.scheduleListItemInfoList = LitePal.where("account=? and listName=?",this.account, listName)
                .find(ScheduleListItemInfo.class);

        for (ScheduleListItemInfo scheduleListItemInfo : scheduleListItemInfoList) {
            Log.d("待办事项有：",scheduleListItemInfo.getItemName());
        }

        this.scheduleListItemViewAdapter.setData(this.scheduleListItemInfoList);
        this.scheduleListView.setAdapter(this.scheduleListItemViewAdapter);

        this.taskListCrud = new TaskListCrudImpl();
        this.labelInfoList = LitePal.findAll(LabelInfo.class);
    }


    private void popUpdateAlertDialog(String listName,String description){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("修改待办清单名称");
        EditText editText = new EditText(this);
        editText.setMaxLines(1);
        editText.setSingleLine(true);
        editText.setText(listName);
        builder.setView(editText);
        builder.setPositiveButton("确定", (dialog, which) -> {
            String newName = editText.getText().toString();
            List<TaskListItemInfo> taskListItemInfoList = LitePal.where("account=?  and taskListName=?",this.account,newName).find(TaskListItemInfo.class);
            if(taskListItemInfoList.isEmpty()){
                taskListCrud.updateTaskListItemInfoByName(listName,newName,description,this.account);
                this.listNameTextView.setText(newName);
                BaseActivity.alertHandler(this,"待办清单名称修改成功！");

            }
            else{
                BaseActivity.alertHandler(this,"已有清单！");
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showInsertScheduleDialog(String taskListName,String itemName,String labelName){

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_schedule_insert,null);

        TextView scheduleLabelName = view.findViewById(R.id.label_name);
        scheduleLabelName.setText(labelName);

        EditText scheduleName = view.findViewById(R.id.schedule_name);
        if(!itemName.equals("")){
            scheduleName.setText(itemName);
        }

        RadioGroup labelRadioGroup = view.findViewById(R.id.label_group);

        List<LabelInfo> labelInfoList = LitePal.where("account=?",this.account).find(LabelInfo.class);
        for (LabelInfo labelInfo : labelInfoList) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(labelInfo.getLabelName());
            labelRadioGroup.addView(radioButton,LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        }

        ScheduleListItemInfo scheduleListItemInfo = new ScheduleListItemInfo();
        scheduleListItemInfo.setAccount(this.account);
        scheduleListItemInfo.setListName(taskListName);
        scheduleListItemInfo.setState(false);
        labelRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton tempRadioButton = group.findViewById(checkedId);
            Log.d("选择的是：",tempRadioButton.getText().toString()+"分类");
            scheduleListItemInfo.setLabelName(tempRadioButton.getText().toString());
        });

        Button yesBtn = view.findViewById(R.id.insert_yes);
        Button cancelBtn = view.findViewById(R.id.insert_no);
        yesBtn.setOnClickListener(v->{
            String name = scheduleName.getText().toString();
            if(itemName.isEmpty()){
                if(name.isEmpty()){
                    BaseActivity.alertHandler(this,"待办事项名称不能为空！");
                }else{
                    List<TaskListItemInfo> taskListItemInfoList = LitePal.where("account=? and taskListName=?",this.account,taskListName).find(TaskListItemInfo.class);
                    List<ScheduleListItemInfo> scheduleListItemInfoList =
                            LitePal.where("account=? and listName=?",this.account,taskListName)
                                    .find(ScheduleListItemInfo.class);

                    boolean flag = true;
                    for (ScheduleListItemInfo listItemInfo : scheduleListItemInfoList) {
                        if(listItemInfo.getItemName().equals(name)){
                            flag = false;
                            break;
                        }
                    }

                    if(flag) {
                        scheduleListItemInfo.setItemName(name);
                        scheduleListItemInfo.save();
                        this.scheduleListItemViewAdapter.addData(scheduleListItemInfo);
                        this.scheduleListItemViewAdapter.notifyDataSetChanged();
                        this.scheduleListView.setAdapter(this.scheduleListItemViewAdapter);
                        BaseActivity.alertHandler(this,"待办事项创建成功！");
                    }else{
                        BaseActivity.alertHandler(this,"待办事项名称已存在！");
                    }
                }
            }
            else{
                if(name.isEmpty()){
                    BaseActivity.alertHandler(this,"待办事项名称不能为空！");
                }else{
                    List<ScheduleListItemInfo> scheduleListItemInfoList =
                            LitePal.where("account=? and listName=?",this.account,taskListName)
                                    .find(ScheduleListItemInfo.class);
                    scheduleListItemInfo.setItemName(name);
                    scheduleListItemInfo.updateAll("itemName=? and account=? and listName=?",itemName,this.account,taskListName);
                    List<ScheduleListItemInfo> scheduleListItemInfoList1 = LitePal.where("account=? and listName=?",this.account,taskListName).find(ScheduleListItemInfo.class);
                    this.scheduleListItemViewAdapter.setData(scheduleListItemInfoList1);
                    this.scheduleListItemViewAdapter.notifyDataSetChanged();
                    this.scheduleListView.setAdapter(this.scheduleListItemViewAdapter);
                    BaseActivity.alertHandler(this,"待办事项更改成功！");
                }
            }


            bottomSheetDialog.dismiss();
        });

        cancelBtn.setOnClickListener(v-> bottomSheetDialog.cancel());
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.show();
    }

    private void popUpdateDescriptionAlertDialog(String listName,String description){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("修改待办清单描述");
        EditText editText = new EditText(this);
        editText.setMaxLines(1);
        editText.setSingleLine(true);
        editText.setText(description);
        builder.setView(editText);
        builder.setPositiveButton("确定", (dialog, which) -> {
            String newDescription = editText.getText().toString();
            taskListCrud.updateTaskListItemInfoByDescription(listName,newDescription,this.account);
            this.deleteScheduleItem.setText(newDescription);
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void popDeleteAlertDialog(String itemName,String listName,int position){

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("提示");
        dialog.setMessage("是否确定删除选中待办事项？");
        dialog.setCancelable(false); //设置按下返回键不能消失
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LitePal.deleteAll("ScheduleListItemInfo","account=? and listName=? and itemName=?",account,listName,itemName);
                scheduleListItemViewAdapter.removeData(position);
                scheduleListItemViewAdapter.notifyDataSetChanged();
            }
        });
        dialog.setNegativeButton("取消", (dialog1, which) -> dialog1.cancel());
        dialog.show();//显示弹出窗口
    }

    private void popFinishAlertDialog(String itemName,String listName,String labelName,ScheduleListItemInfo scheduleListItemInfo1){

        ScheduleListItemInfo scheduleListItemInfo = scheduleListItemInfo1;
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("提示");
        dialog.setMessage("是否已完成选中待办事项？");
        dialog.setCancelable(false); //设置按下返回键不能消失
        dialog.setPositiveButton("是", (dialog12, which) -> {
//            ScheduleListItemInfo scheduleListItemInfo = new ScheduleListItemInfo(account,itemName,labelName,listName);
            scheduleListItemInfo.setState(true);
            scheduleListItemInfo.setFinishTime();
            scheduleListItemInfo.updateAll("account=? and listName=? and itemName=?",account,listName,itemName);
            this.scheduleListItemInfoList = LitePal.where("account=? and listName=?",this.account, listName)
                    .find(ScheduleListItemInfo.class);

//            scheduleListItemViewAdapter.setData(scheduleListItemInfoList);
            scheduleListItemViewAdapter.notifyDataSetChanged();

        });
        dialog.setNegativeButton("否", (dialog1, which) -> dialog1.cancel());
        dialog.show();//显示弹出窗口
    }

}