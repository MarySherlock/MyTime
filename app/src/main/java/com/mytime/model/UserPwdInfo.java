package com.mytime.model;

import org.litepal.crud.LitePalSupport;

public class UserPwdInfo extends LitePalSupport {
    String account;
    String password;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
