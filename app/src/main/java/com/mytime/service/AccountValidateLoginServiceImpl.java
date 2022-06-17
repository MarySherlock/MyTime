package com.mytime.service;

import android.content.Context;
import android.util.Log;

import com.mytime.activity.BaseActivity;
import com.mytime.model.UserPwdInfo;
import com.mytime.util.MailUtil;

import org.litepal.LitePal;

import java.util.List;

public class AccountValidateLoginServiceImpl extends AccountValidateService{

    String ACCOUNT_UNREGISTERED_ERROR = "此邮箱未注册，请先注册！";
    String ACCOUNT_WRONG_ERROR = "账号或者密码错误，请重新输入！";
    String LOGIN_SUCCESS_INFO = "登录成功！";


    @Override
    public boolean judgeAccount(Context context, String account){

        if(!account.isEmpty()){
            if(MailUtil.isEmail(account)){

                List<UserPwdInfo> userPwdInfoList = LitePal.where("account=?", account).find(UserPwdInfo.class);
                if(!userPwdInfoList.isEmpty()){
                    return true;
                }
                else BaseActivity.alertHandler(context,ACCOUNT_UNREGISTERED_ERROR);
            }else BaseActivity.alertHandler(context,ACCOUNT_ILLEGAL_ERROR);
        }else BaseActivity.alertHandler(context,ACCOUNT_EMPTY_ERROR);
        return false;
    }

    public boolean judgeLogin(Context context,String account,String password){

        List<UserPwdInfo> userPwdInfoList = LitePal.findAll(UserPwdInfo.class);

        if(!password.isEmpty()){
            if(!userPwdInfoList.isEmpty()) {
                for (UserPwdInfo userPwdInfo : userPwdInfoList) {
                    if (userPwdInfo.getPassword().equals(password)) {
                        Log.d("pwd", userPwdInfo.getPassword());
                        BaseActivity.alertHandler(context, LOGIN_SUCCESS_INFO);
                        return true;
                    }
                }
                BaseActivity.alertHandler(context,ACCOUNT_WRONG_ERROR);
            }
        }else BaseActivity.alertHandler(context,PWD_EMPTY_ERROR);
        return false;
    }

}
