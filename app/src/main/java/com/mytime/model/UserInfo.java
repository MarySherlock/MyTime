package com.mytime.model;

import org.litepal.crud.LitePalSupport;

public class UserInfo extends LitePalSupport {
    String account;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
