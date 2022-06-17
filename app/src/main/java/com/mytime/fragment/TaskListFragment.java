package com.mytime.fragment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mytime.R;
import com.mytime.activity.BaseActivity;
import com.mytime.activity.MainActivity;
import com.mytime.activity.TaskListDetailActivity;
import com.mytime.activity.TaskListHistoryActivity;
import com.mytime.adapter.TaskListItemViewAdapter;
import com.mytime.crud.TaskListCrud;
import com.mytime.crud.TaskListCrudImpl;
import com.mytime.model.LabelInfo;
import com.mytime.model.TaskListItemInfo;
import com.mytime.model.ScheduleListItemInfo;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class TaskListFragment extends Fragment {

    private ImageView listHistory;
    private ImageView addTaskListItem;
    private ImageView selectTaskItem;
    private ListView taskListItem;
    private CheckBox checkAll;
    private LinearLayout selectLinearLayout;
    private TextView deleteTextView;

    private TaskListItemViewAdapter taskListItemViewAdapter;
    private TaskListCrud taskListCrud;

    private String account;
    private boolean selectFlag = false;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("当前生命周期：","onCreate");

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("当前生命周期：","onActivityCreated");


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Log.d("当前生命周期：","onCreateView");


        View view = inflater.inflate(R.layout.fragment_task_list, container, false);
        this.listHistory = view.findViewById(R.id.history_list);
        this.addTaskListItem = view.findViewById(R.id.add_task_list_item);
        this.selectTaskItem = view.findViewById(R.id.select_task_list_item);
        this.checkAll = view.findViewById(R.id.select_all);
        this.taskListItem = view.findViewById(R.id.task_list_view);
        this.selectLinearLayout = view.findViewById(R.id.select_list_layout);
        this.deleteTextView = view.findViewById(R.id.delete_items);
        this.taskListItemViewAdapter = new TaskListItemViewAdapter(view.getContext());

        MainActivity mainActivity = (MainActivity) getActivity();
        this.account = mainActivity.getAccount();
        init();
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("当前生命周期：","onViewCreated");



        this.listHistory.setOnClickListener(v -> {

            Intent intent = new Intent(getActivity(), TaskListHistoryActivity.class);
            Bundle bundle=new Bundle();
            bundle.putString("account",this.account);
            intent.putExtras(bundle);
            startActivity(intent);
        });


        this.taskListItem.setOnItemClickListener((parent, view1, position, id) -> {
            ImageView updateImage = view1.findViewById(R.id.update_task_list_item);
            ImageView finishImage = view1.findViewById(R.id.finish_task_list_item_img);
            ImageView deleteImage = view1.findViewById(R.id.delete_task_list_item);
            ImageView addScheduleItem = view1.findViewById(R.id.add_schedule_item);
            CheckBox checkBox = view1.findViewById(R.id.select_task_list_item);

            if(!checkBox.isChecked()&&checkBox.getVisibility()==View.VISIBLE) this.checkAll.setChecked(false);
            String itemName = this.taskListItemViewAdapter.getItem(position).getTaskListName();
            String description = this.taskListItemViewAdapter.getItem(position).getTaskListDescription();

            TaskListItemInfo taskListItemInfo = this.taskListItemViewAdapter.getItem(position);

            updateImage.setOnClickListener(v-> this.showBottomSheetDialog(itemName,description));

            finishImage.setOnClickListener(v -> this.popFinishAlertDialog(itemName,position,taskListItemInfo));

            deleteImage.setOnClickListener(v -> this.popDeleteAlertDialog(itemName,position));

            addScheduleItem.setOnClickListener(v->{
                this.showInsertScheduleDialog(itemName);
            });

            view1.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), TaskListDetailActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("account",this.account);
                bundle.putString("listName",itemName);
                intent.putExtras(bundle);
                startActivity(intent);
            });
        });

        this.addTaskListItem.setOnClickListener(v -> this.showBottomSheetDialog("",""));

        this.selectTaskItem.setOnClickListener(v -> {
            if(!selectFlag){
                selectFlag = true;
                checkAll.setChecked(false);
                selectLinearLayout.setVisibility(View.VISIBLE);
                for (int i = 0; i < taskListItem.getChildCount(); i++) {
                    taskListItem.getChildAt(i).findViewById(R.id.select_task_list_item).setVisibility(View.VISIBLE);
                }


            }
            else{
                selectFlag = false;
                selectLinearLayout.setVisibility(View.GONE);
                taskListItemViewAdapter.initCheck(false);
                for (int i = 0; i < taskListItem.getChildCount(); i++) {
                    taskListItem.getChildAt(i).findViewById(R.id.select_task_list_item).setVisibility(View.GONE);
                }
            }
        });

        this.checkAll.setOnClickListener(
                v -> {
                    taskListItemViewAdapter.initCheck(checkAll.isChecked());
                    taskListItemViewAdapter.notifyDataSetChanged();

                }
        );

        this.deleteTextView.setOnClickListener(v->{
            Map<Integer, Boolean> isCheck = this.taskListItemViewAdapter.getMap();
            int count = this.taskListItemViewAdapter.getCount();
            for (int i = 0; i < count; i++) {
                int position = i - (count - this.taskListItemViewAdapter.getCount());
                if (isCheck.get(i) != null && isCheck.get(i)) {
                    String itemName = this.taskListItemViewAdapter.getItem(position).getTaskListName();
                    LitePal.deleteAll(TaskListItemInfo.class,"taskListName=?",itemName);
                    isCheck.remove(i);
                    this.taskListItemViewAdapter.removeData(position);
                }
            }
            if(this.checkAll.isChecked()) this.checkAll.setChecked(false);
            this.taskListItemViewAdapter.notifyDataSetChanged();
        });


    }

    public void init(){

        taskListCrud = new TaskListCrudImpl();

        List<TaskListItemInfo> taskListItemList = LitePal.where("account=? and taskListState=?",this.account,"0").find(TaskListItemInfo.class);

        this.taskListItemViewAdapter.setData(taskListItemList);
        this.taskListItem.setAdapter(this.taskListItemViewAdapter);


    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showBottomSheetDialog(String itemName,String itemDescription){

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());

        View view = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_dialog,null);
        EditText taskListName = view.findViewById(R.id.task_list_name);
        EditText taskListDescription = view.findViewById(R.id.task_list_description);
        Button yesBtn = view.findViewById(R.id.yes);
        Button cancelBtn = view.findViewById(R.id.no);

        taskListDescription.setHint("建议不超过20个字");

        if(!"".equals(itemName)){
            taskListName.setText(itemName);
            taskListDescription.setText(itemDescription);
        }

        yesBtn.setOnClickListener(v->{
            String name = taskListName.getText().toString();
            String description = taskListDescription.getText().toString();
            if(itemName.isEmpty()){
                if(name.isEmpty()){
                    BaseActivity.alertHandler(this.getActivity(),"待办事项清单名称不能为空！");
                }else{
                    if(TaskListCrud.checkName(name)){
                        TaskListItemInfo taskListItemInfo = new TaskListItemInfo(this.account,name,description);
                        taskListItemInfo.save();
                        this.taskListItemViewAdapter.addData(taskListItemInfo);
                        this.taskListItemViewAdapter.notifyDataSetChanged();
                        this.taskListItem.setAdapter(this.taskListItemViewAdapter);
                        BaseActivity.alertHandler(this.getActivity(),"待办事项清单创建成功！");
                    }else{
                        BaseActivity.alertHandler(this.getActivity(),"待办事项清单名称已存在！");
                    }
                }
            }
            else{
                if(name.isEmpty()){
                    BaseActivity.alertHandler(this.getActivity(),"待办事项清单名称不能为空！");
                }else{
                    if(!itemName.equals(name)||!description.equals(itemDescription)){
                        taskListCrud.updateTaskListItemInfoByName(itemName,name,description,this.account);
                        List<TaskListItemInfo> taskListItemList = LitePal.where("account=? and taskListState=?",this.account,"0").find(TaskListItemInfo.class);
                        this.taskListItemViewAdapter.setData(taskListItemList);
                        this.taskListItemViewAdapter.notifyDataSetChanged();
                        this.taskListItem.setAdapter(this.taskListItemViewAdapter);

                        BaseActivity.alertHandler(this.getActivity(),"待办事项清单修改成功！");
                    }
                    else{
                        BaseActivity.alertHandler(this.getActivity(),"已有待办事项清单！");

                    }


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

    private void popFinishAlertDialog(String itemName,int position,TaskListItemInfo taskListItemInfo){

        String description = taskListItemInfo.getTaskListDescription();
        AlertDialog.Builder dialog = new AlertDialog.Builder(this.getActivity());
        dialog.setTitle("提示");
        dialog.setMessage("是否已完成选中待办事项清单？");
        dialog.setCancelable(false);
        dialog.setPositiveButton("是", (dialog1, which) -> {
            taskListCrud.finishTaskListItem(this.account,itemName,description);
            this.taskListItemViewAdapter.removeData(position);
            this.taskListItemViewAdapter.notifyDataSetChanged();
        });
        dialog.setNegativeButton("否", (dialog12, which) -> dialog12.cancel());
        dialog.show();
    }

    private void popDeleteAlertDialog(String itemName,int position){

        AlertDialog.Builder dialog = new AlertDialog.Builder(this.getActivity());
        dialog.setTitle("提示");
        dialog.setMessage("删除待办事项清单，其中待办事项也会同时删除，是否确定删除选中待办事项清单？");
        dialog.setCancelable(false); //设置按下返回键不能消失
        dialog.setPositiveButton("确定", (dialog1, which) -> {
            this.taskListCrud.deleteTaskListItem(account,itemName);
            this.taskListItemViewAdapter.removeData(position);
            this.taskListItemViewAdapter.notifyDataSetChanged();
        });
        dialog.setNegativeButton("取消", (dialog12, which) -> dialog12.cancel());
        dialog.show();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showInsertScheduleDialog(String taskListName){

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_schedule_insert,null);
        EditText scheduleName = view.findViewById(R.id.schedule_name);
        RadioGroup labelRadioGroup = view.findViewById(R.id.label_group);

        Button yesBtn = view.findViewById(R.id.insert_yes);
        Button cancelBtn = view.findViewById(R.id.insert_no);

        List<LabelInfo> labelInfoList = LitePal.where("account=?",this.account).find(LabelInfo.class);
        for (LabelInfo labelInfo : labelInfoList) {
            RadioButton radioButton = new RadioButton(this.getActivity());
            radioButton.setText(labelInfo.getLabelName());
            labelRadioGroup.addView(radioButton,LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        }



        ScheduleListItemInfo scheduleListItemInfo = new ScheduleListItemInfo();
        scheduleListItemInfo.setAccount(this.account);
        scheduleListItemInfo.setListName(taskListName);
        scheduleListItemInfo.setState(false);
        labelRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton tempRadioButton = group.findViewById(checkedId);
            scheduleListItemInfo.setLabelName(tempRadioButton.getText().toString());
        });

        yesBtn.setOnClickListener(v->{
            String name = scheduleName.getText().toString();
                if(name.isEmpty()){
                    BaseActivity.alertHandler(this.getActivity(),"待办事项名称不能为空！");
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
                        BaseActivity.alertHandler(this.getActivity(),"待办事项创建成功！");
                    }else{
                        BaseActivity.alertHandler(this.getActivity(),"待办事项名称已存在！");
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d("当前生命周期：","onAttach");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("当前生命周期：","onStart");
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("当前生命周期：","onResume");


    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("当前生命周期：","onPause");

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("当前生命周期：","onStop");

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("当前生命周期：","onDestroyView");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("当前生命周期：","onDestroy");

    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("当前生命周期：","onDetach");

    }
}