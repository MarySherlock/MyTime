package com.mytime.service;

import android.content.Context;
import com.mytime.activity.BaseActivity;
import com.mytime.model.UserPwdInfo;
import com.mytime.util.MailUtil;
import org.litepal.LitePal;
import java.util.List;

public class AccountValidateRegisterServiceImpl extends AccountValidateService {


    /**
     * 用户输入的邮箱是否能进行注册
     * @param account 用户输入的邮箱
     * @return Boolean
     */
    @Override
    public boolean judgeAccount(Context context, String account){

        if(!account.isEmpty()){
            if(MailUtil.isEmail(account)){

                //查询和输入的账号相同的用户信息
                List<UserPwdInfo> userPwdInfoList = LitePal.where("account = ?",account)
                        .find(UserPwdInfo.class);
                if(userPwdInfoList.isEmpty()){
                    return true;
                }
                else BaseActivity.alertHandler(context,ACCOUNT_ALREADY_REGISTERED_ERROR);        //邮箱已经被注册
            }else BaseActivity.alertHandler(context,ACCOUNT_ILLEGAL_ERROR);                      //邮箱输入不合法
        }else BaseActivity.alertHandler(context,ACCOUNT_EMPTY_ERROR);                            //邮箱输入为空
        return false;
    }

}
