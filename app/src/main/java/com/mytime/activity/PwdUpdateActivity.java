package com.mytime.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.mytime.R;
import com.mytime.model.UserPwdInfo;
import com.mytime.service.AccountValidateLoginServiceImpl;
import com.mytime.service.AccountValidateService;


public class PwdUpdateActivity extends BaseActivity {

    EditText accountInsertEditText;
    EditText codeEditText;
    EditText pwdInsertEditText;
    EditText pwdInsertAgainEditText;
    Button sendCodeButton;
    Button updateButton;

    String account;
    String code;

    AccountValidateService accountValidateService = new AccountValidateLoginServiceImpl();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwd_update);

        initView();

        sendCodeButton.setOnClickListener(v->{
            this.account = accountInsertEditText.getText().toString();
            if(accountValidateService.judgeAccount(this,account)){
                new Thread(()->this.code = accountValidateService.sendCode(account)).start();
            }
        });

        updateButton.setOnClickListener(v->{

            String codeString = codeEditText.getText().toString();
            String pwd1 = pwdInsertEditText.getText().toString();
            String pwd2 = pwdInsertAgainEditText.getText().toString();


            if(accountValidateService.judgeAccount(this,account)){
                if(accountValidateService.judgeCode(this,codeString,code)){
                    if(accountValidateService.judgePWD(this,pwd1,pwd2)
                    && accountValidateService.judgePWD(this,pwd1)
                    && accountValidateService.judgePWD(this,pwd2)){

                        UserPwdInfo userPwdInfo = new UserPwdInfo();
                        userPwdInfo.setPassword(pwd1);
                        userPwdInfo.updateAll("account=?",account);

                        Intent intent =new Intent();
                        intent.putExtra("account",account);
                        intent.putExtra("password",pwd1);
                        setResult(RESULT_OK,intent);
                        finish();
                    }
                }
            }
        });


    }

    public void initView(){
        accountInsertEditText = findViewById(R.id.account_editText);
        codeEditText = findViewById(R.id.code_editText);
        pwdInsertEditText = findViewById(R.id.pwd_insert_editText);
        pwdInsertAgainEditText=findViewById(R.id.pwd_insert_again_editText);
        sendCodeButton = findViewById(R.id.send_code_button);
        updateButton = findViewById(R.id.update_button);

        account = getIntent().getStringExtra("account");
        accountInsertEditText.setText(account);
    }
}