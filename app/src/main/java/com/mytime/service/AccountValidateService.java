package com.mytime.service;

import android.content.Context;

import com.mytime.activity.BaseActivity;
import com.mytime.model.UserPwdInfo;
import com.mytime.util.MailUtil;

public abstract class AccountValidateService {


    String ACCOUNT_EMPTY_ERROR = "用户账户输入不能为空，请重新输入！";
    String ACCOUNT_ILLEGAL_ERROR = "用户账户输入格式错误，请重新输入！";
    String ACCOUNT_ALREADY_REGISTERED_ERROR = "此邮箱已注册，请重新输入！";
    String PWD_EMPTY_ERROR = "密码输入不能为空，请输入密码！";
    String PWD_DIFFERENT_ERROR ="两次密码输入不同，请重新输入！";
    String PWD_ILLEGAL_ERROR = "密码输入不合法，请重新输入！";
    String CODE_EMPTY_ERROR ="验证码为空，请输入验证码！";
    String CODE_DIFFERENT_ERROR ="验证码输入错误，请重新输入！";
    String NOTICE_CHECKED_ERROR = "请阅读并同意隐私条款!";

    String fromEmailAccountString = "growgentally@163.com";
    MailUtil mailsUtil;

    public abstract boolean judgeAccount(Context context, String account);

    public boolean judgePWD(Context context,String password1,String password2){
        if(password1.equals(password2)){
            return true;
        }else BaseActivity.alertHandler(context,PWD_DIFFERENT_ERROR);
        return false;
    }

    public boolean judgePWD(Context context,String password){
        if(!password.isEmpty()){
            if(password.matches("[A-Za-z0-9]+")){
                return true;
            }else BaseActivity.alertHandler(context,PWD_ILLEGAL_ERROR);
        }else BaseActivity.alertHandler(context,PWD_EMPTY_ERROR);
        return false;
    }

    public String sendCode(String account){
        String code = null;
        try {
            mailsUtil = new MailUtil(fromEmailAccountString, account);
            mailsUtil.setCode();
            mailsUtil.setProperties();
            mailsUtil.setSSLSocket();
            mailsUtil.sendMessage();
            code = mailsUtil.getCode().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }

    public boolean judgeCode(Context context,String code1,String code2){
        if(!code1.isEmpty()){
            if(code1.equals(code2)){
                return true;
            }else BaseActivity.alertHandler(context,CODE_DIFFERENT_ERROR);
        }else BaseActivity.alertHandler(context,CODE_EMPTY_ERROR);
        return false;
    }



    public boolean saveUserPwdInfo(Context context,String account, String password, Boolean check){
        if(check){
            UserPwdInfo userPwdInfo = new UserPwdInfo();
            userPwdInfo.setAccount(account);
            userPwdInfo.setPassword(password);
            userPwdInfo.save();
            return true;
        }else BaseActivity.alertHandler(context,NOTICE_CHECKED_ERROR);
        return false;

    }


}
