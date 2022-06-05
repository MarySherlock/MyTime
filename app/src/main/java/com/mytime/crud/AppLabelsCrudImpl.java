package com.mytime.crud;


import android.util.Log;

import com.mytime.model.LabelInfo;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class AppLabelsCrudImpl extends AppLabelsCrud{
    @Override
    public List<String> getAppLabelsName(String account) {
        List<LabelInfo> appLabelsInfo
                = LitePal.where("account=?",account).select("labelName")
                .find(LabelInfo.class);
        List<String> labels = new ArrayList<>();
        for (LabelInfo labelInfo : appLabelsInfo) {
            labels.add(labelInfo.getLabelName());
        }
        return labels;
    }

    @Override
    public void deleteLabel(String account,String labelName) {
        LitePal.deleteAll("LabelInfo","account=? and labelName=?",account,labelName);
    }



    @Override
    public boolean insertLabel(LabelInfo labelInfo) {
        String labelName = labelInfo.getLabelName().replaceAll(" ","");
        String account = labelInfo.getAccount();
        List<LabelInfo> appLabelsInfo
                = LitePal.where("account=? and labelName=?",account,labelName)
                .find(LabelInfo.class);
        if(appLabelsInfo.isEmpty()){
            labelInfo.save();
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean updateLabelName(String account,String oldName,String newName) {
        List<LabelInfo> appLabelsInfo
                = LitePal.where("account=? and labelName=?",account,newName)
                .find(LabelInfo.class);

        for (LabelInfo labelInfo : appLabelsInfo) {
            Log.d("已有分类",labelInfo.getLabelName());
        }
        if(appLabelsInfo.isEmpty()){
            LabelInfo labelInfo = new LabelInfo();
            labelInfo.setLabelName(newName);
            labelInfo.updateAll("labelNAme=?",oldName);
            return true;
        }
        return false;
    }
}
