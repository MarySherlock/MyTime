package com.mytime.crud;

import com.mytime.model.LabelInfo;

import java.util.List;

public abstract class AppLabelsCrud {

    // 初始化应用分类信息
    public static void initAppLabelsInfo(String account){

        LabelInfo labelInfo1 = new LabelInfo(account,"工作");
        LabelInfo labelInfo2 = new LabelInfo(account, "学习");
        LabelInfo labelInfo3 = new LabelInfo(account, "娱乐");

        labelInfo1.save();
        labelInfo2.save();
        labelInfo3.save();
    }

    public abstract List<String> getAppLabelsName(String account);
    public abstract void deleteLabel(String account,String labelName);
    public abstract boolean insertLabel(LabelInfo labelInfo);
    public abstract  boolean updateLabelName(String account,String oldName,String newName);

}
