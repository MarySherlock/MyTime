package com.mytime.service;

import android.content.Context;
import android.util.Log;

import com.mytime.activity.BaseActivity;
import com.mytime.model.UserPwdInfo;
import com.mytime.util.MailUtil;

import org.litepal.LitePal;

import java.util.List;

public class AccountValidateLoginServiceImpl extends AccountValidateService{

    //用户信息错误提示
    String ACCOUNT_UNREGISTERED_ERROR = "此邮箱未注册，请先注册！";
    String ACCOUNT_WRONG_ERROR = "账号或者密码错误，请重新输入！";
    String LOGIN_SUCCESS_INFO = "登录成功！";


    /**
     * 用户输入的邮箱是否能进行登录
     * @param account 用户输入的邮箱
     * @return Boolean
     */
    @Override
    public boolean judgeAccount(Context context, String account){

        if(!account.isEmpty()){
            if(MailUtil.isEmail(account)){

                //查询和输入的账号相同的用户信息
                List<UserPwdInfo> userPwdInfoList = LitePal.where("account=?", account).find(UserPwdInfo.class);
                if(!userPwdInfoList.isEmpty()){
                    return true;
                }
                else BaseActivity.alertHandler(context,ACCOUNT_UNREGISTERED_ERROR);        //邮箱未注册
            }else BaseActivity.alertHandler(context,ACCOUNT_ILLEGAL_ERROR);                //邮箱输入不合法
        }else BaseActivity.alertHandler(context,ACCOUNT_EMPTY_ERROR);                      //邮箱输入为空
        return false;
    }

    public boolean judgeLogin(Context context,String account,String password){

        //查询目标用户
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
                BaseActivity.alertHandler(context,ACCOUNT_WRONG_ERROR);     //用户输入信息错误
            }
        }else BaseActivity.alertHandler(context,PWD_EMPTY_ERROR);            //密码为空
        return false;
    }

}
