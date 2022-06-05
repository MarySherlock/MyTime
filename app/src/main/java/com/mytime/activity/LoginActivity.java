package com.mytime.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.mytime.R;
import com.mytime.crud.AppLabelsCrud;
import com.mytime.crud.AppLabelsCrudImpl;
import com.mytime.model.UserPwdInfo;
import com.mytime.service.AccountValidateLoginServiceImpl;

import org.litepal.LitePal;

import java.util.List;
import java.util.Objects;


public class LoginActivity extends BaseActivity {

    EditText accountLoginEditText;
    EditText pwdLoginEditText;
    TextView registerTextView;
    TextView pwdUpdateTextView;
    TextView exitTextView;
    Button loginButton;

    String accountInsertLoginString;
    String pwdInsertLoginString;

    AccountValidateLoginServiceImpl accountValidateService = new AccountValidateLoginServiceImpl();

    List<UserPwdInfo> userPwdInfoList;

    private boolean checkUserPwdInfo(){
        userPwdInfoList = LitePal.findAll(UserPwdInfo.class);
        return !userPwdInfoList.isEmpty();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (checkUserPwdInfo()){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("account", this.userPwdInfoList.get(0).getAccount());
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.activity_login);
        initView();

        registerTextView.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
            startActivity(intent);
            finish();
        });

        pwdUpdateTextView.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this,PwdUpdateActivity.class);
            intent.putExtra("account",accountInsertLoginString);
            startActivityForResult(intent,1);
        });

        this.exitTextView.setOnClickListener(v -> {
            ActivityController.finishAll();
            android.os.Process.killProcess(android.os.Process.myPid());
        });

        loginButton.setOnClickListener(view -> {

            accountInsertLoginString = accountLoginEditText.getText().toString();       //用户账户
            pwdInsertLoginString = pwdLoginEditText.getText().toString();               //密码输入

            if(accountValidateService.judgeAccount(this,accountInsertLoginString)) {
                if (accountValidateService.judgeLogin(this, accountInsertLoginString, pwdInsertLoginString)) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("account", accountInsertLoginString);
                    AppLabelsCrud appLabelsCrud = new AppLabelsCrudImpl();if(appLabelsCrud.getAppLabelsName(accountInsertLoginString).isEmpty()){
                    AppLabelsCrud.initAppLabelsInfo(accountInsertLoginString);
                }
                startActivity(intent);
                    finish();
                }
            }
        });
    }



    private void initView(){
        this.registerTextView = findViewById(R.id.register_textView);
        this.pwdUpdateTextView = findViewById(R.id.update_pwd_textView);
        this.loginButton = findViewById(R.id.loginButton);
        this.accountLoginEditText = findViewById(R.id.accountEditText);
        this.pwdLoginEditText = findViewById(R.id.pwdEditText);
        this.exitTextView = findViewById(R.id.login_exit);

        exitTextView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            if(resultCode == RESULT_OK){
                accountLoginEditText.setText(Objects.requireNonNull(data).getStringExtra("account"));
                pwdLoginEditText.setText(Objects.requireNonNull(data).getStringExtra("password"));
            }
        }
    }


}