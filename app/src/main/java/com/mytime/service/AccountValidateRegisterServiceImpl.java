package com.mytime.service;

import android.content.Context;
import com.mytime.activity.BaseActivity;
import com.mytime.model.UserPwdInfo;
import com.mytime.util.MailUtil;
import org.litepal.LitePal;
import java.util.List;

public class AccountValidateRegisterServiceImpl extends AccountValidateService {


    @Override
    public boolean judgeAccount(Context context, String account){

        if(!account.isEmpty()){
            if(MailUtil.isEmail(account)){

                List<UserPwdInfo> userPwdInfoList = LitePal.where("account = ?",account)
                        .find(UserPwdInfo.class);
                if(userPwdInfoList.isEmpty()){
                    return true;
                }
                else BaseActivity.alertHandler(context,ACCOUNT_ALREADY_REGISTERED_ERROR);
            }else BaseActivity.alertHandler(context,ACCOUNT_ILLEGAL_ERROR);
        }else BaseActivity.alertHandler(context,ACCOUNT_EMPTY_ERROR);
        return false;
    }

}
