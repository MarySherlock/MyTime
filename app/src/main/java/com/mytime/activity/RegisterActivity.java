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

    // 控件
    EditText accountInsertEditText;         //输入用户电子邮箱
    EditText codeEditText;                  //输入邮箱验证码
    EditText pwdInsertEditText;             //第一次输入的密码
    EditText pwdInsertAgainEditText;        //第二次输入的密码
    Button sendCodeButton;                  //发送验证码
    Button registerButton;                  //注册
    CheckBox noticeCheckBox;
    TextView backTextView;

    //变量
    String accountRegisterString;           //用户电子邮箱
    String pwdRegisterString;               //第一次输入的密码
    String pwdAgainRegisterString;          //第二次输入的密码
    String codeRegisterString;              //用户输入的邮箱验证码
    String codeMailSend;                    //邮箱发送的验证码


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

            // 发送验证码
            sendCodeButton.setOnClickListener(view -> {
                accountRegisterString = accountInsertEditText.getText().toString();
                if (accountValidateService.judgeAccount(this, accountRegisterString)) {
                    new Thread(()-> this.codeMailSend = accountValidateService.sendCode(accountRegisterString)).start();
                }
            });

            //账户注册监听
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

                            //保存账号
                            if(accountValidateService.saveUserPwdInfo(this, accountRegisterString, pwdRegisterString, noticeCheckBox.isChecked())){
                                //传回账号
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

    /**
     * @Description: 初始化控件
     */
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