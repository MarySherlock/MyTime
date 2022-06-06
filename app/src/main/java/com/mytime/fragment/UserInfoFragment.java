package com.mytime.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mytime.R;
import com.mytime.activity.ActivityController;
import com.mytime.activity.LoginActivity;
import com.mytime.activity.MainActivity;
import com.mytime.activity.SettingActivity;
import com.mytime.model.UserInfo;
import com.mytime.model.UserPwdInfo;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserInfoFragment extends Fragment {

    SimpleAdapter simpleAdapter;
    ListView toolsListView;
    TextView userTextView;
    String account;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_info, container, false);

        MainActivity mainActivity = (MainActivity) getActivity();
        this.account = mainActivity.getAccount();
        this.toolsListView = view.findViewById(R.id.tool_list);
        this.userTextView = view.findViewById(R.id.userNameTextView);
        this.userTextView.setText(this.account);
        simpleAdapter = new SimpleAdapter(getActivity(),getData(),R.layout.tool_list_item_layout,new String[]{"title","image"},new int[]{R.id.myMenu_name,R.id.myMenu_image});
        toolsListView.setAdapter(simpleAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        // listview监听事件
        this.toolsListView.setOnItemClickListener((parent, view, position, id) -> {
            if(position == 2){
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.putExtra("flag",true);
                startActivity(intent);
                ActivityController.finishAll();
            }else if(position == 0){
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                intent.putExtra("account",this.account);
                getActivity().startActivity(intent);
            }else if (position == 3){
                LitePal.deleteDatabase("Mytime");
                Intent intent = new Intent(getActivity(),LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private List<Map<String,Object>> getData() {
        String [] titles={"设置","绑定微信\\电话","退出登录","注销登录"};
        Integer [] images={R.drawable.ic_settings,R.drawable.ic_other_account_info,R.drawable.ic_exit,R.drawable.ic_delete_user_info};
        List<Map<String,Object>> list= new ArrayList<>();
        for(int i=0;i<4;i++){
            HashMap<String,Object> map = new HashMap<>();
            map.put("title",titles[i]);
            map.put("image",images[i]);
            list.add(map);
        }
        return list;
    }
    public String getAccount(){
        return this.account;
    }
}