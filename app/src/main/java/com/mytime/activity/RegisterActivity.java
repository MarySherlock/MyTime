package com.mytime.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.mytime.R;
import com.mytime.service.AccountValidateRegisterServiceImpl;
import com.mytime.service.AccountValidateService;


public class RegisterActivity extends BaseActivity {

    EditText accountInsertEditText;
    EditText codeEditText;
    EditText pwdInsertEditText;
    EditText pwdInsertAgainEditText;
    Button sendCodeButton;
    Button registerButton;
    CheckBox noticeCheckBox;
    TextView backTextView;

    String accountRegisterString;
    String pwdRegisterString;
    String pwdAgainRegisterString;
    String codeRegisterString;
    String codeMailSend;


    AccountValidateService accountValidateService = new AccountValidateRegisterServiceImpl();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
            initView();

            backTextView.setOnClickListener(v->{
                Intent intent = new Intent(this,LoginActivity.class);
                startActivity(intent);
                finish();
            });

            sendCodeButton.setOnClickListener(view -> {
                accountRegisterString = accountInsertEditText.getText().toString();
                if (accountValidateService.judgeAccount(this, accountRegisterString)) {
                    new Thread(()-> this.codeMailSend = accountValidateService.sendCode(accountRegisterString)).start();
                }
            });

            registerButton.setOnClickListener(view -> {


                accountRegisterString = accountInsertEditText.getText().toString();
                pwdRegisterString = pwdInsertEditText.getText().toString();
                pwdAgainRegisterString = pwdInsertAgainEditText.getText().toString();
                codeRegisterString = codeEditText.getText().toString();


                if (accountValidateService.judgeAccount(this, accountRegisterString)) {
                    if (accountValidateService.judgeCode(this, codeRegisterString, codeMailSend)) {
                        if (accountValidateService.judgePWD(this, pwdAgainRegisterString, pwdRegisterString)
                                && accountValidateService.judgePWD(this, pwdAgainRegisterString)
                                && accountValidateService.judgePWD(this, pwdRegisterString)) {

                            if(accountValidateService.saveUserPwdInfo(this, accountRegisterString, pwdRegisterString, noticeCheckBox.isChecked())){
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                intent.putExtra("account", accountRegisterString);
                                intent.putExtra("pwd", pwdAgainRegisterString);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                }
            });
        }


    private void initView(){
        this.accountInsertEditText = findViewById(R.id.account_editText);
        this.codeEditText = findViewById(R.id.code_editText);
        this.pwdInsertEditText = findViewById(R.id.pwd_insert_editText);
        this.pwdInsertAgainEditText = findViewById(R.id.pwd_insert_again_editText);
        this.sendCodeButton = findViewById(R.id.send_code_button);
        this.noticeCheckBox = findViewById(R.id.notice_checkBox);
        this.registerButton = findViewById(R.id.update_button);
        this.backTextView = findViewById(R.id.back_textView);

        this.backTextView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
    }


}